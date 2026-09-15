package 多线程下载器;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;

public class FileUtils {
  //targetPath: 本地保存路径（例如："C:/downloads/100MB.bin"）
  //totalSize: 第一步获取到的文件总字节数
  public static void createPlaceholder(String targetPath, long totalSize) throws IOException {
    File file = new File(targetPath);

    //使用"rw"(续写)模式打开文件，如果文件不存在，会自动创建
    //使用try-with-resources语法确保流自动关闭
    try (RandomAccessFile raf = new RandomAccessFile(file,"rw")) {

      //核心操作：直接撑大文件到指定尺寸，完成物理占据
      raf.setLength(totalSize);
    }
  }
}
