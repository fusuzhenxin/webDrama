package net.xdclass.crawler.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.crawler.entity.Crawler;
import net.xdclass.video.entity.Acquire;
import net.xdclass.video.entity.Details;

import java.util.List;

public interface CrawlerService  {
    void saveList(String name, String classify, String taskId);

    void saveListOneway(String name, String classify, String taskId);

    void saves(String name, String taskId);

    List<Acquire> seleteAcquireLists();

    List<Acquire> saveListAcquire(String name, String classify);
}
