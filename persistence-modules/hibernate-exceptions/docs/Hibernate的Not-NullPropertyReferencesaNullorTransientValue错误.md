## 1. 概述

在本文中，我们将了解 Hibernate 的 PropertyValueException。特别是，我们将考虑 “not-null property references a null or transient value”错误消息。

Hibernate主要会在两种情况下抛出PropertyValueException ：

-   为标有nullable = false 的列保存空值时
-   保存具有引用未保存实例的关联的实体时

## 2. Hibernate 的可空性检查

首先，让我们讨论一下 Hibernate 的[@Column(nullable = false)](https://www.baeldung.com/hibernate-notnull-vs-nullable)注解。如果不存在其他 Bean 验证， 我们可以依赖Hibernate 的可空性检查。

此外，我们可以通过设置 hibernate.check_nullability = true 来强制执行此验证。为了重现以下示例，我们需要启用可空性检查。

## 3.为非空列保存空值

现在，让我们利用 Hibernate 的验证机制来重现一个简单用例的错误。我们将尝试在 不设置必填字段的情况下保存@Entity 。对于这个例子，我们将使用简单的Book类：

```java
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    // getters and setters
}
```

标题列的可空标志设置为false 。 我们现在可以在不设置标题的情况下保存Book对象，并断言抛出PropertyValueException ：

```java
@Test
public void whenSavingEntityWithNullMandatoryField_thenThrowPropertyValueException() {    
    Book book = new Book();

    assertThatThrownBy(() -> session.save(book))
      .isInstanceOf(PropertyValueException.class)
      .hasMessageContaining("not-null property references a null or transient value");
}

```

因此，我们只需要在保存实体之前设置必填字段来解决这个问题：book.setTitle(“Clean Code”)。

## 4. 保存引用未保存实例的关联

在本节中，我们将探讨一个具有更复杂设置的常见场景。对于此示例，我们将使用 共享双向关系的Author和Article实体：

```java
@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany
    @Cascade(CascadeType.ALL)
    private List<Article> articles;

    // constructor, getters and setters
}
```

文章字段具有@Cascade (CascadeType.ALL ) 注解。因此，当我们保存Author实体时，该操作将传播到所有Article对象：

```java
@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @ManyToOne(optional = false)
    private Author author;

    // constructor, getters and setters
}
```

现在，让我们尝试保存一个作者 和一些文章，看看会发生什么：

```java
@Test
public void whenSavingBidirectionalEntityiesithCorrectParent_thenDoNotThrowException() {
    Author author = new Author("John Doe");
    author.setArticles(asList(new Article("Java tutorial"), new Article("What's new in JUnit5")));

    assertThatThrownBy(() -> session.save(author))
      .isInstanceOf(PropertyValueException.class)
      .hasMessageContaining("not-null property references a null or transient value");
}

```

当我们处理双向关系时，我们可能会犯一个常见的错误，即忘记更新双方的分配。如果我们更改Author类的setter以更新所有子文章，我们可以避免这种情况。

为了说明本文中出现的所有用例，我们将为此创建一个不同的方法。但是，从父实体的设置器中设置这些字段是一个好习惯：

```java
public void addArticles(List<Article> articles) {
    this.articles = articles;
    articles.forEach(article -> article.setAuthor(this));
}
```

我们现在可以使用新方法来设置赋值并且不会出现错误：

```java
@Test
public void whenSavingBidirectionalEntitesWithCorrectParent_thenDoNotThrowException() {
    Author author = new Author("John Doe");
    author.addArticles(asList(new Article("Java tutorial"), new Article("What's new in JUnit5")));

    session.save(author);
}
```

## 5.总结

在本文中，我们了解了 Hibernate 的验证机制是如何工作的。首先，我们发现了如何在我们的项目中启用可空性检查。之后，我们举例说明了导致PropertyValueException的主要原因，并学习了如何修复它们。