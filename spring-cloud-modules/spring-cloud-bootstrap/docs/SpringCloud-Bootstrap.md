## 1. 概述

Spring Cloud是一个用于构建健壮的云应用程序的框架。该框架通过为迁移到分布式环境时面临的许多常见问题提供解决方案来促进应用程序的开发。

使用微服务架构运行的应用程序旨在简化开发、部署和维护。应用程序的分解性质允许开发人员一次专注于一个问题。可以在不影响系统其他部分的情况下引入改进。

另一方面，当我们采用微服务方法时，会出现不同的挑战：

- 外部化配置，使其灵活且不需要在更改时重新构建服务
- 服务发现
- 隐藏部署在不同主机上的服务的复杂性

在本文中，我们将构建五个微服务：配置服务器、发现服务器、网关服务器、图书服务，最后是评级服务。这五个微服务构成了一个坚实的基础应用程序，用于开始云开发并解决上述挑战。

## 2. 配置服务器

在开发云应用程序时，一个问题是维护配置并将其分发到我们的服务。我们真的不想在水平扩展我们的服务之前花时间配置每个环境，或者通过将我们的配置烘焙到我们的应用程序来冒安全漏洞的风险。

为了解决这个问题，我们将把我们所有的配置整合到一个单一的Git仓库中，并将其连接到一个管理我们所有应用程序配置的应用程序。我们将设置一个非常简单的实现。

要了解更多详细信息并查看更复杂的示例，请查看我们的[Spring Cloud配置](https://www.baeldung.com/spring-cloud-configuration)文章。

### 2.1 设置

导航到[https://start.spring.io](https://start.spring.io/)并选择Maven和Spring Boot 2.2.x。

将artifact设置为“config”。在dependencies部分，搜索“config server”并添加该模块。然后点击generate按钮，我们就可以下载一个zip文件，其中包含一个预配置的项目，可以开始使用了。

或者，我们可以生成一个Spring Boot项目并手动将一些依赖项添加到POM文件中。

这些依赖关系将在所有项目之间共享：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.0</version>
    <relativePath/>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies> 

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Hoxton.SR4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

让我们为配置服务器添加一个依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

作为参考，我们可以在Maven Central上找到最新版本([spring-cloud-dependencies](https://search.maven.org/search?q=a:spring-cloud-dependencies)、[test](https://search.maven.org/search?q=a:spring-boot-starter-test)、[config-server](https://search.maven.org/search?q=a:spring-cloud-config-server))。

### 2.2 Spring配置

要启用配置服务器，我们必须在主应用程序类中添加一些注解：

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigApplication {
    // ...
}
```

@EnableConfigServer会将我们的应用程序变成配置服务器。

### 2.3 属性

让我们在src/main/resources添加application.properties：

```properties
server.port=8081
spring.application.name=config

spring.cloud.config.server.git.uri=file://${user.home}/application-config
```

配置服务器最重要的设置是git.uri参数。这当前设置为相对文件路径，通常在Windows上解析为c:\Users\{username}\或在*nix上解析为/Users/{username}/。此属性指向一个Git仓库，其中存储了所有其他应用程序的属性文件。如有必要，可以将其设置为绝对文件路径。

> 提示：在Windows机器上，值以“file:///”开头，在*nix上则使用“file://”。

### 2.4 Git仓库

导航到spring.cloud.config.server.git.uri定义的文件夹并添加文件夹application-config。cd进入该文件夹并输入git init。这将初始化一个Git仓库，我们可以在其中存储文件并跟踪它们的更改。

### 2.5 运行

让我们运行配置服务器并确保它正常工作。从命令行输入mvn spring-boot:run。这将启动服务器。

我们应该看到此输出，表明服务器正在运行：

```shell
Tomcat started on port(s): 8081 (http)
```

### 2.6 引导配置

在我们后续的服务器中，我们希望它们的应用程序属性由这个配置服务器管理。为此，我们实际上需要做一些先有鸡还是先有蛋的操作：**在每个应用程序中配置知道如何与该服务器通信的属性**。

这是一个引导过程，**这些应用程序中的每一个都将有一个名为bootstrap.properties的文件**。它将包含类似于application.properties的属性，但有一点不同：

父Spring ApplicationContext**首先加载bootstrap.properties**。这一点至关重要，这样配置服务器才能开始管理application.properties中的属性。正是这个特殊的ApplicationContext也将解密任何加密的应用程序属性。

**保持这些属性文件不同是明智的**。bootstrap.properties用于准备好配置服务器，而application.properties用于特定于我们的应用程序的属性。不过，从技术上讲，可以将应用程序属性放在bootstrap.properties中。

最后，由于配置服务器正在管理我们的应用程序属性，人们可能想知道为什么要有一个application.properties？答案是这些仍然作为配置服务器可能没有的默认值派上用场。

## 3. 发现

现在我们已经完成了配置，我们需要一种方法让我们所有的服务器能够找到彼此。我们将通过设置Eureka发现服务器来解决这个问题。由于我们的应用程序可以在任何ip/端口组合上运行，因此我们需要一个可以用作应用程序地址查找的中央地址注册表。

当提供新服务器时，它将与发现服务器通信并注册其地址，以便其他人可以与其通信。这样，其他应用程序可以在发出请求时使用此信息。

要了解更多详细信息并查看更复杂的发现实现，请查看[Spring Cloud Eureka文章](https://www.baeldung.com/spring-cloud-netflix-eureka)。

### 3.1 设置

我们将再次导航到[start.spring.io](https://start.spring.io/)。将artifact设置为“discovery”。搜索“eureka server”并添加该依赖项。搜索“config client”并添加该依赖项。最后，生成项目。

或者，我们可以创建一个Spring Boot项目，从配置服务器复制POM的内容并交换这些依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka-server</artifactId>
</dependency>
```

作为参考，我们将在Maven Central([config-client](https://search.maven.org/search?q=a:spring-cloud-starter-config)、[eureka-server](https://search.maven.org/search?q=a:spring-cloud-starter-eureka-server))上找到这些包。

### 3.2 Spring配置

让我们将Java配置添加到主类：

```java
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryApplication {
    // ...
}
```

@EnableEurekaServer将此服务器配置为使用Netflix Eureka的发现服务器。Spring Boot会自动检测类路径上的配置依赖，并从配置服务器中查找配置。

### 3.3 属性

现在我们将添加两个属性文件：

首先，我们将bootstrap.properties添加到src/main/resources中：

```properties
spring.cloud.config.name=discovery
spring.cloud.config.uri=http://localhost:8081
```

这些属性将允许发现服务器在启动时查询配置服务器。

其次，我们将discovery.properties添加到我们的Git仓库中。

```properties
spring.application.name=discovery
server.port=8082

eureka.instance.hostname=localhost

eureka.client.serviceUrl.defaultZone=http://localhost:8082/eureka/
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

文件名必须与spring.application.name属性匹配。

此外，我们告诉该服务器它正在默认时区中运行，这与配置客户端的时区设置相匹配。我们还告诉服务器不要注册到另一个发现实例。

在生产中，我们会有多个这样的设置以在发生故障时提供冗余，并且该设置是正确的。

让我们将文件提交到Git仓库。否则，将无法检测到该文件。

### 3.4 为配置服务器添加依赖

将此依赖项添加到配置服务器POM文件：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

作为参考，我们可以在Maven Central([eureka-client](https://search.maven.org/search?q=a:spring-cloud-starter-eureka))上找到该包。

将这些属性添加到配置服务器的src/main/resources中的application.properties文件：

```properties
eureka.client.region=default
eureka.client.registryFetchIntervalSeconds=5
eureka.client.serviceUrl.defaultZone=http://localhost:8082/eureka/
```

### 3.5 运行

使用相同的命令mvn spring-boot:run启动发现服务器。命令行的输出应包括：

```shell
Fetching config from server at: http://localhost:8081
...
Tomcat started on port(s): 8082 (http)
```

停止并重新运行配置服务。如果一切正常，输出应该如下所示：

```shell
DiscoveryClient_CONFIG/10.1.10.235:config:8081: registering service...
Tomcat started on port(s): 8081 (http)
DiscoveryClient_CONFIG/10.1.10.235:config:8081 - registration status: 204
```

## 4. 网关

现在我们已经解决了配置和发现问题，我们仍然遇到客户端访问我们所有应用程序的问题。

如果我们将所有内容都保留在分布式系统中，那么我们将不得不管理复杂的CORS标头以允许客户端上的跨域请求。我们可以通过创建网关服务器来解决这个问题。这将充当反向代理，将请求从客户端传送到我们的后端服务器。

网关服务器是微服务架构中的一个优秀应用程序，因为它允许所有响应都来自单个主机。这将消除对CORS的需求，并为我们提供一个方便的位置来处理身份验证等常见问题。

### 4.1 设置

导航到[https://start.spring.io](https://start.spring.io/)。将artifact设置为“gateway”。搜索“zuul”并添加该依赖项。搜索“config client”并添加该依赖项。搜索“eureka discovery”并添加该依赖项。最后，生成该项目。

或者，我们可以创建一个具有以下依赖项的Spring Boot应用程序：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zuul</artifactId>
</dependency>
```

作为参考，我们可以在Maven Central([config-client](https://search.maven.org/search?q=a:spring-cloud-starter-config)、[eureka-client](https://search.maven.org/search?q=a:spring-cloud-starter-eureka)、[zuul](https://search.maven.org/search?q=a:spring-cloud-starter-zuul))上找到捆绑包。

### 4.2 Spring配置

让我们将配置添加到主类：

```java
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
@EnableFeignClients
public class GatewayApplication {
    // ...
}
```

### 4.3 属性

现在我们将添加两个属性文件：

src/main/resources中的bootstrap.properties：

```properties
spring.cloud.config.name=gateway
spring.cloud.config.discovery.service-id=config
spring.cloud.config.discovery.enabled=true

eureka.client.serviceUrl.defaultZone=http://localhost:8082/eureka/
```

我们的Git仓库中的gateway.properties

```properties
spring.application.name=gateway
server.port=8080

eureka.client.region = default
eureka.client.registryFetchIntervalSeconds = 5

zuul.routes.book-service.path=/book-service/**
zuul.routes.book-service.sensitive-headers=Set-Cookie,Authorization
hystrix.command.book-service.execution.isolation.thread.timeoutInMilliseconds=600000

zuul.routes.rating-service.path=/rating-service/**
zuul.routes.rating-service.sensitive-headers=Set-Cookie,Authorization
hystrix.command.rating-service.execution.isolation.thread.timeoutInMilliseconds=600000

zuul.routes.discovery.path=/discovery/**
zuul.routes.discovery.sensitive-headers=Set-Cookie,Authorization
zuul.routes.discovery.url=http://localhost:8082
hystrix.command.discovery.execution.isolation.thread.timeoutInMilliseconds=600000
```

zuul.routes属性允许我们定义一个应用程序来根据ant URL匹配器路由某些请求。我们的属性告诉Zuul将来自/book-service/**的任何请求路由到spring.application.name为book-service的应用程序。然后Zuul将使用应用程序名称从发现服务器查找主机并将请求转发到该服务器。

请记住提交仓库中的更改！

### 4.4 运行

运行配置和发现应用程序并等待配置应用程序注册到发现服务器。如果它们已经在运行，我们不必重新启动它们。完成后，运行网关服务器。网关服务器应在端口8080上启动并向发现服务器注册自己。控制台的输出应包含：

```shell
Fetching config from server at: http://10.1.10.235:8081/
...
DiscoveryClient_GATEWAY/10.1.10.235:gateway:8080: registering service...
DiscoveryClient_GATEWAY/10.1.10.235:gateway:8080 - registration status: 204
Tomcat started on port(s): 8080 (http)
```

一个容易犯的错误是在配置服务器向Eureka注册之前启动服务器。在这种情况下，我们将看到包含以下输出的日志：

```shell
Fetching config from server at: http://localhost:8888
```

这是配置服务器的默认URL和端口，表明我们的发现服务在发出配置请求时没有地址。只需等待几秒钟，然后重试，一旦配置服务器注册到Eureka，问题就会解决。

## 5. 图书服务

在微服务架构中，我们可以自由地创建尽可能多的应用程序来满足业务目标。通常工程师会按领域划分他们的服务。我们将遵循这种模式并创建一个图书服务来处理我们应用程序中图书的所有操作。

### 5.1 设置

再一次。导航到[https://start.spring.io](https://start.spring.io/)。将artifact设置为“book-service”。搜索“web”并添加该依赖项。搜索“config client”并添加该依赖项。搜索“eureka discovery”并添加该依赖项。生成该项目。

或者，将这些依赖项添加到项目中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

作为参考，我们可以在Maven Central([config-client](https://search.maven.org/search?q=a:spring-cloud-starter-config)、[eureka-client](https://search.maven.org/search?q=a:spring-cloud-starter-eureka)、[web](https://search.maven.org/search?q=a:spring-boot-starter-web))上找到捆绑包。

### 5.2 Spring配置

让我们修改我们的主类：

```java
@SpringBootApplication
@EnableEurekaClient
@RestController
@RequestMapping("/books")
public class BookServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }

    private List<Book> bookList = Arrays.asList(
          new Book(1L, "Tuyucheng goes to the market", "Tim Schimandle"),
          new Book(2L, "Tuyucheng goes to the park", "Slavisa")
    );

    @GetMapping("")
    public List<Book> findAllBooks() {
        return bookList;
    }

    @GetMapping("/{bookId}")
    public Book findBook(@PathVariable Long bookId) {
        return bookList.stream().filter(b -> b.getId().equals(bookId)).findFirst().orElse(null);
    }
}
```

我们还添加了一个REST控制器和一个由我们的属性文件设置的字段，以返回我们将在配置期间设置的值。

现在让我们添加Book POJO：

```java
public class Book {
    private Long id;
    private String author;
    private String title;

    // standard getters and setters
}
```

### 5.3 属性

现在我们只需要添加两个属性文件：

src/main/resources中的bootstrap.properties：

```properties
spring.cloud.config.name=book-service
spring.cloud.config.discovery.service-id=config
spring.cloud.config.discovery.enabled=true

eureka.client.serviceUrl.defaultZone=http://localhost:8082/eureka/
```

我们的Git仓库中的book-service.properties：

```properties
spring.application.name=book-service
server.port=8083

eureka.client.region = default
eureka.client.registryFetchIntervalSeconds = 5
eureka.client.serviceUrl.defaultZone=http://localhost:8082/eureka/
```

让我们将更改提交到仓库。

### 5.4 运行

一旦所有其他应用程序都启动了，我们就可以启动图书服务了。控制台输出应如下所示：

```shell
DiscoveryClient_BOOK-SERVICE/10.1.10.235:book-service:8083: registering service...
DiscoveryClient_BOOK-SERVICE/10.1.10.235:book-service:8083 - registration status: 204
Tomcat started on port(s): 8083 (http)
```

一旦启动，我们就可以使用浏览器访问我们刚刚创建的端点。导航到http://localhost:8080/book-service/books，我们得到一个JSON对象，其中包含我们在out控制器中添加的两本书。请注意，我们不是直接在端口8083上访问图书服务，而是通过网关服务器。

## 6. 评级服务

与我们的图书服务一样，我们的评级服务将是一个领域驱动的服务，它将处理与评级相关的操作。

### 6.1 设置

再一次。导航到[https://start.spring.io](https://start.spring.io/)。将artifact设置为“rating-service”。搜索“web”并添加该依赖项。搜索“config client”并添加该依赖项。搜索“eureka discovery”并添加该依赖项。然后，生成该项目。

或者，将这些依赖项添加到项目中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

作为参考，我们可以在Maven Central([config-client](https://search.maven.org/search?q=a:spring-cloud-starter-config)、[eureka-client](https://search.maven.org/search?q=a:spring-cloud-starter-eureka)、[web](https://search.maven.org/search?q=a:spring-boot-starter-web))上找到捆绑包。

### 6.2 Spring配置

让我们修改我们的主类：

```java
@SpringBootApplication
@EnableEurekaClient
@RestController
@RequestMapping("/ratings")
public class RatingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RatingServiceApplication.class, args);
    }

    private List<Rating> ratingList = Arrays.asList(
          new Rating(1L, 1L, 2),
          new Rating(2L, 1L, 3),
          new Rating(3L, 2L, 4),
          new Rating(4L, 2L, 5)
    );

    @GetMapping("")
    public List<Rating> findRatingsByBookId(@RequestParam Long bookId) {
        return bookId == null || bookId.equals(0L) ? Collections.EMPTY_LIST : ratingList.stream().filter(r -> r.getBookId().equals(bookId)).collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<Rating> findAllRatings() {
        return ratingList;
    }
}
```

我们还添加了一个REST控制器和一个由我们的属性文件设置的字段，以返回我们将在配置期间设置的值。

让我们添加Rating POJO：

```java
public class Rating {
    private Long id;
    private Long bookId;
    private int stars;

    // standard getters and setters
}
```

### 6.3 属性

现在我们只需要添加两个属性文件：

src/main/resources中的bootstrap.properties：

```properties
spring.cloud.config.name=rating-service
spring.cloud.config.discovery.service-id=config
spring.cloud.config.discovery.enabled=true

eureka.client.serviceUrl.defaultZone=http://localhost:8082/eureka/
```

我们的Git仓库中的rating-service.properties：

```properties
spring.application.name=rating-service
server.port=8084

eureka.client.region = default
eureka.client.registryFetchIntervalSeconds = 5
eureka.client.serviceUrl.defaultZone=http://localhost:8082/eureka/
```

让我们将更改提交到仓库。

### 6.4 运行

一旦所有其他应用程序都启动了，我们就可以启动评级服务。控制台输出应如下所示：

```shell
DiscoveryClient_RATING-SERVICE/10.1.10.235:rating-service:8083: registering service...
DiscoveryClient_RATING-SERVICE/10.1.10.235:rating-service:8083 - registration status: 204
Tomcat started on port(s): 8084 (http)
```

一旦启动，我们就可以使用浏览器访问我们刚刚创建的端点。导航到[http://localhost:8080/rating-service/ratings/all](http://localhost:8080/rating-service/ratings/all)，我们将返回包含所有评级的JSON。请注意，我们不是直接在端口8084上访问评级服务，而是通过网关服务器。

## 7. 总结

现在我们能够将Spring Cloud的各个部分连接到一个功能正常的微服务应用程序中，这构成了我们可以用来开始构建更复杂应用程序的基础。