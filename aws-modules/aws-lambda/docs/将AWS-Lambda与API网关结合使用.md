## 1. 概述

[AWS Lambda](https://aws.amazon.com/lambda/)是 Amazon Web Services 提供的无服务器计算服务。

在之前的两篇文章中，我们讨论了[如何使用Java创建 AWS Lambda 函数](https://www.baeldung.com/java-aws-lambda)，以及[如何从 Lambda 函数访问 DynamoDB](https://www.baeldung.com/aws-lambda-dynamodb-java)。

在本教程中，我们将讨论如何使用[AWS Gateway](https://aws.amazon.com/api-gateway/)将 Lambda 函数发布为 REST 终端节点。

我们将详细了解以下主题：

-   API网关的基本概念和术语
-   使用 Lambda 代理集成将 Lambda 函数与 API 网关集成
-   API 的创建、其结构以及如何将 API 资源映射到 Lambda 函数
-   API的部署和测试

## 2. 基础知识和术语

API Gateway 是一项完全托管的服务，使开发人员能够创建、发布、维护、监控和保护任何规模的 API。

我们可以实施一致且可扩展的基于 HTTP 的编程接口(也称为 RESTful 服务)来访问后端服务，如 Lambda 函数、进一步的 AWS 服务(例如 EC2、S3、DynamoDB)和任何 HTTP 端点。

功能包括但不限于：

-   交通管理
-   授权和访问控制
-   监控
-   API版本管理
-   限制请求以防止攻击

与 AWS Lambda 一样，API Gateway 会自动横向扩展并按 API 调用计费。

详细信息可以在[官方文档](https://docs.aws.amazon.com/apigateway/latest/developerguide/welcome.html)中找到。

### 2.1. 条款

API Gateway是一项 AWS 服务，支持创建、部署和管理 RESTful 应用程序编程接口以公开后端 HTTP 端点、AWS Lambda 函数和其他 AWS 服务。

API 网关 API是资源和方法的集合，可以在后端与 Lambda 函数、其他 AWS 服务或 HTTP 端点集成。API 由构成 API 结构的资源组成。每个 API 资源都可以公开一个或多个必须具有唯一 HTTP 动词的 API 方法。

要发布 API，我们必须创建API 部署并将其与所谓的阶段相关联。阶段就像 API 的时间快照。如果我们重新部署 API，我们可以更新现有阶段或创建新阶段。通过这种方式，可以同时使用不同版本的 API，例如开发阶段、测试阶段，甚至多个生产版本，如v1、v2等。

Lambda 代理集成是 Lambda 函数和 API 网关之间集成的简化配置。

API 网关将整个请求作为输入发送到后端 Lambda 函数。在响应方面，API 网关将 Lambda 函数输出转换回前端 HTTP 响应。

## 3.依赖关系

我们需要与 [AWS Lambda 使用 DynamoDB 和 Java](https://www.baeldung.com/aws-lambda-dynamodb-java)一 文中相同的依赖项。

除此之外，我们还需要[JSON Simple](https://search.maven.org/classic/#search|ga|1|g%3A"com.googlecode.json-simple")库：

```xml
<dependency>
    <groupId>com.googlecode.json-simple</groupId>
    <artifactId>json-simple</artifactId>
    <version>1.1.1</version>
</dependency>
```

## 4. 开发和部署 Lambda 函数

在本节中，我们将使用Java开发和构建我们的 Lambda 函数，我们将使用 AWS 控制台部署它，并且我们将运行一个快速测试。

由于我们想要演示将 API 网关与 Lambda 集成的基本功能，因此我们将创建两个函数：

-   功能 1： 使用 PUT 方法从 API 接收有效负载
-   功能 2： 演示如何使用来自 API 的 HTTP 路径参数或 HTTP 查询参数

在实现方面，我们将创建一个RequestHandler类，它有两个方法——每个函数一个。

### 4.1. 模型

在我们实现实际的请求处理程序之前，让我们快速浏览一下我们的数据模型：

```java
public class Person {

    private int id;
    private String name;

    public Person(String json) {
        Gson gson = new Gson();
        Person request = gson.fromJson(json, Person.class);
        this.id = request.getId();
        this.name = request.getName();
    }

    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    // getters and setters
}
```

我们的模型由一个简单的Person类组成，它有两个属性。唯一值得注意的部分是Person(String)构造函数，它接受 JSON 字符串。

### 4.2. RequestHandler 类的实现

就像在 [AWS Lambda With Java](https://www.baeldung.com/java-aws-lambda#handler)文章中一样，我们将创建 RequestStreamHandler接口的实现：

```java
public class APIDemoHandler implements RequestStreamHandler {

    private static final String DYNAMODB_TABLE_NAME = System.getenv("TABLE_NAME"); 
    
    @Override
    public void handleRequest(
      InputStream inputStream, OutputStream outputStream, Context context)
      throws IOException {

        // implementation
    }

    public void handleGetByParam(
      InputStream inputStream, OutputStream outputStream, Context context)
      throws IOException {

        // implementation
    }
}
```

正如我们所见，RequestStreamHander接口只定义了一个方法，handeRequest()。总之，我们可以在同一个类中定义更多的函数，就像我们在这里所做的那样。另一种选择是 为每个函数创建一个RequestStreamHander实现。

在我们的具体案例中，为简单起见，我们选择了前者。但是，必须根据具体情况进行选择，同时考虑性能和代码可维护性等因素。

我们还从TABLE_NAME 环境变量中读取了 DynamoDB 表的名称 。我们稍后会在部署期间定义该变量。

### 4.3. 功能 1 的实现

在我们的第一个函数中，我们想要演示如何从 API 网关获取有效负载(例如来自 PUT 或 POST 请求)：

```java
public void handleRequest(
  InputStream inputStream, 
  OutputStream outputStream, 
  Context context)
  throws IOException {

    JSONParser parser = new JSONParser();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    JSONObject responseJson = new JSONObject();

    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    DynamoDB dynamoDb = new DynamoDB(client);

    try {
        JSONObject event = (JSONObject) parser.parse(reader);

        if (event.get("body") != null) {
            Person person = new Person((String) event.get("body"));

            dynamoDb.getTable(DYNAMODB_TABLE_NAME)
              .putItem(new PutItemSpec().withItem(new Item().withNumber("id", person.getId())
                .withString("name", person.getName())));
        }

        JSONObject responseBody = new JSONObject();
        responseBody.put("message", "New item created");

        JSONObject headerJson = new JSONObject();
        headerJson.put("x-custom-header", "my custom header value");

        responseJson.put("statusCode", 200);
        responseJson.put("headers", headerJson);
        responseJson.put("body", responseBody.toString());

    } catch (ParseException pex) {
        responseJson.put("statusCode", 400);
        responseJson.put("exception", pex);
    }

    OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
    writer.write(responseJson.toString());
    writer.close();
}
```

如前所述，我们稍后将配置 API 以使用 Lambda 代理集成。我们希望 API 网关将完整的请求传递给InputStream参数中的 Lambda 函数。

我们所要做的就是从包含的 JSON 结构中选择相关属性。

正如我们所见，该方法基本上由三个步骤组成：

1.  从我们的输入流中获取body对象并从中创建一个Person对象
2.  将该Person对象存储在 DynamoDB 表中
3.  构建一个 JSON 对象，它可以包含多个属性，例如响应主体、自定义标头以及 HTTP 状态代码

这里有一点值得一提：API Gateway 期望正文是一个字符串(对于请求和响应)。

正如我们期望从 API 网关获取一个String作为主体，我们将主体转换为String并初始化我们的Person对象：

```java
Person person = new Person((String) event.get("body"));
```

API Gateway 还期望响应主体是一个String：

```java
responseJson.put("body", responseBody.toString());
```

官方文档中没有明确提及该主题。然而，如果我们仔细观察，我们可以看到 body 属性[在请求](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format)和[响应](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-output-format)的两个片段中都是一个字符串。

优势应该很明显：即使 JSON 是 API 网关和 Lambda 函数之间的格式，实际主体也可以包含纯文本、JSON、XML 或其他任何内容。然后由 Lambda 函数负责正确处理格式。

稍后在 AWS 控制台中测试我们的函数时，我们将看到请求和响应主体的外观。

这同样适用于以下两个功能。

### 4.4. 功能 2 的实现

在第二步中，我们要演示如何使用路径参数或查询字符串参数通过 ID 从数据库中检索Person项目：

```java
public void handleGetByParam(
  InputStream inputStream, OutputStream outputStream, Context context)
  throws IOException {

    JSONParser parser = new JSONParser();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    JSONObject responseJson = new JSONObject();

    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    DynamoDB dynamoDb = new DynamoDB(client);

    Item result = null;
    try {
        JSONObject event = (JSONObject) parser.parse(reader);
        JSONObject responseBody = new JSONObject();

        if (event.get("pathParameters") != null) {
            JSONObject pps = (JSONObject) event.get("pathParameters");
            if (pps.get("id") != null) {
                int id = Integer.parseInt((String) pps.get("id"));
                result = dynamoDb.getTable(DYNAMODB_TABLE_NAME).getItem("id", id);
            }
        } else if (event.get("queryStringParameters") != null) {
            JSONObject qps = (JSONObject) event.get("queryStringParameters");
            if (qps.get("id") != null) {

                int id = Integer.parseInt((String) qps.get("id"));
                result = dynamoDb.getTable(DYNAMODB_TABLE_NAME)
                  .getItem("id", id);
            }
        }
        if (result != null) {
            Person person = new Person(result.toJSON());
            responseBody.put("Person", person);
            responseJson.put("statusCode", 200);
        } else {
            responseBody.put("message", "No item found");
            responseJson.put("statusCode", 404);
        }

        JSONObject headerJson = new JSONObject();
        headerJson.put("x-custom-header", "my custom header value");

        responseJson.put("headers", headerJson);
        responseJson.put("body", responseBody.toString());

    } catch (ParseException pex) {
        responseJson.put("statusCode", 400);
        responseJson.put("exception", pex);
    }

    OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
    writer.write(responseJson.toString());
    writer.close();
}
```

同样，三个步骤是相关的：

1.  我们检查是否存在 具有id属性的pathParameters 或queryStringParameters数组。
2.  如果为true，我们使用 belonging 值从数据库中请求具有该 ID的Person项目。
3.  我们将接收到的项目的 JSON 表示添加到响应中。

官方文档对Proxy Integration的[输入格式](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format)和[输出格式](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-output-format)有更详细的解释。

### 4.5. 建筑规范

同样，我们可以简单地使用 Maven 构建我们的代码：

```powershell
mvn clean package shade:shade
```

JAR 文件将在目标文件夹下创建。

### 4.6. 创建 DynamoDB 表

我们可以按照 [AWS Lambda 使用 DynamoDB 和 Java](https://www.baeldung.com/aws-lambda-dynamodb-java#dynamodb-table)中的说明创建表。

让我们选择Person作为表名，id作为主键名，Number作为主键类型。

### 4.7. 通过 AWS 控制台部署代码

在构建我们的代码并创建表格之后，我们现在可以创建函数并上传代码。

这可以通过重复[AWS Lambda with Java](https://www.baeldung.com/java-aws-lambda#lambda-console)文章中的步骤 1-5 来完成，我们的两种方法各重复一次。

让我们使用以下函数名称：

-   handleRequest方法的StorePersonFunction (函数 1)
-   handleGetByParam方法的GetPersonByHTTPParamFunction  (函数 2)

我们还必须定义一个 值为“Person”的环境变量TABLE_NAME。

### 4.8. 测试功能

在继续实际的 API 网关部分之前，我们可以在 AWS 控制台中运行一个快速测试，以检查我们的 Lambda 函数是否正确运行并且可以处理代理集成格式。

从 AWS 控制台测试 Lambda 函数的工作方式如[AWS Lambda with Java](https://www.baeldung.com/java-aws-lambda#invoke)一文中所述。

但是，当我们创建测试事件时，我们必须考虑我们的功能所期望的特殊代理集成格式。我们可以使用 API 网关 AWS 代理模板并根据需要自定义它，也可以并粘贴以下事件：

对于 StorePersonFunction，我们应该使用这个：

```javascript
{
    "body": "{"id": 1, "name": "John Doe"}"
}
```

如前所述，主体必须具有String类型，即使包含 JSON 结构也是如此。原因是 API 网关将以相同的格式发送其请求。

应返回以下响应：

```javascript
{
    "isBase64Encoded": false,
    "headers": {
        "x-custom-header": "my custom header value"
    },
    "body": "{"message":"New item created"}",
    "statusCode": 200
}
```

在这里，我们可以看到我们的响应主体是一个String，尽管它包含一个 JSON 结构。

让我们看一下 GetPersonByHTTPParamFunction 的输入。

为了测试路径参数功能，输入将如下所示：

```javascript
{
    "pathParameters": {
        "id": "1"
    }
}
```

发送查询字符串参数的输入是：

```javascript
{
    "queryStringParameters": {
        "id": "1"
    }
}
```

作为回应，我们应该得到以下两种情况的方法：

```javascript
{
  "headers": {
    "x-custom-header": "my custom header value"
  },
  "body": "{"Person":{n  "id": 88,n  "name": "John Doe"n}}",
  "statusCode": 200
}
```

同样，正文是一个String。

## 5. 创建和测试 API

在上一节中创建并部署 Lambda 函数后，我们现在可以使用 AWS 控制台创建实际的 API。

让我们看看基本的工作流程：

1.  在我们的 AWS 账户中创建一个 API。
2.  将资源添加到 API 的资源层次结构。
3.  为资源创建一个或多个方法。
4.  设置方法与所属的 Lambda 函数之间的集成。

我们将在以下部分中为我们的两个函数中的每一个重复步骤 2-4。

### 5.1. 创建 API

为了创建 API，我们必须：

1.  [通过https://console.aws.amazon.com/apigateway](https://console.aws.amazon.com/apigateway)登录 API 网关控制台 
2.  单击“开始”，然后选择“新 API”
3.  输入我们的 API ( TestAPI ) 的名称并单击“创建 API”确认

创建 API 后，我们现在可以创建 API 结构并将其链接到我们的 Lambda 函数。

### 5.2. 函数 1 的 API 结构

我们的StorePersonFunction需要以下步骤 ：

1.  在“资源”树下选择父资源项，然后从“操作”下拉菜单中选择“创建资源”。然后，我们必须在“新子资源”窗格中执行以下操作：
    -   在“资源名称”输入文本字段中键入“人员”作为名称
    -   在“资源路径”输入文本字段中保留默认值
    -   选择“创建资源”
2.  选择刚刚创建的资源，在“Actions”下拉菜单中选择“Create Method”，执行以下步骤：
    -   从 HTTP 方法下拉列表中选择 PUT，然后选择复选标记图标以保存选择
    -   将“Lambda Function”保留为集成类型，并选择“Use Lambda Proxy integration”选项
    -   从“Lambda Region”中选择区域，我们之前在其中部署了 Lambda 函数
    -   在“Lambda 函数”中键入“StorePersonFunction ”
3.  选择“保存”并在提示“向 Lambda 函数添加权限”时单击“确定”确认

### 5.3. 函数 2 的 API 结构——路径参数

我们检索路径参数的步骤类似：

1.  在“Resources”树下选择

    / 

    persons

    资源项，然后从“Actions”下拉菜单中选择“Create Resource”。然后，我们必须在“新建子资源”窗格中执行以下操作：

    -   在“资源名称”输入文本字段中键入“人员”作为名称
    -   将“资源路径”输入文本字段更改为“{id}”
    -   选择“创建资源”

2.  选择刚刚创建的资源，在“Actions”下拉菜单中选择“Create Method”，执行以下步骤：

    -   从 HTTP 方法下拉列表中选择 GET，然后选择复选标记图标以保存选择
    -   将“Lambda Function”保留为集成类型，并选择“Use Lambda Proxy integration”选项
    -   从“Lambda Region”中选择区域，我们之前在其中部署了 Lambda 函数
    -   在“Lambda 函数”中输入“ GetPersonByHTTPParamFunction ”

3.  选择“保存”并在提示“向 Lambda 函数添加权限”时单击“确定”确认

注意：此处将“Resource Path”参数设置为“{id}”很重要，因为我们的GetPersonByPathParamFunction 期望此参数的命名与此完全相同。

### 5.4. 函数 2 的 API 结构——查询字符串参数

接收查询字符串参数的步骤有点不同，因为我们不必创建资源，而是必须为id参数创建一个查询参数：

1.  选择“Resources”树下的

    /persons

    资源项，从“Actions”下拉菜单中选择“Create Method”，执行以下步骤：

    -   从 HTTP 方法下拉列表中选择 GET，然后选择复选标记图标以保存选择
    -   将“Lambda Function”保留为集成类型，并选择“Use Lambda Proxy integration”选项
    -   从“Lambda Region”中选择区域，我们之前在其中部署了 Lambda 函数
    -   在“Lambda 函数”中键入“GetPersonByHTTPParamFunction ”。

2.  选择“保存”并在提示“向 Lambda 函数添加权限”时单击“确定”确认

3.  选择右边的“Method Request”，执行以下步骤：

    -   展开 URL 查询字符串参数列表
    -   点击“添加查询字符串”
    -   在名称字段中键入“id”，然后选择复选标记图标进行保存
    -   选择“必填”复选框
    -   单击面板顶部“Request validator”旁边的钢笔符号，选择“Validate query string parameters and headers”，然后选择复选标记图标

注意：将“查询字符串”参数设置为“id”很重要，因为我们的GetPersonByHTTPParamFunction 期望此参数的命名与此完全相同。

### 5.5. 测试 API

我们的 API 现已准备就绪，但尚未公开。在我们发布它之前，我们想先从控制台运行一个快速测试。

为此，我们可以在“资源”树中选择要测试的相应方法，然后单击“测试”按钮。在以下屏幕上，我们可以输入我们的输入，因为我们将通过 HTTP 将其发送给客户端。

对于 StorePersonFunction，我们必须在“请求正文”字段中键入以下结构：

```javascript
{
    "id": 2,
    "name": "Jane Doe"
}
```

对于带有路径参数的GetPersonByHTTPParamFunction，我们必须在“路径”下的“{id}”字段中键入2作为值。

对于带有查询字符串参数的GetPersonByHTTPParamFunction ，我们必须在“查询字符串”下的“{persons}”字段中键入id=2作为值。

### 5.6. 部署 API

到目前为止，我们的 API 尚未公开，因此只能通过 AWS 控制台使用。

如前所述，当我们部署 API 时，我们必须将它与一个阶段相关联，这就像 API 的时间快照。如果我们重新部署 API，我们可以更新现有阶段或创建新阶段。

让我们看看我们的 API 的 URL 方案是什么样的：

```html
https://{restapi-id}.execute-api.{region}.amazonaws.com/{stageName}
```

部署需要以下步骤：

1.  在“API”导航窗格中选择特定的 API
2.  在资源导航窗格中选择“操作”，然后从“操作”下拉菜单中选择“部署 API”
3.  从“部署阶段”下拉列表中选择“[新阶段]”，在“阶段名称”中键入“测试”，并可选择提供阶段和部署的描述
4.  通过选择“部署”触发部署

在最后一步之后，控制台将提供 API 的根 URL，例如 https://0skaqfgdw4.execute-api.eu-central-1.amazonaws.com/test。

### 5.7. 调用端点

由于 API 现在是公开的，我们可以使用我们想要的任何 HTTP 客户端调用它。

使用cURL，调用将如下所示。

StorePerson函数：

```powershell
curl -X PUT 'https://0skaqfgdw4.execute-api.eu-central-1.amazonaws.com/test/persons' 
  -H 'content-type: application/json' 
  -d '{"id": 3, "name": "Richard Roe"}'
```

路径参数的GetPersonByHTTPParamFunction ：

```powershell
curl -X GET 'https://0skaqfgdw4.execute-api.eu-central-1.amazonaws.com/test/persons/3' 
  -H 'content-type: application/json'
```

查询字符串参数的GetPersonByHTTPParamFunction ：

```powershell
curl -X GET 'https://0skaqfgdw4.execute-api.eu-central-1.amazonaws.com/test/persons?id=3' 
  -H 'content-type: application/json'
```

## 六. 总结

在本文中，我们了解了如何使用 AWS API Gateway 使 AWS Lambda 函数可用作 REST 端点。

我们探索了 API 网关的基本概念和术语，并学习了如何使用 Lambda 代理集成来集成 Lambda 函数。

最后，我们了解了如何创建、部署和测试 API。