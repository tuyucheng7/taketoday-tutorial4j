## **一、概述**

Java*远程方法调用*允许调用驻留在不同*Java 虚拟机*中的对象。这是一项成熟的技术，但使用起来有点麻烦，正如我们在专门针对该主题的[官方 Oracle 跟踪中所看到的那样。](https://docs.oracle.com/javase/tutorial/rmi/index.html)

在这篇简短的文章中，我们将探索*Spring Remoting*如何以更简单、更简洁的方式利用*RMI*。

本文还完成了*Spring Remoting*的概述。您可以在前几期中找到有关其他受支持技术的详细信息：[HTTP Invokers](https://www.baeldung.com/spring-remoting-http-invoker)、[JMS](https://www.baeldung.com/spring-remoting-jms)、[AMQP](https://www.baeldung.com/spring-remoting-amqp)、[Hessian 和 Burlap](https://www.baeldung.com/spring-remoting-hessian-burlap)。

## **2.Maven依赖**

正如我们在之前的文章中所做的那样，我们将设置几个*Spring Boot*应用程序：一个公开远程可调用对象的服务器和一个调用公开服务的客户端。

我们需要的一切都在*spring-context* jar 中——所以我们可以使用我们喜欢的任何*Spring Boot*帮助器来引入它——因为我们的主要目标只是让主库可用。

现在让我们继续使用通常的*spring-boot-starter-web* - 记住删除*Tomcat*依赖项以排除嵌入式 Web 服务：

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
</dependency>复制
```

## **3.服务器应用**

我们将开始声明一个接口，该接口定义一个服务来预订出租车，最终将暴露给客户：

```java
public interface CabBookingService {
    Booking bookRide(String pickUpLocation) throws BookingException;
}复制
```

然后我们将定义一个实现该接口的 bean。这是将在服务器上实际执行业务逻辑的 bean：

```java
@Bean 
CabBookingService bookingService() {
    return new CabBookingServiceImpl();
}复制
```

让我们继续声明使服务对客户端可用的*导出器。*在这种情况下，我们将使用*RmiServiceExporter*：

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
}复制
```

通过*setServiceInterface()*，我们提供了一个可以远程调用的接口的引用。

我们还应该提供对实际执行*setService()*方法的对象的引用。然后，如果我们不想使用默认端口 1099，我们可以提供运行服务器的机器上可用的*RMI 注册表端口。*

我们还应该设置一个服务名称，以允许在*RMI*注册表中识别公开的服务。

使用给定的配置，客户端将能够通过以下 URL联系*CabBookingService ：* *rmi://HOST:1199/CabBookingService*。

最后让我们启动服务器。我们甚至不需要自己启动 RMI 注册中心，因为如果注册中心不可用， *Spring*会自动为我们启动。

## **4.客户申请**

现在让我们编写客户端应用程序。

我们开始声明*RmiProxyFactoryBean*，它将创建一个 bean，该 bean 具有与服务器端运行的服务公开的相同接口，并且透明地将接收到的调用路由到服务器：

```java
@Bean 
RmiProxyFactoryBean service() {
    RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
    rmiProxyFactory.setServiceUrl("rmi://localhost:1099/CabBookingService");
    rmiProxyFactory.setServiceInterface(CabBookingService.class);
    return rmiProxyFactory;
}复制
```

然后让我们编写一个简单的代码来启动客户端应用程序并使用上一步中定义的代理：

```java
public static void main(String[] args) throws BookingException {
    CabBookingService service = SpringApplication
      .run(RmiClient.class, args).getBean(CabBookingService.class);
    Booking bookingOutcome = service
      .bookRide("13 Seagate Blvd, Key Largo, FL 33037");
    System.out.println(bookingOutcome);
}复制
```

现在足以启动客户端以验证它调用了服务器公开的服务。

## **5.结论**

在本教程中，我们看到了如何使用*Spring Remoting*来简化 RMI 的使用，否则*RMI*将需要一系列繁琐的任务，其中包括启动注册表并使用大量使用已检查异常的接口定义服务。