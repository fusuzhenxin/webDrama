package net.xdclass.video.crawler.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.xdclass.video.crawler.service.CrawlerService;
import net.xdclass.video.common.Result;
import net.xdclass.video.config.DownloadProgressManager;
import net.xdclass.video.entity.Acquire;
import net.xdclass.video.service.AcquireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/crawler")
public class CrawlerController {

    @Autowired
    private CrawlerService crawlerService;


    @Autowired
    private AcquireService acquireService;

    @Autowired
    private RedisTemplate redisTemplate;

    //其他剧的同时爬取多个视频接口
    @GetMapping("/startDownload")
    public String startDownload(@RequestParam String name, @RequestParam String classify) {
        String taskId = UUID.randomUUID().toString();
        new Thread(() -> {
            try {
                crawlerService.saveList(name,classify,taskId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return taskId;
    }
    //其他剧的爬取一个一个视频接口
    @GetMapping("/startDownloadOnyWay")
    public String startDownloadOnyWay(@RequestParam String name,@RequestParam String classify) {
        String taskId = UUID.randomUUID().toString();
        new Thread(() -> {
            try {
                crawlerService.saveListOneway(name,classify,taskId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return taskId;
    }
    //爬取短剧视频接口
    @GetMapping("/startDownloads")
    public String startDownload(@RequestParam String name) {
        String taskId = UUID.randomUUID().toString();
        new Thread(() -> {
            try {
                crawlerService.saves(name, taskId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return taskId;
    }


    //把下载进度发送给前端
    @GetMapping("/progress")
    public SseEmitter progress(@RequestParam String taskId) {
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                while (true) {
                    Double progress = DownloadProgressManager.getProgress(taskId);
                    if (progress != null) {
                        emitter.send(progress);
                        if (progress >= 100) {
                            DownloadProgressManager.setProgress(taskId, 0);
                        }
                    }
                    Thread.sleep(10); // 每秒发送一次进度
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }

    //把短剧详情信息全部爬取下来存数据库
    @GetMapping("/acquire")
    public Result acquireList(){
        List<Acquire> diversity= crawlerService.seleteAcquireLists();
        return Result.success(diversity);
    }
    //搜索框爬取其他剧详情
    @GetMapping("/saveListAcquire")
    public Result saveListAcquire(@RequestParam String name,@RequestParam String classify){
        List<Acquire> diversity= crawlerService.saveListAcquire(name,classify);
        return Result.success(diversity);
    }

    // //爬虫搜索短剧视频
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {

        String AcquireKey="Acquire::"+name+":"+"pageNum:"+pageNum+"-"+"pageSize"+pageSize;
        Object AcquireList = redisTemplate.opsForValue().get(AcquireKey);
        if (AcquireList == null){
            //QueryWrapper 来构建查询条件，并基于条件执行了分页查询
            QueryWrapper<Acquire> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            if (!"".equals(name)) {
                queryWrapper.like("name", name);
            }
            Page<Acquire> page = acquireService.page(new Page<>(pageNum, pageSize), queryWrapper);
            if (page!=null){
                redisTemplate.opsForValue().set(AcquireKey,page);
                return Result.success(page);
            }
        }
//        //QueryWrapper 来构建查询条件，并基于条件执行了分页查询
//        QueryWrapper<Acquire> queryWrapper = new QueryWrapper<>();
//        queryWrapper.orderByDesc("id");
//        if (!"".equals(name)) {
//            queryWrapper.like("name", name);
//        }
//        Page<Acquire> page = acquireService.page(new Page<>(pageNum, pageSize), queryWrapper);
//        return Result.success(page);
        return Result.success(AcquireList);
    }
}
