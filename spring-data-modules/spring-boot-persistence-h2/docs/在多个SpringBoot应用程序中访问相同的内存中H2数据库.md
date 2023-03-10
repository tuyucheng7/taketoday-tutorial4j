## 1. 概述

在本快速教程中，我们将演示**如何从多个Spring Boot应用程序访问同一个内存中的H2数据库**。

为此，我们将创建两个不同的Spring Boot应用程序。第一个Spring Boot应用程序将启动内存中的H2实例，而第二个应用程序将通过TCP访问第一个应用程序的嵌入式H2实例。

## 2. 背景

众所周知，内存数据库速度更快，并且通常在应用程序中以嵌入式模式使用。但是，内存数据库不会在服务器重新启动时保留数据。

有关其他背景，请查看我们关于[最常用的内存数据库](https://www.baeldung.com/java-in-memory-databases)和[内存数据库在自动化测试中的使用](https://www.baeldung.com/spring-jpa-test-in-memory-database)的文章。

## 3. Maven依赖

**本文中的两个Spring Boot应用程序需要相同的依赖项**：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
    </dependency>
</dependencies>
```

## 4. 设置H2数据源

首先，让我们定义最重要的组件-用于内存中H2数据库的Spring bean，并通过TCP端口公开它：

```java
@Bean(initMethod = "start", destroyMethod = "stop")
public Server inMemoryH2DatabaseaServer() throws SQLException {
    return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9090");
}
```

initMethod和destroyMethod参数定义的方法被Spring调用来启动和停止H2数据库。

-tcp参数指示H2使用TCP服务器启动H2。我们在createTcpServer方法的第3个和第4个参数中指定要使用的TCP端口。

参数tcpAllowOthers打开H2，以便从运行在同一主机或远程主机上的外部应用程序进行访问。

接下来，让我们通过向application.properties文件添加一些属性来**覆盖由Spring Boot的自动配置功能创建的默认数据源**：

```properties
spring.datasource.url=jdbc:h2:mem:mydb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
```

**重写这些属性很重要，因为我们需要在其他想要共享同一H2数据库的应用程序中使用相同的属性和值**。

## 5. 引导第一个Spring Boot应用程序

接下来，为了引导我们的Spring Boot应用程序，我们将创建一个带有@SpringBootApplication注解的类：

```java
@SpringBootApplication
public class SpringBootApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApp.class, args);
    }
}
```

为了测试一切是否正确连接，让我们添加代码来创建一些测试数据。

我们将定义一个名为initDb的方法，并用@PostConstruct标注它，以便Spring容器在应用程序主类初始化后立即自动调用该方法：

```java
@PostConstruct
private void initDb() {
    String sqlStatements[] = {
        "drop table employees if exists",
        "create table employees(id serial,first_name varchar(255),last_name varchar(255))",
        "insert into employees(first_name, last_name) values('Eugen','Paraschiv')",
        "insert into employees(first_name, last_name) values('Scott','Tiger')"
    };

    Arrays.asList(sqlStatements).forEach(sql -> {
        jdbcTemplate.execute(sql);
    });

    // Query test data and print results
}
```

## 6. 第二个Spring Boot应用程序

现在让我们看看客户端应用程序的组件，它需要与上面定义的相同的Maven依赖项。

首先，我们将覆盖数据源属性。**我们需要确保JDBC URL中的端口号与H2在第一个应用程序中监听传入连接的端口号相同**。

这是客户端应用程序的application.properties文件：

```properties
spring.datasource.url=jdbc:h2:tcp://localhost:9090/mem:mydb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
```

最后，我们创建客户端Spring Boot应用程序的主类。

同样为了简单起见，我们定义了一个@SpringBootApplication，其中包含一个带有@PostConstruct注解的initDb方法：

```java
@SpringBootApplication
public class ClientSpringBootApp {
    public static void main(String[] args) {
        SpringApplication.run(ClientSpringBootApp.class, args);
    }

    @PostConstruct
    private void initDb() {
        String[] sqlStatements = {
              "insert into employees(first_name, last_name) values('Donald','Trump')",
              "insert into employees(first_name, last_name) values('Barack','Obama')"
        };

        Arrays.asList(sqlStatements).forEach(sql -> jdbcTemplate.execute(sql));

        // Fetch data using SELECT statement and print results
    }
}
```

## 7. 样本输出

现在，当我们逐个运行这两个应用程序时，我们可以检查控制台日志并确认第二个应用程序是否按预期打印数据。

这是**第一个Spring Boot应用程序的控制台日志**：

```shell
****** Creating table: Employees, and Inserting test data ******
drop table employees if exists
create table employees(id serial,first_name varchar(255),last_name varchar(255))
insert into employees(first_name, last_name) values('Eugen','Paraschiv')
insert into employees(first_name, last_name) values('Scott','Tiger')
****** Fetching from table: Employees ******
id:1,first_name:Eugen,last_name:Paraschiv
id:2,first_name:Scott,last_name:Tiger
```

这是**第二个Spring Boot应用程序的控制台日志**：

```shell
****** Inserting more test data in the table: Employees ******
insert into employees(first_name, last_name) values('Donald','Trump')
insert into employees(first_name, last_name) values('Barack','Obama')
****** Fetching from table: Employees ******
id:1,first_name:Eugen,last_name:Paraschiv
id:2,first_name:Scott,last_name:Tiger
id:3,first_name:Donald,last_name:Trump
id:4,first_name:Barack,last_name:Obama
```

## 8. 总结

在这篇快速文章中，我们了解了**如何从多个Spring Boot应用程序访问同一个内存中的H2数据库实例**。