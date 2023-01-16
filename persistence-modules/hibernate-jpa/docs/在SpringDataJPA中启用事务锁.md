## 1. 概述

在本快速教程中，我们将讨论在 Spring Data JPA 中为[自定义查询方法](https://www.baeldung.com/spring-data-jpa-query)和预定义存储库 CRUD 方法启用事务锁。

我们还将了解不同的锁类型和设置事务锁超时。

## 2.锁类型

JPA 定义了两种主要的锁类型，悲观锁和乐观锁。

### 2.1. 悲观锁

当我们在事务中使用[悲观锁](https://www.baeldung.com/jpa-pessimistic-locking)，并访问一个实体时，它会立即被锁定。事务通过提交或回滚事务来释放锁。

### 2.2. 乐观锁

在[乐观锁定](https://www.baeldung.com/jpa-optimistic-locking)中，事务不会立即锁定实体。相反，交易通常会保存实体的状态，并为其分配一个版本号。

当我们尝试在不同的事务中更新实体的状态时，事务会在更新期间将保存的版本号与现有版本号进行比较。

此时，如果版本号不同，就意味着我们不能修改实体。如果有一个活动事务，那么该事务将被回滚并且底层 JPA 实现将抛出一个[OptimisticLockException](https://docs.oracle.com/javaee/7/api/javax/persistence/OptimisticLockException.html)。

根据最适合我们当前开发环境的方法，我们还可以使用其他方法，例如时间戳、哈希值计算或序列化校验和。

## 3.在查询方法上启用事务锁

要获取实体上的锁，我们可以通过传递所需的锁模式类型来使用[Lock](https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/Lock.html)注解来注解目标查询方法。

[锁定模式类型](https://docs.oracle.com/javaee/7/api/javax/persistence/LockModeType.html)是在锁定实体时指定的枚举值。然后将指定的锁定模式传播到数据库以在实体对象上应用相应的锁定。

要在 Spring Data JPA 存储库的自定义查询方法上指定锁定，我们可以使用@Lock注解该方法并指定所需的锁定模式类型：

```java
@Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
@Query("SELECT c FROM Customer c WHERE c.orgId = ?1")
public List<Customer> fetchCustomersByOrgId(Long orgId);
```

要对预定义的存储库方法(例如findAll或findById(id) )强制执行锁定，我们必须在存储库中声明该方法并使用Lock注解对该方法进行注解：

```java
@Lock(LockModeType.PESSIMISTIC_READ)
public Optional<Customer> findById(Long customerId);
```

当锁定被显式启用并且没有活动事务时，底层 JPA 实现将抛出[TransactionRequiredException](https://docs.oracle.com/javaee/7/api/javax/persistence/TransactionRequiredException.html)。

如果无法授予锁定，并且锁定冲突不会导致事务回滚，则 JPA 会抛出[LockTimeoutException](https://docs.oracle.com/javaee/7/api/javax/persistence/LockTimeoutException.html)，但不会将活动事务标记为回滚。

## 4.设置事务锁定超时

使用悲观锁时，数据库会尝试立即锁定实体。当无法立即获得锁时，底层 JPA 实现将抛出LockTimeoutException 。为了避免此类异常，我们可以指定锁定超时值。

[在 Spring Data JPA 中，可以使用QueryHints](https://docs.spring.io/spring-data/jpa/docs/current/api/index.html?org/springframework/data/jpa/repository/QueryHints.html)注解通过在查询方法上放置[QueryHint](https://docs.oracle.com/javaee/7/api/javax/persistence/QueryHint.html)来指定锁定超时：

```java
@Lock(LockModeType.PESSIMISTIC_READ)
@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
public Optional<Customer> findById(Long customerId);
```

[我们可以在这篇ObjectDB 文章](https://www.objectdb.com/java/jpa/persistence/lock#Pessimistic_Locking_)中找到有关在不同范围内设置锁定超时提示的更多详细信息。

## 5.总结

在本文中，我们探讨了不同类型的事务锁定模式。然后我们学习了如何在 Spring Data JPA 中启用事务锁。我们还介绍了设置锁定超时。

在正确的位置应用正确的事务锁有助于在高容量并发使用的应用程序中保持数据完整性。

当事务需要严格遵守 ACID 规则时，我们应该使用悲观锁。当我们需要允许多个并发读取并且在应用程序上下文中可以接受最终一致性时，应该应用乐观锁定。