package net.xdclass.video.service.Impl;

import net.xdclass.video.common.Result;
import net.xdclass.video.controller.dto.UserDTO;
import net.xdclass.video.entity.Menu;
import net.xdclass.video.entity.User;
import net.xdclass.video.entity.domain.LoginUser;
import net.xdclass.video.mapper.RoleMapper;
import net.xdclass.video.mapper.RoleMenuMapper;
import net.xdclass.video.service.LoginService;
import net.xdclass.video.service.MenuService;
import net.xdclass.video.utils.JwtUtil;
import net.xdclass.video.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpHeaders;

/**
 * @author 35238
 * @date 2023/7/12 0012 11:47
 */
@Service
//写登录的核心代码
public class LoginServiceImpl implements LoginService {

    @Autowired
    //先在SecurityConfig，使用@Bean注解重写官方的authenticationManagerBean类，然后这里才能注入成功
    private AuthenticationManager authenticationManager;

    @Autowired
    //RedisCache是我们在utils目录写好的类
    private RedisCache redisCache;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private MenuService menuService;
    @Override
    //ResponseResult和user是我们在domain目录写好的类
    public Result login(UserDTO userDTO) {

        //用户在登录页面输入的用户名和密码,把用户名和密码装在UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDTO.getUserName(),userDTO.getPassword());

        //获取AuthenticationManager的authenticate方法来进行用户认证
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        //判断上面那行的authenticate是否为null，如果是则认证没通过，就抛出异常
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("登录失败");
        }

        //如果认证通过，就使用userid生成一个jwt，然后把jwt存入Result后返回
        //从 Authentication 对象中提取经过身份验证的用户详细信息
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        //获取用户id
        String userid = loginUser.getXxuser().getId().toString();
        String jwt = JwtUtil.createJWT(userid);

        //获取角色
        String userType = loginUser.getXxuser().getUserType();

        List<Menu> roleMenus = getRoleMenus(userType);

        System.out.println(roleMenus);
        userDTO.setMenus(roleMenus);
        userDTO.setToken(jwt);
        userDTO.setUserType(userType);
        userDTO.setUserName(userDTO.getUserName());
        userDTO.setUserType(loginUser.getXxuser().getUserType());

        redisCache.setCacheObject("login:"+userid,loginUser);
        return Result.success(userDTO);
    }

    @Override
    public Result yylogout() {

        //获取我们在JwtAuthenticationTokenFilter类写的SecurityContextHolder对象中的用户id
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        //loginUser是我们在domain目录写好的实体类
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        //获取用户id
        Integer userid = loginUser.getXxuser().getId();

        //根据用户id，删除redis中的token值，注意我们的key是被 login: 拼接过的，所以下面写完整key的时候要带上 longin:
        redisCache.deleteObject("login:"+userid);

        return Result.success("注销成功");
    }

    /**
     * 获取当前角色的菜单列表
     * @param roleFlag
     * @return
     */
    private List<Menu> getRoleMenus(String roleFlag) {
        Long roleId = roleMapper.selectByFlag(roleFlag);
// 当前角色的所有菜单id集合
        List<Long> menuIds = roleMenuMapper.selectByRoleId(roleId);
        System.out.println(menuIds);
// 查出系统所有的菜单(树形)
        List<Menu> menus = menuService.findMenus("");
// new一个最后筛选完成之后的list
        List<Menu> roleMenus = new ArrayList<>();
// 筛选当前用户角色的菜单
        for (Menu menu : menus) {
            System.out.println("id=="+menu.getId());
            if (menuIds.contains(menu.getId())) {

                System.out.println("id=="+menu.getId());
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();
            // removeIf()  移除 children 里面不在 menuIds集合中的 元素
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenus;
    }


}
