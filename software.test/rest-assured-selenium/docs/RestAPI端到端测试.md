在本教程中，我们将学习编写***REST API 端到端测试。***

我们精心奠定了 API 自动化框架组件的基础。我们将在很大程度上依赖我们之前从***Rest Assured、Cucumber***和***Maven***获得的知识。随后，这些将构成我们构建*API 自动化框架的基础。*我会不时提及它们，并在我们需要更多链接时添加有用的链接。建议通过阅读教程来清除基础知识：

1.  [***放心***](https://www.toolsqa.com/rest-assured-tutorial/)
2.  [***黄瓜***](https://www.toolsqa.com/cucumber-tutorial/)
3.  [***马文***](https://www.toolsqa.com/maven/maven-introduction/)

在我们的自动化框架中，我们将自动化用例的端到端业务流程。此外，它将有助于演示框架组件的正确示例并解释它们的用法。此外，随着我们学习本教程，我们对从头开始构建***Rest Assured API 自动化框架的理解会有所提高。***

很多时候，我们参与获取由高级测试架构师或软件开发人员构建的现成框架。了解基本原理和设计理念对于培养我们作为软件工程师的技能至关重要。

## REST API 端到端测试的先决条件

1.  *Java 设置*
2.  *IDE 设置*
3.  *Maven 设置*
4.  *创建一个maven项目*
5.  *添加放心依赖*
6.  *设置 Maven 编译器插件*
7.  *为测试创建用户*

### ***第 1 步 - Java 设置***

我们将使用 Java 作为我们的语言，用于编写基于 Rest Assured 库的 REST API 自动化框架。为此，如果以前没有安装，我们将需要在我们的机器上安装 Java。同样，请按照教程[***安装 Java***](https://www.toolsqa.com/selenium-webdriver/install-java/)作为我们的第一个先决条件。

### ***第 2 步 - IDE 设置***

由于我们将使用 Java，因此我们需要一个用于 Java 的编辑器。***Eclipse、IntelliJ、Net Beans***和其他几个是您可以选择使用的流行 IDE。此外，还有一个关于在 Windows和[***Mac***](https://toolsqa.com/maven/how-to-install-maven-on-mac/)用户上安装[***Eclipse***](https://www.toolsqa.com/selenium-webdriver/download-and-start-eclipse/)的教程来简化这个过程。请选择一个您喜欢使用的 IDE 并开始使用。

### ***第 3 步 - Maven 设置***

构建工具使我们能够从源代码创建可执行应用程序。它们有助于自动化日常活动并编写脚本，例如下载依赖项、编译、运行我们的测试和部署。此外，我们将为端到端场景使用 Maven 构建工具。我们创建了一个教程来解释[***在 Windows 上安装 Maven***](https://toolsqa.com/maven/how-to-install-maven-on-windows/)。此外，请安装 Maven，如果以前没有安装，因为我们需要它。

### ***第 4 步：创建一个新的 Maven 项目***

Eclipse 使用您添加项目的工作区、文件夹和空间进行操作。软件团队对工作区和项目结构使用不同的方法，但尽量遵循并坚持默认结构。

除此之外，要创建一个新的 Maven 项目，请按照我们的文章 [***Steps to create a New Maven Project***](https://toolsqa.com/maven/create-new-maven-project/)进行操作。只是为了确保您为 Maven Archetype 参数指定以下值：

-   *组 ID：ToolsQA*
-   *工件 ID：API 测试框架*

![新的 Maven 项目 - REST API 自动化框架](https://toolsqa.com/gallery/Rest%20Assured/1.New%20Maven%20Project%20-%20REST%20API%20Automation%20Framework.webp)

***注意**：您可以使用任何名称作为组 ID 和工件 ID。但是，为了方便本教程，最好在整个项目中保持相同的命名约定。此外，它有助于在复制粘贴代码时解决问题。*

### ***第 5 步 - 添加放心依赖项***

我们将通过 pom.xml 文件将***Rest Assured Dependencies添加到我们的项目中。***要添加所需的依赖项，请转到[***Rest Assured Maven Repository。***](https://mvnrepository.com/artifact/io.rest-assured/rest-assured)然后，选择最新的依赖项。将其复制粘贴到项目***pom.xml***文件中。

```java
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>4.2.0</version>
    <scope>test</scope>
</dependency>
```

*注：截至2020年2月，最新放心版本为4.2.0。*

我们应该在依赖标签中添加依赖标签，如下所示。

```java
<dependencies>
   <dependency>Project Dependency</dependency>
</dependencies>
```

此外，我们将使用***JUnit Dependency***。所以请在 pom.xml 中添加该依赖项。

```java
  <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13</version>
        <scope>test</scope>
  </dependency>
```

***注意**：截至 2020 年 2 月，最新的 JUnit 版本为 4.13。始终使用最新的 Junit 版本。*

### ***第 6 步：设置 Maven 编译器插件***

***Compiler Plugin***编译项目的源代码。无论您使用哪种 JDK 运行 Maven，默认源设置为 1.5，默认目标设置为 1.5。此外，要更改默认值，请按照设置 Java 编译器的 -source 和 -target 中的说明设置***源*** 和***目标*** 。

您可以在下面找到 ***maven-compiler-plugin*** 设置：

```java
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
```

完整的 POM 也会变成：

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="https://maven.apache.org/POM/4.0.0"
         xmlns:xsi="https://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
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

### ***第 7 步：为测试创建授权用户***

最后，在我们开始自动化测试之前，还剩下最后一块。它是创建授权用户。我们需要创建一个用户来使用我们的端到端自动化测试。我们在[***之前的教程***](https://www.toolsqa.com/rest-assured/api-documentation/)中学习了在 POST 请求下创建用户。[***POST 请求教程***](https://www.toolsqa.com/rest-assured/post-request-using-rest-assured/)解释了 POST 请求方法如何将数据发送到服务器。

小号

```java
    @Test
    public void RegistrationSuccessful() {
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();
        /*I have put a unique username and password as below,
        you can enter any as per your liking. */
        requestParams.put("UserName", "TOOLSQA-Test");
        requestParams.put("Password", "Test@@123");

        request.body(requestParams.toJSONString());
        Response response = request.post("/Account/v1/User");

        Assert.assertEquals(response.getStatusCode(), 201);
        // We will need the userID in the response body for our tests, please save it in a local variable
        String userID = response.getBody().jsonPath().getString("userID");
    }
```

## 编写 REST API 端到端测试：



让我们考虑这种情况：-

***测试场景：作为现有授权用户，我检索图书馆中可供我使用的书籍列表。我会给自己分配一本书，然后再归还。***

为简单起见，我们将测试场景分为以下步骤：

***1.测试将从生成授权令牌开始-**首先，我们有一个注册用户的用户名和密码。使用这些凭据，我们将生成一个令牌。此外，我们会将这个令牌发送到请求中，而不是用户名和密码。我们遵循这种做法的原因是要以有时限的方式分配特定的资源。此步骤涉及通过传递用户名和密码在 Rest Assured 中进行 POST 请求调用。请按照此[***POST 请求教程***](https://www.toolsqa.com/rest-assured/post-request-using-rest-assured/)了解如何发送 POST 请求。*

***注意**：下面提到的用户将不起作用，请创建您的用户进行练习。*

***2. 获取图书馆中可用书籍的列表 -**其次，它是一个 GET 请求调用。它为我们提供了可用书籍的列表。访问[***GET 请求教程***](https://www.toolsqa.com/rest-assured/rest-api-test-using-rest-assured/)以了解我们如何在 Rest-Assured 中进行 GET 请求调用的逻辑。*

***3. 从列表中向用户添加一本书 -**第三个是 POST 请求调用。我们将在请求中发送用户和书籍详细信息。*

***4. 从书籍列表中删除添加的书籍 -**第四是删除请求调用。我们将从列表中删除添加的书籍。[***删除请求教程***](https://www.toolsqa.com/rest-assured/delete-request-using-rest-assured/)将为您提供帮助。*

***5. 确认图书删除是否成功 -**最后但同样重要的是，作为验证步骤，我们将验证图书是否已从用户手中删除。因此，我们将在 GET 请求调用中发送用户详细信息以获取用户的详细信息。*

### ***创建一个测试包和一个测试类***

1.  首先通过*右键单击*src ***/test/java***包并选择***New >> Package创建一个******新***包。此外，为您的包命名***apiTests***并单击***Finish***。
2.  其次，通过*右键单击*src ***/test/java***包并选择***New >> Class创建一个******New Class***文件。在出现的对话框中为您的测试用例提供正确的名称，然后单击***完成***以创建文件。此外，为了确保检查***public static void main，***因为我们将从相同的主要方法运行测试。我已将该类命名为***E2E_Tests***，您可以在下面的代码片段中看到。

![REST API 端到端测试](https://toolsqa.com/gallery/Rest%20Assured/2.REST%20API%20End%20to%20End%20Test.webp)

### ***编写完整的 REST API 端到端测试***

我已经编写了包含上述步骤的端到端测试。这个例子比较容易理解。此外，它还涉及*GET、POST 和 DELETE*请求的使用。如果您不遵循这一点，那么如果您在这一点上阅读“[***放心教程***](https://www.toolsqa.com/rest-assured-tutorial/)”，这将使您受益。

```java
package apiTests;

import java.util.List;
import java.util.Map;

import org.junit.Assert;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class E2E_Tests {

    public static void main(String[] args) {
        String userID = "9b5f49ab-eea9-45f4-9d66-bcf56a531b85";
        String userName = "TOOLSQA-Test";
        String password = "Test@@123";
        String baseUrl = "https://bookstore.toolsqa.com";

        RestAssured.baseURI = baseUrl;
        RequestSpecification request = RestAssured.given();


        //Step - 1
        //Test will start from generating Token for Authorization
        request.header("Content-Type", "application/json");

        Response response = request.body("{ \"userName\":\"" + userName + "\", \"password\":\"" + password + "\"}")
                .post("/Account/v1/GenerateToken");

        Assert.assertEquals(response.getStatusCode(), 200);

        String jsonString = response.asString();
        Assert.assertTrue(jsonString.contains("token"));

        //This token will be used in later requests
        String token = JsonPath.from(jsonString).get("token");


        //Step - 2
        // Get Books - No Auth is required for this.
        response = request.get("/BookStore/v1/Books");

        Assert.assertEquals(response.getStatusCode(), 200);

        jsonString = response.asString();
        List<Map<String, String>> books = JsonPath.from(jsonString).get("books");
        Assert.assertTrue(books.size() > 0);

         //This bookId will be used in later requests, to add the book with respective isbn
        String bookId = books.get(0).get("isbn");


        //Step - 3
        // Add a book - with Auth
        //The token we had saved in the variable before from response in Step 1, 
        //we will be passing in the headers for each of the succeeding request
        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        response = request.body("{ \"userId\": \"" + userID + "\", " +
                "\"collectionOfIsbns\": [ { \"isbn\": \"" + bookId + "\" } ]}")
                .post("/BookStore/v1/Books");

        Assert.assertEquals( 201, response.getStatusCode());


        //Step - 4
        // Delete a book - with Auth
        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        response = request.body("{ \"isbn\": \"" + bookId + "\", \"userId\": \"" + userID + "\"}")
                .delete("/BookStore/v1/Book");

        Assert.assertEquals(204, response.getStatusCode());

        //Step - 5
        // Get User
        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        response = request.get("/Account/v1/User/" + userID);
        Assert.assertEquals(200, response.getStatusCode());

        jsonString = response.asString();
        List<Map<String, String>> booksOfUser = JsonPath.from(jsonString).get("books");
        Assert.assertEquals(0, booksOfUser.size());
    }
}
```

***注意**：我们为 JSONpath 添加了一个 import 语句**import io.restassured.path.json.JsonPath**；它将帮助我们遍历 JSON 的特定部分。您可以在**JSONPath 文章**中阅读更多内容。*

### ***运行 REST API 测试***

下一步，我们需要执行测试。右键单击测试主体并选择 ***Run As >> Java Application***。测试将运行，您将在控制台中看到结果。因此，程序成功执行，没有任何错误。万一您碰巧在 IntelliJ IDE 上进行了配置，观察到的效果将是“ ***Process finished with exit code 0*** ”。此外，这是另一个标志，表明没有错误并且我们的测试成功执行。

***注意**：它不是基于 UI 的测试。此外，不会有任何视觉输出来观察测试执行。*

最后，在下一个教程中，我们会将[***我们的 API 测试转换为 Cucumber BDD***](https://www.toolsqa.com/rest-assured-tutorial/)样式。它将进一步了解 Cucumber 测试的结构方式。