package io.cronx.web.model.fo.asyncjob;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteJobFO {

    @Min(value = 1, message = "{min.jobid}")
    private long jobId;

}
