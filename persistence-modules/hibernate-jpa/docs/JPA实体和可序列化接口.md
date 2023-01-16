## 1. 简介

在本教程中，我们将讨论 JPA 实体和JavaSerializable接口如何融合。首先，我们将看一下java.io.Serializable接口以及我们需要它的原因。之后，我们将了解 JPA 规范和 Hibernate 作为其最流行的实现。

## 2. 什么是可序列化接口？

Serializable是核心Java中为数不多的标记接口之一。[标记接口](https://www.baeldung.com/java-marker-interfaces)是没有方法或常量的特例接口。

对象序列化是将Java对象转换为字节流的过程。然后我们可以通过网络传输这些字节流或将它们存储在持久内存中。反序列化是相反的过程，我们获取字节流并将它们转换回Java对象。要允许对象序列化(或反序列化)，类必须实现Serializable接口。否则，我们将遇到java.io.NotSerializableException。[序列化](https://www.baeldung.com/java-serialization)广泛应用于 RMI、JPA 和 EJB 等技术中。

## 3. JPA和Serializable

让我们看看 JPA 规范对Serializable的描述以及它与 Hibernate 的关系。

### 3.1. JPA规范

JPA 的核心部分之一是实体类。我们将此类标记为实体(使用@Entity注解或 XML 描述符)。[我们的实体类必须满足几个要求，根据 JPA 规范](https://download.oracle.com/otn-pub/jcp/persistence-2_1-fr-eval-spec/JavaPersistence.pdf)，我们最关心的是：

>   如果要将实体实例作为分离对象按值传递(例如，通过远程接口)，实体类必须实现Serializable接口。

实际上，如果我们的对象要离开 JVM 的域，则需要序列化。

每个实体类都由持久字段和属性组成。该规范要求实体的字段可以是Java原语、Java 可序列化类型或用户定义的可序列化类型。

一个实体类也必须有一个主键。主键可以是原始的(单个持久字段)或复合的。多个规则适用于复合键，其中之一是复合键必须是可序列化的。

让我们使用 Hibernate、H2 内存数据库和以UserId作为复合键的用户域对象创建一个简单示例：

```java
@Entity
public class User {
    @EmbeddedId UserId userId;
    String email;
    
    // constructors, getters and setters
}

@Embeddable
public class UserId implements Serializable{
    private String name;
    private String lastName;
    
    // getters and setters
}
```

我们可以使用集成测试来测试我们的域定义：

```java
@Test
public void givenUser_whenPersisted_thenOperationSuccessful() {
    UserId userId = new UserId();
    userId.setName("John");
    userId.setLastName("Doe");
    User user = new User(userId, "johndoe@gmail.com");

    entityManager.persist(user);

    User userDb = entityManager.find(User.class, userId);
    assertEquals(userDb.email, "johndoe@gmail.com");
}
```

如果我们的UserId 类没有实现Serializable接口，我们将得到一个MappingException，其中包含一条具体消息，表明我们的复合键必须实现该接口。

### 3.2. Hibernate @JoinColumn注解

[Hibernate 官方文档](https://hibernate.org/orm/documentation/)，在描述Hibernate 中的映射时，指出当我们使用[@JoinColumn](https://www.baeldung.com/jpa-join-column)注解中的referencedColumnName时，引用的字段必须是可序列化的。通常，此字段是另一个实体中的主键。在复杂实体类的极少数情况下，我们的引用必须是可序列化的。

让我们扩展之前的User类，其中email字段不再是String而是一个独立的实体。此外，我们将添加一个引用用户并具有字段类型的帐户类。每个用户可以拥有多个不同类型的帐户。我们将通过电子邮件映射帐户，因为通过电子邮件地址搜索更自然：

```java
@Entity
public class User {
    @EmbeddedId private UserId userId;
    private Email email;
}

@Entity
public class Email implements Serializable {
    @Id
    private long id;
    private String name;
    private String domain;
}

@Entity
public class Account {
    @Id
    private long id;
    private String type;
    @ManyToOne
    @JoinColumn(referencedColumnName = "email")
    private User user;
}
```

为了测试我们的模型，我们将编写一个测试，我们为一个用户创建两个帐户并通过电子邮件对象进行查询：

```java
@Test
public void givenAssociation_whenPersisted_thenMultipleAccountsWillBeFoundByEmail() {
    // object creation 

    entityManager.persist(user);
    entityManager.persist(account);
    entityManager.persist(account2);

    List userAccounts = entityManager.createQuery("select a from Account a join fetch a.user where a.user.email = :email")
      .setParameter("email", email)
      .getResultList();
    
    assertEquals(userAccounts.size(), 2);
}
```

如果Email类没有实现Serializable接口，我们将再次获得MappingException，但这次会出现一条有点神秘的消息：“无法确定类型”。

### 3.3. 将实体暴露给表示层

当使用 HTTP 通过网络发送对象时，我们通常为此目的创建特定的 DTO(数据传输对象)。通过创建 DTO，我们将内部域对象与外部服务分离。如果我们想在没有 DTO 的情况下直接将我们的实体暴露给表示层，那么实体必须是可序列化的。

我们使用HttpSession对象来存储相关数据，帮助我们在访问我们网站的多个页面中识别用户。Web 服务器可以在正常关闭时将会话数据存储在磁盘上，或者在集群环境中将会话数据传输到另一个 Web 服务器。如果一个实体是这个过程的一部分，那么它必须是可序列化的。否则，我们将遇到NotSerializableException。

## 4. 总结

在本文中，我们介绍了Java序列化的基础知识，并了解了它如何在 JPA 中发挥作用。首先，我们回顾了 JPA 规范关于Serializable的要求。之后，我们将 Hibernate 视为最流行的 JPA 实现。最后，我们介绍了 JPA 实体如何与 Web 服务器一起工作。