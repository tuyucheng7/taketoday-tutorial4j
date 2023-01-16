## 1. 概述

在本快速教程中，我们将了解[JSR-330](https://www.baeldung.com/javax-validation)的@Size注解、[Hibernate](https://www.baeldung.com/hibernate-4-spring)的@Length注解和[JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)的@Column的长度属性。

乍一看，这些可能看起来相同，但它们执行不同的功能。

## 2. 起源

简而言之，所有这些注解都是为了传达字段的大小。

@Size和@Length是相似的。我们可以使用任一注解来验证字段的大小。前者是[Java 标准注解](https://docs.oracle.com/javaee/7/tutorial/bean-validation001.htm)，而后者是[特定于 Hibernate 的](http://docs.jboss.org/ejb3/app-server/HibernateAnnotations/api/org/hibernate/validator/Length.html)。

但是，@Column 是我们用来控制 DDL 语句的JPA注解[。](https://docs.oracle.com/javaee/7/api/javax/persistence/Column.html)

现在让我们详细了解它们中的每一个。

## 3. @大小

对于验证，我们将使用@Size ，一个 bean 验证注解。我们将使用带有 @Size 注解的属性middleName来验证 其在属性min和max 之间的值：

```java
public class User {

    // ...

    @Size(min = 3, max = 15)
    private String middleName;

    // ...

}
```

最重要的是，@Size使 bean 独立于 JPA 及其供应商，例如 Hibernate。因此，它比@Length更便携。

## 4. @长度

正如我们之前提到的，@Length是@Size 的特定于 Hibernate 的版本。我们将使用@Length强制执行lastName的范围：

```java
@Entity
public class User {

    // ...
      
    @Length(min = 3, max = 15)
    private String lastName;

    // ...

}
```

## 5. @Column(长度=值)

@Column与前两个注解有很大的不同。

我们将使用@Column来指示物理数据库列的特定特征。我们将使用@Column注解的[length](http://java.sun.com/javaee/5/docs/api/javax/persistence/Column.html#length())属性来指定字符串值列的长度：

```java
@Entity
public class User {

    @Column(length = 3)
    private String firstName;

    // ...

}
```

结果列将生成为VARCHAR(3)，尝试插入更长的字符串将导致 SQL 错误。

请注意，我们将仅使用@Column来指定表列属性，因为它不提供验证。

当然，我们可以将@Column与@Size一起使用来指定带有bean 验证的数据库列属性：

```java
@Entity
public class User {

    // ... 
    
    @Column(length = 5)
    @Size(min = 3, max = 5)
    private String city;

    // ...

}
```

## 六. 总结

在本文中，我们了解了@Size注解、@Length注解和@Column的长度属性之间的区别。然后我们在它们的使用范围内分别检查了每一个。