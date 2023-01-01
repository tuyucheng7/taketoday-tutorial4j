## 1. 概述

在本教程中，我们将学习如何使用预请求脚本将HTTP标头添加到Postman中的每个请求。

## 2. HTTP标头

在深入实现之前，让我们回顾一下[HTTP标头](https://www.baeldung.com/spring-rest-http-headers)是什么。

在HTTP请求中，标头是在客户端和服务器HTTP通信之间提供附加信息的字段。HTTP标头具有键值对格式，它们可以附加到请求和响应中。

Authorization、Content-Type和Cookie是可以由HTTP标头提供的元数据的示例。

例如：

```http request
Authorization: Bearer YmFyIiwiaWF0IjoxN;
Content-Type: application/json;
Cookie: foo=bar;
```

我**们将使用Postman预请求脚本功能通过执行JavaScript代码来设置标头**。

## 3. 运行服务器

在本教程中，我们将使用之前的项目[spring-boot-json](https://www.baeldung.com/spring-boot-json)进行演示，该应用程序由单个控制器StudentController组成，它接收对Student java模型的CRUD操作。

我们必须使用Maven install命令安装所有依赖项，然后运行SpringBootStudentsApplication文件，该文件将在端口8080上启动Tomcat服务器。

使用Postman，我们可以通过向以下端点发送GET请求并期待JSON响应来确认服务器正在运行：

```bash
http://localhost:8080/students/
```

例如：

<img src="../assets/img_1.png">

现在我们验证了服务器正在运行，**我们可以以编程方式将HTTP标头添加到Postman发送的请求中**。

## 4. 添加带有预请求脚本的Header

要在Postman中使用预请求脚本向HTTP请求添加标头，我们需要访问名为pm的Postman JavaScript API对象提供的请求数据。

**我们可以通过调用pm.request对象对请求元数据进行操作；因此，我们可以在发送请求之前添加、修改和删除HTTP标头**。

如前所述，HTTP标头具有键值对格式。Postman JavaScript API期望在向请求中添加标头时同时提供键和值。

我们可以使用name: value格式作为字符串添加标题：

```javascript
pm.request.headers.add("foo: bar");
```

我们还可以传递一个带有key和value属性的JavaScript对象，如下所示：

```javascript
pm.request.headers.add({
    key: "foo",
    value: "bar"
});
```

**但是，根据Postman文档，我们可以向标头对象添加其他属性，例如id、name和disabled，这将扩展Postman JavaScript运行时环境中的功能**。

现在，让我们看看它的实际效果。首先，我们将向单个Postman请求添加脚本；然后，我们将为整个集合添加标题。

### 4.1 单个请求

我们可以使用预请求脚本为Postman中的单个请求添加标头。我们可以参考上一节中展示的实现；但是，我们将专注于第二个，我们传递一个JavaScript对象，以便我们可以添加扩展功能的附加属性。

在Postman窗口的pre-request Script中，我们添加以下脚本，指示客户端期望json类型的响应：

```javascript
pm.request.headers.add({
    key: "Accept",
    value: "application/json"
});
```

在Postman中，请求如下所示：

<img src="../assets/img_2.png">

现在，我们通过单击发送按钮发送一个GET请求。发送请求后，我们必须打开Postman控制台(通常通过单击左下角的控制台按钮)并展开我们最近的请求以查看请求标头部分：

<img src="../assets/img_3.png">

在控制台中，我们可以看到Accept: “application/json”标头，表明它已成功附加到脚本的GET请求。此外，我们可以检查响应的正文和状态码，以确认请求成功。

为了进一步验证预请求脚本，我们可以添加以下标头并期望一个空响应以及406 Not Acceptable状态代码：

```javascript
pm.request.headers.add({ 
    key: "Accept",
    value: "image/" 
});
```

### 4.2 集合

同样，我们可以使用预请求脚本将HTTP标头添加到整个集合。

首先，我们将创建一个学生API集合以[使用Postman测试我们的API端点](使用Postman集合测试WebAPI.md)，并确认每个请求都包含我们使用预请求脚本添加的标头。

在Postman中，我们可以通过转到左侧的Collections菜单选项对Web API端点进行分组。然后，我们单击加号按钮并创建一个名为Student API Collection的新集合：

<img src="../assets/img_4.png">

请注意，我们还在我们的集合中添加了两个端点：http://localhost:8080/students/ 和 http://localhost:8080/students/2。

与单个请求类似，我们可以通过选择左侧菜单上的Student API Collection并转到Pre-request Script选项卡，将预请求脚本添加到我们的集合中。现在，我们可以添加我们的脚本：

```plaintext
pm.request.headers.add({ 
    key: "Accept",
    value: "application/json" 
});
```

在Postman中，学生API集合应如下所示：

<img src="../assets/img_5.png">

在运行集合之前，我们必须确保删除了我们最初在上一节中添加的预请求脚本。否则，HTTP标头将被请求脚本中指定的标头覆盖，并且集合级别的标头将被解除。

现在，我们准备好运行我们的集合了。点击收藏栏上的Run按钮，Runner选项卡将自动打开：

<img src="../assets/img_6.png">

Runner选项卡允许我们对我们的请求进行排序，从我们的集合中选择或取消选择请求，并指定其他设置。单击Run Student API Collection以执行我们的请求。

整个集合完成后，我们可以看到执行顺序和测试结果(如果有)。但是，我们要确保我们的HTTP标头是我们请求的一部分，我们可以通过打开Postman控制台来确认：

<img src="../assets/img_7.png">

再次，我们可以在控制台中展开请求的请求标头部分，并确认我们的预请求脚本添加了Accept标头。此外，你可以通过查看状态代码和响应正文来确认请求是否成功。

## 5. 总结

在本文中，我们使用Postman的预请求脚本功能为每个请求添加HTTP标头。首先，我们回顾了HTTP标头是什么；然后我们将预请求脚本添加到单个请求和集合中，以添加标头。有关预请求脚本和其他功能的进一步探索，请参阅[Postman文档](https://learning.postman.com/docs/writing-scripts/pre-request-scripts/)。