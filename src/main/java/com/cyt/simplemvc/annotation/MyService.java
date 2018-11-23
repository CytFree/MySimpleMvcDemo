package com.cyt.simplemvc.annotation;

import java.lang.annotation.*;

/**
 * 自定义Bean注解
 *
 * @author CaoYangTao
 * @date 2018/11/13  20:35
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyService {
    String value() default "";
}
