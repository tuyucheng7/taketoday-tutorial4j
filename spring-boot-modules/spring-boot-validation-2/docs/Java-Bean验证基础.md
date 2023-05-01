## 1. 概述

在本快速教程中，我们将介绍使用**标准框架验证Java bean**的基础知识-使用标准的JSR-380框架及其Jakarta Bean Validation 3.0规范，它建立在Java EE 7中引入的Bean Validation API的特性之上。

在大多数应用程序中，验证用户输入是一个非常普遍的要求。而Java Bean Validation框架已经成为处理这种逻辑的事实上的标准。

## 延伸阅读

### [Spring Boot中的验证](https://www.baeldung.com/spring-boot-bean-validation)

了解如何使用Hibernate Validator(Bean Validation框架的参考实现)在Spring Boot中验证域对象。

[阅读更多](https://www.baeldung.com/spring-boot-bean-validation)→

### [Bean Validation 2.0的方法约束](https://www.baeldung.com/javax-validation-method-constraints)

使用Bean Validation 2.0的方法约束简介。

[阅读更多](https://www.baeldung.com/javax-validation-method-constraints)→

## 2. JSR 380

JSR 380是用于bean验证的Java API规范，是Jakarta EE和Java SE的一部分。这可确保bean的属性满足特定标准，并使用注解(如@NotNull、@Min和@Max)。

此版本需要Java 17或更高版本，因为使用Spring Boot 3.x带来了Hibernate-Validator 8.0.0，它还支持Java 9及更高版本中引入的新功能，如Stream和Optional改进、模块、私有接口方法等。

有关规范的完整信息，请继续阅读[JSR 380](https://jcp.org/en/jsr/detail?id=380)。

## 3. 依赖

在最新版本的**spring-boot-starter-validation**中，除了其他依赖之外，还会提供hibernate-validator的传递依赖。

如果你只想添加用于验证的依赖项，你只需在pom.xml中添加hibernate-validator。

```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>8.0.0.Final</version>
</dependency>
```

快速说明：**[hibernate-validator](https://mvnrepository.com/artifact/org.hibernate.validator/hibernate-validator)完全独立于Hibernate的持久层方面**。因此，通过将其添加为依赖项，我们并没有将这些持久层方面添加到项目中。 

## 4. 使用验证注解

在这里，我们将采用一个User bean并向其添加一些简单的验证：

```java
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public class User {

    @NotNull(message = "Name cannot be null")
    private String name;

    @AssertTrue
    private boolean working;

    @Size(min = 10, max = 200, message
            = "About Me must be between 10 and 200 characters")
    private String aboutMe;

    @Min(value = 18, message = "Age should not be less than 18")
    @Max(value = 150, message = "Age should not be greater than 150")
    private int age;

    @Email(message = "Email should be valid")
    private String email;

    // standard setters and getters 
}
```

示例中使用的所有注解都是标准的JSR注解：

-   **@NotNull**验证带注解的属性值不为null。
-   **@AssertTrue**验证带注解的属性值是否为true。
-   **@Size**验证带注解的属性值的大小介于属性min和max之间；可以应用于String、Collection、Map和数组属性。
-   **@Min**验证带注解的属性的值不小于value属性。
-   **@Max**验证带注解的属性的值不大于value属性。
-   **@Email**验证带注解的属性是有效的电子邮件地址。

某些注解接受额外的属性，但message属性对它们都是通用的。这是当相应属性的值未通过验证时通常会呈现的消息。

还有一些额外的注解可以在JSR中找到：

-   **@NotEmpty**验证该属性不为null或为空；可以应用于String、Collection、Map或Array值。
-   **@NotBlank**只能应用于文本值，并验证该属性不是null或空格。
-   **@Positive**和**@PositiveOrZero**应用于数值并验证它们是严格正数，还是正数(包括0)。
-   **@Negative**和**@NegativeOrZero**应用于数值并验证它们是严格负数，还是负数(包括0)。
-   **@Past**和**@PastOrPresent**验证日期值是过去还是过去包括现在；可以应用于日期类型，包括那些在Java 8中添加的类型。
-   **@Future**和**@FutureOrPresent**验证日期值是在未来，还是在包括现在在内的未来。

**验证注解也可以应用于集合的元素**：

```java
List<@NotBlank String> preferences;
```

在这种情况下，将验证添加到preferences列表的任何值。

此外，该规范还**支持Java 8中的新Optional类型**：

```java
private LocalDate dateOfBirth;

public Optional<@Past LocalDate> getDateOfBirth() {
    return Optional.of(dateOfBirth);
}
```

在这里，验证框架将自动解包LocalDate值并对其进行验证。

## 5. 编程化验证

某些框架(例如Spring)具有仅使用注解即可触发验证过程的简单方法。这主要是为了让我们不必与编程验证API进行交互。

现在，让我们采用手动路线并以编程方式进行设置：

```java
ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
Validator validator = factory.getValidator();
```

要验证bean，我们首先需要一个Validator对象，它是使用ValidatorFactory构建的。

### 5.1 定义Bean

我们现在要设置这个无效的用户-使用一个空name值：

```java
User user = new User();
user.setWorking(true);
user.setAboutMe("Its all about me!");
user.setAge(50);
```

### 5.2 验证Bean

现在我们有了一个Validator，我们可以通过将它传递给validate方法来验证我们的bean。

任何违反User对象中定义的约束的行为都将作为Set返回：

```java
Set<ConstraintViolation<User>> violations = validator.validate(user);
```

通过迭代violations，我们可以使用getMessage方法获取所有违规消息：

```java
for (ConstraintViolation<User> violation : violations) {
    log.error(violation.getMessage()); 
}
```

在我们的示例(ifNameIsNull_nameValidationFails)中，该集合将包含一个带有消息“Name cannot be null”的ConstraintViolation。

## 6. 总结

本文重点介绍通过标准Java Validation API的简单传递。我们展示了使用javax.validation注解和API进行bean验证的基础知识。