## 1. 概述

在本文中，我们将讨论不同的 HQL 查询以及如何在不必要时避免在 SQL 查询中添加不同的 关键字。

## 2. 理解问题

首先，让我们看看我们的数据模型并确定我们要完成的任务。

我们将使用共享一对多关系的Post和Comment实体对象。我们想要检索一个包含所有相关评论的帖子列表。

让我们从尝试以下 HQL 查询开始：

```java
String hql = "SELECT p FROM Post p LEFT JOIN FETCH p.comments";
List<Post> posts = session.createQuery(hql, Post.class).getResultList();
```

这将生成以下 SQL 查询：

```sql
select
    post0_.id as id1_3_0_,
    comment2_.id as id1_0_1_,
    post0_.title as title2_3_0_,
    comment2_.text as text2_0_1_,
    comments1_.Post_id as Post_id1_4_0__,
    comments1_.comments_id as comments2_4_0__
from
    Post post0_
left outer join
    Post_Comment comments1_
        on post0_.id=comments1_.Post_id
left outer join
    Comment comment2_
        on comments1_.comments_id=comment2_.id
```

结果将包含重复项。 一个帖子的显示次数与相关评论的次数一样多——具有三个评论的帖子将在结果列表中显示三次。

## 3.在 HQL 查询中使用distinct

我们需要在 HQL 查询中使用distinct关键字来消除重复项：

```java
String hql = "SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.comments";
List<Post> posts = session.createQuery(hql, Post.class).getResultList();
```

现在，我们得到了正确的结果：不再有重复的Post对象。我们来看看Hibernate生成的SQL语句：

```sql
select
    distinct post0_.id as id1_3_0_,
    comment2_.id as id1_0_1_,
    post0_.title as title2_3_0_,
    comment2_.text as text2_0_1_,
    comments1_.Post_id as Post_id1_4_0__,
    comments1_.comments_id as comments2_4_0__
from
    Post post0_
left outer join
    Post_Comment comments1_
        on post0_.id=comments1_.Post_id
left outer join
    Comment comment2_
        on comments1_.comments_id=comment2_.id

```

我们可以注意到，distinct关键字不仅被 Hibernate 使用，而且还包含在 SQL 查询中。我们应该避免这种情况，因为这是不必要的并且会导致性能问题。

## 4. 使用QueryHint停止传递不同的关键字

从 Hibernate 5.2 开始，我们可以利用pass-distinct-through机制不再传递 SQL 语句的 HQL/JPQL distinct子句。

要禁用pass-distinct-through，我们需要向查询添加提示QueryHint.PASS_DISTINCT_THROUGH，值为false：

```java
String hql = "SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.comments";
List<Post> posts = session.createQuery(hql, Post.class)
  .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
  .getResultList();
```

如果我们检查结果，我们会看到没有重复的实体。此外，SQL 语句中没有使用distinct子句：

```sql
select
    post0_.id as id1_3_0_,
    comment2_.id as id1_0_1_,
    post0_.title as title2_3_0_,
    comment2_.text as text2_0_1_,
    comments1_.Post_id as Post_id1_4_0__,
    comments1_.comments_id as comments2_4_0__ 
from
    Post post0_ 
left outer join
    Post_Comment comments1_ 
        on post0_.id=comments1_.Post_id 
left outer join
    Comment comment2_ 
        on comments1_.comments_id=comment2_.id
```

## 5.总结

在本文中，我们发现SQL 查询中存在distinct 关键字可能是不必要的，而且它会影响性能。之后，我们学习了如何使用PASS_DISTINCT_THROUGH查询提示来避免这种行为。