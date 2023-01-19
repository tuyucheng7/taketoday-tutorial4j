## 1. 简介

在 Web 应用程序中使用 Spring 时，我们有多种选择来组织将它们连接起来的应用程序上下文。

在本文中，我们将分析和解释 Spring 提供的最常见的选项。

## 2. 根 Web 应用程序上下文

每个 Spring webapp 都有一个与其生命周期相关联的应用程序上下文：根 web 应用程序上下文。

这是一个早于 Spring Web MVC 的旧功能，因此它没有专门绑定到任何 Web 框架技术。

上下文在应用程序启动时启动，并在应用程序停止时销毁，这要归功于 servlet 上下文侦听器。最常见的上下文类型也可以在运行时刷新，尽管并非所有ApplicationContext实现都具有此功能。

Web 应用程序中的上下文始终是WebApplicationContext的一个实例。这是一个扩展ApplicationContext的接口，带有用于访问ServletContext的合同。

无论如何，应用程序通常不应该关心那些实现细节：根 Web 应用程序上下文只是一个定义共享 bean 的集中位置。

### 2.1. ContextLoaderListener _ 

上一节中描述的根 Web 应用程序上下文由org.springframework.web.context.ContextLoaderListener类的侦听器管理，它是spring-web模块的一部分。

默认情况下，侦听器将从/WEB-INF/applicationContext.xml加载 XML 应用程序上下文。但是，可以更改这些默认值。例如，我们可以使用Java注解代替 XML。

我们可以在 webapp 描述符(web.xml文件)中或在 Servlet 3.x 环境中以编程方式配置此侦听器。

在以下部分中，我们将详细介绍这些选项中的每一个。

### 2.2. 使用web.xml和 XML 应用程序上下文

使用web.xml时，我们像往常一样配置监听器：

```xml
<listener>
    <listener-class>
        org.springframework.web.context.ContextLoaderListener
    </listener-class>
</listener>
```

我们可以使用contextConfigLocation参数指定 XML 上下文配置的备用位置：

```xml
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/rootApplicationContext.xml</param-value>
</context-param>
```

或多个位置，以逗号分隔：

```xml
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/context1.xml, /WEB-INF/context2.xml</param-value>
</context-param>
```

我们甚至可以使用模式：

```xml
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/-context.xml</param-value>
</context-param>
```

在任何情况下，通过组合从指定位置加载的所有 bean 定义，只定义了一个上下文。

### 2.3. 使用web.xml和Java应用程序上下文

除了默认的基于 XML 的上下文之外，我们还可以指定其他类型的上下文。例如，让我们看看如何使用Java注解配置。

我们使用contextClass参数来告诉监听器要实例化哪种类型的上下文：

```xml
<context-param>
    <param-name>contextClass</param-name>
    <param-value>
        org.springframework.web.context.support.AnnotationConfigWebApplicationContext
    </param-value>
</context-param>
```

每种类型的上下文都可能有一个默认配置位置。在我们的例子中，AnnotationConfigWebApplicationContext没有，所以我们必须提供它。

因此，我们可以列出一个或多个带注解的类：

```xml
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>
        com.baeldung.contexts.config.RootApplicationConfig,
        com.baeldung.contexts.config.NormalWebAppConfig
    </param-value>
</context-param>
```

或者我们可以告诉上下文扫描一个或多个包：

```xml
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>com.baeldung.bean.config</param-value>
</context-param>
```

当然，我们可以混合搭配这两个选项。

### 2.4. 使用Servlet 3.x 进行编程配置

Servlet API 的版本 3 通过web.xml文件进行配置完全可选。库可以提供它们的 Web 片段，这些片段是可以注册侦听器、过滤器、servlet 等的 XML 配置片段。

此外，用户还可以访问 API，该 API 允许以编程方式定义基于 servlet 的应用程序的每个元素。

spring-web模块利用这些特性并提供其 API 以在应用程序启动时注册其组件。 

Spring 扫描应用程序的类路径以查找org.springframework.web.WebApplicationInitializer类的实例。这是一个只有一个方法的接口，void onStartup(ServletContext servletContext) throws ServletException，在应用程序启动时调用。

现在让我们看看我们如何使用这个工具来创建我们之前看到的相同类型的根 Web 应用程序上下文。

### 2.5. 使用 Servlet 3.x 和 XML 应用程序上下文

让我们从 XML 上下文开始，就像在第 2.2 节中一样。

我们将实现前面提到的onStartup方法：

```java
public class ApplicationInitializer implements WebApplicationInitializer {
    
    @Override
    public void onStartup(ServletContext servletContext) 
      throws ServletException {
        //...
    }
}
```

让我们逐行分解实现。

我们首先创建一个根上下文。由于我们要使用 XML，它必须是基于 XML 的应用程序上下文，并且由于我们处于 Web 环境中，它也必须实现WebApplicationContext。

因此，第一行是我们之前遇到的contextClass参数的显式版本，我们通过它来决定使用哪个特定的上下文实现：

```java
XmlWebApplicationContext rootContext = new XmlWebApplicationContext();
```

然后，在第二行，我们告诉上下文从哪里加载它的 bean 定义。同样，setConfigLocations是web.xml中contextConfigLocation参数的编程类比：

```java
rootContext.setConfigLocations("/WEB-INF/rootApplicationContext.xml");
```

最后，我们使用根上下文创建一个ContextLoaderListener并将其注册到 servlet 容器。正如我们所见，ContextLoaderListener有一个适当的构造函数，它接受一个WebApplicationContext并使其对应用程序可用：

```java
servletContext.addListener(new ContextLoaderListener(rootContext));
```

### 2.6. 使用 Servlet 3.x 和Java应用程序上下文

如果我们想使用基于注解的上下文，我们可以更改上一节中的代码片段，使其实例化一个 AnnotationConfigWebApplicationContext。

但是，让我们看看获得相同结果的更专业的方法。

我们之前看到的WebApplicationInitializer类是一个通用接口。事实证明，Spring 提供了一些更具体的实现，包括一个名为AbstractContextLoaderInitializer的抽象类。

顾名思义，它的工作是创建一个ContextLoaderListener并将其注册到 servlet 容器。

我们只需要告诉它如何构建根上下文：

```java
public class AnnotationsBasedApplicationInitializer 
  extends AbstractContextLoaderInitializer {
 
    @Override
    protected WebApplicationContext createRootApplicationContext() {
        AnnotationConfigWebApplicationContext rootContext
          = new AnnotationConfigWebApplicationContext();
        rootContext.register(RootApplicationConfig.class);
        return rootContext;
    }
}
```

在这里我们可以看到我们不再需要注册 ContextLoaderListener，这使我们免于编写一些样板代码。

还要注意使用特定于AnnotationConfigWebApplicationContext的register方法而不是更通用的setConfigLocations：通过调用它，我们可以在上下文中注册单独的@Configuration注解类，从而避免包扫描。

## 3. Dispatcher Servlet 上下文

现在让我们关注另一种类型的应用程序上下文。这一次，我们将提到一个特定于Spring MVC的特性，而不是 Spring 的通用 Web 应用程序支持的一部分。

Spring MVC 应用程序至少配置了一个 Dispatcher Servlet(但可能不止一个，我们稍后会讨论这种情况)。这是接收传入请求、将它们分派到适当的控制器方法并返回视图的 servlet。

每个DispatcherServlet都有一个关联的应用程序上下文。在此类上下文中定义的 Bean 配置 servlet 并定义 MVC 对象，如控制器和视图解析器。

让我们先看看如何配置 servlet 的上下文。稍后我们将深入了解一些细节。

### 3.1. 使用web.xml和 XML 应用程序上下文

DispatcherServlet通常在web.xml中使用名称和映射声明：

```xml
<servlet>
    <servlet-name>normal-webapp</servlet-name>
    <servlet-class>
        org.springframework.web.servlet.DispatcherServlet
    </servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>normal-webapp</servlet-name>
    <url-pattern>/api/</url-pattern>
</servlet-mapping>
```

如果没有另外指定，则使用 servlet 的名称来确定要加载的 XML 文件。在我们的示例中，我们将使用文件 WEB-INF/normal-webapp-servlet.xml。

我们还可以以类似于ContextLoaderListener的方式指定一个或多个 XML 文件的路径：

```xml
<servlet>
    ...
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/normal/.xml</param-value>
    </init-param>
</servlet>
```

### 3.2. 使用web.xml和Java应用程序上下文

当我们想要使用不同类型的上下文时，我们再次像使用ContextLoaderListener一样进行。也就是说，我们指定一个contextClass参数以及一个合适的contextConfigLocation：

```xml
<servlet>
    <servlet-name>normal-webapp-annotations</servlet-name>
    <servlet-class>
        org.springframework.web.servlet.DispatcherServlet
    </servlet-class>
    <init-param>
        <param-name>contextClass</param-name>
        <param-value>
            org.springframework.web.context.support.AnnotationConfigWebApplicationContext
        </param-value>
    </init-param>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>com.baeldung.contexts.config.NormalWebAppConfig</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
```

### 3.3. 使用 Servlet 3.x 和 XML 应用程序上下文

同样，我们将查看以编程方式声明DispatcherServlet的两种不同方法，我们将把一种方法应用于 XML 上下文，另一种应用于Java上下文。

因此，让我们从一个通用的WebApplicationInitializer和一个 XML 应用程序上下文开始。

正如我们之前所见，我们必须实现onStartup方法。然而，这次我们也将创建并注册一个调度程序 servlet：

```java
XmlWebApplicationContext normalWebAppContext = new XmlWebApplicationContext();
normalWebAppContext.setConfigLocation("/WEB-INF/normal-webapp-servlet.xml");
ServletRegistration.Dynamic normal
  = servletContext.addServlet("normal-webapp", 
    new DispatcherServlet(normalWebAppContext));
normal.setLoadOnStartup(1);
normal.addMapping("/api/");
```

我们可以轻松地将上述代码与等效的web.xml配置元素进行比较。

### 3.4. 使用 Servlet 3.x 和Java应用程序上下文

这一次，我们将使用WebApplicationInitializer的专门实现来配置基于注解的上下文：AbstractDispatcherServletInitializer。

这是一个抽象类，除了如前所述创建根 Web 应用程序上下文之外，它还允许我们使用最少的样板文件注册一个调度程序 servlet：

```java
@Override
protected WebApplicationContext createServletApplicationContext() {
 
    AnnotationConfigWebApplicationContext secureWebAppContext
      = new AnnotationConfigWebApplicationContext();
    secureWebAppContext.register(SecureWebAppConfig.class);
    return secureWebAppContext;
}

@Override
protected String[] getServletMappings() {
    return new String[] { "/s/api/" };
}
```

在这里我们可以看到一种创建与 servlet 关联的上下文的方法，就像我们之前看到的根上下文一样。此外，我们还有一个方法来指定 servlet 的映射，如web.xml中所示。

## 4.父子上下文

到目前为止，我们已经看到了两种主要类型的上下文：根 Web 应用程序上下文和调度程序 servlet 上下文。那么，我们可能会有一个疑问：那些上下文相关吗？

事实证明，是的，他们是。事实上，根上下文是每个调度程序 servlet 上下文的父级。因此，在根 Web 应用程序上下文中定义的 bean 对每个调度程序 servlet 上下文都是可见的，但反之则不然。

因此，通常，根上下文用于定义服务 bean，而调度程序上下文包含那些与 MVC 特别相关的 bean。

请注意，我们还看到了以编程方式创建调度程序 servlet 上下文的方法。如果我们手动设置它的父级，那么 Spring 不会覆盖我们的决定，并且本节不再适用。

在更简单的 MVC 应用程序中，将一个上下文关联到唯一的一个调度程序 servlet 就足够了。不需要过于复杂的解决方案！

尽管如此，当我们配置了多个调度程序 servlet 时，父子关系还是很有用的。但是我们什么时候应该费心拥有多个呢？

通常，当我们需要多套 MVC 配置时，我们会声明多个 Dispatcher Servlet 。例如，我们可能有一个 REST API 和一个传统的 MVC 应用程序或一个网站的不安全和安全部分：

[![语境](https://www.baeldung.com/wp-content/uploads/2018/05/contexts.png)](https://www.baeldung.com/wp-content/uploads/2018/05/contexts.png)

注意：当我们扩展AbstractDispatcherServletInitializer(参见第 3.4 节)时，我们同时注册了根 Web 应用程序上下文和单个调度程序 servlet。

因此，如果我们想要多个 servlet，则需要多个AbstractDispatcherServletInitializer实现。但是，我们只能定义一个根上下文，否则应用程序将无法启动。

幸运的是，createRootApplicationContext方法可以返回null。因此，我们可以有一个AbstractContextLoaderInitializer和许多不创建根上下文的AbstractDispatcherServletInitializer实现。在这种情况下，建议使用@Order显式地对初始化程序进行排序。

另外，请注意AbstractDispatcherServletInitializer在给定名称 ( dispatcher )下注册 servlet ，当然，我们不能有多个同名的 servlet。所以，我们需要覆盖getServletName：

```java
@Override
protected String getServletName() {
    return "another-dispatcher";
}
```

## 5. 父子上下文 示例 

假设我们的应用程序有两个区域，例如一个可在全球访问的公共区域和一个安全区域，具有不同的 MVC 配置。在这里，我们只定义两个输出不同消息的控制器。

另外，假设一些控制器需要一个拥有大量资源的服务；一个普遍存在的例子是持久性。然后，我们只想将该服务实例化一次，以避免其资源使用量加倍，因为我们相信不要重复自己的原则！

现在让我们继续这个例子。

### 5.1. 共享服务

在我们的 hello world 示例中，我们选择了一个更简单的欢迎服务而不是持久性服务：

```java
package com.baeldung.contexts.services;

@Service
public class GreeterService {
    @Resource
    private Greeting greeting;
    
    public String greet() {
        return greeting.getMessage();
    }
}
```

我们将使用组件扫描在根 Web 应用程序上下文中声明服务：

```java
@Configuration
@ComponentScan(basePackages = { "com.baeldung.contexts.services" })
public class RootApplicationConfig {
    //...
}
```

我们可能更喜欢 XML：

```xml
<context:component-scan base-package="com.baeldung.contexts.services" />
```

### 5.2. 控制者

让我们定义两个使用该服务并输出问候语的简单控制器：

```java
package com.baeldung.contexts.normal;

@Controller
public class HelloWorldController {

    @Autowired
    private GreeterService greeterService;
    
    @RequestMapping(path = "/welcome")
    public ModelAndView helloWorld() {
        String message = "<h3>Normal " + greeterService.greet() + "</h3>";
        return new ModelAndView("welcome", "message", message);
    }
}

//"Secure" Controller
package com.baeldung.contexts.secure;

String message = "<h3>Secure " + greeterService.greet() + "</h3>";
```

正如我们所见，控制器位于两个不同的包中并打印不同的消息：一个说“正常”，另一个说“安全”。

### 5.3. 调度程序 Servlet 上下文

正如我们之前所说，我们将有两个不同的调度程序 servlet 上下文，每个控制器一个。那么，让我们用Java定义它们：

```java
//Normal context
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.baeldung.contexts.normal" })
public class NormalWebAppConfig implements WebMvcConfigurer {
    //...
}

//"Secure" context
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.baeldung.contexts.secure" })
public class SecureWebAppConfig implements WebMvcConfigurer {
    //...
}
```

或者，如果我们愿意，在 XML 中：

```xml
<!-- normal-webapp-servlet.xml -->
<context:component-scan base-package="com.baeldung.contexts.normal" />

<!-- secure-webapp-servlet.xml -->
<context:component-scan base-package="com.baeldung.contexts.secure" />
```

### 5.4. 把它们放在一起

现在我们有了所有的部分，我们只需要告诉 Spring 把它们连接起来。回想一下，我们需要加载根上下文并定义两个调度程序 servlet。虽然我们已经看到了多种方法来做到这一点，但我们现在将重点关注两种情况，一种是Java一种，另一种是 XML 一种。让我们从Java开始。

我们将定义一个AbstractContextLoaderInitializer来加载根上下文：

```java
@Override
protected WebApplicationContext createRootApplicationContext() {
    AnnotationConfigWebApplicationContext rootContext
      = new AnnotationConfigWebApplicationContext();
    rootContext.register(RootApplicationConfig.class);
    return rootContext;
}

```

然后，我们需要创建两个 servlet，因此我们将定义AbstractDispatcherServletInitializer的两个子类。首先，“正常”的：

```java
@Override
protected WebApplicationContext createServletApplicationContext() {
    AnnotationConfigWebApplicationContext normalWebAppContext
      = new AnnotationConfigWebApplicationContext();
    normalWebAppContext.register(NormalWebAppConfig.class);
    return normalWebAppContext;
}

@Override
protected String[] getServletMappings() {
    return new String[] { "/api/" };
}

@Override
protected String getServletName() {
    return "normal-dispatcher";
}

```

然后是“安全”的，它加载不同的上下文并映射到不同的路径：

```java
@Override
protected WebApplicationContext createServletApplicationContext() {
    AnnotationConfigWebApplicationContext secureWebAppContext
      = new AnnotationConfigWebApplicationContext();
    secureWebAppContext.register(SecureWebAppConfig.class);
    return secureWebAppContext;
}

@Override
protected String[] getServletMappings() {
    return new String[] { "/s/api/" };
}

@Override
protected String getServletName() {
    return "secure-dispatcher";
}
```

我们完成了！我们刚刚应用了我们在前面部分中触及的内容。

我们可以对web.xml做同样的事情，再次只是通过组合我们到目前为止讨论的部分。

定义根应用上下文：

```xml
<listener>
    <listener-class>
        org.springframework.web.context.ContextLoaderListener
    </listener-class>
</listener>

```

一个“正常”的调度上下文：

```xml
<servlet>
    <servlet-name>normal-webapp</servlet-name>
    <servlet-class>
        org.springframework.web.servlet.DispatcherServlet
    </servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>normal-webapp</servlet-name>
    <url-pattern>/api/</url-pattern>
</servlet-mapping>

```

最后，一个“安全”的上下文：

```xml
<servlet>
    <servlet-name>secure-webapp</servlet-name>
    <servlet-class>
        org.springframework.web.servlet.DispatcherServlet
    </servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>secure-webapp</servlet-name>
    <url-pattern>/s/api/</url-pattern>
</servlet-mapping>
```

## 6.组合多个上下文

除了 parent-child 之外，还有其他方法可以组合多个配置位置，拆分大上下文并更好地分离不同的关注点。我们已经看过一个例子：当我们用多个路径或包指定contextConfigLocation时，Spring 通过组合所有 bean 定义来构建一个上下文，就好像它们是按顺序写在一个 XML 文件或Java类中一样。

但是，我们可以通过其他方式达到类似的效果，甚至可以结合使用不同的方法。让我们检查一下我们的选择。

一种可能性是组件扫描，我们[在另一篇文章](https://www.baeldung.com/spring-bean-annotations#scanning)中对此进行了解释。

### 6.1. 将上下文导入另一个上下文

或者，我们可以让上下文定义导入另一个。根据情况，我们有不同种类的导入。

在Java中导入@Configuration类：

```java
@Configuration
@Import(SomeOtherConfiguration.class)
public class Config { ... }
```

在Java中加载一些其他类型的资源，例如 XML 上下文定义：

```java
@Configuration
@ImportResource("classpath:basicConfigForPropertiesTwo.xml")
public class Config { ... }
```

最后，在另一个文件中包含一个 XML 文件：

```xml
<import resource="greeting.xml" />
```

因此，我们有很多方法来组织服务、组件、控制器等，它们协作创建我们很棒的应用程序。好消息是 IDE 都能理解它们！

## 7.Spring BootWeb 应用程序

Spring Boot 会自动配置应用程序的组件，因此通常不需要考虑如何组织它们。

尽管如此，在幕后，Boot 使用了 Spring 特性，包括我们目前已经看到的特性。让我们看看几个值得注意的差异。

在嵌入式容器中运行的Spring BootWeb 应用程序[不会按设计运行任何WebApplicationInitializer](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-embedded-container-context-initializer)。

如果有必要，我们可以在 SpringBootServletInitializer 或ServletContextInitializer中编写相同的逻辑，具体取决于所选择的部署策略。

但是，对于如本文中所见添加 servlet、过滤器和侦听器，则没有必要这样做。事实上，Spring Boot 会自动将每个与 servlet 相关的 bean 注册到容器中：

```java
@Bean
public Servlet myServlet() { ... }
```

如此定义的对象根据约定进行映射：过滤器自动映射到 /，即映射到每个请求。如果我们注册一个 servlet，它被映射到 /，否则，每个 servlet 被映射到它的 bean 名称。

如果上述约定对我们不起作用，我们可以定义一个FilterRegistrationBean、ServletRegistrationBean 或ServletListenerRegistrationBean代替。这些类允许我们控制注册的精细方面。

## 八. 总结

在本文中，我们深入介绍了可用于构造和组织 Spring Web 应用程序的各种选项。

我们遗漏了一些特性，特别是对[企业应用程序中共享上下文的支持，在撰写本文时， ](https://spring.io/blog/2007/06/11/using-a-shared-parent-application-context-in-a-multi-war-spring-application/)[Spring 5](https://jira.spring.io/browse/SPR-16258)仍然缺少这些特性。