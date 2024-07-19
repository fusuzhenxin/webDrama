package net.xdclass.video.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.video.common.Result;
import net.xdclass.video.entity.User;

public interface UserService extends IService<User> {

    void register(User user);

    Result UserRegister(User user);
}
