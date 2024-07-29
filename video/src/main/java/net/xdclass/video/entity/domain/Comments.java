package net.xdclass.video.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comments")
public class Comments {
    @TableId(value = "id",type = IdType.AUTO)
    @TableField("id")
    private String id;
    private String userId;
    private String name;
    private String content;
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;

    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private String updatetime;
}
