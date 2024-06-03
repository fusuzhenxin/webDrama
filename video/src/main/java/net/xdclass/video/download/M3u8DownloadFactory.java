package net.xdclass.video.download;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.xdclass.video.Exception.M3u8Exception;

import net.xdclass.video.entity.FileOne;
import net.xdclass.video.mapper.FileMapper;
import net.xdclass.video.service.FileService;
import net.xdclass.video.utils.Constant;
import net.xdclass.video.utils.Log;
import net.xdclass.video.utils.MediaFormat;
import net.xdclass.video.utils.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.*;
import java.util.concurrent.*;

import net.xdclass.video.listener.DownloadListener;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import static net.xdclass.video.utils.Constant.FILESEPARATOR;

@Component
public class M3u8DownloadFactory {

    private static M3u8Download m3u8Download;

    /**
     *
     * 解决java不支持AES/CBC/PKCS7Padding模式解密
     *
     */
    static {
        Security.addProvider(new BouncyCastleProvider());
    }




    public static class M3u8Download {

        private  FileService fileService;

        //要下载的m3u8链接，索引文件的请求url
        private final String DOWNLOADURL;

        //优化内存占用，阻塞队列
        private static final BlockingQueue<byte[]> BLOCKING_QUEUE = new LinkedBlockingQueue<>();

        //线程数
        private int threadCount = 1;

        //重试次数
        private int retryCount = 30;

        //链接连接超时时间（单位：毫秒）
        private long timeoutMillisecond = 1000L;

        //合并后的文件存储目录
        private String dir;

        //合并后的视频文件名称
        private String fileName;

        //已完成ts片段个数
        private int finishedCount = 0;

        //解密算法名称
        private String method;

        //密钥
        private String key = "";

        //密钥字节
        private byte[] keyBytes = new byte[16];

        //key是否为字节
        private boolean isByte = false;

        //IV
        private String iv = "";

        //所有ts片段下载链接
        private Set<String> tsSet = new LinkedHashSet<>();

        //解密后的片段
        private Set<File> finishedFiles = new ConcurrentSkipListSet<>(Comparator.comparingInt(o -> Integer.parseInt(o.getName().replace(".xyz", ""))));

        //已经下载的文件大小
        private BigDecimal downloadBytes = new BigDecimal(0);

        //监听间隔
        private volatile long interval = 0L;

        //自定义请求头
        private Map<String, Object> requestHeaderMap = new HashMap<>();
        ;

        //监听事件
        private Set<DownloadListener> listenerSet = new HashSet<>(5);

        //代理设置
        private Proxy proxy;
        private String title;
        private String extractedUrl;
        public Runnable onComplete;


        /**
         * 开始下载视频
         */
        public void start() {

            checkField();
            String tsUrl = getTsUrl();
            if (StringUtils.isEmpty(tsUrl))
                Log.i("不需要解密");
            startDownload();
        }


        //回调
        public void mergeAndCleanUpFiles(Runnable onComplete) {
            new Thread(() -> {
                mergeTs();
                deleteFiles();
               // Ensure the callback is called after merging and cleanup
                onComplete.run();
            }).start();
        }

        /**
         * 下载视频
         */
        private void startDownload() {

            //线程池
            final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadCount);
            int i = 0;
            //如果生成目录不存在，则创建
            File file1 = new File(dir);
            if (!file1.exists())
                file1.mkdirs();
            //执行多线程下载
            for (String s : tsSet) {
                i++;
                fixedThreadPool.execute(getThread(s, i));
            }
            fixedThreadPool.shutdown();
            //下载过程监视
            new Thread(() -> {
                int consume = 0;
                //轮询是否下载成功
                while (!fixedThreadPool.isTerminated()) {
                    try {
                        consume++;
                        BigDecimal bigDecimal = new BigDecimal(downloadBytes.toString());
                        Thread.sleep(1000L);
                        Log.i("已用时" + consume + "秒！\t下载速度：" + StringUtils.convertToDownloadSpeed(new BigDecimal(downloadBytes.toString()).subtract(bigDecimal), 3) + "/s");
                        Log.i("\t已完成" + finishedCount + "个，还剩" + (tsSet.size() - finishedCount) + "个！");
                        Log.i(new BigDecimal(finishedCount).divide(new BigDecimal(tsSet.size()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("下载完成，正在合并文件！共" + finishedFiles.size() + "个！" + StringUtils.convertToDownloadSpeed(downloadBytes, 3));
                //开始合并视频
                mergeAndCleanUpFiles(onComplete);
                Log.i("视频合并完成，欢迎使用!");

            }).start();
            startListener(fixedThreadPool);

        }

        private void startListener(ExecutorService fixedThreadPool) {
            new Thread(() -> {
                for (DownloadListener downloadListener : listenerSet)
                    downloadListener.start();
                //轮询是否下载成功
                while (!fixedThreadPool.isTerminated()) {
                    try {
                        Thread.sleep(interval);
                        for (DownloadListener downloadListener : listenerSet)
                            downloadListener.process(DOWNLOADURL, finishedCount, tsSet.size(), new BigDecimal(finishedCount).divide(new BigDecimal(tsSet.size()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (DownloadListener downloadListener : listenerSet)
                    downloadListener.end();
            }).start();
            new Thread(() -> {
                while (!fixedThreadPool.isTerminated()) {
                    try {
                        BigDecimal bigDecimal = new BigDecimal(downloadBytes.toString());
                        Thread.sleep(1000L);
                        for (DownloadListener downloadListener : listenerSet)
                            downloadListener.speed(StringUtils.convertToDownloadSpeed(new BigDecimal(downloadBytes.toString()).subtract(bigDecimal), 3) + "/s");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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

        /**
         * 合并下载好的ts片段
         */
        private void mergeTs() {
            try {
                // 将标题转换为拼音
                String pinyin = convertToPinyin(title);
                // 根据拼音创建文件夹
                String destDir = dir + java.io.File.separator + pinyin;
                java.io.File dir = new java.io.File(destDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 合并后的视频文件路径
                String outputFile = destDir + java.io.File.separator + fileName + "mp4";
                System.out.println("Output File: " + outputFile);

                // Write input file list to a temporary file
                //临时文件，并将 TS 文件列表写入其中
                Path fileListPath = java.nio.file.Files.createTempFile("filelist", ".txt");
                List<String> tsFiles = getTsFiles();
                java.nio.file.Files.write(fileListPath, tsFiles);

                // 构建 FFmpeg 命令
                List<String> command = new ArrayList<>();
                command.add("ffmpeg");
                command.add("-f");
                command.add("concat");
                command.add("-safe");
                command.add("0");
                command.add("-i");
                command.add(fileListPath.toString()); // Input file list
                command.add("-c");
                command.add("copy");
                command.add(outputFile);

                System.out.println("FFmpeg Command: " + command); // 打印命令，检查是否正确

                // 执行 FFmpeg 命令
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();

                // 处理 FFmpeg 输出流的线程
                Thread outputThread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                // 处理 FFmpeg 错误流的线程
                Thread errorThread = new Thread(() -> {
                    try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        String errorLine;
                        while ((errorLine = errorReader.readLine()) != null) {
                            System.out.println(errorLine);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                // 启动处理线程
                outputThread.start();
                errorThread.start();

                // 等待转码完成
                int exitCode = process.waitFor();
                outputThread.join(); // 确保处理线程完成
                errorThread.join(); // 确保处理线程完成

                if (exitCode == 0) {
                    System.out.println("Merge success!");

                    // Calculate MD5 hash
                    MessageDigest md5Digest = MessageDigest.getInstance("MD5");
                    try (InputStream is = new FileInputStream(outputFile)) {
                        byte[] buffer = new byte[8192];
                        int read;
                        while ((read = is.read(buffer)) > 0) {
                            md5Digest.update(buffer, 0, read);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] mdBytes = md5Digest.digest();
                    String md5 = bytesToHex(mdBytes);

                    // Set up other file properties
                    int maxIndex = getMaxIndexForName(title);
                    int diversity = maxIndex + 1;
                    long videoSize = new File(outputFile).length(); // 使用文件大小

                    // Set up video URL
                    String videoUrl = "http://localhost:9090/files/video/" + pinyin + "/" + fileName + "mp4";

                    // Create and save file entity
                    FileOne files = new FileOne();
                    files.setName(title);
                    files.setSize(videoSize);
                    files.setUrl(videoUrl);
                    files.setOriginalFilename(extractedUrl);
                    files.setType("mp4");
                    files.setMd5(md5);
                    files.setDiversity(String.valueOf(diversity));
                    fileService.saveFile(files);

                } else {
                    System.out.println("Merge failed!");
                    // 打印 FFmpeg 错误信息
                    try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        String errorLine;
                        System.out.println("FFmpeg 错误信息:");
                        while ((errorLine = errorReader.readLine()) != null) {
                            System.out.println(errorLine);
                        }
                    }
                }

                // Delete the temporary file
                java.nio.file.Files.delete(fileListPath);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //拿到分集最大数
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



        // 获取所有的ts文件
        private List<String> getTsFiles() {
            List<String> tsFiles = new ArrayList<>();
            for (File f : finishedFiles) {
                tsFiles.add("file '" + f.getAbsolutePath() + "'");
            }
            return tsFiles;
        }

        private long getVideoSize() {
            long totalSize = 0;
            try {
                for (File f : finishedFiles) {
                    totalSize += f.length();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return totalSize;
        }

        //MD5
        private static String bytesToHex(byte[] bytes){
            StringBuilder sb = new StringBuilder();
            for (byte b:bytes){
                sb.append(String.format("%02x",b));
            }
            return sb.toString();
        }

        /**
         * 删除下载好的片段
         */
        private void deleteFiles() {
            File file = new File(dir);
            for (File f : file.listFiles()) {
                if (f.getName().endsWith(".xy") || f.getName().endsWith(".xyz"))
                    f.delete();
            }
        }

        /**
         * 开启下载线程
         *
         * @param urls ts片段链接
         * @param i    ts片段序号
         * @return 线程
         */
        private Thread getThread(String urls, int i) {
            return new Thread(() -> {
                int count = 1;
                HttpURLConnection httpURLConnection = null;
                //xy为未解密的ts片段，如果存在，则删除
                File file2 = new File(dir + FILESEPARATOR + i + ".xy");
                if (file2.exists())
                    file2.delete();
                OutputStream outputStream = null;
                InputStream inputStream1 = null;
                FileOutputStream outputStream1 = null;
                byte[] bytes;
                try {
                    bytes = BLOCKING_QUEUE.take();
                } catch (InterruptedException e) {
                    bytes = new byte[Constant.BYTE_COUNT];
                }
                //重试次数判断
                while (count <= retryCount) {
                    try {
                        //模拟http请求获取ts片段文件
                        URL url = new URL(urls);
                        if (proxy ==null) {
                            httpURLConnection = (HttpURLConnection) url.openConnection();
                        }else {
                            httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
                        }
                        httpURLConnection.setConnectTimeout((int) timeoutMillisecond);
                        for (Map.Entry<String, Object> entry : requestHeaderMap.entrySet())
                            httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue().toString());
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setReadTimeout((int) timeoutMillisecond);
                        httpURLConnection.setDoInput(true);
                        InputStream inputStream = httpURLConnection.getInputStream();
                        try {
                            outputStream = new FileOutputStream(file2);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            continue;
                        }
                        int len;
                        //将未解密的ts片段写入文件
                        while ((len = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, len);
                            synchronized (this) {
                                downloadBytes = downloadBytes.add(new BigDecimal(len));
                            }
                        }
                        outputStream.flush();
                        inputStream.close();
                        inputStream1 = new FileInputStream(file2);
                        int available = inputStream1.available();
                        if (bytes.length < available)
                            bytes = new byte[available];
                        inputStream1.read(bytes);
                        File file = new File(dir + FILESEPARATOR + i + ".xyz");
                        outputStream1 = new FileOutputStream(file);
                        //开始解密ts片段，这里我们把ts后缀改为了xyz，改不改都一样
                        byte[] decrypt = decrypt(bytes, available, key, iv, method);
                        if (decrypt == null)
                            outputStream1.write(bytes, 0, available);
                        else outputStream1.write(decrypt);
                        finishedFiles.add(file);
                        break;
                    } catch (Exception e) {
                        if (e instanceof InvalidKeyException || e instanceof InvalidAlgorithmParameterException) {
                            Log.e("解密失败！");
                            break;
                        }
                        Log.d("第" + count + "获取链接重试！\t" + urls);
                        count++;
//                        e.printStackTrace();
                    } finally {
                        try {
                            if (inputStream1 != null)
                                inputStream1.close();
                            if (outputStream1 != null)
                                outputStream1.close();
                            if (outputStream != null)
                                outputStream.close();
                            BLOCKING_QUEUE.put(bytes);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                    }
                }
                if (count > retryCount)
                    //自定义异常
                    throw new M3u8Exception("连接超时！");
                finishedCount++;
//                Log.i(urls + "下载完毕！\t已完成" + finishedCount + "个，还剩" + (tsSet.size() - finishedCount) + "个！");
            });
        }

        /**
         * 获取所有的ts片段下载链接
         *
         * 检查并获取 m3u8 文件内容。
         * 判断是否是有效的 m3u8 文件。
         * 解析 m3u8 文件以确定密钥和 ts 文件的获取方式。
         * @return 链接是否被加密，null为非加密
         */
        private String getTsUrl() {

            StringBuilder content = getUrlContent(DOWNLOADURL, false);
            //  解析 m3u8 文件，获取 key 和 ts 文件链接
            //判断是否是m3u8链接
            if (!content.toString().contains("#EXTM3U"))
                throw new M3u8Exception(DOWNLOADURL + "不是m3u8链接！");
            String[] split = content.toString().split("\\n");
            String keyUrl = "";
            boolean isKey = false;
            for (String s : split) {
                //如果含有此字段，则说明只有一层m3u8链接
                if (s.contains("#EXT-X-KEY") || s.contains("#EXTINF")) {
                    isKey = true;
                    keyUrl = DOWNLOADURL;
                    break;
                }
                //如果含有此字段，则说明ts片段链接需要从第二个m3u8链接获取
                if (s.contains(".m3u8")) {
                    if (StringUtils.isUrl(s))
                        return s;
                    String relativeUrl = DOWNLOADURL.substring(0, DOWNLOADURL.lastIndexOf("/") + 1);
                    if (s.startsWith("/"))
                        s = s.replaceFirst("/", "");
                    //获取第二个索引文件的m3u8链接
                    keyUrl = mergeUrl(relativeUrl, s);
                    break;
                }
            }
            if (StringUtils.isEmpty(keyUrl))
                throw new M3u8Exception("未发现key链接！");
            //获取密钥
            //如果 isKey 为 true，调用 getKey(keyUrl, content) 获取密钥。
            //如果 isKey 为 false，调用 getKey(keyUrl, null) 获取密钥。
            String key1 = isKey ? getKey(keyUrl, content) : getKey(keyUrl, null);
            if (StringUtils.isNotEmpty(key1))
                key = key1;
            else key = null;
            return key;
        }

        /**
         * 获取ts解密的密钥，并把ts片段加入set集合
         *
         * @param url     密钥链接，如果无密钥的m3u8，则此字段可为空
         * @param content 内容，如果有密钥，则此字段可以为空
         * @return ts是否需要解密，null为不解密
         *
         * 调用 getUrlContent 获取M3U8文件内容。
         * 解析M3U8文件内容，获取TS文件链接并存储到集合中。
         */
        private String getKey(String url, StringBuilder content) {
            StringBuilder urlContent;
            if (content == null || StringUtils.isEmpty(content.toString()))
                //获取所有ts文件链接
                urlContent = getUrlContent(url, false);
            else urlContent = content;
            //将 urlContent 转换为字符串并检查是否包含 #EXTM3U。
            if (!urlContent.toString().contains("#EXTM3U"))
                throw new M3u8Exception(DOWNLOADURL + "不是m3u8链接！");
            //这段代码将 urlContent 转换为字符串并使用换行符 \n 进行分割，将 M3U8 文件内容拆分成一个字符串数组
            String[] split = urlContent.toString().split("\\n");
            for (String s : split) {
                //如果含有此字段，则获取加密算法以及获取密钥的链接
                if (s.contains("EXT-X-KEY")) {
                    String[] split1 = s.split(",");
                    for (String s1 : split1) {
                        if (s1.contains("METHOD")) {
                            method = s1.split("=", 2)[1];
                            continue;
                        }
                        if (s1.contains("URI")) {
                            key = s1.split("=", 2)[1];
                            continue;
                        }
                        if (s1.contains("IV"))
                            iv = s1.split("=", 2)[1];
                    }
                }
            }

            // 提取基础路径的方法
            String relativeUrl = url.substring(0, url.lastIndexOf("/") + 1);
            //将ts片段链接加入set集合
            //遍历字符串数组 split，找到包含 #EXTINF 的行，这标志着 TS 片段链接的开始。
            //获取下一行（即 TS 片段链接 s1），并将其加入到集合 tsSet 中。
            //使用 StringUtils.isUrl(s1) 判断 s1 是否是一个完整的 URL。如果是，直接加入集合；如果不是，使用 mergeUrl(relativeUrl, s1) 将相对路径转换为完整的 URL 后加入集合
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                if (s.contains("#EXTINF")) {
                    String s1 = split[++i];
                    //将ts片段链接加入set集合
                    tsSet.add(StringUtils.isUrl(s1) ? s1 : mergeUrl(relativeUrl, s1));
                }
            }
            if (!StringUtils.isEmpty(key)) {
                // 去掉 key 中的引号
                key = key.replace("\"", "");

                // 判断 key 是否是一个完整的 URL
                String keyUrl = StringUtils.isUrl(key) ? key : mergeUrl(relativeUrl, key);

                // 获取密钥内容并移除所有的空白字符
                return getUrlContent(keyUrl, true).toString().replaceAll("\\s+", "");
            }
            return null;
        }

        /**
         * 模拟http请求获取内容
         *
         * @param urls  http链接，m3u8请求url
         * @param isKey 这个url链接是否用于获取key
         * @return 内容
         */
         private StringBuilder getUrlContent(String urls, boolean isKey) {
            int count = 1; // 初始化重试计数器
            HttpURLConnection httpURLConnection = null; // 声明 HttpURLConnection 对象
            StringBuilder content = new StringBuilder(); // 创建一个 StringBuilder 对象用于存储内容

            while (count <= retryCount) { // 当重试次数小于等于最大重试次数时
                try {
                    URL url = new URL(urls); // 创建 URL 对象
                    if (proxy == null) { // 如果没有代理
                        httpURLConnection = (HttpURLConnection) url.openConnection(); // 打开连接
                    } else {
                        httpURLConnection = (HttpURLConnection) url.openConnection(proxy); // 使用代理打开连接
                    }
                    httpURLConnection.setConnectTimeout((int) timeoutMillisecond); // 设置连接超时时间
                    httpURLConnection.setReadTimeout((int) timeoutMillisecond); // 设置读取超时时间
                    httpURLConnection.setUseCaches(false); // 禁用缓存
                    httpURLConnection.setDoInput(true); // 允许输入流

                    // 添加请求头
                    /**
                     * 1.遍历请求头映射：
                     * 使用 for 循环遍历 requestHeaderMap 中的每一个 Map.Entry 对象。
                     * requestHeaderMap 是一个 Map<String, Object>，其中键是请求头的名称，值是请求头的值。
                     * 2.添加请求头
                     * 对于每一个键值对，调用 httpURLConnection 的 addRequestProperty 方法，将键和值添加到 HTTP 请求头中。
                     * 由于请求头的值必须是字符串类型，因此调用 entry.getValue().toString() 将值转换为字符串。
                     */
                    for (Map.Entry<String, Object> entry : requestHeaderMap.entrySet()) {
                        httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue().toString());
                    }

                    ///获取M3U8文件内容或密钥内容。
                    String line; // 用于存储每一行读取的内容
                    InputStream inputStream = httpURLConnection.getInputStream(); // 获取输入流
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)); // 包装输入流以读取文本

                    if (isKey) { // 如果是获取密钥
                        byte[] bytes = new byte[128]; // 创建字节数组用于存储密钥数据
                        int len; // 用于存储读取的字节数
                        len = inputStream.read(bytes); // 读取字节数据
                        isByte = true; // 设置标志位
                        if (len == 16) { // 如果读取到的数据长度为16字节
                            keyBytes = Arrays.copyOf(bytes, 16); // 存储密钥数据
                            content.append("isByte"); // 向内容中添加标志
                        } else {
                            content.append(new String(Arrays.copyOf(bytes, len))); // 将读取到的数据作为字符串存储
                        }
                        return content; // 返回内容
                    }

                    // 读取文本内容
                    while ((line = bufferedReader.readLine()) != null) {
                        content.append(line).append("\n"); // 将每一行内容添加到 StringBuilder 中
                    }

                    bufferedReader.close(); // 关闭 BufferedReader
                    inputStream.close(); // 关闭输入流
//                    Log.i(content); // 打印日志信息=====这里打印索引文件内容出来
                    break; // 成功获取内容后跳出循环
                } catch (Exception e) { // 捕获异常
                    Log.d("第" + count + "获取链接重试！\t" + urls); // 打印重试日志
                    count++; // 增加重试计数器
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect(); // 断开连接
                    }
                }
            }

            if (count > retryCount) { // 如果超过重试次数
                throw new M3u8Exception("连接超时！"); // 抛出异常
            }

            //这个就是索引文件的内容
            return content; // 返回获取的内容
        }


        /**
         * 解密ts
         *
         * @param sSrc   ts文件字节数组
         * @param length
         * @param sKey   密钥
         * @return 解密后的字节数组
         */
        private byte[] decrypt(byte[] sSrc, int length, String sKey, String iv, String method) throws Exception {
            if (StringUtils.isNotEmpty(method) && !method.contains("AES"))
                throw new M3u8Exception("未知的算法！");
            // 判断Key是否正确
            if (StringUtils.isEmpty(sKey))
                return null;
            // 判断Key是否为16位
            if (sKey.length() != 16 && !isByte) {
                throw new M3u8Exception("Key长度不是16位！");
            }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keySpec = new SecretKeySpec(isByte ? keyBytes : sKey.getBytes(StandardCharsets.UTF_8), "AES");
            byte[] ivByte;
            if (iv.startsWith("0x"))
                ivByte = StringUtils.hexStringToByteArray(iv.substring(2));
            else ivByte = iv.getBytes();
            if (ivByte.length != 16)
                ivByte = new byte[16];
            //如果m3u8有IV标签，那么IvParameterSpec构造函数就把IV标签后的内容转成字节数组传进去
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivByte);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return cipher.doFinal(sSrc, 0, length);
        }

        /**
         * 字段校验
         */
        private void checkField() {
            if ("m3u8".compareTo(MediaFormat.getMediaFormat(DOWNLOADURL)) != 0)
                throw new M3u8Exception(DOWNLOADURL + "不是一个完整m3u8链接！");
            if (threadCount <= 0)
                throw new M3u8Exception("同时下载线程数只能大于0！");
            if (retryCount < 0)
                throw new M3u8Exception("重试次数不能小于0！");
            if (timeoutMillisecond < 0)
                throw new M3u8Exception("超时时间不能小于0！");
            if (StringUtils.isEmpty(dir))
                throw new M3u8Exception("视频存储目录不能为空！");
            if (StringUtils.isEmpty(fileName))
                throw new M3u8Exception("视频名称不能为空！");
            finishedCount = 0;
            method = "";
            key = "";
            isByte = false;
            iv = "";
            tsSet.clear();
            finishedFiles.clear();
            downloadBytes = new BigDecimal(0);
        }

        private String mergeUrl(String start, String end) {
            if (end.startsWith("/"))
                end = end.replaceFirst("/", "");
            int position = 0;
            String subEnd, tempEnd = end;
            while ((position = end.indexOf("/", position)) != -1) {
                subEnd = end.substring(0, position + 1);
                if (start.endsWith(subEnd)) {
                    tempEnd = end.replaceFirst(subEnd, "");
                    break;
                }
                ++position;
            }
            return start + tempEnd;
        }

        public String getDOWNLOADURL() {
            return DOWNLOADURL;
        }

        public int getThreadCount() {
            return threadCount;
        }

        public void setThreadCount(int threadCount) {
            if (BLOCKING_QUEUE.size() < threadCount) {
                for (int i = BLOCKING_QUEUE.size(); i < threadCount * Constant.FACTOR; i++) {
                    try {
                        BLOCKING_QUEUE.put(new byte[Constant.BYTE_COUNT]);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            this.threadCount = threadCount;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }

        public long getTimeoutMillisecond() {
            return timeoutMillisecond;
        }

        public void setTimeoutMillisecond(long timeoutMillisecond) {
            this.timeoutMillisecond = timeoutMillisecond;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getFinishedCount() {
            return finishedCount;
        }

        public void setLogLevel(int level) {
            Log.setLevel(level);
        }

        public Map<String, Object> getRequestHeaderMap() {
            return requestHeaderMap;
        }

        public void addRequestHeaderMap(Map<String, Object> requestHeaderMap) {
            this.requestHeaderMap.putAll(requestHeaderMap);
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }

        public void addListener(DownloadListener downloadListener) {
            listenerSet.add(downloadListener);
        }

        public M3u8Download(String DOWNLOADURL, FileService fileService) {
            this.DOWNLOADURL = DOWNLOADURL;
            this.fileService = fileService;
            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
        }

        public Proxy getProxy() {
            return proxy;
        }

        public void setProxy(int port) {
            this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", port));
        }

        public void setProxy(String address, int port) {
            this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(address, port));
        }

        public void setProxy(Proxy.Type type, String address, int port) {
            this.proxy = new Proxy(type, new InetSocketAddress(address, port));
        }

        public void setName(String title) {
            this.title=title;
        }

        public void setOriginal(String extractedUrl) {
            this.extractedUrl=extractedUrl;
        }

        public void setRunnable(Runnable onComplete) {

            this.onComplete=onComplete;
        }
    }

    /**
     * 获取实例
     *
     * @param downloadUrl 要下载的链接
     * @return 返回m3u8下载实例
     */


    public static M3u8Download getInstance(String downloadUrl, FileService fileService) {
        return new M3u8Download(downloadUrl, fileService);
    }

    public static void destroied() {
        m3u8Download = null;
    }

}
