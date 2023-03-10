## 1. 简介

Hibernate是一个用于管理持久数据的便捷框架，但有时很难理解它的内部工作原理。

在本教程中，我们将了解对象状态以及如何在它们之间移动。我们还将研究分离实体可能遇到的问题以及如何解决这些问题。

## 2. Hibernate的Session

[Session](https://docs.jboss.org/hibernate/orm/3.5/javadocs/org/hibernate/Session.html)接口是用来与Hibernate进行通信的主要工具。它提供了一个API，使我们能够创建、读取、更新和删除持久对象。会话有一个简单的生命周期：我们打开它，执行一些操作，然后关闭它。

当我们在会话期间对对象进行操作时，它们会附加到该会话。我们所做的更改会在关闭时被检测到并保存。关闭后，Hibernate断开对象和会话之间的连接。

## 3. 对象状态

在Hibernate的Session上下文中，对象可以处于三种可能的状态之一：瞬时、持久或分离。

### 3.1 瞬时态

**我们没有附加到任何会话的对象处于瞬时态**。由于它从未被持久化，因此它在数据库中没有任何表示。由于没有会话知道它，因此它不会自动保存。

让我们使用构造函数创建一个用户对象，并断言它不是由会话管理的：

```java
Session session = openSession();
UserEntity userEntity = new UserEntity("John");
assertThat(session.contains(userEntity)).isFalse();
```

### 3.2 持久态

**我们与会话关联的对象处于持久状态**。我们要么保存它，要么从持久性上下文中读取它，因此它代表数据库中的某一行数据。

让我们创建一个对象，然后使用persist方法使其持久化：

```java
Session session = openSession();
UserEntity userEntity = new UserEntity("John");
session.persist(userEntity);
assertThat(session.contains(userEntity)).isTrue();
```

或者，我们可以使用save方法。不同之处在于persist方法只会保存一个对象，而save方法会在需要时额外生成它的id。

### 3.3 分离态

当我们关闭会话时，其中的所有对象都会分离。**尽管它们仍然代表数据库中的某一行，但它们不再由任何会话管理**：

```java
session.persist(userEntity);
session.close();
assertThat(session.isOpen()).isFalse();
assertThatThrownBy(() -> session.contains(userEntity));
```

接下来，我们介绍如何保存瞬态实体和分离实体。

## 4. 保存和重新附加实体

### 4.1 保存瞬态实体

让我们创建一个新实体并将其保存到数据库中。**当我们第一次构造对象时，它会处于瞬态状态**。

为了持久化我们的新实体，我们将使用persist方法：

```java
UserEntity userEntity = new UserEntity("John");
session.persist(userEntity);
```

现在，我们将创建另一个与第一个对象具有相同标识符的对象。第二个对象是瞬态的，因为它还没有被任何会话管理，但我们不能使用persist方法使它持久化。它已经出现在数据库中，所以在持久层的上下文中它并不是真正的新事物。

相反，**我们将使用merge方法来更新数据库并使对象持久化**：

```java
UserEntity onceAgainJohn = new UserEntity("John");
session.merge(onceAgainJohn);
```

### 4.2 保存分离的实体

**如果我们关闭上一个会话，我们的对象将处于分离状态**。与前面的示例类似，它们在数据库中表示，但当前不受任何会话管理。我们可以使用merge方法使它们再次持久化：

```java
UserEntity userEntity = new UserEntity("John");
session.persist(userEntity);
session.close();
session.merge(userEntity);
```

## 5. 嵌套实体

当我们考虑嵌套实体时，事情变得更加复杂。假设我们的用户实体还将存储有关其经理的信息：

```java
public class UserEntity {
    @Id
    private String name;

    @ManyToOne
    private UserEntity manager;
}
```

当我们保存这个实体时，我们不仅需要考虑实体本身的状态，还需要考虑嵌套实体的状态。让我们创建一个持久的用户实体，然后设置它的manager：

```java
UserEntity userEntity = new UserEntity("John");
session.persist(userEntity);
UserEntity manager = new UserEntity("Adam");
userEntity.setManager(manager);
```

如果我们现在尝试更新它，我们会得到一个异常：

```java
assertThatThrownBy(() -> {
    session.saveOrUpdate(userEntity);
    transaction.commit();
});
```

```shell
java.lang.IllegalStateException: org.hibernate.TransientPropertyValueException: object references an unsaved transient instance - save the transient instance before flushing : cn.tuyucheng.taketoday.states.UserEntity.manager -> cn.tuyucheng.taketoday.states.UserEntity
```

发生这种情况是因为Hibernate不知道如何处理瞬态嵌套实体。

### 5.1 持久化嵌套实体

解决此问题的一种方法是显式持久化嵌套实体：

```java
UserEntity manager = new UserEntity("Adam");
session.persist(manager);
userEntity.setManager(manager);
```

然后，在提交事务后，我们将能够检索正确保存的实体：

```java
transaction.commit();
session.close();

Session otherSession = openSession();
UserEntity savedUser = otherSession.get(UserEntity.class, "John");
assertThat(savedUser.getManager().getName()).isEqualTo("Adam");
```

### 5.2 级联操作

如果我们在实体类中正确配置关系的cascade属性，则瞬态嵌套实体可以自动持久化：

```java
@ManyToOne(cascade = CascadeType.PERSIST)
private UserEntity manager;
```

**现在，当我们持久化对象时，该操作将级联到所有嵌套实体**：

```java
UserEntityWithCascade userEntity = new UserEntityWithCascade("John");
session.persist(userEntity);
UserEntityWithCascade manager = new UserEntityWithCascade("Adam");

userEntity.setManager(manager); // add transient manager to persistent user
session.saveOrUpdate(userEntity);
transaction.commit();
session.close();

Session otherSession = openSession();
UserEntityWithCascade savedUser = otherSession.get(UserEntityWithCascade.class, "John");
assertThat(savedUser.getManager().getName()).isEqualTo("Adam");
```

## 6. 总结

在本教程中，我们仔细研究了Hibernate Session如何处理对象状态。然后我们检查了它可能产生的一些问题以及如何解决这些问题。