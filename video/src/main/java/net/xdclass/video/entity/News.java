package net.xdclass.video.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("news")
public class News {
    @TableId(value = "id",type = IdType.AUTO)
    @TableField("id")
    private String id;
    private String name;
    private String browse; //浏览
    private String source;//来源
    private String description;
    private String cover;//封面

    @TableField("update_time")
    private String updateTime;
}
