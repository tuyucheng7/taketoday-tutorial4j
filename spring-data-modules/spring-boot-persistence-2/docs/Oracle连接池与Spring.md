## 1. 概述

Oracle是大型生产环境中最受欢迎的数据库之一。因此，作为Spring开发人员，必须使用这些数据库是很常见的。

在本教程中，我们将讨论如何进行这种集成。

## 2. 数据库

我们首先需要的当然是数据库。如果我们没有安装，我们可以获取并安装[Oracle数据库软件下载](https://www.oracle.com/database/technologies/oracle-database-software-downloads.html)中提供的任何数据库。但如果我们不想进行任何安装，我们也可以使用[Docker提供的Oracle数据库镜像](https://github.com/oracle/docker-images/tree/master/OracleDatabase/SingleInstance)。

在本例中，我们将使用Oracle Database 12c第2版(12.2.0.2)标准版[Docker镜像](https://github.com/oracle/docker-images/tree/master/OracleDatabase/SingleInstance#building-oracle-database-docker-install-images)，这样我们可以不必在计算机上安装新软件。

## 3. 连接池

现在我们已经为传入连接准备好数据库。接下来，让我们学习一些在Spring中配置连接池的不同方法。

### 3.1 HikariCP

使用Spring连接池的最简单方法是使用自动配置。spring-boot-starter-jdbc依赖包括[HikariCP](https://www.baeldung.com/hikaricp)作为首选池化数据源。因此，如果我们查看我们的pom.xml，我们将看到：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

[spring-boot-starter-data-jpa](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/3.0.3)依赖为我们传递了spring-boot-starter-jdbc依赖。

现在只需要将我们的[配置](https://www.baeldung.com/spring-boot-hikari)添加到application.properties文件中：

```properties
# OracleDB connection settings
spring.datasource.url=jdbc:oracle:thin:@//localhost:11521/ORCLPDB1
spring.datasource.username=books
spring.datasource.password=books
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# HikariCP settings
spring.datasource.hikari.minimumIdle=5
spring.datasource.hikari.maximumPoolSize=20
spring.datasource.hikari.idleTimeout=30000
spring.datasource.hikari.maxLifetime=2000000
spring.datasource.hikari.connectionTimeout=30000
spring.datasource.hikari.poolName=HikariPoolBooks

# JPA settings
spring.jpa.database-platform=org.hibernate.dialect.Oracle12cDialect
spring.jpa.hibernate.use-new-id-generator-mappings=false
spring.jpa.hibernate.ddl-auto=create
```

如你所见，我们有三个不同的部分配置设置：

-   OracleDB连接设置部分是我们像往常一样配置JDBC连接属性的地方
-   HikariCP设置部分是我们配置HikariCP连接池的地方。如果需要高级配置，我们可以阅读[HikariCP配置属性列表](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)
-   JPA设置部分是使用Hibernate的一些基本配置

### 3.2 Tomcat和Commons DBCP2连接池

[Spring因其性能推荐HikariCP](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-connect-to-production-database)。**另一方面，它在Spring Boot自动配置应用程序中也支持Tomcat和Commons DBCP2**。

Spring Boot首先尝试使用HikariCP。如果它不可用，则尝试使用Tomcat连接池。如果这些都不可用，则它会尝试使用Commons DBCP2。

我们还可以指定要使用的连接池。在这种情况下，我们只需要在application.properties文件中添加一个新属性：

```properties
spring.datasource.type=org.apache.tomcat.jdbc.pool.DataSource
```

如果我们需要配置特定设置，我们可以使用它们的前缀：

-   spring.datasource.hikari.*用于HikariCP配置
-   spring.datasource.tomcat.*用于Tomcat池配置
-   spring.datasource.dbcp2.*用于Commons DBC2配置

而且，实际上，我们可以设置spring.datasource.type为任何其他DataSource实现。不一定是上面提到的三个中的任何一个。

**但在这种情况下，我们将只有一个基本的开箱即用配置**。在很多情况下，我们需要一些高级配置。让我们看看其中的一些。

### 3.3 Oracle通用连接池

如果我们想使用高级配置，我们可以声明UCP数据源并在application.properties文件中设置其余属性。从UCP的21.1.0.0版开始，这是最简单的方法。

[适用于JDBC的Oracle通用连接池(UCP)](https://docs.oracle.com/en/database/oracle/oracle-database/21/jjucp/)为缓存JDBC连接提供了一个功能齐全的实现，它重用连接而不是创建新连接。它还为我们提供了一组用于自定义池行为的属性。

如果我们要使用UCP，我们需要添加以下Maven依赖项：

```xml
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
</dependency>
<dependency>
    <groupId>com.oracle.database.ha</groupId>
    <artifactId>ons</artifactId>
</dependency>
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ucp</artifactId>
</dependency>
```

现在我们只需要将我们的配置添加到application.properties文件中：

```properties
# UCP settings
spring.datasource.type=oracle.oracleucp.jdbc.UCPDataSource
spring.datasource.oracleucp.connection-factory-class-name=oracle.jdbc.pool.OracleDataSource
spring.datasource.oracleucp.sql-for-validate-connection=select * from dual
spring.datasource.oracleucp.connection-pool-name=UcpPoolBooks
spring.datasource.oracleucp.initial-pool-size=5
spring.datasource.oracleucp.min-pool-size=5
spring.datasource.oracleucp.max-pool-size=10
```

在上面的示例中，我们自定义了一些池属性：

-   spring.datasource.oracleucp.initial-pool-size指定池启动后创建的可用连接数
-   spring.datasource.oracleucp.min-pool-size指定我们的池维护的可用和借用连接的最小数量
-   spring.datasource.oracleucp.max-pool-size指定我们的池维护的最大可用和借用连接数

如果我们需要添加更多配置属性，可以查看[UCPDataSource JavaDoc](https://docs.oracle.com/en/database/oracle/oracle-database/21/jjuar/oracle/ucp/jdbc/UCPDataSource.html)或[开发人员指南](https://docs.oracle.com/en/database/oracle/oracle-database/21/jjucp/)。

## 4. 旧的Oracle版本

**对于11.2之前的版本，如Oracle 9i或10g**，我们应该创建一个OracleDataSource而不是使用Oracle的通用连接池。

在我们的OracleDataSource实例中，我们通过setConnectionCachingEnabled打开连接缓存：

```java
@Configuration
@Profile("oracle")
public class OracleConfiguration {
    @Bean
    public DataSource dataSource() throws SQLException {
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setUser("books");
        dataSource.setPassword("books");
        dataSource.setURL("jdbc:oracle:thin:@//localhost:11521/ORCLPDB1");
        dataSource.setFastConnectionFailoverEnabled(true);
        dataSource.setImplicitCachingEnabled(true);
        dataSource.setConnectionCachingEnabled(true);
        return dataSource;
    }
}
```

在上面的示例中，我们为连接池创建了OracleDataSource并配置了一些参数。我们可以在[OracleDataSource JavaDoc](https://docs.oracle.com/en/database/oracle/oracle-database/12.2/jajdb/oracle/jdbc/pool/OracleDataSource.html)上检查所有可配置的参数。

## 5. 总结

我们已经了解了如何使用自动配置和编程方式来配置Oracle数据库连接池。尽管Spring推荐使用HikariCP，但其他选项也是可用的。我们应该根据我们当前的需求选择正确的实现。