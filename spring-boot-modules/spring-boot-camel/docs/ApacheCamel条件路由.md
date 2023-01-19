## 一、概述

[Apache Camel](https://www.baeldung.com/apache-camel-spring-boot)是一个强大的开源集成框架，实现了几个已知的 [企业集成模式](https://www.baeldung.com/camel-integration-patterns)。

通常在使用 Camel 处理消息路由时，我们需要一种方法来根据消息的内容以不同的方式处理消息。为此，Camel 提供了一个强大的功能，称为[EIP 模式](https://camel.apache.org/components/3.18.x/eips/enterprise-integration-patterns.html)集合中的[基于内容的路由器](https://camel.apache.org/components/3.18.x/eips/choice-eip.html)。

在本教程中，我们将了解根据某些条件路由消息的几种方法。

## 2.依赖关系

我们需要开始的就是将[camel-spring-boot-starter](https://search.maven.org/search?q=a:camel-spring-boot-starter)添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.camel.springboot</groupId>
     <artifactId>camel-spring-boot-starter</artifactId>
     <version>3.18.1</version>
</dependency>
```

然后，我们需要将 [camel-test-spring-junit5](https://search.maven.org/search?q=g:org.apache.camel a:camel-spring-boot-starter) 依赖项添加到我们的 pom.xml中：

```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-test-spring-junit5</artifactId>
    <version>3.18.1</version>
</dependency>
```

顾名思义，这种依赖是专门针对我们的单元测试的。

## 3. 定义一个简单的 Camel Spring Boot 应用程序

在本教程中，我们示例的重点将是一个简单的 Apache Camel Spring Boot 应用程序。

因此，让我们从定义我们的应用程序入口点开始：

```java
@SpringBootApplication
public class ConditionalRoutingSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConditionalRoutingSpringApplication.class, args);
    }
}
```

正如我们所见，这是一个标准的 Spring Boot 应用程序。

## 4.条件路由

快速回顾一下，Apache Camel 中的[路由](https://camel.apache.org/manual/routes.html)是一个基本构建块，通常由一系列步骤组成，由 Camel 按顺序执行，用于消费和处理消息。

例如，一个路由通常会使用消费者从磁盘上的文件或消息队列中接收消息。然后，Camel 执行路由中的其余步骤，这些步骤要么以某种方式处理消息，要么将其发送到其他端点。

毫无疑问，我们需要一种方法来根据某些事实有条件地路由消息。为此，Camel 提供了选择和何时构建。我们可以认为这相当于 Java 中的[if-else语句](https://www.baeldung.com/java-if-else)。

考虑到这一点，让我们继续使用一些条件逻辑创建我们的第一条路线。

## 5.创建路线

在此示例中，我们将根据收到的消息正文的内容定义一个具有一些条件逻辑的基本路由：

```java
@Component
public class ConditionalBodyRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        from("direct:start-conditional")
          .routeId("conditional-body-route")
          .choice()
            .when(body().contains("Baeldung"))
              .setBody(simple("Goodbye, Baeldung!"))
              .to("mock:result-body")
            .otherwise()
              .to("mock:result-body")
          .end();
    }
}
```

正如我们在简单示例中所见，我们将路由配置为使用来自名为start-conditional的[直接端点](https://camel.apache.org/components/next/direct-component.html)的消息。

现在让我们来看看我们路线的关键部分：

-   首先，我们使用choice()方法开始路由——这告诉 Camel 以下几行将包含一些要评估的条件
-   接下来，when()方法指示要评估的新条件——在此示例中，我们只是检查消息正文是否包含字符串 Baeldung。我们可以根据需要添加任意数量的 when 条件
-   为了总结我们的路线，我们使用otherwise()方法来定义当前面的条件都不满足时要做什么。
-   最后，路由使用end()方法终止，该方法关闭了选择块。

总而言之，当我们运行路由时，如果我们的消息正文包含字符串Baeldung，我们会将消息正文设置为再见，Baeldung！并将结果发送到名为result-body的[模拟端点](https://camel.apache.org/components/3.18.x/mock-component.html)。

或者，我们将把原始消息路由到我们的模拟端点。

## 6. 测试路由

考虑到最后一节，让我们继续编写单元测试来探索我们的路由的行为方式：

```java
@SpringBootTest
@CamelSpringBootTest
class ConditionalBodyRouterUnitTest {

    @Autowired
    private ProducerTemplate template;

    @EndpointInject("mock:result-body")
    private MockEndpoint mock;

    @Test
    void whenSendBodyWithBaeldung_thenGoodbyeMessageReceivedSuccessfully() throws InterruptedException {
        mock.expectedBodiesReceived("Goodbye, Baeldung!");

        template.sendBody("direct:start-conditional", "Hello Baeldung Readers!");

        mock.assertIsSatisfied();
    }
}
```

正如我们所见，我们的测试包括三个简单的步骤：

-   首先，让我们设定一个期望，即我们的模拟端点将收到给定的消息正文
-   然后我们将 使用我们的模板向我们的direct:start-conditional端点发送一条消息。请注意，我们将确保我们的消息正文包含字符串Baeldung
-   为了结束我们的测试， 我们使用 assertIsSatisfied 方法来验证我们对模拟端点的初始期望是否已得到满足

此测试确认我们的条件路由正常工作。惊人的！

请务必查看我们之前的教程，以了解有关如何为我们的 Camel 路由编写可靠、[独立的单元测试的更多信息。](https://www.baeldung.com/spring-boot-apache-camel-routes-testing)

## 7. 构建其他条件谓词

到目前为止，我们已经探索了一种关于如何构建我们的 when[谓词](https://camel.apache.org/manual/predicate.html)的选项——检查我们交换的消息体。但是，我们还有其他几种选择。

例如，我们还可以通过检查给定消息头的值来控制我们的条件：

```java
@Component
public class ConditionalHeaderRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        from("direct:start-conditional-header")
            .routeId("conditional-header-route")
            .choice()
              .when(header("fruit").isEqualTo("Apple"))
                .setHeader("favourite", simple("Apples"))
                .to("mock:result")
              .otherwise()
                .setHeader("favourite", header("fruit"))
                .to("mock:result")
            .end();
    }
}
```

这一次，我们修改了when方法来查看名为 fruit 的标头的值。也完全可以在我们的 when 条件中使用 Camel 提供的[简单语言。](https://camel.apache.org/components/latest/languages/simple-language.html)

## 8. 使用 Java Bean 

此外，当我们想在我们的谓词中使用 Java 方法调用的结果时，我们也可以使用[Camel Bean 语言。](https://camel.apache.org/components/3.18.x/languages/bean-language.html)

首先，我们需要创建一个包含返回布尔值的方法的 Java bean：

```java
public class FruitBean {

    public static boolean isApple(Exchange exchange) {
        return "Apple".equals(exchange.getIn().getHeader("fruit"));
    }
}
```

在这里，我们还可以选择将Exchange添加为参数，以便 Camel 自动将Exchange传递给我们的方法。

然后我们可以继续使用我们的when块中的FruitBean：

```java
@Component
public class ConditionalBeanRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        from("direct:start-conditional-bean")
            .routeId("conditional-bean-route")
            .choice()
              .when(method(FruitBean.class, "isApple"))
                .setHeader("favourite", simple("Apples"))
                .to("mock:result")
              .otherwise()
                .setHeader("favourite", header("fruit"))
                 .to("mock:result")
              .endChoice()
            .end();
    }
}
```

## 9.总结

在本文中，我们了解了如何根据路由中的某种条件路由消息。首先，我们创建了一个简单的 Camel 应用程序，其中包含一个检查消息正文的路由。

然后我们学习了其他几种使用消息头和 Java bean 在我们的路由中构建谓词的技术。