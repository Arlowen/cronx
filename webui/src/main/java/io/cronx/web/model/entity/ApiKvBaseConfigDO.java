package io.cronx.web.model.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;

@Data
@TableName(value = "api_kv_base_config")
public class ApiKvBaseConfigDO {

    @TableId(type = IdType.AUTO)
    private Long   id;

    @TableField(insertStrategy = FieldStrategy.NOT_NULL, updateStrategy = FieldStrategy.NOT_NULL)
    private Date   gmtCreate;

    @TableField(insertStrategy = FieldStrategy.NOT_NULL, updateStrategy = FieldStrategy.NOT_NULL)
    private Date   gmtModified;

    private long   apiSourceId;

    private String configName;

    private String configValue;

    private String defaultValue;

    private String valueRange;

    private String descKey;
}
