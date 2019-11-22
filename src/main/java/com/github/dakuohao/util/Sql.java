package com.github.dakuohao.util;

import cn.hutool.db.Entity;
import cn.hutool.db.sql.SqlUtil;
import com.github.dakuohao.DataBase;
import com.github.dakuohao.bean.Page;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SQL构建工具
 *
 * @author Peng 1029538990@qq.com
 * @version 1.0
 */
public final class Sql implements DataBase {
    /**
     * sql构建
     */
    private StringBuilder stringBuilder = new StringBuilder();
    /**
     * sql语句
     */
    private String sql;
    /**
     * sql参数
     */
    private List<Object> params;

    /**
     * 私有构造函数
     */
    private Sql() {
    }

    public static Sql create() {
        return new Sql();
    }

    /**
     * 追加sql
     *
     * @param sql sql
     * @return Sql
     */
    public Sql append(CharSequence sql) {
        stringBuilder.append(sql);
        return this;
    }

    /**
     * 追加
     *
     * @param isNull 如果为true，拼接后边的sql,否则不拼接
     * @param sql    sql
     * @return Sql
     */
    public Sql append(Boolean isNull, CharSequence sql) {
        if (isNull) {
            stringBuilder.append(sql);
        }
        return this;
    }

    /**
     * 设置参数
     * 变量使用 @变量名  方式
     * 示例： SELECT * FROM user WHERE name = @name;
     *
     * @param entity entity对象 key - value形式
     * @return Sql
     */
    public Sql setParams(Entity entity) {
        sql = stringBuilder.toString();
        params = new ArrayList<>();
        for (Map.Entry<String, Object> entry : entity.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String flag = "@" + key;
            while (sql.contains(flag)) {
                sql = sql.replace(flag, "?");
                params.add(value);
            }
        }
        return this;
    }

    /**
     * 执行sql，返回结果  会智能判断是执行查询还是执行修改
     *
     * @param <T> 泛型T
     * @return T，执行查询语句返回List<Entity>,执行修改语句返回Integer
     */
    @SuppressWarnings("unchecked")
    public <T> T execute() {
        this.sql = this.sql.trim();
        String select = this.sql.substring(0, 6);
        if ("select".equalsIgnoreCase(select)) {
            return (T) executeQuery();
        }
        return (T) executeUpdate();
    }


    /**
     * 执行查询
     *
     * @return List<Entity>
     */
    public List<Entity> executeQuery() {
        checkSql();
        return select(this.sql, this.params);
    }

    /**
     * 执行查询，并包装为Bean类型
     *
     * @param tClass 类型
     * @param <T>    泛型
     * @return List<T>
     */
    public <T> List<T> executeQuery(Class<T> tClass) {
        return select(tClass, this.sql, this.params);
    }

    /**
     * 执行修改
     *
     * @return Integer
     */
    public Integer executeUpdate() {
        try {
            return cn.hutool.db.Db.use().execute(this.sql, this.params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 分页查询，自动包装查询sql为分页查询
     * 提供count和list查询
     *
     * @param page 分页参数
     * @return Page
     * @see Page
     */
    public Page page(Page page) {
        return page(page, this.sql, this.params);
    }


    /**
     * 分页查询，自动包装查询sql为分页查询
     * 提供count和list查询
     *
     * @param current 当前页
     * @param size    页面大小，每页数据条数
     * @param orderBy 排序字段，多个字段用逗号拼接，例：name asc,age desc，逗号兼容 中/英文，特意做了适配
     * @return Page
     * @see Page
     */
    public Page page(Integer current, Integer size, String orderBy) {
        return page(current, size, orderBy, this.sql, this.params);
    }


    /**
     * 检测sql，处理为规范sql
     */
    private void checkSql() {
        //格式化sql
        this.sql = SqlUtil.formatSql(this.sql);
        //去除 where后的and
        this.sql = this.sql.replaceAll("WHERE\\SAND ", "WHERE");
    }


    //--- get,set ----

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }
}
