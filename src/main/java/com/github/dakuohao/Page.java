package com.github.dakuohao;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import com.github.dakuohao.util.ExceptionUtil;

import java.util.List;

/**
 * 分页对象 表示一页数据
 *
 * @author Peng 1029538990@qq.com
 */
public class Page {
    //---查询参数----
    /**
     * 当前页，默认为1
     */
    private Integer current = 1;
    /**
     * 页面大小，每页多少条数据，默认10
     */
    private Integer size = 10;
    /**
     * 排序字段，多个字段用逗号拼接，例：name asc,age desc
     * 逗号兼容 中/英文，特意做了适配
     */
    private String orderBy;

    //---返回结果参数---
    /**
     * 数据总数，默认0
     */
    private Integer total = 0;
    /**
     * 数据列表
     */
    private List<Entity> list;

    /**
     * 默认构造
     */
    public Page() {
    }

    public Page(Integer current, Integer size, String orderBy) {
        this();
        setCurrent(current);
        setCurrent(size);
        setOrderBy(orderBy);
    }

    /**
     * 获得limit
     * // limit limit,offset
     *
     * @return Integer
     */
    public Integer getLimit() {
        //limit = (current-1)*size
        return (current - 1) * size;
    }


    //---get,set方法

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        if (current <= 0) {
            ExceptionUtil.throwDbRuntimeException("查询页current必须大于等于1");
        }
        this.current = current;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        if (size <= 0) {
            ExceptionUtil.throwDbRuntimeException("页面大小参数size必须大于等于1");
        }
        this.size = size;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        //orderBy字段兼容 中英文逗号
        if (StrUtil.isNotBlank(orderBy)) {
            orderBy = orderBy.trim();
            orderBy = orderBy.replaceAll("，", ",");
        } else {
            //智能点 此处不报错，自动转空字符串 不影响查询
            orderBy = "";
        }
        this.orderBy = orderBy;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Entity> getList() {
        return list;
    }

    public void setList(List<Entity> list) {
        this.list = list;
    }
}
