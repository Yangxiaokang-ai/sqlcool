package com.github.dakuohao;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.sql.SqlLog;
import cn.hutool.db.transaction.TransactionLevel;
import com.github.dakuohao.factory.DbFatory;
import com.github.dakuohao.util.ExceptionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * mysql操作工具类
 * 实现该接口，表示赋予其操作数据库的能力
 *
 * @author Peng 1029538990@qq.com
 * @version 1.0
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
     * 批量执行执行修改sql
     *
     * @param sql 多个sql语句
     * @return int[]，每个SQL执行影响的行数
     */
    default int[] executeUpdateBatch(String... sql) {
        int[] result = null;
        try {
            result = getDb().executeBatch(sql);
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
            result = getDb().executeBatch(sql, paramsBatch);
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
            id = getDb().executeForGeneratedKey(sql, params);
        } catch (SQLException e) {
            ExceptionUtil.throwDbRuntimeException(e, "执行executeForGeneratedKey方法时异常");
        }
        return id;
    }

    /**
     * 插入
     *
     * @return 插入成功返回true，否则返回false
     */
    default Boolean insert() {
        //object to bean
        String tableName = getTableName();
        Entity entity = new Entity(tableName);
        //转化为下划线字段  忽略null值的字段
        bean2Entity(entity, this);
        Boolean insert = insert(entity);
        //插入成功后 将自增主键赋值给id字段，未设置自增或没有id字段则不赋值
        try {
            //获取一个类的 ==所有成员变量，不包括基类==
            Field field = this.getClass().getDeclaredField("id");
            field.setAccessible(true);
            Object id = ConverterRegistry.getInstance().convert(field.getType(), entity.getInt("id"));
            field.set(this, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ExceptionUtil.logException(e, "inset后设置自动生成的主键，反射时异常，插入数据库成功但未取到自增主键，检查数据库是否设置自增主键，且字段名必须是id");
        }
        return insert;
    }

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
                    insert(entity);
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
            String tableName = getTableName(o.getClass());
            for (Object bean : list) {
                Entity entity = Entity.create(tableName);
                bean2Entity(entity, bean);
                entityList.add(entity);
            }
        }
        int[] ids = null;
        try {
            ids = getDb().insert(entityList);
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
     * 通过id删除数据
     *
     * @return 删除成功返回true，失败返回false
     */
    default Boolean deleteById() {
        try {
            String tableName = getTableName();
            Field idField = this.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Object id = idField.get(this);
            return deleteById(tableName, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ExceptionUtil.throwDbRuntimeException(e, "deleteById方法执行时异常");
        }
        return false;
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
     * 通过ids删除数据
     *
     * @param ids 主键ids数组
     * @return 删除成功返回true，失败返回false
     */
    default Boolean deleteByIds(Object... ids) {
        String tableName = getTableName();
        return deleteByIds(tableName, ids);
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
     * 通过id修改，字段不为空就修改，为空则不修改
     *
     * @return 修改成功返回true，否则返回false
     */
    default Boolean updateById() {
        Entity entity = Entity.create(getTableName());
        bean2Entity(entity, this);
        return updateById(entity);
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
                    updateById(entity);
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
     * 插入或者修改,通过id查询数据库，数据存在则修改，不存在则插入
     *
     * @return 修改成功返回true，否则返回false
     */
    default Boolean insertOrUpdate() {
        Entity entity = Entity.create(getTableName());
        bean2Entity(entity, this);
        return insertOrUpdate(entity);
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

    /**
     * 通过主键id查询
     *
     * @param tableName 表名
     * @param id        主键id
     * @return 实体对象Entity，默认为null
     */
    default Entity selectByIdEntity(String tableName, Object id) {
        String sql = "SELECT * FROM " + tableName + " WHERE `id`=?";
        return selectOneEntity(sql, id);
    }

    /**
     * 通过主键id查询
     *
     * @param id 主键id
     * @return 实体对象Entity，默认为null
     */
    default <T> T selectById(Object id) {
        String tableName = getTableName();
        String sql = "SELECT * FROM " + tableName + " WHERE `id`=?";
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
            ExceptionUtil.throwDbRuntimeException(e, "count方法异常");
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
     * @see TransactionLevel,VoidFunc1
     */
    default void transaction(TransactionLevel transactionLevel, VoidFunc1<Db> func) {
        try {
            getDb().tx(transactionLevel, func);
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

    //---- 工具方法方法  ---

    /**
     * 获得Db对象
     *
     * @return Db
     * @see Db
     */
    default Db getDb() {
        Table table = this.getClass().getAnnotation(Table.class);
        if (table != null && StrUtil.isNotEmpty(table.dataSource())) {
            return DbFatory.get(table.dataSource());
        }
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
    default String getTableName() {
        return getTableName(this.getClass());
    }

    /**
     * 获取表名
     * 将驼峰式命名的类名转换为下划线方式返回
     *
     * @param aClass 类
     * @return String
     */
    default String getTableName(Class aClass) {
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
