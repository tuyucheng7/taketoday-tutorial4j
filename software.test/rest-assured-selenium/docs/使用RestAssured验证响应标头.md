## 什么是 REST API 中的 HTTP 响应标头？

从服务器接收到的响应由零个或多个标头以及响应状态和响应正文组成。每个标头都是一个键值对。服务器使用响应的标头部分来发送额外的信息，这些信息也称为响应的“***元数据***”。

例如，标头包含一个“ ***Content-Type*** ”属性，它告诉我们如何解释响应正文的数据。因此，如果响应正文包含 JSON 数据，则标头中对应的 content-type 属性将为“ ***application/json*** ”。*同样，如果正文中的数据是 XML，则 **Content-Type**标头将为“ **application/xml** ”*。

例如，对于以下请求 URL：[***https ://demoqa.com/BookStore/v1/Books***](https://demoqa.com/BookStore/v1/Books)

当我们发出以下 GET 请求时：

```java
curl -X GET "https://demoqa.com/BookStore/v1/Books" -H "accept: application/json"
```

我们得到以下响应。

![响应标头示例](https://toolsqa.com/gallery/Rest%20Assured/1.Response%20Header%20example.png)

请注意获得的响应标头（*红色矩形*）。由于正文是 JSON，因此***Content-Type***设置为“ ***application/JSON*** ”。

## 用于验证 HTTP 响应标头的放心方法

Rest Assured 库的 Response 接口提供了访问所有标头或单个标头的方法。只需在 Eclipse（*或任何此类编辑器*）中键入“ ***Response.head*** ”即可显示所有支持访问标头的方法。

![响应头方法](https://toolsqa.com/gallery/Rest%20Assured/2.Response%20Header%20methods.png)

如上图所示，REST Assured 的 Response 接口提供了与 headers 相关的方法。

-   ***headers()*** ：返回 ***标头***
-   ***getHeader()***：返回一个***标题***
-   ***getHeaders()***：返回 ***标头***

当返回响应中的所有标头时，我们可以通过简单地迭代每个标头来打印每个标头。

*注意：标头的集合由一个名为**io.restassured**的类表示。HTTP.Headers。此类实现**Iterable** 接口。因此，我们可以使用“ **for each (for(: ))** ”循环来读取所有标题。*

## 如何使用 REST Assured 访问和读取 HTTP 响应标头？

现在让我们看看如何使用 Rest-Assured 读取 Header。让我们编写一个测试来记录响应中的以下 Header Types：

-   *内容类型。*
-   *服务器。*
-   *内容编码。*

下面显示的是此测试的代码：

```java
@Test 
public void IteratingHeaders() 
{ RestAssured.baseURI = "https://demoqa.com/BookStore/v1/Books"; 
 RequestSpecification httpRequest = RestAssured.given(); 
 Response response = httpRequest.get(""); 
 // Get all the headers and then iterate over allHeaders to print each header 
 Headers allHeaders = response.headers(); 
 // Iterate over all the Headers 
 for(Header header : allHeaders) { 
   System.out.println("Key: " + header.getName() + " Value: " + header.getValue()); 
 } 
}
```

在上面的代码中，我们访问所有标题，然后通过循环访问 Headers 集合来提取单个标题。下面显示的是上述测试的控制台输出。

![响应标头键值对](https://toolsqa.com/gallery/Rest%20Assured/3.Response%20Header%20key%20value%20pairs.png)

上面的输出显示了我们在代码中使用 for 循环显示的所有标题键值对。

让我们演示获取特定标头的***.header*** ( *String arg0 ) 方法。*在这里，我们将确切的***标头名称***作为参数传递。

```java
@Test
public void GetBookHeaders() { 
RestAssured.baseURI = "https://demoqa.com/BookStore/v1/Books";
RequestSpecification httpRequest = RestAssured.given();
Response response = httpRequest.get(""); 
// Access header with a given name. 
String contentType = response.header("Content-Type"); 
System.out.println("Content-Type value: " + contentType); 
// Access header with a given name. 
String serverType = response.header("Server"); 
System.out.println("Server value: " + serverType); 
// Access header with a given name. Header = Content-Encoding 
String acceptLanguage = response.header("Content-Encoding"); 
System.out.println("Content-Encoding: " + acceptLanguage); 
  } 
 } 
}
```

在上面的代码中，我们 通过将相应的标头名称指定为 header() 方法的参数来访问***Content-Type、Server*** *和* ***Content-Encoding标头。***下面显示的是上述代码的控制台输出。

![Rest Assured 响应中的访问标头](https://toolsqa.com/gallery/Rest%20Assured/4.Access%20header%20in%20Rest%20Assured%20response.png)

*注意： Response.GetHeader(String headerName) 方法的行为与 Response.Header(String headerName) 方法完全相同。因此，上面的代码可以通过将“ **.Header()** ”方法替换为“.GetHeader( **)** ”方法来编写。*

## 如何使用 Rest Assured 验证 HTTP 响应标头？

现在我们已经讨论了访问 header 的方法，让我们编写一个测试来验证 header 的值，将其作为 Assert。下面的代码显示了与前面部分中的代码类似的测试，但仅使用 Assert 来验证结果。

```java
@Test
public void ValidateBookHeaders() 
{ 
RestAssured.baseURI = "https://demoqa.com/BookStore/v1/Books";
RequestSpecification httpRequest = RestAssured.given();
Response response = httpRequest.get("");
// Access header with a given name. Header = Content-Type 
String contentType = response.header("Content-Type"); 
Assert.assertEquals(contentType /* actual value */, "application/json; charset=utf-8" /* expected value */); 
// Access header with a given name. Header = Server 
String serverType = response.header("Server"); 
Assert.assertEquals(serverType /* actual value */, "nginx/1.17.10 (Ubuntu)" /* expected value */);
```

在上面的代码中，我们用期望值验证了每个标头的实际值，即 Content-Type、Server 和 Content-Encoding。下图是测试结果的截图。

![使用 rest Assured 验证标头](https://toolsqa.com/gallery/Rest%20Assured/5.Validate%20headers%20using%20rest%20Assured.png)

此输出表明每个标头的实际值和预期值匹配。

现在假设我们在上面的代码中将Content-Type 的期望值提供给“ ***application/XML ”，如下所示。***

Assert.assertEquals(contentType /* 实际值*/, "application/xml" /*期望值 */);

如果我们在更改后运行上述相同的测试，我们将得到以下输出。

![放心的标头验证失败](https://toolsqa.com/gallery/Rest%20Assured/6.Validation%20failed%20for%20headers%20in%20rest%20assured.png)

如我们所见，实际值“ ***application/JSON*** ”和预期值“ ***application/XML*** ”的差异会引发断言错误。

这样通过使用 Response 接口的 header-specific 方法，我们可以在 Rest API 中验证 Response Header。

注意：*可以[**在 Rest Assured 中的 Verify Response Headers 中**](https://www.youtube.com/watch?v=QASNbQFGxVM)找到有关同一主题的视频教程。*

## 关键要点

在本文中，我们讨论了使用 Rest Assured 验证 REST 中的响应标头的方法。

1.  *从服务器获得的每个响应都可能包含零个或多个标头，这些标头提供有关响应的元数据。*
2.  *Rest Assured 提供了各种方法，我们可以使用这些方法读取服务器发送的单个或所有标头。*
3.  *headers() 和 getHeaders() 返回可对单个条目进行迭代的标头集合。方法“ **getHeader()** ”和 header() 返回名称被指定为参数的单个标头。*
4.  *使用这些方法和 TestNG Assert，我们可以通过比较每个标头的实际值和预期值来验证标头。*

在验证响应的状态和标头之后，我们将在下一篇文章中继续阅读实际的响应正文。