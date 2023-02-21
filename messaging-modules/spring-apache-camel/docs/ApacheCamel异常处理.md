## 一、概述

[Apache Camel](https://www.baeldung.com/apache-camel-spring-boot)是一个强大的开源集成框架，实现了几个已知的[企业集成模式](https://www.baeldung.com/camel-integration-patterns)。

通常在使用 Camel 处理消息路由时，我们需要一种有效处理错误的方法。为此，Camel 提供了一些处理异常的策略。

在本教程中，我们将了解可用于 Camel 应用程序内部异常处理的两种方法。

## 2.依赖关系

我们需要开始的就是将 [camel-spring-boot-starter](https://search.maven.org/search?q=a:camel-spring-boot-starter) 添加到我们的 pom.xml中：

```xml
<dependency>
    <groupId>org.apache.camel.springboot</groupId>
    <artifactId>camel-spring-boot-starter</artifactId>
    <version>3.19.0</version>
</dependency>
```

## 3.创建路线

让我们从定义一个故意抛出异常的相当基本的路由开始：

```java
@Component
public class ExceptionThrowingRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionThrowingRoute.class);

    @Override
    public void configure() throws Exception {
        
        from("direct:start-exception")
          .routeId("exception-handling-route")
          .process(new Processor() {
              
              @Override
              public void process(Exchange exchange) throws Exception {
                  LOGGER.error("Exception Thrown");
                  throw new IllegalArgumentException("An exception happened on purpose");
                  
              }
          }).to("mock:received");
    }
}
```

快速回顾一下，Apache Camel 中的[路由](https://camel.apache.org/manual/routes.html)是一个基本构建块，通常由一系列步骤组成，由 Camel 按顺序执行，用于消费和处理消息。

正如我们在简单示例中所见，我们将路由配置为使用来自名为 start的[直接端点的消息。](https://camel.apache.org/components/next/direct-component.html)

然后，我们从一个新的处理器中抛出一个IllegalArgumentException ，我们使用 Java DSL 在我们的路由中内联创建它。

目前，我们的路由不包含任何类型的异常处理，因此当我们运行它时，我们会在应用程序的输出中看到一些丑陋的东西：

```plaintext
...
10:21:57.087 [main] ERROR c.b.c.e.ExceptionThrowingRoute - Exception Thrown
10:21:57.094 [main] ERROR o.a.c.p.e.DefaultErrorHandler - Failed delivery for (MessageId: 50979CFF47E7816-0000000000000000 on ExchangeId: 50979CFF47E7816-0000000000000000). 
Exhausted after delivery attempt: 1 caught: java.lang.IllegalArgumentException: An exception happened on purpose

Message History (source location and message history is disabled)
---------------------------------------------------------------------------------------------------------------------------------------
Source                                   ID                             Processor                                          Elapsed (ms)
                                         exception-handling-route/excep from[direct://start-exception]                               11
	...
                                         exception-handling-route/proce Processor@0x3e28af44                                          0

Stacktrace
---------------------------------------------------------------------------------------------------------------------------------------
java.lang.IllegalArgumentException: An exception happened on purpose
...

```

## 4. 使用doTry()块

现在让我们继续为我们的路由添加一些异常处理。在本节中，我们将看一下 Camel 的doTry()块，我们可以将其视为 Java 中[try catch finally](https://www.baeldung.com/java-exceptions)的等价物，但直接嵌入到 DSL 中。

但首先，为了帮助简化我们的代码，我们将定义一个抛出IllegalArgumentException 的专用处理器类——这将使我们的代码更具可读性，并且我们可以在以后的其他路由中重用我们的处理器：

```java
@Component
public class IllegalArgumentExceptionThrowingProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionLoggingProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        LOGGER.error("Exception Thrown");
        throw new IllegalArgumentException("An exception happened on purpose");
    }
}
```

有了我们的新处理器，让我们在我们的第一个异常处理路由中使用它：

```java
@Component
public class ExceptionHandlingWithDoTryRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        from("direct:start-handling-exception")
          .routeId("exception-handling-route")
          .doTry()
            .process(new IllegalArgumentExceptionThrowingProcessor())
            .to("mock:received")
          .doCatch(IOException.class, IllegalArgumentException.class)
            .to("mock:caught")
          .doFinally()
            .to("mock:finally")
          .end();
    }
}
```

正如我们所看到的，我们路由中的代码是不言自明的。我们基本上是使用 Camel 等价物来模仿常规的 Java try catch finally语句。

然而，让我们来看看我们路线的关键部分：

-   首先，我们包围了我们希望使用doTry()方法立即捕获抛出异常的路由部分
-   接下来，我们使用doCatch方法关闭这个块。请注意，我们可以传递我们希望捕获的不同异常类型的列表
-   最后，我们调用doFinally()，它定义了始终在doTry()和任何doCatch()块之后运行的代码

此外，我们应该注意调用end()方法来表示 Java DSL 中块的结束是很重要的。

Camel 还提供了另一个强大的功能，可以让我们在使用doCatch()块时使用[谓词：](https://camel.apache.org/manual/predicate.html)

```java
...
.doCatch(IOException.class, IllegalArgumentException.class).onWhen(exceptionMessage().contains("Hello"))
   .to("mock:catch")
...
```

这里我们添加一个运行时谓词来确定是否应该触发 catch 块。在这种情况下，我们只想在引发的异常消息包含单词 Hello 时触发它。很酷！

## 5. 使用例外条款

不幸的是，先前方法的局限性之一是它仅适用于单一路线。

通常随着我们的应用程序的增长和我们添加越来越多的路由，我们可能不希望在逐条路由的基础上处理异常。这可能会导致重复代码，我们可能需要为我们的应用程序采用通用的错误处理策略。

值得庆幸的是，Camel 通过 Java DSL 提供了一个 Exception Clause 机制来指定我们在每个异常类型基础上或全局基础上需要的错误处理：

假设我们要为我们的应用程序实施异常处理策略。对于我们的简单示例，我们假设只有一条路线：

```java
@Component
public class ExceptionHandlingWithExceptionClauseRoute extends RouteBuilder {
    
    @Autowired
    private ExceptionLoggingProcessor exceptionLogger;
    
    @Override
    public void configure() throws Exception {
        onException(IllegalArgumentException.class).process(exceptionLogger)
          .handled(true)
          .to("mock:handled")
        
        from("direct:start-exception-clause")
          .routeId("exception-clause-route")
          .process(new IllegalArgumentExceptionThrowingProcessor())
          .to("mock:received");
    }
}
```

如我们所见，我们正在使用onException方法来处理何时发生IllegalArgumentException并应用一些特定的处理。

对于我们的示例，我们将处理传递给自定义的ExceptionLoggingProcessor类，该类只记录消息标头。最后，在将结果发送到名为handled的模拟端点之前，我们使用handled(true)方法将消息交换标记为已处理。

但是，我们应该注意，在 Camel 中，我们代码的全局范围是每个RouteBuilder实例。因此，如果我们想通过多个RouteBuilder类共享此异常处理代码，我们可以使用以下技术。

只需创建一个基本的抽象RouteBuilder类并将错误处理逻辑放在其配置方法中。

随后，我们可以简单地扩展这个类并确保调用super.configure()方法。本质上，我们只是在使用 Java 继承技术。

## 六，总结

在本文中，我们学习了如何处理路由中的异常。首先，我们创建了一个简单的 Camel 应用程序，其中包含多个用于了解异常的路由。

然后我们了解了两种使用doTry()和doCatch()块语法以及后来的onException()子句的具体方法。