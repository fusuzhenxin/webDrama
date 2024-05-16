package net.xdclass.video.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("images")
public class Images {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String type;
    private Long size;
    private String cover;
    private String md5;
    @TableField("original_filename")
    private String originalFilename;
}
