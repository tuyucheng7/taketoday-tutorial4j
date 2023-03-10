## 1. 简介

在本教程中，我们将介绍@Transactional注释及其隔离和传播行为设置。

## 延伸阅读

### [Java和Spring中的事务介绍](https://www.baeldung.com/java-transactions)

Java和Spring事务的快速实用指南。

[阅读更多](https://www.baeldung.com/java-transactions)→

### [Spring中的程序化事务管理](https://www.baeldung.com/spring-programmatic-transaction-management)

学习在Spring中以编程方式管理事务，以及为什么这种方法有时比简单地使用声明性事务注解更好。

[阅读更多](https://www.baeldung.com/spring-programmatic-transaction-management)→

### [检测Spring事务是否处于活动状态](https://www.baeldung.com/spring-transaction-active)

让我们看看如何找到Spring事务是否处于活动状态。

[阅读更多](https://www.baeldung.com/spring-transaction-active)→

## 2. 什么是@Transactional？

我们可以使用[@Transactional](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)将方法包装在数据库事务中。

它允许我们为事务设置传播行为、隔离级别、超时、只读和回滚条件。我们还可以指定事务管理器。

### 2.1 @Transactional实现细节

Spring创建一个代理，或操纵类字节码，来管理事务的创建、提交和回滚。在代理的情况下，Spring忽略内部方法调用中的@Transactional。

简单地说，如果我们有一个像callMethod这样的方法并将其标记为@Transactional，那么Spring将围绕调用@Transactional方法的调用包装一些事务管理代码：

```java
createTransactionIfNecessary();
try {
    callMethod();
    commitTransactionAfterReturning();
} catch (exception) {
    completeTransactionAfterThrowing();
    throw exception;
}
```

### 2.2 如何使用@Transactional

我们可以将该注解放在接口、类的定义上，或者直接放在方法上。它们根据优先级顺序相互覆盖；从最低到最高的优先级为：接口，父类，类，接口方法，父类方法和类方法。

**Spring将类级注解应用于我们没有使用@Transactional标注的该类的所有公共方法**。

**但是，如果我们将注解放在私有或受保护的方法上，Spring将忽略它而不会出错**。

让我们从一个接口示例开始：

```java
@Transactional
public interface TransferService {
    void transfer(String user1, String user2, double val);
}
```

通常不建议在接口上设置@Transactional；但是，对于像Spring Data与@Repository这样的情况，这是可以接受的。我们可以将注解放在类定义上以覆盖接口/父类的事务设置：

```java
@Service
@Transactional
public class TransferServiceImpl implements TransferService {
    @Override
    public void transfer(String user1, String user2, double val) {
        // ...
    }
}
```

现在让我们通过直接在方法上设置注解来覆盖它：

```java
@Transactional
public void transfer(String user1, String user2, double val) {
    // ...
}
```

## 3. 事务传播

传播定义了我们业务逻辑的事务边界。Spring设法根据我们的propagation设置来启动和暂停事务。

Spring根据传播行为调用TransactionManager::getTransaction来获取或创建一个事务，它支持所有类型的TransactionManager的一些传播行为，但其中有一些仅受TransactionManager的特定实现支持。

让我们来看看不同的传播行为及其工作原理。

### 3.1 REQUIRED

REQUIRED是默认传播行为。Spring检查是否存在活动事务，如果不存在，它会创建一个新事务。否则，业务逻辑将附加到当前活动的事务：

```java
@Transactional(propagation = Propagation.REQUIRED)
public void requiredExample(String user) { 
    // ... 
}
```

此外，由于REQUIRED是默认传播行为，我们可以通过删除它来简化代码：

```java
@Transactional
public void requiredExample(String user) { 
    // ... 
}
```

让我们看看REQUIRED传播行为事务创建的伪代码：

```java
if (isExistingTransaction()) {
    if (isValidateExistingTransaction()) {
        validateExisitingAndThrowExceptionIfNotValid();
    }
    return existing;
}
return createNewTransaction();
```

### 3.2 SUPPORTS

对于SUPPORTS，Spring首先检查是否存在活动事务。如果存在事务，则将使用现有事务。如果没有事务，则以非事务方式执行：

```java
@Transactional(propagation = Propagation.SUPPORTS)
public void supportsExample(String user) { 
    // ... 
}
```

让我们看看SUPPORTS的事务创建伪代码：

```java
if (isExistingTransaction()) {
    if (isValidateExistingTransaction()) {
        validateExisitingAndThrowExceptionIfNotValid();
    }
    return existing;
}
return emptyTransaction;
```

### 3.3 MANDATORY

当传播行为是MANDATORY时，如果存在活动的事务，那么它将被使用。如果没有活动事务，则Spring会抛出异常：

```java
@Transactional(propagation = Propagation.MANDATORY)
public void mandatoryExample(String user) { 
    // ... 
}
```

让我们再看看伪代码：

```java
if (isExistingTransaction()) {
    if (isValidateExistingTransaction()) {
        validateExisitingAndThrowExceptionIfNotValid();
    }
    return existing;
}
throw IllegalTransactionStateException;
```

### 3.4 NEVER

对于NEVER传播行为的事务逻辑，如果存在活动事务，Spring将抛出异常：

```java
@Transactional(propagation = Propagation.NEVER)
public void neverExample(String user) { 
    // ... 
}
```

下面是NEVER事务创建的伪代码：

```java
if (isExistingTransaction()) {
    throw IllegalTransactionStateException;
}
return emptyTransaction;
```

### 3.5 NOT_SUPPORTED

如果当前事务存在，则首先Spring将其挂起，然后在没有事务的情况下执行业务逻辑：

```java
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public void notSupportedExample(String user) { 
    // ... 
}
```

**JTATransactionManager支持开箱即用的真正事务挂起。而其他事务管理器通过持有对现有对象的引用然后从线程上下文中清除它来模拟挂起**。

### 3.6 REQUIRES_NEW

当传播行为是REQUIRES_NEW时，Spring会暂停当前事务(如果存在)，然后创建一个新事务：

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void requiresNewExample(String user) { 
    // ... 
}
```

**与NOT_SUPPORTED类似，我们需要JTATransactionManager来实现实际的事务暂停**。

伪代码如下所示：

```java
if (isExistingTransaction()) {
    suspend(existing);
    try {
        return createNewTransaction();
    } catch (exception) {
        resumeAfterBeginException();
        throw exception;
    }
}
return createNewTransaction();
```

### 3.7 NESTED

对于NESTED传播行为，Spring检查事务是否存在，如果存在，则标记一个保存点。这意味着如果我们的业务逻辑执行抛出异常，那么事务就会回滚到这个保存点。如果没有活动事务，它的工作方式与REQUIRED类似。

**DataSourceTransactionManager支持这种开箱即用的传播行为，JTATransactionManager的一些实现也可能支持这一点**。

**[JpaTransactionManager](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/orm/jpa/JpaTransactionManager.html)仅支持JDBC连接的NESTED。但是，如果我们将nestedTransactionAllowed标志设置为true，并且如果我们的JDBC驱动程序支持保存点，那么它也适用于JPA事务中的JDBC访问代码**。


最后，让我们将propagation设置为NESTED：

```java
@Transactional(propagation = Propagation.NESTED)
public void nestedExample(String user) { 
    // ... 
}
```

## 4. 事务隔离

隔离性是常见的ACID属性之一：原子性、一致性、隔离性和持久性。隔离描述了并发事务应用的更改如何相互可见。

每个隔离级别都可以防止对事务产生零个或多个并发副作用：

-   **脏读**：读取并发事务未提交的更改
-   **不可重复读**：如果并发事务更新同一行并提交，则在重新读取一行时获得不同的值
-   **幻读**：如果另一个事务添加或删除范围中的某些行并提交，则在重新执行范围查询后获取不同的行

我们可以通过@Transactional::isolation来设置事务的隔离级别。它在Spring中涉及五个枚举值：DEFAULT、READ_UNCOMMITTED、READ_COMMITTED、REPEATABLE_READ、SERIALIZABLE。

### 4.1 Spring中的隔离管理

默认隔离级别是DEFAULT。因此，当Spring创建新事务时，隔离级别将是我们RDBMS的默认隔离级别。因此，我们在更换数据库时需要小心。

我们还应该考虑调用具有不同隔离级别的方法链的情况。在正常流程中，隔离仅在创建新事务时适用。因此，如果出于任何原因我们不想让一个方法在不同的隔离级别中执行，我们必须将TransactionManager::setValidateExistingTransaction设置为true。

那么事务验证的伪代码将是：

```java
if (isolationLevel != ISOLATION_DEFAULT) {
    if (currentTransactionIsolationLevel() != isolationLevel) {
        throw IllegalTransactionStateException
    }
}
```

现在让我们深入了解不同的隔离级别及其影响。

### 4.2 READ_UNCOMMITTED

READ_UNCOMMITTED是最低的隔离级别，允许最多的并发访问。

因此，它遭受了所有三个提到的并发副作用。具有此隔离级别的事务读取其他并发事务的未提交数据。此外，不可重复读和幻读都可能发生。因此，我们可能在重新读取行或重新执行范围查询时得到不同的结果。

我们可以为方法或类级别设置隔离级别：

```java
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public void log(String message) {
    // ...
}
```

**Postgres不支持READ_UNCOMMITTED隔离级别，而是回退到READ_COMMITTED。此外，Oracle不支持或不允许READ_UNCOMMITTED**。

### 4.3 READ_COMMITTED

第二级隔离级别READ_COMMITTED，防止脏读。

其余的并发副作用仍然可能发生。因此，并发事务中未提交的更改对我们没有影响，但是如果事务提交了它的更改，我们的结果可能会通过重新查询而改变。

在这里，我们设置isolation：

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public void log(String message){
    // ...
}
```

**READ_COMMITTED是Postgres、SQL Server和Oracle的默认级别**。

### 4.4 REPEATABLE_READ

第三级隔离级别REPEATABLE_READ，防止脏读和不可重复读。因此我们不会受到并发事务中未提交更改的影响。

此外，当我们重新查询一行时，我们不会得到不同的结果。但是，在重新执行范围查询时，我们可能会得到新添加或删除的行。

此外，它是防止丢失更新所需的最低级别。当两个或多个并发事务读取和更新同一行时，就会发生丢失更新。REPEATABLE_READ根本不允许同时访问同一行，因此丢失的更新不会发生。

以下是如何为方法设置隔离级别：

```java
@Transactional(isolation = Isolation.REPEATABLE_READ) 
public void log(String message){
    // ...
}
```

**REPEATABLE_READ是Mysql中的默认级别。Oracle不支持REPEATABLE_READ**。

### 4.5 SERIALIZABLE

可序列化读是最高级别的隔离。它可以防止以上所有提到的并发副作用，但可能会导致最低的并发访问率，因为它按顺序执行并发调用。

换句话说，并发执行一组可序列化事务与串行执行它们具有相同的结果。

现在让我们看看如何将SERIALIZABLE设置为隔离级别：

```java
@Transactional(isolation = Isolation.SERIALIZABLE)
public void log(String message){
    // ...
}
```

## 5. 总结

在本文中，我们详细探讨了@Transaction的propagation属性。然后我们了解了并发副作用和隔离级别。