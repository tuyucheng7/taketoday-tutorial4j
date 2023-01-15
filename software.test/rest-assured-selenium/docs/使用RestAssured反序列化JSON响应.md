就 API 而言，JSON 是一种非常流行的格式。几乎所有 API 都以 XML 格式或 JSON 格式传输数据，其中 JSON 是一种流行的格式。不仅通过 API，公司还使用 JSON 在他们自己的服务器之间传输数据到 UI，因为它具有轻量级且易于阅读的特性。作为专业的测试人员或开发人员，如果您没有遇到 JSON 文件并验证其可读性，这将是很少见的。要使用它们，您应该知道如何通过代码阅读它们并利用自动化测试轻松完成您的任务。在本文中，我们将讨论 REST 响应正文中使用的相同概念。这种读取 JSON 响应的技术也称为***“反序列化”JSON 响应。***

***序列化*** 和 ***反***序列化 是编程技术，我们分别将 ***对象***转换 为 ***字节流*** 和从 ***字节流转*** 换回 ***对象***。我们已经在[***Java 中的序列化和反序列化一文中讨论了这种技术。***](https://toolsqa.com/rest-assured/serialization-and-deserialization-in-java/)我们现在将在本文中介绍以下主题：

-   *什么是序列化 JSON？*
-   *什么是 JSON 响应的反序列化？*
-   *如何以放心的方式反序列化 JSON 对类的响应？*
-   *如何根据响应状态反序列化 JSON 响应体？*

## 什么是序列化 JSON？

如上所述，序列化是将数据对象转换为数据流的过程。*序列化（流到对象）*的逆过程是反序列化。这两个过程都与平台无关。这意味着我们可以在 Windows 平台上序列化一个对象，并在 macOS 上反序列化它。

就[***放心***](https://www.toolsqa.com/rest-assured-tutorial/)而言，我们知道客户端和服务器之间的数据交换是通过 REST Web 服务以 JSON 格式进行的。这里的数据流是 JSON 数据。

例如，如果我们有以下对象：

```java
{tools: [1, 3, 7, 9], api: "QA"}
```

当上述对象序列化为 JSON 时，输出将如下所示：

```java
{
"tools":[1, 3, 7, 9],
"api":"QA"
}
```

上述数据串可以在任何地方存储或传输。然后接收者将数据反序列化回其原始格式，即对象。

Java POJO 类是我们可以序列化对象并在 Rest Assured 中使用它们的一种方式。实现的细节超出了本教程的范围。在我们之前的教程Java 中的[***序列化和反序列化中讨论了序列化。***](https://toolsqa.com/rest-assured/serialization-and-deserialization-in-java/)在测试行业工作的同时，我们会更加关注反序列化。下一节将对此进行详细讨论。

## 什么是反序列化 JSON 响应？

在继续之前，建议阅读文章[***REST Api testing in Rest-Assured***](https://toolsqa.com/rest-assured/rest-api-test-using-rest-assured/) *并与 REST、API 测试、REST Assured 和其他类似概念进行对话。*

在 REST API 中，反序列化过程是专门针对我们从 API 收到的 JSON 响应执行的。所以，当我们说我们正在反序列化 JSON 时，这意味着我们将 JSON 格式转换为我们喜欢的类型，最常见的是[***POJO***](https://en.wikipedia.org/wiki/Plain_old_Java_object) *(普通的旧 Java 对象）*类。

所以在这个过程中，我们基本上告诉我们的代码使用不易出错的强类型字符串而不是 JSON 字符串。让我们继续看看如何用 Rest Assured 反序列化 JSON 对类的响应。

## 如何在放心的情况下反序列化对类的 JSON 响应？

我们将从[***使用 Rest-Assured 发出 POST 请求中***](https://toolsqa.com/rest-assured/post-request-using-rest-assured/)的示例继续，以实现 JSON 响应的反序列化。在本文中，我们看到一个成功的 post 请求会发送以下响应：

```java
{
    "SuccessCode": "OPERATION_SUCCESS",
    "Message": "Operation completed successfully"
}
```

在 POST 请求示例中，我们使用***JSONPath***来验证响应正文部分。现在我们必须将此响应主体转换为 Java 类 (POJO)。换句话说，我们将把字符串形式的 JSON 转换为类形式，即反序列化 JSON。

*注意：反序列化也称为结构化数据的**对象表示**。这里的结构化数据是 JSON。*

那么我们如何开始反序列化呢？

首先，我们需要创建一个包含JSON 响应的所有节点*（或键）的类。*正如我们已经知道的，成功响应有两个节点：

-   ***成功代码***
-   ***信息***

这两个节点都包含字符串值。下面的屏幕截图显示了我们收到的部分响应。

![JSON 成功响应](https://toolsqa.com/gallery/Rest%20Assured/1.JSON%20Successful%20response.png)

所以我们必须创建一个包含两个字符串变量的类，它们将代表 JSON 中的节点。下面给出的是相同的类代码。

```java
@Test
public class JSONSuccessResponse { 

// Note: The name should be exactly as the JSON node name 
// Variable SuccessCode will contain value of SuccessCode node 
public String SuccessCode; 

// Variable Message will contain the value of Message node 
public String Message; 
}
```

现在我们有了一个  ***JSONSuccessResponse***类来存储成功响应中存在的所有节点，让我们了解如何使用***Rest-Assured***将 JSON 响应体自动转换为 JSONSuccessResponse 类的实例。

### ***将 JSON 响应正文转换为 JSONSuccessResponse 类***

表示 Rest Assured 中的响应的类***io.restassured.response.ResponseBody有一个名为******.as(Class<T>) 的方法。*** 在这里，*Class`<T>`* 是任何类型 *T*的类的泛型形式，也称为模板类。在这种情况下，Class`<T>`将是 JSONSuccessResponse。

在内部，***“as()”***方法执行两个操作：

-   *创建**JSONSuccessResponse**类的实例。*
-   *将 JSON 节点变量复制到类的相应实例变量中。例如，**SuccessCode**节点的值到变量**SucessCode**和 Message 的值到变量 Message。*

下面给出的是演示***Response.Body.as(Class `<T>`)*** 方法的代码。

```java
@Test
public void UserRegistrationSuccessful() { 
RestAssured.baseURI ="https://demoqa.com"; 
RequestSpecification request = RestAssured.given(); 
JSONObject requestParams = new JSONObject(); 
requestParams.put("UserName", "test_rest"); 
requestParams.put("Password", "rest@123"); 
request.body(requestParams.toJSONString()); 
Response response = request.post("/Account/v1/User"); 
ResponseBody body = response.getBody(); 
// Deserialize the Response body into JSONSuccessResponse 
JSONSuccessResponse responseBody = body.as(JSONSuccessResponse.class); 
// Use the JSONSuccessResponseclass instance to Assert the values of Response. 
Assert.assertEquals("OPERATION_SUCCESS", responseBody.SuccessCode); 
Assert.assertEquals("Operation completed successfully", responseBody.Message); }
```

一旦我们获得 responseBody 变量中的值，我们就可以验证响应，如下面的代码所示。

```java
	// Use the RegistrationSuccessResponse class instance to Assert the values of 
	// Response.
	Assert.assertEquals("OPERATION_SUCCESS", responseBody.SuccessCode);
	Assert.assertEquals("Operation completed successfully", responseBody.Message);
```

通过这种方式，我们可以应用断言来验证响应，甚至可以将此响应作为输入传递给其他测试。

## 如何根据响应状态反序列化 JSON 响应体？

在上一节中，我们讨论了如何在响应成功的情况下反序列化 JSON。但在实时应用程序中，我们也可以收到失败的响应。在这种情况下，Rest API 可能会返回完全不同的响应体。一种这样的失败响应格式可能如下所示：

```java
{
    "FaultId": "User already exists",
    "fault": "FAULT_USER_ALREADY_EXISTS"
}
```

如果我们使用***JSONSuccessResponse***类来反序列化上面的响应，它是行不通的。这是因为 Rest Assured 不会像上一节那样在响应正文中找到节点***SuccessCode***和***Message 。***类中的这两个变量的值为 null。

对此的解决方案是维护另一个将用于反序列化故障响应的类。此类将具有以下结构。

```java
public class JSONFailureResponse {
	String FaultId;
	String fault;
}
```

但是现在我们有两个类，一个用于成功响应，另一个用于失败，我们如何在应用程序中处理这两个类？

我们可以使用服务器返回的[***HTTP 状态码来做到这一点。***](https://www.toolsqa.com/client-server/http-response/)本系列中的 Rest API 在***成功***的情况下返回***Status Code = 201 ，在******失败***的情况下返回***200 。***

因此，通过使用状态码，我们可以根据成功或失败将响应反序列化为适当的 POJO 类。下面给出的代码是上面代码的更新版本，它负责成功和失败响应。

```java
@Test
public void UserRegistrationSuccessful() {
RestAssured.baseURI ="https://demoqa.com";
RequestSpecification request = RestAssured.given();
JSONObject requestParams = new JSONObject();
requestParams.put("UserName", "test_rest");
requestParams.put("Password", "rest@123");
request.body(requestParams.toJSONString());
Response response = request.post("/Account/v1/User");
ResponseBody body = response.getBody();
System.out.println(response.body().asString());
if(response.statusCode() == 200) { 

// Deserialize the Response body into JSONFailureResponse 
JSONFailureResponse responseBody = body.as(JSONFailureResponse.class); 

// Use the JSONFailureResponse class instance to Assert the values of Response. 
Assert.assertEquals("User already exists", responseBody.FaultId);
Assert.assertEquals("FAULT_USER_ALREADY_EXISTS", responseBody.fault); 
} else if (response.statusCode() == 201) { 

// Deserialize the Response body into JSONSuccessResponse
JSONSuccessResponse responseBody = body.as(JSONSuccessResponse.class);

// Use the JSONSuccessResponse class instance to Assert the values of response. 
Assert.assertEquals("OPERATION_SUCCESS", responseBody.SuccessCode); 
Assert.assertEquals("Operation completed successfully", responseBody.Message); 
} 
}
```

在上面的代码中，我们根据状态码是成功还是失败，反序列化响应到 JSONSuccessResponse 或 JSONFailureResponse 类。

*注意：反序列化多个响应的另一种方法是使用继承链，读者可以将其作为练习来实现。*

## 关键要点

在本文中，我们讨论了将 JSON 响应反序列化为 POJO 类。

-   *反序列化是将 JSON 响应转换为类的过程。通过这样做，我们可以将 JSON 字符串转换为不易出错的强类型字符串。*
-   *为了反序列化响应，我们创建了一个单独的类，该类具有与 JSON 响应中相同的变量，例如 StatusCode 和 Message。*
-   *在代码中，我们可以使用这个类对象来读取 JSON 响应，如上例所示。*
-   *成功和失败等多个响应的反序列化取决于状态码。为此，我们需要两个 POJO 类，分别用于成功和失败。根据状态码的值，我们可以调用适当类的实例并反序列化响应。*