package net.xdclass.video.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.xdclass.video.common.Result;
import net.xdclass.video.entity.User;
import net.xdclass.video.mapper.UserMapper;
import net.xdclass.video.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/api/user/register")
    public Result register(@RequestBody User user){
       userService.register(user);
       return Result.success();
    }

    @PostMapping("/apiOne/user/userRegister")
    public Result UserRegister(@RequestBody User user){
        return userService.UserRegister(user);
    }
}
