package net.xdclass.video.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.video.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 35238
 * @date 2023/7/13 0013 21:35
 */
@Mapper
//BaseMapper是mybatisplus官方提供的接口，里面提供了很多单表查询的方法
public interface MenuMapper extends BaseMapper<Menu> {
    //由于是多表联查，mybatisplus的BaseMapper接口没有提供，我们需要自定义方法，所以需要创建对应的mapper文件，定义对应的sql语句
    List<String> selectPermsByUserId(Long id);
}
