## 1. 概述

Apache Camel的核心是一个集成引擎，简单地说，它可用于促进各种技术之间的交互。

这些服务和技术之间的桥梁称为路由。路由在引擎(CamelContext)上实现，它们通过所谓的“交换消息”进行通信。

## 2. Maven依赖

首先，我们需要包含Spring Boot、Camel、Rest API与Swagger和JSON的依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.camel.springboot</groupId>
        <artifactId>camel-servlet-starter</artifactId>
        <version>3.15.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.camel.springboot</groupId>
        <artifactId>camel-jackson-starter</artifactId>
        <version>3.15.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.camel.springboot</groupId>
        <artifactId>camel-swagger-java-starter</artifactId>
        <version>3.15.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.camel.springboot</groupId>
        <artifactId>camel-spring-boot-starter</artifactId>
        <version>3.15.0</version>
    </dependency>    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

可以在[此处](https://central.sonatype.com/artifact/org.apache.camel.springboot/camel-spring-boot-starter/4.0.0-M2)找到最新版本的Apache Camel依赖项。

## 3. 主类

让我们首先创建一个Spring Boot应用程序：

```java
@SpringBootApplication
@ComponentScan(basePackages="cn.tuyucheng.taketoday.camel")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 4. Spring Boot的Camel配置

现在让我们使用Spring配置我们的应用程序，从配置文件(属性)开始。

例如，让我们在src/main/resources中的application.properties文件中为我们的应用程序配置一个日志：

```properties
logging.config=classpath:logback.xml
camel.springboot.name=MyCamel
server.address=0.0.0.0
management.address=0.0.0.0
management.port=8081
endpoints.enabled=true
endpoints.health.enabled=true
```

此示例显示了一个application.properties文件，该文件还设置了Logback配置的路径。通过将IP设置为“0.0.0.0”，我们完全限制了Spring Boot提供的Web服务器上的admin和management访问。此外，我们还启用了对我们的应用程序端点以及健康检查端点的所需网络访问。

另一个配置文件是application.yml。在其中，我们将添加一些属性来帮助我们将值注入到我们的应用程序路由中：

```yaml
server:
    port: 8080
camel:
    springboot:
        name: ServicesRest
management:
    port: 8081
endpoints:
    enabled: false
    health:
        enabled: true
quickstart:
    generateOrderPeriod: 10s
    processOrderPeriod: 30s
```

## 5. 设置Camel Servlet

开始使用Camel的一种方法是将其注册为Servlet，这样它就可以拦截HTTP请求并将它们重定向到我们的应用程序。

如前所述，从Camel的2.18版本及以下版本开始，我们可以利用我们的application.yml-通过为我们的最终URL创建一个参数。稍后它将被注入到我们的Java代码中：

```yaml
tuyucheng:
    api:
        path: '/camel'
```

回到我们的Application类，我们需要在上下文路径的根部注册Camel Servlet，当应用程序启动时，它将从application.yml中的引用tuyucheng.api.path注入：

```java
@Value("${tuyucheng.api.path}")
String contextPath;

@Bean
ServletRegistrationBean servletRegistrationBean() {
    ServletRegistrationBean servlet = new ServletRegistrationBean(new CamelHttpTransportServlet(), contextPath+"/*");
    servlet.setName("CamelServlet");
    return servlet;
}
```

从Camel的2.19版本开始，此配置已被删除，因为CamelServlet默认设置为“/camel”。

## 6. 构建路由

让我们通过从Camel扩展RouteBuilder类开始创建路由，并将其设置为@Component以便组件扫描例程可以在Web服务器初始化期间找到它：

```java
@Component
class RestApi extends RouteBuilder {
    @Override
    public void configure() {
        CamelContext context = new DefaultCamelContext();

        restConfiguration()...
        rest("/api/")...
        from("direct:remoteService")...
    }
}
```

在这个类中，我们覆盖了Camel的RouteBuilder类中的configure()方法。

**Camel总是需要一个CamelContext实例**-保存传入和传出消息的核心组件。

在这个简单的例子中，DefaultCamelContext就足够了，因为它只是将消息绑定到其中，就像我们要创建的REST服务一样。

### 6.1 restConfiguration()路由

接下来，我们为计划在restConfiguration()方法中创建的端点创建一个REST声明：

```java
restConfiguration()
    .contextPath(contextPath) 
    .port(serverPort)
    .enableCORS(true)
    .apiContextPath("/api-doc")
    .apiProperty("api.title", "Test REST API")
    .apiProperty("api.version", "v1")
    .apiContextRouteId("doc-api")
    .component("servlet")
    .bindingMode(RestBindingMode.json)
```

在这里，我们使用YAML文件中的注入属性注册上下文路径。同样的逻辑也应用于我们应用程序的端口。CORS已启用，允许跨站点使用此Web服务。绑定模式允许并将参数转换为我们的API。

接下来，我们将Swagger文档添加到我们之前设置的URI、标题和版本中。当我们为REST Web服务创建方法/端点时，Swagger文档将自动更新。

这个Swagger上下文本身就是一个Camel路由，在启动过程中我们可以在服务器日志中看到一些关于它的技术信息。默认情况下，我们的示例文档在[http://localhost:8080/camel/api-doc](http://localhost:8080/camel/api-doc)提供。

### 6.2 rest()路由

现在，让我们从上面列出的configure()方法实现rest()方法调用：

```java
rest("/api/")
    .id("api-route")
    .consumes("application/json")
    .post("/bean")
    .bindingMode(RestBindingMode.json_xml)
    .type(MyBean.class)
    .to("direct:remoteService");
```

对于熟悉API的人来说，此方法非常简单。id是CamelContext内部路由的标识。下一行定义媒体类型。这里定义了绑定模式，以表明我们可以在restConfiguration()上设置模式。

post()方法向API添加一个操作，生成一个“POST /bean”端点，而MyBean(具有Integer id和String name的常规Java bean)定义预期参数。

同样，GET、PUT和DELETE等HTTP操作也可以以get()、put()、delete()的形式提供。

最后，to()方法创建到另一个路由的桥梁。在这里，它告诉Camel在其上下文/引擎中搜索我们将要创建的另一个路由-该路由由值/id“direct:...”命名和检测，与from()方法中定义的路由匹配。

### 6.3 from()路由与transform()

使用Camel时，路由接收参数，然后转换和处理这些参数。之后，它将这些参数发送到另一个路由，该路由将结果转发到所需的输出(文件、数据库、SMTP服务器或REST API响应)。

在本文中，我们只在要覆盖的configure()方法中创建另一个路由。这将是我们最后一个to()路由的目的地路由：

```java
from("direct:remoteService")
    .routeId("direct-route")
    .tracing()
    .log(">>> ${body.id}")
    .log(">>> ${body.name}")
    .transform().simple("Hello ${in.body.name}")
    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
```

from()方法遵循与rest()方法相同的原则，并且具有许多相同的方法，除了它从Camel上下文消息中消费。这就是参数“direct-route”的原因，它创建了到上述方法rest().to()的链接。

**还有许多其他转换可用**，包括提取为Java原始类型(或对象)并将其向下发送到持久层。请注意，路由始终从传入消息中读取，因此链式路由将忽略传出消息。

该例子已经准备好了，我们可以尝试一下：

-   运行提示命令：mvn spring-boot:run
-   使用标头参数Content-Type:application/json和有效负载{"id":1,"name":"World"}对[http://localhost:8080/camel/api/bean](http://localhost:8080/camel/api/bean)执行POST请求：
-   我们应该收到201的返回码和响应：Hello, World

### 6.4 简单的脚本语言

该示例使用tracing()方法输出日志记录。请注意，我们使用了${}占位符；这些是属于Camel的称为SIMPLE的脚本语言的一部分。它应用于通过路由交换的消息，例如消息中的正文。

在我们的示例中，我们使用SIMPLE将Camel消息正文中的bean属性输出到日志中。

我们也可以使用它来进行简单的转换，如transform()方法所示。

### 6.5 from()路由与process()

让我们做一些更有意义的事情，比如调用服务层返回处理后的数据。SIMPLE不适用于繁重的数据处理，所以让我们用process()方法替换transform()：

```java
from("direct:remoteService")
    .routeId("direct-route")
    .tracing()
    .log(">>> ${body.id}")
    .log(">>> ${body.name}")
    .process(new Processor() {
        @Override
        public void process(Exchange exchange) throws Exception {
            MyBean bodyIn = (MyBean) exchange.getIn().getBody();
            ExampleServices.example(bodyIn);
            exchange.getIn().setBody(bodyIn);
        }
    })
    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
```

这允许我们将数据提取到一个bean中，与之前在type()方法上定义的那个相同，并在我们的ExampleServices层中处理它。

由于我们之前将bindingMode()设置为JSON，因此响应已经是基于我们的POJO生成的正确JSON格式。这意味着对于ExampleServices类：

```java
public class ExampleServices {
    public static void example(MyBean bodyIn) {
        bodyIn.setName( "Hello, " + bodyIn.getName() );
        bodyIn.setId(bodyIn.getId() * 10);
    }
}
```

相同的HTTP请求现在返回响应代码201和正文：{"id":10,"name":"Hello, World"}。

## 7. 总结

通过几行代码，我们成功创建了一个相对完整的应用程序。所有依赖项都通过单个命令自动构建、管理和运行。此外，我们可以创建将各种技术联系在一起的API。

这种方法对容器也非常友好，从而产生了一个非常精简的服务器环境，可以很容易地按需复制。额外的配置可能性可以很容易地合并到容器模板配置文件中。

最后，除了filter()、process()、transform()和marshall() API之外，Camel中还有许多其他集成模式和数据操作：

-   [Camel集成模式](http://camel.apache.org/enterprise-integration-patterns.html)
-   [Camel用户指南](http://camel.apache.org/user-guide.html)
-   [Camel SIMPLE语言](http://camel.apache.org/simple.html)