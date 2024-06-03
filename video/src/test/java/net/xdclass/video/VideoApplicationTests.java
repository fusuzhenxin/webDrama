package net.xdclass.video;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;

import org.openqa.selenium.devtools.v124.network.Network;
import org.openqa.selenium.devtools.v124.network.model.RequestId;
import org.openqa.selenium.devtools.v124.network.model.Response;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.*;

@SpringBootTest
class VideoApplicationTests {

    @SneakyThrows
    @Test
    void contextLoads() {
        try {
            for (int page = 0; page < 250; page += 25) { // 每页显示25个电影，总共10页
                String url = "https://movie.douban.com/top250?start=" + page;
                Document document = Jsoup.connect(url).get();
                Elements element = document.select("ol.grid_view li");

                for (Element elements : element) {

                    String title = elements.select("span.title").text();
                    String href = elements.select("div.pic a img").attr("src");
                    System.out.println("Title: " + title);
                    System.out.println("Link: " + href);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Test
    void contextLoad() {
        try {
            List<String> imglist = new ArrayList<String>();
            for (int page = 1; page < 2; page++) {
                //逆袭分类的url
                String url = "https://www.kuaikaw.cn/browse/417-464" + "/" + page;
                Document document = Jsoup.connect(url).get();
                //类名
                Elements element = document.getElementsByClass("BrowseList_itemBox__S5QRX");
                for (Element elements : element) {
                    String href = elements.select("a.image_imageScaleBox__JFwzM.BrowseList_imageBox__fPYH7").attr("href");
                    String url1 = "https://www.kuaikaw.cn" + href;
                    Document document1 = Jsoup.connect(url1).get();
                    Elements elements1 = document1.getElementsByClass("pcDrama_catalogItem__4Q_C0");
                    for (Element element1 : elements1) {
                        String text = element1.select("img").attr("src");
                        //分集
                        String text2 = element1.select("a.pcDrama_catalogItem__4Q_C0").text();
                        //分集的视频url的部分
                        String href1 = element1.select("a.pcDrama_catalogItem__4Q_C0").attr("href");
                        String videoUrl = "https://www.kuaikaw.cn" + href1;
//                        System.out.println("分集："+text2+":"+videoUrl);

                        String url2 = videoUrl;
                        Document document2 = Jsoup.connect(url2).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 Edg/124.0.0.0").get();
//                        Elements elements2 = document2.select();
//
//                        System.out.println(document2);
//                        System.out.println(elements2);
//                        for (Element element2:elements2){
//                            String attr = element2.select("video").text();
//
////                            System.out.println("视频url"+attr);
//                        }


                    }
                    String attr = elements.select("img").attr("src");
                    String attr1 = "https://www.kuaikaw.cn/" + attr;
                    String attr2 = elements.select("img").attr("alt");
                    String text = elements.select("a.BrowseList_tagItem__fAz6w").text();
                    String text1 = elements.select("a.BrowseList_bookIntro__mp8fu").text();
//                    System.out.println(text+attr2+":"+attr1);
                    imglist.add(attr1);

                }
            }
            System.out.println(imglist);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 存储图片到本地的方法


    //    @Value("${chromedriverPath}")
//    public String chromedriverPath;
    @SneakyThrows
    @Test
    void contextLoad2() {
//        https://v.ijujitv.cc/show/1--------1---.html
        for (int page = 1; page <= 1; page++) {
            String url = "https://v.ijujitv.cc/show/1--------" + page + "---.html";
            Document document = Jsoup.connect(url).get();
            Elements elements = document.getElementsByClass("m-item");
            for (Element element : elements) {
                String attr = element.select("a.thumb").attr("href");
                String url2 = "https://v.ijujitv.cc" + attr;
                Document document1 = Jsoup.connect(url2).get();
                Elements elements1 = document1.getElementsByClass("autowidth");
                //设置驱动地址
                setProperty("webdriver.chrome.driver", chromedriverPath);
                ChromeDriver chromeDriver = new ChromeDriver();
                for (Element element1 : elements1) {
                    String href = element1.select("a.autowidth").attr("href");
                    String text3 = element1.select("a.autowidth").text();
                    String url3 = "https://v.ijujitv.cc" + href;

                    // 判断是否是需要跳过的链接
                    if ("https://v.ijujitv.cc//m.nwtef.xyz/dl/index.html?sc=1001_dc_xk&srcPlay=play".equals(url3)) {
                        continue; // 跳过当前循环
                    }

                    chromeDriver.get(url3);
                    List<WebElement> elements3 = chromeDriver.findElements(By.cssSelector("#playleft > iframe"));
                    for (WebElement element2 : elements3) {
                        String src = element2.getAttribute("src");
                        System.out.println(src);

                    }


                }
            }
        }

    }


    //    @Value("${chromedriverPath}")
//    public String chromedriverPath;
    @SneakyThrows
    @Test
    void contextLoad4() {

        for (int page = 1; page <= 1; page++) {
            String HomeUrl = "https://v.ijujitv.cc/show/9--------" + page + "---.html";
            //最外层url
            Document document = Jsoup.connect(HomeUrl).get();
            //最外层类名
            Elements elements = document.getElementsByClass("m-item");
            //遍历每一部剧
            for (Element element : elements) {
                //跳转到每一个剧的详情页的urL,第二层url
                String href = element.select("a.thumb").attr("href");
                String twoUrl = "https://v.ijujitv.cc/" + href;
                Document document1 = Jsoup.connect(twoUrl).get();
                //第二层的整个页面类名，可以爬取图片和名称，详情，用选择器才能一步一步往里取类名，直接类名是不能往里套的
                Elements elements1 = document1.select(".main-inner");

                //遍历每一个 详情页
                for (Element element1 : elements1) {
                    //详情，名称,封面
                    String title = element1.select("h1.title").text();
                    String img = element1.select("img").attr("data-original");
                    String imgURL = "https://v.ijujitv.cc/" + img;
//                    imgUrl(imgURL,title);
//                    System.out.println(title+":"+imgURL);
                    //遍历每一部的分集li
                    Elements elements2 = element1.select("#playlist3 > div > ul > li");
//                    设置驱动地址
                    setProperty("webdriver.chrome.driver", chromedriverPath);
                    ChromeDriver chromeDriver = new ChromeDriver();
                    for (Element element2 : elements2) {
                        //跳转到每一集里面的url界面
                        String text = element2.select(".autowidth").text();
                        String href1 = element2.select(".autowidth").attr("href");
                        String videoUrl = "https://v.ijujitv.cc/" + href1;
                        // 判断是否是需要跳过的链接
                        if ("https://v.ijujitv.cc//m.nwtef.xyz/dl/index.html?sc=1001_dc_xk&srcPlay=play".equals(videoUrl)) {
                            continue; // 跳过当前循环
                        }

                        chromeDriver.get(videoUrl);
                        List<WebElement> elements3 = chromeDriver.findElements(By.cssSelector("#playleft > iframe"));
                        for (WebElement element3 : elements3) {
                            String VideoUrl = element3.getAttribute("src");
                            videoUrl(videoUrl, title);
                            System.out.println(text + ":" + VideoUrl);
                        }
                    }
                }
            }
        }
    }

    //图片下载
    @SneakyThrows
    public static void downLoadImage(String imgeUrl, String destinationPath) {
        URL url = new URL(imgeUrl);
        URLConnection connection = url.openConnection();
        try (
                InputStream inputStream = connection.getInputStream();
                OutputStream outputStream = new FileOutputStream(destinationPath)) {
            byte[] bytes = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, bytesRead);
            }
        }
    }

    /**
     * 通过视频的URL下载该视频并存入本地
     *
     * @param url      视频的URL
     * @param destFile 视频存入的位置
     * @throws IOException
     */
    public static void downloadVideo(String url, File destFile) throws IOException {
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


    //静态文件路径
    private static final String FILE_UPLOAD_PATH = getProperty("user.dir")
            + File.separator
            + "src" + File.separator
            + "main" + File.separator
            + "resources"
            + File.separator
            + "image" + File.separator;

    //所以图片的集合
    public void imgUrl(String imgUrl, String title) {
        List<String> imgList = new ArrayList<String>();
        List<String> titleList = new ArrayList<String>();
        titleList.add(title);
        imgList.add(imgUrl);
        String folderPath = FILE_UPLOAD_PATH;
        System.out.println(folderPath);
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        for (int i = 0; i < imgList.size(); i++) {
            String imageUrl = imgList.get(i);
            String titles = titleList.get(i);
            // 获取文件扩展名
            String type = imageUrl.substring(imageUrl.lastIndexOf(".") + 1);
            String UUID = IdUtil.fastSimpleUUID() + StrUtil.DOT + type;
            String destinationPath = folderPath + File.separator + UUID; // 本地文件路径
            // 将反斜杠替换为正斜杠
            String destinationPaths = destinationPath.replaceAll("\\\\", "/");
            System.out.println("===" + destinationPaths);
            try {
                downLoadImage(imageUrl, destinationPaths);
                System.out.println("Image downloaded successfully: " + destinationPaths);
            } catch (Exception e) {
                System.err.println("Error downloading image: " + e.getMessage());
            }
        }
    }

    public void videoUrl(String videoUrl, String title) {
        List<String> videoList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        titleList.add(title);
        videoList.add(videoUrl);
        String folderPath = FILE_UPLOAD_PATH;
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        for (int i = 0; i < videoList.size(); i++) {
            String videourl = videoList.get(i);
            String titles = titleList.get(i);
        }

    }

    @Value("${chromedriverPath}")
    public String chromedriverPath;

    @Test
    void crawlGetTest() {
        // 设置驱动地址
        System.setProperty("webdriver.chrome.driver", chromedriverPath);
        WebDriver chromeDriver = new ChromeDriver();
        chromeDriver.get("https://hant.cc/play/94025-0-0.html");

        // 获取页面中的所有 script 标签
        WebElement scriptElement = chromeDriver.findElement(By.tagName("script"));

        // 获取 script 标签内的文本内容
        String scriptContent = scriptElement.getAttribute("innerHTML");

        // 使用正则表达式提取视频链接
        String videoUrl = extractVideoUrl(scriptContent);
        System.out.println("视频链接: " + videoUrl);
        System.out.println(scriptElement);
        chromeDriver.quit();
    }

    private String extractVideoUrl(String scriptContent) {
        String videoUrl = "";

        // 使用正则表达式提取视频链接
        Pattern pattern = Pattern.compile("\"url\":\\s*\"(.*?)\"");
        Matcher matcher = pattern.matcher(scriptContent);
        if (matcher.find()) {
            videoUrl = matcher.group(1);
        }

        return videoUrl;
    }


    @SneakyThrows
    @Test
    void crawlGetTest2() {


//            String HomeUrl="https://v.ijujitv.cc/show/9--------"+page+"---.html";
        //最外层url
        String HomeUrl = "https://www.naifei.art/vodtype/ribenju-2.html";
        Document document = Jsoup.connect(HomeUrl).get();
        //最外层类名
        Elements elements = document.getElementsByClass("module-item");
        //遍历每一部剧
        for (Element element : elements) {
            //跳转到每一个剧的详情页的urL,第二层url
            String href = element.select("a").attr("href");

            Document document1 = Jsoup.connect(href).get();
            //第二层的整个页面类名，可以爬取图片和名称，详情，用选择器才能一步一步往里取类名，直接类名是不能往里套的
            Elements elements1 = document1.select(".content");

            //遍历每一个 详情页
            for (Element element1 : elements1) {
                //详情，名称,封面
                String title = element1.select("h1.page-title").text();
                String img = element1.select("img").attr("src");

                Elements elements2 = element1.select("#sort-item-7 a");
                // 设置驱动地址
                setProperty("webdriver.chrome.driver", chromedriverPath);
                ChromeDriver chromeDriver = new ChromeDriver();
                chromeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // 设置等待时间
                for (Element element2 : elements2) {
                    //跳转到每一集里面的url界面
                    String text = element2.attr("title");
                    String href1 = element2.attr("href");
                    String videoUrl = "https://www.naifei.art" + href1;


                    chromeDriver.get(videoUrl);
                    List<WebElement> elements3 = chromeDriver.findElements(By.cssSelector("#playleft > iframe"));
                    for (WebElement element3 : elements3) {
                        //视频url
                        String VideoUrl = element3.getAttribute("src");

                        System.out.println(VideoUrl);
                    }
                }
            }


        }
    }

    @Test
    void crawlGetTest3() {
        // 设置ChromeDriver的路径
        System.setProperty("webdriver.chrome.driver", chromedriverPath);

        // 设置Chrome选项
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 无头模式，不显示浏览器界面
        options.addArguments("--disable-gpu"); // 禁用GPU加速
        options.addArguments("--no-sandbox"); // 以非沙盒模式启动
        options.addArguments("--disable-dev-shm-usage"); // 解决资源受限的问题

        // 创建ChromeDriver对象
        WebDriver driver = new ChromeDriver(options);

        try {
            // 打开网页
            driver.get("https://v.ijujitv.cc/play/35236-2-1.html8");

            // 等待几秒以确保页面完全加载和JavaScript执行完成
            Thread.sleep(5000); // 等待5秒

            // 获取渲染后的页面内容
            String content = driver.getPageSource();
            System.out.println(content);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭浏览器
            driver.quit();
        }
    }

    @SneakyThrows
    @Test

    void crawlGetTest4() {
        // 设置ChromeDriver的路径
        System.setProperty("webdriver.chrome.driver", chromedriverPath);

        // 设置Chrome选项
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 无头模式

        // 初始化WebDriver
        WebDriver driver = new ChromeDriver(options);

        // 启用DevTools
        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        // 启用网络跟踪
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // 监听网络响应
        devTools.addListener(Network.responseReceived(), responseReceived -> {
            Response response = responseReceived.getResponse();
            RequestId requestId = responseReceived.getRequestId();
            if (response.getUrl().contains("desired_endpoint")) { // 检查请求的URL
                String responseBody = devTools.send(Network.getResponseBody(requestId)).getBody();
                System.out.println("URL: " + response.getUrl());
                System.out.println("Status: " + response.getStatus());
                System.out.println("Response: " + responseBody);
            }
        });

        try {
            // 打开网页
            driver.get("https://v.ijujitv.cc/play/35236-2-1.html");

            // 等待页面加载
            WebElement dynamicElement = new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.presenceOfElementLocated(By.id("dynamicContentId")));

            // 获取页面源代码
            String pageSource = driver.getPageSource();
            System.out.println(pageSource);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭浏览器
            driver.quit();
        }
    }
}
