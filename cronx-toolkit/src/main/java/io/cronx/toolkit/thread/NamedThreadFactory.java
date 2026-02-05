package io.cronx.toolkit.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cronx.toolkit.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NamedThreadFactory implements ThreadFactory {

    private static final Logger                   logger       = LoggerFactory.getLogger(NamedThreadFactory.class);
    private static final AtomicInteger            poolNumber   = new AtomicInteger();
    private final AtomicInteger                   threadNumber = new AtomicInteger();
    private final ThreadGroup                     group;
    private final String                          namePrefix;
    private final boolean                         isDaemon;
    private final Thread.UncaughtExceptionHandler handler      = (t, e) -> {
                                                                   String errMsg = "Thread execute failed with exception. Root cause is " + ExceptionUtils.getRootCauseMessage(e);
                                                                   logger.error(errMsg, e);
                                                               };

    public NamedThreadFactory(){
        this("pool");
    }

    public NamedThreadFactory(String prefix){
        this(prefix, false);
    }

    public NamedThreadFactory(String prefix, boolean daemon){
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = prefix + "-" + poolNumber.getAndIncrement() + "-thread-";
        isDaemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        t.setDaemon(isDaemon);
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }

        t.setUncaughtExceptionHandler(handler);
        return t;
    }
}
