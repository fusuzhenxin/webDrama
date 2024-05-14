package net.xdclass.video.common;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageDownloader {

    public static void main(String[] args) {
        List<String> imgUrls = new ArrayList<>();
        // 添加图片链接到 imgUrls 列表
        imgUrls.add("https://www.example.com/image1.jpg");
        imgUrls.add("https://www.example.com/image2.jpg");

        String directory = "F://rob/"; // 指定保存图片的目录
        downloadImages(imgUrls, directory);
    }

    static void downloadImages(List<String> imgUrls, String directory) {
        for (String imgUrl : imgUrls) {
            try {
                URL url = new URL(imgUrl);
                String fileName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);
                String destinationFile = directory + fileName;

                try (BufferedInputStream in = new BufferedInputStream(url.openStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
                    byte[] dataBuffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                    System.out.println("已下载：" + fileName);
                } catch (IOException e) {
                    System.err.println("下载失败：" + fileName);
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
