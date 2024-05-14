package net.xdclass.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.video.entity.FileOne;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper extends BaseMapper<FileOne> {
    List<FileOne> selectTop10(String classify);

    List<FileOne> selectTop101();

    FileOne selectName(String name);

    List<FileOne> selectSearch(String name);

    void inserts(FileOne files);
}
