package com.dynamicdb.aop;

import java.lang.annotation.*;

/**
 * @Author whh
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyDataSource {
    String name();
}
