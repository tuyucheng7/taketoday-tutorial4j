## 1. 概述

在本教程中，我们将学习如何**在Spring中设置REST**，包括控制器和HTTP响应代码、有效负载编组的配置以及内容协商。

## 延伸阅读

### [使用Spring @ResponseStatus设置HTTP状态码](https://www.baeldung.com/spring-response-status)

了解@ResponseStatus注解以及如何使用它来设置响应状态代码。

[阅读更多](https://www.baeldung.com/spring-response-status)→

### [Spring @Controller和@RestController注解](https://www.baeldung.com/spring-controller-vs-restcontroller)

了解Spring MVC中@Controller和@RestController注解的区别。

[阅读更多](https://www.baeldung.com/spring-controller-vs-restcontroller)→

## 2. 了解Spring REST

Spring框架支持两种创建RESTful服务的方式：

-   将MVC与ModelAndView结合使用
-   使用HTTP消息转换器

ModelAndView方法较老且有更好的文档记录，但也更冗长且配置繁重。它试图将REST范式硬塞进旧模型，这并非没有问题。Spring团队理解这一点，并从Spring 3.0开始提供一流的REST支持。

**基于HttpMessageConverter和注解的新方法更加轻量级且易于实现**。配置是最少的，它为我们对RESTful服务的期望提供了合理的默认值。

## 3. Java配置

```java
@Configuration
@EnableWebMvc
public class WebConfig{
    // ...
}
```

新的@EnableWebMvc注解做了一些有用的事情；具体来说，对于REST，它会检测类路径中是否存在Jackson和JAXB 2，并自动创建和注册默认的JSON和XML转换器。注解的功能等同于XML版本：

```xml
<mvc:annotation-driven/>
```

这是一条捷径，虽然它在许多情况下可能有用，但并不完美。当我们需要更复杂的配置时，可以去掉注解，直接扩展WebMvcConfigurationSupport。

### 3.1 使用Spring Boot

如果我们使用@SpringBootApplication注解，并且spring-webmvc库位于类路径上，那么@EnableWebMvc注解会自动添加一个[默认的自动配置](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-auto-configuration)。

我们仍然可以通过在@Configuration注解类上实现WebMvcConfigurer接口来向此配置添加MVC功能。我们还可以使用WebMvcRegistrationsAdapter实例来提供我们自己的RequestMappingHandlerMapping、RequestMappingHandlerAdapter或ExceptionHandlerExceptionResolver实现。

最后，如果我们想放弃Spring Boot的MVC功能并声明自定义配置，我们可以通过使用@EnableWebMvc注解来实现。

## 4. 测试Spring上下文

从Spring 3.1开始，我们获得了对@Configuration类的一流测试支持：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
      classes = {WebConfig.class, PersistenceConfig.class},
      loader = AnnotationConfigContextLoader.class)
public class SpringContextIntegrationTest {

    @Test
    public void contextLoads(){
        // When
    }
}
```

我们使用@ContextConfiguration注解指定Java配置类。新的AnnotationConfigContextLoader从@Configuration类加载bean定义。

请注意，WebConfig配置类未包含在测试中，因为它需要在未提供的Servlet上下文中运行。

### 4.1 使用Spring Boot

Spring Boot提供了几个注解来以更直观的方式为我们的测试设置Spring ApplicationContext。

我们可以只加载应用程序配置的特定切片，也可以模拟整个上下文启动过程。

例如，如果我们想在不启动服务器的情况下创建整个上下文，我们可以使用@SpringBootTest注解。

有了它，我们就可以添加@AutoConfigureMockMvc来注入MockMvc实例并发送HTTP请求：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FooControllerAppIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenTestApp_thenEmptyResponse() throws Exception {
        this.mockMvc.perform(get("/foos")
              .andExpect(status().isOk())
              .andExpect(...);
    }
}
```

为了避免创建整个上下文并只测试我们的MVC控制器，我们可以使用@WebMvcTest：

```java
@RunWith(SpringRunner.class)
@WebMvcTest(FooController.class)
public class FooControllerWebLayerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IFooService service;

    @Test()
    public void whenTestMvcController_thenRetrieveExpectedResult() throws Exception {
        // ...

        this.mockMvc.perform(get("/foos")
              .andExpect(...);
    }
}
```

我们可以在[Spring Boot中的测试](https://www.baeldung.com/spring-boot-testing)一文中找到有关此主题的详细信息。

## 5. 控制器

**@RestController是RESTful API整个Web层中的核心工件**。出于本文的目的，控制器只是对一个简单的REST资源Foo进行建模：

```java
@RestController
@RequestMapping("/foos")
class FooController {

    @Autowired
    private IFooService service;

    @GetMapping
    public List<Foo> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public Foo findById(@PathVariable("id") Long id) {
        return RestPreconditions.checkFound(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody Foo resource) {
        Preconditions.checkNotNull(resource);
        return service.create(resource);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable( "id" ) Long id, @RequestBody Foo resource) {
        Preconditions.checkNotNull(resource);
        RestPreconditions.checkNotNull(service.getById(resource.getId()));
        service.update(resource);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        service.deleteById(id);
    }
}
```

如我们所见，我们使用的是一个简单的，Guava风格的RestPreconditions实用程序：

```java
public class RestPreconditions {
    public static <T> T checkFound(T resource) {
        if (resource == null) {
            throw new MyResourceNotFoundException();
        }
        return resource;
    }
}
```

**控制器实现是非公共的，因为它不需要**。

通常，控制器是依赖链中的最后一个。它接收来自Spring前端控制器(DispatcherServlet)的HTTP请求，并简单地将它们委托给服务层。如果没有必须通过直接引用注入或操作控制器的用例，那么我们可能不希望将其声明为public。

请求映射非常简单。**与任何控制器一样，映射的实际值以及HTTP方法决定了请求的目标方法**。@RequestBody会将方法的参数绑定到HTTP请求的主体，而@ResponseBody对响应和返回类型执行相同的操作。

**@RestController是我们类中包含的@ResponseBody和@Controller注解的[简写](https://www.baeldung.com/spring-controller-vs-restcontroller)**。

它们还确保使用正确的HTTP转换器对资源进行编组和解组。将进行内容协商以选择将使用哪个激活的转换器，主要基于Accept标头，尽管也可以使用其他HTTP标头来确定表示形式。

## 6. 映射HTTP响应代码

HTTP响应的状态代码是REST服务最重要的部分之一，这个主题很快就会变得非常复杂。正确处理这些可能是服务成败的原因。

### 6.1 未映射的请求

如果Spring MVC收到一个没有映射的请求，它会认为该请求是不允许的，并返回一个405 METHOD NOT ALLOWED给客户端。

在向客户端返回405时包含Allow HTTP标头以指定允许的操作也是一种很好的做法。这是Spring MVC的标准行为，不需要任何额外的配置。

### 6.2 有效的映射请求

对于任何具有映射的请求，如果没有另外指定其他状态代码，Spring MVC认为请求有效并以200 OK响应。

正因为如此，控制器为create、update和delete操作声明了不同的@ResponseStatus，但为get声明了不同的@ResponseStatus，它应该返回默认的200 OK。

### 6.3 客户端错误

**如果出现客户端错误，将定义自定义异常并将其映射到相应的错误代码**。

只需从Web层的任何层抛出这些异常，即可确保Spring在HTTP响应上映射相应的状态代码：

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    // ...
}

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    // ...
}
```

这些异常是REST API的一部分，因此，我们应该只在与REST对应的适当层中使用它们；例如，如果存在DAO/DAL层，则不应直接使用异常。

另请注意，这些不是受检异常，而是符合Spring实践和习惯用法的运行时异常。

### 6.4 使用@ExceptionHandler

将自定义异常映射到特定状态代码的另一种选择是在控制器中使用@ExceptionHandler注解。该方法的问题在于注解仅适用于定义它的控制器。这意味着我们需要在每个控制器中单独声明它们。

当然，在Spring和Spring Boot中[有更多的方法来处理错误](https://www.baeldung.com/exception-handling-for-rest-with-spring)，它们提供了更大的灵活性。

## 7. 额外的Maven依赖

除了[标准Web应用程序](https://www.baeldung.com/spring-with-maven#mvc)所需的spring-webmvc依赖项之外，我们还需要为REST API设置内容编组和解组：

```xml
<dependencies>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.9.8</version>
    </dependency>
    <dependency>
        <groupId>javax.xml.bind</groupId>
        <artifactId>jaxb-api</artifactId>
        <version>2.3.1</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

这些是我们将用于将REST资源的表示形式转换为JSON或XML的库。

### 7.1 使用Spring Boot

如果我们要检索JSON格式的资源，Spring Boot提供了对不同库的支持，即Jackson、Gson和JSON-B。

我们可以通过简单地在类路径中包含任何映射库来执行自动配置。

通常，如果我们正在开发Web应用程序，**我们只需添加spring-boot-starter-web依赖项并依赖它来将所有必要的工件包含到我们的项目中**：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.2</version>
</dependency>
```

Spring Boot默认使用Jackson。

如果我们想以XML格式序列化我们的资源，我们必须将Jackson XML扩展(jackson-dataformat-xml)添加到我们的依赖项中，或者通过在资源上使用@XmlRootElement注解回退到JAXB实现(JDK中默认提供)。

## 8. 总结

本文说明了如何使用Spring和基于Java的配置来实现和配置REST服务。

在本系列的下一篇文章中，我们将重点介绍[API的可发现性](https://www.baeldung.com/restful-web-service-discoverability)、高级内容协商以及使用资源的其他表示形式。