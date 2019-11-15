package com.github.dakuohao.exeption;

/**
 * Db运行时异常
 */
public class DbRuntimeException extends  RuntimeException {

    public DbRuntimeException(String message) {
        super(message);
    }
}
