## 1. 概述

对于简单的查询，只需查看我们代码中相应的方法名称，就很容易知道该方法的功能应该是什么。

在本教程中，我们探讨Spring Data JPA如何以方法命名约定的形式利用这种想法。

## 2. Spring中派生查询方法的结构

**派生方法名称有两个主要部分，由第一个By关键字分隔**：

```java
List<User> findByName(String name)
```

第一部分(例如find)是动词，其余部分(例如ByName)是标准。

**Spring Data JPA支持find、read、query、count和get**。所以，我们也可以使用queryByName，Spring Data的行为也是一样的。

我们还可以使用Distinct、First或Top来删除重复项或分页我们的结果集：

```java
List<User> findTop3ByAge()
```

条件部分包含查询的特定于实体的条件表达式，我们可以将条件关键字与实体的属性名称一起使用，也可以将表达式与And和Or连接起来。

## 3. 简单案例

首先，我们需要构建一个Spring Data JPA的应用程序。

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

我们还需要定义一个Repository，它将继承JpaRepository，这是Spring Data自带的Repository类型之一：

```java
public interface UserRepository extends JpaRepository<User, Integer> {

}
```

我们将在该接口中定义所有派生查询方法。

## 4. Equals条件关键字

完全相等是查询中最常用的条件之一，我们有几种方式可以在查询中表达“=”或“IS”运算符。

对于完全匹配条件，我们可以只附加属性名称而不使用任何关键字：

```java
List<User> findByName(String name);
```

为了可读性，我们可以添加Is或Equals关键字：

```java
List<User> findByNameIs(String name);
List<User> findByNameEquals(String name);
```

当我们需要表达不等式时，这种额外的可读性会派上用场：

```java
List<User> findByNameIsNot(String name);
```

这比findByNameNot(String)更具可读性。

由于空相等是一种特殊情况，我们不应该使用“=”运算符，Spring Data JPA默认处理空参数。因此，当我们为相等条件传递空值时，Spring在生成的SQL中将查询解释为IS NULL。

我们还可以使用IsNull关键字将IS NULL条件添加到查询中：

```java
List<User> findByNameIsNull();
List<User> findByNameIsNotNull();
```

请注意，IsNull和IsNotNull都不需要声明方法参数。

还有另外两个不需要任何参数的关键字。我们可以使用True和False关键字来为布尔类型添加相等条件：

```java
List<User> findByActiveTrue();
List<User> findByActiveFalse();
```

## 5. 相似条件关键字

当我们需要使用属性模式查询结果时，我们有几种选择。我们可以使用StartingWith找到以某个值开头的name：

```java
List<User> findByNameStartingWith(String prefix);
```

粗略地说，这个方法被解释为WHERE name LIKE ‘value%’。

如果我们想要以给定值结尾的名称，则可以使用EndingWith：

```java
List<User> findByNameEndingWith(String suffix);
```

或者我们可以找到哪些名称包含以下内容的值:

```java
List<User> findByNameContaining(String infix);
```

请注意，上述所有条件都称为预定义模式表达式。因此，当调用这些方法时，**我们不需要在参数中添加“%”运算符**。

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

相反，我们也可以使用GreaterThan和GreaterThanEqual关键字：

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

由于我们知道用户的birthdates，我们可能希望查询在给定日期之前或之后出生的用户。

我们可以使用Before和After：

```java
List<User> findByBirthDateAfter(ZonedDateTime birthDate);
List<User> findByBirthDateBefore(ZonedDateTime birthDate);
```

## 7. 多条件表达式

我们可以使用And或Or关键字组合任意多个表达式：

```java
List<User> findByNameOrBirthDate(String name, ZonedDateTime birthDate);
List<User> findByNameOrBirthDateAndActive(String name, ZonedDateTime birthDate, Boolean active);
```

优先顺序是And，然后是Or，就像Java一样。

**虽然Spring Data JPA对我们可以添加的表达式数量没有限制，但我们不应该无限制地组合多个条件**。长名称的方法名难以阅读且难以维护。**对于复杂的查询，应该使用@Query注解**。

## 8. 排序结果

我们可以使用OrderBy按姓名的字母顺序对用户进行排序：

```java
List<User> findByNameOrderByName(String name);
List<User> findByNameOrderByNameAsc(String name);
```

升序是默认的排序方式，但我们可以使用Desc来降序排序：

```java
List<User> findByNameOrderByNameDesc(String name);
```

## 9. CurdRepository中的findOne和findById比较

Spring团队使用Spring Boot 2.x对CrudRepository进行了一些重大更改，其中之一是将findOne重命名为findById。

以前在Spring Boot 1.x中，当我们想通过主键检索实体时，我们会调用findOne：

```java
User user = userRepository.findOne(1);
```

从Spring Boot 2.X以后，我们可以用findById()进行同样的操作：

```java
User user = userRepository.findById(1);
```

请注意，在CrudRepository中已经为我们定义了findById()方法。因此，我们不必在继承CrudRepository的自定义Repository中明确定义它。

## 10. 总结

在本文中，我们介绍了Spring Data JPA中的查询派生机制，通过使用属性条件关键字在Spring Data JPA Repository中编写派生查询方法。