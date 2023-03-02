## 1. 概述

顾名思义，[Sentinel](https://github.com/alibaba/Sentinel)是微服务的强大卫士。它提供流量控制、并发限制、熔断和自适应系统保护等功能来保证其可靠性。它是由阿里巴巴集团积极维护的开源组件。此外，它正式成为[Spring Cloud Circuit Breaker](https://spring.io/projects/spring-cloud-circuitbreaker)的一部分。

在本教程中，我们将了解Sentinel的一些主要功能。此外，我们将看到一个示例，说明如何使用它、它的注解支持和它的监控仪表板。

## 2. 特点

### 2.1 流量控制

Sentinel控制随机传入请求的速度，以避免微服务过载。这可确保我们的服务不会被流量激增所扼杀。**它支持多种流量整形策略。当每秒查询数(QPS)过高时，这些策略会自动将流量调整为适当的形状**。

其中一些流量整形策略是：

-   **直接拒绝模式**：当每秒请求数超过设定的阈值时，它将自动拒绝更多的请求
-   **慢启动预热模式**：如果流量突然激增，此模式可确保请求计数逐渐增加，直到达到上限

### 2.2 熔断与降级

当一个服务同步调用另一个服务时，另一个服务可能由于某种原因而关闭。在这种情况下，线程会在继续等待其他服务响应时被阻塞。这可能会导致资源耗尽，调用者服务也将无法处理进一步的请求。**这称为级联效应，可能摧毁我们的整个微服务架构**。

为了防止这种情况发生，断路器应运而生。它将立即阻止对其他服务的所有后续调用。超时时间过后，将传递某些请求。如果成功，断路器将恢复正常流量。否则，超时时间重新开始。

**Sentinel利用最大并发限制的原理来实现熔断**。它通过限制并发线程数来减少不稳定资源的影响。

Sentinel也会对不稳定的资源进行降级。当资源的响应时间过长时，在指定的时间窗口内将拒绝对该资源的所有调用。这可以防止调用变得非常慢的情况，从而导致级联效应。

### 2.3 自适应系统保护

**Sentinel会在系统负载过高时保护我们的服务器**。它使用load1(系统负载)作为启动流量控制的指标。在以下情况下请求将被阻止：

-   当前系统负载(load1) > 阈值(highestSystemLoad)；
-   当前并发请求数(线程数) > 估计容量(最小响应时间 * 最大QPS)

## 3. 使用方法

### 3.1 添加Maven依赖

在我们的Maven项目中，我们需要在pom.xml中添加[sentinel-core](https://mvnrepository.com/artifact/com.alibaba.csp/sentinel-core)依赖：

```xml
<dependency>
	<groupId>com.alibaba.csp</groupId>
	<artifactId>sentinel-core</artifactId>
	<version>1.8.0</version>
</dependency>
```

### 3.2 定义资源

让我们使用Sentinel API在try-catch块中定义我们的资源和相应的业务逻辑：

```java
try (Entry entry = SphU.entry("HelloWorld")) {
    // Our business logic here.
    System.out.println("hello world");
} catch (BlockException e) {
    // Handle rejected request.
}
```

这个资源名称为“HelloWorld”的try-catch块用作我们业务逻辑的入口点，由Sentinel保护。

### 3.3 定义流量控制规则

这些规则控制流向我们资源的流量，例如阈值计数或控制行为-例如，直接拒绝或缓慢启动。让我们使用FlowRuleManager.loadRules()来配置流规则：

```java
List<FlowRule> flowRules = new ArrayList<>();
FlowRule flowRule = new FlowRule();
flowRule.setResource(RESOURCE_NAME);
flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
flowRule.setCount(1);
flowRules.add(flowRule);
FlowRuleManager.loadRules(flowRules);
```

此规则定义了我们的资源“RESOURCE_NAME”每秒最多可以响应一个请求。

### 3.4 定义降级规则

使用降级规则，我们可以配置断路器的阈值请求计数、恢复超时和其他设置。让我们使用DegradeRuleManager.loadRules()配置降级规则：

```java
List<DegradeRule> rules = new ArrayList<DegradeRule>();
DegradeRule rule = new DegradeRule();
rule.setResource(RESOURCE_NAME);
rule.setCount(10);
rule.setTimeWindow(10);
rules.add(rule);
DegradeRuleManager.loadRules(rules);
```

此规则指定当我们的资源RESOURCE_NAME未能满足10个请求(阈值计数)时，电路将断开。所有后续对该资源的请求都会被Sentinel阻塞10秒(时间窗口)。

### 3.5 定义系统保护规则

使用系统保护规则，我们可以配置并确保自适应系统保护(load1的阈值、平均响应时间、并发线程数)。让我们使用SystemRuleManager.loadRules()方法配置系统规则：

```java
List<SystemRule> rules = new ArrayList<>();
SystemRule rule = new SystemRule();
rule.setHighestSystemLoad(10);
rules.add(rule);
SystemRuleManager.loadRules(rules);
```

此规则指定，对于我们的系统，最高系统负载是每秒10个请求。如果当前负载超过此阈值，所有进一步的请求都将被阻止。

## 4. 注解支持

**Sentinel还为定义资源提供了面向切面的注解支持**。

首先，我们将为[sentinel-annotation-aspectj](https://central.sonatype.com/artifact/com.alibaba.csp/sentinel-annotation-aspectj/2.0.0-alpha)添加Maven依赖项：

```xml
<dependency>
	<groupId>com.alibaba.csp</groupId>
	<artifactId>sentinel-annotation-aspectj</artifactId>
	<version>1.8.0</version>
</dependency>
```

然后，我们将@Configuration添加到我们的配置类以将Sentinel切面注册为Spring bean：

```java
@Configuration
public class SentinelAspectConfiguration {

	@Bean
	public SentinelResourceAspect sentinelResourceAspect() {
		return new SentinelResourceAspect();
	}
}
```

@SentinelResource表示资源定义。它具有诸如value之类的属性，用于定义资源名称。属性fallback是回退方法名称。当电路断开时，这个回退方法定义了我们程序的备用流程。让我们使用@SentinelResource注解来定义资源：

```java
@SentinelResource(value = "resource_name", fallback = "doFallback")
public String doSomething(long i) {
    return "Hello " + i;
}

public String doFallback(long i, Throwable t) {
    // Return fallback value.
    return "fallback";
}
```

这定义了名称为resource_name的资源，以及回退方法。

## 5. 监控仪表板

**Sentinel还提供了一个监控仪表板**。有了这个，我们可以监控客户端并动态配置规则。我们可以实时查看我们定义的资源的传入流量。

### 5.1 启动仪表板

首先，我们需要下载[Sentinel Dashboard jar](https://github.com/alibaba/Sentinel/releases)。然后，我们可以使用以下命令启动仪表板：

```shell
java -Dserver.port=8080 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard.jar
```

仪表板应用程序启动后，我们可以按照下一节中的步骤连接我们的应用程序。

### 5.2 准备我们的应用程序

让我们将[sentinel-transport-simple-http](https://central.sonatype.com/artifact/com.alibaba.csp/sentinel-transport-simple-http/2.0.0-alpha)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-transport-simple-http</artifactId>
    <version>1.8.0</version>
</dependency>
```

### 5.3 将我们的应用程序连接到仪表板

启动应用程序时，我们需要添加仪表板IP地址：

```properties
-Dcsp.sentinel.dashboard.server=consoleIp:port
```

现在，每当调用资源时，仪表板都会从我们的应用程序接收心跳：

<img src="../assets/img.png">

我们还可以使用仪表板动态地操纵流、降级和系统规则。

## 6. 总结

在本文中，我们看到了阿里巴巴Sentinel流量控制、断路器和自适应系统保护的主要特性。