package net.xdclass.video.crawler.service;

import net.xdclass.video.entity.Acquire;

import java.util.List;

public interface CrawlerService  {
    void saveList(String name, String classify, String taskId);

    void saveListOneway(String name, String classify, String taskId);

    void saves(String name, String taskId);

    List<Acquire> seleteAcquireLists();

    List<Acquire> saveListAcquire(String name, String classify);
}
