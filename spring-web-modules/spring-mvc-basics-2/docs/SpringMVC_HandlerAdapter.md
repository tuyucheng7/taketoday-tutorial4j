## 1. 概述

在本文中，我们重点介绍Spring框架中可用的各种HandlerAdapter实现。

## 2. 什么是HandlerAdapter

HandlerAdapter基本上是一个接口，它在Spring MVC中以非常灵活的方式促进HTTP请求的处理。

它与HandlerMapping结合使用，HandlerMapping将方法映射到特定的URL。

然后DispatcherServlet使用HandlerAdapter调用此方法，servlet并不直接调用该方法，它基本上充当自身与处理程序对象之间的桥梁，实现松耦合设计。

让我们看看这个接口中可用的一些方法：

```java
public interface HandlerAdapter {
    boolean supports(Object handler);

    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

    long getLastModified(HttpServletRequest request, Object handler);
}
```

support方法用于检查是否支持特定的处理程序实例。在调用该接口的handle()方法之前，应首先调用此方法，以确保是否支持处理程序实例。

handle方法用于处理特定的HTTP请求，此方法负责通过传递HttpServletRequest和HttpServletResponse对象作为参数来调用处理程序。
然后，处理程序执行应用程序逻辑并返回一个ModelAndView对象，然后由DispatcherServlet处理该对象。

## 3. Maven依赖

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.13</version>
</dependency>
```

## 4. HandlerAdapter类型

### 4.1 SimpleControllerHandlerAdapter

这是Spring MVC注册的默认处理程序适配器，它处理实现Controller接口的类，并用于将请求转发到控制器对象。

如果Web应用程序仅使用控制器，那么我们不需要配置任何HandlerAdapter，因为框架使用该类作为处理请求的默认适配器。

让我们定义一个简单的控制器类，使用旧式控制器(实现Controller接口)：

```java
public class SimpleControllerHandlerAdapterExample implements Controller {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView model = new ModelAndView("Greeting");
        model.addObject("message", "Dinesh Madhwal");
        return model;
    }
}
```

类似的XML配置如下：

```xml
<!-- @formatter:off -->
<beans ...>
    <bean name="/greeting.html" class="cn.tuyucheng.taketoday.spring.controller.SimpleControllerHandlerAdapterExample"/>
    <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/" />
        <property name="suffix" value=".jsp" />
    </bean>
</beans>
<!-- @formatter:on -->
```

BeanNameUrlHandlerMapping类是此处理程序适配器的映射类。

**注意**：如果在BeanFactory中定义了自定义处理程序适配器，则不会自动注册该适配器。
因此，我们需要在上下文中明确定义它。如果没有定义它并且我们已经定义了一个自定义处理程序适配器，那么我们将得到一个异常，表示没有为处理程序指定适配器。

### 4.2 SimpleServletHandlerAdapter

此处理程序适配器允许使用任何Servlet与DispatcherServlet一起处理请求。
它通过调用其service()方法，将请求从DispatcherServlet转发到适当的Servlet类。

实现Servlet接口的bean由该适配器自动处理。默认情况下，它没有注册，我们需要像其他普通bean一样在DispatcherServlet的配置文件中注册它：

```xml
<bean name="simpleServletHandlerAdapter" class="org.springframework.web.servlet.handler.SimpleServletHandlerAdapter"/>
```

### 4.3 AnnotationMethodHandlerAdapter

这个适配器类用于执行带有@RequestMapping注解的方法，它用于根据HTTP请求方法和HTTP路径映射方法。

此适配器的映射类为DefaultAnnotationHandlerMapping，它用于在类级别处理@RequestMapping注解，AnnotationMethodHandlerAdaptor用于在方法级别进行处理。

当DispatcherServlet初始化时，框架已经注册了这两个类。但是，如果已经定义了其他处理程序适配器，那么我们也需要在配置文件中定义它。

让我们定义一个控制器类：

```java
@Controller
public class AnnotationMethodHandlerAdapterExample {

    @RequestMapping("/annotedName")
    public ModelAndView getEmployeeName() {
        ModelAndView model = new ModelAndView("Greeting");
        model.addObject("message", "Dinesh");
        return model;
    }
}
```

@Controller注解表明该类充当控制器的角色。

@RequestMapping注解将getEmployeeName()方法映射到URL “/annotedName”。

根据应用程序是使用基于Java的配置还是基于XML的配置，有2种不同的方式来配置此适配器，让我们看看使用Java配置的第一种方式：

```java
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"cn.tuyucheng.taketoday.spring.controller"})
public class ApplicationConfiguration implements WebMvcConfigurer {

    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setPrefix("/WEB-INF/");
        bean.setSuffix(".jsp");
        return bean;
    }
}
```

如果应用程序使用XML配置，那么在Web应用程序上下文XML中配置此处理程序适配器有两种不同的方法。
让我们看一下spring-servlet_AnnotationMethodHandlerAdapter.xml文件中定义的第一种方法：

```xml
<!-- @formatter:off -->
<beans ...>
    <context:component-scan base-package="cn.tuyucheng.taketoday.spring.controller" />
    <bean class="org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping"/>
    <bean class="org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter"/>
    <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/" />
        <property name="suffix" value=".jsp" />
    </bean>
</beans>
<!-- @formatter:on -->
```

<context:component-scan\>标签用于指定要扫描控制器类的包。

下面是第二种方法：

```xml
<!-- @formatter:off -->
<beans ...>
    <mvc:annotation-driven/>
    <context:component-scan base-package="cn.tuyucheng.taketoday.spring.controller" />
    <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/" />
        <property name="suffix" value=".jsp" />
    </bean>
</beans>
<!-- @formatter:on -->
```

<mvc:annotation-driven\>标签会自动将这两个类注册到Spring MVC中。
此适配器在Spring 3.2中已被弃用，并且在Spring 3.1中引入了一个名为RequestMappingHandlerAdapter的新处理程序适配器。

### 4.4 RequestMappingHandlerAdapter

这个适配器类是在Spring 3.1中引入的，在Spring 3.2中弃用了AnnotationMethodHandlerAdaptor处理程序适配器。

**它与RequestMappingHandlerMapping类一起使用，该类执行使用@RequestMapping注解标注的方法**。

RequestMappingHandlerMapping用于维护请求URI到处理程序的映射。
获得处理程序后，DispatcherServlet将请求分派到适当的处理程序适配器，然后调用handlerMethod()。

在3.1之前的Spring版本中，类级和方法级映射在两个不同的阶段进行处理。

第一阶段是通过DefaultAnnotationHandlerMapping选择控制器，第二阶段是通过AnnotationMethodHandlerAdapter调用实际方法。

从Spring 3.1版本开始，只有一个阶段，包括识别控制器以及需要调用哪个方法来处理请求。

让我们定义一个简单的控制器类：

```java
@Controller
public class RequestMappingHandlerAdapterExample {

    @RequestMapping("/requestName")
    public ModelAndView getEmployeeName() {
        ModelAndView model = new ModelAndView("Greeting");
        model.addObject("message", "Madhwal");
        return model;
    }
}
```

根据应用程序是使用基于Java的配置还是使用基于XML的配置，有2种不同的方式来配置此适配器。

让我们看一下使用Java配置的第一种方法：

```java
@ComponentScan("cn.tuyucheng.taketoday.spring.controller")
@Configuration
@EnableWebMvc
public class ServletConfig implements WebMvcConfigurer {

    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setPrefix("/WEB-INF/");
        bean.setSuffix(".jsp");
        return bean;
    }
}
```

如果应用程序使用XML配置，那么在Web应用程序上下文XML中配置此处理程序适配器有两种不同的方法。
让我们看一下spring-servlet_RequestMappingHandlerAdapter.xml文件中定义的第一种方法：

```xml
<!-- @formatter:off -->
<beans ...>
    <context:component-scan base-package="cn.tuyucheng.taketoday.spring.controller" />
    
    <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"/>
    
    <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"/>
    
    <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/" />
        <property name="suffix" value=".jsp" />
    </bean>
</beans>
<!-- @formatter:on -->
```

下面是第二种方法：

```xml
<!-- @formatter:off -->
<beans ...>
    <mvc:annotation-driven />
    
    <context:component-scan base-package="cn.tuyucheng.taketoday.spring.controller" />
    
    <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/" />
        <property name="suffix" value=".jsp" />
    </bean>
</beans>
<!-- @formatter:on -->
```

这个标签会自动向Spring MVC注册这两个类。

如果我们需要自定义RequestMappingHandlerMapping，那么我们需要从应用上下文XML中去掉这个标签，并在应用程序上下文XML中手动配置它。

### 4.5 HttpRequestHandlerAdapter

此处理程序适配器用于处理HttpRequests的处理程序，
它实现了HttpRequestHandler接口，该接口包含单个用于处理请求和生成响应的handleRequest()方法。

此方法的返回类型为void，它不会返回其他处理程序适配器生成的ModelAndView返回类型。
基本上它用于生成二进制响应，并且不会生成要渲染的视图。

## 5. 总结

在本文中，我们介绍了Spring框架中可用的各种类型的处理程序适配器。

使用默认配置对于我们来说是足够的，但是当我们需要理解基础知识时，了解框架的灵活性是非常有必要的。