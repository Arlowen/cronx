package io.cronx.web.model.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;

import io.cronx.async.task.framework.constant.AsyncJobStatus;
import io.cronx.web.model.enumeration.LifeCycleState;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.Tolerate;

@Data
@FieldNameConstants
@TableName(value = "async_job")
@Builder
public class AsyncJobDO {

    @Tolerate
    public AsyncJobDO(){
    }

    @TableId(type = IdType.AUTO)
    private long           id;

    @TableField(insertStrategy = FieldStrategy.NOT_NULL, updateStrategy = FieldStrategy.NOT_NULL)
    private Date           gmtCreate;

    @TableField(insertStrategy = FieldStrategy.NOT_NULL, updateStrategy = FieldStrategy.NOT_NULL)
    private Date           gmtModified;

    private String         jobName;

    private String         jobDesc;

    private AsyncJobStatus jobStatus;

    private LifeCycleState lifeCycleState;

    private Date           startTime;

    private Date           finishTime;

    private Date           nextStartTime;

    private Boolean        isTiming;
}
