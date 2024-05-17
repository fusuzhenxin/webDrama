package net.xdclass.video.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import net.xdclass.video.common.Result;
import net.xdclass.video.entity.FileOne;
import net.xdclass.video.entity.Images;
import net.xdclass.video.mapper.FileMapper;
import net.xdclass.video.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getProperty;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private FileService fileService;
    private static final String FILE_UPLOAD_PATH = getProperty("user.dir")
            + java.io.File.separator
            + "src" + java.io.File.separator
            + "main" + java.io.File.separator
            + "resources" + java.io.File.separator
            + "files" + java.io.File.separator
            + "image" + java.io.File.separator;

    private static final String FILE_UPLOAD_PATH1 = getProperty("user.dir")
            + java.io.File.separator
            + "src" + java.io.File.separator
            + "main" + java.io.File.separator
            + "resources" + java.io.File.separator
            + "files" + java.io.File.separator
            + "video" + java.io.File.separator;

    //单个视频上传
    @PostMapping("/upload")
    public  String upload(@RequestParam MultipartFile file,String name,String diversity) throws IOException{
        String originalFilename= file.getOriginalFilename();
        String type= FileUtil.extName(originalFilename);

        long size = file.getSize();
        //定义文件唯一的标识码
        String fileUUID=IdUtil.fastSimpleUUID()+ StrUtil.DOT+type;

        java.io.File uploadFile=new java.io.File(FILE_UPLOAD_PATH1+fileUUID);
        java.io.File parentFile = uploadFile.getParentFile();
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
    @PostMapping("/uploadList")
    public List<String> uploads(@RequestParam("files") List<MultipartFile> files, String name) throws IOException {

        List<String> urls = new ArrayList<>();

        // 循环外保存封面图片，放循环外面就保存一次
//        String coverUrl = saveCoverImage(cover);

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
//            String diversity =String.valueOf(i+1);
            //原文件名
            String originalFilename = file.getOriginalFilename();
            String type = FileUtil.extName(originalFilename);

            long size = file.getSize();
            //定义文件唯一的标识码
            String fileUUID = IdUtil.fastSimpleUUID() + StrUtil.DOT + type;

            java.io.File uploadFile = new java.io.File(FILE_UPLOAD_PATH1 + fileUUID);
            java.io.File parentFile = uploadFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            String url;
            String md5 = SecureUtil.md5(file.getInputStream());
            //这是生成的MD5 不是数据库拿的
            FileOne dbFiles = getFileByMd5(md5);
            if (dbFiles != null) {
                //检查服务器上是否已经存在对应的文件。如果不存在，则将文件保存到服务器上，并设置 URL。
                url = dbFiles.getUrl();
                //检查服务器上是否存在特定文件的。它的逻辑是从一个完整的URL中提取文件名，然后检查服务器上指定路径下是否存在这个文件。
                boolean exist = FileUtil.exist(FILE_UPLOAD_PATH1 + url.substring(url.lastIndexOf("/") + 1));
                if (!exist) {
                    file.transferTo(uploadFile);
                    url = "http://localhost:9090/files/video/" + fileUUID;
                }
            } else {
                //上传到静态资源
                file.transferTo(uploadFile);
                url = "http://localhost:9090/files/video/" + fileUUID;
            }

            int maxIndex=getMaxIndexForName(name);
            int diversity=maxIndex+1;
            FileOne saveFile = new FileOne();
            saveFile.setName(name);
            saveFile.setOriginalFilename(originalFilename);
            saveFile.setType(type);
            saveFile.setSize(size / 1024);
            saveFile.setUrl(url);
            saveFile.setMd5(md5);
            saveFile.setDiversity(String.valueOf(diversity));
            fileMapper.insert(saveFile);
            urls.add(url);
        }

        return urls;
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


    //点照片上传时候已经把url返回到前端了，然后发送编辑请求
    @PostMapping("/cover")
    private String saveCoverImages(@RequestParam MultipartFile file) throws IOException{

        String originalFilename= file.getOriginalFilename();
        String type= FileUtil.extName(originalFilename);
        String UUID=IdUtil.fastSimpleUUID()+StrUtil.DOT+type;
        java.io.File uploadFile = new java.io.File(FILE_UPLOAD_PATH + UUID);
        String cover1;

        file.transferTo(uploadFile);
        cover1="http://localhost:9090/files/image/"+UUID;
        return cover1;
    }

    //删除原有图片
    @PostMapping("/cover1")
    private String saveCoverImages(@RequestBody JsonNode requestBody) throws IOException{
        // 使用 Jackson ObjectMapper 将请求体解析为 JsonNode 对象
        // 然后从 JsonNode 中提取 cover 参数的值

        String cover = requestBody.get("params").get("cover").asText();
        String fileName = cover.substring(cover.lastIndexOf("/") + 1);
        String url=FILE_UPLOAD_PATH+fileName;
        // 删除静态资源图片
        boolean deleted = deleteImage(url);

        if (deleted) {
            return "图片删除成功";
        } else {
            return "图片删除失败";
        }

    }

    //删除图片方法
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
                System.out.println("文件删除失败：" + imageUrl);
                return false;
            }
        } else {
            System.out.println("文件不存在：" + imageUrl);
            return false;
        }
    }

    @GetMapping("/{id}")
    public Result findId(@PathVariable Integer id){
        FileOne files = fileMapper.selectById(id);
        return Result.success(files);
    }

    //第一集url和短剧的信息
    @GetMapping("/Inception")
    public Result Inception(@RequestParam String name){
       FileOne filesList=fileMapper.selectName(name);
        return Result.success(filesList);
    }
    //使用了 @RequestParam 注解来声明方法的参数，用于从请求中获取对应的参数值。
    @GetMapping("/page")
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

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id){
        fileService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        fileService.removeByIds(ids);
        return Result.success();
    }

}
