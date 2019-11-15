package com.github.dakuohao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.github.dakuohao.util.DbUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) throws SQLException {
        DruidDataSource ds = new DruidDataSource();
        DruidPooledConnection connection = ds.getConnection();

        DbUtil.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("");

    }

}
