## 1. 概述

在本文中，我们将了解[Spring Cloud Sleuth](https://www.Tuyucheng.com/spring-cloud-sleuth-single-application)并了解如何使用它在Spring Boot中进行跟踪。**它为我们的日志添加了有用的额外信息，并通过向它们添加唯一标识符使调试操作变得更加容易。这些操作在Sleuth术语中称为跟踪。它们可以由几个步骤组成，称为跨度**。

例如，跟踪可以是从我们的应用程序查询数据的GET请求。当我们的应用程序处理请求时，可以将其拆分为更小的步骤：用户授权、执行数据库查询、转换响应。这些步骤中的每一个都是属于同一跟踪的唯一跨度。

在某些情况下，我们可能想要获取当前跟踪或跨度的ID。例如，我们可以在发生事件时将这些发送给开发团队。然后他们可以使用它来调试和解决问题。

## 2. 应用程序设置

**让我们首先创建一个Spring Boot项目并添加[spring-cloud-starter-sleuth](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-sleuth/3.1.0)依赖项**：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
    <version>3.1.0</version>
</dependency>
```

这个启动器依赖项与Spring Boot集成良好，并提供了开始使用Spring Cloud Sleuth所需的配置。

但是，我们可以采取一个额外的步骤。**让我们在application.properties文件中设置我们的应用程序的名称，这样我们将在日志中看到它以及跟踪和跨度ID**：

```properties
spring.application.name=Tuyucheng Sleuth Tutorial
```

现在我们需要一个应用程序的入口点。让我们创建一个带有单个GET端点的REST控制器：

```java
@RestController
public class SleuthTraceIdController {

    @GetMapping("/traceid")
    public String getSleuthTraceId() {
        return "Hello from Sleuth";
    }
}
```

让我们在[http://localhost:8080/traceid](http://localhost:8080/traceid)访问我们的API端点。我们应该在响应中看到“Hello from Sleuth”。

## 3. 日志记录

让我们向getSleuthTraceId方法添加一条日志语句。首先，我们的类需要一个Logger。然后我们可以记录消息：

```java
private static final Logger logger = LoggerFactory.getLogger(SleuthTraceIdController.class);

@GetMapping("/traceid")
public String getSleuthTraceId() {
    logger.info("Hello with Sleuth");
    return "Hello from Sleuth";
}
```

让我们再次调用我们的API端点并检查日志。我们应该看到类似这样的东西：

```shell
INFO [Tuyucheng Sleuth Tutorial,e48f140a63bb9fbb,e48f140a63bb9fbb] 9208 --- [nio-8080-exec-1] c.t.t.s.traceid.SleuthTraceIdController : Hello with Sleuth
```

**请注意，应用程序名称位于开头的括号内，这些括号是由Sleuth添加的。它们代表应用程序名称、跟踪ID和跨度ID**。

## 4. 当前跟踪和跨度

我们可以使用上面的示例来调试应用程序中的问题，但要确定导致问题的原因以及要遵循的跟踪可能具有挑战性。这就是为什么我们将以编程方式获取当前跟踪，然后我们可以将其用于任何进一步调查。

在我们的实现中，我们将简化此用例，我们将仅将跟踪ID记录到控制台。

**首先，我们需要获取Tracer对象的实例。让我们将它注入我们的控制器并获取当前跨度**：

```java
@Autowired
private Tracer tracer;

@GetMapping("/traceid")
public String getSleuthTraceId() {
    logger.info("Hello with Sleuth");
    Span span = tracer.currentSpan();
    return "Hello from Sleuth";
}
```

**请注意，如果此时没有活动跨度，则currentSpan方法可以返回null**。因此，我们必须执行额外的检查，看看我们是否可以在不获取NullPointerException的情况下继续使用此Span对象。让我们执行此检查并记录当前跟踪和跨度ID：

```java
Span span = tracer.currentSpan();
if (span != null) {
    logger.info("Trace ID {}", span.context().traceIdString());
    logger.info("Span ID {}", span.context().spanIdString());
}
```

让我们运行应用程序并在访问API端点时查找这些消息。它们应该包含与Sleuth添加的括号相同的ID。

## 5. Trace和Span ID为十进制数

还有另一种方法可以使用spanId方法而不是spanIdString来获取跨度ID。**它们之间的区别在于，后者返回值的十六进制表示，而前者返回十进制数**。让我们比较它们的实际情况并记录十进制值：

```java
Span span = tracer.currentSpan();
if (span != null) {
    logger.info("Span ID hex {}", span.context().spanIdString());
    logger.info("Span ID decimal {}", span.context().spanId());
}
```

这两个值代表相同的数字，输出应类似于以下内容：

```shell
INFO [Tuyucheng Sleuth Tutorial,0de46b6fcbc8da83,0de46b6fcbc8da83] 8648 --- [nio-8080-exec-3] c.t.t.s.traceid.SleuthTraceIdController    : Span ID hex 0de46b6fcbc8da83
INFO [Tuyucheng Sleuth Tutorial,0de46b6fcbc8da83,0de46b6fcbc8da83] 8648 --- [nio-8080-exec-3] c.t.t.s.traceid.SleuthTraceIdController    : Span ID decimal 1001043145087572611
```

同样，这也适用于跟踪ID。**我们可以使用traceId方法代替traceIdString。traceIdString返回一个十六进制值，而traceId返回一个十进制值**：

```java
logger.info("Trace ID hex {}", span.context().traceIdString());
logger.info("Trace ID decimal {}", span.context().traceId());
```

输出与上一个非常相似。它首先包含十六进制的跟踪ID，然后包含十进制的跟踪ID：

```shell
INFO [Tuyucheng Sleuth Tutorial,34ec0b8ac9d65e91,34ec0b8ac9d65e91] 7384 --- [nio-8080-exec-1] c.t.t.s.traceid.SleuthTraceIdController    : Trace ID hex 34ec0b8ac9d65e91
INFO [Tuyucheng Sleuth Tutorial,34ec0b8ac9d65e91,34ec0b8ac9d65e91] 7384 --- [nio-8080-exec-1] c.t.t.s.traceid.SleuthTraceIdController    : Trace ID decimal 3813435675195629201
```

## 6. 总结

在本文中，我们讨论了Spring Cloud Sleuth如何帮助调试和跟踪Spring Boot中的事件。**首先，我们使用Tracer对象来引用当前跨度和TraceContext。之后，我们就能够获取当前跟踪和跨度的ID。此外，我们还看到了不同的方法如何在不同的数字系统中返回ID**。