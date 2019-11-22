package com.github.dakuohao.test;

@Name("父类名称")
public interface FuInterface {

    default String getSimpleName() {
        return this.getClass().getSimpleName();
    }

    default String getName() {
        return this.getClass().getAnnotation(Name.class).value();
    }

}
