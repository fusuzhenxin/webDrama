package net.xdclass.video.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.xdclass.video.entity.domain.Comments;
import net.xdclass.video.mapper.CommentsMapper;
import net.xdclass.video.service.CommentsService;
import org.springframework.stereotype.Service;

@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements CommentsService {

}
