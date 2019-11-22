package com.github.dakuohao.ds;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.dakuohao.util.PropertiesUtil;

import javax.sql.DataSource;

/**
 * 数据源创建，工厂模式
 * 阿里Druid 数据源实现
 *
 * @author Peng 1029538990@qq.com
 * @version 1.0
 * @date 2019/11/19 11:17
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    @Override
    public DataSource getDataSource() {
        final DruidDataSource ds = new DruidDataSource();

        ds.setUrl(PropertiesUtil.getUrl());
        ds.setDriverClassName(PropertiesUtil.getDriverClassName());
        ds.setUsername(PropertiesUtil.getUsername());
        ds.setPassword(PropertiesUtil.getPassword());

        // 连接池信息
        ds.configFromPropety(PropertiesUtil.getProperties());

        // 检查关联配置，在用户未设置某项配置时，
        if (null == ds.getValidationQuery()) {
            // 在validationQuery未设置的情况下，以下三项设置都将无效
            ds.setTestOnBorrow(false);
            ds.setTestOnReturn(false);
            ds.setTestWhileIdle(false);
        }

        return ds;
    }

}
