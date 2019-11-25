package com.github.dakuohao;

import lombok.Data;

import java.time.LocalDate;

/**
 * 用户
 * 用作模拟测试
 *
 * @author Peng 1029538990@qq.com
 * @version 1.0
 * @date 2019/11/20 16:54
 */
//lombok插件的注解，帮你生成get，set等方法，推荐使用
@Data
//类名对应数据库表名，表示类和表绑定
//类命名方式使用驼峰命名，表命名使用下划线命名，工具类会自动把驼峰命名转下划线命名，这里注意。
// 如果数据库表名和类名不一致，使用注解来标识：@Table(value = "表名称",dataSource = "数据源名称")，
//@Table("user")
public class User implements ORM {
    private Integer id;
    private String name;
    private Integer age;
    private LocalDate createTime;
    private LocalDate createTime1;
    private LocalDate createTime2;
    private LocalDate updateTime;
    private Boolean deleted;
}
