package com.github.dakuohao;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.transaction.TransactionLevel;
import com.github.dakuohao.factory.DbFatory;
import com.github.dakuohao.util.ExceptionUtil;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

/**
 * mysql操作工具类
 * 实现该接口，表示赋予其操作数据库的能力
 *
 * @author Peng 1029538990@qq.com
 * @version 1.0
 * @date 2019/11/21 9:14
 */
public interface DataBase {

    /**
     * 执行查询sql
     *
     * @param sql    sql语句
     * @param params sql参数
     * @return List<Entity> 默认返回null
     * @see Entity
     */
    default List<Entity> executeQuery(String sql, Object... params) {
        List<Entity> list = null;
        try {
            list = getDb().query(sql, params);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "执行executeQuery时发生异常");
        }
        return list;
    }

    /**
     * 执行修改sql
     *
     * @param sql    sql语句
     * @param params sql参数
     * @return int，数据库修改数据的行数，默认为0
     */
    default int executeUpdate(String sql, Object... params) {
        int result = 0;
        try {
            result = getDb().execute(sql, params);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "执行executeUpdate时发生异常");
        }
        return result;
    }

    /**
     * 执行插入数据sql
     *
     * @param sql    sql
     * @param params 参数
     * @return 插入成功返回true，否则返回false
     */
    default Boolean insert(String sql, Object... params) {
        return executeUpdate(sql, params) > 0;
    }

    /**
     * 执行插入数据sql
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
     * 执行插入数据sql
     *
     * @return 插入成功返回true，否则返回false
     */
    default Boolean insert() {
        //object to bean
        Entity entity = new Entity(getTableName());
        //转化为下划线字段  忽略null值的字段
        BeanUtil.beanToMap(this, entity, true, true);
        Boolean insert = insert(entity);
        try {
            //获取一个类的 ==所有成员变量，不包括基类==
            Field field = this.getClass().getDeclaredField("id");
            field.setAccessible(true);
            Object id = ConverterRegistry.getInstance().convert(field.getType(), entity.getInt("id"));
            field.set(this, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ExceptionUtil.throwDbRuntimeException(e, "inset后设置自动生成的主键，反射时异常");
        }
        return insert;
    }

    /**
     * 获取表名
     * 将驼峰式命名的类名转换为下划线方式返回
     *
     * @return String
     */
    default String getTableName() {
        String name = this.getClass().getSimpleName();
        return StrUtil.toUnderlineCase(name);
    }

    /**
     * 执行删除sql
     *
     * @param sql    sql
     * @param params 参数
     * @return 删除成功返回true，失败返回false
     */
    default Boolean delete(String sql, Object... params) {
        return executeUpdate(sql, params) > 0;
    }

    /**
     * 执行删除sql
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
     * 执行修改sql
     *
     * @param sql    sql
     * @param params 参数
     * @return 修改成功返回true，否则返回false
     */
    default Boolean update(String sql, Object... params) {
        return executeUpdate(sql, params) > 0;
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
        int update = 0;
        try {
            update = getDb().update(entity, where);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "执行updateById时发生异常");
        }
        return update > 0;
    }

    /**
     * 通过id修改，字段不为空就修改，为空则不修改
     *
     * @return 修改成功返回true，否则返回false
     */
    default Boolean updateById() {
        Entity entity = Entity.create(getTableName());
        return updateById(entity);
    }

    /**
     * 执行查询sql
     *
     * @param sql    sql
     * @param params 参数
     * @return 返回数据列表List<Entity>，默认为null
     */
    default List<Entity> selectEntity(String sql, Object... params) {
        return executeQuery(sql, params);
    }

    /**
     * 执行查询sql
     *
     * @param <T>    泛型T
     * @param sql    sql
     * @param params 参数
     * @return 返回数据列表List<T>，默认为null
     */
    @SuppressWarnings("unchecked")
    default <T> List<T> select(String sql, Object... params) {
        return (List<T>) select(this.getClass(), sql, params);
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
    default Entity selectOneEntity(String sql, Object... params) {
        Entity entity = null;
        try {
            entity = getDb().queryOne(sql, params);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "查询一条数据时发生异常");
        }
        return entity;
    }

    /**
     * 查询一条数据
     *
     * @param <T>    泛型T
     * @param sql    sql
     * @param params 参数
     * @return 返回实体对象T，默认为空
     */
    default <T> T selectOne(String sql, Object... params) {
        List<T> list = select(sql, params);
        if (list != null && list.size() >= 1) {
            return list.get(0);
        }
        return null;
    }

//    /**
//     * 通过主键id查询
//     *
//     * @param tableName 表名
//     * @param id        主键id
//     * @return 实体对象Entity，默认为null
//     */
//    default Entity selectByIdEntity(String tableName, Object id) {
//        String sql = "SELECT * FROM " + tableName + " WHERE `id`=?";
//        return selectOne(sql, id);
//    }


    /**
     * 通过主键id查询
     *
     * @param id 主键id
     * @return 实体对象Entity，默认为null
     */
    default <T> T selectById(Object id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE `id`=?";
        return selectOne(sql, id);
    }


    /**
     * 查询数据数量
     *
     * @param sql    sql
     * @param params 参数
     * @return 数据条数Integer，默认为0
     */
    default Integer count(String sql, Object... params) {
        Number number = 0;
        try {
            number = getDb().queryNumber(sql, params);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "查询数据数量，count时发生异常");
        }
        return number.intValue();
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

    /**
     * 事务执行
     *
     * @param transactionLevel 事务级别
     * @param func             空方法
     * @see TransactionLevel,VoidFunc1, cn.hutool.db.Db
     */
    default cn.hutool.db.Db transaction(TransactionLevel transactionLevel, VoidFunc1<cn.hutool.db.Db> func) {
        cn.hutool.db.Db db = null;
        try {
            db = getDb().tx(transactionLevel, func);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "事务执行时发生异常");
        }
        return db;
    }

    /**
     * 事务执行
     *
     * @param func 空方法
     * @see TransactionLevel,VoidFunc1, cn.hutool.db.Db
     */
    default cn.hutool.db.Db transaction(VoidFunc1<cn.hutool.db.Db> func) {
        return transaction(null, func);
    }

    //---- 私有方法  ---

    /**
     * 获得Db对象
     *
     * @return Db
     * @see Db
     */
    default Db getDb() {
        return DbFatory.get();
    }

    /**
     * 检查entity
     *
     * @param entity 参数
     */
    default void checkEntity(Entity entity) {
        if (CollectionUtil.isEmpty(entity)) {
            ExceptionUtil.throwDbRuntimeException("entity 不能为空！");
        }
        if (StrUtil.isEmpty(entity.getTableName())) {
            ExceptionUtil.throwDbRuntimeException("必须指定表名");
        }
    }


}
