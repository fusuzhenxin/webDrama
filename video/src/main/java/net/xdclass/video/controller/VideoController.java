package net.xdclass.video.controller;
import net.xdclass.video.common.Result;
import net.xdclass.video.conf.DownloadProgressManager;
import net.xdclass.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;


    //获取这个剧有多少集
    @GetMapping("/diversity")
    public Result diversitys(@RequestParam String name){
       Integer diversity= videoService.seleteDiversitys(name);
       return Result.success(diversity);
    }

    //获取每集url
    @GetMapping("/{videoName}/episode/{episodeNumber}")
    public Result getEpisodeUrl(@PathVariable String videoName, @PathVariable Integer episodeNumber) {
        // 根据 videoName 和 episodeNumber 获取视频的 URL
        String videoUrl = videoService.seleteEpisodeUrl(videoName, episodeNumber);

        if (videoUrl == null) {
            return Result.error("Video not found");
        }
        return Result.success(videoUrl);
    }

    //除了短剧走这个爬虫接口
    @GetMapping("/save")
    public Result save(@RequestParam String name,String classify){
        videoService.saveList(name,classify);
        return Result.success();
    }


    @GetMapping("/startDownload")
    public String startDownload(@RequestParam String name) {
        String taskId = UUID.randomUUID().toString();
        new Thread(() -> {
            try {
                videoService.saves(name,taskId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return taskId;
    }

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
                            DownloadProgressManager.removeProgress(taskId);
                            emitter.complete();
                            break;
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


    //短剧的爬虫接口
//    @GetMapping("/saves")
//    public Result url(@RequestParam String name){
//        videoService.saves(name);
//        return Result.success();
//    }


}
