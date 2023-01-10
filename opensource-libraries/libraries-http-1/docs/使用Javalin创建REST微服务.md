## 1. 简介

[Javalin](https://javalin.io/)是为Java和 Kotlin 编写的轻量级 Web 框架。它是在 Jetty 网络服务器之上编写的，这使得它具有很高的性能。Javalin 是根据[koa.js](http://koajs.com/)建模的，这意味着它是从头开始编写的，易于理解和构建。

在本教程中，我们将逐步介绍使用此轻型框架构建基本 REST 微服务的步骤。

## 2.添加依赖

要创建一个基本的应用程序，我们只需要一个依赖项——Javalin 本身：

```xml
<dependency>
    <groupId>io.javalin</groupId>
    <artifactId>javalin</artifactId>
    <version>1.6.1</version>
</dependency>
```

当前版本可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"io.javalin" AND a%3A"javalin")找到。

## 3.设置Javalin

Javalin 使基本应用程序的设置变得容易。我们将从定义主类并设置一个简单的“Hello World”应用程序开始。

让我们在我们的基础包中创建一个名为 JavalinApp.java的新文件。

在这个文件中，我们创建一个 main 方法并添加以下内容来设置一个基本的应用程序：

```java
Javalin app = Javalin.create()
  .port(7000)
  .start();
app.get("/hello", ctx -> ctx.html("Hello, Javalin!"));
```

我们正在创建一个新的 Javalin 实例，让它侦听端口 7000，然后启动应用程序。

我们还设置了第一个端点，用于 在 / hello端点侦听GET请求。

让我们运行这个应用程序并访问 http://localhost:7000/hello查看结果。

## 4. 创建用户控制器

“Hello World”示例非常适合介绍主题，但对于实际应用程序却无益。现在让我们研究 Javalin 的一个更现实的用例。

首先，我们需要为我们正在使用的对象创建一个模型。我们首先在根项目下创建一个名为 user的包。

然后，我们添加一个新的User类：

```java
public class User {
    public final int id;
    public final String name;

    // constructors
}
```

此外，我们需要设置我们的数据访问对象 (DAO)。在此示例中，我们将使用内存中对象来存储我们的用户。

我们在 用户打包中新建一个类叫 UserDao.java：

```java
class UserDao {

    private List<User> users = Arrays.asList(
      new User(0, "Steve Rogers"),
      new User(1, "Tony Stark"),
      new User(2, "Carol Danvers")
    );

    private static UserDao userDao = null;

    private UserDao() {
    }

    static UserDao instance() {
        if (userDao == null) {
            userDao = new UserDao();
        }
        return userDao;
    }

    Optional<User> getUserById(int id) {
        return users.stream()
          .filter(u -> u.id == id)
          .findAny();
    }

    Iterable<String> getAllUsernames() {
        return users.stream()
          .map(user -> user.name)
          .collect(Collectors.toList());
    }
}
```

将我们的 DAO 实现为单例使其更易于在示例中使用。我们也可以将它声明为我们主类的静态成员，或者如果我们愿意，可以使用来自像 Guice 这样的库的依赖注入。

最后，我们要创建我们的控制器类。Javalin 允许我们在声明路由处理程序时非常灵活，因此这只是定义它们的一种方式。

我们在 用户包中创建一个名为UserController.java的新类：

```java
public class UserController {
    public static Handler fetchAllUsernames = ctx -> {
        UserDao dao = UserDao.instance();
        Iterable<String> allUsers = dao.getAllUsernames();
        ctx.json(allUsers);
    };

    public static Handler fetchById = ctx -> {
        int id = Integer.parseInt(Objects.requireNonNull(ctx.param("id")));
        UserDao dao = UserDao.instance();
        Optional<User> user = dao.getUserById(id);
        if (user.isPresent()) {
            ctx.json(user);
        } else {
            ctx.html("Not Found");
        }
    };
}
```

通过将处理程序声明为静态的，我们确保控制器本身不持有任何状态。但是，在更复杂的应用程序中，我们可能希望在请求之间存储状态，在这种情况下，我们需要删除 static 修饰符。

另请注意，静态方法的单元测试更难，因此如果我们想要那种级别的测试，我们将需要使用非静态方法。

## 5. 添加路由

我们现在有多种方式从我们的模型中获取数据。最后一步是通过 REST 端点公开此数据。我们需要在主应用程序中注册两条新路线。

让我们将它们添加到我们的主应用程序类中：

```java
app.get("/users", UserController.fetchAllUsernames);
app.get("/users/:id", UserController.fetchById);
```

编译并运行应用程序后，我们可以向这些新端点中的每一个发出请求。调用 http://localhost:7000/users 将列出所有用户，调用 http://localhost:7000/users/0 将获得 ID 为 0 的单个用户 JSON 对象。我们现在有一个微服务，允许我们检索用户数据。

## 6. 延伸路线

检索数据是大多数微服务的重要任务。

但是，我们还需要能够将数据存储在我们的数据存储中。Javalin 提供构建服务所需的全套路径处理程序。

我们在上面看到了GET的示例 ，但 PATCH、POST、DELETE 和 PUT也是可能的。

此外，如果我们将 Jackson 作为依赖项，我们可以将 JSON 请求主体自动解析到我们的模型类中。例如：

```java
app.post("/") { ctx ->
  User user = ctx.bodyAsClass(User.class);
}
```

将允许我们从请求主体中获取 JSON User对象并将其转换为 User模型对象。

## 七. 总结

我们可以结合所有这些技术来制作我们的微服务。

在本文中，我们了解了如何设置 Javalin 并构建一个简单的应用程序。我们还讨论了如何使用不同的 HTTP 方法类型让客户端与我们的服务进行交互。