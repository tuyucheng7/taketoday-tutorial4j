## 1. 概述

在本文中，我们介绍Spring MVC中的一个核心概念控制器。

首先，我们看看典型Spring Model-View-Controller架构中前端控制器的概念。

在高层次上，以下是我们需要考虑的前端控制器的主要职责：

+ 拦截传入请求
+ 将请求的有效负载转换为数据的内部结构
+ 将数据发送到模型进行进一步处理
+ 从模型中获取处理后的数据，并将该数据发送到视图以进行渲染

下面是Spring MVC中从高层次上看的处理流程：

<img src="../assets/img.png">

可以看到，DispatcherServlet在体系结构中扮演了前端控制器的角色。

该图既适用于典型的MVC控制器，也适用于Restful控制器，只不过有一些小的差异(如下所述)。

在传统方法中，MVC应用程序不是面向服务的，因此有一个视图解析器，它根据从控制器接收到的数据呈现最终视图。

Restful应用程序被设计为面向服务并返回原始数据(通常为JSON/XML)。
由于这些应用程序不进行任何视图渲染，因此没有视图解析器，通常期望控制器直接通过HTTP响应发送数据。

## 2. Maven依赖

为了能够使用Spring MVC，首先我们需要添加Maven依赖项：

```xml

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.13</version>
</dependency>
```

## 3. Web配置

首先，我们需要构建一个简单的Web项目并进行快速的Servlet配置。

让我们看看如何在不使用web.xml的情况下设置DispatcherServlet：

```java
public class StudentControllerConfig implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
        root.register(WebConfig.class);
        root.setServletContext(sc);
        sc.addListener(new ContextLoaderListener(root));

        DispatcherServlet dv = new DispatcherServlet(root);

        ServletRegistration.Dynamic appServlet = sc.addServlet("test-mvc", dv);
        appServlet.setLoadOnStartup(1);
        appServlet.addMapping("/test/");
    }
}
```

当不使用XML时，请确保在你的类路径中有servlet-api依赖。

下面是使用web.xml的配置：

```xml

<servlet>
    <servlet-name>test-mvc</servlet-name>
    <servlet-class>
        org.springframework.web.servlet.DispatcherServlet
    </servlet-class>
    <load-on-startup>1</load-on-startup>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/test-mvc.xml</param-value>
    </init-param>
</servlet>
```

在这里，我们设置contextConfigLocation属性，指向用于加载Spring上下文的XML文件。
如果该属性不存在，Spring会搜索名为{servlet-name}-servlet.xml的文件。

在我们的例子中，“servlet-name”是test-mvc，因此，DispatcherServlet会搜索一个名为test-mvc-servlet.xml的文件。

最后，我们设置DispatcherServlet并将其映射到特定的URL，在这里完成基于前端控制器的系统：

```xml

<servlet-mapping>
    <servlet-name>test-mvc</servlet-name>
    <url-pattern>/test/</url-pattern>
</servlet-mapping>
```

在这种情况下，DispatcherServlet将拦截匹配“/test/”的所有请求。

## 4. Spring MVC配置

现在让我们看看如何使用Spring设置DispatcherServlet：

```java

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "cn.tuyucheng.taketoday.controller.controller",
        "cn.tuyucheng.taketoday.controller.config"
})
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setPrefix("/WEB-INF/");
        bean.setSuffix(".jsp");
        return bean;
    }
}
```

下面是使用XML设置DispatcherServlet，DispatcherServlet用于加载自定义控制器和其他Spring实体：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans     
        http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context-4.0.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc-4.0.xsd">

    <context:component-scan base-package="cn.tuyucheng.taketoday.controller.controller"/>
    <mvc:annotation-driven/>

    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix">
            <value>/WEB-INF/</value>
        </property>
        <property name="suffix">
            <value>.jsp</value>
        </property>
    </bean>
</beans>
```

基于这个简单的配置，框架会初始化它会在类路径中找到的任何控制器bean。

请注意，我们还定义了ViewResolver，它负责视图渲染。
这里使用的是Spring的InternalResourceViewResolver实现，
需要解析视图的名称，这意味着通过使用前缀和后缀(均在XML配置中定义)来查找相应的页面。

例如，如果控制器返回一个名为“welcome”的视图，视图解析器会尝试解析WEB-INF文件夹中名为“welcome.jsp”的页面。

## 5. MVC控制器

现在我们实现MVC控制器，这里返回的是一个ModelAndView对象，它包含一个模型映射和一个视图对象；
视图解析器使用它们来进行数据渲染：

```java

@Controller
@RequestMapping(value = "/test")
public class TestController {

    @GetMapping
    public ModelAndView getTestData() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("welcome");
        mv.getModel().put("data", "Welcome home man");

        return mv;
    }
}
```

在上面的代码中，首先我们创建了一个名为TestController的控制器并将其映射到“/test”路径。
在该类中，我们创建了一个方法，该方法返回一个ModelAndView对象并映射到一个GET请求，
因此任何以“test”结尾的URL调用都将由DispatcherServlet路由到TestController中的getTestData方法。

视图对象的名称设置为“welcome”，如上所述，视图解析器会在WEB-INF文件夹中搜索名为“welcome.jsp”的页面。

你可以通过访问“localhost:8080/test-mvc/test”来观察结果：

