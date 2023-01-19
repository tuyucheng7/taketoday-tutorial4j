## 1. 概述

所有MVC框架都提供了一种处理视图的方法。

Spring通过视图解析器实现这一点，这使你能够在浏览器中呈现模型，而无需将实现绑定到特定的视图技术。

ViewResolver将视图名称映射到实际视图。

Spring框架附带了很多视图解析器，例如InternalResourceViewResolver、BeanNameViewResolver等等。

本教程展示了如何设置最常见的视图解析器，以及如何在同一配置中使用多个ViewResolver。

## 2. Spring Web配置

我们使用@EnableWebMvc、@Configuration和@ComponentScan标注我们的配置类：

```java

@EnableWebMvc
@ComponentScan(basePackages = {"cn.tuyucheng.web.controller"})
@Configuration
public class WebConfig implements WebMvcConfigurer {

}
```

## 3. InternalResourceViewResolver

这个ViewResolver允许我们为视图名称设置前缀或后缀等属性，以生成最终的视图页面URL：

```java
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/view/");
        bean.setSuffix(".jsp");
        return bean;
    }
}
```

为了简单起见，我们不需要控制器来处理请求。

我们只需要一个简单的jsp页面，放在配置中定义的/WEB-INF/view文件夹中：

```html

<html>
<head></head>

<body>
<h1>This is the body of the sample view</h1>
</body>
</html>
```

## 4. BeanNameViewResolver

这是ViewResolver的一个实现，它将视图名称解析为当前应用程序上下文中的bean名称。每个这样的视图都可以定义为XML或Java配置中的bean。

首先，我们将BeanNameViewResolver添加到之前的配置类中：

```java
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public BeanNameViewResolver beanNameViewResolver() {
        return new BeanNameViewResolver();
    }
}
```

定义了ViewResolver之后，我们需要定义View类型的bean，以便DispatcherServlet可以执行它来呈现视图：

```java
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public View sample() {
        return new JstlView("/WEB-INF/view/sample.jsp");
    }
}
```

这是控制器类中相应的处理程序方法：

```java

@Controller
public class SampleController {

    @GetMapping("/sample")
    public String showForm() {
        return "sample";
    }
}
```

在控制器方法中，视图名称作为“sample”返回，这意味着该处理程序方法的视图解析为包含/WEB-INF/view/sample.jsp
URL的JstlView类。

## 5. 优先级

Spring MVC还支持**多视图解析器**。

这允许你在某些情况下覆盖特定视图。我们可以通过在配置中添加多个解析器来简单地链接视图解析器。

然后，我们需要为这些解析器定义一个顺序。order属性用于定义解析器链中调用的顺序。order属性值越大，视图解析器在链中的优先级越低。

要定义顺序，我们可以将以下代码添加到视图解析器的配置中：

```text
bean.setOrder(0);
```

注意顺序优先级，InternalResourceViewResolver应该具有更高的顺序 - 因为它旨在表示非常明确的映射。
如果其他解析器具有更高的顺序，则可能永远不会调用InternalResourceViewResolver。

## 6. Spring Boot

使用Spring Boot时，WebMvcAutoConfiguration会在我们的应用程序上下文中自动配置
InternalResourceViewResolver和BeanNameViewResolver bean。

此外，为模板引擎添加相应的启动器依赖会减少我们必须进行的手动配置。

例如，通过将spring-boot-starter-thymeleaf依赖添加到我们的pom.xml中，Thymeleaf被启用，并且不需要额外的配置：

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>${spring-boot-starter-thymeleaf.version}</version>
</dependency>
```

该启动器依赖项在我们的应用程序上下文中配置一个名为thymeleafViewResolver的ThymeleafViewResolver bean。
我们可以通过提供一个同名的bean来覆盖自动配置的ThymeleafViewResolver。

Thymeleaf视图解析器通过设置视图的前缀和后缀。前缀和后缀的默认值分别是“classpath:/templates/”和“.html”。

Spring Boot还提供了一个选项，可以通过分别设置spring.thymeleaf.prefix和spring.thymeleaf.suffix属性来更改前缀和后缀的默认值。

此外，还可以添加groovy-templates、freemarker和mustache模板引擎的启动器依赖项，
我们可以通过它们来使用Spring Boot自动配置相应的视图解析器。

DispatcherServlet使用它在应用程序上下文中找到的所有视图解析器，并尝试每一个，直到得到结果，因此如果我们添加自己的视图解析器，这些视图解析器的顺序变得非常重要。

## 7. 总结

在本教程中，我们使用Java配置了一系列视图解析器。通过使用setOrder()方法，我们可以设置它们的调用顺序。