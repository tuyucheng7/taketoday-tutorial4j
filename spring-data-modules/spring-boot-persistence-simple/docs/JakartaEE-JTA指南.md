## 1. 概述

**Java Transaction API，通常称为JTA，是一种用于管理Java事务的API**。它允许我们以与资源无关的方式启动、提交和回滚事务。

JTA的真正强大之处在于它能够在单个事务中管理多个资源(即数据库、消息服务)。

在本教程中，我们将从概念层面上了解JTA，并了解业务代码通常如何与JTA交互。

## 2. 通用API和分布式事务

JTA为业务代码提供了对事务控制(开始、提交和回滚)的抽象。

如果没有这种抽象，我们将不得不处理每种资源类型的单独API。

比如我们需要像[这样](https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html)处理JDBC资源。同样，JMS资源可能具有[相似但不兼容的模型](https://docs.oracle.com/cd/E19798-01/821-1841/bncgh/index.html)。

**通过JTA，我们可以对多种不同类型的资源进行一致、协调的管理**。

作为API，JTA定义了由事务管理器实现的接口和语义。实现由[Narayana](http://narayana.io/)和[Atomikos](https://www.baeldung.com/java-atomikos)等库提供。

## 3. 示例项目设置

示例应用程序是银行应用程序的一个非常简单的后端服务。我们有两个Service，BankAccountService和AuditService使用两个不同的数据库。**这些独立的数据库需要在事务开始、提交或回滚时进行协调**。

首先，我们的示例项目使用Spring Boot来简化配置：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.6.6</version>
</parent>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jta-atomikos</artifactId>
</dependency>
```

最后，在每个测试方法之前，我们用空数据初始化AUDIT_LOG并用2行初始化数据库ACCOUNT：

```shell
+-----------+----------------+
| ID        |  BALANCE       |
+-----------+----------------+
| a0000001  |  1000          |  
| a0000002  |  2000          |
+-----------+----------------+
```

## 4. 声明式事务划分

在JTA中处理事务的第一种方法是使用[@Transactional](https://docs.oracle.com/javaee/7/api/javax/transaction/Transactional.html)注解。有关更详细的解释和配置，请参阅[本文](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)。

让我们用@Transactional标注Service方法executeTransfer()。这指示事务管理器开始事务：

```java
@Transactional
public void executeTransfer(String fromAccontId, String toAccountId, BigDecimal amount) {
    bankAccountService.transfer(fromAccontId, toAccountId, amount);
    auditService.log(fromAccontId, toAccountId, amount);
    // ...
}
```

在这里，方法executeTransfer()调用2个不同的服务，AccountService和AuditService。这些服务使用2个不同的数据库。

**当executeTransfer()返回时，事务管理器会识别到这是事务的结束并将提交给两个数据库**：

```java
tellerService.executeTransfer("a0000001", "a0000002", BigDecimal.valueOf(500));
assertThat(accountService.balanceOf("a0000001")).isEqualByComparingTo(BigDecimal.valueOf(500));        
assertThat(accountService.balanceOf("a0000002")).isEqualByComparingTo(BigDecimal.valueOf(2500));

TransferLog lastTransferLog = auditService.lastTransferLog();
assertThat(lastTransferLog).isNotNull();        
assertThat(lastTransferLog.getFromAccountId()).isEqualTo("a0000001");
assertThat(lastTransferLog.getToAccountId()).isEqualTo("a0000002"); 
assertThat(lastTransferLog.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(500));
```

### 4.1 在声明性事务中回滚

在方法的最后，executeTransfer()检查账户余额，如果资金不足则抛出RuntimeException：

```java
@Transactional
public void executeTransfer(String fromAccontId, String toAccountId, BigDecimal amount) {
    bankAccountService.transfer(fromAccontId, toAccountId, amount);
    auditService.log(fromAccontId, toAccountId, amount);
    BigDecimal balance = bankAccountService.balanceOf(fromAccontId);
    if(balance.compareTo(BigDecimal.ZERO) < 0) {
        throw new RuntimeException("Insufficient fund.");
    }
}
```

**超过第一个@Transactional的未处理的RuntimeException会将事务回滚到两个数据库**。实际上，执行金额大于余额的转账将导致回滚：

```java
assertThatThrownBy(() -> {
    tellerService.executeTransfer("a0000002", "a0000001", BigDecimal.valueOf(10000));
}).hasMessage("Insufficient fund.");

assertThat(accountService.balanceOf("a0000001")).isEqualByComparingTo(BigDecimal.valueOf(1000));
assertThat(accountService.balanceOf("a0000002")).isEqualByComparingTo(BigDecimal.valueOf(2000));
assertThat(auditServie.lastTransferLog()).isNull();
```

## 5. 编程化事务划分

另一种控制JTA事务的方法是以编程方式使用[UserTransaction](https://javaee.github.io/javaee-spec/javadocs/javax/transaction/UserTransaction.html)。

现在让我们修改executeTransfer()以手动处理事务：

```java
userTransaction.begin();
 
bankAccountService.transfer(fromAccontId, toAccountId, amount);
auditService.log(fromAccontId, toAccountId, amount);
BigDecimal balance = bankAccountService.balanceOf(fromAccontId);
if(balance.compareTo(BigDecimal.ZERO) < 0) {
    userTransaction.rollback();
    throw new RuntimeException("Insufficient fund.");
} else {
    userTransaction.commit();
}
```

在我们的示例中，begin()方法启动了一个新事务。如果余额验证失败，我们调用rollback()将回滚两个数据库。否则，**对commit()的调用会将更改提交到两个数据库**。

重要的是要注意commit()和rollback()都会结束当前事务。

最后，使用编程划分为我们提供了细粒度事务控制的灵活性。

## 6. 总结

在本文中，我们讨论了JTA试图解决的问题。**代码示例说明了使用注解和以编程方式控制事务**，涉及需要在单个事务中协调的2个事务资源。