在上一章中，我们实现了[***Cucumber Steps 之间的共享测试上下文***](https://www.toolsqa.com/rest-assured/share-test-context/)。此外，我们进行了更改以使步骤定义文件整洁，并与所有 ***步骤定义文件共享******测试上下文***。作为上一章的继续，我们现在将继续使用 Cucumber 中的场景上下文来共享数据。

***Scenario Context***类显式地保存测试数据信息。它可以帮助您将值存储在步骤之间的键值对中。此外，它有助于更好地组织步骤定义，而不是在步骤定义类中使用私有变量。我们之前在 Cucumber 教程[***中使用场景上下文在 Cucumber 中的步骤之间共享数据***](https://www.toolsqa.com/selenium-cucumber-framework/share-data-between-steps-in-cucumber-using-scenario-context/)已经实现了这一点。

作为下一步，我们将在 cucumber 中实现场景上下文，以便在我们的 API Rest Assured Automation Framework 中的步骤定义之间共享数据。

## 在 Cucumber 中共享场景上下文

在端到端测试中，您有兴趣验证发送的请求的响应。假设您希望检查测试中的后续操作：

-   *增加书籍*
-   *取书*

在这种情况下，我们希望在将图书添加到用户帐户时将图书详细信息存储在*Scenario Context对象中。*同样，在我们的下一个验证步骤中，我们从用户帐户中删除这本书。此外，我们从*ScenarioContext*检索所添加书籍的书籍详细信息，并验证其从用户帐户中的删除。

[***我们将按照与 Cucumber Step Definitions 共享 Scenario Context***](https://www.toolsqa.com/selenium-cucumber-framework/share-data-between-steps-in-cucumber-using-scenario-context/)相同的步骤来跨步骤共享测试数据信息状态：

1.  *首先，创建一个场景上下文类*。
2.  *其次，将测试信息状态保存在 Scenario Context*中。
3.  *第三，将 Verification Step 文件添加到 Test for Responses*。
4.  *第四，运行 Cucumber Test*。

### ***第 1 步：创建场景上下文类***

为项目中的所有固定值集创建枚举是一种简洁的代码实践。

1.  首先，右键单击***src/test/java*** 并选择 ***New >> Package***。此外，将其命名为 ***enums***。我们将在这个包中保留所有项目枚举。
2.  其次，右键单击***enums 包*** 并选择 ***New >> Enum***。将其命名为***Context***。此外，我们将在其中添加我们的枚举。

***Context.java***

```java
package enums;

public enum Context {
    BOOK,
    USER_ID,
    USER_ACCOUNT_RESPONSE,
    BOOK_REMOVE_RESPONSE;
}
```

1.  之后，*右键单击* cucumber ***包*** 并选择 ***New >> Class***。将其命名为 ***ScenarioContext***。

```java
package cucumber;


import enums.Context;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

    private Map<String, Object> scenarioContext;

    public ScenarioContext(){
        scenarioContext = new HashMap<String, Object>();
    }

    public void setContext(Context key, Object value) {
        scenarioContext.put(key.toString(), value);
    }

    public Object getContext(Context key){
        return scenarioContext.get(key.toString());
    }

    public Boolean isContains(Context key){
        return scenarioContext.containsKey(key.toString());
    }
}
```

***代码说明：***

-   ***scenarioContext**：它是一个**HashMap**对象，用于存储**Key-Value** 对中的信息。键类型是 **String**。此外，theValue 可以是任何 **Object** Type*。
-   ***setContext()**：这个方法有两个参数，key作为Context，value作为对象。Key 只不过是一个**Context** enum*。
-   ***getContext()**：该方法以键为参数，返回与该键匹配的对象*。
-   ***isContains()**：此方法检查完整的 Map 是否存在相应的键*。

1.  第四， 在 ***TextContext中包含******ScenarioContext***。它将确保使用 Pico-Container 库在所有 Cucumber Steps 之间共享***ScenarioContext 。***我们在上一章中添加了。此外，添加一个***getter*** 方法作为 ***getScenarioContext()*** 来获取 *scenarioContext* 对象。

***TestContext.java***

```java
package cucumber;

import apiEngine.EndPoints;
import enums.Context;

public class TestContext {
	
	private final String BASE_URL = "https://bookstore.toolsqa.com";
	private final String USER_ID = "9b5f49ab-eea9-45f4-9d66-bcf56a531b85";

	private EndPoints endPoints;
	private ScenarioContext scenarioContext;

	public TestContext() {
		endPoints = new EndPoints(BASE_URL);
		scenarioContext = new ScenarioContext();
		scenarioContext.setContext(Context.USER_ID, USER_ID);
	}

	public EndPoints getEndPoints() {
        return endPoints;
    }

	public ScenarioContext getScenarioContext() {
		return scenarioContext;
	}

}
```

此外，***BaseSteps.java*** 类更新为：

```java
package stepDefinitions;

import apiEngine.EndPoints;
import cucumber.ScenarioContext;
import cucumber.TestContext;

public class BaseStep {

   private EndPoints endPoints;
   private ScenarioContext scenarioContext;

    public BaseStep(TestContext testContext) {
    	endPoints = testContext.getEndPoints();
    	scenarioContext = testContext.getScenarioContext();
    }

    public EndPoints getEndPoints() {
        return endPoints;
    }
    
    public ScenarioContext getScenarioContext() {
        return scenarioContext;
    }
}
```

### ***步骤 2：将测试信息状态保存在 Scenario Context 中***



在这一步中，我们将请求的响应保存到 ScenarioContext 对象。首先，我们将这本书添加到用户的阅读列表中并保存我们的响应。随后，作为验证步骤的一部分，响应将在我们的断言中进行验证。

在***BooksSteps.java***类的以下步骤中，我们正在保存对场景上下文对象的响应。

***BooksSteps.java***

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
import cucumber.api.java.en.When;
import enums.Context;
import io.restassured.response.Response;

public class BooksSteps extends BaseStep {

    public BooksSteps(TestContext testContext) {
    	super(testContext);
    }

    @Given("^A list of books are available$")
    public void listOfBooksAreAvailable() {
        IRestResponse<Books> booksResponse = getEndpoints().getBooks();
        Book book = booksResponse.getBody().books.get(0);
        getScenarioContext().setContext(Context.BOOK, book);
    }

    @When("^I add a book to my reading list$")
    public void addBookInList() {
    	Book book = (Book) getScenarioContext().getContext(Context.BOOK);
    	String userId = (String) getScenarioContext().getContext(Context.USER_ID);
    	
        ISBN isbn = new ISBN(book.isbn);
        AddBooksRequest addBooksRequest = new AddBooksRequest(userId, isbn);
        
        IRestResponse<UserAccount> userAccountResponse = getEndpoints().addBook(addBooksRequest);
        getScenarioContext().setContext(Context.USER_ACCOUNT_RESPONSE, userAccountResponse);
    }

    @When("^I remove a book from my reading list$")
    public void removeBookFromList() {
    	Book book = (Book) getScenarioContext().getContext(Context.BOOK);
    	String userId = (String) getScenarioContext().getContext(Context.USER_ID);
    	
        RemoveBookRequest removeBookRequest = new RemoveBookRequest(userId, book.isbn);
        
        Response response = getEndpoints().removeBook(removeBookRequest);
        getScenarioContext().setContext(Context.BOOK_REMOVE_RESPONSE, response);
    }
}
```

### ***步骤 3：添加验证步骤文件以测试响应***

作为此步骤的一部分，我们将验证保存在 ScenarioContext 对象中的响应。我们将把所有的验证步骤移到一个创建为***VerificationSteps.java***的新类中。

通过 *右键单击* stepDefinitions ***创建***一个 ***新类***并将其命名为***VerificationSteps***。之后，选择***New >> Class***。

***VerificationSteps.java***

```java
package stepDefinitions;

import apiEngine.IRestResponse;
import apiEngine.model.Book;
import apiEngine.model.responses.UserAccount;
import cucumber.TestContext;
import cucumber.api.java.en.Then;
import enums.Context;
import io.restassured.response.Response;
import org.junit.Assert;

public class VerificationSteps extends BaseStep {

    public VerificationSteps(TestContext testContext) {
        super(testContext);
    }

    @Then("^The book is added$")
    public void bookIsAdded() {
    	Book book = (Book) getScenarioContext().getContext(Context.BOOK);
    	String userId = (String) getScenarioContext().getContext(Context.USER_ID);
        IRestResponse<UserAccount> userAccountResponse = (IRestResponse<UserAccount>) getScenarioContext().getContext(Context.USER_ACCOUNT_RESPONSE);

        Assert.assertTrue(userAccountResponse.isSuccessful());
        Assert.assertEquals(201, userAccountResponse.getStatusCode());

        Assert.assertEquals(userId, userAccountResponse.getBody().userID);
        Assert.assertEquals(book.isbn, userAccountResponse.getBody().books.get(0).isbn);
    }

    @Then("^The book is removed$")
    public void bookIsRemoved() {
    	String userId = (String) getScenarioContext().getContext(Context.USER_ID);
        Response response = (Response) getScenarioContext().getContext(Context.BOOK_REMOVE_RESPONSE);

        Assert.assertEquals(204, response.getStatusCode());

        IRestResponse<UserAccount> userAccountResponse = getEndPoints().getUserAccount(userId);
        Assert.assertEquals(200, userAccountResponse.getStatusCode());

        Assert.assertEquals(0, userAccountResponse.getBody().books.size());
    }
}
```

***解释***

-   *我们只需要将**Key**传递 给 **getContext()**方法，我们可以访问它，如**scenarioContext.getContext(Key Name)**。此外，它将检索 Book 响应返回的值*。
-   *由于该方法返回一个对象，我们需要将其转换为正确的类型。如果将其转换为错误的类型，则测试将在此处失败。因此，我们必须确定我们为相应的 key 存储的对象类型*。

### ***第 4 步：运行 Cucumber 测试***

***以 JUnit 运行测试***

我们现在都准备好运行更新的 Cucumber 测试了。首先，*右键单击* TestRunner ***类*** ，然后单击 ***Run As >> JUnit Test***。*Cucumber以与**Selenium WebDriver*相同的方式运行脚本。因此，结果将显示在控制台的*JUnit选项卡中。*

![在 Cucumber 中共享场景上下文](https://www.toolsqa.com/gallery/Rest%20Assured/1.Share%20Scenario%20Context%20in%20Cucumber.png)

***从 Cucumber 功能运行测试***

要将测试作为 Cucumber 功能运行，请右键单击*End2End_Test.feature*文件。选择*运行方式>>黄瓜特征*。

![图片：第 10 章黄瓜结果](https://www.toolsqa.com/gallery/Rest%20Assured/2.Image%20Chapter%2010%20Cucumber%20Results.png)

测试成功通过了我们为***在 Cucumber Steps 中使用***我们框架中的场景上下文共享测试数据所做的更改。

我们更新后的框架项目文件夹结构将如下所示：

![图片：第 10 章项目结构](https://www.toolsqa.com/gallery/Rest%20Assured/3.Image%20Chapter%2010%20Project%20Structure.png)

在下一章中，我们将为 BaseUrl 和 UserId 添加[***Config Reader 类***](https://www.toolsqa.com/rest-assured/configuration-reader/)。请尝试在您的框架中实现上述更改，如上所述。