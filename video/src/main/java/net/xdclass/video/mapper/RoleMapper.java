package net.xdclass.video.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.video.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author
 *
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select id from sys_role where role_key = #{flag}")
    Long selectByFlag(@Param("flag") String flag);
}
