## 1. 概述

[Keycloak](https://www.keycloak.org/)是一种开源身份和访问管理或IAM解决方案，可用作第三方授权服务器来管理我们的Web或移动应用程序的身份验证和授权要求。

在本教程中，我们将重点介绍**如何为我们的Keycloak服务器自定义主题**，以便我们可以为面向最终用户的网页提供不同的外观。

首先，我们将从独立的Keycloak服务器的角度建立一个背景，在后面的部分中，我们将在嵌入式示例的上下文中查看类似的示例。

为此，**我们将在之前的文章的基础上构建：在Spring Boot应用程序中使用Keycloak和嵌入**[Keycloak的快速指南]()。因此，对于刚起步的人来说，最好先通读一遍。

## 2. Keycloak中的主题

### 2.1 默认主题

Keycloak中预先构建了几个主题，并与发行版捆绑在一起。

对于独立服务器，可以在keycloak-<version\>/themes目录中找到不同的文件夹：

-   base：一个包含HTML模板和消息包的骨架主题；所有主题，包括自定义主题，通常都继承自它
-   keycloak：包含用于美化页面的图像和样式表；如果我们不提供自定义主题，这是默认使用的主题

不建议修改现有主题。相反，我们应该创建一个扩展上述两个主题之一的新主题。

**要创建一个新的自定义主题，我们需要添加一个新文件夹(我们称它为custom)，添加到themes目录**。如果我们想要彻底检修，从base文件夹复制内容是启动的最佳方式。

对于我们的演示，我们不打算替换所有内容，因此从keycloak目录中获取内容是务实的。

正如我们将在下一节中看到的那样，自定义只需要我们要覆盖的主题类型的内容，而不是整个keycloak文件夹。

### 2.2 主题类型

Keycloak支持六种类型的主题：

1.  常用：用于字体等常用项目；由其他主题类型导入
2.  欢迎：用于登录页面
3.  登录：用于登录、OTP、授予、注册和忘记密码页面
4.  帐户：用于用户帐户管理页面
5.  管理控制台：用于管理控制台
6.  电子邮件：用于服务器发送的电子邮件

上面列表中的最后四个主题可以通过独立服务器的管理控制台进行设置，当我们在themes目录中创建一个新文件夹时，它可以在服务器重新启动后进行选择。

让我们使用凭据initial1/zaq1!QAZ登录到管理控制台，然后转到我们Realm的Themes选项卡：

<img src="../assets/img_26.png">

值得注意的是，主题是按Realm设置的，因此我们可以为不同的Realm设置不同的主题。在这里，**我们为SpringBootKeycloak Realm的用户帐户管理设置自定义主题**。

### 2.3 主题类型的结构

除了我们的默认主题部分中概述的HTML模板、消息包、图像和样式表之外，Keycloak中的主题还包含更多元素-主题属性和脚本。

每个主题类型都包含一个theme.properties文件。例如，让我们从帐户类型看一下这个文件：

```properties
parent=base
import=common/keycloak

styles=css/account.css
stylesCommon=node_modules/patternfly/dist/css/patternfly.min.css node_modules/patternfly/dist/css/patternfly-additions.min.css
```

正如我们所看到的，这个主题从base主题扩展到它的所有HTML和消息包，并且还导入了通用主题以包含其中的一些样式。除此之外，它还定义了自己的样式css/account.css。

脚本是一项可选功能，如果我们需要为给定主题类型的模板包含定制的JavaScript文件，我们可以创建一个resources/js目录并将它们保存在那里。接下来，我们需要将它们包含在我们的theme.properties中：

```properties
scripts=js/script1.js js/script2.js
```

### 2.4 添加自定义

让我们以我们的账户管理页面为例，看看如何改变它的外观。准确地说，我们将更改页面上显示的徽标。

在我们进行所有更改之前，下面是原始模板，可从[http://localhost:8080/realms/SpringBootKeycloak/account]()获得：

<img src="../assets/img_27.png">

让我们尝试将徽标更改为我们自己的徽标，为此，我们需要在themes/custom目录中添加一个新文件夹account，我们可以从themes/keycloak目录中复制它，以便我们拥有所有必需的元素。

现在，只需添加我们的新徽标文件(比如tuyucheng.png)到我们custom目录中的resources/img并修改resources/css/account.css：

```css
.navbar-title {
    background-image: url('../img/tuyucheng.png');
    height: 25px;
    background-repeat: no-repeat;
    width: 123px;
    margin: 3px 10px 5px;
    text-indent: -99999px;
}

.container {
    height: 100%;
    background-color: #fff;
}
```

这是页面现在的样子：

<img src="../assets/img_30.png">

**重要的是，在开发阶段，我们希望立即看到更改的效果，而无需重新启动服务器。为了实现这一点，我们需要对standalone/configuration文件夹中的Keycloak的standalone.xml进行一些更改**：

```xml
<theme>
    <staticMaxAge>-1</staticMaxAge>
    <cacheThemes>false</cacheThemes>
    <cacheTemplates>false</cacheTemplates>
    ...
</theme>
```

与我们在此处自定义帐户主题的方式类似，要更改其他主题类型的外观，我们需要添加名为admin、email或login的新文件夹，并遵循相同的过程。

### 2.5 自定义欢迎页面

要自定义欢迎页面，首先，我们需要在standalone.xml中添加一行：

```xml
<theme>
    ...
    <welcomeTheme>custom</welcomeTheme>
    ... 
</theme> 
```

其次，我们必须在themes/custom下创建一个文件夹welcome。同样，谨慎的做法是从默认的keycloak主题目录中复制index.ftl和theme.properties以及现有资源。

现在让我们尝试更改此页面的背景。

让我们导航到[http://localhost:8080/](http://localhost:8080/)看看它最初的样子：

<img src="../assets/img_28.png">

要更改背景图像，请在themes/custom/welcome/resources中保留新图像，比如geo.png，然后只需编辑resources/css/welcome.css：

```css
body {
    background: #fff url(../geo.png);
    background-size: cover;
}
```

效果如下：

<img src="../assets/img_29.png">

## 3. 自定义嵌入式Keycloak服务器

根据定义，[嵌入式Keycloak服务器]()意味着我们没有在我们的机器上安装IAM提供程序。因此，**我们需要在源代码中保留所有必需的工件，例如themes.properties和CSS文件**。

将它们保存在Spring Boot项目的src/main/resources/themes文件夹中是个好的选择。

当然，由于主题结构的文件是一样的，因此我们自定义它们的方式也与独立服务器相同。

然而，我们需要配置一些东西来指示Keycloak服务器从我们的自定义主题中挑选内容。

### 3.1 Realm定义文件的更改

首先，让我们看看如何为给定的主题类型指定自定义主题。

回想一下，在我们的独立服务器的情况下，在我们管理控制台的主题页面上，我们从下拉列表中为帐户主题添加了自定义主题。

为了在这里达到同样的效果，我们需要在我们的Realm定义文件tuyucheng-realm.json中添加一行：

```json
"accountTheme": "custom",
```

这就是我们所需要的；所有其他类型(例如登录和电子邮件)仍将遵循标准主题。

### 3.2 重定向到自定义主题目录

接下来，让我们看看如何告诉服务器上述自定义主题所在的位置。

我们可以通过几种方式做到这一点。

在为我们的嵌入式服务器启动Boot App时，我们可以将主题目录指定为VM参数：

```bash
mvn spring-boot:run -Dkeycloak.theme.dir=/src/main/resources/themes
```

为了以编程方式实现相同的目的，我们可以将其设置为@SpringBootApplication类中的系统属性：

```java
public static void main(String[] args) throws Exception {
    System.setProperty("keycloak.theme.dir","src/main/resources/themes");
    SpringApplication.run(JWTAuthorizationServerApp.class, args);
}
```

或者，我们可以更改keycloak-server.json中的服务器配置：

```json
"theme": {
    ....
    "welcomeTheme": "custom",
    "folder": {
        "dir": "src/main/resources/themes"
    }
},
```

值得注意的是，我们在这里还添加了一个welcomeTheme属性，以启用对欢迎页面的自定义。

如前所述，**对CSS文件和图像的所有其他更改保持不变**。

要查看欢迎页面的变化，我们需要启动嵌入式服务器并导航到[http://localhost:8083/](http://localhost:8083/)。

帐户管理页面位于[http://localhost:8083/realms/tuyucheng/account](http://localhost:8083/realms/tuyucheng/account)，我们可以使用以下凭据访问它：john@test.com/123。

## 4. 总结

在本教程中，**我们了解了Keycloak中的主题-它们的类型和结构**。

然后我们查看了几个预构建的主题以及如何扩展它们以在独立实例中创建我们自己的自定义主题。

最后，我们看到了如何在嵌入式Keycloak服务器中实现相同的目的。

与往常一样，源代码可在GitHub上获得。对于独立服务器，它在[教程GitHub上]()，对于嵌入式实例，它在[OAuth GitHub上]()。