## 1. 概述

检测事务对于审计目的或处理未实现良好事务约定的复杂代码库可能很有用。

在这个简短的教程中，我们将介绍几种在我们的代码中检测[Spring事务](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)的方法。

## 2. 事务配置

为了让事务在Spring中工作，必须启用事务管理。**如果我们使用带有spring-data-\*或spring-tx依赖项的Spring Boot项目，Spring将默认启用事务管理**。否则，我们必须启用事务并显式提供事务管理器。

首先，我们需要将[@EnableTransactionManagement](https://www.baeldung.com/spring-enable-annotations#enabletransactionmanagement)注解添加到我们的@Configuration类上。这为我们的项目启用了Spring的注解驱动的事务管理。

接下来，我们必须提供[PlatformTransactionManager](https://www.baeldung.com/spring-programmatic-transaction-management#platform-transaction-manager)或ReactiveTransactionManager bean。这个bean需要一个DataSource。我们可以选择使用一些通用库，例如用于H2或MySQL的库。我们的实现对于本教程无关紧要。

启用事务后，我们就可以使用[@Transactional](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring#the-transactional-annotation)注解来生成事务。

## 3. 使用TransactionSynchronizationManager

Spring提供了一个名为TransactionSynchronizationManager的类。值得庆幸的是，这个类有一个静态方法可以让我们知道是否处于一个事务中，称为isActualTransactionActive()。

为了测试这一点，让我们用@Transactional标注一个测试方法。然后我们可以断言isActualTransactionActive()返回true：

```java
@Test
@Transactional
void givenTransactional_whenCheckingForActiveTransaction_thenReceiveTrue() {
    assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
}
```

同样，当我们删除@Transactional注解时，测试应该断言返回false：

```java
@Test
void givenNoTransactional_whenCheckingForActiveTransaction_thenReceiveFalse() {
    assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
}
```

## 4. 使用Spring事务日志记录

也许我们不需要以编程方式检测事务。如果我们只想在应用程序的日志中查看事务发生的时间，**我们可以在属性文件中启用Spring的事务日志**：

```properties
logging.level.org.springframework.transaction.interceptor=TRACE
```

启用该日志记录级别后，事务日志将开始i西安市：

```shell
2022-11-25 14:45:07,162 TRACE - Getting transaction for [com.Class.method]
2022-11-25 14:45:07,273 TRACE - Completing transaction for [com.Class.method]
```

如果没有任何上下文，这些日志不会提供非常有用的信息。我们可以简单地添加一些我们自己的日志记录，然后应该能够轻松地看到事务在我们的Spring管理代码中发生的位置。

## 5. 总结

在本文中，我们了解了如何检查Spring事务是否处于活动状态。我们学习了如何使用TransactionSynchronizationManager.isActualTransactionActive()方法以编程方式检测事务。我们还介绍了如何启用Spring的内部事务日志记录，以防我们希望在日志中看到事务。