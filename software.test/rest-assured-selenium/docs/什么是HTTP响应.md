## 使用 Rest Assured 验证 HTTP 响应状态

HTTP 响应对象通常表示 Web 服务服务器为响应客户端请求而发回的 HTTP 数据包（响应数据包）。HTTP 响应包含：

1.  一种状态。
2.  标题的集合。
3.  身体。

我们在[**这里有一篇关于 HTTP 响应的详细文章。**](https://www.toolsqa.com/client-server/http-response/)

因此，当我们说需要验证 HTTP 响应状态时，我们期待有一种机制来读取和验证整个响应对象，包括状态、标头和正文。因此，我们将分别验证每个 HTTP 响应组件。所以在本文中，HTTP响应状态的验证将分三个部分来处理：

-   验证 HTTP 响应状态代码。
-   如何验证错误状态代码
-   验证响应状态行。

正如我们已经知道的，相同的 REST API 返回 XML 或 JSON 格式的响应消息。此格式取决于 HTTP 请求中的**Media-Type** 属性。

但是，客户端如何知道它将从 API 获得什么类型的响应呢？好吧，这是由响应标头管理的。Response Header 包含一个**Content-Type**属性，用于通知响应正文格式的类型。

考虑我们在之前的文章中讨论的 Swagger UI 示例。假设我们通过浏览器向书店发送 GET 请求，如下所示：

java curl -X GET "https://demoqa.com/BookStore/v1/Books" -H "accept: application/json"

当上述命令执行时，我们会得到如下屏幕所示的响应：

![HTTP响应](https://toolsqa.com/gallery/Rest%20Assured/1.HTTP-Response.png)

从上面的屏幕截图中可以看出，响应具有状态、标题和正文。如果我们检查*“响应标头”*部分，在上面的屏幕中，它有一个内容类型属性，该属性具有与其他属性一起的值。在验证此标头时，客户端知道我们可以期待什么类型的响应（正文）。

现在让我们继续验证响应的状态部分。

### **如何验证 HTTP 响应状态码**

当客户端向服务器请求一条特定信息时，服务器将带有状态代码的响应发送回客户端。服务器返回的状态码告诉我们请求是否成功。如果请求成功，服务器会发送 200-299 范围内的状态码。如果请求不成功，则返回范围以外的状态码。[**我们可以在W3 页面**](https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html)上获取 HTTP 状态代码列表及其描述。

Rest Assured 库提供了一个名为*“io.restassured.response”*的包，它有一个 Response 接口。Response 接口提供的方法可以帮助获取接收到的响应的一部分。下面的截图展示了**响应**接口的一些重要方法。

![响应接口方法](https://toolsqa.com/gallery/Rest%20Assured/2.Response-Interface-Methods.png)

方法 * *getStatusCode()* 用于获取响应的状态码。这个方法返回一个整数，然后我们可以验证它的值。[*TestNG Assert* *](https://www.original.toolsqa.com/testng-asserts/)用于验证状态码。现在考虑下面给出的代码：

java导入静态org.junit.Assert.*；导入 org.testng.Assert；//用于验证响应状态 import org.testng.annotations.Test; 导入 io.restassured.RestAssured；导入 io.restassured.response.Response；导入 io.restassured.specification.RequestSpecification；

public class RestAssuredTestResponse { @Test public void GetBookDetails() {
// 指定 RESTful Web 服务的基本 URL RestAssured.baseURI = "https://demoqa.com/BookStore/v1/Books"; // 获取要发送到服务器的请求的RequestSpecification RequestSpecification httpRequest = RestAssured.given();

```
       Response response = httpRequest.get("");

       // Get the status code of the request. 
       //If request is successful, status code will be 200
      int statusCode = response.getStatusCode();

       // Assert that correct status code is returned.
      Assert.assertEquals(statusCode /*actual value*/, 200 /*expected value*/, 
        "Correct status code returned");

}
```

}

下面的代码行从消息中提取状态代码：

java int statusCode = response.getStatusCode();

返回值*“statusCode”*与预期值（即 200）进行比较。如果两个值相等，则返回适当的消息。

java // 断言返回了正确的状态码。Assert.assertEquals(statusCode /*实际值*/, 200 /*期望值*/, "返回正确的状态码");

如果您运行上述测试，您将看到测试通过，因为 Web 服务返回状态代码为 200，如下图所示。

![响应状态测试NG输出](https://toolsqa.com/gallery/Rest%20Assured/3,Response-Status-TestNG-output.png)

这样，我们就可以使用响应接口的*“getStatusCode()”方法来验证响应的状态码。*请注意，由于我们已经知道这里的成功代码是 200，因此我们编写了相应的代码。您的服务器可能会以 200 到 299 之间的任何代码作为成功响应。最好事先检查一下。现在让我们继续讨论如何验证返回 200 以外的值的状态代码，即错误状态代码。

### **如何验证 HTTP 错误状态码？**

到目前为止，请求-响应情况都很好，我们只收到了 200 个表示请求成功的状态码。但这在现实世界中可能并不总是正确的。原因可能是服务器已关闭或 REST API 无法正常运行，或者请求本身可能存在问题。综上所述，我们可能会面临以下几种情况：

1.  托管 REST API 的服务器已关闭。
2.  客户请求不正确。
3.  客户端请求的资源不存在。
4.  在处理请求的过程中，服务器端发生错误。

因此，当上述任何一种情况发生时，REST API 将返回一个除 200 之外的适当状态代码。反过来，客户端必须验证此状态代码并相应地处理它。

对于 ToolsQA Book Store 服务，让我们创建另一个带有错误场景的测试。这里我们将验证输入无效参数时书店 Web 服务返回的 HTTP 状态码。

所以这里我们提供了获取用户详细信息的参数。这里我们提供不存在的 userId 作为参数。代码如下所示：

java导入静态org.junit.Assert.*；导入 org.testng.Assert；//用于验证响应状态 import org.testng.annotations.Test; 导入 io.restassured.RestAssured；导入 io.restassured.response.Response；导入 io.restassured.specification.RequestSpecification；

公共类 RestAssuredTestResponse {

```
@Test
public void GetPetDetails()
{  
   // Specify the base URL to the RESTful web service
       RestAssured.baseURI = "https://demoqa.com/Account/v1/User/";
       // Get the RequestSpecification of the request to be sent to the server
       RequestSpecification httpRequest = RestAssured.given();

       Response response = httpRequest.get("test");

       // Get the status code of the request. 
       //If request is successful, status code will be 200
       int statusCode = response.getStatusCode();

       // Assert that correct status code is returned.
       Assert.assertEquals(statusCode /*actual value*/, 200 /*expected value*/, 
       "Correct status code returned");
}
```

}

当我们运行这个测试时，它返回 401 的错误代码。我们可以在下面看到测试执行的结果。

![错误状态输出](https://toolsqa.com/gallery/Rest%20Assured/4.Error-status-output.png)

注意：我们可以快速更改上面的代码以确保测试通过。此更改如下所示：

java Assert.assertEquals(statusCode /*实际值*/, 401 /*预期值*/, "返回正确的状态码");

所以这里我们期望返回的值是 401 而不是 200，因此测试通过了。接下来，我们将验证*“状态行”*。

### **如何验证响应状态行？**

“*状态行”*是 HTTP 响应中返回的第一行。状态行由三个子字符串组成：

-   HTTP 协议版本。
-   状态码。
-   状态码的字符串值。

例如，当请求成功时，状态行的值将是*“HTTP/1.1 200 OK”。*这里，第一部分是 HTTP 协议*（HTTP/1.1）。* 接下来是 HTTP 状态代码*(200)。*第三个是状态消息 *（OK）。*

我们可以使用响应接口的getStatusLine()方法读取整个状态行。以下代码显示了演示。

java @Test public void GetBookDetails() {
// 指定 RESTful Web 服务的基本 URL RestAssured.baseURI = "https://demoqa.com/BookStore/v1/Books"; // 获取要发送到服务器的请求的RequestSpecification RequestSpecification httpRequest = RestAssured.given(); 响应响应 = httpRequest.get("");

```
// Get the status line from the Response in a variable called statusLine
String statusLine = response.getStatusLine();
Assert.assertEquals(statusLine /*actual value*/, "HTTP/1.1 200 OK" 
  /*expected value*/, "Correct status code returned");
```

}

在这里，我们执行与状态代码类似的测试。我们使用 getStatusLine() 方法将状态行读入一个字符串值。然后我们将此返回值与*“HTTP/1.1 200 OK”*进行比较，以检查状态是否成功。

注意：关于同一主题的邮递员文章（邮递员响应）可以在邮递员  [响应中找到。](https://www.toolsqa.com/postman/response-in-postman/)

[在 Rest Assured](https://www.youtube.com/watch?v=uD4sKHzWf8Q)中验证状态码中提供了验证状态码的视频教程。

## 关键要点

在本文中，我们讨论了 REST 响应中的 HTTP 状态验证。

1.  从服务器获得的响应由状态、标头和正文组成。
2.  响应的状态依次包含状态代码和状态字符串。
3.  Rest Assured 库提供了一个“响应”接口，该接口提供了多种方法来提取响应字段。
4.  我们可以使用 getStatusCode() 方法读取状态码。同样，我们可以使用响应接口的getStatusLine()方法读取状态行。
5.  读取状态后，我们可以验证代码是成功（200）还是任何其他代码。