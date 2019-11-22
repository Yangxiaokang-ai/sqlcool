package com.github.dakuohao;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

class SqlTest {

    @Test
    void test() {

        //多条件查询参数
        // 这个在开发中会由mvc框架反射前端传来的json自动生成
        Entity params = Entity.create()
                .set("name", "张三")
                .set("age", 16)
                .set("createTime1", "2019-11-22 15:11:45")
                .set("createTime2", "2019-11-23 15:11:45");

        List<Entity> list = Sql.create()
                .append("SELECT * FROM `user` WHERE")
                .append(StrUtil.isNotEmpty(params.getStr("name")), "and `name` LIKE " + params.getStr("name") + "%")
                .append(params.getInt("age") > 0, "AND age >${age}")
                .append(StrUtil.isNotEmpty(params.getStr("createTime1")) && StrUtil.isNotEmpty(params.getStr("createTime2")), "AND create_time BETWEEN ${createTime1} AND ${createTime2}")
                .setParams(params)
                .execute();
        System.out.println(JSONUtil.toJsonStr(list));
    }

    @Test
    void testAppend() {
    }

    @Test
    void setParams() {
    }

    @Test
    void execute() {
    }

    @Test
    void executeQuery() {
    }

    @Test
    void testExecuteQuery() {
    }

    @Test
    void executeUpdate() {
    }

    @Test
    void page() {
    }

    @Test
    void testPage() {
    }

    @Test
    void getSql() {
    }

    @Test
    void setSql() {
    }

    @Test
    void getParams() {
    }

    @Test
    void testSetParams() {
    }
}
