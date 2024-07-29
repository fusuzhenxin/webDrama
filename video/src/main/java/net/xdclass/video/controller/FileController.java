package net.xdclass.video.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import net.xdclass.video.common.Result;
import net.xdclass.video.entity.Details;
import net.xdclass.video.entity.FileOne;
import net.xdclass.video.entity.Images;
import net.xdclass.video.mapper.DetailsMapper;
import net.xdclass.video.mapper.FileMapper;
import net.xdclass.video.mapper.ImagesMapper;
import net.xdclass.video.service.FileService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getProperty;

@RestController
public class FileController {

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private FileService fileService;

    @Autowired
    private DetailsMapper detailsMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    ImagesMapper imagesMapper;
    private static final String FILE_UPLOAD_PATH = getProperty("user.dir")
            + File.separator
            + "src" + File.separator
            + "main" + File.separator
            + "resources" + File.separator
            + "files" + File.separator
            + "image" + File.separator;

    private static final String FILE_UPLOAD_PATH1 = getProperty("user.dir")
            + File.separator
            + "src" + File.separator
            + "main" + File.separator
            + "resources" + File.separator
            + "files" + File.separator
            + "video" + File.separator;

    private static final String FILE_UPLOAD_PATH2 = getProperty("user.dir")
            + File.separator
            + "src" + File.separator
            + "main" + File.separator
            + "resources" + File.separator
            + "files" + File.separator
            + "images" + File.separator;

    //单个视频上传
    @PostMapping("/apiOne/file/upload")
    public  String upload(@RequestParam MultipartFile file,String name,String diversity) throws IOException{
        String originalFilename= file.getOriginalFilename();
        String type= FileUtil.extName(originalFilename);

        long size = file.getSize();
        //定义文件唯一的标识码
        String fileUUID=IdUtil.fastSimpleUUID()+ StrUtil.DOT+type;

        File uploadFile=new File(FILE_UPLOAD_PATH1+fileUUID);
        File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()){
            parentFile.mkdirs();
        }

        String url;
        String md5= SecureUtil.md5(file.getInputStream());
        FileOne dbFiles=getFileByMd5(md5);
        if (dbFiles!=null){
             url = dbFiles.getUrl();
             boolean exist=FileUtil.exist(FILE_UPLOAD_PATH1+url.substring(url.lastIndexOf("/")+1));
             if (!exist){

                 file.transferTo(uploadFile);
                 url="http://localhost:9090/files/video/"+fileUUID;
             }
        }else {
            file.transferTo(uploadFile);
            url="http://localhost:9090/files/video/"+fileUUID;
        }

        FileOne saveFile = new FileOne();
        saveFile.setName(name);
        saveFile.setOriginalFilename(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size / 1024);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        saveFile.setDiversity(diversity);

        fileMapper.insert(saveFile);
        return url;

    }

    private FileOne getFileByMd5(String md5) {
     QueryWrapper<FileOne> queryWrapper= new QueryWrapper<>();
     queryWrapper.eq("md5",md5);
     List<FileOne> filesList=fileMapper.selectList(queryWrapper);
     return filesList.size() == 0 ? null : filesList.get(0);

    }

    //批量上传视频
//    @PostMapping("/apiOne/file/uploadList")
//    public List<String> uploads(@RequestParam("files") List<MultipartFile> files, String name) throws IOException {
//
//        List<String> urls = new ArrayList<>();
//
//        // 循环外保存封面图片，放循环外面就保存一次
////        String coverUrl = saveCoverImage(cover);
//
//        for (int i = 0; i < files.size(); i++) {
//            MultipartFile file = files.get(i);
////            String diversity =String.valueOf(i+1);
//            //原文件名
//            String originalFilename = file.getOriginalFilename();
//            String type = FileUtil.extName(originalFilename);
//
//            long size = file.getSize();
//            //定义文件唯一的标识码
//            String fileUUID = IdUtil.fastSimpleUUID() + StrUtil.DOT + type;
//
//            java.io.File uploadFile = new java.io.File(FILE_UPLOAD_PATH1 + fileUUID);
//            java.io.File parentFile = uploadFile.getParentFile();
//            if (!parentFile.exists()) {
//                parentFile.mkdirs();
//            }
//
//            String url;
//            String md5 = SecureUtil.md5(file.getInputStream());
//            //这是生成的MD5 不是数据库拿的
//            FileOne dbFiles = getFileByMd5(md5);
//            if (dbFiles != null) {
//                //检查服务器上是否已经存在对应的文件。如果不存在，则将文件保存到服务器上，并设置 URL。
//                url = dbFiles.getUrl();
//                //检查服务器上是否存在特定文件的。它的逻辑是从一个完整的URL中提取文件名，然后检查服务器上指定路径下是否存在这个文件。
//                boolean exist = FileUtil.exist(FILE_UPLOAD_PATH1 + url.substring(url.lastIndexOf("/") + 1));
//                if (!exist) {
//                    file.transferTo(uploadFile);
//                    url = "http://localhost:9090/files/video/" + fileUUID;
//                }
//            } else {
//                //上传到静态资源
//                file.transferTo(uploadFile);
//                url = "http://localhost:9090/files/video/" + fileUUID;
//            }
//
//            int maxIndex=getMaxIndexForName(name);
//            int diversity=maxIndex+1;
//            FileOne saveFile = new FileOne();
//            saveFile.setName(name);
//            saveFile.setOriginalFilename(originalFilename);
//            saveFile.setType(type);
//            saveFile.setSize(size / 1024);
//            saveFile.setUrl(url);
//            saveFile.setMd5(md5);
//            saveFile.setDiversity(String.valueOf(diversity));
//            fileMapper.insert(saveFile);
//            urls.add(url);
//        }
//
//        return urls;
//    }
//    @PostMapping("/apiOne/file/uploadList")
//    public List<String> uploads(@RequestParam("files") List<MultipartFile> files,
//                                @RequestParam("username") String username,
//                                @RequestParam("cover") MultipartFile cover,
//                                @RequestParam("title") String name,
//                                @RequestParam("description") String description,
//                                @RequestParam("type") String classify,
//                                @RequestParam("actors") String actors) throws IOException {
//
//        List<String> urls = new ArrayList<>();
//
//        // 循环外保存封面图片，放循环外面就保存一次
////        String coverUrl = saveCoverImage(cover);
//
//        QueryWrapper<Details> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("name",name);
//        Details details1 = detailsMapper.selectOne(queryWrapper);
//        if (details1==null){
//            Details details = new Details();
//            details.setName(name);
//            details.setClassify(classify);
//            details.setDescription(description);
//            details.setActors(actors);
//            String coverVideo = saveCoverImages(cover, name);
//            details.setCover(coverVideo);
//            details.setUsername(username);
//            detailsMapper.insert(details);
//        }
//
//
//
//        QueryWrapper<Details> queryWrapper1 = new QueryWrapper<>();
//        queryWrapper1.eq("name",name);
//        Details details2 = detailsMapper.selectOne(queryWrapper1);
//
//        for (int i = 0; i < files.size(); i++) {
//            MultipartFile file = files.get(i);
////            String diversity =String.valueOf(i+1);
//            //原文件名
//            String originalFilename = file.getOriginalFilename();
//            String type = FileUtil.extName(originalFilename);
//
//            long size = file.getSize();
//            //定义文件唯一的标识码
//            String fileUUID = IdUtil.fastSimpleUUID() + StrUtil.DOT + type;
//
//            java.io.File uploadFile = new java.io.File(FILE_UPLOAD_PATH1 + fileUUID);
//            java.io.File parentFile = uploadFile.getParentFile();
//            if (!parentFile.exists()) {
//                parentFile.mkdirs();
//            }
//
//            String url;
//            String md5 = SecureUtil.md5(file.getInputStream());
//            //这是生成的MD5 不是数据库拿的
//            FileOne dbFiles = getFileByMd5(md5);
//            if (dbFiles != null) {
//                //检查服务器上是否已经存在对应的文件。如果不存在，则将文件保存到服务器上，并设置 URL。
//                url = dbFiles.getUrl();
//                //检查服务器上是否存在特定文件的。它的逻辑是从一个完整的URL中提取文件名，然后检查服务器上指定路径下是否存在这个文件。
//                boolean exist = FileUtil.exist(FILE_UPLOAD_PATH1 + url.substring(url.lastIndexOf("/") + 1));
//                if (!exist) {
//                    file.transferTo(uploadFile);
//                    url = "http://localhost:9090/files/video/" + fileUUID;
//                }
//            } else {
//                //上传到静态资源
//                file.transferTo(uploadFile);
//                url = "http://localhost:9090/files/video/" + fileUUID;
//            }
//            int maxIndex=getMaxIndexForName(name);
//            int diversity=maxIndex+1;
//            FileOne saveFile = new FileOne();
//
//            saveFile.setDetailsId(details2.getDetailsId());
//            saveFile.setName(name);
//            saveFile.setOriginalFilename(originalFilename);
//            saveFile.setType(type);
//            saveFile.setSize(size / 1024);
//            saveFile.setUrl(url);
//            saveFile.setMd5(md5);
//            saveFile.setDiversity(String.valueOf(diversity));
//            fileMapper.insert(saveFile);
//            urls.add(url);
//        }
//        return urls;
//    }

//    @PostMapping("/apiOne/file/uploadList")
//    public List<String> uploads(@RequestParam("files") List<MultipartFile> files,
//                                @RequestParam("username") String username,
//                                @RequestParam("cover") MultipartFile cover,
//                                @RequestParam("title") String name,
//                                @RequestParam("description") String description,
//                                @RequestParam("type") String classify,
//                                @RequestParam("actors") String actors,
//                                @RequestParam(value = "chunk", required = false, defaultValue = "0") int chunk,
//                                @RequestParam(value = "chunks", required = false, defaultValue = "1") int chunks) throws IOException {
//
//        List<String> urls = new ArrayList<>();
//        String coverVideo;
//        String fileUUID = null;
//        // 循环外保存封面图片，放循环外面就保存一次
////        String coverUrl = saveCoverImage(cover,name);
//        QueryWrapper<Details> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("name",name);
//        Details details1 = detailsMapper.selectOne(queryWrapper);
//        //这个是存详情的，只需要存一遍
//        if (details1==null){
//            Details details = new Details();
//            details.setName(name);
//            details.setClassify(classify);
//            details.setDescription(description);
//            details.setActors(actors);
//            coverVideo= saveCoverImages(cover, name);
//            details.setCover(coverVideo);
//            details.setUsername(username);
//            detailsMapper.insert(details);
//        }
//        // 保存封面图片
//
//        // 处理分片文件
//        for (MultipartFile file : files) {
//            // 生成唯一的文件名
//           fileUUID = IdUtil.fastSimpleUUID() + "." + FileUtil.extName(file.getOriginalFilename());
//            File tempFile = new File(FILE_UPLOAD_PATH1 + fileUUID);
//
//            // 如果是第一个分片或者只有一个分片，直接保存
//            if (chunk == 0 || chunks == 1) {
//                file.transferTo(tempFile);
//            } else {
//                // 追加方式写入文件
//                try (FileOutputStream fos = new FileOutputStream(tempFile, true)) {
//                    fos.write(file.getBytes());
//                }
//            }
//        }
//            // 如果是最后一个分片，合并文件并保存到最终路径
//            if (chunk == chunks - 1) {
//                // 合并文件逻辑，这里示意合并过程
//                // 实际情况可能需要更复杂的合并逻辑，例如根据分片的顺序合并
//
//                // 例如，可以用Apache Commons IO工具类来实现文件合并D
//                File finalFile = new File(FILE_UPLOAD_PATH1 + fileUUID);
//                try (FileOutputStream fos = new FileOutputStream(finalFile)) {
//                    for (int i = 0; i < chunks; i++) {
//                        File partFile = new File(FILE_UPLOAD_PATH1 + IdUtil.fastSimpleUUID() + "." + FileUtil.extName(file.getOriginalFilename()));
//                        Files.move(partFile.toPath(), (Path) fos);
//                    }
//                }
//
//                // 删除临时文件夹中的所有分片文件
//                FileUtils.deleteDirectory(new File(FILE_UPLOAD_PATH1));
//
//                String url;
//                //这是生成的MD5 不是数据库拿的
//                String md5 = SecureUtil.md5(partFile.getInputStream());
//                FileOne dbFiles = getFileByMd5(md5);
//                if (dbFiles != null) {
//                    //检查服务器上是否已经存在对应的文件。如果不存在，则将文件保存到服务器上，并设置 URL。
//                    url = dbFiles.getUrl();
//                    //检查服务器上是否存在特定文件的。它的逻辑是从一个完整的URL中提取文件名，然后检查服务器上指定路径下是否存在这个文件。
//                    boolean exist = FileUtil.exist(FILE_UPLOAD_PATH1 + url.substring(url.lastIndexOf("/") + 1));
//                    if (!exist) {
//                        file.transferTo(uploadFile);
//                        url = "http://localhost:9090/files/video/" + fileUUID;
//                    }
//                } else {
//                    //上传到静态资源
//                    file.transferTo(uploadFile);
//                    url = "http://localhost:9090/files/video/" + fileUUID;
//                }
//
//            }
//
//
//
//
//
//        QueryWrapper<Details> queryWrapper1 = new QueryWrapper<>();
//        queryWrapper1.eq("name",name);
//        Details details2 = detailsMapper.selectOne(queryWrapper1);
//
//        for (int i = 0; i < files.size(); i++) {
//            MultipartFile file = files.get(i);
////            String diversity =String.valueOf(i+1);
//            //原文件名
//            String originalFilename = file.getOriginalFilename();
//            String type = FileUtil.extName(originalFilename);
//
//            long size = file.getSize();
//            //定义文件唯一的标识码
//            String fileUUID = IdUtil.fastSimpleUUID() + StrUtil.DOT + type;
//
//            File uploadFile = new File(FILE_UPLOAD_PATH1 + fileUUID);
//            File parentFile = uploadFile.getParentFile();
//            if (!parentFile.exists()) {
//                parentFile.mkdirs();
//            }
//
//            String url;
//            //这是生成的MD5 不是数据库拿的
//            String md5 = SecureUtil.md5(file.getInputStream());
//            FileOne dbFiles = getFileByMd5(md5);
//            if (dbFiles != null) {
//                //检查服务器上是否已经存在对应的文件。如果不存在，则将文件保存到服务器上，并设置 URL。
//                url = dbFiles.getUrl();
//                //检查服务器上是否存在特定文件的。它的逻辑是从一个完整的URL中提取文件名，然后检查服务器上指定路径下是否存在这个文件。
//                boolean exist = FileUtil.exist(FILE_UPLOAD_PATH1 + url.substring(url.lastIndexOf("/") + 1));
//                if (!exist) {
//                    file.transferTo(uploadFile);
//                    url = "http://localhost:9090/files/video/" + fileUUID;
//                }
//            } else {
//                //上传到静态资源
//                file.transferTo(uploadFile);
//                url = "http://localhost:9090/files/video/" + fileUUID;
//            }
//            int maxIndex=getMaxIndexForName(name);
//            int diversity=maxIndex+1;
//            FileOne saveFile = new FileOne();
//
//            saveFile.setDetailsId(details2.getDetailsId());
//            saveFile.setName(name);
//            saveFile.setOriginalFilename(originalFilename);
//            saveFile.setType(type);
//            saveFile.setSize(size / 1024);
//            saveFile.setUrl(url);
//            saveFile.setMd5(md5);
//            saveFile.setDiversity(String.valueOf(diversity));
//            fileMapper.insert(saveFile);
//            urls.add(url);
//        }
//        return urls;
//    }

    @PostMapping("/apiOne/file/uploadList")
    public List<String> uploads(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("username") String username,
            @RequestParam("cover") MultipartFile cover,
            @RequestParam("title") String name,
            @RequestParam("description") String description,
            @RequestParam("type") String classify,
            @RequestParam("actors") String actors,
            @RequestParam(value = "chunk", required = false, defaultValue = "0") int chunk,
            @RequestParam(value = "chunks", required = false, defaultValue = "1") int chunks) throws IOException {

        List<String> urls = new ArrayList<>();
        String coverUrl = null;

        // 保存封面图片，只在第一个分片时保存一次

        // 查询或插入电影详情信息
        QueryWrapper<Details> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        Details details1 = detailsMapper.selectOne(queryWrapper);
        //这个是存详情的，只需要存一遍
        if (details1==null){
            Details details = new Details();
            details.setName(name);
            details.setClassify(classify);
            details.setDescription(description);
            details.setActors(actors);
            coverUrl= saveCoverImages(cover, name);
            details.setCover(coverUrl);
            details.setUsername(username);
            detailsMapper.insert(details);
        }
          QueryWrapper<Details> queryWrapper1 = new QueryWrapper<>();
          queryWrapper1.eq("name",name);
          Details details2 = detailsMapper.selectOne(queryWrapper1);

        // 处理分片文件
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String type = FileUtil.extName(originalFilename);
            long size = file.getSize();
            String md5 = SecureUtil.md5(file.getInputStream());

            // 生成唯一的文件名
            String fileUUID = IdUtil.fastSimpleUUID() + "." + type;
            File tempFile = new File(FILE_UPLOAD_PATH1 + fileUUID);

            // 如果是第一个分片或者只有一个分片，直接保存
            if (chunk == 0 || chunks == 1) {
                file.transferTo(tempFile);
            } else {
                // 追加方式写入文件
                try (FileOutputStream fos = new FileOutputStream(tempFile, true)) {
                    fos.write(file.getBytes());
                }
            }

            // 如果是最后一个分片，合并文件并保存到最终路径
            if (chunk == chunks - 1) {
                String finalMd5 = mergeAndSaveFile(tempFile, originalFilename, type, size, md5);
                // 删除临时文件


                // 根据MD5查询数据库是否已经存在这个文件，避免重复保存
                FileOne dbFile = getFileByMd5(finalMd5);
                if (dbFile != null) {
                    urls.add(dbFile.getUrl());
                } else {
                    // 如果数据库中不存在该文件，则保存
                    String url = saveFileToServer(finalMd5, originalFilename, type, size, tempFile);
                    urls.add(url);

                    // 将文件信息保存到数据库
                    saveFileInfoToDatabase(details2.getDetailsId(), name, originalFilename, type, size, url, finalMd5);
                }
            }
        }

        List<String> classity=new ArrayList<>();
        classity.add("时空之旅");
        classity.add("逆袭");
        classity.add("甜宠");
        classity.add("重生");
        for (String classifys: classity){
            String CoverList="coverList:"+classifys;
            if (CoverList!=null){
                redisTemplate.delete(CoverList);
            }
        }

        return urls;
    }

    /**
     * 查询或插入电影详情信息
     */
    private Details getOrCreateDetails(String name, String classify, String description, String actors, String username, String coverUrl) {
        QueryWrapper<Details> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        Details details = detailsMapper.selectOne(queryWrapper);
        if (details == null) {
            details = new Details();
            details.setName(name);
            details.setClassify(classify);
            details.setDescription(description);
            details.setActors(actors);
            details.setCover(coverUrl);
            details.setQuantity("2000");
            details.setCollect("1000");
            details.setUsername(username);
            detailsMapper.insert(details);
        }
        return details;
    }

    /**
     * 合并文件并保存到服务器
     */
    private String mergeAndSaveFile(File tempFile, String originalFilename, String type, long size, String md5) throws IOException {
        // 实际情况可能需要更复杂的合并逻辑，这里示意将分片文件合并为最终文件
        File finalFile = new File(FILE_UPLOAD_PATH1 + originalFilename);
        try (FileOutputStream fos = new FileOutputStream(finalFile, true);
             FileInputStream fis = new FileInputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        // 计算最终文件的MD5
        String finalMd5 = SecureUtil.md5(finalFile);

        return finalMd5;
    }

    /**
     * 保存文件到服务器
     */
    private String saveFileToServer(String md5, String originalFilename, String type, long size, File tempFile) throws IOException {
        // 生成唯一的文件名
        String fileUUID = IdUtil.fastSimpleUUID() + "." + type;
        File uploadFile = new File(FILE_UPLOAD_PATH1 + fileUUID);
        File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        // 保存文件到服务器
        try (FileOutputStream fos = new FileOutputStream(uploadFile);
             FileInputStream fis = new FileInputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        return "http://localhost:9090/files/video/" + fileUUID;
    }

    /**
     * 将文件信息保存到数据库
     */
    private void saveFileInfoToDatabase(Integer detailsId, String name, String originalFilename, String type, long size, String url, String md5) {
        FileOne saveFile = new FileOne();
        int maxIndex=getMaxIndexForName(name);
        int diversity=maxIndex+1;
        saveFile.setDetailsId(detailsId);
        saveFile.setName(name);
        saveFile.setOriginalFilename(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size / 1024);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        saveFile.setDiversity(String.valueOf(diversity));
        fileMapper.insert(saveFile);
    }
    //分集排序
    private int getMaxIndexForName(String name){
        //创建一个查询条件， QueryWrapper包装器构建查询条件
        QueryWrapper<FileOne> queryWrapper=new QueryWrapper<>();
        //比较名字获取与这个名字相同的数据
        queryWrapper.eq("name",name);
        queryWrapper.orderByDesc("diversity");
        List<FileOne> filesList=fileMapper.selectList(queryWrapper);
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


    //删除图片方法
    @SneakyThrows
    public static boolean deleteImage(String imageUrl) {
        // 假设 imageUrl 是文件的绝对路径
        File imageFile = new File(imageUrl);

        // 检查文件是否存在
        if (imageFile.exists()) {
            // 删除文件
            if (imageFile.delete()) {
                System.out.println("文件删除成功：" + imageUrl);
                return true;
            } else {
                throw new ServerException("文件删除失败");

            }
        } else {
            System.out.println("文件不存在：" + imageUrl);
            return false;
        }
    }
    //第一集url和短剧的信息
    @GetMapping("/apiOne/file/Inception")
    public Result Inception(@RequestParam String name){
        FileOne filesList=fileMapper.selectName(name);
        return Result.success(filesList);
    }
    @GetMapping("/api/file/{id}")
    public Result findId(@PathVariable Integer id){
        FileOne files = fileMapper.selectById(id);
        return Result.success(files);
    }

    //使用了 @RequestParam 注解来声明方法的参数，用于从请求中获取对应的参数值。
    @GetMapping("/api/file/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String diversity
                          ) {
        //QueryWrapper 来构建查询条件，并基于条件执行了分页查询
        QueryWrapper<FileOne> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("episode_id");
        if (!"".equals(name) || !"".equals(diversity)) {
            queryWrapper.and(wrapper -> wrapper.like(!"".equals(name), "name", name)
                    .or()
                    .like(!"".equals(diversity), "diversity", diversity));
        }

        Page<FileOne> page = fileService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }

    @DeleteMapping("/api/file/{id}")
    public Result deleteById(@PathVariable Integer id){
        fileService.removeById(id);
        return Result.success();
    }

    @PostMapping("/api/file/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        fileService.removeByIds(ids);
        return Result.success();
    }


    //点照片上传时候已经把url返回到前端了，然后发送编辑请求
    @PostMapping("/api/file/cover")
    private String saveCoverImages(@RequestParam MultipartFile file,String name) throws IOException{

        String originalFilename= file.getOriginalFilename();
        String type= FileUtil.extName(originalFilename);
        String UUID=IdUtil.fastSimpleUUID()+StrUtil.DOT+type;
        File uploadFile = new File(FILE_UPLOAD_PATH + UUID);

        String md5 = SecureUtil.md5(file.getInputStream());
        String url;
        //这是生成的MD5 不是数据库拿的

        FileOne dbFiles = getFileByMd5(md5);
        if (dbFiles != null) {
            //检查服务器上是否已经存在对应的文件。如果不存在，则将文件保存到服务器上，并设置 URL。
            url = dbFiles.getUrl();
            //检查服务器上是否存在特定文件的。它的逻辑是从一个完整的URL中提取文件名，然后检查服务器上指定路径下是否存在这个文件。
            boolean exist = FileUtil.exist(FILE_UPLOAD_PATH + url.substring(url.lastIndexOf("/") + 1));
            if (!exist) {
                file.transferTo(uploadFile);
                url = "http://localhost:9090/files/image/" + UUID;
            }
        } else {
            //上传到静态资源
            file.transferTo(uploadFile);
            url = "http://localhost:9090/files/image/" + UUID;
        }
        long size = file.getSize();
        Images images=new Images();
        images.setCover(url);
        images.setName(name);
        images.setSize(size);
        images.setType(type);
        images.setOriginalFilename(originalFilename);
        images.setMd5(md5);
        imagesMapper.insert(images);
        return url;
    }

    //删除原有图片
    @PostMapping("/api/file/coverOne")
    private String saveCoverImages(@RequestBody JsonNode requestBody) throws IOException{
        // 使用 Jackson ObjectMapper 将请求体解析为 JsonNode 对象
        // 然后从 JsonNode 中提取 cover 参数的值

        String cover = requestBody.get("params").get("cover").asText();
        String fileName = cover.substring(cover.lastIndexOf("/") + 1);
        String url=FILE_UPLOAD_PATH+fileName;
        // 删除静态资源图片
        boolean deleted = deleteImage(url);

        if (deleted) {
            try {
                imagesMapper.deleteImagsUrl(cover);

            }catch (Exception e) {
                throw new ServerException("删除失败");
            }
            return "图片删除成功";
        } else {
            return "图片删除失败";
        }

    }


}
