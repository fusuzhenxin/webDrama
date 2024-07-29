package net.xdclass.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.video.entity.FileOne;
import net.xdclass.video.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {

    Integer seleteDiversitys(String name);


    FileOne seleteEpisodeUrl(String videoName, Integer episodeNumber);

    void save(String name, String url);
}
