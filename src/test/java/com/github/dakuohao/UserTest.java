package com.github.dakuohao;

import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static cn.hutool.db.transaction.TransactionLevel.READ_UNCOMMITTED;

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
    void executeUpdateBatch1() {
        String sql = "INSERT INTO `user` (`name`, `age`) VALUES (?, ?)";
        int[] update = Sql.sql().executeUpdateBatch(sql, new Object[]{"张三1", 19}, new Object[]{"张三2", 45});
        assert update != null;
        //[SQL] : INSERT INTO `user` (`name`, `age`) VALUES (?, ?)

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
        Boolean insert = Sql.sql().insertBatch(list);
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

        Boolean insert = Sql.sql().insertBatch(list);
        assert insert;
        //[Batch SQL] -> INSERT INTO `user` (`name`, `age`) VALUES (?, ?)

        System.out.println(JSONUtil.toJsonStr(list));
        //[{"name":"张三001","age":20},{"name":"张三002","age":22}]
    }

    @Test
    void insertBatch21() {
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

        Boolean insert = Sql.sql().insert(list);
        assert insert;
        //[SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三001', 20)
        //[SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三002', 22)

        System.out.println(JSONUtil.toJsonStr(list));
        //[{"name":"张三001","age":20},{"name":"张三002","age":22}]
    }

    @Test
    void insertBatch22() {
        //模拟数据
        Entity e1 = Entity.create("user")
                .set("name", "张三001")
                .set("age", 20);

        Entity e2 = new Entity("user")
                .set("name", "张三002")
                .set("age", 22);

        List<Entity> list = new ArrayList<>();
        list.add(e1);
        list.add(e2);

        Boolean insert = Sql.sql().insert(list);
        assert insert;
        //[SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三001', 20)
        //[SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三002', 22)

        System.out.println(JSONUtil.toJsonStr(list));
        //[{"name":"张三001","id":55,"age":20},{"name":"张三002","id":56,"age":22}]
    }


    @Test
    void delete() {
        String sql = "DELETE FROM `user` WHERE id = ?";
        Boolean delete = Sql.sql().delete(sql, 1);
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
        Boolean delete = user.deleteById();
        //[执行SQL] : DELETE FROM user WHERE id =1
        System.out.println(delete);
    }

    @Test
    void deleteByIds() {
        Boolean delete = Sql.sql().deleteByIds("user", 1, 2, 3);
        //[SQL] : DELETE FROM user WHERE id =?
        System.out.println(delete);
    }


    @Test
    void deleteByIds1() {
        Boolean delete = new User().deleteByIds(4, 5, 6, 7);
        //[SQL] : DELETE FROM user WHERE id =?
        System.out.println(delete);
    }


    @Test
    void update() {
        String sql = "UPDATE `user` SET `name` = '测试' WHERE id = ?";
        Boolean update = Sql.sql().update(sql, 11);
        assert update;
        //[SQL] : UPDATE `user` SET `name` = '测试' WHERE id = 11
    }

    @Test
    void updateById() {
        User user = new User();
        user.setId(11);
        user.setName("测试");
        Boolean update = user.updateById();
        assert update;
        //[SQL] : UPDATE `user` SET `name` = '测试'  WHERE `id` = 11
    }

    @Test
    void updateById1() {
        Entity entity = Entity.create("user")
                .set("id", 11)
                .set("name", "测试");

        Boolean update = Sql.sql().updateById(entity);
        assert update;
        //[SQL] : UPDATE `user` SET `name` = '测试'  WHERE `id` = 11
    }

    @Test
    void updateBatch() {
        User u1 = new User();
        u1.setId(11);
        u1.setName("测试");

        User u2 = new User();
        u2.setId(12);
        u2.setName("测试");

        List<User> list = new ArrayList<>();
        list.add(u1);
        list.add(u2);

        Boolean update = Sql.sql().update(list);
        //[SQL] : UPDATE `user` SET `name` = '测试'  WHERE `id` = 11
        //[SQL] : UPDATE `user` SET `name` = '测试'  WHERE `id` = 12
    }

    @Test
    void updateBatch1() {
        Entity e1 = Entity.create("user")
                .set("id", 11)
                .set("name", "测试");

        Entity e2 = Entity.create("user")
                .set("id", 12)
                .set("name", "测试");

        List<Entity> list = new ArrayList<>();
        list.add(e1);
        list.add(e2);

        Boolean update = Sql.sql().update(list);
        //[SQL] : UPDATE `user` SET `name` = '测试'  WHERE `id` = 11
        //[SQL] : UPDATE `user` SET `name` = '测试'  WHERE `id` = 12
    }


    @Test
    void insetOrUpdate() {
        Entity entity = Entity.create("user")
                .set("id", 203)
                .set("name", "测试001")
                .set("age", 45);
        Boolean result = Sql.sql().insertOrUpdate(entity);
        assert result;
        System.out.println(JSONUtil.toJsonStr(entity));
        //第一次执行 不存在则执行插入
        //[SQL] : SELECT COUNT(*) FROM user WHERE id =86
        //[SQL] : INSERT INTO `user` (`id`, `name`, `age`) VALUES (86, '测试001', 45)
        //{"id":203,"name":"测试001","age":45}

        //第二次执行 存在则修改
        //[SQL] : SELECT COUNT(*) FROM user WHERE id =86
        //[SQL] : UPDATE `user` SET `name` = '测试001' , `age` = 45  WHERE `id` = 86
        //{"name":"测试001","age":45}

    }

    @Test
    void insetOrUpdate1() {
        Entity entity = Entity.create("user")
                .set("name", "测试001")
                .set("age", 45);
        Boolean result = Sql.sql().insertOrUpdate(entity);
        assert result;
        //[SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('测试001', 45)

        System.out.println(JSONUtil.toJsonStr(entity));
        //{"name":"测试001","age":45,"id":89}
    }

    @Test
    void insetOrUpdate2() {
        User user = new User();
        user.setId(100);
        user.setName("测试");
        user.setAge(56);
        Boolean result = user.insertOrUpdate();
        assert result;
        System.out.println(JSONUtil.toJsonStr(user));
        //第一次执行 不存在则执行插入
        //[SQL] : SELECT COUNT(*) FROM `user` WHERE id =100
        //[SQL] : INSERT INTO `user` (`id`, `name`, `age`) VALUES (100, '测试', 56)

        //第二次执行 存在则修改
        //[SQL] : SELECT COUNT(*) FROM `user` WHERE id =100
        //[SQL] : UPDATE `user` SET `name` = '测试' , `age` = 56  WHERE `id` = 100
        //{"name":"测试","id":100,"age":56}
    }

    @Test
    void selectEntity() {
        String sql = "select * from user where id >?";
        List<Entity> list = Sql.sql().select(sql, 30);
        System.out.println(JSONUtil.toJsonStr(list));
        //[SQL] : select * from user where id >30
    }

    @Test
    void select() {
        String sql = "select * from user where id >?";
        List<Entity> list = new User().select(sql, 30);
        System.out.println(JSONUtil.toJsonStr(list));
        //[SQL] : select * from user where id >30
    }

    @Test
    void selectOneEntity() {
        String sql = "select * from user where id =?";
        List<Entity> list = Sql.sql().select(sql, 30);
        System.out.println(JSONUtil.toJsonStr(list));
        //[SQL] : select * from user where id =30
    }

    @Test
    void selectOne() {
        String sql = "select * from user where id =?";
        List<Entity> list = new User().select(sql, 30);
        System.out.println(JSONUtil.toJsonStr(list));
        //[SQL] : select * from user where id =30
    }

    @Test
    void selectByIdEntity() {
        Entity entity = Sql.sql().selectById("user", 30);
        System.out.println(JSONUtil.toJsonStr(entity));
        //[SQL] : SELECT * FROM user WHERE `id`=30
    }

    @Test
    void selectById() {
        User user = new User().selectById(30);
        System.out.println(JSONUtil.toJsonStr(user));
        //[SQL] : SELECT * FROM user WHERE `id`=30
    }


    @Test
    void transaction() {
        Sql.sql().transaction(parameter -> {
            User user = new User();
            user.setId(100);
            user.setName("测试xxxxxxxxx");
            user.updateById();
//            throw new RuntimeException("测试事务异常");
            //[SQL] : UPDATE `user` SET `name` = '测试xxxxxxxxx'  WHERE `id` = 100
            //java.sql.SQLException: java.lang.RuntimeException: 测试事务异常
        });
    }

    //等同于上边写法 适合jdk8一下使用
    @Test
    void transaction1() {
        Sql.sql().transaction(
                new VoidFunc1<Db>() {
                    @Override
                    public void call(Db parameter) throws Exception {
                        User user = new User();
                        user.setId(100);
                        user.setName("测试xxxxxxxxx");
                        user.updateById();
//                        throw new RuntimeException("测试事务异常");
                        //[SQL] : UPDATE `user` SET `name` = '测试xxxxxxxxx'  WHERE `id` = 100
                        //java.sql.SQLException: java.lang.RuntimeException: 测试事务异常
                    }
                });
    }


    //指定事务级别
    @Test
    void transactionLevel() {
        Sql.sql().transaction(READ_UNCOMMITTED, parameter -> {
            User user = new User();
            user.setId(100);
            user.setName("测试xxxxxxxxx");
            user.updateById();
//            throw new RuntimeException("测试事务异常");
            //[SQL] : UPDATE `user` SET `name` = '测试xxxxxxxxx'  WHERE `id` = 100
            //java.sql.SQLException: java.lang.RuntimeException: 测试事务异常
        });
    }

}
