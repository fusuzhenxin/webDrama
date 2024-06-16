package net.xdclass.crawler.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.xdclass.crawler.entity.Crawler;
import net.xdclass.crawler.mapper.CrawlerMapper;
import net.xdclass.crawler.service.CrawlerService;
import net.xdclass.video.entity.Acquire;
import net.xdclass.video.entity.Details;
import net.xdclass.video.mapper.AcquireMapper;
import net.xdclass.video.mapper.DetailsMapper;
import net.xdclass.video.service.AcquireService;
import net.xdclass.video.service.DetailsService;
import org.springframework.stereotype.Service;

@Service
public class CrawlerServiceImpl extends ServiceImpl<CrawlerMapper, Crawler> implements CrawlerService {
}
