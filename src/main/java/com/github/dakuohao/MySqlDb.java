package com.github.dakuohao;

import cn.hutool.core.collection.CollectionUtil;
import com.github.dakuohao.bean.Entity;
import com.github.dakuohao.bean.Page;
import com.github.dakuohao.util.DbUtil;
import com.github.dakuohao.util.ExceptionUtil;
import com.github.dakuohao.util.LogUtil;

import javax.sql.DataSource;
import javax.sql.PooledConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Db对于mysql数据库的具体实现
 *
 * @author Peng 1029538990@qq.com
 */
public class MySqlDb implements Db {

    public ResultSet executeQuery(String sql, Object... params) {
        PreparedStatement ps = DbUtil.getPreparedStatement(sql, params);
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            LogUtil.log(sql, params);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "SQLException：PreparedStatement执行executeQuery异常");
        }
        return rs;
    }

    public int executeUpdate(String sql, Object... params) {
        PreparedStatement ps = DbUtil.getPreparedStatement(sql, params);
        int result = 0;
        try {
            result = ps.executeUpdate();
            LogUtil.log(sql, params);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "SQLException：PreparedStatement执行executeUpdate异常");
        }
        return result;
    }

    public Boolean insert(String sql, Object... params) {
        return executeUpdate(sql, params) > 0;
    }

    public Boolean insert(Entity entity) {
        checkEntity(entity);
        StringBuilder sql = new StringBuilder("INSERT INTO (");
        StringBuilder valuesSql = new StringBuilder();
        List<Object> params = new ArrayList<Object>(entity.size());
        for (String key : entity.keySet()) {
            sql.append(key).append(",");
            valuesSql.append("?").append(",");
            params.add(entity.get(key));
        }
        //删除最后一个逗号
        sql.deleteCharAt(sql.length()-1);
        valuesSql.deleteCharAt(sql.length()-1);

        sql.append(") VALUES (").append(valuesSql).append(")");
        int insert = executeUpdate(sql.toString(), params);
        return insert > 0;
    }

    /**
     * 检查参数是否为空
     *
     * @param entity 参数
     */
    private void checkEntity(Entity entity) {
        if (CollectionUtil.isEmpty(entity)) {
            ExceptionUtil.throwDbRuntimeException("entity 不能为空！");
        }
    }

    public Boolean delete(String sql, Object... params) {
        return null;
    }

    public Boolean update(String sql, Object... params) {
        return null;
    }

    public List select(String sql, Object... params) {
        return null;
    }

    public Entity selectOne(String sql, Object... params) {
        return null;
    }

    public Entity selectById(Object id) {
        return null;
    }

    public int count(String sql, Object... params) {
        return 0;
    }

    public Page page(String sql, Object... params) {
        return null;
    }

    public Page page(Integer current, Integer size, String orderBy, String sql, Object... params) {
        return null;
    }

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
