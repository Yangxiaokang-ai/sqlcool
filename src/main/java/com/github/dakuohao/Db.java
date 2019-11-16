package com.github.dakuohao;

import com.github.dakuohao.bean.Entity;
import com.github.dakuohao.bean.Page;

import javax.sql.DataSource;
import javax.sql.PooledConnection;
import java.sql.ResultSet;
import java.util.List;

/**
 * 数据库操作工具 简化开发，解放生产力
 */
public interface Db {

    /**
     * 执行查询语句  (SELECT)
     *
     * @param sql    sql
     * @param params 参数
     * @return ResultSet
     * @see ResultSet
     */
    ResultSet executeQuery(String sql, Object... params);

    /**
     * 执行修改语句 (INSERT,UPDATE,DELETE,CREATE,DROP等非查询操作)
     *
     * @param sql    sql
     * @param params 参数
     * @return ResultSet
     * @see ResultSet
     */
    ResultSet executeUpdate(String sql, Object... params);

    /**
     * 添加
     *
     * @param sql    sql
     * @param params 参数
     * @return 添加成功返回true，失败返回false
     */
    Boolean insert(String sql, Object... params);

    /**
     * 添加
     *
     * @param entity 实体对象
     * @return 添加成功返回true，失败返回false
     */
    Boolean insert(Entity entity);
//    Boolean insert(Object entity);

    /**
     * 删除
     *
     * @param sql    sql
     * @param params 参数
     * @return 删除成功返回true，失败返回false
     */
    Boolean delete(String sql, Object... params);

    /**
     * 修改
     *
     * @param sql    sql
     * @param params 参数
     * @return 修改成功返回true，失败返回false
     */
    Boolean update(String sql, Object... params);


    /**
     * 查询
     *
     * @param sql    sql
     * @param params 参数
     * @return 返回查询结果List，未查询到时返回空null
     */
    List select(String sql, Object... params);

    /**
     * 查询一个
     *
     * @param sql    sql
     * @param params 参数
     * @return 返回查询结果List，未查询到时返回空null
     */
    Entity selectOne(String sql, Object... params);

    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 返回查询结果Entity，未查询到时返回空null
     */
    Entity selectById(Object id);

    /**
     * 计数
     *
     * @param sql    sql
     * @param params 参数
     * @return 返回数据总数
     */
    int count(String sql, Object... params);

    /**
     * 分页查询
     *
     * @param sql    sql
     * @param params 查询参数
     * @return 返回分页对象Page
     * @see Page
     */
    Page page(String sql, Object... params);

    /**
     * 分页查询
     *
     * @param current 当前页
     * @param size    页面大小，多少条数据
     * @param orderBy 排序字段，多个排序字段用逗号隔开 例： id desc,name asc,sage desc
     * @param sql     sql
     * @param params  查询参数
     * @return 返回分页对象Page
     * @see Page
     */
    Page page(Integer current, Integer size, String orderBy, String sql, Object... params);


    //---创建Db---

    /**
     * 创建Db
     *
     * @return Db
     */
    Db createDb();

    /**
     * 创建Db
     *
     * @param dataSource 数据源
     * @return Db
     */
    Db createDb(DataSource dataSource);

    //---创建数据库连接池

    /**
     * 获取数据库连接池
     *
     * @return PooledConnection
     * @see PooledConnection
     */
    PooledConnection getPooledConnection();
}
