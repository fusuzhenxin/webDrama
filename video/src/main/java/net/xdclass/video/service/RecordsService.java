package net.xdclass.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.video.entity.Records;

import java.util.List;


public interface RecordsService extends IService<Records> {
    void recordsVideo(Records records);

    List<Records> recordsVideoAll(String username);


}
