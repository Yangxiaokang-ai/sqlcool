package com.github.dakuohao.ds;

import javax.sql.DataSource;

/**
 * 数据源创建，工厂模式
 * @author Peng 1029538990@qq.com
 * @version 1.0
 * @date 2019/11/19 11:10
 */
public interface DataSourceFactory {
    /**
     * 获得数据源
     * @return DataSource
     */
    DataSource getDataSource();
}
