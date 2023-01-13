## 1. 概述

在本教程中，我们将探索使用映射诊断上下文(MDC) 来改进应用程序日志记录。

Mapped Diagnostic Context提供了一种方法来丰富日志消息的信息，这些信息在实际发生日志记录的范围内可能不可用，但对于更好地跟踪程序的执行确实很有用。

## 2. 为什么使用MDC

假设我们必须编写转账软件。

我们设置了一个Transfer类来表示一些基本信息——一个唯一的传输 ID 和发件人的姓名：

```java
public class Transfer {
    private String transactionId;
    private String sender;
    private Long amount;
    
    public Transfer(String transactionId, String sender, long amount) {
        this.transactionId = transactionId;
        this.sender = sender;
        this.amount = amount;
    }
    
    public String getSender() {
        return sender;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Long getAmount() {
        return amount;
    }
}

```

要执行传输，我们需要使用由简单 API 支持的服务：

```java
public abstract class TransferService {

    public boolean transfer(long amount) {
        // connects to the remote service to actually transfer money
    }

    abstract protected void beforeTransfer(long amount);

    abstract protected void afterTransfer(long amount, boolean outcome);
}

```

可以覆盖 beforeTransfer() 和 afterTransfer() 方法以在传输完成之前和之后立即运行自定义代码。

我们将利用beforeTransfer()和afterTransfer()来记录有关传输的一些信息。

让我们创建服务实现：

```java
import org.apache.log4j.Logger;
import com.baeldung.mdc.TransferService;

public class Log4JTransferService extends TransferService {
    private Logger logger = Logger.getLogger(Log4JTransferService.class);

    @Override
    protected void beforeTransfer(long amount) {
        logger.info("Preparing to transfer " + amount + "$.");
    }

    @Override
    protected void afterTransfer(long amount, boolean outcome) {
        logger.info(
          "Has transfer of " + amount + "$ completed successfully ? " + outcome + ".");
    }
}

```

这里要注意的主要问题是，创建日志消息时，无法访问Transfer对象——只能访问金额，因此无法记录交易 ID 或发送者。

让我们设置通常的log4j.properties文件来登录控制台：

```plaintext
log4j.appender.consoleAppender=org.apache.log4j.ConsoleAppender
log4j.appender.consoleAppender.layout=org.apache.log4j.PatternLayout
log4j.appender.consoleAppender.layout.ConversionPattern=%-4r [%t] %5p %c %x - %m%n
log4j.rootLogger = TRACE, consoleAppender

```

最后，我们将设置一个能够通过ExecutorService同时运行多个传输的小型应用程序：

```java
public class TransferDemo {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        TransactionFactory transactionFactory = new TransactionFactory();
        for (int i = 0; i < 10; i++) {
            Transfer tx = transactionFactory.newInstance();
            Runnable task = new Log4JRunnable(tx);            
            executor.submit(task);
        }
        executor.shutdown();
    }
}
```

请注意，为了使用ExecutorService，我们需要将Log4JTransferService的执行包装在一个适配器中，因为executor.submit()需要一个Runnable：

```java
public class Log4JRunnable implements Runnable {
    private Transfer tx;
    
    public Log4JRunnable(Transfer tx) {
        this.tx = tx;
    }
    
    public void run() {
        log4jBusinessService.transfer(tx.getAmount());
    }
}

```

当我们运行同时管理多个传输的演示应用程序时，我们很快就会发现日志并没有我们希望的那么有用。

跟踪每次转账的执行情况很复杂，因为记录的唯一有用信息是转账金额和运行该特定转账的线程的名称。

更重要的是，不可能区分同一线程运行的相同数量的两个不同事务，因为相关的日志行看起来基本相同：

```plaintext
...
519  [pool-1-thread-3]  INFO Log4JBusinessService 
  - Preparing to transfer 1393$.
911  [pool-1-thread-2]  INFO Log4JBusinessService 
  - Has transfer of 1065$ completed successfully ? true.
911  [pool-1-thread-2]  INFO Log4JBusinessService 
  - Preparing to transfer 1189$.
989  [pool-1-thread-1]  INFO Log4JBusinessService 
  - Has transfer of 1350$ completed successfully ? true.
989  [pool-1-thread-1]  INFO Log4JBusinessService 
  - Preparing to transfer 1178$.
1245 [pool-1-thread-3]  INFO Log4JBusinessService 
  - Has transfer of 1393$ completed successfully ? true.
1246 [pool-1-thread-3]  INFO Log4JBusinessService 
  - Preparing to transfer 1133$.
1507 [pool-1-thread-2]  INFO Log4JBusinessService 
  - Has transfer of 1189$ completed successfully ? true.
1508 [pool-1-thread-2]  INFO Log4JBusinessService 
  - Preparing to transfer 1907$.
1639 [pool-1-thread-1]  INFO Log4JBusinessService 
  - Has transfer of 1178$ completed successfully ? true.
1640 [pool-1-thread-1]  INFO Log4JBusinessService 
  - Preparing to transfer 674$.
...

```

幸运的是，MDC可以提供帮助。

## 3.Log4j中的MDC

Log4j 中的MDC允许我们用信息片段填充类似地图的结构，这些信息片段在实际写入日志消息时可供附加程序访问。

MDC 结构以与ThreadLocal变量相同的方式在内部附加到执行线程。

这是高层次的想法：

1.  用我们希望提供给 appender 的信息片段填充 MDC
2.  然后记录一条消息
3.  最后清除 MDC

appender 的模式应该改变以检索存储在 MDC 中的变量。

因此，让我们根据这些准则更改代码：

```java
import org.apache.log4j.MDC;

public class Log4JRunnable implements Runnable {
    private Transfer tx;
    private static Log4JTransferService log4jBusinessService = new Log4JTransferService();

    public Log4JRunnable(Transfer tx) {
        this.tx = tx;
    }

    public void run() {
        MDC.put("transaction.id", tx.getTransactionId());
        MDC.put("transaction.owner", tx.getSender());
        log4jBusinessService.transfer(tx.getAmount());
        MDC.clear();
    }
}

```

MDC.put()用于在MDC中添加一个key和对应的value，而MDC.clear()清空MDC。

现在让我们更改log4j.properties以打印我们刚刚存储在 MDC 中的信息。

更改转换模式就足够了，对我们要记录的 MDC 中包含的每个条目使用%X{}占位符：

```plaintext
log4j.appender.consoleAppender.layout.ConversionPattern=
  %-4r [%t] %5p %c{1} %x - %m - tx.id=%X{transaction.id} tx.owner=%X{transaction.owner}%n
```

现在，如果我们运行该应用程序，我们会注意到每一行还包含有关正在处理的事务的信息，这使我们更容易跟踪应用程序的执行情况：

```plaintext
638  [pool-1-thread-2]  INFO Log4JBusinessService 
  - Has transfer of 1104$ completed successfully ? true. - tx.id=2 tx.owner=Marc
638  [pool-1-thread-2]  INFO Log4JBusinessService 
  - Preparing to transfer 1685$. - tx.id=4 tx.owner=John
666  [pool-1-thread-1]  INFO Log4JBusinessService 
  - Has transfer of 1985$ completed successfully ? true. - tx.id=1 tx.owner=Marc
666  [pool-1-thread-1]  INFO Log4JBusinessService 
  - Preparing to transfer 958$. - tx.id=5 tx.owner=Susan
739  [pool-1-thread-3]  INFO Log4JBusinessService 
  - Has transfer of 783$ completed successfully ? true. - tx.id=3 tx.owner=Samantha
739  [pool-1-thread-3]  INFO Log4JBusinessService 
  - Preparing to transfer 1024$. - tx.id=6 tx.owner=John
1259 [pool-1-thread-2]  INFO Log4JBusinessService 
  - Has transfer of 1685$ completed successfully ? false. - tx.id=4 tx.owner=John
1260 [pool-1-thread-2]  INFO Log4JBusinessService 
  - Preparing to transfer 1667$. - tx.id=7 tx.owner=Marc

```

## 4. Log4j2中的MDC

Log4j2 中也提供了完全相同的功能，所以让我们看看如何使用它。

我们将首先设置一个使用 Log4j2 进行日志记录的TransferService子类：

```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4J2TransferService extends TransferService {
    private static final Logger logger = LogManager.getLogger();

    @Override
    protected void beforeTransfer(long amount) {
        logger.info("Preparing to transfer {}$.", amount);
    }

    @Override
    protected void afterTransfer(long amount, boolean outcome) {
        logger.info("Has transfer of {}$ completed successfully ? {}.", amount, outcome);
    }
}

```

我们再改一下使用MDC的代码，在Log4j2中其实叫ThreadContext ：

```java
import org.apache.log4j.MDC;

public class Log4J2Runnable implements Runnable {
    private final Transaction tx;
    private Log4J2BusinessService log4j2BusinessService = new Log4J2BusinessService();

    public Log4J2Runnable(Transaction tx) {
        this.tx = tx;
    }

    public void run() {
        ThreadContext.put("transaction.id", tx.getTransactionId());
        ThreadContext.put("transaction.owner", tx.getOwner());
        log4j2BusinessService.transfer(tx.getAmount());
        ThreadContext.clearAll();
    }
}

```

同样，ThreadContext.put()在 MDC 中添加一个条目，而ThreadContext.clearAll()删除所有现有条目。

我们仍然缺少用于配置日志记录的log4j2.xml文件。

我们可以注意到，指定应记录哪些 MDC 条目的语法与 Log4j 中使用的语法相同：

```xml
<Configuration status="INFO">
    <Appenders>
        <Console name="stdout" target="SYSTEM_OUT">
            <PatternLayout
              pattern="%-4r [%t] %5p %c{1} - %m - tx.id=%X{transaction.id} tx.owner=%X{transaction.owner}%n" />
        </Console>
    </Appenders>
    <Loggers>
        <Logger name="com.baeldung.log4j2" level="TRACE" />
        <AsyncRoot level="DEBUG">
            <AppenderRef ref="stdout" />
        </AsyncRoot>
    </Loggers>
</Configuration>

```

再次，让我们运行应用程序，我们将在日志中看到打印的 MDC 信息：

```plaintext
1119 [pool-1-thread-3]  INFO Log4J2BusinessService 
  - Has transfer of 1198$ completed successfully ? true. - tx.id=3 tx.owner=Samantha
1120 [pool-1-thread-3]  INFO Log4J2BusinessService 
  - Preparing to transfer 1723$. - tx.id=5 tx.owner=Samantha
1170 [pool-1-thread-2]  INFO Log4J2BusinessService 
  - Has transfer of 701$ completed successfully ? true. - tx.id=2 tx.owner=Susan
1171 [pool-1-thread-2]  INFO Log4J2BusinessService 
  - Preparing to transfer 1108$. - tx.id=6 tx.owner=Susan
1794 [pool-1-thread-1]  INFO Log4J2BusinessService 
  - Has transfer of 645$ completed successfully ? true. - tx.id=4 tx.owner=Susan

```

## 5. SLF4J/Logback 中的 MDC

在底层日志库支持的情况下，SLF4J 中也提供了MDC 。

正如我们刚刚看到的，Logback 和 Log4j 都支持 MDC，因此我们不需要任何特殊的东西就可以通过标准设置使用它。

让我们准备通常的TransferService子类，这次使用 Simple Logging Facade for Java：

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Slf4TransferService extends TransferService {
    private static final Logger logger = LoggerFactory.getLogger(Slf4TransferService.class);

    @Override
    protected void beforeTransfer(long amount) {
        logger.info("Preparing to transfer {}$.", amount);
    }

    @Override
    protected void afterTransfer(long amount, boolean outcome) {
        logger.info("Has transfer of {}$ completed successfully ? {}.", amount, outcome);
    }
}

```

现在让我们使用 SLF4J 的 MDC 风格。

在这种情况下，语法和语义与 log4j 中的相同：

```java
import org.slf4j.MDC;

public class Slf4jRunnable implements Runnable {
    private final Transaction tx;
    
    public Slf4jRunnable(Transaction tx) {
        this.tx = tx;
    }
    
    public void run() {
        MDC.put("transaction.id", tx.getTransactionId());
        MDC.put("transaction.owner", tx.getOwner());
        new Slf4TransferService().transfer(tx.getAmount());
        MDC.clear();
    }
}

```

我们必须提供 Logback 配置文件logback.xml：

```xml
<configuration>
    <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%-4r [%t] %5p %c{1} - %m - tx.id=%X{transaction.id} tx.owner=%X{transaction.owner}%n</pattern>
	</encoder>
    </appender>
    <root level="TRACE">
        <appender-ref ref="stdout" />
    </root>
</configuration>

```

同样，我们将看到 MDC 中的信息已正确添加到记录的消息中，即使此信息未在log.info()方法中明确提供：

```plaintext
1020 [pool-1-thread-3]  INFO c.b.m.s.Slf4jBusinessService 
  - Has transfer of 1869$ completed successfully ? true. - tx.id=3 tx.owner=John
1021 [pool-1-thread-3]  INFO c.b.m.s.Slf4jBusinessService 
  - Preparing to transfer 1303$. - tx.id=6 tx.owner=Samantha
1221 [pool-1-thread-1]  INFO c.b.m.s.Slf4jBusinessService 
  - Has transfer of 1498$ completed successfully ? true. - tx.id=4 tx.owner=Marc
1221 [pool-1-thread-1]  INFO c.b.m.s.Slf4jBusinessService 
  - Preparing to transfer 1528$. - tx.id=7 tx.owner=Samantha
1492 [pool-1-thread-2]  INFO c.b.m.s.Slf4jBusinessService 
  - Has transfer of 1110$ completed successfully ? true. - tx.id=5 tx.owner=Samantha
1493 [pool-1-thread-2]  INFO c.b.m.s.Slf4jBusinessService 
  - Preparing to transfer 644$. - tx.id=8 tx.owner=John
```

值得注意的是，如果我们将 SLF4J 后端设置为不支持 MDC 的日志系统，所有相关调用将被简单地跳过而没有副作用。

## 6. MDC 和线程池

MDC 实现通常使用ThreadLocal来存储上下文信息。这是实现线程安全的一种简单而合理的方法。

但是，我们应该小心地将 MDC 与线程池一起使用。

让我们看看 基于ThreadLocal的 MDC 和线程池的组合有何危险：

1.  我们从线程池中获取一个线程。
2.  然后我们使用MDC.put() 或 ThreadContext.put()将一些上下文信息存储在 MDC 中 。
3.  我们在一些日志中使用了这些信息，但不知何故我们忘记了清除 MDC 上下文。
4.  借用的线程返回线程池。
5.  一段时间后，应用程序从池中获取相同的线程。
6.  由于我们上次没有清理 MDC，这个线程仍然拥有上次执行的一些数据。

这可能会导致执行之间出现一些意外的不一致。

防止这种情况的一种方法是始终记住在每次执行结束时清理 MDC 上下文。 这种方法通常需要严格的人工监督，因此容易出错。

另一种方法是使用 ThreadPoolExecutor 挂钩并在每次执行后执行必要的清理。 

为此，我们可以扩展ThreadPoolExecutor 类并覆盖 afterExecute() 钩子：

```java
public class MdcAwareThreadPoolExecutor extends ThreadPoolExecutor {

    public MdcAwareThreadPoolExecutor(int corePoolSize, 
      int maximumPoolSize, 
      long keepAliveTime, 
      TimeUnit unit, 
      BlockingQueue<Runnable> workQueue, 
      ThreadFactory threadFactory, 
      RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        System.out.println("Cleaning the MDC context");
        MDC.clear();
        org.apache.log4j.MDC.clear();
        ThreadContext.clearAll();
    }
}
```

这样，MDC 清理将在每次正常或异常执行后自动发生。

因此，无需手动执行：

```java
@Override
public void run() {
    MDC.put("transaction.id", tx.getTransactionId());
    MDC.put("transaction.owner", tx.getSender());

    new Slf4TransferService().transfer(tx.getAmount());
}
```

现在我们可以用新的执行器实现重新编写相同的演示：

```java
ExecutorService executor = new MdcAwareThreadPoolExecutor(3, 3, 0, MINUTES, 
  new LinkedBlockingQueue<>(), Thread::new, new AbortPolicy());
        
TransactionFactory transactionFactory = new TransactionFactory();

for (int i = 0; i < 10; i++) {
    Transfer tx = transactionFactory.newInstance();
    Runnable task = new Slf4jRunnable(tx);

    executor.submit(task);
}

executor.shutdown();
```

## 七. 总结

MDC 有很多应用，主要是在运行多个不同线程导致交错的日志消息的场景中，否则这些消息将难以阅读。

正如我们所见，Java 中使用最广泛的三个日志记录框架都支持它。