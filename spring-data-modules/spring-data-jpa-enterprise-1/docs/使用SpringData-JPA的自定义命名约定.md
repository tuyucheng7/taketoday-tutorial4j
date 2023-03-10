## 1. 简介

Spring Data JPA提供了许多在应用程序中使用JPA的功能。在这些功能中，有DDL和DML查询中表名和列名的标准化。

在这个简短的教程中，我们将了解如何配置此默认命名约定。

## 2. 默认命名约定

首先，让我们看看Spring关于表名和列名的默认命名约定是什么。

假设我们有一个Person实体：

```java
@Entity
public class Person {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
}
```

我们这里有一些必须映射到数据库的属性。**Spring默认使用蛇形大小写**，这意味着它只使用小写字母并用下划线分隔单词。因此，Person实体的表创建查询将是：

```sql
create table person (id bigint not null, first_name varchar(255), last_name varchar(255), primary key (id));
```

返回所有名字的select查询将是：

```sql
select first_name from person;
```

为此，**Spring实现了Hibernate的[PhysicalNamingStrategy](https://www.baeldung.com/hibernate-naming-strategy)版本：[SpringPhysicalNamingStrategy](https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/orm/jpa/hibernate/SpringImplicitNamingStrategy.java)**。

## 3. RDMS区分大小写

在详细介绍如何创建我们的自定义命名约定之前，让我们先谈谈RDMS如何管理标识符的大小写。

有两种情况需要考虑：RDMS区分大小写，或者不区分大小写。

在第一种情况下，**RDMS将严格匹配大小写相同的标识符**。因此，在我们的示例中，以下查询将起作用：

```sql
select first_name from person;
```

而这个会抛出错误，甚至不会返回结果：

```sql
select FIRST_NAME from PERSON;
```

另一方面，**对于不区分大小写的RDMS，这两个查询都可以正常工作**。

我们将如何强制RDMS也匹配有关其大小写的标识符？我们可以使用带引号的标识符(例如，“person”)。

通过在我们的标识符周围使用引号，我们告诉数据库在将这些标识符与表名和列名进行比较时它也应该匹配大小写。因此，仍然使用我们的示例，以下查询将起作用：

```sql
select "first_name" from "person";
```

而这个不会：

```sql
select "first_name" from "PERSON";
```

但这只是理论上的，因为每个RDMS都以自己的方式管理引用的标识符，所以[里程会有所不同](https://www.alberton.info/dbms_identifiers_and_case_sensitivity.html)。

## 4. 自定义命名约定

现在，让我们实现我们自己的命名约定。

想象一下，我们不能使用Spring的蛇形小写策略，但我们需要使用蛇形大写。然后，我们需要**提供PhysicalNamingStrategy的实现**。

由于我们要保持蛇形大小写，最快的选择是从SpringPhysicalNamingStrategy继承并将标识符转换为大写：

```java
public class UpperCaseNamingStrategy extends SpringPhysicalNamingStrategy {
    @Override
    protected Identifier getIdentifier(String name, boolean quoted, JdbcEnvironment jdbcEnvironment) {
        return new Identifier(name.toUpperCase(), quoted);
    }
}
```

我们只是重写了getIdentifier()方法，该方法负责将父类中的标识符转换为小写。在这里，我们使用它将它们转换为大写。

一旦编写了我们的实现，我们必须注册它以便Hibernate知道如何使用它。使用Spring，这是通过在我们的application.properties中**设置spring.jpa.hibernate.naming.physical-strategy属性**来完成的：

```properties
spring.jpa.hibernate.naming.physical-strategy=cn.tuyucheng.taketoday.namingstrategy.UpperCaseNamingStrategy
```

现在，我们的查询使用大写标识符：

```sql
create table PERSON (ID bigint not null, FIRST_NAME varchar(255), LAST_NAME varchar(255), primary key (ID));

select FIRST_NAME from PERSON;
```

假设我们想要使用带引号的标识符，以便RDMS强制匹配大小写。然后我们将不得不**使用true作为Identifier()构造函数的quoted参数**：

```java
@Override
protected Identifier getIdentifier(String name, boolean quoted, JdbcEnvironment jdbcEnvironment) {
    return new Identifier(name.toUpperCase(), true);
}
```

然后，我们的查询应该包含引号：

```sql
create table "PERSON" ("ID" bigint not null, "FIRST_NAME" varchar(255), "LAST_NAME" varchar(255), primary key ("ID"));

select "FIRST_NAME" from "PERSON";
```

## 5. 总结

在这篇简短的文章中，我们讨论了使用Spring Data JPA实现自定义命名策略的可能性，以及RDMS将如何处理有关其内部配置的DDL和DML语句。