package com.github.dakuohao.util;

import com.github.dakuohao.exeption.DbRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Db用到的所有工具类方法
 */
public class DbUtil {
    private static String driverClassName;
    private static String url;
    private static String username;
    private static String password;
    /**
     * 配置文件
     */
    private static Properties properties;

    //类加载时 初始化配置文件
    static {
        properties = new Properties();
        try {
            //加载资源文件
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
            properties.load(in);
            driverClassName = properties.getProperty("db.jdbc.driverClassName");
            url = properties.getProperty("db.jdbc.url");
            username = properties.getProperty("db.jdbc.username");
            password = properties.getProperty("db.jdbc.password");
        } catch (IOException e) {
            DbUtil.throwDbRuntimeException(e, "初始化配置文件报错，请检测配置文件");
        }
    }

    /**
     * 获取数据库连接
     *
     * @return Connection
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            //加载数据库驱动
            Class.forName(driverClassName);
            //获取数据库连接
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throwDbRuntimeException(e, "ClassNotFoundException：未找到数据库驱动");
        } catch (SQLException e) {
            throwDbRuntimeException(e, "SQLException：获取数据库连接失败");
        }
        return conn;
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
            DbUtil.throwDbRuntimeException(e, "SQLException：关闭ResultSet时发生异常");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                DbUtil.throwDbRuntimeException(e, "SQLException：关闭Statement时发生异常");
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    DbUtil.throwDbRuntimeException(e, "SQLException：关闭Connection时发生异常");
                }
            }
        }
    }


    //-- 异常处理---

    /**
     * 抛出DbRuntimeException
     *
     * @param exception 异常对象
     * @param message   提示信息
     * @see DbRuntimeException
     */
    public static void throwDbRuntimeException(Exception exception, String message) {
        exception.printStackTrace();
        throw new DbRuntimeException(message);
    }

}
