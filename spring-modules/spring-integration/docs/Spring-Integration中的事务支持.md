## 1. 概述

在本教程中，我们将介绍[Spring Integration框架](https://www.baeldung.com/spring-integration)中的事务支持。

## 2. 消息流中的事务

从最早的版本开始，Spring就提供了同步资源和事务的支持。我们经常使用它来同步由多个事务管理器管理的事务。

例如，我们可以将[JMS](https://www.baeldung.com/spring-jms)提交与[JDBC](https://www.baeldung.com/java-jdbc)提交同步。

另一方面，我们在消息流中也有更复杂的用例。它们包括非事务性资源以及各种类型的事务性资源的同步。

通常，消息流可以由两种不同类型的机制发起。

### 2.1 用户进程发起的消息流

某些消息流依赖于第三方进程的启动，例如在某些消息通道上触发消息或调用消息网关方法。

**我们通过Spring的标准事务支持为这些流配置[事务支持](https://www.baeldung.com/transaction-configuration-with-jpa-and-spring)**。流不必由Spring Integration显式配置以支持事务。Spring Integration消息流自然地遵循Spring组件的事务语义。

例如，我们可以使用@Transactional标注ServiceActivator或其方法：

```java
@Transactional
public class TxServiceActivator {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void storeTestResult(String testResult) {
        this.jdbcTemplate.update("insert into STUDENT values(?)", testResult);
        log.info("Test result is stored: {}", testResult);
    }
}
```

我们可以从任何组件运行storeTestResult方法，事务上下文将照常应用。通过这种方法，我们可以完全控制事务配置。

### 2.2 守护进程发起的消息流

我们经常使用这种类型的消息流来实现自动化。例如，Poller轮询消息队列以使用轮询的消息启动新的消息流，或者调度程序通过创建新消息并在预定义的时间启动消息流来调度进程。

本质上，这些是由触发器进程(守护进程)发起的基于触发器的流程。**对于这些流，我们必须提供一些事务配置，以便在新消息流开始时创建事务上下文**。

通过配置，我们将流程委托给Spring现有的事务支持。

在本文的其余部分，我们将重点介绍对此类消息流的事务支持。

## 3. Poller事务支持

Poller是集成流中的常见组件。它定期从各种来源检索数据并通过集成链传递。

Spring Integration为Poller提供了开箱即用的事务支持。任何时候我们配置Poller组件，我们都可以提供事务配置：

```java
@Bean
@InboundChannelAdapter(value = "someChannel", poller = @Poller(value = "pollerMetadata"))
public MessageSource<File> someMessageSource() {
    // ...
}

@Bean
public PollerMetadata pollerMetadata() {
    return Pollers.fixedDelay(5000)
        .advice(transactionInterceptor())
        .transactionSynchronizationFactory(transactionSynchronizationFactory)
        .get();
}

private TransactionInterceptor transactionInterceptor() {
    return new TransactionInterceptorBuilder()
        .transactionManager(txManager)
        .build();
}
```

我们必须提供对TransactionManager和自定义TransactionSynchronizationFactory的引用，或者我们可以依赖默认值。在内部，Spring的本机事务包装了该过程。因此，此轮询器发起的所有消息流都是事务性的。

## 4. 事务边界

当一个事务开始时，事务上下文总是绑定到当前线程。无论我们的消息流中有多少端点和通道，**只要消息流存在于同一个线程中，我们的事务上下文就会始终得到保留**。

如果我们通过在某个服务中启动一个新线程来打破它，我们也会打破事务边界。本质上，事务将在该点结束。

如果线程之间发生了成功的切换，则流程将被视为成功。这将在此时提交事务，但流程将继续，并且它仍然可能导致下游某处出现异常。

因此，该异常可以返回到流程的发起者，以便事务可以以回滚结束。**这就是为什么我们必须在线程边界可能被打破的任何地方使用事务通道**。

例如，我们应该使用JMS、JDBC或其他一些事务通道。

## 5. 事务同步

在某些用例中，将某些操作与包含整个流程的事务同步是有益的。

例如，我们将演示如何使用轮询器读取传入文件，并根据其内容执行数据库更新。当数据库操作完成时，它还会根据操作是否成功重命名文件。

在我们转到示例之前，**了解此方法将文件系统上的操作与事务同步是至关重要的。它不会使本质上不是事务性的文件系统实际上变成事务性的**。

事务在轮询之前开始，并在流程完成时提交或回滚，然后是文件系统上的同步操作。

首先，我们使用一个简单的Poller定义一个InboundChannelAdapter：

```java
@Bean
@InboundChannelAdapter(value = "inputChannel", poller = @Poller(value = "pollerMetadata"))
public MessageSource<File> fileReadingMessageSource() {
    FileReadingMessageSource sourceReader = new FileReadingMessageSource();
    sourceReader.setDirectory(new File(INPUT_DIR));
    sourceReader.setFilter(new SimplePatternFileListFilter(FILE_PATTERN));
    return sourceReader;
}

@Bean
public PollerMetadata pollerMetadata() {
    return Pollers.fixedDelay(5000)
        .advice(transactionInterceptor())
        .transactionSynchronizationFactory(transactionSynchronizationFactory)
        .get();
}
```

如前所述，轮询器包含对TransactionManager的引用。此外，它还包含对TransactionSynchronizationFactory的引用。该组件提供了文件系统操作与事务同步的机制：

```java
@Bean
public TransactionSynchronizationFactory transactionSynchronizationFactory() {
    ExpressionEvaluatingTransactionSynchronizationProcessor processor = new ExpressionEvaluatingTransactionSynchronizationProcessor();

    SpelExpressionParser spelParser = new SpelExpressionParser();
 
    processor.setAfterCommitExpression(spelParser.parseExpression("payload.renameTo(new java.io.File(payload.absolutePath + '.PASSED'))"));
 
    processor.setAfterRollbackExpression(spelParser.parseExpression("payload.renameTo(new java.io.File(payload.absolutePath + '.FAILED'))"));

    return new DefaultTransactionSynchronizationFactory(processor);
}
```

如果事务提交，TransactionSynchronizationFactory将通过将“.PASSED”附加到文件名来重命名文件。但是，如果它回滚，它将附加“.FAILED”。

InputChannel使用FileToStringTransformer转换有效负载并将其委托给toServiceChannel。此通道绑定到ServiceActivator：

```java
@Bean
public MessageChannel inputChannel() {
    return new DirectChannel();
}
    
@Bean
@Transformer(inputChannel = "inputChannel", outputChannel = "toServiceChannel")
public FileToStringTransformer fileToStringTransformer() {
    return new FileToStringTransformer();
}
```

ServiceActivator读取传入的文件，其中包含学生的考试成绩。它将结果写入数据库。如果结果包含字符串“fail”，它会抛出Exception，这会导致数据库回滚：

```java
@ServiceActivator(inputChannel = "toServiceChannel")
public void serviceActivator(String payload) {
    jdbcTemplate.update("insert into STUDENT values(?)", payload);

    if (payload.toLowerCase().startsWith("fail")) {
        log.error("Service failure. Test result: {} ", payload);
        throw new RuntimeException("Service failure.");
    }

    log.info("Service success. Test result: {}", payload);
}
```

在数据库操作成功提交或回滚后，TransactionSynchronizationFactory将文件系统操作与其结果同步。

## 6. 总结

在本文中，我们解释了Spring Integration框架中的事务支持。此外，我们还演示了如何将事务与对非事务性资源(如文件系统)的操作同步。