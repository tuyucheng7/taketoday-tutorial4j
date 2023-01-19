## 1. 概述

在本入门教程中，我们将了解[HikariCP JDBC](https://github.com/brettwooldridge/HikariCP)连接池项目。这是一个非常轻量级(大约 130Kb)且快如闪电的 JDBC 连接池框架，由[Brett Wooldridge](https://github.com/brettwooldridge)于 2012 年左右开发。

## 延伸阅读：

## [Java 连接池的简单指南](https://www.baeldung.com/java-connection-pooling)

快速概述几种流行的连接池解决方案，并快速深入了解自定义连接池实现

[阅读更多](https://www.baeldung.com/java-connection-pooling)→

## [使用Spring Boot配置 Hikari 连接池](https://www.baeldung.com/spring-boot-hikari)

了解如何在 Spring Boot(1 和 2)应用程序中配置 Hikari CP

[阅读更多](https://www.baeldung.com/spring-boot-hikari)→

## [在 Hibernate 中使用 c3p0](https://www.baeldung.com/hibernate-c3p0)

了解如何将 c3p0 添加到 Hibernate 应用程序并配置一些常用属性

[阅读更多](https://www.baeldung.com/hibernate-c3p0)→

## 2.简介

有几个基准测试结果可用于比较 HikariCP 与其他连接池框架的性能，例如[c3p0](http://www.mchange.com/projects/c3p0/)、[dbcp2](https://commons.apache.org/proper/commons-dbcp/)、[tomcat](https://people.apache.org/~fhanik/jdbc-pool/jdbc-pool.html)和[vibur](http://www.vibur.org/)。例如，HikariCP 团队发布了以下基准(原始结果可[在此处](https://github.com/brettwooldridge/HikariCP-benchmark)获取)：

![HikariCP-bench-2.6.0](https://raw.githubusercontent.com/wiki/brettwooldridge/HikariCP/HikariCP-bench-2.6.0.png)

该框架之所以如此之快，是因为应用了以下技术：

-   字节码级工程——一些极端的字节码级工程(包括汇编级本机编码)已经完成
-   微优化——虽然几乎无法衡量，但这些优化结合起来可以提高整体性能
-   智能使用 Collections 框架——ArrayList <Statement>被替换为自定义类FastList，它消除了范围检查并执行从头到尾的移除扫描

## 3.Maven依赖

首先，让我们构建一个示例应用程序来突出显示它的用法。HikariCP 支持所有主要版本的 JVM。每个版本都需要其依赖项。对于Java8 到 11，我们有：

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>3.4.5</version>
</dependency>
```

HikariCP 还支持旧的 JDK 版本，如 6 和 7。可以分别在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"com.zaxxer" AND a%3A"HikariCP-java7")和[此处](https://search.maven.org/classic/#search|gav|1|g%3A"com.zaxxer" AND a%3A"HikariCP-java6")找到合适的版本。我们还可以在[Central Maven Repository](https://search.maven.org/classic/#search|gav|1|g%3A"com.zaxxer" AND a%3A"HikariCP")中查看最新版本。

## 4.用法

现在我们可以创建一个演示应用程序。请注意，我们需要在pom.xml中包含合适的 JDBC 驱动程序类依赖项。如果没有提供依赖项，应用程序将抛出ClassNotFoundException。

### 4.1. 创建数据源

我们将使用 HikariCP 的数据源为我们的应用程序创建数据源的单个实例：

```java
public class DataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl( "jdbc_url" );
        config.setUsername( "database_username" );
        config.setPassword( "database_password" );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource( config );
    }

    private DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
```

这里需要注意的一点是静态块中的初始化。

[HikariConfig](https://github.com/openbouquet/HikariCP/blob/master/src/main/java/com/zaxxer/hikari/HikariConfig.java)是用于初始化数据源的配置类。它带有四个众所周知的必须使用的参数：username、password、jdbcUrl和dataSourceClassName。

在jdbcUrl和dataSourceClassName之外，我们通常一次使用一个。但是，当将此属性用于较旧的驱动程序时，我们可能需要同时设置这两个属性。

除了这些属性之外，还有其他几个可用的属性，我们可能找不到其他池化框架提供的属性：

-   自动提交
-   连接超时
-   空闲超时
-   最大寿命
-   连接测试查询
-   连接初始化SQL
-   验证超时
-   最大池大小
-   池名
-   允许池暂停
-   只读
-   事务隔离
-   泄漏检测阈值

HikariCP 因这些数据库属性而脱颖而出。它甚至先进到足以自行检测连接泄漏。

可以在[此处](https://github.com/brettwooldridge/HikariCP)找到上述属性的详细说明。

我们还可以使用放在资源目录中的属性文件来初始化HikariConfig ：

```java
private static HikariConfig config = new HikariConfig(
    "datasource.properties" );
```

属性文件应如下所示：

```plaintext
dataSourceClassName= //TBD
dataSource.user= //TBD
//other properties name should start with dataSource as shown above
```

另外，我们可以使用基于java.util.Properties的配置：

```java
Properties props = new Properties();
props.setProperty( "dataSourceClassName" , //TBD );
props.setProperty( "dataSource.user" , //TBD );
//setter for other required properties
private static HikariConfig config = new HikariConfig( props );
```

或者，我们可以直接初始化数据源：

```java
ds.setJdbcUrl( //TBD  );
ds.setUsername( //TBD );
ds.setPassword( //TBD );
```

### 4.2. 使用数据源

现在我们已经定义了数据源，我们可以使用它从配置的连接池中获取连接，并执行 JDBC 相关操作。

假设我们有两个表，分别名为dept和emp，以模拟员工部门用例。我们将编写一个类来使用 HikariCP 从数据库中获取这些详细信息。

下面我们将列出创建示例数据所需的 SQL 语句：

```sql
create table dept(
  deptno numeric,
  dname  varchar(14),
  loc    varchar(13),
  constraint pk_dept primary key ( deptno )
);
 
create table emp(
  empno    numeric,
  ename    varchar(10),
  job      varchar(9),
  mgr      numeric,
  hiredate date,
  sal      numeric,
  comm     numeric,
  deptno   numeric,
  constraint pk_emp primary key ( empno ),
  constraint fk_deptno foreign key ( deptno ) references dept ( deptno )
);

insert into dept values( 10, 'ACCOUNTING', 'NEW YORK' );
insert into dept values( 20, 'RESEARCH', 'DALLAS' );
insert into dept values( 30, 'SALES', 'CHICAGO' );
insert into dept values( 40, 'OPERATIONS', 'BOSTON' );
 
insert into emp values(
 7839, 'KING', 'PRESIDENT', null,
 to_date( '17-11-1981' , 'dd-mm-yyyy' ),
 7698, null, 10
);
insert into emp values(
 7698, 'BLAKE', 'MANAGER', 7839,
 to_date( '1-5-1981' , 'dd-mm-yyyy' ),
 7782, null, 20
);
insert into emp values(
 7782, 'CLARK', 'MANAGER', 7839,
 to_date( '9-6-1981' , 'dd-mm-yyyy' ),
 7566, null, 30
);
insert into emp values(
 7566, 'JONES', 'MANAGER', 7839,
 to_date( '2-4-1981' , 'dd-mm-yyyy' ),
 7839, null, 40
);
```

请注意，如果我们使用 H2 等内存数据库，我们需要在运行实际代码以获取数据之前自动加载数据库脚本。值得庆幸的是，H2 带有一个INIT参数，可以在运行时从类路径加载数据库脚本。JDBC URL 应如下所示：

```bash
jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;INIT=runscript from 'classpath:/db.sql'
```

我们需要创建一个方法来从数据库中获取这些数据：

```java
public static List<Employee> fetchData() throws SQLException {
    String SQL_QUERY = "select  from emp";
    List<Employee> employees = null;
    try (Connection con = DataSource.getConnection();
        PreparedStatement pst = con.prepareStatement( SQL_QUERY );
        ResultSet rs = pst.executeQuery();) {
            employees = new ArrayList<>();
            Employee employee;
            while ( rs.next() ) {
                employee = new Employee();
                employee.setEmpNo( rs.getInt( "empno" ) );
                employee.setEname( rs.getString( "ename" ) );
                employee.setJob( rs.getString( "job" ) );
                employee.setMgr( rs.getInt( "mgr" ) );
                employee.setHiredate( rs.getDate( "hiredate" ) );
                employee.setSal( rs.getInt( "sal" ) );
                employee.setComm( rs.getInt( "comm" ) );
                employee.setDeptno( rs.getInt( "deptno" ) );
                employees.add( employee );
            }
	} 
    return employees;
}
```

然后我们需要创建一个 JUnit 方法来测试它。因为我们知道表emp中的行数，所以我们可以预期返回列表的大小应该等于行数：

```java
@Test
public void givenConnection_thenFetchDbData() throws SQLException {
    HikariCPDemo.fetchData();
 
    assertEquals( 4, employees.size() );
}
```

## 5.总结

在这篇简短的文章中，我们了解了使用 HikariCP 的好处及其配置。