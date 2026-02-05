package io.cronx.web.model.fo.apisource;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteApiSourceFO {

    @Min(value = 1, message = "{min.apisourceid}")
    private long apiSourceId;
}
