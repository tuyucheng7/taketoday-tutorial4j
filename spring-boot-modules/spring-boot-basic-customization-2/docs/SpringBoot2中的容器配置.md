## 1. 概述

在这个快速教程中，我们将了解如何在Spring Boot 2中替换EmbeddedServletContainerCustomizer和ConfigurableEmbeddedServletContainer。

这些类是Spring Boot早期版本的一部分，但从Spring Boot 2开始已被删除。当然，**这些功能仍然可以通过接口WebServerFactoryCustomizer和类ConfigurableServletWebServerFactory获得**。

让我们来看看如何使用它们。

## 2. Spring Boot 2之前

首先，让我们看一下使用旧类和接口的配置，我们需要替换它们：

```java
@Component
public class CustomContainer implements EmbeddedServletContainerCustomizer {

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        container.setPort(8080);
        container.setContextPath("");
    }
}
```

在这里，我们自定义了Servlet容器的端口和上下文路径。

实现这一点的另一种可能性是使用ConfigurableEmbeddedServletContainer的更具体的子类，用于容器类型，例如Tomcat：

```java
@Component
public class CustomContainer implements EmbeddedServletContainerCustomizer {

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        if (container instanceof TomcatEmbeddedServletContainerFactory) {
            TomcatEmbeddedServletContainerFactory tomcatContainer = (TomcatEmbeddedServletContainerFactory) container;
            tomcatContainer.setPort(8080);
            tomcatContainer.setContextPath("");
        }
    }
}
```

## 3. Spring Boot 2

**在Spring Boot 2中，EmbeddedServletContainerCustomizer接口被WebServerFactoryCustomizer取代，而ConfigurableEmbeddedServletContainer类被ConfigurableServletWebServerFactory取代**。

让我们为Spring Boot 2项目重写前面的示例：

```java
public class CustomContainer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    public void customize(ConfigurableServletWebServerFactory factory) {
        factory.setPort(8080);
        factory.setContextPath("");
    }
}
```

第二个示例现在将使用TomcatServletWebServerFactory：

```java
@Component
public class CustomContainer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.setContextPath("");
        factory.setPort(8080);
    }
}
```

同样，我们将JettyServletWebServerFactory和UndertowServletWebServerFactory作为已删除的JettyEmbeddedServletContainerFactory和UndertowEmbeddedServletContainerFactory的等价物。

## 4. 总结

这篇简短的文章展示了如何解决我们在将Spring Boot应用程序升级到版本2.x时可能遇到的问题。