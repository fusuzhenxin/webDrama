package net.xdclass.video.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.xdclass.video.common.Result;
import net.xdclass.video.controller.dto.UserDTO;
import net.xdclass.video.entity.Admin;
import net.xdclass.video.entity.User;
import net.xdclass.video.mapper.UserMapper;
import net.xdclass.video.service.LoginService;
import net.xdclass.video.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {


    @Autowired
    //LoginService是我们在service目录写好的接口
    private LoginService loginService;


    @PostMapping("/api/user/login")
    //ResponseResult和user是我们在domain目录写好的类
    public Result xxlogin(@RequestBody UserDTO userDTO){
        //登录
        return loginService.login(userDTO);

    }


    //-----------------------------------退出登录--------------------------------

    @GetMapping("/api/user/logout")
    //ResponseResult是我们在domain目录写好的实体类
    public Result xxlogout(){
        return loginService.yylogout();
    }






}
