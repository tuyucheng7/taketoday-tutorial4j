在本章中，我们将实现我们收到***的 JSON 响应的反序列化。***它可以帮助我们阅读响应的正文。随后，在这个反序列化的过程中，我们会将***JSON Response Body 转换为 Java Objects***。它也称为 JSON 响应的对象表示。

此外，要了解有关 JSON 响应反序列化的更多信息，我们建议您阅读[***Deserialize JSON Response***](https://www.toolsqa.com/rest-assured/deserialize-json-response/)。

我们将按照以下步骤在框架中实现响应的反序列化。[***我们在将 JSON 请求正文转换为 Java 对象***](https://www.toolsqa.com/rest-assured/convert-json-to-java-object/)的上一章中也遵循了它们 。

1.  *为我们的每个响应对象创建 POJO 类：*

-   *书*
-   *图书*
-   *令牌*
-   *用户帐号*

1.  *将 Step 文件中的 Response 主体替换为 POJO 类对象*
2.  *运行测试*

## 将 JSON 响应正文转换为 Java 对象

首先，我们需要将*JSON Response* 转换为我们的响应对象的*POJO*类。*因此，让我们学习从JSON Response*创建*POJO*类。在反序列化下，我们将研究我们的*JSON*主体参数并为其创建一个*POJO*类。让我们从 Get A Book 请求的简单请求示例开始：

### ***创建 POJO 类 Book***

作为此请求的一部分，我们发送 ISBN 参数以获取特定图书的详细信息。

![将 JSON 响应正文转换为 Java 对象示例](https://toolsqa.com/gallery/Rest%20Assured/1.Convert%20JSON%20Response%20Body%20to%20Java%20Object%20Example.png)

[***因此，来自Swagger Bookstore API***](https://bookstore.toolsqa.com/swagger/index.html)的响应正文，如上图所示：

```java
{
  "books": [
    {
      "isbn": "9781449325862",
      "title": "Git Pocket Guide",
      "subTitle": "A Working Introduction",
      "author": "Richard E. Silverman",
      "published": "2013-08-02T00:00:00",
      "publisher": "O'Reilly Media",
      "pages": 234,
      "description": "This pocket guide is the perfect on-the-job companion to Git, the distributed version control system. It provides a compact, readable introduction to Git for new users, as well as a reference to common commands and procedures for those of you with Git experience.",
      "website": "https://chimera.labs.oreilly.com/books/1230000000561/index.html"
    }
}
```

作为下一步的一部分，我们将为响应对象创建一个 POJO 类。此外，您可以使用我们在前一章中讨论的相同[***在线实用程序将响应转换为 POJO 类。***](https://www.jsonschema2pojo.org/)

要创建它的 POJO 类，请按照以下步骤操作：

*1.首先，右键点击****apiEngine***包下的***model** Package*。之后，选择***New>>Class***。将其命名为书。此外，我们将在此类下捕获书籍响应。

***书类***

```java
package apiEngine.model;

public class Book {
    public String isbn;
    public String title;
    public String subTitle;
    public String author;
    public String published;
    public String publisher;
    public int pages;
    public String description;
    public String website;
}
```

***代码说明***

这个 Book 类将包含我们在响应中得到的所有字段，例如*标题、isbn、作者、页数*等。

### ***为 Books Response 对象创建一个 POJO 类：***

Bookstore API 的响应正文，如下图所示：

![将 JSON 响应正文转换为 Java 对象](https://toolsqa.com/gallery/Rest%20Assured/2.Convert%20JSON%20Response%20Body%20to%20Java%20Object.png)

```java
{
  "userID": "9b5f49ab-eea9-45f4-9d66-bcf56a531b85",
  "userName": "TOOLSQA-Test",
  "books": [
    {
      "isbn": "9781449325862",
      "title": "Git Pocket Guide",
      "subTitle": "A Working Introduction",
      "author": "Richard E. Silverman",
      "published": "2013-08-02T00:00:00",
      "publisher": "O'Reilly Media",
      "pages": 234,
      "description": "This pocket guide is the perfect on-the-job companion to Git, the distributed version control system. It provides a compact, readable introduction to Git for new users, as well as a reference to common commands and procedures for those of you with Git experience.",
      "website": "https://chimera.labs.oreilly.com/books/1230000000561/index.html"
    }
  ]
}
```

***注意**：作为响应正文的一部分，我们获得了为用户添加的图书的图书详细信息以及其他用户详细信息，例如**userName**和**userID。***

要创建它的 POJO 类，请按照以下步骤操作：

1.  首先，在这个***模型**包中，右键单击*模型并选择 ***新建>>包。*** *将其命名*为***响应。*** 此外，我们将捕获此包下的所有响应类。
2.  其次，*右键单击* 上面创建的***响应***包并选择 ***New >> Class***。将其命名为***书籍。***

***书籍类***

```java
package apiEngine.model.responses;

import java.util.List;
import apiEngine.model.Book;

public class Books {
	 public List<Book> books;
}
```

***代码说明****我们创建了 Books 类来保存我们在GET Books Request的**JSON*响应中 收到的 Book 类型的列表

### ***为 Token Object 创建一个 POJO 类：***



至此，我们已经了解了创建 POJO 类的过程，如上例所示。因此，在接下来的 API 中，我将直接添加各个 API 的 POJO。***因此，来自Bookstore API***的 Generate Token API 的响应正文 是：

```java
{
  "token": "string",
  "expires": "2020-03-14T13:42:15.716Z",
  "status": "string",
  "result": "string"
}
```

要创建它的 POJO 类，*请右键单击*上面创建的***响应** 包*并选择 ***New >> Class***。之后，将其命名为***Token***。

```java
package apiEngine.model.responses;

public class Token {
	 public String token;
	 public String expires;
	 public String status;
	 public String result;
}
```

***代码说明***

我们创建了 Token 类，它将包含我们在响应字段中获得的所有字段，即令牌、到期、状态以及我们在*GenerateToken的**JSON*响应中收到的结果。

### ***为用户帐户对象创建一个 POJO 类：***

同样，对于用户帐户 API，响应正文为：

```java
{
  "userID": "9b5f49ab-eea9-45f4-9d66-bcf56a531b85",
  "userName": "TOOLSQA-Test",
  "books": []
}
```

要创建响应主体的 POJO 类，*请右键单击*上面创建的***响应** 包。*之后，选择***New >> Class***。将其命名为***UserAccount***。

```java
package apiEngine.model.responses;

import java.util.List;
import apiEngine.model.Book;

public class UserAccount {
    public String userID;
    public String userName;
    public List<Book> books;
}
```

***代码说明***

我们创建了 UserAccount 类来保存与用户帐户关联的信息，例如与用户关联的*书籍、用户**ID*和用户名*。*

作为我们项目实施的下一步，我们将修改我们的 Steps 类。

## 将 Step 文件中的 Response 主体替换为 POJO 类对象

现在是时候使用上面在测试代码中创建的 POJO 类了。让我们看一下步骤定义中下面的老黄瓜步骤之一：

```java
    @Given("^I am an authorized user$")
    public void iAmAnAuthorizedUser() {
        AuthorizationRequest credentials = new AuthorizationRequest("TOOLSQA-Test","Test@@123");

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        response = request.body(credentials).post("/Account/v1/GenerateToken");

        String jsonString = response.asString();
       //We will deserialize the Response body into Token Response
        token = JsonPath.from(jsonString).get("token");
    }
```

我们同样更新它：

```java
  @Given("^I am an authorized user$")
    public void iAmAnAuthorizedUser() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        AuthorizationRequest authRequest = new AuthorizationRequest("TOOLSQA-Test", "Test@@123");

        response = request.body(authRequest).post("/Account/v1/GenerateToken");

        // Deserializing the Response body into tokenResponse
        tokenResponse = response.getBody().as(Token.class);
    }
```

*注意导入语句：**import io.restassured.path.json.JsonPath**；不再需要，因为我们已经反序列化了对 Token 类的响应。*

***代码说明：*** 我们在上面这一步中将响应体反序列化为***Token***类。此响应正文保存在“ ***tokenResponse*** ”变量中。保存的变量将与令牌一起在请求中进一步使用。

我们将这些代码片段放在了我们的 Steps 文件中：

```java
package stepDefinitions;

import java.util.List;
import java.util.Map;

import org.junit.Assert;

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
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Steps {
	private static final String USER_ID = "9b5f49ab-eea9-45f4-9d66-bcf56a531b85";
	private static final String BASE_URL = "https://bookstore.toolsqa.com";

	private static Response response; 
	private static Token tokenResponse; 
	private static Book book;


	@Given("I am an authorized user")
	public void iAmAnAuthorizedUser() {
		AuthorizationRequest credentials = new AuthorizationRequest("TOOLSQA-Test","Test@@123");

		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();

		request.header("Content-Type", "application/json");
		response = request.body(credentials).post("/Account/v1/GenerateToken");
		
		// Deserializing the Response body into tokenResponse 
		tokenResponse = response.getBody().as(Token.class);
	}

	@Given("A list of books are available")
	public void listOfBooksAreAvailable() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();
		
		response = request.get("/BookStore/v1/Books");

		// Deserializing the Response body into Books class 
		Books books = response.getBody().as(Books.class); 
		book = books.books.get(0);	   
	}

	@When("I add a book to my reading list")
	public void addBookInList() {
		ISBN isbn = new ISBN(book.isbn);
		AddBooksRequest addBooksRequest = new AddBooksRequest(USER_ID, isbn);
		
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", "Bearer " + tokenResponse.token)
		.header("Content-Type", "application/json");

		response = request.body(addBooksRequest).post("/BookStore/v1/Books");
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
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();
		
		RemoveBookRequest removeBookRequest = new RemoveBookRequest(USER_ID, book.isbn);

		request.header("Authorization", "Bearer " + tokenResponse.token)
		.header("Content-Type", "application/json");

		response = request.body(removeBookRequest).delete("/BookStore/v1/Book");
	}

	@Then("The book is removed")
	public void bookIsRemoved() {
		Assert.assertEquals(204, response.getStatusCode());

		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();

		request.header("Authorization", "Bearer " + tokenResponse.token)
		.header("Content-Type", "application/json");

		response = request.get("/Account/v1/User/" + USER_ID);
		Assert.assertEquals(200, response.getStatusCode());

		UserAccount userAccount = response.getBody().as(UserAccount.class);
		Assert.assertEquals(0, userAccount.books.size());
	}
}
```

## 运行黄瓜测试

***以 JUnit 运行测试***

我们现在都准备好运行更新的 Cucumber 测试了。首先，*右键单击* TestRunner ***类*** ，然后单击 ***Run As >> JUnit Test。*** *Cucumber以与**Selenium WebDriver*相同的方式运行脚本。 因此，结果将显示在控制台的*JUnit选项卡中。*

![图片：第 4 章 Junit 结果](https://toolsqa.com/gallery/Rest%20Assured/3.Image%20Chapter%204%20Junit%20Results.png)

***从 Cucumber 功能运行测试***

要将测试作为 Cucumber 功能运行，请右键单击*End2End_Test.feature*文件。之后，选择*运行方式>>黄瓜功能。*

![图片：第 4 章黄瓜结果](https://toolsqa.com/gallery/Rest%20Assured/4.Image%20Chapter%204%20Cucumber%20Results.png)

我们的测试通过了我们为将*JSON 响应主体转换为 Java 对象所做的更改。*请尝试在您的框架中实现它，如上所述，并分享您的宝贵反馈。

创建上述 POJO 类后，项目文件夹结构将如下所示：

![项目结构响应类](https://toolsqa.com/gallery/Rest%20Assured/5.Project%20Structure%20Response%20classes.png)

随后，我们将在下一章尝试将[***测试层与 API 服务分离。***](https://www.toolsqa.com/rest-assured/separation-of-test-layer/)