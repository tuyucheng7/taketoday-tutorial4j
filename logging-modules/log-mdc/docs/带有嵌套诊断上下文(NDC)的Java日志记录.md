## 1. 概述

嵌套诊断上下文 (NDC) 是一种有助于区分来自不同来源的交错日志消息的机制。NDC 通过提供向每个日志条目添加独特的上下文信息的能力来做到这一点。

在本文中，我们将探索 NDC 的使用及其在各种Java日志框架中的使用/支持。

## 2.诊断上下文

在典型的多线程应用程序(如 Web 应用程序或 REST API)中，每个客户端请求都由不同的线程处理。从此类应用程序生成的日志将混合所有客户端请求和来源。这使得很难对日志进行任何商业意义或调试。

嵌套诊断上下文 (NDC) 在每个线程的基础上管理一堆上下文信息。NDC 中的数据可用于代码中的每个日志请求，并且可以配置为与每个日志消息一起记录——即使在数据不在范围内的地方也是如此。每条日志消息中的上下文信息有助于通过日志的来源和上下文来区分日志。

[映射诊断上下文 (MDC)](https://www.baeldung.com/mdc-in-log4j-2-logback)也以每个线程为基础管理信息，但作为一个映射。

## 3. 示例应用程序中的 NDC 堆栈

为了演示 NDC 堆栈的用法，让我们以将钱发送到投资账户的 REST API 为例。

作为输入所需的信息在Investment类中表示：

```java
public class Investment {
    private String transactionId;
    private String owner;
    private Long amount;

    public Investment (String transactionId, String owner, Long amount) {
        this.transactionId = transactionId;
        this.owner = owner;
        this.amount = amount;
    }
    
    // standard getters and setters
}
```

向投资账户的转账是使用InvestmentService执行的。这些类的完整源代码可以在[这个 github 项目](https://github.com/eugenp/tutorials/tree/master/logging-modules/log-mdc)中找到。

在示例应用程序中，数据transactionId和所有者被放置在 NDC 堆栈中，在处理给定请求的线程中。该数据在该线程的每条日志消息中都可用。这样，可以跟踪每个唯一的事务，并且可以识别每个日志消息的相关上下文。

## 4. Log4j中的NDC

Log4j 提供了一个名为NDC的类，它提供了静态方法来管理 NDC 堆栈中的数据。基本用法：

-   进入上下文时，使用NDC.push()在当前线程中添加上下文数据
-   离开上下文时，使用NDC.pop()取出上下文数据
-   退出线程时，调用NDC.remove()以删除线程的诊断上下文并确保释放内存(从 Log4j 1.3 开始，不再需要)

在示例应用程序中，让我们使用 NDC 在代码的相关位置添加/删除上下文数据：

```java
import org.apache.log4j.NDC;

@RestController
public class Log4JController {
    @Autowired
    @Qualifier("Log4JInvestmentService")
    private InvestmentService log4jBusinessService;

    @RequestMapping(
      value = "/ndc/log4j", 
      method = RequestMethod.POST)
    public ResponseEntity<Investment> postPayment(
      @RequestBody Investment investment) {
        
        NDC.push("tx.id=" + investment.getTransactionId());
        NDC.push("tx.owner=" + investment.getOwner());

        log4jBusinessService.transfer(investment.getAmount());

        NDC.pop();
        NDC.pop();

        NDC.remove();

        return 
          new ResponseEntity<Investment>(investment, HttpStatus.OK);
    }
}
```

通过在log4j.properties中的 appender 使用的ConversionPattern中使用%x选项，可以在日志消息中显示 NDC 的内容：

```plaintext
log4j.appender.consoleAppender.layout.ConversionPattern 
  = %-4r [%t] %5p %c{1} - %m - [%x]%n
```

让我们将 REST API 部署到 tomcat。样品要求：

```plaintext
POST /logging-service/ndc/log4j
{
  "transactionId": "4",
  "owner": "Marc",
  "amount": 2000
}
```

我们可以在日志输出中看到诊断上下文信息：

```plaintext
48569 [http-nio-8080-exec-3]  INFO Log4JInvestmentService 
  - Preparing to transfer 2000$. 
  - [tx.id=4 tx.owner=Marc]
49231 [http-nio-8080-exec-4]  INFO Log4JInvestmentService 
  - Preparing to transfer 1500$. 
  - [tx.id=6 tx.owner=Samantha]
49334 [http-nio-8080-exec-3]  INFO Log4JInvestmentService 
  - Has transfer of 2000$ completed successfully ? true. 
  - [tx.id=4 tx.owner=Marc] 
50023 [http-nio-8080-exec-4]  INFO Log4JInvestmentService 
  - Has transfer of 1500$ completed successfully ? true. 
  - [tx.id=6 tx.owner=Samantha]
...
```

## 5. Log4j 2 中的 NDC

Log4j 2 中的 NDC 称为线程上下文堆栈：

```java
import org.apache.logging.log4j.ThreadContext;

@RestController
public class Log4J2Controller {
    @Autowired
    @Qualifier("Log4J2InvestmentService")
    private InvestmentService log4j2BusinessService;

    @RequestMapping(
      value = "/ndc/log4j2", 
      method = RequestMethod.POST)
    public ResponseEntity<Investment> postPayment(
      @RequestBody Investment investment) {
        
        ThreadContext.push("tx.id=" + investment.getTransactionId());
        ThreadContext.push("tx.owner=" + investment.getOwner());

        log4j2BusinessService.transfer(investment.getAmount());

        ThreadContext.pop();
        ThreadContext.pop();

        ThreadContext.clearAll();

        return 
          new ResponseEntity<Investment>(investment, HttpStatus.OK);
    }
}
```

与 Log4j 一样，让我们在 Log4j 2 配置文件log4j2.xml中使用%x选项：

```xml
<Configuration status="INFO">
    <Appenders>
        <Console name="stdout" target="SYSTEM_OUT">
            <PatternLayout
              pattern="%-4r [%t] %5p %c{1} - %m -%x%n" />
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

日志输出：

```plaintext
204724 [http-nio-8080-exec-1]  INFO Log4J2InvestmentService 
  - Preparing to transfer 1500$. 
  - [tx.id=6, tx.owner=Samantha]
205455 [http-nio-8080-exec-2]  INFO Log4J2InvestmentService 
  - Preparing to transfer 2000$. 
  - [tx.id=4, tx.owner=Marc]
205525 [http-nio-8080-exec-1]  INFO Log4J2InvestmentService 
  - Has transfer of 1500$ completed successfully ? false. 
  - [tx.id=6, tx.owner=Samantha]
206064 [http-nio-8080-exec-2]  INFO Log4J2InvestmentService 
  - Has transfer of 2000$ completed successfully ? true. 
  - [tx.id=4, tx.owner=Marc]
...
```

## 6. 日志门面中的 NDC(JBoss 日志)

像 SLF4J 这样的日志外观提供了与各种日志框架的集成。SLF4J 不支持 NDC(但包含在 slf4j-ext 模块中)。JBoss Logging 是一个日志桥，就像 SLF4J 一样。JBoss 日志记录支持 NDC。

默认情况下，JBoss Logging 将按照以下优先顺序在ClassLoader中搜索后端/提供程序的可用性：JBoss LogManager、Log4j 2、Log4j、SLF4J 和 JDK Logging。

JBoss LogManager 作为日志提供程序通常在 WildFly 应用程序服务器中使用。在我们的例子中，JBoss 日志桥将按优先顺序选择下一个(即 Log4j 2)作为日志提供程序。

让我们首先在pom.xml中添加所需的依赖项：

```xml
<dependency>
    <groupId>org.jboss.logging</groupId>
    <artifactId>jboss-logging</artifactId>
    <version>3.3.0.Final</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.jboss.logging" AND a%3A"jboss-logging")检查最新版本的依赖项。

让我们将上下文信息添加到 NDC 堆栈：

```java
import org.jboss.logging.NDC;

@RestController
public class JBossLoggingController {
    @Autowired
    @Qualifier("JBossLoggingInvestmentService")
    private InvestmentService jbossLoggingBusinessService;

    @RequestMapping(
      value = "/ndc/jboss-logging", 
      method = RequestMethod.POST)
    public ResponseEntity<Investment> postPayment(
      @RequestBody Investment investment) {
        
        NDC.push("tx.id=" + investment.getTransactionId());
        NDC.push("tx.owner=" + investment.getOwner());

        jbossLoggingBusinessService.transfer(investment.getAmount());

        NDC.pop();
        NDC.pop();

        NDC.clear();

        return 
          new ResponseEntity<Investment>(investment, HttpStatus.OK);
    }
}
```

日志输出：

```plaintext
17045 [http-nio-8080-exec-1]  INFO JBossLoggingInvestmentService 
  - Preparing to transfer 1,500$. 
  - [tx.id=6, tx.owner=Samantha]
17725 [http-nio-8080-exec-1]  INFO JBossLoggingInvestmentService 
  - Has transfer of 1,500$ completed successfully ? true. 
  - [tx.id=6, tx.owner=Samantha]
18257 [http-nio-8080-exec-2]  INFO JBossLoggingInvestmentService 
  - Preparing to transfer 2,000$. 
  - [tx.id=4, tx.owner=Marc]
18904 [http-nio-8080-exec-2]  INFO JBossLoggingInvestmentService 
  - Has transfer of 2,000$ completed successfully ? true. 
  - [tx.id=4, tx.owner=Marc]
...
```

## 七. 总结

我们已经看到诊断上下文如何以有意义的方式帮助关联日志——从业务角度以及调试目的。这是丰富日志记录的宝贵技术，尤其是在多线程应用程序中。