package com.cyt.simplemvc.annotation;

import java.lang.annotation.*;

/**
 * 自定义mvc请求参数注解
 *
 * @author CaoYangTao
 * @date 2018/11/13  20:35
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {
    String value() default "";
}
