package com.cyt.simplemvc.annotation;

import java.lang.annotation.*;

/**
 * 自定义依赖注入注解
 *
 * @author CaoYangTao
 * @date 2018/11/13  20:35
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAutowired {
    String value() default "";
}
