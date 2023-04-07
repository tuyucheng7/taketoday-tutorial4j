## 1. 概述

在本文中，我们将把Zipkin添加到我们的[Spring Cloud项目](https://www.baeldung.com/spring-cloud-securing-services)中。Zipkin是一个开源项目，它提供发送、接收、存储和可视化跟踪的机制。这使我们能够关联服务器之间的活动，并更清楚地了解我们的服务中到底发生了什么。

本文不是分布式跟踪或Spring Cloud的介绍性文章。如果你想了解有关分布式跟踪的更多信息，请阅读我们对[Spring Sleuth](https://www.baeldung.com/spring-cloud-sleuth-single-application)的介绍。

## 2. Zipkin服务

我们的Zipkin服务将作为我们所有跨度的商店。每个跨度都被发送到此服务并收集到跟踪中以供将来识别。

### 2.1 设置

创建一个新的Spring Boot项目并将这些依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>io.zipkin.java</groupId>
    <artifactId>zipkin-server</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.java</groupId>
    <artifactId>zipkin-autoconfigure-ui</artifactId>
    <scope>runtime</scope>
</dependency>
```

作为参考：你可以在Maven Central上找到最新版本([zipkin-server](https://search.maven.org/search?q=a:zipkin-server)、[zipkin-autoconfigure-ui](https://search.maven.org/search?q=a:zipkin-autoconfigure-ui))。依赖项的版本继承自[spring-boot-starter-parent](https://search.maven.org/search?q=a:spring-boot-starter-parent)。

### 2.2 启用Zipkin服务器

要启用Zipkin服务器，我们必须在主应用程序类中添加一些注解：

```java
@SpringBootApplication
@EnableZipkinServer
public class ZipkinApplication {
    // ...
}
```

新注解@EnableZipkinServer将设置此服务器以监听传入的跨度并充当我们的UI进行查询。

### 2.3 配置

首先，让我们在src/main/resources中创建一个名为bootstrap.properties的文件。请记住，需要此文件才能从配置服务器获取我们的配置。

让我们向其中添加这些属性：

```properties
spring.cloud.config.name=zipkin
spring.cloud.config.discovery.service-id=config
spring.cloud.config.discovery.enabled=true
spring.cloud.config.username=configUser
spring.cloud.config.password=configPassword

eureka.client.serviceUrl.defaultZone=http://discUser:discPassword@localhost:8082/eureka/
```

现在让我们添加一个配置文件到我们的配置仓库，在Windows上位于c:\Users\{username}\或在*nix上位于/home/{username}/。

在这个目录中，让我们添加一个名为zipkin.properties的文件并添加以下内容：

```properties
spring.application.name=zipkin
server.port=9411
eureka.client.region=default
eureka.client.registryFetchIntervalSeconds=5
logging.level.org.springframework.web=debug
```

请记住提交此目录中的更改，以便配置服务检测更改并加载文件。

### 2.4 运行

现在让我们运行我们的应用程序并导航到http://localhost:9411。我们应该看到Zipkin的主页：

[![zipkin主页1](https://www.baeldung.com/wp-content/uploads/2017/03/zipkinhomepage-1-1-300x92.png)](https://www.baeldung.com/wp-content/uploads/2017/03/zipkinhomepage-1-1.png)

伟大的！现在我们已准备好向我们要跟踪的服务添加一些依赖项和配置。

## 3. 服务配置

资源服务器的设置几乎相同。在接下来的部分中，我们将详细介绍如何设置图书服务。我们将通过解释将这些更新应用于rating-service和gateway-service所需的修改来跟进。

### 3.1 设置

要开始将span发送到我们的Zipkin服务器，我们会将此依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```

作为参考：你可以在MavenCentral([spring-cloud-starter-zipkin](https://search.maven.org/search?q=a:spring-cloud-starter-zipkin))上找到最新版本。

### 3.2 Spring配置

我们需要添加一些配置，以便book-service将使用Eureka找到我们的Zipkin服务。打开BookServiceApplication.java并将此代码添加到文件中：

```java
@Autowired
private EurekaClient eurekaClient;
 
@Autowired
private SpanMetricReporter spanMetricReporter;
 
@Autowired
private ZipkinProperties zipkinProperties;
 
@Value("${spring.sleuth.web.skipPattern}")
private String skipPattern;

// ... the main method goes here

@Bean
public ZipkinSpanReporter makeZipkinSpanReporter() {
    return new ZipkinSpanReporter() {
        private HttpZipkinSpanReporter delegate;
        private String baseUrl;

        @Override
        public void report(Span span) {
 
            InstanceInfo instance = eurekaClient.getNextServerFromEureka("zipkin", false);
            if (!(baseUrl != null && instance.getHomePageUrl().equals(baseUrl))) {
                baseUrl = instance.getHomePageUrl();
                delegate = new HttpZipkinSpanReporter(new RestTemplate(), 
                    baseUrl, zipkinProperties.getFlushInterval(), spanMetricReporter);
                if (!span.name.matches(skipPattern)) delegate.report(span);
            }
        }
    };
}
```

上面的配置注册了一个自定义的ZipkinSpanReporter，它从eureka获取它的URL。此代码还会跟踪现有的URL，并且仅在URL更改时更新HttpZipkinSpanReporter。这样无论我们将Zipkin服务器部署到哪里，我们都可以在不重启服务的情况下找到它。

我们还导入由springboot加载的默认Zipkin属性，并使用它们来管理我们的自定义报告程序。

### 3.3 配置

现在让我们向配置存储库中的book-service.properties文件添加一些配置：

```properties
spring.sleuth.sampler.percentage=1.0
spring.sleuth.web.skipPattern=(^cleanup.*)
```

Zipkin通过对服务器上的操作进行采样来工作。通过将spring.sleuth.sampler.percentage设置为1.0，我们将采样率设置为100%。跳过模式只是一个正则表达式，用于排除名称匹配的跨度。

跳过模式将阻止所有以“cleanup”一词开头的跨度被报告。这是为了停止源自spring会话代码库的跨度。

### 3.4 评级服务

按照上面图书服务部分的相同步骤，将更改应用于评级服务的等效文件。

### 3.5 网关服务

按照相同的步骤预订服务。但是，当将配置添加到网关.properties时，请改为添加以下内容：

```properties
spring.sleuth.sampler.percentage=1.0
spring.sleuth.web.skipPattern=(^cleanup.*|.+favicon.*)
```

这将配置网关服务不发送关于图标或spring会话的跨度。

### 3.6 运行

如果你还没有这样做，请启动config、discovery、gateway、book、rating和zipkin服务。

导航到http://localhost:8080/book-service/books。

打开一个新选项卡并导航到http://localhost:9411。选择预订服务并按下“Find Traces”按钮。你应该会在搜索结果中看到一条轨迹。点击那个打开的痕迹：

[![zipkintrace1](https://www.baeldung.com/wp-content/uploads/2017/03/zipkintrace-1-1-300x32.png)](https://www.baeldung.com/wp-content/uploads/2017/03/zipkintrace-1-1.png)

在跟踪页面上，我们可以看到按服务细分的请求。前两个跨度由网关创建，最后一个跨度由图书服务创建。这向我们展示了请求在图书服务(18.379毫秒)和网关(87.961毫秒)上的处理时间。

## 4. 总结

我们已经看到将Zipkin集成到我们的云应用程序中是多么容易。

这使我们对通信如何通过我们的应用程序进行了一些急需的了解。随着我们的应用程序变得越来越复杂，Zipkin可以为我们提供有关请求花费时间的急需信息。这可以帮助我们确定哪些地方速度变慢，并指出我们的应用程序的哪些方面需要改进。