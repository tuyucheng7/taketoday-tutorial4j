## 1. 概述

将 HQL 和 SQL 分散在数据访问对象中的一个主要缺点是它使代码不可读。因此，将所有 HQL 和 SQL 集中在一个地方并在实际数据访问代码中仅使用它们的引用可能是有意义的。幸运的是，Hibernate 允许我们使用命名查询来做到这一点。

命名查询是静态定义的查询，具有预定义的不可更改的查询字符串。它们在创建会话工厂时得到验证，从而使应用程序在出现错误时快速失败。

在本文中，我们将了解如何使用@NamedQuery和@NamedNativeQuery注解来定义和使用Hibernate 命名查询。

## 2.实体

让我们首先看看我们将在本文中使用的实体：

```java
@Entity
public class DeptEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String employeeNumber;

    private String designation;

    private String name;

    @ManyToOne
    private Department department;

    // getters and setters
}
```

在我们的示例中，我们将根据员工编号检索员工。

## 3.命名查询

要将其定义为命名查询，我们将使用org.hibernate.annotations.NamedQuery注解。它使用 Hibernate 功能扩展了 javax .persistence.NamedQuery 。

我们将其定义为DeptEmployee类的注解：

```java
@org.hibernate.annotations.NamedQuery(name = "DeptEmployee_findByEmployeeNumber", 
  query = "from DeptEmployee where employeeNumber = :employeeNo")

```

重要的是要注意每个 @NamedQuery 注解都恰好附加到一个实体类或映射的超类。但是， 由于命名查询的范围是整个持久化单元，我们应该仔细选择查询名称以避免冲突。 我们通过使用实体名称作为前缀来实现这一点。

如果我们对一个实体有多个命名查询，我们将使用 @NamedQueries 注解对这些查询进行分组：

```java
@org.hibernate.annotations.NamedQueries({
    @org.hibernate.annotations.NamedQuery(name = "DeptEmployee_FindByEmployeeNumber", 
      query = "from DeptEmployee where employeeNumber = :employeeNo"),
    @org.hibernate.annotations.NamedQuery(name = "DeptEmployee_FindAllByDesgination", 
      query = "from DeptEmployee where designation = :designation"),
    @org.hibernate.annotations.NamedQuery(name = "DeptEmployee_UpdateEmployeeDepartment", 
      query = "Update DeptEmployee set department = :newDepartment where employeeNumber = :employeeNo"),
...
})
```

请注意，HQL 查询可以是 DML 样式的操作。因此，它不必只是一个 select语句。例如，我们可以像上面的 DeptEmployee_UpdateEmployeeDesignation那样有一个更新查询。

### 3.1. 配置查询功能

我们可以使用@NamedQuery 注解设置各种查询功能 。让我们看一个例子：

```java
@org.hibernate.annotations.NamedQuery(
  name = "DeptEmployee_FindAllByDepartment", 
  query = "from DeptEmployee where department = :department",
  timeout = 1,
  fetchSize = 10
)
```

在这里，我们配置了超时间隔和获取大小。除了这两个之外，我们还可以设置以下功能：

-   可缓存——查询(结果)是否可缓存
-   cacheMode – 用于此查询的缓存模式；这可以是GET、IGNORE、NORMAL、PUT或 REFRESH之一
-   cacheRegion – 如果查询结果是可缓存的，命名要使用的查询缓存区域
-   评论——添加到生成的 SQL 查询的评论；针对 DBA
-   flushMode – 此查询的刷新模式， ALWAYS、AUTO、COMMIT、MANUAL或 PERSISTENCE_CONTEXT之一

### 3.2. 使用命名查询

现在我们已经定义了命名查询，让我们用它来检索员工：

```java
Query<DeptEmployee> query = session.createNamedQuery("DeptEmployee_FindByEmployeeNumber", 
  DeptEmployee.class);
query.setParameter("employeeNo", "001");
DeptEmployee result = query.getSingleResult();

```

在这里，我们使用了createNamedQuery 方法。它采用查询的名称并返回一个org.hibernate.query.Query 对象。

## 4.命名本机查询

除了 HQL 查询，我们还可以将原生 SQL 定义为命名查询。为此，我们可以使用@NamedNativeQuery注解。虽然它类似于 @NamedQuery，但它需要更多的配置。

让我们用一个例子来探索这个注解：

```java
@org.hibernate.annotations.NamedNativeQueries(
    @org.hibernate.annotations.NamedNativeQuery(name = "DeptEmployee_GetEmployeeByName", 
      query = "select  from deptemployee emp where name=:name",
      resultClass = DeptEmployee.class)
)
```

由于这是一个本地查询，我们必须告诉 Hibernate 将结果映射到哪个实体类。因此，我们使用 resultClass 属性来执行此操作。

映射结果的另一种方法是使用 resultSetMapping 属性。在这里，我们可以指定预定义的SQLResultSetMapping的名称。

请注意，我们只能使用 resultClass和resultSetMapping之一。

### 4.1. 使用命名本机查询

要使用命名本机查询，我们可以使用Session.createNamedQuery()：

```java
Query<DeptEmployee> query = session.createNamedQuery("DeptEmployee_FindByEmployeeName", DeptEmployee.class);
query.setParameter("name", "John Wayne");
DeptEmployee result = query.getSingleResult();
```

或者 Session.getNamedNativeQuery()：

```java
NativeQuery query = session.getNamedNativeQuery("DeptEmployee_FindByEmployeeName");
query.setParameter("name", "John Wayne");
DeptEmployee result = (DeptEmployee) query.getSingleResult();
```

这两种方法之间的唯一区别是返回类型。第二种方法返回 NativeQuery， 它是Query的子类 。

## 5. 存储过程和函数

我们也可以使用@NamedNativeQuery 注解来定义对存储过程和函数的调用：

```java
@org.hibernate.annotations.NamedNativeQuery(
  name = "DeptEmployee_UpdateEmployeeDesignation", 
  query = "call UPDATE_EMPLOYEE_DESIGNATION(:employeeNumber, :newDesignation)", 
  resultClass = DeptEmployee.class)
```

请注意，虽然这是一个更新查询，但我们使用了 resultClass 属性。这是因为 Hibernate 不支持纯本机标量查询。解决该问题的方法是设置 resultClass 或 resultSetMapping 。

## 六. 总结

在本文中，我们了解了如何定义和使用命名 HQL 和本机查询。