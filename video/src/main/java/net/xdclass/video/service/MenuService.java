package net.xdclass.video.service;


import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.video.entity.Menu;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author
 *
 */
public interface MenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
