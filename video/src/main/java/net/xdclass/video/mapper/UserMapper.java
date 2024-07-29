package net.xdclass.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.video.controller.dto.UserPasswordDTO;
import net.xdclass.video.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("update sys_user set password = #{newPassword} where user_name = #{username}")
    int updatePassword(UserPasswordDTO userPasswordDTO);

    @Select("select * from sys_user where user_name = #{username} ")
    User selectUsername(String username);
    @Select("select password from sys_user where user_name = #{username} ")
    String getUsernamePassword(String username);
    @Select("select id from sys_user where user_name = #{username} ")
    String getUsernameId(String username);
}

