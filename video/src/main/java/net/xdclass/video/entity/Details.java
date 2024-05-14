package net.xdclass.video.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("details")
public class Details {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String name;
    private String classify;
    private String description; //描述
    private String actors;
    private String quantity;
    private String collect;
    private String cover;
    private String md5;
}
