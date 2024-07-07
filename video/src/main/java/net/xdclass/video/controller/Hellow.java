package net.xdclass.video.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hellow {
    @GetMapping("/hello")
    //只有用户具有叫test的权限，才能访问这个路径
//    @PreAuthorize("hasAuthority('system:dept:list')")
//    @PreAuthorize("hasAnyAuthority('system:dept:list')")
//    @PreAuthorize("hasRole('system:dept:list')") //只能传一个权限字符串，多传会报红线,这个是业务是Roles_system:dept:list  与用户system:dept:list所以不一致
//    @PreAuthorize("hasAnyRole('zidingyi','huanf','system:dept:list')") //跟hasRole一样操作，只不过这个可以多参数
    //自定义权限校验方法，huanfHasAuthority
    @PreAuthorize("@huanfEX.huanfHasAuthority('system:dept:list')")
    public String hello(){
        return "老弟，你成功了";
    }


}
