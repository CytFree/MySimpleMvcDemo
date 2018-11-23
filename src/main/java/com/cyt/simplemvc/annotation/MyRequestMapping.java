package com.cyt.simplemvc.annotation;

import java.lang.annotation.*;

/**
 * 自定义请求URL注解
 *
 * @author CaoYangTao
 * @date 2018/11/13  20:35
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface MyRequestMapping {
    String value() default "";
}
