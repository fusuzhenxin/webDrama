package net.xdclass.video.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.video.entity.FileOne;

import java.util.List;

public interface FileService extends IService<FileOne> {
    List<FileOne> selectTop10(String classify);
    void saveFile(FileOne files);

    List<FileOne> selectSearch(String name);


    List<FileOne> selectList(QueryWrapper<FileOne> queryWrapper);
}
