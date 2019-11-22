package com.github.dakuohao.test;

import cn.hutool.db.Db;

import java.sql.SQLException;

@Name("子类名称")
public class Zi implements FuInterface {
    public static void main(String[] args) {
        FuInterface obj = new Zi();
        System.out.println(obj.getSimpleName());
        System.out.println(obj.getName());


    }

    public  void  test() throws SQLException {
        Db.use().query("",this.getClass(),"");
    }
}
