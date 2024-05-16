package net.xdclass.video.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.xdclass.video.entity.Images;
import net.xdclass.video.mapper.ImagesMapper;
import net.xdclass.video.service.ImagesService;
import org.springframework.stereotype.Service;

@Service
public class ImagesServiceImpl extends ServiceImpl<ImagesMapper, Images> implements ImagesService {
}
