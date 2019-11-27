# sqlcool是什么？

**sqlcool是一个数据库操作工具，极致简洁、快速、高效、稳定。**
**永久开源免费！**

# 为什么重复造轮子？
因为市面上没发现有很好用的Java操作数据库的工具或框架<br />Mybatis太繁琐，在XML里写动态SQL太蠢且复杂<br />JPA太重，学习成本也过高，复杂查询使用极复杂，效率低下<br />Github搜索一圈，没有我想要的工具，so……自己写<br />源码地址[：](https://github.com/dakuohao/sql) [https://github.com/dakuohao/sqlcool](https://github.com/dakuohao/sqlcool)<br />使用依赖：[https://www.hutool.cn/](https://www.hutool.cn/)  
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

3. **引入jar**：该代码还未提交Maven仓库，近期提交，暂时您需要下载源码，然后mvn package 打包为一个jar
,我已经打包好了一个 [https://github.com/dakuohao/sqlcool/releases/tag/v1.0.1](https://github.com/dakuohao/sqlcool/releases/tag/v1.0.1)<br />![image.png](https://cdn.nlark.com/yuque/0/2019/png/251474/1574866671147-5f8caab3-8d92-4e4e-92b6-90a536633958.png#align=left&display=inline&height=372&name=image.png&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&originHeight=743&originWidth=1249&search=Pre-release%20V1.0.1%E7%89%88%E6%9C%AC%20v1.0.1%20dakuohaoreleasedthisnow%20d4ddcd8%20Maven%E4%BD%BF%E7%94%A8%2C%E5%A4%8D%E5%88%B6%E8%AF%A5jar%E5%88%B0%E4%BD%A0%E7%9A%84%E9%A1%B9%E7%9B%AE%20%3Cdependency%3E%20%3CgroupIdcom.github.dakuohaos%2FeroupId%3E%20%3CartifactId%3EsqlartifactId%3E%20%3Cversion%3E1.0.0-SNAPSHOT%2Fversio%20%3Cscope%3Esystems%2Fscope%20tsystempathstproject.basd%20%3Cdependency%3E%20Assets3%20Psql-1.0.0-SNAPSHOTjar%204.47MB%20%E5%9B%9Bsourcecode%28zip%29%20%E5%9B%9BSourcecode%28tar.gz%29&size=71906&status=done&width=624.5)<br />Maven使用，复制该jar到你的项目

```xml
<dependency>
  <groupId>com.github.dakuohao</groupId>
  <artifactId>sql</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <scope>system</scope>
  <systemPath>${project.basedir}/src/main/resources/lib/sql-1.0.0-SNAPSHOT.jar</systemPath>
</dependency>
```

备注说明：本工具底层使用[Hutool工具类](https://www.hutool.cn/docs/#/)（Hutool是一个工具类集合，用来代替你项目中的utils包，很方便）。hutool本身提供了多种数据库连接池支持，故本工具的使用跟hutool类似，只需要添加一个数据库配置文件即可，就这么简单。**本工具只对Hutool做增强，不做修改，完全兼容**，故hutool的老用户可以放心大胆的引入，不会冲突。
<a name="w6FCl"></a>
# 快速开始使用

```java
//模拟前端传来的数据
user.setName("张三");
user.setAge(18);

//添加并返回自增主键
user.insert();

//通过id修改
user.updateById();

//通过id删除
user.deleteById();

//复杂查询加分页
Page page = new Page(1,10,"name desc,age asc");
Page result = Sql.sql("SELECT * FROM `user` WHERE")  //创建sql
    .append(StrUtil.isNotEmpty(user.getName()), "and `name` LIKE #{name}") //动态拼接sql
    .append(user.getAge() > 0, "AND age >${age}")
    .setParams(user) //设置参数
    .page(page); //分页查询
```

# 集成Spring-Boot
与上述方式一致，优点是不需要写service层和dao层了，项目中只有Controller和Entity。<br />我写了一个demo，参考这里：[https://github.com/dakuohao/spring-boot-sqlcool-demo](https://github.com/dakuohao/spring-boot-sqlcool-demo)

用法示例：

```java

@Api(tags = "我的测试-用户管理")
@RestController
@RequestMapping("testUser")
public class MyTestController {

    @ApiOperation("添加用户")
    @PostMapping("add")
    public R add(@RequestBody User user) {
        return R.ok(user.insert());
    }

    @ApiOperation("多种条件分页查询")
    @PostMapping("page")
    public R page(@RequestBody Page page) {
        //模拟前端传来的数据：按照名称和年龄查询
        User user = new User();
        user.setName("张三" + "%");
        user.setAge(18);
        Page result = Sql.sql("SELECT * FROM `user` WHERE")
                .append(StrUtil.isNotEmpty(user.getName()), "and `name` LIKE #{name}")
                .append(user.getAge() > 0, "AND age >${age}")
                .setParams(user)
                .page(page);

        return R.ok(result);
    }

}
```
![image.png](https://cdn.nlark.com/yuque/0/2019/png/251474/1574867472320-1028ac12-46b6-4145-9df2-67ce5d779fb7.png#align=left&display=inline&height=405&name=image.png&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&originHeight=809&originWidth=1919&search=%E7%8E%8BAPI%E6%8E%A5%E5%8F%A3%E6%96%87%E6%A1%A3%20detaull%20%E8%AF%B7%E7%BF%B0%E5%85%A5%E6%8E%A5%E7%B4%A2%E5%86%85%E5%AE%A2%20%E4%B8%B0%E9%A1%B5%20%E6%B7%BB%E5%8A%A0%E7%94%A8%E6%88%B7x%20%E8%AF%B7%E6%B1%82%E7%B1%BB%E5%9E%8B%20%E6%AC%BE%E8%AF%B4%E6%98%8E%20%E6%98%AF%E5%90%A6%E5%BF%85%E9%A1%BB%20%E6%A2%A6%E6%95%85%E5%90%8D%E7%A7%B0%20%E6%95%B0%E6%8B%92%E7%B1%BB%E5%9E%8B%20schema%20%E6%96%87%E6%A1%A3%E7%AE%A1%E7%90%86%20bocy%20wser%20Us%20%E6%88%91%E7%9A%84%E6%B1%89%E8%AF%95-%E7%94%A8%E6%88%B7%E7%AE%A1%E4%BB%B6%20%E6%97%A5age%20interer%28int32%29%20createTime%20body%20strng%28dALE%29%20S%E7%A4%BE%E4%BA%AC%E4%BB%B6%E5%88%86%E9%A1%B5%E6%9F%A5%20deleted%20body%20%E7%94%A8%E6%88%B7%E7%88%B8%E8%BF%8E%20%E5%9B%9Eld%20bocy%20alse%20integortlnt32%29%20%E5%9B%9Ename%20sLing%20LpdateTima%20alse%20suingIDALE%20%E5%90%91%E5%BA%94%E7%8A%B6%E6%80%81%20%E7%8A%B6%E6%80%81%E7%A0%81%20%E8%AF%B4%E6%98%8E%20Forbladen&size=128382&status=done&width=959.5)<br />![image.png](https://cdn.nlark.com/yuque/0/2019/png/251474/1574867493259-df2c3ba4-d5a0-4f2c-b08a-662b455e394e.png#align=left&display=inline&height=421&name=image.png&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&ocrLocations=%5Bobject%20Object%5D&originHeight=841&originWidth=1469&search=%E5%A4%9A%E7%A7%8D%E6%9D%A1%E4%BB%B6%E5%88%86%E9%A1%B5%E6%9F%A5%E8%AF%A2x%20%E4%B8%BB%E9%A1%B5%20%E6%B7%BB%E5%8A%A0%E7%94%A8%E6%88%B7x%20%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0%20%E8%AF%B7%E6%B1%82%E7%B1%BB%E5%9E%8B%20%E5%8F%82%E6%95%B0%E5%90%8D%E7%A7%B0%20%E5%8F%82%E6%95%B0%E8%AF%B4%E6%98%8E%20%E6%98%AF%E5%90%A6%E5%BF%85%E9%A1%BB%20%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%20schema%20%E5%99%A8page%20body%20Page%20page%20true%20Page%20body%20false%20integer%28int32%29%20current%20False%20body%20lnteger%28nt32%29%20limit%20body%20false%20Entity%20Ilst%20array%20body%20FalsE%20orderBy%20string%20body%20false%20integer%28int32%29%20size%20bady%20false%20total%20integer%28int32%29%20%E5%93%8D%E5%BA%94%E7%8A%B6%E6%80%81%20%E7%8A%B6%E6%80%81%E7%A0%81%20%E8%AF%B4%E6%98%8E%20schema%20200%20OK%20R%20201%20Created%20401%20Unauthorized%20403%20Forbidden%20404%20NotFound&size=97702&status=done&width=734.5)

**(其他框架类似，举一反三)**<br />**

# 该工具的缺陷

- 时间有限，仅实现了MySQL语法（因为现在主要用mysql），其他关系型数据库慢慢支持
- 希望大佬能实现其他数据库的语法
- **贡献代码流程：fork本代码，修改后提交，然后到github页面 pull request给我，等邮件合并通知**
- QQ交流群：[967073790](https://jq.qq.com/?_wv=1027&k=57bmhwO)


# 详细文档和架构说明

https://www.yuque.com/dakuohao/sqlcool
