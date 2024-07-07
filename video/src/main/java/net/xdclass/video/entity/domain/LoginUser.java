package net.xdclass.video.entity.domain;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.xdclass.video.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 35238
 * @date 2023/7/11 0011 20:59
 */
@Data //get和set方法
@NoArgsConstructor //无参构造
//@AllArgsConstructor  //带参构造
//实现UserDetails接口之后，要重写UserDetails接口里面的7个方法
public class LoginUser implements UserDetails {

    //查询用户的用户名和密码等数据信息
    private User xxuser;

    //查询用户权限信息
    private List<String> xxpermissions;

    public LoginUser(User xxuser,List<String> permissions){
        this.xxuser=xxuser;
        this.xxpermissions=permissions;
    }

    //我们把这个List写到外面这里了，注意成员变量名一定要是authorities，不然会出现奇奇怪怪的问题
    @JSONField(serialize = false) //这个注解的作用是不让下面那行的成员变量序列化存入redis，避免redis不支持而报异常
    private List<SimpleGrantedAuthority> authorities;

    @Override
    //用于返回权限信息。现在我们正在学'认证'，'权限'后面才学。所以返回null即可
    //当要查询用户信息的时候，我们不能单纯返回null，要重写这个方法，作用是封装权限信息
    public Collection<? extends GrantedAuthority> getAuthorities() { //重写GrantedAuthority接口的getAuthorities方法

        /* 第一种权限集合的转换写法如下，传统的方式
        //把xxpermissions中的String类型的权限信息(也就是"test","adminAuth","huanfAuth")封装成SimpleGrantedAuthority对象
        //List<GrantedAuthority> authorities = new ArrayList<>(); //简化这行如下一行，我们把authorities成员变量写到外面了
        authorities = new ArrayList<>();
        for (String yypermission : xxpermissions) {
            SimpleGrantedAuthority yyauthority = new SimpleGrantedAuthority(yypermission);
            authorities.add(yyauthority);
        }
        */

        /* 第二种权限集合的转换写法如下，函数式编程 + stream流 的方式，双引号表示方法引用 */
        //当authorities集合为空，就说明是第一次，就需要转换，当不为空就说明不是第一次，就不需要转换直接返回
        if(authorities != null){ //严谨来说这个if判断是避免整个调用链中security本地线程变量在获取用户时的重复解析，和redis存取无关
            return authorities;
        }
        //为空的话就会执行下面的转换代码
        //List<SimpleGrantedAuthority> authorities = xxpermissions //简化这行如下一行，我们把authorities成员变量写到外面了
        authorities = xxpermissions
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        //最终返回转换结果
        return authorities;
    }


    @Override
    //用于获取用户密码。由于使用的实体类是User，所以获取的是数据库的用户密码
    public String getPassword() {
        return xxuser.getPassword();
    }

    @Override
    //用于获取用户名。由于使用的实体类是User，所以获取的是数据库的用户名
    public String getUsername() {
        return xxuser.getUserName();
    }

    @Override
    //判断登录状态是否过期。把这个改成true，表示永不过期
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    //判断账号是否被锁定。把这个改成true，表示未锁定，不然登录的时候，不让你登录
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    //判断登录凭证是否过期。把这个改成true，表示永不过期
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    //判断用户是否可用。把这个改成true，表示可用状态
    public boolean isEnabled() {
        return true;
    }
}
