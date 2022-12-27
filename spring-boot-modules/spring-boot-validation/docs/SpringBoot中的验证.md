## 1. 概述

在验证用户输入方面，[Spring Boot](https://www.baeldung.com/spring-boot)为这种常见但关键的任务提供了强大的支持，直接开箱即用。

尽管Spring Boot支持与自定义验证器的无缝集成，**但执行验证的事实标准是**[Hibernate Validator](http://hibernate.org/validator/)，即[Bean Validation框架](https://www.baeldung.com/javax-validation)的参考实现。

在本教程中，我们将了解**如何在Spring Boot中验证域对象**。

## 2. Maven依赖

在本例中，我们将学习如何通过**构建一个基本的REST控制器**来验证Spring Boot中的域对象。

控制器将首先获取一个域对象，然后使用Hibernate Validator对其进行验证，最后将其持久化到内存中的H2数据库中。

该项目的依赖项是相当标准的：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency> 
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency> 
<dependency> 
    <groupId>com.h2database</groupId> 
    <artifactId>h2</artifactId>
    <version>1.4.197</version> 
    <scope>runtime</scope>
</dependency>
```

如上所示，我们在pom.xml文件中包含了[spring-boot-starter-web](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-web)，因为我们需要它来创建REST控制器。此外，让我们确保检查最新版本的[spring-boot-starter-data-jpa](https://search.maven.org/search?q=a:spring-boot-starter-data-jpa)和Maven Central上的[H2](https://search.maven.org/search?q=a:h2)数据库。

从Boot 2.3开始，我们还需要显式添加[spring-boot-starter-validation](https://search.maven.org/search?q=a:spring-boot-starter-validation)依赖项：

```xml
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-validation</artifactId> 
</dependency>
```

## 3. 一个简单的域类

有了我们项目的依赖项，接下来我们需要定义一个示例JPA实体类，其角色将仅用于对用户进行建模。

让我们来看看这个类：

```java
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Email is mandatory")
    private String email;

    // standard constructors / setters / getters / toString
}
```

我们的User实体类的实现确实很贫乏，但它简要地说明了如何使用Bean Validation的约束注解来约束name和email字段。

为了简单起见，我们仅使用[@NotBlank](https://www.baeldung.com/java-bean-validation-not-null-empty-blank)约束来约束目标字段。此外，我们还使用注解的message属性指定了错误消息。

因此，当Spring Boot验证类实例时，**约束字段必须不为null，并且它们的修剪长度必须大于0**。

此外，除了@NotBlank之外，[Bean Validation](https://www.baeldung.com/javax-validation)还提供了许多其他方便的约束注解，这允许我们将不同的验证规则应用于约束类并组合在一起。有关详细信息，请阅读[官方Bean Validation文档](https://beanvalidation.org/2.0/)。

由于我们将使用[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)将用户保存到内存中的H2数据库中，因此我们还需要定义一个简单的Repository接口，以便在User对象上具有基本的CRUD功能：

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {}
```

## 4. 实现REST控制器

当然，我们需要实现一个Web层，允许我们能够获取分配给User对象的约束字段的值。因此，我们可以验证它们并根据验证结果执行一些进一步的任务。

Spring Boot通过REST控制器的实现使这个看似复杂的过程变得非常简单。

让我们看看REST控制器的实现：

```java
@RestController
public class UserController {

    @PostMapping("/users")
    ResponseEntity<String> addUser(@Valid @RequestBody User user) {
        // persisting the user
        return ResponseEntity.ok("User is valid");
    }

    // standard constructors / other methods
}
```

在[Spring REST上下文](https://www.baeldung.com/rest-with-spring-series)中，addUser()方法的实现是相当标准的。当然，最相关的部分是@Valid注解的使用。

**当Spring Boot发现一个使用@Valid注解的参数时，它会自动引导默认的JSR 380实现(Hibernate Validator)并验证该参数**。

当目标参数未能通过验证时，Spring Boot会抛出[MethodArgumentNotValidException](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/MethodArgumentNotValidException.html)异常。

## 5. @ExceptionHandler注解

虽然让Spring Boot自动验证传递给addUser()方法的User对象非常方便，但这个过程缺少的方面是我们如何处理验证结果。

[@ExceptionHandler注解](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/ExceptionHandler.html)**允许我们通过一个方法处理指定类型的异常**。

因此，我们可以用它来处理验证错误：

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(MethodArgumentNotValidException.class)
public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
        String fieldName = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        errors.put(fieldName, errorMessage);
    });
    return errors;
}
```

我们将MethodArgumentNotValidException异常指定为[要处理的异常](https://www.baeldung.com/exception-handling-for-rest-with-spring)，因此，**当指定的User对象无效时，Spring Boot将调用此方法**。

该方法将每个无效字段的名称和验证后的错误消息存储在Map中，接下来它将Map作为JSON的表示形式发送回客户端以供进一步处理。

简而言之，REST控制器允许我们能够轻松地处理对不同端点的请求、验证用户对象并以JSON格式发送响应。

该设计足够灵活，可以通过多个Web层处理控制器响应，范围从模板引擎(例如[Thymeleaf](https://www.baeldung.com/spring-boot-crud-thymeleaf))到功能齐全的JavaScript框架(例如[Angular](https://angular.io/))。

## 6. 测试REST控制器

我们可以通过[集成测试](https://www.baeldung.com/spring-boot-testing)轻松测试REST控制器的功能。

让我们开始Mock/自动装配UserRepository接口实现，以及UserController实例和[MockMvc](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/web/servlet/MockMvc.html)对象：

```java
@ExtendWith(SpringExtension.class)
@WebMvcTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    UserController userController;

    @Autowired
    private MockMvc mockMvc;
    
    // ...
}
```

由于我们只测试Web层，因此我们使用[@WebMvcTest](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/autoconfigure/web/servlet/WebMvcTest.html)注解，它允许我们使用由[MockMvcRequestBuilders](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/request/MockMvcRequestBuilders.html)和[MockMvcResultMatchers](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/web/servlet/result/MockMvcResultMatchers.html)类实现的一组静态方法轻松测试请求和响应。

现在让我们使用在请求正文中传递的有效和无效User对象来测试addUser()方法：

```java
@Test
void whenPostRequestToUsersAndValidUser_thenCorrectResponse() throws Exception {
	MediaType textPlainUtf8 = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
	String user = "{\"name\": \"bob\", \"email\" : \"bob@domain.com\"}";
	mockMvc.perform(MockMvcRequestBuilders.post("/users")
			.content(user)
			.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().contentType(textPlainUtf8));
}

@Test
void whenPostRequestToUsersAndInValidUser_thenCorrectResponse() throws Exception {
	String user = "{\"name\": \"\", \"email\" : \"bob@domain.com\"}";
	mockMvc.perform(MockMvcRequestBuilders.post("/users")
			.content(user)
			.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.status().isBadRequest())
		.andExpect(MockMvcResultMatchers.jsonPath("$.name", Is.is("Name is mandatory")))
		.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
}
```

此外，我们可以**使用免费的API生命周期测试应用程序**(例如[Postman](https://www.getpostman.com/))来测试REST控制器API。

## 7. 运行示例应用程序

最后，我们可以使用标准的main()方法运行我们的示例项目：

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner run(UserRepository userRepository) throws Exception {
        return (String[] args) -> {
            User user1 = new User("Bob", "bob@domain.com");
            User user2 = new User("Jenny", "jenny@domain.com");
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.findAll().forEach(System.out::println);
        };
    }
}
```

正如预期的那样，我们应该看到在控制台中打印出两个User对象。

使用有效的User对象对http://localhost:8080/users端点的POST请求将返回字符串“User is valid”。

同样，传递不带名称和电子邮件值的User对象的POST请求将返回以下响应：

```json
{
    "name": "Name is mandatory",
    "email": "Email is mandatory"
}
```

## 8. 总结

在本文中，我们了解了在Spring Boot中执行验证的基础知识。