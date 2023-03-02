## 1. 概述

Java远程方法调用允许调用驻留在不同Java虚拟机中的对象。这是一项成熟的技术，但使用起来有点麻烦，正如我们在专门针对该主题的[官方Oracle跟踪](https://docs.oracle.com/javase/tutorial/rmi/index.html)中所看到的那样。

在这篇简短的文章中，我们将探索Spring Remoting如何以更简单、更简洁的方式利用RMI。

本文还完成了Spring Remoting的概述。你可以在前几期中找到有关其他受支持技术的详细信息：[HTTP Invokers](https://www.baeldung.com/spring-remoting-http-invoker)、[JMS](https://www.baeldung.com/spring-remoting-jms)、[AMQP](https://www.baeldung.com/spring-remoting-amqp)、[Hessian和Burlap](https://www.baeldung.com/spring-remoting-hessian-burlap)。

## 2. Maven依赖

正如我们在之前的文章中所做的那样，我们将设置几个Spring Boot应用程序：一个公开远程可调用对象的服务器和一个调用公开服务的客户端。

我们需要的一切都在spring-context jar中-所以我们可以使用我们喜欢的任何Spring Boot Starter来引入它，因为我们的主要目标只是让主库可用。

现在让我们继续使用通常的spring-boot-starter-web，记住删除Tomcat依赖项以排除嵌入式Web服务：

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

## 3. 服务器应用

我们将开始声明一个接口，该接口定义一个服务来预订出租车，最终将暴露给客户：

```java
public interface CabBookingService {
    Booking bookRide(String pickUpLocation) throws BookingException;
}
```

然后我们将定义一个实现该接口的bean。这是将在服务器上实际执行业务逻辑的bean：

```java
@Bean 
CabBookingService bookingService() {
    return new CabBookingServiceImpl();
}
```

让我们继续声明使服务对客户端可用的导出器。在这种情况下，我们将使用RmiServiceExporter：

```java
@Bean 
RmiServiceExporter exporter(CabBookingService implementation) {
    Class<CabBookingService> serviceInterface = CabBookingService.class;
    RmiServiceExporter exporter = new RmiServiceExporter();
    exporter.setServiceInterface(serviceInterface);
    exporter.setService(implementation);
    exporter.setServiceName(serviceInterface.getSimpleName());
    exporter.setRegistryPort(1099); 
    return exporter;
}
```

通过setServiceInterface()，我们提供了一个可以远程调用的接口的引用。

我们还应该提供对实际执行setService()方法的对象的引用。然后，如果我们不想使用默认端口1099，我们可以提供运行服务器的机器上可用的RMI注册表端口。

我们还应该设置一个服务名称，以允许在RMI注册表中识别公开的服务。

使用给定的配置，客户端将能够通过以下URL调用CabBookingService：rmi://HOST:1199/CabBookingService。

最后让我们启动服务器。我们甚至不需要自己启动RMI注册中心，因为如果注册中心不可用，Spring会自动为我们启动。

## 4. 客户端应用

现在让我们编写客户端应用程序。

我们开始声明RmiProxyFactoryBean，它将创建一个bean，该bean具有与服务器端运行的服务公开的相同接口，并且透明地将接收到的调用路由到服务器：

```java
@Bean 
RmiProxyFactoryBean service() {
    RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
    rmiProxyFactory.setServiceUrl("rmi://localhost:1099/CabBookingService");
    rmiProxyFactory.setServiceInterface(CabBookingService.class);
    return rmiProxyFactory;
}
```

然后让我们编写一个简单的代码来启动客户端应用程序并使用上一步中定义的代理：

```java
public static void main(String[] args) throws BookingException {
    CabBookingService service = SpringApplication
        .run(RmiClient.class, args).getBean(CabBookingService.class);
    Booking bookingOutcome = service
        .bookRide("13 Seagate Blvd, Key Largo, FL 33037");
    System.out.println(bookingOutcome);
}
```

现在，启动客户端以验证它是否调用服务器公开的服务就足够了。

## 5. 总结

在本教程中，我们了解了如何使用Spring Remoting来简化RMI的使用，否则RMI将需要一系列繁琐的任务，其中包括启动注册表并使用大量使用受检异常的接口定义服务。