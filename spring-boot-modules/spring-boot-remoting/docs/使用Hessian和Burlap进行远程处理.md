## 1. 概述

在[上一篇标题为“使用HTTP调用程序的Spring Remoting简介”的文章中](https://www.baeldung.com/spring-remoting-http-invoker)，我们看到了通过Spring Remoting设置利用远程方法调用 (RMI)的客户端/服务器应用程序是多么容易。

在本文中，**我们将展示Spring Remoting如何支持使用Hessian和Burlap代替RMI的实现**。

## 2. Maven依赖

Hessian和Burlap均由以下库提供，你需要将其显式包含在pom.xml文件中：

```xml
<dependency>
    <groupId>com.caucho</groupId>
    <artifactId>hessian</artifactId>
    <version>4.0.38</version>
</dependency>
```

你可以在[Maven Central](https://central.sonatype.com/artifact/com.caucho/hessian/4.0.66)上找到最新版本。

## 3. Hessian

Hessian是来自Caucho的轻量级二进制协议，Caucho是Resin应用服务器的制造商。Hessian实现适用于多种平台和语言，包括Java。

在接下来的小节中，我们将修改上一篇文章中出现的“出租车预订”示例，使客户端和服务器使用Hessian而不是基于Spring Remote HTTP的协议进行通信。

### 3.1 公开服务

让我们通过配置一个HessianServiceExporter类型的RemoteExporter来公开服务，替换之前使用的HttpInvokerServiceExporter：

```java
@Bean(name = "/booking") 
RemoteExporter bookingService() {
    HessianServiceExporter exporter = new HessianServiceExporter();
    exporter.setService(new CabBookingServiceImpl());
    exporter.setServiceInterface( CabBookingService.class );
    return exporter;
}
```

现在，我们可以启动服务器并在我们准备客户端时保持它处于活动状态。

### 3.2 客户端应用程序

让我们实现客户端。同样，修改非常简单-我们需要用HessianProxyFactoryBean替换HttpInvokerProxyFactoryBean：

```java
@Configuration
public class HessianClient {

    @Bean
    public HessianProxyFactoryBean hessianInvoker() {
        HessianProxyFactoryBean invoker = new HessianProxyFactoryBean();
        invoker.setServiceUrl("http://localhost:8080/booking");
        invoker.setServiceInterface(CabBookingService.class);
        return invoker;
    }

    public static void main(String[] args) throws BookingException {
        CabBookingService service = SpringApplication.run(HessianClient.class, args)
              .getBean(CabBookingService.class);
        out.println(service.bookRide("13 Seagate Blvd, Key Largo, FL 33037"));
    }
}
```

现在让我们运行客户端，使其使用Hessian连接到服务器。

## 4. Burlap

Burlap是Caucho的另一个基于XML的轻量级协议。Caucho很久以前就停止维护它了，为此，它的支持在最新的Spring版本中已被弃用，即使它已经存在。

因此，只有当你的应用程序已经分发并且不能轻易迁移到另一个Spring Remoting实现时，你才应该合理地继续使用Burlap。

### 4.1 公开服务

我们可以像使用Hessian一样使用Burlap-我们只需要选择正确的实现方式：

```java
@Bean(name = "/booking") 
RemoteExporter burlapService() {
    BurlapServiceExporter exporter = new BurlapServiceExporter();
    exporter.setService(new CabBookingServiceImpl());
    exporter.setServiceInterface( CabBookingService.class );
    return exporter;
}
```

如你所见，我们只是将导出器的类型从HessianServiceExporter更改为BurlapServiceExporter。所有设置代码都可以保持不变。

同样，让我们启动服务器并在我们处理客户端时让它保持运行。

### 4.2 客户端实现

我们同样可以在客户端将Hessian换成Burlap，将HessianProxyFactoryBean换成BurlapProxyFactoryBean：

```java
@Bean
public BurlapProxyFactoryBean burlapInvoker() {
    BurlapProxyFactoryBean invoker = new BurlapProxyFactoryBean();
    invoker.setServiceUrl("http://localhost:8080/booking");
    invoker.setServiceInterface(CabBookingService.class);
    return invoker;
}
```

现在，我们可以运行客户端并查看它如何使用Burlap成功连接到服务器应用程序。

## 5. 总结

通过这些快速示例，我们展示了如何使用Spring Remoting轻松地在不同技术之间进行选择以实现远程方法调用，以及如何开发一个应用程序而完全不知道用于表示远程方法调用的协议的技术细节。

与往常一样，你可以在[GitHub](https://github.com/eugenp/tutorials/tree/master/spring-remoting-modules/remoting-hessian-burlap)上找到源代码，其中包含Hessian和Burlap的客户端以及负责运行服务器和客户端的JUnit测试CabBookingServiceTest.java。