## 1. 概述

在本快速教程中，我们介绍了使用标准框架 JSR 380(也称为Bean Validation 2.0 )验证Javabean 的基础知识。

在大多数应用程序中，验证用户输入是一个非常普遍的要求。而JavaBean Validation 框架已经成为处理这种逻辑的事实上的标准。

## 延伸阅读：

## [Spring Boot 中的验证](https://www.baeldung.com/spring-boot-bean-validation)

了解如何使用 Hibernate Validator(Bean 验证框架的参考实现)在Spring Boot中验证域对象。

[阅读更多](https://www.baeldung.com/spring-boot-bean-validation)→

## [Bean Validation 2.0 的方法约束](https://www.baeldung.com/javax-validation-method-constraints)

使用 Bean Validation 2.0 的方法约束简介。

[阅读更多](https://www.baeldung.com/javax-validation-method-constraints)→

## 2.JSR 380

JSR 380 是用于 bean 验证的JavaAPI 规范，是 Jakarta EE 和 JavaSE 的一部分。这确保了 bean 的属性满足特定标准，使用@NotNull、@Min和@Max等注解。

此版本需要Java8 或更高版本，并利用Java8 中添加的新功能，例如类型注解和对Optional和LocalDate等新类型的支持。

有关规范的完整信息，请继续阅读[JSR 380](https://jcp.org/en/jsr/detail?id=380)。

## 3.依赖关系

我们将使用 Maven 示例来显示所需的依赖项。但当然，可以通过各种方式添加这些罐子。

### 3.1. 验证API

根据 JSR 380 规范，validation-api依赖项包含标准验证 API：

```xml
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>2.0.1.Final</version>
</dependency>
```

### 3.2. 验证 API 参考实现

Hibernate Validator 是验证 API 的参考实现。

要使用它，我们需要添加以下依赖项：

```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.0.13.Final</version>
</dependency>

```

快速说明：[hibernate-validator](https://search.maven.org/artifact/org.hibernate.validator/hibernate-validator)完全独立于 Hibernate 的持久性方面。因此，通过将其添加为依赖项，我们并没有将这些持久性方面添加到项目中。 

### 3.3. 表达式语言依赖

JSR 380 支持变量插值，允许在违规消息中使用表达式。

为了解析这些表达式，我们将添加来自 GlassFish 的[javax.el](https://search.maven.org/artifact/org.glassfish/javax.el)依赖项，它包含表达式语言规范的实现：

```xml
<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>javax.el</artifactId>
    <version>3.0.0</version>
</dependency>
```

## 4.使用验证注解

在这里，我们将采用一个User bean 并向其添加一些简单的验证：

```java
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;

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

示例中使用的所有注解都是标准的 JSR 注解：

-   @NotNull验证带注解的属性值不为null。
-   @AssertTrue验证带注解的属性值是否为真。
-   @Size验证带注解的属性值的大小介于属性min和max之间；可以应用于String、 Collection、 Map和数组属性。
-   @Min验证带注解的属性的值不小于value属性。
-   @Max验证带注解的属性的值不大于value属性。
-   @Email验证带注解的属性是有效的电子邮件地址。

一些注解接受额外的属性，但message属性对它们都是通用的。这是当相应属性的值未通过验证时通常会呈现的消息。

以及可以在 JSR 中找到的一些附加注解：

-   @NotEmpty验证该属性不为 null 或为空；可以应用于String、 Collection、 Map或Array值。
-   @NotBlank只能应用于文本值并验证该属性不是 null 或空格。
-   @Positive和@PositiveOrZero应用于数值并验证它们是严格正数，还是正数包括 0。
-   @Negative和@NegativeOrZero适用于数值并验证它们是否严格为负数，或是否为负数(包括 0)。
-   @Past和@PastOrPresent验证日期值是过去还是过去包括现在；可以应用于日期类型，包括那些在Java8 中添加的类型。
-   @Future和@FutureOrPresent验证日期值是在未来，还是在包括现在在内的未来。

验证注解也可以应用于集合的元素：

```java
List<@NotBlank String> preferences;
```

在这种情况下，将验证添加到首选项列表的任何值。

此外，该规范还支持Java 8 中 的新Optional类型：

```java
private LocalDate dateOfBirth;

public Optional<@Past LocalDate> getDateOfBirth() {
    return Optional.of(dateOfBirth);
}
```

在这里，验证框架将自动解包LocalDate值并对其进行验证。

## 5.程序化验证

某些框架(例如 Spring)具有仅使用注解即可触发验证过程的简单方法。这主要是为了让我们不必与编程验证 API 进行交互。

现在让我们走手动路线并以编程方式进行设置：

```java
ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
Validator validator = factory.getValidator();

```

要验证 bean，我们首先需要一个Validator对象，它是使用ValidatorFactory构建的。

### 5.1. 定义 Bean

我们现在要设置这个无效的用户——使用一个空名称值：

```java
User user = new User();
user.setWorking(true);
user.setAboutMe("Its all about me!");
user.setAge(50);

```

### 5.2. 验证 Bean

现在我们有了一个Validator，我们可以通过将它传递给validate方法来验证我们的 bean。

任何违反User对象中定义的约束的行为都将作为Set返回：

```java
Set<ConstraintViolation<User>> violations = validator.validate(user);

```

通过迭代违规，我们可以使用getMessage方法获取所有违规消息：

```java
for (ConstraintViolation<User> violation : violations) {
    log.error(violation.getMessage()); 
}

```

在我们的示例 ( ifNameIsNull_nameValidationFails ) 中，该集合将包含一个 带有消息“Name cannot be null”的ConstraintViolation 。

## 六. 总结

本文重点介绍如何简单地通过标准JavaValidation API。我们展示了使用javax.validation注解和 API进行 bean 验证的基础知识。