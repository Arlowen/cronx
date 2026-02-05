package io.cronx.web.model.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.Tolerate;

@Data
@FieldNameConstants
@TableName(value = "opt_user")
@Builder
public class OptUserDO {

    @Tolerate
    public OptUserDO(){
    }

    @TableId(type = IdType.AUTO)
    private long    id;

    @TableField(insertStrategy = FieldStrategy.NOT_NULL, updateStrategy = FieldStrategy.NOT_NULL)
    private Date    gmtCreate;

    @TableField(insertStrategy = FieldStrategy.NOT_NULL, updateStrategy = FieldStrategy.NOT_NULL)
    private Date    gmtModified;

    private String  username;

    private String  password;

    private Date    lastTryLoginTime;

    private int     loginFailCount;

    private boolean loginLocked;

}
