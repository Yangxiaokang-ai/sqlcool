package cn.hutool.db.sql;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.log.level.Level;

/**
 * 覆写hutool中的日志打印工具类
 * SQL在日志中打印配置
 *
 * @author looly
 * @since 4.1.0
 */
public enum SqlLog {
    INSTANCE;

    private final static Log log = LogFactory.get();

    /**
     * 是否debugSQL
     */
    private boolean showSql;
    /**
     * 是否格式化SQL
     */
    private boolean formatSql;
    /**
     * 是否显示参数
     */
    private boolean showParams;
    /**
     * 默认日志级别
     */
    private Level level = Level.DEBUG;

    /**
     * 设置全局配置：是否通过debug日志显示SQL
     *
     * @param isShowSql    是否显示SQL
     * @param isFormatSql  是否格式化显示的SQL
     * @param isShowParams 是否打印参数
     * @param level        日志级别
     */
    public void init(boolean isShowSql, boolean isFormatSql, boolean isShowParams, Level level) {
        this.showSql = isShowSql;
        this.formatSql = isFormatSql;
        this.showParams = isShowParams;
        this.level = level;
    }

    /**
     * 打印SQL日志
     *
     * @param sql SQL语句
     * @since 4.6.7
     */
    public void log(String sql) {
        log(sql, null);
    }

    /**
     * 打印批量 SQL日志
     *
     * @param sql SQL语句
     * @since 4.6.7
     */
    public void logForBatch(String sql) {
        if (this.showSql) {
            log.log(this.level, "\n[Batch SQL] -> {}", this.formatSql ? SqlFormatter.format(sql) : sql);
        }
    }

    /**
     * 打印批量 SQL日志
     *
     * @param sqls SQL语句
     * @since 4.6.7
     */
    public void logForBatch(String... sqls) {
        StringBuilder sb = new StringBuilder();
        for (String sql : sqls) {
            sql = sql.trim();
            if (!sql.endsWith(";")) {
                sql = sql + ";\n";
            }
            sb.append(sql);
        }
        String logSql = sb.toString();
        if (this.showSql) {
            log.log(this.level, "\n[Batch SQL] -> {}", this.formatSql ? SqlFormatter.format(logSql) : logSql);
        }
    }

    /**
     * 打印SQL日志
     *
     * @param sql         SQL语句
     * @param paramValues 参数，可为null
     */
//	public void log(String sql, Object paramValues) {
//		if (this.showSql) {
//			if (null != paramValues && this.showParams) {
//				log.log(this.level, "\n[SQL] -> {}\nParams -> {}", this.formatSql ? SqlFormatter.format(sql) : sql, paramValues);
//			} else {
//				log.log(this.level, "\n[SQL] -> {}", this.formatSql ? SqlFormatter.format(sql) : sql);
//			}
//		}
//	}
    //改进sql打印输出时  去掉？ 直接输出参数值
    public void log(String sql, Object paramValues) {
        if (this.showSql) {
            String logSql = this.formatSql ? SqlFormatter.format(sql) : sql;
            if (this.showParams && ArrayUtil.isArray(paramValues)) {
                //替换?为{}
                logSql = logSql.replaceAll("\\?", "{}");
                Object[] params = (Object[]) paramValues;
                //如果是字符串时，给参数拼接 单引号''
                //示例：INSERT INTO `test`.`user` (`name`, `age`) VALUES ('测试002', 19);
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof CharSequence) {
                        params[i] = "'" + param + "'";
                    }
                }
                log.log(this.level, "\n[SQL] : " + logSql, params);
            } else {
                log.log(this.level, "\n[SQL] : " + logSql);
            }
        }
    }
}
