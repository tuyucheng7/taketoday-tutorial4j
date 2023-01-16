## 1. 概述

Hibernate 等持久性提供程序利用持久性上下文来管理应用程序中的实体生命周期。

在本教程中，我们将从介绍持久性上下文开始，然后我们将了解它的重要性。最后，我们将通过示例了解事务范围的持久性上下文和扩展范围的持久性上下文之间的区别。

## 2.持久化上下文

我们先看一下[Persistence Context](https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html)的官方定义：

>   EntityManager 实例与持久性上下文相关联。持久性上下文是一组实体实例，其中对于任何持久性实体身份都有一个唯一的实体实例。在持久性上下文中，管理实体实例及其生命周期。EntityManager API 用于创建和删除持久实体实例、通过主键查找实体以及查询实体。

上面的陈述现在看起来有点复杂，但随着我们的继续，它会变得完全有意义。持久性上下文是一级缓存，所有实体都从数据库中获取或保存到数据库中。它位于我们的应用程序和持久存储之间。

持久性上下文跟踪对托管实体所做的任何更改。如果在事务期间有任何变化，则该实体被标记为脏的。当事务完成时，这些更改被刷新到持久存储中。

EntityManager是让我们与持久性上下文交互的接口。每当我们使用EntityManager时，我们实际上是在与持久性上下文进行交互。

如果在实体中所做的每个更改都调用持久存储，我们可以想象将调用多少次。这将导致性能影响，因为持久存储调用非常昂贵。

## 3.持久化上下文类型

持久性上下文有两种类型：

-   事务范围的持久化上下文
-   扩展范围的持久性上下文

让我们逐一看看。

### 3.1. 事务范围的持久性上下文

事务持久性上下文绑定到事务。一旦事务完成，存在于持久性上下文中的实体将被刷新到持久性存储中。

[![事务持久化上下文图](https://www.baeldung.com/wp-content/uploads/2019/11/transition-persistence-context.png)](https://www.baeldung.com/wp-content/uploads/2019/11/transition-persistence-context.png)

当我们在事务中执行任何操作时，EntityManager会检查持久化上下文。如果存在，则将使用它。否则，它将创建一个持久性上下文。

默认持久性上下文类型是 PersistenceContextType.TRANSACTION。要告诉EntityManager使用事务持久性上下文，我们只需使用@PersistenceContext对其进行注解：

```java
@PersistenceContext
private EntityManager entityManager;
```

### 3.2. 扩展范围的持久性上下文

扩展的持久性上下文可以跨越多个事务。我们可以在没有事务的情况下保留实体，但不能在没有事务的情况下刷新它。

[![扩展持久性上下文图](https://www.baeldung.com/wp-content/uploads/2019/11/extended-persistence-context.png)](https://www.baeldung.com/wp-content/uploads/2019/11/extended-persistence-context.png)

要告诉EntityManager使用扩展范围的持久性上下文，我们需要应用@PersistenceContext的类型属性：

```java
@PersistenceContext(type = PersistenceContextType.EXTENDED)
private EntityManager entityManager;
```

在无状态会话 bean 中，一个组件中的扩展持久性上下文完全不知道另一个组件的任何持久性上下文。即使两者在同一个事务中也是如此。

假设我们在 Component A的方法中保留了一些实体，该方法在事务中运行。然后我们调用 Component B的一些方法。在组件B 的方法持久化上下文中，我们将找不到之前在组件A的方法中持久化的实体。

## 4.持久化上下文示例

现在我们对持久性上下文了解得足够多了，是时候深入研究一个例子了。我们将使用事务持久化上下文和扩展持久化上下文创建不同的用例。

首先，让我们创建我们的服务类TransctionPersistenceContextUserService：

```java
@Component
public class TransctionPersistenceContextUserService {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Transactional
    public User insertWithTransaction(User user) {
        entityManager.persist(user);
        return user;
    }
    
    public User insertWithoutTransaction(User user) {
        entityManager.persist(user);
        return user;
    }
    
    public User find(long id) {
        return entityManager.find(User.class, id);
    }
}
```

下一个类ExtendedPersistenceContextUserService与上面的非常相似，除了@PersistenceContext注解。这次我们将PersistenceContextType.EXTENDED传递到其@PersistenceContext注解的类型参数中：

```java
@Component
public class ExtendedPersistenceContextUserService {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    // Remaining code same as above
}
```

## 5. 测试用例

现在我们已经设置了服务类，是时候使用事务持久性上下文和扩展持久性上下文创建不同的用例了。

### 5.1. 测试事务持久性上下文

让我们使用事务范围的持久化上下文来持久化一个用户实体。该实体将保存在持久存储中。然后，我们通过使用扩展持久性上下文的EntityManager进行查找调用来进行验证：

```java
User user = new User(121L, "Devender", "admin");
transctionPersistenceContext.insertWithTransaction(user);

User userFromTransctionPersistenceContext = transctionPersistenceContext
  .find(user.getId());
assertNotNull(userFromTransctionPersistenceContext);

User userFromExtendedPersistenceContext = extendedPersistenceContext
  .find(user.getId());
assertNotNull(userFromExtendedPersistenceContext);
```

当我们尝试插入没有事务的用户实体时，将抛出TransactionRequiredException ：

```java
@Test(expected = TransactionRequiredException.class)
public void testThatUserSaveWithoutTransactionThrowException() {
    User user = new User(122L, "Devender", "admin");
    transctionPersistenceContext.insertWithoutTransaction(user);
}
```

### 5.2. 测试扩展持久性上下文

接下来，让我们使用扩展的持久性上下文而不使用事务来持久化用户。User实体将保存在持久性上下文(缓存)中，但不会保存在持久性存储中：

```java
User user = new User(123L, "Devender", "admin");
extendedPersistenceContext.insertWithoutTransaction(user);

User userFromExtendedPersistenceContext = extendedPersistenceContext
  .find(user.getId());
assertNotNull(userFromExtendedPersistenceContext);

User userFromTransctionPersistenceContext = transctionPersistenceContext
  .find(user.getId());
assertNull(userFromTransctionPersistenceContext);
```

在任何持久实体身份的持久上下文中，都会有一个唯一的实体实例。如果我们尝试保留具有相同标识符的另一个实体：

```java
@Test(expected = EntityExistsException.class)
public void testThatPersistUserWithSameIdentifierThrowException() {
    User user1 = new User(126L, "Devender", "admin");
    User user2 = new User(126L, "Devender", "admin");
    extendedPersistenceContext.insertWithoutTransaction(user1);
    extendedPersistenceContext.insertWithoutTransaction(user2);
}
```

我们会看到EntityExistsException：

```java
javax.persistence.EntityExistsException: 
A different object with the same identifier value
was already associated with the session
```

事务中的扩展持久性上下文在事务结束时将实体保存在持久性存储中：

```java
User user = new User(127L, "Devender", "admin");
extendedPersistenceContext.insertWithTransaction(user);

User userFromDB = transctionPersistenceContext.find(user.getId());
assertNotNull(userFromDB);
```

当在事务中使用时，扩展的持久性上下文将缓存的实体刷新到持久性存储中。首先，我们在没有事务的情况下持久化实体。接下来，我们在事务中持久化另一个实体：

```java
User user1 = new User(124L, "Devender", "admin");
extendedPersistenceContext.insertWithoutTransaction(user1);

User user2 = new User(125L, "Devender", "admin");
extendedPersistenceContext.insertWithTransaction(user2);

User user1FromTransctionPersistenceContext = transctionPersistenceContext
  .find(user1.getId());
assertNotNull(user1FromTransctionPersistenceContext);

User user2FromTransctionPersistenceContext = transctionPersistenceContext
  .find(user2.getId());
assertNotNull(user2FromTransctionPersistenceContext);
```

## 六. 总结

在本教程中，我们对持久性上下文有了很好的理解。

首先，我们查看了事务持久化上下文，它存在于事务的整个生命周期中。然后，我们研究了扩展的持久性上下文，它可以跨越多个事务。