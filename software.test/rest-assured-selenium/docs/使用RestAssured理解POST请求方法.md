## 什么是 HTTP Post 请求方法？

当我们处理网络时，我们到处都使用动词“***发布”。***例如，当我们在 Gmail 等特定网页上提交任何注册表单时。我们提供所需的数据并单击提交。所以通过这个提交数据的动作，我们其实是在POSTING或者发送数据到服务器。HTTP 中的动词“ ***POST*** ”，也称为 HTTP 中的 POST 请求方法，向服务器发送数据。在我们的示例中，我们将姓名、电子邮件和确定的密码作为数据发送。

现在让我们转到 API [***https://demoqa.com/BookStore/v1/Books***](https://demoqa.com/BookStore/v1/Books)的 Swagger UI 接下来，让我们使用 POST 请求创建一个新用户，如下面的屏幕截图所示。

![使用 Swagger 用户界面发布示例请求](https://toolsqa.com/gallery/Rest%20Assured/1.POST%20Example%20Request%20using%20Swagger%20user%20interface.png)

在上面的示例中，我们提供了新用户的所有详细信息，包括 id 和 ISBN。当我们“***执行***”请求时，我们发起一个 POST 请求并将所有信息发送到服务器。在服务器端，将使用所有提供的信息创建新用户，并将响应发送回客户端。看下面的截图。

![发布请求输出](https://toolsqa.com/gallery/Rest%20Assured/2.Post%20request%20output.png)

查看带有所有数据的 POST 请求的“ ***curl ”命令。***操作成功，因此我们得到响应代码为 201。

![发布请求输出](https://toolsqa.com/gallery/Rest%20Assured/3.Post%20request%20output.png)

上例中的 content-type 参数为“ ***application/JSON*** ”。通过 POST 请求发送到服务器的数据也可以是 XML（*内容类型：应用程序/XML*）。此数据位于 HTTP 请求的正文中。正如我们从上面的示例中了解到的，POST 方法通常会在应用程序数据库中创建一条新记录。虽然，这并不总是正确的。有时在创建 Web 应用程序时，我们使用 POST 请求重定向来确保没有人在没有适当导航通道的情况下直接点击该 URL。例如，您一定注意到在支付页面上刷新或按“***返回***”并不能直接起作用。这是安全的，因为正在发送的数据在 URL 字符串中不可见。数据也可以使用 HTTPS（*HTTP Secure*) 以增强安全性。

就实际应用程序而言，POST 请求的大小可能非常大，而且主体结构也很复杂。由于它提供了额外的安全层，我们经常使用它来传递与业务相关的敏感数据。这里还值得注意的是，POST 请求并不总是要求用户填写所有数据。它完全取决于服务器实现，因此您应该始终阅读文档。

现在让我们使用 Rest Assured 来模拟 HTTP POST 请求。

## 如何通过 Rest Assured 使用 POST 请求方法？

***在 Rest Assured 中，我们使用 post() 方法发出 HTTP POST 请求。***

为了使用 Rest Assured 发出 HTTP Post 请求，让我们在类路径中添加一个简单的 JSON 库，以便我们可以在代码中创建 JSON 对象。我们可以使用以下 URL 从 Maven 下载简单的 JSON：[***https***](https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple) ://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple 。下载 jar 后，我们可以将其添加到类路径中。

以下是我们使用 Rest Assured 发出 POST 请求的步骤。

1.  ***创建一个指向服务端点的请求。***
2.  ***创建一个包含所有字段的 JSON 请求。***
3.  ***在请求中添加 JSON 正文并发送请求。***
4.  ***验证请求。***
5.  ***更改 POST 请求的 HTTP 方法。***

现在让我们一步一步执行。

### ***创建一个指向服务端点的请求***

一旦 JSON jar 下载，我们就可以开始编码。

考虑以下代码行。

```java
RestAssured.baseURI = "https://demoqa.com/BookStore/v1/Books"; 
RequestSpecification request = RestAssured.given();
```

在上面的代码中，我们初始化了一个带有书店链接和“createUser”API 的基本 URI。接下来，我们使用 RequestSpecification 创建一个“请求”。

*注意：上面的代码是不言自明的。如有任何问题，请参考之前的教程。*

### ***创建一个包含所有字段的 JSON 请求***

接下来，我们将创建一个 JSON 请求对象，其中包含创建新用户所需的数据。下面给出的是相同的代码：

```java
// JSONObject is a class that represents a Simple JSON. 
// We can add Key - Value pairs using the put method 
JSONObject requestParams = new JSONObject(); 
requestParams.put("userId", "TQ123"); 
requestParams.put("isbn", "9781449325862"); 
```

在上面的代码中，我们创建了一个***JSONObject***变量（*JSONObject 属于 org.json.simple 包*）。在这里，我们提供了一个 JSON 字符串，其中包含要发布到服务器的数据。这里我们有上面的***requestParams*** **对象，用于我们的测试 Web 服务，在*****JSON***中有多个节点。***使用JSONObject.put*** ( *String, String* ) 方法将每个节点添加到 JSON 字符串。以这种方式添加所有节点后，我们 通过调用 *JSONObject.toJSONString()* 方法获得***JSONObject的 String 表示。***

### ***在请求中添加 JSON 正文并发送请求***

现在我们已经创建了包含所需数据的 JSON 字符串，下一步是将此 JSON 添加到请求正文并发送或发布请求。看下面的代码：

```java
// Add a header stating the Request body is a JSON 
request.header("Content-Type", "application/json"); // Add the Json to the body of the request 
request.body(requestParams.toJSONString()); // Post the request and check the response
```

因此，在这一步中，我们只需将***JSON*** 字符串添加到 ***HTTP 请求***的正文中，并将***Content-Type***标头字段值设置为***application/JSON***。接下来，我们使用方法***RequestSpecification.body*** ( *JsonString* ) 将 JSON 正文放入请求中。使用这种方法，我们可以更新***HTTP 请求*** 正文的内容。

接下来使用请求对象上的 post() 方法，我们使用“BookStoreV1BooksPost”API 将此数据发送到服务器。

*注意：多次调用**RequestSpecification.body方法每次都会将 body 更新为最新的 JSON String。***

### ***验证响应***

发布请求后，我们必须验证作为 POST 请求的结果从服务器收到的响应。下面给出了验证响应的代码：

```java
Response response = request.post("/BookStoreV1BooksPost"); 
System.out.println("The status received: " + response.statusLine());
```

这里我们读取使用*statusLine()*方法获得的状态并将其打印到控制台。

## ***使用 Rest Assured 更改 POST 请求的 HTTP 方法***

那么当我们更改 POST 请求的 HTTP 请求方法时会发生什么？例如，当我们发送 GET 而不是预期的 POST 时会发生什么？让我们讨论一下这个场景。

以下是我们在 Endpoint 实际需要 POST 时向 Endpoint 发送 GET 请求的代码。

```java
public void UserRegistrationSuccessful() 
{ 
    RestAssured.baseURI ="https://demoqa.com/Account/v1"; 
    RequestSpecification request = RestAssured.given(); 
    JSONObject requestParams = new JSONObject();
    requestParams.put("userName", "test_rest");
    requestParams.put("password", "Testrest@123"); 
    request.body(requestParams.toJSONString());
    Response response = request.put("/User"); 
    ResponseBody body = response.getBody();
    System.out.println(response.getStatusLine());
    System.out.println(body.asString());
     
}
```

当我们执行上述代码时，我们得到以下响应。

![如何使用 Rest Assured 更改 POST 请求中的 HTTP 方法](https://toolsqa.com/gallery/Rest%20Assured/4.How%20to%20change%20a%20HTTP%20method%20in%20POST%20request%20using%20Rest%20Assured.png)

我们可以清楚地看到输出表明 HTTP 请求方法的使用不正确。同样，我们在下面列出了其他负面情况，我们将留给用户自己尝试。

-   *发送不完整的 POST 数据。*
-   *尝试使用不正确的语法发送 JSON 数据。*
-   *在请求中发送不正确的 HTTP 请求方法。*

您可以在上面用于演示 POST 请求的相同 URL 上尝试上述场景。

***注意**：对应 Post 请求的 Postman 教程可以在[**Response in Postman**](https://www.toolsqa.com/postman/response-in-postman/)找到。*

*此主题的视频教程可在[**什么是发布请求？**](https://www.youtube.com/watch?v=YHWngWsmcWg)*

## 关键要点

在本文中，我们讨论了使用 Rest Assured 的 HTTP POST 请求方法。

-   *Post 请求用于将数据发送或发布到服务器。*
-   *发布请求主要导致在数据库中创建新记录。它还可以更新数据库中的现有记录。*
-   *Rest Assured 使用 post() 方法发出 HTTP POST 请求。*
-   *我们通常将 JSON 数据与请求对象一起发送，然后将其 POST 到服务器。*