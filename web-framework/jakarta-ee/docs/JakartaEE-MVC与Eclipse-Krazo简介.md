## 1. 简介

模型视图控制器 (MVC) 是构建 Web 应用程序的流行设计模式。多年来，它一直是构建现代基于 Web 的应用程序的实际设计原则。

在本教程中，我们将学习如何使用 Jakarta EE MVC 2.0 构建一个带有网页和 REST API 的 Web 应用程序。

## 2.JSR-371

Jakarta MVC 2.0(以前称为[JSR 371](https://jcp.org/en/jsr/detail?id=371) MVC 1.0)是一个基于动作的 Web 框架，它建立在[Jakarta RESTful Web 服务](https://jakarta.ee/specifications/restful-ws/)或 JAX-RS(以前称为[RESTful Web 服务的JavaAPI](https://www.baeldung.com/jax-rs-spec-and-implementations))之上。JSR-371 通过附加注解补充了 JAX-RS，使构建 Web 应用程序更加方便。

JSR 371 或 Jakarta MVC 标准化了我们使用Java开发 Web 应用程序的方式。此外，主要目标是利用现有的[CDI(上下文和依赖注入)](https://jcp.org/en/jsr/detail?id=346)和[Bean 验证](https://jcp.org/en/jsr/detail?id=349)并支持[JSP](https://www.baeldung.com/jsp)和[Facelets](https://javaee.github.io/tutorial/jsf-facelets.html)作为视图技术。

目前，Jakarta MVC 2.1 规范工作正在进行中，可能会与 Jakarta EE 10 版本一起发布。

## 3. JSR-371注解

除了 JAX-RS 注解之外，JSR-371 还定义了一些注解。所有这些注解都是jakarta.mvc.包的一部分。


### 3.1. jakarta.mvc.控制器

@Controller注解将资源标记为 MVC 控制器。当用于一个类时，该类中的所有资源方法都成为控制器。类似地，在资源方法上使用此注解会使该方法成为控制器。通常，如果我们想在同一个类中定义 MVC 控制器和 REST API，那么在方法上定义@Controller会很有帮助。

例如，让我们定义一个控制器：

```java
@Path("user")
public class UserController {
    @GET
    @Produces("text/html")
    @Controller
    public String showUserForm(){
        return "user.jsp";
    }
    @GET
    @Produces("application/json")    
    public String getUserDetails(){
        return getUserDetails();
    }
}
```

此类有一个呈现用户表单的@Controller ( showUserForm ) 和一个返回用户详细信息 JSON ( getUserDetails ) 的 REST API。

### 3.2. jakarta.mvc.视图

和@Controller一样，我们可以用@View注解来标记资源类或者资源方法。通常，返回void 的资源方法应该有一个@View。带有@View 的类表示该类中具有void类型的控制器的默认视图。

例如，让我们用@View定义一个控制器：

```java
@Controller
@Path("user")
@View("defaultModal.jsp")
public class UserController {
    @GET
    @Path("void")
    @View("userForm.jsp")
    @Produces("text/html")
    public void showForm() {
        getInitFormData();
    }

    @GET
    @Path("string")
    @Produces("text/html")
    public void showModal() {
        getModalData();
    }
}
```

这里，资源类和资源方法都有@View注解。控制器showForm呈现视图userForm.jsp。同样，showModal控制器呈现在资源类上定义的defaultModal.jsp 。

### 3.3. 雅加达.mvc.binding.MvcBinding

Jakarta RESTful Web 服务拒绝具有绑定和验证错误的请求。类似的设置可能不适用于与网页交互的用户。幸运的是，即使发生绑定和验证错误，Jakarta MVC 也会调用控制器。通常，用户应该充分了解数据绑定错误。

控制器注入[BindingResult](https://jakarta.ee/specifications/mvc/2.0/apidocs/jakarta/mvc/binding/bindingresult)以向用户呈现人类可读的验证和绑定错误消息。例如，让我们用@MvcBinding定义一个控制器：

```java
@Controller
@Path("user")
public class UserController {
    @MvcBinding
    @FormParam("age")
    @Min(18)
    private int age;
    @Inject
    private BindingResult bindingResult;
    @Inject
    private Models models;
    @POST
    public String processForm() {
        if (bindingResult.isFailed()) {
            models.put("errors", bindingResult.getAllMessages());
            return "user.jsp";
        }
    }
}
```

在这里，如果用户输入的年龄小于 18 岁，用户将被送回同一页面并出现绑定错误。使用表达式语言 (EL)的user.jsp页面可以检索请求属性错误并将它们显示在页面上。

### 3.4. jakarta.mvc.RedirectScoped

考虑一个用户填写并提交数据的表单 (HTTP POST)。服务器处理数据并将用户重定向到成功页面 (HTTP GET)。这种模式被广泛称为[PRG (Post-Redirect-Get) 模式](https://en.wikipedia.org/wiki/Post/Redirect/Get)。在一些场景中，我们喜欢在 POST 和 GET 之间保存数据。在这些场景中，模型/bean 的范围超出了单个请求。

当用@RedirectScoped 注解一个bean 时，该bean 的状态超出了单个请求。然而，状态在 POST、重定向和 Get 完成后被销毁。用@RedirectScoped划分的 bean在 POST、Redirect 和 GET 完成后被销毁。

例如，假设 bean User有注解@RedirectScoped：

```java
@RedirectScoped
public class User
{
    private String id;
    private String name;
    // getters and setters
}
```

接下来，将这个 bean 注入到控制器中：

```java
@Controller
@Path("user")
public class UserController {
    @Inject
    private User user;
    @POST
    public String post() {
        user.setName("John Doe");
        return "redirect:/submit";
    }
    @GET
    public String get() {
        return "success.jsp";
    }
}
```

在这里，用户bean可用于 POST 和后续的重定向和 GET。因此，success.jsp可以使用 EL访问 bean 的名称属性。

### 3.5. 雅加达.mvc.UriRef

我们只能对资源方法使用@UriRef注解。@UriRef使我们能够为资源方法提供名称。我们可以使用这些名称来调用视图中的控制器，而不是控制器路径 URI。

假设有一个带有href的用户表单：

```xml
<a href="/app/user">Clich Here</a>
```

单击Click Here调用映射到 GET /app/user的控制器。

```java
@GET
@UriRef("user-details")
public String getUserDetails(String userId) {
    userService.getUserDetails(userId);
} 

```

在这里，我们将我们的控制器命名为user-details。现在，我们可以在我们的视图中引用这个名称而不是 URI：

```xml
<a href="${mvc.uri('user-details')}">Click Here</a>
```

### 3.6. jakarta.mvc.security.CsrfProtected

此注解要求 CSRF 验证对于调用资源方法是必要的。如果 CSRF 令牌无效，客户端将收到ForbiddenException (HTTP 403) 异常。只有资源方法可以有这个注解。

考虑一个控制器：

```java
@POST
@Path("user")
@CsrfProtected
public String saveUser(User user) {
    service.saveUser(user);
}
```

鉴于控制器具有@CsrfProtected注解，只有当请求包含有效的 CSRF 令牌时，请求才会到达控制器。

## 4. 构建 MVC 应用程序

接下来，让我们使用 REST API 和控制器构建一个 Web 应用程序。最后，让我们在最新版本的[Eclipse Glassfish](https://projects.eclipse.org/projects/ee4j.glassfish)中部署我们的 Web 应用程序。

### 4.1. 生成项目

首先，让我们使用 Maven archetype:generate生成 Jakarta MVC 2.0 项目：

```bash
mvn archetype:generate 
  -DarchetypeGroupId=org.eclipse.krazo
  -DarchetypeArtifactId=krazo-jakartaee9-archetype
  -DarchetypeVersion=2.0.0 -DgroupId=com.baeldung
  -DartifactId=krazo -DkrazoImpl=jersey
```

上面的原型生成一个带有所需工件的 Maven 项目，类似于：

[![文件夹结构](https://www.baeldung.com/wp-content/uploads/2022/03/1.png)](https://www.baeldung.com/wp-content/uploads/2022/03/1.png)

此外，生成的pom.xml包含[jakarta.platform](https://search.maven.org/search?q=jakarta.jakartaee-web-api)、[jakarta.mvc](https://search.maven.org/search?q=jakarta.mvc)和[org.eclipse.krazo](https://search.maven.org/search?q=a:krazo-jersey)依赖项：

```xml
<dependency>
    <groupId>jakarta.platform</groupId>
    <artifactId>jakarta.jakartaee-web-api</artifactId>
    <version>9.1.0</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>jakarta.mvc</groupId>
    <artifactId>jakarta.mvc-api</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>org.eclipse.krazo</groupId>
    <artifactId>krazo-jersey</artifactId>
    <version>2.0.0</version>
</dependency>
```

### 4.2. 控制器

接下来，让我们定义用于显示表单、保存用户详细信息的控制器，以及用于获取用户详细信息的 API。但是，首先，让我们定义我们的应用程序路径：

```java
@ApplicationPath("/app")
public class UserApplication extends Application {
}
```

应用程序路径定义为/app。接下来，让我们定义将用户转发到用户详细信息表单的控制器：

```java
@Path("users")
public class UserController {
    @GET
    @Controller
    public String showForm() {
        return "user.jsp";
    }
}
```

接下来，在WEB-INF/views 下，我们可以创建一个视图user.jsp，并构建和部署应用程序：

```xml
mvn clean install glassfish:deploy
```

这个[Glassfish Maven](https://search.maven.org/search?q=g:org.glassfish.maven.plugin a:maven-glassfish-plugin)插件在端口 8080 上构建、部署和运行。部署成功后，我们可以打开浏览器并点击 URL：

http://localhost:8080/mvc-2.0/app/users：


[![表单数据](https://www.baeldung.com/wp-content/uploads/2022/03/form-1-1024x304.png)](https://www.baeldung.com/wp-content/uploads/2022/03/form-1.png)

接下来，让我们定义一个处理表单提交操作的 HTTP POST：

```java
@POST
@Controller
public String saveUser(@Valid @BeanParam User user) {   
    return "redirect:users/success";
}
```

现在，当用户点击创建按钮时，控制器处理 POST 请求并将用户重定向到成功页面：

[![成功](https://www.baeldung.com/wp-content/uploads/2022/03/success-1024x133.png)](https://www.baeldung.com/wp-content/uploads/2022/03/success.png)

让我们使用 Jakarta Validations、CDI 和@MvcBinding来提供表单验证：

```java
@Named("user")
public class User implements Serializable {

    @MvcBinding
    @Null
    private String id;

    @MvcBinding
    @NotNull
    @Size(min = 1, message = "Name cannot be blank")
    @FormParam("name")
    private String name;
    // other validations with getters and setters 
}
```

完成表单验证后，让我们检查绑定错误。如果有任何绑定错误，我们必须向用户提供验证消息。为此，让我们注入BindingResult来处理无效的表单参数。让我们更新我们的saveUser方法：

```java
@Inject
private BindingResult bindingResult;

public String saveUser(@Valid @BeanParam User user) {
    if (bindingResult.isFailed()) {
        models.put("errors", bindingResult.getAllErrors());
        return "user.jsp";
    }  
    return "redirect:users/success";
}
```

验证到位后，如果用户提交没有强制参数的表单，我们将显示验证错误：

[![验证](https://www.baeldung.com/wp-content/uploads/2022/03/validations-1024x402.png)](https://www.baeldung.com/wp-content/uploads/2022/03/validations.png)

接下来，让我们使用@CsrfProtected来保护我们的 POST 方法免受 CSRF 攻击。将@CsrfProtected添加到方法saveUser：

```java
@POST
@Controller
@CsrfProtected
public String saveUser(@Valid @BeanParam User user) {
}
```

接下来，让我们尝试点击创建按钮：

[![403](https://www.baeldung.com/wp-content/uploads/2022/03/403-1024x154.png)](https://www.baeldung.com/wp-content/uploads/2022/03/403.png)

当控制器免受 CSRF 攻击时，客户端应始终传递 CSRF 令牌。因此，让我们在user.jsp中添加一个隐藏字段，为每个请求添加一个 CSRF 令牌：

```xml
<input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
```

同样，现在让我们开发一个[REST API](https://www.baeldung.com/jax-rs-spec-and-implementations)：

```java
@GET
@Produces(MediaType.APPLICATION_JSON)
public List<User> getUsers() {
    return users;
}
```

此 HTTP GET API 返回用户列表。

## 5.总结

在本文中，我们了解了 Jakarta MVC 2.0 以及如何使用 Eclipse Krazo 开发 Web 应用程序和 REST API。我们已经看到 MVC 2.0 如何标准化我们在Java中构建基于 MVC 的 Web 应用程序的方式。