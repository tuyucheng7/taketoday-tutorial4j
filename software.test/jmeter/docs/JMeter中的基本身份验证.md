## 1. 概述

当我们[使用 JMeter 进行性能测试时](https://www.baeldung.com/jmeter)，我们可能会遇到受 HTTP 基本身份验证协议保护的 Web 服务。

在本教程中，我们将了解如何配置[Apache JMeter](https://jmeter.apache.org/)以在测试期间提供必要的凭据。

## 2. 什么是基本认证？

基本身份验证是我们可以用来保护 Web 资源的最简单的访问控制方法。它由客户端发送的 HTTP 标头组成：

```http
Authorization: Basic <credentials>
```

在这里，凭据被编码为用户名和密码的 Base64 字符串，由单个冒号“:”分隔。

我们可以看到，当在浏览器窗口而不是 HTML 表单中询问凭据时，使用了基本身份验证。我们可能会在浏览器中得到提示：

[![Google Chrome 凭据窗口](https://www.baeldung.com/wp-content/uploads/2022/04/basicAuthenticationChrome.png)](https://www.baeldung.com/wp-content/uploads/2022/04/basicAuthenticationChrome.png)

因此，如果我们尝试在受保护的 Web 资源上启动 JMeter 测试，响应代码将是 HTTP 401，这意味着“未经授权”。我们还将收到一个“WWW-Authenticate”响应标头，它将描述服务器所需的身份验证类型。在这种情况下，它将是“基本”：

[![HTTP 401 响应标头](https://www.baeldung.com/wp-content/uploads/2022/04/http-401-response-code.png)](https://www.baeldung.com/wp-content/uploads/2022/04/http-401-response-code.png)

## 3. JMeter中实现基本认证的简单方法

### 3.1. 添加授权标头

发送凭据的最简单方法是将它们直接添加到请求标头中。我们可以使用HTTP 标头管理器组件轻松完成此操作，它允许我们向 HTTP 请求组件发送的请求添加标头。Header Manager 必须是 HTTP Request 组件的子组件：

[![带有标题管理器的测试计划结构](https://www.baeldung.com/wp-content/uploads/2022/04/jmeter-header-manager.png)](https://www.baeldung.com/wp-content/uploads/2022/04/jmeter-header-manager.png)

在HTTP Header Manager的配置选项卡中，我们只需添加一个键/值条目，其中包含我们的身份验证详细信息和授权作为名称：

[![HTTP 标头管理器配置选项卡](https://www.baeldung.com/wp-content/uploads/2022/04/http-header-manager-config.png)](https://www.baeldung.com/wp-content/uploads/2022/04/http-header-manager-config.png)

我们可以使用[在线工具](https://www.base64encode.org/)对我们的字符串进行编码并将其粘贴到 Header Manager 中。我们应该注意在编码凭证之前添加“basic” 。

如果一切顺利，我们应该会收到来自服务器的 200 响应代码。

### 3.2. 使用 JSR223 预处理器对凭证进行编码

如果我们希望 JMeter 为我们编码我们的凭据，我们可以使用JSR223 PreProcessor组件。如果我们想改变我们的测试计划使用的凭据，我们将需要使用它。

我们所要做的就是在我们的HTTP Header Manager组件之前添加一个JSR223 PreProcessor ：

[![JSR223 预处理器](https://www.baeldung.com/wp-content/uploads/2022/04/jsr223-preprocessor.png)](https://www.baeldung.com/wp-content/uploads/2022/04/jsr223-preprocessor.png)

使用此组件，我们可以在运行时执行脚本。我们需要提供一个脚本来检索凭据并对它们进行编码。让我们使用 Java：

```java
import org.apache.commons.codec.binary.Base64;

String username = vars.get("username");
String password = vars.get("password");
String credentials = username + ":" + password;
byte[] encodedUsernamePassword = Base64.encodeBase64(credentials.getBytes());
vars.put("base64Credentials", new String(encodedUsernamePassword));
```

我们现在应该在用户定义变量组件中定义用户名和密码变量：

[![用户定义的变量](https://www.baeldung.com/wp-content/uploads/2022/04/User-Defined-Variables.png)](https://www.baeldung.com/wp-content/uploads/2022/04/User-Defined-Variables.png)

最后，在HTTP 标头管理器组件中，我们必须设置授权标头以使用编码凭证：

[![使用 JSR223 的 HTTP 标头管理器配置](https://www.baeldung.com/wp-content/uploads/2022/04/HTTP-Header-Manager-Config-with-JSR223.png)](https://www.baeldung.com/wp-content/uploads/2022/04/HTTP-Header-Manager-Config-with-JSR223.png)

我们完成了！一切都应该工作正常，我们能够在用户定义的变量中轻松更改凭据。

## 4. 使用 HTTP 授权管理器

JMeter 提供了HTTP 授权管理器组件，以简化使用凭据进行身份验证的过程。使用此组件，我们可以为多个域和身份验证协议提供凭据。该组件必须是线程组的子组件，并且在HTTP 请求组件之前定义：

[![JMeter 授权管理器](https://www.baeldung.com/wp-content/uploads/2022/04/JMeter-Authorization-Manager.png)](https://www.baeldung.com/wp-content/uploads/2022/04/JMeter-Authorization-Manager.png)

在组件的配置选项卡中，我们必须定义用于身份验证的用户名和密码：

[![HTTP 授权管理器配置](https://www.baeldung.com/wp-content/uploads/2022/04/HTTP-Authorization-Manager-Configuration.png)](https://www.baeldung.com/wp-content/uploads/2022/04/HTTP-Authorization-Manager-Configuration.png)

如果我们在用户定义的变量组件中定义了用户名和密码，我们可以在此选项卡中使用变量。它也适用于密码。虽然它仍然被屏蔽，但我们可以在密码字段中输入“${password}” 。

我们必须注意选择正确的身份验证机制。在这里，我们将选择“BASIC”。

就是这样！HTTP Request组件会自动在请求中添加一个Authorization标头，我们应该会得到一个 HTTP 200 OK 响应代码。

## 5. 在 HTTP 授权管理器中使用多个凭证

有时，我们可能希望在测试期间使用多个凭据。例如，这可能有助于验证基于角色的访问限制。

要配置此测试用例，我们应该创建一个 CSV 文件，我们将在其中存储凭证和对我们的测试计划有用的其他信息。该文件由JMeter 中的CSV 数据集配置组件读取。该组件应该是线程组的子组件，并将在每个线程组循环中迭代 CSV 行：

[![CSV 数据集配置组件](https://www.baeldung.com/wp-content/uploads/2022/04/CSV-Data-Set-Config-Component.png)](https://www.baeldung.com/wp-content/uploads/2022/04/CSV-Data-Set-Config-Component.png)

然后，在这个组件中，我们必须定义：

-   文件的位置作为用户定义变量组件中的路径
-   CSV 数据集组件执行后设置的变量名称
-   组件是否应该忽略第一行——在 CSV 文件中有列名的情况下很有帮助
-   CSV 文件中使用哪个定界符

[![CSV 数据集配置选项卡](https://www.baeldung.com/wp-content/uploads/2022/04/CSV-Data-Set-Config-Tab.png)](https://www.baeldung.com/wp-content/uploads/2022/04/CSV-Data-Set-Config-Tab.png)

在 CSV 文件中定义多个凭据时，我们应该注意配置我们的线程组以执行多个循环。

通过这些设置，我们应该能够看到我们的请求标头中使用了不同的凭据。

## 六. 总结

在本文中，我们了解了基本身份验证如何用于 HTTP 资源。

我们还学习了如何在 Apache JMeter 中设置测试计划以使用此协议进行身份验证。我们介绍了硬编码凭据，使用 JSR223 预处理器，然后从 CSV 文件提供多个凭据。