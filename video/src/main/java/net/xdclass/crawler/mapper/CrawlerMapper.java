package net.xdclass.crawler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.crawler.entity.Crawler;
import net.xdclass.video.entity.Details;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CrawlerMapper extends BaseMapper<Crawler> {
}
