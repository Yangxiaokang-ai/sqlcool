package com.github.dakuohao.util.entity;

import cn.hutool.db.Entity;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;

class UserTest {
    User user = new User();

    @Test
    void insert() {
        User user = new User();
        user.setName("张三");
        user.setAge(20);
        Boolean insert = user.insert();
        assert insert;
        System.out.println(JSONUtil.toJsonStr(user));
    }

    @Test
    void insertBySql() {
        User user = new User();
        String sql = "INSERT INTO `user` (`name`, `age`) VALUES (?, ?)";
        Boolean insert = user.insert(sql, "张三", 20);
        assert insert;
    }

    @Test
    void insertEntity() {
        Entity entity = Entity.create("user")
                .set("name", "张三")
                .set("age", 20);
        Boolean insert = user.insert(entity);
        assert insert;
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
