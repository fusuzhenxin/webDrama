package net.xdclass.video.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.video.common.Result;
import net.xdclass.video.controller.dto.UserPasswordDTO;
import net.xdclass.video.entity.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


public interface UserService extends IService<User> {

    void register(User user);

    Result UserRegister(User user);
    void updatePassword(UserPasswordDTO userPasswordDTO);
}
