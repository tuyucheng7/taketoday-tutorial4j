## 1. 概述

在本教程中，我们将讨论一个非常有用的 JPA 功能 — Criteria Queries。

它使我们能够在不执行原始 SQL 的情况下编写查询，并为我们提供了对查询的一些面向对象的控制，这是 Hibernate 的主要特性之一。Criteria API 允许我们以编程方式构建一个条件查询对象，我们可以在其中应用不同类型的过滤规则和逻辑条件。

从 Hibernate 5.2 开始，Hibernate Criteria API 被弃用，新的开发集中在 JPA Criteria API。我们将探讨如何使用 Hibernate 和 JPA 来构建 Criteria Queries。

## 延伸阅读：

## [Spring Data JPA @Query](https://www.baeldung.com/spring-data-jpa-query)

了解如何使用 Spring Data JPA 中的 @Query 注解来使用 JPQL 和本机 SQL 定义自定义查询。

[阅读更多](https://www.baeldung.com/spring-data-jpa-query)→

## [Spring Data JPA 简介](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)

Spring Data JPA 与 Spring 4 简介 - Spring 配置、DAO、手动和生成的查询以及事务管理。

[阅读更多](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)→

## [使用 JPA 的 Querydsl 指南](https://www.baeldung.com/querydsl-with-jpa-tutorial)

将 Querydsl 与JavaPersistence API 结合使用的快速指南。

[阅读更多](https://www.baeldung.com/querydsl-with-jpa-tutorial)→

## 2.Maven依赖

为了说明 API，我们将使用参考 JPA 实现 Hibernate。

要使用 Hibernate，我们将确保将它的最新版本添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>   
    <version>5.3.2.Final</version>
</dependency>
```

我们可以在这里找到最新版本的 Hibernate [。](https://search.maven.org/classic/#search|gav|1|g%3A"org.hibernate" AND a%3A"hibernate-core")

## 3. 使用条件的简单示例

让我们先看看如何使用 Criteria Queries 检索数据。我们将研究如何从数据库中获取特定类的所有实例。

我们有一个Item类，代表数据库中的元组“ITEM” ：

```java
public class Item implements Serializable {

    private Integer itemId;
    private String itemName;
    private String itemDescription;
    private Integer itemPrice;

   // standard setters and getters
}
```

让我们看一个简单的条件查询，它将从数据库中检索“ITEM”的所有行：

```java
Session session = HibernateUtil.getHibernateSession();
CriteriaBuilder cb = session.getCriteriaBuilder();
CriteriaQuery<Item> cr = cb.createQuery(Item.class);
Root<Item> root = cr.from(Item.class);
cr.select(root);

Query<Item> query = session.createQuery(cr);
List<Item> results = query.getResultList();
```

上面的查询是如何获取所有项目的简单演示。让我们一步步来看：

1.  从SessionFactory对象创建Session的实例
2.  通过调用getCriteriaBuilder()方法创建 C riteriaBuilder的实例
3.  通过调用CriteriaBuilder createQuery()方法创建CriteriaQuery的实例
4.  通过调用Session 的createQuery()方法创建一个Query实例
5.  调用查询对象的getResultList()方法，它给我们结果

现在我们已经介绍了基础知识，让我们继续讨论条件查询的一些功能。

### 3.1. 使用表达式

CriteriaBuilder可用于根据特定条件限制查询结果，方法是使用CriteriaQuery where()方法并提供由CriteriaBuilder创建的表达式。

让我们看一些常用的Expressions的例子。

为了获得价格超过 1000 的物品：

```java
cr.select(root).where(cb.gt(root.get("itemPrice"), 1000));
```

接下来，获取itemPrice小于 1000 的项目：

```java
cr.select(root).where(cb.lt(root.get("itemPrice"), 1000));
```

具有itemName的项目包含Chair：

```java
cr.select(root).where(cb.like(root.get("itemName"), "%chair%"));
```

itemPrice在 100 到 200 之间的记录：

```java
cr.select(root).where(cb.between(root.get("itemPrice"), 100, 200));
```

在Skate Board、Paint和Glue中具有itemName的项目：

```java
cr.select(root).where(root.get("itemName").in("Skate Board", "Paint", "Glue"));
```

检查给定属性是否为空：

```java
cr.select(root).where(cb.isNull(root.get("itemDescription")));
```

检查给定属性是否不为空：

```java
cr.select(root).where(cb.isNotNull(root.get("itemDescription")));
```

我们还可以使用方法isEmpty()和isNotEmpty()来测试类中的列表是否为空。

此外，我们可以组合上述比较中的两个或多个。Criteria API 允许我们轻松地链接表达式：

```java
Predicate[] predicates = new Predicate[2];
predicates[0] = cb.isNull(root.get("itemDescription"));
predicates[1] = cb.like(root.get("itemName"), "chair%");
cr.select(root).where(predicates);
```

使用逻辑运算添加两个表达式：

```java
Predicate greaterThanPrice = cb.gt(root.get("itemPrice"), 1000);
Predicate chairItems = cb.like(root.get("itemName"), "Chair%");
```

具有上述定义条件的项目与Logical OR连接：

```java
cr.select(root).where(cb.or(greaterThanPrice, chairItems));
```

要获取与上述定义条件匹配的项目，请使用逻辑与：

```java
cr.select(root).where(cb.and(greaterThanPrice, chairItems));
```

### 3.2. 排序

现在我们知道了Criteria的基本用法，让我们看看 Criteria 的排序功能。

在以下示例中，我们按名称升序排列列表，然后按价格降序排列：

```java
cr.orderBy(
  cb.asc(root.get("itemName")), 
  cb.desc(root.get("itemPrice")));
```

在下一节中，我们将了解如何执行聚合函数。

### 3.3. 投影、聚合和分组函数

现在让我们看看不同的聚合函数。

获取行数：

```java
CriteriaQuery<Long> cr = cb.createQuery(Long.class);
Root<Item> root = cr.from(Item.class);
cr.select(cb.count(root));
Query<Long> query = session.createQuery(cr);
List<Long> itemProjected = query.getResultList();
```

以下是聚合函数的示例 - Average的聚合函数：

```java
CriteriaQuery<Double> cr = cb.createQuery(Double.class);
Root<Item> root = cr.from(Item.class);
cr.select(cb.avg(root.get("itemPrice")));
Query<Double> query = session.createQuery(cr);
List avgItemPriceList = query.getResultList();
```

其他有用的聚合方法是sum()、max()、min()、count()等。 

### 3.4. 标准更新

从 JPA 2.1 开始，支持使用Criteria API 执行数据库更新。

CriteriaUpdate有一个set()方法，可用于为数据库记录提供新值：

```java
CriteriaUpdate<Item> criteriaUpdate = cb.createCriteriaUpdate(Item.class);
Root<Item> root = criteriaUpdate.from(Item.class);
criteriaUpdate.set("itemPrice", newPrice);
criteriaUpdate.where(cb.equal(root.get("itemPrice"), oldPrice));

Transaction transaction = session.beginTransaction();
session.createQuery(criteriaUpdate).executeUpdate();
transaction.commit();
```

在上面的代码片段中，我们从CriteriaBuilder创建了一个CriteriaUpdate<Item>实例，并使用它的set()方法为itemPrice提供新值。为了更新多个属性，我们只需要多次调用set()方法即可。

### 3.5. 条件删除

CriteriaDelete使用Criteria API 启用删除操作。

我们只需要创建一个CriteriaDelete实例并使用where()方法应用限制：

```java
CriteriaDelete<Item> criteriaDelete = cb.createCriteriaDelete(Item.class);
Root<Item> root = criteriaDelete.from(Item.class);
criteriaDelete.where(cb.greaterThan(root.get("itemPrice"), targetPrice));

Transaction transaction = session.beginTransaction();
session.createQuery(criteriaDelete).executeUpdate();
transaction.commit();
```

## 4. 优于 HQL

在前面的部分中，我们介绍了如何使用 Criteria Queries。

显然，Criteria Queries 相对于 HQL 的主要和最有力的优势是漂亮、干净、面向对象的 API。

与普通 HQL 相比，我们可以简单地编写更灵活、动态的查询。可以使用 IDE 重构逻辑，并具有Java语言本身的所有类型安全优势。

当然，也有一些缺点，尤其是在更复杂的连接方面。

因此，我们通常必须使用最好的工具来完成这项工作——在大多数情况下可以是 Criteria API，但在某些情况下我们必须使用较低级别的工具。

## 5.总结

在本文中，我们重点介绍了 Hibernate 和 JPA 中条件查询的基础知识以及 API 的一些高级功能。