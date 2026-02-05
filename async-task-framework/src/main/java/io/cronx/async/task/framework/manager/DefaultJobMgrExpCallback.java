package io.cronx.async.task.framework.manager;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultJobMgrExpCallback implements Consumer<Throwable> {

    @Override
    public void accept(Throwable throwable) {
        log.error("Execute job mgr exp callback.......", throwable);
    }
}
