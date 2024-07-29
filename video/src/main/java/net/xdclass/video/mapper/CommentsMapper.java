package net.xdclass.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.video.entity.domain.Comments;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentsMapper extends BaseMapper<Comments> {
}
