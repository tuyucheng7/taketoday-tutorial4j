在前面的章节中，我们学习了将[***JSON 请求正文***](https://www.toolsqa.com/rest-assured/convert-json-to-java-object/)和[***JSON 响应正文转换为 Java 对象。***](https://www.toolsqa.com/rest-assured/json-response-body-to-java-object/)我们使用序列化和反序列化概念来实现这一点。它使我们的测试变得更易于维护且不易出错。现在在本章中，我们将研究***测试层与 API 服务的分离，***并进一步使我们的测试更清晰和可维护。

如果您查看 Steps 类，这是我们的测试层，您会发现我们有很多与测试实际上无关的逻辑。它涉及使用给定的测试请求与服务器通信并获得响应。请参阅下面的屏幕截图：

![测试中的重复逻辑](https://toolsqa.com/gallery/Rest%20Assured/1.Duplicate%20logic%20in%20tests.png)

在实际项目中，我们实现了大量的 API 和测试。它们都包含在每个测试中与服务器通信的相同逻辑，例如：

-   *构建 RequestSpecification 对象*
-   *添加标题*
-   *调用服务器*

这会导致每个测试文件中出现大量代码重复。此外，如果明天某个特定端点需要进行一些更改，我们将需要在几个地方进行更改。这是不可取的，因为它使我们的框架更难维护。

此外，测试层只需要关注请求中发送的测试数据*（参数）*并接收来自 API 的响应。它不应该专注于实现的 API 内部的繁重逻辑。因此，作为软件测试人员，我们只对为这些请求获得的请求和响应感兴趣。而且，请求中发送的测试数据一般都是从Cucumber框架中的特征文件中传递过来的。该测试数据可以是参数，形成请求体、请求头或请求资源。

正如我们在第一章[***了解 API 文档***](https://www.toolsqa.com/rest-assured/api-documentation/)中所见，端点指示从服务器访问资源的方式。因此，我们将本质上将 Steps 文件中的所有***端点***逻辑组合起来，并将其移动到一个公共类中。该类将包含获取所需请求参数并发回从服务器接收到的响应的方法。端点应始终从测试层获取请求正文。在本文中，我们将介绍：-

-   *测试层与 API 服务的分离*
-   为测试创建 API 服务
    -   *创建 API 服务*
    -   *添加 API 服务的方法*
    -   *在步骤定义中调用 API 服务（测试层）*
    -   *运行测试*

## 测试层与 API 服务的分离

我们将与服务器通信的逻辑抽象为一个单独的类。它将使我们的 Steps 类更干净。随后，随着我们将更多的步骤写入框架中的 Step-Definition 文件中，针对不同的场景，我们可以重用这种与服务器通信的逻辑。

考虑生成我们[***书店 API***](https://bookstore.toolsqa.com/swagger/index.html)的令牌请求的示例：***/Account/v1/GenerateToken***

为了编写测试来测试生成令牌，我们的测试应该关注为通过的请求正文获得的响应。除此之外，他们不应专注于 API 的内部实现。在有大量 API 的项目中，最好将 Endpoints 与 Steps 文件分开。

此外，如果我们发送正确的凭据，我们会得到一个成功的响应。参考下面的截图：

![测试层分离](https://toolsqa.com/gallery/Rest%20Assured/2.Separation%20of%20Test%20Layer.png)

但是，如果我们发送无效凭据，我们将收到错误响应。

![测试层分离 - 参数](https://toolsqa.com/gallery/Rest%20Assured/3.Separation%20of%20Test%20Layer%20-%20Parameters.png)

因此，作为测试人员，重点是在我们获得的请求和响应正文中发送的参数。这与我们在上面 Swagger Tool 中的 Book Store API 中所做的相同。我们不知道 swagger 如何使用 HTTP 以及它如何与服务器通信。同样，测试层应该是不可知的，服务层如何处理请求的事实。此外，测试层应该只关注传递给请求的参数，无论是查询参数、标头参数还是正文参数等。

让我们看看如何将 Service 层与 Test 层分开。

## 为测试创建 API 服务

现在我们将创建 API 服务，它将被测试层使用，这样，测试层将变得干净并有效地只关注测试参数。

1.  *首先，创建 API 服务*
2.  *其次，添加API Services的方法*
3.  *三、Step Definitions（测试层）调用API Services*
4.  *四、运行测试*

### ***创建 API 服务***

1.  首先，右键单击*apiEngine*包并选择 Class。此外，将其命名为***Endpoints***。这将是我们的 API 服务类。

```java
package apiEngine;

public class Endpoints {

}
```

我们将所有端点移动到 Endpoints 类。***将BASE URL***也从 Steps 文件移动到 Endpoints 类是合理的。此外，它将帮助我们在向我们的端点发送请求时引用***BASE URL 。***

因此，更新后的 Endpoints 类将如下所示：

```java
package apiEngine;

public class Endpoints {
	
	private static final String BASE_URL = "https://bookstore.toolsqa.com";

}
```

### ***添加 API 服务的方法***



首先，我们将从 Steps 中提取对用户进行身份验证的方式到 Endpoints 类中。此外，我们将 *AuthorizationRequest * 的对象传递给该方法。

```java
public static Response authenticateUser(AuthorizationRequest authRequest) {
	RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        Response response = request.body(authRequest).post("/Account/v1/GenerateToken");
        return response;
	}
```

***代码说明：***

如您所见，我们创建了一个方法*authenticateUser*。在此方法中，我们通过*AuthorizationRequest*对象传递用户凭据，即 Request 中的*authRequest*。此外，我们还将 BASE_URL 和标头作为请求的一部分传递。

与上面的 authenticateUser() 方法类似，我们将为：

-   *获取书籍（）*
-   *添加书（）*
-   *删除书（）*
-   *获取用户帐户（）*

将 Endpoints 类的所有方法放在一起：

***Endpoints.java 类***

```java
package apiEngine;

import apiEngine.model.requests.AddBooksRequest;
import apiEngine.model.requests.AuthorizationRequest;
import apiEngine.model.requests.RemoveBookRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EndPoints {
	
	private static final String BASE_URL = "https://bookstore.toolsqa.com";

	public static Response authenticateUser(AuthorizationRequest authRequest) {
		RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        Response response = request.body(authRequest).post("/Account/v1/GenerateToken");
        return response;
	}

    public static Response getBooks() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        Response response = request.get("/BookStore/v1/Books");
        return response;
    }

    public static Response addBook(AddBooksRequest addBooksRequest, String token) {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        Response response = request.body(addBooksRequest).post("/BookStore/v1/Books");
        return response;
    }

    public static Response removeBook(RemoveBookRequest removeBookRequest, String token) {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        Response response = request.body(removeBookRequest).delete("/BookStore/v1/Book");
        return response;
    }

    public static Response getUserAccount(String userId, String token) {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        Response response = request.get("/Account/v1/User/" + userId);
        return response;
    }

}
```

我们现在进入下一步。在下一步中，我们将用我们新创建的方法替换 Steps 文件中的端点。

### ***与步骤定义中的 API 服务对话***

我们已经将与服务器通信的逻辑抽象为一个 Endpoint 类。现在，当我们用相关方法替换端点时，它将帮助我们使 Steps 类更清晰。除此之外，我们将重用这种与服务器通信的逻辑。

因此，Steps 类的第一部分，声明变量的变化将是：

```java
public class Steps {
	
	private static final String USER_ID = "9b5f49ab-eea9-45f4-9d66-bcf56a531b85";
	private static Response response; 
	private static Token tokenResponse; 
	private static Book book;
```

对于步骤定义：

```java
@Given("I am an authorized user")
```

更新为：

```java
@Given("I am an authorized user")
	public void iAmAnAuthorizedUser() {
		 AuthorizationRequest authRequest = new AuthorizationRequest("TOOLSQA-Test", "Test@@123");
    	         response = EndPoints.authenticateUser(authRequest);
                 tokenResponse = response.getBody().as(Token.class);
	}
```

我们将同样修改其余的步骤定义。此外，我们将步骤定义的所有这些更改一起放入步骤文件中。

更新后的 Steps 文件将如下所示：

```java
package stepDefinitions;

import java.util.List;
import java.util.Map;

import org.junit.Assert;

import apiEngine.Endpoints;
import apiEngine.model.Book;
import apiEngine.model.requests.AddBooksRequest;
import apiEngine.model.requests.AuthorizationRequest;
import apiEngine.model.requests.ISBN;
import apiEngine.model.requests.RemoveBookRequest;
import apiEngine.model.response.Books;
import apiEngine.model.response.Token;
import apiEngine.model.response.UserAccount;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import io.restassured.response.Response;

public class Steps {
	
	private static final String USER_ID = "9b5f49ab-eea9-45f4-9d66-bcf56a531b85";
	private static Response response; 
	private static Token tokenResponse; 
	private static Book book;


	@Given("I am an authorized user")
	public void iAmAnAuthorizedUser() {
		AuthorizationRequest authRequest = new AuthorizationRequest("TOOLSQA-Test", "Test@@123");
    	response = Endpoints.authenticateUser(authRequest);
        tokenResponse = response.getBody().as(Token.class);
	}

	@Given("A list of books are available")
	public void listOfBooksAreAvailable() {
		response = Endpoints.getBooks(); 
        Books books = response.getBody().as(Books.class);
        book = books.books.get(0);	   
	}

	@When("I add a book to my reading list")
	public void addBookInList() {
		ISBN isbn = new ISBN(book.isbn);
        AddBooksRequest addBooksRequest = new AddBooksRequest(USER_ID, isbn);
        response = Endpoints.addBook(addBooksRequest, tokenResponse.token);
	}

	@Then("The book is added")
	public void bookIsAdded() {
		Assert.assertEquals(201, response.getStatusCode());
		
		UserAccount userAccount = response.getBody().as(UserAccount.class); 
		
		Assert.assertEquals(USER_ID, userAccount.userID); 
		Assert.assertEquals(book.isbn, userAccount.books.get(0).isbn);
	}

	@When("I remove a book from my reading list")
	public void removeBookFromList() {	
		 RemoveBookRequest removeBookRequest = new RemoveBookRequest(USER_ID, book.isbn);
	     response = Endpoints.removeBook(removeBookRequest, tokenResponse.token);
	}

	@Then("The book is removed")
	public void bookIsRemoved() {
	    Assert.assertEquals(204, response.getStatusCode());

        response = Endpoints.getUserAccount(USER_ID, tokenResponse.token);
        Assert.assertEquals(200, response.getStatusCode());
        
        UserAccount userAccount = response.getBody().as(UserAccount.class);
        Assert.assertEquals(0, userAccount.books.size());
	}
}
```

### ***运行测试***

***以 JUnit 运行测试***

我们现在都准备好运行更新的 Cucumber 测试了。首先，*右键单击* TestRunner ***类*** ，然后单击 ***Run As >> JUnit Test***。 *Cucumber将以与在**Selenium WebDriver*中运行相同的方式运行脚本。*最后，结果将显示在JUnit* 选项卡的左侧 *项目浏览器窗口*中 。

![图片：第 5 章 Junit 结果](https://toolsqa.com/gallery/Rest%20Assured/4.Image%20Chapter%205%20Junit%20Results.png)

***从 Cucumber 功能运行测试***

要将测试作为 Cucumber 功能运行，请右键单击 End2End_Test.feature 文件。之后，选择*运行方式>>黄瓜功能。*

![图片第 5 章黄瓜结果](https://toolsqa.com/gallery/Rest%20Assured/5.Image%20Chapter%205%20Cucumber%20Results.png)

此外，我们更新的框架的项目文件夹结构将如下所示：

![图片：第 5 章项目框架结构](https://toolsqa.com/gallery/Rest%20Assured/6.Image%20Chapter%205%20Project%20Framework%20Structure.png)

在下一章中，我们将介绍API 自动化框架[***中端点中路由的实现。***](https://www.toolsqa.com/rest-assured/rest-routes/)这将有助于我们使框架可维护，并且当路由更改时，我们不必在任何地方进行更改，除了 Routes 类。此外，请实现上面讨论的 Endpoints 类，并与我们分享您的宝贵反馈。