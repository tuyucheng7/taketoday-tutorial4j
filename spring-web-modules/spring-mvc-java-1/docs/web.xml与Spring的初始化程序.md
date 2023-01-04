## 1. 概述

在本文中，我们将介绍三种不同的配置DispatcherServlet的方法，这些方法在最新版本的Spring Framework中可用：

1.  我们将从XML配置和web.xml文件开始
2.  然后我们将Servlet声明从web.xml文件迁移到Java配置，但我们将保留XML中的任何其他配置
3.  最后，在重构的第三步也是最后一步，我们将拥有一个100% Java配置的项目

## 2. DispatcherServlet

Spring MVC的核心概念之一是DispatcherServlet。[Spring文档](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/DispatcherServlet.html)将其定义为：

>   HTTP请求处理程序/控制器的中央调度程序，例如用于Web UI控制器或基于HTTP的远程服务导出器。分派给已注册的处理程序以处理Web请求，提供方便的映射和异常处理设施。

基本上DispatcherServlet是每个Spring MVC应用程序的入口点。它的目的是拦截HTTP请求并将它们分派给知道如何处理它的正确组件。

## 3. 配置web.xml

如果你处理遗留的Spring项目，找到XML配置是很常见的，在Spring 3.1之前，配置DispatcherServlet的唯一方法是使用WEB-INF/web.xml文件。在这种情况下，需要两个步骤。

让我们看一个示例配置，第一步是Servlet声明：

```xml
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>
        org.springframework.web.servlet.DispatcherServlet
    </servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/spring/dispatcher-config.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
```

通过这个XML块，我们声明了一个Servlet：

1.  被命名为“dispatcher”
2.  是org.springframework.web.servlet.DispatcherServlet的实例
3.  将使用名为contextConfigLocation的参数进行初始化，该参数包含配置XML的路径

load-on-startup是一个整数值，指定加载多个Servlet的顺序。因此，如果你需要声明多个Servlet，你可以定义它们的初始化顺序。标记为较低整数的Servlet在标记为较高整数的Servlet之前加载。

现在我们的Servlet已经配置好了，第二步是声明一个servlet-mapping：

```xml
<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

通过servlet-mapping，我们通过其名称将其绑定到一个URL模式，该模式指定它将处理哪些HTTP请求。

## 4. 混合配置

随着Servlet APIs 3.0版本的采用，web.xml文件变得可选，我们现在可以使用Java来配置DispatcherServlet。

我们可以注册一个实现WebApplicationInitializer的Servlet，这相当于上面的XML配置：

```java
public class MyWebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext container) {
        XmlWebApplicationContext context = new XmlWebApplicationContext();
        context.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");

        ServletRegistration.Dynamic dispatcher = container
              .addServlet("dispatcher", new DispatcherServlet(context));

        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
```

在这个例子中，我们是：

1.  实现WebApplicationInitializer接口
2.  重写onStartup方法，我们创建一个新的XmlWebApplicationContext配置了与contextConfigLocation传递给XML示例中的Servlet的相同文件
3.  然后我们使用刚刚实例化的新上下文创建DispatcherServlet的实例
4.  最后我们使用映射URL模式注册Servlet

所以我们使用Java来声明Servlet并将其绑定到URL映射，但我们将配置保存在一个单独的XML文件中：dispatcher-config.xml。

## 5. 100% Java配置

使用这种方法，我们的Servlet是在Java中声明的，但我们仍然需要一个XML文件来配置它。使用WebApplicationInitializer，你可以实现100%的Java配置。

让我们看看如何重构前面的示例。

我们需要做的第一件事是为Servlet创建应用程序上下文。

这次我们将使用基于注解的上下文，这样我们就可以使用Java和注解进行配置，并且不再需要像dispatcher-config.xml这样的XML文件：

```java
AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
```

然后可以通过注册配置类来配置这种类型的上下文：

```java
context.register(AppConfig.class);
```

或者设置将扫描配置类的整个包：

```java
context.setConfigLocation("cn.tuyucheng.taketoday.app.config");
```

现在我们的应用程序上下文已经创建，我们可以向将加载上下文的ServletContext添加一个监听器：

```java
container.addListener(new ContextLoaderListener(context));
```

下一步是创建和注册我们的dispatcher Servlet：

```java
ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", new DispatcherServlet(context));

dispatcher.setLoadOnStartup(1);
dispatcher.addMapping("/");
```

现在我们的WebApplicationInitializer应该是这样的：

```java
public class MyWebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext container) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation("cn.tuyucheng.taketoday.app.config");

        container.addListener(new ContextLoaderListener(context));

        ServletRegistration.Dynamic dispatcher = container
              .addServlet("dispatcher", new DispatcherServlet(context));

        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
```

Java和注解配置提供了许多优点。通常它会导致更短和更简洁的配置，并且注解为声明提供更多上下文，因为它与它们配置的代码位于同一位置。

但这并不总是一种更可取甚至可能的方式。例如，一些开发人员可能更喜欢将他们的代码和配置分开，或者你可能需要使用你无法修改的第三方代码。

## 6. 总结

在本文中，我们介绍了在Spring 3.2+中配置DispatcherServlet的不同方法，你可以根据自己的喜好决定使用哪一种。无论你选择什么，Spring都会适应你的决定。