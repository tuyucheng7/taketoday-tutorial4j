## 1. 简介

在本文中，我们将快速介绍[Spark 框架](http://sparkjava.com/)。Spark 框架是一个快速开发的 Web 框架，其灵感来自 Ruby 的 Sinatra 框架，并围绕Java8 Lambda 表达式理念构建，使其比大多数用其他Java框架编写的应用程序更简洁。

如果你想在使用Java开发 Web API 或微服务时拥有类似Node.js的体验，这是一个不错的选择。借助 Spark，你可以在不到十行代码的情况下准备好 REST API 来为 JSON 提供服务。

我们将从一个“Hello World”示例快速开始，然后是一个简单的 REST API。

## 2.Maven依赖

### 2.1. 火花框架

在pom.xml中包含以下 Maven 依赖项：

```xml
<dependency>
    <groupId>com.sparkjava</groupId>
    <artifactId>spark-core</artifactId>
    <version>2.5.4</version>
</dependency>
```

[你可以在Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"com.sparkjava" AND a%3A"spark-core")上找到最新版本的 Spark 。

### 2.2. Gson库

在示例的不同位置，我们将使用 Gson 库进行 JSON 操作。要在你的项目中包含 Gson，请在你的pom.xml中包含此依赖项：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.0</version>
</dependency>
```

[你可以在Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.code.gson" AND a%3A"gson")上找到最新版本的 Gson 。

## 3. Spark 框架入门

让我们看一下 Spark 应用程序的基本构建块并演示一个快速的 Web 服务。

### 3.1. 航线

SparkJava中的 Web 服务构建在路由及其处理程序之上。路由是 Spark 中必不可少的元素。根据[文档](http://sparkjava.com/documentation.html#routes)，每个路由都由三个简单的部分组成——一个verb、一个path和一个callback。

1.  动词是对应于 HTTP方法的方法。动词方法包括：get、post、put、delete、head、trace、connect和options
2.  路径(也称为路由模式)确定路由应侦听哪些 URI 并为其提供响应
3.  回调是针对给定动词和路径调用的处理程序函数，以便生成并返回对相应 HTTP 请求的响应。回调将请求对象和响应对象作为参数

这里我们展示了使用get动词的路由的基本结构：

```java
get("/your-route-path/", (request, response) -> {
    // your callback code
});
```

### 3.2. 你好世界API

让我们创建一个简单的 Web 服务，它有两个 GET 请求路由并返回“Hello”消息作为响应。这些路由使用get方法，它是类spark.Spark的静态导入：

```java
import static spark.Spark.;

public class HelloWorldService {
    public static void main(String[] args) {
 
        get("/hello", (req, res)->"Hello, world");
        
        get("/hello/:name", (req,res)->{
            return "Hello, "+ req.params(":name");
        });
    }
}
```

get方法的第一个参数是路由的路径。第一个路由包含仅表示单个 URI ( “/hello” ) 的静态路径。

第二条路由的路径(“/hello/:name” )包含“name”参数的占位符，通过在参数前面加上冒号(“:”)来表示。此路由将被调用以响应对 URI(例如“/hello/Joe”和“/hello/Mary”)的 GET 请求。

get方法的第二个参数是一个[lambda 表达式](https://www.baeldung.com/java-8-lambda-expressions-tips)，为该框架提供了函数式编程风格。

lambda 表达式将请求和响应作为参数并帮助返回响应。我们将把我们的控制器逻辑放在 REST API 路由的 lambda 表达式中，正如我们将在本教程后面看到的那样。

### 3.3. 测试 Hello World API

在将HelloWorldService类作为普通Java类运行后，你将能够使用上面get方法定义的路由在其默认端口4567上访问该服务。

让我们看看第一个路由的请求和响应：

要求：

```plaintext
GET http://localhost:4567/hello
```

回复：

```plaintext
Hello, world
```

让我们测试第二条路由，在其路径中传递name参数：

要求：

```plaintext
GET http://localhost:4567/hello/baeldung
```

回复：

```plaintext
Hello, baeldung
```

查看URI 中文本“baeldung”的位置如何用于匹配路由模式“/hello/:name” ——导致调用第二个路由的回调处理函数。

## 4. 设计 RESTful 服务

在本节中，我们将为以下用户实体设计一个简单的 REST Web 服务：

```java
public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String email;

    // constructors, getters and setters
}
```

### 4.1. 航线

让我们列出构成我们 API 的路由：

-   GET /users — 获取所有用户的列表
-   GET /users/:id — 获取给定 id 的用户
-   POST /users/:id — 添加用户
-   PUT /users/:id — 编辑特定用户
-   选项 /users/:id — 检查给定 ID 的用户是否存在
-   DELETE /users/:id — 删除特定用户

### 4.2. 用户服务

下面是声明User实体的 CRUD 操作的UserService接口：

```java
public interface UserService {
 
    public void addUser (User user);
    
    public Collection<User> getUsers ();
    public User getUser (String id);
    
    public User editUser (User user) 
      throws UserException;
    
    public void deleteUser (String id);
    
    public boolean userExist (String id);
}
```

出于演示目的，我们在 GitHub 代码中提供了此UserService接口的Map实现，以模拟持久化。你可以使用你选择的数据库和持久层来提供你自己的实现。

### 4.3. JSON 响应结构

以下是我们的 REST 服务中使用的响应的 JSON 结构：

```javascript
{
    status: <STATUS>
    message: <TEXT-MESSAGE>
    data: <JSON-OBJECT>
}
```

状态字段值可以是SUCCESS或ERROR。数据字段将包含返回数据的 JSON 表示形式，例如User或Users的集合。

当没有数据返回时，或者如果状态为ERROR，我们将填充消息字段以传达错误或缺少返回数据的原因。

让我们使用Java类表示上述 JSON 结构：

```java
public class StandardResponse {
 
    private StatusResponse status;
    private String message;
    private JsonElement data;
    
    public StandardResponse(StatusResponse status) {
        // ...
    }
    public StandardResponse(StatusResponse status, String message) {
        // ...
    }
    public StandardResponse(StatusResponse status, JsonElement data) {
        // ...
    }
    
    // getters and setters
}
```

其中StatusResponse是一个枚举，定义如下：

```java
public enum StatusResponse {
    SUCCESS ("Success"),
    ERROR ("Error");
 
    private String status;       
    // constructors, getters
}
```

## 5. 实现 RESTful 服务

现在让我们为我们的 REST API 实现路由和处理程序。

### 5.1. 创建控制器

以下Java类包含我们 API 的路由，包括动词和路径以及每个路由的处理程序的概要：

```java
public class SparkRestExample {
    public static void main(String[] args) {
        post("/users", (request, response) -> {
            //...
        });
        get("/users", (request, response) -> {
            //...
        });
        get("/users/:id", (request, response) -> {
            //...
        });
        put("/users/:id", (request, response) -> {
            //...
        });
        delete("/users/:id", (request, response) -> {
            //...
        });
        options("/users/:id", (request, response) -> {
            //...
        });
    }
}
```

我们将在以下小节中展示每个路由处理程序的完整实现。

### 5.2. 添加用户

下面是将添加User 的post方法响应处理程序：

```java
post("/users", (request, response) -> {
    response.type("application/json");
    User user = new Gson().fromJson(request.body(), User.class);
    userService.addUser(user);

    return new Gson()
      .toJson(new StandardResponse(StatusResponse.SUCCESS));
});
```

注意：在此示例中，用户对象的 JSON 表示作为 POST 请求的原始主体传递。

让我们测试路线：

要求：

```plaintext
POST http://localhost:4567/users
{
    "id": "1012", 
    "email": "your-email@your-domain.com", 
    "firstName": "Mac",
    "lastName": "Mason1"
}
```

回复：

```plaintext
{
    "status":"SUCCESS"
}
```

### 5.3. 获取所有用户

下面是从UserService返回所有用户的get方法响应处理程序：

```java
get("/users", (request, response) -> {
    response.type("application/json");
    return new Gson().toJson(
      new StandardResponse(StatusResponse.SUCCESS,new Gson()
        .toJsonTree(userService.getUsers())));
});
```

现在让我们测试路线：

要求：

```plaintext
GET http://localhost:4567/users
```

回复：

```plaintext
{
    "status":"SUCCESS",
    "data":[
        {
            "id":"1014",
            "firstName":"John",
            "lastName":"Miller",
            "email":"your-email@your-domain.com"
        },
        {
            "id":"1012",
            "firstName":"Mac",
            "lastName":"Mason1",
            "email":"your-email@your-domain.com"
        }
    ]
}
```

### 5.4. 通过 Id 获取用户

下面是get方法响应处理程序，它返回具有给定id 的用户：

```java
get("/users/:id", (request, response) -> {
    response.type("application/json");
    return new Gson().toJson(
      new StandardResponse(StatusResponse.SUCCESS,new Gson()
        .toJsonTree(userService.getUser(request.params(":id")))));
});
```

现在让我们测试路线：

要求：

```plaintext
GET http://localhost:4567/users/1012
```

回复：

```plaintext
{
    "status":"SUCCESS",
    "data":{
        "id":"1012",
        "firstName":"Mac",
        "lastName":"Mason1",
        "email":"your-email@your-domain.com"
    }
}
```

### 5.5. 编辑用户

下面是put方法响应处理程序，它编辑具有路由模式中提供的id的用户：

```java
put("/users/:id", (request, response) -> {
    response.type("application/json");
    User toEdit = new Gson().fromJson(request.body(), User.class);
    User editedUser = userService.editUser(toEdit);
            
    if (editedUser != null) {
        return new Gson().toJson(
          new StandardResponse(StatusResponse.SUCCESS,new Gson()
            .toJsonTree(editedUser)));
    } else {
        return new Gson().toJson(
          new StandardResponse(StatusResponse.ERROR,new Gson()
            .toJson("User not found or error in edit")));
    }
});
```

注意：在此示例中，数据作为 JSON 对象在 POST 请求的原始正文中传递，其属性名称与要编辑的用户对象的字段相匹配。

让我们测试路线：

要求：

```plaintext
PUT http://localhost:4567/users/1012
{
    "lastName": "Mason"
}
```

回复：

```plaintext
{
    "status":"SUCCESS",
    "data":{
        "id":"1012",
        "firstName":"Mac",
        "lastName":"Mason",
        "email":"your-email@your-domain.com"
    }
}
```

### 5.6. 删除用户

下面是delete方法响应处理程序，它将删除具有给定id的用户：

```java
delete("/users/:id", (request, response) -> {
    response.type("application/json");
    userService.deleteUser(request.params(":id"));
    return new Gson().toJson(
      new StandardResponse(StatusResponse.SUCCESS, "user deleted"));
});
```

现在，让我们测试路线：

要求：

```plaintext
DELETE http://localhost:4567/users/1012
```

回复：

```plaintext
{
    "status":"SUCCESS",
    "message":"user deleted"
}
```

### 5.7. 检查用户是否存在

options方法是条件检查的不错选择。下面是选项方法响应处理程序，它将检查是否存在具有给定ID的用户：

```java
options("/users/:id", (request, response) -> {
    response.type("application/json");
    return new Gson().toJson(
      new StandardResponse(StatusResponse.SUCCESS, 
        (userService.userExist(
          request.params(":id"))) ? "User exists" : "User does not exists" ));
});
```

现在让我们测试路线：

要求：

```plaintext
OPTIONS http://localhost:4567/users/1012
```

回复：

```plaintext
{
    "status":"SUCCESS",
    "message":"User exists"
}
```

## 六. 总结

在本文中，我们快速介绍了用于快速 Web 开发的 Spark 框架。

该框架主要用于在Java中生成微服务。具有Java知识并希望利用构建在 JVM 库上的库的Node.js开发人员应该对使用此框架感到自如。