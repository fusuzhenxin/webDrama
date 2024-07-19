package net.xdclass.video.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.xdclass.video.common.Result;
import net.xdclass.video.config.SecurityConfig;
import net.xdclass.video.entity.Admin;

import net.xdclass.video.entity.User;
import net.xdclass.video.mapper.AdminMapper;
import net.xdclass.video.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class AdminController {
    @Autowired
    private AdminService adminService;

        /**
         * 新增
         * @param admin
         * @return
         */
        @PostMapping("/api/admin/save")
        public Result save(@RequestBody Admin admin){
            adminService.saveOrUpdate(admin);
            return Result.success();
        }
        //使用了 @RequestParam 注解来声明方法的参数，用于从请求中获取对应的参数值。
        @GetMapping("/api/admin/page")
        public Result findPage(@RequestParam(defaultValue = "") String username,
                               @RequestParam Integer pageNum,
                               @RequestParam Integer pageSize) {
            //QueryWrapper 来构建查询条件，并基于条件执行了分页查询
            QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            if (!"".equals(username)) {
                queryWrapper.like("username", username);
            }
            Page<Admin> page = adminService.page(new Page<>(pageNum, pageSize), queryWrapper);
            return Result.success(page);
        }

        @DeleteMapping("/api/admin/{id}")
        public Result deleteById(@PathVariable Integer id){
            adminService.removeById(id);
            return Result.success();
        }

        //批量删除
        @PostMapping("/api/admin/del/batch")
        public Result deleteBatch(@RequestBody List<Integer> ids) {
            adminService.removeByIds(ids);
            return Result.success();
        }
    }
