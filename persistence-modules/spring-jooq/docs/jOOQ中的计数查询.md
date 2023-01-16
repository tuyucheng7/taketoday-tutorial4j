## 1. 概述

在本教程中，我们将演示如何使用jOOQ Object-Oriented Querying [(](https://www.baeldung.com/jooq-with-spring)也称为jOOQ)执行计数查询。jOOQ 是一个流行的Java数据库库，可帮助你用Java编写类型安全的 SQL 查询。

## 2.jOOQ

jOOQ 是 ORM 的替代品。与大多数其他 ORM 不同，jOOQ 是以关系模型为中心的，而不是以领域模型为中心的。例如，[Hibernate帮助我们编写Java代码，然后自动将其转换为 SQL。](https://www.baeldung.com/learn-jpa-hibernate)然而，jOOQ 允许我们使用 SQL 在数据库中创建关系对象，然后它生成Java代码来映射到这些对象。

## 3.Maven依赖

在本教程中我们需要[jooq](https://search.maven.org/classic/#search|ga|1|g%3A"org.jooq") 模块：

```xml
<dependency> 
    <groupId>org.jooq</groupId> 
    <artifactId>jooq</artifactId> 
    <version>3.14.8</version> 
</dependency>
```

## 4.计数查询

假设我们的数据库中有一个作者表。author表包含一个 id、first_name 和 last_name。

可以通过几种不同的方式运行计数查询。

### 4.1. 获取次数 

[DSL.fetchCount](https://www.jooq.org/javadoc/latest/org.jooq/org/jooq/DSLContext.html#fetchCount(org.jooq.Select))有不止一种方法来计算表中的记录数。

首先我们看一下fetchCount (Table<?> table)方法统计记录条数：

```java
int count = dsl.fetchCount(DSL.selectFrom(AUTHOR));
Assert.assertEquals(3, count);

```

接下来，让我们尝试使用fetchCount (Table<?> table)方法与selectFrom方法和where子句来计算记录数：

```java
int count = dsl.fetchCount(DSL.selectFrom(AUTHOR)
  .where(AUTHOR.FIRST_NAME.equalIgnoreCase("Bryan")));
Assert.assertEquals(1, count);
```

现在，让我们尝试使用fetchCount (Table<?> table, Condition condition)方法来统计记录数：

```java
int count = dsl.fetchCount(AUTHOR, AUTHOR.FIRST_NAME.equalIgnoreCase("Bryan"));
Assert.assertEquals(1, count);

```

我们还可以对多个条件使用fetchCount (Table<?> table, Collection<? extends Condition> conditions)方法：

```java
Condition firstCond = AUTHOR.FIRST_NAME.equalIgnoreCase("Bryan");
Condition secondCond = AUTHOR.ID.notEqual(1);
List<Condition> conditions = new ArrayList<>();
conditions.add(firstCond);
conditions.add(secondCond);
int count = dsl.fetchCount(AUTHOR, conditions);
Assert.assertEquals(1, count);

```

在这种情况下，我们将过滤条件添加到列表并将其提供给fetchCount方法。

fetchCount方法还允许在多个条件下使用可变参数：

```java
Condition firstCond = AUTHOR.FIRST_NAME.equalIgnoreCase("Bryan");
Condition secondCond = AUTHOR.ID.notEqual(1);
int count = dsl.fetchCount(AUTHOR, firstCond, secondCond);
Assert.assertEquals(1, count);

```

或者，我们可以使用[and(Condition condition)](https://www.jooq.org/javadoc/latest/org.jooq/org/jooq/Condition.html#and(org.jooq.Condition)) 来组合内联条件：

```java
int count = dsl.fetchCount(AUTHOR, AUTHOR.FIRST_NAME.equalIgnoreCase("Bryan").and(AUTHOR.ID.notEqual(1)));
Assert.assertEquals(1, count);
```

### 4.2. 数数

让我们尝试使用[count](https://www.jooq.org/javadoc/latest/org.jooq/org/jooq/impl/DSL.html#count())方法来获取可用记录的数量：

```java
int count = dsl.select(DSL.count()).from(AUTHOR)
  .fetchOne(0, int.class);
Assert.assertEquals(3, count);

```

### 4.3. 选择计数

现在，让我们尝试使用[selectCount](https://www.jooq.org/javadoc/latest/org.jooq/org/jooq/DSLContext.html#selectCount())方法来获取可用记录的计数：

```java
int count = dsl.selectCount().from(AUTHOR)
  .where(AUTHOR.FIRST_NAME.equalIgnoreCase("Bryan"))
  .fetchOne(0, int.class);
Assert.assertEquals(1, count);
```

### 4.4. 简单选择

我们还可以使用一个简单的[select](https://www.jooq.org/javadoc/latest/org.jooq/org/jooq/DSLContext.html#select(java.util.Collection)) 方法来获取可用记录的数量：

```java
int count = dsl.select().from(AUTHOR).execute();
Assert.assertEquals(3, count);
```

### 4.5. 用groupBy计数

让我们尝试使用select 和count方法来查找按字段分组的记录数：

```java
Result<Record2<String, Integer>> result = dsl.select(AUTHOR.FIRST_NAME, DSL.count())
  .from(AUTHOR).groupBy(AUTHOR.FIRST_NAME).fetch();
Assert.assertEquals(3, result.size());
Assert.assertEquals(result.get(0).get(0), "Bert");
Assert.assertEquals(result.get(0).get(1), 1);
```

## 5.总结

在本文中，我们研究了如何在 jOOQ 中执行计数查询。

我们已经了解了使用selectCount、count、fetchCount、select和count with groupBy方法来计算记录数。
