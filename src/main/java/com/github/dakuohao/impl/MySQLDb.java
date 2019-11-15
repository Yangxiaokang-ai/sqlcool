package com.github.dakuohao.impl;

import com.github.dakuohao.Db;

import javax.sql.DataSource;
import javax.sql.PooledConnection;

/**
 * Db对于mysql数据库的具体实现
 */
public class MySQLDb implements Db {
    public Db createDb() {
        return null;
    }

    public Db createDb(DataSource dataSource) {
        return null;
    }

    public PooledConnection getPooledConnection() {
        return null;
    }
}
