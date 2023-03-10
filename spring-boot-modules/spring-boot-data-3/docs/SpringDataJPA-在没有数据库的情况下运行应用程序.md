## 1. 概述

在本教程中，**我们将学习如何在没有正在运行的数据库的情况下启动Spring Boot应用程序**。

默认情况下，如果我们有一个包含[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)的Spring Boot应用程序，那么该应用程序将自动寻找创建数据库连接。但是，在应用程序启动时数据库不可用的情况下，可能需要避免这种情况。

## 2. 设置

我们将使用一个使用MySQL的简单Spring Boot应用程序。让我们看看设置应用程序的步骤。

### 2.1 依赖关系

让我们将[Spring Data JPA Starter](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa)和[MySql Connector](https://mvnrepository.com/artifact/mysql/mysql-connector-java)依赖项添加到pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```

这会将JPA、MySQL Connector和Hibernate添加到类路径中。

除此之外，我们还希望任务在应用程序启动时保持运行。为此，让我们将[Web Starter](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)添加到pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

这将在端口8080上启动Web服务器并保持应用程序运行。

### 2.2 属性

**在启动应用程序之前，我们需要在application.properties文件中设置一些强制性属性**：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/myDb
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
```

让我们了解一下我们设置的属性：

-   spring.datasource.url：服务器的URL和数据库的名称。
-   spring.datasource.driver-class-name：驱动程序类名。MySQL连接器提供了这个驱动程序。
-   spring.jpa.properties.hibernate.dialect：我们已将其设置为MySQL 5。这告诉JPA提供程序使用MySQL 5方言。

**除此之外，我们还需要设置连接数据库所需的用户名和密码**：

```properties
spring.datasource.username=root
spring.datasource.password=root
```

### 2.3 启动应用程序

如果我们启动应用程序，我们将看到以下错误：

```shell
HHH000342: Could not obtain connection to query metadata
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.
```

这是因为我们没有在指定URL上运行的数据库服务器。但是，应用程序的默认行为是执行这两个操作：

-   **JPA尝试连接到数据库服务器并获取元数据**
-   **如果数据库不存在，Hibernate将尝试创建数据库**。这是由于属性spring.jpa.hibernate.ddl-auto默认设置为create

## 3. 在没有数据库的情况下运行

要在没有数据库的情况下继续，我们需要通过覆盖上面提到的两个属性来修改默认行为。

首先，**让我们禁用元数据获取**：

```properties
spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults=false
```

然后，**我们禁用自动数据库创建**：

```properties
spring.jpa.hibernate.ddl-auto=none
```

通过设置此属性，**我们已禁用数据库的创建。因此应用程序没有理由创建连接**。

与之前不同的是，现在，当我们启动应用程序时，它会正常启动时没有任何错误。**除非操作需要与数据库交互，否则不会启动连接**。

## 4. 总结

在本文中，我们学习了如何在不需要正在运行的数据库的情况下启动Spring Boot应用程序。

我们查看了查找数据库连接的应用程序的默认行为。然后我们通过覆盖两个属性来修复默认行为。