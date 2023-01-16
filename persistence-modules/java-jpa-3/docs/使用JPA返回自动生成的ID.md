## 1. 简介

在本教程中，我们将讨论如何使用 JPA 处理自动生成的 ID。在看一个实际例子之前，我们必须了解两个关键概念，即生命周期和id生成策略。

## 2. 实体生命周期和Id生成

每个实体在其生命周期中有四种可能的状态。这些状态是new、managed、detached和removed。我们的重点将放在新状态和托管状态上。在对象创建期间，实体处于新状态。因此，EntityManager不知道这个对象。调用 EntityManager 上的persist方法，对象从新状态转换为托管状态。此方法需要一个活动事务。

JPA 定义了四种 id 生成策略。我们可以将这四种策略分为两类：

-   在提交之前，ID 已预先分配并可供EntityManager使用
-   ID 在事务提交后分配

有关每个 id 生成策略的更多详细信息，请参阅我们的文章[JPA 何时设置主键](https://www.baeldung.com/jpa-strategies-when-set-primary-key)。

## 3.问题陈述

返回对象的 id 可能会成为一项繁琐的任务。我们需要了解上一节中提到的原则以避免出现问题。根据 JPA 配置，服务可能会返回 id 等于零(或 null)的对象。重点将放在服务类实现以及不同的修改如何为我们提供解决方案上。

我们将创建一个具有 JPA 规范和 Hibernate 作为其实现的 Maven 模块。为简单起见，我们将使用 H2 内存数据库。

让我们从创建域实体并将其映射到数据库表开始。对于这个例子，我们将创建一个具有一些基本属性的用户实体：

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String password;
 
    //...
}
```

在域类之后，我们将创建一个 UserService类。这个简单的服务将引用EntityManager和一个将User对象保存到数据库的方法：

```java
public class UserService {
    EntityManager entityManager;
 
    public UserService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
 
    @Transactional
    public long saveUser(User user){
        entityManager.persist(user);
        return user.getId();
    }
}
```

这种设置是我们之前提到的一个常见陷阱。我们可以通过测试证明saveUser方法的返回值为零：

```java
@Test
public void whenNewUserIsPersisted_thenEntityHasNoId() {
    User user = new User();
    user.setUsername("test");
    user.setPassword(UUID.randomUUID().toString());
 
    long index = service.saveUser(user);
 
    Assert.assertEquals(0L, index);
}
```

在接下来的部分中，我们将回过头来了解为什么会发生这种情况，以及我们如何解决它。

## 4. 手动交易控制

创建对象后，我们的用户实体处于新状态。在saveUser方法中调用persist方法后，实体状态变为托管状态。我们从回顾部分记得，托管对象在事务提交后获得一个 id。由于saveUser方法仍在运行，@Transactional 注解创建的事务[尚未](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)提交。当saveUser完成执行时，我们的托管实体获得一个 id 。

一种可能的解决方案是手动调用EntityManager上的flush方法。另一方面，我们可以手动控制事务并保证我们的方法正确返回id。我们可以用EntityManager做到这一点：

```java
@Test
public void whenTransactionIsControlled_thenEntityHasId() {
    User user = new User();
    user.setUsername("test");
    user.setPassword(UUID.randomUUID().toString());
     
    entityManager.getTransaction().begin();
    long index = service.saveUser(user);
    entityManager.getTransaction().commit();
     
    Assert.assertEquals(2L, index);
}
```

## 5. 使用 Id 生成策略

到目前为止，我们使用的是第二类，其中 id 分配发生在事务提交之后。预分配策略可以在事务提交之前为我们提供 id，因为它们在内存中保留了一些 id。此选项并不总是可以实现，因为并非所有数据库引擎都支持所有生成策略。将策略更改为GenerationType.SEQUENCE可以解决我们的问题。此策略使用数据库序列而不是GenerationType.IDENTITY中的自动递增列。

为了改变策略，我们编辑我们的域实体类：

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
 
    //...
}
```

## 六. 总结

在本文中，我们介绍了 JPA 中的 ID 生成技术。首先，我们对 ID 生成最重要的关键方面做了一些回顾。然后我们介绍了 JPA 中使用的常见配置，以及它们的优点和缺点。