## 1. 概述

Java JSR 380 的特性之一是允许在使用参数插入验证消息时使用表达式。

当我们使用 Hibernate Validator 时，有一个要求我们需要将JavaJSR 341 的统一实现之一作为依赖添加到我们的项目中。JSR 341 也称为表达式语言 API。

但是，如果我们不需要根据我们的用例支持解析表达式，那么添加额外的库可能会很麻烦。

在这个简短的教程中，我们将了解如何在 Hibernate Validator中配置[ParameterMessageInterpolator 。](https://docs.jboss.org/hibernate/stable/validator/api/org/hibernate/validator/messageinterpolation/ParameterMessageInterpolator.html)

## 2. 消息插值器

除了[验证Javabean 的基础知识](https://www.baeldung.com/javax-validation)之外，Bean Validation API 的MessageInterpolator是一种抽象，它为我们提供了一种执行简单插值的方法，而无需解析表达式的麻烦。

此外，Hibernate Validator 提供了一个基于非表达式的ParameterMessageInterpolator，因此，我们不需要任何额外的库来配置它。

## 3. 设置自定义消息插值器

要删除表达式语言依赖性，我们可以使用自定义消息插值器并配置不支持表达式的 Hibernate Validator。

让我们展示一些设置自定义消息插值器的便捷方法。在我们的案例中，我们将使用内置的ParameterMessageInterpolator。

### 3.1. 配置ValidatorFactory

设置自定义消息插值器的一种方法是在引导时配置ValidatorFactory 。

因此，我们可以使用ParameterMessageInterpolator构建一个ValidatorFactory实例：

```java
ValidatorFactory validatorFactory = Validation.byDefaultProvider()
  .configure()
  .messageInterpolator(new ParameterMessageInterpolator())
  .buildValidatorFactory();

```

### 3.2. 配置验证器

同样，我们可以在初始化Validator实例时设置ParameterMessageInterpolator ：

```java
Validator validator = validatorFactory.usingContext()
  .messageInterpolator(new ParameterMessageInterpolator())
  .getValidator();

```

## 4. 执行验证

要查看ParameterMessageInterpolator的工作原理，我们需要一个带有一些 JSR 380 注解的示例Javabean。

### 4.1. 示例JavaBean

让我们定义我们的示例Javabean Person：

```java
public class Person {

    @Size(min = 10, max = 100, message = "Name should be between {min} and {max} characters")
    private String name;

    @Min(value = 18, message = "Age should not be less than {value}")
    private int age;

    @Email(message = "Email address should be in a correct format: ${validatedValue}")
    private String email;

    // standard getters and setters
}

```

### 4.2. 测试消息参数

当然，要执行我们的验证，我们应该使用从我们之前配置的ValidatorFactory访问的Validator实例。

所以，我们需要访问我们的验证器：

```java
Validator validator = validatorFactory.getValidator();

```

之后，我们可以为name字段编写我们的测试方法：

```java
@Test
public void givenNameLengthLessThanMin_whenValidate_thenValidationFails() {
    Person person = new Person();
    person.setName("John Doe");
    person.setAge(18);

    Set<ConstraintViolation<Person>> violations = validator.validate(person);
 
    assertEquals(1, violations.size());

    ConstraintViolation<Person> violation = violations.iterator().next();
 
    assertEquals("Name should be between 10 and 100 characters", violation.getMessage());
}
```

验证消息正确插入了{min}和{max}的变量：

```java
Name should be between 10 and 100 characters

```

接下来，让我们为年龄字段编写一个类似的测试：

```java
@Test
public void givenAgeIsLessThanMin_whenValidate_thenValidationFails() {
    Person person = new Person();
    person.setName("John Stephaner Doe");
    person.setAge(16);

    Set<ConstraintViolation<Person>> violations = validator.validate(person);
 
    assertEquals(1, violations.size());

    ConstraintViolation<Person> violation = violations.iterator().next();
 
    assertEquals("Age should not be less than 18", violation.getMessage());
}
```

类似地，验证消息如我们预期的那样使用变量{value}正确插入：

```java
Age should not be less than 18

```

### 4.3. 测试表达式

要查看ParameterMessageInterpolator如何处理表达式，让我们为涉及简单${validatedValue}表达式的电子邮件字段编写另一个测试：

```java
@Test
public void givenEmailIsMalformed_whenValidate_thenValidationFails() {
    Person person = new Person();
    person.setName("John Stephaner Doe");
    person.setAge(18);
    person.setEmail("johndoe.dev");
    
    Set<ConstraintViolation<Person>> violations = validator.validate(person);
 
    assertEquals(1, violations.size());
    
    ConstraintViolation<Person> violation = violations.iterator().next();
 
    assertEquals("Email address should be in a correct format: ${validatedValue}", violation.getMessage());
}
```

这次，不对表达式${validatedValue}进行插值。

ParameterMessageInterpolator只支持参数的插值，不支持使用$表示法的解析表达式。相反，它只是返回未插值的它们。

## 5.总结

在本文中，我们了解了ParameterMessageInterpolator的用途以及如何在 Hibernate Validator 中配置它。