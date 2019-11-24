package com.github.dakuohao.entity;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.json.JSONUtil;
import com.github.dakuohao.Sql;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class UserTest {

    @Test
    void executeQuery() {
        String sql = "SELECT * FROM `user`  LIMIT 0,10";
        List<Entity> list = Sql.sql().executeQuery(sql);
        assert list != null;
        //[执行SQL] : SELECT * FROM `user`  LIMIT 0,10
        System.out.println(JSONUtil.toJsonStr(list));
    }

    @Test
    void executeQuery1() {
        String sql = "SELECT * FROM `user`  WHERE  `name` LIKE ?  LIMIT ?,?";
        List<Entity> list = Sql.sql().executeQuery(sql, "张三%", 1, 10);
        assert list != null;
        //[执行SQL] : SELECT * FROM `user`  WHERE  `name` LIKE '张三%'  LIMIT 1,10
        System.out.println(JSONUtil.toJsonStr(list));
    }

    @Test
    void executeUpdate() {
        String sql = "INSERT INTO `user` (`name`, `age`) VALUES (?, ?)";
        int update = Sql.sql().executeUpdate(sql, "张三", 20);
        assert update > 0;
        //[执行SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三', 20)
        System.out.println(update);
    }

    @Test
    void executeUpdate1() {
        String sql = "INSERT INTO `user` (`name`, `age`) VALUES ('张三', 20)";
        int update = Sql.sql().executeUpdate(sql);
        assert update > 0;
        //[执行SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三', 20)
        System.out.println(update);
    }


    @Test
    void executeUpdateBatch() {
        String sql1 = "INSERT INTO `user` (`name`, `age`) VALUES ('张三1', 20)";
        String sql2 = "INSERT INTO `user` (`name`, `age`) VALUES ('张三2', 20)";
        int[] update = Sql.sql().executeUpdateBatch(sql1, sql2);
        assert update != null;
        //[Batch SQL] -> INSERT INTO `user` (`name`, `age`) VALUES ('张三1', 20);
        //INSERT INTO `user` (`name`, `age`) VALUES ('张三2', 20);

        System.out.println(ArrayUtil.toString(update));
        //[1, 1]
    }

    @Test
    void insertBySql() {
        String sql = "INSERT INTO `user` (`name`, `age`) VALUES (?, ?)";
        Boolean insert = Sql.sql().insert(sql, "张三", 20);
        assert insert;
        //[执行SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三', 20)
    }

    @Test
    void insertBySql1() {
        String sql = "INSERT INTO `user` (`name`, `age`) VALUES ('张三', 20)";
        Boolean insert = Sql.sql().insert(sql);
        assert insert;
        //[执行SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三', 20)
    }

    @Test
    void insertForGeneratedKey() {
        String sql = "INSERT INTO `user` (`name`, `age`) VALUES ('张三', 20)";
        long insert = Sql.sql().insertForGeneratedKey(sql);
        assert insert > 0;
        //[执行SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三', 20)
        System.out.println(insert);
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

        System.out.println(JSONUtil.toJsonStr(entity));
        //{"name":"张三","age":20,"id":29}
    }


    @Test
    void insertBatch() {
        //模拟数据
        User u1 = new User();
        u1.setName("张三001");
        u1.setAge(20);

        User u2 = new User();
        u2.setName("张三002");
        u2.setAge(22);

        List<User> list = new ArrayList<>();
        list.add(u1);
        list.add(u2);

//        Boolean insert = Sql.sql().insertBatch(list);
        Boolean insert = Sql.sql().insert(list);
        assert insert;
        //[Batch SQL] -> INSERT INTO `user` (`name`, `age`) VALUES (?, ?)

        System.out.println(JSONUtil.toJsonStr(list));
        //[{"name":"张三001","age":20},{"name":"张三002","age":22}]
    }

    @Test
    void insertBatch1() {
        //模拟数据
        Entity e1 = Entity.create("user")
                .set("name", "张三001")
                .set("age", 20);

        Entity e2 = new Entity("user")
                .set("name", "张三002")
                .set("age", 22);

        List<Entity> list = new ArrayList<>();
        list.add(e1);
//        list.add(e2);

        Boolean insert = Sql.sql().insert(list);
        assert insert;
        //[Batch SQL] -> INSERT INTO `user` (`name`, `age`) VALUES (?, ?)

        System.out.println(JSONUtil.toJsonStr(list));
        //[{"name":"张三001","age":20},{"name":"张三002","age":22}]
    }


    @Test
    void delete() {
        String sql = "DELETE FROM `user` WHERE id = ?";
        Boolean delete = Sql.sql().delete(sql,1);
        System.out.println(delete);
    }

    @Test
    void deleteById() {
        Boolean delete = Sql.sql().deleteById("user", 2);
        //[执行SQL] : DELETE FROM user WHERE id =2
        System.out.println(delete);
    }

    @Test
    void deleteById1() {
        User user = new User();
        user.setId(1);
        user.deleteById();
        //[执行SQL] : DELETE FROM user WHERE id =1
    }

    @Test
    void deleteBatch() {
        //批量删除
        //where条件删除
        Db.use().del()
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
