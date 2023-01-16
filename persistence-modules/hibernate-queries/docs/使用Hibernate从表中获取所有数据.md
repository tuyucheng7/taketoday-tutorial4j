## 1. 概述

在本快速教程中，我们将了解如何使用 JPQL 或 Criteria API 通过 Hibernate 从表中获取所有数据。

JPQL 为我们提供了更快、更简单的实现，同时使用 Criteria API 更加动态和健壮。

## 2.JPQL

[JPQL](https://www.baeldung.com/spring-data-jpa-query)提供了一种从表中获取所有实体的简单直接的方法。

让我们看看使用 JPQL 从表中检索所有学生会是什么样子：

```java
public List<Student> findAllStudentsWithJpql() {
    return session.createQuery("SELECT a FROM Student a", Student.class).getResultList();      
}

```

我们的 Hibernate 会话的 createQuery()方法接收一个类型化的查询字符串作为第一个参数，实体的类型作为第二个参数。我们通过调用 getResultList()方法来执行查询，该方法将结果作为类型化的List返回。

简单是这种方法的优点。JPQL 与 SQL 非常接近，因此更易于编写和理解。

## 3.标准API

Criteria [API](https://www.baeldung.com/hibernate-criteria-queries)提供了一种用于构建 JPA 查询的动态方法。

它允许我们通过实例化表示查询元素的Java对象来构建查询。如果查询是从许多可选字段构造的，那么它是一个更简洁的解决方案，因为它消除了很多字符串连接。

我们刚刚看到了使用 JPQL 的全选查询。让我们使用 Criteria API 看一下它的等价物：

```java
public List<Student> findAllStudentsWithCriteriaQuery() {
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Student> cq = cb.createQuery(Student.class);
    Root<Student> rootEntry = cq.from(Student.class);
    CriteriaQuery<Student> all = cq.select(rootEntry);

    TypedQuery<Student> allQuery = session.createQuery(all);
    return allQuery.getResultList();
}

```

首先，我们得到一个CriteriaBuilder，我们用它来创建类型化的 Criteria Query。稍后，我们设置查询的根条目。最后，我们使用getResultList()方法执行它。

现在，这种方法类似于我们之前所做的。 但是，它使我们能够完全访问Java语言，以便在制定查询时阐明更细微的差别。

除了相似之外，JPQL 查询和基于 JPA 条件的查询的性能相当。

## 4. 总结

在本文中，我们演示了如何使用 JPQL 或 Criteria API 从表中获取所有实体。