package net.xdclass.video.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.xdclass.video.entity.Acquire;
import net.xdclass.video.mapper.AcquireMapper;
import net.xdclass.video.service.AcquireService;
import org.springframework.stereotype.Service;

@Service
public class AcquireServiceImpl extends ServiceImpl<AcquireMapper, Acquire> implements AcquireService {
}
