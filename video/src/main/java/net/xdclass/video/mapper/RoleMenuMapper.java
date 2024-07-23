package net.xdclass.video.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.video.entity.RoleMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    @Delete("delete from sys_role_menu where role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    //根据角色id查找出拥有的菜单id
    @Select("select menu_id from sys_role_menu where role_id = #{roleId}")
    List<Long> selectByRoleId(@Param("roleId")Long roleId);

}
