## 1. 简介

在这个简短的教程中，我们将学习如何使用 JPA 设置 SQL 表名。

我们将介绍 JPA 如何生成默认名称以及如何提供自定义名称。

## 2. 默认表名

JPA 默认表名生成特定于其实现。

例如，在 Hibernate 中，默认的表名是首字母大写的类名。它是通过ImplicitNamingStrategy合同确定的。

[但是我们可以通过实现PhysicalNamingStrategy接口](https://www.baeldung.com/hibernate-naming-strategy)来改变这种行为。

## 3.使用@Table

设置自定义 SQL 表名称的最简单方法是使用@ javax.persistence.Table注解实体并定义其名称参数：

```java
@Entity
@Table(name = "ARTICLES")
public class Article {
    // ...
}
```

我们还可以将表名存储在静态最终变量中：

```java
@Entity
@Table(name = Article.TABLE_NAME)
public class Article {
    public static final String TABLE_NAME= "ARTICLES";
    // ...
}
```

## 4. 在 JPQL 查询中覆盖表名

默认情况下，在 JPQL 查询中，我们使用实体类名称：

```sql
select  from Article
```

但是我们可以通过在@javax.persistence.Entity注解中定义name参数来改变它：

```java
@Entity(name = "MyArticle")
```

然后我们将 JPQL 查询更改为：

```sql
select  from MyArticle
```

## 5.总结

在本文中，我们了解了 JPA 如何生成默认表名以及如何使用 JPA 设置 SQL 表名。