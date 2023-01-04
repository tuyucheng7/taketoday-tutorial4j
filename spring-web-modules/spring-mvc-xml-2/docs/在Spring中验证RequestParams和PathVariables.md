## 1. 简介

在本教程中，我们将学习如何在Spring MVC中验证HTTP请求参数和路径变量。

具体来说，我们将 使用[JSR 303注解验证](https://beanvalidation.org/1.0/spec/)String和Number参数。

要探索其他类型的验证，我们可以参考我们关于[Java Bean验证](https://www.baeldung.com/javax-validation)和[方法约束](https://www.baeldung.com/javax-validation-method-constraints)的教程，或者我们可以学习如何[创建我们自己的验证器](https://www.baeldung.com/spring-mvc-custom-validator)。

## 2. 配置

要使用Java验证API，我们必须添加一个JSR 303实现，例如[hibernate-validator](https://search.maven.org/search?q=a:hibernate-validator)：

```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.0.10.Final</version>
</dependency>
```

我们还必须通过添加@Validated注解来为我们的控制器中的请求参数和路径变量启用验证：

```java
@RestController
@RequestMapping("/")
@Validated
public class Controller {
    // ...
}
```

请务必注意，启用参数验证还需要一个MethodValidationPostProcessor bean。如果我们使用的是Spring Boot应用程序，那么这个bean是自动配置的，因为我们在类路径上有hibernate-validator依赖项。

否则，在标准的Spring应用程序中，我们必须显式添加此bean：

```java
@EnableWebMvc
@Configuration
@ComponentScan("cn.tuyucheng.taketoday.spring")
public class ClientWebConfigJava implements WebMvcConfigurer {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
    // ...
}
```

默认情况下，Spring中路径或请求验证期间的任何错误都会导致HTTP 500响应。在本教程中，我们将使用[ControllerAdvice](https://www.baeldung.com/exception-handling-for-rest-with-spring)的自定义实现以更具可读性的方式处理这些类型的错误，并为任何错误请求返回HTTP 400。我们可以在[GitHub上](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-web-modules/spring-mvc-xml)找到这个解决方案的源代码。

## 3. 验证RequestParam

让我们考虑一个例子，我们将一个数字工作日作为请求参数传递给控制器方法：

```java
@GetMapping("/name-for-day")
public String getNameOfDayByNumber(@RequestParam Integer dayOfWeek) {
    // ...
}
```

我们的目标是确保dayOfWeek的值在1到7之间。为此，我们将使用@Min和@Max注解：

```java
@GetMapping("/name-for-day")
public String getNameOfDayByNumber(@RequestParam @Min(1) @Max(7) Integer dayOfWeek) {
    // ...
}
```

任何不符合这些条件的请求都将返回带有默认错误消息的HTTP状态400。

例如，如果我们调用[http:// localhost:8080/name-for-day?dayOfWeek=24](http://localhost:8080/name-for-day?dayOfWeek=24)，响应消息将是：

```bash
getNameOfDayByNumber.dayOfWeek: must be less than or equal to 7
```

我们可以通过添加自定义消息来更改默认消息：

```java
@Max(value = 1, message = "day number has to be less than or equal to 7")
```

## 4. 验证PathVariable

与@RequestParam一样，我们可以使用javax.validation.constraints包中的任何注解来验证@PathVariable。

让我们考虑一个示例，在该示例中我们验证String参数不为空且长度小于或等于10：

```java
@GetMapping("/valid-name/{name}")
public void createUsername(@PathVariable("name") @NotBlank @Size(max = 10) String username) {
    // ...
}
```

例如，任何名称参数超过10个字符的请求都将导致HTTP 400错误并显示一条消息：

```bash
createUser.name:size must be between 0 and 10
```

通过在@Size注解中设置消息参数，可以轻松覆盖默认消息。

## 5. 总结

在本文中，我们学习了如何在Spring应用程序中验证请求参数和路径变量。