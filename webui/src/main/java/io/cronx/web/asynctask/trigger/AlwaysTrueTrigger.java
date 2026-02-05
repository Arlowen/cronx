package io.cronx.web.asynctask.trigger;

import java.util.function.BooleanSupplier;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.extern.slf4j.Slf4j;

/**
 * If you do not need condition trigger,just use this
 **/
@Slf4j
public class AlwaysTrueTrigger implements BooleanSupplier {

    @JsonIgnore
    @Override
    public boolean getAsBoolean() {
        log.info("【TRIGGER】Use always true trigger ....");
        return true;
    }
}
