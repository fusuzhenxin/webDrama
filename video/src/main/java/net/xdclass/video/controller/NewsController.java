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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    @Autowired
    private NewsMapper newsMapper;
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
        List<News> top10News = newsMapper.findTop10ByQuantity();
        return Result.success(top10News);
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
}
