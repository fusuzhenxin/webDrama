package net.xdclass.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.video.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
