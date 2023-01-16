## 1. 概述

在本教程中，我们将研究 JPA 中的默认列值。

我们将学习如何将它们设置为实体中的默认属性以及直接在 SQL 表定义中。

## 2.创建实体时

设置默认列值的第一种方法是直接将其设置为实体属性值：

```java
@Entity
public class User {
    @Id
    private Long id;
    private String firstName = "John Snow";
    private Integer age = 25;
    private Boolean locked = false;
}
```

现在，每次我们使用new运算符创建实体时，它都会设置我们提供的默认值：

```java
@Test
void saveUser_shouldSaveWithDefaultFieldValues() {
    User user = new User();
    user = userRepository.save(user);
    
    assertEquals(user.getName(), "John Snow");
    assertEquals(user.getAge(), 25);
    assertFalse(user.getLocked());
}
```

这种解决方案有一个缺点。

当我们查看 SQL 表定义时，我们不会在其中看到任何默认值：

```sql
create table user
(
    id     bigint not null constraint user_pkey primary key,
    name   varchar(255),
    age    integer,
    locked boolean
);
```

因此，如果我们用null覆盖它们，实体将被保存而不会出现任何错误：

```java
@Test
void saveUser_shouldSaveWithNullName() {
    User user = new User();
    user.setName(null);
    user.setAge(null);
    user.setLocked(null);
    user = userRepository.save(user);

    assertNull(user.getName());
    assertNull(user.getAge());
    assertNull(user.getLocked());
}
```

## 3.在架构定义中

要直接在 SQL 表定义中创建默认值，我们可以使用@Column注解并设置其columnDefinition参数：

```java
@Entity
public class User {
    @Id
    Long id;

    @Column(columnDefinition = "varchar(255) default 'John Snow'")
    private String name;

    @Column(columnDefinition = "integer default 25")
    private Integer age;

    @Column(columnDefinition = "boolean default false")
    private Boolean locked;
}
```

使用此方法，默认值将出现在 SQL 表定义中：

```sql
create table user
(
    id     bigint not null constraint user_pkey primary key,
    name   varchar(255) default 'John Snow',
    age    integer      default 35,
    locked boolean      default false
);
```

并且实体将使用默认值正确保存：

```java
@Test
void saveUser_shouldSaveWithDefaultSqlValues() {
    User user = new User();
    user = userRepository.save(user);

    assertEquals(user.getName(), "John Snow");
    assertEquals(user.getAge(), 25);
    assertFalse(user.getLocked());
}
```

请记住，通过使用此解决方案，我们将无法 在第一次保存实体时将给定列设置为空。如果我们不提供任何值，将自动设置默认值。

## 4. 总结

在这篇简短的文章中，我们学习了如何在 JPA 中设置默认列值。