在我们之前的教程中，我们编写了一个简单的[***End to End Rest API Test***](https://toolsqa.com/rest-assured/rest-api-end-to-end-test/)。用例的业务流程被转换为简单的 API 请求和响应格式***GET、POST 和 DELETE 请求***。随后，我们的下一步是转换***Cucumber 中的 REST API 测试***。在此过程中，我们需要了解***Cucumber BDD 框架***。[***Cucumber 教程***](https://www.toolsqa.com/cucumber-tutorial/) 相对容易理解，在我们深入研究之前将是一本很好的入门书。

现在，我们从 Cucumber 中的 Rest API 测试开始。

## Cucumber BDD 样式测试中的 REST API 测试

我们将使用***Cucumber BDD 框架***来执行我们的测试。此外，这将要求我们***将我们的 Rest Assured API 测试转换为 Cucumber BDD 样式测试***。

以下步骤将帮助我们实现这一目标：

1.  *将 Cucumber 依赖项添加到项目中*
2.  *在功能文件中编写测试*
3.  *创建测试运行器*
4.  *将测试代码写入 Step 文件*
5.  *作为 JUnit 测试和 Maven 命令行运行测试*

### ***第 1 步：将 Cucumber 依赖项添加到项目中***

首先，将以下依赖项添加到我们的项目中以在 Cucumber 中执行我们的测试：

-   *黄瓜-java*
-   *黄瓜-jvm-deps*
-   *黄瓜-JUnit*

当我们在 Maven 中开发框架时，添加依赖项会很有用。因此，将以下依赖项添加到项目 POM XML 中。

***cucumber-java：***依赖细节的位置在 [ ***Maven Repository*** ]tps://mvnrepository.com/artifact/io.cucumber/cucumber-junit/5.2.0)。

```java
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>5.2.0</version>
</dependency>
```

***注意**：截至 2020 年 2 月，最新的 cucumber-java 版本为 5.2.0。*

***cucumber-jvm-deps：***依赖细节的位置在[ ***Maven Repository*** ]。（https://mvnrepository.com/artifact/io.cucumber/cucumber-jvm-deps）

```java
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-jvm-deps</artifactId>
    <version>1.0.6</version>
    <scope>provided</scope>
</dependency>
```

***注意**：截至 2020 年 2 月，最新的 cucumber-jvm-deps 版本为 1.0.6*

***cucumber-junit：***依赖细节的位置在[***Maven Repository***](https://mvnrepository.com/artifact/info.cukes/cucumber-junit/1.2.5)。

```java
<dependency>
    <groupId>io.cucumber<</groupId>
    <artifactId>cucumber-junit</artifactId>
    <version>5.2.0</version>
    <scope>test</scope>
</dependency>
```

***注意**：截至 2020 年 2 月，最新的 cucumber-junit 版本为 5.2.0*

因此，完整的 pom.xml 将如下所示：

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="https://maven.apache.org/POM/4.0.0" xmlns:xsi="https://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="https://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
   <modelVersion>4.0.0</modelVersion>
   <groupId>ToolsQA</groupId>
   <artifactId>RestAssured_APITests</artifactId>
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

***注意**：通过右键单击项目根目录并选择 Maven **>> 更新项目**来更新 POM 。此操作将使基于依赖项的问题无效。*

***附加说明**：如果您愿意在项目库中添加 jars 而不是使用 Maven 依赖项，那也可以。*

### ***第 2 步：在功能文件中编写测试***

其次，我们强烈建议您熟悉有关[***功能文件***](https://mvnrepository.com/artifact/info.cukes/cucumber-junit/1.2.5)的教程。它将有助于理解 Cucumber 功能文件的基础知识。因此，我们将开始将我们的测试场景转换为 Cucumber Feature 文件。

在上一章中，我们将场景分解为多个部分，以便我们轻松地将场景转换为步骤。让我们再次访问场景，

1.  *测试将首先生成一个用于授权的令牌*
2.  *获取图书馆中可用书籍的列表*
3.  *将列表中的一本书添加到用户*
4.  *从图书列表中删除添加的图书*
5.  *确认图书移除是否成功*

***将上述场景转换为 Cucumber BDD 样式测试：***

-   ***背景：用户生成授权令牌***

    ***鉴于我是授权用户***

-   ***场景：授权用户可以添加和删除一本书。***

    ***给定的书籍列表可用***

    ***当我将一本书添加到我的阅读列表时***

    ***然后添加了书***

    ***当我从阅读列表中删除一本书时***

    ***然后这本书被删除***

#### ***创建特征文件***

1.  首先，创建一个***新包*** 并将其命名为 ***functionalTests。*** *您可以通过右键单击* src ***/test/resources*** 并选择 ***New >> Package***来完成。

***注意**：始终建议将所有功能文件放在 **资源**文件夹中。*

1.  其次，创建一个**Feature** 文件并将其命名为 ***End2End_Test.feature***通过右键单击上面创建的包并选择***New >> File***。

![Cucumber 中的 REST API 测试 - 如何创建新文件](https://toolsqa.com/gallery/Rest%20Assured/1.REST%20API%20Test%20in%20Cucumber%20-%20How%20to%20create%20new%20File.webp)

将***.feature*** 扩展名添加到文件中。

![在 Cucumber 中创建 REST API 测试](https://toolsqa.com/gallery/Rest%20Assured/2.Create%20REST%20API%20Test%20in%20Cucumber.webp)

1.  最后，将测试步骤添加到功能文件中。

![图片：End2End 功能文件](https://toolsqa.com/gallery/Rest%20Assured/3.%20End2End%20Feature%20file.png)

***注意**：如您所知，测试步骤中使用的关键字以不同的颜色显示。这些是 Gherkin 关键字。Eclipse 不理解这些。但是，如果我们安装 cucumber Eclipse 插件，这将被识别。请按照我们的教程[***安装 Cucumber Eclipse 插件***](https://toolsqa.com/cucumber/install-cucumber-eclipse-plugin/)*。

### ***第 3 步：创建 JUnit 测试运行器***

第三，我们将创建一个 Cucumber Test Runner 来执行我们的测试。此外，我们需要一个基于 JUnit Test Runner 的 Cucumber Test Runner 来进行测试。要了解有关 Cucumber Test Runner 的更多信息，请参阅教程中的[***JUnit Test Runner***](https://www.toolsqa.com/cucumber/junit-test-runner-class/)。

1.  首先，*右键单击* src ***/test/java****并使用**New >> Package***选择一个 New Package 。将其命名为***runners***。
2.  之后， 通过 *右键单击*上面创建的包并选择***New >> Class*** *，创建一个新的 Java Class* 文件并将其命名为 ***TestRunner***。

```java
package runners;
 

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/functionalTests",
)
public class TestRunner {
}
```

注意：不要在创建运行器类时选择 public static void main。

### ***第 4 步：将测试代码写入 Step 文件***

第四，当我们在下一步将测试代码转换为 Step 文件时，我们需要修改我们对[***Cucumber Step Definitions***](https://www.toolsqa.com/cucumber/step-definition/)的知识。

1.  为了减少我们在步骤创建中的工作量，我们将自动生成非常需要的 Cucumber Steps 以供实施。因此，我们将为此执行***TestRunner***类。*右键单击* TestRunner *文件* 并选择 ***Run As >> JUnit Test***。因此，您将在 Eclipse 控制台中获得以下结果。

![图片：创建了未实现的测试步骤](https://toolsqa.com/gallery/Rest%20Assured/4.Image%20Unimplemented%20Test%20Steps%20created.png)

1.  创建一个***新包***并将其命名为***stepDefinitions*** ，方法 是 *右键单击* src */test/java* 并选择 ***New >> Package***。
2.  之后， 通过 *右键单击*上面创建的包并选择***New >> Class ，创建一个******New Java Class*** 并将其命名为 ***Steps***。
3.  最后，将 Eclipse 创建的所有步骤复制到此*Steps*文件中，并开始使用 Selenium Code 填充这些步骤。***从我们在End2End_Test 中***创建的 Selenium 测试文件中选择所有代码。Steps 测试文件将如下所示：

```java
package stepDefinitions;

import java.util.List;
import java.util.Map;

import org.junit.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Steps {
	private static final String USER_ID = "9b5f49ab-eea9-45f4-9d66-bcf56a531b85";
	private static final String USERNAME = "TOOLSQA-Test";
	private static final String PASSWORD = "Test@@123";
	private static final String BASE_URL = "https://bookstore.toolsqa.com";

	private static String token;
	private static Response response;
	private static String jsonString;
	private static String bookId;


	@Given("I am an authorized user")
	public void iAmAnAuthorizedUser() {

		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();

		request.header("Content-Type", "application/json");
		response = request.body("{ \"userName\":\"" + USERNAME + "\", \"password\":\"" + PASSWORD + "\"}")
				.post("/Account/v1/GenerateToken");

		String jsonString = response.asString();
		token = JsonPath.from(jsonString).get("token");

	}

	@Given("A list of books are available")
	public void listOfBooksAreAvailable() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();
		response = request.get("/BookStore/v1/Books");

		jsonString = response.asString();
		List<Map<String, String>> books = JsonPath.from(jsonString).get("books");
		Assert.assertTrue(books.size() > 0);

		bookId = books.get(0).get("isbn");	   
	}

	@When("I add a book to my reading list")
	public void addBookInList() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", "Bearer " + token)
		.header("Content-Type", "application/json");

		response = request.body("{ \"userId\": \"" + USER_ID + "\", " +
				"\"collectionOfIsbns\": [ { \"isbn\": \"" + bookId + "\" } ]}")
				.post("/BookStore/v1/Books");
	}

	@Then("The book is added")
	public void bookIsAdded() {
		Assert.assertEquals(201, response.getStatusCode());
	}

	@When("I remove a book from my reading list")
	public void removeBookFromList() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();

		request.header("Authorization", "Bearer " + token)
		.header("Content-Type", "application/json");

		response = request.body("{ \"isbn\": \"" + bookId + "\", \"userId\": \"" + USER_ID + "\"}")
				.delete("/BookStore/v1/Book");


	}

	@Then("The book is removed")
	public void bookIsRemoved() {
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

***注意**：与我们创建的 E2E 测试相比，我们进行了以下更改：*

-   *将变量声明为**private static final**以不允许在类外进行更改。*
-   *我们更新了方法名称，并且与自动生成的名称不同。*

1.  TestRunner 文件必须能够找到步骤文件***。***为此，我们需要提及包的路径。这条路径在 CucumberOptions 中有我们所有的步骤定义。此外，要了解更多关于我们可以在 @CucumberOptions 中添加的参数，请访问我们的教程[***Cucumber Options***](https://toolsqa.com/cucumber/cucumber-options/)。

```java
package runners;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/functionalTests",
        glue = {"stepDefinitions"},
        monochrome = true,
        strict = true
)

public class TestRunner {

}
```

***注意**：默认情况下，Junit/Cucumber 在**src/test/java**文件夹中查找测试代码。因此，这就是为什么我们只需要指定黄瓜胶的包名。*

### ***第 5 步：运行 Cucumber 测试***



***作为 JUnit 运行***

最后，我们都准备好运行第一个 Cucumber 测试。*右键单击* TestRunner ***类*** ，然后单击 ***Run As >> JUnit Test。*** *Cucumber将按照在**Selenium WebDriver*中运行的方式执行脚本。*因此，结果将出现在JUnit* 选项卡的左侧 *项目浏览器窗口*中 。

![图片：Junit 结果将 Rest Assured 测试转换为 Cucumber](https://toolsqa.com/gallery/Rest%20Assured/5.Image%20Junit%20Results%20Convert%20Rest%20Assured%20test%20to%20Cucumber.png)

***从命令提示符运行测试***

我们有一个 Maven 类型项目，因此，我们也可以从命令提示符运行我们的测试。运行测试的一个简单命令是*mvn clean test。*此外，要使用此命令，我们必须将目录更改为 Cucumber 项目的位置。在下面的屏幕截图中，首先我去了我的项目位置，然后我使用上面提到的 Maven 命令来运行测试。

![图片：命令行 mvn clean test](https://toolsqa.com/gallery/Rest%20Assured/6.Image%20Command%20line%20mvn%20clean%20test.png)

因此，我们可以在命令提示符下看到以下测试的输出。

![图片：命令行结果](https://toolsqa.com/gallery/Rest%20Assured/7.Image%20Command%20Line%20Results.webp)

总而言之，到目前为止，我们已经将我们的***Rest Assured API 测试转换为基于 Cucumber 的测试***。随后，我们将看到如何在我们的框架中实现 Java 序列化概念并对其进行增强。[***此外，在此阶段，在继续下一个框架教程之前，将 JSON 转换为 JAVA 对象***](https://www.toolsqa.com/rest-assured/convert-json-to-java-object/)教程作为复习会很有用。