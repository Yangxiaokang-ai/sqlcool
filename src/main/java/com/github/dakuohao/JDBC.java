package com.github.dakuohao;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.sql.SqlLog;
import cn.hutool.db.transaction.TransactionLevel;
import com.github.dakuohao.util.ExceptionUtil;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作接口，实现该接口表示赋予类操作数据库的能力
 * 封装JDBC方式操作数据库
 *
 * @author Peng 1029538990@qq.com
 * @version 1.0
 * @date 2019/11/25 21:45
 */
public interface JDBC {
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
            list = initDb().query(sql, params);
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
            result = initDb().execute(sql, params);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "执行executeUpdate时发生异常");
        }
        return result;
    }

    /**
     * 批量执行执行修改sql
     *
     * @param sql 多个sql语句
     * @return int[]，每个SQL执行影响的行数
     */
    default int[] executeUpdateBatch(String... sql) {
        int[] result = null;
        try {
            result = initDb().executeBatch(sql);
            //todo hutool没做日志  这里补充
            SqlLog.INSTANCE.logForBatch(sql);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "执行executeUpdate时发生异常");
        }
        return result;
    }

    /**
     * 批量执行执行修改sql
     *
     * @param sql         sql语句
     * @param paramsBatch 批量sql参数
     * @return int[]，每个SQL执行影响的行数
     */
    default int[] executeUpdateBatch(String sql, Object[]... paramsBatch) {
        int[] result = null;
        try {
            result = initDb().executeBatch(sql, paramsBatch);
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
     * 执行插入数据sql 并返回自增主键
     *
     * @param sql    sql
     * @param params 参数
     * @return 插入成功返回true，否则返回false
     */
    default Long insertForGeneratedKey(String sql, Object... params) {
        Long id = 0L;
        try {
            id = initDb().executeForGeneratedKey(sql, params);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "执行executeForGeneratedKey方法时异常");
        }
        return id;
    }

    /**
     * 事务执行
     *
     * @param transactionLevel 事务级别
     * @param func             空方法
     * @see TransactionLevel , VoidFunc1
     */
    default void transaction(TransactionLevel transactionLevel, VoidFunc1<Db> func) {
        try {
            initDb().tx(transactionLevel, func);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "事务执行时发生异常");
        }
    }

    /**
     * 事务执行
     *
     * @param func 空方法
     * @see TransactionLevel,VoidFunc1
     */
    default void transaction(VoidFunc1<Db> func) {
        transaction(null, func);
    }

    /**
     * 批量插入数据，一条一条的循环插入，速度慢
     * 会自动生成主键，并设置id值
     *
     * @param list 多个实体对象
     * @return 插入成功返回true，否则返回false
     */
    @SuppressWarnings("unchecked")
    default Boolean insert(List list) {
        transaction(parameter -> {
            checkList(list);
            Object o = list.get(0);
            if (o instanceof Entity) {
                for (Entity entity : (List<Entity>) list) {
                    Sql.sql().insert(entity);
                }
            } else {
                for (Object bean : list) {
                    Method insert = bean.getClass().getMethod("insert");
                    insert.invoke(bean);
                }
            }
        });
        return true;
    }

    /**
     * 批量插入数据，本质是jdbc的批次插入，效率快
     * 需要注意的是，批量插入每一条数据结构必须一致。批量插入数据时会获取第一条数据的字段结构，之后的数据会按照这个格式插入。<br>
     * 也就是说假如第一条数据只有2个字段，后边数据多于这两个字段的部分将被抛弃。
     *
     * @param list 多个实体对象
     * @return 插入成功返回true，否则返回false
     */
    @SuppressWarnings("unchecked")
    default Boolean insertBatch(List list) {
        checkList(list);
        Object o = list.get(0);
        List<Entity> entityList;
        //判断不是Entity类型就 beanToEntity
        if (o instanceof Entity) {
            entityList = list;
        } else {
            entityList = new ArrayList<>(list.size());
            String tableName = tableName(o.getClass());
            for (Object bean : list) {
                Entity entity = Entity.create(tableName);
                bean2Entity(entity, bean);
                entityList.add(entity);
            }
        }
        int[] ids = null;
        try {
            ids = initDb().insert(entityList);
        } catch (Exception e) {
            ExceptionUtil.throwDbRuntimeException(e, "插入数据时发生异常");
        }
        return ids != null;
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
     * 批量修改，一条一条循环修改
     *
     * @param list 数据列表
     * @return 修改成功返回true，否则返回false
     */
    @SuppressWarnings("unchecked")
    default Boolean update(List list) {
        transaction(parameter -> {
            checkList(list);
            Object o = list.get(0);
            if (o instanceof Entity) {
                for (Entity entity : (List<Entity>) list) {
                    Sql.sql().updateById(entity);
                }
            } else {
                for (Object bean : list) {
                    Method insert = bean.getClass().getMethod("updateById");
                    insert.invoke(bean);
                }
            }
        });
        return true;
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
            number = initDb().queryNumber(sql, params);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "count方法异常");
        }
        return number.intValue();
    }

    //---- 工具方法方法  ---

    /**
     * 获得Db对象
     *
     * @return Db
     * @see Db
     */
    default Db initDb() {
        Table table = this.getClass().getAnnotation(Table.class);
        String group = null;
        if (table != null && StrUtil.isNotEmpty(group)) {
            group = table.dataSource();
        }
        return Db.use(group);
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

    /**
     * 检查list
     *
     * @param list 参数
     */
    default void checkList(List list) {
        if (list.isEmpty()) {
            ExceptionUtil.throwDbRuntimeException("批量插入，数据不能为空");
        }
    }

    /**
     * 获取表名
     * 将驼峰式命名的类名转换为下划线方式返回
     *
     * @return String
     */
    default String tableName() {
        return tableName(this.getClass());
    }

    /**
     * 获取表名
     * 将驼峰式命名的类名转换为下划线方式返回
     *
     * @param aClass 类
     * @return String
     */
    default String tableName(Class aClass) {
        Table table = (Table) aClass.getAnnotation(Table.class);
        if (table != null && StrUtil.isNotBlank(table.value())) {
            return toUnderlineCase(table.value());
        }
        return toUnderlineCase(aClass.getSimpleName());
    }

    /**
     * 驼峰命名转下划线命名
     *
     * @param tableName 表名
     * @return 下划线格式的表名
     */
    default String toUnderlineCase(String tableName) {
        tableName = StrUtil.toUnderlineCase(tableName);
        if (tableName.startsWith("_")) {
            tableName = tableName.substring(1);
        }
        return "`" + tableName + "`";
    }

    /**
     * bean转换为Entity
     *
     * @param entity entity对象
     * @param bean   bean对象
     */
    default void bean2Entity(Entity entity, Object bean) {
        //驼峰转下划线 and  忽略null字段
        BeanUtil.beanToMap(bean, entity, true, true);
    }

}
