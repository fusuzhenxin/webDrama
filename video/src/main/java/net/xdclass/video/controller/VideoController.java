package net.xdclass.video.controller;

import net.xdclass.video.common.Result;
import net.xdclass.video.entity.Details;
import net.xdclass.video.entity.Video;
import net.xdclass.video.mapper.VideoMapper;
import net.xdclass.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideoController {
    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private VideoService videoService;

    @GetMapping("/diversity")
    public Result diversitys(@RequestParam String name){
       Integer diversity= videoService.seleteDiversitys(name);
       return Result.success(diversity);
    }

    @GetMapping("/{videoName}/episode/{episodeNumber}")
    public Result getEpisodeUrl(@PathVariable String videoName, @PathVariable Integer episodeNumber) {
        // 根据 videoName 和 episodeNumber 获取视频的 URL
        String videoUrl = videoService.seleteEpisodeUrl(videoName, episodeNumber);

        if (videoUrl == null) {
            return Result.error("Video not found");
        }
        return Result.success(videoUrl);
    }

    @GetMapping("/save")
    public Result save(@RequestParam String name,String classify){
        videoService.saveList(name,classify);
        return Result.success();
    }

    @GetMapping("/saves")
    public Result url(@RequestParam String name){
        videoService.saves(name);
        return Result.success();
    }


}
