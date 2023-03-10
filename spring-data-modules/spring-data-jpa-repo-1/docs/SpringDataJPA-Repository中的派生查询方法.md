## 1. 概述

**对于简单的查询，只需查看我们代码中相应的方法名称，就可以很容易地得出查询应该是什么**。

在本教程中，我们将探讨[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa#customquery)如何以方法命名约定的形式利用这一想法。

### [Spring Data JPA简介](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)

Spring Data JPA与Spring 4简介-Spring配置、DAO、手动和生成的查询以及事务管理。

[阅读更多](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)→

### [Spring Data中的CrudRepository、JpaRepository和PagingAndSortingRepository](https://www.baeldung.com/spring-data-repositories)

了解Spring Data提供的不同风格的Repository。

[阅读更多](https://www.baeldung.com/spring-data-repositories)→

### [使用Spring Data对查询结果进行排序](https://www.baeldung.com/spring-data-sorting)

了解在Spring Data查询中对结果进行排序的不同方法。

[阅读更多](https://www.baeldung.com/spring-data-sorting)→

## 2. Spring派生查询方法结构

**派生方法名称有两个主要部分，由第一个By关键字分隔**：

```java
List<User> findByName(String name)
```

第一部分(例如find)是动词，其余部分(例如ByName)是标准。

**Spring Data JPA支持find、read、query、count和get**。因此，我们可以使用queryByName，Spring Data的行为也是一样的。

我们还可以使用Distinct、First或Top来删除重复项或限制我们的结果集：

```java
List<User> findTop3ByAge()
```

**条件部分包含查询的特定于实体的条件表达式**。我们可以将条件关键字与实体的属性名称一起使用。

我们还可以使用And和Or将表达式连接起来，稍后我们将看到。

## 3. 示例应用程序

首先，我们需要构建一个[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)的应用程序。

在该应用程序中，让我们定义一个实体类：

```java
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private Integer age;
    private ZonedDateTime birthDate;
    private Boolean active;
    // standard getters and setters ...
}
```

我们还需要定义一个Repository。它将扩展JpaRepository，这是[Spring Data Repository类型](https://www.baeldung.com/spring-data-repositories)之一：

```java
public interface UserRepository extends JpaRepository<User, Integer> {
}
```

我们将在该接口中定义所有派生查询方法。

## 4. Equals条件关键字

完全相等是查询中最常用的条件之一。我们有几个选项可以在查询中表达“=”或“IS”运算符。

对于完全匹配条件，我们可以只附加不带任何关键字的属性名称：

```java
List<User> findByName(String name);
```

为了提高可读性，我们可以添加Is或Equals关键字：

```java
List<User> findByNameIs(String name);
List<User> findByNameEquals(String name);
```

当我们需要表达不等式时，这种额外的可读性会派上用场：

```java
List<User> findByNameIsNot(String name);
```

这比findByNameNot(String)更具可读性。

由于null相等是一种特殊情况，我们不应该使用“=”运算符。Spring Data JPA默认处理null参数。因此，当我们为相等条件传递空值时，Spring会在生成的SQL中将查询解释为IS NULL。

我们还可以使用IsNull关键字将IS NULL条件添加到查询中：

```java
List<User> findByNameIsNull();
List<User> findByNameIsNotNull();
```

请注意，IsNull和IsNotNull都不需要声明方法参数。

还有另外两个不需要任何参数的关键字。

我们可以使用True和False关键字为布尔类型添加相等条件：

```java
List<User> findByActiveTrue();
List<User> findByActiveFalse();
```

## 5. 相似条件关键字

当我们需要使用属性模式查询结果时，我们有几个选择。

我们可以使用StartingWith查找以某个值开头的name：

```java
List<User> findByNameStartingWith(String prefix);
```

粗略地说，这个方法被解释为WHERE name LIKE ‘value%’。

如果我们想要以给定值结尾的名称，则可以使用EndingWith：

```java
List<User> findByNameEndingWith(String suffix);
```

或者我们可以通过Containing找到哪些名称包含一个值：

```java
List<User> findByNameContaining(String infix);
```

请注意，上述所有条件都称为预定义模式表达式。因此，调用这些方法时，**我们不需要在参数中添加“%”运算符**。

但是假设我们正在做一些更复杂的事情。假设我们需要获取名称以a开头、包含b并以c结尾的用户。

为此，我们可以使用Like关键字添加自己的LIKE条件：

```java
List<User> findByNameLike(String likePattern);
```

然后我们可以在调用方法时传递我们的LIKE条件：

```java
String likePattern = "a%b%c";
userRepository.findByNameLike(likePattern);
```

## 6. 比较条件关键字

此外，我们可以使用LessThan和LessThanEqual关键字来使用”<“和”<=“运算符将记录与给定值进行比较：

```java
List<User> findByAgeLessThan(Integer age);
List<User> findByAgeLessThanEqual(Integer age);
```

在相反的情况下，我们可以使用GreaterThan和GreaterThanEqual关键字：

```java
List<User> findByAgeGreaterThan(Integer age);
List<User> findByAgeGreaterThanEqual(Integer age);
```

或者我们可以找到两个年龄段之间的用户：

```java
List<User> findByAgeBetween(Integer startAge, Integer endAge);
```

我们还可以提供一个包含age值的集合，以匹配年龄在集合中的用户：

```java
List<User> findByAgeIn(Collection<Integer> ages);
```

因为我们知道用户的生日，所以我们可能想要查询出生在给定日期之前或之后的用户。

为此，我们将使用Before和After：

```java
List<User> findByBirthDateAfter(ZonedDateTime birthDate);
List<User> findByBirthDateBefore(ZonedDateTime birthDate);
```

## 7. 多条件表达式

我们可以通过使用And和Or关键字来组合任意数量的表达式：

```java
List<User> findByNameOrBirthDate(String name, ZonedDateTime birthDate);
List<User> findByNameOrBirthDateAndActive(String name, ZonedDateTime birthDate, Boolean active);
```

优先顺序是And，然后是Or，就像Java一样。

**虽然Spring Data JPA对我们可以添加的表达式数量没有限制，但我们不应该无限制地组合多个条件**。长名称的方法名难以阅读且难以维护。**对于复杂的查询，请改为查看[@Query](https://www.baeldung.com/spring-data-jpa-query)注解**。

## 8. 排序结果

接下来，让我们看一下排序。

我们可以使用OrderBy按姓名的字母顺序对用户进行排序：

```java
List<User> findByNameOrderByName(String name);
List<User> findByNameOrderByNameAsc(String name);
```

升序是默认的排序方式，但我们可以使用Desc进行降序排序：

```java
List<User> findByNameOrderByNameDesc(String name);
```

## 9. CurdRepository中的findOne和findById比较

Spring团队在Spring Boot 2.x的[CrudRepository](https://www.baeldung.com/spring-data-repositories#crudrepository)中做了一些重大更改。其中之一是将findOne重命名为findById。

以前使用Spring Boot 1.x时，当我们想通过主键检索实体时，我们会调用findOne：

```java
User user = userRepository.findOne(1);
```

从Spring Boot 2.X开始，我们可以用findById()进行同样的操作：

```java
User user = userRepository.findById(1);
```

请注意，findById()方法已经在CrudRepository中为我们定义。因此，我们不必在扩展CrudRepository的自定义Repository中明确定义它。

## 10. 总结

在本文中，我们解释了Spring Data JPA中的查询派生机制。我们使用属性条件关键字在Spring Data JPA Repository中编写派生查询方法。