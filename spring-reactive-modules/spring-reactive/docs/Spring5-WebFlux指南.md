## 1. 概述

**Spring 5包含Spring WebFlux，它为Web应用程序提供响应式编程支持**。

在本教程中，我们将使用响应式Web组件RestController和WebClient创建一个小型响应式REST应用程序。

我们还将研究如何使用Spring Security保护我们的响应式端点。

## 2. Spring WebFlux框架

**Spring WebFlux在内部使用**[Reactor](https://projectreactor.io/)**及其Publisher实现，**[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)**和**[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)。

WebFlux支持两种编程模型：

+ 基于注解的响应式组件。
+ 基于函数的routing(路由)和handling(处理器)。

我们将专注于基于注解的响应式组件，因为我们已经在另一个教程中介绍了[函数式风格-routing和handling](Spring5中函数式Web框架介绍.md)。

## 3. Maven依赖

让我们从spring-boot-starter-webflux依赖项开始，它会引入所有其他必需的依赖项：

+ spring-boot和spring-boot-starter用于基本的Spring Boot应用程序
+ spring-webflux框架
+ 响应式流所需的reactor-core以及reactor-netty

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <version>2.6.4</version>
</dependency>
```

最新的[spring-boot-starter-webflux](https://search.maven.org/search?q=a:spring-boot-starter-webflux)可以从Maven Central下载。

## 4. 响应式REST应用程序

**现在我们将使用Spring WebFlux构建一个非常简单的响应式REST员工管理应用程序**：

+ 使用一个简单的域模型Employee，包含一个id和一个name字段
+ 使用RestController构建REST API以将Employee资源作为单个资源和集合发布
+ 使用WebClient构建客户端以检索Employee资源
+ 使用WebFlux和Spring Security创建一个安全的响应式端点

## 5. 响应式RestController

Spring Web Flux以与Spring Web MVC框架相同的方式支持基于注解的配置。

首先，**在服务器端，我们创建一个带注解的控制器，它发布Employee资源的响应流**。

让我们创建带注解的EmployeeController：

```java
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    
    // constructor ...
}
```

EmployeeRepository可以是任何支持非阻塞响应式流的Repository。

```java
@Repository
public class EmployeeRepository {
    private static final Map<String, Employee> EMPLOYEE_DATA;

    static {
        EMPLOYEE_DATA = new HashMap<>();
        // create some employees and add them to the map
    }

    public Mono<Employee> findEmployeeById(String id) {
        return Mono.just(EMPLOYEE_DATA.get(id));
    }

    public Flux<Employee> findAllEmployees() {
        return Flux.fromIterable(EMPLOYEE_DATA.values());
    }

    public Mono<Employee> updateEmployee(Employee employee) {
        Employee existingEmployee = EMPLOYEE_DATA.get(employee.getId());
        if (existingEmployee != null)
            existingEmployee.setName(employee.getName());
        assert existingEmployee != null;
        return Mono.just(existingEmployee);
    }
}
```

### 5.1 单一资源

然后让我们在控制器中创建一个端点来发布单个Employee资源：

```java
@GetMapping("/{id}")
private Mono<Employee> getEmployeeById(@PathVariable String id) {
    return employeeRepository.findEmployeeById(id);
}
```

**我们将单个Employee对象封装在Mono中，因为我们最多返回一个Employee对象**。

### 5.2 集合资源

我们还添加另一个端点，用于发布所有Employee的集合资源：

```java
@GetMapping
private Flux<Employee> getAllEmployees() {
    return employeeRepository.findAllEmployees();
}
```

**对于集合资源，我们使用Employee类型的Flux，因为它是0...n个元素的发布者**。

## 6. 响应式WebClient

[WebClient](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-client)在Spring 5中引入，是一个支持响应式流的非阻塞客户端。

**我们可以使用WebClient创建一个客户端来从EmployeeController提供的端点检索数据**。

让我们创建一个简单的EmployeeWebClient：

```java
public class EmployeeWebClient {
    // LOGGER ...
    WebClient client = WebClient.create("http://localhost:8080");
    
    // ...
}
```

在这里，我们使用其工厂方法create创建了一个WebClient。它将指向localhost:8080，因此我们可以使用相对URL来调用此客户端实例。

### 6.1 检索单个资源

要从端点/employee/{id}检索Mono类型的单个资源，请执行以下操作：

```java
public void consume() {
    Mono<Employee> employeeMono = client.get()
        .uri("/employees/{id}", 1)
        .retrieve()
        .bodyToMono(Employee.class);

    employeeMono.subscribe(employee -> LOGGER.info("Employee: {}", employee));
}
```

### 6.2 检索集合资源

同样，要从端点/employees检索Flux类型的集合资源，请执行以下操作：

```java
public void consume() {
    Flux<Employee> employeeFlux = client.get()
        .uri("/employees")
        .retrieve()
        .bodyToFlux(Employee.class);

    employeeFlux.subscribe(employee -> LOGGER.info("Employee: {}", employee));
}
```

我们还有一篇关于[设置和使用WebClient](Spring5-WebClient.md)的详细文章。

## 7. Spring WebFlux Security

**我们可以使用Spring Security来保护我们的响应式端点**。

假设我们在EmployeeController中有一个新的端点/update。此端点更新员工详细信息并将更新后的员工返回。

由于这涉及到对Employee数据的修改，因此我们希望将此端点限制为仅ADMIN角色用户。

因此，让我们向EmployeeController添加一个新方法：

```java
@PostMapping("/update")
private Mono<Employee> updateEmployee(@RequestBody Employee employee) {
    return employeeRepository.updateEmployee(employee);
}
```

现在，为了限制对该方法的访问，让我们创建Spring Security配置类并定义一些基于路径的规则以仅允许ADMIN用户访问：

```java
@EnableWebFluxSecurity
public class EmployeeWebSecurityConfig {

    // ...

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf().disable()
              .authorizeExchange()
              .pathMatchers(HttpMethod.POST, "/employees/update").hasRole("ADMIN")
              .pathMatchers("/**").permitAll()
              .and()
              .httpBasic();
        return http.build();
    }
}
```

此配置将限制对端点/employees/update的访问。因此，只有具有ADMIN角色的用户才能访问此端点并更新现有员工。

最后，注解@EnableWebFluxSecurity添加了带有一些默认配置的Spring Security WebFlux支持。

有关更多信息，我们还有一篇关于[配置和使用Spring WebFlux Security](https://www.baeldung.com/spring-security-5-reactive)的详细文章。

## 8. 总结

在本文中，我们探讨了如何创建和使用Spring WebFlux框架支持的响应式Web组件。例如，我们构建了一个小型的响应式REST应用程序。

然后我们学习了如何使用RestController和WebClient来发布和消费响应式流。

我们还研究了如何在Spring Security的帮助下创建一个安全的响应式端点。

除了响应式RestController和WebClient，WebFlux框架还支持响应式WebSocket和相应的WebSocketClient，用于响应式流的套接字风格流。

有关更多信息，我们还有一篇详细的文章，重点介绍如何在[Spring 5使用Reactive WebSocket](https://www.baeldung.com/spring-5-reactive-websockets)。