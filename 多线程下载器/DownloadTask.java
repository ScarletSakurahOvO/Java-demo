package 多线程下载器;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class DownloadTask implements Runnable {
    
    private String downloadUrl;
    private String savePath;
    private long startPos;
    private long endPos;
    private CountDownLatch latch;

    // 构造函数：接收主线程计算好的分块参数
    public DownloadTask(String downloadUrl, String savePath, long startPos, long endPos, CountDownLatch latch) {
        this.downloadUrl = downloadUrl;
        this.savePath = savePath;
        this.startPos = startPos;
        this.endPos = endPos;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            URL url = new URI(downloadUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            
            // 核心 1：请求网络流的指定区间
            connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);

            // HTTP 状态码 206 (Partial Content) 表示服务器成功处理了部分 GET 请求
            if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                
                // 使用 try-with-resources 自动关闭输入流和文件流
                try (
                    InputStream is = connection.getInputStream();
                    RandomAccessFile raf = new RandomAccessFile(savePath, "rw")
                ) {
                    // 核心 2：将本地文件指针移动到当前线程负责的起始位置
                    raf.seek(startPos);
                    
                    byte[] buffer = new byte[8192]; // 8KB 缓冲区
                    int len;
                    // 循环读取网络流，写入本地文件
                    while ((len = is.read(buffer)) != -1) {
                        raf.write(buffer, 0, len);
                    }
                    System.out.println("线程 " + Thread.currentThread().getName() + " 下载完成: " + startPos + "-" + endPos);
                }
            } else {
                System.err.println("服务器不支持分块下载或请求失败，状态码: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            System.err.println("线程下载发生异常: " + e.getMessage());
        } finally {
            // 核心 3：必须在 finally 块中调用 countDown
            // 确保即使发生异常，计数器也会减 1，防止主线程一直阻塞死等
            if (latch != null) {
                latch.countDown();
            }
        }
    }
}
