## 1. 概述

在本教程中，我们将了解如何使用 JPA 和 Hibernate 查询以及 Criteria、JPQL 和 HQL 查询之间的区别。条件查询使用户能够在不使用原始 SQL 的情况下编写查询。除了 Criteria 查询，我们还将探索编写 Hibernate 命名查询以及如何在 Spring Data JPA 中使用@Query注解。

在深入研究之前，我们应该注意 Hibernate Criteria API 自 Hibernate 5.2 以来已被弃用。因此，我们将在示例中使用[JPA Criteria API](https://www.baeldung.com/hibernate-criteria-queries)，因为它是编写 Criteria 查询的新工具和首选工具。因此，从这里开始，我们将简称为 Criteria API。

## 2.条件查询

Criteria API 通过在其上应用不同的过滤器和逻辑条件来帮助构建 Criteria 查询对象。这是另一种操作对象并从 RDBMS 表返回所需数据的方法。

Hibernate Session中的createCriteria ()方法返回用于在应用程序中运行条件查询的持久性对象实例。简而言之，Criteria API 构建了一个应用不同过滤器和逻辑条件的条件查询。

### 2.1. Maven 依赖项

让我们获取[参考 JPA 依赖](https://search.maven.org/search?q=g:org.hibernate AND a:hibernate-core)项的最新版本——它在 Hibernate 中实现 JPA——并将其添加到我们的pom.xml中：

```yaml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.3.2.Final</version>
</dependency>
```

### 2.2. 使用条件查询和表达式

根据用户的条件，CriteriaBuilder控制查询结果。它使用CriteriaQuery中的where()方法，该方法提供CriteriaBuilder表达式。

让我们看看我们将在本文中使用的实体：

```java
public class Employee {

    private Integer id;
    private String name;
    private Long salary;

   // standard getters and setters
}

```

让我们看一个简单的条件查询，它将 从数据库中检索“Employee”的所有行：

```java
Session session = HibernateUtil.getHibernateSession();
CriteriaBuilder cb = session.getCriteriaBuilder();
CriteriaQuery<Employee> cr = cb.createQuery(Employee.class);
Root<Employee> root = cr.from(Employee.class);
cr.select(root);

Query<Employee> query = session.createQuery(cr);
List<Employee> results = query.getResultList();
session.close();
return results;
```

上面的 Criteria 查询返回所有项目的集合。让我们看看它是如何发生的：

1.  SessionFactory对象创建Session实例
2.  Session使用getCriteriaBuilder()方法返回CriteriaBuilder的一个实例
3.  CriteriaBuilder使用createQuery()方法。这将创建进一步返回 Query 实例的CriteriaQuery()对象
4.  最后我们调用getResult()方法获取保存结果的查询对象

让我们看一下CriteriaQuery中的另一个表达式：

```java
cr.select(root).where(cb.gt(root.get("salary"), 50000));
```

对于其结果，上述查询返回薪水超过 50000 的员工集。

## 3.JPQL

[JPQL](https://www.baeldung.com/spring-data-jpa-query)代表Java持久性查询语言。Spring Data 提供了多种创建和执行查询的方法，JPQL 就是其中之一。它使用 Spring 中的@Query注解定义查询以执行 JPQL 和本机 SQL 查询。查询定义默认使用 JPQL。

我们使用@Query注解在 Spring 中定义 SQL 查询。由@Query注解定义的任何查询都比使用@NamedQuery注解的命名查询具有更高的优先级。

### 3.1. 使用 JPQL 查询

让我们使用 JPQL 构建一个动态查询：

```java
@Query(value = "SELECT e FROM Employee e")
List<Employee> findAllEmployees(Sort sort);
```

对于具有参数参数的 JPQL 查询，Spring Data 以与方法声明相同的顺序将方法参数传递给查询。让我们看几个将方法参数传递到查询中的示例：

```java
@Query("SELECT e FROM Employee e WHERE e.salary = ?1")
Employee findAllEmployeesWithSalary(Long salary);
@Query("SELECT e FROM Employee e WHERE e.name = ?1 and e.salary = ?2")
Employee findEmployeeByNameAndSalary(String name, Long salary);
```

对于上面的后一个查询，name方法参数作为索引 1 的查询参数传递，salary参数作为索引 2 查询参数传递。

### 3.2. 使用 JPQL 本机查询

我们可以使用本地查询直接在我们的数据库中执行这些 SQL 查询，这些查询引用真实的数据库和表对象。我们需要将nativeQuery属性的值设置为true以定义本机 SQL 查询。本机 SQL 查询将在注解的值属性中定义。

让我们看一个本机查询，它显示要作为查询参数传递的索引参数：

```java
@Query(
  value = "SELECT  FROM Employee e WHERE e.salary = ?1",
  nativeQuery = true)
Employee findEmployeeBySalaryNative(Long salary);
```

使用命名参数使查询更易于阅读，并且在重构的情况下更不容易出错。让我们看一下 JPQL 和本机格式的简单命名查询的图示：

```java
@Query("SELECT e FROM Employee e WHERE e.name = :name and e.salary = :salary")
Employee findEmployeeByNameAndSalaryNamedParameters(
  @Param("name") String name,
  @Param("salary") Long salary);
```

方法参数使用命名参数传递给查询。我们可以通过在存储库方法声明中使用 @Param 注解来定义命名查询。因此，@Param注解必须具有与相应的 JPQL 或 SQL 查询名称相匹配的字符串值。

```java
@Query(value = "SELECT  FROM Employee e WHERE e.name = :name and e.salary = :salary", 
  nativeQuery = true) 
Employee findUserByNameAndSalaryNamedParamsNative( 
  @Param("name") String name, 
  @Param("salary") Long salary);
```

## 4. 总部

[HQL](https://www.baeldung.com/hibernate-named-query)代表 Hibernate 查询语言。它是一种类似于 SQL 的面向对象语言，我们可以使用它来查询数据库。然而，主要的缺点是代码的不可读性。我们可以将我们的查询定义为命名查询，以将它们放置在访问数据库的实际代码中。

### 4.1. 使用 Hibernate 命名查询

命名查询使用预定义的、不可更改的查询字符串定义查询。这些查询是快速失败的，因为它们在会话工厂的创建过程中得到验证。让我们使用org.hibernate.annotations.NamedQuery 注解定义一个命名查询：

```java
@NamedQuery(name = "Employee_FindByEmployeeId",
 query = "from Employee where id = :id")
```

每个@NamedQuery注解仅将自身附加到一个实体类。我们可以使用@NamedQueries注解对一个实体的多个命名查询进行分组：

```java
@NamedQueries({
    @NamedQuery(name = "Employee_findByEmployeeId", 
      query = "from Employee where id = :id"),
    @NamedQuery(name = "Employee_findAllByEmployeeSalary", 
      query = "from Employee where salary = :salary")
})
```

### 4.2. 存储过程和表达式

总之，我们可以使用@NamedNativeQuery注解来存储过程和函数：

```java
@NamedNativeQuery(
  name = "Employee_FindByEmployeeId", 
  query = "select  from employee emp where id=:id", 
  resultClass = Employee.class)
```

## 5. Criteria 查询相对于 HQL 和 JPQL 查询的优势

Criteria Queries 相对于 HQL 的主要优势是漂亮、干净、面向对象的 API。因此，我们可以在编译期间检测 Criteria API 中的错误。

此外，JPQL 查询和 Criteria 查询具有相同的性能和效率。

与 HQL 和 JPQL 相比，Criteria 查询更灵活，并为编写动态查询提供更好的支持。

但是 HQL 和 JPQL 提供了 Criteria 查询无法提供的本机查询支持。这是 Criteria 查询的缺点之一。

我们可以使用 JPQL 原生查询轻松编写复杂的连接，而在使用 Criteria API 应用相同的查询时却很难管理。

## 六. 总结

在本文中，我们主要了解了 Hibernate 和 JPA 中的 Criteria 查询、JPQL 查询和 HQL 查询的基础知识。此外，我们还学习了如何使用这些查询以及每种方法的优缺点。