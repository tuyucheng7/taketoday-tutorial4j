## 1. 概述

[Keycloak](https://www.keycloak.org/)是一个第三方授权服务器，用于管理我们的Web或移动应用程序的身份验证和授权要求，它使用默认登录页面代表我们的应用程序登录用户。

在本教程中，我们将重点介绍**如何为我们的Keycloak服务器自定义登录页面**，以便我们可以拥有不同的外观，我们将在独立服务器和嵌入式服务器上演示这一点。

我们将[在Keycloak教程的自定义主题的]()基础上进行构建。

## 2. 自定义独立的Keycloak服务器

继续我们的[自定义]()主题示例，让我们先看看独立服务器。

### 2.1 管理控制台设置

要启动服务器，让我们导航到我们的Keycloak发行版所在的目录，并从其bin文件夹运行以下命令：

```bash
./standalone.sh -Djboss.socket.binding.port-offset=100
```
服务器启动后，由于我们之前对[standalone.xml]()进行了修改，因此我们只需刷新页面即可看到我们所做的更改。

现在让我们在themes/custom目录中创建一个名为login的新文件夹，为了简单起见，我们首先将themes/keycloak/login目录的所有内容复制到这里，这是默认的登录页面主题。

然后，我们将转到[管理控制台]()，输入initial1/zaq1!QAZ凭据并转到我们realm的主题选项卡：

<img src="../assets/img_25.png">

我们将为登录主题选择custom并保存我们的更改。

有了这个设置，我们现在可以尝试一些自定义，但在此之前，让我们看一下默认的[登录页面](http://localhost:8080/realms/SpringBootKeycloak/protocol/openid-connect/auth?response_type=code&client_id=login-app&scope=openid&redirect_uri=http://localhost:8081/)：

<img src="../assets/img_31.png">

### 2.2 添加自定义

现在假设我们需要改变背景，为此，我们将打开login/resources/css/login.css并更改类定义：

```css
.login-pf body {
    background: #39a5dc;
    background-size: cover;
    height: 100%;
}
```

要查看效果，我们刷新一下页面：

<img src="../assets/img_32.png">

接下来，让我们尝试更改用户名和密码的标签。

为此，我们需要在theme/login/messages文件夹中创建一个新文件messages_en.properties，此文件覆盖用于给定属性的默认消息包：

```properties
usernameOrEmail=Enter Username
password=Enter Password
```

要进行测试，请再次刷新页面：

<img src="../assets/img_33.png">

假设我们想要更改整个HTML或其中的一部分，我们需要覆盖Keycloak默认使用的freemarker模板，登录页面的默认模板保存在base/login目录中。

假设我们希望显示WELCOME TO TUYUCHENG来代替SPRINGBOOTKEYCLOAK。为此，我们需要将base/login/template.ftl复制到我们的custom/login文件夹。

在复制的文件中，更改以下行：

```html
<div id="kc-header-wrapper" class="${properties.kcHeaderWrapperClass!}">
    ${kcSanitize(msg("loginTitleHtml",(realm.displayNameHtml!'')))?no_esc}
</div>
```

为：

```html
<div id="kc-header-wrapper" class="${properties.kcHeaderWrapperClass!}">
    WELCOME TO TUYUCHENG
</div>
```

登录页面现在将显示我们的自定义消息而不是Realm名称。

## 3. 自定义嵌入式Keycloak服务器

这里的第一步是将我们为独立服务器更改的所有工件添加到我们的嵌入式授权服务器的源代码中。

因此，让我们在src/main/resources/themes/custom中创建一个新文件夹login，其中包含以下内容：

<img src="../assets/img_34.png">

现在我们需要做的就是在我们的Realm定义文件tuyucheng-realm.json中添加指令，以便将custom用于我们的登录主题类型：

```json
"loginTheme": "custom",
```

我们已经[重定向到自定义主题目录]()，以便我们的服务器知道从哪里获取登录页面的主题文件。

为了进行测试，让我们点击登录页面：

<img src="../assets/img_35.png">

正如我们所看到的，之前为独立服务器所做的所有自定义(例如背景、标签名称和页面标题)都显示在这里。

## 4. 绕过Keycloak登录页面

从技术上讲，我们可以通过[使用密码或直接访问授权](https://oauth.net/2/grant-types/password/)流程来完全绕过Keycloak登录页面，但是，**强烈建议根本不要使用这种授权类型**。

在这种情况下，没有获取授权代码然后接收访问令牌作为交换的中间步骤。相反，我们可以通过REST API调用直接发送用户凭据并获取访问令牌作为响应。

这实际上意味着我们可以使用我们的登录页面来收集用户的ID和密码，并将其与客户端ID和密码一起通过POST发送到Keycloak的令牌端点。

但同样，由于Keycloak提供了丰富的登录选项功能集-例如记住我、密码重置和MFA(仅举几例)，因此没有理由绕过它。

## 5. 总结

在本教程中，我们学习了如何更改Keycloak的默认登录页面并添加我们的自定义项，我们在独立实例和嵌入式实例中都演示了这一点。

最后，我们简要介绍了如何完全绕过Keycloak的登录页面以及为什么不推荐这样做。

与往常一样，源代码可在GitHub上获得。对于独立服务器，它在[教程GitHub上]()，对于嵌入式实例，它在[OAuth GitHub上]()。