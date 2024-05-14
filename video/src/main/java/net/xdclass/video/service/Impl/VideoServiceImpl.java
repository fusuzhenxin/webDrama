package net.xdclass.video.service.Impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.xdclass.video.entity.Details;
import net.xdclass.video.entity.FileOne;
import net.xdclass.video.entity.Video;
import net.xdclass.video.main.M3u8Main;
import net.xdclass.video.mapper.DetailsMapper;
import net.xdclass.video.mapper.FileMapper;
import net.xdclass.video.mapper.VideoMapper;
import net.xdclass.video.service.FileService;
import net.xdclass.video.service.VideoService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.*;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper , Video> implements VideoService {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private DetailsMapper detailsMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileMapper fileMapper;

    //静态文件路径
    private static final String FILE_UPLOAD_PATH = getProperty("user.dir")
            + java.io.File.separator
            + "src" + java.io.File.separator
            + "main" + java.io.File.separator
            + "resources"
            + java.io.File.separator
            + "files" + java.io.File.separator
            + "image" + java.io.File.separator;

    private static final String FILE_UPLOAD_PATH1 = getProperty("user.dir")
            + java.io.File.separator
            + "src" + java.io.File.separator
            + "main" + java.io.File.separator
            + "resources"
            + java.io.File.separator
            + "files" + java.io.File.separator
            + "video" + java.io.File.separator;

    @Override
    public Integer seleteDiversitys(String name) {
       return videoMapper.seleteDiversitys(name);
    }

    @Override
    public String seleteEpisodeUrl(String videoName, Integer episodeNumber) {

        return videoMapper.seleteEpisodeUrl(videoName,episodeNumber);
    }

    @Value("${chromedriverPath}")
    public String chromedriverPath;




    private AtomicInteger downloadedEpisodes = new AtomicInteger(0); // 已下载的集数计数器
    private AtomicInteger downloadedEpisodes1 = new AtomicInteger(1); // 已下载的集数计数器
    private WebDriver chromeDriver; // 用于关闭ChromeDriver

    public void saves(String name) {
        try {

                String urls = "https://www.kuaikaw.cn/search/?searchValue=" + name;
                System.out.println("正在爬取：" + urls);
                //最外面的url
                Document document = Jsoup.connect(urls).get();
                Elements element = document.select(".TagBookList_tagItem__xL2IA");
                for (Element elements : element) {
                    //第二页详情页url
                    String href = elements.select("a.TagBookList_totalChapterNum__CHaGs").attr("href");
                    String url1 = "https://www.kuaikaw.cn" + href;
                    Document document1 = Jsoup.connect(url1).get();

                    Elements elements1 = document1.select("#__next > main");
                    for (Element element1 : elements1) {

                        //剧名
                        String title = element1.select("h1.DramaDetail_bookName__7bcjB").text();
                        String classify = element1.select("a.DramaDetail_tagItem__L546Y").text();
                        String imgUrl = element1.select("img.DramaDetail_bookCover__mvLQU").attr("src");
                        String url="https://www.kuaikaw.cn"+imgUrl;
                        String details = element1.select(".DramaDetail_intro__O7jEz").text();

                        String role="";
                        out.println(url+":"+title+":"+details+":"+classify);
                        imgUrl(url,title,classify,role,details);
                        //分集类名
                        Elements elements2 = element1.select(".pcDrama_catalogItem__4Q_C0");
                        for (Element element2 : elements2) {
                            if (downloadedEpisodes.get() >= 5) { // 限制下载5集
                                System.out.println("已下载5集，结束下载。");
                                downloadedEpisodes.set(0);
                                return;
                            }
                            //每一集url
                            String href1 = element2.select("a.pcDrama_catalogItem__4Q_C0").attr("href");
                            String videoUrl = "https://www.kuaikaw.cn" + href1;
                            System.setProperty("webdriver.chrome.driver", chromedriverPath);
                            chromeDriver = new ChromeDriver();
                            chromeDriver.get(videoUrl);

                            // 等待页面加载完成
                            waitForPageLoad(chromeDriver);

                            List<WebElement> elements3 = chromeDriver.findElements(By.cssSelector("#playVideo > video"));
                            for (WebElement element3 : elements3) {
                                String src = element3.getAttribute("src");
                                System.out.println(src);
                                download(src, title, () -> {
                                    if (downloadedEpisodes.incrementAndGet() >= 5) {
                                        chromeDriver.close();

                                    }
                                });
                            }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 辅助方法：将中文标题转换为拼音
    public static String convertToPinyin(String chinese) {
        StringBuilder pinyin = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
                if (pinyinArray != null && pinyinArray.length > 0) {
                    // 拼接所有拼音结果（去除声调）
                    pinyin.append(pinyinArray[0].replaceAll("\\d", ""));
                }
            } else {
                pinyin.append(c);
            }
        }
        return pinyin.toString();
    }

    @SneakyThrows
    public  void download(String url, String title,  Runnable onComplete) throws IOException {

        // 将标题转换为拼音
        String pinyin = convertToPinyin(title);
        // 根据拼音创建文件夹
        String destDir = FILE_UPLOAD_PATH1 + java.io.File.separator + pinyin;
        java.io.File dir = new java.io.File(destDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String UUID = IdUtil.fastSimpleUUID() + StrUtil.DOT + "mp4";
        String destinationPath = destDir + java.io.File.separator + UUID; // 本地文件路径
        URL videoUrl = new URL(url);
        long fileSize = 0;
        try (InputStream is = videoUrl.openStream();
             FileOutputStream fos = new FileOutputStream(destinationPath)) {
            int len;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fileSize += len;
            }
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            byte[] mdBytes=md5Digest.digest();
            String md5 = bytesToHex(mdBytes);
            String VideoUrl="http://localhost:9090/files/video/"+pinyin+ "/" + UUID;
            int maxIndex=getMaxIndexForName(title);
            int diversity=maxIndex+1;
            FileOne files=new FileOne();
            files.setName(title);
            files.setUrl(VideoUrl);
            files.setMd5(md5);
            files.setSize(fileSize);
            files.setType("mp4");
            files.setOriginalFilename(url);
            files.setDiversity(String.valueOf(diversity));
            fileMapper.insert(files);
            onComplete.run();
            System.out.println("下载成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private int getMaxIndexForName(String name){
        //创建一个查询条件， QueryWrapper包装器构建查询条件
        QueryWrapper<FileOne> queryWrapper=new QueryWrapper<>();
        //比较名字获取与这个名字相同的数据
        queryWrapper.eq("name",name);
        queryWrapper.orderByDesc("diversity");
        List<FileOne> filesList=fileService.selectList(queryWrapper);
        //文件空不是数据库空，返回一个0
        if (filesList.isEmpty()){
            return 0;
        }else {
            int maxIndex=0;
            //遍历出最大序号
            for (FileOne files: filesList) {
                int i = Integer.parseInt(files.getDiversity());
                if (i>maxIndex){
                    maxIndex=i;
                }
            }
            return maxIndex;
        }
    }

    @SneakyThrows
    public void saveList(String name, String classify) {
        try {
            String homeUrl = "https://v.ijujitv.cc/search/-------------.html?wd=" + name;
            System.out.println("正在爬取：" + homeUrl);

            Document document = Jsoup.connect(homeUrl).get();
            Elements elements = document.select(".m-item");

            for (Element element : elements) {
                String href = element.select("a.thumb").attr("href");
                String twoUrl = "https://v.ijujitv.cc/" + href;
                Document document1 = Jsoup.connect(twoUrl).get();
                Elements elements1 = document1.select(".main-inner");

                for (Element element1 : elements1) {
                    String title = element1.select("h1.title").text();
                    String intro = element1.select("div.albumDetailMain-right > p").text();
                    String role = element1.select("p:nth-child(5) > a").text();
                    String img = element1.select("img").attr("data-original");
                    String imgURL = "https://v.ijujitv.cc/" + img;
                    imgUrl(imgURL, title, classify, role, intro);
                    Elements elements2 = element1.select("#playlist3 > div > ul > li");

                    for (Element element2 : elements2) {
                        if (downloadedEpisodes1.get() >= 3) {
                            System.out.println("已下载 " + 3 + " 集，结束下载。");
                            return;
                        }

                        String text = element2.select(".autowidth").text();
                        String href1 = element2.select(".autowidth").attr("href");
                        String videoUrl = "https://v.ijujitv.cc/" + href1;

                        if ("https://v.ijujitv.cc//m.nwtef.xyz/dl/index.html?sc=1001_dc_xk&srcPlay=play".equals(videoUrl)) {
                            continue;
                        }

                        System.setProperty("webdriver.chrome.driver", chromedriverPath);
                        ChromeDriver chromeDriver = new ChromeDriver();
                        chromeDriver.get(videoUrl);
                        waitForPageLoad(chromeDriver);

                        List<WebElement> elements3 = chromeDriver.findElements(By.cssSelector("#playleft > iframe"));
                        for (WebElement element3 : elements3) {
                            String VideoUrl = element3.getAttribute("src");
                            int startIndex = VideoUrl.indexOf("url=");
                            if (startIndex != -1) {
                                String extractedUrl = VideoUrl.substring(startIndex + "url=".length());
                                int endIndex = extractedUrl.indexOf(".m3u8");
                                if (endIndex != -1) {
                                    extractedUrl = extractedUrl.substring(0, endIndex + ".m3u8".length());
                                    String modifiedUrl = removeLastSegment(extractedUrl);
                                    String thirdLine = getThirdLine(extractedUrl);
                                    String newVideoUrl;
                                    if (thirdLine == null) {
                                        newVideoUrl = extractedUrl;
                                    } else {
                                        newVideoUrl = modifiedUrl + thirdLine;
                                    }

                                    M3u8Main.downloadM3u8Video(newVideoUrl, title, extractedUrl, fileService, () -> {
                                        if (downloadedEpisodes1.incrementAndGet() >= 3) {
                                            out.println("已下载三集");
                                            chromeDriver.close();
                                            downloadedEpisodes1.set(1);
                                            out.println("你好");
                                        }
                                    });

                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void waitForPageLoad(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(webDriver ->
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    private void downloadNextVideo(WebDriver driver, Elements elements, Element currentElement,String title) {
        driver.close();

        boolean next = false;
        for (Element element2 : elements) {
            if (next) {
                //每一集视频链接
                String text = element2.select(".autowidth").text();
                String href1 = element2.select(".autowidth").attr("href");
                String videoUrl = "https://v.ijujitv.cc/" + href1;

                System.setProperty("webdriver.chrome.driver", chromedriverPath);
                ChromeDriver chromeDriver = new ChromeDriver();
                chromeDriver.get(videoUrl);

                // 等待页面加载完成
                waitForPageLoad(chromeDriver);

                List<WebElement> elements3 = chromeDriver.findElements(By.cssSelector("#playleft > iframe"));
                for (WebElement element3 : elements3) {
                    String VideoUrl = element3.getAttribute("src");
                    int startIndex = VideoUrl.indexOf("url=");
                    if (startIndex != -1) {
                        String extractedUrl = VideoUrl.substring(startIndex + "url=".length());
                        int endIndex = extractedUrl.indexOf(".m3u8");
                        if (endIndex != -1) {
                            extractedUrl = extractedUrl.substring(0, endIndex + ".m3u8".length());
                            String modifiedUrl = removeLastSegment(extractedUrl);
                            String thirdLine = getThirdLine(extractedUrl);
                            String newVideoUrl;
                            if (thirdLine == null) {
                                newVideoUrl = extractedUrl;
                            } else {
                                newVideoUrl = modifiedUrl + thirdLine;
                            }

                            M3u8Main.downloadM3u8Video(newVideoUrl, title, extractedUrl,fileService,() -> downloadNextVideo(chromeDriver, elements, element2,title));
                            return;
                        }
                    }
                }
            }
            if (element2.equals(currentElement))
                next = true;
        }
    }






//@Value("${chromedriverPath}")
//public String chromedriverPath;
//    public void saveList() {
//        for (int page = 2; page <= 3; page++) {
//            String homeUrl = "https://www.naifei.art/vodtype/ribenju-" + page + ".html";
//            Document document = Jsoup.connect(homeUrl).get();
//            Elements elements = document.getElementsByClass("module-item");
//
//            for (Element element : elements) {
//                String href = element.select("a").attr("href");
//                Document document1 = Jsoup.connect(href).get();
//                Elements elements1 = document1.select(".content");
//
//                for (Element element1 : elements1) {
//                    String title = element1.select("h1.page-title").text();
//                    String img = element1.select(".module-item-pic img").attr("src");
//                    imgUrl(img, title);
//                    System.out.println(title + ":" + img);
//
//                    Elements elements2 = element1.select("#sort-item-7 a");
//
//                    System.setProperty("webdriver.chrome.driver", chromedriverPath);
//                    ChromeDriver chromeDriver = new ChromeDriver();
//
//                    for (Element element2 : elements2) {
//                        String text = element2.attr("title");
//                        String href1 = element2.attr("href");
//                        String videoUrl = "https://www.naifei.art" + href1;
//
//                        chromeDriver.get(videoUrl);
//
//                        // 等待页面加载完成
//                        waitForPageLoad(chromeDriver);
//
//                        List<WebElement> elements3 = chromeDriver.findElements(By.cssSelector("#playleft > iframe"));
//                        for (WebElement element3 : elements3) {
//                            String VideoUrl = element3.getAttribute("src");
//                            int startIndex = VideoUrl.indexOf("url=");
//                            if (startIndex != -1) {
//                                String extractedUrl = VideoUrl.substring(startIndex + "url=".length());
//                                int endIndex = extractedUrl.indexOf(".m3u8");
//                                if (endIndex != -1) {
//                                    extractedUrl = extractedUrl.substring(0, endIndex + ".m3u8".length());
//                                    String modifiedUrl = removeLastSegment(extractedUrl);
//                                    String thirdLine = getThirdLine(extractedUrl);
//                                    String newVideoUrl;
//                                    if (thirdLine == null) {
//                                        newVideoUrl = extractedUrl;
//                                    } else {
//                                        newVideoUrl = modifiedUrl + thirdLine;
//                                    }
//
//                                    M3u8Main.downloadM3u8Video(newVideoUrl, text, () -> downloadNextVideo(chromeDriver, elements2, element2));
//                                    return;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }


    //图片下载
    @SneakyThrows
    public  String downLoadImage(String imageUrl, String destinationPath) {
        try {
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false); // 禁用缓存
            try (
                    InputStream inputStream = connection.getInputStream();
                    OutputStream outputStream = new FileOutputStream(destinationPath);
                    DigestInputStream digestInputStream = new DigestInputStream(inputStream, MessageDigest.getInstance("MD5"))) {

                byte[] bytes = new byte[4096];
                int bytesRead;
                while ((bytesRead = digestInputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, bytesRead);
                }

                byte[] md5Bytes = digestInputStream.getMessageDigest().digest();
                String md5 = bytesToHex(md5Bytes);
                return md5;
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    //所以图片的集合
    public void imgUrl(String imgUrl,String title ,String classify ,String role,String intro){

        String folderPath = FILE_UPLOAD_PATH;
        java.io.File file=new java.io.File(folderPath);
        if (!file.exists()){
            file.mkdirs();
        }

            // 获取文件扩展名
            String type;
            type = imgUrl.substring(imgUrl.lastIndexOf(".") + 1);
            if (type.length()>4){
                 type = "jpg";
            }
            String UUID = IdUtil.fastSimpleUUID() + StrUtil.DOT + type;
            String destinationPath = folderPath + java.io.File.separator + UUID; // 本地文件路径
            // 将反斜杠替换为正斜杠
            String destinationPaths = destinationPath.replaceAll("\\\\", "/");
            System.out.println("==="+destinationPaths);
            try {
                String  url="http://localhost:9090/files/image/"+UUID;
                //下载
                String md5 = downLoadImage(imgUrl, destinationPaths);
                Details details=new Details();
                details.setDescription(intro);
                details.setName(title);
                details.setClassify(classify);
                details.setActors(role);
                details.setCover(url);
                details.setMd5(md5);
                details.setCollect("1000");
                details.setQuantity("2000");
                detailsMapper.insert(details);
                System.out.println("Image downloaded successfully: " + destinationPaths);
            } catch (Exception e) {
                System.err.println("Error downloading image: " + e.getMessage());
        }
    }
    private Details getFileByMd5(String md5) {
        QueryWrapper<Details> queryWrapper= new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<Details> filesList=detailsMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);

    }

    public void  videoUrl(String modifiedUrl, String videoUrl,String title,Runnable onComplete,Elements elements, ChromeDriver chromeDriver){
        List<String> videoList =new ArrayList<>();
        List<String> titleList =new ArrayList<>();
        List<String> modifiedUrlList =new ArrayList<>();
        videoList.add(videoUrl);
        titleList.add(title);
        modifiedUrlList.add(modifiedUrl);
        for (int i=0;i<videoList.size();i++){
            String m3u81 = videoList.get(i);
            String title1 = titleList.get(i);
            String modifiedUrl1 = modifiedUrlList.get(i);
            //第一个index.m3u8文件中第三行，也就是第二个文件的不同后缀
            String thirdLine = getThirdLine(m3u81);
            String NewVideoUrl;
            if (thirdLine ==null){
                 NewVideoUrl=m3u81;
            }else {
                //第二个index.m3u8的url
                 NewVideoUrl=modifiedUrl1+thirdLine;
            }

//            System.out.println(title1+":"+NewVideoUrl);
//            M3u8Main.downloadM3u8Video(NewVideoUrl, title1, onComplete); // 触发回调函数

        }

    }

    public static String getThirdLine(String urlpath) {
        try {
            URL url = new URL(urlpath);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            int count = 0;
            while ((line = in.readLine()) != null) {
                count++;
                if (count == 3) {
                    String nextLine = in.readLine();
                    if (nextLine == null) {
                        in.close();
                        return line;
                    } else {
                        in.close();
                        return null;
                    }
                }
            }
            in.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String removeLastSegment(String original) {
        int lastSlashIndex = original.lastIndexOf("/");
        if (lastSlashIndex != -1) {
            return original.substring(0, lastSlashIndex + 1);
        }
        return original;
    }

}
