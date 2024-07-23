package net.xdclass.video.service.Impl;

import cn.hutool.core.collection.CollUtil;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import net.xdclass.video.entity.Menu;
import net.xdclass.video.entity.Role;
import net.xdclass.video.entity.RoleMenu;
import net.xdclass.video.mapper.RoleMapper;
import net.xdclass.video.mapper.RoleMenuMapper;
import net.xdclass.video.service.MenuService;
import net.xdclass.video.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author
 *
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private MenuService menuService;

    @Transactional
    @Override
    public void setRoleMenu(Long roleId, List<Long> menuIds) {
//        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("role_id", roleId);
//        roleMenuMapper.delete(queryWrapper);

        // 先删除当前角色id所有的绑定关系
        roleMenuMapper.deleteByRoleId(roleId);

        // 再把前端传过来的菜单id数组绑定到当前的这个角色id上去
        List<Long> menuIdsCopy = CollUtil.newArrayList(menuIds);
        for (Long menuId : menuIds) {
            //获取菜单详细信息
            Menu menu = menuService.getById(menuId);
            if (menu.getPid() != null && !menuIdsCopy.contains(menu.getPid())) {
                // 二级菜单 并且传过来的menuId数组里面没有它的父级id
                // 那么我们就得补上这个父级id
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menu.getId());
                roleMenuMapper.insert(roleMenu);
                menuIdsCopy.add(menu.getPid());
            }
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    @Override
    public List<Long> getRoleMenu(Long roleId) {

        return roleMenuMapper.selectByRoleId(roleId);
    }

}
