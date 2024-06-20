package net.xdclass.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.video.entity.Details;
import net.xdclass.video.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NewsMapper extends BaseMapper<News> {
    List<Details> findTop10ByQuantity();

    List<News> findTop10ByCollect();


    List<Details> findById(@Param("list") List<Long> detailsIds);

    List<Details> findTop10ByScore();
}
