## 1. 概述

在本教程中，我们将讨论[Bean 验证](https://www.baeldung.com/javax-validation)的约束组合。

在单个自定义注解下对多个约束进行分组可以减少代码重复并提高可读性。我们将看到如何创建组合约束以及如何根据我们的需要自定义它们。

[对于代码示例，我们将具有与Java Bean Validation Basics](https://www.baeldung.com/javax-validation)中相同的依赖关系。

## 2. 理解问题

首先，让我们熟悉一下数据模型。我们将在本文中的大部分示例中使用Account类：

```java
public class Account {

    @NotNull
    @Pattern(regexp = ".d.", message = "must contain at least one numeric character")
    @Length(min = 6, max = 32, message = "must have between 6 and 32 characters")
    private String username;
	
    @NotNull
    @Pattern(regexp = ".d.", message = "must contain at least one numeric character")
    @Length(min = 6, max = 32, message = "must have between 6 and 32 characters")
    private String nickname;
	
    @NotNull
    @Pattern(regexp = ".d.", message = "must contain at least one numeric character")
    @Length(min = 6, max = 32, message = "must have between 6 and 32 characters")
    private String password;

    // getters and setters
}
```

我们可以注意到@NotNull、 @Pattern和@Length约束组对三个字段中的每一个重复。

此外，如果这些字段之一出现在来自不同层的多个类中，则约束应该匹配——导致更多的代码重复。

例如，我们可以想象在 DTO 对象和@Entity模型中有用户名字段。

## 3. 创建组合约束

我们可以通过在具有合适名称的自定义注解下对三个约束进行分组来避免代码重复：

```java
@NotNull
@Pattern(regexp = ".d.", message = "must contain at least one numeric character")
@Length(min = 6, max = 32, message = "must have between 6 and 32 characters")
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidAlphanumeric {

    String message() default "field should have a valid length and contain numeric character(s).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

因此，我们现在可以使用@ValidAlphanumeric来验证帐户字段：

```typescript
public class Account {

    @ValidAlphanumeric
    private String username;

    @ValidAlphanumeric
    private String password;

    @ValidAlphanumeric
    private String nickname;

    // getters and setters
}
```

因此，我们可以测试@ValidAlphanumeric 注解，并期望违反约束的次数与违反次数一样多。

例如，如果我们将用户名设置为“john”，我们应该预料到两次违规，因为它太短而且不包含数字字符：

```java
@Test
public void whenUsernameIsInvalid_validationShouldReturnTwoViolations() {
    Account account = new Account();
    account.setPassword("valid_password123");
    account.setNickname("valid_nickname123");
    account.setUsername("john");

    Set<ConstraintViolation<Account>> violations = validator.validate(account);

    assertThat(violations).hasSize(2);
}
```

## 4.使用@ReportAsSingleViolation

另一方面，我们可能希望验证为整个组返回一个ConstraintViolation。

为此，我们必须使用@ReportAsSingleViolation注解我们的组合约束：

```java
@NotNull
@Pattern(regexp = ".d.", message = "must contain at least one numeric character")
@Length(min = 6, max = 32, message = "must have between 6 and 32 characters")
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
@ReportAsSingleViolation
public @interface ValidAlphanumericWithSingleViolation {

    String message() default "field should have a valid length and contain numeric character(s).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

之后，我们可以使用密码字段测试我们的新注解，并期待一次违规：

```java
@Test
public void whenPasswordIsInvalid_validationShouldReturnSingleViolation() {
    Account account = new Account();
    account.setUsername("valid_username123");
    account.setNickname("valid_nickname123");
    account.setPassword("john");

    Set<ConstraintViolation<Account>> violations = validator.validate(account);

    assertThat(violations).hasSize(1);
}

```

## 5. 布尔约束组合

到目前为止，只有当所有组合约束都有效时，验证才会通过。发生这种情况是因为ConstraintComposition值默认为CompositionType.AND。 

但是，如果我们想检查是否至少存在一个有效约束，我们可以更改此行为。

为此，我们需要将ConstraintComposition切换为CompositionType。或者：

```java
@Pattern(regexp = ".d.", message = "must contain at least one numeric character")
@Length(min = 6, max = 32, message = "must have between 6 and 32 characters")
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
@ConstraintComposition(CompositionType.OR)
public @interface ValidLengthOrNumericCharacter {

    String message() default "field should have a valid length or contain numeric character(s).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

例如，给定一个太短但至少有一个数字字符的值，应该没有违规。

让我们使用模型中的昵称字段来测试这个新注解：

```java
@Test
public void whenNicknameIsTooShortButContainsNumericCharacter_validationShouldPass() {
    Account account = new Account();
    account.setUsername("valid_username123");
    account.setPassword("valid_password123");
    account.setNickname("doe1");

    Set<ConstraintViolation<Account>> violations = validator.validate(account);

    assertThat(violations).isEmpty();
}
```

同样，我们可以使用CompositionType。ALL_FALSE 如果我们想确保约束失败。

## 6. 使用组合约束进行方法验证

此外，我们可以使用组合约束作为[方法约束](https://www.baeldung.com/javax-validation-method-constraints)。

为了验证方法的返回值，我们只需要将@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)添加到组合约束中：

```java
@NotNull
@Pattern(regexp = ".d.", message = "must contain at least one numeric character")
@Length(min = 6, max = 32, message = "must have between 6 and 32 characters")
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public @interface AlphanumericReturnValue {

    String message() default "method return value should have a valid length and contain numeric character(s).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

为了举例说明这一点，我们将使用getAnInvalidAlphanumericValue方法，该方法使用我们的自定义约束进行注解：

```java
@Component
@Validated
public class AccountService {

    @AlphanumericReturnValue
    public String getAnInvalidAlphanumericValue() {
        return "john"; 
    }
}
```

现在，让我们调用此方法并期望抛出ConstraintViolationException ：

```java
@Test
public void whenMethodReturnValuesIsInvalid_validationShouldFail() {
    assertThatThrownBy(() -> accountService.getAnInvalidAlphanumericValue())				 
      .isInstanceOf(ConstraintViolationException.class)
      .hasMessageContaining("must contain at least one numeric character")
      .hasMessageContaining("must have between 6 and 32 characters");
}
```

## 七. 总结

在本文中，我们了解了如何使用组合约束来避免代码重复。

之后，我们学习了自定义组合约束以使用布尔逻辑进行验证、返回单个约束违规以及应用于方法返回值。