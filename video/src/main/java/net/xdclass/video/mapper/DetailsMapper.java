package net.xdclass.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xdclass.video.entity.Details;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DetailsMapper extends BaseMapper<Details> {

    List<Details> selectTop10(String classify);


    Details selectLikes(String name);


    void updateQuantityStr(String quantityStr, String name);

    void updateCollect(String collectStr, String name);

    Details selectCollect(String name);

    List<Details> finAllName(String name);

    List<Details> search(String name);

    List<Details> selectAcquiesce(String classify);


}
