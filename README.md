# SQL工具类

**sql是一个极致的数据库操作工具，极致简洁、快速、高效、稳定。**<br />**永久开源免费！**
<a name="I95PZ"></a>
# 为什么重复造轮子？
因为市面上没发现有很好用的Java操作数据库的工具或框架.Mybatis太繁琐，在XML里写动态SQL太蠢且复杂。JPA太重，学习成本也过高，复杂查询使用极复杂，效率低下。

Github搜索一圈，没有我想要的工具，so……自己写。
<a name="51ATI"></a>
# 源码地址
**github：**[https://github.com/dakuohao/sql](https://github.com/dakuohao/sql)<br />**gitee:**  [https://gitee.com/lovepeng/sql](https://gitee.com/lovepeng/sql)

使用依赖：[https://www.hutool.cn/](https://www.hutool.cn/)
<a name="1LvFE"></a>
# 我想要的是什么？

- 极致高效
  - Java中最快的应该是JDBC了，那就基于JDBC封装，为了效率我甚至尽量不使用或少使用**反射**（虽然JDK已经把反射优化的很好了）。
- 极致稳定
  - 提供100%的单元测试，代码要规范，注释要全面，保证代码的健壮性就是保证工具的稳定性。
- 极致简单
  - 开发者开箱即用，不用看文档，看代码就知道怎么用。是一个工具类而不是框架，引入即用，设计思想是**静态方法**大于**对象**，**面向接口**大于**面向对象**，开发者直接使用（而不需要先new一个对象再使用）
- 支持ORM（对象关系映射）
  - [ORM（对象关系映射）](https://baike.baidu.com/item/%E5%AF%B9%E8%B1%A1%E5%85%B3%E7%B3%BB%E6%98%A0%E5%B0%84/311152?fromtitle=ORM&fromid=3583252)是Java开发必备了，就是把数据库中一条数据映射为一个Bean对象，一个数据库表映射为一个List<Bean>对象
- 支持ActiveRecord模式
  - [ActiveRecord](https://zh.wikipedia.org/wiki/Active_Record)思想：就是把数据库中一条数据映射为一个Map<String,Object>对象。一个数据库表映射为一个List<Map<String,Object>>。由于Map写着费劲，且get对象需要类型转换，所以我封装了一个Entity(本质就是一个LinkHashMap<String,Object>）来代替Map<String,Object>，方便使用。
- 应该以SQL为基准，并提供一种优雅的动态SQL实现
  - 关系型数据库是绕不过SQL的，那就是规范，而且Web开发种SQL都极其复杂，所以以SQL为基准是必须的，不需要用Java去模拟一种蹩脚的SQL实现。
- 好的架构设计，好的扩展性，（提供开关式插件）   
  - 便于拔插像缓存、事物、sql过滤等其他特性功能

<a name="W9m2j"></a>
# 如何使用？
**1.添加配置文件：**Maven项目中在`src/main/resources`目录下添加`db.setting`文件（非Maven项目添加到ClassPath中即可）：
```
## db.setting文件
driver = com.mysql.cj.jdbc.Driver
url = jdbc:mysql://127.0.0.1:3306/test?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=GMT
user = root
pass = root

## 可选配置
# 是否在日志中显示执行的SQL
showSql = true
# 是否格式化显示的SQL
formatSql = false
# 是否显示SQL参数
showParams = true
# 打印SQL的日志等级，默认debug，可以是info、warn、error
sqlLevel = debug

#### 关于多数据源配置，后边有说明
```

2. **然后引入MySQL JDBC驱动jar**
```
<!--mysql数据库驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>${mysql.version}</version>
</dependency>
```

3. 该代码还未提交Maven仓库，近期提交，暂时您需要下载源码使用

备注说明：本工具底层使用[Hutool工具类](https://www.hutool.cn/docs/#/)（Hutool是一个工具类集合，用来代替你项目中的utils包，很方便）。hutool本身提供了多种数据库连接池支持，故本工具的使用跟hutool类似，只需要添加一个数据库配置文件即可，就这么简单。**本工具只对Hutool做增强，不做修改，完全兼容**，故hutool的老用户可以放心大胆的引入，不会冲突。
<a name="o33Q3"></a>
# 准备一个数据库做测试

1. 创建一个用户表做测试。这里遵循阿里巴巴开发规范，每个表创建都至少有id，create_time，update_time,deleted 四个字段，且命名方式使用下划线格式。

**<br />![image.png](https://cdn.nlark.com/yuque/0/2019/png/251474/1574519132952-c0e5ceda-0c98-42c0-856c-eeddec0c8d19.png#align=left&display=inline&height=250&name=image.png&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&originHeight=376&originWidth=1124&search=%E5%9F%BA%E6%9C%AC%20%E5%88%86%E5%8C%BA%E5%B0%8F%29CREATE%E4%BB%A3%E7%A0%81%E5%B0%8FALTER%E4%BB%A3%E7%A0%81%20%E7%B4%A2%E5%BC%95%20%E9%80%89%E9%A1%B9%20%E5%A4%96%E9%94%AE%20%E5%90%8D%E7%A7%B0%3A%20user%20%E7%94%A8%E6%88%B7%E8%A1%A8%2C%E7%94%A8%E4%BD%9C%E6%B5%8B%E8%AF%95%20%E6%B3%A8%E9%87%8A%3A%20%E5%AE%81%E6%AE%B5%3A%20%E6%B7%BB%E5%8A%A0%20%E5%88%A0%E9%99%A4%20%E5%90%91%E4%B8%8A%20%E5%90%91%E4%B8%8B%20%E6%97%A0%E7%89%B9%E5%8F%B7%E7%9A%84%E5%85%81%E8%AE%B8NU...%E5%A1%AB%E9%9B%B6%20%E9%95%BF%E5%BA%A6%2F%E8%AE%BE%E7%BD%AE%20%E9%BB%98%E8%AE%A4%20%E5%90%8D%E7%A7%B0%20%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%20%E6%B3%A8%E9%87%8A%20INT%2010%20%E5%8F%A3%E5%9B%9E%E5%9B%9E%E5%9B%9E%E5%9B%9E%E5%9B%9E%20id%E4%B8%BB%E9%94%AE%20id%20%E5%8F%A3%20AUTOINCREMENT%20%E5%8F%A3%20%E5%A7%93%E5%90%8D%20VARCHAR%2050%20NULL%20name%20%20%E5%B9%B4%E9%BE%84%20INT%2011%20NULL%20age%20%E5%8F%A3%20%E5%88%9B%E5%BB%BA%E6%97%B6%E9%97%B4%20TIMESTAMP%20CURRENTTIMESTAMP%20createtime%20DATETIME%20%E4%BF%AE%E6%94%B9%E6%97%B6%E9%97%B4%20updatetime%20CURRENTTIMESTAM...%20%E6%98%AF%E5%90%A6%E5%88%A0%E9%99%A41%E6%98%AF0%E9%A6%99%20deleted%20TINYINT&size=58754&status=done&width=746)

```sql
-- --------------------------------------------------------
-- 主机:                 127.0.0.1
-- 服务器版本:            8.0.17 - MySQL Community Server - GPL
-- 服务器操作系统:        Win64
-- 默认字符集：          utf8mb4（由于历史原因，这个才是mysql真正的utf-8字符集）
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci 
-- --------------------------------------------------------

--  test 的数据库结构
CREATE DATABASE IF NOT EXISTS `test` 

--  表 test.user 结构
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除 1是 0 否',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表，用作测试';

```

2. 创建一个JavaBean，与数据库表对应。

```java
//类名对应数据库表名，表示类和表绑定
//类命名方式使用驼峰命名，表命名使用下划线命名，工具类会自动把驼峰命名转下划线命名，这里注意。
// 如果数据库表名和类名不一致，使用注解来标识：@Table(value = "表名称",dataSource = "数据源名称")，
//@Table("user")

@Data //lombok插件的注解，帮你生成get，set等方法，推荐使用
public class User implements DataBase {
    private Integer id;
    private String name;
    private Integer age;
    private LocalDate createTime;
    private LocalDate createTime1;
    private LocalDate createTime2;
    private LocalDate updateTime;
    private Boolean deleted;
}
```

- 这里使用[lombok](https://projectlombok.org/)插件，方便省略get，set方法，建议使用
- JDK8+以上版本，推荐使用LocalDate时间类型。
- **类名对应数据库表名，表示类和表绑定，类命名方式使用驼峰命名，表命名使用下划线命名，工具类会自动把驼峰命名转下划线命名，这里注意。**如果数据库表名和类名不一致，使用注解来标识：@Table**(**value = **"表名称"**,dataSource = **"数据源名称")**
- 实现DataBase接口即可，代表赋予User操作DataBase的能力。

只要你的Bean类implements DataBase就可以了，无其他操作，就可以使用我的工具类了，就这么简单，接下来开始使用吧。
<a name="GcPkh"></a>
# 执行SQL，复杂SQL，动态SQL

**需求：我就想自己写个SQL，然后直接执行就行了。**

<a name="t59bC"></a>
## 执行修改操作的SQL

- Sql.sql(sql).execute(); //创建一个sql语句并执行，执行返回影响的数据行数

```java
//执行修改操作的sql语句
String sql = "INSERT INTO `test`.`user` (`name`, `age`) VALUES ('张三', '18')";
int result = Sql.sql(sql).execute();
assert result == 1;

//执行带参数的sql
//变量用${}或#{}表示
//${} 为预编译sql，可以有效的防止sql注入
//#{} 直接拼接参数，无法防止sql注入
User user = new User();
user.setName("张三");
user.setAge(18);
String sql = "INSERT INTO `test`.`user` (`name`, `age`) VALUES (${name},${age})";
int result = Sql.sql(sql).setParams(user).execute();
assert result == 1;

Entity user = Entity.create()
    .set("name", "张三")
    .set("age", 16);
String sql = "INSERT INTO `test`.`user` (`name`, `age`) VALUES (${name},${age})";
int result = Sql.sql(sql).setParams(user).execute();
assert result == 1;
```
**注意:**

- **变量用${}或#{}表示**
  - **${} 为预编译sql，可以有效的防止sql注入**
  - **#{} 直接拼接参数，无法防止sql注入**
- **execute()方法会根据sql只能判断是执行查询操作还是修改操作，当然你也可以手工指定查询操作executeQuery()或修改操作executeUpdate()**
<a name="y2vdw"></a>
## 执行查询

- Sql.sql(sql).setParams(user)execute(); //创建sql，然后设置查询参数，然后执行，执行返回查询的数据列表

```java
//多条件查询参数，orm方式
// 这个在开发中会由mvc框架反射前端传来的json自动生成
User user = new User();
user.setName("张三" + "%");
user.setAge(18);

List<User> list = Sql.sql("SELECT * FROM `user` WHERE `name` LIKE #{name} AND age >${age}")
    .setParams(user)
    .execute(); // execute方法会智能判断是查询sql还是修改sql
//当然你也可以手动指定 调用查询sql 如下方式
//.executeQuery();
//输出[SQL] : SELECT * FROM `user`  WHERE  `name` LIKE '张三%' AND age >18

//多条件查询参数，activeRecord方式
// 这个在开发中会由mvc框架反射前端传来的json自动生成
Entity params = Entity.create()
    .set("name", "张三" + "%") //%表示模糊查询
    .set("age", 16);

List<Entity> list = Sql.sql("SELECT * FROM `user` WHERE `name` LIKE #{name} AND age >${age}")
    .setParams(params)
    .execute(); // execute方法会智能判断是查询sql还是修改sql
//当然你也可以手动指定 调用查询sql 如下方式
//.executeQuery();
//[SQL] : SELECT * FROM `user`  WHERE  `name` LIKE '张三%' AND age >16

```

<a name="uwQYj"></a>
## 执行复杂SQL或动态SQL

- append(String sql）方法表示追加sql
- append(boolean isnull，String sql) 表示isnull为true时，拼接sql
```java
//多条件查询参数
// 这个在开发中会由mvc框架反射前端传来的json自动生成
Entity params = Entity.create()
    .set("name", "张三" + "%") //%表示模糊查询
    .set("age", 16)
    //时间格式支持LocalDateTime  JDK8以上推荐使用这个
    .set("createTime1", LocalDateTime.parse("2019-11-22T15:11:45"))
    .set("createTime2", LocalDateTime.parse("2019-12-23T20:30:29"));

//时间格式支持字符串
//                .set("createTime1", "2019-11-22T15:11:45")
//                .set("createTime2","2019-11-22T15:11:45");

//时间格式支持Date对象
//                .set("createTime1", new Date())
//                .set("createTime2", new Date());

List<Entity> list = Sql.sql("SELECT * FROM `user` WHERE")
    .append(StrUtil.isNotEmpty(params.getStr("name")), "and `name` LIKE #{name}")
    .append(params.getInt("age") > 0, "AND age >${age}")
    .append(StrUtil.isNotEmpty(params.getStr("createTime1"))
            && StrUtil.isNotEmpty(params.getStr("createTime2")),
            "AND create_time BETWEEN ${createTime1} AND ${createTime2}")
    .setParams(params)
    .execute(); // execute方法会智能判断是查询sql还是修改sql
//当然你也可以手动指定 调用查询sql 如下方式
//                .executeQuery();
//输出[SQL] : SELECT * FROM `user`  WHERE   `name` LIKE '张三%' AND age >16 
AND create_time BETWEEN '2019-11-22T15:11:45' AND '2019-12-23T20:30:29'
```

<a name="WwPjs"></a>
## 分页查询

```java
// 查询参数,这个在开发中会由mvc框架反射前端传来的json自动生成
Entity params = Entity.create()
    .set("name", "张三" + "%") //%表示模糊查询
    .set("age", 16);
//分页参数 第一页，页面数据10条 排序字段： order by name asc,age desc
Page page = new Page(1, 10, "name asc,age desc");

Page result = Sql.sql("SELECT * FROM `user` WHERE")
    .append(StrUtil.isNotEmpty(params.getStr("name")), "and `name` LIKE #{name}")
    .append(params.getInt("age") > 0, "AND age >${age}")
    .append(StrUtil.isNotEmpty(params.getStr("createTime1"))
            && StrUtil.isNotEmpty(params.getStr("createTime2")),
            "AND create_time BETWEEN ${createTime1} AND ${createTime2}")
    .setParams(params)
    //                .page(page);
    //或者直接传分页参数 等同于 page(page)方法
    .page(1, 10, "name asc,age desc");

// [SQL] : SELECT COUNT(*) FROM (SELECT * FROM `user`  WHERE   `name` LIKE '张三%' AND age >16 ) temp
//[SQL] : SELECT * FROM `user`  WHERE   `name` LIKE '张三%' AND age >16 ORDER BY name asc,age desc LIMIT 0,10

System.out.println(JSONUtil.toJsonStr(result));
```

<a name="25SX2"></a>
# 单表增删改查操作
**需求：开发中90%以上时间都是对单笔进行curd操作，必须简化这部分代码。**

<a name="P7jqo"></a>
## 添加并返回自增主键

- user.insert()；
-  Sql.sql().insert(entity);
```java
//模拟数据
User user = new User();
user.setName("张三");
user.setAge(20);

Boolean insert = user.insert();
assert insert;
//数据库设置自增主键的，插入成功会自动把id字段设置为主键值
//[执行SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三', 20)

System.out.println(JSONUtil.toJsonStr(user));
//{"name":"张三","id":27,"age":20}
```

```java
//模拟数据
Entity entity = Entity.create("user")
    .set("name", "张三")
    .set("age", 20);

Boolean insert = Sql.sql().insert(entity);
assert insert;
//数据库设置自增主键的，插入成功会自动把id字段设置为主键值
//[执行SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三', 20)

System.out.println(entity);
//{tableName=`user`, fieldNames=null, fields={name=张三, age=20, id=28}}

```

<a name="twpB3"></a>
## 批量插入数据

- Sql.sql().insertBatch(List<Bean>);
- Sql.sql().insertBatch(List<Entity>);
- **Sql._sql_****()****.insert****(****List<Bean>****)****;**
- **Sql.****_sql_****()****.insert****(****List<Entity>****);**
- insertBatch()方法：批量提交插入，速度快，但是不返回主键
- insert()方法：一条一条循环插入，速度慢，会返回自增主键
- 都是原子性操作，要么都插入成功，要么都插入失败。
```java
//模拟数据
User u1 = new User();
u1.setName("张三001");
u1.setAge(20);

User u2 = new User();
u2.setName("张三002");
u2.setAge(22);

List<User> list = new ArrayList<>();
list.add(u1);
list.add(u2);

//批次插入，快速
Boolean insert = Sql.sql().insertBatch(list);
assert insert;
//[Batch SQL] -> INSERT INTO `user` (`name`, `age`) VALUES (?, ?)
System.out.println(JSONUtil.toJsonStr(list));
//[{"name":"张三001","age":20},{"name":"张三002","age":22}]

//循环插入，慢速
Boolean insert = Sql.sql().insert(list);
assert insert;
//[SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三001', 20)
//[SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三002', 22)
System.out.println(JSONUtil.toJsonStr(list));
//[{"name":"张三001","id":61,"age":20},{"name":"张三002","id":62,"age":22}]


```

```java
 //模拟数据
Entity e1 = Entity.create("user")
    .set("name", "张三001")
    .set("age", 20);

Entity e2 = new Entity("user")
    .set("name", "张三002")
    .set("age", 22);

List<Entity> list = new ArrayList<>();
list.add(e1);
list.add(e2);

//批次插入，快速
Boolean insert = Sql.sql().insertBatch(list);
assert insert;
//[Batch SQL] -> INSERT INTO `user` (`name`, `age`) VALUES (?, ?)
System.out.println(JSONUtil.toJsonStr(list));
//[{"name":"张三001","age":20},{"name":"张三002","age":22}]

//循环插入，慢速
Boolean insert = Sql.sql().insert(list);
assert insert;
//[SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三001', 20)
//[SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('张三002', 22)

System.out.println(JSONUtil.toJsonStr(list));
//[{"name":"张三001","id":55,"age":20},{"name":"张三002","id":56,"age":22}]
```


<a name="Ve579"></a>
## 删除数据
```java
//通过主键id删除
Boolean delete = Sql.sql().deleteById("user", 2);
//[执行SQL] : DELETE FROM user WHERE id =2

User user = new User();
user.setId(1);
user.deleteById();
//[执行SQL] : DELETE FROM user WHERE id =1

//批量删除
Boolean delete = Sql.sql().deleteByIds("user", 1, 2, 3);
//[SQL] : DELETE FROM user WHERE id =?

Boolean delete = new User().deleteByIds(4,5,6,7);
//[SQL] : DELETE FROM user WHERE id =?

//执行where条件删除
User user = new User();
user.setId(10);
user.setAge(60);
Sql where = Sql.sql("delete from user where id<${id} and age <${age}")
    .setParams(user);

Boolean delete = Sql.sql().delete( where);
//[SQL] : delete from user  WHERE  id<10 and age <60

```

<a name="8rMDQ"></a>
## 修改数据

```java
////通过id修改数据，为空的字段不修改，只修改不为空的字段
User user = new User();
user.setId(11);
user.setName("测试");
Boolean update = user.updateById();
assert update;
//[SQL] : UPDATE `user` SET `name` = '测试'  WHERE `id` = 11

Entity entity = Entity.create("user")
    .set("id", 11)
    .set("name", "测试");

Boolean update = Sql.sql().updateById(entity);
assert update;
//[SQL] : UPDATE `user` SET `name` = '测试'  WHERE `id` = 11

////批量修改，循环一条一条执行修改，效率慢
User u1 = new User();
u1.setId(11);
u1.setName("测试");

User u2 = new User();
u2.setId(12);
u2.setName("测试");

List<User> list = new ArrayList<>();
list.add(u1);
list.add(u2);

Boolean update = Sql.sql().update(list);
//[SQL] : UPDATE `user` SET `name` = '测试'  WHERE `id` = 11
//[SQL] : UPDATE `user` SET `name` = '测试'  WHERE `id` = 12

Entity e1 = Entity.create("user")
    .set("id", 11)
    .set("name", "测试");

Entity e2 = Entity.create("user")
    .set("id", 12)
    .set("name", "测试");

List<Entity> list = new ArrayList<>();
list.add(e1);
list.add(e2);

Boolean update = Sql.sql().update(list);
//[SQL] : UPDATE `user` SET `name` = '测试'  WHERE `id` = 11
//[SQL] : UPDATE `user` SET `name` = '测试'  WHERE `id` = 12
```
<a name="PmBHa"></a>
## 插入或更新数据
根据id查询，数据存在则更新，不存在则插入。

```java
Entity entity = Entity.create("user")
    .set("id", 86)
    .set("name", "测试001")
    .set("age", 45);
Boolean result = Sql.sql().insertOrUpdate(entity);
assert result;
//第一次执行 不存在则执行插入
//[SQL] : SELECT COUNT(*) FROM user WHERE id =86
//[SQL] : INSERT INTO `user` (`id`, `name`, `age`) VALUES (86, '测试001', 45)
//第二次执行 存在则修改
//[SQL] : SELECT COUNT(*) FROM user WHERE id =86
//[SQL] : UPDATE `user` SET `name` = '测试001' , `age` = 45  WHERE `id` = 86


Entity entity = Entity.create("user")
    .set("name", "测试001")
    .set("age", 45);
Boolean result = Sql.sql().insertOrUpdate(entity);
assert result;
//[SQL] : INSERT INTO `user` (`name`, `age`) VALUES ('测试001', 45)
System.out.println(JSONUtil.toJsonStr(entity));
//{"name":"测试001","age":45,"id":89}



User user = new User();
user.setId(100);
user.setName("测试");
user.setAge(56);
Boolean result = user.insertOrUpdate();
assert result;
System.out.println(JSONUtil.toJsonStr(user));
//第一次执行 不存在则执行插入
//[SQL] : SELECT COUNT(*) FROM `user` WHERE id =100
//[SQL] : INSERT INTO `user` (`id`, `name`, `age`) VALUES (100, '测试', 56)

//第二次执行 存在则修改
//[SQL] : SELECT COUNT(*) FROM `user` WHERE id =100
//[SQL] : UPDATE `user` SET `name` = '测试' , `age` = 56  WHERE `id` = 100
//{"name":"测试","id":100,"age":56}
```
<a name="PUXLA"></a>
## 通过id查询

```java
Entity entity = Sql.sql().selectById("user", 30);
System.out.println(JSONUtil.toJsonStr(entity));
//[SQL] : SELECT * FROM user WHERE `id`=30

User user = new User().selectById(30);
System.out.println(JSONUtil.toJsonStr(user));
//[SQL] : SELECT * FROM user WHERE `id`=30
```

<a name="GslPF"></a>
# 事务

```java
    @Test
    void transaction() {
        Sql.sql().transaction(parameter -> {
            User user = new User();
            user.setId(100);
            user.setName("测试xxxxxxxxx");
            user.updateById();
            throw new RuntimeException("测试事务异常");
            //[SQL] : UPDATE `user` SET `name` = '测试xxxxxxxxx'  WHERE `id` = 100
            //java.sql.SQLException: java.lang.RuntimeException: 测试事务异常
        });
    }

    //等同于上边写法 适合jdk8一下使用
    @Test
    void transaction1() {
        Sql.sql().transaction(
                new VoidFunc1<Db>() {
                    @Override
                    public void call(Db parameter) throws Exception {
                        User user = new User();
                        user.setId(100);
                        user.setName("测试xxxxxxxxx");
                        user.updateById();
                        throw new RuntimeException("测试事务异常");
                        //[SQL] : UPDATE `user` SET `name` = '测试xxxxxxxxx'  WHERE `id` = 100
                        //java.sql.SQLException: java.lang.RuntimeException: 测试事务异常
                    }
                });
    }


    //指定事务级别
    @Test
    void transactionLevel() {
        Sql.sql().transaction(READ_UNCOMMITTED, parameter -> {
            User user = new User();
            user.setId(100);
            user.setName("测试xxxxxxxxx");
            user.updateById();
            throw new RuntimeException("测试事务异常");
            //[SQL] : UPDATE `user` SET `name` = '测试xxxxxxxxx'  WHERE `id` = 100
            //java.sql.SQLException: java.lang.RuntimeException: 测试事务异常
        });
    }

```
**说明：**

- **跟Spring集成，可以使用Spring的数据源，进而事务操作交给Spring处理即可(@Transactional )**
- **事务级别如下**

```java

/**
 * 事务级别枚举
 * 
 * <p>
 * <b>脏读（Dirty Read）</b>：<br>
 * 一个事务会读到另一个事务更新后但未提交的数据，如果另一个事务回滚，那么当前事务读到的数据就是脏数据
 * <p>
 * <b>不可重复读（Non Repeatable Read）</b>：<br>
 * 在一个事务内，多次读同一数据，在这个事务还没有结束时，如果另一个事务恰好修改了这个数据，那么，在第一个事务中，两次读取的数据就可能不一致
 * <p>
 * <b>幻读（Phantom Read）</b>：<br>
 * 在一个事务中，第一次查询某条记录，发现没有，但是，当试图更新这条不存在的记录时，竟然能成功，且可以再次读取同一条记录。
 * 
 * @see Connection#TRANSACTION_NONE
 * @see Connection#TRANSACTION_READ_UNCOMMITTED
 * @see Connection#TRANSACTION_READ_COMMITTED
 * @see Connection#TRANSACTION_REPEATABLE_READ
 * @see Connection#TRANSACTION_SERIALIZABLE
 * @author looly
 * @since 4.1.2
 */
public enum TransactionLevel {
	/** 驱动不支持事务 */
	NONE(Connection.TRANSACTION_NONE),

	/**
	 * 允许脏读、不可重复读和幻读
	 * <p>
	 * 在这种隔离级别下，一个事务会读到另一个事务更新后但未提交的数据，如果另一个事务回滚，那么当前事务读到的数据就是脏数据，这就是脏读（Dirty Read）
	 */
	READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),

	/**
	 * 禁止脏读，但允许不可重复读和幻读
	 * <p>
	 * 此级别下，一个事务可能会遇到不可重复读（Non Repeatable Read）的问题。<br>
	 * 不可重复读是指，在一个事务内，多次读同一数据，在这个事务还没有结束时，如果另一个事务恰好修改了这个数据，那么，在第一个事务中，两次读取的数据就可能不一致。
	 */
	READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),

	/**
	 * 禁止脏读和不可重复读，但允许幻读，MySQL的InnoDB引擎默认使用此隔离级别。
	 * <p>
	 * 此级别下，一个事务可能会遇到幻读（Phantom Read）的问题。<br>
	 * 幻读是指，在一个事务中，第一次查询某条记录，发现没有，但是，当试图更新这条不存在的记录时，可以成功，且可以再次读取同一条记录。
	 */
	REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),

	/**
	 * 禁止脏读、不可重复读和幻读
	 * <p>
	 * 虽然Serializable隔离级别下的事务具有最高的安全性，但是，由于事务是串行执行，所以效率会大大下降，应用程序的性能会急剧降低。
	 */
	SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

	/** 事务级别，对应Connection中的常量值 */
	private int level;

	TransactionLevel(int level) {
		this.level = level;
	}

	/**
	 * 获取数据库事务级别int值
	 * 
	 * @return 数据库事务级别int值
	 */
	public int getLevel() {
		return this.level;
	}
}

```
<a name="TnTXD"></a>
# 多数据源和适配各种数据库连接池
这里使用Hutool已经封装的很好了，我拿来主义：
<a name="h7IQY"></a>
## 多数据源

```java
# suppress inspection "Annotator" for whole file
#===================================================================
# 数据库配置文件样例
# DsFactory默认读取的配置文件是config/db.setting
# db.setting的配置包括两部分：基本连接信息和连接池配置信息。
# 基本连接信息所有连接池都支持，连接池配置信息根据不同的连接池，连接池配置是根据连接池相应的配置项移植而来
#===================================================================

## 打印SQL的配置
# 是否在日志中显示执行的SQL，默认false
showSql = true
# 是否格式化显示的SQL，默认false
formatSql = true
# 是否显示SQL参数，默认false
showParams = true
# 打印SQL的日志等级，默认debug
sqlLevel = debug

# 默认数据源
url = jdbc:sqlite:test.db


# 测试数据源
[test]
url = jdbc:sqlite:test.db

# 测试用HSQLDB数据库
[hsqldb]
url = jdbc:hsqldb:mem:mem_hutool
user = SA
pass = 

# 测试用Oracle数据库
[orcl]
url = jdbc:oracle:thin:@//looly.centos:1521/XE
user = looly
pass = 123456

[mysql]
url = jdbc:mysql://looly.centos:3306/test_hutool?useSSL=false
user = root
pass = 123456

[postgre]
url = jdbc:postgresql://looly.centos:5432/test_hutool
user = postgres
pass = 123456

[sqlserver]
url = jdbc:sqlserver://looly.database.chinacloudapi.cn:1433;database=test;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.chinacloudapi.cn;loginTimeout=30;
user = looly@looly
pass = 123
```
<a name="nUiV3"></a>
## 各种常见数据库连接池配置
<a name="null"></a>
## [基本配置样例](https://www.hutool.cn/docs/#/db/%E6%95%B0%E6%8D%AE%E6%BA%90%E9%85%8D%E7%BD%AEdb.setting%E6%A0%B7%E4%BE%8B?id=%e5%9f%ba%e6%9c%ac%e9%85%8d%e7%bd%ae%e6%a0%b7%e4%be%8b)
```
#------------------------------------------------------------------------------------------
## 基本配置信息
# JDBC URL，根据不同的数据库，使用相应的JDBC连接字符串
url = jdbc:mysql://<host>:<port>/<database_name>
# 用户名，此处也可以使用 user 代替
username = 用户名
# 密码，此处也可以使用 pass 代替
password = 密码
# JDBC驱动名，可选（Hutool会自动识别）
driver = com.mysql.jdbc.Driver
## 可选配置
# 是否在日志中显示执行的SQL
showSql = true
# 是否格式化显示的SQL
formatSql = false
# 是否显示SQL参数
showParams = true
# 打印SQL的日志等级，默认debug
sqlLevel = debug
#------------------------------------------------------------------------------------------Copy to clipboardErrorCopied
```
<a name="hikaricp"></a>
## [HikariCP](https://www.hutool.cn/docs/#/db/%E6%95%B0%E6%8D%AE%E6%BA%90%E9%85%8D%E7%BD%AEdb.setting%E6%A0%B7%E4%BE%8B?id=hikaricp)
```
## 连接池配置项
# 自动提交
autoCommit = true
# 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
connectionTimeout = 30000
# 一个连接idle状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
idleTimeout = 600000
# 一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired），缺省:30分钟，建议设置比数据库超时时长少30秒，参考MySQL wait_timeout参数（show variables like '%timeout%';）
maxLifetime = 1800000
# 获取连接前的测试SQL
connectionTestQuery = SELECT 1
# 最小闲置连接数
minimumIdle = 10
# 连接池中允许的最大连接数。缺省值：10；推荐的公式：((core_count * 2) + effective_spindle_count)
maximumPoolSize = 10
# 连接只读数据库时配置为true， 保证安全
readOnly = falseCopy to clipboardErrorCopied
```
<a name="druid"></a>
## [Druid](https://www.hutool.cn/docs/#/db/%E6%95%B0%E6%8D%AE%E6%BA%90%E9%85%8D%E7%BD%AEdb.setting%E6%A0%B7%E4%BE%8B?id=druid)
```
# 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
initialSize = 0
# 最大连接池数量
maxActive = 8
# 最小连接池数量
minIdle = 0
# 获取连接时最大等待时间，单位毫秒。配置了maxWait之后， 缺省启用公平锁，并发效率会有所下降， 如果需要可以通过配置useUnfairLock属性为true使用非公平锁。
maxWait = 0
# 是否缓存preparedStatement，也就是PSCache。 PSCache对支持游标的数据库性能提升巨大，比如说oracle。 在mysql5.5以下的版本中没有PSCache功能，建议关闭掉。作者在5.5版本中使用PSCache，通过监控界面发现PSCache有缓存命中率记录， 该应该是支持PSCache。
poolPreparedStatements = false
# 要启用PSCache，必须配置大于0，当大于0时， poolPreparedStatements自动触发修改为true。 在Druid中，不会存在Oracle下PSCache占用内存过多的问题， 可以把这个数值配置大一些，比如说100
maxOpenPreparedStatements = -1
# 用来检测连接是否有效的sql，要求是一个查询语句。 如果validationQuery为null，testOnBorrow、testOnReturn、 testWhileIdle都不会其作用。
validationQuery = SELECT 1
# 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
testOnBorrow = true
# 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
testOnReturn = false
# 建议配置为true，不影响性能，并且保证安全性。 申请连接的时候检测，如果空闲时间大于 timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
testWhileIdle = false
# 有两个含义： 1) Destroy线程会检测连接的间隔时间 2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明
timeBetweenEvictionRunsMillis = 60000
# 物理连接初始化的时候执行的sql
connectionInitSqls = SELECT 1
# 属性类型是字符串，通过别名的方式配置扩展插件， 常用的插件有： 监控统计用的filter:stat  日志用的filter:log4j 防御sql注入的filter:wall
filters = stat
# 类型是List<com.alibaba.druid.filter.Filter>， 如果同时配置了filters和proxyFilters， 是组合关系，并非替换关系
proxyFilters = Copy to clipboardErrorCopied
```
<a name="tomcat-jdbc-pool"></a>
## [Tomcat JDBC Pool](https://www.hutool.cn/docs/#/db/%E6%95%B0%E6%8D%AE%E6%BA%90%E9%85%8D%E7%BD%AEdb.setting%E6%A0%B7%E4%BE%8B?id=tomcat-jdbc-pool)
```
# (boolean) 连接池创建的连接的默认的auto-commit 状态
defaultAutoCommit = true
# (boolean) 连接池创建的连接的默认的read-only 状态。 如果没有设置则setReadOnly 方法将不会被调用。 ( 某些驱动不支持只读模式， 比如：Informix)
defaultReadOnly = false
# (String) 连接池创建的连接的默认的TransactionIsolation 状态。 下面列表当中的某一个： ( 参考javadoc) NONE READ_COMMITTED EAD_UNCOMMITTED REPEATABLE_READ SERIALIZABLE
defaultTransactionIsolation = NONE
# (int) 初始化连接： 连接池启动时创建的初始化连接数量，1。2 版本后支持
initialSize = 10
# (int) 最大活动连接： 连接池在同一时间能够分配的最大活动连接的数量， 如果设置为非正数则表示不限制
maxActive = 100
# (int) 最大空闲连接： 连接池中容许保持空闲状态的最大连接数量， 超过的空闲连接将被释放， 如果设置为负数表示不限制 如果启用，将定期检查限制连接，如果空闲时间超过minEvictableIdleTimeMillis 则释放连接 （ 参考testWhileIdle ）
maxIdle = 8
# (int) 最小空闲连接： 连接池中容许保持空闲状态的最小连接数量， 低于这个数量将创建新的连接， 如果设置为0 则不创建 如果连接验证失败将缩小这个值（ 参考testWhileIdle ）
minIdle = 0
# (int) 最大等待时间： 当没有可用连接时， 连接池等待连接被归还的最大时间( 以毫秒计数)， 超过时间则抛出异常， 如果设置为-1 表示无限等待
maxWait = 30000
# (String) SQL 查询， 用来验证从连接池取出的连接， 在将连接返回给调用者之前。 如果指定， 则查询必须是一个SQL SELECT 并且必须返回至少一行记录 查询不必返回记录，但这样将不能抛出SQL异常
validationQuery = SELECT 1
# (boolean) 指明是否在从池中取出连接前进行检验， 如果检验失败， 则从池中去除连接并尝试取出另一个。注意： 设置为true 后如果要生效，validationQuery 参数必须设置为非空字符串 参考validationInterval以获得更有效的验证
testOnBorrow = false
# (boolean) 指明是否在归还到池中前进行检验 注意： 设置为true 后如果要生效，validationQuery 参数必须设置为非空字符串
testOnReturn = false
# (boolean) 指明连接是否被空闲连接回收器( 如果有) 进行检验。 如果检测失败， 则连接将被从池中去除。注意： 设置为true 后如果要生效，validationQuery 参数必须设置为非空字符串
testWhileIdle = falseCopy to clipboardErrorCopied
```
<a name="TGqzh"></a>
## [C3P0（不推荐）](https://www.hutool.cn/docs/#/db/%E6%95%B0%E6%8D%AE%E6%BA%90%E9%85%8D%E7%BD%AEdb.setting%E6%A0%B7%E4%BE%8B?id=c3p0%ef%bc%88%e4%b8%8d%e6%8e%a8%e8%8d%90%ef%bc%89)
```
# 连接池中保留的最大连接数。默认值: 15
maxPoolSize = 15
# 连接池中保留的最小连接数，默认为：3
minPoolSize = 3
# 初始化连接池中的连接数，取值应在minPoolSize与maxPoolSize之间，默认为3
initialPoolSize = 3
# 最大空闲时间，60秒内未使用则连接被丢弃。若为0则永不丢弃。默认值: 0
maxIdleTime = 0
# 当连接池连接耗尽时，客户端调用getConnection()后等待获取新连接的时间，超时后将抛出SQLException，如设为0则无限期等待。单位毫秒。默认: 0
checkoutTimeout = 0
# 当连接池中的连接耗尽的时候c3p0一次同时获取的连接数。默认值: 3
acquireIncrement = 3
# 定义在从数据库获取新连接失败后重复尝试的次数。默认值: 30 ；小于等于0表示无限次
acquireRetryAttempts = 0
# 重新尝试的时间间隔，默认为：1000毫秒
acquireRetryDelay = 1000
# 关闭连接时，是否提交未提交的事务，默认为false，即关闭连接，回滚未提交的事务
autoCommitOnClose = false
# c3p0将建一张名为Test的空表，并使用其自带的查询语句进行测试。如果定义了这个参数那么属性preferredTestQuery将被忽略。你不能在这张Test表上进行任何操作，它将只供c3p0测试使用。默认值: null
automaticTestTable = null
# 如果为false，则获取连接失败将会引起所有等待连接池来获取连接的线程抛出异常，但是数据源仍有效保留，并在下次调用getConnection()的时候继续尝试获取连接。如果设为true，那么在尝试获取连接失败后该数据源将申明已断开并永久关闭。默认: false
breakAfterAcquireFailure = false
# 检查所有连接池中的空闲连接的检查频率。默认值: 0，不检查
idleConnectionTestPeriod = 0
# c3p0全局的PreparedStatements缓存的大小。如果maxStatements与maxStatementsPerConnection均为0，则缓存不生效，只要有一个不为0，则语句的缓存就能生效。如果默认值: 0
maxStatements = 0
# maxStatementsPerConnection定义了连接池内单个连接所拥有的最大缓存statements数。默认值: 0
maxStatementsPerConnection = 0Copy to clipboardErrorCopied
```
<a name="vGOCI"></a>
## [DBCP（不推荐）](https://www.hutool.cn/docs/#/db/%E6%95%B0%E6%8D%AE%E6%BA%90%E9%85%8D%E7%BD%AEdb.setting%E6%A0%B7%E4%BE%8B?id=dbcp%ef%bc%88%e4%b8%8d%e6%8e%a8%e8%8d%90%ef%bc%89)
```
# (boolean) 连接池创建的连接的默认的auto-commit 状态
defaultAutoCommit = true
# (boolean) 连接池创建的连接的默认的read-only 状态。 如果没有设置则setReadOnly 方法将不会被调用。 ( 某些驱动不支持只读模式， 比如：Informix)
defaultReadOnly = false
# (String) 连接池创建的连接的默认的TransactionIsolation 状态。 下面列表当中的某一个： ( 参考javadoc) NONE READ_COMMITTED EAD_UNCOMMITTED REPEATABLE_READ SERIALIZABLE
defaultTransactionIsolation = NONE
# (int) 初始化连接： 连接池启动时创建的初始化连接数量，1。2 版本后支持
initialSize = 10
# (int) 最大活动连接： 连接池在同一时间能够分配的最大活动连接的数量， 如果设置为非正数则表示不限制
maxActive = 100
# (int) 最大空闲连接： 连接池中容许保持空闲状态的最大连接数量， 超过的空闲连接将被释放， 如果设置为负数表示不限制 如果启用，将定期检查限制连接，如果空闲时间超过minEvictableIdleTimeMillis 则释放连接 （ 参考testWhileIdle ）
maxIdle = 8
# (int) 最小空闲连接： 连接池中容许保持空闲状态的最小连接数量， 低于这个数量将创建新的连接， 如果设置为0 则不创建 如果连接验证失败将缩小这个值（ 参考testWhileIdle ）
minIdle = 0
# (int) 最大等待时间： 当没有可用连接时， 连接池等待连接被归还的最大时间( 以毫秒计数)， 超过时间则抛出异常， 如果设置为-1 表示无限等待
maxWait = 30000
# (String) SQL 查询， 用来验证从连接池取出的连接， 在将连接返回给调用者之前。 如果指定， 则查询必须是一个SQL SELECT 并且必须返回至少一行记录 查询不必返回记录，但这样将不能抛出SQL异常
validationQuery = SELECT 1
# (boolean) 指明是否在从池中取出连接前进行检验， 如果检验失败， 则从池中去除连接并尝试取出另一个。注意： 设置为true 后如果要生效，validationQuery 参数必须设置为非空字符串 参考validationInterval以获得更有效的验证
testOnBorrow = false
# (boolean) 指明是否在归还到池中前进行检验 注意： 设置为true 后如果要生效，validationQuery 参数必须设置为非空字符串
testOnReturn = false
# (boolean) 指明连接是否被空闲连接回收器( 如果有) 进行检验。 如果检测失败， 则连接将被从池中去除。注意： 设置为true 后如果要生效，validationQuery 参数必须设置为非空字符串
testWhileIdle = false
```


<a name="Uh1IL"></a>
# 扩展开发

1. Spring-Boot-starter
1. 缓存，sql过滤，慢sql过滤，sql监控
<a name="IJmkx"></a>
# 架构设计
**设计思想：**

- 面向接口编程，而非面向对象编程，这样可以避免Java单继承的尴尬
- 方法大于对象，静态方式直接使用，而非new一个对象再使用方法
- 工具类大于框架，引入该工具类简单快捷，不跟任何框架冲突
- 极简设计，极致轻量，极致速度

**实现方式：**

- 总共三个接口，分别代表JDBC方式，ORM方式，ActiveRecord方式
- **interface **JDBC：_封装JDBC方式操作数据库，，实现该接口表示赋予类操作数据库的能力_
- **interface **ORM **extends **JDBC：_封装ORM方式操作数据库，实现该接口表示赋予类操作数据库的能力_
  - _ORM（对象关系映射）:一条数据映射为一个Bean对象，多条数据映射为List<Bean>_
- **interface **ActiveRecord **extends **JDBC：_封装ActiveRecord方式操作数据库，实现该接口表示赋予类操作数据库的能力_
  - _ActiveRecord（活动记录）:一条数据映射为一个Map<String,Object>对象，多条数据映射为List<Map<String,Object>>* 这里为了操作Map<String,Object>方便，封装类Entity代之Map<String,Object>，使用方便_
- 提供一个负载SQL的实现方式：**class **Sql **implements **ActiveRecord ，封装SQL执行等操作。

![image.png](https://cdn.nlark.com/yuque/0/2019/png/251474/1574691728810-f0ae73b6-a2d6-4be6-8653-ea4f942511af.png#align=left&display=inline&height=606&name=image.png&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&originHeight=606&originWidth=569&search=JDBC%20executeQuery%28String%2CObject..%3AListxentity%3E%20executeUpdate%28String%2CObject..int%20executeUpdateBatch%28String..%3Aint%20executeUpdateBatch%28String%2Cobject..%3Aint%20insert%28String%2Cobject..%3ABoolean%20insertForGeneratedKey%28String%2Cobject..Lon%20ransaction%28Transactionlevel%2CVoidFunc1xDb%3E%29%3A%20transaction%28VoidFunc1kDb%3E%29%3Avoid%20insert%28List%29%3ABoolean%20insertBatch%28List%29%3ABoolean%20delete%28String%2Cobject..%29%3ABoolean%20update%28String%2CObject..%3ABoolean%20update%28List%29%3ABoolean%20Pcount%28String%2Cobject..%3Anteger%20getDbo%3ADb%20checkEntity%28Entity%29%3Avoid%20checkList%28List%29%3Avoid%20getTableNameo%3AString%20getTableName%28Class%29%3AString%20PtoUnderlineCase%28String%29%3AString%20Pbean2Entity%28Entity%2Cobject%3Avoid&size=58749&status=done&width=569)


![image.png](https://cdn.nlark.com/yuque/0/2019/png/251474/1574691744631-e17f91d7-03aa-480f-8b1c-6369288fb815.png#align=left&display=inline&height=252&name=image.png&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&originHeight=252&originWidth=543&search=ORM%20inserto%3ABoolean%20deleteByldo%3ABoolean%20PdeleteBylds%28object..%3ABoolean%20updateByldo%3ABoolean%20insertOrUpdate0%3ABoolean%20Pselect%28String%2CObject..%3AListT%3E%20selectOne%28String%2Cobject..%3AT%20selectbyldobject%29%3AT&size=21378&status=done&width=543)

![image.png](https://cdn.nlark.com/yuque/0/2019/png/251474/1574691778620-b9c36d1d-695b-4c58-abac-d55789a3a1c0.png#align=left&display=inline&height=350&name=image.png&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&originHeight=350&originWidth=547&search=ActiveRecord%20insert%28Entity%3ABoolean%20deleteByldString%2CObject%29%3ABoolean%20ndeleteBylds%28String%2Cobject..%3ABoolean%20updateByld%28Entity%29%3ABoolean%20insertOrUpdate%28Entity%29%3ABoolean%20select%28String%2CObject..%3AListxentity%3E%20select%28Class%3CT%3E%2CString%2Cobject..%3AListT%3E%20selectOne%28String%2Cobject..%29%3AEntity%20selectByld%28String%2CObject%29%3AEntity%20%3Fpage%28lnteger%2Clnteger%2CString%2Ctring%2Cbject..%20page%28Page%2CString%2Cobject.%3APage&size=33225&status=done&width=547)

![image.png](https://cdn.nlark.com/yuque/0/2019/png/251474/1574691867017-380fbd09-7a76-4731-a242-72274ef0d4dd.png#align=left&display=inline&height=621&name=image.png&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&originHeight=621&originWidth=561&search=Sql%20Sqlo%20Sql%28String%29%20SqlO%3ASql%20sql%28string%29%3ASql%20append%28Charsequence%29%3ASql%20append%28Boolean%2CCharsequence%29%3ASql%20setParams%28MapsString%2Cobject%3E%3ASq%20setParams%28object%29%3ASql%20execute0%3AT%20executeQueryQ%3AListsentity%3E%20PexecuteQuery%28ClasssT%3E%29%3AListT%3E%20executeUpdate0%3Alnteger%20page%28Page%29%3APage%20mapage%28nteger%2Clnteger%2CString%29%3APage%20checksqlo%3Avoid%20getSqlo%3AString%20setsal%28String%29%3Avoid%20pgetParamso%3Aobject%20stringBuilder%3AStringBuilder-newStringBuilde%20sql%3AString%20params%3Aobjecto%20tClass%3AClass&size=51353&status=done&width=561)

<a name="Y5ozm"></a>
# 该工具的缺陷

- 时间有限（前后抽空写了11天），仅实现了MySQL语法（因为现在主要用mysql），其他关系型数据库慢慢支持
- 希望大佬能实现其他数据库的语法
- **贡献代码流程：fork本代码，修改后提交，然后到github页面 pull request给我，等邮件合并通知**
- QQ交流群：[967073790](https://jq.qq.com/?_wv=1027&k=57bmhwO)


