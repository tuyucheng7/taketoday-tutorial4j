## 1. 概述

在本教程中，我们将了解如何使用JPA和Hibernate查询以及Criteria、JPQL和HQL查询之间的区别。条件查询使用户能够在不使用原始SQL的情况下编写查询。除了Criteria查询，我们还将探索编写Hibernate命名查询以及如何在Spring Data JPA中使用@Query注解。

在深入研究之前，我们应该注意Hibernate Criteria API自Hibernate 5.2以来已被弃用。因此，**我们将在示例中使用[JPA Criteria API](https://www.baeldung.com/hibernate-criteria-queries)**，因为它是编写Criteria查询的新工具和首选工具。因此，从现在开始，我们将简称为Criteria API。

## 2. 条件查询

**Criteria API通过在对象上应用不同的过滤器和逻辑条件来帮助构建条件查询对象**。这是另一种操作对象并从RDBMS表返回所需数据的方法。

Hibernate Session中的createCriteria()方法返回用于在应用程序中运行条件查询的持久性对象实例。简而言之，Criteria API构建了一个应用不同过滤器和逻辑条件的条件查询。

### 2.1 Maven依赖项

让我们获取最新版本的参考[JPA依赖项](https://central.sonatype.com/artifact/org.hibernate/hibernate-core/6.2.0.CR3)-它在Hibernate中实现JPA，并将其添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.3.2.Final</version>
</dependency>
```

### 2.2 使用条件查询和表达式

根据用户的条件，**CriteriaBuilder控制查询结果**。它使用CriteriaQuery中的where()方法，该方法提供CriteriaBuilder表达式。

让我们看一下我们将在本文中使用的实体：

```java
public class Employee {

    private Integer id;
    private String name;
    private Long salary;

    // standard getters and setters
}
```

让我们看一个简单的条件查询，它将从数据库中检索“Employee”的所有行：

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

上面的Criteria查询返回所有元素的集合。下面是涉及到的主要步骤：

1.  SessionFactory对象创建Session实例
2.  Session使用getCriteriaBuilder()方法返回CriteriaBuilder的一个实例
3.  CriteriaBuilder使用createQuery()方法，这将创建进一步返回Query实例的CriteriaQuery对象
4.  最后我们调用getResult()方法获取保存结果的查询对象

让我们看一下CriteriaQuery中的另一个表达式：

```java
cr.select(root).where(cb.gt(root.get("salary"), 50000));
```

对于其结果，上述查询返回工资超过50000的员工集。

## 3. JPQL

[JPQL](https://www.baeldung.com/spring-data-jpa-query)代表Java持久性查询语言。Spring Data提供了多种创建和执行查询的方法，JPQL就是其中之一。它使用Spring中的@Query注解定义查询以执行JPQL和原生SQL查询。**默认情况下，查询定义使用JPQL**。

我们使用@Query注解在Spring中定义SQL查询。**由@Query注解定义的任何查询都比使用@NamedQuery注解的命名查询具有更高的优先级**。

### 3.1 使用JPQL查询

让我们使用JPQL构建一个动态查询：

```java
@Query(value = "SELECT e FROM Employee e")
List<Employee> findAllEmployees(Sort sort);
```

对于具有查询参数的JPQL查询，Spring Data按照与方法声明相同的顺序将方法参数传递给查询。让我们看几个将方法参数传递到查询中的示例：

```java
@Query("SELECT e FROM Employee e WHERE e.salary = ?1")
Employee findAllEmployeesWithSalary(Long salary);
```

```java
@Query("SELECT e FROM Employee e WHERE e.name = ?1 and e.salary = ?2")
Employee findEmployeeByNameAndSalary(String name, Long salary);
```

对于上面的后一个查询，name方法参数作为索引1的查询参数传递，salary参数作为索引2查询参数传递。

### 3.2 使用JPQL原生查询

我们可以使用原生查询直接在我们的数据库中执行这些SQL查询，这些查询引用真实的数据库和表对象。**我们需要将nativeQuery属性的值设置为true以定义原生SQL查询。原生SQL查询将在注解的value属性中定义**。

让我们看一个原生查询，它显示要作为查询参数传递的索引参数：

```java
@Query(value = "SELECT * FROM Employee e WHERE e.salary = ?1", nativeQuery = true)
Employee findEmployeeBySalaryNative(Long salary);
```

**使用命名参数可使查询更易于阅读，并且在重构的情况下更不容易出错**。让我们看一下JPQL和原生格式的简单命名查询的示例：

```java
@Query("SELECT e FROM Employee e WHERE e.name = :name and e.salary = :salary")
Employee findUserByEmployeeNameAndSalaryNamedParameters(@Param("name") String employeeName,
                                                        @Param("salary") Long employeeSalary);
```

方法参数使用命名参数传递给查询。我们可以通过在Repository方法声明中使用@Param注解来定义命名查询。因此，**@Param注解必须具有与相应的JPQL或SQL查询名称相匹配的字符串值**。

```java
@Query(value = "SELECT * FROM Employee e WHERE e.name = :name and e.salary = :salary", nativeQuery = true)
Employee findUserByNameAndSalaryNamedParamsNative(@Param("name") String employeeName,
                                                  @Param("salary") Long employeeSalary);
```

## 4. HQL

[HQL](https://www.baeldung.com/hibernate-named-query)代表Hibernate查询语言。它是一种**类似于SQL的面向对象语言**，我们可以使用它来查询数据库。但是，它的主要的缺点是代码的不可读性。我们可以将我们的查询定义为命名查询，然后将它们放置在访问数据库的实际代码中。

### 4.1 使用Hibernate命名查询

命名查询使用预定义的、不可更改的查询字符串定义查询。这些查询是快速失败的，因为它们在会话工厂的创建过程中得到验证。让我们使用org.hibernate.annotations.NamedQuery注解定义一个命名查询：

```java
@NamedQuery(name = "Employee_FindByEmployeeId", query = "from Employee where id = :id")
```

每个@NamedQuery注解仅将自身附加到一个实体类。我们可以使用@NamedQueries注解对一个实体的多个命名查询进行分组：

```java
@NamedQueries({
    @NamedQuery(name = "Employee_findByEmployeeId", query = "from Employee where id = :id"), 
    @NamedQuery(name = "Employee_findAllByEmployeeSalary", query = "from Employee where salary = :salary")
})
```

### 4.2 存储过程和表达式

总之，我们可以使用@NamedNativeQuery注解来调用存储过程和函数：

```java
@NamedNativeQuery(
    name = "Employee_FindByEmployeeId", 
    query = "select * from employee emp where id=:id", 
    resultClass = Employee.class)
```

## 5. Criteria查询相对于HQL和JPQL查询的优势

**与HQL相比，Criteria查询的主要优势是优雅、干净、面向对象的API**。因此，我们可以在编译期间检测Criteria API中的错误。

此外，JPQL查询和Criteria查询具有相同的性能和效率。

**与HQL和JPQL相比，Criteria查询更灵活，并为编写动态查询提供更好的支持**。

但是HQL和JPQL提供了Criteria查询无法提供的原生查询支持，这是Criteria查询的缺点之一。

我们可以使用JPQL原生查询轻松编写复杂的连接。而在使用Criteria API应用相同的查询时，它变得难以管理。

## 6. 总结

在本文中，我们主要了解了Hibernate和JPA中的Criteria查询、JPQL查询和HQL查询的基础知识。此外，我们还学习了如何使用这些查询以及每种方法的优缺点。