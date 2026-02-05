package io.cronx.web.model.vo.asyncjob;

import java.util.List;

import lombok.Data;

@Data
public class QueryJobVO {

    private List<AsyncJobVO> asyncJobVOS;

    private long             totalCount;

    public QueryJobVO(List<AsyncJobVO> asyncJobVOS, long totalCount){
        this.asyncJobVOS = asyncJobVOS;
        this.totalCount = totalCount;
    }
}
