在上一章中，我们[***重构了端点类***](https://www.toolsqa.com/rest-assured/refactoring-for-request-headers/)，将***请求头***作为端点类的实例传递，而不是在每个方法中传递它们。随后，作为下一步，我们将学习在 API 放心自动化框架中***的步骤定义之间共享测试上下文。***

## 需要在 Cucumber 步骤定义之间共享测试上下文

在现实生活中的项目中，期望我们构建一个可扩展的自动化框架。此外，随着我们添加多个场景，自动化覆盖范围也会增加。如果单个步骤定义文件包含所有步骤定义，它会使步骤定义文件变得混乱。此外，它变得不可维护。因此，必须重构在不同类下分离的步骤定义。

在 Cucumber 中的功能文件的任何场景中，都会一个接一个地执行一系列步骤。场景的每个步骤都可能拥有一个状态，该状态可能证明对同一场景中的另一个步骤有用。因此，这些步骤取决于先前执行的步骤。此外，出于这个原因，需要在步骤之间共享状态。

在我们的框架中，我们只有一个场景用于说明目的。但是，随着我们添加大量场景，它会在一段时间内增长。此外，为了保持步骤定义文件整洁并与所有 ***步骤定义文件共享******测试上下文***，我们将求助于依赖注入***容器 PicoContainer***。我们之前在之前的***Cucumber 教程中使用 Selenium***实现了这一点，关于[***与 Cucumber Step Definitions 共享测试上下文***](https://www.toolsqa.com/selenium-cucumber-framework/sharing-test-context-between-cucumber-step-definitions/)。

作为下一步，我们将学习如何在 API Rest Assured Automation Framework 中的步骤定义之间实现测试上下文共享。

## 如何使用 PicoContainer 在 Cucumber 步骤之间共享测试上下文

[***我们将按照与 Cucumber Step Definitions 共享 Test Context***](https://www.toolsqa.com/selenium-cucumber-framework/sharing-test-context-between-cucumber-step-definitions/)相同的步骤来跨步骤共享数据状态：

1.  *首先，将 PicoContainer 添加到项目中。*
2.  *其次，创建一个测试上下文类来保存所有对象的状态。*
3.  *第三，将Steps类划分为多个steps类。*
4.  *四、编写Constructor来共享Test Context。*
5.  *最后，运行测试。*

### ***将 PicoContainer 库添加到 Maven 项目***

在我们的项目pom.xml中添加依赖注入的maven依赖-PicoContainer。此外，您可以在 [ ***Maven Repository - Cucumber PicoContainer*** ]找到有关版本等的更多详细信息

```java
(https://mvnrepository.com/artifact/io.cucumber/cucumber-picocontainer).

<!-- https://mvnrepository.com/artifact/io.cucumber/cucumber-picocontainer -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-picocontainer</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>
```

### ***创建一个包含所有对象状态的测试上下文类***

Text Context 类应包含您的 Steps 文件正在使用的所有信息。在我们的框架中，我们同样使用 Steps 文件中 Endpoints 类的信息。很简单，我们需要在这个 Test Context 类中添加一个 Endpoints 类的对象。

要创建一个测试上下文类，

1.  首先，*右键单击* src ***/test/java*** 并选择 ***New >> Package。*** 此外，将其命名为***黄瓜***。所有 Cucumber Helper 类都将在同一个包中。
2.  其次，*右键单击*上面创建的包并选择 ***New >> Class***。将其命名为 ***TestContext。***

***TestContext.java***

```java
package cucumber;

import apiEngine.EndPoints;

public class TestContext {
	
	private String BASE_URL = "https://bookstore.toolsqa.com";
	private EndPoints endPoints;
	
	public TestContext() {
		endPoints = new EndPoints(BASE_URL);
	}
	
	 public EndPoints getEndPoints() {
        return endPoints;
    }
}
```

因此，我们在构造函数中保留了 Endpoints 的初始化，并为对象创建了***getEndPoints()***。

### ***将 Steps 类划分为多个 step 类***

我们可以在逻辑上将端点划分为 Account Steps 和 Book Steps。一些必要的步骤将包含在 BaseStep 中。

 在 ***stepDefinitions***包中创建三个 具有以下名称的***新类：***

-   *帐户步骤*
-   *书籍步骤*
-   *基本步骤*

***注意**：您可以在清理期间通过将所需的步骤定义移动到关联的类来用 BaseSteps 替换 Steps 文件。或者，您可以创建一个 BaseStep 类，一旦我们完成本教程，然后删除 Steps 文件，因为我们将不再使用它。*

### ***编写构造函数以共享测试上下文***

我们将步骤定义划分为不同的步骤类。这些类之间通常共享 Endpoints 类。此外，在现实生活中的项目中，我们可能有多个类，它们对每个 Step 类都有广泛的要求。此外，不建议一次又一次地使用 new 运算符为所有标准类创建对象。

如果我们在Steps 类文件中添加一个**构造函数并将*****TestContext 作为参数传递给构造函数，***它将解决所有问题。在*TestContext*对象中，我们得到了测试所需的一切。

因此***AccountsSteps.java***类将如下所示：

```java
package stepDefinitions;

import apiEngine.model.requests.*;
import cucumber.TestContext;
import cucumber.api.java.en.Given;

public class AccountSteps extends BaseStep {

    public AccountSteps(TestContext testContext){
    	super(testContext);
    }

    @Given("^I am an authorized user$")
    public void iAmAnAuthorizedUser() {
        AuthorizationRequest authRequest = new AuthorizationRequest("TOOLSQA-Test", "Test@@123");
        getEndPoints().authenticateUser(authRequest);
    }

}
```

同样，***BooksSteps.java***类的更改是：

```java
package stepDefinitions;

import apiEngine.IRestResponse;
import apiEngine.model.Book;
import apiEngine.model.requests.AddBooksRequest;
import apiEngine.model.requests.ISBN;
import apiEngine.model.requests.RemoveBookRequest;
import apiEngine.model.responses.Books;
import apiEngine.model.responses.UserAccount;
import cucumber.TestContext;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;
import org.junit.Assert;

public class BooksSteps extends BaseStep {

    public BooksSteps(TestContext testContext){
    	super(testContext);
    }

    private final String USER_ID = "9b5f49ab-eea9-45f4-9d66-bcf56a531b85";
    private Response response;
    private IRestResponse<UserAccount> userAccountResponse;
    private Book book;

    @Given("^A list of books are available$")
    public void listOfBooksAreAvailable() {
        IRestResponse<Books> booksResponse = getEndPoints().getBooks();
        book = booksResponse.getBody().books.get(0);
    }

    @When("^I add a book to my reading list$")
    public void addBookInList() {
        ISBN isbn = new ISBN(book.isbn);
        AddBooksRequest addBooksRequest = new AddBooksRequest(USER_ID, isbn);
        userAccountResponse = getEndPoints().addBook(addBooksRequest);
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
        response = getEndPoints().removeBook(removeBookRequest);
    }

    @Then("^The book is removed$")
    public void bookIsRemoved() {
        Assert.assertEquals(204, response.getStatusCode());

        userAccountResponse = getEndPoints().getUserAccount(USER_ID);
        Assert.assertEquals(200, userAccountResponse.getStatusCode());

        Assert.assertEquals(0, userAccountResponse.getBody().books.size());
    }
}
```

最后，**BaseSteps.java**类更新为：

```java
package stepDefinitions;

import apiEngine.EndPoints;
import cucumber.TestContext;

public class BaseStep {
    private static final String BASE_URL = "https://bookstore.toolsqa.com";
    private EndPoints endPoints;

    public BaseStep(TestContext testContext) {
    	endPoints = testContext.getEndPoints();
    }

    public EndPoints getEndPoints() {
        return endPoints;
    }
}
```

### ***运行黄瓜测试***

***以 JUnit 运行测试***

我们现在都准备好运行更新的 Cucumber 测试了。首先，*右键单击* TestRunner ***类*** ，然后单击 ***Run As >> JUnit Test。****Cucumber 以与Selenium WebDriver* 相同的方式运行脚本。 因此，结果将显示在控制台的*JUnit选项卡中。*

![在 Cucumber 步骤定义之间共享测试上下文 - Junit 结果](https://www.toolsqa.com/gallery/Rest%20Assured/1.Share%20Test%20Context%20between%20Cucumber%20Step%20Definitions%20-%20Junit%20results.png)

***从 Cucumber 功能运行测试***

要将测试作为 Cucumber 功能运行，请右键单击*End2End_Test.feature*文件。之后，选择*运行方式>>黄瓜功能。*

![图片：第 9 章 Cucumber 结果](https://www.toolsqa.com/gallery/Rest%20Assured/2.Image%20Chapter%209%20Cucumber%20results.png)

测试成功通过了我们为在我们的框架中***与 Cucumber 步骤定义共享测试上下文所做的更改。***请尝试在您的框架中实现上述更改，如上所述。

我们更新后的框架项目文件夹结构将如下所示：

![图片：第 9 章项目结构](https://www.toolsqa.com/gallery/Rest%20Assured/3.Image%20Chapter%209%20Project%20Structure.png)

下一章我们将介绍[***Framework中Scenario Context***](https://toolsqa.com/selenium-cucumber-framework/sharing-test-context-between-cucumber-step-definitions/)的使用概念，具体分享测试数据信息。此外，我们将进一步将我们的步骤***与步骤类分离为验证步骤***。