## 1. 概述

通常，当我们需要验证用户输入时，Spring MVC 会提供标准的预定义验证器。

但是，当我们需要验证更特定类型的输入时，我们可以创建自己的自定义验证逻辑。 

在本教程中，我们将这样做；我们将创建一个自定义验证器来验证带有电话号码字段的表单，然后我们将显示一个用于多个字段的自定义验证器。

本教程重点介绍 Spring MVC。我们题为Spring Boot 中的[验证的](https://www.baeldung.com/spring-boot-bean-validation)文章描述了如何在Spring Boot中创建自定义验证。

## 2.设置

为了从 API 中获益，我们将依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.0.10.Final</version>
</dependency>

```

可以在 [此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.hibernate" AND a%3A"hibernate-validator")检查最新版本的依赖项。

如果我们使用 Spring Boot，那么我们只能添加[spring-boot-starter-web](https://www.baeldung.com/spring-boot-starters)，这也会引入hibernate-validator依赖项。

## 3.自定义验证

创建自定义验证器需要推出我们自己的注解并在我们的模型中使用它来执行验证规则。

所以让我们创建我们的自定义验证器，它检查电话号码。电话号码必须是至少 8 位数字但不超过 11 位数字的号码。

## 4. 新注解

让我们创建一个新的@interface 来定义我们的注解：

```java
@Documented
@Constraint(validatedBy = ContactNumberValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ContactNumberConstraint {
    String message() default "Invalid phone number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

使用@Constraint注解，我们定义了将要验证我们的字段的类。message()是显示在用户界面中的错误消息。最后，附加代码主要是符合 Spring 标准的样板代码。

## 5. 创建验证器

现在让我们创建一个验证器类来执行我们的验证规则：

```java
public class ContactNumberValidator implements 
  ConstraintValidator<ContactNumberConstraint, String> {

    @Override
    public void initialize(ContactNumberConstraint contactNumber) {
    }

    @Override
    public boolean isValid(String contactField,
      ConstraintValidatorContext cxt) {
        return contactField != null && contactField.matches("[0-9]+")
          && (contactField.length() > 8) && (contactField.length() < 14);
    }

}
```

验证类实现了ConstraintValidator接口，还必须实现isValid方法；我们正是在这种方法中定义了验证规则。

自然地，我们在这里使用一个简单的验证规则来展示验证器是如何工作的。

ConstraintValidator定义了验证给定对象的给定约束的逻辑。实施必须遵守以下限制：

-   该对象必须解析为非参数化类型
-   对象的通用参数必须是无限通配符类型

## 6.应用验证注解

在我们的例子中，我们创建了一个带有一个字段的简单类来应用验证规则。在这里，我们正在设置要验证的注解字段：

```java
@ContactNumberConstraint
private String phone;
```

我们定义了一个字符串字段，并使用我们的自定义注解@ContactNumberConstraint 对其进行了注解。在我们的控制器中，我们创建了映射并处理了所有错误：

```java
@Controller
public class ValidatedPhoneController {
 
    @GetMapping("/validatePhone")
    public String loadFormPage(Model m) {
        m.addAttribute("validatedPhone", new ValidatedPhone());
        return "phoneHome";
    }
    
    @PostMapping("/addValidatePhone")
    public String submitForm(@Valid ValidatedPhone validatedPhone,
      BindingResult result, Model m) {
        if(result.hasErrors()) {
            return "phoneHome";
        }
        m.addAttribute("message", "Successfully saved phone: "
          + validatedPhone.toString());
        return "phoneHome";
    }   
}
```

我们定义了这个具有单个JSP页面的简单控制器，并使用submitForm方法强制验证我们的电话号码。

## 7. 景色

我们的视图是一个基本的 JSP 页面，其表单只有一个字段。当用户提交表单时，该字段将由我们的自定义验证器验证并重定向到同一页面，并显示验证成功或失败的消息：

```html
<form:form 
  action="/${pageContext.request.contextPath}/addValidatePhone"
  modelAttribute="validatedPhone">
    <label for="phoneInput">Phone: </label>
    <form:input path="phone" id="phoneInput" />
    <form:errors path="phone" cssClass="error" />
    <input type="submit" value="Submit" />
</form:form>

```

## 8. 测试

现在让我们测试我们的控制器以检查它是否给了我们适当的响应和视图：

```java
@Test
public void givenPhonePageUri_whenMockMvc_thenReturnsPhonePage(){
    this.mockMvc.
      perform(get("/validatePhone")).andExpect(view().name("phoneHome"));
}
```

让我们也测试一下我们的字段是否根据用户输入进行了验证：

```java
@Test
public void 
  givenPhoneURIWithPostAndFormData_whenMockMVC_thenVerifyErrorResponse() {
 
    this.mockMvc.perform(MockMvcRequestBuilders.post("/addValidatePhone").
      accept(MediaType.TEXT_HTML).
      param("phoneInput", "123")).
      andExpect(model().attributeHasFieldErrorCode(
          "validatedPhone","phone","ContactNumberConstraint")).
      andExpect(view().name("phoneHome")).
      andExpect(status().isOk()).
      andDo(print());
}
```

在测试中，我们向用户提供输入“123”，正如我们预期的那样，一切正常，我们在客户端看到了错误。

## 9.自定义类级别验证

也可以在类级别定义自定义验证注解，以验证类的多个属性。

此场景的一个常见用例是验证一个类的两个字段是否具有匹配值。

### 9.1. 创建注解

让我们添加一个名为FieldsValueMatch的新注解，稍后可以将其应用于类。注解将有两个参数，field和fieldMatch，表示要比较的字段的名称：

```java
@Constraint(validatedBy = FieldsValueMatchValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldsValueMatch {

    String message() default "Fields values don't match!";

    String field();

    String fieldMatch();

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        FieldsValueMatch[] value();
    }
}
```

我们可以看到我们的自定义注解还包含一个List子接口，用于在一个类上定义多个FieldsValueMatch注解。

### 9.2. 创建验证器

接下来我们需要添加包含实际验证逻辑的FieldsValueMatchValidator类：

```java
public class FieldsValueMatchValidator 
  implements ConstraintValidator<FieldsValueMatch, Object> {

    private String field;
    private String fieldMatch;

    public void initialize(FieldsValueMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    public boolean isValid(Object value, 
      ConstraintValidatorContext context) {

        Object fieldValue = new BeanWrapperImpl(value)
          .getPropertyValue(field);
        Object fieldMatchValue = new BeanWrapperImpl(value)
          .getPropertyValue(fieldMatch);
        
        if (fieldValue != null) {
            return fieldValue.equals(fieldMatchValue);
        } else {
            return fieldMatchValue == null;
        }
    }
}
```

isValid()方法检索两个字段的值并检查它们是否相等。

### 9.3. 应用注解

让我们创建一个NewUserForm模型类，用于用户注册所需的数据。它将有两个电子邮件和密码属性，以及两个verifyEmail和verifyPassword属性以重新输入这两个值。

由于我们有两个字段要检查其相应的匹配字段，让我们在NewUserForm类上添加两个@FieldsValueMatch注解，一个用于电子邮件值，一个用于密码值：

```java
@FieldsValueMatch.List({ 
    @FieldsValueMatch(
      field = "password", 
      fieldMatch = "verifyPassword", 
      message = "Passwords do not match!"
    ), 
    @FieldsValueMatch(
      field = "email", 
      fieldMatch = "verifyEmail", 
      message = "Email addresses do not match!"
    )
})
public class NewUserForm {
    private String email;
    private String verifyEmail;
    private String password;
    private String verifyPassword;

    // standard constructor, getters, setters
}
```

为了在Spring MVC中验证模型，让我们创建一个带有/user POST 映射的控制器，它接收一个用@Valid注解的NewUserForm对象并验证是否存在任何验证错误：

```java
@Controller
public class NewUserController {

    @GetMapping("/user")
    public String loadFormPage(Model model) {
        model.addAttribute("newUserForm", new NewUserForm());
        return "userHome";
    }

    @PostMapping("/user")
    public String submitForm(@Valid NewUserForm newUserForm, 
      BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "userHome";
        }
        model.addAttribute("message", "Valid form");
        return "userHome";
    }
}
```

### 9.4. 测试注解

为了验证我们的自定义类级注解，让我们编写一个JUnit测试，将匹配信息发送到/user端点，然后验证响应不包含错误：

```java
public class ClassValidationMvcTest {
  private MockMvc mockMvc;
    
    @Before
    public void setup(){
        this.mockMvc = MockMvcBuilders
          .standaloneSetup(new NewUserController()).build();
    }
    
    @Test
    public void givenMatchingEmailPassword_whenPostNewUserForm_thenOk() 
      throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
          .post("/user")
          .accept(MediaType.TEXT_HTML).
          .param("email", "john@yahoo.com")
          .param("verifyEmail", "john@yahoo.com")
          .param("password", "pass")
          .param("verifyPassword", "pass"))
          .andExpect(model().errorCount(0))
          .andExpect(status().isOk());
    }
}
```

然后我们还将添加一个JUnit测试，它将不匹配的信息发送到/user端点并断言结果将包含两个错误：

```java
@Test
public void givenNotMatchingEmailPassword_whenPostNewUserForm_thenOk() 
  throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders
      .post("/user")
      .accept(MediaType.TEXT_HTML)
      .param("email", "john@yahoo.com")
      .param("verifyEmail", "john@yahoo.commmm")
      .param("password", "pass")
      .param("verifyPassword", "passsss"))
      .andExpect(model().errorCount(2))
      .andExpect(status().isOk());
    }
```

## 10.总结

在这篇简短的文章中，我们学习了如何创建自定义验证器来验证字段或类，然后将它们连接到Spring MVC中。