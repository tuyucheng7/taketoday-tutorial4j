## 1. 简介

在使用对象关系映射 (ORM) 框架将Java对象持久化到数据库记录时，我们通常希望忽略某些字段。如果框架符合JavaPersistence API (JPA)，我们可以在这些字段上添加@Transient注解。

在本教程中，我们将演示@Transient注解的正确用法。我们还将研究它与[Java 内置的transient关键字](https://www.baeldung.com/java-transient-keyword)的关系。

## 2. @Transient注解与transient关键字

@Transient注解和Java内置的transient关键字之间的关系通常存在一些混淆。transient关键字主要用于在Java对象序列化期间忽略字段，[但](https://www.baeldung.com/java-serialization)它也可以防止在使用 JPA 框架时保留这些字段。

换句话说，在保存到数据库时， transient关键字与@Transient注解具有相同的效果。但是，@Transient注解不影响Java对象序列化。


## 3. JPA @Transient示例

假设我们有一个User类，它是一个映射到我们数据库中的 Users 表的[JPA 实体。](https://www.baeldung.com/jpa-entities)当用户登录时，我们从 Users 表中检索他们的记录，然后我们在User实体上设置一些额外的字段。这些额外的字段不对应于 Users 表中的任何列，因为我们不想保存这些值。

例如，我们将在User实体上设置一个时间戳，表示用户何时登录到他们的当前会话：

```java
@Entity
@Table(name = "Users")
public class User {

    @Id
    private Integer id;
 
    private String email;
 
    private String password;
 
    @Transient
    private Date loginTime;
    
    // getters and setters
}
```

当我们使用 Hibernate 等 JPA 提供程序将此User对象保存到数据库时，由于@Transient注解，提供程序会忽略loginTime字段。

如果我们序列化此User对象并将其传递给我们系统中的另一个服务，则loginTime字段将包含在序列化中。如果我们不想包含这个字段，我们可以用transient关键字替换@Transient注解：

```java
@Entity
@Table(name = "Users")
public class User implements Serializable {

    @Id
    private Integer id;
 
    private String email;
 
    private String password;
 
    private transient Date loginTime;

    //getters and setters
}
```

现在，在数据库持久化和对象序列化期间将忽略loginTime字段。

## 4. 总结

在本文中，我们研究了如何在典型用例中正确使用 JPA @Transient注解。请务必[查看有关 JPA 的其他文章](https://www.baeldung.com/category/persistence/tag/jpa/)以了解有关持久性的更多信息。