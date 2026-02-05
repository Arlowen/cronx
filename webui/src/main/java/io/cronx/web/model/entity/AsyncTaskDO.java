package io.cronx.web.model.entity;

import java.util.Comparator;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;

import io.cronx.async.task.framework.constant.AsyncTaskStatus;
import io.cronx.web.model.enumeration.LifeCycleState;
import lombok.*;
import lombok.experimental.Tolerate;

@Data
@TableName(value = "async_task")
@Builder
@AllArgsConstructor
public class AsyncTaskDO {

    @Tolerate
    public AsyncTaskDO(){
    }

    @TableId(type = IdType.AUTO)
    private long            id;

    @TableField(insertStrategy = FieldStrategy.NOT_NULL, updateStrategy = FieldStrategy.NOT_NULL)
    private Date            gmtCreate;

    @TableField(insertStrategy = FieldStrategy.NOT_NULL, updateStrategy = FieldStrategy.NOT_NULL)
    private Date            gmtModified;

    private String          taskName;

    private LifeCycleState  lifeCycleState;

    private String          taskDesc;

    private AsyncTaskStatus taskStatus;

    private Long            jobId;

    private Long            execOrder;

    private String          uid;

    private String          errorMsg;

    private String          taskResult;

}
