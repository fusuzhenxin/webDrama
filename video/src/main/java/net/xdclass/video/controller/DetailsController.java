package net.xdclass.video.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.xdclass.video.common.Result;
import net.xdclass.video.entity.Details;
import net.xdclass.video.entity.FileOne;
import net.xdclass.video.mapper.DetailsMapper;
import net.xdclass.video.service.DetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.lang.System.getProperty;

@RestController
@RequestMapping("/details")
public class DetailsController {
    @Autowired
    private DetailsMapper detailsMapper;
    @Autowired
    private DetailsService detailsService;

    @PostMapping("/save")
    public Result save(@RequestBody Details details){
        detailsService.saveOrUpdate(details);
        return Result.success();
    }

    @PostMapping("/saveVideo")
    public Result saveVideo(){
        detailsService.saveVideo();
        return Result.success();
    }


    private static final String FILE_UPLOAD_PATH = getProperty("user.dir")
            + File.separator
            + "src" + File.separator
            + "main" + File.separator
            + "resources"
            + File.separator
            + "files" + File.separator;



    @PostMapping("/description")
    public Result  details(@RequestParam("cover") MultipartFile cover ,String name, String classify, String description, String actors) throws IOException {
        String originalFilename = cover.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        String UUID= IdUtil.fastSimpleUUID()+ StrUtil.DOT+type;
        File uploadFile = new File(FILE_UPLOAD_PATH+UUID);
        String md5 = SecureUtil.md5(cover.getInputStream());
        Details dbDetails = getDetailsByMd5(md5);
        String url;
        if (dbDetails !=null){
            url = dbDetails.getCover();
            boolean exist=FileUtil.exist(FILE_UPLOAD_PATH+url.substring(url.lastIndexOf("/")+1));
            if (!exist){
                detailsMapper.deleteById(dbDetails.getId());
                cover.transferTo(uploadFile);
                url="http://localhost:9090/files/"+UUID;
            }
        }else {
            cover.transferTo(uploadFile);
             url="http://localhost:9090/files/"+UUID;
        }

        Details details=new Details();
        details.setDescription(description);
        details.setCover(url);
        details.setClassify(classify);
        details.setActors(actors);
        details.setName(name);
        details.setMd5(md5);
        detailsMapper.insert(details);
        return Result.success();
    }
    private Details getDetailsByMd5(String md5) {
        QueryWrapper<Details> queryWrapper= new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<Details> detailsList=detailsMapper.selectList(queryWrapper);
        return detailsList.size() == 0 ? null : detailsList.get(0);

    }

    @GetMapping("/finAll")
    public Result finAll(String name){
        QueryWrapper<Details> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("name",name);
        Details details = detailsMapper.selectOne(queryWrapper);
        return Result.success(details);
    }

    @GetMapping("/finAllName")
    public Result finAllName(@RequestParam String name){
        List<Details> detailsList=detailsService.finAllName(name);
        return Result.success(detailsList);
    }

    @GetMapping("/search")
    public Result search(@RequestParam String name){
        List<Details> detailsList=detailsService.search(name);
        return Result.success(detailsList);
    }

    @GetMapping("/selectTop10")
    public Result selectTop10(@RequestParam String classify){
        QueryWrapper<Details> queryWrapper=new QueryWrapper<>();
        queryWrapper.like("classify", classify);
        List<Details> details= detailsMapper.selectList(queryWrapper);
        return Result.success(details);
    }

    @GetMapping("/selectAcquiesce")
    public Result selectAcquiesce(@RequestParam String classify){
        List<Details> details= detailsMapper.selectAcquiesce(classify);
        System.out.println("");
        return Result.success(details);

    }
    @GetMapping("/likes")
    public Result likes(@RequestParam String name){
        Details details = detailsService.selectLikes(name);
        return Result.success(details.getQuantity());
    }


    @GetMapping("/likesDelete")
    public Result collectDelete(@RequestParam String name){
        return Result.success(detailsService.selectlikesDelete(name).getQuantity());
    }


    @GetMapping("/collect")
    public Result Collect(@RequestParam String name){
        return Result.success(detailsService.selectCollect(name).getCollect());
    }

    @GetMapping("/collectDelete")
    public Result CollectDelete(@RequestParam String name){
        return Result.success(detailsService.selectCollectDelete(name).getCollect());
    }

    //使用了 @RequestParam 注解来声明方法的参数，用于从请求中获取对应的参数值。
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        //QueryWrapper 来构建查询条件，并基于条件执行了分页查询
        QueryWrapper<Details> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        Page<Details> page = detailsService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }

    @GetMapping("/paging")
    public Result findPaging(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                             @RequestParam String classify) {
        //QueryWrapper 来构建查询条件，并基于条件执行了分页查询
        QueryWrapper<Details> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name).eq("classify",classify);
        }
        Page<Details> page = detailsService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id){
        detailsService.removeById(id);
        return Result.success();
    }
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        detailsService.removeByIds(ids);
        return Result.success();
    }


}
