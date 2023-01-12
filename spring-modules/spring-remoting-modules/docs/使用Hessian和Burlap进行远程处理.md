## **一、概述**

在[上一篇标题为“使用 HTTP 调用程序的 Spring Remoting 简介”的文章中，我们看到了通过](https://www.baeldung.com/spring-remoting-http-invoker)*Spring Remoting*设置利用远程方法调用 (RMI) 的客户端/服务器应用程序是多么容易。

在本文中，我们将展示***Spring Remoting\*****如何支持使用\*Hessian\*和\*Burlap\***代替**RMI 的实现。**

## **2.Maven依赖**

*Hessian*和*Burlap*均由以下库提供，您需要将其显式包含在*pom.xml*文件中：

```xml
<dependency>
    <groupId>com.caucho</groupId>
    <artifactId>hessian</artifactId>
    <version>4.0.38</version>
</dependency>复制
```

[您可以在Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.caucho" AND a%3A"hessian")上找到最新版本。

## **3. 麻布**

*Hessian*是来自*Caucho的轻量级二进制协议，Caucho 是**Resin*应用服务器的制造商。*Hessian*实现适用于多种平台和语言，包括 Java。

在接下来的小节中，我们将修改上一篇文章中出现的“出租车预订”示例，使客户端和服务器使用*Hessian*而不是基于*Spring Remote HTTP*的协议进行通信。

### **3.1. 公开服务**

让我们通过配置*HessianServiceExporter*类型的*RemoteExporter*来公开服务，替换之前使用的*HttpInvokerServiceExporter*：

```java
@Bean(name = "/booking") 
RemoteExporter bookingService() {
    HessianServiceExporter exporter = new HessianServiceExporter();
    exporter.setService(new CabBookingServiceImpl());
    exporter.setServiceInterface( CabBookingService.class );
    return exporter;
}复制
```

我们现在可以启动服务器并在我们准备客户端时保持它处于活动状态。

### **3.2. 客户申请**

让我们实现客户端。同样，修改非常简单——我们需要用*HessianProxyFactoryBean替换**HttpInvokerProxyFactoryBean*：

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
        CabBookingService service
          = SpringApplication.run(HessianClient.class, args)
              .getBean(CabBookingService.class);
        out.println(
          service.bookRide("13 Seagate Blvd, Key Largo, FL 33037"));
    }
}复制
```

现在让我们运行客户端，使其使用*Hessian*连接到服务器。

## **4. 粗麻布**

*Burlap*是*Caucho*的另一个基于*XML*的轻量级协议。*Caucho*很久以前就停止维护它了，为此，它的支持在最新的 Spring 版本中已被弃用，即使它已经存在。

因此，只有当您的应用程序已经分发并且不能轻易迁移到另一个*Spring Remoting*实现时，您才应该合理地继续使用*Burlap 。*

### **4.1. 公开服务**

我们可以像使用*Hessian*一样使用*Burlap——*我们只需要选择正确的实现方式：

```java
@Bean(name = "/booking") 
RemoteExporter burlapService() {
    BurlapServiceExporter exporter = new BurlapServiceExporter();
    exporter.setService(new CabBookingServiceImpl());
    exporter.setServiceInterface( CabBookingService.class );
    return exporter;
}复制
```

如您所见，我们只是将导出器的类型从*HessianServiceExporter*更改为*BurlapServiceExporter。*所有设置代码都可以保持不变。

同样，让我们启动服务器并在我们处理客户端时让它保持运行。

### **4.2. 客户端实现**

我们同样可以在客户端将*Hessian*换成*Burlap ，将**HessianProxyFactoryBean**换成*BurlapProxyFactoryBean ：

```java
@Bean
public BurlapProxyFactoryBean burlapInvoker() {
    BurlapProxyFactoryBean invoker = new BurlapProxyFactoryBean();
    invoker.setServiceUrl("http://localhost:8080/booking");
    invoker.setServiceInterface(CabBookingService.class);
    return invoker;
}复制
```

我们现在可以运行客户端并查看它如何使用*Burlap*成功连接到服务器应用程序。

## **5.结论**

*通过这些快速示例，我们展示了使用Spring Remoting*如何轻松地在不同技术之间进行选择以实现远程方法调用，以及如何开发一个应用程序而完全不知道用于表示远程方法调用的协议的技术细节。

与往常一样，您可以[在 GitHub 上](https://github.com/eugenp/tutorials/tree/master/spring-remoting-modules/remoting-hessian-burlap)找到源代码，其中包含*Hessian*和*Burlap*的客户端以及负责运行服务器和客户端的*JUnit*测试*CabBookingServiceTest.java*。