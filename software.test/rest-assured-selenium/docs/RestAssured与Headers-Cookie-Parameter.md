## **一、概述**

在本快速教程中，我们将探索一些 REST 保证的高级场景。我们之前在教程[REST-assured 指南中探讨了 REST-assured](https://www.baeldung.com/rest-assured-tutorial)。

为了继续，**我们将介绍如何为我们的请求设置标头、cookie 和参数的示例。**

设置与上一篇文章相同，因此让我们深入研究示例。

## **2. 设置参数**

现在，让我们讨论如何为我们的请求指定不同的参数——从路径参数开始。

### **2.1。路径参数**

我们可以使用*pathParam(parameter-name, value)*来指定路径参数：

```java
@Test
public void whenUsePathParam_thenOK() {
    given().pathParam("user", "eugenp")
      .when().get("/users/{user}/repos")
      .then().statusCode(200);
}复制
```

要添加多个路径参数，我们将使用*pathParams()*方法：

```java
@Test
public void whenUseMultiplePathParam_thenOK() {
    given().pathParams("owner", "eugenp", "repo", "tutorials")
      .when().get("/repos/{owner}/{repo}")
      .then().statusCode(200);

    given().pathParams("owner", "eugenp")
      .when().get("/repos/{owner}/{repo}","tutorials")
      .then().statusCode(200);
}复制
```

在此示例中，我们使用了命名路径参数，但我们也可以添加未命名参数，甚至将两者结合起来：

```java
given().pathParams("owner", "eugenp")
  .when().get("/repos/{owner}/{repo}", "tutorials")
  .then().statusCode(200);复制
```

在这种情况下，生成的 URL 是*https://api.github.com/repos/eugenp/tutorials。*

请注意，未命名的参数是基于索引的。

### **2.2. 查询参数**

接下来，让我们看看如何使用*queryParam() 指定查询参数：*

```java
@Test
public void whenUseQueryParam_thenOK() {
    given().queryParam("q", "john").when().get("/search/users")
      .then().statusCode(200);

    given().param("q", "john").when().get("/search/users")
      .then().statusCode(200);
}复制
```

***param()*方法的作用类似于带有 GET 请求的*queryParam() 。***

要添加多个查询参数，我们可以链接多个*queryParam()*方法，或者将参数添加到*queryParams()*方法：

```java
@Test
public void whenUseMultipleQueryParam_thenOK() {
 
    int perPage = 20;
    given().queryParam("q", "john").queryParam("per_page",perPage)
      .when().get("/search/users")
      .then().body("items.size()", is(perPage));   
     
    given().queryParams("q", "john","per_page",perPage)
      .when().get("/search/users")
      .then().body("items.size()", is(perPage));
}复制
```

### **2.3. 表单参数**

最后，我们可以使用*formParam() 指定表单参数：*

```java
@Test
public void whenUseFormParam_thenSuccess() {
 
    given().formParams("username", "john","password","1234").post("/");

    given().params("username", "john","password","1234").post("/");
}复制
```

***param()*方法将对 POST 请求执行 life formParam *()*。**

另请注意，*formParam()*添加了一个值为“ *application/x-www-form-urlencoded ”的**Content-Type*标头。

## **3.设置标题**

接下来，**我们可以使用*header() 自定义我们的请求标头：***

```java
@Test
public void whenUseCustomHeader_thenOK() {
 
    given().header("User-Agent", "MyAppName").when().get("/users/eugenp")
      .then().statusCode(200);
}复制
```

在此示例中，我们使用*header()*来设置*User-Agent*标头。

我们还可以使用相同的方法添加具有多个值的标头：

```java
@Test
public void whenUseMultipleHeaderValues_thenOK() {
 
    given().header("My-Header", "val1", "val2")
      .when().get("/users/eugenp")
      .then().statusCode(200);
}复制
```

在此示例中，我们将有一个带有两个标头的请求：*My-Header:val1*和*My-Header:val2。*

**为了添加多个标题，我们将使用*headers()*方法：**

```java
@Test
public void whenUseMultipleHeaders_thenOK() {
 
    given().headers("User-Agent", "MyAppName", "Accept-Charset", "utf-8")
      .when().get("/users/eugenp")
      .then().statusCode(200);
}复制
```

## **4. 添加 Cookie**

我们还可以使用*cookie()*为我们的请求指定自定义 cookie ：

```java
@Test
public void whenUseCookie_thenOK() {
 
    given().cookie("session_id", "1234").when().get("/users/eugenp")
      .then().statusCode(200);
}复制
```

我们还可以使用 cookie *Builder*自定义我们的 cookie ：

```java
@Test
public void whenUseCookieBuilder_thenOK() {
    Cookie myCookie = new Cookie.Builder("session_id", "1234")
      .setSecured(true)
      .setComment("session id cookie")
      .build();

    given().cookie(myCookie)
      .when().get("/users/eugenp")
      .then().statusCode(200);
}复制
```

## **5. 结论**

在本文中，我们展示了如何在使用 REST-assured 时指定请求参数、标头和 cookie。