## 1. 概述

在本快速教程中，我们将探讨一些Rest-Assured的高级场景。我们之前在教程[Rest-Assured指南](https://www.baeldung.com/rest-assured-tutorial)中探讨了Rest-Assured。

接下来，**我们将介绍如何为我们的请求设置标头、cookie和参数的示例**。

## 2. 设置参数

现在，让我们讨论如何为我们的请求指定不同的参数-从路径参数开始。

### 2.1 路径参数

我们可以使用pathParam(parameter-name, value)来指定路径参数：

```java
@Test
void whenUsePathParam_thenOK() {
	given().pathParam("user", "tu-yucheng")
        .when().get("/users/{user}/repos")
        .then().statusCode(200);
}
```

要添加多个路径参数，我们可以使用pathParams()方法：

```java
@Test
void whenUseMultiplePathParam_thenOK() {
	given().pathParams("owner", "tu-yucheng", "repo", "fullstack-tutorial4j")
        .when().get("/repos/{owner}/{repo}")
        .then().statusCode(200);
    
	given().pathParams("owner", "tu-yucheng")
        .when().get("/repos/{owner}/{repo}", "fullstack-tutorial4j")
        .then().statusCode(200);
}
```

在此示例中，我们使用了命名路径参数，但我们也可以添加未命名参数，甚至可以将两者结合起来：

```java
given().pathParams("owner", "tu-yucheng")
    .when().get("/repos/{owner}/{repo}", "fullstack-tutorial4j")
    .then().statusCode(200);
```

在这种情况下，生成的URL是[https://api.github.com/repos/tuyucheng/fullstack-tutorials4j](https://api.github.com/repos/tuyucheng/fullstack-tutorials4j)。

请注意，未命名参数是基于索引的。

### 2.2 查询参数

接下来，让我们看看如何使用queryParam()指定查询参数：

```java
@Test
void whenUseQueryParam_thenOK() {
	given().queryParam("q", "john")
        .when().get("/search/users")
        .then().statusCode(200);
    
	given().param("q", "john")
        .when().get("/search/users")
        .then().statusCode(200);
}
```

**param()方法的行为类似于使用GET请求的queryParam()**。

要添加多个查询参数，我们可以链接多个queryParam()方法，或者将参数添加到queryParams()方法：

```java
@Test
void whenUseMultipleQueryParam_thenOK() {
	int perPage = 20;
	given().queryParam("q", "john").queryParam("per_page", perPage)
        .when().get("/search/users")
        .then().body("items.size()", is(perPage));
    
	given().queryParams("q", "john", "per_page", perPage)
        .when().get("/search/users")
        .then().body("items.size()", is(perPage));
}
```

### 2.3 表单参数

最后，我们可以使用formParams()指定表单参数：

```java
@Test
void whenUseFormParam_thenSuccess() {
    given().formParams("username", "john","password","1234").post("/");

    given().params("username", "john","password","1234").post("/");
}
```

**params()方法将为POST请求执行formParams()的生命周期**。

另请注意，formParam()添加了一个值为“application/x-www-form-urlencoded”的Content-Type标头。

## 3. 设置请求头

接下来，**我们可以使用header()自定义请求头**：

```java
@Test
void whenUseCustomHeader_thenOK() {
    given().header("User-Agent", "MyAppName").when().get("/users/tu-yucheng")
        .then().statusCode(200);
}
```

在此示例中，我们使用header()来设置User-Agent标头。

我们还可以使用相同的方法添加具有多个值的标头：

```java
@Test
void whenUseMultipleHeaderValues_thenOK() {
    given().header("My-Header", "val1", "val2")
        .when().get("/users/tu-yucheng")
        .then().statusCode(200);
}
```

在此示例中，我们将有一个包含两个标头的请求：My-Header:val1和My-Header:val2。

**要添加多个标头，我们可以使用headers()方法**：

```java
@Test
void whenUseMultipleHeaders_thenOK() {
    given().headers("User-Agent", "MyAppName", "Accept-Charset", "utf-8")
        .when().get("/users/tu-yucheng")
        .then().statusCode(200);
}
```

## 4. 添加Cookie

我们还可以使用cookie()为我们的请求指定自定义cookie：

```java
@Test
void whenUseCookie_thenOK() {
    given().cookie("session_id", "1234").when().get("/users/tu-yucheng")
        .then().statusCode(200);
}
```

我们还可以使用Cookie.Builder自定义我们的cookie：

```java
@Test
void whenUseCookieBuilder_thenOK() {
    Cookie myCookie = new Cookie.Builder("session_id", "1234")
        .setSecured(true)
        .setComment("session id cookie")
        .build();

    given().cookie(myCookie)
        .when().get("/users/tu-yucheng")
        .then().statusCode(200);
}
```

## 5. 总结

在本文中，我们演示了如何在使用Rest-Assured时指定请求参数、请求头和cookie。