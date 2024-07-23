package net.xdclass.video.controller.dto;

import lombok.Data;
import net.xdclass.video.entity.Menu;

import java.util.List;

/**
 * 接受前端登录请求的参数
 */
@Data
public class UserDTO {
    private Long id;
    private String userName;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String token;
    private String userType;
    private List<Menu> menus;
}
