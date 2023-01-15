## 什么是 HTTP PUT 请求方法？

\* *PUT*方法 *（HTTP PUT 请求方法）创建新资源或使用请求有效负载更新（替换）目标资源的表示。这意味着 Put 请求会更新指定 URI 处的资源。它还用于在给定的 URI 上创建新资源或替换整个产品实体。

官方[**HTTP RFC**](https://tools.ietf.org/html/rfc7231#section-4.3.4)指定：

-   **PUT**方法将*文件或资源精确地放置或放置在特定的 URI 中。*

    

    -   如果该 URI 中已存在文件或资源，则 PUT 方法将替换该文件或资源。
    -   如果没有文件或资源，PUT 会创建一个新文件或资源。

-   对 PUT 方法的响应是不可缓存的。

-   PUT 请求通常返回状态码 200。

## 放置与。POST 请求

现在让我们讨论 PUT 和 POST 请求之间的主要区别。

| 放                                                           | 邮政                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| 这种方法是幂等的。这意味着如果多次执行，它将产生相同的结果。 | 这种方法不是幂等的。每次执行都会产生不同的结果。             |
| 当我们需要修改已经是资源集合一部分的单个资源时，我们调用 PUT 方法。 | 当要在资源集合下添加子资源时调用 POST 方法。                 |
| PUT 方法语法：PUT /questions/{question-id}                   | POST 方法语法：POST /questions                               |
| PUT 按特定方式工作。                                         | POST 作为抽象工作。                                          |
| Put 方法使用*“UPDATE”*查询。                                 | POST 方法使用*“CREATE”*查询。                                |
| 在 PUT 方法中，客户端决定应该拥有哪个 URI 资源。             | 在 POST 方法中，服务器决定应该拥有哪个 URI 资源。            |
| * *PUT /vi/cake/orders/1234表示更新由**“1234”*标识的资源。   | *POST /vi/cake/orders* * 表示我们正在创建一个新资源并返回一个标识符来描述该资源。 |

**为 PUT 和 POST 请求获取的状态码**

POST 和 PUT 方法使用以下状态码：

**POST 请求**

-   201 带有指向新资源的位置标头。
-   如果未创建新项目，则为 400。

**放置请求**

-   204 表示 OK/SUCCESS（但没有内容）。
-   200 表示内容正文确定（更新响应）。
-   如果发送的数据无效，则为 400。

### **REST API 示例**

现在我们将使用 Swagger UI 演示 PUT 请求。PUT 请求的 URL 在这里给出[***https://demoqa.com/BookStore/v1/Books/***](https://demoqa.com/BookStore/v1/Books/)

另外，您可以看看[**书店 Swagger**](https://demoqa.com/swagger/)

当我们访问这个 URL 时，我们会看到以下屏幕。

![1.Put请求UI.png](https://toolsqa.com/gallery/Rest%20Assured/1.Put%20Request%20UI.png)

在上面的屏幕中，我们尝试使用 ISBN= *"9781449325862" 更新图书记录。*我们将更新的两个参数是 userId: "toolsqa_test" 和 ISBN: *"9781449325865"*。当我们执行这个 put 请求时，我们得到以下输出。

![来自 PUT 请求的响应](https://toolsqa.com/gallery/Rest%20Assured/2.Response%20from%20PUT%20request.png)

我们可以看到我们从服务器收到的状态码 200，表明请求成功。

在下一节中，我们将使用 Rest Assured 实现 PUT 请求。

## 如何使用 Rest Assured 实现 PUT 请求？

正如关于[* *POST 请求的教程中所解释的，*](https://www.toolsqa.com/rest-assured/post-request-using-rest-assured/)要创建*JSON 对象，*我们将在代码的类路径中添加一个*Simple JSON* * 库。完成此操作后，我们将按照以下步骤使用 REST Assured 发出请求。

-   使用简单的 JSON 库创建 JSON 数据。
-   在请求正文中发送 JSON 内容。
-   验证响应。

让我们在下面讨论这些步骤中的每一个。

### **使用简单 JSON 库创建 JSON 数据**

我们将使用相同的 URL 来实现使用 Rest Assured 的 PUT 请求。

端点：[***https ://demoqa.com/BookStore/v1/Books/***](https://demoqa.com/BookStore/v1/Books/)

第一步是创建一个我们需要使用*“put()”*方法发送的 JSON 数据请求。下面的一段代码实现了这一点。

java 导入 io.restassured.RestAssured; 导入 io.restassured.specification.RequestSpecification；导入 org.junit.Test；

公共类 PUTMethod { @Test 公共无效 putMethod() {

```
    String userId = "toolsqa_test";

    String baseUrl = "https://demoqa.com";

    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6InRlc3RpbmcxMjMiLCJwYXNzd29yZCI6IlBhc3N3b3JkQDEiLCJpYXQiOjE2Mjg1NjQyMjF9.lW8JJvJF7jKebbqPiHOBGtCAus8D9Nv1BK6IoIIMJQ4";

    String isbn = "9781449325865";
    RequestSpecification httpRequest = RestAssured.given().header("Authorization", "Bearer " + token).header("Content-Type", "application/json");
}
```

}

在上面的代码中，我们使用了 ISBN 参数。请注意，我们已经有了这个 ISBN 的记录。我们将通过发送 PUT 请求来更新此记录。所以对于这个 ISBN，我们将使用新的 ISBN 和 userId 更新记录。我们为 ISBN 设置了一个新值并更新了 userId。因此，当我们发送此请求时，具有 ISBN= *"9781449325862"*的记录将其 userId= *"toolsqa_test"*和 ISBN 更新为新值*"9781449325865"*。

### **在请求正文中发送 JSON 内容**

生成的请求被发送到服务器。

java 响应 res = httpRequest.body("{ "isbn": "" + isbn + "", "userId": "" + userId + ""}")

.put("/BookStore/v1/Book/9781449325862");

因此，在上面的代码中，我们创建了一个 JSON 字符串形式的请求正文，然后我们通过发送 ISBN 作为参数来调用这个请求的*“put()”方法。*这可确保更新具有给定 ISBN 的记录。

### **验证响应**

下一步是验证响应以确保记录更新。这是通过读取响应的状态代码来完成的，如下所示：

java //验证响应 System.out.println("响应代码 -" +res.getStatusCode());

Assert.assertEquals(res.getStatusCode(),200);

我们使用了返回响应状态码的 getStatusCode() 方法。值 200 表示 PUT 请求成功。上述测试的执行给出以下响应：

![验证响应](https://toolsqa.com/gallery/Rest%20Assured/3.Validate%20the%20Response.png)

我们看到测试用例已经通过了。

使用 Rest Assured 的 PUT 请求演示的完整代码如下：

java包org.example；

导入 io.restassured.RestAssured；导入 io.restassured.specification.RequestSpecification；导入 org.junit.Assert；导入 org.junit.Test；导入 io.restassured.response.Response；导入 io.restassured.response.ResponseBody；导入 io.restassured.specification.RequestSpecification；导入 org.testng.annotations.AfterTest；导入 org.testng.annotations.BeforeTest；

公共类 PUTMethod { String userId = "toolsqa_test"; 字符串 baseUrl="https://demoqa.com"; String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6InRlc3RpbmcxMjMiLCJwYXNzd29yZCI6IlBhc3N3b3JkQDEiLCJpYXQiOjE2Mjg1NjQyMjF9.lW8JJvJF7jKebbqPiHOBGtCAus8D9Nv1BK6IoIIMJQ4"; 字符串 isbn ="9781449325865";

```
@Test
public void updateBook() {
    RestAssured.baseURI = baseUrl;
    RequestSpecification httpRequest = RestAssured.given().header("Authorization", "Bearer " + token)
            .header("Content-Type", "application/json");

    //Calling the Delete API with request body
    Response res = httpRequest.body("{ \"isbn\": \"" + isbn + "\", \"userId\": \"" + userId + "\"}").put("/BookStore/v1/Book/9781449325862");

    //Fetching the response code from the request and validating the same
    System.out.println("The response code - " +res.getStatusCode());
    Assert.assertEquals(res.getStatusCode(),200);

}
```

}

上面讨论的步骤演示了 PUT 请求的基本工作以及它与 POST 请求的区别。我们使用 JSON 库创建在请求正文内容中发送的 JSON 数据。然后，我们在获得响应后使用状态码验证响应。

注意：Put 请求的视频教程可在[Put Request in Rest Assured获得](https://www.youtube.com/watch?v=RzWX4z2_QjE)

## 关键要点

在本文中，我们讨论了 PUT 请求。

-   Put 请求是幂等的，即无论请求执行多少次，它都会产生相同的结果。
-   HTTP Put 请求更新或创建资源。
-   放置请求通常在成功时响应状态码 200。对 PUT 请求的响应不可缓存。
-   我们可以使用 Rest Assured 库来提出请求（使用 put 方法）。为此，我们发送一个带有请求的 JSON 对象并获得响应。
-   我们通过检查状态码来验证响应。

在 Rest Assured 系列的下一篇文章中，我们将讨论*“DELETE”*方法。