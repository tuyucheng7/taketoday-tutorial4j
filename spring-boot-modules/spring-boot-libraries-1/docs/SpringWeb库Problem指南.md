## 1. 概述

在本教程中，我们将探讨**如何使用[Problem Spring Web库生成](https://github.com/zalando/problem-spring-web)application/problem+json响应**，这个库可以帮助我们避免与错误处理相关的重复性任务。

通过将Problem Spring Web集成到我们的Spring Boot应用程序中，我们可以**简化我们在项目中处理异常并相应地生成响应的方式**。

## 2. Problem库

[Problem](https://github.com/zalando/problem)是一个小型库，其目的是标准化基于Java的Rest API向其消费者表达错误的方式。

Problem是我们想要通知的任何错误的抽象，它包含有关错误的方便信息。让我们看看Problem响应的默认表示形式：

```json
{
    "title": "Not Found",
    "status": 404
}
```

在这种情况下，status和title足以描述错误，但是，我们也可以添加对它的详细描述：

```json
{
    "title": "Service Unavailable",
    "status": 503,
    "detail": "Database not reachable"
}
```

我们还可以创建适合我们需要的自定义Problem对象：

```java
Problem.builder()
      .withType(URI.create("https://example.org/out-of-stock"))
      .withTitle("Out of Stock")
      .withStatus(BAD_REQUEST)
      .withDetail("Item B00027Y5QG is no longer available")
      .with("product", "B00027Y5QG")
      .build();
```

在本教程中，我们将重点介绍Spring Boot项目的Problem库实现。

## 3. Problem Spring Web设置

由于这是一个基于Maven的项目，因此我们将[problem-spring-web](https://search.maven.org/search?q=g:"org.zalando" AND a:"problem-spring-web")依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>problem-spring-web</artifactId>
    <version>0.23.0</version>
</dependency>
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.4.0</version> 
</dependency>
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-security</artifactId>
    <version>2.4.0</version>  
</dependency>
```

我们还需要[spring-boot-starter-web](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-web)和[spring-boot-starter-security](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-security)依赖项，Spring Security从problem-spring-web的0.23.0版本开始是必需的。

## 4. 基本配置

作为我们的第一步，我们需要禁用白标签错误页面，以便我们能够看到我们的自定义错误表示：

```java
@EnableAutoConfiguration(exclude = ErrorMvcAutoConfiguration.class)
```

现在，让我们在ObjectMapper bean中注册一些必需的组件：

```java
@Bean
public ObjectMapper objectMapper() {
    return new ObjectMapper()
      .registerModules(new ProblemModule(), new ConstraintViolationProblemModule());
}
```

之后，我们需要将以下属性添加到application.properties文件中：

```properties
spring.resources.add-mappings=false
spring.mvc.throw-exception-if-no-handler-found=true
spring.http.encoding.force=true
```

最后，我们需要实现ProblemHandling接口：

```java
@ControllerAdvice
public class ExceptionHandler implements ProblemHandling {}
```

## 5. 高级配置

除了基本配置之外，我们还可以配置我们的项目来处理与安全相关的Problem，第一步是创建一个配置类以启用与Spring Security的库集成：

```java
@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration {

    @Autowired
    private SecurityProblemSupport problemSupport;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Other security-related configuration
        http.exceptionHandling()
              .authenticationEntryPoint(problemSupport)
              .accessDeniedHandler(problemSupport);
        return http.build();
    }
}
```

最后，我们需要为与安全相关的异常创建一个异常处理程序：

```java
@ControllerAdvice
public class SecurityExceptionHandler implements SecurityAdviceTrait {}
```

## 6. REST控制器

配置完我们的应用程序后，我们创建一个RESTful控制器：

```java
@RestController
@RequestMapping("/tasks")
public class ProblemDemoController {

    private static final Map<Long, Task> MY_TASKS;

    static {
        MY_TASKS = new HashMap<>();
        MY_TASKS.put(1L, new Task(1L, "My first task"));
        MY_TASKS.put(2L, new Task(2L, "My second task"));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Task> getTasks() {
        return new ArrayList<>(MY_TASKS.values());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Task getTasks(@PathVariable("id") Long taskId) {
        if (MY_TASKS.containsKey(taskId)) {
            return MY_TASKS.get(taskId);
        } else {
            throw new TaskNotFoundProblem(taskId);
        }
    }

    @PutMapping("/{id}")
    public void updateTask(@PathVariable("id") Long id) {
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable("id") Long id) {
        throw new AccessDeniedException("You can't delete this task");
    }
}
```

在这个控制器中，我们故意抛出了一些异常，这些异常将自动转换为Problem对象，以生成包含故障详细信息的application/problem+json响应。

现在，让我们谈谈内置的建议特征以及如何创建自定义Problem实现。

## 7. 内置建议特征

advice trait是一个小型异常处理程序，用于捕获异常并返回正确的Problem对象。

有针对常见异常的内置建议特征，因此，我们可以通过简单地抛出异常来使用它们：

```java
throw new UnsupportedOperationException();
```

结果，我们将得到响应：

```json
{
    "title": "Not Implemented",
    "status": 501
}
```

由于我们还配置了与Spring Security的集成，因此我们能够抛出与安全相关的异常：

```java
throw new AccessDeniedException("You can't delete this task");
```

并得到正确的响应：

```json
{
    "title": "Forbidden",
    "status": 403,
    "detail": "You can't delete this task"
}
```

## 8. 创建自定义Problem

可以创建Problem的自定义实现，我们只需要扩展AbstractThrowableProblem类：

```java
public class TaskNotFoundProblem extends AbstractThrowableProblem {

    private static final URI TYPE
          = URI.create("https://example.org/not-found");

    public TaskNotFoundProblem(Long taskId) {
        super(TYPE, "Not found", Status.NOT_FOUND, String.format("Task '%s' not found", taskId));
    }
}
```

我们可以抛出我们的自定义Problem，如下所示：

```java
if (MY_TASKS.containsKey(taskId)) {
    return MY_TASKS.get(taskId);
} else {
    throw new TaskNotFoundProblem(taskId);
}
```

作为抛出TaskNotFoundProblem Problem的结果，我们将得到：

```json
{
    "type": "https://example.org/not-found",
    "title": "Not found",
    "status": 404,
    "detail": "Task '3' not found"
}
```

## 9. 处理堆栈跟踪

如果我们想在响应中包含堆栈跟踪，我们需要相应地配置我们的ProblemModule：

```java
ObjectMapper mapper = new ObjectMapper()
      .registerModule(new ProblemModule().withStackTraces());
```

默认情况下禁用cause链，但我们可以通过覆盖行为来轻松启用它：

```java
@ControllerAdvice
class ExceptionHandling implements ProblemHandling {

    @Override
    public boolean isCausalChainsEnabled() {
        return true;
    }
}
```

启用这两个功能后，我们将得到类似于以下内容的响应：

```json
{
    "title": "Internal Server Error",
    "status": 500,
    "detail": "Illegal State",
    "stacktrace": [
        "org.example.ExampleRestController.newIllegalState(ExampleRestController.java:96)",
        "org.example.ExampleRestController.nestedThrowable(ExampleRestController.java:91)"
    ],
    "cause": {
        "title": "Internal Server Error",
        "status": 500,
        "detail": "Illegal Argument",
        "stacktrace": [
            "org.example.ExampleRestController.newIllegalArgument(ExampleRestController.java:100)",
            "org.example.ExampleRestController.nestedThrowable(ExampleRestController.java:88)"
        ],
        "cause": {
            // ....
        }
    }
}
```

## 10. 总结

在本文中，我们探讨了如何使用Problem Spring Web库使用application/problem+json响应创建包含错误详细信息的响应，并学习了如何在我们的Spring Boot应用程序中配置库并创建Problem对象的自定义实现。