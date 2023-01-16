## 1. 概述

在本教程中，我们将了解 Hibernate 的PersistentObjectException，它在尝试保存分离的实体时发生。

我们将从了解分离状态的含义以及 Hibernate 的持久化 和合并方法之间的区别开始。然后我们将在各种用例中重现错误以演示如何修复它。

## 2.分离实体

让我们首先简要回顾一下分离状态是什么以及它与[实体生命周期](https://www.baeldung.com/hibernate-entity-lifecycle)的关系。

分离的实体是不再由持久性上下文跟踪的Java对象。如果我们关闭或清除会话，实体可以达到此状态。同样，我们可以通过手动将实体从持久化上下文中移除来分离实体。

我们将在本文的代码示例中使用Post和Comment实体。为了分离特定的Post实体，我们可以使用session.evict(post)。我们可以通过使用session.clear()清除会话来将所有实体从上下文中分离出来。

例如，一些测试将需要一个分离的Post。那么让我们看看如何实现这一目标：

```java
@Before
public void beforeEach() {
    session = HibernateUtil.getSessionFactory().openSession();
    session.beginTransaction();
 
    this.detachedPost = new Post("Hibernate Tutorial");
    session.persist(detachedPost);
    session.evict(detachedPost);
}
```

首先，我们保留了Post实体，然后我们使用session.evict(post)将其分离。

## 3. 尝试持久化分离的实体

如果我们尝试持久化一个分离的实体，Hibernate 将抛出一个PersistenceException并显示“分离的实体传递给持久化”错误消息。

让我们尝试保留一个分离的Post实体来预测这个异常：

```java
@Test
public void givenDetachedPost_whenTryingToPersist_thenThrowException() {
    detachedPost.setTitle("Hibernate Tutorial for Absolute Beginners");

    assertThatThrownBy(() -> session.persist(detachedPost))
      .isInstanceOf(PersistenceException.class)
      .hasMessageContaining("org.hibernate.PersistentObjectException: detached entity passed to persist");
}
```

为避免这种情况，我们应该了解实体状态并使用适当的方法来保存它。

如果我们使用merge方法，Hibernate 将根据@Id字段将实体重新附加到持久化上下文：

```java
@Test
public void givenDetachedPost_whenTryingToMerge_thenNoExceptionIsThrown() {
    detachedPost.setTitle("Hibernate Tutorial for Beginners");

    session.merge(detachedPost);
    session.getTransaction().commit();

    List<Post> posts = session.createQuery("Select p from Post p", Post.class).list();
    assertThat(posts).hasSize(1);
    assertThat(posts.get(0).getTitle())
        .isEqualTo("Hibernate Tutorial for Beginners");
}
```

同样，我们可以使用其他特定于 Hibernate 的方法，例如[update、save和saveOrUpdate](https://www.baeldung.com/hibernate-save-persist-update-merge-saveorupdate)。与persist和merge不同， 这些方法不是 JPA 规范的一部分。因此，如果我们想使用 JPA 抽象，我们应该避免使用它们。

## 4. 尝试通过关联来持久化分离的实体

对于这个例子，我们将介绍Comment实体：

```java
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne(cascade = CascadeType.MERGE)
    private Post post;

    // constructor, getters and setters
}
```

我们可以看到Comment实体与Post具有多对一的关系。

级联[类型](https://www.baeldung.com/jpa-cascade-types)设置为CascadeType.MERGE；因此，我们只会将合并操作传播到关联的Post。

换句话说，如果我们合并一个Comment实体，Hibernate 会将操作传播到关联的Post，并且两个实体都将在数据库中更新。但是，如果我们想使用此设置保留Comment ，我们首先必须合并关联的Post：

```java
@Test
public void givenDetachedPost_whenMergeAndPersistComment_thenNoExceptionIsThrown() {
    Comment comment = new Comment("nice article!");
    Post mergedPost = (Post) session.merge(detachedPost);
    comment.setPost(mergedPost);

    session.persist(comment);
    session.getTransaction().commit();

    List<Comment> comments = session.createQuery("Select c from Comment c", Comment.class).list();
    Comment savedComment = comments.get(0);
    assertThat(savedComment.getText()).isEqualTo("nice article!");
    assertThat(savedComment.getPost().getTitle())
        .isEqualTo("Hibernate Tutorial");
}
```

相反，如果[级联类型](https://www.baeldung.com/jpa-cascade-types)设置为PERSIST或ALL，Hibernate 将尝试在分离的关联字段上传播持久化操作。因此，当我们 使用这些级联类型之一持久化Post实体时，Hibernate 将持久化关联的分离Comment， 这将导致另一个PersistentObjectException。

## 5.总结

在本文中，我们讨论了 Hibernate 的PersistentObjectException并了解了其主要原因。我们可以通过正确使用[Hibernate 的save、persist、update、merge和saveOrUpdate](https://www.baeldung.com/hibernate-save-persist-update-merge-saveorupdate)方法来避免它。

此外，良好地利用[JPA 级联类型](https://www.baeldung.com/jpa-cascade-types)将防止PersistentObjectException在我们的实体关联中发生。