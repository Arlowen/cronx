package io.cronx.web.model.fo.asynctask;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryTaskFO {

    private long jobId;

    private long pageNumber;

    private long pageSize;
}
