在之前的 [Chai 断言库](https://toolsqa.com/postman/assertions-in-postman-with-chai-assertion-library/)教程中，我们介绍了使用expect关键字的 Postman 断言。Postman 中还有许多其他断言围绕Postman Sandbox工作，我们在 Postman 的 [预请求脚本](https://toolsqa.com/postman/pre-request-script-in-postman/)中介绍了这些断言。在本教程中，我们将讨论 Postman 中一些常见的不同类型的断言。

在本教程中，我们将重点关注响应部分，因为它非常重要，因为我们几乎所有时间都需要对响应进行断言。所以我们将应用断言：

-   响应时间
-   状态码
-   状态码含义
-   响应类型
-   响应头
-   后方法检查
-   响应中的字符串

## Postman 中不同类型的断言

例如，我们可以考虑发送请求并检查上面提到的所有内容。在本教程的最后，你还可以将所有断言添加到一个请求中以练习和提高你的技能。那么，我们现在就开始吧。

先决条件：

-   POST 方法 API 端点：我们正在使用[客户注册](https://toolsqa.com/postman/post-request-in-postman/)API

### 断言响应时间

这个断言帮助我们验证请求的响应时间。下面我们验证一下 Response Time 是否小于 100ms。转到“测试”选项卡并编写以下代码：

pm.test("响应时间小于100ms", function () { pm.expect(pm.response.responseTime).to.be.below(100); });

![Response_Code_Chai](https://www.toolsqa.com/gallery/Postman/1.Response_Code_Chai.jpg)

注意：也可以修改此断言以检查时间是否高于某个值 (to.be.above(value)) 并等于某个值 (to.be.equal(value))。

按发送并查看响应。

![Response_Code_Chai_Response 回复](https://www.toolsqa.com/gallery/Postman/2.Response_Code_Chai_Response.jpg)

注意：在上面的例子中，Assert 失败了，因为响应时间是 1121ms。此外，在响应框中可以清楚地看到相同的断言错误：预期 1121 低于 100，这显然是错误的。

### 断言响应状态代码

此断言基于检查 Response Status Code。在下面的测试中，我们正在验证响应状态代码是否为 200。如果状态代码为 200，测试将通过，否则如果状态代码不是 200，则测试将失败。在测试选项卡中编写以下代码：

pm.test("状态码为 200", function () { pm.response.to.have.status(200); });

![Status_Code_200_Chai](https://www.toolsqa.com/gallery/Postman/3.Status_Code_200_Chai.jpg)

你可以将任何状态代码放在值框内以检查状态的值。同样的在Chai Assertion Library中也可以表示为

用于检查状态正常

pm.test("状态正常", function () { pm.response.to.be.ok; });

用于检查状态为 BAD REQUEST

pm.test("Status is Bad Request", function () { pm.response.to.be.badRequest; });

按发送并查看在我的情况下是正确的响应。

![Status_Code_200_Chai_Response](https://www.toolsqa.com/gallery/Postman/4.Status_Code_200_Chai_Response.jpg)

我们得到的响应状态代码为 200，因此我们的断言已通过。

### 断言响应状态代码含义

此断言基于检查特定属性。在此断言中，我们将检查特定属性及其值。在下面给出的示例中，我们正在检查属性状态及其值为OK。

在“测试”选项卡中编写以下代码。

pm.test("状态正常", function(){ pm.response.to.have.property('status', 'OK'); });

![Status_Code_OK_Chai](https://www.toolsqa.com/gallery/Postman/5.Status_Code_OK_Chai.jpg)

按发送并查看结果，这对我来说是正确的。

![Status_Code_OK_Chai_Response](https://www.toolsqa.com/gallery/Postman/6.Status_Code_OK_Chai_Response.jpg)

我想这很好理解。

### 断言响应类型

此断言基于验证 Response Type。在下面的测试中，我们正在验证响应类型是否为 JSON。在测试选项卡中编写以下代码：

pm.test("Response if Json", function(){ pm.response.to.be.json; });

![Response_Json_Chai](https://www.toolsqa.com/gallery/Postman/7.Response_Json_Chai.png)

注意：我希望你记住，当我们使用 weather api 发送请求时，在[Get Request](https://toolsqa.com/postman/get-request-in-postman/)中，我们收到了文本格式的响应，而不是 JSON 格式。我们在这里使用相同的 API。

按发送并查看结果。

![响应_Json_Chai_Response](https://www.toolsqa.com/gallery/Postman/8.Response_Json_Chai_Response.png)

由于响应类型，断言失败。我们期望响应类型为JSON，但我们在 weather api 中获得的响应是 TEXT格式。

### 断言响应头

此断言基于检查标头是否具有内容类型。

在你的测试选项卡中写入以下内容

pm.test("Content-Type 存在", function () { pm.response.to.have.header("Content-Type"); });

![Response_To_Have_Content_Type](https://www.toolsqa.com/gallery/Postman/9.Response_To_Have_Content_Type.png)

此断言检查响应中是否存在内容类型标头。按发送，看看它是否是。

![Response_To_Have_Content_Type_Response](https://www.toolsqa.com/gallery/Postman/10.Response_To_Have_Content_Type_Response.png)

是的，测试通过了。但是，我们如何检查它是否真的存在。正如你所看到的，除了还编写了 Headers 。转到Headers并且Content-Type必须出现在那里。

![Content_Type_Header](https://www.toolsqa.com/gallery/Postman/11.Content_Type_Header.png)

所以现在我们已经看到了常用的断言。现在，我们将尝试同时使用 Chai Assertion 和这些断言来创建一些有意义的测试。

### 断言多个状态代码

为此，我们将使用客户注册 api，因为它使用 POST 方法类型来发送请求，或者你也可以使用 Weather API，但最终测试将失败。[你可以从此处](https://toolsqa.com/wp-content/uploads/2018/07/Newman-Collection.postman_collection.zip)下载这两个 API 。

转到测试选项卡并编写以下代码

pm.test("POST请求成功", function () { pm.expect(pm.response.code).to.be.oneOf([201,202]); });

![Successful_Post_Request](https://www.toolsqa.com/gallery/Postman/12.Successful_Post_Request.png)

注意：201 已创建，202 已接受。

按发送并查看响应，如果状态代码为 201 或 202，则响应将通过，否则将失败。

### 断言响应文本

检查响应是否包含字符串

在任何正确并给出响应的API的测试选项卡中编写以下代码。

pm.test("Body matches string", function () { pm.expect(pm.response.text()).to.include("string_you_want_to_search"); });

将查询“string_you_want_to_search”替换为你要搜索的字符串。如果你的响应将包含你的断言将通过或失败的字符串。

按发送并查看响应。

要了解更多信息，我们强烈建议阅读[Chai Library](https://toolsqa.com/postman/assertions-in-postman-with-chai-assertion-library/)和[Postman Sandbox](https://www.getpostman.com/docs/v6/postman/scripts/postman_sandbox_api_reference)的文档。

因此，在本教程中，我们断言了一些在 Postman Sandbox 下执行的断言。这些断言对测试人员非常有帮助，因为他可以更轻松有效地完成测试。我希望你已经了解什么是断言以及如何使用它们。你可以结合 Chai 断言和邮递员的断言来创建你自己的自定义断言，看看你是否做对了。我们现在将进入下一个教程。在那之前，继续练习断言。它们是通过 Postman 进行测试的核心。