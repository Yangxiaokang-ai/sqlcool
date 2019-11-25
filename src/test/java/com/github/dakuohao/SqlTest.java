package com.github.dakuohao;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

class SqlTest {


    @Test
    void execute() {
        //直接执行sql语句
        String sql = "INSERT INTO `test`.`user` (`name`, `age`) VALUES ('张三', '18')";
        int result = Sql.sql(sql).execute();
        assert result == 1;
    }

    @Test
    void execute1() {
        User user = new User();
        user.setName("张三");
        user.setAge(18);

        //执行带参数的sql
        String sql = "INSERT INTO `test`.`user` (`name`, `age`) VALUES (${name},${age})";
        int result = Sql.sql(sql).setParams(user).execute();
        assert result == 1;
    }


    @Test
    void execute2() {
        Entity user = Entity.create()
                .set("name", "张三")
                .set("age", 16);

        //执行带参数的sql
        String sql = "INSERT INTO `test`.`user` (`name`, `age`) VALUES (${name},${age})";
        int result = Sql.sql(sql).setParams(user).execute();
        assert result == 1;
    }

    @Test
    void executeQuery1() {

        //多条件查询参数
        // 这个在开发中会由mvc框架反射前端传来的json自动生成
        User user = new User();
        user.setName("张三" + "%");
        user.setAge(18);

        List<User> list = Sql.sql("SELECT * FROM `user` WHERE `name` LIKE #{name} AND age >${age}")
                .setParams(user)
                .execute(); // execute方法会智能判断是查询sql还是修改sql
        //当然你也可以手动指定 调用查询sql 如下方式
        //.executeQuery();

        //[SQL] : SELECT * FROM `user`  WHERE  `name` LIKE '张三%' AND age >18
        System.out.println(JSONUtil.toJsonStr(list));
    }

    @Test
    void executeQuery11() {

        //多条件查询参数
        // 这个在开发中会由mvc框架反射前端传来的json自动生成
        Entity params = Entity.create()
                .set("name", "张三" + "%") //%表示模糊查询
                .set("age", 16);

        List<Entity> list = Sql.sql("SELECT * FROM `user` WHERE `name` LIKE #{name} AND age >${age}")
                .setParams(params)
                .execute(); // execute方法会智能判断是查询sql还是修改sql
        //当然你也可以手动指定 调用查询sql 如下方式
        //.executeQuery();

        //[SQL] : SELECT * FROM `user`  WHERE  `name` LIKE '张三%' AND age >16

        System.out.println(JSONUtil.toJsonStr(list));
    }

    @Test
    void executeQuery() {

        //多条件查询参数
        // 这个在开发中会由mvc框架反射前端传来的json自动生成
        Entity params = Entity.create()
                .set("name", "张三" + "%") //%表示模糊查询
                .set("age", 16)
                //时间格式支持LocalDateTime  JDK8以上推荐使用这个
                .set("createTime1", LocalDateTime.parse("2019-11-22T15:11:45"))
                .set("createTime2", LocalDateTime.parse("2019-12-23T20:30:29"));

        //时间格式支持字符串
//                .set("createTime1", "2019-11-22T15:11:45")
//                .set("createTime2","2019-11-22T15:11:45");

        //时间格式支持Date对象
//                .set("createTime1", new Date())
//                .set("createTime2", new Date());

        List<Entity> list = Sql.sql("SELECT * FROM `user` WHERE")
                .append(StrUtil.isNotEmpty(params.getStr("name")), "and `name` LIKE #{name}")
                .append(params.getInt("age") > 0, "AND age >${age}")
                .append(StrUtil.isNotEmpty(params.getStr("createTime1"))
                                && StrUtil.isNotEmpty(params.getStr("createTime2")),
                        "AND create_time BETWEEN ${createTime1} AND ${createTime2}")
                .setParams(params)
                .execute(); // execute方法会智能判断是查询sql还是修改sql
        //当然你也可以手动指定 调用查询sql 如下方式
//                .executeQuery();

        System.out.println(JSONUtil.toJsonStr(list));
    }

    @Test
    void page() {
        // 查询参数,这个在开发中会由mvc框架反射前端传来的json自动生成
        Entity params = Entity.create()
                .set("name", "张三" + "%") //%表示模糊查询
                .set("age", 16);
        //分页参数 第一页，页面数据10条 排序字段： order by name asc,age desc
        Page page = new Page(1, 10, "name asc,age desc");

        Page result = Sql.sql("SELECT * FROM `user` WHERE")
                .append(StrUtil.isNotEmpty(params.getStr("name")), "and `name` LIKE #{name}")
                .append(params.getInt("age") > 0, "AND age >${age}")
                .append(StrUtil.isNotEmpty(params.getStr("createTime1"))
                                && StrUtil.isNotEmpty(params.getStr("createTime2")),
                        "AND create_time BETWEEN ${createTime1} AND ${createTime2}")
                .setParams(params)
//                .page(page);
                //或者直接传分页参数 等同于 page(page)方法
                .page(1, 10, "name asc,age desc");

        // [SQL] : SELECT COUNT(*) FROM (SELECT * FROM `user`  WHERE   `name` LIKE '张三%' AND age >16 ) temp
        //[SQL] : SELECT * FROM `user`  WHERE   `name` LIKE '张三%' AND age >16 ORDER BY name asc,age desc LIMIT 0,10

        System.out.println(JSONUtil.toJsonStr(result));
    }


}
