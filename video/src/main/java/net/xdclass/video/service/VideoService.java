package net.xdclass.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.video.entity.Acquire;
import net.xdclass.video.entity.Details;
import net.xdclass.video.entity.Video;

import java.util.List;


public interface VideoService extends IService<Video> {


    Integer seleteDiversitys(String name);

    String seleteEpisodeUrl(String videoName, Integer episodeNumber);

    void saveList(String name, String classify,String taskId);

    void saves(String name,String taskId);

    List<Details> seleteAcquireList(String name);

    List<Acquire> seleteAcquireLists();

    List<Acquire> saveListAcquire(String name, String classify);

    void saveListOneway(String name, String classify, String taskId);
}
