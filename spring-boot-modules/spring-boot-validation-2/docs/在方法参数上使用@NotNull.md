## 1. 概述

NullPointerException是一个常见问题。可以保护我们代码的一种方法是向我们的方法参数添加注解，例如@NotNull。

通过使用@NotNull，我们表明如果我们想避免异常，我们绝不能使用null调用我们的方法。然而，仅靠它本身是不够的。让我们了解一下为什么。

## 2. 方法参数上的@NotNull注解

首先，让我们创建一个类，其中包含一个仅返回字符串长度的方法。

让我们也为参数添加一个@NotNull注解：

```java
public class NotNullMethodParameter {
    public int validateNotNull(@NotNull String data) {
        return data.length();
    }
}
```

**当我们导入NotNull时，我们应该注意@NotNull注解有多种实现。因此，我们需要确保它来自正确的包**。

我们将使用jakarta.validation.constraints包。

现在，让我们创建一个NotNullMethodParameter并使用null参数调用我们的方法：

```java
NotNullMethodParameter notNullMethodParameter = new NotNullMethodParameter();
notNullMethodParameter.doesNotValidate(null);
```

尽管有NotNull注解，但我们还是得到了NullPointerException：

```xml
java.lang.NullPointerException
```

**我们的注解没有效果，因为没有验证器来强制执行它**。

## 3. 添加验证器

因此，让我们添加Hibernate Validator，即jakarta.validation参考实现，以识别我们的@NotNull。

```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>8.0.0.Final</version>
</dependency>
```

有了依赖项，我们就可以强制执行@NotNull注解。

因此，让我们使用默认的ValidatorFactory创建一个验证器：

```java
ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
Validator validator = factory.getValidator();
```

然后，让我们验证方法参数：

```java
validator.validate(myString);
```

现在，当我们使用null参数调用我们的方法时，我们的@NotNull被强制执行：

```shell
java.lang.IllegalArgumentException: HV000116: The object to be validated must not be null.
```

这很好，但是**必须在每个带注解的方法中添加对验证器的调用会导致大量样板文件**。

## 4. Spring Boot

幸运的是，我们可以在Spring Boot应用程序中使用一种更简单的方法。

### 4.1 Spring Boot Validation

首先，让我们添加Maven依赖以使用Spring Boot进行验证：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
    <version>2.7.1</version>
</dependency>
```

spring-boot-starter-validation依赖项带来了Spring Boot和验证所需的一切。这意味着我们可以删除早期的Hibernate依赖项以保持我们的pom.xml干净。

现在，让我们创建一个Spring管理的组件，**确保我们添加了@Validated注解**。并在其中添加一个validateNotNull方法，该方法接收一个字符串参数并返回字符串长度，同样使用@NotNull标注我们的参数：

```java
@Component
@Validated
public class ValidatingComponent {
    public int validateNotNull(@NotNull String data) {
        return data.length();
    }
}
```

最后，让我们创建一个带有自动装配的ValidatingComponent的Spring Boot Test。我们还添加一个以null作为方法参数的测试：

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

我们得到的ConstraintViolationException有我们的参数名称和一条“must not be null”的消息：

```shell
javax.validation.ConstraintViolationException: validate.data: must not be null
```

我们可以在[方法约束](https://www.baeldung.com/javax-validation-method-constraints)一文中了解更多关于标注方法的信息。

### 4.2 警示语

尽管这适用于我们的公共方法，但让我们看看当我们添加另一个未标注但调用我们原始注解方法的方法时会发生什么：

```java
public String callAnnotatedMethod(String data) {
    return validateNotNull(data);
}
```

我们的NullPointerException返回。**当我们从驻留在同一类中的另一个方法调用带注解的方法时，Spring不会强制执行NotNull约束**。
## 5. 总结

在本文中，我们学习了如何在标准Java应用程序的方法参数上使用@NotNull注解。我们还学习了如何使用Spring Boot的@Validated注解来简化我们的Spring bean方法参数验证，同时也注意到它的局限性。