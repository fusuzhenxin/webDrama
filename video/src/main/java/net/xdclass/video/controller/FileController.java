package net.xdclass.video.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.xdclass.video.common.Result;
import net.xdclass.video.entity.FileOne;
import net.xdclass.video.mapper.FileMapper;
import net.xdclass.video.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            + "resources"
            + java.io.File.separator
            + "files" + java.io.File.separator;


    @PostMapping("/upload")
    public  String upload(@RequestParam MultipartFile file,String name,String classify,String diversity) throws IOException{
        String originalFilename= file.getOriginalFilename();
        String type= FileUtil.extName(originalFilename);

        long size = file.getSize();
        //定义文件唯一的标识码
        String fileUUID=IdUtil.fastSimpleUUID()+ StrUtil.DOT+type;

        java.io.File uploadFile=new java.io.File(FILE_UPLOAD_PATH+fileUUID);
        java.io.File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()){
            parentFile.mkdirs();
        }

        String url;
        String md5= SecureUtil.md5(file.getInputStream());
        FileOne dbFiles=getFileByMd5(md5);
        if (dbFiles!=null){
             url = dbFiles.getUrl();
             boolean exist=FileUtil.exist(FILE_UPLOAD_PATH+url.substring(url.lastIndexOf("/")+1));
             if (!exist){
                 fileMapper.deleteById(dbFiles.getId());
                 file.transferTo(uploadFile);
                 url="http://localhost:9090/files/"+fileUUID;
             }
        }else {
            file.transferTo(uploadFile);
            url="http://localhost:9090/files/"+fileUUID;
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

    @PostMapping("/uploadList")
    public List<String> uploads(@RequestParam("files") List<MultipartFile> files, String name,
                               String classifications) throws IOException {

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

            java.io.File uploadFile = new java.io.File(FILE_UPLOAD_PATH + fileUUID);
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
                boolean exist = FileUtil.exist(FILE_UPLOAD_PATH + url.substring(url.lastIndexOf("/") + 1));
                if (!exist) {
                    file.transferTo(uploadFile);
                    url = "http://localhost:9090/files/" + fileUUID;
                }
            } else {
                //上传到静态资源
                file.transferTo(uploadFile);
                url = "http://localhost:9090/files/" + fileUUID;
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
//            saveFile.setCover(coverUrl);
            fileMapper.insert(saveFile);
            urls.add(url);
        }

        return urls;
    }


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

    private String saveCoverImage(MultipartFile cover) throws IOException{
        String originalFilename= cover.getOriginalFilename();
        String type= FileUtil.extName(originalFilename);
        String UUID=IdUtil.fastSimpleUUID()+StrUtil.DOT+type;
        java.io.File uploadFile = new java.io.File(FILE_UPLOAD_PATH + UUID);
        String cover1;

        cover.transferTo(uploadFile);
        cover1="http://localhost:9090/files/"+UUID;
        return cover1;
    }


    @PostMapping("/cover")
    private String saveCoverImages(@RequestParam MultipartFile file) throws IOException{
        String originalFilename= file.getOriginalFilename();
        String type= FileUtil.extName(originalFilename);
        String UUID=IdUtil.fastSimpleUUID()+StrUtil.DOT+type;
        java.io.File uploadFile = new java.io.File(FILE_UPLOAD_PATH + UUID);
        String cover1;

        file.transferTo(uploadFile);
        cover1="http://localhost:9090/files/"+UUID;
        return cover1;
    }

    @GetMapping("/selectTop10")
    public Result selectTop10(@RequestParam  String classify){
        List<FileOne> filesList=fileService.selectTop10(classify);
        return Result.success(filesList);
    }

    @GetMapping("/selectTop101")
    public Result selectTop101(){
        List<FileOne> filesList=fileMapper.selectTop101();
        return Result.success(filesList);
    }

    @GetMapping("/{id}")
    public Result findId(@PathVariable Integer id){
        FileOne files = fileMapper.selectById(id);
        return Result.success(files);
    }

    @GetMapping("/Inception")
    public Result Inception(@RequestParam String name){
       FileOne filesList=fileMapper.selectName(name);
        return Result.success(filesList);
    }

    @GetMapping("/search")
    public Result search(@RequestParam String name){
        List<FileOne> filesList=fileService.selectSearch(name);
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
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name).eq("diversity",diversity);
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
