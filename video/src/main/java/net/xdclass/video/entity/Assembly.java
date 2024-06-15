package net.xdclass.video.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Assembly {
    @TableId(value = "id",type = IdType.AUTO)
    @TableField("id")
    private String taskId;
    private String name;
}
