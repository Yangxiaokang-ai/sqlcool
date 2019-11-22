package com.github.dakuohao.util.entity;

import com.github.dakuohao.DataBase;
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
@Data
public class User implements DataBase {
    private Integer id;
    private String name;
    private Integer age;
    private LocalDate createTime;
    private LocalDate createTime1;
    private LocalDate createTime2;
    private LocalDate updateTime;
    private Boolean deleted;
}
