package net.xdclass.video.common;


import net.xdclass.video.entity.domain.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 35238
 * @date 2023/7/16 0016 14:21
 */
@Component("huanfEX")
//自定义权限校验的方法
public class HuanfExpressionRoot {

    //自定义权限校验的方法
    public boolean huanfHasAuthority(String authority){

        //获取用户具有的权限字符串，有可能用户具有多个权限字符串，所以获取后是一个集合
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //LoginUser是我们在domain目录写好的实体类
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        List<String> xxpermissions = loginUser.getXxpermissions();

        //判断用户权限集合中，是否存在跟业务接口(业务接口的权限字符串会作为authority形参传进来)一样的权限
        return xxpermissions.contains(authority);
    }

}
