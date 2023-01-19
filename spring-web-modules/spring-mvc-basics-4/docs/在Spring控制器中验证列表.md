## 1. 简介

验证用户输入是任何应用程序中的常见要求。在本教程中，我们将介绍将对象列表作为 Spring 控制器的参数进行验证的方法。

我们将在控制器层添加验证以确保用户指定的数据满足指定的条件。

## 2. 给字段添加约束

对于我们的示例，我们将使用一个简单的 Spring 控制器来管理电影数据库。我们将专注于接受电影列表并在对列表执行验证后将它们添加到数据库的方法。

因此，让我们首先 使用[javax 验证在](https://www.baeldung.com/javax-validation)Movie类上添加约束：

```java
public class Movie {

    private String id;

    @NotEmpty(message = "Movie name cannot be empty.")
    private String name;

    // standard setters and getters
}
```

## 3.在控制器中添加验证注解

让我们看看我们的控制器。首先，我们将@Validated注解添加到控制器类：

```java
@Validated
@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    //...
}
```

接下来，让我们编写控制器方法，我们将在其中验证传入的Movie对象列表。

我们将把@NotEmpty注解添加到我们的电影列表中，以验证列表中应该至少有一个元素。同时，我们将添加@Valid注解以确保Movie对象本身是有效的：

```java
@PostMapping
public void addAll(
  @RequestBody 
  @NotEmpty(message = "Input movie list cannot be empty.")
  List<@Valid Movie> movies) {
    movieService.addAll(movies);
}
```

如果我们用一个空的Movie列表输入调用控制器方法，那么由于@NotEmpty注解，验证将失败，我们将看到消息：

```plaintext
Input movie list cannot be empty.
```

@Valid注解将确保为列表中的每个对象评估Movie类中指定的约束。因此，如果我们在列表中传递一个名称为空的电影，验证将失败并显示以下消息：

```plaintext
Movie name cannot be empty.
```

## 4.自定义验证器

我们还可以将[自定义约束验证器](https://www.baeldung.com/spring-mvc-custom-validator)添加到输入列表中。

对于我们的示例，自定义约束将验证输入列表大小限制为最多四个元素的条件。让我们创建这个自定义约束注解：

```java
@Constraint(validatedBy = MaxSizeConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxSizeConstraint {
    String message() default "The input list cannot contain more than 4 movies.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

现在，我们将创建一个将应用上述约束的验证器：

```java
public class MaxSizeConstraintValidator implements ConstraintValidator<MaxSizeConstraint, List<Movie>> {
    @Override
    public boolean isValid(List<Movie> values, ConstraintValidatorContext context) {
        return values.size() <= 4;
    }
}
```

最后，我们将@MaxSizeConstraint注解添加到我们的控制器方法中：

```java
@PostMapping
public void addAll(
  @RequestBody
  @NotEmpty(message = "Input movie list cannot be empty.")
  @MaxSizeConstraint
  List<@Valid Movie> movies) {
    movieService.addAll(movies);
}
```

在这里，@MaxSizeConstraint将验证输入的大小。因此，如果我们在输入列表中传递超过四个Movie对象，验证将失败。

## 5.处理异常

如果任何验证失败，则抛出[ConstraintViolationException 。](https://javaee.github.io/javaee-spec/javadocs/javax/validation/ConstraintViolationException.html)现在，让我们看看如何添加[异常处理](https://www.baeldung.com/exception-handling-for-rest-with-spring)组件来捕获此异常。

```java
@ExceptionHandler(ConstraintViolationException.class)
public ResponseEntity handle(ConstraintViolationException constraintViolationException) {
    Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
    String errorMessage = "";
    if (!violations.isEmpty()) {
        StringBuilder builder = new StringBuilder();
        violations.forEach(violation -> builder.append(" " + violation.getMessage()));
        errorMessage = builder.toString();
    } else {
        errorMessage = "ConstraintViolationException occured.";
    }
    return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
 }
```

## 6. 测试 API

现在，我们将使用有效和无效输入测试我们的控制器。

首先，让我们向 API 提供有效输入：

```java
curl -v -d '[{"name":"Movie1"}]' -H "Content-Type: application/json" -X POST http://localhost:8080/movies 
```

在这种情况下，我们将获得 HTTP 状态 200 响应：

```plaintext
...
HTTP/1.1 200
...
```

接下来，我们将在传递无效输入时检查我们的 API 响应。

让我们尝试一个空列表：

```shell
curl -d [] -H "Content-Type: application/json" -X POST http://localhost:8080/movies
```

在这种情况下，我们将收到 HTTP 状态 400 响应。这是因为输入不满足@NotEmpty约束。

```plaintext
Input movie list cannot be empty.
```

接下来，让我们尝试在列表中传递五个Movie对象：

```shell
curl -d '[{"name":"Movie1"},{"name":"Movie2"},{"name":"Movie3"},{"name":"Movie4"},{"name":"Movie5"}]'
  -H "Content-Type: application/json" -X POST http://localhost:8080/movie
```

这也将导致 HTTP 状态 400 响应，因为我们未通过@MaxSizeConstraint约束：

```plaintext
The input list cannot contain more than 4 movies.
```

## 七. 总结

在这篇快速文章中，我们学习了如何在 Spring 中验证对象列表。