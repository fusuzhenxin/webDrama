package net.xdclass.video.controller;

import net.xdclass.video.common.Result;
import net.xdclass.video.entity.User;
import net.xdclass.video.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    //LoginService是我们在service目录写好的接口
    private LoginService loginService;

    @PostMapping("/api/user/login")
    //ResponseResult和user是我们在domain目录写好的类
    public Result xxlogin(@RequestBody User user){
        //登录
        return loginService.login(user);
    }

    //-----------------------------------退出登录--------------------------------

    @RequestMapping("/api/user/logout")
    //ResponseResult是我们在domain目录写好的实体类
    public Result xxlogout(){
        return loginService.yylogout();
    }



}
