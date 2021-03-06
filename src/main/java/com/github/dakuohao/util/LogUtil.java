package com.github.dakuohao.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 日志工具类封装
 *
 * @author Peng 1029538990@qq.com
 * @version 1.0
 * @date 2019/11/19 17:29
 */
public class LogUtil {
    private static final Log LOG = LogFactory.get();

    /**
     * 打印日志
     *
     * @param sql    sql语句
     * @param params 参数
     */
    public static void log(String sql, Object... params) {
//        LOG.debug("原SQL： " + sql);
//        if (ArrayUtil.isNotEmpty(params)) {
//            LOG.debug("参数： " + ArrayUtil.toString(params));
//        }
        //替换?为{}
        sql = sql.replaceAll("\\?", "{}");
        //如果是字符串时，给参数拼接 单引号''
        //示例：INSERT INTO `test`.`user` (`name`, `age`) VALUES ('测试002', 19);
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof CharSequence) {
                params[i] = "'" + param + "'";
            }
        }
        LOG.info("执行sql： " + sql, params);
    }

}
