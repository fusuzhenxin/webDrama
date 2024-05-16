package net.xdclass.video.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.xdclass.video.entity.Details;
import net.xdclass.video.mapper.DetailsMapper;
import net.xdclass.video.service.DetailsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.System.out;

@Service
public class DetailsServiceImpl extends ServiceImpl<DetailsMapper , Details>  implements DetailsService{


    @Override
    public Details selectLikes(String name) {
        out.println("你好");
        Details details = this.baseMapper.selectLikes(name);
        int quantity = Integer.parseInt(details.getQuantity());
        quantity++;
        String quantityStr = String.valueOf(quantity);
        this.baseMapper.updateQuantityStr(quantityStr,name);
        Details details1 = this.baseMapper.selectLikes(name);

        return details1;
    }

    @Override
    public Details selectlikesDelete(String name) {
        Details details = this.baseMapper.selectLikes(name);
        int quantity = Integer.parseInt(details.getQuantity());
        --quantity;
        String quantityStr = String.valueOf(quantity);
        this.baseMapper.updateQuantityStr(quantityStr,name);
        Details details1 = this.baseMapper.selectLikes(name);
        return details1;
    }

    @Override
    public List<Details> finAllName(String name) {
        return this.baseMapper.finAllName(name);
    }

    @Override
    public List<Details> search(String name) {
        return this.baseMapper.search(name);
    }

    @Override
    public void add(Details details) {

    }


    @Override
    public Details selectCollect(String name) {
        Details details = this.baseMapper.selectCollect(name);
        int collect = Integer.parseInt(details.getCollect());
        collect++;
        String collectStr = String.valueOf(collect);
        this.baseMapper.updateCollect(collectStr,name);
        Details details1=this.baseMapper.selectCollect(name);
        return details1;
    }
    @Override
    public Details selectCollectDelete(String name) {
        Details details = this.baseMapper.selectCollect(name);
        int collect = Integer.parseInt(details.getCollect());
        --collect;
        String collectStr = String.valueOf(collect);
        this.baseMapper.updateCollect(collectStr,name);
        Details details1 = this.baseMapper.selectCollect(name);
        return details1;
    }




}
