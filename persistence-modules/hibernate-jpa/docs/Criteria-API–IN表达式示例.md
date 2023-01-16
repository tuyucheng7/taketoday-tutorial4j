## 1. 概述

我们经常遇到需要根据单值属性是否是给定集合的成员来查询实体的问题。

在本教程中，我们将学习如何借助 [Criteria API](https://www.baeldung.com/hibernate-criteria-queries)解决此问题。

## 2. 示例实体

在我们开始之前，让我们看一下我们将在我们的文章中使用的实体。

我们有一个 DeptEmployee类，它与Department类具有 [多对一的关系](https://www.baeldung.com/hibernate-one-to-many)：

```java
@Entity
public class DeptEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String title;

    @ManyToOne
    private Department department;
}
```

此外，映射到多个DeptEmployees的Department 实体：

```java
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String name;

    @OneToMany(mappedBy="department")
    private List<DeptEmployee> employees;
}
```

## 3. CriteriaBuilder.In

首先，让我们使用 CriteriaBuilder 接口。in ()方法接受一个Expression并返回一个CriteriaBuilder.In 类型的 新Predicate 。它可用于测试给定表达式是否包含在值列表中：

```java
CriteriaQuery<DeptEmployee> criteriaQuery = 
  criteriaBuilder.createQuery(DeptEmployee.class);
Root<DeptEmployee> root = criteriaQuery.from(DeptEmployee.class);
In<String> inClause = criteriaBuilder.in(root.get("title"));
for (String title : titles) {
    inClause.value(title);
}
criteriaQuery.select(root).where(inClause);
```

## 4.表达式.In

或者，我们可以使用一组来自[Expression](https://javaee.github.io/javaee-spec/javadocs/javax/persistence/criteria/Expression.html)接口的重载in()方法 ：

```java
criteriaQuery.select(root)
  .where(root.get("title")
  .in(titles));
```

与 CriteriaBuilder 形成对比 。in()， Expression.in() 接受值的集合。正如我们所看到的，它也稍微简化了我们的代码。

## 5. IN表达式使用子查询

到目前为止，我们已经使用了具有预定义值的集合。现在，让我们看一个从子查询的输出派生集合的示例。

例如，我们可以获取属于某个部门的所有DeptEmployee ，名称中包含指定的关键字：

```java
Subquery<Department> subquery = criteriaQuery.subquery(Department.class);
Root<Department> dept = subquery.from(Department.class);
subquery.select(dept)
  .distinct(true)
  .where(criteriaBuilder.like(dept.get("name"), "%" + searchKey + "%"));

criteriaQuery.select(emp)
  .where(criteriaBuilder.in(emp.get("department")).value(subquery));
```

在这里，我们创建了一个子查询，然后将其作为表达式传递到 value()以搜索Department实体。

## 六. 总结

在这篇快速文章中，我们了解了使用 Criteria API 实现 IN 操作的不同方法。我们还探讨了如何将 Criteria API 与子查询一起使用。