## 1. 概述

在本教程中，我们将使用Spring Security实现一个基本的注册过程。我们将在[上一篇文章](https://www.baeldung.com/spring-security-login-error-handling-localization)中探讨的概念的基础上进行构建，在其中我们研究了登录。

这里的目标是添加一个**完整的注册过程**，允许用户注册，以及验证和保存用户数据。

## 延伸阅读

### [Servlet 3异步支持与Spring MVC和Spring Security](https://www.baeldung.com/spring-mvc-async-security)

快速介绍Spring Security对Spring MVC中异步请求的支持。

[阅读更多](https://www.baeldung.com/spring-mvc-async-security)→

### [Thymeleaf的Spring Security](https://www.baeldung.com/spring-security-thymeleaf)

集成Spring Security和Thymeleaf的快速指南

[阅读更多](https://www.baeldung.com/spring-security-thymeleaf)→

### [Spring Security-缓存控制头](https://www.baeldung.com/spring-security-cache-control-headers)

使用Spring Security控制HTTP缓存控制标头的指南。

[阅读更多](https://www.baeldung.com/spring-security-cache-control-headers)→

## 2. 注册页面

首先，我们将实现一个简单的注册页面，显示以下字段：

-   name(名字和姓氏)
-   email
-   password(和密码确认字段)

以下示例显示了一个简单的registration.html页面：

```html
<html>
<body>
<h1 th:text="#{label.form.title}">form</h1>
<form action="/" th:object="${user}" method="POST" enctype="utf8">
    <div>
        <label th:text="#{label.user.firstName}">first</label>
        <input th:field="*{firstName}"/>
        <p th:each="error: ${#fields.errors('firstName')}"
           th:text="${error}">Validation error</p>
    </div>
    <div>
        <label th:text="#{label.user.lastName}">last</label>
        <input th:field="*{lastName}"/>
        <p th:each="error : ${#fields.errors('lastName')}"
           th:text="${error}">Validation error</p>
    </div>
    <div>
        <label th:text="#{label.user.email}">email</label>
        <input type="email" th:field="*{email}"/>
        <p th:each="error : ${#fields.errors('email')}"
           th:text="${error}">Validation error</p>
    </div>
    <div>
        <label th:text="#{label.user.password}">password</label>
        <input type="password" th:field="*{password}"/>
        <p th:each="error : ${#fields.errors('password')}"
           th:text="${error}">Validation error</p>
    </div>
    <div>
        <label th:text="#{label.user.confirmPass}">confirm</label>
        <input type="password" th:field="*{matchingPassword}"/>
    </div>
    <button type="submit" th:text="#{label.form.submit}">submit</button>
</form>
<a th:href="@{/login.html}" th:text="#{label.form.loginLink}">login</a>
</body>
</html>
```

## 3. UserDto对象

我们需要一个**数据传输对象**来将所有注册信息发送到我们的Spring后端。**DTO**对象应该包含我们稍后在创建和填充User对象时需要的所有信息：

```java
public class UserDto {
    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;

    @NotNull
    @NotEmpty
    private String email;

    // standard getters and setters
}
```

请注意，我们在DTO对象的字段上使用了标准的javax.validation注解。稍后，我们还将**实现自己的自定义验证注解**来验证电子邮件地址的格式以及密码确认(请参阅**第5节**)。

## 4. 注册控制器

登录页面上的注册链接会将用户带到注册页面。该页面的后端位于注册控制器中，并映射到“/user/registration”：

```java
@GetMapping("/user/registration")
public String showRegistrationForm(WebRequest request, Model model) {
    UserDto userDto = new UserDto();
    model.addAttribute("user", userDto);
    return "registration";
}
```

当控制器收到请求“/user/registration”时，它会创建新的UserDto对象，该对象支持注册表单、绑定它并返回。

## 5. 验证注册数据

接下来，我们将查看控制器在注册新帐户时将执行的验证：

1.  填写所有必填字段(不能为empty或null)。
2.  电子邮件地址有效(格式正确)。
3.  密码确认字段与密码字段匹配。
4.  该帐户尚不存在。

### 5.1 内置验证

对于简单的检查，我们将在DTO对象上使用开箱即用的Bean验证注解。这些是@NotNull、@NotEmpty等注解。

然后，为了触发验证过程，我们将简单地使用@Valid注解对控制器层中的对象进行标注：

```java
public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDto userDto, HttpServletRequest request, Errors errors) {
    // ...
}
```

### 5.2 检查电子邮件有效性的自定义验证

然后我们将验证电子邮件地址并确保其格式正确。为此，我们将构建一个**自定义验证器**，以及一个**自定义验证注解**；我们称之为@ValidEmail。

重要的是要注意我们正在滚动我们自己的自定义注解而不是Hibernate的@Email，因为Hibernate认为旧的内联网地址格式myaddress@myserver是有效的(请参阅[Stackoverflow](https://stackoverflow.com/questions/4459474/hibernate-validator-email-accepts-askstackoverflow-as-valid)文章)，这并不好。

所以这是电子邮件验证注解和自定义验证器：

```java
@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {
    String message() default "Invalid email";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

请注意，我们在FIELD级别定义了注解，因为这是它在概念上适用的地方。

```java
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    private Pattern pattern;
    private Matcher matcher;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+
        (.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*
          (.[A-Za-z]{2,})$"; 
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context){
        return (validateEmail(email));
    }
    private boolean validateEmail(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
```

然后我们将在我们的UserDto实现上使用新注解：

```java
@ValidEmail
@NotNull
@NotEmpty
private String email;
```

### 5.3 使用自定义验证进行密码确认

我们还需要一个自定义注解和验证器来确保password和matchingPassword字段匹配：

```java
@Target({TYPE,ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {
    String message() default "Passwords don't match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

请注意，@Target注解表示这是一个TYPE级别的注解。这是因为我们需要整个UserDto对象来执行验证。

此注解将调用的自定义验证器如下所示：

```java
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        UserDto user = (UserDto) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }
}
```

然后@PasswordMatches注解应该应用于我们的UserDto对象：

```java
@PasswordMatches
public class UserDto {
    // ...
}
```

当然，在整个验证过程运行时，所有自定义验证都会与所有标准注解一起进行评估。

### 5.4 检查帐户是否不存在

我们将实现的第四个检查是验证数据库中是否尚不存在电子邮件帐户。

这是在验证表单之后执行的，并且是在UserService实现的帮助下完成的。

```java
@PostMapping("/user/registration")
public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDto userDto, HttpServletRequest request, Errors errors) {
    try {
        User registered = userService.registerNewUserAccount(userDto);
    } catch (UserAlreadyExistException uaeEx) {
        mav.addObject("message", "An account for that username/email already exists.");
        return mav;
    }

    // rest of the implementation
}
```

```java
@Service
@Transactional
public class UserService implements IUserService {
    @Autowired
    private UserRepository repository;

    @Override
    public User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException {
        if (emailExists(userDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + userDto.getEmail());
        }

        // the rest of the registration operation
    }
    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }
}
```

UserService依赖于UserRepository类来检查具有给定电子邮件地址的用户是否已存在于数据库中。

持久层中UserRepository的实际实现与当前文章无关；一种快速的方法是[使用Spring Data生成Repository层](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)。

## 6. 持久化数据和完成表单处理

接下来，我们将在我们的控制器层中实现注册逻辑：

```java
@PostMapping("/user/registration")
public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDto userDto, HttpServletRequest request, Errors errors) {
    try {
        User registered = userService.registerNewUserAccount(userDto);
    } catch (UserAlreadyExistException uaeEx) {
        mav.addObject("message", "An account for that username/email already exists.");
        return mav;
    }

    return new ModelAndView("successRegister", "user", userDto);
}
```

上面代码需要注意的地方：

1.  控制器返回一个ModelAndView对象，该对象是发送绑定到视图的模型数据(用户)的便捷类。
2.  如果在验证时设置了任何错误，控制器将重定向到注册表单。

## 7. UserService-注册操作

最后，我们将在UserService中完成注册操作的实现：

```java
public interface IUserService {
    User registerNewUserAccount(UserDto userDto);
}
```

```java
@Service
@Transactional
public class UserService implements IUserService {
    @Autowired
    private UserRepository repository;

    @Override
    public User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException {
        if (emailExists(userDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + userDto.getEmail());
        }

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setRoles(Arrays.asList("ROLE_USER"));

        return repository.save(user);
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }
}
```

## 8. 加载安全登录的用户详细信息

在我们[之前的文章](https://www.baeldung.com/spring-security-login)中，登录使用了硬编码凭据。我们将**使用新注册的用户信息**和凭据来更改它。此外，我们将实现一个自定义的UserDetailsService来从持久层检查凭据用于登录。

### 8.1 自定义UserDetailsService

我们将从自定义UserDetailsService实现开始：

```java
@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with username: " + email);
        }
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new org.springframework.security.core.userdetails.User(
              user.getEmail(), user.getPassword().toLowerCase(), enabled, accountNonExpired,
              credentialsNonExpired, accountNonLocked, getAuthorities(user.getRoles()));
    }

    private static List<GrantedAuthority> getAuthorities (List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
```

### 8.2 启用新的身份验证提供程序

要在Spring Security配置中启用新的UserDetailsService，我们只需要在authentication-manager元素中添加对UserDetailsService的引用，并添加UserDetailsService bean：

```xml
<authentication-manager>
    <authentication-provider user-service-ref="userDetailsService" />
</authentication-manager>
 
<beans:bean id="userDetailsService" class="cn.tuyucheng.taketoday.security.MyUserDetailsService" />
```

另一种选择是通过Java配置：

```java
@Autowired
private MyUserDetailsService userDetailsService;

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService);
}
```

## 9. 总结

在本文中，我们演示了使用Spring Security和Spring MVC实现的完整且几乎可用于生产的注册过程。接下来，我们讨论了通过验证新用户的电子邮件来激活新注册帐户的过程。