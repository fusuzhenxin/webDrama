package net.xdclass.video.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("sys_role_menu")
@Data
public class RoleMenu {

    private Long roleId;
    private Long menuId;

}
