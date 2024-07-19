package net.xdclass.video.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("records")
public class Records {
    @TableId(value = "id",type = IdType.AUTO)
    @TableField("id")
    private Integer id;
    private String name;
    private String cover;
    private String username;
}
