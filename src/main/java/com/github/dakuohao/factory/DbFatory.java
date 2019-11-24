package com.github.dakuohao.factory;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.ds.DSFactory;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DataBase工厂构建方法
 * <p>
 * 配置此类的目的：
 * 平时开发，添加一个db.properties文件即可
 * 跟SpringBoot结合时，使用spring的DataSource，让spring接管
 */
public class DbFatory {
    /**
     * 默认数据源名称
     */
    private static final String DEFAULT_DATA_SOURCE_NAME = StrUtil.EMPTY;

    /**
     * 多数据源使用
     * 线程安全
     */
    private static Map<String, DataSource> dsmap = new ConcurrentHashMap<>();

    public static Db get() {
        return Db.use(getDataSource());
    }
    public static Db get(String dsName) {
        return Db.use(getDataSource(dsName));
    }
    /**
     * 获取数据源
     *
     * @return DataSource
     */
    public static DataSource getDataSource() {
        return getDataSource(DEFAULT_DATA_SOURCE_NAME);
    }

    /**
     * 获取指定的数据源
     *
     * @param dataSourceName 数据源名称
     * @return DataSource
     */
    private static DataSource getDataSource(String dataSourceName) {
        if (dsmap.get(dataSourceName) == null) {
            dsmap.put(dataSourceName, getDS(dataSourceName));
        }
        return dsmap.get(dataSourceName);
    }


    /**
     * 设置数据源
     *
     * @param dataSource 数据源
     */
    public static void setDataSource(DataSource dataSource) {
        dsmap.put(DEFAULT_DATA_SOURCE_NAME, dataSource);
    }

    /**
     * 设置数据源
     *
     * @param group      配置文件中对应的分组
     * @param dataSource 数据源
     */
    public static void setDataSource(String group, DataSource dataSource) {
        dsmap.put(group, dataSource);
    }

    /**
     * 获得数据源
     *
     * @return 数据源
     */
    private static DataSource getDs() {
        return getDS(null);
    }

    /**
     * 获得数据源
     *
     * @param group 配置文件中对应的分组
     * @return 数据源
     */
    private static DataSource getDS(String group) {
        return DSFactory.get(group);
    }
}
