package net.xdclass.video.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.xdclass.video.entity.Details;
import net.xdclass.video.entity.Records;
import net.xdclass.video.mapper.DetailsMapper;
import net.xdclass.video.mapper.RecordsMapper;
import net.xdclass.video.service.RecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordsServiceImpl extends ServiceImpl<RecordsMapper, Records> implements RecordsService {
    @Autowired
    private DetailsMapper detailsMapper;
    @Autowired
    private RecordsMapper recordsMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    private String recordKey;
    @Override
    public void recordsVideo(Records records) {

        Integer detailsId = records.getId();
        QueryWrapper<Details> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("details_id",detailsId);
        Details details = detailsMapper.selectOne(queryWrapper);
        Records records1 = new Records();
        records1.setName(details.getName());
        records1.setCover(details.getCover());
        records1.setUsername(records.getUsername());
        String recordsDetailsKey="recordsDetails:recordsDetailsKey"+records.getUsername()+"--"+details.getName();

        Object recordsDetails = redisTemplate.opsForValue().get(recordsDetailsKey);
        if (recordsDetails==null){
            QueryWrapper<Records> queryWrapperOne = new QueryWrapper<>();
            //根据剧名和登录的用户名来查询数据库有没有数据
            queryWrapperOne.eq("name",details.getName()).eq("username",records.getUsername());
            Records records2 = recordsMapper.selectOne(queryWrapperOne);
            //没有这个剧和有登录了的用户才能存储数据
            if (records2 == null && records.getUsername()!=null){
                recordsMapper.insert(records1);
                redisTemplate.opsForValue().set(recordsDetailsKey,records2);
                redisTemplate.delete(recordKey);
            }

        }else {
            return;
        }

    }

//    @Scheduled(cron = "0 15 10 ? * 6L")
//    public  void Scheduled(){
//
//        redisTemplate.delete(recordKey);
//
//        System.out.println("清除缓存观看记录更新数据-线程2");
//    }

    @Override
    public List<Records> recordsVideoAll(String username) {
        recordKey="record:recordKey"+"--"+username;
        Object recordVideoKey = redisTemplate.opsForValue().get(recordKey);
        if (recordVideoKey == null){
            QueryWrapper<Records> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username",username);
            System.out.println("--"+username);
            List<Records> records1 = recordsMapper.selectList(queryWrapper);
            redisTemplate.opsForValue().set(recordKey,records1);
            return records1;
        }

        return (List<Records>) recordVideoKey;
    }
}
