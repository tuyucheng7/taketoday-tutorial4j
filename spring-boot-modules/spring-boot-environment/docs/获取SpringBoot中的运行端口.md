## 1. 概述

Spring Boot应用程序嵌入了一个Web服务器，有时，我们可能希望在运行时发现HTTP端口。

在本教程中，我们将介绍如何在Spring Boot应用程序中以编程方式获取HTTP端口。

## 2. 简介

### 2.1 我们的Spring Boot应用程序

我们将创建一个简单的Spring Boot应用程序示例，以快速演示在运行时发现HTTP端口的方法：

```java
@SpringBootApplication
public class GetServerPortApplication {
    public static void main(String[] args) {
        SpringApplication.run(GetServerPortApplication.class, args);
    }
}
```

### 2.2 两种设置端口的场景

通常，配置Spring Boot应用程序的[HTTP端口]()最直接的方法是在配置文件application.properties或application.yml中定义端口。

例如，在application.properties文件中，我们可以将7777设置为我们的应用程序运行的端口：

```properties
server.port=7777
```

或者，**我们可以通过将“0”设置为“server.port”属性的值来让Spring Boot应用程序在随机端口上运行，而不是定义固定端口**：

```properties
server.port=0
```

接下来，让我们通过这两种情况并讨论在运行时以编程方式获取端口的不同方法。在本教程中，我们将在单元测试中发现服务器端口。

## 3. 在运行时获取固定端口

让我们创建一个属性文件application-fixedport.properties并在其中定义一个固定端口7777：

```properties
server.port=7777
```

接下来，我们将尝试在单元测试类中获取端口：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GetServerPortApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("fixedport")
class GetServerFixedPortUnitTest {
    private final static int EXPECTED_PORT = 7777;
    // ....
}
```

在编写测试方法之前，让我们快速浏览一下测试类的注解：

-   @ExtendWith(SpringExtension.class)：这将使用Spring TestContext加入JUnit测试
-   @SpringBootTest( ... SpringBootTest.WebEnvironment.DEFINED_PORT)：在SpringBootTest中，我们将为嵌入式Web服务器使用DEFINED_PORT
-   @ActiveProfiles(“fixedport”)：通过这个注解，我们启用了[Spring Profile]() “fixedport”，这样我们的application-fixedport.properties就会被加载

### 3.1 使用@Value("${server.port}")注解

由于将加载application-fixedport.properties文件，我们可以使用[@Value注解]()“server.port”属性：

```java
@Value("${server.port}")
private int serverPort;

@Test
void givenFixedPortAsServerPort_whenReadServerPort_thenGetThePort() {
    assertEquals(EXPECTED_PORT, serverPort);
}
```

### 3.2 使用ServerProperties类

[ServerProperties](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/web/ServerProperties.html)保存嵌入式Web服务器的属性，例如端口、地址和服务器标头。

我们可以注入一个ServerProperties组件并从中获取端口：

```java
@Autowired
private ServerProperties serverProperties;

@Test
void givenFixedPortAsServerPort_whenReadServerProps_thenGetThePort() {
    int port = serverProperties.getPort();
 
    assertEquals(EXPECTED_PORT, port);
}
```

到目前为止，我们已经学习了两种在运行时获取固定端口的方法，接下来，让我们看看如何在随机端口场景中发现端口。

## 4. 运行时获取随机端口

这一次，让我们创建另一个属性文件application-randomport.properties：

```properties
server.port=0
```

如上面的代码所示，我们允许Spring Boot在Web服务器启动时随机选择一个空闲端口。

同样，让我们创建另一个单元测试类：

```java
....
@ActiveProfiles("randomport")
class GetServerRandomPortUnitTest {
...
}
```

在这里，我们需要激活“randomport” Spring Profile来加载相应的属性文件。

我们已经学习了两种在运行时发现固定端口的方法。但是，他们无法帮助我们获取随机端口：

```java
@Value("${server.port}")
private int randomServerPort;

@Test
void given0AsServerPort_whenReadServerPort_thenGet0() {
    assertEquals(0, randomServerPort);
}

@Autowired
private ServerProperties serverProperties;

@Test
void given0AsServerPort_whenReadServerProps_thenGet0() {
    int port = serverProperties.getPort();
 
    assertEquals(0, port);
}
```

正如这两种测试方法所示，**@Value("${server.port}")和serverProperties.getPort()都将“0”报告为端口**。显然，这不是我们期望的正确端口。

### 4.1 使用ServletWebServerApplicationContext

如果嵌入式Web服务器启动，Spring Boot会启动一个[ServletWebServerApplicationContext](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/servlet/context/ServletWebServerApplicationContext.html)。

因此，我们可以从上下文对象中获取[WebServer](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/server/WebServer.html)来获取服务器信息或操作服务器：

```java
@Autowired
private ServletWebServerApplicationContext webServerAppCtxt;

@Test
void given0AsServerPort_whenReadWebAppCtxt_thenGetThePort() {
    int port = webServerAppCtxt.getWebServer().getPort();
 
    assertTrue(port > 1023);
}
```

在上面的测试中，我们检查端口值是否大于1023，这是因为0-1023是系统端口。

### 4.2 处理ServletWebServerInitializedEvent

Spring应用程序可以发布各种[事件]()，[EventListeners]()处理这些事件。

当嵌入式Web服务器启动时，将发布[ServletWebServerInitializedEvent](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/servlet/context/ServletWebServerInitializedEvent.html)，此事件包含有关Web服务器的信息。

因此，我们可以创建一个EventListener来从这个事件中获取端口：

```java
@Service
public class ServerPortService {
    private int port;

    public int getPort() {
        return port;
    }

    @EventListener
    public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
        port = event.getWebServer().getPort();
    }
}
```

我们可以将服务组件注入到我们的测试类中以快速获取随机端口：

```java
@Autowired
private ServerPortService serverPortService;

@Test
void given0AsServerPort_whenReadFromListener_thenGetThePort() {
    int port = serverPortService.getPort();
 
    assertTrue(port > 1023);
}
```

## 5. 总结

通常，我们在属性文件或YAML文件中配置Spring Boot应用程序的服务器端口，我们可以在其中设置固定端口或随机端口。

在本文中，我们讨论了在运行时获取固定端口和随机端口的不同方法。