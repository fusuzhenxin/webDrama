package net.xdclass.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.video.entity.News;

public interface NewsService extends IService<News> {
    void saveList();

    void preloadTop10News();

    void incrementVideoScore(Long detailsId);
}
