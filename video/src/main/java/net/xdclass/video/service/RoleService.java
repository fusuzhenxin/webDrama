package net.xdclass.video.service;



import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.video.entity.Role;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author
 *
 */
public interface RoleService extends IService<Role> {

    void setRoleMenu(Long roleId, List<Long> menuIds);

    List<Long> getRoleMenu(Long roleId);
}
