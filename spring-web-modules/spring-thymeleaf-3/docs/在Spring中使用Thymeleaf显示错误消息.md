## 1. 概述

在本教程中，我们将了解如何在[Thymeleaf](https://www.baeldung.com/spring-boot-crud-thymeleaf)模板中显示源自基于Spring的后端应用程序的错误消息。

出于演示目的，我们将创建一个简单的Spring Boot用户注册应用程序并验证各个输入字段。此外，我们将看到一个如何处理全局级别错误的示例。

首先，我们将快速设置后端应用程序，然后进入UI部分。

## 2. 示例Spring Boot应用程序

要为用户注册创建一个简单的Spring Boot应用程序，我们需要一个控制器、一个Repository和一个实体。

然而，即使在那之前，我们也应该添加Maven依赖项。

### 2.1 Maven依赖

让我们添加我们需要的所有Spring Boot启动器，用于MVC的[Web](https://search.maven.org/search?q=a:spring-boot-starter-web)、用于Hibernate实体验证的[hibernate-validator](https://search.maven.org/search?q=a:hibernate-validator)、用于UI的[Thymeleaf](https://search.maven.org/search?q=a:spring-boot-starter-thymeleaf)和用于Repository的[JPA](https://search.maven.org/search?q=a:spring-boot-starter-thymeleaf)。此外，我们需要一个[H2](https://search.maven.org/artifact/com.h2database/h2)依赖项来拥有一个内存数据库：

```xml
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-web</artifactId> 
    <version>2.7.2</version> 
</dependency> 
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-validation</artifactId> 
    <version>2.7.2</version> 
</dependency> 
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-thymeleaf</artifactId> 
    <version>2.7.2</version> 
</dependency> 
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-data-jpa</artifactId> 
    <version>2.7.2</version> 
</dependency> 
<dependency> 
    <groupId>com.h2database</groupId> 
    <artifactId>h2</artifactId> 
    <scope>runtime</scope> 
    <version>1.4.200</version> 
</dependency>
```

### 2.2 实体

这是我们的用户实体：

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "User's name cannot be empty.")
    @Size(min = 5, max = 250)
    private String fullName;

    @NotEmpty(message = "User's email cannot be empty.")
    private String email;

    @NotNull(message = "User's age cannot be null.")
    @Min(value = 18)
    private Integer age;

    private String country;

    private String phoneNumber;

    // getters and setters
}
```

如我们所见，我们为用户输入添加了一些[验证约束](https://www.baeldung.com/spring-boot-bean-validation)。例如，字段不应为null或空并且具有特定的大小或值。

值得注意的是，我们没有对country或phoneNumber字段添加任何限制。那是因为我们将使用它们作为生成全局错误或未绑定到特定字段的错误的示例。

### 2.3 Repository

我们将为我们的基本用例使用一个简单的[JPA Repository](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)：

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {}
```

### 2.4 控制器

最后，为了在后端将所有内容连接在一起，让我们将一个UserController放在一起：

```java
@Controller
public class UserController {

    @Autowired
    private UserRepository repository;
    @GetMapping("/add")
    public String showAddUserForm(User user) {
        return "errors/addUser";
    }

    @PostMapping("/add")
    public String addUser(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "errors/addUser";
        }
        repository.save(user);
        model.addAttribute("users", repository.findAll());
        return "errors/home";
    }
}
```

这里我们在路径/add定义了一个[GetMapping](https://www.baeldung.com/spring-new-requestmapping-shortcuts)来显示注册表单。我们在同一路径的PostMapping处理提交表单时的验证，如果一切顺利，随后将保存到Repository。

## 3. 带有错误信息的Thymeleaf模板

现在已经介绍了基础知识，我们已经到了问题的关键，即创建UI模板并显示错误消息(如果有)。

让我们根据可以显示的错误类型逐步构建模板。

### 3.1 显示字段错误

Thymeleaf 提供了一个内置的field.hasErrors方法，它根据给定字段是否存在任何错误返回一个布尔值。将它与[th:if](https://www.baeldung.com/spring-mvc-thymeleaf-conditional-css-classes#using-thif)结合起来，我们可以选择显示错误(如果存在)：

```html
<p th:if="${#fields.hasErrors('age')}">Invalid Age</p>
```

接下来，如果我们想添加任何样式，我们可以有条件地使用[th:class](https://www.baeldung.com/spring-mvc-thymeleaf-conditional-css-classes#using-thclass)：

```html
<p  th:if="${#fields.hasErrors('age')}" th:class="${#fields.hasErrors('age')}? error">
  Invalid Age</p>
```

我们简单的嵌入式CSS类错误会将元素变为红色：

```html
<style>
    .error {
        color: red;
    }
</style>
```

Thymeleaf的另一个属性th:errors使我们能够显示指定选择器上的所有错误，比如电子邮件：

```html
<div>
    <label for="email">Email</label> <input type="text" th:field="*{email}" />
    <p th:if="${#fields.hasErrors('email')}" th:errorclass="error" th:errors="*{email}" />
</div>
```

在上面的代码片段中，我们还可以看到使用CSS样式的变化。这里我们使用th:errorclass，它消除了我们使用任何条件属性来应用CSS的需要。

或者，我们可以选择使用[th:each](https://www.baeldung.com/thymeleaf-iteration)遍历给定字段上的所有验证消息：

```html
<div>
    <label for="fullName">Name</label> <input type="text" th:field="*{fullName}"
                                              id="fullName" placeholder="Full Name">
    <ul>
        <li th:each="err : ${#fields.errors('fullName')}" th:text="${err}" class="error" />
    </ul>
</div>
```

值得注意的是，我们在这里使用了另一个Thymeleaf方法fields.errors()来收集我们的后端应用程序为fullName字段返回的所有验证消息。

现在，为了对此进行测试，让我们启动我们的Boot应用程序并访问端点[http://localhost:8080/add](http://localhost:8080/add)。

这是我们根本不提供任何输入时页面的样子：

<img src="../assets/img_1.png">

### 3.2 一次显示所有错误

接下来，让我们看看如何在一个地方全部显示，而不是一一显示每条错误消息。

为此，我们将使用Thymeleaf的fields.hasAnyErrors()方法：

```html
<div th:if="${#fields.hasAnyErrors()}">
    <ul>
        <li th:each="err : ${#fields.allErrors()}" th:text="${err}" />
    </ul>
</div>
```

如我们所见，我们在这里使用了另一个变体fields.allErrors()来遍历HTML表单上所有字段的所有错误。

我们可以使用#fields.hasErrors('\*')而不是fields.hasAnyErrors()。同样，#fields.errors('\*')是上面使用的#fields.allErrors()的替代方法。

这是效果：

<img src="../assets/img_2.png">

### 3.3 在表单外显示错误

下一个。让我们考虑一个场景，其中我们想要在HTML表单之外显示验证消息。

在这种情况下，不使用选择或(*{...})，我们只需要使用格式为(${...})的完全限定变量名：

```html
<h4>Errors on a single field:</h4>
<div th:if="${#fields.hasErrors('${user.email}')}"
     th:errors="*{user.email}"></div>
<ul>
    <li th:each="err : ${#fields.errors('user.*')}" th:text="${err}" />
</ul>
```

这将在电子邮件字段中显示所有错误消息。

现在，让我们看看如何一次显示所有消息：

```html
<h4>All errors:</h4>
<ul>
    <li th:each="err : ${#fields.errors('user.*')}" th:text="${err}" />
</ul>
```

这是我们在页面上看到的：

<img src="../assets/img_3.png">

### 3.4 显示全局错误

在现实生活中，可能会出现与特定字段无关的错误。我们可能有一个用例，我们需要考虑多个输入以验证业务条件。这些称为全局错误。

让我们考虑一个简单的例子来证明这一点。对于我们的country和phoneNumber字段，我们可能会添加一个检查，以确保对于给定的国家，电话号码应以特定的前缀开头。

我们需要对后端进行一些更改以添加此验证。

首先，我们将添加一个[Service](https://www.baeldung.com/spring-component-repository-service)来执行此验证：

```java
@Service
public class UserValidationService {
    public String validateUser(User user) {
        String message = "";
        if (user.getCountry() != null && user.getPhoneNumber() != null) {
            if (user.getCountry().equalsIgnoreCase("India")
                  && !user.getPhoneNumber().startsWith("91")) {
                message = "Phone number is invalid for " + user.getCountry();
            }
        }
        return message;
    }
}
```

如我们所见，我们添加了一个简单的案例。对于印度国家，电话号码应以前缀91开头。

其次，我们需要调整控制器的PostMapping：

```java
@PostMapping("/add")
public String addUser(@Valid User user, BindingResult result, Model model) {
    String err = validationService.validateUser(user);
    if (!err.isEmpty()) {
        ObjectError error = new ObjectError("globalError", err);
        result.addError(error);
    }
    if (result.hasErrors()) {
        return "errors/addUser";
    }
    repository.save(user);
    model.addAttribute("users", repository.findAll());
    return "errors/home";
}
```

最后，在Thymeleaf模板中，我们将添加全局常量来显示此类错误：

```xml
<div th:if="${#fields.hasErrors('global')}">
    <h3>Global errors:</h3>
    <p th:each="err : ${#fields.errors('global')}" th:text="${err}" class="error" />
</div>
```

或者，我们可以使用方法#fields.hasGlobalErrors()和#fields.globalErrors()来代替常量来实现相同的目的。

这是我们在输入无效输入时看到的：

<img src="../assets/img_4.png">

## 4. 总结

在本教程中，我们构建了一个简单的Spring Boot应用程序来演示如何在Thymeleaf中显示各种类型的错误。

我们查看了一个一个地显示字段错误，然后一次全部显示，HTML表单之外的错误和全局错误。