## 1. 概述

在运行应用程序之前，我们必须维护一份包含一长串手动步骤的文档的日子已经一去不复返了。使用Docker安装应用程序依赖项变得更加容易。但是，你仍然必须根据操作系统维护不同版本的脚本，以手动将应用程序依赖项作为Docker容器启动。

借助Spring Boot 3.1.0中添加的测试容器支持，现在开发人员可以简单地克隆仓库并运行应用程序。所有应用程序依赖项(如数据库、消息代理等)都可以配置为在我们运行应用程序时自动启动。

>   如果你不熟悉Testcontainers，那么请阅读[Spring Boot项目指南中的Testcontainers入门](https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/)，了解如何使用Testcontainers测试你的Spring Boot应用程序。

## 2. 使用ServiceConnections简化集成测试

在Spring Boot 3.1.0之前，我们必须使用@DynamicPropertySource设置从Testcontainers启动的容器中获取的动态属性，如下所示：

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CustomerControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    // your tests
}
```

从Spring Boot 3.1.0开始，出现了一个新的ServiceConnection概念，它会自动为支持的容器配置必要的Spring Boot属性。

首先让我们添加spring-boot-testcontainersas测试依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-testcontainers</artifactId>
    <scope>test</scope>
</dependency>
```

现在我们可以重写前面的示例，只需添加@ServiceConnection，而不必使用@DynamicPropertySource方法显式配置spring.datasource.url、spring.datasource.username和spring.datasource.password。

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CustomerControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    // your tests
}
```

请注意，我们不再显式注册数据源属性。

@ServiceConnection支持不仅适用于关系型数据库，还适用于许多其他常用的依赖项，如Kafka、RabbitMQ、Redis、MongoDB、ElasticSearch、Neo4j等。有关支持服务的完整列表，请参阅[官方文档](https://docs.spring.io/spring-boot/docs/3.1.0-SNAPSHOT/reference/htmlsingle/#features.testing.testcontainers.service-connections)。

你还可以在一个TestConfiguration类中定义所有容器依赖项，并将其导入集成测试中。

例如，假设你在应用程序中使用Postgres和Kafka。你可以创建一个名为ContainersConfig的类，如下所示：

```java
@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:15.2-alpine");
    }

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));
    }
}
```

然后你可以按如下方式在测试中导入ContainersConfig：

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ContainersConfig.class)
class ApplicationTests {
    // your tests ...
}
```

## 3. 如何使用不支持ServiceConnection的容器？

在你的应用程序中，你可能需要使用可能没有专用Testcontainers模块的依赖项，或者没有来自Spring Boot的开箱即用的ServiceConnection支持。别担心，你仍然可以使用Testcontainers GenericContainer并使用DynamicPropertyRegistry注册属性。

例如，你可能想使用Mailhog来测试电子邮件功能。然后你可以使用Testcontainers GenericContainer并注册Spring Boot电子邮件属性，如下所示：

```java
@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:15.2-alpine");
    }

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));
    }

    @Bean
    public GenericContainer mailhogContainer(DynamicPropertyRegistry registry) {
        GenericContainer container = new GenericContainer("mailhog/mailhog").withExposedPorts(1025);
        registry.add("spring.mail.host", container::getHost);
        registry.add("spring.mail.port", container::getFirstMappedPort);
        return container;
    }
}
```

正如我们所见，我们可以使用任何容器化服务并注册应用程序属性。

## 4. 使用Testcontainer进行本地开发

在上一节中，我们了解了如何使用Testcontainer来测试Spring Boot应用程序。借助Spring Boot 3.1.0 Testcontainers支持，我们还可以在开发期间使用Testcontainers在本地运行应用程序。

在src/test/java下的测试类路径中创建一个TestApplication类，如下所示：

```java
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication
                .from(Application::main) // Application is main entrypoint class
                .with(ContainersConfig.class)
                .run(args);
    }
}
```

请注意，我们使用.with(...)将配置类附加到应用程序启动器。

现在你可以从IDE运行TestApplication，它会自动启动ContainersConfig中定义的所有容器并自动配置属性。

你还可以使用Maven构建工具运行TestApplication，如下所示：

```shell
./mvnw spring-boot:test-run
```

### 4.1 在开发时将DevTools与测试容器一起使用

我们已经了解了如何使用Testcontainer进行本地开发，但此设置的一个挑战是每次修改应用程序并触发构建时，现有容器将被销毁并创建新容器。这可能会导致应用程序重新启动之间的速度变慢或数据丢失。

Spring Boot提供了开发工具，通过在代码更改时刷新应用程序来改善开发人员的体验。我们可以使用devtools提供的@RestartScope注解来指示要重用的某些bean，而不是重新创建它们。

首先，让我们添加spring-boot-devtools依赖，如下所示：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

然后在ContainersConfig中的bean定义上添加@RestartScope注解，如下所示：

```java
@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @ServiceConnection
    @RestartScope
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:15.2-alpine");
    }

    @Bean
    @ServiceConnection
    @RestartScope
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));
    }
    // ...
}
```

现在，如果你更改任何应用程序代码并触发构建(Eclipse会在保存代码更改时自动触发构建，而在Intellij IDEA中你需要手动触发构建)，应用程序将重新启动但会使用现有容器。

## 5. 总结

现代软件开发涉及使用大量技术和工具来解决复杂的业务需求，这增加了开发环境设置的复杂性。改善开发人员体验(DX)不再是“可有可无”，它已成为敏捷的“必需品”。

我们坚信，改善开发人员体验将有助于开发人员做正确的事情，从而大大提高整体生产力。

Spring Boot 3.1.0通过添加对测试容器的开箱即用支持改进了开发人员体验(DX)。Spring Boot和Testcontainers集成与本地Docker、CI和[Testcontainers Cloud](https://testcontainers.com/cloud/)无缝协作。

与往常一样，本教程的完整源代码可在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-boot-modules/spring-boot-3-container)上找到。