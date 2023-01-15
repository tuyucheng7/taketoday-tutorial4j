在上一章中，我们介绍了在框架的 Endpoints 类中使用[***REST 路由的概念。***](https://www.toolsqa.com/rest-assured/rest-routes/)如果路线发生变化，我们不必在任何地方进行更改，它只是在 Routes 类中的一个地方。

随后，在本章中，我们将在我们的框架中实现泛型概念。我们正在处理各种数据类型的响应对象，例如*Token、Books 和 User Account。*此外，可以通过使用泛型来构建这些响应对象的故障安全。泛型增加了一层抽象。除此之外，它们还添加了一种为类和方法指定类型的方法。[***您可以在本泛型教程***](https://toolsqa.com/java/generics/)中阅读有关泛型概念的更多信息  。在本文中，我们将介绍：-

-   *泛型类实现需要什么？*
-   *泛型的实现*

## 泛型类实现需要什么

考虑一个从 Endpoint 方法返回[***Book 对象的示例，如下所示：***](https://bookstore.toolsqa.com/swagger/index.html)

```java
public static Books getBooks() {
     RequestSpecification request = RestAssured.given();
     Response response = request.get(Route.books());
     return response.getBody().as(Books.class);
}
```

如果我们得到一个失败的响应，我们的代码将在上述方法实现中失败。

因此，假设我们要像下面这样处理失败：

```java
public static Books getBooks() {
       RequestSpecification request = RestAssured.given();
       Response response = request.get(Route.books());
       int code = response.getStatusCode();
       if( code == 200 || code == 201 || code == 202 || code == 203 || code == 204 || code == 205) {
          return response.getBody().as(Book.Class());
       }
}
```

我们将无法通过发送错误的正文并期待 204 来测试阴性测试。我们可能会陷入这种情况。

因此我们需要一个类，它将返回响应正文以及状态，并在失败的情况下返回异常或错误消息。

现在，从服务器接收到的响应可以是多种数据类型。因此，我们需要一个能够处理不同响应对象的***接口。***为了将此参数化值提供给参数化类型，我们将其实现为***通用接口***。这个接口将包含我们操作*REST*响应时需要的所有方法。

## 泛型的实现

我们将按照以下步骤在我们的框架中实现泛型：

1.  *创建通用接口*
2.  *创建一个类来实现通用接口方法*
3.  Endpoints*类的修改*
4.  *Steps类的修改*
5.  运行*测试*

### ***创建通用接口***

首先，右键单击***apiEngine***包并选择***New >> Interface***。将其命名为***IRestResponse***。

***IRestResponse.java***

```java
package apiEngine;

import io.restassured.response.Response;

public interface IRestResponse<T>{
		public T getBody();
		
		public String getContent();
		
		public int getStatusCode();
		
		public boolean isSuccessful();
		
		public String getStatusDescription();
		
		public Response getResponse();
		
		public Exception getException();
	}
```

***解释：***

我们创建了一个类型为 的通用接口`<T>`。因此，我们可以使用这个接口来保存不同类型的响应。 *例如，IRestResponse`<Books>`将保存 Books 类型的响应。同样，它将用于 IRestResponse`<UserAccount>`和 IRestResponse`<Token>`*。

***注意**：如上所述，我们需要这个接口来返回 Body 以及状态，并在失败的情况下返回异常或错误消息。因此，这就是我们在 interface 中具有这些属性的原因*。

#### ***创建一个类来实现通用接口方法***

我们定义了对*REST*响应进行操作所需的方法。接下来我们需要在我们的 Rest Response 类中应用这些方法。

右键单击***piEngine***包并选择***New >> Class***。将其命名为***RestResponse。***

```java
package apiEngine;

import io.restassured.response.Response;

public class RestResponse<T> implements IRestResponse<T> {
	private T data;
	private Response response;
	private Exception e;

	public RestResponse(Class<T> t, Response response) {
		this.response = response;
		try{
			this.data = t.newInstance();
		}catch (Exception e){
			throw new RuntimeException("There should be a default constructor in the Response POJO");
		}
	}
	
	public String getContent() {
		return response.getBody().asString();
	}

	public int getStatusCode() {
		return response.getStatusCode();
	}

	public boolean isSuccessful() {
		int code = response.getStatusCode();
		if( code == 200 || code == 201 || code == 202 || code == 203 || code == 204 || code == 205) return true;
		return false;
	}

	public String getStatusDescription() {
		return response.getStatusLine();
	}
	
	public Response getResponse() {
		return response;
	}
	

	public T getBody() {
		try {
			data = (T) response.getBody().as(data.getClass());
		}catch (Exception e) {
			this.e=e;
		}
		return data;
	}

	public Exception getException() {
		return e;
	}

}
```

***解释：***

我们实现了这些方法，以便根据测试需要向我们返回 REST 响应的相关详细信息。

-   ***isSuccessful()**：将验证发送请求是否成功。它根据表示请求已成功处理的众多 HTTP 状态代码验证收到的响应状态代码*
-   ***getResponse().getBody().asString()**：我们有时需要字符串格式的响应正文内容。此方法实现负责处理它*。
-   ***getException()**：如果我们的响应体没有成功反序列化，我们会得到一个异常。e 将包含这个异常，我们使用这个方法得到它*。

### ***Endpoints 类的修改***

我们 Endpoints 类的方法将会改变。它们将为各自的方法返回*RestResponse `<Books>`、 RestResponse`<Token>`*和*RestResponse类型的响应。`<UserAccount>`*

*例如：我们的方法 authenticateUser() 更新自：*

```java
public static Response authenticateUser(AuthorizationRequest authRequest) {
	RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        Response response = request.body(authRequest).post(Route.generateToken());
        return response;
	}
```

至

```java
public static IRestResponse<Token> authenticateUser(AuthorizationRequest authRequest) {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        Response response = request.body(authRequest).post(Route.generateToken());
        return new RestResponse(Token.class, response);
}
```

***解释：***

在此方法中，我们将 Rest Assured Response 包装到 Token 类型的*RestResponse*类中，我们在其中反序列化了响应。

同样，我们更新了 Endpoints 类的以下方法：

1.  *获取书籍（）*
2.  *添加书（）*
3.  *删除书（）*
4.  *获取用户帐户（）*

我们更新后的 Endpoints 类看起来也一样：

```java
package apiEngine;

import apiEngine.model.requests.AddBooksRequest;
import apiEngine.model.requests.AuthorizationRequest;
import apiEngine.model.requests.RemoveBookRequest;
import apiEngine.model.responses.Books;
import apiEngine.model.responses.Token;
import apiEngine.model.responses.UserAccount;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EndPoints {
	
    private static final String BASE_URL = "https://bookstore.toolsqa.com";

    public static IRestResponse<Token> authenticateUser(AuthorizationRequest authRequest) {
	RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        Response response = request.body(authRequest).post(Route.generateToken());
        return new RestResponse(Token.class, response);
	}

    public static IRestResponse<Books> getBooks() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        Response response = request.get(Route.books());
        return new RestResponse(Books.class, response);
    }

    public static IRestResponse<UserAccount> addBook(AddBooksRequest addBooksRequest, String token) {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        Response response = request.body(addBooksRequest).post(Route.books());
        return new RestResponse(UserAccount.class, response);
    }

    public static Response removeBook(RemoveBookRequest removeBookRequest, String token) {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        return request.body(removeBookRequest).delete(Route.book());
    }

    public static IRestResponse<UserAccount> getUserAccount(String userId, String token) {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        Response response = request.get(Route.userAccount(userId));
        return new RestResponse(UserAccount.class, response);
    }

}
```

### ***Steps类的修改***

我们将修改步骤定义以调用端点类中列出的方法。

此外，您将直接在步骤定义类中获取响应。如前所述，与服务器通信并将其转换为响应类的逻辑移出。因此，我们的步骤定义仅包含我们感兴趣的测试层，而不是 API 的内部工作。

我们更新的步骤定义文件如下所示：

```java
package stepDefinitions;

import apiEngine.EndPoints;
import apiEngine.IRestResponse;
import apiEngine.model.*;
import apiEngine.model.requests.*;
import apiEngine.model.responses.*;
import org.junit.Assert;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;

public class Steps {

    private static final String USER_ID = "9b5f49ab-eea9-45f4-9d66-bcf56a531b85";    
    private static Response response;
    private static Token tokenResponse;
    private static IRestResponse<UserAccount> userAccountResponse;
    private static Book book;
    
    
    @Given("^I am an authorized user$")
    public void iAmAnAuthorizedUser() {

        AuthorizationRequest authRequest = new AuthorizationRequest("TOOLSQA-Test", "Test@@123");
        tokenResponse = EndPoints.authenticateUser(authRequest).getBody();
    }

    @Given("^A list of books are available$")
    public void listOfBooksAreAvailable() {
    	IRestResponse<Books> booksResponse = EndPoints.getBooks();
    	book = booksResponse.getBody().books.get(0);
    }

    @When("^I add a book to my reading list$")
    public void addBookInList() {
    	
        ISBN isbn = new ISBN(book.isbn);
        AddBooksRequest addBooksRequest = new AddBooksRequest(USER_ID, isbn);
        userAccountResponse = EndPoints.addBook(addBooksRequest, tokenResponse.token);
    }

    @Then("^The book is added$")
    public void bookIsAdded() {
        
    	Assert.assertTrue(userAccountResponse.isSuccessful());
        Assert.assertEquals(201, userAccountResponse.getStatusCode());

        Assert.assertEquals(USER_ID, userAccountResponse.getBody().userID);
        Assert.assertEquals(book.isbn, userAccountResponse.getBody().books.get(0).isbn);
    }

    @When("^I remove a book from my reading list$")
    public void removeBookFromList() {

        RemoveBookRequest removeBookRequest = new RemoveBookRequest(USER_ID, book.isbn);
        response = EndPoints.removeBook(removeBookRequest, tokenResponse.token);
    }

    @Then("^The book is removed$")
    public void bookIsRemoved() {
    	
        Assert.assertEquals(204, response.getStatusCode());

        userAccountResponse = EndPoints.getUserAccount(USER_ID, tokenResponse.token);
        Assert.assertEquals(200, userAccountResponse.getStatusCode());
        
        Assert.assertEquals(0, userAccountResponse.getBody().books.size());
    }

}
```

#### ***运行黄瓜测试***

***以 JUnit 运行测试***

我们现在都准备好运行更新的 Cucumber 测试了。*右键单击**TestRunner***类，然后单击***Run As >> JUnit Test***。因此，您将在 JUnit 选项卡的左侧*项目浏览* *器窗口中看到结果。*

![API 框架中的泛型](https://www.toolsqa.com/gallery/Rest%20Assured/1,Generics%20in%20API%20Framework.png)

***从 Cucumber 功能运行测试***

要将测试作为 Cucumber 功能运行，请右键单击*End2End_Test.feature*文件。之后，选择***Run As>>Cucumber Feature***。

![图片：Cucumber 结果为端点的第 7 章实现泛型概念](https://www.toolsqa.com/gallery/Rest%20Assured/2.Image%20Cucumber%20Results%20for%20Chapter%207%20Implementation%20Generics%20concept%20for%20the%20Endpoints.png)

我们更新后的框架项目文件夹结构将如下所示：

![图片：端点的第 7 章实现泛型概念的文件夹结构](https://www.toolsqa.com/gallery/Rest%20Assured/3.Image%20%20Folder%20Structure%20for%20Chapter%207%20Implementation%20Generics%20concept%20for%20the%20Endpoints.png)

我们的测试通过了我们为框架中的泛型实现所做的更改。我们将对[***请求标头进行重构，***](https://www.toolsqa.com/rest-assured/refactoring-for-request-headers/)以便我们可以在下一章中使用单个请求对象。此外，它将避免为每个请求重复添加 auth 标头的复杂性。