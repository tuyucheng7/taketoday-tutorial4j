到目前为止，我们已经将我们的[***Rest Assured E2E API 测试***](https://toolsqa.com/rest-assured/rest-api-end-to-end-test/) 转换为[***Cucumber BDD 样式测试***](https://www.toolsqa.com/rest-assured/rest-api-test-in-cucumber/)。随后，我们的下一步将使用***序列化******将 JSON 转换为 JAVA 对象***。我们已经介绍了 Java 中的序列化和反序列化教程。如果您重新访问[***序列化和反序列化***](https://www.toolsqa.com/rest-assured/serialization-and-deserialization-in-java/)章节以更好地了解我们下一阶段框架开发的整体情况，我们将不胜感激。

## 将 JSON 转换为 JAVA 对象

我们现在发送的请求正文是这样的格式：

```java
request.body("{ \"userName\":\"" + USERNAME + "\", \"password\":\"" + PASSWORD + "\"}")
                .post("/Account/v1/GenerateToken");
```

我们以原始 JSON 字符串格式发送正文请求。以这种格式发送请求体，维护繁琐，容易出错。现在，我们只处理两个参数。但是，在实际的正文请求中，我们可能不得不处理更多的参数。

此外，建议将请求正文中的用户名和密码作为对象发送。为此，我们需要将 JSON 转换为 JAVA 对象。但是，网络不理解 Java 对象。因此，我们需要在发送请求之前将对象序列化为字符串。

我们可以使用许多可用的序列化-反序列化库来做到这一点。但是，Rest Assured 内置了此功能。它使我们能够直接在 Rest Assured 请求中发送对象，而库在内部负责序列化。如果您深入了解 RequestSpecification 的实现，您将看到下面的代码片段，它清楚地显示了 Rest Assured 如何处理序列化。

![Image-RequestSpecificationImpl](https://toolsqa.com/gallery/Rest%20Assured/1.Image-RequestSpecificationImpl.png)

所以我们现在将在下一节中了解将 JSON 请求转换为 Java 对象的过程。

### ***为请求正文创建 POJO 类***

我们专注于为我们的请求对象创建一个*POJO类。*所以，让我们学习用*JSON创建一个**POJO*类。让我们从请求正文中的一个简单请求示例开始：

考虑 API 端点：***/Account/v1/GenerateToken***

在此 API 的请求正文中，我们将用户名和密码作为请求正文发送。

![将 JSON 字符串转换为 JAVA 对象示例](https://toolsqa.com/gallery/Rest%20Assured/2.Convert%20JSON%20String%20to%20JAVA%20Object%20Example.png)

从上面的[***Swagger 书店 API***](https://bookstore.toolsqa.com/swagger/index.html)文档中可以看出，我们在请求体中传入的请求*JSON*体参数为：

```java
{
  "userName": "TOOLSQA-Test",
  "password": "Test@@123"
}
```

此外，您可以手动创建一个*POJO类，其中包含**JSON*正文的所有列出的字段。此外，还有各种免费在线可用的实用程序，我们可以使用它们将任何*JSON*结构转换为 Java *POJO*类。

#### ***如何使用在线实用程序为 JSON 请求正文创建 Java POJO 类？***



在这里，我们将看到***如何将 JSON 字符串转换为 Java 对象***。因此，让我们借助其中一个网站来帮助我们将*JSON*转换为 Java *POJO*类。

***请按照以下步骤操作***

-   *首先，导航到网站： http:* [***//www.jsonschema2pojo.org/***](https://www.jsonschema2pojo.org/)
-   *其次，在左侧文本框中输入 JSON 正文。*
-   *第三，在右侧面板中输入包名和类名。*
-   *最后，根据下图输入其他所需的选择详细信息。*

![使用 Online Utitlity 将 JSON 转换为 POJO 或 JAVA 对象](https://toolsqa.com/gallery/Rest%20Assured/3.Convert%20JSON%20to%20POJO%20or%20JAVA%20Object%20using%20Online%20Utitlity.png)

单击上图中突出显示的预览按钮。

显示以下图像：

![图像 json2schema 预览示例](https://toolsqa.com/gallery/Rest%20Assured/4.image%20json2schema%20preview%20example.png)

***解释***

因此，我们输入了一个带有用户名和密码作为字段的*JSON*，我们创建了一个*POJO*。此外，我们将使用此*POJO*发送到我们的 API ***/Account/v1/GenerateToken 的请求正文中。***

## 使用 POJO 类更新 JSON 字符串的步骤

我们将按照以下步骤在我们的框架中实现序列化：

1.  *首先，为我们的每个请求对象创建 POJO 类：*

-   *令牌请求*
-   *添加书籍请求*
-   *国际标准书号*
-   *删除图书请求*

1.  *其次，将 Step 文件中的 Request 主体替换为 POJO 类对象。*
2.  *最后，运行测试。*

### ***授权令牌请求的 POJO 类：***

![图片：AuthorizationRequest 生成令牌 API](https://toolsqa.com/gallery/Rest%20Assured/5.Image%20AuthorizationRequest%20Generate%20Token%20API.png)

如上图所示，来自我们的[***Swagger 书店 API 文档***](https://www.jsonschema2pojo.org/)的 Generate Token API，请求正文为：

```java
{ 
     "userName": "TOOLSQA-Test",
     "password": "Test@@123" 
}
```

要创建它的*POJO*类，请按照以下步骤操作：

1.  *首先，右键单击* src ***/test/java*** 并选择 **New >> Package**。之后，创建一个*New Package*文件并将其命名为*apiEngine。*在内部，***apiEngine*** *包*创建了一个名为 ***model****的新包*。此外，在这个***模型****Package*中，创建一个名称为***requests****的Package 。* 我们将捕获此包下的所有请求类。
2.  其次，在其下创建一个*New Class*文件并将其命名为***AuthorizationRequest***，*右键单击* 上面创建的 Package 并选择 ***New >> Class***。

***AuthorizationRequest.class***

```java
package apiEngine.model.requests;

public class AuthorizationRequest {

    public String username;
    public String password;

    public AuthorizationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
```

***代码说明***

Bookstore URL 要求客户端发送用户名和密码，正如我们在授权令牌的[***API 文档教程中学习的那样。***](https://www.toolsqa.com/rest-assured/api-documentation/)我们为此目的定义了*AuthorizationRequest类。*我们将使用带有参数用户名和密码的构造函数创建一个*AuthorizationRequest对象。*

同样，我们将为 Add Books Request、Remove Books Request 和*ISBN 创建类。*

### ***添加书籍请求的 POJO 类：***

正如我们在 Add Books API 的*Swagger 书店 API 文档* 中看到的，请求正文是：

```java
{
  "userId": "string",
  "collectionOfIsbns": [
    {
      "isbn": "string"
    }
  ]
}
```

要创建*JSON*请求正文的*POJO*类，请*右键单击*上面创建的***请求****包*并选择 ***New >> Class***。此外，将其命名为***AddBooksRequest***。

***AddBooksRequest.class***

```java
package apiEngine.model.requests;

import java.util.ArrayList;
import java.util.List;

public class AddBooksRequest {

    public String userId;
    public List<ISBN> collectionOfIsbns;
    
    //As of now this is for adding a single book, later we will add another constructor.
    //That will take a collection of ISBN to add multiple books
    public AddBooksRequest(String userId, ISBN isbn){
        this.userId = userId;
        collectionOfIsbns = new ArrayList<ISBN>();
        collectionOfIsbns.add(isbn);
    }

}
```

***代码说明***

AddBooksRequest类将负责为我们提供一个将一本书添加到用户帐户的对象*。*在此过程中，我们需要 userId 和图书的唯一标识符 ISBN。因此，*userID*和*isbn*作为参数传递给*AddBooksRequest*类对象。

### ***ISBN 的 POJO 类：***

右键单击上面创建的***请求**包*。之后，选择 ***New >> Class***。将其命名为***ISBN***。

```java
package apiEngine.model.requests;

public class ISBN {
    public String isbn;

    public ISBN(String isbn) {
        this.isbn = isbn;
    }
}
```

***代码说明***

我们创建了 ISBN 类以在 AddBooksRequest 类中用于存储 ISBN 类型的集合。

### ***删除图书请求的 POJO 类：***

正如我们在 Remove Books Endpoint 的 Bookstore API 中看到的，请求正文是：

```java
{
  "isbn": "string",
  "userId": "string"
}
```

要创建 JSON 请求正文的 POJO 类，请*右键单击*上面创建的***请求**包*并选择 ***New >> Class***。此外，将其命名为***RemoveBookRequest***。

```java
package apiEngine.model.requests;

public class RemoveBookRequest {
    public String isbn;
    public String userId;

    public RemoveBookRequest(String userId, String isbn) {
    	this.userId = userId;
    	this.isbn = isbn;
    }
}
```

***代码说明***

*在RemoveBooksRequest*类的帮助下，我们将通过传递参数*userId*和*isbn*创建一个对象。此外，请求正文使用此对象。

### ***用 POJO 类对象替换 Step 文件中的请求正文***

让我们首先从测试的第一步开始，即***“Given("^I am an authorized user$")”***

我们对其步骤定义进行了以下更改：

```java
  @Given("^I am an authorized user$")
    public void iAmAnAuthorizedUser() {
        AuthorizationRequest authRequest = new AuthorizationRequest("TOOLSQA-Test","Test@@123");

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        response = request.body(authRequest).post("/Account/v1/GenerateToken");

        String jsonString = response.asString();
        token = JsonPath.from(jsonString).get("token");
    }
```

***代码说明***

我们创建了一个对象， AuthorizationRequest 类的*authRequest* *。*在这个对象中，我们传递*用户名*和*密码。*此外，此*authRequest* 对象将传入请求的正文。

同样，我们将对以下步骤定义进行更改，以使用我们使用我们定义的 POJO 创建的 Java 对象。在内部，放心库会在通过网络发送请求之前将此对象序列化为 JSON 字符串。因此，我们不必担心序列化请求对象。

```java
@When("^I add a book to my reading list$")
@When("I remove a book from my reading list")
```

另外，请注意，对于剩余的三个步骤定义，我们暂时不会进行任何更改。

```java
@Given("A list of books are available")
@Then("^The book is added$")
@Then("^The book is removed$")
```

我们将所有这些代码更改放在了 Steps 文件中

```java
package stepDefinitions;

import java.util.List;
import java.util.Map;

import apiEngine.model.requests.AddBooksRequest;
import apiEngine.model.requests.AuthorizationRequest;
import apiEngine.model.requests.ISBN;
import apiEngine.model.requests.RemoveBookRequest;
import org.junit.Assert;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Steps {

    private static final String USER_ID = "9b5f49ab-eea9-45f4-9d66-bcf56a531b85";
    private static final String BASE_URL = "https://bookstore.toolsqa.com";

    private static String token;
    private static Response response;
    private static String jsonString;
    private static String bookId;


    @Given("^I am an authorized user$")
    public void iAmAnAuthorizedUser() {
        AuthorizationRequest credentials = new AuthorizationRequest("TOOLSQA-Test","Test@@123");

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        response = request.body(credentials).post("/Account/v1/GenerateToken");

        String jsonString = response.asString();
        token = JsonPath.from(jsonString).get("token");
    }

    @Given("^A list of books are available$")
    public void listOfBooksAreAvailable() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        response = request.get("/BookStore/v1/Books");

        jsonString = response.asString();
        List<Map<String, String>> books = JsonPath.from(jsonString).get("books");

        bookId = books.get(0).get("isbn");
    }

    @When("^I add a book to my reading list$")
    public void addBookInList() {

        AddBooksRequest addBooksRequest = new AddBooksRequest(USER_ID, new ISBN(bookId));

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

          response = request.body(addBooksRequest).post("/BookStore/v1/Books");

    }

    @Then("^The book is added$")
    public void bookIsAdded() {
        Assert.assertEquals(201, response.getStatusCode());
    }

    @When("^I remove a book from my reading list$")
    public void removeBookFromList() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        RemoveBookRequest removeBookRequest = new RemoveBookRequest(USER_ID, bookId);
        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        response = request.body(removeBookRequest).delete("/BookStore/v1/Book");
    }

    @Then("^The book is removed$")
    public void bookIsRemoved(){
        Assert.assertEquals(204, response.getStatusCode());

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        response = request.get("/Account/v1/User/" + USER_ID);
        Assert.assertEquals(200, response.getStatusCode());

        jsonString = response.asString();
        List<Map<String, String>> booksOfUser = JsonPath.from(jsonString).get("books");
        Assert.assertEquals(0, booksOfUser.size());
    }

}
```

***注意**：我们为 JSONpath 添加了一个 import 语句**import io.restassured.path.json.JsonPath**；它将帮助我们遍历 JSON 的特定部分。此外，您可以在[***JSONPath 文章***](https://www.toolsqa.com/rest-assured/jsonpath-and-query-json-using-jsonpath/)中阅读更多内容。*

现在，如果我们尝试在对 Steps 文件进行所有更改后使用***TestRunner运行测试，我们的测试将******失败***。

我们会遇到的***Runtime-Exception是：***

```java
java.lang.IllegalStateException: Cannot serialize object because no JSON serializer found in classpath. Please put Jackson (Databind), Gson, Johnzon, or Yasson in the classpath.
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(Unknown Source)
	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(Unknown Source)
	at java.lang.reflect.Constructor.newInstance(Unknown Source)
	at org.codehaus.groovy.reflection.CachedConstructor.invoke(CachedConstructor.java:80)
	at org.codehaus.groovy.runtime.callsite.ConstructorSite$ConstructorSiteNoUnwrapNoCoerce.callConstructor(ConstructorSite.java:105)
	at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallConstructor(CallSiteArray.java:59)
	at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callConstructor(AbstractCallSite.java:237)
	at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callConstructor(AbstractCallSite.java:249)
	at io.restassured.internal.mapping.ObjectMapping.serialize(ObjectMapping.groovy:160)
	at io.restassured.internal.mapping.ObjectMapping$serialize.call(Unknown Source)
	at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCall(CallSiteArray.java:47)
	at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:115)
	at io.restassured.internal.RequestSpecificationImpl.body(RequestSpecificationImpl.groovy:751)
	at stepDefinitions.Steps.iAmAnAuthorizedUser(Steps.java:38
```

我们在整个节目中缺少一个关键元素。如上文第一节所述，Rest Assured 在内部负责序列化。我们还在代码片段中确认了这一点。Rest Assured 库依赖于[***Jackson (Databind)***](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind/2.10.2)库来完成序列化的这项工作。由于我们还没有添加这个库，所以我们遇到了错误。

让我们在我们的框架项目文件 pom.xml 中快速添加依赖：

```java
<!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.10.2</version>
</dependency>
```

***注意：**截至 2019 年 2 月，jackson-databind 的最新可用依赖项为 2.10.2 版。请在构建框架时使用最新的依赖项。*

我们的新 POM 文件将如下所示：

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="https://maven.apache.org/POM/4.0.0" xmlns:xsi="https://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="https://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
   <modelVersion>4.0.0</modelVersion>
   <groupId>ToolsQA</groupId>
   <artifactId>APITestingFramework</artifactId>
   <version>1.0-SNAPSHOT</version>
   <dependencies>
      <dependency>
         <groupId>junit</groupId>
         <artifactId>junit</artifactId>
         <version>4.13</version>
         <scope>test</scope>
      </dependency>
      <dependency>
         <groupId>io.rest-assured</groupId>
         <artifactId>rest-assured</artifactId>
         <version>4.2.0</version>
         <scope>test</scope>
      </dependency>
      <dependency>
         <groupId>io.cucumber</groupId>
         <artifactId>cucumber-java</artifactId>
         <version>5.2.0</version>
      </dependency>
      <dependency>
         <groupId>io.cucumber</groupId>
         <artifactId>cucumber-jvm-deps</artifactId>
         <version>1.0.6</version>
         <scope>provided</scope>
      </dependency>
      <dependency>
         <groupId>io.cucumber</groupId>
         <artifactId>cucumber-junit</artifactId>
         <version>5.2.0</version>
         <scope>test</scope>
      </dependency>
      <dependency>
         <groupId>com.fasterxml.jackson.core</groupId>
         <artifactId>jackson-databind</artifactId>
         <version>2.10.2</version>
      </dependency>
   </dependencies>
   <build>
      <plugins>
         <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.7.0</version>
            <configuration>
               <source>1.8</source>
               <target>1.8</target>
               <encoding>UTF-8</encoding>
            </configuration>
         </plugin>
      </plugins>
   </build>
</project>
```

#### ***运行黄瓜测试***

***以 JUnit 运行测试***

现在我们都准备好运行更新的 Cucumber 测试了。*右键单击* TestRunner ***类***，然后单击***Run As >> JUnit Test。*** 因此，结果将显示在控制台的*JUnit选项卡中。*

![第三章 Junit 测试结果](https://toolsqa.com/gallery/Rest%20Assured/6.Chapter%203%20Junit%20test%20result.png)

***从 Cucumber 功能运行测试***

要将测试作为 Cucumber 功能运行，请右键单击 End2End_Test.feature 文件。之后，选择*运行方式>>黄瓜功能。*

![将 JSON 转换为 POJO 对象执行结果](https://toolsqa.com/gallery/Rest%20Assured/7.Convert%20JSON%20to%20POJO%20Object%20Execution%20Results.png)

正如您在上面附加的屏幕截图中看到的，我们的测试已经通过更改。我们在本章中使用序列化*将 JSON 转换为 JAVA 对象。*请尝试在您的框架中实现它，如上所述。下面是当前项目浏览器的屏幕截图。

![图片：第 3 章文件夹结构](https://toolsqa.com/gallery/Rest%20Assured/8.Image%20Chapter%203%20Folder%20Structure.png)

随后，我们将在下一章访问[***将 JSON 响应体转换为 POJO 。***](https://www.toolsqa.com/rest-assured/convert-json-to-java-object/)