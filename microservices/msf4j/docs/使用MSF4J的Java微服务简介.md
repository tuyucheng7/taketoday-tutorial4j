## 1. 概述

在本教程中，我们将展示**使用MSF4J框架的微服务开发**。

这是一个轻量级工具，它提供了一种简单的方法来构建各种专注于高性能的服务。

## 2. Maven依赖

我们需要比平时更多的Maven配置来构建基于MSF4J的微服务。这个框架的简单性和强大性是有代价的：**基本上，我们需要定义一个父工件，以及主类**：

```xml
<parent>
    <groupId>org.wso2.msf4j</groupId>
    <artifactId>msf4j-service</artifactId>
    <version>2.6.0</version>
</parent>

<properties>
    <microservice.mainClass>
        cn.tuyucheng.taketoday.msf4j.Application
    </microservice.mainClass>
</properties>
```

可以在Maven Central上找到最新版本的[msf4j-service](https://central.sonatype.com/artifact/org.wso2.msf4j/msf4j-service/2.8.11)。

接下来，我们将展示三种不同的微服务场景。首先是一个简约示例，然后是一个RESTful API，最后是一个Spring集成示例。

## 3. 基础项目

### 3.1 简单的API

我们将发布一个简单的Web资源。

该服务由一个使用一些注解的类提供，其中每个方法处理一个请求。通过这些注解，我们设置了每次请求所需要的方法、路径和参数。

返回的内容类型只是纯文本：

```java
@Path("/")
public class SimpleService {

    @GET
    public String index() {
        return "Default content";
    }

    @GET
    @Path("/say/{name}")
    public String say(@PathParam("name") String name) {
        return "Hello " + name;
    }
}
```

请记住，使用的所有类和注解都只是[标准的JAX-RS](http://www.oracle.com/technetwork/java/javaee/tech/jax-rs-159890.html)元素，我们已经在[本文](https://www.baeldung.com/jax-rs-spec-and-implementations)中介绍过这些元素。

### 3.2 应用程序

**我们可以使用这个主类启动微服务**，在该主类中我们设置、部署和运行之前定义的服务：

```java
public class Application {
    public static void main(String[] args) {
        new MicroservicesRunner()
              .deploy(new SimpleService())
              .start();
    }
}
```

如果需要，我们可以在此处链接deploy调用以同时运行多个服务：

```java
new MicroservicesRunner()
    .deploy(new SimpleService())
    .deploy(new ComplexService())
    .start()
```

### 3.3 运行微服务

要运行MSF4J微服务，我们有几个选项：

1.  在IDE上，作为Java应用程序运行
2.  运行生成的jar包

启动后，你可以在[http://localhost:9090](http://localhost:9090)看到结果。

### 3.4 启动配置

只需在启动代码中添加一些子句，我们就可以通过多种方式调整配置。

例如，我们可以为请求添加任何类型的拦截器：

```java
new MicroservicesRunner()
    .addInterceptor(new MetricsInterceptor())
    .deploy(new SimpleService())
    .start();
```

或者，我们可以添加一个全局拦截器，例如用于身份验证的拦截器：

```java
new MicroservicesRunner()
    .addGlobalRequestInterceptor(newUsernamePasswordSecurityInterceptor())
    .deploy(new SimpleService())
    .start();
```

或者，如果我们需要会话管理，我们可以设置一个会话管理器：

```java
new MicroservicesRunner()
    .deploy(new SimpleService())
    .setSessionManager(new PersistentSessionManager()) 
    .start();
```

有关每个场景的更多详细信息并查看一些工作示例，请查看MSF4J的[官方GitHub](https://github.com/wso2/msf4j/tree/master/samples)仓库。

## 4. 构建API微服务

我们已经展示了最简单的例子。现在我们将转向一个更现实的项目。

这一次，我们展示了如何使用所有典型的CRUD操作构建一个API来管理食物存储库。

### 4.1 模型

模型只是一个代表一顿饭的简单POJO：

```java
public class Meal {
    private String name;
    private Float price;

    // getters and setters
}
```

### 4.2 API

我们将API构建为Web控制器。使用标准注解，我们使用以下内容设置每个函数：

-   URL路径
-   HTTP方法：GET、POST等
-   输入(@Consumes)内容类型
-   输出(@Produces)内容类型

因此，让我们为每个标准CRUD操作创建一个方法：

```java
@Path("/menu")
public class MenuService {

    private List<Meal> meals = new ArrayList<Meal>();

    @GET
    @Path("/")
    @Produces({ "application/json" })
    public Response index() {
        return Response.ok()
              .entity(meals)
              .build();
    }

    @GET
    @Path("/{id}")
    @Produces({ "application/json" })
    public Response meal(@PathParam("id") int id) {
        return Response.ok()
              .entity(meals.get(id))
              .build();
    }

    @POST
    @Path("/")
    @Consumes("application/json")
    @Produces({ "application/json" })
    public Response create(Meal meal) {
        meals.add(meal);
        return Response.ok()
              .entity(meal)
              .build();
    }

    // other CRUD operations ...
}
```

### 4.3 数据转换功能

**MSF4J提供对不同数据转换库的支持**，例如GSON(默认提供)和Jackson(通过[msf4j-feature](https://search.maven.org/classic/#search|ga|1|msf4j-feature)依赖项)。例如，我们可以明确地使用GSON：

```java
@GET
@Path("/{id}")
@Produces({ "application/json" })
public String meal(@PathParam("id") int id) {
    Gson gson = new Gson();
    return gson.toJson(meals.get(id));
}
```

顺便说一下，请注意我们在@Consumes和@Produces注解中都使用了花括号，因此我们可以设置多个MIME类型。

### 4.4 运行API微服务

我们通过发布MenuService的Application类运行微服务，就像我们在前面的示例中所做的那样。

启动后，你可以在[http://localhost:9090/menu](http://localhost:9090/menu)看到结果。

## 5. MSF4J和Spring

**我们还可以在我们基于MSF4J的微服务中应用Spring**，从中我们将获得它的依赖注入功能。

### 5.1 Maven依赖项

我们必须向先前的Maven配置添加适当的依赖项以添加Spring和Mustache支持：

```xml
<dependencies>
    <dependency>
        <groupId>org.wso2.msf4j</groupId>
        <artifactId>msf4j-spring</artifactId>
        <version>2.6.1</version>
    </dependency>
    <dependency>
        <groupId>org.wso2.msf4j</groupId>
        <artifactId>msf4j-mustache-template</artifactId>
        <version>2.6.1</version>
    </dependency>
</dependencies>
```

可以在Maven Central上找到最新版本的[msf4j-spring](https://central.sonatype.com/artifact/org.wso2.msf4j/msf4j-spring/2.8.11)和[msf4j-mustache-template](https://central.sonatype.com/artifact/org.wso2.msf4j/msf4j-mustache-template/2.8.11)。

### 5.2 API

这个API只是一个简单的服务，使用一个模拟MealRepository。请注意我们如何**使用Spring注解进行自动装配**并将此类设置为Spring服务组件。

```java
@Service
public class MealService {

    @Autowired
    private MealRepository mealRepository;

    public Meal find(int id) {
        return mealRepository.find(id);
    }

    public List<Meal> findAll() {
        return mealRepository.findAll();
    }

    public void create(Meal meal) {
        mealRepository.create(meal);
    }
}
```

### 5.3 控制器

我们将控制器声明为组件，Spring通过自动装配提供服务。第一个方法展示了如何提供一个Mustache模板，第二个方法返回一个JSON资源：

```java
@Component
@Path("/meal")
public class MealResource {

    @Autowired
    private MealService mealService;

    @GET
    @Path("/")
    public Response all() {
        Map map = Collections.singletonMap("meals", mealService.findAll());
        String html = MustacheTemplateEngine.instance()
              .render("meals.mustache", map);
        return Response.ok()
              .type(MediaType.TEXT_HTML)
              .entity(html)
              .build();
    }

    @GET
    @Path("/{id}")
    @Produces({ "application/json" })
    public Response meal(@PathParam("id") int id) {
        return Response.ok()
              .entity(mealService.find(id))
              .build();
    }
}
```

### 5.4 主程序

在Spring场景中，我们是这样启动微服务的：

```java
public class Application {

    public static void main(String[] args) {
        MSF4JSpringApplication.run(Application.class, args);
    }
}
```

**启动后，我们可以在[http://localhost:8080/meals](http://localhost:8080/meals)看到结果**。默认端口在Spring项目中有所不同，但我们可以将其设置为我们想要的任何端口。

### 5.5 配置Bean

要启用特定设置，包括拦截器和会话管理，我们可以添加配置bean。

例如，这更改了微服务的默认端口：

```java
@Configuration
public class PortConfiguration {

    @Bean
    public HTTPTransportConfig http() {
        return new HTTPTransportConfig(9090);
    }
}
```

## 6. 总结

在本文中，我们介绍了MSF4J框架，应用不同的场景来构建基于Java的微服务。

围绕这个概念有很多议论，但[已经设置了一些理论背景](https://martinfowler.com/articles/microservices.html)，并且MSF4J提供了一种方便和标准化的方式来应用这种模式。

此外，要进一步阅读，请查看[使用Eclipse Microprofile构建微服务](https://www.baeldung.com/eclipse-microprofile)，当然还有我们关于[使用Spring Boot和Spring Cloud构建Spring微服务](https://www.baeldung.com/spring-microservices-guide)的指南。