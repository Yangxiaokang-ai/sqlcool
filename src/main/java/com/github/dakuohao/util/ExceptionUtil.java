package com.github.dakuohao.util;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.github.dakuohao.exeption.DbRuntimeException;

/**
 * 封装通用的异常处理 工具类
 *
 * @author Peng 1029538990@qq.com
 * @version 1.0
 * @date 2019/11/19 17:14
 */
public class ExceptionUtil {
    private static final Log LOG = LogFactory.get();

    /**
     * 抛出DbRuntimeException
     *
     * @param exception 异常对象
     * @param message   提示信息
     * @see DbRuntimeException
     */
    public static void throwDbRuntimeException(Exception exception, String message) {
        exception.printStackTrace();
        LOG.error(message);
        throw new DbRuntimeException(message, exception);
    }

    /**
     * 抛出DbRuntimeException
     *
     * @param message 提示信息
     * @see DbRuntimeException
     */
    public static void throwDbRuntimeException(String message) {
        LOG.error(message);
        throw new DbRuntimeException(message);
    }
}
