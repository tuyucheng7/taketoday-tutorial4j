## 1. 概述

Spring Web Flux是一个使用响应式原理构建的新函数式Web框架。

在本教程中，我们将学习如何在实践中使用它。

我们将基于我们现有的[Guide to Spring 5 WebFlux](Spring_WebFlux.md)项目。
之前我们使用基于注解的组件创建了一个简单的响应式REST应用程序。本文中，我们将使用函数式API。

## 2. Maven依赖

我们需要与上一篇文章中使用的相同的spring-boot-starter-webflux依赖项：

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <version>2.6.1</version>
</dependency>
```

## 3. 函数式Web框架

函数式Web框架引入了一种新的编程模型，我们使用函数来路由和处理请求。

与使用基于注解映射的模型相反，这里我们将使用HandlerFunction和RouterFunctions。

### 3.1 HandlerFunction

HandlerFunction表示一个handler函数，它为路由到它们的请求生成响应：

```java

@FunctionalInterface
public interface HandlerFunction<T extends ServerResponse> {
    Mono<T> handle(ServerRequest request);
}
```

这个接口主要是一个Function<Request, Response<T>>，它的行为非常像一个Servlet。

与标准的Servlet#service(ServletRequest req, ServletResponse res)相比，HandlerFunction不将response作为输入参数。

### 3.2 RouterFunction

RouterFunction作为@RequestMapping注解的替代品。我们可以使用它将请求路由到handler函数：

```java

@FunctionalInterface
public interface RouterFunction<T extends ServerResponse> {
    Mono<HandlerFunction<T>> route(ServerRequest request);
    // ...
}
```

通常，我们可以导入静态方法RouterFunctions.route()。

它允许我们通过应用RequestPredicate来路由请求。当谓词匹配时，将返回第二个参数，即handler函数：

```
public static <T extends ServerResponse> RouterFunction<T> route(
  RequestPredicate predicate,
  HandlerFunction<T> handlerFunction)
```

因为route()方法返回一个RouterFunction，因此我们可以将它链接起来以构建强大而复杂的路由方案。

## 4. 使用函数式Web的响应式REST应用程序

在我们之前的文章中，我们使用@RestController和WebClient创建了一个简单的EmployeeManagement REST应用程序。

现在，让我们使用router和handler函数来实现相同的逻辑。

首先，我们需要使用RouterFunction创建路由来发布和消费我们的Employee响应流。

路由注册为Spring bean，可以在任何配置类中创建。

### 4.1 单个资源

让我们使用发布单个Employee资源的RouterFunction创建我们的第一个路由：

```java

@Configuration
public class EmployeeFunctionalConfig {

    @Bean
    EmployeeRepository employeeRepository() {
        return new EmployeeRepository();
    }

    @Bean
    RouterFunction<ServerResponse> getEmployeeByIdRoute() {
        return route(GET("/employees/{id}"),
                req -> ok().body(employeeRepository().findEmployeeById(req.pathVariable("id")), Employee.class));
    }
}
```

第一个参数是RequestPredicate。注意，在这里使用静态导入的RequestPredicates.GET方法。
第二个参数定义了一个在谓词适用时将使用的handler函数。

换句话说，上面的代码将对/employees/{id}的所有GET请求路由到EmployeeRepository#findEmployeeById(String id)方法。

### 4.2 集合资源

接下来，为了发布一个集合资源，让我们添加另一个路由：

```java

@Configuration
public class EmployeeFunctionalConfig {

    @Bean
    RouterFunction<ServerResponse> getAllEmployeesRoute() {
        return route(GET("/employees"), request -> ok().body(employeeRepository().findAllEmployees(), Employee.class));
    }
}
```

### 4.3 单一资源更新

最后，让我们添加一个用于更新Employee资源的路由：

```java

@Configuration
public class EmployeeFunctionalConfig {

    @Bean
    RouterFunction<ServerResponse> updateEmployeeRoute() {
        return route(POST("/employees/update"), req -> req.body(toMono(Employee.class))
                .doOnNext(employeeRepository()::updateEmployee)
                .then(ok().build()));
    }
}
```

## 5. 组合路由

我们还可以将多个路由组合在一个路由函数中。

让我们看看如何组合上面创建的路由：

```java

@Configuration
public class EmployeeFunctionalConfig {

    @Bean
    RouterFunction<ServerResponse> composedRoutes() {
        return route(GET("/employees"), req -> ok().body(employeeRepository().findAllEmployees(), Employee.class))

                .and(route(GET("/employees/{id}"), req -> ok().body(employeeRepository().findEmployeeById(req.pathVariable("id")), Employee.class)))

                .and(route(POST("/employees/update"), req -> req.body(toMono(Employee.class))
                        .doOnNext(employeeRepository()::updateEmployee)
                        .then(ok().build())));
    }
}
```

在这里，我们使用RouterFunction.and()来组合我们的路由。

至此，我们使用router和handler实现了EmployeeManagement应用程序所需的完整REST API。

要运行应用程序，我们可以使用单独的路由，也可以使用我们在上面创建的单个组合路由。

## 6. 测试路由

现在可以使用WebTestClient来测试我们的路由。

为此，我们首先需要使用bindToRouterFunction方法绑定路由，然后构建WebTestClient实例。

让我们测试getEmployeeByIdRoute：

```java

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = EmployeeSpringFunctionalApplication.class)
class EmployeeSpringFunctionalIntegrationTest {
    @Autowired
    private EmployeeFunctionalConfig config;
    @MockBean
    private EmployeeRepository employeeRepository;

    @Test
    void givenEmployeeId_whenGetEmployeeById_thenCorrectEmployee() {
        WebTestClient client = WebTestClient.bindToRouterFunction(config.getEmployeeByIdRoute()).build();

        Employee employee = new Employee("1", "Employee 1");

        given(employeeRepository.findEmployeeById("1")).willReturn(Mono.just(employee));

        client.get()
                .uri("/employees/1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Employee.class)
                .isEqualTo(employee);
    }
}
```

类似地，getAllEmployeesRoute：

```java
class EmployeeSpringFunctionalIntegrationTest {

    @Test
    void whenGetAllEmployees_thenCorrectEmployees() {
        WebTestClient client = WebTestClient.bindToRouterFunction(config.getAllEmployeesRoute()).build();

        List<Employee> employees = Arrays.asList(new Employee("1", "Employee 1"), new Employee("2", "Employee 2"));

        Flux<Employee> employeeFlux = Flux.fromIterable(employees);
        given(employeeRepository.findAllEmployees()).willReturn(employeeFlux);

        client.get()
                .uri("/employees")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Employee.class)
                .isEqualTo(employees);
    }
}
```

我们还可以通过断言我们的Employee实例是通过EmployeeRepository#updateEmployee来测试我们的updateEmployeeRoute：

```java
class EmployeeSpringFunctionalIntegrationTest {

    @Test
    void whenUpdateEmployee_thenEmployeeUpdated() {
        WebTestClient client = WebTestClient.bindToRouterFunction(config.updateEmployeeRoute()).build();

        Employee employee = new Employee("1", "Employee 1 Updated");

        client.post()
                .uri("/employees/update")
                .body(Mono.just(employee), Employee.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(employeeRepository).updateEmployee(employee);
    }
}
```

有关使用WebTestClient进行测试的更多详细信息，
请参阅我们的[WebClient和WebTestClient的教程](../../docs/Spring5_WebClient.md)。

## 7. 总结

在本教程中，我们介绍了Spring 5中新的函数式Web框架，并研究了它的两个核心接口，RouterFunction和HandlerFunction。
我们还学习了如何创建各种路由来处理请求和发送响应。

此外，我们使用函数式端点模型重新创建了Spring 5 WebFlux入门中介绍的EmployeeManagement应用程序。