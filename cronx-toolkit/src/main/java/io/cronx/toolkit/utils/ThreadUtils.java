/*
 * Copyright 2008-2009 the original author or authors. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package io.cronx.toolkit.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version : 2014年7月8日
 */
public class ThreadUtils {

    private static final AtomicInteger globalCnt = new AtomicInteger(0);

    /**
     * 挂起当前线程
     *
     * @param timeout 时长
     * @param timeUnit 时长单位
     * @return 中断返回 false，否则true
     */
    public static boolean sleep(Number timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout.longValue());
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    /**
     * 挂起当前线程
     *
     * @param millis 时长
     * @return 中断返回 false，否则true
     */
    public static boolean sleep(Number millis) {
        if (millis == null || millis.longValue() == 0) {
            return true;
        }

        try {
            Thread.sleep(millis.longValue());
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    /**
     * 考虑{@link Thread#sleep(long)}方法有可能时间不足给定毫秒数，此方法保证sleep时间不小于给定的毫秒数
     *
     * @param millis 时长
     * @return 中断返回 false，否则true
     */
    public static boolean safeSleep(Number millis) {
        long millisLong = millis.longValue();
        long done = 0;
        while (done < millisLong) {
            long before = System.currentTimeMillis();
            if (!sleep(millisLong - done)) {
                return false;
            }
            long after = System.currentTimeMillis();
            done += (after - before);
        }
        return true;
    }

    public static Thread frontThread(final ClassLoader loader, Runnable runnable) {
        Thread t = new Thread(runnable);
        t.setContextClassLoader(loader);
        t.setName(String.format("Thread-%s", globalCnt.incrementAndGet()));
        t.setDaemon(false);
        return t;
    }

    public static Thread daemonThread(final ClassLoader loader, Runnable runnable) {
        Thread t = new Thread(runnable);
        t.setContextClassLoader(loader);
        t.setName(String.format("Thread-%s", globalCnt.incrementAndGet()));
        t.setDaemon(true);
        return t;
    }
}
