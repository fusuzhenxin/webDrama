package net.xdclass.video.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.xdclass.video.entity.FileOne;
import net.xdclass.video.mapper.FileMapper;
import net.xdclass.video.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.System.out;

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileOne> implements FileService {
    @Autowired
    private FileMapper fileMapper;
    @Override
    public List<FileOne> selectTop10(String classify) {
        return fileMapper.selectTop10(classify);
    }

    @Override
    public List<FileOne> selectSearch(String name) {
         return this.baseMapper.selectSearch(name);
    }

    @Override
    @Transactional
    public List<FileOne> selectList(QueryWrapper<FileOne> queryWrapper) {
        return fileMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void saveFile(FileOne files) {
        out.println("你好");
        try {
            fileMapper.insert(files);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
