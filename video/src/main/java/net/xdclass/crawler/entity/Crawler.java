package net.xdclass.crawler.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("acquire")
public class Crawler {
    @TableId(value = "id",type = IdType.AUTO)
    @TableField("id")
    private Integer id;
    private String name;
    private String classify;
    private String description; //描述
    private String actors;
    private String cover;
}
