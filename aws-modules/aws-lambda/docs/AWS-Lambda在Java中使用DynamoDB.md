## 1. 简介

[AWS Lambda](https://aws.amazon.com/lambda/)是 Amazon Web Services 提供的无服务器计算服务，[WS DynamoDB](https://aws.amazon.com/dynamodb/)也是 Amazon 提供的 NoSQL 数据库服务。

有趣的是，DynamoDB 同时支持文档存储和键值存储，并且完全由 AWS 管理。

在我们开始之前，请注意本教程需要一个有效的 AWS 账户(你可以[在此处](https://aws.amazon.com/)创建一个)。此外，最好先阅读[AWS Lambda with Java](https://www.baeldung.com/java-aws-lambda)一文。

## 2.Maven依赖

要启用 lambda，我们需要可以在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.amazonaws" AND a%3A"aws-lambda-java-core")上找到的以下依赖项：

```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-lambda-java-core</artifactId>
    <version>1.2.1</version>
</dependency>

```

要使用不同的 AWS 资源，我们需要以下依赖项，它们也可以在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.amazonaws" AND a%3A"aws-lambda-java-events")上找到：

```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-lambda-java-events</artifactId>
    <version>3.11.0</version>
</dependency>

```

为了构建应用程序，我们将使用[Maven Shade 插件](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.maven.plugins" AND a%3A"maven-shade-plugin")：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.0.0</version>
    <configuration>
        <createDependencyReducedPom>false</createDependencyReducedPom>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 3. 拉姆达代码

在 lambda 应用程序中创建处理程序有不同的方法：

-   方法处理器
-   请求处理器
-   请求流处理器

我们将在我们的应用程序中使用RequestHandler接口。我们将接受 JSON 格式的PersonRequest，响应也将是JSON格式的PersonResponse ：

```java
public class PersonRequest {
    private String firstName;
    private String lastName;
    
    // standard getters and setters
}

public class PersonResponse {
    private String message;
    
    // standard getters and setters
}
```

接下来是我们的入口点类，它将实现 RequestHandler 接口：

```java
public class SavePersonHandler implements RequestHandler<PersonRequest, PersonResponse> {

    private AmazonDynamoDB amazonDynamoDB;

    private String DYNAMODB_TABLE_NAME = "Person";
    private Regions REGION = Regions.US_WEST_2;

    public PersonResponse handleRequest(PersonRequest personRequest, Context context) {
        this.initDynamoDbClient();

        persistData(personRequest);

        PersonResponse personResponse = new PersonResponse();
        personResponse.setMessage("Saved Successfully!!!");
        return personResponse;
    }

    private void persistData(PersonRequest personRequest) throws ConditionalCheckFailedException {

        Map<String, AttributeValue> attributesMap = new HashMap<>();

        attributesMap.put("id", new AttributeValue(String.valueOf(personRequest.getId())));
        attributesMap.put("firstName", new AttributeValue(personRequest.getFirstName()));
        attributesMap.put("lastName", new AttributeValue(personRequest.getLastName()));
        attributesMap.put("age", new AttributeValue(String.valueOf(personRequest.getAge())));
        attributesMap.put("address", new AttributeValue(personRequest.getAddress()));

        amazonDynamoDB.putItem(DYNAMODB_TABLE_NAME, attributesMap);
    }

    private void initDynamoDbClient() {
        this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withRegion(REGION)
            .build();
    }
}
```

这里我们在实现RequestHandler接口的时候，需要实现handleRequest()来进行请求的实际处理。至于其余的代码，我们有：

-   PersonRequest对象——它将包含以 JSON 格式传递的请求值
-   上下文对象——用于从 lambda 执行环境中获取信息
-   PersonResponse –这是 lambda 请求的响应对象

创建 AmazonDynamoDB 对象时，我们将首先创建一个新的构建器实例，并设置所有默认值。请注意，区域是强制性的。

要在 DynamoDB 表中添加项目，我们将创建一个表示项目属性的键值对映射，然后我们可以使用 putItem(String, Map<String, AttributeValue>)。

我们不需要 DynamoDB 表中的任何预定义模式，我们只需要定义主键列名称，在我们的例子中是“id”。

## 4.构建部署文件

要构建 lambda 应用程序，我们需要执行以下 Maven 命令：

```java
mvn clean package shade:shade
```

Lambda 应用程序将被编译并打包到目标文件夹下的jar文件中。

## 5. 创建 DynamoDB 表

按照以下步骤创建 DynamoDB 表：

-   登录[AWS 账户](https://aws.amazon.com/)
-   单击位于“所有服务”下的“DynamoDB ”
-   此页面将显示已创建的 DynamoDB 表(如果有)
-   单击“创建表”按钮
-   提供“表名”和“主键”，其数据类型为“数字”
-   单击“创建”按钮
-   将创建表

## 6. 创建 Lambda 函数

按照以下步骤创建 Lambda 函数：

-   登录[AWS 账户](https://aws.amazon.com/)
-   单击位于“所有服务”下的“Lambda ”
-   此页面将显示已创建的 Lambda 函数(如果有)或未创建 Lambda 函数单击“立即开始”
-   “选择蓝图” ->选择“空白功能”
-   “配置触发器” ->单击“下一步”按钮
-   “配置功能”
    -   “姓名”：保存人
    -   “描述”：将人保存到 DDB
    -   “运行时”：选择“Java 8”
    -   “上传”：点击“上传”按钮，选择lambda应用的jar文件
-   “处理程序”：com.baeldung.lambda.dynamodb.SavePersonHandler
-   “角色”：选择“创建自定义角色”
-   将弹出一个新窗口，允许为 lambda 执行配置 IAM 角色，我们需要在其中添加 DynamoDB 授权。完成后，单击“允许”按钮
-   单击“下一步”按钮
-   “审查”：审查配置
-   单击“创建函数”按钮

## 7. 测试 Lambda 函数

下一步是测试 lambda 函数：

-   点击“测试”按钮
-   将显示“输入测试事件”窗口。在这里，我们将为我们的请求提供 JSON 输入：

```xml
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "age": 30,
  "address": "United States"
}
```

-   单击“保存并测试”或“保存”按钮
-   输出可以在“执行结果”部分看到：

```plaintext
{
  "message": "Saved Successfully!!!"
}
```

-   我们还需要在 DynamoDB 中检查记录是否已保存：
    -   转到“DynamoDB”管理控制台
    -   选择表“人”
    -   选择“项目”选项卡
    -   在这里你可以看到在请求中传递给 lambda 应用程序的人的详细信息
-   所以我们的 lambda 应用程序成功处理了请求

## 八. 总结

在这篇快速文章中，我们学习了如何使用 DynamoDB 和Java8 创建 Lambda 应用程序。详细的说明应该让你在设置一切方面抢先一步。