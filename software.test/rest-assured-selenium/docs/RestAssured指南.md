## **一、简介**

REST-assured 旨在简化 REST API 的测试和验证，并受到 Ruby 和 Groovy 等动态语言中使用的测试技术的高度影响。

该库对 HTTP 有坚实的支持，当然从动词和标准 HTTP 操作开始，但也远远超出了这些基础。

在本指南中，我们将**探索 REST-assured**，并使用 Hamcrest 进行断言。如果您还不熟悉 Hamcrest，您应该先复习一下教程：[使用 Hamcrest 进行测试](https://www.baeldung.com/java-junit-hamcrest-guide)。

此外，要了解 REST-assured 的更高级用例，请查看我们的其他文章：

-   [使用 Groovy 确保 REST](https://www.baeldung.com/rest-assured-groovy)
-   [JSON Schema Validation with REST-assured](https://www.baeldung.com/rest-assured-json-schema)
-   [具有 REST 保证的参数、标头和 Cookie](https://www.baeldung.com/rest-assured-header-cookie-parameter)

现在让我们通过一个简单的例子来深入了解。

## **2. 简单示例测试**

在开始之前，让我们确保我们的测试具有以下静态导入：

```java
io.restassured.RestAssured.*
io.restassured.matcher.RestAssuredMatchers.*
org.hamcrest.Matchers.*复制
```

我们需要它来保持测试简单并轻松访问主要 API。

现在，让我们从一个简单的例子开始——一个公开一些游戏数据的基本投注系统：

```javascript
{
    "id": "390",
    "data": {
        "leagueId": 35,
        "homeTeam": "Norway",
        "visitingTeam": "England",
    },
    "odds": [{
        "price": "1.30",
        "name": "1"
    },
    {
        "price": "5.25",
        "name": "X"
    }]
}复制
```

假设这是访问本地部署的 API 的 JSON 响应 - *http://localhost:8080/events?id=390。*：

现在让我们使用 REST-assured 来验证响应 JSON 的一些有趣特性：

```java
@Test
public void givenUrl_whenSuccessOnGetsResponseAndJsonHasRequiredKV_thenCorrect() {
   get("/events?id=390").then().statusCode(200).assertThat()
      .body("data.leagueId", equalTo(35)); 
}复制
```

所以，我们在这里所做的是——我们验证了对端点*/events?id=390*的调用会响应一个包含*JSON 字符串*的主体，该 JSON 字符串的*数据*对象的*LeagueId为 35。*

让我们看一个更有趣的例子。假设您想验证*赔率*数组是否包含价格为*1.30*和*5.25*的记录：

```java
@Test
public void givenUrl_whenJsonResponseHasArrayWithGivenValuesUnderKey_thenCorrect() {
   get("/events?id=390").then().assertThat()
      .body("odds.price", hasItems("1.30", "5.25"));
}复制
```

## **3. REST 保证设置**

如果你最喜欢的依赖工具是 Maven，我们在*pom.xml*文件中添加如下依赖：

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>3.3.0</version>
    <scope>test</scope>
</dependency>复制
```

要获取最新版本，请点击[此链接](https://search.maven.org/classic/#search|ga|1|a%3A"rest-assured")。
REST-assured 利用 Hamcrest 匹配器的强大功能来执行其断言，因此我们也必须包含该依赖项：

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-all</artifactId>
    <version>2.1</version>
</dependency>复制
```

[此链接](https://search.maven.org/classic/#search|ga|1|a%3A"hamcrest-all")将始终提供最新版本。

## **4. 匿名 JSON 根验证**

考虑一个由基元而不是对象组成的数组：

```javascript
[1, 2, 3]复制
```

这称为匿名 JSON 根，这意味着它没有键值对，但它仍然是有效的 JSON 数据。

在这种情况下，我们可以使用*`$`*符号或空字符串（“”）作为路径来运行验证。*假设我们通过http://localhost:8080/json*公开上述服务，那么我们可以使用 REST-assured 来验证它：

```java
when().get("/json").then().body("$", hasItems(1, 2, 3));复制
```

或像这样：

```java
when().get("/json").then().body("", hasItems(1, 2, 3));复制
```

## **5. 花车和双打**

当我们开始使用 REST-assured 来测试我们的 REST 服务时，我们需要了解 JSON 响应中的浮点数被映射到原始类型*float。*

*浮点*类型的使用不能与*double*互换，就像 java 中的许多场景一样。

一个典型的例子是这样的回应：

```javascript
{
    "odd": {
        "price": "1.30",
        "ck": 12.2,
        "name": "1"
    }
}复制
```

假设我们正在对*ck*的值运行以下测试：

```java
get("/odd").then().assertThat().body("odd.ck", equalTo(12.2));复制
```

即使我们正在测试的值等于响应中的值，此测试也会失败。这是因为我们比较的是*double*而不是*float*。

为了使它工作，我们必须将*equalTo*匹配器方法的操作数显式指定为*float*，如下所示：

```java
get("/odd").then().assertThat().body("odd.ck", equalTo(12.2f));复制
```

## **6. 指定请求方法**

通常，我们会通过调用*get() 等方法来执行请求，*对应于我们要使用的请求方法。

此外，**我们还可以使用*request()*方法**指定 HTTP 动词：

```java
@Test
public void whenRequestGet_thenOK(){
    when().request("GET", "/users/eugenp").then().statusCode(200);
}复制
```

上面的例子等价于直接使用*get()*。

同样，我们可以发送*HEAD*、*CONNECT*和*OPTIONS*请求：

```java
@Test
public void whenRequestHead_thenOK() {
    when().request("HEAD", "/users/eugenp").then().statusCode(200);
}复制
```

***POST*请求也遵循类似的语法，我们可以使用*with()* 和*body()* 方法指定*正文 。***

因此，要通过发送*POST* 请求来创建新的 *Odd ：*

```java
@Test
public void whenRequestedPost_thenCreated() {
    with().body(new Odd(5.25f, 1, 13.1f, "X"))
      .when()
      .request("POST", "/odds/new")
      .then()
      .statusCode(201);
}复制
```

*作为body* 发送的*Odd* 对象将自动转换为 JSON。我们还可以传递我们想要发送的任何*字符串作为我们的**POST**正文。*

## **7. 默认值配置**

我们可以为测试配置很多默认值：

```java
@Before
public void setup() {
    RestAssured.baseURI = "https://api.github.com";
    RestAssured.port = 443;
}复制
```

在这里，我们为我们的请求设置一个基本 URI 和端口。除此之外，我们还可以配置基本路径、root pat 和身份验证。

注意：我们还可以使用以下命令重置为标准 REST 保证默认值：

```java
RestAssured.reset();复制
```

## **8. 测量响应时间**

让我们看看如何使用***Response*****对象的*****time()*****和*****timeIn()*****方法****测量响应时间**：

```java
@Test
public void whenMeasureResponseTime_thenOK() {
    Response response = RestAssured.get("/users/eugenp");
    long timeInMS = response.time();
    long timeInS = response.timeIn(TimeUnit.SECONDS);
    
    assertEquals(timeInS, timeInMS/1000);
}复制
```

注意：

-   *time()*用于获取以毫秒为单位的响应时间
-   *timeIn()*用于获取指定时间单位的响应时间

### **8.1。验证响应时间**

我们还可以在简单的*long* *Matcher 的帮助下验证响应时间（以毫秒为单位）：*

```java
@Test
public void whenValidateResponseTime_thenSuccess() {
    when().get("/users/eugenp").then().time(lessThan(5000L));
}复制
```

如果我们想以不同的时间单位验证响应时间，那么我们将使用*time()*匹配器和第二个*TimeUnit*参数：

```java
@Test
public void whenValidateResponseTimeInSeconds_thenSuccess(){
    when().get("/users/eugenp").then().time(lessThan(5L),TimeUnit.SECONDS);
}复制
```

## **9. XML 响应验证**

它不仅可以验证 JSON 响应，还可以验证 XML。

假设我们向*http://localhost:8080/employees*发出请求，我们得到以下响应：

```xml
<employees>
    <employee category="skilled">
        <first-name>Jane</first-name>
        <last-name>Daisy</last-name>
        <sex>f</sex>
    </employee>
</employees>复制
```

我们可以像这样验证*名字*是*Jane*：

```java
@Test
public void givenUrl_whenXmlResponseValueTestsEqual_thenCorrect() {
    post("/employees").then().assertThat()
      .body("employees.employee.first-name", equalTo("Jane"));
}复制
```

我们还可以通过将主体匹配器链接在一起来验证所有值是否与我们的预期值匹配，如下所示：

```java
@Test
public void givenUrl_whenMultipleXmlValuesTestEqual_thenCorrect() {
    post("/employees").then().assertThat()
      .body("employees.employee.first-name", equalTo("Jane"))
        .body("employees.employee.last-name", equalTo("Daisy"))
          .body("employees.employee.sex", equalTo("f"));
}复制
```

或者使用带有可变参数的速记版本：

```java
@Test
public void givenUrl_whenMultipleXmlValuesTestEqualInShortHand_thenCorrect() {
    post("/employees")
      .then().assertThat().body("employees.employee.first-name", 
        equalTo("Jane"),"employees.employee.last-name", 
          equalTo("Daisy"), "employees.employee.sex", 
            equalTo("f"));
}复制
```

## **10. XML 的 XPath**

**我们还可以使用 XPath 验证我们的响应。**考虑下面在*first-name*上执行匹配器的示例：

```java
@Test
public void givenUrl_whenValidatesXmlUsingXpath_thenCorrect() {
    post("/employees").then().assertThat().
      body(hasXPath("/employees/employee/first-name", containsString("Ja")));
}复制
```

XPath 还接受运行*equalTo*匹配器的另一种方式：

```java
@Test
public void givenUrl_whenValidatesXmlUsingXpath2_thenCorrect() {
    post("/employees").then().assertThat()
      .body(hasXPath("/employees/employee/first-name[text()='Jane']"));
}复制
```

## 11. 记录测试细节

### **11.1。日志请求详细信息**

首先，让我们看看如何使用***log().all()*****记录整个请求详细信息***：*

```java
@Test
public void whenLogRequest_thenOK() {
    given().log().all()
      .when().get("/users/eugenp")
      .then().statusCode(200);
}复制
```

这将记录如下内容：

```bash
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
Body:			<none>复制
```

为了只记录请求的特定部分，我们将*log()*方法与*params()、body()、headers()、cookie()、method()、path()*结合使用，例如*log.().params( ）。*

**请注意，使用的其他库或过滤器可能会改变实际发送到服务器的内容，因此这应该只用于记录初始请求规范。**

### **11.2. 记录响应详细信息**

同样，我们可以记录响应详细信息。

在以下示例中，我们仅记录响应正文：

```java
@Test
public void whenLogResponse_thenOK() {
    when().get("/repos/eugenp/tutorials")
      .then().log().body().statusCode(200);
}复制
```

样本输出：

```bash
{
    "id": 9754983,
    "name": "tutorials",
    "full_name": "eugenp/tutorials",
    "private": false,
    "html_url": "https://github.com/eugenp/tutorials",
    "description": "The "REST With Spring" Course: ",
    "fork": false,
    "size": 72371,
    "license": {
        "key": "mit",
        "name": "MIT License",
        "spdx_id": "MIT",
        "url": "https://api.github.com/licenses/mit"
    },
...
}复制
```

### **11.3. 条件发生时记录响应**

我们还可以选择仅在发生错误或状态代码与给定值匹配时记录响应：

```java
@Test
public void whenLogResponseIfErrorOccurred_thenSuccess() {
 
    when().get("/users/eugenp")
      .then().log().ifError();
    when().get("/users/eugenp")
      .then().log().ifStatusCodeIsEqualTo(500);
    when().get("/users/eugenp")
      .then().log().ifStatusCodeMatches(greaterThan(200));
}复制
```

### **11.4. 验证失败时记录**

仅当验证失败时，我们也可以同时记录请求和响应：

```java
@Test
public void whenLogOnlyIfValidationFailed_thenSuccess() {
    when().get("/users/eugenp")
      .then().log().ifValidationFails().statusCode(200);

    given().log().ifValidationFails()
      .when().get("/users/eugenp")
      .then().statusCode(200);
}复制
```

在此示例中，我们要验证状态码是否为 200。只有当此操作失败时，才会记录请求和响应。

## **12. 结论**

在本教程中，我们**探索了 REST-assured 框架**并查看了它最重要的特性，我们可以使用这些特性来测试我们的 RESTful 服务并验证它们的响应。