# db
Java DataBase Utils

最简单高效的数据库操作工具

# 为什么重复造轮子？
因为在开源界没发现小巧、简单、高效的数据库操作工具，
很多时候我需要的是一个工具，而不是一个框架，Java操作数据库远没有动态语言（比如Python、nodejs）
方便快捷，要写大量的jdbc代码，我需要一个工具来帮我简化操作。

MyBatis太笨了，JPA太重了，数据库操作应该是清爽简介的，而不应该代码一大坨，
一大坨的。

数据库操作工具最重要的应该是：写最少的代码实现增删改查等功能！


# 好用的数据库操作工具应该是怎样的？
- 必须是极致高效的。Java中最高效就是JDBC了，工具应该直接调用JDBC，而不要过度封装；
而且为了极致性能，我丢弃了反射（虽然可能JDK对反射的优化已经很高了）

- 必须是可扩展的。这里采用面向接口（interface）编程，多态去实现。

- 是否需要ORM(对象关系映射)让用户绝定，想用就用，不想用不强求。

- 工具类应该是方法优于对象的，静态方法直接引用，而不是让开发者new一个对象再使用。
同时实现ORM和Record方式。

- 必须是及其简单的。工具类要做到不看文档，拿来就能用。

- 关系型数据库应该纯SQL操作，因为规范就是SQL，你是绕不过的，市面上去用Java代码模拟改造SQL
的做法都是很蹩脚的，也是极度浪费运行性能的。

- 能用纯Java代码实现的尽量Java代码实现，SQL如果存放应该放到.sql文件，
至少编辑器打开能智能标识、提示，而不是放到XML那种反人类的、不友好的展现方式。

- 工具必须提供一种动态SQL的实现方式，使用方式尽可能的优雅。

- 事务，缓存，sql过滤，防止注入等其他功能应该是插件式、开关式的，
用着打开，不用则无需处理，不要暴露给用户太多选择，用户开箱即用最好。
不要配置，不要了解原理，不要让用户有选择（选择太多往往会造成困难）。

# 怎么使用？

假设创建一个用户表，如下：
```sql
-- 创建 test 的数据库结构
CREATE DATABASE IF NOT EXISTS `test` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `test`;
-- 创建  表 test.user 结构
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `deleted` tinyint(4) DEFAULT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表，用作测试';

```
[sql源码在 sql.sql文件](sql.sql)

对应表创建

# 站在巨人的肩膀上
本工具得益于开源社区，依赖 [Hutool](https://www.hutool.cn/)工具类。

Hutool是一个很好用的Java工具类，本项目使用其常用工具类代替自己的utils工具类，
以达到简化代码的目的。

对于已经是Hutool老用户们：

本工具只在Hutool基础上做增强扩展，跟Hutool不冲突，完全兼容，互不影响，你可以理解为 Hutool-Db-Plus

# 高级使用
- [复习JDBC，查看JDBC详解](jdbc.md)
- 源码注释写的很详细，下载你肯定看的懂，按你心情修改，不多说了
- 希望有时间的大佬，补充其他关系型数据的语法实现，比如Oracle，SQL Server等等



