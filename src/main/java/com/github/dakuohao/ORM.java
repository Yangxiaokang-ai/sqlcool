package com.github.dakuohao;

import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.db.Entity;
import com.github.dakuohao.util.ExceptionUtil;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 数据库操作接口，实现该接口表示赋予类操作数据库的能力
 * 封装ORM方式操作数据：
 * ORM:一条数据映射为一个Bean对象，多条数据映射为List<Bean>
 *
 * @author Peng 1029538990@qq.com
 * @version 1.0
 * @date 2019/11/25 21:46
 */
public interface ORM extends JDBC {

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
        Boolean insert = Sql.sql().insert(entity);
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
            return Sql.sql().deleteById(tableName, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ExceptionUtil.throwDbRuntimeException(e, "deleteById方法执行时异常");
        }
        return false;
    }

    /**
     * 通过ids删除数据
     *
     * @param ids 主键ids数组
     * @return 删除成功返回true，失败返回false
     */
    default Boolean deleteByIds(Object... ids) {
        String tableName = getTableName();
        return Sql.sql().deleteByIds(tableName, ids);
    }


    /**
     * 通过id修改，字段不为空就修改，为空则不修改
     *
     * @return 修改成功返回true，否则返回false
     */
    default Boolean updateById() {
        Entity entity = Entity.create(getTableName());
        bean2Entity(entity, this);
        return Sql.sql().updateById(entity);
    }

    /**
     * 插入或者修改,通过id查询数据库，数据存在则修改，不存在则插入
     *
     * @return 修改成功返回true，否则返回false
     */
    default Boolean insertOrUpdate() {
        Entity entity = Entity.create(getTableName());
        bean2Entity(entity, this);
        return Sql.sql().insertOrUpdate(entity);
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
        return (List<T>) Sql.sql().select(this.getClass(), sql, params);
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
     * @param id 主键id
     * @return 实体对象Entity，默认为null
     */
    default <T> T selectById(Object id) {
        String tableName = getTableName();
        String sql = "SELECT * FROM " + tableName + " WHERE `id`=?";
        return selectOne(sql, id);
    }


}
