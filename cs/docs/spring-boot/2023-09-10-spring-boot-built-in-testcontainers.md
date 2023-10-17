---
layout: post
title:  Spring Boot中的内置测试容器支持
category: springboot
copyright: springboot
excerpt:Spring Boot3
---

## 1. 概述

在本教程中，我们将讨论Spring Boot 3.1中引入的增强的[Testcontainers](https://www.baeldung.com/spring-boot-testcontainers-integration-test)支持。

此更新提供了一种更简化的容器配置方法，它允许我们出于本地开发目的启动它们。因此，使用Testcontainers开发和运行测试成为一个无缝且高效的过程。

## 2.Spring Boot3.1之前的Testcontainers

我们可以使用Testcontainers在测试阶段创建类似生产的环境，通过这样做，我们能够消除对mock的需求，并编写不与实现细节耦合的高质量自动化测试。

对于本文中的代码示例，我们将使用一个简单的Web应用程序，其中包含MongoDB数据库作为持久层和一个小型REST接口：

```java
@RestController
@RequestMapping("characters")
public class MiddleEarthCharactersController {
    private final MiddleEarthCharactersRepository repository;

    // constructor not shown

    @GetMapping
    public List<MiddleEarthCharacter> findByRace(@RequestParam String race) {
        return repository.findAllByRace(race);
    }

    @PostMapping
    public MiddleEarthCharacter save(@RequestBody MiddleEarthCharacter character) {
        return repository.save(character);
    }
}
```

在集成测试期间，我们将启动一个包含数据库服务器的Docker容器。由于容器暴露的数据库端口将被动态分配，因此我们无法在属性文件中定义数据库URL。因此，对于3.1之前版本的Spring Boot应用程序，我们需要使用@DynamicPropertySource注解才能将这些属性添加到DynamicPropertyRegistry：

```java
@Testcontainers
@SpringBootTest(webEnvironment = DEFINED_PORT)
class DynamicPropertiesIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    // ...
}
```

对于集成测试，我们将使用@SpringBootTest注解在配置文件中定义的端口上启动应用程序。此外，我们将使用Testcontainers来设置环境。

最后，让我们使用[REST-Assured](https://www.baeldung.com/rest-assured-tutorial)来执行HTTP请求并断言响应的有效性：

```java
@Test
void whenRequestingHobbits_thenReturnFrodoAndSam() {
    repository.saveAll(List.of(
        new MiddleEarthCharacter("Frodo", "hobbit"),
        new MiddleEarthCharacter("Samwise", "hobbit"),
        new MiddleEarthCharacter("Aragon", "human"),
        new MiddleEarthCharacter("Gandalf", "wizzard")
    ));

    when().get("/characters?race=hobbit")
        .then().statusCode(200)
        .and().body("name", hasItems("Frodo", "Samwise"));
}
```

## 3. 使用@ServiceConnection作为动态属性

从Spring Boot 3.1开始，我们可以利用@ServiceConnection注解来消除定义动态属性的样板代码。

首先，我们需要在pom.xml中包含[spring-boot-testcontainers](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-testcontainers)依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-testcontainers</artifactId>
    <scope>test</scope>
</dependency>
```

之后，我们可以删除注册所有动态属性的静态方法。相反，我们只需使用@ServiceConnection标注容器：

```java
@Testcontainers
@SpringBootTest(webEnvironment = DEFINED_PORT)
class ServiceConnectionIntegrationTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    // ...
}
```

@ServiceConnection允许Spring Boot的自动配置动态注册所有需要的属性。在幕后，@ServiceConnection根据容器类或Docker镜像名称确定需要哪些属性。

所有支持该注解的容器和镜像的列表可以在Spring Boot的[官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing.testcontainers.service-connections)中找到。

## 4. Testcontainers对本地开发的支持

另一个令人兴奋的功能是以最少的配置将测试容器无缝集成到本地开发中，此功能使我们不仅可以在测试期间复制生产环境，而且还可以用于本地开发。

为了启用它，我们首先需要创建一个@TestConfiguration并将所有Testcontainers声明为Spring Bean。我们还添加@ServiceConnection注解，它将应用程序无缝绑定到数据库：

```java
@TestConfiguration(proxyBeanMethods = false)
class LocalDevTestcontainersConfig {
    @Bean
    @ServiceConnection
    public MongoDBContainer mongoDBContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));
    }
}
```

由于所有Testcontainers依赖项都是通过测试范围导入的，因此我们需要从测试包启动应用程序。因此，让我们在这个包中创建一个main()方法，该方法从主包中调用实际的main()方法：

```java
public class LocalDevApplication {
    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                .with(LocalDevTestcontainersConfig.class)
                .run(args);
    }
}
```

就是这样，现在我们可以从这个main()方法本地启动应用程序，它将使用MongoDB数据库。

让我们从Postman发送一个POST请求，然后直接连接到数据库并检查数据是否正确持久化：

[![img](https://www.baeldung.com/wp-content/uploads/2023/08/postman_post_data-300x94.png)](https://www.baeldung.com/wp-content/uploads/2023/08/postman_post_data.png)

为了连接到数据库，我们需要找到容器公开的端口。我们可以从应用程序日志中获取它，或者只需运行docker ps命令即可：

[![img](https://www.baeldung.com/wp-content/uploads/2023/08/visual-diff-on-changed-files-300x60.png)](https://www.baeldung.com/wp-content/uploads/2023/08/visual-diff-on-changed-files.png)

最后，我们可以使用MongoDB客户端通过URL mongodb://localhost:63437/test连接数据库，并查询字符集合：

[![img](https://www.baeldung.com/wp-content/uploads/2023/08/mongodb_find_all-300x153.png)](https://www.baeldung.com/wp-content/uploads/2023/08/mongodb_find_all.png)

就这样，我们可以连接并查询Testcontainers启动的数据库进行本地开发。

## 5. 与DevTools和@RestartScope集成

如果我们在本地开发期间经常重新启动应用程序，则潜在的缺点是每次都会重新启动所有容器。因此，启动速度可能会变慢，并且测试数据将会丢失。

但是，通过利用Testcontainers与spring-boot-devtools的集成，我们可以在应用程序关闭时使容器保持活动状态。这是一个实验性的Testcontainers功能，可实现更流畅、更高效的开发体验，因为它节省了宝贵的时间和测试数据。

让我们首先添加[spring-boot-devtools依赖项](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-devtools)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

现在，我们可以返回到本地开发的测试配置，并使用@RestartScope注解来标注Testcontainers bean：

```java
@Bean
@RestartScope
@ServiceConnection
public MongoDBContainer mongoDBContainer() {
    return new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));
}
```

或者，我们可以使用Testcontainers API的withReuse(true)方法：

```java
@Bean
@ServiceConnection
public MongoDBContainer mongoDBContainer() {
    return new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))
        .withReuse(true);
}
```

因此，我们现在可以从测试包的main()方法启动应用程序，并利用spring-boot-devtools实时重新加载功能。例如，我们可以保存来自Postman的条目，然后重新编译并重新加载应用程序：

[![img](https://www.baeldung.com/wp-content/uploads/2023/08/postmna-save-again-1-300x95.png)](https://www.baeldung.com/wp-content/uploads/2023/08/postmna-save-again-1.png)

让我们进行一些小的更改，例如将请求映射从“characters”切换到“api/characters”并重新编译：

[![img](https://www.baeldung.com/wp-content/uploads/2023/08/devtools_ive_reload-300x187.png)](https://www.baeldung.com/wp-content/uploads/2023/08/devtools_ive_reload.png)

我们已经可以从应用程序日志或Docker本身看到数据库容器没有重新启动。不过，让我们更进一步，检查应用程序在重新启动后是否重新连接到同一个数据库。例如，我们可以通过在新路径发送GET请求并期望之前插入的数据在那里来实现这一点：

[![img](https://www.baeldung.com/wp-content/uploads/2023/08/app_reconnected_to_db-1-300x178.png)](https://www.baeldung.com/wp-content/uploads/2023/08/app_reconnected_to_db-1.png)

## 6. 总结

在本文中，我们讨论了Spring Boot 3.1的新Testcontainers功能。我们学习了如何使用新的@ServiceConnection注解，该注解提供了使用@DynamicPropertySource和样板配置的简化替代方案。

接下来，我们通过在测试包中创建一个额外的main()方法并将它们声明为Spring bean，深入研究如何利用Testcontainers进行本地开发。除此之外，与spring-boot-devtools和@RestartScope的集成使我们能够为本地开发创建一个快速、一致且可靠的环境。

与往常一样，示例代码可以在[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-boot-modules/spring-boot-3-testcontainers)上找到。