## 1. 简介

在我们的[Java Bean Validation Basics](https://www.baeldung.com/javax-validation)教程中，我们看到了各种内置javax.validation约束的用法。在本教程中，我们将了解如何对javax.validation约束进行分组。

## 2.用例

有很多场景，我们需要对bean 的某一组字段应用约束，然后我们想对同一个 bean 的另一组字段应用约束。

例如，假设我们有一个两步注册表单。在第一步中，我们要求用户提供基本信息，如名字、姓氏、电子邮件 ID、电话号码和验证码。当用户提交此数据时，我们只想验证此信息。

在下一步中，我们要求用户提供一些其他信息，如地址，我们也想验证这些信息——注意验证码出现在这两个步骤中。

## 3.分组验证约束

所有javax验证约束都有一个名为groups的属性。 当我们为一个元素添加约束时，我们可以声明约束所属的组的名称。这是通过在约束的组属性中指定组接口的类名来完成的。

理解某事的最好方法是亲自动手。让我们实际看看如何将javax约束组合成组。

### 3.1. 声明约束组

第一步是创建一些接口。这些接口将是约束组名称。在我们的用例中，我们将验证约束分为两组。

让我们看看第一个约束组BasicInfo：

```java
public interface BasicInfo {
}
```

下一个约束组是AdvanceInfo：

```java
public interface AdvanceInfo {
}
```

### 3.2. 使用约束组

现在我们已经声明了我们的约束组，是时候在我们的RegistrationFormJavabean 中使用它们了：

```java
public class RegistrationForm {
    @NotBlank(groups = BasicInfo.class)
    private String firstName;
    @NotBlank(groups = BasicInfo.class)
    private String lastName;
    @Email(groups = BasicInfo.class)
    private String email;
    @NotBlank(groups = BasicInfo.class)
    private String phone;

    @NotBlank(groups = {BasicInfo.class, AdvanceInfo.class})
    private String captcha;

    @NotBlank(groups = AdvanceInfo.class)
    private String street;
    
    @NotBlank(groups = AdvanceInfo.class)
    private String houseNumber;
    
    @NotBlank(groups = AdvanceInfo.class)
    private String zipCode;
    
    @NotBlank(groups = AdvanceInfo.class)
    private String city;
    
    @NotBlank(groups = AdvanceInfo.class)
    private String contry;
}
```

使用约束组属性，我们根据用例将 bean 的字段分为两组。默认情况下，所有约束都包含在默认约束组中。

### 3.3. 测试具有一组的约束

现在我们已经声明了约束组并在我们的 bean 类中使用了它们，是时候看看这些约束组的作用了。

首先，我们将查看基本信息何时不完整，使用我们的BasicInfo约束组进行验证。在字段的@NotBlank约束的groups属性中使用BasicInfo.class时，我们应该对任何留空的字段违反约束：

```java
public class RegistrationFormUnitTest {
    private static Validator validator;

    @BeforeClass
    public static void setupValidatorInstance() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void whenBasicInfoIsNotComplete_thenShouldGiveConstraintViolationsOnlyForBasicInfo() {
        RegistrationForm form = buildRegistrationFormWithBasicInfo();
        form.setFirstName("");
 
        Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form, BasicInfo.class);
 
        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> {
            assertThat(action.getMessage()).isEqualTo("must not be blank");
            assertThat(action.getPropertyPath().toString()).isEqualTo("firstName");
        });
    }

    private RegistrationForm buildRegistrationFormWithBasicInfo() {
        RegistrationForm form = new RegistrationForm();
        form.setFirstName("devender");
        form.setLastName("kumar");
        form.setEmail("anyemail@yopmail.com");
        form.setPhone("12345");
        form.setCaptcha("Y2HAhU5T");
        return form;
    }
 
    //... additional tests
}
```

在下一个场景中，我们将检查高级信息何时不完整，使用我们的AdvanceInfo约束组进行验证：

```java
@Test
public void whenAdvanceInfoIsNotComplete_thenShouldGiveConstraintViolationsOnlyForAdvanceInfo() {
    RegistrationForm form = buildRegistrationFormWithAdvanceInfo();
    form.setZipCode("");
 
    Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form, AdvanceInfo.class);
 
    assertThat(violations.size()).isEqualTo(1);
    violations.forEach(action -> {
        assertThat(action.getMessage()).isEqualTo("must not be blank");
        assertThat(action.getPropertyPath().toString()).isEqualTo("zipCode");
    });
}

private RegistrationForm buildRegistrationFormWithAdvanceInfo() {
    RegistrationForm form = new RegistrationForm();
    return populateAdvanceInfo(form);
}

private RegistrationForm populateAdvanceInfo(RegistrationForm form) {
    form.setCity("Berlin");
    form.setContry("DE");
    form.setStreet("alexa str.");
    form.setZipCode("19923");
    form.setHouseNumber("2a");
    form.setCaptcha("Y2HAhU5T");
    return form;
}
```

### 3.4. 测试具有多个组的约束

我们可以为一个约束指定多个组。在我们的用例中，我们 在基本信息和高级信息中都使用验证码。让我们首先使用BasicInfo测试验证码：

```java
@Test
public void whenCaptchaIsBlank_thenShouldGiveConstraintViolationsForBasicInfo() {
    RegistrationForm form = buildRegistrationFormWithBasicInfo();
    form.setCaptcha("");
 
    Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form, BasicInfo.class);
 
    assertThat(violations.size()).isEqualTo(1);
    violations.forEach(action -> {
        assertThat(action.getMessage()).isEqualTo("must not be blank");
        assertThat(action.getPropertyPath().toString()).isEqualTo("captcha");
    });
}
```

现在让我们使用AdvanceInfo测试验证码：

```java
@Test
public void whenCaptchaIsBlank_thenShouldGiveConstraintViolationsForAdvanceInfo() {
    RegistrationForm form = buildRegistrationFormWithAdvanceInfo();
    form.setCaptcha("");
 
    Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form, AdvanceInfo.class);
 
    assertThat(violations.size()).isEqualTo(1);
    violations.forEach(action -> {
        assertThat(action.getMessage()).isEqualTo("must not be blank");
        assertThat(action.getPropertyPath().toString()).isEqualTo("captcha");
    });
}
```

## 4. 使用GroupSequence指定约束组验证顺序

默认情况下，不以任何特定顺序评估约束组。但是我们可能有一些用例，其中某些组应该先于其他组进行验证。为此，我们可以使用GroupSequence 指定组验证的顺序。

有两种使用GroupSequence注解的方法：

-   在被验证的实体上
-   在界面上

### 4.1. 在要验证的实体上使用GroupSequence

这是对约束进行排序的简单方法。让我们用GroupSequence注解实体并指定约束的顺序：

```java
@GroupSequence({BasicInfo.class, AdvanceInfo.class})
public class RegistrationForm {
    @NotBlank(groups = BasicInfo.class)
    private String firstName;
    @NotBlank(groups = AdvanceInfo.class)
    private String street;
}
```

### 4.2. 在接口上使用GroupSequence

我们还可以使用接口指定约束验证的顺序。这种方法的优点是相同的序列可以用于其他实体。让我们看看如何将GroupSequence与我们上面定义的接口一起使用：

```java
@GroupSequence({BasicInfo.class, AdvanceInfo.class})
public interface CompleteInfo {
}
```

### 4.3. 测试组序

现在让我们测试GroupSequence。首先，我们将测试如果BasicInfo不完整，则不会评估AdvanceInfo组约束：

```java
@Test
public void whenBasicInfoIsNotComplete_thenShouldGiveConstraintViolationsForBasicInfoOnly() {
    RegistrationForm form = buildRegistrationFormWithBasicInfo();
    form.setFirstName("");
 
    Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form, CompleteInfo.class);
 
    assertThat(violations.size()).isEqualTo(1);
    violations.forEach(action -> {
        assertThat(action.getMessage()).isEqualTo("must not be blank");
        assertThat(action.getPropertyPath().toString()).isEqualTo("firstName");
    });
}
```

接下来，测试当BasicInfo完成时，应该评估AdvanceInfo约束：

```java
@Test
public void whenBasicAndAdvanceInfoIsComplete_thenShouldNotGiveConstraintViolationsWithCompleteInfoValidationGroup() {
    RegistrationForm form = buildRegistrationFormWithBasicAndAdvanceInfo();
 
    Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form, CompleteInfo.class);
 
    assertThat(violations.size()).isEqualTo(0);
}
```

## 5.总结

在本快速教程中，我们了解了如何对javax.validation约束进行分组。