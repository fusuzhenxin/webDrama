package net.xdclass.video.controller;
import net.xdclass.video.common.Result;
import net.xdclass.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    //短剧的爬虫接口
    @GetMapping("/saves")
    public Result url(@RequestParam String name){
        videoService.saves(name);
        return Result.success();
    }


}
