## 1. 概述

在本快速教程中，我们将探索如何使用由[MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)支持的 Spring Session ，无论是否使用 Spring Boot。

Spring Session 也可以支持[Redis](https://www.baeldung.com/spring-session)和[JDBC](https://www.baeldung.com/spring-session-jdbc)等其他存储。

## 2.Spring Boot配置

首先，让我们看一下Spring Boot所需的依赖和配置。首先，让我们将最新版本的[spring-session-data-mongodb](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.session" AND a%3A"spring-session-data-mongodb")和[spring-boot-starter-data-mongodb](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-data-mongodb")添加到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-mongodb</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
```

之后，要启用Spring Boot自动配置，我们需要在application.properties中将 Spring Session 存储类型添加为mongodb：

```java
spring.session.store-type=mongodb
```

## 3.没有Spring Boot的Spring配置

现在，让我们看一下在没有Spring Boot的情况下将 Spring 会话存储在 MongoDB 中所需的依赖项和配置。

与Spring Boot配置类似，我们需要spring-session-data-mongodb依赖项。但是，在这里我们将使用[spring-data-mongodb](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.data" AND a%3A"spring-data-mongodb")依赖项来访问我们的 MongoDB 数据库：

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-mongodb</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-mongodb</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
```

最后，让我们看看如何配置应用程序：

```java
@EnableMongoHttpSession
public class HttpSessionConfig {

    @Bean
    public JdkMongoSessionConverter jdkMongoSessionConverter() {
        return new JdkMongoSessionConverter(Duration.ofMinutes(30));
    }
}
```

@EnableMongoHttpSession批注启用将会话数据存储在 MongoDB 中所需的配置。

另外，请注意JdkMongoSessionConverter负责序列化和反序列化会话数据。

## 4. 示例应用

让我们创建一个应用程序来测试配置。我们将使用 Spring Boot，因为它速度更快并且需要的配置更少。

我们将从创建控制器来处理请求开始：

```java
@RestController
public class SpringSessionMongoDBController {

    @GetMapping("/")
    public ResponseEntity<Integer> count(HttpSession session) {

        Integer counter = (Integer) session.getAttribute("count");

        if (counter == null) {
            counter = 1;
        } else {
            counter++;
        }

        session.setAttribute("count", counter);

        return ResponseEntity.ok(counter);
    }
}
```

正如我们在这个例子中看到的，我们在每次命中端点时递增计数器并将其值存储在名为count的会话属性中。

## 5. 测试应用

让我们测试应用程序，看看我们是否真的能够将会话数据存储在 MongoDB 中。

为此，我们将访问端点并检查我们将收到的 cookie。这将包含一个会话 ID。

之后，我们将查询 MongoDB 集合以使用会话 ID 获取会话数据：

```java
@Test
public void 
  givenEndpointIsCalledTwiceAndResponseIsReturned_whenMongoDBIsQueriedForCount_thenCountMustBeSame() {
    
    HttpEntity<String> response = restTemplate
      .exchange("http://localhost:" + 8080, HttpMethod.GET, null, String.class);
    HttpHeaders headers = response.getHeaders();
    String set_cookie = headers.getFirst(HttpHeaders.SET_COOKIE);

    Assert.assertEquals(response.getBody(),
      repository.findById(getSessionId(set_cookie)).getAttribute("count").toString());
}

private String getSessionId(String cookie) {
    return new String(Base64.getDecoder().decode(cookie.split(";")[0].split("=")[1]));
}
```

## 6. 它是如何工作的？

让我们来看看幕后的春季会议发生了什么。

SessionRepositoryFilter负责大部分工作：

-   将HttpSession转换为MongoSession
-   检查是否存在[Cookie](https://www.baeldung.com/cookies-java)，如果存在，则从商店加载会话数据
-   将更新的会话数据保存在商店中
-   检查会话的有效性

此外，SessionRepositoryFilter创建一个名为SESSION的 cookie，它是 HttpOnly 且安全的。此 cookie 包含会话 ID，它是 Base64 编码的值。

要自定义 cookie 名称或属性，我们必须创建一个类型为DefaultCookieSerializer 的 Spring bean。

例如，这里我们禁用了cookie 的httponly属性：

```java
@Bean
public DefaultCookieSerializer customCookieSerializer(){
    DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        
    cookieSerializer.setUseHttpOnlyCookie(false);
        
    return cookieSerializer;
}
```

## 7. 存储在 MongoDB 中的会话详细信息

让我们在 MongoDB 控制台中使用以下命令查询我们的会话集合：

```plaintext
db.sessions.findOne()
```

结果，我们将得到类似于以下内容的[BSON文档：](https://www.baeldung.com/mongodb-bson)

```plaintext
{
    "_id" : "5d985be4-217c-472c-ae02-d6fca454662b",
    "created" : ISODate("2019-05-14T16:45:41.021Z"),
    "accessed" : ISODate("2019-05-14T17:18:59.118Z"),
    "interval" : "PT30M",
    "principal" : null,
    "expireAt" : ISODate("2019-05-14T17:48:59.118Z"),
    "attr" : BinData(0,"rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAABdAAFY291bnRzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAC3g=")
}
```

_id是一个[UUID](https://www.baeldung.com/java-uuid)，它将由DefaultCookieSerializer进行 Base64 编码，并设置为SESSION cookie 中的一个值。另外，请注意attr属性包含我们计数器的实际值。

## 八. 总结

在本教程中，我们探索了以 MongoDB 为后盾的 Spring Session——一种在分布式系统中管理 HTTP 会话的强大工具。考虑到这个目的，它对于解决跨应用程序的多个实例会话的问题非常有用。