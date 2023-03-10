## 1. 简介

在这个简短的教程中，我们将了解如何在[Spring Boot](https://www.baeldung.com/spring-boot)应用程序中使用[Hibernate命名策略](https://www.baeldung.com/hibernate-naming-strategy)。

## 2. Maven依赖

如果我们从[基于Maven的Spring Boot应用程序](https://www.baeldung.com/spring-boot-start)开始，并且乐于接受Spring Data，那么我们只需要添加Spring Data JPA依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

此外，我们需要一个数据库，因此我们将使用[内存数据库H2](https://central.sonatype.com/artifact/com.h2database/h2/2.1.212)：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

Spring Data JPA依赖项为我们引入了Hibernate依赖项。

## 3. Spring Boot命名策略

**Hibernate使用物理策略和隐式策略映射字段名称**。我们之前在[本教程](https://www.baeldung.com/hibernate-naming-strategy)中讨论了如何使用命名策略。

而且，Spring Boot为两者提供默认值：

- spring.jpa.hibernate.naming.physical-strategy默认为org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy
- spring.jpa.hibernate.naming.implicit-strategy默认为org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy

我们可以覆盖这些值，但默认情况下，这些将：

-   用下划线替换点
-   将骆驼大小写更改为蛇形大小写，并且
-   小写表名

因此，例如，一个AddressBook实体将被创建为address_book表。

## 4. 命名策略实践

如果我们创建一个Account实体：

```java
@Entity
public class Account {
    @Id
    private Long id;
    private String defaultEmail;
}
```

然后在我们的属性文件中打开一些SQL调试：

```yaml
hibernate.show_sql: true
```

启动时，我们会在日志中看到以下创建语句：

```sql
Hibernate: create table account (id bigint not null, default_email varchar(255))
```

正如我们所看到的，这些字段使用蛇形大小写并且是小写的，遵循Spring约定。

**请记住，在这种情况下，我们不需要指定物理策略或隐式策略属性，因为我们接受默认值**。

## 5. 自定义策略

因此，假设我们想要使用JPA 1.0中的策略。

我们只需要在我们的属性文件中指明它：

```yaml
spring:
    jpa:
        hibernate:
            naming:
                physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
                implicit-strategy: org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl
```

或者，将它们公开为@Bean：

```java
@Bean
public PhysicalNamingStrategy physical() {
    return new PhysicalNamingStrategyStandardImpl();
}

@Bean
public ImplicitNamingStrategy implicit() {
    return new ImplicitNamingStrategyLegacyJpaImpl();
}
```

无论哪种方式，如果我们使用这些更改运行我们的示例，我们将看到一个略有不同的DDL语句：

```sql
Hibernate: create table Account (id bigint not null, defaultEmail varchar(255), primary key (id))
```

正如我们所看到的，这次这些策略遵循JPA 1.0的命名约定。

**现在，如果我们想使用JPA 2.0命名规则，我们需要将ImplicitNamingStrategyJpaCompliantImpl设置为我们的隐式策略**。

## 6. 总结

在本教程中，我们了解了Spring Boot如何将命名策略应用于Hibernate的查询生成器，并且我们还看到了如何自定义这些策略。