## 1. 简介

在本文中，我们将熟悉 Zookeeper 以及它如何用于服务发现，服务发现用作有关云中服务的集中知识。

Spring Cloud Zookeeper通过自动配置和绑定到 Spring 环境为Spring Boot应用程序提供[Apache Zookeeper集成。](https://zookeeper.apache.org/)

## 2.服务发现设置

我们将创建两个应用程序：

-   将提供服务的应用程序(在本文中称为Service Provider )
-   将使用此服务的应用程序(称为服务消费者)

Apache Zookeeper 将在我们的服务发现设置中充当协调员。Apache Zookeeper 安装说明可从以下[链接](https://zookeeper.apache.org/doc/current/zookeeperStarted.html)获得。

## 3. 服务商注册

我们将通过添加spring-cloud-starter-zookeeper-discovery依赖项并在主应用程序中使用注解@EnableDiscoveryClient来启用服务注册。

下面，我们将逐步展示返回“Hello World!”的服务的这个过程。在对 GET 请求的响应中。

### 3.1. Maven 依赖项

首先，让我们将所需的[spring-cloud-starter-zookeeper-discovery](https://search.maven.org/classic/#search|ga|1|spring-cloud-starter-zookeeper-discovery)、[spring-web](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework" AND a%3A"spring-web")、[spring-cloud-dependencies](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.cloud"  AND a%3A"spring-cloud-dependencies")和[spring-boot-starter](https://search.maven.org/classic/#search|ga|1|a%3A"spring-boot-starter" AND g%3A"org.springframework.boot")依赖项添加到我们的pom.xml文件中：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter</artifactId>
	<version>2.2.6.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
	<artifactId>spring-web</artifactId>
        <version>5.1.14.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
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
```

### 3.2. 服务提供者注解

接下来，我们将使用@EnableDiscoveryClient注解我们的主类。这将使HelloWorld应用程序具有发现意识：

```java
@SpringBootApplication
@EnableDiscoveryClient
public class HelloWorldApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }
}
```

和一个简单的控制器：

```java
@GetMapping("/helloworld")
public String helloWorld() {
    return "Hello World!";
}
```

### 3.3. YAML 配置

现在让我们创建一个 YAML Application.yml文件，该文件将用于配置应用程序日志级别并通知 Zookeeper 该应用程序已启用发现。

注册到 Zookeeper 的应用程序的名称是最重要的。稍后在服务消费者中，假客户端将在服务发现期间使用此名称：

```xml
spring:
  application:
    name: HelloWorld
  cloud:
    zookeeper:
      discovery:
        enabled: true
logging:
  level:
    org.apache.zookeeper.ClientCnxn: WARN
```

spring boot应用在默认端口2181寻找zookeeper，如果zookeeper在其他地方，需要添加配置：

```xml
spring:
  cloud:
    zookeeper:
      connect-string: localhost:2181
```

## 4. 服务消费者

现在我们将创建一个 REST 服务消费者并使用 Spring Netflix Feign Client 注册它。

### 4.1. Maven 依赖

首先，让我们将所需的[spring-cloud-starter-zookeeper-discovery](https://search.maven.org/classic/#search|ga|1|spring-cloud-starter-zookeeper-discovery)、[spring-web](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework" AND a%3A"spring-web")、[spring-cloud-dependencies](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.cloud"  AND a%3A"spring-cloud-dependencies")、[spring-boot-starter-actuator](https://search.maven.org/classic/#search|ga|1|a%3A"spring-boot-starter-actuator" AND g%3A"org.springframework.boot")和[spring-cloud-starter-feign](https://search.maven.org/classic/#search|ga|1|spring-cloud-starter-feign)依赖项添加到我们的pom.xml文件中：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
        <version>2.2.6.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-feign</artifactId>
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


```

### 4.2. 服务消费者注解

与服务提供者一样，我们将使用@EnableDiscoveryClient注解主类以使其具有发现意识：

```java
@SpringBootApplication
@EnableDiscoveryClient
public class GreetingApplication {
 
    public static void main(String[] args) {
        SpringApplication.run(GreetingApplication.class, args);
    }
}

```

### 4.3. 使用 Feign Client 发现服务

我们将使用Spring Cloud Feign Integration，这是 Netflix 的一个项目，可让你定义声明式 REST 客户端。我们声明 URL 的样子，然后 feign 负责连接到 REST 服务。

Feign客户端是通过[spring-cloud-starter-](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.cloud" AND a%3A"spring-cloud-starter-feign") feign包导入的。我们将使用@EnableFeignClients注解@Configuration以在应用程序中使用它。

最后，我们用@FeignClient(“service-name”)注解一个接口，并将其自动连接到我们的应用程序中，以便我们以编程方式访问该服务。

在注解@FeignClient(name = “HelloWorld”)中，我们引用了我们之前创建的服务生产者的服务名称。

```java
@Configuration
@EnableFeignClients
@EnableDiscoveryClient
public class HelloWorldClient {
 
    @Autowired
    private TheClient theClient;

    @FeignClient(name = "HelloWorld")
    interface TheClient {
 
        @RequestMapping(path = "/helloworld", method = RequestMethod.GET)
        @ResponseBody
	String helloWorld();
    }
    public String HelloWorld() {
        return theClient.HelloWorld();
    }
}
```

### 4.4. 控制器类

下面是一个简单的服务控制器类，它将通过注入的接口helloWorldClient对象调用我们的假客户端类上的服务提供者函数来使用服务(其细节通过服务发现抽象)并显示它作为响应：

```java
@RestController
public class GreetingController {
 
    @Autowired
    private HelloWorldClient helloWorldClient;

    @GetMapping("/get-greeting")
    public String greeting() {
        return helloWorldClient.helloWorld();
    }
}
```

### 4.5. YAML 配置

接下来，我们创建一个与之前使用的文件非常相似的 YAML 文件Application.yml 。配置应用程序的日志级别：

```xml
logging:
  level:
    org.apache.zookeeper.ClientCnxn: WARN
```

该应用程序在默认端口2181上查找 Zookeeper 。如果 Zookeeper 位于其他地方，则需要添加配置：

```xml
spring:
  cloud:
    zookeeper:
      connect-string: localhost:2181
```

## 5. 测试设置

HelloWorld REST 服务在部署时向 Zookeeper 注册自己。然后作为服务消费者的Greeting服务使用 Feign 客户端调用HelloWorld服务。

现在我们可以构建并运行这两个服务。

最后，我们将浏览器指向[http://localhost:8083/get-greeting](http://localhost:8080/get-greeting)，它应该显示：

```html
Hello World!
```

## 六. 总结

在本文中，我们了解了如何使用Spring Cloud Zookeeper实现服务发现，并且我们在 Zookeeper 服务器中注册了一个名为HelloWorld的服务，以便在不知道其位置详细信息的情况下使用Feign Client由Greeting服务发现和使用。