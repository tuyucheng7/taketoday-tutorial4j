## 一、概述

在本文中，我们将介绍[ZeroCode](https://github.com/authorjapps/zerocode)自动化测试框架。我们将通过一个 REST API 测试示例来学习基础知识。

## 2. 方法

ZeroCode 框架采用以下方法：

-   多方面的测试支持
-   声明式测试

让我们讨论它们。

### 2.1。多方面的测试支持

该框架旨在支持对我们应用程序的多个方面进行自动化测试。除其他外，它使我们能够测试：

-   休息
-   肥皂
-   安全
-   负载/应力
-   数据库
-   阿帕奇卡夫卡
-   GraphQL
-   开放 API 规范

测试是通过我们将很快讨论的框架的 DSL 完成的。

### 2.2. 声明式

ZeroCode 使用声明式的测试方式，这意味着我们不必编写实际的测试代码。我们在 JSON/YAML 文件中声明场景，框架会将它们“翻译”成幕后的测试代码。这有助于我们**专注于我们想要测试的内容，而不是如何测试它**。

## 3. 设置

让我们在*pom.xml*文件中添加 Maven 依赖项：

```xml
 <dependency>
      <groupId>org.jsmart</groupId>
      <artifactId>zerocode-tdd</artifactId>
      <version>1.3.27</version>
      <scope>test</scope>
 </dependency>复制
```

最新版本[在 Maven Central 上](https://search.maven.org/artifact/org.jsmart/zerocode-tdd)可用。我们也可以使用 Gradle。如果我们使用 IntelliJ，我们可以从[Jetbrains Marketplace](https://plugins.jetbrains.com/marketplace)下载 ZeroCode 插件。

## 4. REST API 测试

正如我们上面所说，ZeroCode 可以支持测试我们应用程序的多个部分。在本文中，我们将专注于 REST API 测试。出于这个原因，我们将创建一个小型 Spring Boot Web 应用程序并公开一个端点：

```java
@PostMapping
public ResponseEntity create(@RequestBody User user) {
    if (!StringUtils.hasText(user.getFirstName())) {
        return new ResponseEntity("firstName can't be empty!", HttpStatus.BAD_REQUEST);
    }
    if (!StringUtils.hasText(user.getLastName())) {
        return new ResponseEntity("lastName can't be empty!", HttpStatus.BAD_REQUEST);
    }
    user.setId(UUID.randomUUID().toString());
    users.add(user);
    return new ResponseEntity(user, HttpStatus.CREATED);
}

复制
```

让我们看看在我们的控制器中引用的*User*类：

```java
public class User {
    private String id;
    private String firstName;
    private String lastName;

    // standard getters and setters
}复制
```

当我们创建一个用户时，我们设置一个唯一的 id 并将整个*用户*对象返回给客户端。端点可通过*/api/users*路径访问。我们将把用户保存在内存中以保持简单。

## 5. 写一个场景

该场景在 ZeroCode 中起着核心作用。它由一个或多个步骤组成，这些步骤是我们要测试的实际内容。让我们用一个步骤编写一个场景来测试用户创建的成功路径：

```json
{
  "scenarioName": "test user creation endpoint",
  "steps": [
    {
      "name": "test_successful_creation",
      "url": "/api/users",
      "method": "POST",
      "request": {
        "body": {
          "firstName": "John",
          "lastName": "Doe"
        }
      },
      "verify": {
        "status": 201,
        "body": {
          "id": "$NOT.NULL",
          "firstName": "John",
          "lastName": "Doe"
        }
      }
    }
  ]
}复制
```

让我们解释一下每个属性代表什么：

-   *场景*名称——这是场景的名称；我们可以使用任何我们想要的名字

-   步骤

    - JSON 对象数组，我们在其中描述我们想要测试的内容

    -   *name* - 我们为步骤指定的名称

    -   *url* – 端点的相对 URL；我们也可以放一个绝对网址，但一般来说，这不是一个好主意

    -   *方法*- HTTP 方法

    -   请求

        - HTTP 请求

        -   *正文*– HTTP 请求正文

    -   验证

        - 在这里，我们验证/断言服务器返回的响应

        -   *status* – HTTP 响应状态码
        -   *正文*（内部验证属性）– HTTP 响应正文

在这一步中，我们检查用户创建是否成功。我们检查服务器返回的*firstName*和*lastName*值。此外，我们使用 ZeroCode 的断言令牌验证 id 不为*空。*

一般来说，我们在场景中有不止一个步骤。让我们在场景的*步骤*数组中添加另一个步骤：

```json
{
  "name": "test_firstname_validation",
  "url": "/api/users",
  "method": "POST",
  "request": {
    "body": {
      "firstName": "",
      "lastName": "Doe"
    }
  },
  "verify": {
    "status": 400,
    "rawBody": "firstName can't be empty!"
  }
}复制
```

在这一步中，我们提供了一个空的名字，这会导致错误的请求。在这里，响应正文将不是 JSON 格式，因此我们使用*rawbody*属性将其称为纯字符串。

ZeroCode 不能直接运行场景——为此，我们需要一个相应的测试用例。

## 6. 编写测试用例

为了执行我们的场景，让我们编写一个相应的测试用例：

```java
@RunWith(ZeroCodeUnitRunner.class)
@TargetEnv("rest_api.properties")
public class UserEndpointIT {

    @Test
    @Scenario("rest/user_create_test.json")
    public void test_user_creation_endpoint() {
    }
}复制
```

在这里，我们声明了一个方法，并使用 JUnit 4 中的*@Test*注释将其标记为测试。我们只能将 JUnit 5 与 ZeroCode 一起用于负载测试。

我们还使用来自 ZeroCode 框架的*@Scenario 注释指定场景的位置。*方法体为空。正如我们所说，我们不编写实际的测试代码。我们想要测试的内容在我们的场景中进行了描述。我们只是在测试用例方法中引用场景。我们的*UserEndpointIT* 类有两个注解：

-   *@RunWith* – 在这里，我们指定哪个 ZeroCode 类负责运行我们的场景
-   *@TargetEnv* - 这指向我们的场景运行时将使用的属性文件

当我们在上面声明*url*属性时，我们指定了相对路径。显然，ZeroCode 框架需要一个绝对路径，所以我们创建了一个*rest_api.properties*文件，其中包含一些定义我们的测试应该如何运行的属性：

-   *web.application.endpoint.host* – REST API 的主机；在我们的例子中，它是*http://localhost*
-   *web.application.endpoint.port* – 暴露我们的 REST API 的应用程序服务器的端口；在我们的例子中，它是 8080
-   *web.application.endpoint.context* – API 的上下文；在我们的例子中，它是空的

我们在属性文件中声明的属性取决于我们正在进行的测试类型。例如，如果我们想测试[Kafka](https://gitlab.com/zerocodeio/samurai-user-manual/-/wikis/home)生产者/消费者，我们将拥有不同的属性。

## 7. 执行测试

我们已经创建了一个场景、属性文件和测试用例。现在，我们准备好运行我们的测试了。由于 ZeroCode 是一个集成测试工具，我们可以利用 Maven 的*故障保护*插件：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>3.0.0-M5</version>
    <dependencies>
        <dependency>
            <groupId>org.apache.maven.surefire</groupId>
            <artifactId>surefire-junit47</artifactId>
            <version>3.0.0-M5</version>
        </dependency>
    </dependencies>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>复制
```

要运行测试，我们可以使用以下命令：

```java
mvn verify -Dskip.it=false复制
```

 ZeroCode 创建多种类型的日志，我们可以在*${project.basedir}/target*文件夹中查看这些日志。

## 8. 结论

在本文中，我们了解了 ZeroCode 自动化测试框架。我们展示了该框架如何与 REST API 测试示例一起工作。我们还了解到 ZeroCode DSL 消除了编写实际测试代码的需要。