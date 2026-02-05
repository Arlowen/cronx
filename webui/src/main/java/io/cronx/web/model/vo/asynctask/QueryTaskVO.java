package io.cronx.web.model.vo.asynctask;

import java.util.List;

import lombok.Data;

@Data
public class QueryTaskVO {

    private List<AsyncTaskVO> asyncTaskVOS;

    private long              totalCount;

    public QueryTaskVO(List<AsyncTaskVO> asyncTaskVOS, long totalCount){
        this.asyncTaskVOS = asyncTaskVOS;
        this.totalCount = totalCount;
    }
}
