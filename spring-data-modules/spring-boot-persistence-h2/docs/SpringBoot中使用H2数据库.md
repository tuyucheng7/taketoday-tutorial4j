## 1. 概述

在本教程中，我们探讨如何将H2与Spring Boot结合使用。就像其他数据库一样，Spring Boot生态系统对它有完整的内在支持。

## 2.依赖关系

首先，我们在pom.xml中添加以下依赖关系：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

## 3. 数据库配置

**默认情况下，Spring Boot将应用程序配置为使用用户名sa和空密码连接到内存存储**。

但是，我们可以通过将以下属性添加到application.properties文件来更改这些参数：

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

或者，我们也可以通过在application.yaml文件中添加相应的属性，将YAML用于应用程序的数据库配置：

```yaml
spring:
    datasource:
        url: jdbc:h2:mem:mydb
        username: sa
        password: password
        driverClassName: org.h2.Driver
    jpa:
        spring.jpa.database-platform: org.hibernate.dialect.H2Dialect
```

按照设计，内存数据库是易失性的，并在应用程序重新启动后导致数据丢失。

我们可以通过使用基于文件的存储来改变这种行为。为此，我们需要修改spring.datasource.url属性：

```properties
spring.datasource.url=jdbc:h2:file:/data/demo
```

同样，在application.yaml中，我们可以为基于文件的存储添加相同的属性：

```yaml
spring:
    datasource:
        url: jdbc:h2:file:/data/demo
```

数据库还[可以在其他模式下运行](http://www.h2database.com/html/features.html#connection_modes)。

## 4. 数据库操作

在Spring Boot中使用H2执行CRUD操作与其他SQL数据库相同，我们在[Spring Persistence]()系列中的教程涵盖了这些用例。

### 4.1 数据源初始化

我们可以使用基本的SQL脚本来初始化数据库。为了演示这一点，我们在src/main/resources目录下添加一个data.sql文件：

```sql
INSERT INTO countries (id, name) VALUES (1, 'USA');
INSERT INTO countries (id, name) VALUES (2, 'France');
INSERT INTO countries (id, name) VALUES (3, 'Brazil');
INSERT INTO countries (id, name) VALUES (4, 'Italy');
INSERT INTO countries (id, name) VALUES (5, 'Canada');
```

在这里，SQL脚本使用一些示例数据填充我们数据库中的countries表。

Spring Boot将自动获取该文件并针对嵌入式内存数据库运行它，例如我们配置的H2实例。**这是一种为测试或初始化目的为数据库添加数据的好方法**。

我们可以通过将spring.sql.init.mode属性设置为never来禁用此默认行为。此外，还可以配置[多个SQL文件来加载初始数据]()。

### 4.2 Hibernate和data.sql

**默认情况下，data.sql脚本在Hibernate初始化之前执行**，这使基于脚本的初始化与其他数据库迁移工具(例如[Flyway]()和[Liquibase]()保持一致]。当我们每次重新创建Hibernate生成的模式时，我们需要设置一个额外的属性：

```properties
spring.jpa.defer-datasource-initialization=true
```

**这会修改默认的Spring Boot行为并在Hibernate生成模式后填充数据**。此外，我们还可以使用schema.sql脚本在使用data.sql填充之前构建Hibernate生成的模式。但是，不建议混合使用不同的模式生成机制。

## 5. 访问H2控制台

H2数据库有一个嵌入式GUI控制台，用于浏览数据库的内容和运行SQL查询。默认情况下，H2控制台在Spring中未启用。要启用它，我们需要将以下属性添加到application.properties：

```properties
spring.h2.console.enabled=true
```

如果我们使用YAML配置，需要将属性添加到application.yaml：

```yaml
spring:
    h2:
        console.enabled: true
```

然后，在启动应用程序后，我们可以访问http://localhost:8080/h2-console，这会向我们展示一个登录页面。

在登录页面上，我们将提供我们在application.properties中使用的相同凭据：

[![h2 控制台 - 登录](https://www.baeldung.com/wp-content/uploads/2019/04/Screenshot-2019-04-13-at-5.21.34-PM-e1555173105246-1024x496.png)](https://www.baeldung.com/wp-content/uploads/2019/04/Screenshot-2019-04-13-at-5.21.34-PM-e1555173105246-1024x496.png)

连接后，我们将看到一个综合网页，其中列出了页面左侧的所有表格和一个用于运行SQL查询的文本框：

[![h2 控制台 - SQL 语句](https://www.baeldung.com/wp-content/uploads/2019/04/Screenshot-2019-04-13-at-5.25.16-PM.png)](https://www.baeldung.com/wp-content/uploads/2019/04/Screenshot-2019-04-13-at-5.25.16-PM.png)

Web控制台具有建议SQL关键字的自动完成功能。控制台是轻量级的这一事实使其便于直观地检查数据库或直接执行原始SQL。

此外，我们可以通过在项目的application.properties中使用我们想要的值指定以下属性来进一步配置控制台：

```properties
spring.h2.console.path=/h2-console
spring.h2.console.settings.trace=false
spring.h2.console.settings.web-allow-others=false
```

同样，在使用YAML配置时，我们可以将上述属性配置为：

```yaml
spring:
    h2:
        console:
            path: /h2-console
            settings.trace: false
            settings.web-allow-others: false
```

在上面的代码片段中，我们将控制台路径设置为/h2-console，它与我们正在运行的应用程序的地址和端口相关。因此，如果我们的应用程序在http://localhost:9001运行，控制台将在http://localhost:9001/h2-console上可用。

此外，我们将spring.h2.console.settings.trace设置为false以防止跟踪输出，我们还可以通过设置spring.h2.console.settings.web-allow-others为false来禁用远程访问。

## 6. 总结

H2数据库完全兼容Spring Boot，我们介绍了如何配置它以及如何使用H2控制台来管理我们正在运行的数据库。