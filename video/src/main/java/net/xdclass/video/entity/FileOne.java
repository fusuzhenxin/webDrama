package net.xdclass.video.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("file")
public class FileOne {
    @TableId(type = IdType.AUTO)
    @TableField("episode_id")
    private Integer episode_id;
    private String name;
    private String type;
    private Long size;
    private String url;
    private String md5;
    private String diversity;
    private String originalFilename;
}
