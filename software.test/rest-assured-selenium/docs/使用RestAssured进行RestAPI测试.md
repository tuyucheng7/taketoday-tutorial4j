## 什么是 REST API 测试？

[***Rest***](http://www.toolsqa.com/rest-assured/what-is-rest/)代表 Representational State Transfer，是一种用于与 Web 服务通信的架构风格。***API 或应用程序编程接口***是一组用于访问基于 Web 的软件应用程序的编程指令。使用 REST 构建的 API 称为[***REST API***](https://toolsqa.com/restassured/what-is-rest-api)，我们在之前的文章中已经讨论过。现在让我们开始了解 REST API 测试。

那么什么是 REST API 测试呢？

REST API 测试是一种测试 RESTful API 并验证其正确性的技术。我们发送请求（*最好使用自动化*）并记录响应以进行进一步的断言。这样我们就可以检查 REST API 是否工作正常。REST API 测试主要使用四种 REST 方法，即 GET、POST、PUT、DELETE。

有两种方法可以测试 REST API：

1.  *手动测试：类似于应用程序的任何其他手动测试*
2.  *自动化测试：使用可以访问 REST API 的脚本或程序。*

但是，在本教程中，我们将只专注于自动化 REST API 测试。

## REST API 自动化测试的 HTTP 方法和状态码

我们知道 REST API 使用五种 HTTP 方法来请求命令：

| ***方法*** | ***描述***                                                  |
| ---------- | ----------------------------------------------------------- |
| **得到**   | 检索特定 URL 处的信息。                                     |
| ***放***   | 如果之前的资源存在，则更新它，或者在特定 URL 处创建新信息。 |
| **邮政**   | 用于向服务器发送信息，例如上传数据，也用于开发新实体。      |
| **删除**   | 删除特定 URL 处的所有当前表示。                             |
| **修补**   | 这用于资源的部分更新。                                      |

使用上述方法发送请求后，客户端会收到称为***“状态代码”***或有时称为***“响应代码”***的数字代码。然后我们可以解释这些状态码，以了解服务器为特定请求发送了什么样的响应。状态码主要分为五类，如下表所示。

| ***不*** | ***状态码***    | ***描述***                       |
| -------- | --------------- | -------------------------------- |
| 1        | 1xx (100 – 199) | 响应是信息性的。                 |
| 2        | 2xx (200 – 299) | 确保成功响应。                   |
| 3        | 3xx (300 – 399) | 您需要采取进一步行动来满足请求。 |
| 4        | 4xx (400 – 499) | 语法错误，无法完成请求。         |
| 5        | 5xx (500 – 599) | 服务器完全无法完成请求。         |

上面的代码帮助我们解释 HTTP 请求的结果。从上表我们可以推断，如果响应状态码为 2xx，则表示应用程序正在正常运行。状态代码 1xx、2xx、3xx 不被视为错误，而是信息性消息，这些代码不会影响用户体验。

但是，如果我们得到像 4xx 和 5xx 这样的状态代码，这些都是错误消息。这意味着用户在浏览 API 时会遇到错误消息。客户端或浏览器级别的错误大多会导致 4xx 状态代码错误消息。而服务器级错误会导致 5xx 状态代码错误消息。因此，在执行 REST API 测试时，我们应该通过检查错误代码来评估每个响应。

请考虑以下 REST API 示例 URL，[***https://demoqa.com/swagger/#/BookStore。***](https://demoqa.com/swagger/#/BookStore)这是一个书店库存，它为我们提供了各种 REST API 方法来访问书店中的信息。现在让我们在浏览器中访问这个 URL，我们会看到以下屏幕。

![bookstore_api_bookstore_home](https://toolsqa.com/gallery/Rest%20Assured/1.bookstore_api_bookstore_home.png)

点击上述商店中的GET方法*（第一个）*访问宠物库存。当我们单击并执行 GET 方法时，我们得到以下响应。

![休息API测试](https://toolsqa.com/gallery/Rest%20Assured/2.Rest%20API%20test.png)

注意上面屏幕（左面板）上的状态代码。它是 200，这意味着请求已成功执行，并且我们得到了成功的响应。这样，当我们发送请求时，我们会从服务器接收到一个状态码，然后我们可以解释这个状态码并检查请求是否以正常方式执行或发生了一些错误。我们将在接下来的文章中进一步探讨从服务器获得的响应。

## 使用 Rest Assured 进行 REST API 测试

[***REST Assured***](https://toolsqa.com/rest-assured/rest-assured-library/)是一个用于测试 RESTful API 的 Java 库。它广泛用于测试基于 JSON 和 XML 的 Web 应用程序。此外，它完全支持所有 REST 方法，如 GET、PUT、POST、PATCH 和 DELETE。接下来，我们将看到使用 Rest Assured 库测试一个 REST API 的详细演练。

### ***如何使用 Rest Assured 编写 REST API 测试？***

要编写示例 REST API 测试，我们将使用以下 REST API 链接。

|           |                                                              |
| --------- | ------------------------------------------------------------ |
| 请求网址  | [**https://demoqa.com/BookStore/v1/Books**](https://demoqa.com/BookStore/v1/Books) |
| HTTP 方法 | ***得到***                                                   |
| 注释      | 此 URL 将返回书店的库存详细信息。请求没有输入参数。          |
| 回复      | {“书”：[{“isbn”：“字符串”，“标题”：“字符串”，“子标题”：“字符串”，“作者”：“字符串”，“publish_date”：“2022-01-25T13： 44:50.276Z","publisher":"string","pages":0,"description":"string","website":"string"}]} |

实际上，如果我们直接在浏览器中打开上面的 URL，我们会得到如下输出：![bookstore_get_raw_output](https://toolsqa.com/gallery/Rest%20Assured/3.bookstore_get_raw_output.png)

要使用 Rest Assured 库以编程方式获得相同的输出，我们必须遵循以下步骤：

1.  *使用 RestAssured 类为 URL 生成 RequestSpecification： [https ://demoqa.com/BookStore/v1/Books](https://demoqa.com/BookStore/v1/Books)*
2.  *指定 HTTP 方法类型（GET 方法）。*
3.  *将请求发送到服务器。*
4.  *从服务器获取响应。*
5.  *打印返回的响应正文。*

下面给出了上述步骤的完整代码：

```java
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
 
public class RestAssuredAPITest {
 
 @Test
public void GetBooksDetails() { 
	// Specify the base URL to the RESTful web service 
	RestAssured.baseURI = "https://demoqa.com/BookStore/v1/Books"; 
	// Get the RequestSpecification of the request to be sent to the server. 
	RequestSpecification httpRequest = RestAssured.given(); 
	// specify the method type (GET) and the parameters if any. 
	//In this case the request does not take any parameters 
	Response response = httpRequest.request(Method.GET, "");
	// Print the status and message body of the response received from the server 
	System.out.println("Status received => " + response.getStatusLine()); 
	System.out.println("Response=>" + response.prettyPrint());
    	
}
}
```

上面的代码生成的响应与我们之前在浏览器中得到的响应相同。以下屏幕截图显示了响应。

![休息 API 测试输出](https://toolsqa.com/gallery/Rest%20Assured/4.Rest%20API%20Test%20output.png)

通过这种方式，我们可以进行任何测试 API 调用，并从托管 RestFul 服务的网络服务器获取响应。

### ***了解 REST API 测试的放心代码***

现在让我们来看看我们上面实现的代码。

#### ***代码行 1***

```java
// Specify the base URL to the RESTful web service 
RestAssured.baseURI = "https://demoqa.com/BookStore/v1/Books";
```

上面的行使用***RestAssured***类来设置一个基本 URI。在这种情况下，基本 URI 是***“https://demoqa.com/BookStore/v1/Books”。***基本 URI 表示我们要从服务器请求的资源的根地址（因此称为基本 URI）。然后我们将在后续代码中实际发出请求时添加参数（如果有）。

***io.restassured.RestAssured***类是我们为测试发出的任何类型的 HTTP 请求的基础。这个类的一些主要特点是：

-   *它使用基本 URI 生成 HTTP 请求。*
-   *支持为不同的 HTTP 方法类型（GET、POST、PUT、PATCH、DELETE、UPDATE、HEAD 和 OPTIONS）创建请求。*
-   *它使用 HTTP 与服务器通信，并将测试中创建的请求发送到服务器。*
-   *接收来自服务器的响应。*
-   *提供对验证从服务器接收到的响应的支持。*

io.restassured.RestAssured 类在内部使用[***HTTP 构建器库，***](https://github.com/jgritman/httpbuilder)它是基于 Groovy 语言的 HTTP 客户端。

#### ***代码行 2***

```java
// Get the RequestSpecification of the request to be sent to the server. 
RequestSpecification httpRequest = RestAssured.given();
```

下一行获取要发送到服务器的请求的***RequestSpecification 。***Rest Assured 库为此提供了一个名为***RequestSpecification的接口。***变量***httpRequest***存储请求，以便我们可以在需要时对其进行修改，例如添加身份验证详细信息、添加标头等。对于这个特定的测试，我们不会修改变量。

#### ***代码行 3***

```java
// specify the method type (GET) and the parameters if any. 
//In this case the request does not take any parameters 
Response response = httpRequest.request(Method.GET, "");
```

***现在我们使用RequestSpecification*** 对象调用服务器来获取资源。上面的代码行使用 request 方法向服务器发送资源请求。

request 方法有两个参数，第一个是 HTTP 方法，第二个是字符串。字符串参数用于指定要与基本 URI 一起发送的参数。在这种情况下，为了获取宠物商店的详细信息，我们不发送任何参数，因此是空白字符串。请求方法的返回类型是***Response***对象，这意味着请求方法从服务器获取响应。

Response 接口 (io.restassured.response.Response) 表示从服务器返回的响应。它包含服务器发送的所有数据。正如我们将在后续文章中看到的，我们可以在这个响应对象上调用不同的方法来提取响应状态、标头等响应。

#### ***代码行 4 和 5***

```java
// Print the message body of the response received from the server 
System.out.println("Status received => " + response.getStatusLine()); 
System.out.println("Response=>" + response.prettyPrint());
```

在上面的代码行中，我们只是将响应读取为字符串并将其打印到控制台。我们使用响应接口的 getBody 方法返回响应的实际正文。然后将其打印到控制台。

我们也可以使用 Rest Assured 提供的速记方法编写上述测试代码。以下是稍微缩短的代码片段。

```java
	@Test
	public void GetWeatherDetailsCondensed()
	{
		// Specify the base URL to the RESTful web service
		RestAssured.baseURI = "https://demoqa.com/BookStore/v1/Books";

		// Get the RequestSpecification of the request that is to be sent
		// to the server.
		RequestSpecification httpRequest = RestAssured.given();

		// Call RequestSpecification.get() method to get the response.
		// Make sure you specify the resource name.
		Response response = httpRequest.get("");

		// Response.asString method will directly return the content of the body
		// as String.
		System.out.println("Response Body is =>  " + response.asString());
	}
```

所以这里我们在返回Response对象的***RequestSpecification***对象上使用“get”方法。

***当我们详细介绍io.restassured.response.Response*** 接口时，我们将在下一篇文章中引用相同的示例。

注意：*关于同一主题（API 测试和 GET 请求）的 postman 文章可以[**在 Postman 中的新请求和 Postman**](https://github.com/jgritman/httpbuilder)中的[**获取请求中找到。**](https://www.toolsqa.com/postman/get-request-in-postman/)*

*访问 [**Get Request in Rest Assured**](https://www.youtube.com/watch?v=wgMCVUkZ_Fc)以获取视频教程。*

## 关键要点

在本文中，我们讨论了：

1.  *RestFul Web 应用程序使用 Rest API 测试技术。*
2.  *我们还了解了各种 HTTP 方法和状态码。*
3.  *然后我们使用 Rest Assured Library 放置了一个基本的测试 API 调用并分析了它的输出。*
4.  *我们还浏览了测试 API 代码以更好地理解实现。*