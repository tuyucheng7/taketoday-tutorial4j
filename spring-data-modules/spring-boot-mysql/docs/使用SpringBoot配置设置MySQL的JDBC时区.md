## 1. 概述

有时，当我们在MySQL中存储日期时，我们意识到数据库中的日期与我们的系统或JVM不同。

其他时候，我们只需要使用另一个时区运行我们的应用程序。

在本教程中，我们将看到**使用Spring Boot配置更改MySQL时区的不同方法**。

## 2. 时区作为URL参数

我们可以指定时区的一种方法是在连接URL字符串中作为参数。

为了选择我们的时区，我们必须添加connectionTimeZone属性来指定时区：

```yaml
spring:
    datasource:
        url: jdbc:mysql://localhost:3306/test?connectionTimeZone=UTC
        username: root
        password:
```

此外，我们当然可以[使用Java配置](https://www.baeldung.com/spring-boot-configure-data-source-programmatic)来配置数据源。

我们可以在[MySQL官方文档](https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-configuration-properties.html)中找到有关此属性和其他属性的更多信息。

## 3. Spring Boot属性

或者，我们可以在Spring Boot配置中指定time_zone属性，而不是通过connectionTimeZone URL参数指示时区：

```properties
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

或者使用YAML：

```yaml
spring:
    jpa:
        properties:
            hibernate:
                jdbc:
                    time_zone: UTC
```

## 4. JVM默认时区

当然，我们可以更新Java的默认时区。

为了选择我们的时区，我们必须在URL中添加属性forceConnectionTimeZoneToSession=true。然后我们只需要添加一个简单的方法：

```java
@PostConstruct
void started() {
  TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
}
```

**但是，这个解决方案可能会产生其他问题，因为它是应用程序范围的**。也许应用程序的其他部分需要另一个时区。例如，我们可能需要连接到不同的数据库，并且出于某种原因，它们需要将日期存储在不同的时区。

## 5. 总结

在本教程中，我们看到了几种在Spring中配置MySQL JDBC时区的不同方法。我们使用URL参数、属性和更改JVM默认时区来完成此操作。