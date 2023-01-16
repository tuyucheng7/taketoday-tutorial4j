## 1. 简介

在本教程中，我们将学习 JPA 中的复合主键和相应的注解。

## 延伸阅读：

## [Spring JPA @Embedded 和 @EmbeddedId](https://www.baeldung.com/spring-jpa-embedded-method-parameters)

了解如何使用@EmbeddeId和@Embeddable注解来表示 JPA 实体中的组合键。

[阅读更多](https://www.baeldung.com/spring-jpa-embedded-method-parameters)→

## [JPA什么时候设置主键](https://www.baeldung.com/jpa-strategies-when-set-primary-key)

了解 JPA 用于为实体生成主键的不同策略，以及每种策略在持久化期间设置键值的时刻。

[阅读更多](https://www.baeldung.com/jpa-strategies-when-set-primary-key)→

## [使用 JPA 返回自动生成的 ID](https://www.baeldung.com/jpa-get-auto-generated-id)

在本教程中，我们将讨论如何使用 JPA 处理自动生成的 ID。

[阅读更多](https://www.baeldung.com/jpa-get-auto-generated-id)→

## 2.复合主键

复合主键，也称为组合键，是两个或多个列的组合以形成表的主键。

在 JPA 中，我们有两个选项来定义组合键：@IdClass和@EmbeddedId注解。

为了定义复合主键，我们应该遵循一些规则：

-   复合主键类必须是公共的。
-   它必须有一个无参数的构造函数。
-   它必须定义equals()和hashCode()方法。
-   它必须是可序列化的。

## 3. IdClass注解

假设我们有一个名为Account的表，它有两列，即accountNumber和accountType， 它们构成了复合键。现在我们必须在 JPA 中映射它。

根据 JPA 规范，让我们创建一个具有以下主键字段的AccountId类：

```java
public class AccountId implements Serializable {
    private String accountNumber;

    private String accountType;

    // default constructor

    public AccountId(String accountNumber, String accountType) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
    }

    // equals() and hashCode()
}
```

接下来让我们将AccountId类与实体Account相关联。

为此，我们需要使用[@IdClass](https://www.baeldung.com/hibernate-identifiers)注解来注解实体。我们还必须声明实体Account中AccountId 类的字段，并用@Id注解它们：

```java
@Entity
@IdClass(AccountId.class)
public class Account {
    @Id
    private String accountNumber;

    @Id
    private String accountType;

    // other fields, getters and setters
}
```

## 4. EmbeddedId注解

@EmbeddedId是@IdClass注解的替代方法。

让我们考虑另一个例子，我们必须保留Book的一些信息，标题和语言作为主键字段。

在这种情况下，主键类BookId必须使用@Embeddable注解：

```java
@Embeddable
public class BookId implements Serializable {
    private String title;
    private String language;

    // default constructor

    public BookId(String title, String language) {
        this.title = title;
        this.language = language;
    }

    // getters, equals() and hashCode() methods
}
```

然后我们需要使用@EmbeddedId将这个类嵌入到Book实体[中](https://www.baeldung.com/jpa-many-to-many)：

```java
@Entity
public class Book {
    @EmbeddedId
    private BookId bookId;

    // constructors, other fields, getters and setters
}
```

## 5. @IdClass与@EmbeddedId

正如我们所看到的，这两者表面上的区别在于使用@IdClass我们必须指定两次列，一次在AccountId中，另一次在Account 中；但是，对于@EmbeddedId ，我们没有。

但是还有一些其他的权衡。

例如，这些不同的结构会影响我们编写的 JPQL 查询。

使用@IdClass，查询会更简单一些：

```java
SELECT account.accountNumber FROM Account account
```

使用@EmbeddedId，我们必须进行一次额外的遍历：

```java
SELECT book.bookId.title FROM Book book
```

此外，@IdClass 在我们使用无法修改的复合键类的地方非常有用。

如果我们要单独访问复合键的各个部分，我们可以使用@IdClass，但在我们经常将完整标识符用作对象的地方， @ EmbeddedId是首选。

## 六. 总结

在这篇简短的文章中，我们探讨了 JPA 中的复合主键。