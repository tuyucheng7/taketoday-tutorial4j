## URL的组成是什么？

与之前文章中使用的示例类似，我们也将在本文中使用[***Bookstore API 。***](https://demoqa.com/swagger/)考虑以下示例 URL 以获取与书对应的详细信息 -

[***https://demoqa.com/BookStore/v1/Book?ISBN=9781449325862***](https://demoqa.com/BookStore/v1/Book?ISBN=9781449325862) 虽然上面的 URL 看起来很基础，但它仍然让我们了解了它的不同组成部分。

-   ***https://** - 它是一种确保 Web 服务器和 Web 浏览器之间安全连接的协议。*
-   ***https://demoqa.com/swagger/#/BookStore** - 托管网站的域名。它通常以 .com、.in、.net 等结尾。*
-   ***BookStore/v1/Book** - 这是标识请求应用的资源的路径或 URI。*
-   ***?ISBN=9781449325862** - 这是一个字符串查询参数。问号表示查询字符串参数的开始。URL 中可以有一个或多个查询参数。*

现在让我们在下一节中更多地了解查询参数。

## 什么是查询字符串参数？

您可能并不总是希望获取与请求对应的所有结果。在某些情况下，您可能只需要获取几条或一条记录。在这种情况下，查询字符串参数起着重要作用。在使用“？”之后，这些将附加在 URL 的末尾。尝试在浏览器地址栏中输入[***示例 URL***](https://bookstore.toolsqa.com/BookStore/v1/Book?ISBN=9781449325862)并观察 Network -> Payload- 下可用的结果

![查询restassass.png中的字符串参数](https://toolsqa.com/gallery/Rest%20Assured/1.query%20string%20parameter%20in%20rest%20assured.png)

查询参数，*即 ISBN=9781449325862*显示在查询字符串参数字段下。同理，如果 URL 中传递了多个查询参数，您将在该字段中看到所有参数。作为练习，您可以在 Google 搜索中搜索一些关键字并查看附加到 URL 的查询参数。观察搜索关键字显示在 ' *q=your search keyword* ' 之后。*此外，您将观察到通过“ &* ”符号相互分隔的其他查询参数。

这些标志被称为 URL 参数，超出了我们在此讨论的范围。

现在我们已经了解了查询参数是什么以及它们如何在请求中使用，我们可以继续了解如何在 Rest Assured 中使用查询参数发送请求。

## 如何在 Rest Assured 中使用查询参数发送请求？

在本节中，我们将了解如何通过在放心中传递查询参数来自动化我们的测试用例。我们将简单地使用包含上述部分中讨论的查询参数的 URL 来展示我们如何可以放心地使用查询参数发送请求。让我们看看代码的样子，然后我们将逐步完成它。

```java
package bookstore;



import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

public class QueryParam {
	
	@Test
	public void queryParameter() {
		//Defining the base URI
		RestAssured.baseURI= "https://bookstore.toolsqa.com/BookStore/v1";
		RequestSpecification httpRequest = RestAssured.given();
		//Passing the resource details
		Response res = httpRequest.queryParam("ISBN","9781449325862").get("/Book");
		//Retrieving the response body using getBody() method
		ResponseBody body = res.body();
		//Converting the response body to string object
		String rbdy = body.asString();
		//Creating object of JsonPath and passing the string response body as parameter
		JsonPath jpath = new JsonPath(rbdy);
		//Storing publisher name in a string variable
		String title = jpath.getString("title");
		System.out.println("The book title is - "+title);
	}

}
RestAssured.baseURI= "https://bookstore.toolsqa.com/BookStore/v1";
 RequestSpecification httpRequest = RestAssured.given();
```

*我们首先定义基本 URI 以创建对服务端点的请求。*

```java
Response res = httpRequest.queryParam("ISBN","9781449325862").get("/Book");
```

*接下来，我们将书籍 ISBN 等资源详细信息作为查询参数发送，以使用 GET 请求在书籍中搜索。*

***注意**：如果您需要发送多个查询参数，您只需将带有参数名称和值的 queryParam() 方法附加到 RequestSpecification 对象，即上述情况下的 httpRequest。*

```java
ResponseBody body = res.body();
String rbdy = body.asString();
JsonPath jpath = new JsonPath(rbdy);
String title = jpath.getString("title");
System.out.println("The book title is - "+title);
```

*最后，我们将响应主体存储为 String 对象，并使用 JSONPath 对象解析其不同的值。然后我们从响应正文中获取标题。*

***注意**：您可以在我们的详细文章中阅读更多关于[***JSONPath的信息。***](https://www.toolsqa.com/rest-assured/jsonpath-and-query-json-using-jsonpath/)*

就是这样！看看我们可以多么容易地通过简单地使用*queryParam()*方法将查询参数发送到我们的请求。上面的代码执行显示了相对于 ISBN 的书名。

![查询参数控制台结果](https://toolsqa.com/gallery/Rest%20Assured/2.Query%20Parameter%20Console%20Results.png)

您现在可以继续使用具有查询参数的请求来自动化您的测试脚本，并增强您放心的代码。

## 关键要点

-   *我们已经看到了 URL 的不同组成部分以及每个组成部分的用途。*
-   *我们还了解了什么是查询参数以及当我们在浏览器中访问 URL 时它们是如何工作的。*
-   *使用 queryParam() 方法传递给放心测试的查询参数接受参数名称和值。*
-   *您不仅可以使用 queryParam() 方法一次，还可以使用与 GET 请求中的查询参数数量一样多的次数。*