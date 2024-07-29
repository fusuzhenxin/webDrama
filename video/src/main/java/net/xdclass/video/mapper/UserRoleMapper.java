package net.xdclass.video.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.video.entity.RoleMenu;
import net.xdclass.video.entity.UserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {


}
