## 1. 概述

本文说明了如何在JavaPersistence API 中实现分页。

它解释了如何使用基本 JQL 和类型更安全的基于 Criteria 的 API 进行分页，讨论了每种实现的优点和已知问题。

## 延伸阅读：

## [使用 Spring REST 和 AngularJS 表进行分页](https://www.baeldung.com/pagination-with-a-spring-rest-api-and-an-angularjs-table)

广泛了解如何使用 Spring 实现带有分页的简单 API，以及如何使用 AngularJS 和 UI Grid 使用它。

[阅读更多](https://www.baeldung.com/pagination-with-a-spring-rest-api-and-an-angularjs-table)→

## [Spring JPA——多数据库](https://www.baeldung.com/spring-data-jpa-multiple-databases)

如何设置 Spring Data JPA 以使用多个独立的数据库。

[阅读更多](https://www.baeldung.com/spring-data-jpa-multiple-databases)→

## [Spring Data JPA @Query](https://www.baeldung.com/spring-data-jpa-query)

了解如何使用 Spring Data JPA 中的 @Query 注解来使用 JPQL 和本机 SQL 定义自定义查询。

[阅读更多](https://www.baeldung.com/spring-data-jpa-query)→

## 2. 使用 JQL 和setFirstResult()、setMaxResults() API进行分页

实现分页的最简单方法是使用Java 查询语言——创建一个查询并通过setMaxResults和 setFirstResult对其进行配置：

```java
Query query = entityManager.createQuery("From Foo");
int pageNumber = 1;
int pageSize = 10;
query.setFirstResult((pageNumber-1)  pageSize); 
query.setMaxResults(pageSize);
List <Foo> fooList = query.getResultList();
```

API 很简单：

-   setFirstResult(int ) : 设置结果集中的偏移位置开始分页
-   setMaxResults(int)：设置页面中应包含的最大实体数

### 2.1. 总计数和最后一页

对于更完整的分页解决方案，我们还需要获取总结果数：

```java
Query queryTotal = entityManager.createQuery
    ("Select count(f.id) from Foo f");
long countResult = (long)queryTotal.getSingleResult();
```

计算最后一页也很有用：

```java
int pageSize = 10;
int pageNumber = (int) ((countResult / pageSize) + 1);
```

请注意，这种获取结果集总计数的方法确实需要额外的查询(用于计数)。

## 3. 使用实体的 Id 使用 JQL 进行分页

一个简单的替代分页策略是首先检索完整的 ID，然后——基于这些——检索完整的实体。这允许更好地控制实体获取——但这也意味着它需要加载整个表来检索 id：

```java
Query queryForIds = entityManager.createQuery(
  "Select f.id from Foo f order by f.lastName");
List<Integer> fooIds = queryForIds.getResultList();
Query query = entityManager.createQuery(
  "Select f from Foo e where f.id in :ids");
query.setParameter("ids", fooIds.subList(0,10));
List<Foo> fooList = query.getResultList();
```

最后，还要注意它需要 2 个不同的查询来检索完整结果。

## 4. 使用 Criteria API 的 JPA 分页

接下来，让我们看看如何利用 JPA Criteria API来实现分页：

```java
int pageSize = 10;
CriteriaBuilder criteriaBuilder = entityManager
  .getCriteriaBuilder();
CriteriaQuery<Foo> criteriaQuery = criteriaBuilder
  .createQuery(Foo.class);
Root<Foo> from = criteriaQuery.from(Foo.class);
CriteriaQuery<Foo> select = criteriaQuery.select(from);
TypedQuery<Foo> typedQuery = entityManager.createQuery(select);
typedQuery.setFirstResult(0);
typedQuery.setMaxResults(pageSize);
List<Foo> fooList = typedQuery.getResultList();
```

当目的是创建动态的、故障安全的查询时，这很有用。与“硬编码”、“基于字符串”的 JQL 或 HQL 查询相比，JPA Criteria减少了运行时故障，因为编译器会动态检查查询错误。

使用 JPA Criteria获取实体总数非常简单：

```java
CriteriaQuery<Long> countQuery = criteriaBuilder
  .createQuery(Long.class);
countQuery.select(criteriaBuilder.count(
  countQuery.from(Foo.class)));
Long count = entityManager.createQuery(countQuery)
  .getSingleResult();
```

最终结果是一个完整的分页解决方案，使用 JPA Criteria API：

```java
int pageNumber = 1;
int pageSize = 10;
CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

CriteriaQuery<Long> countQuery = criteriaBuilder
  .createQuery(Long.class);
countQuery.select(criteriaBuilder
  .count(countQuery.from(Foo.class)));
Long count = entityManager.createQuery(countQuery)
  .getSingleResult();

CriteriaQuery<Foo> criteriaQuery = criteriaBuilder
  .createQuery(Foo.class);
Root<Foo> from = criteriaQuery.from(Foo.class);
CriteriaQuery<Foo> select = criteriaQuery.select(from);

TypedQuery<Foo> typedQuery = entityManager.createQuery(select);
while (pageNumber < count.intValue()) {
    typedQuery.setFirstResult(pageNumber - 1);
    typedQuery.setMaxResults(pageSize);
    System.out.println("Current page: " + typedQuery.getResultList());
    pageNumber += pageSize;
}
```

## 5.总结

本文探讨了 JPA 中可用的基本分页选项。

有些有缺点——主要与查询性能有关，但这些通常被改进的控制和整体灵活性所抵消。