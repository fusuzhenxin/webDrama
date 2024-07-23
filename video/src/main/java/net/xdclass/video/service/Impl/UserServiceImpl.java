package net.xdclass.video.service.Impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import net.xdclass.video.Exception.ServiceException;
import net.xdclass.video.common.Result;
import net.xdclass.video.controller.dto.UserPasswordDTO;
import net.xdclass.video.entity.News;
import net.xdclass.video.entity.User;
import net.xdclass.video.mapper.NewsMapper;
import net.xdclass.video.mapper.UserMapper;
import net.xdclass.video.service.NewsService;
import net.xdclass.video.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.rmi.ServerException;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @SneakyThrows
    @Override
    public void register(User user) {
        String userName = user.getUserName();
        QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
        QueryWrapper<User> queryWrapper = objectQueryWrapper.eq("user_name", userName);
        User userOne = userMapper.selectOne(queryWrapper);
        if (userOne!=null){
            throw new ServerException("用户已存在");
        }
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setNickName(userName);
        user.setUserName(userName);
        user.setPassword(encodePassword);
        user.setUserType("ROLE_ADMIN");
        userMapper.insert(user);
    }

    @SneakyThrows
    @Override
    public Result UserRegister(User user) {
        String userName = user.getUserName();
        QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
        QueryWrapper<User> queryWrapper = objectQueryWrapper.eq("user_name", userName);
        User userOne = userMapper.selectOne(queryWrapper);
        if (userOne!=null){
            return Result.error(String.valueOf(401),"用户已存在");
        }
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setNickName(userName);
        user.setUserName(userName);
        user.setPassword(encodePassword);
        user.setUserType("ROLE_USER");
        userMapper.insert(user);
        return Result.success();
    }

    @Override
    public void updatePassword(UserPasswordDTO userPasswordDTO) {
        //先把密码加密
        System.out.println(userPasswordDTO);
        userPasswordDTO.setNewPassword(passwordEncoder.encode(userPasswordDTO.getNewPassword()));
        String password=userMapper.getUsernamePassword(userPasswordDTO.getUsername());
        System.out.println("====="+password);
        boolean isPasswordMatch  = BCrypt.checkpw(userPasswordDTO.getPassword(), password);
        if (isPasswordMatch){
            int update = userMapper.updatePassword(userPasswordDTO);
            if (update < 1) {
                throw new ServiceException("密码错误");
            }
        }

    }
}
