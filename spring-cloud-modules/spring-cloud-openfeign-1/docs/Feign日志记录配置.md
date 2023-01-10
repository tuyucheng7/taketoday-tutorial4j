## 1. 概述

在本教程中，我们将描述如何启用 Feign 客户端登录我们的Spring Boot应用程序。此外，我们将看看它的不同类型的配置。如需复习 Feign 客户端，[请查看我们的综合指南](https://www.baeldung.com/spring-cloud-openfeign)。

## 2. 伪装客户端

Feign 是一种声明式 Web 服务 客户端，它通过将注解处理为模板化请求来工作。使用 Feign 客户端，我们摆脱了样板代码来发出 HTTP API 请求。我们只需要放入一个带注解的界面。因此，实际的实现将在运行时创建。

## 3.日志配置

Feign 客户端日志记录有助于我们更好地了解已发出的请求。要启用日志记录，我们需要将Spring Boot日志记录级别设置为DEBUG，以便在应用程序中包含我们的假客户端的类或包。属性文件。 

让我们为一个类设置日志记录级别属性：

```yaml
logging.level.<packageName>.<className> = DEBUG
```

或者，如果我们有一个包，我们可以将所有假客户放入其中，我们可以为整个包添加它：

```yaml
logging.level.<packageName> = DEBUG
```

接下来，我们需要为 feign 客户端设置日志记录级别。请注意，上一步只是启用日志记录。

有四种日志记录级别可供选择：

-   NONE： 没有日志记录(默认)
-   BASIC： 记录请求方法和URL以及响应状态码和执行时间
-   HEADERS： 记录基本信息以及请求和响应标头
-   FULL：记录请求和响应的标头、正文和元数据

我们可以通过 java 配置或在我们的属性文件中配置它们。

### 3.1. Java配置

我们需要声明一个配置类，我们称之为FeignConfig：

```java
public class FeignConfig {
 
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```

之后，我们将配置类绑定到我们的假客户端类FooClient 中：

```java
@FeignClient(name = "foo-client", configuration = FeignConfig.class)
public interface FooClient {
 
    // methods for different requests
}
```

### 3.2. 使用属性

第二种方式是在我们的application.properties 中设置它。让我们在这里引用我们假装客户的名字，在我们的例子中是foo-client：

```yaml
feign.client.config.foo-client.loggerLevel = full
```

或者，我们可以覆盖所有假客户端的默认配置级别：

```yaml
feign.client.config.default.loggerLevel = full
```

## 4.例子

对于这个例子，我们配置了一个客户端来读取[JSONPlaceHolder APIs](https://jsonplaceholder.typicode.com/)。我们将在假客户端的帮助下检索所有用户。

下面，我们将声明UserClient类：

```java
@FeignClient(name = "user-client", url="https://jsonplaceholder.typicode.com", configuration = FeignConfig.class)
public interface UserClient {

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    String getUsers();
}
```

我们将使用在配置部分创建的相同FeignConfig 。请注意，日志记录级别仍然是Logger.Level.FULL。

让我们看一下调用/users时日志记录的样子：

```javascript
2021-05-31 17:21:54 DEBUG 2992 - [thread-1] com.baeldung.UserClient : [UserClient#getUsers] ---> GET https://jsonplaceholder.typicode.com/users HTTP/1.1
2021-05-31 17:21:54 DEBUG 2992 - [thread-1] com.baeldung.UserClient : [UserClient#getUsers] ---> END HTTP (0-byte body)
2021-05-31 17:21:55 DEBUG 2992 - [thread-1] com.baeldung.UserClient : [UserClient#getUsers] <--- HTTP/1.1 200 OK (902ms)
2021-05-31 17:21:55 DEBUG 2992 - [thread-1] com.baeldung.UserClient : [UserClient#getUsers] access-control-allow-credentials: true
2021-05-31 17:21:55 DEBUG 2992 - [thread-1] com.baeldung.UserClient : [UserClient#getUsers] cache-control: max-age=43200
2021-05-31 17:21:55 DEBUG 2992 - [thread-1] com.baeldung.UserClient : [UserClient#getUsers] content-type: application/json; charset=utf-8
2021-05-31 17:21:55 DEBUG 2992 - [thread-1] com.baeldung.UserClient : [UserClient#getUsers] date: Mon, 31 May 2021 14:21:54 GMT
                                                                                            // more headers
2021-05-31 17:21:55 DEBUG 2992 - [thread-1] com.baeldung.UserClient : [UserClient#getUsers] [
  {
    "id": 1,
    "name": "Leanne Graham",
    "username": "Bret",
    "email": "Sincere@april.biz",

    // more user details
  },

  // more users objects
]
2021-05-31 17:21:55 DEBUG 2992 - [thread-1] com.baeldung.UserClient : [UserClient#getPosts] <--- END HTTP (5645-byte body)
```

在日志的第一部分，我们可以看到记录的请求；带有他的 HTTP GET 方法的 URL 端点。在这种情况下，因为它是 GET请求，所以我们没有请求主体。

第二部分包含响应。它显示响应的标头和正文。

## 5.总结

在这个简短的教程中，我们了解了 Feign 客户端日志记录机制以及如何启用它。我们看到有多种方法可以根据我们的需要对其进行配置。