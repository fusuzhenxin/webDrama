package net.xdclass.crawler.service.Impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.xdclass.video.entity.MyData;
import net.xdclass.crawler.listener.DownloadListener;
import net.xdclass.crawler.main.M3u8Main;
import net.xdclass.crawler.service.CrawlerService;
import net.xdclass.video.config.DownloadProgressManager;
import net.xdclass.video.entity.*;
import net.xdclass.video.mapper.*;
import net.xdclass.video.service.FileService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.*;
import static java.lang.System.out;

@Service
public class CrawlerServiceImpl implements CrawlerService{
    private VideoMapper videoMapper;
    @Autowired
    private DetailsMapper detailsMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private ImagesMapper imagesMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private AcquireMapper acquireMapper;

    // 在类中声明 previousProgress 变量
    private long previousProgress = -1;

    private  Integer threadCountOne=20;
    Thread producer;
    Thread consumer;
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
    private static final String FILE_UPLOAD_PATH_IMG = getProperty("user.dir")
            + File.separator
            + "src" + File.separator
            + "main" + File.separator
            + "resources"
            + File.separator
            + "files" + File.separator
            + "images" + File.separator;

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 10, 30, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100));

    private Set<DownloadListener> listenerSet = new HashSet<>();

    private AtomicInteger downloadedEpisodes = new AtomicInteger(0); // 已下载的集数计数器
    private AtomicInteger downloadedEpisodes1 = new AtomicInteger(0); // 已下载的集数计数器
    private WebDriver chromeDriver; // 用于关闭ChromeDriver

    @Value("${chromedriverPath}")
    public String chromedriverPath;

    private  Long downloadBytes;

    @Override
    public void saveList(String name, String classify, String taskId) {
        final ArrayBlockingQueue<webDrama> queue=new ArrayBlockingQueue<>(5);
        producer=new Thread(()-> {
            try {
                ChromeOptions options=new ChromeOptions();
//                options.addArguments("--headless");
                String urls = "https://v.ijujitv.cc/search/-------------.html?wd=" + name;
                out.println("正在爬取：" + urls);
                // 最外面的url
                Document document = Jsoup.connect(urls).get();
                Elements element = document.select(".m-item");
                for (Element elements : element) {
                    // 第二页详情页url
                    String href = elements.select("a.thumb").attr("href");
                    String url1 = "https://v.ijujitv.cc/" + href;
                    Document document1 = Jsoup.connect(url1).get();

                    Elements elements1 = document1.select(".main-inner");
                    for (Element element1 : elements1) {
                        String role;
                        // 剧名
                        String title = element1.select("h1.title").text();

                        // 图片url
                        String imgUrl = element1.select("img").attr("data-original");
                        // 主演
                        String actors = element1.select("p:nth-child(5) > a").text();
                        String url = "https://v.ijujitv.cc/" + imgUrl;
                        // 详情
                        String intro = element1.select("div.albumDetailMain-right > p").text();
                        // 去除简介部分
                        String details = intro.replaceFirst("^简介：\\s*", "");

                        if (actors == null) {
                            role = "";
                        } else {
                            role = actors;
                        }
                        out.println(url + ":" + title + ":" + details + ":" + classify);
                        imgUrl(url, title, classify, role, details);
                        // 分集类名
                        Elements elements2 = element1.select("#playlist3 > div > ul > li");

                        for (Element element2 : elements2) {
                            if (downloadedEpisodes1.get() >= 1) { // 限制下载3集
                                out.println("已下载3集，结束下载。");
                                downloadedEpisodes1.set(0); // 重置计数器
                                break; // 继续下一个剧
                            }
                            // 每一集页面url
                            String href1 = element2.select(".autowidth").attr("href");
                            String videoUrl = "https://v.ijujitv.cc/" + href1;

                            // 判断是否是需要跳过的链接
                            if ("https://v.ijujitv.cc//m.nwtef.xyz/dl/index.html?sc=1001_dc_xk&srcPlay=play".equals(videoUrl)) {
                                continue; // 跳过当前循环
                            }

                            setProperty("webdriver.chrome.driver", chromedriverPath);
                            ChromeDriver chromeDriver = new ChromeDriver(options);
                            // Navigate to the video URL
                            chromeDriver.get(videoUrl);

                            // 等待页面加载完成
                            waitForPageLoad(chromeDriver);

                            List<WebElement> elements3 = chromeDriver.findElements(By.cssSelector("#playleft > iframe"));
                            //是null就直接跳出
                            for (WebElement element3 : elements3) {
                                String extractedUrl = null;
                                String newVideoUrl = null;
                                String VideoUrl = element3.getAttribute("src");
                                int startIndex = VideoUrl.indexOf("url=");
                                if (startIndex != -1) {
                                    extractedUrl = VideoUrl.substring(startIndex + "url=".length());
                                    int endIndex = extractedUrl.indexOf(".m3u8");
                                    if (endIndex != -1) {
                                        // 原视频连接
                                        extractedUrl = extractedUrl.substring(0, endIndex + ".m3u8".length());
                                        // 去掉index.m3u8这个后缀的视频链接
                                        String modifiedUrl = removeLastSegment(extractedUrl);
                                        // 检测第一个index.m3u8是只有三行的话返回thirdLine，不然返回null
                                        String thirdLine = getThirdLine(extractedUrl);

                                        if (thirdLine == null) {
                                            newVideoUrl = extractedUrl;
                                        } else {
                                            newVideoUrl = modifiedUrl + thirdLine;
                                        }
                                    }
                                }

                                Integer detailsId = getDetailsId(title);
                                // Synchronously download the video with callback
//                                boolean downloadSuccess = downloadVideoSynchronously(newVideoUrl, title, extractedUrl,detailsId);
                                //把信息加入阻塞队列
                                downloadedEpisodes1.incrementAndGet();
                                webDrama webDrama=new webDrama();
                                webDrama.setNewVideoUrl(newVideoUrl);
                                webDrama.setTitle(title);
                                webDrama.setExtractedUrl(extractedUrl);
                                webDrama.setDetailsId(detailsId);
                                queue.put(webDrama);

                            }

//                        // 检查是否已经下载了3集，跳出循环
//                        if (downloadedEpisodes1.get() >= 2) {
//                            break;
//                        }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (chromeDriver != null) {
                    chromeDriver.quit(); // 确保在所有操作完成后关闭ChromeDriver
                }
                try {

                    queue.put(null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });


        consumer=new Thread(()->{
            while (true) {
                webDrama poll=null;
                try {
                    poll=queue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (poll==null){
                    break;
                }
                //线程数
                Integer threadCount=10;
                String newVideoUrl = poll.getNewVideoUrl();
                String extractedUrl = poll.getExtractedUrl();
                Integer detailsId = poll.getDetailsId();
                String title = poll.getTitle();
                threadPoolExecutor.submit(()->{
                    M3u8Main.downloadM3u8Video(newVideoUrl, title, extractedUrl, detailsId,taskId,threadCount, fileService,()->{
                        out.println("下载成功");
                    });
                });

            }
            threadPoolExecutor.shutdown();

        });
        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveListOneway(String name, String classify, String taskId) {
        try {
            ChromeOptions options=new ChromeOptions();
//            options.addArguments("--headless");
            String urls = "https://v.ijujitv.cc/search/-------------.html?wd=" + name;
            out.println("正在爬取：" + urls);
            // 最外面的url
            Document document = Jsoup.connect(urls).get();
            Elements element = document.select(".m-item");
            for (Element elements : element) {
                // 第二页详情页url
                String href = elements.select("a.thumb").attr("href");
                String url1 = "https://v.ijujitv.cc/" + href;
                Document document1 = Jsoup.connect(url1).get();

                Elements elements1 = document1.select(".main-inner");
                for (Element element1 : elements1) {
                    String role;
                    // 剧名
                    String title = element1.select("h1.title").text();

                    // 图片url
                    String imgUrl = element1.select("img").attr("data-original");
                    // 主演
                    String actors = element1.select("p:nth-child(5) > a").text();
                    String url = "https://v.ijujitv.cc/" + imgUrl;
                    // 详情
                    String intro = element1.select("div.albumDetailMain-right > p").text();
                    // 去除简介部分
                    String details = intro.replaceFirst("^简介：\\s*", "");

                    if (actors == null) {
                        role = "";
                    } else {
                        role = actors;
                    }
                    out.println(url + ":" + title + ":" + details + ":" + classify);
                    imgUrl(url, title, classify, role, details);
                    // 分集类名
                    Elements elements2 = element1.select("#playlist3 > div > ul > li");

                    //遍历每部剧的每集
                    for (Element element2 : elements2) {
                        if (downloadedEpisodes.get() >= 2) { // 限制下载3集
                            out.println("已下载3集，结束下载。");
                            downloadedEpisodes.set(0); // 重置计数器
                            break; // 继续下一个剧
                        }
                        // 每一集页面url
                        String href1 = element2.select(".autowidth").attr("href");
                        String videoUrl = "https://v.ijujitv.cc/" + href1;

                        // 判断是否是需要跳过的链接
                        if ("https://v.ijujitv.cc//m.nwtef.xyz/dl/index.html?sc=1001_dc_xk&srcPlay=play".equals(videoUrl)) {
                            continue; // 跳过当前循环
                        }

                        setProperty("webdriver.chrome.driver", chromedriverPath);
                        ChromeDriver chromeDriver = new ChromeDriver(options);
                        // Navigate to the video URL
                        chromeDriver.get(videoUrl);

                        // 等待页面加载完成
                        waitForPageLoad(chromeDriver);

                        List<WebElement> elements3 = chromeDriver.findElements(By.cssSelector("#playleft > iframe"));
                        //是null就直接跳出
                        for (WebElement element3 : elements3) {
                            String extractedUrl = null;
                            String newVideoUrl = null;
                            String VideoUrl = element3.getAttribute("src");
                            int startIndex = VideoUrl.indexOf("url=");
                            if (startIndex != -1) {
                                extractedUrl = VideoUrl.substring(startIndex + "url=".length());
                                int endIndex = extractedUrl.indexOf(".m3u8");
                                if (endIndex != -1) {
                                    // 原视频连接
                                    extractedUrl = extractedUrl.substring(0, endIndex + ".m3u8".length());
                                    // 去掉index.m3u8这个后缀的视频链接
                                    String modifiedUrl = removeLastSegment(extractedUrl);
                                    // 检测第一个index.m3u8是只有三行的话返回thirdLine，不然返回null
                                    String thirdLine = getThirdLine(extractedUrl);

                                    if (thirdLine == null) {
                                        newVideoUrl = extractedUrl;
                                    } else {
                                        newVideoUrl = modifiedUrl + thirdLine;
                                    }
                                }
                            }

                            out.println("====="+newVideoUrl);
                            //外键
                            Integer detailsId = getDetailsId(title);
                            //线程数

                            // Synchronously download the video with callback
                            boolean downloadSuccess = downloadVideoSynchronously(newVideoUrl, title, extractedUrl,detailsId,taskId,threadCountOne);
                            out.println(downloadSuccess);
                            if (downloadSuccess){
                                downloadedEpisodes.incrementAndGet();
                                break;
                            }

                        }

                        // 检查是否已经下载了3集，跳出循环
                        if (downloadedEpisodes.get() >= 2) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (chromeDriver != null) {
                chromeDriver.quit(); // 确保在所有操作完成后关闭ChromeDriver
            }

        }
    }

    @Override
    public void saves(String name, String taskId) {
        final BlockingQueue<MyData> blockingQueue=new LinkedBlockingQueue<>(20);
        producer=new Thread(()-> {
            ChromeOptions options = new ChromeOptions();
            String urls;
//        options.addArguments("--headless"); // Set Chrome to run in headless mode
            chromeDriver = new ChromeDriver(options);

                try {
                    for (int i = 1; i < 5; i++) {
                        if (i==1){
                            urls = "https://www.kuaikaw.cn/search/?searchValue=" + name;
                        }else {
                          urls = "https://www.kuaikaw.cn/search/"+i+"?searchValue=" + name;
                        }
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
                                String actors = element1.select("a.TagBookList_bookAuthor__GAd_h > span").text();
                                String url = "https://www.kuaikaw.cn" + imgUrl;
                                //详情
                                String details = element1.select(".DramaDetail_intro__O7jEz").text();

                                if (actors == null) {
                                    role = "";
                                } else {
                                    role = actors;
                                }
                                out.println(url + ":" + title + ":" + details + ":" + classify);
                                imgUrl(url, title, classify, role, details);
                                //分集类名
                                Elements elements2 = element1.select(".pcDrama_catalogItem__4Q_C0");
                                for (Element element2 : elements2) {
                                    //第几集
                                    String episode = element2.select("a.pcDrama_catalogItem__4Q_C0").text();
                                    //只取数字
                                    String number = String.valueOf(convertChineseToArabic(episode));
                                    out.println(episode);
                                    out.println("=="+number);
                                    if (downloadedEpisodes.get() >= 5) { // 限制下载5集
                                        out.println("已下载5集，结束下载。");
                                        downloadedEpisodes.set(0);
                                        QueryWrapper<Acquire> acquireQueryWrapper = new QueryWrapper<>();
                                        acquireQueryWrapper.eq("name", name);
                                        Acquire acquire = acquireMapper.selectOne(acquireQueryWrapper);
                                        if (acquire == null) {
                                            break; // 继续下一个剧
                                        } else {
                                            return;
                                        }
                                    }
                                    //每一集页面url
                                    String href1 = element2.select("a.pcDrama_catalogItem__4Q_C0").attr("href");
                                    String videoUrl = "https://www.kuaikaw.cn" + href1;
                                    setProperty("webdriver.chrome.driver", chromedriverPath);

                                    chromeDriver.get(videoUrl);

                                    // 等待页面加载完成
                                    waitForPageLoad(chromeDriver);
                                    List<WebElement> elements3 = chromeDriver.findElements(By.cssSelector("#playVideo > video"));
                                    for (WebElement element3 : elements3) {
                                        //视频url
                                        String Url = element3.getAttribute("src");
                                        out.println(Url);
//                            download(Url, title, taskId, () -> {
//                                // 重置进度
//                                DownloadProgressManager.setProgress(taskId, 0);
//                                if (downloadedEpisodes.incrementAndGet() >= 5) {
//                                    chromeDriver.close();
//                                }
//                            });

                                        //然后通过线程池来下载，同时下载多个视频

                                        downloadedEpisodes.incrementAndGet();
                                        MyData myData = new MyData();
                                        myData.setTitle(title);
                                        myData.setUrl(Url);
                                        myData.setNumber(number);
                                        blockingQueue.put(myData);
                                        out.println(myData);
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    chromeDriver.quit(); // 确保在所有操作完成后关闭ChromeDriver
                }
        });

        consumer=new Thread(()->{
            while (true) {
                MyData poll = null;
                try {
                    poll = blockingQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (poll==null){
                    break;
                }
                String title = poll.getTitle();
                String url = poll.getUrl();
                String number = poll.getNumber();
                out.println("===============================================================================" + poll);
                threadPoolExecutor.submit(()-> {
                    try {
                        //将标题转换为拼音
                        String pinyin = convertToPinyin(title);
                        // 根据拼音创建文件夹sy
                        String destDir = FILE_UPLOAD_PATH1 + File.separator + pinyin;
                        File dir = new File(destDir);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        String UUID = IdUtil.fastSimpleUUID() + StrUtil.DOT + "mp4";
                        String destinationPath = destDir + File.separator + UUID; // 本地文件路径
                        URL videoUrl = null;
                        videoUrl = new URL(url);
                        // 获取文件大小
                        HttpURLConnection connection = null;
                        connection = (HttpURLConnection) videoUrl.openConnection();
                        connection.setRequestMethod("HEAD");
                        long totalSize = connection.getContentLengthLong();
                        connection.disconnect();

                        long fileSize = 0;
                        MessageDigest md5Digest = null;
                        try {
                            md5Digest = MessageDigest.getInstance("MD5");
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
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
                        if (videoByMd5 != null) {
                            VideoUrl = videoByMd5.getUrl();
                            boolean exist = FileUtil.exist(FILE_UPLOAD_PATH1 + pinyin + File.separator + VideoUrl.substring(VideoUrl.lastIndexOf("/") + 1));
                            if (!exist) {
                                try {
                                    downloadBytes = downloadAndSaveFile(videoUrl, destinationPath, md5Digest, fileSize, totalSize, taskId);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                VideoUrl = "http://localhost:9090/files/video/" + pinyin + "/" + UUID;
                            }
                        } else {
                            downloadBytes = downloadAndSaveFile(videoUrl, destinationPath, md5Digest, fileSize, totalSize, taskId);
                            VideoUrl = "http://localhost:9090/files/video/" + pinyin + "/" + UUID;
                        }
                        //分集排序
                        int maxIndex = getMaxIndexForName(title);
                        int diversity = maxIndex + 1;
                        Integer detailsId = getDetailsId(title);
                        out.println(detailsId);
                        FileOne files = new FileOne();
                        files.setName(title);
                        files.setUrl(VideoUrl);
                        files.setDetailsId(detailsId);
                        files.setMd5(md5);
                        files.setSize(fileSize);
                        files.setType("mp4");
                        files.setOriginalFilename(url);
                        files.setDiversity(number);
                        fileMapper.insert(files);
                        out.println("下载成功");
                        DownloadProgressManager.setProgress(taskId, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            threadPoolExecutor.shutdown();
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //爬取短剧所有的详情
    @Override
    public List<Acquire> seleteAcquireLists() {
        List<Acquire> acquireList=new ArrayList<>();

        String title = null;
        String classify=null;
        String imgUrl=null;
        String actors=null;
        Integer id=1;
        try {
            for (int i = 168; i < 187; i++) {

                String urls = "https://www.kuaikaw.cn/browse/0/" + i;
                out.println("正在爬取：" + urls);
                //最外面的url
                Document document = Jsoup.connect(urls).get();
                Elements element = document.select(".BrowseList_itemBox__S5QRX");
                for (Element elements : element) {
                    //第二页详情页url
                    String href = elements.select("a.image_imageScaleBox__JFwzM.BrowseList_imageBox__fPYH7").attr("href");
                    String url1 = "https://www.kuaikaw.cn" + href;
                    Document document1 = Jsoup.connect(url1).get();

                    Elements elements1 = document1.select("#__next > main");

                    for (Element element1 : elements1) {

                        Acquire acquire = new Acquire();
                        String role;
                        //剧名
                        title = element1.select("h1.DramaDetail_bookName__7bcjB").text();
                        //分类
                        classify = element1.select("a.DramaDetail_tagItem__L546Y").text();
                        //图片url
                        imgUrl = element1.select("img.DramaDetail_bookCover__mvLQU").attr("src");
                        //主演
                        actors = element1.select("a.TagBookList_bookAuthor__GAd_h > span").text();
                        String url = "https://www.kuaikaw.cn" + imgUrl;
                        //详情
                        String details = element1.select(".adm-ellipsis.DramaDetail_intro__O7jEz").text();
                        imgUrlList(url,title,classify,actors,details);
                        acquireList.add(acquire);
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return acquireList;
    }
    //爬取其他剧详情
    public List<Acquire> saveListAcquire(String name, String classify) {
        List<Acquire> acquireList=new ArrayList<>();
        try {
            String urls = "https://v.ijujitv.cc/search/-------------.html?wd=" + name;
            out.println("正在爬取：" + urls);
            // 最外面的url
            Document document = Jsoup.connect(urls).get();
            Elements element = document.select(".m-item");
            for (Element elements : element) {
                // 第二页详情页url
                String href = elements.select("a.thumb").attr("href");
                String url1 = "https://v.ijujitv.cc/" + href;
                Document document1 = Jsoup.connect(url1).get();

                Elements elements1 = document1.select(".main-inner");
                for (Element element1 : elements1) {
                    Acquire acquire=new Acquire();
                    String role;
                    // 剧名
                    String title = element1.select("h1.title").text();

                    // 图片url
                    String imgUrl = element1.select("img").attr("data-original");
                    // 主演
                    String actors = element1.select("p:nth-child(5) > a").text();
                    String url = "https://v.ijujitv.cc/" + imgUrl;
                    // 详情
                    String intro = element1.select("div.albumDetailMain-right > p").text();
                    // 去除简介部分
                    String details = intro.replaceFirst("^简介：\\s*", "");
                    if (actors == null) {
                        role = "";
                    } else {
                        role = actors;
                    }

                    acquire.setDescription(details);
                    acquire.setName(title);
                    acquire.setClassify(classify);
                    acquire.setActors(actors);
                    acquire.setCover(url);
                    acquireList.add(acquire);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return acquireList;
    }
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
        String destinationPath = folderPath+ File.separator + UUID; // 本地文件路径
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

    private void waitForPageLoad(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(webDriver ->
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
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
    //计算出图片的md5值
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    //视频的md5   查看数据库是否已经有与生成一样的md5值
    private FileOne getVideoByMd5(String md5) {
        QueryWrapper<FileOne> queryWrapper= new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<FileOne> filesList=fileMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);

    }

    //短剧的视频下载逻辑，IO流
    private Long downloadAndSaveFile(URL videoUrl, String destinationPath, MessageDigest md5Digest, long fileSize, long totalSize, String taskId) throws IOException {
        long downloadedSize = 0;
        try (InputStream down = videoUrl.openStream();
             FileOutputStream fos = new FileOutputStream(destinationPath)) {
            int len;
            byte[] buffer = new byte[1024];

            while ((len = down.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                downloadedSize += len; // 更新已下载大小
                fileSize += len; // 确保文件大小被正确更新
                md5Digest.update(buffer, 0, len);

                // 计算进度并发送到前端
                long progress = (downloadedSize * 100) / totalSize;
                if (progress != previousProgress) { // Avoid repeating progress
                    DownloadProgressManager.setProgress(taskId, progress);
                    out.println(progress);
                    previousProgress = progress;
                }
            }
        }
        return downloadedSize;
    }

    //分集排序
    private int getMaxIndexForName(String name) {
        //创建一个查询条件， QueryWrapper包装器构建查询条件
        QueryWrapper<FileOne> queryWrapper = new QueryWrapper<>();
        //比较名字获取与这个名字相同的数据
        queryWrapper.eq("name", name);
        queryWrapper.orderByDesc("diversity");
        List<FileOne> filesList = fileService.selectList(queryWrapper);
        //文件空不是数据库空，返回一个0
        if (filesList.isEmpty()) {
            return 0;
        } else {
            int maxIndex = 0;
            //遍历出最大序号
            for (FileOne files : filesList) {
                int i = Integer.parseInt(files.getDiversity());
                if (i > maxIndex) {
                    maxIndex = i;
                }
            }
            return maxIndex;
        }
    }

    private Integer getDetailsId(String name)
    {
        QueryWrapper<Details> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        try {
            Details details1 = detailsMapper.selectOne(queryWrapper);
            Integer detailsId = details1.getDetailsId();
            return detailsId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    //图片的下载
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

    //图片的md5 查看数据库有没有一样的
    private Images getFileByMd5(String md5) {
        QueryWrapper<Images> queryWrapper= new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<Images> filesList=imagesMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);

    }


    public static String removeLastSegment(String original) {
        int lastSlashIndex = original.lastIndexOf("/");
        if (lastSlashIndex != -1) {
            return original.substring(0, lastSlashIndex + 1);
        }
        return original;
    }

    //查询第一个m3u8的第三行是是否为空，不为空就返回后缀
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



    //其他剧一个一个视频下载的下载要走这个方法来获取下载下一集的回调
    private boolean downloadVideoSynchronously(String newVideoUrl, String title, String extractedUrl,Integer detailsId,String taskId,Integer threadCountOne) {
        final boolean[] success = {false};
        CountDownLatch latch = new CountDownLatch(1);

        try {
            M3u8Main.downloadM3u8Video(newVideoUrl, title, extractedUrl,detailsId,taskId,threadCountOne,fileService,()->{
                success[0] = true;
                latch.countDown(); // Notify the main thread after download completion
            });
            // Wait for the download to complete
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success[0];
    }

    //爬取所有短剧详情图片的下载
    public void imgUrlList(String imgUrl,String title,String classify,String actors,String details){

        String folderPath = FILE_UPLOAD_PATH_IMG;
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
        String destinationPath = folderPath+ File.separator + UUID; // 本地文件路径
        // 将反斜杠替换为正斜杠
        String destinationPaths = destinationPath.replaceAll("\\\\", "/");
        out.println("==="+destinationPaths);
        try {
            String  url="http://localhost:9090/files/images/"+UUID;
            //下载

            Map<String, Object> stringObjectMap = downLoadImage(imgUrl, destinationPaths,UUID);

            Acquire acquire=new Acquire();
            acquire.setDescription(details);
            acquire.setName(title);
            acquire.setClassify(classify);
            acquire.setActors(actors);
            acquire.setCover(url);
            acquireMapper.insert(acquire);
            out.println("Image downloaded successfully: " + destinationPaths);
        } catch (Exception e) {
            err.println("Error downloading image: " + e.getMessage());
        }
    }

    public static int convertChineseToArabic(String chineseNumber) {
        // 使用正则表达式匹配汉字数字
        Pattern pattern = Pattern.compile("[一二三四五六七八九十]+"); // 匹配汉字数字
        Matcher matcher = pattern.matcher(chineseNumber);

        if (matcher.find()) {
            String chineseDigit = matcher.group(); // 获取匹配到的汉字数字
            return chineseToArabic(chineseDigit); // 转换为阿拉伯数字
        } else {
            return -1; // 没有匹配到汉字数字，返回默认值
        }
    }

    /**
     * 将单个汉字数字转换为阿拉伯数字
     * @param chineseDigit 单个汉字数字，如 "五"
     * @return 对应的阿拉伯数字，如果无法识别则返回 -1 或其他适当的默认值
     */
    public static int chineseToArabic(String chineseDigit) {
        switch (chineseDigit) {
            case "一":
                return 1;
            case "二":
                return 2;
            case "三":
                return 3;
            case "四":
                return 4;
            case "五":
                return 5;
            case "六":
                return 6;
            case "七":
                return 7;
            case "八":
                return 8;
            case "九":
                return 9;
            case "十":
                return 10;
            default:
                return -1; // 如果无法识别，返回默认值
        }
    }
}

