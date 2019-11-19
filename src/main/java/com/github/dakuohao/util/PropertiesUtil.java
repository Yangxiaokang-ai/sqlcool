package com.github.dakuohao.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 属性文件工具类
 *
 * @author Peng 1029538990@qq.com
 * @version 1.0
 * @date 2019/11/19 16:52
 */
public class PropertiesUtil {
    private static String driverClassName;
    private static String url;
    private static String username;
    private static String password;

    private static Properties properties = new Properties();

    //类加载时 初始化配置文件
    static {
        try {
            //加载资源文件
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
            properties.load(in);
            driverClassName = properties.getProperty("db.jdbc.driverClassName");
            url = properties.getProperty("db.jdbc.url");
            username = properties.getProperty("db.jdbc.username");
            password = properties.getProperty("db.jdbc.password");
        } catch (IOException e) {
            ExceptionUtil.throwDbRuntimeException(e, "初始化配置文件报错，请检测配置文件");
        }
    }

    public static String getDriverClassName() {
        return driverClassName;
    }

    public static String getUrl() {
        return url;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static Properties getProperties() {
        return properties;
    }
}
