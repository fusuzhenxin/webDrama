package net.xdclass.video.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能：爬取某姐的小视频
 * @author cxd
 *
 */
public class WebSpiderDemo1 {

    public static void main(String[] args) throws Exception {

        String source = "https://www.kuaikaw.cn/episode/41000100284/541562145";
        String destDir = "F:/rob/";

        Map<String, String> urlMap = getUrlInSource(source);

        for (Map.Entry<String, String> entry : urlMap.entrySet()) {

            String title = entry.getKey();// 视频名称
            String url = entry.getValue();// 视频url
            File destFile = new File(destDir + title + ".mp4");
            download(url, destFile);
        }
    }

    /**
     * 通过视频的URL下载该视频并存入本地
     *
     * @param url      视频的URL
     * @param destFile 视频存入的位置
     * @throws IOException
     */
    public static void download(String url, File destFile) throws IOException {
        URL videoUrl = new URL(url);

        InputStream is = videoUrl.openStream();
        FileOutputStream fos = new FileOutputStream(destFile);

        int len = 0;
        byte[] buffer = new byte[1024];
        while ((-1) != (len = is.read(buffer))) {
            fos.write(buffer, 0, len);
        }
        fos.flush();

        if (null != fos) {
            fos.close();
        }

        if (null != is) {
            is.close();
        }
    }

    /**
     * 获取视频的URL地址和视频名称存入hashMap
     *
     * @param source
     * @return
     * @throws IOException
     */
    public static Map<String, String> getUrlInSource(String source) throws IOException {

        Map<String, String> hashMap = new HashMap<>();

        for (int index = 1; index <= 1; index++) { // 页数最大为50，自己玩嘛，就只爬取了一页。
            String pageUrl = source;
            URL url = new URL(pageUrl);
            InputStream is = url.openStream();

//            若遇到反爬机制则使用该方法将程序伪装为浏览器进行访问
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("user-agent",
//                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
//            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String info = null;
            String title = null;
            // 此处不要用==null进行判断，因为网页中有很多行都是null，否则会报java.lang.NullPointerException。
            for (int i = 0; i < 10000; i++) {
                info = br.readLine();

                if (null != info) {
                    String urlRegex = "data-mp4=\"(.*?\\.mp4)";

                    if (info.contains("data-title")) {
                        title = info;
                    }

                    Pattern pattern = Pattern.compile(urlRegex);
                    Matcher matcher = pattern.matcher(info);
                    if (matcher.find()) {
                        for (int j = 0; j <= matcher.groupCount(); j++) {
                            String tmp = matcher.group(j);
                            if (!tmp.startsWith("data-mp4=")) {
                                String videoTitle = getTitle(title.trim());
                                hashMap.put(videoTitle, tmp);
                            }
                        }
                    }
                }
            }
        }
        return hashMap;
    }

    /**
     * 清洗整理titile字符串，
     *
     * @param info
     * @return
     */
    private static String getTitle(String info) {

        int len = info.length();
        String title = info.substring(12, len - 1);
        return title;
    }

}
