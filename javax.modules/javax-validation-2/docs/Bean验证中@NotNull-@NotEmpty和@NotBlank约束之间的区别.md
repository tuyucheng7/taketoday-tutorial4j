## 1. 概述

[Bean Validation](https://beanvalidation.org/2.0/) 是一种标准的验证规范，它允许我们通过使用一组以注解形式声明的约束来轻松验证域对象。

虽然总体上使用 Bean 验证实现(例如[Hibernate Validator](http://hibernate.org/validator/))相当简单，但值得探索一些微妙但相关的差异，这些差异是如何实现这些约束的。

在本教程中，我们将探讨@ NotNull、 @ NotEmpty和@NotBlank约束之间的区别。

## 2. Maven 依赖

要快速设置工作环境并测试@NotNull、 @ NotEmpty和@NotBlank约束的行为，首先我们需要添加所需的 Maven 依赖项。

在这种情况下，我们将使用[Hibernate Validator](http://hibernate.org/validator/)(bean 验证参考实现)来验证我们的域对象。

这是我们的pom.xml文件的相关部分：

```xml
<dependencies> 
    <dependency> 
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>6.0.13.Final</version>
    </dependency> 
    <dependency> 
        <groupId>org.glassfish</groupId>
        <artifactId>javax.el</artifactId>
        <version>3.0.0</version>
     </dependency>
</dependencies>

```

我们将在单元测试中使用[JUnit](https://junit.org/junit5/)和[AssertJ](https://joel-costigliola.github.io/assertj/)，因此请确保 在 Maven Central 上检查最新版本的[hibernate-validator](https://search.maven.org/search?q=g:org.hibernate AND a:hibernate-validator&core=gav)、[GlassFish 的 EL 实现](https://mvnrepository.com/artifact/org.glassfish/javax.el)、[junit](https://search.maven.org/search?q=g:junit AND a:junit&core=gav)和[assertj-core 。](https://search.maven.org/search?q=g:org.assertj AND a:assertj-core&core=gav)

## 3. @NotNull约束

继续前进，让我们实现一个简单的UserNotNull域类并使用@NotNull注解约束其名称字段：

```java
public class UserNotNull {
    
    @NotNull(message = "Name may not be null")
    private String name;
    
    // standard constructors / getters / toString   
}
```

现在我们需要检查@NotNull在底层是如何工作的。 

为此，让我们为该类创建一个简单的单元测试，并验证它的几个实例：

```java
@BeforeClass
public static void setupValidatorInstance() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
}

@Test
public void whenNotNullName_thenNoConstraintViolations() {
    UserNotNull user = new UserNotNull("John");
    Set<ConstraintViolation<UserNotNull>> violations = validator.validate(user);
 
    assertThat(violations.size()).isEqualTo(0);
}
    
@Test
public void whenNullName_thenOneConstraintViolation() {
    UserNotNull user = new UserNotNull(null);
    Set<ConstraintViolation<UserNotNull>> violations = validator.validate(user);
 
    assertThat(violations.size()).isEqualTo(1);
}
    
@Test
public void whenEmptyName_thenNoConstraintViolations() {
    UserNotNull user = new UserNotNull("");
    Set<ConstraintViolation<UserNotNull>> violations = validator.validate(user);
 
    assertThat(violations.size()).isEqualTo(0);
}

```

正如预期的那样，@NotNull约束不允许约束字段的空值。但是，字段可以为空。

为了更好地理解这一点，让我们看一下[NotNullValidator类](http://docs.jboss.org/ejb3/app-server/HibernateAnnotations/api/org/hibernate/validator/NotNullValidator.html#isValid(java.lang.Object))的isValid()方法， @ NotNull约束使用该方法。方法实现非常简单：

```java
public boolean isValid(Object object) {
    return object != null;  
}
```

如上所示，使用@NotNull约束的字段(例如CharSequence、Collection、Map或Array )必须不为空。然而，空值是完全合法的。

## 4. @NotEmpty约束

现在让我们实现一个示例UserNotEmpty类并使用@NotEmpty约束：

```java
public class UserNotEmpty {
    
    @NotEmpty(message = "Name may not be empty")
    private String name;
    
    // standard constructors / getters / toString
}
```

有了这个类，让我们通过为name字段分配不同的值来测试它：

```java
@Test
public void whenNotEmptyName_thenNoConstraintViolations() {
    UserNotEmpty user = new UserNotEmpty("John");
    Set<ConstraintViolation<UserNotEmpty>> violations = validator.validate(user);
 
    assertThat(violations.size()).isEqualTo(0);
}
    
@Test
public void whenEmptyName_thenOneConstraintViolation() {
    UserNotEmpty user = new UserNotEmpty("");
    Set<ConstraintViolation<UserNotEmpty>> violations = validator.validate(user);
 
    assertThat(violations.size()).isEqualTo(1);
}
    
@Test
public void whenNullName_thenOneConstraintViolation() {
    UserNotEmpty user = new UserNotEmpty(null);
    Set<ConstraintViolation<UserNotEmpty>> violations = validator.validate(user);
 
    assertThat(violations.size()).isEqualTo(1);
}
```

@NotEmpty注解使用了 @NotNull 类的 isValid()实现，并且还检查所提供对象的大小/长度(当然，这根据被验证对象的类型而有所不同)是否大于零。

简而言之，这意味着用@NotEmpty约束的字段(例如CharSequence、Collection、Map或Array )必须不为空，并且其大小/长度必须大于零。

此外，如果我们将@NotEmpty注解与@Size 结合使用，我们可以更加严格。

这样做时，我们还会强制对象的最小和最大大小值在指定的最小/最大范围内：

```java
@NotEmpty(message = "Name may not be empty")
@Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters long") 
private String name;

```

## 5. @NotBlank约束

同样，我们可以用@NotBlank注解来约束一个类字段：

```java
public class UserNotBlank {

    @NotBlank(message = "Name may not be blank")
    private String name;
    
    // standard constructors / getters / toString

}
```

沿着同样的思路，我们可以实现一个单元测试来理解@NotBlank约束是如何工作的：

```java
@Test
public void whenNotBlankName_thenNoConstraintViolations() {
    UserNotBlank user = new UserNotBlank("John");
    Set<ConstraintViolation<UserNotBlank>> violations = validator.validate(user);
 
    assertThat(violations.size()).isEqualTo(0);
}
    
@Test
public void whenBlankName_thenOneConstraintViolation() {
    UserNotBlank user = new UserNotBlank(" ");
    Set<ConstraintViolation<UserNotBlank>> violations = validator.validate(user);
 
    assertThat(violations.size()).isEqualTo(1);
}
    
@Test
public void whenEmptyName_thenOneConstraintViolation() {
    UserNotBlank user = new UserNotBlank("");
    Set<ConstraintViolation<UserNotBlank>> violations = validator.validate(user);
 
    assertThat(violations.size()).isEqualTo(1);
}
    
@Test
public void whenNullName_thenOneConstraintViolation() {
    UserNotBlank user = new UserNotBlank(null);
    Set<ConstraintViolation<UserNotBlank>> violations = validator.validate(user);
 
    assertThat(violations.size()).isEqualTo(1);
}

```

@NotBlank注解使用NotBlankValidator[类](https://docs.jboss.org/hibernate/validator/6.0/api/org/hibernate/validator/internal/constraintvalidators/hv/NotBlankValidator.html)，它检查字符序列的修剪长度是否不为空：

```java
public boolean isValid(
  CharSequence charSequence, 
  ConstraintValidatorContext constraintValidatorContext)
    if (charSequence == null ) {
        return false; 
    } 
    return charSequence.toString().trim().length() > 0;
}

```

对于空值，该方法返回 false。

简单来说，用@NotBlank约束的String字段必须不为null，并且裁剪后的长度必须大于零。

## 6.并排比较

到目前为止，我们已经深入了解了@ NotNull、 @ NotEmpty和@NotBlank约束如何在类字段上单独运行。

让我们进行快速并排比较，以便我们可以鸟瞰约束的功能并轻松发现它们的差异：

-   @NotNull：受约束的CharSequence、Collection、Map或Array只要不为 null 就有效，但它可以为空。
-   @NotEmpty：受约束的CharSequence、Collection、Map或Array是有效的，只要它不为空，并且其大小/长度大于零。
-   @NotBlank：受约束的字符串只要不为空且修剪后的长度大于零就有效。

## 七. 总结

在本文中，我们研究了 Bean Validation 中实现的@NotNull、@NotEmpty和@NotBlank约束，并强调了它们的相同点和不同点。