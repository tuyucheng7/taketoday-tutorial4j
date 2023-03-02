## 1. 简介

在本文中，我们将熟悉Zookeeper以及它如何用于服务发现，服务发现用作有关云中服务的集中知识。

Spring Cloud Zookeeper通过自动配置和绑定到Spring环境为Spring Boot应用程序提供[Apache Zookeeper](https://zookeeper.apache.org/)集成。

## 2. 服务发现设置

我们将创建两个应用程序：

-   将提供服务的应用程序(在本文中称为**Service Provider**)
-   将使用此服务的应用程序(称为**Service Consumer**)

Apache Zookeeper将在我们的服务发现设置中充当协调员。Apache Zookeeper安装说明可从以下[链接](https://zookeeper.apache.org/doc/current/zookeeperStarted.html)获得。

## 3. 服务提供者注册

我们将通过添加spring-cloud-starter-zookeeper-discovery依赖项并在主应用程序中使用注解@EnableDiscoveryClient来启用服务注册。

下面，我们将逐步展示在响应GET请求时返回“Hello World!”的服务的过程。

### 3.1 Maven依赖项

首先，让我们将所需的[spring-cloud-starter-zookeeper-discovery](https://search.maven.org/classic/#search|ga|1|spring-cloud-starter-zookeeper-discovery)、[spring-web](https://central.sonatype.com/artifact/org.springframework/spring-web/6.0.5)、[spring-cloud-dependencies](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-dependencies/2022.0.1)和[spring-boot-starter](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter/3.0.3)依赖项添加到我们的pom.xml文件中：

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

### 3.2 服务提供者注解

接下来，我们将使用@EnableDiscoveryClient标注我们的主类。这将使HelloWorld应用程序具有发现意识：

```java
@SpringBootApplication
@EnableDiscoveryClient
public class HelloWorldApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApplication.class, args);
	}
}
```

还有一个简单的控制器：

```java
@GetMapping("/helloworld")
public String helloWorld() {
    return "Hello World!";
}
```

### 3.3 YAML配置

现在让我们创建一个YAML application.yml文件，该文件将用于配置应用程序日志级别并通知Zookeeper该应用程序已启用服务发现。

注册到Zookeeper的应用程序的名称是最重要的。稍后在服务消费者中，feign客户端将在服务发现期间使用此名称：

```yaml
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

Spring Boot应用在默认端口2181寻找Zookeeper，如果Zookeeper位于其他地方，则需要添加配置：

```yaml
spring:
    cloud:
        zookeeper:
            connect-string: localhost:2181
```

## 4. 服务消费者

现在我们将创建一个REST服务消费者并使用Spring Netflix Feign Client注册它。

### 4.1 Maven依赖

首先，让我们将所需的[spring-cloud-starter-zookeeper-discovery](https://search.maven.org/classic/#search|ga|1|spring-cloud-starter-zookeeper-discovery)、[spring-web](https://central.sonatype.com/artifact/org.springframework/spring-web/6.0.5)、[spring-cloud-dependencies](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-dependencies/2022.0.1)、[spring-boot-starter-actuator](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-actuator/3.0.3)和[spring-cloud-starter-feign](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-feign/1.4.7.RELEASE)依赖项添加到我们的pom.xml文件中：

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

### 4.2 服务消费者注解

与服务提供者一样，我们将使用@EnableDiscoveryClient标注主类以使其具有发现意识：

```java
@SpringBootApplication
@EnableDiscoveryClient
public class GreetingApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreetingApplication.class, args);
	}
}
```

### 4.3 使用Feign Client发现服务

我们将使用Spring Cloud Feign Integration，这是Netflix的一个项目，可让你定义声明式REST客户端。我们声明URL的样子，然后Feign负责连接到REST服务。

Feign客户端是通过[spring-cloud-starter-feign](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-feign/1.4.7.RELEASE)包导入的。我们将使用@EnableFeignClients标注@Configuration类以在应用程序中使用它。

最后，我们使用@FeignClient("service-name")标注一个接口，并将其自动注入到我们的应用程序中，以便我们以编程方式访问该服务。

在注解@FeignClient(name = "HelloWorld")中，我们引用了我们之前创建的服务生产者的服务名称。

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

### 4.4 控制器类

下面是一个简单的服务控制器类，它将通过注入的接口helloWorldClient对象调用我们的Feign客户端类上的服务提供者函数来使用服务(其细节通过服务发现抽象)并显示它作为响应：

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

### 4.5 YAML配置

接下来，我们创建一个与之前使用的文件非常相似的YAML文件application.yml，这将配置应用程序的日志级别：

```yaml
logging:
    level:
        org.apache.zookeeper.ClientCnxn: WARN
```

该应用程序在默认端口2181上查找Zookeeper。如果Zookeeper位于其他地方，则需要添加配置：

```yaml
spring:
    cloud:
        zookeeper:
            connect-string: localhost:2181
```

## 5. 测试设置

HelloWorld REST服务在部署时向Zookeeper注册自己。然后作为服务消费者的Greeting服务使用Feign客户端调用HelloWorld服务。

现在我们可以构建并运行这两个服务。

最后，我们打开浏览器并访问[http://localhost:8083/get-greeting](http://localhost:8080/get-greeting)，它应该显示：

```html
Hello World!
```

## 6. 总结

在本文中，我们了解了如何使用Spring Cloud Zookeeper实现服务发现，并且我们在Zookeeper服务器中注册了一个名为HelloWorld的服务，以便在不知道其位置详细信息的情况下使用Feign客户端由Greeting服务发现和使用。