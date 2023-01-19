## 一、简介

在本教程中，我们将学习Java 中的服务定位器设计模式。

我们将描述概念、实施示例并强调其使用的优缺点。

## 2.理解模式

服务定位器模式的目的是按需返回服务实例。这对于将服务消费者与具体类解耦很有用。

一个实现将包含以下组件：

-   客户端——客户端对象是服务消费者。它负责从服务定位器调用请求

-   服务定位器——是从缓存返回服务的通信入口点

-   缓存——一个用于存储服务引用以供以后重用的对象

-   初始化程序——在缓存中创建和注册对服务的引用

-   服务——服务组件代表原始服务或其实现

原始服务对象由定位器查找并按需返回。

## 3.实施

现在，让我们实际一点，通过一个例子来看看这些概念。

首先，我们将创建一个MessagingService 接口，用于以不同方式发送消息：

```java
public interface MessagingService {

    String getMessageBody();
    String getServiceName();
}
```

接下来，我们将定义上述接口的两个实现，通过电子邮件和 SMS 发送消息：

```java
public class EmailService implements MessagingService {

    public String getMessageBody() {
        return "email message";
    }

    public String getServiceName() {
        return "EmailService";
    }
}
```

SMSService类定义类似于EmailService类。

在定义了这两个服务之后，我们必须定义初始化它们的逻辑：

```java
public class InitialContext {
    public Object lookup(String serviceName) {
        if (serviceName.equalsIgnoreCase("EmailService")) {
            return new EmailService();
        } else if (serviceName.equalsIgnoreCase("SMSService")) {
            return new SMSService();
        }
        return null;
    }
}
```

在将服务定位器对象放在一起之前我们需要的最后一个组件是缓存。

在我们的示例中，这是一个具有List属性的简单类：

```java
public class Cache {
    private List<MessagingService> services = new ArrayList<>();

    public MessagingService getService(String serviceName) {
        // retrieve from the list
    }

    public void addService(MessagingService newService) {
        // add to the list
    }
}

```

最后，我们可以实现我们的服务定位器类：

```java
public class ServiceLocator {

    private static Cache cache = new Cache();

    public static MessagingService getService(String serviceName) {

        MessagingService service = cache.getService(serviceName);

        if (service != null) {
            return service;
        }

        InitialContext context = new InitialContext();
        MessagingService service1 = (MessagingService) context
          .lookup(serviceName);
        cache.addService(service1);
        return service1;
    }
}
```

这里的逻辑相当简单。

该类包含缓存的一个实例。然后，在getService()方法中，它将首先检查缓存中的服务实例。

然后，如果它为null，它将调用初始化逻辑并将新对象添加到缓存中。

## 4.测试

让我们看看现在如何获取实例：

```java
MessagingService service 
  = ServiceLocator.getService("EmailService");
String email = service.getMessageBody();

MessagingService smsService 
  = ServiceLocator.getService("SMSService");
String sms = smsService.getMessageBody();

MessagingService emailService 
  = ServiceLocator.getService("EmailService");
String newEmail = emailService.getMessageBody();
```

我们第一次从ServiceLocator获取EmailService时，创建并返回了一个新实例。然后，在下次调用后，EmailService将从缓存中返回。

## 5. 服务定位器与依赖注入

乍一看，服务定位器模式可能看起来类似于另一个众所周知的模式——即依赖注入。

首先，重要的是要注意依赖注入和服务定位器模式都是控制反转概念的实现。

[在继续之前，请在这篇文章](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)中了解有关依赖注入的更多信息。

这里的主要区别在于客户端对象仍然创建它的依赖项。它只是为此使用定位器，这意味着它需要对定位器对象的引用。

相比之下，当使用依赖注入时，类被赋予了依赖。注入器仅在启动时调用一次，以将依赖项注入到类中。

最后，让我们考虑一些避免使用服务定位器模式的原因。

反对它的一个论据是它使单元测试变得困难。通过依赖注入，我们可以将依赖类的模拟对象传递给测试实例。另一方面，这是服务定位器模式的瓶颈。

另一个问题是使用基于此模式的 API 比较棘手。这样做的原因是依赖项隐藏在类中并且它们只在运行时验证。

尽管如此，服务定位器模式还是易于编码和理解，对于小型应用程序来说是一个很好的选择。

## 六，总结

本指南展示了如何以及为何使用服务定位器设计模式。它讨论了服务定位器设计模式和依赖注入概念之间的主要区别。

通常，由开发人员选择如何设计应用程序中的类。

服务定位器模式是一种直接解耦代码的模式。但是，如果在多个应用程序中使用这些类，依赖注入是一个正确的选择。