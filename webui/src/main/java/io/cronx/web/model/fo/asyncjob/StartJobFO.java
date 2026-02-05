package io.cronx.web.model.fo.asyncjob;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
public class StartJobFO {

    @Min(value = 1, message = "{min.jobid}")
    private long jobId;

}
