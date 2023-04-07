## 一、简介

在开发 Spring 应用程序时，需要告诉框架去哪里寻找 bean。当应用程序启动时，框架会定位并注册所有这些以供进一步执行。同样，我们需要定义将处理所有传入 Web 应用程序请求的映射。

[所有 Java Web 框架都构建在servlet api](https://www.baeldung.com/java-servlets-containers-intro)之上。在 Web 应用程序中，三个文件起着至关重要的作用。**通常，我们按顺序链接它们：*****web.xml\* -> \*applicationContext.xml\* -> \*spring-servlet.xml\***

在本文中，我们将了解*applicationContext*和*spring-servlet*之间的区别。

## 2.applicationContext.xml *_*

[控制反转（IoC）](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)是Spring框架的核心。在启用 IoC 的框架中，通常，容器负责实例化、创建和删除对象。在 Spring 中，[*applicationContext*](https://www.baeldung.com/spring-application-context)扮演着 IoC 容器的角色。

在开发标准 J2EE 应用程序时，我们在*web.xml文件中声明**ContextLoaderListener*。另外，还定义了一个*contextConfigLocation*来表示XML配置文件。

```xml
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/applicationContext*.xml</param-value>
</context-param>复制
```

当应用程序启动时，Spring 加载此配置文件并使用它来创建*WebApplicationContext*对象。*在没有contextConfigLocation*的情况下，默认情况下*，*系统会寻找*/WEB-INF/applicationContext.xml*来加载*。*

**简而言之，\*applicationContext\*是 Spring 中的中心接口。它为应用程序提供配置信息。**

在此文件中，我们提供与应用程序相关的配置。通常，这些是用于项目本地化的基本数据源、属性占位符文件和消息源，以及其他增强功能。

让我们看一下示例文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:c="http://www.springframework.org/schema/c"
  xmlns:p="http://www.springframework.org/schema/p"
  xmlns:context="http://www.springframework.org/schema/context"
  xsi:schemaLocation="http://www.springframework.org/schema/beans
  http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
  http://www.springframework.org/schema/context
  http://www.springframework.org/schema/context/spring-context-4.1.xsd">

    <context:property-placeholder location="classpath:/database.properties" />

    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
        <property name="driverClassName" value="${jdbc.driverClassName}" />
        <property name="url" value="${jdbc.url}" />
        <property name="username" value="${jdbc.username}" />
        <property name="password" value="${jdbc.password}" />
        <property name="initialSize" value="5" />
        <property name="maxActive" value="10" />
    </bean>

    <bean id="messageSource"
        class="org.springframework.context.support.ResourceBundleMessageSource">
        <property name="basename" value="messages" />
    </bean>
</beans>复制
```

***ApplicationContext是\**BeanFactory\*接口的完整超集，因此提供了\*BeanFactory\*的所有功能。***它还提供集成的生命周期管理、 BeanPostProcessor*和*BeanFactoryPostProcessor*的自动注册*、便捷的MessageSource*访问和*ApplicationEvent 的发布。*

## 3. *spring-servlet.xml*

在 Spring 中，单个前端 servlet 接收传入的请求并将它们委托给适当的控制器方法。[基于前端控制器设计模式的](https://www.baeldung.com/java-front-controller-pattern)前端 servlet处理特定 Web 应用程序的所有 HTTP 请求。这个前端 servlet 拥有对传入请求的所有控制。

同样，*spring-servlet*充当前端控制器 servlet 并提供单一入口点。它接受传入的 URI。在幕后，它使用*HandlerMapping*实现来定义请求和处理程序对象之间的映射。

让我们看一下示例代码：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:mvc="http://www.springframework.org/schema/mvc"
  xmlns:context="http://www.springframework.org/schema/context"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="
    http://www.springframework.org/schema/beans     
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/mvc 
    http://www.springframework.org/schema/mvc/spring-mvc.xsd
    http://www.springframework.org/schema/context 
    http://www.springframework.org/schema/context/spring-context.xsd">

    <mvc:annotation-driven />
    <context:component-scan base-package="com.baeldung.controller" />

    <bean id="viewResolver"
      class="org.springframework.web.servlet.view.UrlBasedViewResolver">
	<property name="viewClass"
          value="org.springframework.web.servlet.view.JstlView" />
	<property name="prefix" value="/WEB-INF/jsp/" />
	<property name="suffix" value=".jsp" />
    </bean>

</beans>复制
```

## 4. *applicationContext.xml*与*spring-servlet.xml*

让我们看一下摘要视图：

| **特征**   | ***应用上下文.xml***                                        | ***spring-servlet.xml***                                     |
| ---------- | ----------------------------------------------------------- | ------------------------------------------------------------ |
| **框架**   | 它是 Spring 框架的一部分。                                  | 它是 Spring MVC 框架的一部分。                               |
| **目的**   | 定义 spring bean 的容器。                                   | 处理传入请求的前端控制器。                                   |
| **范围**   | 它定义了在所有 servlet 之间共享的 beans。                   | 它只定义特定于 servlet 的 bean。                             |
| **管理**   | 它管理全局事物，例如*数据源，*并且在其中定义了连接工厂。    | *反之，只有controllers、 viewresolver*等web相关的东西才会在里面定义。 |
| **参考**   | *它无法访问spring-servlet*的 bean 。                        | *它可以访问applicationContext*中定义的 bean 。               |
| **分享**   | 整个应用程序共有的属性将放在这里。                          | 仅特定于一个 servlet 的属性将放在此处。                      |
| **扫描**   | 我们定义过滤器以包含/排除包。                               | 我们声明控制器的组件扫描。                                   |
| **发生**   | 在应用程序中定义多个上下文文件是很常见的。                  | 同样，我们可以在一个网络应用中定义多个文件。                 |
| **加载中** | 文件 applicationContext.xml 由*ContextLoaderListener*加载。 | 文件 spring-servlet.xml 由*DispatcherServlet*加载。          |
| **必需的** | 选修的                                                      | 强制的                                                       |

## 5.结论

在本教程中，我们了解了*applicationContext*和*spring-servlet*文件。然后，我们讨论了它们在 Spring 应用程序中的角色和职责。最后，我们研究了它们之间的差异。