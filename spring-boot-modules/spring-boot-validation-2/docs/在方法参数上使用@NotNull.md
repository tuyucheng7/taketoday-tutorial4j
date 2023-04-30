## 1. 概述

NullPointerException是一个常见问题。我们可以保护我们的代码的一种方法是向我们的方法参数添加注解，例如@NotNull 。

通过使用@NotNull ，我们表明如果我们想避免异常，我们绝不能使用null调用我们的方法。然而，仅靠它本身是不够的。让我们了解为什么。

## 2.方法参数上的@NotNull注解

首先，让我们创建一个类，其方法仅返回String的长度。

让我们也为我们的参数添加一个@NotNull注解：

```java
public class NotNullMethodParameter {
    public int validateNotNull(@NotNull String data) {
        return data.length();
    }
}
```

当我们导入NotNull 时，我们应该注意@NotNull注解有多种实现。因此，我们需要确保它来自正确的包。

我们将使用javax.validation.constraints包。

现在，让我们创建一个NotNullMethodParameter并使用null参数调用我们的方法：

```java
NotNullMethodParameter notNullMethodParameter = new NotNullMethodParameter();
notNullMethodParameter.doesNotValidate(null);
```

尽管有NotNull注解，我们还是得到了NullPointerException：

```java
java.lang.NullPointerException
```

我们的注解没有效果，因为没有验证器来强制执行它。

## 3. 添加验证器

因此，让我们添加 Hibernate Validator，即javax.validation参考实现，以识别我们的@NotNull。

除了我们的验证器，我们还需要为它用于呈现消息的表达式语言 (EL) 添加依赖项：

```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.2.3.Final</version>
</dependency>

<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>javax.el</artifactId>
    <version>3.0.0</version>
</dependency>
```

当我们不包含 EL 依赖时，我们会得到一个ValidationException来提醒我们：

```java
javax.validation.ValidationException: HV000183: Unable to initialize 'javax.el.ExpressionFactory'. Check that you have the EL dependencies on the classpath, or use ParameterMessageInterpolator instead
```

有了我们的依赖关系，我们可以强制执行我们的@NotNull注解。

因此，让我们使用默认的ValidatorFactory创建一个验证器：

```java
ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
Validator validator = factory.getValidator();
```

然后，让我们验证我们的参数作为我们注解方法的第一行：

```java
validator.validate(myString);
```

现在，当我们使用 null 参数调用我们的方法时，我们的@NotNull被强制执行：

```java
java.lang.IllegalArgumentException: HV000116: The object to be validated must not be null.
```

这很好，但是必须在每个带注解的方法中添加对验证器的调用会导致大量样板文件。

## 4. 弹簧靴

幸运的是，我们可以在Spring Boot应用程序中使用一种更简单的方法。

### 4.1.Spring Boot验证

首先，让我们添加 Maven 依赖以使用Spring Boot进行验证：

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
    <version>2.7.1</version>
</dependency>
```

我们的spring-boot-starter-validation依赖项带来了Spring Boot和验证所需的一切。这意味着我们可以删除我们早期的 Hibernate 和 EL 依赖项以保持我们的pom.xml干净。

现在，让我们创建一个 Spring 管理的组件，确保我们添加了@Validated 注解。让我们用一个validateNotNull方法创建它，该方法接受一个String参数并返回我们数据的长度，并用@NotNull注解我们的参数：

```java
@Component
@Validated
public class ValidatingComponent {
    public int validateNotNull(@NotNull String data) {
        return data.length();
    }
}
```

最后，让我们创建一个带有自动装配的ValidatingComponent的SpringBootTest。我们还添加一个带有null作为方法参数的测试：

```java
@SpringBootTest
class ValidatingComponentTest {
    @Autowired ValidatingComponent component;

    @Test
    void givenNull_whenValidate_thenConstraintViolationException() {
        assertThrows(ConstraintViolationException.class, () -> component.validate(null));
    }
}
```

我们得到的ConstraintViolationException有我们的参数名称和一条“不能为空”的消息：

```java
javax.validation.ConstraintViolationException: validate.data: must not be null
```

我们可以在我们的[方法约束](https://www.baeldung.com/javax-validation-method-constraints)文章中了解更多关于注解我们的方法的信息。

### 4.2. 警示语

尽管这适用于我们的公共方法，但让我们看看当我们添加另一个未注解但调用我们原始注解方法的方法时会发生什么：

```java
public String callAnnotatedMethod(String data) {
    return validateNotNull(data);
}
```

我们的NullPointerException返回。当我们从驻留在同一类中的另一个方法调用带注解的方法时，Spring 不会强制执行NotNull约束。

### 4.3. Jakarta 和Spring Boot3.0

对于 Jakarta，验证包名称最近从javax.validation更改为jakarta.validation。[Spring Boot 3.0 基于 Jakarta](https://spring.io/blog/2021/09/02/a-java-17-and-jakarta-ee-9-baseline-for-spring-framework-6)，因此使用更新的jakarta.validation包。对于7.0. 及更高版本的hibernate-validator版本也是如此。这意味着当我们升级时，我们需要更改我们在验证注解中使用的包名称。

## 5.总结

在本文中，我们了解了如何在标准Java应用程序的方法参数上使用@NotNull注解。我们还学习了如何使用Spring Boot的@Validated注解来简化我们的 Spring Bean 方法参数验证，同时也注意到它的局限性。最后，我们注意到当我们将Spring Boot项目更新到 3.0 时，我们应该期望将我们的javax包更改为jakarta 。