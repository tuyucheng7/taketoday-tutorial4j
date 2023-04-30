## 1. 概述

在本文中，我们将讨论如何使用 Bean Validation 2.0 (JSR-380) 定义和验证方法约束。

在[上一篇文章中](https://www.baeldung.com/javax-validation)，我们讨论了 JSR-380 及其内置注解，以及如何实现属性验证。

在这里，我们将重点关注不同类型的方法约束，例如：

-   单参数约束
-   交叉参数
-   返回约束

此外，我们还将了解如何使用 Spring Validator 手动和自动验证约束。

[对于以下示例，我们需要与Java Bean Validation Basics](https://www.baeldung.com/javax-validation)中完全相同的依赖项。

## 2. 方法约束声明

首先，我们将首先讨论如何声明对方法参数和方法返回值的约束。

如前所述，我们可以使用来自javax.validation.constraints的注解，但我们也可以指定自定义约束(例如，自定义约束或交叉参数约束)。

### 2.1. 单参数约束

定义单个参数的约束很简单。我们只需要根据需要为每个参数添加注解：

```java
public void createReservation(@NotNull @Future LocalDate begin,
  @Min(1) int duration, @NotNull Customer customer) {

    // ...
}
```

同样，我们可以对构造函数使用相同的方法：

```java
public class Customer {

    public Customer(@Size(min = 5, max = 200) @NotNull String firstName, 
      @Size(min = 5, max = 200) @NotNull String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // properties, getters, and setters
}
```

### 2.2. 使用交叉参数约束

在某些情况下，我们可能需要一次验证多个值，例如，两个数值一个比另一个大。

对于这些场景，我们可以定义自定义交叉参数约束，这可能取决于两个或多个参数。

交叉参数约束可以被认为是等同于类级约束的方法验证。我们可以使用两者来实现基于多个属性的验证。

让我们考虑一个简单的示例：上一节中的createReservation()方法的变体采用LocalDate类型的两个参数：开始日期和结束日期。

因此，我们要确保begin在未来，end在begin之后。与前面的示例不同，我们不能使用单个参数约束来定义它。

相反，我们需要一个交叉参数约束。

与单参数约束相反，跨参数约束是在方法或构造函数上声明的：

```java
@ConsistentDateParameters
public void createReservation(LocalDate begin, 
  LocalDate end, Customer customer) {

    // ...
}
```

### 2.3. 创建交叉参数约束

要实现@ConsistentDateParameters约束，我们需要两个步骤。

首先，我们需要定义约束注解：

```java
@Constraint(validatedBy = ConsistentDateParameterValidator.class)
@Target({ METHOD, CONSTRUCTOR })
@Retention(RUNTIME)
@Documented
public @interface ConsistentDateParameters {

    String message() default
      "End date must be after begin date and both must be in the future";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

在这里，这三个属性对于约束注解是必需的：

-   message——返回创建错误消息的默认键，这使我们能够使用消息插值
-   组——允许我们为我们的约束指定验证组
-   payload – Bean Validation API 的客户端可以使用它来将自定义有效负载对象分配给约束

关于如何定义自定义约束的详细信息，请查看[官方文档](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-customconstraints)。

之后，我们可以定义验证器类：

```java
@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class ConsistentDateParameterValidator 
  implements ConstraintValidator<ConsistentDateParameters, Object[]> {

    @Override
    public boolean isValid(
      Object[] value, 
      ConstraintValidatorContext context) {
        
        if (value[0] == null || value[1] == null) {
            return true;
        }

        if (!(value[0] instanceof LocalDate) 
          || !(value[1] instanceof LocalDate)) {
            throw new IllegalArgumentException(
              "Illegal method signature, expected two parameters of type LocalDate.");
        }

        return ((LocalDate) value[0]).isAfter(LocalDate.now()) 
          && ((LocalDate) value[0]).isBefore((LocalDate) value[1]);
    }
}
```

如我们所见，isValid()方法包含实际的验证逻辑。首先，我们确保获得两个LocalDate 类型的参数。之后，我们检查两者是否都在未来，结束是否在开始之后。

此外，重要的是要注意ConsistentDateParameterValidator类上的@SupportedValidationTarget(ValidationTarget . PARAMETERS)注解是必需的。这样做的原因是因为@ConsistentDateParameter是在方法级别设置的，但约束应应用于方法参数(而不是方法的返回值，我们将在下一节中讨论)。

注意：Bean 验证规范建议将null 值视为有效。如果null不是有效值，则应改用@NotNull -annotation。

### 2.4. 返回值约束

有时我们需要验证方法返回的对象。为此，我们可以使用返回值约束。

以下示例使用内置约束：

```java
public class ReservationManagement {

    @NotNull
    @Size(min = 1)
    public List<@NotNull Customer> getAllCustomers() {
        return null;
    }
}
```

对于getAllCustomers()，以下约束适用：

-   首先，返回的列表不能为空，并且必须至少有一个条目
-   此外，该列表不得包含空条目

### 2.5. 返回值自定义约束

在某些情况下，我们可能还需要验证复杂的对象：

```java
public class ReservationManagement {

    @ValidReservation
    public Reservation getReservationsById(int id) {
        return null;
    }
}
```

在此示例中，返回的Reservation对象必须满足@ValidReservation定义的约束，我们将在接下来对其进行定义。

同样，我们首先必须定义约束注解：

```java
@Constraint(validatedBy = ValidReservationValidator.class)
@Target({ METHOD, CONSTRUCTOR })
@Retention(RUNTIME)
@Documented
public @interface ValidReservation {
    String message() default "End date must be after begin date "
      + "and both must be in the future, room number must be bigger than 0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

之后，我们定义验证器类：

```java
public class ValidReservationValidator
  implements ConstraintValidator<ValidReservation, Reservation> {

    @Override
    public boolean isValid(
      Reservation reservation, ConstraintValidatorContext context) {

        if (reservation == null) {
            return true;
        }

        if (!(reservation instanceof Reservation)) {
            throw new IllegalArgumentException("Illegal method signature, "
            + "expected parameter of type Reservation.");
        }

        if (reservation.getBegin() == null
          || reservation.getEnd() == null
          || reservation.getCustomer() == null) {
            return false;
        }

        return (reservation.getBegin().isAfter(LocalDate.now())
          && reservation.getBegin().isBefore(reservation.getEnd())
          && reservation.getRoom() > 0);
    }
}
```

### 2.6. 构造函数中的返回值

由于我们之前在ValidReservation接口中将METHOD和CONSTRUCTOR定义为目标，我们还可以注解Reservation的构造函数以验证构造的实例：

```java
public class Reservation {

    @ValidReservation
    public Reservation(
      LocalDate begin, 
      LocalDate end, 
      Customer customer, 
      int room) {
        this.begin = begin;
        this.end = end;
        this.customer = customer;
        this.room = room;
    }

    // properties, getters, and setters
}
```

### 2.7. 级联验证

最后，Bean Validation API 让我们不仅可以验证单个对象，还可以使用所谓的级联验证来验证对象图。

因此，如果我们想要验证复杂对象，我们可以使用@Valid进行级联验证。这适用于方法参数以及返回值。

假设我们有一个带有一些属性约束的Customer类：

```java
public class Customer {

    @Size(min = 5, max = 200)
    private String firstName;

    @Size(min = 5, max = 200)
    private String lastName;

    // constructor, getters and setters
}
```

Reservation类可能具有Customer属性，以及具有约束的其他属性：

```java
public class Reservation {

    @Valid
    private Customer customer;
	
    @Positive
    private int room;
	
    // further properties, constructor, getters and setters
}
```

如果我们现在将Reservation引用为方法参数，我们可以强制对所有属性进行递归验证：

```java
public void createNewCustomer(@Valid Reservation reservation) {
    // ...
}
```

正如我们所看到的，我们在两个地方使用@Valid ：

-   在reservation参数上：当调用createNewCustomer()时，它会触发Reservation对象的验证
-   由于我们这里有一个嵌套的对象图，我们还必须在customer属性上添加一个@Valid：从而触发这个嵌套属性的验证

这也适用于返回Reservation类型对象的方法：

```java
@Valid
public Reservation getReservationById(int id) {
    return null;
}
```

## 3. 验证方法约束

在上一节中声明约束后，我们现在可以继续实际验证这些约束。为此，我们有多种方法。

### 3.1. 使用 Spring 自动验证

Spring Validation 提供了与 Hibernate Validator 的集成。

注：Spring Validation基于AOP，默认使用Spring AOP实现。因此，验证仅适用于方法，而不适用于构造函数。

如果我们现在希望 Spring 自动验证我们的约束，我们必须做两件事：

首先，我们必须使用@Validated注解应验证的bean ：

```java
@Validated
public class ReservationManagement {

    public void createReservation(@NotNull @Future LocalDate begin, 
      @Min(1) int duration, @NotNull Customer customer){

        // ...
    }
	
    @NotNull
    @Size(min = 1)
    public List<@NotNull Customer> getAllCustomers(){
        return null;
    }
}
```

其次，我们必须提供一个MethodValidationPostProcessor bean：

```java
@Configuration
@ComponentScan({ "org.baeldung.javaxval.methodvalidation.model" })
public class MethodValidationConfig {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
```

如果违反约束，容器现在将抛出javax.validation.ConstraintViolationException 。

如果我们使用 Spring Boot，只要hibernate-validator在类路径中，容器就会为我们注册一个MethodValidationPostProcessor bean。

### 3.2. 使用 CDI 自动验证 (JSR-365)

从 1.1 版开始，Bean 验证与 CDI(Jakarta EE 的上下文和依赖注入)一起工作。

如果我们的应用程序在 Jakarta EE 容器中运行，容器将在调用时自动验证方法约束。

### 3.3. 程序化验证

对于独立Java应用程序中的手动方法验证，我们可以使用javax.validation.executable.ExecutableValidator接口。

我们可以使用以下代码检索实例：

```java
ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
ExecutableValidator executableValidator = factory.getValidator().forExecutables();
```

ExecutableValidator 提供了四种方法：

-   用于方法验证的validateParameters()和validateReturnValue()
-   validateConstructorParameters()和validateConstructorReturnValue()用于构造函数验证

验证我们第一个方法createReservation()的参数将如下所示：

```java
ReservationManagement object = new ReservationManagement();
Method method = ReservationManagement.class
  .getMethod("createReservation", LocalDate.class, int.class, Customer.class);
Object[] parameterValues = { LocalDate.now(), 0, null };
Set<ConstraintViolation<ReservationManagement>> violations 
  = executableValidator.validateParameters(object, method, parameterValues);
```

注意：官方文档不鼓励直接从应用程序代码中调用此接口，而是通过方法拦截技术(如 AOP 或代理)来使用它。

如果你对如何使用ExecutableValidator接口感兴趣，可以查看[官方文档](http://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#section-validating-executable-constraints)。

## 4. 总结

在本教程中，我们快速了解了如何通过 Hibernate Validator 使用方法约束，还讨论了 JSR-380 的一些新功能。

首先，我们讨论了如何声明不同类型的约束：

-   单参数约束
-   交叉参数
-   返回值约束

我们还了解了如何使用 Spring Validator 手动和自动验证约束。