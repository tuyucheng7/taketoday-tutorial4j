## 1. 概述

[Apache Camel](https://www.baeldung.com/apache-camel-spring-boot)是一个强大的开源集成框架，实现了几个已知的[企业集成模式](https://www.baeldung.com/camel-integration-patterns)。

通常在使用Camel处理消息路由时，我们需要一种有效处理错误的方法。为此，Camel提供了一些处理异常的策略。

在本教程中，**我们将介绍可用于Camel应用程序内部异常处理的两种方法**。

## 2. 依赖项

我们需要做的是将[camel-spring-boot-starter](https://central.sonatype.com/artifact/org.apache.camel.springboot/camel-spring-boot-starter/4.0.0-M2)添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.camel.springboot</groupId>
    <artifactId>camel-spring-boot-starter</artifactId>
    <version>3.19.0</version>
</dependency>
```

## 3. 创建路由

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

快速回顾一下，Apache Camel中的[路由](https://camel.apache.org/manual/routes.html)是一个基本构建块，通常由一系列步骤组成，由Camel按顺序执行，用于消费和处理消息。

正如我们在简单示例中所见，我们将路由配置为使用来自名为start的[直接端点](https://camel.apache.org/components/next/direct-component.html)的消息。

然后，**我们从一个new的Processor中抛出一个IllegalArgumentException，我们使用Java DSL在路由中内联创建该处理器**。

目前，我们的路由不包含任何类型的异常处理，因此当我们运行它时，我们会在应用程序的输出中看到一些丑陋的东西：

```plaintext
...
10:21:57.087 [main] ERROR c.t.t.c.e.ExceptionThrowingRoute - Exception Thrown
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

现在让我们继续为我们的路由添加一些异常处理。在本节中，**我们将看看Camel的doTry()块，我们可以将其视为Java中[try catch finally](https://www.baeldung.com/java-exceptions)的等价物，但直接嵌入到DSL中**。

但首先，为了帮助简化我们的代码，我们将定义一个抛出IllegalArgumentException的专用处理器类-这将使我们的代码更具可读性，并且我们可以在以后的其他路由中重用我们的处理器：

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

有了我们的新处理器，让我们在第一个异常处理路由中使用它：

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

正如我们所看到的，我们路由中的代码是不言自明的。我们基本上是使用Camel等价物来模仿常规的Java try catch finally语句。

然而，让我们来看看路由的关键部分：

-   首先，我们包围了我们希望使用doTry()方法立即捕获抛出异常的路由部分
-   接下来，我们使用doCatch方法关闭这个块。**请注意，我们可以传递我们希望捕获的不同异常类型的列表**
-   最后，我们调用doFinally()，它定义了始终在doTry()和任何doCatch()块之后运行的代码

此外，我们应该注意在Java DSL中调用end()方法来表示块的结束是很重要的。

Camel还提供了另一个强大的功能，可以让我们在使用doCatch()块时使用[谓词](https://camel.apache.org/manual/predicate.html)：

```java
...
.doCatch(IOException.class, IllegalArgumentException.class).onWhen(exceptionMessage().contains("Hello"))
    .to("mock:catch")
...
```

这里我们添加一个运行时谓词来确定是否应该触发catch块。**在这种情况下，我们只想在引发的异常消息包含单词”Hello“时才触发它**。

## 5. 使用异常子句

不幸的是，先前方法的局限性之一是它仅适用于单一路由。

通常，随着应用程序的增长和添加的路由越来越多，我们可能不希望在逐个路由的基础上处理异常。这可能会导致重复代码，我们可能需要为应用程序提供通用的错误处理策略。

值得庆幸的是，Camel通过Java DSL提供了一个异常子句机制来指定我们在每个异常类型基础上或全局基础上需要的错误处理：

假设我们要为我们的应用程序实现异常处理策略。对于我们的简单示例，我们假设只有一个路由：

```java
@Component
public class ExceptionHandlingWithExceptionClauseRoute extends RouteBuilder {

    @Autowired
    private ExceptionLoggingProcessor exceptionLogger;

    @Override
    public void configure() throws Exception {
        onException(IllegalArgumentException.class).process(exceptionLogger)
                .handled(true)
                .to("mock:handled");

        from("direct:start-exception-clause")
                .routeId("exception-clause-route")
                .process(new IllegalArgumentExceptionThrowingProcessor())
                .to("mock:received");
    }
}
```

**如我们所见，我们使用onException方法来处理何时发生IllegalArgumentException并应用一些特定的处理**。

对于我们的示例，我们将处理传递给自定义的ExceptionLoggingProcessor类，该类只记录消息标头。最后，在将结果发送到名为handled的mock端点之前，我们使用handled(true)方法将消息交换标记为已处理。

但是，我们应该注意，在Camel中，我们代码的全局范围是每个RouteBuilder实例。因此，如果我们想通过多个RouteBuilder类共享此异常处理代码，我们可以使用以下技术。

**只需创建一个基本的抽象RouteBuilder类并将错误处理逻辑放在其configure方法中**。

随后，我们可以简单地扩展这个类并确保调用super.configure()方法。本质上，我们只是在使用Java继承技术。

## 6. 总结

在本文中，我们学习了如何处理路由中的异常。首先，我们创建了一个包含一些简单路由的Camel应用程序用于学习异常处理。

然后我们了解了两种使用doTry()和doCatch()块语法以及后来的onException()子句的具体方法。