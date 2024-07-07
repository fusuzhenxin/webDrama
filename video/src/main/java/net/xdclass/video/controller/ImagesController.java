package net.xdclass.video.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.xdclass.video.common.Result;
import net.xdclass.video.entity.Details;
import net.xdclass.video.entity.Images;
import net.xdclass.video.mapper.DetailsMapper;
import net.xdclass.video.mapper.ImagesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.lang.System.getProperty;

@RestController
public class ImagesController {

    @Autowired
    private DetailsMapper detailsMapper;

    @Autowired
    ImagesMapper imagesMapper;
    private static final String FILE_UPLOAD_PATH = getProperty("user.dir")
            + File.separator
            + "src" + File.separator
            + "main" + File.separator
            + "resources"
            + File.separator
            + "files" + File.separator
            + "image" + File.separator;



    @PostMapping("/images/description")
    public Result details(@RequestParam("cover") MultipartFile cover , String name, String classify, String description, String actors) throws IOException {
        String originalFilename = cover.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);

        long size=cover.getSize();
        String UUID= IdUtil.fastSimpleUUID()+ StrUtil.DOT+type;
        File uploadFile = new File(FILE_UPLOAD_PATH+UUID);
        String md5 = SecureUtil.md5(cover.getInputStream());
        Details dbDetails = getDetailsByMd5(md5);
        String url;
        if (dbDetails !=null){
            url = dbDetails.getCover();
            boolean exist=FileUtil.exist(FILE_UPLOAD_PATH+url.substring(url.lastIndexOf("/")+1));
            if (!exist){

                cover.transferTo(uploadFile);
                url="http://localhost:9090/files/image/"+UUID;
            }
        }else {
            cover.transferTo(uploadFile);
            url="http://localhost:9090/files/image/"+UUID;
        }


        Images images=new Images();
        images.setName(name);
        images.setSize(size);
        images.setOriginalFilename(originalFilename);
        images.setType(type);
        images.setMd5(md5);
        images.setCover(url);
        imagesMapper.insert(images);

        Details details=new Details();
        details.setDescription(description);
        details.setCover(url);
        details.setClassify(classify);
        details.setActors(actors);
        details.setName(name);
        detailsMapper.insert(details);
        return Result.success();
    }

    private Details getDetailsByMd5(String md5) {
        QueryWrapper<Details> queryWrapper= new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<Details> detailsList=detailsMapper.selectList(queryWrapper);
        return detailsList.size() == 0 ? null : detailsList.get(0);

    }
}
