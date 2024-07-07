package net.xdclass.video.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.xdclass.video.entity.News;
import net.xdclass.video.entity.User;
import net.xdclass.video.mapper.NewsMapper;
import net.xdclass.video.mapper.UserMapper;
import net.xdclass.video.service.NewsService;
import net.xdclass.video.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
