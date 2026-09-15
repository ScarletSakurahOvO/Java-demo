package 多线程下载器;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;


public class HttpUtils {
  //获取文件大小的方法
  public static long getFileSize(String fileUrl) throws Exception {
    URL url = new URI(fileUrl).toURL();
    //打开连接（此时还未真正发送网络请求）
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    //核心1：设置请求方式为HEAD，只获取响应头，不拉取数据体
    connection.setRequestMethod("HEAD");

    //设置连接超时时间（5秒），防止网络不好时程序一直卡住
    connection.setConnectTimeout(5000);

    //核心修复：核心修复：添加 User-Agent，避免被服务器当作恶意脚本拦截
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

    //核心2: 真正发起网络，并获取服务器的HTTP响应状态码
    int responseCode = connection.getResponseCode();

    //状态码200（HTTP_OK）表示请求成功
    if (responseCode == HttpURLConnection.HTTP_OK) {

      //核心3：从响应头中读取Content-Lenght字段，获取文件总字节数
      //注意：一定要用getContentLengthLong(),如果用getContentLength(),超过2GB
      long fileSize = connection.getContentLengthLong();

      return fileSize;
    } else {
      throw new RuntimeException("无法连接服务器，响应码： " + responseCode);
    }
  }

  
}
