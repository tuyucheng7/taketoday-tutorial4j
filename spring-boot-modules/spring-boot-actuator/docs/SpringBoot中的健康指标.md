## 1. 概述

Spring Boot提供了几种不同的方法来检查正在运行的应用程序及其组件的状态和健康状况。在这些方法中，[HealthContributor](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/api/org/springframework/boot/actuate/health/HealthContributor.html)和[HealthIndicator](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/health/HealthIndicator.html) API是其中两个值得注意的方法。

在本教程中，我们将熟悉这些API，了解它们的工作原理，并了解如何为它们提供自定义信息。

## 2. 依赖关系

健康信息是[Spring Boot Actuator]()模块的一部分，因此我们需要适当的[Maven依赖](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-actuator)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## 3. 内置HealthIndicators

**Spring Boot开箱即用地注册了许多HealthIndicator来报告特定应用程序方面的健康状况**。

其中一些指标几乎总是被注册，例如[DiskSpaceHealthIndicator](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/system/DiskSpaceHealthIndicator.html)或[PingHealthIndicator](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/health/PingHealthIndicator.html)。前者报告磁盘的当前状态，后者用作应用程序的ping端点。

**另一方面，Spring Boot有条件地注册了一些指标**。也就是说，如果类路径上存在某些依赖项或满足其他一些条件，Spring Boot也可能会注册其他一些HealthIndicator。例如，如果我们使用关系型数据库，那么Spring Boot会注册[DataSourceHealthIndicator](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/jdbc/DataSourceHealthIndicator.html)。同样，如果我们碰巧使用Cassandra作为我们的数据存储，它会注册[CassandraHealthIndicator](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/cassandra/CassandraHealthIndicator.html)。

**为了检查Spring Boot应用程序的健康状态，我们可以调用/actuator/health端点**，此端点将报告所有已注册HealthIndicator的聚合结果。

**此外，要查看某个特定指标的健康报告，我们可以调用/actuator/health/{name}端点**。例如，调用/actuator/health/diskSpace端点将从DiskSpaceHealthIndicator返回状态报告：

```json
{
    "status": "UP",
    "details": {
        "total": 499963170816,
        "free": 134414831616,
        "threshold": 10485760,
        "exists": true
    }
}
```

## 4. 自定义HealthIndicators

除了内置的，我们还可以注册自定义HealthIndicator来报告组件或子系统的健康状况。**为此，我们所要做的就是将HealthIndicator接口的实现注册为Spring bean**。

例如，以下实现随机报告失败：

```java
@Component
public class RandomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        double chance = ThreadLocalRandom.current().nextDouble();
        Health.Builder status = Health.up();
        if (chance > 0.9) {
            status = Health.down();
        }
        return status.build();
    }
}
```

根据该指标的健康报告，应用程序应该只有90%的时间处于运行状态。在这里，我们使用[Health](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/health/Health.html)构建器来报告健康信息。

**然而，在响应式应用程序中，我们应该注册一个类型为[ReactiveHealthIndicator](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/health/ReactiveHealthIndicator.html)的bean**，响应式health()方法返回[Mono]()而不是简单的Health。除此之外，其他细节对于两种Web应用程序类型都是相同的。

### 4.1 指标名称

要查看此特定指标的报告，我们可以调用/actuator/health/random端点。例如，API响应可能如下所示：

```json
{
    "status": "UP"
}
```

/actuator/health/random URL中的random是该指标的标识符，**特定HealthIndicator实现的标识符等于没有HealthIndicator后缀的bean名称**。由于bean名称是randomHealthIndicator，因此random前缀将是标识符。

使用这个算法，如果我们将bean名称更改为rand：

```java
@Component("rand")
public class RandomHealthIndicator implements HealthIndicator {
    // omitted
}
```

然后指标标识符将是rand而不是random。

### 4.2 禁用指标

**要禁用特定指标，我们可以将“management.health.<indicator_identifier>.enabled”配置属性设置为false**。例如，如果我们将以下内容添加到我们的application.properties中：

```properties
management.health.random.enabled=false
```

然后Spring Boot将禁用RandomHealthIndicator。要激活此配置属性，我们还应该在指标上添加[@ConditionalOnEnabledHealthIndicator](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/autoconfigure/health/ConditionalOnEnabledHealthIndicator.html)注解：

```java
@Component
@ConditionalOnEnabledHealthIndicator("random")
public class RandomHealthIndicator implements HealthIndicator {
    // omitted
}
```

现在，如果我们调用/actuator/health/random，Spring Boot将返回404 Not Found HTTP响应：

```java
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "management.health.random.enabled=false")
class DisabledRandomHealthIndicatorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenADisabledIndicator_whenSendingRequest_thenReturns404() throws Exception {
        mockMvc.perform(get("/actuator/health/random"))
              .andExpect(status().isNotFound());
    }
}
```

请注意，禁用内置或自定义指标彼此类似。因此，我们也可以将相同的配置应用于内置指标。

### 4.3 额外细节

除了报告状态之外，我们还可以使用[withDetail(key, value)](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/health/Health.Builder.html#withDetail-java.lang.String-java.lang.Object-)附加额外的键值详细信息：

```java
public Health health() {
    double chance = ThreadLocalRandom.current().nextDouble();
    Health.Builder status = Health.up();
    if (chance > 0.9) {
        status = Health.down();
    }

    return status
      .withDetail("chance", chance)
      .withDetail("strategy", "thread-local")
      .build();
}
```

在这里，我们向状态报告添加了两条信息。此外，我们可以通过将Map<String, Object>传递给[withDetails(map)](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/health/Health.Builder.html#withDetail-java.lang.String-java.lang.Object-)方法来实现相同的目的：

```java
Map<String, Object> details = new HashMap<>();
details.put("chance", chance);
details.put("strategy", "thread-local");
        
return status.withDetails(details).build();
```

现在，如果我们调用/actuator/health/random，我们可能会看到如下内容：

```json
{
    "status": "DOWN",
    "details": {
        "chance": 0.9883560157173152,
        "strategy": "thread-local"
    }
}
```

我们也可以通过自动化测试来验证这种行为：

```java
mockMvc.perform(get("/actuator/health/random"))
      .andExpect(jsonPath("$.status").exists())
      .andExpect(jsonPath("$.details.strategy").value("thread-local"))
      .andExpect(jsonPath("$.details.chance").exists());
```

有时在与数据库或磁盘等系统组件通信时会发生异常，我们可以使用[withException(ex)](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/health/Health.Builder.html#withException-java.lang.Throwable-)方法报告此类异常：

```java
if (chance > 0.9) {
    status.withException(new RuntimeException("Bad luck"));
}
```

我们还可以将异常传递给我们之前看到的[down(ex)](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/health/Health.Builder.html#down-java.lang.Throwable-)方法：

```java
if (chance > 0.9) {
    status = Health.down(new RuntimeException("Bad Luck"));
}
```

现在健康报告将包含堆栈跟踪：

```json
{
    "status": "DOWN",
    "details": {
        "error": "java.lang.RuntimeException: Bad Luck",
        "chance": 0.9603739107139401,
        "strategy": "thread-local"
    }
}
```

### 4.4 细节曝光

**management.endpoint.health.show-details配置属性控制每个健康端点可以公开的详细信息级别**。 

例如，如果我们将此属性设置为always，那么Spring Boot将始终返回健康报告中的details字段，就像上面的示例一样。

另一方面，**如果我们将此属性设置为never，那么Spring Boot将始终忽略输出中的详细信息**。还有一个when_authorized值，它只为授权用户公开额外的细节，当且仅当以下情况下，用户才获得授权：

-   他已通过身份验证
-   并且他拥有management.endpoint.health.roles配置属性中指定的角色

### 4.5 健康状况

默认情况下，Spring Boot定义了四个不同的值作为健康[状态](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/health/Status.html)：

-   UP：组件或子系统按预期工作
-   DOWN：组件不工作
-   OUT_OF_SERVICE：组件暂时停止服务
-   UNKNOWN：组件状态未知

这些状态被声明为[public static final](https://github.com/spring-projects/spring-boot/blob/310ef6e9995fab302f6af8b284d0a59ca0f212e9/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/health/Status.java#L43)实例而不是Java枚举，因此可以定义我们自己的自定义健康状态。为此，我们可以使用[status(name)](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/health/Health.Builder.html#status-java.lang.String-)方法：

```java
Health.Builder warning = Health.status("WARNING");
```

**健康状态会影响健康端点的HTTP状态码**，默认情况下，Spring Boot映射DOWN和OUT_OF_SERVICE状态以抛出503状态码。另一方面，UP和任何其他未映射的状态将被转换为200 OK状态码。

要自定义此映射，**我们可以将management.endpoint.health.status.http-mapping.<status\>配置属性设置为所需的HTTP状态码编号**：

```properties
management.endpoint.health.status.http-mapping.down=500
management.endpoint.health.status.http-mapping.out_of_service=503
management.endpoint.health.status.http-mapping.warning=500
```

现在Spring Boot会将DOWN状态映射到500，OUT_OF_SERVICE映射到503，WARNING映射到500 HTTP状态码：

```java
mockMvc.perform(get("/actuator/health/warning"))
      .andExpect(jsonPath("$.status").value("WARNING"))
      .andExpect(status().isInternalServerError());
```

同样，**我们可以注册一个[HttpCodeStatusMapper](https://docs.spring.io/spring-boot/docs/2.2.3.RELEASE/api/org/springframework/boot/actuate/health/HttpCodeStatusMapper.html)类型的bean来自定义HTTP状态码映射**：

```java
@Component
public class CustomStatusCodeMapper implements HttpCodeStatusMapper {

    @Override
    public int getStatusCode(Status status) {
        if (status == Status.DOWN) {
            return 500;
        }

        if (status == Status.OUT_OF_SERVICE) {
            return 503;
        }

        if (status == Status.UNKNOWN) {
            return 500;
        }

        return 200;
    }
}
```

getStatusCode(status)方法将健康状态作为输入并返回HTTP状态码作为输出。此外，还可以映射自定义Status实例：

```java
if (status.getCode().equals("WARNING")) {
    return 500;
}
```

默认情况下，Spring Boot使用默认映射注册此接口的简单实现。正如我们之前看到的，**[SimpleHttpCodeStatusMapper](https://docs.spring.io/spring-boot/docs/2.2.3.RELEASE/api/org/springframework/boot/actuate/health/SimpleHttpCodeStatusMapper.html)还能够从配置文件中读取映射**。

## 5. 健康信息与指标

重要的应用程序通常包含几个不同的组件。例如，考虑一个使用Cassandra作为数据库、Apache Kafka作为其发布-订阅平台以及Hazelcast作为其内存数据网格的Spring Boot应用程序。

**我们应该使用HealthIndicator来查看应用程序是否可以与这些组件通信**，如果通信链路出现故障或组件本身出现故障或速度变慢，那么我们应该注意一个不健康的组件。换句话说，这些指标应该用于报告不同组件或子系统的健康状况。

相反，我们应该避免使用HealthIndicator来测量值、计数事件或测量持续时间，这就是为什么我们有指标。简而言之，**指标是报告CPU使用率、平均负载、堆大小、HTTP响应分布等的更好工具**。

## 6. 总结

在本教程中，我们了解了如何向Actuator健康端点提供更多健康信息。此外，我们还深入介绍了健康API中的不同组件，例如Health、Status和HTTP状态映射的状态。

总结一下，我们快速讨论了健康信息和指标之间的区别，并且了解了何时使用它们。