package net.xdclass.video.common;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getProperty;

public class VideoCrawler  {

    @Value("${chromedriverPath}")
    public String chromedriverPath;

    private static final String FILE_UPLOAD_PATH = getProperty("user.dir")
            + File.separator
            + "src" + File.separator
            + "main" + File.separator
            + "resources"
            + File.separator
            + "files" + File.separator
            + "image" + File.separator;

    public  void videoUrl(String name)  {

        String destDir = FILE_UPLOAD_PATH;
        String UUID = IdUtil.fastSimpleUUID() + StrUtil.DOT+"mp4";
        String destinationPath = destDir + File.separator + UUID; // 本地文件路径
        try {
            List<String> imglist = new ArrayList<String>();
            for (int page = 2; page < 4; page++) {
                String urls = "https://www.kuaikaw.cn/search/"+page+"?searchValue"+name;
                //逆袭分类的url
                Document document = Jsoup.connect(urls).get();
                //类名
                Elements element = document.select(".TagBookList_tagItem__xL2IA");
                for (Element elements : element) {
                    String href = elements.select("a.TagBookList_totalChapterNum__CHaGs").attr("href");
                    String url1 = "https://www.kuaikaw.cn" + href;
                    Document document1 = Jsoup.connect(url1).get();

                    Elements elements1 = document1.select("#__next > main");
                    for (Element element1 : elements1) {
                        String text = element1.select("img").attr("src");
                        String title=element1.select("h1.DramaDetail_bookName__7bcjB").text();
                        String details=element1.select(".DramaDetail_intro__O7jEz").text();
                        //分集
                        Elements elements2 = element1.select("pcDrama_catalogItem__4Q_C0");
                        System.setProperty("webdriver.chrome.driver", chromedriverPath);
                        ChromeDriver chromeDriver = new ChromeDriver();
                        for (Element element2 :elements2 ){
                            String href1 = element2.select("a.pcDrama_catalogItem__4Q_C0").attr("href");
                            String videoUrl = "https://www.kuaikaw.cn" + href1;
                            String url2 = videoUrl;

                            chromeDriver.get(url2);
                            List<WebElement> elements3 = chromeDriver.findElements(By.cssSelector("#playVideo > video"));
                            for (WebElement element3:elements3){
                                String src = element3.getAttribute("src");
                                download(src,destinationPath);
                            }
                        }



                    }
                }
            }
            System.out.println(imglist);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 通过视频的URL下载该视频并存入本地
     *
     * @param url      视频的URL
     * @param destFile 视频存入的位置
     * @throws IOException
     */
    public static void download(String url, String destFile) throws IOException {
        URL videoUrl = new URL(url);

        InputStream is = videoUrl.openStream();
        FileOutputStream fos = new FileOutputStream(destFile);

        int len = 0;
        byte[] buffer = new byte[1024];
        while ((-1) != (len = is.read(buffer))) {
            fos.write(buffer, 0, len);
            System.out.println("成功");
        }
        fos.flush();

        if (null != fos) {
            fos.close();
        }

        if (null != is) {
            is.close();
        }
    }



}
