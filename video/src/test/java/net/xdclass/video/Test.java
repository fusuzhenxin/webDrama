package net.xdclass.video;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    /**
     * 下载索引文件信息
     *
     * @param m3u8UrlPath
     * @return 索引文件信息
     */
    public static String getM3u8FileIndexInfo(String m3u8UrlPath) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(m3u8UrlPath).openStream(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解析索引文件中的ts列表信息
     */
    public static List<String> analysisTsList(String m3u8FileIndexInfo) {
        Pattern pattern = Pattern.compile(".*ts");
        Matcher ma = pattern.matcher(m3u8FileIndexInfo);
        List<String> list = new ArrayList<>();
        while (ma.find()) {
            list.add(ma.group());
        }
        return list;
    }


    public static void downLoadIndexFile(List<String> tsList, String folderPath, String preUrlPath) {
        for (int i = 0; i < tsList.size(); i++) {
            String ts = tsList.get(i);
            String fileOutPath = folderPath + File.separator + ts;
            try {
                downloadTs(preUrlPath + "/" + ts, fileOutPath);
                System.out.println("下载成功：" + (i + 1) + "/" + tsList.size());
            } catch (Exception e) {
                System.err.println("下载失败：" + (i + 1) + "/" + tsList.size());
            }
        }
    }

    /**
     * 下载ts文件
     *
     * @param fullUrlPath
     * @param fileOutPath
     */
    public static void downloadTs(String fullUrlPath, String fileOutPath) {
        try (InputStream inStream = new URL(fullUrlPath).openConnection().getInputStream();
             FileOutputStream fs = new FileOutputStream(fileOutPath)) {
            int byteread;
            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String composeFile(List<String> tsList, String folderPath){
        String fileOutPath = folderPath + File.separator + UUID.randomUUID() + ".mp4";
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(fileOutPath))){
            byte[] bytes = new byte[1024];
            int length;
            for (String nodePath : tsList) {
                File file = new File(nodePath);
                if (!file.exists()) {
                    continue;
                }
                try (FileInputStream fis = new FileInputStream(file);) {
                    while ((length = fis.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, length);
                    }
                    // 删除该临时文件

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileOutPath;
    }
@org.junit.jupiter.api.Test
    void s() {
    String introduction = "简介： 该剧由收集漫画改编，讲述即使不再信赖爱情仍然想要谈爱情的女人和认为爱情很棘手然则很想搞暧昧的汉子之间超实际主义题材的浪漫爱情电视剧。";

    // 去除简介及其后的内容
    // 去除简介部分
    String trimmedIntroduction = introduction.replaceFirst("^简介：\\s*", "");

    System.out.println(trimmedIntroduction.trim());

    }

}

