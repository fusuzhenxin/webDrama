package net.xdclass.video.service.Impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import net.xdclass.video.entity.Images;
import net.xdclass.video.entity.News;
import net.xdclass.video.mapper.ImagesMapper;
import net.xdclass.video.mapper.NewsMapper;
import net.xdclass.video.service.ImagesService;
import net.xdclass.video.service.NewsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.*;

@Service
public class NewsServiceImpl  extends ServiceImpl<NewsMapper, News> implements NewsService {
    private static final String FILE_UPLOAD_PATH = getProperty("user.dir")
            + File.separator
            + "src" + File.separator
            + "main" + File.separator
            + "resources"
            + File.separator
            + "files" + File.separator
            + "image" + File.separator;

    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private ImagesMapper imagesMapper;
    @SneakyThrows
    @Override
    public void saveList() {
        for (int i = 2; i < 20; i++) {

            String urls = "https://v.ijujitv.cc/arttype/5-"+i+".html";
            out.println("正在爬取：" + urls);
            String text = null;
            //最外面的url
            Document document = Jsoup.connect(urls).get();
            Elements element = document.select(".rec-news-item");
            for (Element elements : element) {
                //第二页详情页url
                String href = elements.select("a.thumb").attr("href");
                String Url="https://v.ijujitv.cc"+href;
                Document document1 = Jsoup.connect(Url).get();
                out.println("Url"+Url);
                Elements elements1 = document1.select(".articleDetail");
                for (Element element1 : elements1) {
                    String role;
                    //剧名
                    String title = element1.select("h1.title").text();
                    String time = element1.select("span:nth-child(1)").text();//时间
                    String source = element1.select("span:nth-child(2) > em").text();//浏览
                    String url = element1.select("img").attr("src");//图片
                    String img="https://v.ijujitv.cc/"+url;
                    Elements elements2 = document1.select(".article");
                    for (Element element2 : elements2){
                        text = element2.select("p").text();

                    }
                   imgUrl(img,title,time,source,text);
                }
            }
        }
    }
    public void imgUrl(String imgUrl,String title ,String time ,String source,String text){

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

            News news=new News();
            news.setBrowse(source);
            news.setDescription(text);
            news.setUpdateTime(time);
            news.setName(title);
            news.setCover(cover);
            news.setSource("曙光tv");
            newsMapper.insert(news);
            out.println("Image downloaded successfully: " + destinationPaths);
        } catch (Exception e) {
            err.println("Error downloading image: " + e.getMessage());
        }
    }


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
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    private Images getFileByMd5(String md5) {
        QueryWrapper<Images> queryWrapper= new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<Images> filesList=imagesMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);

    }


}
