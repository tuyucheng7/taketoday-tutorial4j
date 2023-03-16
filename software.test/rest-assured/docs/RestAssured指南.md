## 1. 简介

Rest-Assured旨在简化REST API的测试和验证，并深受动态语言(如Ruby和Groovy)中使用的测试技术的影响。

该库对HTTP提供了可靠的支持，当然从动词和标准HTTP操作开始，但也远远超出了这些基础知识。

在本指南中，我们将**探索Rest-Assured的基本使用**，并使用Hamcrest进行断言。如果你还不熟悉Hamcrest，应该首先阅读这篇文章：[使用Hamcrest进行测试](../../testing-libraries-3/docs/使用Hamcrest进行测试.md)。

此外，要了解Rest-Assured的更高级用例，请查看我们的其他文章：

- [Groovy与Rest-Assured](Groovy-RestAssured.md)
- [Rest-Assured中的的JSON模式验证](RestAssured中的的JSON模式验证.md)
- [Rest-Assured中的参数、Header和Cookie](RestAssured中的参数-Header和Cookie.md)

## 2. 简单测试示例

在开始之前，让我们确保我们的测试具有以下静态导入：

```java
io.restassured.RestAssured.*
io.restassured.matcher.RestAssuredMatchers.*
org.hamcrest.Matchers.*
```

我们需要它来保持测试简单并轻松访问主要API。

现在，让我们从一个简单的例子开始-一个基本的投注系统，公开了一些游戏数据：

```json
{
    "id": "390",
    "data": {
        "leagueId": 35,
        "homeTeam": "Norway",
        "visitingTeam": "England"
    },
    "odds": [
        {
            "price": "1.30",
            "name": "1"
        },
        {
            "price": "5.25",
            "name": "X"
        }
    ]
}
```

假设这是来自命中本地部署的API [http://localhost:8080/events?id=390](http://localhost:8080/events?id=390)的JSON响应。

现在让我们使用Rest-Assured来验证响应JSON的一些有趣功能：

```java
@Test
public void givenUrl_whenSuccessOnGetsResponseAndJsonHasRequiredKV_thenCorrect() {
    get("/events?id=390")
        .then()
        .statusCode(200)
        .assertThat()
        .body("data.leagueId", equalTo(35)); 
}
```

因此，我们在这里所做的是-我们验证了对端点/events?id=390的调用响应包含一个JSON字符串，其data对象的leagueId为35。

让我们看一个更有趣的例子。假设你想验证odds数组是否包含price为1.30和5.25的记录：

```java
@Test
public void givenUrl_whenJsonResponseHasArrayWithGivenValuesUnderKey_thenCorrect() {
    get("/events?id=390")
        .then()
        .assertThat()
        .body("odds.price", hasItems("1.30", "5.25"));
}
```

## 3. REST-Assured设置

如果你使用的构建工具是Maven，我们将在pom.xml文件中添加如下依赖：

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>3.3.0</version>
    <scope>test</scope>
</dependency>
```

要获取最新版本，请点击[此链接](https://central.sonatype.com/artifact/io.rest-assured/rest-assured/5.3.0)。

Rest-Assured利用Hamcrest匹配器的强大功能来执行其断言，因此我们还必须包含该依赖项：

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-all</artifactId>
    <version>2.1</version>
</dependency>
```

[此链接](https://central.sonatype.com/artifact/org.hamcrest/hamcrest-all/1.3)始终提供最新版本。

## 4. 匿名JSON根验证

考虑一个由基本类型而不是对象组成的数组：

```javascript
[1, 2, 3]
```

这称为匿名JSON根，这意味着它没有键值对，但它仍然是有效的JSON数据。

在这种情况下，我们可以使用$符号或空字符串(“”)作为路径来运行验证。假设我们通过[http://localhost:8080/json](http://localhost:8080/json)公开上述服务，那么我们可以像这样使用Rest-Assured来验证它：

```java
when().get("/json").then().body("$",hasItems(1,2,3));
```

或者像这样：

```java
when().get("/json").then().body("",hasItems(1,2,3));
```

## 5. Floats和Doubles

当我们开始使用Rest-Assured来测试我们的REST服务时，我们需要了解JSON响应中的浮点数映射到原始类型float。

float类型的使用不能与double互换，Java中的许多场景都是如此。

一个典型的例子是以下这个响应：

```json
{
    "odd": {
        "price": "1.30",
        "ck": 12.2,
        "name": "1"
    }
}
```

假设我们对ck的值运行以下测试：

```java
get("/odd").then().assertThat().body("odd.ck",equalTo(12.2));
```

即使我们正在测试的值等于响应中的值，此测试也会失败。这是因为我们比较的是double而不是float。

为了让它正常通过，我们必须明确地将equalTo匹配器方法的操作数指定为float，如下所示：

```java
get("/odd").then().assertThat().body("odd.ck",equalTo(12.2f));
```

## 6. 指定请求方式

通常，我们会通过调用与我们要使用的请求方法相对应的方法(例如get())来执行请求。

此外，**我们还可以使用request()方法指定HTTP动词**：

```java
@Test
public void whenRequestGet_thenOK(){
    when().request("GET", "/users/tuyucheng")
        .then()
        .statusCode(200);
}
```

上面的例子相当于直接使用get()。

类似地，我们可以发送HEAD、CONNECT和OPTIONS请求：

```java
@Test
public void whenRequestHead_thenOK() {
    when().request("HEAD", "/users/tuyucheng")
        .then()
        .statusCode(200);
}
```

**POST请求也遵循类似的语法，我们可以使用with()和body()方法指定请求体**。

因此，通过发送POST请求创建一个新的Odd的例子为：

```java
@Test
public void whenRequestedPost_thenCreated() {
    with().body(new Odd(5.25f, 1, 13.1f, "X"))
        .when()
        .request("POST", "/odds/new")
        .then()
        .statusCode(201);
}
```

作为请求正文发送的Odd对象将自动转换为JSON。我们还可以将要发送的任何字符串作为POST请求正文传递。

## 7. 默认值配置

我们可以为测试配置很多默认值：

```java
@Before
public void setup() {
    RestAssured.baseURI = "https://api.github.com";
    RestAssured.port = 443;
}
```

在这里，我们为我们的请求设置一个基本URI和端口。除此之外，我们还可以配置基本路径、根路径和身份验证。

注意：我们还可以使用以下方法重置为标准的Rest-Assured默认值：

```java
RestAssured.reset();
```

## 8. 测量响应时间

让我们看看如何**使用Response对象的time()和timeIn()方法测量响应时间**：

```java
@Test
public void whenMeasureResponseTime_thenOK() {
    Response response = RestAssured.get("/users/tuyucheng");
    long timeInMS = response.time();
    long timeInS = response.timeIn(TimeUnit.SECONDS);
    
    assertEquals(timeInS, timeInMS/1000);
}
```

注意：

- time()用于获取以毫秒为单位的响应时间
- timeIn()用于获取指定时间单位的响应时间

### 8.1 验证响应时间

我们还可以在简单的long Matcher的帮助下验证响应时间(以毫秒为单位)：

```java
@Test
public void whenValidateResponseTime_thenSuccess() {
    when().get("/users/tuyucheng")
        .then()
        .time(lessThan(5000L));
}
```

如果我们想以不同的时间单位验证响应时间，那么我们可以使用带有第二个TimeUnit参数的time()匹配器：

```java
@Test
public void whenValidateResponseTimeInSeconds_thenSuccess(){
    when().get("/users/tuyucheng")
        .then()
        .time(lessThan(5L),TimeUnit.SECONDS);
}
```

## 9. XML响应验证

Rest-Assured不仅可以验证JSON响应，还可以验证XML。

假设我们向[http://localhost:8080/employees](http://localhost:8080/employees)发出请求，并得到以下响应：

```xml
<employees>
    <employee category="skilled">
        <first-name>Jane</first-name>
        <last-name>Daisy</last-name>
        <sex>f</sex>
    </employee>
</employees>
```

我们可以像这样验证first-name是Jane：

```java
@Test
public void givenUrl_whenXmlResponseValueTestsEqual_thenCorrect() {
    post("/employees")
        .then()
        .assertThat()
        .body("employees.employee.first-name", equalTo("Jane"));
}
```

我们还可以通过将body匹配器链接在一起来验证所有值是否与我们的预期值匹配，如下所示：

```java
@Test
public void givenUrl_whenMultipleXmlValuesTestEqual_thenCorrect() {
    post("/employees").then().assertThat()
        .body("employees.employee.first-name", equalTo("Jane"))
        .body("employees.employee.last-name", equalTo("Daisy"))
        .body("employees.employee.sex", equalTo("f"));
}
```

或者使用带有可变参数的速记版本：

```java
@Test
public void givenUrl_whenMultipleXmlValuesTestEqualInShortHand_thenCorrect() {
    post("/employees")
        .then()
        .assertThat()
        .body("employees.employee.first-name", equalTo("Jane"),
            "employees.employee.last-name", equalTo("Daisy"), 
            "employees.employee.sex", equalTo("f"));
}
```

## 10. XML的XPath

**我们还可以使用XPath验证我们的响应**。考虑以下对first-name执行匹配器的示例：

```java
@Test
public void givenUrl_whenValidatesXmlUsingXpath_thenCorrect() {
    post("/employees").then().assertThat()
        .body(hasXPath("/employees/employee/first-name", containsString("Ja")));
}
```

XPath还接收另一种运行equalTo匹配器的方法：

```java
@Test
public void givenUrl_whenValidatesXmlUsingXpath2_thenCorrect() {
    post("/employees").then().assertThat()
        .body(hasXPath("/employees/employee/first-name[text()='Jane']"));
}
```

## 11. 记录测试细节

### 11.1 记录请求详细信息

首先，让我们看看如何**使用log().all()记录整个请求细节**：

```java
@Test
public void whenLogRequest_thenOK() {
    given().log().all()
        .when().get("/users/tu-yucheng")
        .then().statusCode(200);
}
```

这将记录如下内容：

```shell
Request method:	GET
Request URI:	https://api.github.com:443/users/eugenp
Proxy:			<none>
Request params:	<none>
Query params:	<none>
Form params:	<none>
Path params:	<none>
Multiparts:		<none>
Headers:		Accept=*/*
Cookies:		<none>
Body:			<none>
```

为了只记录请求的特定部分，我们将log()方法与params()、body()、headers()、cookies()、method()、path()结合使用，例如log().params()。

**请注意，使用的其他库或过滤器可能会改变实际发送到服务器的内容，因此这应该只用于记录初始请求规范**。

### 11.2 记录响应详细信息

同样，我们可以记录响应详细信息。

在以下示例中，我们仅记录响应正文：

```java
@Test
public void whenLogResponse_thenOK() {
    when().get("/repos/tu-yucheng/fullstack-tutorial4j")
        .then().log().body().statusCode(200);
}
```

示例输出：

```json
{
    "id": 9754983,
    "name": "tutorials",
    "full_name": "eugenp/tutorials",
    "private": false,
    "html_url": "https://github.com/eugenp/tutorials",
    "description": "The \"REST With Spring\" Course: ",
    "fork": false,
    "size": 72371,
    "license": {
        "key": "mit",
        "name": "MIT License",
        "spdx_id": "MIT",
        "url": "https://api.github.com/licenses/mit"
    }
    // ...
}
```

### 11.3 条件发生时记录响应

我们还可以选择仅在发生错误或状态码与给定值匹配时才记录响应：

```java
@Test
public void whenLogResponseIfErrorOccurred_thenSuccess() {
    when().get("/users/tu-yucheng")
        .then().log().ifError();
    when().get("/users/tu-yucheng")
        .then().log().ifStatusCodeIsEqualTo(500);
    when().get("/users/tu-yucheng")
        .then().log().ifStatusCodeMatches(greaterThan(200));
}
```

### 11.4 验证失败时记录

我们也可以仅在验证失败时记录请求和响应：

```java
@Test
public void whenLogOnlyIfValidationFailed_thenSuccess() {
    when().get("/users/tu-yucheng")
        .then().log().ifValidationFails().statusCode(200);

    given().log().ifValidationFails()
        .when().get("/users/tuyucheng")
        .then().statusCode(200);
}
```

在此示例中，我们要验证状态码是否为200。仅当此操作失败时，才会记录请求和响应。

## 12. 总结

在本教程中，我们探讨了Rest-Assured框架并演示了它最重要的功能，我们可以使用这些功能来测试我们的Restful服务并验证它们的响应。