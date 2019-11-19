package com.github.dakuohao.util;

import com.github.dakuohao.bean.Entity;
import com.github.dakuohao.ds.DataSourceFactory;
import com.github.dakuohao.ds.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Db用到的所有工具类方法
 *
 * @author Peng 1029538990@qq.com
 */
public class DbUtil {

    /**
     * 获取DataSourceFactory
     *
     * @return DataSourceFactory
     */
    private static DataSourceFactory getDataSourceFactory() {
        //todo 后期增加其他数据库连接池支持
        return new DruidDataSourceFactory();
    }


    /**
     * 获取数据源
     *
     * @return DataSource
     */
    public static DataSource getDataSource() {
        return getDataSourceFactory().getDataSource();
    }

    /**
     * 获取数据库连接
     *
     * @return Connection
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = getDataSource().getConnection();
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "SQLException：获取数据库连接失败");
        }
        return conn;
    }

    /**
     * 获取数据库连接,不使用数据源（数据库连接池）
     *
     * @param noUseDataSource 不使用数据源（数据库连接池）
     * @return Connection
     */
    public static Connection getConnection(boolean noUseDataSource) {
        Connection conn = null;
        try {
            //加载数据库驱动
            Class.forName(PropertiesUtil.getDriverClassName());
            //获取数据库连接
            conn = DriverManager.getConnection(PropertiesUtil.getUrl(), PropertiesUtil.getUsername(), PropertiesUtil.getPassword());
        } catch (ClassNotFoundException e) {
            ExceptionUtil.throwDbRuntimeException(e, "ClassNotFoundException：未找到数据库驱动");
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "SQLException：获取数据库连接失败");
        }
        return conn;
    }

    /**
     * 获取数据库连接
     *
     * @return Connection
     */
    public static PreparedStatement getPreparedStatement(String sql, Object... params) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getConnection().prepareStatement(sql);
            if (null != params && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "SQLException：获取PreparedStatement失败");
        }
        return preparedStatement;
    }

    //---关闭资源---

    /**
     * 关闭JDBC资源
     *
     * @param conn Connection
     */
    public static void close(Connection conn) {
        close(null, conn);
    }

    /**
     * 关闭JDBC资源
     *
     * @param ps   Statement
     * @param conn Connection
     */
    public static void close(Statement ps, Connection conn) {
        close(null, ps, conn);
    }

    /**
     * 关闭JDBC资源  注意顺序
     *
     * @param rs   ResultSet
     * @param ps   Statement
     * @param conn Connection
     */
    public static void close(ResultSet rs, Statement ps, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "SQLException：关闭ResultSet时发生异常");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                ExceptionUtil.throwDbRuntimeException(e, "SQLException：关闭Statement时发生异常");
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    ExceptionUtil.throwDbRuntimeException(e, "SQLException：关闭Connection时发生异常");
                }
            }
        }
    }

}
