## 一、概述

Apache Camel 的核心是一个集成引擎，简单地说，它可用于促进各种技术之间的交互。

这些服务和技术之间的桥梁称为路由。路由在引擎(CamelContext)上实现，它们通过所谓的“交换消息”进行通信。

## 2.Maven依赖

首先，我们需要包含 Spring Boot、Camel、带有 Swagger 和 JSON 的 Rest API 的依赖项：

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

可以在[此处](https://search.maven.org/search?q=g:org.apache.camel.springboot)找到最新版本的 Apache Camel 依赖项。

## 3.主类

让我们首先创建一个 Spring Boot应用程序：

```java
@SpringBootApplication
@ComponentScan(basePackages="com.baeldung.camel")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 4. Spring Boot 的 Camel 配置

现在让我们使用 Spring 配置我们的应用程序，从配置文件(属性)开始。

例如，让我们在src/main/resources中的application.properties文件中为我们的应用程序配置一个日志：

```java
logging.config=classpath:logback.xml
camel.springboot.name=MyCamel
server.address=0.0.0.0
management.address=0.0.0.0
management.port=8081
endpoints.enabled = true
endpoints.health.enabled = true
```

此示例显示了一个application.properties文件，该文件还设置了 Logback 配置的路径。通过将 IP 设置为“0.0.0.0”，我们完全限制了 Spring Boot 提供的 Web 服务器上的管理员和管理访问。此外，我们还启用了对我们的应用程序端点以及健康检查端点的所需网络访问。

另一个配置文件是application.yml。在其中，我们将添加一些属性来帮助我们将值注入到我们的应用程序路由中：

```bash
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

## 5 . 设置骆驼 Servlet

开始使用 Camel 的一种方法是将其注册为 servlet，这样它就可以拦截 HTTP 请求并将它们重定向到我们的应用程序。

如前所述，从 Camel 的 2.18 版及以下版本开始，我们可以利用我们的application.yml - 通过为我们的最终 URL 创建一个参数。稍后它将被注入到我们的 Java 代码中：

```xml
baeldung:
  api:
    path: '/camel'
```

回到我们的Application类，我们需要在我们的上下文路径的根部注册 Camel servlet，当应用程序启动时，它将从 application.yml 中的引用baeldung.api.path注入：

```java
@Value("${baeldung.api.path}")
String contextPath;

@Bean
ServletRegistrationBean servletRegistrationBean() {
    ServletRegistrationBean servlet = new ServletRegistrationBean
      (new CamelHttpTransportServlet(), contextPath+"/");
    servlet.setName("CamelServlet");
    return servlet;
}
```

从 Camel 的 2.19 版本开始，此配置已被删除，因为CamelServlet默认设置为“/camel”。

## 6. 建立路线

让我们通过从 Camel 扩展RouteBuilder类开始创建路由，并将其设置为@Component以便组件扫描例程可以在 Web 服务器初始化期间找到它：

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

在这个类中，我们覆盖了Camel 的RouteBuilder类中的configure()方法。

Camel 总是需要一个CamelContext实例——保存传入和传出消息的核心组件。

在这个简单的例子中，DefaultCamelContext就足够了，因为它只是将消息绑定到其中，就像我们要创建的 REST 服务一样。

### 6.1. restConfiguration ()路由

接下来，我们为计划在restConfiguration()方法中创建的端点创建一个 REST 声明：

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

在这里，我们使用 YAML 文件中的注入属性注册上下文路径。同样的逻辑也适用于我们应用程序的端口。CORS 已启用，允许跨站点使用此 Web 服务。绑定模式允许并将参数转换为我们的 API。

接下来，我们将 Swagger 文档添加到我们之前设置的 URI、标题和版本中。当我们为 REST Web 服务创建方法/端点时，Swagger 文档将自动更新。

这个Swagger上下文本身就是一个Camel路由，在启动过程中我们可以在服务器日志中看到一些关于它的技术信息。我们的示例文档默认位于http://localhost:8080/camel/api-doc。

### 6.2. 其余()路线

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

对于熟悉 API 的人来说，此方法非常简单。id是CamelContext内部路由的标识。下一行定义 MIME 类型。这里定义了绑定模式，以表明我们可以在restConfiguration()上设置模式。

post()方法向API 添加一个操作，生成一个“ POST /bean ”端点，而MyBean(具有Integer id和String name的常规 Java bean )定义预期参数。

同样，GET、PUT 和 DELETE 等 HTTP 操作也可以get()、put()、delete()的形式提供。

最后，to()方法创建到另一条路线的桥梁。在这里，它告诉 Camel 在其上下文/引擎中搜索我们将要创建的另一条路由——该路由由值/id“ direct: … ”命名和检测，匹配from()方法中定义的路由。

### 6.3. from()路线与transform()

使用 Camel 时，路由接收参数，然后转换、转换和处理这些参数。之后，它将这些参数发送到另一个路由，该路由将结果转发到所需的输出(文件、数据库、SMTP 服务器或 REST API 响应)。

在本文中，我们只在我们重写的configure()方法中创建另一个路由。这将是我们最后一条to()路线的目的地路线：

```java
from("direct:remoteService")
  .routeId("direct-route")
  .tracing()
  .log(">>> ${body.id}")
  .log(">>> ${body.name}")
  .transform().simple("Hello ${in.body.name}")
  .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
```

from()方法遵循相同的原则，并具有许多与 rest() 方法相同的方法，除了它从 Camel 上下文消息中消费。这就是参数“ direct-route ”的原因，它创建了到上述方法rest().to()的链接。

许多其他转换可用，包括提取为 Java 基元(或对象)并将其向下发送到持久层。请注意，路由始终从传入消息中读取，因此链式路由将忽略传出消息。

我们的例子已经准备好了，我们可以试试看：

-   运行提示命令：mvn spring-boot:run
-   使用标头参数对http://localhost:8080/camel/api/bean执行 POST 请求： Content-Type: application/json和有效负载{“id”: 1,”name”: “World”}
-   我们应该收到 201 的返回码和响应：Hello, World

### 6.4. 简单的脚本语言

该示例使用tracing()方法输出日志记录。请注意，我们使用了${}占位符；这些是属于 Camel 的称为 SIMPLE 的脚本语言的一部分。它适用于通过路由交换的消息，如消息正文。

在我们的示例中，我们使用 SIMPLE 将 Camel 消息正文中的 bean 属性输出到日志中。

我们也可以使用它来进行简单的转换，如transform()方法所示。

### 6.5. from()路由与process()

让我们做一些更有意义的事情，比如调用服务层返回处理后的数据。SIMPLE 不适用于繁重的数据处理，所以让我们用process()方法替换transform()：

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

这允许我们将数据提取到一个 bean 中，与之前在type()方法上定义的那个相同，并在我们的ExampleServices层中处理它。

由于我们之前将bindingMode()设置为 JSON，响应已经是基于我们的 POJO 生成的正确 JSON 格式。这意味着对于ExampleServices类：

```java
public class ExampleServices {
    public static void example(MyBean bodyIn) {
        bodyIn.setName( "Hello, " + bodyIn.getName() );
        bodyIn.setId(bodyIn.getId()  10);
    }
}
```

相同的 HTTP 请求现在返回响应代码 201 和正文：{“id”: 10,”name”: “Hello, World”}。

## 七、总结

通过几行代码，我们成功创建了一个相对完整的应用程序。所有依赖项都通过单个命令自动构建、管理和运行。此外，我们可以创建将各种技术联系在一起的 API。

这种方法对容器也非常友好，从而产生了一个非常精简的服务器环境，可以很容易地按需。额外的配置可能性可以很容易地合并到容器模板配置文件中。