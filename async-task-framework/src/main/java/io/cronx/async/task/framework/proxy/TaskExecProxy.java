package io.cronx.async.task.framework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;

import io.cronx.async.task.framework.alert.AlarmLevel;
import io.cronx.async.task.framework.constant.AsyncTaskStatus;
import io.cronx.async.task.framework.model.AsyncTaskBase;
import io.cronx.async.task.framework.model.annotation.TaskAutoProcess;
import io.cronx.toolkit.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskExecProxy implements InvocationHandler {

    private final AsyncTaskBase target;

    public TaskExecProxy(AsyncTaskBase target){
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getAnnotation(TaskAutoProcess.class) != null) {
            // fill status
            Method setStatusMethod = target.getClass().getMethod("setTaskStatus", AsyncTaskStatus.class);
            setStatusMethod.invoke(target, AsyncTaskStatus.RUNNING);

            // fill start time
            Method setExecTime = target.getClass().getMethod("setExecTime", Date.class);
            setExecTime.invoke(target, new Date());

            Object result = null;
            try {
                result = method.invoke(target, args);

                // fill finish time
                Method setFinishTime = target.getClass().getMethod("setFinishTime", Date.class);
                setFinishTime.invoke(target, new Date());

            } catch (Throwable t) {
                Method setErrMsgMethod = target.getClass().getMethod("setErrMsg", String.class);
                String errMsg = "Dynamic invoke task execute failed with exception. Root cause is " + ExceptionUtils.getRootCause(t);
                log.error(errMsg, t);
                setErrMsgMethod.invoke(target, errMsg);
                setStatusMethod.invoke(target, AsyncTaskStatus.ABNORMAL);
                if (target.getAlertService() != null) {
                    target.getAlertService().sendMsg(errMsg, AlarmLevel.ERROR);
                }
                throw t;
            }
            // not set finish here, set finish after trigger finished...
            return result;
        } else {
            return method.invoke(target, args);
        }
    }

    public AsyncTaskBase createProxyObj() {
        ClassLoader classLoader = target.getClass().getClassLoader();
        Class<?>[] interfaces = target.getClass().getInterfaces();
        Object newProxyInstance = Proxy.newProxyInstance(classLoader, interfaces, this);
        return (AsyncTaskBase) newProxyInstance;
    }
}
