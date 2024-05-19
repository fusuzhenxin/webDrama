package net.xdclass.video.service.Impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.xdclass.video.conf.DownloadProgressManager;
import net.xdclass.video.entity.Details;
import net.xdclass.video.entity.FileOne;
import net.xdclass.video.entity.Images;
import net.xdclass.video.entity.Video;
import net.xdclass.video.main.M3u8Main;
import net.xdclass.video.mapper.DetailsMapper;
import net.xdclass.video.mapper.FileMapper;
import net.xdclass.video.mapper.ImagesMapper;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private ImagesMapper imagesMapper;

    @Autowired
    private FileMapper fileMapper;

    //静态文件路径
    private static final String FILE_UPLOAD_PATH = getProperty("user.dir")
            + File.separator
            + "src" + File.separator
            + "main" + File.separator
            + "resources"
            + File.separator
            + "files" + File.separator
            + "image" + File.separator;

    private static final String FILE_UPLOAD_PATH1 = getProperty("user.dir")
            + File.separator
            + "src" + File.separator
            + "main" + File.separator
            + "resources"
            + File.separator
            + "files" + File.separator
            + "video" + File.separator;

    @Override
    public Integer seleteDiversitys(String name) {
       return videoMapper.seleteDiversitys(name);
    }

    @Override
    public String  seleteEpisodeUrl(String videoName, Integer episodeNumber) {

        return videoMapper.seleteEpisodeUrl(videoName,episodeNumber);
    }

    @Value("${chromedriverPath}")
    public String chromedriverPath;




    private AtomicInteger downloadedEpisodes = new AtomicInteger(0); // 已下载的集数计数器
    private AtomicInteger downloadedEpisodes1 = new AtomicInteger(1); // 已下载的集数计数器
    private WebDriver chromeDriver; // 用于关闭ChromeDriver

    @SneakyThrows
    public void saves(String name, String taskId) {
        try {

                String urls = "https://www.kuaikaw.cn/search/?searchValue=" + name;
                out.println("正在爬取：" + urls);
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
                        String role;
                        //剧名
                        String title = element1.select("h1.DramaDetail_bookName__7bcjB").text();
                        //分类
                        String classify = element1.select("a.DramaDetail_tagItem__L546Y").text();
                        //图片url
                        String imgUrl = element1.select("img.DramaDetail_bookCover__mvLQU").attr("src");
                        //主演
                        String actors=element1.select("a.TagBookList_bookAuthor__GAd_h > span").text();
                        String url="https://www.kuaikaw.cn"+imgUrl;
                        //详情
                        String details = element1.select(".DramaDetail_intro__O7jEz").text();

                        if (actors==null){
                            role="";
                        }else {
                            role=actors;
                        }
                        out.println(url+":"+title+":"+details+":"+classify);
                        imgUrl(url,title,classify,role,details);
                        //分集类名
                        Elements elements2 = element1.select(".pcDrama_catalogItem__4Q_C0");
                        for (Element element2 : elements2) {
                            if (downloadedEpisodes.get() >= 5) { // 限制下载5集
                                out.println("已下载5集，结束下载。");
                                downloadedEpisodes.set(0);
                                break; // 继续下一个剧
                            }
                            //每一集页面url
                            String href1 = element2.select("a.pcDrama_catalogItem__4Q_C0").attr("href");
                            String videoUrl = "https://www.kuaikaw.cn" + href1;
                            setProperty("webdriver.chrome.driver", chromedriverPath);
                            chromeDriver = new ChromeDriver();
                            chromeDriver.get(videoUrl);

                            // 等待页面加载完成
                            waitForPageLoad(chromeDriver);

                            List<WebElement> elements3 = chromeDriver.findElements(By.cssSelector("#playVideo > video"));
                            for (WebElement element3 : elements3) {
                                //视频url
                                String Url = element3.getAttribute("src");
                                out.println(Url);
                                    download(Url, title, taskId ,() -> {
                                        // 重置进度
                                        DownloadProgressManager.setProgress(taskId, 0);
                                        if (downloadedEpisodes.incrementAndGet() >= 5) {
                                            chromeDriver.close();
                                        }
                                    });
                            }
                    }
                }
            }
        }finally {
            chromeDriver.quit(); // 确保在所有操作完成后关闭ChromeDriver
        }
    }

    @SneakyThrows
    public  void download(String url, String title,String taskId,  Runnable onComplete) throws IOException {

        // 将标题转换为拼音
        String pinyin = convertToPinyin(title);
        // 根据拼音创建文件夹
        String destDir = FILE_UPLOAD_PATH1 + File.separator + pinyin;
        File dir = new File(destDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String UUID = IdUtil.fastSimpleUUID() + StrUtil.DOT + "mp4";
        String destinationPath = destDir + File.separator + UUID; // 本地文件路径
        URL videoUrl = new URL(url);



        // 获取文件大小
        HttpURLConnection connection = (HttpURLConnection) videoUrl.openConnection();
        connection.setRequestMethod("HEAD");
        long totalSize = connection.getContentLengthLong();
        connection.disconnect();

        long fileSize = 0;
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        try (InputStream is = videoUrl.openStream();
             FileOutputStream fos = new FileOutputStream(destinationPath)) {
            int len;
            byte[] buffer = new byte[1024];
            long downloadedSize = 0;
            while ((len = is.read(buffer)) != -1) {

                downloadedSize += len; // 更新已下载大小
                fileSize += len; // 更新文件大小
                md5Digest.update(buffer, 0, len);


            }
        }
            String md5 = bytesToHex(md5Digest.digest());
            FileOne videoByMd5 = getVideoByMd5(md5);
            String VideoUrl;
            if (videoByMd5!=null){
                VideoUrl = videoByMd5.getUrl();
                boolean exist = FileUtil.exist(FILE_UPLOAD_PATH1 +pinyin+File.separator+ VideoUrl.substring(VideoUrl.lastIndexOf("/") + 1));
                if (!exist){
                    downloadAndSaveFile(videoUrl, destinationPath, md5Digest, fileSize,totalSize,taskId);
                    VideoUrl="http://localhost:9090/files/video/"+pinyin+ "/" + UUID;
                }
            }else {
                downloadAndSaveFile(videoUrl, destinationPath, md5Digest, fileSize,totalSize,taskId);
                VideoUrl="http://localhost:9090/files/video/"+pinyin+ "/" + UUID;
            }

            //分集排序
            int maxIndex=getMaxIndexForName(title);
            int diversity=maxIndex+1;
            Integer detailsId = getDetailsId(title);
            out.println(detailsId);
            FileOne files=new FileOne();
            files.setName(title);
            files.setUrl(VideoUrl);
            files.setDetailsId(detailsId);
            files.setMd5(md5);
            files.setSize(fileSize);
            files.setType("mp4");
            files.setOriginalFilename(url);
            files.setDiversity(String.valueOf(diversity));
            fileMapper.insert(files);
            onComplete.run();
            out.println("下载成功");
        }
    private void downloadAndSaveFile(URL videoUrl, String destinationPath, MessageDigest md5Digest, long fileSize,long totalSize,String taskId) throws IOException {
        try (InputStream down = videoUrl.openStream();
             FileOutputStream fos = new FileOutputStream(destinationPath)) {
            int len;
            byte[] buffer = new byte[1024];
            long downloadedSize = 0;
            while ((len = down.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                downloadedSize += len; // 更新已下载大小
                fileSize += len; // 确保文件大小被正确更新
                md5Digest.update(buffer, 0, len);

                // 计算进度并发送到前端
                double progress = (double) downloadedSize / totalSize * 100;
                DecimalFormat df = new DecimalFormat("#.###");
                progress = Double.parseDouble(df.format(progress));
                DownloadProgressManager.setProgress(taskId, progress);
                out.println(progress);
            }
        }
    }


    private Integer getDetailsId(String name) {
        QueryWrapper<Details> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("name",name);
        try {
            Details details1 = detailsMapper.selectOne(queryWrapper);
            Integer detailsId = details1.getDetailsId();
            return detailsId;
        }catch (Exception e){
           e.printStackTrace();
           return null;
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

        String homeUrl = "https://v.ijujitv.cc/search/-------------.html?wd="+name;

        Document document = Jsoup.connect(homeUrl).get();
        Elements elements = document.select(".m-item");

        for (Element element : elements) {
            String href = element.select("a.thumb").attr("href");
            //每一部剧
            String twoUrl = "https://v.ijujitv.cc/" + href;
            Document document1 = Jsoup.connect(twoUrl).get();
            Elements elements1 = document1.select(".main-inner");

            for (Element element1 : elements1) {
                //剧名
                String title = element1.select("h1.title").text();
                //详情
                String introduction=element1.select("div.albumDetailMain-right > p").text();
                // 去除简介部分
                String intro = introduction.replaceFirst("^简介：\\s*", "");
                //主演
                String role=element1.select("p:nth-child(5) > a").text();
                //图片链接
                String img = element1.select("img").attr("data-original");
                String imgURL = "https://v.ijujitv.cc/" + img;
                imgUrl(imgURL, title,classify,role,intro);
                Elements elements2 = element1.select("#playlist3 > div > ul > li");

                setProperty("webdriver.chrome.driver", chromedriverPath);
                ChromeDriver chromeDriver = new ChromeDriver();

                for (Element element2 : elements2) {
                    String text = element2.select(".autowidth").text();
                    String href1 = element2.select(".autowidth").attr("href");
                    String videoUrl = "https://v.ijujitv.cc/" + href1;

                    // 判断是否是需要跳过的链接
                    if ("https://v.ijujitv.cc//m.nwtef.xyz/dl/index.html?sc=1001_dc_xk&srcPlay=play".equals(videoUrl)) {
                        continue; // 跳过当前循环
                    }
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
                                //原视频连接
                                extractedUrl = extractedUrl.substring(0, endIndex + ".m3u8".length());
                                //去掉index.m3u8这个后缀的视频链接
                                String modifiedUrl = removeLastSegment(extractedUrl);
                                //检测第一个index.m3u8是只有三行的话返回thirdLine，不然返回null
                                String thirdLine = getThirdLine(extractedUrl);
                                String newVideoUrl;
                                if (thirdLine == null) {
                                    newVideoUrl = extractedUrl;
                                } else {
                                    newVideoUrl = modifiedUrl + thirdLine;
                                }

                                M3u8Main.downloadM3u8Video(newVideoUrl, title, extractedUrl,fileService, () -> downloadNextVideo(chromeDriver, elements2, element2,title));
                                return;
                            }
                        }
                    }
                }
            }
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

                setProperty("webdriver.chrome.driver", chromedriverPath);
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
    public Map<String, Object> downLoadImage(String imageUrl, String destinationPath, String UUID) {
        Map<String, Object> result = new HashMap<>();

        try {
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false); // 禁用缓存

            // 第一次打开连接，计算MD5哈希
            byte[] md5Bytes;
            try (InputStream inputStream = connection.getInputStream();
                 DigestInputStream digestInputStream = new DigestInputStream(inputStream, MessageDigest.getInstance("MD5"))
            ){
                byte[] bytes = new byte[4096];
                while (digestInputStream.read(bytes) != -1) {
                    // 读取流以计算哈希
                }
                md5Bytes = digestInputStream.getMessageDigest().digest();
            }

            String md5 = bytesToHex(md5Bytes);

            // 检查是否存在相同MD5的文件
            Images fileByMd5 = getFileByMd5(md5);
            String cover;
            if (fileByMd5 != null) {
                cover = fileByMd5.getCover();
                boolean exist = FileUtil.exist(FILE_UPLOAD_PATH + cover.substring(cover.lastIndexOf("/") + 1));
                if (!exist) {
                    // 重新打开连接以下载文件
                    connection = url.openConnection();
                    connection.setUseCaches(false);
                    try (InputStream downloadInputStream = connection.getInputStream();
                         OutputStream outputStream = new FileOutputStream(destinationPath)) {
                        byte[] bytes = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = downloadInputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, bytesRead);
                        }
                    }
                    cover = "http://localhost:9090/files/image/" + UUID;
                }
            } else {
                // 重新打开连接以下载文件
                connection = url.openConnection();
                connection.setUseCaches(false);
                try (InputStream downloadInputStream = connection.getInputStream();
                     OutputStream outputStream = new FileOutputStream(destinationPath)) {
                    byte[] bytes = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = downloadInputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, bytesRead);
                    }
                }
                cover = "http://localhost:9090/files/image/" + UUID;
            }

            String fileName = new File(imageUrl).getName();
            long fileSize = new File(destinationPath).length();

            // 将值放入Map
            result.put("cover", cover);
            result.put("fileName", fileName);
            result.put("fileSize", fileSize);
            result.put("md5", md5);
            return result;

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }



    //所以图片的集合
    public void imgUrl(String imgUrl,String title ,String classify ,String role,String intro){

        String folderPath = FILE_UPLOAD_PATH;
        File file=new File(folderPath);
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
            String destinationPath = folderPath+ java.io.File.separator + UUID; // 本地文件路径
            // 将反斜杠替换为正斜杠
            String destinationPaths = destinationPath.replaceAll("\\\\", "/");
            out.println("==="+destinationPaths);
            try {
                String  url="http://localhost:9090/files/image/"+UUID;
                //下载

                Map<String, Object> stringObjectMap = downLoadImage(imgUrl, destinationPaths,UUID);
                String fileName = (String) stringObjectMap.get("fileName");
                long fileSize = (long) stringObjectMap.get("fileSize");
                String md5 = (String) stringObjectMap.get("md5");
                String cover = (String) stringObjectMap.get("cover");
                out.println("========================================="+cover);
                Images images=new Images();
                images.setCover(cover);
                images.setSize(fileSize);
                images.setOriginalFilename(fileName);
                images.setType(type);
                images.setMd5(md5);
                images.setName(title);
                imagesMapper.insert(images);

                Details details=new Details();
                details.setDescription(intro);
                details.setName(title);
                details.setClassify(classify);
                details.setActors(role);
                details.setCover(cover);
                details.setActors(role);
                details.setCollect("1000");
                details.setQuantity("2000");
                detailsMapper.insert(details);
                out.println("Image downloaded successfully: " + destinationPaths);
            } catch (Exception e) {
                err.println("Error downloading image: " + e.getMessage());
        }
    }
    private Images getFileByMd5(String md5) {
        QueryWrapper<Images> queryWrapper= new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<Images> filesList=imagesMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);

    }

    private FileOne getVideoByMd5(String md5) {
        QueryWrapper<FileOne> queryWrapper= new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<FileOne> filesList=fileMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);

    }

    //这个方法的作用就是检测这个视频是有一个索引文件还是有两个，
    // 一个的话第一个就是有切片文件的，两个文件第二个才是索引文件，要获取第一个索引文件第三行拼成第二行请求url
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

    // 去掉index.m3u8这个后缀的视频链接
    public static String removeLastSegment(String original) {
        int lastSlashIndex = original.lastIndexOf("/");
        if (lastSlashIndex != -1) {
            return original.substring(0, lastSlashIndex + 1);
        }
        return original;
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
    //生成MD5
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
