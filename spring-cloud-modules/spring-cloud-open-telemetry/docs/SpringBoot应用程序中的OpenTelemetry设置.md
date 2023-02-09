## 一、概述

在分布式系统中，预期在服务请求时必然会偶尔发生错误。中央[可观察性](https://www.baeldung.com/distributed-systems-observability#:~:text=Observability is the ability to,basically known as telemetry data.)平台通过捕获应用程序跟踪/日志来提供帮助，并提供一个接口来查询特定请求。[OpenTelemetry](https://opentelemetry.io/)有助于标准化捕获和导出遥测数据的过程。

在本教程中，我们将学习如何将 Spring Boot 应用程序与 OpenTelemetry 集成。此外，我们将配置 OpenTelemetry 以捕获应用程序跟踪并将它们发送到中央系统以监控请求。

首先，让我们了解几个基本概念。

## 2. OpenTelemetry简介

[OpenTelemetry](https://opentelemetry.io/) (Otel) 是标准化的供应商不可知工具、API 和 SDK 的集合。这是一个[CNCF](https://www.cncf.io/)孵化项目，是 OpenTracing 和 OpenCensus 项目的合并。

OpenTracing 是供应商中立的 API，用于将遥测数据发送到可观察性后端。OpenCensus 项目提供了一组特定于语言的库，开发人员可以使用这些库来检测他们的代码并将其发送到任何受支持的后端。Otel 使用与其前身项目相同的[跟踪](https://www.baeldung.com/spring-cloud-sleuth-get-trace-id)和跨度概念来表示跨微服务的请求流。

OpenTelemetry 允许我们检测、生成和收集遥测数据，这有助于分析应用程序行为或性能。遥测数据可以包括日志、指标和跟踪。我们可以自动或手动检测 HTTP、数据库调用等代码。

使用 Otel SDK，我们可以轻松地覆盖或向跟踪添加更多属性。

让我们通过一个例子来深入探讨这个问题。

## 3. 示例应用

假设我们需要构建两个微服务，其中一个服务与另一个服务交互。为了检测遥测数据的应用程序，我们将应用程序与 Spring Cloud 和 OpenTelemetry 集成。

### 3.1. Maven 依赖项

*[spring-cloud-starter-sleuth](https://search.maven.org/search?q=g:org.springframework.cloud and a:spring-cloud-starter-sleuth)*、spring *[-cloud-sleuth-otel-autoconfigure](https://search.maven.org/search?q=g:org.springframework.cloud and a:spring-cloud-sleuth-otel-autoconfigure)*和*[opentelemetry-exporter-otlp-trace](https://search.maven.org/search?q=g:io.opentelemetry and a:opentelemetry-exporter-otlp-trace)*依赖项将自动捕获跟踪并将其导出到任何支持的收集器。此外，我们还需要包含*[grpc-okhttp](https://search.maven.org/search?q=g:io.grpc and a:grpc-okhttp)*依赖项，Otel 需要它来支持 HTTP/2。

首先，我们将首先创建一个 Spring Boot Web 项目，并将以下 Spring 和 OpenTelemetry 依赖项包含到两个应用程序中：

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-sleuth-brave</artifactId>
        </exclusion>
   </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-otel-autoconfigure</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp-trace</artifactId>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-okhttp</artifactId>
    <version>1.42.1</version>
</dependency>复制
```

我们应该注意，我们已经排除了 Spring Cloud Brave 依赖项，以便用Otel**替换默认的跟踪实现。**

此外，我们还需要包括[Spring Cloud Sleuth 的 Spring 依赖管理 BOM](https://www.baeldung.com/spring-maven-bom)：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2020.0.4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-sleuth-otel-dependencies</artifactId>
            <version>1.0.0.M12</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>复制
```

### 3.2. 实施下游应用

我们的下游应用程序将有一个端点来返回*价格*数据。

首先，让我们为 *Price* 类建模：

```java
public class Price {
    private long productId;
    private double priceAmount;
    private double discount;
}复制
```

接下来，让我们使用*获取价格* 端点实现*PriceController ：*

```java
@RestController(value = "/price")
public class PriceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceController.class);

    @Autowired
    private PriceRepository priceRepository;

    @GetMapping(path = "/{id}")
    public Price getPrice(@PathVariable("id") long productId) {
        LOGGER.info("Getting Price details for Product Id {}", productId);
        return priceRepository.getPrice(productId);
    }
}复制
```

然后，我们将在*PriceRepository中实现**getPrice*方法：

```java
public Price getPrice(Long productId){
    LOGGER.info("Getting Price from Price Repo With Product Id {}", productId);
    if(!priceMap.containsKey(productId)){
        LOGGER.error("Price Not Found for Product Id {}", productId);
        throw new PriceNotFoundException("Price Not Found");
    }
    return priceMap.get(productId);
}复制
```

### 3.3. 实施上游应用程序

上游应用程序还将有一个端点来获取*产品*详细信息并与上面的*获取价格*端点集成。

首先，让我们实现*Product* *类*：

```java
public class Product {
    private long id;
    private String name;
    private Price price;
}复制
```

然后，让我们使用用于获取产品的端点来实现*ProductController 类：*

```java
@RestController
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private PriceClient priceClient;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping(path = "/product/{id}")
    public Product getProductDetails(@PathVariable("id") long productId){
        LOGGER.info("Getting Product and Price Details with Product Id {}", productId);
        Product product = productRepository.getProduct(productId);
        product.setPrice(priceClient.getPrice(productId));
        return product;
    }
}复制
```

接下来，我们将在*ProductRepository中实现**getProduct*方法：

```java
public Product getProduct(Long productId){
    LOGGER.info("Getting Product from Product Repo With Product Id {}", productId);
    if(!productMap.containsKey(productId)){
        LOGGER.error("Product Not Found for Product Id {}", productId);
        throw new ProductNotFoundException("Product Not Found");
    }
    return productMap.get(productId);
}复制
```

最后，让我们在*PriceClient中实现**getPrice*方法：

```java
public Price getPrice(@PathVariable("id") long productId){
    LOGGER.info("Fetching Price Details With Product Id {}", productId);
    String url = String.format("%s/price/%d", baseUrl, productId);
    ResponseEntity<Price> price = restTemplate.getForEntity(url, Price.class);
    return price.getBody();
}复制
```

## 4. 使用 OpenTelemetry 配置 Spring Boot

OpenTelemetry 提供了一个称为 Otel 收集器的收集器，它处理遥测数据并将其导出到任何可观察性后端，如 Jaeger *、*[Prometheus](https://www.baeldung.com/spring-boot-self-hosted-monitoring)等。

可以使用一些 Spring Sleuth 配置将跟踪导出到 Otel 收集器。

### 4.1. 配置 Spring 侦探

我们需要使用 Otel 端点配置应用程序以发送遥测数据。

*让我们在application.properties*中包含 Spring Sleuth 配置：

```bash
spring.sleuth.otel.config.trace-id-ratio-based=1.0
spring.sleuth.otel.exporter.otlp.endpoint=http://collector:4317
复制
```

***trace-id-ratio-based\*** 属性定义了所 收集跨度**的采样率**。值*1.0*表示将导出所有跨度。

### 4.2. 配置 OpenTelemetry 收集器

Otel 收集器是 OpenTelemetry 跟踪的引擎。它由接收器、处理器和导出器组件组成。有一个可选的扩展组件可以帮助进行健康检查、服务发现或数据转发。扩展组件不涉及处理遥测数据。

为了快速引导 Otel 服务，我们将使用托管在端口*14250*的 Jaeger 后端端点。

让 我们使用 Otel 管道阶段配置*otel-config.yml ：*

```yaml
receivers:
  otlp:
    protocols:
      grpc:
      http:

processors:
  batch:

exporters:
  logging:
    logLevel: debug
  jaeger:
    endpoint: jaeger-service:14250
    tls:
      insecure: true

service:
  pipelines:
    traces:
      receivers:  [ otlp ]
      processors: [ batch ]
      exporters:  [ logging, jaeger ]复制
```

我们应该注意，上面的*处理器**配置*是可选的，默认情况下是不启用的。*处理器* *批处理*选项有助于更好地压缩数据并减少传输数据所需的传出连接数。

另外，我们应该注意*接收器*配置了*GRPC*和*HTTP*协议。

## 5. 运行应用程序

我们现在将配置并运行整个设置、应用程序和 Otel 收集器。

### 5.1. 在应用程序中配置*Dockerfile*

让我们为我们的产品服务实施*Dockerfile ：*

```plaintext
FROM adoptopenjdk/openjdk11:alpine
COPY target/spring-cloud-open-telemetry1-1.0.0-SNAPSHOT.jar spring-cloud-open-telemetry.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/spring-cloud-open-telemetry.jar"]复制
```

我们应该注意到价格服务的*Dockerfile本质上是相同的**。*

### 5.2. 使用 Docker Compose 配置服务

现在，让我们使用整个设置配置[*docker-compose.yml ：*](https://www.baeldung.com/ops/docker-compose)

```bash
version: "4.0"

services:
  product-service:
    build: spring-cloud-open-telemetry1/
    ports:
      - "8080:8080"

  price-service:
    build: spring-cloud-open-telemetry2/
    ports:
      - "8081"

  collector:
    image: otel/opentelemetry-collector:0.47.0
    command: [ "--config=/etc/otel-collector-config.yml" ]
    volumes:
      - ./otel-config.yml:/etc/otel-collector-config.yml
    ports:
      - "4317:4317"
    depends_on:
      - jaeger-service

  jaeger-service:
    image: jaegertracing/all-in-one:latest
    ports:
      - "16686:16686"
      - "14250"
复制
```

现在让我们通过*docker-compose*运行服务：

```bash
$ docker-compose up复制
```

### 5.3. 验证正在运行的 Docker 服务

除了*product-service*和*price-service*，我们还在整个设置中添加了*collector-service*和*jaeger-service* *。*上面的*product-service*和*price-service*使用*采集器*服务*端口* *4317*发送trace数据。收集*器*服务反过来依赖*jaeger-service*端点将跟踪数据导出到*Jaeger*后端。

对于*jaeger-service*，我们使用*jaegertracing/all-in-one*图像，其中包括其后端和 UI 组件。

[*让我们使用docker 容器*](https://www.baeldung.com/ops/docker-list-containers) 命令验证服务的状态：

```bash
$ docker container ls --format "table {{.ID}}\t{{.Names}}\t{{.Status}}\t{{.Ports}}"复制
CONTAINER ID   NAMES                                           STATUS         PORTS
7b874b9ee2e6   spring-cloud-open-telemetry-collector-1         Up 5 minutes   0.0.0.0:4317->4317/tcp, 55678-55679/tcp
29ed09779f98   spring-cloud-open-telemetry-jaeger-service-1    Up 5 minutes   5775/udp, 5778/tcp, 6831-6832/udp, 14268/tcp, 0.0.0.0:16686->16686/tcp, 0.0.0.0:61686->14250/tcp
75bfbf6d3551   spring-cloud-open-telemetry-product-service-1   Up 5 minutes   0.0.0.0:8080->8080/tcp, 8081/tcp
d2ca1457b5ab   spring-cloud-open-telemetry-price-service-1     Up 5 minutes   0.0.0.0:61687->8081/tcp复制
```

## 6. 监控收集器中的痕迹

Jaeger 等遥测收集器工具提供前端应用程序来监控请求。我们可以实时或稍后查看请求跟踪。

让我们在请求成功和失败时监视跟踪。

### 6.1. 请求成功时监控跟踪

首先，我们调用*产品*端点*http://localhost:8080/product/100003*。

该请求将使一些日志出现：

```bash
spring-cloud-open-telemetry-price-service-1 | 2023-01-06 19:03:03.985 INFO [price-service,825dad4a4a308e6f7c97171daf29041a,346a0590f545bbcf] 1 --- [nio-8081-exec-1] c.b.opentelemetry.PriceRepository : Getting Price from Price With Product Id 100003
spring-cloud-open-telemetry-product-service-1 | 2023-01-06 19:03:04.432 INFO [,825dad4a4a308e6f7c97171daf29041a,fb9c54565b028eb8] 1 --- [nio-8080-exec-1] c.b.opentelemetry.ProductRepository : Getting Product from Product Repo With Product Id 100003
spring-cloud-open-telemetry-collector-1 | Trace ID : 825dad4a4a308e6f7c97171daf29041a复制
```

Spring Sleuth 将自动配置 *ProductService* 以将 *跟踪 ID*附加 到当前线程，并将其作为 HTTP*标头*附加到下游 API 调用。PriceService *还将*自动在线程上下文和日志中 包含相同的 *跟踪 ID 。*Otel 服务将使用此跟踪 ID 来确定跨服务的请求流。

正如预期的那样，上面的 *跟踪 ID ….f29041a* 在 *PriceService* 和 *ProductService* 日志中是相同的。

*让我们在端口16686*托管的 Jaeger UI 中可视化整个请求跨度时间线：

[![img](https://www.baeldung.com/wp-content/uploads/2023/01/6_BAEL-5975_issue_detail.png)](https://www.baeldung.com/wp-content/uploads/2023/01/Screenshot-2023-01-07-at-8.10.58-AM.png)

上面显示了请求流的时间线，并包含表示请求的元数据。

### 6.2. 请求失败时监视跟踪

想象一个场景，下游服务抛出异常，导致请求失败。

同样，我们将利用相同的 UI 来分析根本原因。

*让我们使用产品*端点*/product/100005*调用 测试上述场景，其中*产品*不存在于下游应用程序中。

现在，让我们可视化失败的请求跨度：

![Jaegar-UI-错误示例](https://www.baeldung.com/wp-content/uploads/2023/01/BAEL-5975_alert-1.png)

如上所示，我们可以将请求追溯到 错误起源的最终*API调用。*

## 七、结论

在本文中，我们了解了 OpenTelemetry 如何帮助标准化微服务的可观察性模式。

我们还通过示例了解了如何使用 OpenTelemetry 配置 Spring Boot 应用程序。最后，我们在 Collector 中跟踪了一个 API 请求流。