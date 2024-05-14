package net.xdclass.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.xdclass.video.entity.Details;

import java.util.List;

public interface DetailsService extends IService<Details> {
    Details selectCollect(String name);

    Details selectLikes(String name);

    Details selectCollectDelete(String name);


    Details selectlikesDelete(String name);

    List<Details> finAllName(String name);

    List<Details> search(String name);

    void add(Details details);

    void saveVideo();
}
