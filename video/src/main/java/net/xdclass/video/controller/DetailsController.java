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
import net.xdclass.video.entity.Images;
import net.xdclass.video.mapper.DetailsMapper;
import net.xdclass.video.mapper.ImagesMapper;
import net.xdclass.video.service.DetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Autowired
    private ImagesMapper imagesMapper;

    private static final String FILE_UPLOAD_PATH = getProperty("user.dir")
            + File.separator
            + "src" + File.separator
            + "main" + File.separator
            + "resources"
            + File.separator
            + "files" + File.separator
            + "image" + File.separator;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/save")
    public Result save(@RequestBody Details details){
        detailsService.saveOrUpdate(details);
        return Result.success();
    }


    //上传图片
    @PostMapping("/description")
    public Result  details(@RequestParam("cover") MultipartFile cover ,String name, String classify, String description, String actors) throws IOException {
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

    //通过剧名查找视频详情，详情页
    @GetMapping("/finAll")
    public Result finAll(String name){
        String VideoKey="details:"+name;
        Object detailsOne =redisTemplate.opsForValue().get(VideoKey);
        if (detailsOne == null){
            QueryWrapper<Details> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("name",name);
            Details details = detailsMapper.selectOne(queryWrapper);
            if (details!=null){
                redisTemplate.opsForValue().set(VideoKey,details);
                return Result.success(details);
            }
        }
        return Result.success(detailsOne);
        //        Details details = detailsMapper.selectOne(queryWrapper);
//        return Result.success(details);

    }

    @GetMapping("/finAllName")
    public Result finAllName(@RequestParam String name){
        List<Details> detailsList=detailsService.finAllName(name);
        return Result.success(detailsList);
    }


    //主页的推荐视频和详情页娱乐新闻的热门视频
    @GetMapping("/selectTop10")
    public Result selectTop10(@RequestParam String classify){
        String CoverList="coverList:"+classify;
        Object List = redisTemplate.opsForValue().get(CoverList);
        if (List == null){
            QueryWrapper<Details> queryWrapper=new QueryWrapper<>();
            queryWrapper.like("classify", classify);
            queryWrapper.last("limit 12"); // Limit to top 10
            List<Details> details= detailsMapper.selectList(queryWrapper);
            if (details!=null){
                redisTemplate.opsForValue().set(CoverList,details);
                return Result.success(details);
            }
        }
        return Result.success(List);
//        List<Details> details= detailsMapper.selectList(queryWrapper);
//        return Result.success(details);
    }

    //首页的大视频
    @GetMapping("/selectTopSix")
    public Result selectTopSix(@RequestParam String classify){
        String selectTopSix="selectTopSix:"+classify;
        Object selectTopList = redisTemplate.opsForValue().get(selectTopSix);
        if (selectTopList == null){
            QueryWrapper<Details> queryWrapper=new QueryWrapper<>();
            queryWrapper.like("classify", classify);
            queryWrapper.last("limit 6"); // Limit to top 8
            List<Details> details= detailsMapper.selectList(queryWrapper);
            if (details !=null){
                redisTemplate.opsForValue().set(selectTopSix,details);
                return Result.success(details);
            }
        }
        return Result.success(selectTopList);
    }

    //搜索页面默认的剧
    @GetMapping("/selectAcquiesce")
    public Result selectAcquiesce(@RequestParam String classify){
        List<Details> details= detailsMapper.selectAcquiesce(classify);
        return Result.success(details);

    }
    //点赞增加
    @GetMapping("/likes")
    public Result likes(@RequestParam String name){
        Details details = detailsService.selectLikes(name);
        return Result.success(details.getQuantity());
    }

    //点赞删除
    @GetMapping("/likesDelete")
    public Result collectDelete(@RequestParam String name){
        return Result.success(detailsService.selectlikesDelete(name).getQuantity());
    }

    //收藏增加
    @GetMapping("/collect")
    public Result Collect(@RequestParam String name){
        return Result.success(detailsService.selectCollect(name).getCollect());
    }

    //收藏取消
    @GetMapping("/collectDelete")
    public Result CollectDelete(@RequestParam String name){
        return Result.success(detailsService.selectCollectDelete(name).getCollect());
    }

    //模糊查询
    @GetMapping("/paging")
    public Result findPaging(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                             @RequestParam String classify) {
        //QueryWrapper 来构建查询条件，并基于条件执行了分页查询
        QueryWrapper<Details> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("details_id");
        // 如果 name 或 classify 中有一个不为空，则添加对应的查询条件
        if (!"".equals(name) || !"".equals(classify)) {
            queryWrapper.and(wrapper -> wrapper.like(!"".equals(name), "name", name)
                    .or()
                    .like(!"".equals(classify), "classify", classify));
        }

        Page<Details> page = detailsService.page(new Page<>(pageNum, pageSize), queryWrapper);

        return Result.success(page);
    }

    //分页组件，搜索短剧
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize){
        QueryWrapper<Details> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("details_id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        Page<Details> page = detailsService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id){
        detailsService.removeById(id);
        return Result.success();
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        detailsService.removeByIds(ids);
        return Result.success();
    }


}
