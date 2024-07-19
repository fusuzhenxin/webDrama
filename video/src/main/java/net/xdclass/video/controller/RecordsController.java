package net.xdclass.video.controller;

import net.xdclass.video.common.Result;
import net.xdclass.video.entity.Records;
import net.xdclass.video.service.RecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecordsController {
    @Autowired
    private RecordsService recordsService;


    //观看记录
    @PostMapping("/apiOne/news/click/")
    public Result recordsVideo(@RequestBody Records records){

        recordsService.recordsVideo(records);
        return Result.success();
    }

    @PostMapping("/apiOne/news/findAll")
    public Result recordsVideoAll(@RequestBody Records records){
        List<Records> recordsList=recordsService.recordsVideoAll(records.getUsername());
        return Result.success(recordsList);
    }
}
