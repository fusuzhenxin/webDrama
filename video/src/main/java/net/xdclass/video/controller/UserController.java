package net.xdclass.video.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import net.xdclass.video.Exception.ServiceException;
import net.xdclass.video.common.Result;
import net.xdclass.video.controller.dto.UserPasswordDTO;
import net.xdclass.video.entity.User;
import net.xdclass.video.mapper.UserMapper;
import net.xdclass.video.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/api/user/register")
    public Result register(@RequestBody User user){
       userService.register(user);
       return Result.success();
    }

    @PostMapping("/apiOne/user/userRegister")
    public Result UserRegister(@RequestBody User user){
        return userService.UserRegister(user);
    }

    private final AtomicLong idGenerator = new AtomicLong(1);


    // 新增或者更新
    @PostMapping("/api/user/save")
    public Result save(@RequestBody User user) {
        String username = user.getUserName();
        User user1 = userMapper.selectUsername(username);
        if (user1 != null){
            return  Result.error("用户名已存在");
        }
        if (StrUtil.isBlank(user.getNickName())) {
            user.setNickName(username);
        }
        if(user.getPassword()==null){
            String encode = passwordEncoder.encode("123");
            user.setPassword(encode);
        }
        return Result.success(userMapper.insert(user));
    }
    // 新增或者更新
    @PostMapping("/api/user/edit")
    public Result Edit(@RequestBody User user) {
        return Result.success(userService.saveOrUpdate(user));
    }


    @PutMapping("/api/user/reset")
    public Result reset(@RequestBody UserPasswordDTO userPasswordDTO) {
        if (StrUtil.isBlank(userPasswordDTO.getUsername()) || StrUtil.isBlank(userPasswordDTO.getPhone())) {
            throw new ServiceException("参数异常");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", userPasswordDTO.getUsername());
        queryWrapper.eq("phoneNumber", userPasswordDTO.getPhone());
        List<User> list = userService.list(queryWrapper);
        if (CollUtil.isNotEmpty(list)) {
            User user = list.get(0);
            user.setPassword("123");
            userService.updateById(user);
        }
        return Result.success();
    }

    @DeleteMapping("/api/user/{id}")
    public Result delete(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }

    @PostMapping("/api/user/del/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        return Result.success(userService.removeByIds(ids));
    }

    @GetMapping("/api/user/")
    public Result findAll() {
        return Result.success(userService.list());
    }

    @GetMapping("/api/user/{id}")
    public Result findOne(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @GetMapping("/api/user/username/{userName}")
    public Result findByUsername(@PathVariable String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", userName);
        return Result.success(userService.getOne(queryWrapper));
    }
    /**
     * 修改密码
     *
     * @param userPasswordDTO
     * @return
     */
    @PostMapping("/api/user/password")
    public Result password(@RequestBody UserPasswordDTO userPasswordDTO) {
        userService.updatePassword(userPasswordDTO);
        System.out.println("----"+userPasswordDTO);
        return Result.success();
    }
    @GetMapping("/api/user/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String username,
                           @RequestParam(defaultValue = "") String email,
                           @RequestParam(defaultValue = "") String address) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(username)) {
            queryWrapper.like("user_name", username);
        }
        if (!"".equals(email)) {
            queryWrapper.like("email", email);
        }
        if (!"".equals(address)) {
            queryWrapper.like("address", address);
        }

        System.out.println("--"+userService.page(new Page<>(pageNum, pageSize), queryWrapper));
        return Result.success(userService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 导出接口
     */
    @GetMapping("/api/user/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        Date createTime=null;
        List<User> list = userService.list();
        // 通过工具类创建writer 写出到磁盘路径
//        ExcelWriter writer = ExcelUtil.getWriter(filesUploadPath + "/用户信息.xlsx");
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("userName", "用户名");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("nick_name", "昵称");
        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("phonenumber", "电话");
        writer.addHeaderAlias("address", "地址");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("avatar", "头像");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();

    }

    //导出部分字段
    @GetMapping("/api/user/exports")
    public void exports(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<User> list = userService.list();

        // 筛选并映射需要导出的字段
        List<Map<String, Object>> exportData = new ArrayList<>();
        for (User user : list) {
            Map<String, Object> userData = new LinkedHashMap<>();
            userData.put("用户名", user.getUserName());
            userData.put("昵称", user.getNickName());
            userData.put("邮箱", user.getEmail());
            // 添加其他需要导出的字段...

            exportData.add(userData);
        }

        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 设置标题别名，这里不需要再设置 User 对象的字段别名了

        // 一次性写出映射后的数据到excel，使用默认样式，强制输出标题
        writer.write(exportData, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    /**
     * excel 导入
     *
     * @param file
     * @throws Exception
     */
    @PostMapping("/api/user/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 方式1：(推荐) 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
//        List<User> list = reader.readAll(User.class);

        // 方式2：忽略表头的中文，直接读取表的内容
        List<List<Object>> list = reader.read(1);
        List<User> users = CollUtil.newArrayList();
        for (List<Object> row : list) {
            User user = new User();
            user.setUserName(row.get(0).toString());
            user.setPassword(row.get(1).toString());
            user.setNickName(row.get(2).toString());
            user.setEmail(row.get(3).toString());
            user.setPhonenumber(row.get(4).toString());
            user.setAddress(row.get(5).toString());
            user.setAvatar(row.get(6).toString());
            users.add(user);
        }

        userService.saveBatch(users);
        return Result.success(true);
    }

}
