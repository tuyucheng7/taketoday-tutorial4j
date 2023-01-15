## 什么是删除请求方法？

HTTP 删除请求是使用 HTTP 删除方法（*写为 delete*）执行的，该方法从我们通过它发送的 URI 标识的服务器中删除资源。另请注意，删除方法旨在更改服务器的状态。尽管即使是 200 个代码也不能保证这一点。[***下面列出了官方HTTP RFC***](https://datatracker.ietf.org/doc/html/rfc7231#section-4.3.5)中关于删除请求方法的几个要点：

-   *Delete 方法请求服务器删除由请求 URI 标识的资源。*
-   *资源删除取决于服务器，如果规定删除则删除。此外，资源的恢复实施也相当可观。*
-   *在资源和相应的当前功能之间存在关联的删除请求。*
-   *它类似于 rm UNIX 命令，其中删除了资源的当前关联而不是以前的关联（如果有）。*
-   *删除请求方法被放在 W3C 文档的幂等类别下。这意味着一旦请求删除资源，再次对同一资源的请求将给出与该资源已被删除相同的结果。*
-   *删除方法响应是不可缓存的。用户无法缓存服务器响应以供以后使用。缓存删除请求会产生不一致。*

现在让我们看看删除请求的响应代码。

## 删除请求有哪些不同的响应代码？

在发送 Delete 请求时，服务器会以一些响应代码进行响应，这些响应代码表示 Delete 方法执行的操作。考虑以下相同的代码-

-   ***202**（已接受）：服务器接受请求但不执行。*
-   ***204**（无内容）- HTTP 删除请求方法上的状态代码 204 表示删除请求成功执行，响应中没有任何内容。*
-   ***200**（OK）- 操作成功，响应消息包括状态表示。*
-   ***404** (Not Found) - 当服务器找不到资源时。原因可能不存在或先前已删除。*

现在我们已经了解了 Delete 方法的基础知识，我们将了解如何使用放心发送 Delete 请求，但在此之前让我们看一下使用 Swagger UI 的简单 DELETE 请求。请求的 URL 是 -

[***https://demoqa.com/swagger/***](https://demoqa.com/swagger/)

该 URL 将显示如下屏幕 -

![书店 api 主页](https://toolsqa.com/gallery/Rest%20Assured/1.bookstore%20api%20home.png)

现在，您必须在此页面的 BookStore 部分中选择第一个“***删除***”。

![删除请求](https://toolsqa.com/gallery/Rest%20Assured/2.delete%20request.jpg)

***注意**：我们已禁用 UserID 以防止出现空数据库。在实际应用中，您必须将所需的值传递给服务器。*

当您单击该部分时，您将看到描述执行成功的响应。***对于“未找到 bookid*** ”或“***未找到用户***”等情况，会收到特定响应。请参阅下面的快照以获得更清晰的信息-

![书店删除回复](https://toolsqa.com/gallery/Rest%20Assured/3.bookstore%20delete%20responses.jpg)

在介绍中，我们将 delete 方法定义为可以删除资源的东西。提醒一下，您可以在此处将“***资源***”与宠物记录相关联。通用术语“**资源**”表示各种请求中的不同资源。现在我们已经了解了如何使用 swagger 手动删除资源，接下来我们将尝试使用 Rest Assured 自动执行删除请求。

## 如何使用放心提出删除请求？

我们现在将看到如何使用放心从服务器中删除资源。为此，我们将使用之前示例中一直使用的[***BookStore API 。***](https://demoqa.com/swagger/)我们将自动化以下用例-

1.  *[**使用 Get User Data API**](https://demoqa.com/swagger/#/Account/AccountV1UserPost)获取可供用户使用的书籍列表。*
2.  *删除与特定 ISBN 对应的图书，并使用[**Delete Book API**](https://demoqa.com/swagger/#/BookStore/BookStoreV1BookDelete)验证响应代码。*
3.  *验证步骤 1 中 API 的执行是否更新了图书列表并且不显示已删除的图书。*

现在让我们看看使用 Rest Assured 的上述用例的代码。

```java
package bookstore;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

public class DeleteBook {
	String userId= "de5d75d1-59b4-487e-b632-f18bc0665c0d";
	String baseUrl="https://demoqa.com/swagger/";
	String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6InRlc3RpbmcxMjMiLCJwYXNzd29yZCI6IlBhc3N3b3JkQDEiLCJpYXQiOjE2Mjg1NjQyMjF9.lW8JJvJF7jKebbqPiHOBGtCAus8D9Nv1BK6IoIIMJQ4";
	String isbn ="9781449337711";
		
	  @BeforeTest
	  @AfterTest
	  public void getUserData() { 
	  RestAssured.baseURI = baseUrl;
	  RequestSpecification httpRequest =
	  RestAssured.given().header("Authorization", "Bearer " + token)
	  .header("Content-Type", "application/json");
	  
	  Response res = httpRequest.get("/Account/v1/User/"+userId);
	  ResponseBody body = res.body(); 
	  //Converting the response body to string
	  String rbdy = body.asString(); 
	  System.out.println("Data from the GET API- "+rbdy); 
	  }
	 
	  @Test
	  public void deleteBook() {
		  RestAssured.baseURI = baseUrl;
		  RequestSpecification httpRequest = RestAssured.given().header("Authorization", "Bearer " + token)
			         .header("Content-Type", "application/json");
		 
		  //Calling the Delete API with request body
		  Response res = httpRequest.body("{ \"isbn\": \"" + isbn + "\", \"userId\": \"" + userId + "\"}").delete("/BookStore/v1/Book");
	 
		  //Fetching the response code from the request and validating the same
		  System.out.println("The response code is - " +res.getStatusCode());
	      Assert.assertEquals(res.getStatusCode(),204);
     
	  }
}
```

#### ***代码演练***

让我们浏览一下上面的代码，看看我们在这里试图实现什么。请注意，第一种方法，*即 getUserData()*是一个简单的 GET 方法，我们试图获取与*userId*对应的用户数据。如果您还没有读过代码，可以参考我们关于[***使用 Rest Assured 的 Rest API的文章来理解代码。***](https://www.toolsqa.com/rest-assured/rest-api-test-using-rest-assured/)此外，您会注意到授权字段。对于这个关于删除请求方法的特定教程，您不必担心它。但是，如果您想了解[***授权***](https://toolsqa.com/postman/basic-authentication-in-postman/)和[***OAuth 2.0***](https://toolsqa.com/postman/oauth-2-0-authorization/)，您可以参考我们的其他教程。在本关于删除方法的教程中，我们将在这里介绍在*deleteBook()*方法下编写的代码。

```java
RestAssured.baseURI = baseUrl;
RequestSpecification httpRequest = RestAssured.given().header("Authorization", "Bearer " + token)
			         .header("Content-Type", "application/json");
```

我们首先使用基本 URI 设置请求。接下来，我们使用*RequestSpecification*接口添加还包含授权令牌的标头（ *s ）。*

```java
Response res = httpRequest.body("{ \"isbn\": \"" + isbn + "\", \"userId\": \"" + userId + "\"}").delete("/BookStore/v1/Book");
```

接下来，使用 RequestSpecification 对象存储 Response 对象。在这里，我们将请求的 JSON 正文与 Delete 方法的端点 URL 一起传递。响应存储在变量中。

```java
System.out.println("The response code is - " +res.getStatusCode());
Assert.assertEquals(res.getStatusCode(),204);
```

最后，我们只是打印响应状态代码并使用[***TestNG Assert***](https://www.toolsqa.com/testng/testng-asserts/)来验证它。由于在我们的例子中预期的响应代码是 204，我们的测试会将它作为预期的参数。

执行代码后，您将看到首先执行 getUserData 方法，然后执行 deleteBook 方法。最后 getUserData 方法再次运行。这是因为我们为 getUserData 方法使用了*@BeforeTest*和*@AfterTest*。控制台会显示如下结果 -

![使用放心结果删除请求](https://toolsqa.com/gallery/Rest%20Assured/4.Delete%20request%20using%20Rest%20Assured%20results.png)

你去吧！您已使用 Delete API 从用户数据中成功删除了一条记录。现在，您可以继续尝试使用 Rest Assured 的不同 API 方法并强化您的概念。在我们的下一篇文章中，我们将看到 Basic Auth 如何处理 Rest Assured。

注意：删除请求的视频教程可[***在 Rest Assured 中的删除请求中找到***](https://www.youtube.com/watch?v=DZP97jtU3-M)

## 关键要点

-   *“ ***delete*** ”方法从服务器中删除一个资源。它与 rm UNIX 命令非常相似。*
-   *资源的删除是基于服务器实现的，并且接收到的响应是不可缓存的。*
-   *删除请求返回三种响应代码中的任何一种，即***202、204***和***200***。*
-   ****与“ path*** ”或“ ***pathParams*** ”一起使用的***delete()***方法删除请求。*
-   *为了验证删除，您可以根据需要使用断言或任何其他相关代码。*