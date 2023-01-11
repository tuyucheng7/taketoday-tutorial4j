## 1. 概述

虽然Spring Data JPA可以抽象查询的创建，以便在特定情况下从数据库中检索实体，**但有时我们需要自定义查询，例如当我们添加聚合函数时**。

在本教程中，我们重点介绍如何将这些查询的结果转换为对象。我们将探索两种不同的解决方案，一种涉及JPA规范和POJO，另一种使用Spring Data Projection。

## 2. JPA查询和聚合问题

JPA查询通常将其结果作为映射实体的实例生成。但是，**使用聚合函数的查询通常会将结果作为Object[]返回**。

为了理解这个问题，让我们根据帖子和评论之间的关系定义一个域模型：

```java
@Entity
public class Post {
    @Id
    private Integer id;
    private String title;
    private String content;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;
    // getters, setters, constructors...
}

@Entity
public class Comment {
    @Id
    private Integer id;
    private Integer year;
    private boolean approved;
    private String content;

    @ManyToOne
    private Post post;
    // getters, setters, constructors...
}
```

我们的模型定义了一个帖子可以有多个评论，并且每个评论都属于一篇帖子。下面将Spring Data Repository用于此模型：

```java
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

}
```

现在，我们统计一下按年份分组的评论：

```java
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("select c.year, count (c.year) from Comment as c group by c.year order by c.year desc ")
    List<Object[]> countTotalCommentsByYear();
}
```

**我们不能将以上一个JPA查询的结果加载到Comment实例中，因为结果是不同的类型**。查询中指定的year和count(c.year)与实体对象不匹配。

虽然我们仍然可以访问集合中返回的Object[]中的结果，但这样做会导致代码混乱且容易出错。

## 3. 使用类构造函数自定义结果

**JPA规范允许我们以面向对象的方式自定义结果**。因此，我们可以使用JPQL构造函数表达式来设置结果：

```java
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("select new cn.tuyucheng.taketoday.aggregation.model.custom.CommentCount(c.year, count (c.year)) from Comment as c group by c.year order by c.year desc ")
    List<CommentCount> countTotalCommentsByYearClass();
}
```

这会将select语句的输出绑定到CommentCount。**指定的类需要有一个与投影属性完全匹配的构造函数，但不需要用@Entity注解标注**。

我们还可以看到，JPQL中声明的构造函数必须是一个全限定名称：

```java
package cn.tuyucheng.taketoday.aggregation.model.custom;

public class CommentCount {
    private Integer year;
    private Long total;

    public CommentCount(Integer year, Long total) {
        this.year = year;
        this.total = total;
    }
    // getters and setters ...
}
```

## 4. 使用Spring Data Projection自定义结果

另一种可能的解决方案是使用Spring Data Projection自定义JPA查询的结果，**此功能使我们能够用更少的代码投影查询结果**。

### 4.1 自定义JPA查询的结果

要使用基于接口的projection，我们必须定义一个Java接口，该接口由匹配映射属性名称的getter方法组成：

```java
public interface CommentCountProjection {

    Integer getYearComment();

    Long getTotalComment();
}
```

现在，我们用返回的结果List<CommentCountProjection>来表示我们的查询：

```java
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("select c.year as yearComment, count(c.year) as totalComment from Comment as c group by c.year order by c.year desc ")
    List<CommentCountProjection> countTotalCommentsByYearInterface();
}
```

**为了让Spring将投影值绑定到我们的接口，我们需要使用接口中的属性名为每个投影属性指定别名**。

然后，Spring Data会动态构造结果，并为结果的每一行返回一个代理实例。

### 4.2 使用原生查询自定义结果

我们可能会遇到JPA查询速度不如原生SQL快的问题，或者无法使用数据库引擎的特定功能。为了解决这个问题，我们可以使用原生查询。

**基于接口的投影的一个优点是，我们可以将它用于本地查询**。

```java
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query(value = "select c.year as yearComment, count (c.*) as totalComment from comment as c group by c.year order by c.year desc ", nativeQuery = true)
    List<CommentCountProjection> countTotalCommentsByYearNative();
}
```

这与JPQL查询的工作原理相同。

## 5. 总结

**在本文中，我们介绍了两种不同的解决方案，以解决使用聚合函数映射JPA查询的结果**。首先，我们使用了涉及POJO类的JPA标准。

对于第二个解决方案，我们使用基于接口的轻量级Spring Data Projections。Spring Data Projections允许我们用Java和JPQL编写更少的代码。