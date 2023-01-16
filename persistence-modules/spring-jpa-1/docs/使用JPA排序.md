## 1. 概述

本文说明了JPA 可用于排序的各种方式。

## 延伸阅读：

## [Spring Data JPA @Query](https://www.baeldung.com/spring-data-jpa-query)

了解如何使用 Spring Data JPA 中的 @Query 注解来使用 JPQL 和本机 SQL 定义自定义查询。

[阅读更多](https://www.baeldung.com/spring-data-jpa-query)→

## [JPA 属性转换器](https://www.baeldung.com/jpa-attribute-converters)

查看使用属性转换器将 JDBC 类型映射到 JPA 中的Java类。

[阅读更多](https://www.baeldung.com/jpa-attribute-converters)→

## 2. 使用 JPA / JQL API 排序

使用 JQL 排序是在Order By子句的帮助下完成的：

```java
String jql ="Select f from Foo as f order by f.id";
Query query = entityManager.createQuery (jql);
```

基于此查询，JPA 生成以下简单的SQL 语句：

```java
Hibernate: select foo0_.id as id1_4_, foo0_.name as name2_4_ 
    from Foo foo0_ order by foo0_.id
```

请注意，JQL 字符串中的 SQL 关键字不区分大小写，但实体名称及其属性区分大小写。

### 2.1. 设置排序顺序

默认情况下，排序顺序为升序，但可以在 JQL 字符串中明确设置。就像在纯 SQL 中一样，排序选项是asc和desc：

```java
String jql = "Select f from Foo as f order by f.id desc";
Query sortQuery = entityManager.createQuery(jql);
```

生成的SQL 查询将包含订单方向：

```java
Hibernate: select foo0_.id as id1_4_, foo0_.name as name2_4_ 
    from Foo foo0_ order by foo0_.id desc
```

### 2.2. 按多个属性排序

要按多个属性排序，将这些属性添加到JQL 字符串的 order by子句中：

```java
String jql ="Select f from Foo as f order by f.name asc, f.id desc";
Query sortQuery = entityManager.createQuery(jql);
```

两种排序条件都会出现在生成的SQL查询语句中：

```java
Hibernate: select foo0_.id as id1_4_, foo0_.name as name2_4_ 
    from Foo foo0_ order by foo0_.name asc, foo0_.id desc
```

### 2.3. 设置空值的排序优先级

空值的默认优先级是特定于数据库的，但这可以通过HQL 查询字符串中的NULLS FIRST或NULLS LAST子句进行自定义。

这是一个简单的例子——按名称Foo降序排列，并将Null放在末尾：

```java
Query sortQuery = entityManager.createQuery
    ("Select f from Foo as f order by f.name desc NULLS LAST");
```

生成的 SQL 查询包括is null the 1 else 0 end 子句(第 3 行)：

```sql
Hibernate: select foo0_.id as id1_4_, foo0_.BAR_ID as BAR_ID2_4_, 
    foo0_.bar_Id as bar_Id2_4_, foo0_.name as name3_4_,from Foo foo0_ order 
    by case when foo0_.name is null then 1 else 0 end, foo0_.name desc
```

### 2.4. 排序一对多关系

跳过基本示例，现在让我们看一个涉及在一对多关系中对实体进行排序的用例–包含Foo实体集合的Bar 。

我们想要对Bar实体及其Foo实体集合进行排序——JPA 对于此任务特别简单：

1.  对集合进行排序：在Bar实体 中的Foo集合之前添加一个

    OrderBy

    注解：

    

    

    

    

    ```java
    @OrderBy("name ASC")
    List <Foo> fooList;
    ```

2.  对包含集合的实体进行排序：

    ```java
    String jql = "Select b from Bar as b order by b.id";
    Query barQuery = entityManager.createQuery(jql);
    List<Bar> barList = barQuery.getResultList();
    ```

请注意，@OrderBy注解是可选的，但我们在这种情况下使用它，因为我们想要对每个Bar的Foo集合进行排序。

让我们看一下发送到 RDMS的SQL 查询：

```sql
Hibernate: select bar0_.id as id1_0_, bar0_.name as name2_0_ from Bar bar0_ order by bar0_.id

Hibernate: 
select foolist0_.BAR_ID as BAR_ID2_0_0_, foolist0_.id as id1_4_0_, 
foolist0_.id as id1_4_1_, foolist0_.BAR_ID as BAR_ID2_4_1_, 
foolist0_.bar_Id as bar_Id2_4_1_, foolist0_.name as name3_4_1_ 
from Foo foolist0_ 
where foolist0_.BAR_ID=? order by foolist0_.name asc

```

第一个查询对父Bar实体进行排序。生成第二个查询以对属于Bar的子Foo实体的集合进行排序。

## 3. 使用 JPA Criteria Query Object API 排序

使用 JPA Criteria – orderBy方法是设置所有排序参数的“一站式”替代方法：可以设置排序方向和排序依据的属性。以下是该方法的 API：

-   orderBy ( CriteriaBuilder.asc ): 按升序排序。
-   orderBy ( CriteriaBuilder.desc )：降序排序。

每个Order实例都是通过 C riteriaBuilder对象通过其asc或desc方法创建的。

这是一个简单的示例——按名称对Foos进行排序：

```java
CriteriaQuery<Foo> criteriaQuery = criteriaBuilder.createQuery(Foo.class);
Root<Foo> from = criteriaQuery.from(Foo.class);
CriteriaQuery<Foo> select = criteriaQuery.select(from);
criteriaQuery.orderBy(criteriaBuilder.asc(from.get("name")));
```

get方法的参数区分大小写，因为它需要与属性名称相匹配。

与简单的 JQL 不同，JPA Criteria Query Object API在查询中强制执行明确的顺序方向。请注意，在此代码片段的最后一行中，criteriaBuilder对象通过调用其asc方法指定要升序的排序顺序。

执行上述代码时，JPA 会生成如下所示的 SQL 查询。JPA Criteria Object 生成带有显式asc子句的 SQL 语句：

```sql
Hibernate: select foo0_.id as id1_4_, foo0_.name as name2_4_
    from Foo foo0_ order by foo0_.name asc
```

### 3.1. 按多个属性排序

要按多个属性排序，只需将一个Order实例传递给每个要排序的属性的orderBy方法。

这是一个简单的例子——按name和id排序，分别按asc和desc顺序：

```java
CriteriaQuery<Foo> criteriaQuery = criteriaBuilder.createQuery(Foo.class);
Root<Foo> from = criteriaQuery.from(Foo.class); 
CriteriaQuery<Foo> select = criteriaQuery.select(from); 
criteriaQuery.orderBy(criteriaBuilder.asc(from.get("name")),
    criteriaBuilder.desc(from.get("id")));
```

对应的SQL查询如下所示：

```java
Hibernate: select foo0_.id as id1_4_, foo0_.name as name2_4_ 
    from Foo foo0_ order by foo0_.name asc, foo0_.id desc
```

## 4. 总结

本文探讨了JavaPersistence API 中的排序选项，适用于简单实体以及一对多关系中的实体。这些方法将排序工作的负担委托给数据库层。