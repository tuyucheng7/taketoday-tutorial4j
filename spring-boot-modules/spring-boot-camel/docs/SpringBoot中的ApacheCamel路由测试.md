## 1. 概述

[Apache Camel](https://www.baeldung.com/apache-camel-spring-boot)是一个强大的开源集成框架，实现了许多已知的[企业集成模式](https://www.baeldung.com/camel-integration-patterns)。

在本教程中，**我们将学习如何为我们的Camel路由编写可靠、独立的单元测试**。

首先，我们将首先使用[Spring Boot](https://www.baeldung.com/category/spring/spring-boot/)创建一个基本的Camel应用程序。然后我们将看看如何使用Camel的Spring测试支持API和[JUnit 5](https://www.baeldung.com/junit-5)来测试我们的应用程序。

## 2. 依赖项

假设我们已经[设置好项目并配置](https://www.baeldung.com/apache-camel-spring-boot#maven-dependencies)为使用Spring Boot和Camel。

然后，我们需要将[camel-test-spring-junit5](https://central.sonatype.com/artifact/org.apache.camel/camel-test-spring-junit5/4.0.0-M2)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-test-spring-junit5</artifactId>
    <version>3.15.0</version>
    <scope>test</scope>
</dependency>
```

顾名思义，此依赖是专门针对我们的单元测试的。

## 3. 定义一个简单的Camel Spring Boot应用程序

在本教程中，我们测试的重点将是一个简单的Apache Camel Spring Boot应用程序。

因此，让我们从定义应用程序入口点开始：

```java
@SpringBootApplication
public class GreetingsFileSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreetingsFileSpringApplication.class, args);
    }
}
```

正如我们所见，这是一个标准的Spring Boot应用程序。

### 3.1 创建路由

接下来，我们将定义一个相当基本的路由：

```java
@Component
public class GreetingsFileRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:start")
                .routeId("greetings-route")
                .setBody(constant("Hello Tuyucheng Readers!"))
                .to("file:output");
    }
}
```

快速回顾一下，Apache Camel中的路由是一个基本构建块，通常由一系列步骤组成，由Camel按顺序执行，用于消费和处理消息。

正如我们在简单示例中所见，我们将路由配置为使用来自名为start的[直接端点](https://camel.apache.org/components/next/direct-component.html)的消息。

然后，**我们将消息正文设置为包含字符串“Hello Tuyucheng Readers!”并使用[文件组件](https://camel.apache.org/components/next/file-component.html)将我们的消息交换内容写入名为output的文件目录**。

我们还为我们的路由提供了一个称为greetings-route的id。在我们的路由中使用id通常被认为是好的做法，并且可以在测试我们的路由时帮助我们。

### 3.2 运行应用程序

如果我们运行应用程序并向我们的直接消费者端点发送消息，我们应该在输出目录的文件中看到问候语文本。如果我们不指定文件名，Camel会为我们创建一个：

```shell
$ cat output/D97099B6B2958D2-0000000000000000 
Hello Tuyucheng Readers!
```

## 4. 关于测试的一句话

一般来说，在编写干净的测试时，我们不应该依赖我们可能无法控制或可能突然停止工作的外部服务或文件系统。这可能会对我们的测试结果产生不利影响。

我们也不想在路由中专门为我们的单元测试编写代码。**值得庆幸的是，Camel有一组专门用于测试的扩展和API**。我们可以将其视为一种测试套件。

该工具包通过向路由发送消息并检查是否按预期接收消息，使测试我们的Camel应用程序变得更加容易。

## 5. 使用@MockEndpoints进行测试

考虑到最后一节，让我们继续编写我们的第一个单元测试：

```java
@SpringBootTest
@CamelSpringBootTest
@MockEndpoints("file:output")
class GreetingsFileRouterUnitTest {

    @Autowired
    private ProducerTemplate template;

    @EndpointInject("mock:file:output")
    private MockEndpoint mock;

    @Test
    void whenSendBody_thenGreetingReceivedSuccessfully() throws InterruptedException {
        mock.expectedBodiesReceived("Hello Tuyucheng Readers!");
        template.sendBody("direct:start", null);
        mock.assertIsSatisfied();
    }
}
```

让我们来看看测试的关键部分。

首先，我们首先用三个注解装饰我们的测试类：

-   [@SpringBootTest](https://www.baeldung.com/spring-boot-testing)注解将确保我们的测试引导Spring应用程序上下文
-   我们还使用@CamelSpringBootRunner，它为我们基于Boot的测试带来了基于Spring的Camel测试支持
-   **最后，我们添加@MockEndpoints注解，它告诉Camel我们想要mock哪些端点**

接下来，我们自动装配一个ProducerTemplate对象，这是一个允许我们向端点发送消息交换的接口。

另一个关键组件是我们使用@EndpointInject注入的MockEndpoint。**使用这个注解告诉Camel路由何时开始，我们要注入我们的mock端点**。

现在我们已经完成了所有测试设置，我们可以编写测试了，它包括三个步骤：

-   首先，让我们设置一个期望，即我们的mock端点将接收给定的消息正文
-   然后我们将使用模板向我们的direct:start端点发送一条消息。请注意，我们将发送一个空正文，因为我们的路由不会处理传入的消息正文
-   为了结束我们的测试，**我们使用assertIsSatisfied方法来验证我们对mock端点的初始期望是否已得到满足**

这证实了我们的测试工作正常，我们现在有一种方法可以使用Camel测试支持实用程序来编写自包含的、独立的单元测试。

## 6. 总结

在本文中，我们学习了如何在Spring Boot中测试我们的Apache Camel路由。首先，我们学习了如何使用Spring Boot创建一个只有一个路由的简单Camel应用程序。

然后了解了使用Apache Camel内置测试支持项目中可用的一些功能来测试路由的推荐方法。