package net.xdclass.video.main;//package net.xdclass.video.main;
//
//
//
//import net.xdclass.video.download.M3u8DownloadFactory;
//import net.xdclass.video.listener.DownloadListener;
//import net.xdclass.video.utils.Constant;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//public class M3u8Main {
//
//    // 创建一个固定大小的线程池，控制同时进行的下载任务数量
//    public static void main(String url,String title){
////    public static void main(String[] args) {
//        List<String> urls = new ArrayList<>();
//        List<String> titles = new ArrayList<>();
//        urls.add(url);
//        titles.add(title);
//
//        // 定义多个M3U8链接和对应的文件名
//        List<String[]> m3u8UrlsAndFileNames = new ArrayList<>();
//
//        // 将URL和标题组合成数组放入m3u8UrlsAndFileNames列表中
//        for (int i = 0; i < Math.min(urls.size(), titles.size()); i++) {
//            m3u8UrlsAndFileNames.add(new String[]{urls.get(i), titles.get(i)});
//        }
//
//
//        // 开始循环下载
//        downloadNextVideo(0, m3u8UrlsAndFileNames);
//
//
//    }
//
//    private static void downloadNextVideo(int index, List<String[]> m3u8UrlsAndFileNames) {
//        if (index >= m3u8UrlsAndFileNames.size()) {
//            // 所有视频下载完成
//            System.out.println("所有视频下载完成！");
//            return;
//        }
//
//        String[] pair = m3u8UrlsAndFileNames.get(index);
//        String m3u8Url = pair[0];
//        String fileName = pair[1];
//
//        // 提交任务到线程池
//
//            downloadM3u8Video(m3u8Url, fileName, () -> {
//                // 下载完成后，递归调用下载下一个视频
//                downloadNextVideo(index + 1, m3u8UrlsAndFileNames);
//            });
//
//    }
//
//    private static void downloadM3u8Video(String m3u8Url, String fileName, Runnable onComplete) {
//
//        M3u8DownloadFactory.M3u8Download m3u8Download = M3u8DownloadFactory.getInstance(m3u8Url);
//        // 设置生成目录
//        m3u8Download.setDir("F://m3u8JavaTest");
//        // 设置视频名称
//        m3u8Download.setFileName(fileName);
//        // 设置线程数
//        m3u8Download.setThreadCount(100);
//        // 设置重试次数
//        m3u8Download.setRetryCount(100);
//        // 设置连接超时时间（单位：毫秒）
//        m3u8Download.setTimeoutMillisecond(10000L);
//        // 设置日志级别
//        m3u8Download.setLogLevel(Constant.INFO);
//        // 设置监听器间隔（单位：毫秒）
//        m3u8Download.setInterval(500L);
//        // 添加监听器
//        m3u8Download.addListener(new DownloadListener() {
//            @Override
//            public void start() {
//                System.out.println("开始下载 " + fileName + " ：" + m3u8Url);
//            }
//
//            @Override
//            public void process(String downloadUrl, int finished, int sum, float percent) {
//                System.out.println("下载网址：" + downloadUrl + "\t已下载" + finished + "个\t一共" + sum + "个\t已完成" + percent + "%");
//            }
//
//            @Override
//            public void speed(String speedPerSecond) {
//                System.out.println("下载速度：" + speedPerSecond);
//            }
//
//            @Override
//            public void end() {
//                System.out.println("下载完毕 " + fileName);
//                // 调用下载完成的回调函数
//                onComplete.run();
//            }
//        });
//
//        // 开始下载
//        m3u8Download.start();
//    }
//
//
//}


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import net.xdclass.video.conf.DownloadProgressManager;
import net.xdclass.video.download.M3u8DownloadFactory;
import net.xdclass.video.listener.DownloadListener;
import net.xdclass.video.service.FileService;
import net.xdclass.video.service.Impl.FileServiceImpl;
import net.xdclass.video.utils.Constant;

import java.io.File;
import java.util.concurrent.Semaphore;

import static java.lang.System.getProperty;

public class M3u8Main {


    //静态文件路径
    private static final String FILE_UPLOAD_PATH = getProperty("user.dir")
            + File.separator
            + "src" + File.separator
            + "main" + File.separator
            + "resources"
            + File.separator
            + "files" + File.separator
            + "video" + File.separator;
    public static void downloadM3u8Video(String M3U8URL, String title  , String extractedUrl , Integer detailsId,String taskId,Integer threadCount, FileService fileService,Runnable onComplete) {
        String UUID = IdUtil.fastSimpleUUID();

        M3u8DownloadFactory.M3u8Download m3u8Download = M3u8DownloadFactory.getInstance(M3U8URL, fileService);

        //设置生成目录
        m3u8Download.setDir(FILE_UPLOAD_PATH);
        //设置视频名称
        m3u8Download.setFileName(UUID);
        //视频源文件
        m3u8Download.setOriginal(extractedUrl);
        //视频名称
        m3u8Download.setName(title);
        //外键id
        m3u8Download.setDetailsId(detailsId);
        //设置线程
        m3u8Download.setThreadCount(threadCount);
        //设置重试次数
        m3u8Download.setRetryCount(5);
        //设置连接超时时间（单位：毫秒）
        m3u8Download.setTimeoutMillisecond(1000L);
        /*
        设置日志级别
        可选值：NONE INFO DEBUG ERROR
        */
        m3u8Download.setLogLevel(Constant.INFO);
        //设置监听器间隔（单位：毫秒）
        m3u8Download.setInterval(500L);
        m3u8Download.setRunnable(onComplete);
        //添加监听器
        m3u8Download.addListener(new DownloadListener() {
            @Override
            public void start() {
                System.out.println("开始下载！");
            }

            @Override
            public void process(String downloadUrl, int finished, int sum, float percent) {
                DownloadProgressManager.setProgress(taskId, percent);
                System.out.println("下载网址：" + downloadUrl + "\t已下载" + finished + "个\t一共" + sum + "个\t已完成" + percent + "%");
            }

            @Override
            public void speed(String speedPerSecond) {
                System.out.println("下载速度：" + speedPerSecond);
            }

            @Override
            public void end() {
                System.out.println("下载完毕");
                m3u8Download.mergeAndCleanUpFiles(()->{
                    onComplete.run();
                });
            }
        });
        //开始下载
        m3u8Download.start();
    }
}
