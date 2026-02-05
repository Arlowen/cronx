package io.cronx.web.model.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;

import io.cronx.web.constant.api.RequestMethod;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.Tolerate;

@Data
@FieldNameConstants
@TableName(value = "api_source")
@Builder
public class ApiSourceDO {

    @Tolerate
    public ApiSourceDO(){
    }

    @TableId(type = IdType.AUTO)
    private long          id;

    @TableField(insertStrategy = FieldStrategy.NOT_NULL, updateStrategy = FieldStrategy.NOT_NULL)
    private Date          gmtCreate;

    @TableField(insertStrategy = FieldStrategy.NOT_NULL, updateStrategy = FieldStrategy.NOT_NULL)
    private Date          gmtModified;

    private String        host;

}
