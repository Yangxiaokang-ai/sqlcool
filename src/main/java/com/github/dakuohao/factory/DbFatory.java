package com.github.dakuohao.factory;

import cn.hutool.db.Db;
import cn.hutool.db.ds.DSFactory;

import javax.sql.DataSource;

/**
 * DataBase工厂构建方法
 * <p>
 * 配置此类的目的：
 * 平时开发，添加一个db.properties文件即可
 * 跟SpringBoot结合时，使用spring的DataSource，让spring接管
 */
public class DbFatory {
    private static DataSource dataSource = null;

    public static Db get() {
        return Db.use(getDataSource());
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = DSFactory.get();
        }
        return dataSource;
    }

    public static void setDataSource(DataSource dataSource) {
        DbFatory.dataSource = dataSource;
    }
}
