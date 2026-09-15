package 多线程下载器;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadDownloader {
  private static final int THREAD_COUNT = 3;
    // 这里替换为你实际测试的下载链接和本地保存路径
  private static final String DOWNLOAD_URL = "https://speed.hetzner.de/100MB.bin"; 
  private static final String SAVE_PATH = "D:/100MB.bin";

  public static void main(String[] args) {
    try {
      System.out.println("1.开始获取文件大小...");
      //调用第一步的工具类
      long totalSize = HttpUtils.getFileSize(DOWNLOAD_URL);
      System.out.println("文件大小： " + totalSize + "字节");

      System.out.println("2. 开始本地预分配空间...");
      //调用第二步的工具类
      FileUtils.createPlaceholder(SAVE_PATH,totalSize);

      System.out.println("3.初始化线程池和分块任务...");
      //初始化固定大小的线程池
      ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
      //初始化CountDownLatch，计数器设为线程数
      CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

      long chunkSize = totalSize / THREAD_COUNT;
      for (int i = 0; i < THREAD_COUNT; i++) {
        long startPos = i * chunkSize;
        long endPos = (i == THREAD_COUNT - 1) ? totalSize - 1 : (i + 1) * chunkSize - 1;

        //创建我们在第四步编写下的任务
        DownloadTask task = new DownloadTask(DOWNLOAD_URL, SAVE_PATH, startPos, endPos, latch);

        //将任务提交给线程池执行
        threadPool.execute(task);
      }

      System.out.println("4.所有任务已提交，等待下载完成...");
      //主线程在此阻塞，直到latch的计数器变为0（即所有DownloadTask 部调用了countDown）
      latch.await();

      System.out.println("5. 下载成功!文件已保存在：" + SAVE_PATH);

      //下载完成后关闭线程池，释放资源
      threadPool.shutdown();

    } catch (Exception e) {
      System.err.println("下载过程中发生异常：" + e.getMessage());
      e.printStackTrace();
    }
  }


}
