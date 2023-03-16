## 1. 概述

为我们的API实现输入验证通常很有用，以避免以后在处理数据时出现意外错误。

**不幸的是，在Spring 5中，无法像我们在基于注解的端点上那样在函数式端点上自动运行验证。我们必须手动管理它们**。

尽管如此，我们仍然可以利用Spring提供的一些有用工具来轻松地以干净的方式验证我们的资源是否有效。

## 2. 使用Spring验证

在深入进行实际验证之前，让我们先用一个有效的函数式端点配置我们的项目。

假设我们有以下RouterFunction：

```java
@Configuration
public class ValidationsRouters {

    @Bean
    public RouterFunction<ServerResponse> functionalRoute(FunctionalHandler handler) {
        return route(POST("/functional-endpoint"), handler::handleRequest);
    }
}
```

该路由使用以下控制器类提供的处理函数：

```java
@Component
public class FunctionalHandler {

    public Mono<ServerResponse> handleRequest(ServerRequest request) {
        Mono<String> responseBody = request
              .bodyToMono(CustomRequestEntity.class)
              .map(cre -> String.format("Hi, %s [%s]!", cre.getName(), cre.getCode()));

        return ServerResponse.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(responseBody, String.class);
    }
}
```

正如我们所看到的，我们在这个函数式端点中所做的只是格式化和检索我们在请求正文中收到的信息，该请求正文的结构为CustomRequestEntity对象：

```java
public class CustomRequestEntity {

    private String name;
    private String code;

    // Constructors, Getters and Setters ...
}
```

这工作得很好，但让我们想象一下，我们现在需要检查我们的输入是否符合某些给定的约束，例如，所有字段都不能为null并且code应该超过6位。

我们需要找到一种方法来有效地进行这些断言，并且如果可能的话，将其与我们的业务逻辑分开。

### 2.1 实现验证器

**正如这个[Spring参考文档](https://docs.spring.io/spring/docs/5.1.x/spring-framework-reference/core.html#validator)中所解释的那样，我们可以使用Spring的Validator接口来评估我们的资源值**：

```java
public class CustomRequestEntityValidator implements Validator {
    public static final int MINIMUM_CODE_LENGTH = 6;

    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return CustomRequestEntityValidator.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NotNull Object target, @NotNull Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "field.required");
        CustomRequestEntity request = (CustomRequestEntity) target;
        if (request.getCode() != null && request.getCode().trim().length() < MINIMUM_CODE_LENGTH)
            errors.rejectValue("code", "field.min.length", new Object[]{MINIMUM_CODE_LENGTH}, "The code must be at least [" + MINIMUM_CODE_LENGTH + "] characters in length.");
    }
}
```

我们不会详细介绍[Validator](https://www.baeldung.com/javax-validation)的工作原理，只要知道在验证对象时收集了所有错误就足够了-**空的错误集合意味着该对象遵守我们所有的约束**。

因此现在我们已经有了验证器，我们必须在实际执行我们的业务逻辑之前显式地调用它的validate。

### 2.2 执行验证

起初，我们可能认为使用[HandlerFilterFunction](https://www.baeldung.com/spring-webflux-filters)会适合我们的情况。

但我们必须记住，在这些过滤器中，与在处理程序中一样，我们处理的是Mono和Flux这样的[异步构造](https://www.baeldung.com/spring-webflux)。

这意味着我们可以访问Publisher(Mono或Flux对象)，但不能访问它最终提供的数据。

**因此，我们能做的最好的事情就是当我们在处理程序函数中实际处理它时验证它**。

让我们继续修改我们的处理程序方法，包括验证逻辑：

```java
@Component
public class FunctionalHandler {

    public Mono<ServerResponse> handleRequest(final ServerRequest request) {
        Validator validator = new CustomRequestEntityValidator();
        Mono<String> responseBody = request.bodyToMono(CustomRequestEntity.class)
              .map(body -> {
                  Errors errors = new BeanPropertyBindingResult(body, CustomRequestEntityValidator.class.getName());
                  validator.validate(body, errors);

                  if (errors == null || errors.getAllErrors().isEmpty())
                      return String.format("Hi, %s [%s]!", body.getName(), body.getCode());
                  else
                      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.getAllErrors().toString());
              });

        return ServerResponse.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(responseBody, String.class);
    }
}
```

简而言之，如果请求的正文不符合我们的限制，我们的服务现在将返回“BAD_REQUEST”响应。

我们能说我们实现了我们的目标吗？好吧，我们快到了。**我们正在运行验证，但这种方法有很多缺点**。

我们将验证与业务逻辑混合在一起，更糟糕的是，我们将不得不在我们想要进行输入验证的任何处理程序中重复上面的代码。

让我们尝试改进这一点。

## 3. 采用DRY方法

**为了创建一个更简洁的解决方案，我们将首先声明一个包含处理请求的基本过程的抽象类**。

所有需要输入验证的处理程序都会扩展这个抽象类，以便重用它的主要方案，因此遵循DRY(不要重复自己)原则。

我们将使用泛型，使其足够灵活，以支持任何主体类型及其相应的验证器：

```java
public abstract class AbstractValidationHandler<T, U extends Validator> {
    private final Class<T> validationClass;
    private final U validator;

    protected AbstractValidationHandler(Class<T> clazz, U validator) {
        this.validationClass = clazz;
        this.validator = validator;
    }

    public final Mono<ServerResponse> handleRequest(final ServerRequest request) {
        // here we will validate and process the request ...
    }
}
```

现在让我们使用标准的校验过程编写handleRequest方法：

```java
public final Mono<ServerResponse> handleRequest(final ServerRequest request) {
    return request.bodyToMono(this.validationClass)
        .flatMap(body -> {
            Errors errors = new BeanPropertyBindingResult(body, this.validationClass.getName());
            this.validator.validate(body, errors);
            if (errors == null || errors.getAllErrors().isEmpty())
                return processBody(body, request);
            else
                return onValidationErrors(errors, body, request);
        });
}
```

正如我们所看到的，我们使用了两个尚未定义的方法processBody和onValidationErrors。

当我们校验不通过时，我们调用onValidationErrors：

```java
protected Mono<ServerResponse> onValidationErrors(Errors errors, T invalidBody, final ServerRequest request) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.getAllErrors().toString());
}
```

这只是一个默认实现，它可以很容易地被子类覆盖。

**最后，我们将processBody方法保留为抽象-我们将它留给子类来决定在这种情况下如何做进一步处理**：

```java
abstract protected Mono<ServerResponse> processBody(T validBody, final ServerRequest originalRequest);
```

在这个类中几个方面需要分析。

**首先通过使用泛型，子类必须显式声明他们期望的内容类型以及将用于评估它的验证器**。

这也使我们的结构更加健壮，因为它限制了我们方法的签名。

在运行时，构造函数将分配实际的验证器对象和用于转换请求主体的类。

现在让我们看看如何从这种结构中获益。

### 3.1 调整我们的处理程序

显然，我们要做的第一件事是从这个抽象类扩展我们的处理程序。

通过这样做，**我们将被迫使用父级的构造函数并定义我们将如何在processBody方法中处理我们的请求**：

```java
@Component
public class CustomRequestEntityValidationHandler extends AbstractValidationHandler<CustomRequestEntity, CustomRequestEntityValidator> {

    public CustomRequestEntityValidationHandler() {
        super(CustomRequestEntity.class, new CustomRequestEntityValidator());
    }

    @Override
    protected Mono<ServerResponse> processBody(CustomRequestEntity validBody, ServerRequest originalRequest) {
        String responseBody = String.format("Hi, %s [%s]!", validBody.getName(), validBody.getCode());
        return ServerResponse.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(Mono.just(responseBody), String.class);
    }

    @Override
    protected Mono<ServerResponse> onValidationErrors(Errors errors, CustomRequestEntity invalidBody, ServerRequest request) {
        return ServerResponse.badRequest()
              .contentType(MediaType.APPLICATION_JSON)
              .body(Mono.just(String.format("Custom message showing the errors: %s", errors.getAllErrors())), String.class);
    }
}
```

**正如我们所理解的，我们的子处理程序现在比我们在上一节中获得的处理程序简单得多，因为它避免了与资源的实际验证混淆**。

## 4. 对Bean Validation注解的支持

**通过这种方法，我们还可以利用javax.validation包提供的强大的[Bean Validation注解](https://www.baeldung.com/javax-validation#validation)**。

例如，让我们定义一个带有注解字段的新实体：

```java
public class AnnotatedRequestEntity {

    @NotNull
    private String user;

    @NotNull
    @Size(min = 4, max = 7)
    private String password;

    // ... Constructors, Getters and Setters ...
}
```

**现在，我们可以简单地创建一个新的处理程序，注入由LocalValidatorFactoryBean bean提供的默认Spring Validator**：

```java
@Component
public class AnnotatedRequestEntityValidationHandler extends AbstractValidationHandler<AnnotatedRequestEntity, Validator> {

    private AnnotatedRequestEntityValidationHandler(@Autowired Validator validator) {
        super(AnnotatedRequestEntity.class, validator);
    }

    @Override
    protected Mono<ServerResponse> processBody(AnnotatedRequestEntity validBody, ServerRequest originalRequest) {
        String responseBody = String.format("Hi, %s. Password: %s!", validBody.getUser(), validBody.getPassword());
        return ServerResponse.ok()
              .contentType(MediaType.APPLICATION_JSON)
              .body(Mono.just(responseBody), String.class);
    }
}
```

我们必须记住，如果Spring上下文中存在其他Validator bean，我们可能必须使用@Primary注解显式声明它：

```java
@Bean
@Primary
public Validator springValidator() {
    return new LocalValidatorFactoryBean();
}
```

## 5. 总结

总而言之，在这篇文章中，我们学习了如何在Spring 5函数式端点中验证输入数据。

我们创建了一种很好的方法来优雅地处理验证，避免将其逻辑与业务逻辑混合在一起。

**当然，建议的解决方案可能并不适用于任何场景。我们必须分析我们的情况，并可能根据我们的需要调整结构**。