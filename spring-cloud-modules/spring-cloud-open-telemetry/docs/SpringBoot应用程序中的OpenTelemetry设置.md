## 1. 概述

在分布式系统中，预期在服务请求时必然会偶尔发生错误。中央[可观察性](https://www.baeldung.com/distributed-systems-observability#:~:text=Observability%20is%20the%20ability%20to,basically%20known%20as%20telemetry%20data.)平台通过捕获应用程序跟踪/日志来提供帮助，并提供一个接口来查询特定请求。[OpenTelemetry](https://opentelemetry.io/)有助于标准化捕获和导出遥测数据的过程。

在本教程中，我们将学习如何将Spring Boot应用程序与OpenTelemetry集成。此外，我们将配置OpenTelemetry以捕获应用程序跟踪并将它们发送到中央系统以监控请求。

首先，让我们了解一些基本概念。

## 2. OpenTelemetry简介

[OpenTelemetry](https://opentelemetry.io/)(Otel)是与供应商无关的标准化工具、API和SDK的集合。这是一个[CNCF](https://www.cncf.io/)孵化项目，是OpenTracing和OpenCensus项目的合并。

OpenTracing是一个供应商中立的API，用于将遥测数据发送到可观察性后端。OpenCensus项目提供了一组特定于语言的库，开发人员可以使用这些库来检测他们的代码并将其发送到任何受支持的后端。Otel使用与其前身项目相同的[跟踪](https://www.baeldung.com/spring-cloud-sleuth-get-trace-id)和跨度概念来表示跨微服务的请求流。

OpenTelemetry允许我们检测、生成和收集遥测数据，这有助于分析应用程序行为或性能。遥测数据可以包括日志、指标和跟踪。我们可以自动或手动检测HTTP、数据库调用等代码。

使用Otel SDK，我们可以轻松地覆盖或向跟踪添加更多属性。

让我们通过一个例子来深入探讨这个问题。

## 3. 示例应用

假设我们需要构建两个微服务，其中一个服务与另一个服务交互。为了检测遥测数据的应用程序，我们将应用程序与Spring Cloud和OpenTelemetry集成。

### 3.1 Maven依赖项

[spring-cloud-starter-sleuth](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-sleuth/3.1.7)、[spring -cloud-sleuth-otel-autoconfigure](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-sleuth-otel-autoconfigure/1.1.2)和[opentelemetry-exporter-otlp-trace](https://central.sonatype.com/artifact/io.opentelemetry/opentelemetry-exporter-otlp-trace/1.14.0)依赖项将自动捕获跟踪并将其导出到任何支持的收集器。此外，我们还需要包含[grpc-okhttp](https://central.sonatype.com/artifact/io.grpc/grpc-okhttp/1.53.0)依赖项，Otel需要它来支持HTTP/2。

首先，我们将首先创建一个Spring Boot Web项目，并将以下Spring和OpenTelemetry依赖项包含到两个应用程序中：

```xml
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
</dependency>
```

我们应该注意，我们已经**排除了Spring Cloud Brave依赖项，以便用Otel替换默认的跟踪实现**。

此外，我们还需要包括[Spring Cloud Sleuth的Spring依赖管理BOM](https://www.baeldung.com/spring-maven-bom)：

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
</dependencyManagement>
```

### 3.2 实现下游应用程序

我们的下游应用程序将有一个端点来返回Price数据。

首先，让我们为Price类建模：

```java
public class Price {
    private long productId;
    private double priceAmount;
    private double discount;
}
```

接下来，让我们使用获取价格端点实现PriceController：

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
}
```

然后，我们将在PriceRepository中实现getPrice方法：

```java
public Price getPrice(Long productId){
    LOGGER.info("Getting Price from Price Repo With Product Id {}", productId);
    if(!priceMap.containsKey(productId)){
        LOGGER.error("Price Not Found for Product Id {}", productId);
        throw new PriceNotFoundException("Price Not Found");
    }
    return priceMap.get(productId);
}
```

### 3.3 实现上游应用程序

上游应用程序还将有一个端点来获取Product详细信息并与上面的获取价格端点集成。

首先，让我们实现Product类：

```java
public class Product {
    private long id;
    private String name;
    private Price price;
}
```

然后，让我们使用用于获取产品的端点来实现ProductController类：

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
}
```

接下来，我们将在ProductRepository中实现getProduct方法：

```java
public Product getProduct(Long productId){
    LOGGER.info("Getting Product from Product Repo With Product Id {}", productId);
    if(!productMap.containsKey(productId)){
        LOGGER.error("Product Not Found for Product Id {}", productId);
        throw new ProductNotFoundException("Product Not Found");
    }
    return productMap.get(productId);
}
```

最后，让我们在PriceClient中实现getPrice方法：

```java
public Price getPrice(@PathVariable("id") long productId){
    LOGGER.info("Fetching Price Details With Product Id {}", productId);
    String url = String.format("%s/price/%d", baseUrl, productId);
    ResponseEntity<Price> price = restTemplate.getForEntity(url, Price.class);
    return price.getBody();
}
```

## 4. 使用OpenTelemetry配置Spring Boot

OpenTelemetry提供了一个称为Otel收集器的收集器，它处理遥测数据并将其导出到任何可观察性后端，如Jaeger、[Prometheus](https://www.baeldung.com/spring-boot-self-hosted-monitoring)等。

可以使用一些Spring Sleuth配置将跟踪导出到Otel收集器。

### 4.1 配置Spring Sleuth

我们需要使用Otel端点配置应用程序以发送遥测数据。

让我们在application.properties中包含Spring Sleuth配置：

```properties
spring.sleuth.otel.config.trace-id-ratio-based=1.0
spring.sleuth.otel.exporter.otlp.endpoint=http://collector:4317
```

**trace-id-ratio-based属性定义了所收集跨度的采样率**。值1.0表示将导出所有跨度。

### 4.2 配置OpenTelemetry收集器

Otel收集器是OpenTelemetry跟踪的引擎。它由接收器、处理器和导出器组件组成。有一个可选的扩展组件可以帮助进行健康检查、服务发现或数据转发。扩展组件不涉及处理遥测数据。

为了快速启动Otel服务，我们将使用托管在端口14250的Jaeger后端端点。

让我们使用Otel管道阶段配置otel-config.yml：

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
            exporters:  [ logging, jaeger ]
```

我们应该注意，上面的processors配置是可选的，默认情况下是不启用的。processors batch选项有助于更好地压缩数据并减少传输数据所需的传出连接数。

另外，我们应该注意receiver配置了GRPC和HTTP协议。

## 5. 运行应用程序

我们现在将配置并运行整个设置、应用程序和Otel收集器。

### 5.1 在应用程序中配置Dockerfile

让我们为我们的产品服务实现Dockerfile：

```dockerfile
FROM adoptopenjdk/openjdk11:alpine
COPY target/spring-cloud-open-telemetry1-1.0.0.jar spring-cloud-open-telemetry.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/spring-cloud-open-telemetry.jar"]
```

我们应该注意到价格服务的Dockerfile本质上是相同的。

### 5.2 使用Docker Compose配置服务

现在，让我们使用整个设置配置[docker-compose.yml](https://www.baeldung.com/ops/docker-compose)：

```yaml
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
```

现在让我们通过docker-compose运行服务：

```shell
$ docker-compose up
```

### 5.3 验证正在运行的Docker服务

除了product-service和price-service，我们还在整个设置中添加了collector-service和jaeger-service。上面的product-service和price-service使用collector服务端口4317发送跟踪数据。collector服务反过来依赖jaeger-service端点将跟踪数据导出到Jaeger后端。

对于jaeger-service，我们使用jaegertracing/all-in-one镜像，其中包括其后端和UI组件。

让我们使用[docker container](https://www.baeldung.com/ops/docker-list-containers)命令验证服务的状态：

```shell
$ docker container ls --format "table {{.ID}}\t{{.Names}}\t{{.Status}}\t{{.Ports}}"
```

```shell
CONTAINER ID   NAMES                                           STATUS         PORTS
7b874b9ee2e6   spring-cloud-open-telemetry-collector-1         Up 5 minutes   0.0.0.0:4317->4317/tcp, 55678-55679/tcp
29ed09779f98   spring-cloud-open-telemetry-jaeger-service-1    Up 5 minutes   5775/udp, 5778/tcp, 6831-6832/udp, 14268/tcp, 0.0.0.0:16686->16686/tcp, 0.0.0.0:61686->14250/tcp
75bfbf6d3551   spring-cloud-open-telemetry-product-service-1   Up 5 minutes   0.0.0.0:8080->8080/tcp, 8081/tcp
d2ca1457b5ab   spring-cloud-open-telemetry-price-service-1     Up 5 minutes   0.0.0.0:61687->8081/tcp
```

## 6. 监控收集器中的痕迹

像Jaeger这样的遥测收集器工具提供前端应用程序来监视请求。我们可以实时或稍后查看请求跟踪。

让我们在请求成功和失败时监视跟踪。

### 6.1 请求成功时监控跟踪

首先，我们调用产品端点http://localhost:8080/product/100003。

该请求将使一些日志出现：

```shell
spring-cloud-open-telemetry-price-service-1 | 2023-01-06 19:03:03.985 INFO [price-service,825dad4a4a308e6f7c97171daf29041a,346a0590f545bbcf] 1 --- [nio-8081-exec-1] c.t.t.opentelemetry.PriceRepository : Getting Price from Price With Product Id 100003
spring-cloud-open-telemetry-product-service-1 | 2023-01-06 19:03:04.432 INFO [,825dad4a4a308e6f7c97171daf29041a,fb9c54565b028eb8] 1 --- [nio-8080-exec-1] c.t.t.opentelemetry.ProductRepository : Getting Product from Product Repo With Product Id 100003
spring-cloud-open-telemetry-collector-1 | Trace ID : 825dad4a4a308e6f7c97171daf29041a
```

Spring Sleuth将自动配置ProductService以将trace id附加到当前线程，并将其作为HTTP标头附加到下游API调用。PriceService还将自动在线程上下文和日志中包含相同的trace id。Otel服务将使用此trace id来确定跨服务的请求流。

正如预期的那样，上面的trace id ...f29041a在PriceService和ProductService日志中是相同的。

让我们在端口16686托管的Jaeger UI中可视化整个请求跨度时间线：

<img src="../assets/img.png">

上面显示了请求流的时间线，并包含表示请求的元数据。

### 6.2 请求失败时监视跟踪

假设下游服务引发异常，从而导致请求失败。

同样，我们将利用相同的UI来分析根本原因。

让我们使用产品端点/product/100005调用测试上述场景，其中下游应用程序中不存在产品。

现在，让我们可视化失败的请求跨度：

<img src="../assets/img_1.png">

如上所示，我们可以将请求追溯到错误起源的最终API调用。

## 7. 总结

在本文中，我们了解了OpenTelemetry如何帮助标准化微服务的可观察性模式。

我们还通过一个示例了解了如何使用OpenTelemetry配置Spring Boot应用程序。最后，我们在收集器中跟踪了一个API请求流。