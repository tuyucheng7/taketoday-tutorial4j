## 1. 概述

在本教程中，我们将学习如何在 Spring 中设置 REST，包括控制器和 HTTP 响应代码、有效负载编组的配置以及内容协商。

## 延伸阅读：

## [使用 Spring @ResponseStatus 设置 HTTP 状态码](https://www.baeldung.com/spring-response-status)

查看 @ResponseStatus 注解以及如何使用它来设置响应状态代码。

[阅读更多](https://www.baeldung.com/spring-response-status)→

## [Spring @Controller 和@RestController 注解](https://www.baeldung.com/spring-controller-vs-restcontroller)

了解 Spring MVC 中@Controller 和@RestController 注解的区别。

[阅读更多](https://www.baeldung.com/spring-controller-vs-restcontroller)→

## 2. 了解 Spring 中的 REST 

Spring 框架支持两种创建 RESTful 服务的方式：

-   将 MVC 与ModelAndView结合使用
-   使用 HTTP 消息转换器

ModelAndView方法较旧且有更好的文档记录，但也更冗长且配置繁重。它试图将 REST 范式硬塞进旧模型，这并非没有问题。Spring 团队理解这一点，并从 Spring 3.0 开始提供一流的 REST 支持。

基于HttpMessageConverter 和注解的新方法更加轻量级且易于实现。配置是最少的，它为我们对 RESTful 服务的期望提供了合理的默认值。

## 3.Java配置

```java
@Configuration
@EnableWebMvc
public class WebConfig{
   //
}
```

新的@EnableWebMvc注解做了一些有用的事情；具体来说，对于 REST，它会检测类路径中是否存在 Jackson 和 JAXB 2，并自动创建和注册默认的 JSON 和 XML 转换器。注解的功能等同于 XML 版本：

<mvc:注解驱动/>

这是一条捷径，虽然在很多情况下可能有用，但并不完美。当我们需要更复杂的配置时，可以去掉注解，直接扩展WebMvcConfigurationSupport。

### 3.1. 使用弹簧引导

如果我们使用@SpringBootApplication注解，并且spring-webmvc 库在类路径中，那么 @EnableWebMvc注解会自动添加[一个默认的自动配置](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-auto-configuration)。

我们仍然可以通过 在@Configuration 注解类上实现WebMvcConfigurer 接口 来向此配置添加 MVC 功能。我们还可以使用 WebMvcRegistrationsAdapter实例来提供我们自己的 RequestMappingHandlerMapping、RequestMappingHandlerAdapter或ExceptionHandlerExceptionResolver 实现。

最后，如果我们想放弃 Spring Boot 的 MVC 功能并声明自定义配置，我们可以通过使用@EnableWebMvc注解来实现。

## 4. 测试 Spring 上下文

从 Spring 3.1 开始，我们获得了对@Configuration类的一流测试支持：

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

我们使用@ContextConfiguration注解指定 Java 配置类。新的AnnotationConfigContextLoader从@Configuration类加载 bean 定义。

请注意，WebConfig配置类未包含在测试中，因为它需要在未提供的 Servlet 上下文中运行。

### 4.1. 使用弹簧引导

Spring Boot 提供了几个注解来以更直观的方式为我们的测试设置 Spring ApplicationContext 。

我们可以只加载应用程序配置的特定部分，也可以模拟整个上下文启动过程。

例如， 如果我们想在不启动服务器的情况下创建整个上下文，我们可以使用@SpringBootTest注解。

有了它，我们就可以添加@AutoConfigureMockMvc 来注入 MockMvc 实例并发送 HTTP 请求：

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

为了避免创建整个上下文并只测试我们的 MVC 控制器，我们可以使用 @WebMvcTest：

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

我们可以在[“在 Spring Boot 中测试”一文](https://www.baeldung.com/spring-boot-testing)中找到有关此主题的详细信息。

## 5. 控制器

@RestController是 RESTful API 整个 Web 层中的核心工件。出于本文的目的，控制器正在对一个简单的 REST 资源Foo进行建模：

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

如我们所见，我们正在使用一个简单的 Guava 风格的RestPreconditions实用程序：

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

Controller 实现是非公开的，因为它不需要。

通常，控制器是依赖链中的最后一个。它从 Spring 前端控制器(DispatcherServlet)接收 HTTP 请求，并简单地将它们委托给服务层。如果没有必须通过直接引用注入或操作控制器的用例，那么我们可能不希望将其声明为公共。

请求映射很简单。与任何控制器一样，映射的实际值以及 HTTP 方法决定了请求的目标方法。@RequestBody会将方法的参数绑定到 HTTP 请求的主体，而@ResponseBody对响应和返回类型做同样的事情。

@RestController 是在我们的类中包含 @ResponseBody 和 @Controller注解的[简写](https://www.baeldung.com/spring-controller-vs-restcontroller)。

他们还确保使用正确的 HTTP 转换器对资源进行编组和解组。将进行内容协商以选择将使用哪个活动转换器，主要基于Accept标头，尽管也可以使用其他 HTTP 标头来确定表示形式。

## 6.映射HTTP响应代码

HTTP 响应的状态代码是 REST 服务最重要的部分之一，这个主题很快就会变得非常复杂。获得这些权利可能是服务成败的关键。

### 6.1. 未映射的请求

如果 Spring MVC 收到一个没有映射的请求，它会认为该请求不允许，并返回一个 405 METHOD NOT ALLOWED 返回给客户端。

在向客户端返回405以指定允许哪些操作时包含Allow HTTP 标头也是一种很好的做法。这是 Spring MVC 的标准行为，不需要任何额外的配置。

### 6.2. 有效的映射请求

对于任何确实有映射的请求，如果没有另外指定其他状态代码，Spring MVC 认为请求有效并以 200 OK 响应。

正因为如此，控制器为create、update和delete操作声明了不同的@ResponseStatus，但为get声明了不同的 @ResponseStatus ，它确实应该返回默认的 200 OK。

### 6.3. 客户端错误

在客户端错误的情况下，自定义异常被定义并映射到适当的错误代码。

简单地从 Web 层的任何层抛出这些异常将确保 Spring 在 HTTP 响应上映射相应的状态代码：

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
   //
}
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
   //
}
```

这些异常是 REST API 的一部分，因此，我们应该只在与 REST 对应的适当层中使用它们；例如，如果存在 DAO/DAL 层，则不应直接使用异常。

另请注意，这些不是已检查的异常，而是符合 Spring 实践和习惯用法的运行时异常。

### 6.4. 使用@ExceptionHandler

将自定义异常映射到特定状态代码的另一种选择是在控制器中使用@ExceptionHandler注解。该方法的问题在于注解仅适用于定义它的控制器。这意味着我们需要在每个控制器中单独声明它们。

当然，在 Spring 和 Spring Boot 中有更多的[方法来处理错误，它们提供了更大的灵活性。](https://www.baeldung.com/exception-handling-for-rest-with-spring)

## 7. 额外的 Maven 依赖 

除了[标准 Web 应用程序所需的](https://www.baeldung.com/spring-with-maven#mvc)spring-webmvc依赖项之外，我们还需要为 REST API 设置内容编组和解组：

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

这些是我们将用于将 REST 资源的表示形式转换为 JSON 或 XML 的库。

### 7.1. 使用弹簧引导

如果我们要检索JSON格式的资源，Spring Boot提供了对不同库的支持，即Jackson、Gson和JSON-B。

我们可以通过简单地在类路径中包含任何映射库来执行自动配置。

通常，如果我们正在开发 Web 应用程序，我们只需添加 spring-boot-starter-web依赖项并依赖它来将所有必要的工件包含到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.2</version>
</dependency>
```

Spring Boot 默认使用 Jackson。

如果我们想以 XML 格式序列化我们的资源，我们必须将 Jackson XML 扩展(jackson-dataformat-xml)添加到我们的依赖项中，或者通过使用回退到 JAXB 实现(JDK 中默认提供) 我们资源上的@XmlRootElement注解。

## 八、总结

本文说明了如何使用 Spring 和基于 Java 的配置来实现和配置 REST 服务。

在本系列的下一篇文章中，我们将重点介绍[API 的可发现性](https://www.baeldung.com/restful-web-service-discoverability)、高级内容协商以及使用资源的其他表示形式。