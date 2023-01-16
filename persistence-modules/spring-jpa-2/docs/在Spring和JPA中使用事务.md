## 1. 概述

本教程将讨论配置 Spring Transactions 的正确方法、如何使用@Transactional注解和常见陷阱。

有关核心持久性配置的更深入讨论，请查看[Spring with JPA 教程](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)。

基本上，有两种截然不同的方式来配置事务，注解和 AOP，各有各的优势。我们将在这里讨论更常见的注解配置。

## 延伸阅读：

## [为测试配置单独的 Spring DataSource](https://www.baeldung.com/spring-testing-separate-data-source)

一个快速、实用的教程，介绍如何配置单独的数据源以在 Spring 应用程序中进行测试。

[阅读更多](https://www.baeldung.com/spring-testing-separate-data-source)→

## [使用Spring Boot加载初始数据的快速指南](https://www.baeldung.com/spring-boot-data-sql-and-schema-sql)

在Spring Boot中使用 data.sql 和 schema.sql 文件的快速实用示例。

[阅读更多](https://www.baeldung.com/spring-boot-data-sql-and-schema-sql)→

## [从Spring Boot显示 Hibernate/JPA SQL 语句](https://www.baeldung.com/sql-logging-spring-boot)

了解如何在Spring Boot应用程序中配置生成的 SQL 语句的日志记录。

[阅读更多](https://www.baeldung.com/sql-logging-spring-boot)→

## 2. 配置交易

Spring 3.1 引入了@EnableTransactionManagement注解，我们可以在@Configuration类中使用它来启用事务支持：

```java
@Configuration
@EnableTransactionManagement
public class PersistenceJPAConfig{

   @Bean
   public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
       //...
   }

   @Bean
   public PlatformTransactionManager transactionManager() {
      JpaTransactionManager transactionManager = new JpaTransactionManager();
      transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
      return transactionManager;
   }
}
```

但是，如果我们使用的是Spring Boot项目并且在类路径上有 spring-data- 或 spring-tx 依赖项，那么事务管理将默认启用。

## 3. 使用 XML 配置事务

对于 3.1 之前的版本，或者如果Java不是一个选项，这里是使用注解驱动和命名空间支持的 XML 配置：

```xml
<bean id="txManager" class="org.springframework.orm.jpa.JpaTransactionManager">
   <property name="entityManagerFactory" ref="myEmf" />
</bean>
<tx:annotation-driven transaction-manager="txManager" />
```

## 4. @Transactional注解

配置事务后，我们现在可以在类或方法级别使用@Transactional注解 bean：

```java
@Service
@Transactional
public class FooService {
    //...
}
```

注解还支持进一步的配置：

-   事务的传播类型
-   事务的隔离级别
-   事务包装的操作超时
-   一个readOnly 标志——提示持久性提供者事务应该是只读的
-   事务的回滚规则

请注意，默认情况下，回滚仅针对运行时、未经检查的异常发生。检查的异常不会触发事务的回滚。当然，我们可以使用rollbackFor和noRollbackFor注解参数配置此行为。

## 5. 潜在的陷阱

### 5.1. 交易和代理

在高层次上， Spring 为所有用@Transactional注解的类创建代理，无论是在类上还是在任何方法上。代理允许框架在运行方法前后注入事务逻辑，主要用于启动和提交事务。

需要牢记的重要一点是，如果事务 bean 正在实现一个接口，则默认代理将是Java动态代理。这意味着只有通过代理传入的外部方法调用才会被拦截。任何自调用都不会启动任何事务，即使该方法具有@Transactional注解也是如此。

使用代理的另一个警告是只有公共方法才应该用@Transactional 注解。任何其他可见性的方法将简单地忽略注解，因为它们没有被代理。

### 5.2. 更改隔离级别

```java
courseDao.createWithRuntimeException(course);
```

我们还可以更改事务隔离级别：

```java
@Transactional(isolation = Isolation.SERIALIZABLE)
```

请注意，这实际上已在 Spring 4.1中[引入；](https://jira.spring.io/browse/SPR-5012)如果我们在 Spring 4.1 之前运行上面的示例，它将导致：

```java
org.springframework.transaction.InvalidIsolationLevelException: Standard JPA does not support custom isolation levels – use a special JpaDialect for your JPA implementation
```

### 5.3. 只读事务

readOnly标志通常会产生混淆，尤其是在使用 JPA 时。来自 Javadoc：

```java
This just serves as a hint for the actual transaction subsystem; it will not necessarily cause failure of write access attempts. A transaction manager which cannot interpret the read-only hint will not throw an exception when asked for a read-only transaction.
```

事实上，我们不能确定在设置了readOnly标志时不会发生插入或更新。此行为依赖于供应商，而 JPA 与供应商无关。

了解readOnly标志仅在事务内部相关也很重要。如果操作发生在事务上下文之外，则该标志将被忽略。一个简单的例子是调用一个方法注解：

```java
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
```

从非事务性上下文中，不会创建事务并且将忽略readOnly标志。

### 5.4. 事务日志

了解事务相关问题的一个有用方法是微调事务包中的日志记录。Spring 中的相关包是“ org.springframework.transaction”，应该配置日志记录级别为TRACE。

### 5.5. 事务回滚

@Transactional注解是指定方法事务语义的元数据。我们有两种回滚事务的方法：声明式和编程式。

在声明式方法中，我们使用 @Transactional 注解对方法进行注解。@Transactional注解使用属性rollbackFor或rollbackForClassName来回滚事务，并使用属性noRollbackFor或noRollbackForClassName来避免在列出的异常上回滚。

声明式方法中的默认回滚行为将在运行时异常时回滚。

让我们看一个简单的例子，使用声明性方法来回滚运行时异常或错误的事务：

```java
@Transactional
public void createCourseDeclarativeWithRuntimeException(Course course) {
    courseDao.create(course);
    throw new DataIntegrityViolationException("Throwing exception for demoing Rollback!!!");
}
```

接下来，我们将使用声明式方法为列出的已检查异常回滚事务。我们示例中的回滚是在SQLException上：

```java
@Transactional(rollbackFor = { SQLException.class })
public void createCourseDeclarativeWithCheckedException(Course course) throws SQLException {
    courseDao.create(course);
    throw new SQLException("Throwing exception for demoing rollback");
}
```

让我们看看在声明性方法中如何简单使用属性noRollbackFor来防止列出的异常的事务回滚：

```java
@Transactional(noRollbackFor = { SQLException.class })
public void createCourseDeclarativeWithNoRollBack(Course course) throws SQLException {
    courseDao.create(course);
    throw new SQLException("Throwing exception for demoing rollback");
}
```

在编程方法中，我们使用TransactionAspectSupport回滚事务：

```java
public void createCourseDefaultRatingProgramatic(Course course) {
    try {
       courseDao.create(course);
    } catch (Exception e) {
       TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
```

声明式回滚策略应该 优于编程式回滚策略。

## 六. 总结

在本文中，我们介绍了使用Java和 XML 的事务语义的基本配置。我们还学习了如何使用@Transactional，以及事务策略的最佳实践。