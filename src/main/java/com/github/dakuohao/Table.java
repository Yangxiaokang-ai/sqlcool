package com.github.dakuohao;

import java.lang.annotation.*;

/**
 * 标注数据库表信息
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    //表名
    String value() default "";

    //数据源名称
    String dataSource() default "";
}
