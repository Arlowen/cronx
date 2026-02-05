package io.cronx.async.task.framework.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *  If annotated, task status will auto do following things:
 *  - Auto state manage
 *  - Auto start/finish time record
 *  - Auto error message record
 *  - auto context record
 * </pre>
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskAutoProcess {

}
