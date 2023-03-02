## 1. 概述

在某些情况下，我们需要将系统分解为多个进程，每个进程负责我们应用程序的不同方面。在这些场景中，其中一个进程需要从另一个进程同步获取数据的情况并不少见。

Spring框架提供了一系列综合称为Spring Remoting的工具，使我们能够调用远程服务，就好像它们至少在某种程度上在本地可用一样。

在本文中，我们将建立一个基于Spring的HTTP调用程序的应用程序，该应用程序利用原生Java序列化和HTTP在客户端和服务器应用程序之间提供远程方法调用。

## 2. 服务定义

假设我们必须实现一个允许用户预订出租车的系统。

我们还假设我们选择构建**两个不同的应用程序**来实现此目标：

-   一个预订引擎应用程序，用于检查是否可以满足出租车请求
-   一个前端Web应用程序，允许客户预订他们的乘车服务，确保出租车的可用性得到确认

### 2.1 服务接口

当我们将Spring Remoting与HTTP调用程序一起使用时，我们必须通过一个接口定义我们的远程可调用服务，让Spring在客户端和服务器端创建代理，封装远程调用的技术细节。因此，让我们从允许我们预订出租车的服务接口开始：

```java
public interface CabBookingService {
    Booking bookRide(String pickUpLocation) throws BookingException;
}
```

当服务能够分配出租车时，它会返回一个带有预订码的Booking对象。Booking必须是可序列化的，因为Spring的HTTP调用程序必须将其实例从服务器传输到客户端：

```java
public class Booking implements Serializable {
    private String bookingCode;

    @Override
    public String toString() {
        return format("Ride confirmed: code '%s'.", bookingCode);
    }

    // standard getters/setters and a constructor
}
```

如果该服务无法预订出租车，则会抛出BookingException。在这种情况下，不需要将类标记为可序列化，因为Exception已经实现了它：

```java
public class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}
```

### 2.2 打包服务

服务接口以及用作参数、返回类型和异常的所有自定义类必须在客户端和服务器的类路径中都可用。最有效的方法之一是将它们全部打包到一个.jar文件中，该文件稍后可以作为依赖项包含在服务器和客户端的pom.xml中。

因此，让我们将所有代码放在一个名为“api”专用的Maven模块中；对于此示例，我们将使用以下Maven坐标：

```xml
<groupId>cn.tuyucheng.taketoday</groupId>
<artifactId>api</artifactId>
<version>1.0.0</version>
```

## 3. 服务器应用程序

让我们构建预订引擎应用程序以使用Spring Boot公开服务。

### 3.1 Maven依赖项

首先，你需要确保你的项目使用的是Spring Boot：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.2</version>
</parent>
```

你可以在[此处](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-parent/3.0.3)找到最新的Spring Boot版本。然后我们需要Web Starter模块：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

我们需要我们在上一步中组装的服务定义模块：

```xml
<dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>api</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 3.2 服务实现

我们首先定义一个实现服务接口的类：

```java
public class CabBookingServiceImpl implements CabBookingService {

    @Override
    public Booking bookPickUp(String pickUpLocation) throws BookingException {
        if (random() < 0.3) throw new BookingException("Cab unavailable");
        return new Booking(randomUUID().toString());
    }
}
```

让我们假设这是一个可能的实现。使用具有随机值的测试，我们将能够重现成功的场景-当找到可用的出租车并返回预订码时，以及失败的场景-当抛出BookingException以指示没有任何可用的出租车时。

### 3.3 公开服务

然后，我们需要在上下文中定义一个带有HttpInvokerServiceExporter类型bean的应用程序。它将负责在Web应用程序中公开一个HTTP入口点，稍后将由客户端调用：

```java
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Server {

    @Bean(name = "/booking") HttpInvokerServiceExporter accountService() {
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        exporter.setService( new CabBookingServiceImpl() );
        exporter.setServiceInterface( CabBookingService.class );
        return exporter;
    }

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
}
```

值得注意的是，Spring的HTTP调用程序使用HttpInvokerServiceExporter bean的名称作为HTTP端点URL的相对路径。

现在，我们可以启动服务器应用程序并在我们设置客户端应用程序时保持它运行。

## 4. 客户端应用程序

现在让我们编写客户端应用程序。

### 4.1 Maven依赖项

我们将使用与在服务器端使用的相同的服务定义和相同的Spring Boot版本。我们仍然需要Web Starter依赖，但由于我们不需要自动启动嵌入式容器，因此我们可以从依赖中排除Tomcat Starter：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 4.2 客户端实现

让我们实现客户端：

```java
@Configuration
public class Client {

    @Bean
    public HttpInvokerProxyFactoryBean invoker() {
        HttpInvokerProxyFactoryBean invoker = new HttpInvokerProxyFactoryBean();
        invoker.setServiceUrl("http://localhost:8080/booking");
        invoker.setServiceInterface(CabBookingService.class);
        return invoker;
    }

    public static void main(String[] args) throws BookingException {
        CabBookingService service = SpringApplication
              .run(Client.class, args)
              .getBean(CabBookingService.class);
        out.println(service.bookRide("13 Seagate Blvd, Key Largo, FL 33037"));
    }
}
```

@Bean标注的invoker()方法创建HttpInvokerProxyFactoryBean的实例。我们需要通过setServiceUrl()方法提供远程服务器响应的URL。

与我们为服务器所做的类似，我们还应该通过setServiceInterface()方法提供我们想要远程调用的服务的接口。

HttpInvokerProxyFactoryBean实现了Spring的FactoryBean。FactoryBean被定义为一个bean，但是Spring IoC容器将注入它创建的对象，而不是工厂本身。你可以在我们的[工厂bean文章](https://www.baeldung.com/spring-factorybean)中找到有关FactoryBean的更多详细信息。

main()方法引导独立应用程序并从上下文中获取CabBookingService的实例。在幕后，这个对象只是一个由HttpInvokerProxyFactoryBean创建的代理，它负责处理远程调用执行中涉及的所有技术细节。多亏了它，我们现在可以轻松地使用代理，就像服务实现在本地可用时一样。

让我们多次运行该应用程序以执行多个远程调用，以验证客户端在出租车可用和不可用时的行为。

## 5. 警告

当我们使用允许远程调用的技术时，我们应该充分意识到一些陷阱。

### 5.1 当心网络相关的异常

当我们使用不可靠的资源作为网络时，我们应该始终预料到意外情况。

假设客户端在无法访问服务器时调用服务器-要么是因为网络问题，要么是因为服务器已关闭，那么Spring Remoting将引发一个RemoteAccessException，它是一个RuntimeException。

编译器不会强制我们将调用包含在try-catch块中，但我们应该始终考虑这样做，以正确管理网络问题。

### 5.2 对象按值传递，而不是按引用传递

Spring Remoting HTTP封送方法参数和返回值以在网络上传输它们。这意味着服务器根据提供的参数的副本进行操作，而客户端根据服务器创建的结果的副本进行操作。

所以我们不能指望，例如，在结果对象上调用一个方法会改变服务器端同一对象的状态，因为客户端和服务器之间没有任何共享对象。

### 5.3 当心细粒度接口

跨网络边界调用方法比在同一进程中的对象上调用它要慢得多。

出于这个原因，定义应该使用更粗粒度的接口远程调用的服务通常是一个很好的做法，这些接口能够完成需要更少交互的业务事务，即使以更繁琐的接口为代价。

## 6. 总结

通过这个例子，我们看到了使用Spring Remoting调用远程进程是多么容易。

该解决方案的开放性略低于其他广泛使用的机制，如REST或Web服务，但在所有组件均使用Spring开发的场景中，它可以代表一种可行且速度快得多的替代方案。