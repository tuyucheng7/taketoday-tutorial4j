## 1. 概述

在[JPA](https://en.wikipedia.org/wiki/Java_Persistence_API)实体上提供CRUD功能的[DAO]()层的实现可能是一项重复、耗时的任务，我们在大多数情况下都希望避免这种情况。

幸运的是，[Spring Boot]()可以通过一层基于标准JPA的CRUD Repository轻松创建CRUD应用程序。

**在本教程中，我们将学习如何使用Spring Boot和[Thymeleaf]()开发CRUD Web应用程序**。

## 2. Maven依赖

在这种情况下，我们依赖[spring-boot-starter-parent](https://search.maven.org/search?q=a:spring-boot-starter-parent&g:org.springframework.boot=&core=gav)进行简单的依赖管理、版本控制和插件配置。

因此，除了覆盖Java版本之外，我们不需要在pom.xml文件中指定项目依赖项的版本：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.0</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
    </dependency>
</dependencies>

```

## 3. 域层

在已经具备所有项目依赖项后，现在让我们实现一个简单的域层。

为了简单起见，这只包含一个类，负责对用户实体进行建模：

```java
@Entity
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @NotBlank(message = "Name is mandatory")
    private String name;
    
    @NotBlank(message = "Email is mandatory")
    private String email;

    // standard constructors / setters / getters / toString
}
```

请记住，我们已经使用@Entity注解对类进行了标注，**因此，在这种情况下，Hibernate的JPA实现将能够对域实体执行CRUD操作**。有关Hibernate的介绍性指南，请访问我们的[Hibernate 5与Spring]()教程。

此外，我们还使用@NotBlank约束来约束name和email字段，这意味着我们可以在持久化或更新数据库中的实体之前使用Hibernate Validator来验证受约束的字段。

有关这方面的基础知识，请查看我们关于[Bean Validation的相关教程]()。

## 4. 存储层

Spring Data JPA允许我们以最小的麻烦实现基于JPA的Repository(DAO模式实现的奇特名称)。

**[Spring Data JPA]()是Spring Boot的spring-boot-starter-data-jpa的关键组件，它可以通过放置在JPA实现之上的强大抽象层轻松添加CRUD功能。这个抽象层允许我们访问持久层，而不必从头开始提供我们自己的DAO实现**。

要为我们的应用程序提供对用户对象的基本CRUD功能，我们只需要扩展CrudRepository接口：

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {}
```

就是这样！通过扩展CrudRepository接口，Spring Data JPA将为我们提供Repository的CRUD方法的实现。

## 5. 控制器层

**由于spring-boot-starter-data-jpa放置在底层JPA实现之上的抽象层，我们可以通过基本的Web层轻松地向我们的Web应用程序添加一些CRUD功能**。

在我们的例子中，单个控制器类足以处理GET和POST HTTP请求，然后将它们映射到对我们的UserRepository实现的调用。

控制器类依赖于Spring MVC的一些关键特性，有关Spring MVC的详细指南，请查看我们的[Spring MVC教程]()。

让我们从控制器的showSignUpForm()和addUser()方法开始。前者将显示用户注册表单，而后者将在验证受限字段后在数据库中保留一个新实体。

如果实体未通过验证，则将重新显示注册表单。否则，一旦实体被保存，持久实体列表将在相应的视图中更新：

```java
@Controller
public class UserController {

    @GetMapping("/signup")
    public String showSignUpForm(User user) {
        return "add-user";
    }

    @PostMapping("/adduser")
    public String addUser(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-user";
        }

        userRepository.save(user);
        return "redirect:/index";
    }

    // additional CRUD methods
}
```

我们还需要/index URL的映射：

```java
@GetMapping("/index")
public String showUserList(Model model) {
    model.addAttribute("users", userRepository.findAll());
    return "index";
}
```

在UserController中，我们还有showUpdateForm()方法，该方法负责从数据库中获取与提供的id相匹配的User实体。

如果实体存在，它将作为模型属性传递给更新表单视图。因此，可以使用name和email字段的值填充表单：

```java
@GetMapping("/edit/{id}")
public String showUpdateForm(@PathVariable("id") long id, Model model) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    
    model.addAttribute("user", user);
    return "update-user";
}
```

最后，我们在UserController类中有updateUser()和deleteUser()方法，第一个将更新的实体保存在数据库中，而后一个将删除给定的实体。

在任何一种情况下，持久实体列表都将相应地更新：

```java
@PostMapping("/update/{id}")
public String updateUser(@PathVariable("id") long id, @Valid User user, BindingResult result, Model model) {
    if (result.hasErrors()) {
        user.setId(id);
        return "update-user";
    }
        
    userRepository.save(user);
    return "redirect:/index";
}
    
@GetMapping("/delete/{id}")
public String deleteUser(@PathVariable("id") long id, Model model) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    userRepository.delete(user);
    return "redirect:/index";
}
```

## 6. 视图层

至此，我们已经实现了一个功能控制器类，它对用户实体执行CRUD操作。**即便如此，此架构中仍然缺少一个组件：视图层**。

在src/main/resources/templates文件夹下，我们需要创建显示注册表单和更新表单以及呈现持久化用户实体列表所需的HTML模板。

如介绍中所述，我们将使用Thymeleaf作为解析模板文件的底层模板引擎。

以下是add-user.html文件的相关部分：

```html
<form action="#" th:action="@{/adduser}" th:object="${user}" method="post">
    <label for="name">Name</label>
    <input type="text" th:field="{name}" id="name" placeholder="Name">
    <span th:if="${#fields.hasErrors('name')}" th:errors="{name}"></span>
    <label for="email">Email</label>
    <input type="text" th:field="{email}" id="email" placeholder="Email">
    <span th:if="${#fields.hasErrors('email')}" th:errors="{email}"></span>
    <input type="submit" value="Add User">
</form>
```

**请注意我们如何使用@{/adduser} URL表达式来指定表单的action属性和${}变量表达式以在模板中嵌入动态内容，例如name和email字段的值以及验证后错误**。

与add-user.html类似，update-user.html模板如下所示：

```html
<form action="#"
      th:action="@{/update/{id}(id=${user.id})}"
      th:object="${user}"
      method="post">
    <label for="name">Name</label>
    <input type="text" th:field="{name}" id="name" placeholder="Name">
    <span th:if="${#fields.hasErrors('name')}" th:errors="{name}"></span>
    <label for="email">Email</label>
    <input type="text" th:field="{email}" id="email" placeholder="Email">
    <span th:if="${#fields.hasErrors('email')}" th:errors="{email}"></span>
    <input type="submit" value="Update User">
</form>
```

最后，我们有index.html文件，它显示持久实体列表以及用于编辑和删除现有实体的链接：

```html
<div th:switch="${users}">
    <h2 th:case="null">No users yet!</h2>
    <div th:case="">
        <h2>Users</h2>
        <table>
            <thead>
            <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Edit</th>
                <th>Delete</th>
            </tr>
            </thead>
            <tbody>
            <tr th:each="user : ${users}">
                <td th:text="${user.name}"></td>
                <td th:text="${user.email}"></td>
                <td><a th:href="@{/edit/{id}(id=${user.id})}">Edit</a></td>
                <td><a th:href="@{/delete/{id}(id=${user.id})}">Delete</a></td>
            </tr>
            </tbody>
        </table>
    </div>
    <p><a href="/signup">Add a new user</a></p>
</div>
```

为简单起见，模板看起来很简陋，只提供所需的功能，而没有添加不必要的修饰。

为了在不花太多时间在HTML/CSS上的情况下为模板提供改进的、引人注目的外观，我们可以简单地使用免费的[Twitter Bootstrap](https://getbootstrap.com/) UI工具包，例如[Shards](https://designrevision.com/downloads/shards/)。

## 7. 运行应用程序

最后，我们定义应用程序的入口点。

像大多数Spring Boot应用程序一样，我们可以使用普通的旧main()方法来完成此操作：

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

现在，在我们的IDE中点击“Run”，然后打开我们的浏览器并访问http://localhost:8080。

如果构建已成功编译，我们应该会看到一个基本的CRUD用户仪表板，其中包含用于添加新实体以及用于编辑和删除现有实体的链接。

## 8. 总结

在本文中，我们学习了如何使用Spring Boot和Thymeleaf构建一个基本的CRUD Web应用程序。