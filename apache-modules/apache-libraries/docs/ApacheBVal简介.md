## 1. 简介

在本文中，我们将了解Apache BVal库对Java Bean 验证规范 ( JSR 349 )的实现。

## 2.Maven依赖

为了使用Apache BVal，我们首先需要将以下依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.apache.bval</groupId>
    <artifactId>bval-jsr</artifactId>
    <version>1.1.2</version>
</dependency>
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>1.1.0.Final</version>
</dependency>
```

可以在可选的bval-extras依赖项中找到自定义BVal约束：

```java
<dependency>
    <groupId>org.apache.bval</groupId>
    <artifactId>bval-extras</artifactId>
    <version>1.1.2</version>
</dependency>
```

最新版本的[bval-jsr](https://search.maven.org/classic/#search|ga|1|a%3A"bval-jsr")、[bval-extras](https://search.maven.org/classic/#search|ga|1|apache bval extras)和[validation-api](https://search.maven.org/classic/#search|ga|1|g%3A"javax.validation")可以从 Maven Central 下载。

## 3.应用约束

Apache BVal为javax.validation包中定义的所有约束提供实现。为了对 bean 的属性应用约束，我们可以将约束注解添加到属性声明中。

让我们创建一个具有四个属性的User类，然后应用@NotNull、@Size和@Min注解：

```java
public class User {
    
    @NotNull
    private String email;
    
    private String password;

    @Size(min=1, max=20)
    private String name;

    @Min(18)
    private int age;

    // standard constructor, getters, setters
}
```

## 4. 验证 Bean

为了验证应用于User类的约束，我们需要获得一个ValidatorFactory实例和一个或多个Validator实例。

### 4.1. 获取ValidatorFactory

Apache BVal文档建议获取此类的单个实例，因为工厂创建是一个要求很高的过程：

```java
ValidatorFactory validatorFactory 
  = Validation.byProvider(ApacheValidationProvider.class)
  .configure().buildValidatorFactory();
```

### 4.2. 获取验证器

接下来，我们需要从上面定义的validatorFactory中获取一个Validator实例：

```java
Validator validator = validatorFactory.getValidator();
```

这是一个线程安全的实现，所以我们可以安全地重用已经创建的实例。

Validator类提供了三种方法来确定 bean 的有效性：validate ()、validateProperty()和validateValue()。

这些方法中的每一个都返回一组ConstraintViolation对象，其中包含有关未遵守的约束的信息。

### 4.3. 验证() API

validate()方法检查整个 bean的有效性，这意味着它验证应用于作为参数传递的对象的属性的所有约束。

让我们创建一个JUnit测试，我们在其中定义一个User对象并使用validate()方法来测试其属性：

```java
@Test
public void givenUser_whenValidate_thenValidationViolations() {
    User user
      = new User("ana@yahoo.com", "pass", "nameTooLong_______________", 15);

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertTrue("no violations", violations.size() > 0);
}
```

### 4.4. 验证属性() API

validateProperty()方法可用于验证 bean的单个属性。

让我们创建一个JUnit测试，我们将在其中定义一个年龄属性小于所需最小值 18的User对象，并验证验证此属性是否会导致一次违规：

```java
@Test
public void givenInvalidAge_whenValidateProperty_thenConstraintViolation() {
    User user = new User("ana@yahoo.com", "pass", "Ana", 12);

    Set<ConstraintViolation<User>> propertyViolations
      = validator.validateProperty(user, "age");
 
    assertEquals("size is not 1", 1, propertyViolations.size());
}
```

### 4.5. 验证值() API

validateValue()方法可用于在将某些值设置到 bean 之前检查它是否是 bean 属性的有效值。

让我们使用User对象创建一个JUnit测试，然后验证值20是否为age属性的有效值：

```java
@Test
public void givenValidAge_whenValidateValue_thenNoConstraintViolation() {
    User user = new User("ana@yahoo.com", "pass", "Ana", 18);
    
    Set<ConstraintViolation<User>> valueViolations
      = validator.validateValue(User.class, "age", 20);
 
    assertEquals("size is not 0", 0, valueViolations.size());
}
```

### 4.6. 关闭ValidatorFactory

在使用完ValidatorFactory之后，我们必须记得在最后关闭它：

```java
if (validatorFactory != null) {
    validatorFactory.close();
}
```

## 5. 非JSR约束

Apache BVal库还提供了一系列不属于JSR规范的约束，并提供了额外的和更强大的验证功能。

bval -jsr包包含两个附加约束：@Email用于验证有效的电子邮件地址，@NotEmpty用于确保值不为空。

其余自定义BVal约束在可选包bval-extras中提供。

此包包含用于验证各种数字格式的约束，例如确保数字是有效国际银行帐号的@IBAN注解、验证有效标准书号的@Isbn 注解以及用于验证国际商品编号的@ EAN13注解.

该库还提供注解以确保各种类型信用卡号的有效性： @ AmericanExpress、 @Diners 、@Discover、@ Mastercard和@Visa。

你可以使用@Domain和@InetAddress注解来确定一个值是否包含有效的域或 Internet 地址。

最后，该包包含用于验证File对象是否为目录的@Directory和@NotDirectory注解。

让我们在我们的User类上定义额外的属性，并对它们应用一些非JSR注解：

```java
public class User {
    
    @NotNull
    @Email
    private String email;
    
    @NotEmpty
    private String password;

    @Size(min=1, max=20)
    private String name;

    @Min(18)
    private int age;
    
    @Visa
    private String cardNumber = "";
    
    @IBAN
    private String iban = "";
    
    @InetAddress
    private String website = "";

    @Directory
    private File mainDirectory = new File(".");

    // standard constructor, getters, setters
}
```

可以用与JSR约束类似的方式测试约束：

```java
@Test
public void whenValidateNonJSR_thenCorrect() {
    User user = new User("ana@yahoo.com", "pass", "Ana", 20);
    user.setCardNumber("1234");
    user.setIban("1234");
    user.setWebsite("10.0.2.50");
    user.setMainDirectory(new File("."));
    
    Set<ConstraintViolation<User>> violations 
      = validator.validateProperty(user,"iban");
 
    assertEquals("size is not 1", 1, violations.size());
 
    violations = validator.validateProperty(user,"website");
 
    assertEquals("size is not 0", 0, violations.size());

    violations = validator.validateProperty(user, "mainDirectory");
 
    assertEquals("size is not 0", 0, violations.size());
}
```

虽然这些额外的注解可以方便地满足潜在的验证需求，但使用不属于JSR规范的注解的一个缺点是，如果以后有必要，你无法轻松切换到不同的JSR实现。

## 6.自定义约束

为了定义我们自己的约束，我们首先需要按照标准语法创建一个注解。

让我们创建一个密码注解来定义用户密码必须满足的条件：

```java
@Constraint(validatedBy = { PasswordValidator.class })
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Invalid password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int length() default 6;

    int nonAlpha() default 1;
}
```

密码值的实际验证是在一个实现了ConstraintValidator接口的类中完成的——在我们的例子中是PasswordValidator类。此类重写isValid()方法并验证密码的长度是否小于length属性，以及它是否包含少于nonAlpha属性中指定数量的非字母数字字符：

```java
public class PasswordValidator 
  implements ConstraintValidator<Password, String> {

    private int length;
    private int nonAlpha;

    @Override
    public void initialize(Password password) {
        this.length = password.length();
        this.nonAlpha = password.nonAlpha();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value.length() < length) {
            return false;
        }
        int nonAlphaNr = 0;
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isLetterOrDigit(value.charAt(i))) {
                nonAlphaNr++;
            }
        }
        if (nonAlphaNr < nonAlpha) {
            return false;
        }
        return true;
    }
}
```

让我们将自定义约束应用于User类的密码属性：

```java
@Password(length = 8)
private String password;
```

我们可以创建一个JUnit测试来验证无效的密码值是否会导致违反约束：

```java
@Test
public void givenValidPassword_whenValidatePassword_thenNoConstraintViolation() {
    User user = new User("ana@yahoo.com", "password", "Ana", 20);
    Set<ConstraintViolation<User>> violations 
      = validator.validateProperty(user, "password");
 
    assertEquals(
      "message incorrect",
      "Invalid password", 
      violations.iterator().next().getMessage());
}
```

现在让我们创建一个JUnit测试，我们在其中验证一个有效的密码值：

```java
@Test
public void givenValidPassword_whenValidatePassword_thenNoConstraintViolation() {
    User user = new User("ana@yahoo.com", "password#", "Ana", 20);
		
    Set<ConstraintViolation<User>> violations 
      = validator.validateProperty(user, "password");
    assertEquals("size is not 0", 0, violations.size());
}
```

## 七. 总结

在本文中，我们举例说明了Apache BVal bean 验证实现的使用。