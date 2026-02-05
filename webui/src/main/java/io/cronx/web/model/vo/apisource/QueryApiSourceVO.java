package io.cronx.web.model.vo.apisource;

import java.util.List;

import lombok.Data;

@Data
public class QueryApiSourceVO {

    private List<ApiSourceVO> apiSourceVOS;

    private long              totalCount;

    public QueryApiSourceVO(List<ApiSourceVO> ApiSourceVO, long totalCount){
        this.apiSourceVOS = ApiSourceVO;
        this.totalCount = totalCount;
    }
}
