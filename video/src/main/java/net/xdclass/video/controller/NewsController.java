package net.xdclass.video.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.xdclass.video.common.Result;
import net.xdclass.video.entity.Admin;
import net.xdclass.video.entity.Details;
import net.xdclass.video.entity.News;
import net.xdclass.video.mapper.NewsMapper;
import net.xdclass.video.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping
    public Result save(){
        newsService.saveList();
        return Result.success();
    }

    //根据浏览量
    @GetMapping("/browsePage")
    public Result findBrowsePage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize){
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("browse");
        Page<News> page = newsService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }

    //根据更新时间
    @GetMapping("/updateTimePage")
    public Result findUpdateTimePage(@RequestParam Integer pageNum,
                                 @RequestParam Integer pageSize){
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("update_time");
        Page<News> page = newsService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize){
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        Page<News> page = newsService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }

    //根据点赞数来排序热门视频
    @GetMapping("/top10")
    public Result findTop10(){
        //尝试从redis获取前十的id
        Set<String> top10NewsIds = stringRedisTemplate.opsForZSet().reverseRange("news:rank", 0, 9);
        if(top10NewsIds == null || top10NewsIds.isEmpty()){
            //数据库中获取数据
            //预加载前 10 条新闻到 Redis
            newsService.preloadTop10News();
            // 再次从 Redis 中获取前 10 条新闻的 ID
            //从大到小排序，这个是利于更新redis中的数据
            top10NewsIds = stringRedisTemplate.opsForZSet().reverseRange("news:rank", 0, 9);
        }
        //初始化新闻详情表
        List<Object> top10News = new ArrayList<>();
        //需要从数据库中查询的新闻id列表
        List<Long> missingIds = new ArrayList<>();

        //Redis获取每个新闻ID的详情
        for(String newsId:top10NewsIds){
            String VideoKey="video:details:"+newsId;
            //注意用Details是否能拿到值
            Object VideoDetails =  redisTemplate.opsForValue().get(VideoKey);
            if (VideoDetails != null){
                //如果缓存中有详情，直接添加到列表
                top10News.add(VideoDetails);
            }else {
                //如果缓存中没有详情，将id添加到缺少列表
                missingIds.add(Long.valueOf(newsId));
            }
        }

        //如果有缺少的详情，从数据库中批量查询并缓存到redis
        if (!missingIds.isEmpty()){
            List<Details> missingVideos = newsMapper.findById(missingIds);
            for (Details news: missingVideos){
                String videoKey="video:details:"+news.getDetailsId();
                redisTemplate.opsForValue().set(videoKey,news);
                System.out.println("===="+news);
                top10News.add(news);
            }

        }



//        List<Details> top10News = top10NewsIds.stream()
//                .map(newsId -> newsMapper.findById(Long.valueOf(newsId)))
//                .collect(Collectors.toList());
        return Result.success(top10News);
    }

    /**
     * @PathVariable 通常用于从 URL 路径中提取参数，如 /click/{detailsId}。
     * @RequestParam 用于从查询字符串或请求体中提取参数。
     * @param detailsId
     * @return
     */
    //模拟新闻点击
    @PostMapping("/click/{detailsId}")
    public Result clickVideo(@PathVariable Integer detailsId){
        newsService.incrementVideoScore(Long.valueOf(detailsId));
        return Result.success();
    }

    @GetMapping("/collectTop10")
    public Result findCollectTop10(){
        List<News> top10News = newsMapper.findTop10ByCollect();
        return Result.success(top10News);
    }

    //根据新闻标题获取内容
    @GetMapping("/finAll")
    public Result finAll(String name){
        QueryWrapper<News> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("name",name);
        News details = newsMapper.selectOne(queryWrapper);
        return Result.success(details);
    }

    @PostMapping("/save")
    public Result save(@RequestBody News news){
        newsService.saveOrUpdate(news);
        return Result.success();
    }
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id){
        newsService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        newsService.removeByIds(ids);
        return Result.success();
    }

    public void incrementNewsScore(Long newsId){
        stringRedisTemplate.opsForZSet().incrementScore("news:rank",String.valueOf(newsId),1);
    }
}
