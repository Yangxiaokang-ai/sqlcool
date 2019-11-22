package com.github.dakuohao;

import com.github.dakuohao.bean.Entity;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

class MySqlDataBaseTest {
    private DataBase dataBase = new MySqlDb();

    @Test
    void executeQuery() throws SQLException {
        String sql = "select * from user where id =?";
        ResultSet resultSet = dataBase.executeQuery(sql, 1);
    }

    @Test
    void executeUpdate() {
        String sql = "INSERT INTO `test`.`user` (`name`, `age`) VALUES (?, ?);";
        int update = dataBase.executeUpdate(sql, "测试002", 19);
        assert update > 0;
    }

    @Test
    void execute() {
        String sql = "DROP TABLE user_copy";
        Boolean result = dataBase.execute(sql);
        assert !result;
    }

    @Test
    void insert() {
        String sql = "INSERT INTO `test`.`user` (`name`, `age`) VALUES (?, ?);";
        Boolean insert = dataBase.insert(sql, "测试xxx", 190);
        assert insert;
    }

    @Test
    void insertEntity() {
        Entity entity = new Entity("user");
        entity.set("name", "张三001");
        entity.set("age", 68);
        Boolean insert = dataBase.insert(entity);
        assert insert;
    }

    @Test
    void delete() {
    }

    @Test
    void update() {
    }

    @Test
    void select() {
    }

    @Test
    void selectOne() {
    }

    @Test
    void selectById() {
    }

    @Test
    void count() {
    }

    @Test
    void page() {
    }

    @Test
    void testPage() {
    }

    @Test
    void createDb() {
    }

    @Test
    void testCreateDb() {
    }

    @Test
    void getPooledConnection() {
    }
}
