## 1. 简介

使用 JPA 构建查询并不困难；然而，我们有时会忘记一些简单的事情，但却会产生巨大的影响。

其中之一是 JPA 查询参数，这就是我们将在本教程中关注的内容。

## 2. 什么是查询参数？

让我们首先解释什么是查询参数。

查询参数是一种构建和执行参数化查询的方法。所以，而不是：

```sql
SELECT  FROM employees e WHERE e.emp_number = '123';
```

我们会这样做：

```sql
SELECT  FROM employees e WHERE e.emp_number = ?;
```

通过使用 JDBC 准备语句，我们需要在执行查询之前设置参数：

```java
pStatement.setString(1, 123);
```

## 3. 为什么要使用查询参数？

除了使用查询参数，我们还可以使用文字，尽管这不是推荐的方法，正如我们现在将要看到的那样。

让我们使用 JPA API 重写之前的查询以通过emp_number获取员工，但我们将使用文字而不是使用参数，以便我们可以清楚地说明情况：

```java
String empNumber = "A123";
TypedQuery<Employee> query = em.createQuery(
  "SELECT e FROM Employee e WHERE e.empNumber = '" + empNumber + "'", Employee.class);
Employee employee = query.getSingleResult();
```

这种方法有一些缺点：

-   嵌入参数会带来安全风险，使我们容易受到[JPQL 注入攻击](https://www.baeldung.com/sql-injection)。攻击者可能会注入任何意外且可能危险的 JPQL 表达式，而不是预期值。
-   根据我们使用的 JPA 实现以及我们应用程序的启发式方法，查询缓存可能会耗尽。每次我们将新查询与每个新值/参数一起使用时，都可能会构建、编译和缓存新查询。至少，它不会有效率，而且还可能导致意外的OutOfMemoryError。

## 4.JPA查询参数

与 JDBC 准备语句参数类似，JPA 指定了两种不同的方式来编写参数化查询：

-   位置参数
-   命名参数

我们可以使用位置参数或命名参数，但不能在同一个查询中混合使用它们。

### 4.1. 位置参数

使用位置参数是避免前面列出的上述问题的一种方法。

让我们看看如何在位置参数的帮助下编写这样的查询：

```java
TypedQuery<Employee> query = em.createQuery(
  "SELECT e FROM Employee e WHERE e.empNumber = ?1", Employee.class);
String empNumber = "A123";
Employee employee = query.setParameter(1, empNumber).getSingleResult();
```

正如我们在前面的示例中看到的那样，我们通过键入一个问号，后跟一个正整数来在查询中声明这些参数。我们将从 1开始并向前移动，每次递增 1。

我们可能会在同一个查询中多次使用同一个参数，这使得这些参数更类似于命名参数。

参数编号是一个非常有用的特性，因为它提高了可用性、可读性和维护性。

值得一提的是，原生 SQL 查询也支持位置参数绑定。

### 4.2. 集合值位置参数

如前所述，我们还可以使用集合值参数：

```java
TypedQuery<Employee> query = entityManager.createQuery(
  "SELECT e FROM Employee e WHERE e.empNumber IN (?1)" , Employee.class);
List<String> empNumbers = Arrays.asList("A123", "A124");
List<Employee> employees = query.setParameter(1, empNumbers).getResultList();
```

### 4.3. 命名参数

命名参数与位置参数非常相似；然而，通过使用它们，我们使参数更明确并且查询变得更具可读性：

```java
TypedQuery<Employee> query = em.createQuery(
  "SELECT e FROM Employee e WHERE e.empNumber = :number" , Employee.class);
String empNumber = "A123";
Employee employee = query.setParameter("number", empNumber).getSingleResult();
```

前面的示例查询与第一个示例查询相同，但我们使用了:number，一个命名参数，而不是?1。

我们可以看到我们用冒号声明了参数，后跟一个字符串标识符(JPQL 标识符)，它是我们将在运行时设置的实际值的占位符。在执行查询之前，我们必须通过发出setParameter 方法来设置一个或多个参数。

值得注意的一件有趣的事情是TypedQuery支持方法链接，这在必须设置多个参数时变得非常有用。

让我们继续使用两个命名参数创建先前查询的变体来说明方法链接：

```java
TypedQuery<Employee> query = em.createQuery(
  "SELECT e FROM Employee e WHERE e.name = :name AND e.age = :empAge" , Employee.class);
String empName = "John Doe";
int empAge = 55;
List<Employee> employees = query
  .setParameter("name", empName)
  .setParameter("empAge", empAge)
  .getResultList();
```

在这里，我们检索所有具有给定姓名和年龄的员工。正如我们清楚地看到的，并且人们可能会预料到，我们可以构建具有多个参数的查询，并且可以根据需要多次出现这些参数。

如果出于某种原因我们确实需要在同一个查询中多次使用同一个参数，我们只需要通过发出“ setParameter ”方法来设置一次。在运行时，指定的值将替换每次出现的参数。

最后，值得一提的是，Java Persistence API 规范并未强制要求本机查询支持命名参数。即使某些实现(如 Hibernate)确实支持它，我们也需要考虑到如果我们确实使用它，查询将不会那么可移植。

### 4.4. 集合值命名参数

为清楚起见，我们还演示它如何与集合值参数一起使用：

```java
TypedQuery<Employee> query = entityManager.createQuery(
  "SELECT e FROM Employee e WHERE e.empNumber IN (:numbers)" , Employee.class);
List<String> empNumbers = Arrays.asList("A123", "A124");
List<Employee> employees = query.setParameter("numbers", empNumbers).getResultList();
```

正如我们所见，它的工作方式与位置参数类似。

## 5.条件查询参数

可以使用[JPA Criteria API](https://www.baeldung.com/hibernate-criteria-queries-metamodel)构建 JPA 查询，[Hibernate 的官方文档对其](https://docs.jboss.org/hibernate/orm/5.2/topical/html_single/metamodelgen/MetamodelGenerator.html)进行了非常详细的解释。

在这种类型的查询中，我们使用对象而不是名称或索引来表示参数。

让我们再次构建相同的查询，但这次使用 Criteria API 来演示在处理CriteriaQuery时如何处理查询参数：

```java
CriteriaBuilder cb = em.getCriteriaBuilder();

CriteriaQuery<Employee> cQuery = cb.createQuery(Employee.class);
Root<Employee> c = cQuery.from(Employee.class);
ParameterExpression<String> paramEmpNumber = cb.parameter(String.class);
cQuery.select(c).where(cb.equal(c.get(Employee_.empNumber), paramEmpNumber));

TypedQuery<Employee> query = em.createQuery(cQuery);
String empNumber = "A123";
query.setParameter(paramEmpNumber, empNumber);
Employee employee = query.getResultList();
```

对于这种类型的查询，参数的机制有点不同，因为我们使用参数对象，但本质上没有区别。

在前面的示例中，我们可以看到Employee_类的用法。我们使用 Hibernate 元模型生成器生成了这个类。这些组件是静态 JPA 元模型的一部分，它允许以强类型方式构建条件查询。

## 六. 总结

在本文中，我们重点介绍了使用 JPA 查询参数或输入参数构建查询的机制。

我们了解到我们有两种类型的查询参数，位置参数和命名参数，这取决于我们哪一种最适合我们的目标。

还值得注意的是，除了in表达式外，所有查询参数都必须是单值的。对于in表达式，我们可以使用集合值输入参数，例如数组或List，如前面的示例所示。