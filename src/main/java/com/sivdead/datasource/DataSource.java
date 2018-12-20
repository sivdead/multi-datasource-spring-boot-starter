package com.sivdead.datasource;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 动态数据源注解
 *
 * @author 敬文
 * @since 0.1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DataSource {

    @AliasFor("source")
    String value() default "";

    @AliasFor("value")
    String source() default "";
}
