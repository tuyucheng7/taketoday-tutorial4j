在上一章中，我们介绍了在框架中使用***Endpoints 类的概念。***作为将与服务器通信的逻辑抽象到一个单独的类中并使我们的步骤更清晰的一种方法，我们引入了这个 Endpoints 类。随后，在本章中，我们将通过在我们的框架中引入***REST 路由***概念来更进一步。众所周知，我们使用 API 端点来执行特定任务。此外，路由是我们可以访问端点的路径。简单来说，我们将***路由称为 URI***，而端点是我们对 URI 执行的操作。

## API 中的 REST 路由是什么？

众所周知，客户端-服务器通信是*REST（表示状态传输）*原则之一。此外，它还提供了*HTTP*动词（如*GET、PUT、POST、DELETE 等）与 CRUD（创建、读取、更新和删除）*操作之间的映射。我们点击链接并浏览各个站点。此外，在这个过程中，我们只是在从另一个站点导航到一个站点时进行状态转换。*符合 REST 的*系统通常被称为*RESTful*系统。

除了上述之外，*RESTful*路由可以类似于构建不同路由时遵循的传统模式。每次发生*HTTP*请求时，它都会与服务器交互。

例如在 URL 中，

***http://bookstore.toolsqa.com/BookStore/v1/Books:***

基本路径：http: ***//bookstore.toolsqa.com/***

***路线：/BookStore/v1/Books***

***端点：***我们可以在这条路线上执行多项任务。例如：

|                                                        |                      |
| ------------------------------------------------------ | -------------------- |
| 获取 - http://bookstore.toolsqa.com/BookStore/v1/Books | 获取书籍列表         |
| POST-http://bookstore.toolsqa.com/BookStore/v1/Books   | 添加与用户关联的书籍 |
| 删除 - http://bookstore.toolsqa.com/BookStore/v1/Books | 删除与用户关联的书籍 |

这就是端点在我们的 Swagger [***Bookstore API中的样子***](https://bookstore.toolsqa.com/swagger/index.html)

![REST 路由 - BookStore API](https://toolsqa.com/gallery/Rest%20Assured/1.REST%20Routes-%20BookStore%20APIs.webp)

## 框架中 REST 路由的实现

我们将按照以下步骤在我们的框架中实现路由：

1.  *首先，创建一个Route类*
2.  *其次，修改Endpoints类*
3.  *第三，运行测试*

### ***创建路由类***

让我们将上面第一节中讨论的 Route 添加到我们的 Route 类中。我们将把所有的路线都放在一个地方，无论哪里需要路线，我们都会在这个类中使用它。

此外，它提供的优势是假设任何路线发生变化。我们不必在任何地方进行更改。换句话说，它只是在 Routes 类中的一个地方。

1.  首先，要创建一个***Route*** 类，*右键单击*apiEngine ***Package***并选择 ***New >>Class***。将其命名为***Route***。
2.  之后，将以下代码片段添加到其中：

```java
package apiEngine;

public class Route {

    private static final String BOOKSTORE = "/BookStore";
    private static final String ACCOUNT = "/Account";
    private static final String VERSION = "/v1";
    
    
    public static String generateToken(){
    	return ACCOUNT + VERSION + "/GenerateToken";
    }

    public static String books(){ 
    	return BOOKSTORE + VERSION + "/Books";
    }

    public static String book(){ 
    	return BOOKSTORE + VERSION + "/Book";
    }

    public static String userAccount(String userId){
    	return ACCOUNT + VERSION + "/User" + "/" + userId; 
    }

}
```

### ***修改 REST 路由的 Endpoints 类***

我们将修改端点类中的方法。此外，我们将从 Routes 类传递路由，而不是将它们写入方法本身中的特定路由。

例如：

我们的方法*authenticateUser()*更新自：

```java
public static Response authenticateUser(AuthorizationRequest authRequest) {
	RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
       //The hardcoded route "/Account/vi/GenerateToken" will be modified to take the route from the Route class
        Response response = request.body(authRequest).post("/Account/v1/GenerateToken");
        return response;
	}
```

至

```java
public static Response authenticateUser(AuthorizationRequest authRequest) {
	RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        Response response = request.body(authRequest).post(Route.generateToken());
        return response;
}
```

***解释：***

在此方法中，生成令牌的路由现在取自 Route 类，而不是传递路由***"/Account/v1/GenerateToken"***。

同样，我们更新 Endpoints 类的以下方法：

1.  *获取书籍（）*
2.  *添加书（）*
3.  *删除书（）*
4.  *获取用户帐户（）*

因此，我们更新后的 Endpoints 类将如下所示：

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
        Response response = request.body(authRequest).post(Route.generateToken());
        return response;
    }

    public static Response getBooks() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        Response response = request.get(Route.books());
        return response;
    }

    public static Response addBook(AddBooksRequest addBooksRequest, String token) {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        Response response = request.body(addBooksRequest).post(Route.books());
        return response;
    }

    public static Response removeBook(RemoveBookRequest removeBookRequest, String token) {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        Response response = request.body(removeBookRequest).delete(Route.book());
        return response;
    }

    public static Response getUserAccount(String userId, String token) {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        Response response = request.get(Route.userAccount(userId));
        return response;
    }

}
```

### ***运行黄瓜测试***

***以 JUnit 运行测试***

我们现在都准备好运行更新的 Cucumber 测试了。首先，*右键单击*TestRunner ***类***。其次，点击***Run As >> JUnit Test***。最后，结果将显示在控制台的*JUnit选项卡中。*

![REST 路由 Junit 结果的实现](https://toolsqa.com/gallery/Rest%20Assured/2.Implementation%20of%20REST%20Routes%20Junit%20Results.webp)

***从 Cucumber 功能运行测试***

要将测试作为[***Cucumber Feature***](https://www.toolsqa.com/cucumber/cucumber-jvm-feature-file/)运行，请右键单击*End2End_Test.feature*文件。之后，选择*运行方式>>黄瓜功能。*

![图片：第 6 章 Routes Cucumber 结果的实现](https://toolsqa.com/gallery/Rest%20Assured/3.Image%20Chapter%206%20Implementation%20of%20Routes%20Cucumber%20Results.webp)

我们的测试通过了我们为框架中的 Rest Routes 实现所做的更改。我们更新后的框架项目文件夹结构将如下所示：

![图片：第 6 章 Endpoints 项目结构中路由的实现](https://toolsqa.com/gallery/Rest%20Assured/4.Images%20Chapter%206%20Implementation%20of%20Routes%20in%20Endpoints%20Project%20Structure.webp)

随后，在下一章中，我们将[***在我们的框架中实现泛型***](https://www.toolsqa.com/rest-assured/generics-in-api-framework/)来处理各种数据类型的响应对象。同时，请按照说明在您的框架中应用上述更改。