package io.cronx.toolkit.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryUtil {

    @SneakyThrows
    public static void retry(int retryCount, Consumer retryLogic, Object param, long sleepMs) {
        for (int i = 0; i < retryCount; i++) {
            try {
                retryLogic.accept(param);
                return;
            } catch (Exception e) {
                log.warn("Meet exception. Root cause is " + ExceptionUtils.getRootCauseMessage(e) + ", will retry " + retryCount + ", current retry count is " + i
                         + ". Retry interval is " + sleepMs + " ms");
                Thread.sleep(sleepMs);
            }
        }

        throw new RuntimeException("Retry " + retryCount + ", retry logic still failed, please check....");

    }

    @SneakyThrows
    public static Object retry(int retryCount, Supplier retryLogic, long sleepMs) {
        for (int i = 0; i < retryCount; i++) {
            try {
                return retryLogic.get();
            } catch (Exception e) {
                log.warn("Meet exception. Root cause is " + ExceptionUtils.getRootCauseMessage(e) + ", will retry " + retryCount + ", current retry count is " + i
                         + ". Retry interval is " + sleepMs + " ms");
                Thread.sleep(sleepMs);
            }
        }

        throw new RuntimeException("Retry " + retryCount + ", retry logic still failed, please check....");

    }

}
