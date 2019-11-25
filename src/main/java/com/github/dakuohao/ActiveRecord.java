package com.github.dakuohao;

import cn.hutool.db.Entity;
import com.github.dakuohao.util.ExceptionUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * 数据库操作接口，实现该接口表示赋予类操作数据库的能力
 * 封装ActiveRecord方式操作数据：
 * ActiveRecord:一条数据映射为一个Map<String,Object>对象，多条数据映射为List<Map<String,Object>>
 * 这里为了操作Map<String,Object>方便，封装类Entity代之Map<String,Object>，使用方便
 *
 * @author Peng 1029538990@qq.com
 * @version 1.0
 * @date 2019/11/25 21:46
 * @see cn.hutool.db.Entity
 */
public interface ActiveRecord extends JDBC {

    /**
     * 插入Entity
     * 如果数据库配置自动生成主键，那么会自动设置id字段为主键值
     *
     * @param entity 实体对象
     * @return 插入成功返回true，否则返回false
     */
    default Boolean insert(Entity entity) {
        checkEntity(entity);
        Long id = 0L;
        try {
            id = getDb().insertForGeneratedKey(entity);
            if (id > 0) {
                entity.set("id", id);
            }
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "插入数据时发生异常");
        }
        return id > 0L;
    }

    /**
     * 通过表名和id删除数据
     *
     * @param tableName 表名
     * @param id        主键id
     * @return 删除成功返回true，失败返回false
     */
    default Boolean deleteById(String tableName, Object id) {
        String sql = "DELETE FROM " + tableName + " WHERE id =?";
        return delete(sql, id);
    }


    /**
     * 通过表名和ids删除数据
     *
     * @param tableName 表名
     * @param ids       主键ids数组
     * @return 删除成功返回true，失败返回false
     */
    default Boolean deleteByIds(String tableName, Object... ids) {
        String sql = "DELETE FROM " + tableName + " WHERE id =?";
        Object[][] params = new Object[ids.length][1];
        for (int i = 0; i < ids.length; i++) {
            params[i][0] = ids[i];
        }
        return executeUpdateBatch(sql, params) != null;
    }


    /**
     * 通过id修改，字段不为空就修改，为空则不修改
     *
     * @param entity 实体对象
     * @return 修改成功返回true，否则返回false
     */
    default Boolean updateById(Entity entity) {
        checkEntity(entity);
        Entity where = Entity.create(entity.getTableName()).set("id", entity.get("id"));
        entity.remove("id");
        int update = 0;
        try {
            update = getDb().update(entity, where);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "执行updateById时发生异常");
        }
        return update > 0;
    }

    /**
     * 插入或者修改,通过id查询数据库，数据存在则修改，不存在则插入
     *
     * @param entity 实体对象
     * @return 修改成功返回true，否则返回false
     */
    default Boolean insertOrUpdate(Entity entity) {
        transaction(parameter -> {
            checkEntity(entity);
            Object id = entity.get("id");
            if (id != null) {
                String sql = "SELECT COUNT(*) FROM " + entity.getTableName() + " WHERE id =?";
                Integer count = count(sql, id);
                if (count > 0) {//数据库存在，修改数据
                    updateById(entity);
                } else {
                    insert(entity);
                }
            } else {//插入数据
                insert(entity);
            }
        });
        return true;
    }

    /**
     * 执行查询sql
     *
     * @param sql    sql
     * @param params 参数
     * @return 返回数据列表List<Entity>，默认为null
     */
    default List<Entity> select(String sql, Object... params) {
        return executeQuery(sql, params);
    }

    /**
     * 执行查询sql
     *
     * @param <T>    泛型T
     * @param tClass 类型
     * @param sql    sql
     * @param params 参数
     * @return 返回数据列表List<T>，默认为null
     */
    default <T> List<T> select(Class<T> tClass, String sql, Object... params) {
        List<T> select = null;
        try {
            select = getDb().query(sql, tClass, params);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "select方法发生异常");
        }
        return select;
    }

    /**
     * 查询一条数据
     *
     * @param sql    sql
     * @param params 参数
     * @return 返回实体对象Entity，默认为空
     */
    default Entity selectOne(String sql, Object... params) {
        Entity entity = null;
        try {
            entity = getDb().queryOne(sql, params);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "查询一条数据时发生异常");
        }
        return entity;
    }

    /**
     * 通过主键id查询
     *
     * @param tableName 表名
     * @param id        主键id
     * @return 实体对象Entity，默认为null
     */
    default Entity selectById(String tableName, Object id) {
        String sql = "SELECT * FROM " + tableName + " WHERE `id`=?";
        return selectOne(sql, id);
    }

    /**
     * 分页查询
     *
     * @param current 当前页
     * @param size    页面大小，每页数据条数
     * @param orderBy 排序字段，多个字段用逗号拼接，例：name asc,age desc，逗号兼容 中/英文，特意做了适配
     * @param sql     查询sql语句，不用拼接ORDER BY 和 LIMIT
     * @param params  查询参数
     * @return Page 对象
     * @see Page
     */
    default Page page(Integer current, Integer size, String orderBy, String sql, Object... params) {
        Page page = new Page(current, size, orderBy);
        return page(page, sql, params);
    }

    /**
     * 分页查询
     *
     * @param page   分页查询参数
     * @param sql    查询sql语句，不用拼接ORDER BY 和 LIMIT
     * @param params 查询参数
     * @return Page 对象
     * @see Page
     */
    default Page page(Page page, String sql, Object... params) {
        ////1.count查询
        String countSql = "SELECT COUNT(*) FROM (" + sql + ") temp";
        Integer count = count(countSql, params);
        //count查询为0时，直接返回
        if (count <= 0) {
            return page;
        }

        ////2.查询数据列表
        String pageSql = sql + "ORDER BY " + page.getOrderBy() + " LIMIT " + page.getLimit() + "," + page.getSize();
        List<Entity> list = select(pageSql, params);

        ////3.构造Page对象
        page.setTotal(count);
        page.setList(list);
        return page;
    }
}
