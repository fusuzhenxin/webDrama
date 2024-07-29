package net.xdclass.video.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.xdclass.video.common.Result;
import net.xdclass.video.entity.Admin;
import net.xdclass.video.entity.domain.Comments;
import net.xdclass.video.mapper.CommentsMapper;
import net.xdclass.video.service.AdminService;
import net.xdclass.video.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentsController {
    @Autowired
    private CommentsService commentsService;

    @Autowired
    private CommentsMapper commentsMapper;
        /**
         * 新增
         * @param
         * @return
         */
        @PostMapping("/apiOne/comments/save")
        public Result save(@RequestBody Comments comments){
            commentsService.saveOrUpdate(comments);
            return Result.success();
        }
        //使用了 @RequestParam 注解来声明方法的参数，用于从请求中获取对应的参数值。
        @GetMapping("/api/comments/page")
        public Result findPage(@RequestParam(defaultValue = "") String name,
                               @RequestParam Integer pageNum,
                               @RequestParam Integer pageSize) {
            //QueryWrapper 来构建查询条件，并基于条件执行了分页查询
            QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            if (!"".equals(name)) {
                queryWrapper.like("name", name);
            }
            Page<Comments> page = commentsService.page(new Page<>(pageNum, pageSize), queryWrapper);
            return Result.success(page);
        }

        @GetMapping("/apiOne/comments")
        public  Result findAll(@RequestParam String name){
            QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name",name);
            List<Comments> commentsList=commentsMapper.selectList(queryWrapper);
            return Result.success(commentsList);
        }

        @DeleteMapping("/api/comments/{id}")
        public Result deleteById(@PathVariable Integer id){
            commentsService.removeById(id);
            return Result.success();
        }

        //批量删除
        @PostMapping("/api/comments/del/batch")
        public Result deleteBatch(@RequestBody List<Integer> ids) {
            commentsService.removeByIds(ids);
            return Result.success();
        }
    }
