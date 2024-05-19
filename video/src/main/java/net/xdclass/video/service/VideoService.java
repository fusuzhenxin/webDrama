package net.xdclass.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.video.entity.Video;


public interface VideoService extends IService<Video> {


    Integer seleteDiversitys(String name);

    String seleteEpisodeUrl(String videoName, Integer episodeNumber);

    void saveList(String name, String classify);

    void saves(String name,String taskId);
}
