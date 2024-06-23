package net.xdclass.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.video.entity.Details;
import net.xdclass.video.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NewsMapper extends BaseMapper<News> {
    List<Details> findTop10ByQuantity();

    List<Details> findTop10ByCollect();


    List<Details> findById(@Param("list") List<Long> detailsIds);

    List<News> findNewsById(@Param("list") List<Long> detailsIds);

    List<Details> findTop10ByScore();

    List<News> findNewsIds();
}
