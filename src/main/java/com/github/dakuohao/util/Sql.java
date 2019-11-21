package com.github.dakuohao.util;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SQL构建工具
 */
public final class Sql {
    private StringBuilder stringBuilder = new StringBuilder();
    private String sql;
    private List<Object> params;

    /**
     * 私有构造函数
     */
    private Sql() {
    }

    public static Sql create() {
        return new Sql();
    }

    public Sql append(CharSequence sql) {
        stringBuilder.append(sql);
        return this;
    }

    public Sql append(Boolean isNull, CharSequence sql) {
        if (isNull) {
            stringBuilder.append(sql);
        }
        return this;
    }

    public Sql setParams(Entity entity) {
        sql = stringBuilder.toString();
        params = new ArrayList<>();
        for (Map.Entry<String, Object> entry : entity.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            while (true) {
                String flag = "@" + key;
                if (!sql.contains(flag)) break;
                sql = sql.replace(flag, "?");
                params.add(value);
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T execute() {
        this.sql = this.sql.trim();
        String select = this.sql.substring(0, 6);
        if ("select".equalsIgnoreCase(select)) {
            return (T) executeQuery();
        }
        return (T) executeUpdate();
    }


    public List<Entity> executeQuery() {
        try {
            return Db.use().query(this.sql, this.params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public <T> List<T> executeQuery(Class<T> tClass) {
        try {
            return Db.use().query(this.sql, tClass, this.params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //count


    //page

    public Integer executeUpdate() {
        try {
            return Db.use().execute(this.sql, this.params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
