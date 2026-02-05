package io.cronx.web.model.fo.asyncjob;

import java.util.List;

import io.cronx.web.model.fo.asynctask.AddTaskWithJobFO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
public class AddJobFO {

    private String                 jobDesc;

    @Valid
    private List<AddTaskWithJobFO> taskFOList;

}
