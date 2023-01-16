## 1. 简介

[Spring 在整个应用程序代码](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)和[集成测试](https://www.baeldung.com/spring-jpa-test-in-memory-database)中都对声明式事务管理提供了出色的支持。

然而，我们有时可能需要对事务边界进行细粒度控制。

在本文中，我们将看到如何以编程方式与 Spring 在事务测试中设置的自动事务进行交互。

## 2.先决条件

假设我们在 Spring 应用程序中进行了一些集成测试。

具体来说，我们正在考虑与数据库交互的测试，例如，检查我们的持久层是否正常运行。

让我们考虑一个标准的测试类——注解为事务性的：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateConf.class })
@Transactional
public class HibernateBootstrapIntegrationTest { ... }
```

在这样的测试中，每个测试方法都包含在一个事务中，该事务在方法退出时回滚。

当然，也可以只注解特定的方法。我们将在本文中讨论的所有内容也适用于该场景。

## 3. TestTransaction类

我们将在本文的其余部分讨论一个类：org.springframework.test.context.transaction.TestTransaction。

这是一个具有一些静态方法的实用程序类，我们可以使用这些方法与测试中的事务进行交互。

每个方法都与测试方法执行期间存在的唯一当前事务交互。

### 3.1. 检查当前交易的状态

我们在测试中经常做的一件事是检查事物是否处于应有的状态。

因此，我们可能想检查当前是否有活动事务：

```java
assertTrue(TestTransaction.isActive());
```

或者，我们可能有兴趣检查当前事务是否被标记为回滚：

```java
assertTrue(TestTransaction.isFlaggedForRollback());
```

如果是，那么 Spring 将在它结束之前自动或以编程方式将其回滚。否则，它会在关闭之前提交它。

### 3.2. 标记事务以进行提交或回滚

我们可以通过编程方式更改策略以在关闭事务之前提交或回滚事务：

```java
TestTransaction.flagForCommit();
TestTransaction.flagForRollback();
```

通常，测试中的事务在开始时会被标记为回滚。但是，如果该方法具有@Commit注解，它们将开始标记为提交：

```java
@Test
@Commit
public void testFlagForCommit() {
    assertFalse(TestTransaction.isFlaggedForRollback());
}
```

请注意，正如其名称所暗示的那样，这些方法仅标记事务。也就是说，事务不会立即提交或回滚，而只是在它结束之前才提交或回滚。

### 3.3. 开始和结束交易

要提交或回滚事务，我们要么让方法退出，要么显式结束它：

```java
TestTransaction.end();
```

如果稍后，我们想再次与数据库交互，我们必须开始一个新的事务：

```java
TestTransaction.start();
```

请注意，根据方法的默认值，新事务将被标记为回滚(或提交)。换句话说，之前对flagFor... 的调用对新交易没有任何影响。

## 4. 一些实现细节

TestTransaction没什么神奇的。我们现在将查看它的实现，以了解更多有关 Spring 测试中的事务的信息。

我们可以看到它的几个方法只是简单地访问当前事务并封装它的一些功能。

### 4.1. TestTransaction从哪里获取当前交易？

我们直接上代码：

```java
TransactionContext transactionContext
  = TransactionContextHolder.getCurrentTransactionContext();
```

TransactionContextHolder只是一个包含TransactionContext 的ThreadLocal的静态包装器。

### 4.2. 谁设置线程本地上下文？

如果我们查看谁调用了setCurrentTransactionContext方法，我们会发现只有一个调用者：TransactionalTestExecutionListener.beforeTestMethod。

TransactionalTestExecutionListener是 Springs 在注解为@Transactional的测试上自动配置的侦听器。

请注意，TransactionContext不包含对任何实际交易的引用；相反，它只是PlatformTransactionManager的一个外观。

是的，这段代码是高度分层和抽象的。这些通常是 Spring 框架的核心部分。

有趣的是，在如此复杂的情况下，Spring 并没有施展任何黑魔法——只是进行了许多必要的簿记、管道、异常处理等工作。

## 5. 总结

在本快速教程中，我们了解了如何在基于 Spring 的测试中以编程方式与事务交互。