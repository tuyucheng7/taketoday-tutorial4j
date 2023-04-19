## 1. 概述

在本文中，我们将探讨如何使用[Gatling](https://www.baeldung.com/introduction-to-gatling)在任何Rest端点上进行性能测试，特别是[负载测试](https://www.baeldung.com/gatling-distributed-perf-testing)。我们将从快速介绍各种类型的性能测试及其关键性能指标(KPI)开始。

接下来，我们将快速概述Gatling术语。我们将使用Maven Gatling插件和依赖项设置一个示例。我们将探索[Gatling Java DSL](https://gatling.io/docs/gatling/)来执行模拟场景的负载测试。

最后，我们将运行模拟并查看生成的报告。

## 2. 性能测试类型

**性能测试涉及测量各种指标以了解系统在不同级别的流量和[吞吐量](https://www.baeldung.com/cs/bandwidth-packet-loss-latency-jitter)下的性能**。其他类型的性能测试包括负载测试、压力测试、浸泡测试、峰值测试和可扩展性测试。接下来让我们快速了解每种性能测试策略的目的。

**[负载测试](https://www.baeldung.com/gatling-jmeter-grinder-comparison)涉及在一段时间内在并发虚拟用户的重负载下测试系统**。另一方面，压力测试涉及逐渐增加系统的负载以找到其突破点。浸泡测试旨在使系统的流量在较长时间内保持稳定，以识别瓶颈。顾名思义，峰值测试包括测试当请求数量迅速增加到压力水平，然后很快再次减少时系统的性能。最后，可扩展性测试涉及测试当用户请求数量增加或减少时系统的性能。

在进行性能测试时，我们可以收集几个关键性能指标(KPI)来衡量系统性能。**这些包括事务响应时间、吞吐量(一段时间内处理的事务数)和错误(例如超时)**。压力测试还可以帮助识别内存泄漏、速度减慢、安全漏洞和数据损坏。

在本文中，我们将重点介绍使用Gatling进行负载测试。

## 3. 关键术语

让我们从[Gatling框架的一些基本术语](https://www.baeldung.com/introduction-to-gatling)开始。

-   场景：场景是虚拟用户为复制常见用户操作(例如登录或购买)而采取的一系列步骤。
-   馈送器：馈送器是允许从外部源(如CSV或JSON文件)将数据输入到虚拟用户操作中的机制。
-   模拟：模拟确定在特定时间范围内运行场景的虚拟用户数量
-   会话：每个虚拟用户都由一个会话支持，该会话跟踪场景期间交换的消息。
-   记录器：Gatling的UI提供了一个记录器工具，可以生成场景和模拟

## 4. 示例设置

让我们专注于员工管理微服务的一小部分，它由一个带有必须进行负载测试的POST和GET端点的RestController组成。

在我们开始实现我们的简单解决方案之前，让我们添加所需的[Gatling依赖项](https://central.sonatype.com/artifact/io.gatling/gatling-app/3.9.3)：

```xml
<dependency>
    <groupId>io.gatling</groupId>
    <artifactId>gatling-app</artifactId>
    <version>3.7.2</version>
</dependency>
<dependency>
    <groupId>io.gatling.highcharts</groupId>
    <artifactId>gatling-charts-highcharts</artifactId>
    <version>3.7.2</version>
</dependency>
```

接下来，让我们添加[Maven插件](https://central.sonatype.com/artifact/io.gatling/gatling-maven-plugin/4.3.0)：

```xml
<plugin>
    <groupId>io.gatling</groupId>
    <artifactId>gatling-maven-plugin</artifactId>
    <version>4.2.9</version>
    <configuration>
        <simulationClass>cn.tuyucheng.taketoday.EmployeeRegistrationSimulation</simulationClass>
    </configuration>
</plugin>
```

**如pom.xml文件中的信息所示，我们已通过插件配置将模拟类显式设置为EmployeeRegistrationSimulation**。这意味着插件将使用指定的类作为运行模拟的基础。

接下来，让我们使用我们想要使用Gatling进行负载测试的POST和GET端点来定义我们的RestController：

```java
@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
public ResponseEntity<Void> addEmployee(@RequestBody EmployeeCreationRequest request, UriComponentsBuilder uriComponentsBuilder) {
    URI location = uriComponentsBuilder.path("/api/employees/{id}")
        .buildAndExpand("99")
        .toUri();
    return ResponseEntity.created(location)
        .build();
}
```

接下来让我们添加GET端点：

```java
@GetMapping("/{id}")
public Employee getEmployeeWithId(@PathVariable("id") Long id) {
    List<Employee> allEmployees = createEmployees();
    return allEmployees.get(ThreadLocalRandom.current().nextInt(0, allEmployees.size()));
}
```

接下来，让我们扩展Simulation类及其支持负载测试的组成组件和API。

## 5. 模拟步骤

模拟代表捕获各个方面的负载测试，例如多个用户群体可能如何操作、他们将执行什么场景以及如何注入新的虚拟用户。**在Gatling框架中，Simulation类是启动负载测试过程的主要组件。Gatling Java API包括可变抽象类Simulation**。我们可以根据我们的特定要求扩展Simulation类以创建自定义模拟：

```java
public class EmployeeRegistrationSimulation extends Simulation {

    private static final HttpProtocolBuilder HTTP_PROTOCOL_BUILDER = setupProtocolForSimulation();

    private static final Iterator<Map<String, Object>> FEED_DATA = setupTestFeedData();

    private static final ScenarioBuilder POST_SCENARIO_BUILDER = buildPostScenario();
    
    // ...
}
```

基本上，我们需要定义以下内容：

-    HTTP协议配置
-    标头
-    馈送器
-    HTTP请求
-    场景
-    负载注入模式

现在，让我们看看各个步骤以及如何使用Gatling提供的DSL来定义它们。接下来我们将从协议配置开始。

### 5.1 HTTP协议配置

Gatling是一种与技术无关的负载测试工具，它支持各种协议，包括HTTP、HTTPS和WebSockets。本节将重点介绍为我们的负载测试场景配置HTTP协议。

**为了在EmployeeRegistrationSimulation类中设置HTTP协议详细信息，我们将使用HttpDsl类型，它用作Gatling HTTP DSL的入口点。然后我们将使用HTTPProtocolBuilder DSL来定义HTTP协议配置**：

```java
private static HttpProtocolBuilder setupProtocolForSimulation() {
    return HttpDsl.http.baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .maxConnectionsPerHost(10)
        .userAgentHeader("Gatling/Performance Test");
}
```

在Gatling中配置HTTP协议涉及使用HttpDsl类来定义使用HTTPProtocolBuilder DSL的HTTP协议配置。关键配置设置包括baseUrl、acceptHeader、maxConnectionsPerHost和userAgentHeader。这些设置有助于确保我们的负载测试准确地模拟真实场景。

### 5.2 馈送器定义

**Feeders是一个方便的API，允许测试人员将来自外部源的数据注入到虚拟用户会话中**。Gatling支持各种馈送器，例如CSV、JSON、基于文件和基于数组/列表的馈送器。

接下来，让我们创建一个方法来返回测试用例的测试数据：

```java
private static Iterator<Map<String, Object>> feedData() {
    Faker faker = new Faker();
    Iterator<Map<String, Object>> iterator;
    iterator = Stream.generate(() -> {
          Map<String, Object> stringObjectMap = new HashMap<>();
          stringObjectMap.put("empName", faker.name().fullName());
          return stringObjectMap;
    })
    .iterator();
    return iterator;
}
```

在这里，我们将创建一个返回Iterator<Map<String, Object\>>的方法来为我们的测试用例检索测试数据。然后，方法feedData()使用Faker库生成测试数据，创建一个HashMap来存储数据，并返回数据的迭代器。

**馈送器本质上是由feed方法创建的Iterator<Map<String, Object\>>组件的类型别名。feed方法轮询Map<String, Object\>记录并将其内容注入模拟场景**。

Gatling还提供了内置的馈送器策略，例如queue()、random()、shuffle()和circular()。此外，根据被测系统的不同，我们可以将数据加载机制配置为eager()或batch()。

### 5.3 场景定义

**Gatling中的场景表示虚拟用户将遵循的典型用户行为**。它是一个基于EmployeeController中定义的资源的工作流。在这种情况下，我们将创建一个场景，使用简单的工作流程模拟员工创建：

```java
private static ScenarioBuilder buildPostScenario() {
    return CoreDsl.scenario("Load Test Creating Employee")
        .feed(FEED_DATA)
        .exec(http("create-employee-request").post("/api/employees")
            .header("Content-Type," "application/json")
            .body(StringBody("{ \"empName\": \"${empName}\" }"))
            .check(status().is(201))
            .check(header("Location").saveAs("location")))
        .exec(http("get-employee-request").get(session -> session.getString("location"))
            .check(status().is(200)));
    }
```

Gatling API提供了scenario(String name)方法，该方法返回ScenarioBuilder类的一个实例。ScenarioBuilder封装了场景的详细信息，包括测试数据的来源和HTTP请求详细信息，例如请求正文、标头和预期状态代码。

从本质上讲，我们构建了一个场景，在该场景中，我们首先使用post方法发送请求，通过发送包含从测试数据中检索到的empName值的JSON请求正文来创建员工。该方法还检查预期的HTTP状态代码(201)并使用saveAs方法将Location标头值保存到会话中。

第二个请求使用get方法通过将保存的Location标头值发送到请求URL来检索创建的员工。它还会检查预期的HTTP状态代码(200)。

### 5.4 负载注入模型

除了定义场景和协议之外，我们还必须为我们的模拟定义负载注入模式。在我们的示例中，我们将通过随着时间的推移添加虚拟用户来逐渐增加负载。**Gatling提供了两种负载注入模型：open和closed。开放模型允许我们控制虚拟用户的到达率，这更能代表现实生活中的系统**。

**Gatling的Java API提供了一个名为OpenInjectionStep的类，它封装了开放注入工作负载模式的常见属性和行为**。我们可以使用OpenInjectionStep的三个子类：

-   ConstantRateOpenInjection：这种注入模型保持虚拟用户的恒定到达率
-   RampRateOpenInjection：这种注入模型逐渐增加虚拟用户的到达率
-   Composite：这种注入模型允许我们组合不同类型的注入模式

对于我们的示例，让我们使用RampRateOpenInjection。我们将从50个虚拟用户开始，逐渐增加负载，每30秒添加50个用户，直到达到200个虚拟用户。然后我们将负载保持在200个虚拟用户5分钟：

```java
private RampRateOpenInjectionStep postEndpointInjectionProfile() {
    int totalDesiredUserCount = 200;
    double userRampUpPerInterval = 50;
    double rampUpIntervalSeconds = 30;
    int totalRampUptimeSeconds = 120;
    int steadyStateDurationSeconds = 300;

    return rampUsersPerSec(userRampUpPerInterval / (rampUpIntervalSeconds / 60)).to(totalDesiredUserCount)
        .during(Duration.ofSeconds(totalRampUptimeSeconds + steadyStateDurationSeconds));
}
```

**通过定义负载注入模式，我们可以准确地模拟我们的系统在不同负载水平下的行为**。这有助于我们识别性能瓶颈并确保我们的系统能够处理预期的用户负载。

### 5.5 设置模拟器

为了设置模拟，我们将结合之前定义的协议、场景和负载注入模型。**这个设置将从EmployeeRegistrationSimulation类的构造函数中调用**：

```java
public EmployeeRegistrationSimulation() {

    setUp(BUILD_POST_SCENARIO.injectOpen(postEndpointInjectionProfile())
        .protocols(HTTP_PROTOCOL_BUILDER));
}
```

### 5.6 断言

最后，**我们将使用Gatling DSL断言模拟按预期工作**。让我们回到我们的EmployeeRegistrationSimulation()构造函数并向现有的setup(...)方法添加一些断言：

```java
setUp(BUILD_POST_SCENARIO.injectOpen(postEndpointInjectionProfile())
    .protocols(HTTP_PROTOCOL_BUILDER))
    .assertions(
        global().responseTime().max().lte(10000),
        global().successfulRequests().percent().gt(90d)
```

如我们所见，这里我们要断言以下条件：

-   基于设置的最大响应时间应小于或等于10秒
-   成功请求的百分比应大于90

## 6. 运行模拟和报告分析

当我们创建Gatling项目时，我们将Maven与[Gatling Maven Plugin](https://gatling.io/docs/current/extensions/maven_plugin)一起使用。因此，我们可以使用Maven任务来执行我们的Gatling测试。要通过Maven运行Gatling脚本，请在Gatling项目的文件夹中打开命令提示符：

```shell
mvn gatling:test
```

因此，我们将收集以下指标：

<img src="../assets/img.png">

**最后，Gatling在target/gatling目录中生成一个HTML报告**。此目录中的主要文件是index.html，它汇总了负载测试配置、响应时间分布图表以及每个请求的统计信息，如上所述。让我们来看看报告中的一些图表：

<img src="../assets/img_1.png">

**每秒请求数图表有助于我们了解系统如何处理不断增加的流量级别**。通过分析每秒请求数图，我们可以确定系统在不降低性能或导致错误的情况下可以运行的最佳请求数。此信息可用于提高系统的可伸缩性并确保它能够处理预期的流量级别。

另一个有趣的图表是响应时间范围：

<img src="../assets/img_2.png">

响应时间分布图显示了属于特定响应时间段的请求的百分比，例如小于800毫秒、介于800毫秒和1.2秒之间以及大于1.2秒。我们可以看到我们所有的响应都在<800毫秒内。

让我们看看响应时间百分位数：

<img src="../assets/img_3.png">

请求统计部分显示了每个请求的详细信息，包括请求数量、成功请求的百分比以及各种响应时间百分位数，例如第50、75、95和99个百分位数。总体而言，index.html文件提供了负载测试结果的全面摘要，可以轻松识别性能瓶颈或问题。

## 7. 总结

在本文中，我们学习了使用Gatling Java DSL对任何REST端点进行负载测试。

首先，我们简要概述了不同类型的性能测试。接下来，我们介绍了特定于Gatling的关键术语。我们演示了如何在POST端点上实施负载测试，同时遵守所需的注入负载和时间限制。此外，我们可以分析测试结果以确定需要改进和优化的领域。