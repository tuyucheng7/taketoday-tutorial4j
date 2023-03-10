## 1. 概述

Spring的[@Transactional](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)注解提供了一个很好的声明式API来标记事务边界。

在幕后，Spring通过一个切面负责创建和维护事务，并且它们在每次出现的@Transactional注解中定义。这种方法可以很容易地将我们的核心业务逻辑与事务管理等[横切关注点分离](https://en.wikipedia.org/wiki/Cross-cutting_concern)。

在本教程中，我们将看到这并不总是最好的方法。我们将探讨Spring提供了哪些编程式替代方案，例如TransactionTemplate，以及我们使用它们的原因。

## 2. 天堂里的麻烦

假设我们在一个简单的服务(Service)中混合使用两种不同类型的I/O：

```java
@Transactional
public void initialPayment(PaymentRequest request) {
    savePaymentRequest(request); // DB
    callThePaymentProviderApi(request); // API
    updatePaymentState(request); // DB
    saveHistoryForAuditing(request); // DB
}
```

在这里，我们有一些数据库调用以及一个可能耗时昂贵的REST API调用。乍一看，使整个方法具有事务性可能是有意义的，因为我们可能希望使用一个EntityManager以原子方式执行整个操作。

但是，如果这里的外部API出于某种原因需要比平时更长的时间来响应，我们可能很快就会耗尽数据库连接！

### 2.1 现实的残酷本质

以下是我们调用initialPayment方法时发生的情况：

1.  事务切面创建一个新的EntityManager，并开始了一个新的事务，因此它从连接池中借用了一个Connection
2.  在第一次数据库调用之后，它会调用外部API，同时保留借用的Connection
3.  最后，它使用该Connection来执行剩余的数据库调用

**如果API调用在一段时间内响应非常缓慢，则此方法将在等待响应时占用借用的Connection**。

想象一下，在此期间我们收到了对initialPayment方法的大量调用。在这种情况下，所有连接都可能等待API调用的响应。**这就是为什么我们可能会耗尽数据库连接的原因-因为后端服务速度很慢！**

**在事务上下文中将数据库I/O与其他类型的I/O糅合在一起并不是一个好主意。因此，解决此类问题的第一个解决方案是将这些类型的I/O完全分开**。如果出于某种原因我们无法将它们分开，我们仍然可以使用Spring API手动管理事务。

## 3. 使用TransactionTemplate

[TransactionTemplate](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/transaction/support/TransactionTemplate.html)提供了一组基于回调的API来手动管理事务。为了使用它，我们应该首先使用PlatformTransactionManager初始化它。

我们可以使用依赖注入来设置这个模板：

```java
// test annotations
class ManualTransactionIntegrationTest {

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    // omitted
}
```

PlatformTransactionManager用于帮助模板创建、提交或回滚事务。

当使用Spring Boot时，一个合适的PlatformTransactionManager类型的bean会被自动注册，所以我们只需要简单地注入它。否则，我们应该[手动注册](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)一个PlatformTransactionManager bean。

### 3.1 示例域模型

从现在开始，为了演示，我们将使用简化的支付域模型。

在这个简单的域中，我们有一个Payment实体来封装每笔支付的详细信息：

```java
@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    private Long amount;

    @Column(unique = true)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    private State state;

    // getters and setters

    public enum State {
        STARTED, FAILED, SUCCESSFUL
    }
}
```

此外，我们将在测试类中运行所有测试，使用[Testcontainers](https://www.baeldung.com/spring-boot-testcontainers-integration-test)库在每个测试用例之前运行PostgreSQL实例：

```java
@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = NONE)
@Transactional(propagation = NOT_SUPPORTED) // we're going to handle transactions manually
class ManualTransactionIntegrationTest {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EntityManager entityManager;

    @Container
    private static PostgreSQLContainer<?> pg = initPostgres();

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    // tests

    private static PostgreSQLContainer<?> initPostgres() {
        PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:11.1")
              .withDatabaseName("tuyucheng")
              .withUsername("test")
              .withPassword("test");
        pg.setPortBindings(singletonList("54320:5432"));

        return pg;
    }
}
```

### 3.2 具有结果的事务

TransactionTemplate提供了一个名为[execute](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/transaction/support/TransactionTemplate.html#execute-org.springframework.transaction.support.TransactionCallback-)的方法，它可以在事务中运行任何给定的代码块，然后返回一些结果：

```java
@Test
void givenAPayment_WhenNotDuplicate_ThenShouldCommit() {
	Long id = transactionTemplate.execute(status -> {
		Payment payment = new Payment();
		payment.setAmount(1000L);
		payment.setReferenceNumber("Ref-1");
		payment.setState(Payment.State.SUCCESSFUL);
        
		entityManager.persist(payment);
        
		return payment.getId();
	});
    
	Payment payment = entityManager.find(Payment.class, id);
	assertThat(payment).isNotNull();
}
```

在这里，我们将一个新的Payment实例保存到数据库中，然后返回其自动生成的ID。

与声明式方法类似，**模板可以为我们保证原子性**。

如果事务中的某个操作未能正常完成，它将回滚所有操作：

```java
@Test
void givenTwoPayments_WhenRefIsDuplicate_ThenShouldRollback() {
	try {
		transactionTemplate.execute(s -> {
			Payment first = new Payment();
			first.setAmount(1000L);
			first.setReferenceNumber("Ref-1");
			first.setState(Payment.State.SUCCESSFUL);
            
			Payment second = new Payment();
			second.setAmount(2000L);
			second.setReferenceNumber("Ref-1");
			second.setState(Payment.State.SUCCESSFUL);
            
			entityManager.persist(first); // ok
			entityManager.persist(second); // fails
            
			return "Ref-1";
		});
	} catch (Exception ignored) {
	}
    
	assertThat(entityManager
	    .createQuery("select p from Payment p", Payment.class)
	    .getResultList()).isEmpty();
}
```

**由于第二个Payment实例的referenceNumber重复，数据库拒绝了第二个persist操作，导致整个事务回滚**。因此，数据库不包含事务开启后的任何Payment操作。

也可以通过在[TransactionStatus](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/transaction/TransactionStatus.html)上调用setRollbackOnly()来手动触发回滚：

```java
@Test
void givenAPayment_WhenMarkAsRollback_ThenShouldRollback() {
	transactionTemplate.execute(status -> {
		Payment payment = new Payment();
		payment.setAmount(1000L);
		payment.setReferenceNumber("Ref-1");
		payment.setState(Payment.State.SUCCESSFUL);
        
		entityManager.persist(payment);
		status.setRollbackOnly();
        
		return payment.getId();
	});
    
	assertThat(entityManager
	    .createQuery("select p from Payment p", Payment.class)
	    .getResultList()).isEmpty();
}
```

### 3.3 没有结果的事务

如果我们不打算从事务中返回任何内容，我们可以使用[TransactionCallbackWithoutResult](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/support/TransactionCallbackWithoutResult.html)回调类：

```java
@Test
void givenAPayment_WhenNotExpectingAnyResult_ThenShouldCommit() {
	transactionTemplate.execute(new TransactionCallbackWithoutResult() {
		@Override
		protected void doInTransactionWithoutResult(TransactionStatus status) {
			Payment payment = new Payment();
			payment.setReferenceNumber("Ref-1");
			payment.setState(Payment.State.SUCCESSFUL);
            
			entityManager.persist(payment);
		}
	});
    
	assertThat(entityManager
	    .createQuery("select p from Payment p", Payment.class)
	    .getResultList()).hasSize(1);
}
```

### 3.4 自定义事务配置

到目前为止，我们使用的是默认配置的TransactionTemplate。虽然这些默认值在大多数情况下已经足够了，但我们仍然可以手动更改配置。

让我们设置[事务隔离级别](https://en.wikipedia.org/wiki/Isolation_(database_systems))：

```java
transactionTemplate = new TransactionTemplate(transactionManager);
transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
```

类似地，我们可以更改事务传播行为：

```java
transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
```

或者我们可以为事务设置一个超时时间(以秒为单位)：

```java
transactionTemplate.setTimeout(1000);
```

甚至可以从只读事务的优化中获益：

```java
transactionTemplate.setReadOnly(true);
```

一旦我们创建了一个带有配置的TransactionTemplate，所有的事务都将使用该配置来执行。因此，**如果我们需要多个配置，我们应该创建多个模板实例**。

## 4. 使用PlatformTransactionManager

除了TransactionTemplate之外，我们还可以使用更低级别的API(如[PlatformTransactionManager](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/transaction/PlatformTransactionManager.html))来手动管理事务。非常有趣的是，@Transactional和TransactionTemplate都使用这个API在内部管理它们的事务。

### 4.1 配置事务

在使用这个API之前，我们应该定义我们事务的基本配置。

让我们使用可重复读事务隔离级别设置三秒超时：

```java
DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
definition.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
definition.setTimeout(3);
```

事务定义类似于TransactionTemplate配置。但是，**我们可以在一个PlatformTransactionManager中使用多个定义**。

### 4.2 维护事务

配置我们的事务后，我们可以通过编程方式管理事务：

```java
@Test
void givenAPayment_WhenUsingTxManager_ThenShouldCommit() {
	DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
	definition.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
	definition.setTimeout(3);
    
	TransactionStatus status = transactionManager.getTransaction(definition);
	try {
		Payment payment = new Payment();
		payment.setReferenceNumber("Ref-1");
		payment.setState(Payment.State.SUCCESSFUL);
        
		entityManager.persist(payment);
		transactionManager.commit(status);
	} catch (Exception ex) {
		transactionManager.rollback(status);
	}
    
	assertThat(entityManager
	    .createQuery("select p from Payment p", Payment.class)
	    .getResultList()).hasSize(1);
}
```

## 5. 总结

在本文中，我们首先看到了何时应该选择编程式事务管理而不是声明式方法。

然后，通过介绍两个不同的API，我们学习了如何手动创建、提交或回滚任何给定的事务。