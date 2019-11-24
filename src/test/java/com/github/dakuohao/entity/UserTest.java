package com.github.dakuohao.entity;

import cn.hutool.db.Entity;
import cn.hutool.json.JSONUtil;
import com.github.dakuohao.Sql;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void insertBySql() {
        User user = new User();
        String sql = "INSERT INTO `user` (`name`, `age`) VALUES (?, ?)";
        Boolean insert = user.insert(sql, "张三", 20);
        assert insert;
    }

    @Test
    void insertBySql1() {
        User user = new User();
        String sql = "INSERT INTO `user` (`name`, `age`) VALUES ('张三', 20)";
        Boolean insert = user.insert(sql);
        assert insert;
    }

    @Test
    void insert() {
        //模拟数据
        User user = new User();
        user.setName("张三");
        user.setAge(20);

        Boolean insert = user.insert();
        assert insert;
        //数据库设置自增主键的，插入成功会自动把id字段设置为主键值
        //[执行SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三', 20)

        System.out.println(JSONUtil.toJsonStr(user));
        //{"name":"张三","id":27,"age":20}
    }

    @Test
    void insertEntity() {
        //模拟数据
        Entity entity = Entity.create("user")
                .set("name", "张三")
                .set("age", 20);

        Boolean insert = Sql.sql().insert(entity);
        assert insert;
        //数据库设置自增主键的，插入成功会自动把id字段设置为主键值
        //[执行SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三', 20)

        System.out.println(entity);
        //{tableName=`user`, fieldNames=null, fields={name=张三, age=20, id=28}}
    }



    @Test
    void getCreateTime() {
    }

    @Test
    void getUpdateTime() {
    }

    @Test
    void getDeleted() {
    }

    @Test
    void setId() {
    }

    @Test
    void setName() {
    }

    @Test
    void setAge() {
    }

    @Test
    void setCreateTime() {
    }

    @Test
    void setUpdateTime() {
    }

    @Test
    void setDeleted() {
    }

    @Test
    void testEquals() {
    }

    @Test
    void canEqual() {
    }

    @Test
    void testHashCode() {
    }

    @Test
    void testToString() {
    }
}
