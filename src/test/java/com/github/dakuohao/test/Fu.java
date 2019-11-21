package com.github.dakuohao.test;

@Name("父类名称")
public class Fu {

    protected String getSimpleName() {
        return this.getClass().getSimpleName();
    }

    protected String getName() {
        return this.getClass().getAnnotation(Name.class).value();
    }

    public static void main(String[] args) {
        Fu obj = new Fu();
        System.out.println(obj.getSimpleName());
        System.out.println(obj.getName());
    }
}
