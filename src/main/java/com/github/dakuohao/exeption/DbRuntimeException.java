package com.github.dakuohao.exeption;

/**
 * Db运行时异常
 * @author Peng 1029538990@qq.com
 */
public class DbRuntimeException extends  RuntimeException {
    public DbRuntimeException() {
    }

    public DbRuntimeException(String message) {
        super(message);
    }

    public DbRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DbRuntimeException(Throwable cause) {
        super(cause);
    }

    public DbRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
