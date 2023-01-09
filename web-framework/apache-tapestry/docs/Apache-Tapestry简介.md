## 1. 概述

如今，从社交网络到银行业务，从医疗保健到政府服务，所有活动都可以在线进行。因此，他们严重依赖网络应用程序。

Web 应用程序使用户能够消费/享受公司提供的在线服务。同时，它充当后端软件的接口。

在本介绍性教程中，我们将探索 Apache Tapestry Web 框架并使用它提供的基本功能创建一个简单的 Web 应用程序。

## 2.阿帕奇挂毯

Apache Tapestry 是一个基于组件的框架，用于构建可扩展的 Web 应用程序。

它遵循配置约定范式，并使用注解和命名约定进行配置。

所有组件都是简单的 POJO。同时，它们是从头开发的，不依赖于其他库。

除了 Ajax 支持，Tapestry 还具有强大的异常报告功能。它还提供了一个广泛的内置通用组件库。

在其他强大功能中，一个突出的功能是代码的热重新加载。因此，利用这个特性，我们可以即时看到开发环境的变化。

## 3.设置

Apache Tapestry 需要一组简单的工具来创建 Web 应用程序：

-  Java1.6 或更高版本
-   构建工具(Maven 或 Gradle)
-   IDE(Eclipse 或 IntelliJ)
-   应用服务器(Tomcat 或 Jetty)

在本教程中，我们将结合使用Java8、Maven、Eclipse 和 Jetty Server。

要设置[最新的](https://tapestry.apache.org/download.html)Apache Tapestry 项目，我们将使用[Maven 原型](https://www.baeldung.com/maven-archetype#creating-archetype)并按照官方文档提供的[说明进行操作：](https://tapestry.apache.org/getting-started.html)

```shell
$ mvn archetype:generate -DarchetypeCatalog=http://tapestry.apache.org
```

或者，如果我们有一个现有项目，我们可以简单地将[tapestry-core](https://search.maven.org/search?q=g:org.apache.tapestry a:tapestry-core) Maven 依赖项添加到pom.xml：

```xml
<dependency>
    <groupId>org.apache.tapestry</groupId>
    <artifactId>tapestry-core</artifactId>
    <version>5.4.5</version>
</dependency>
```

准备好设置后，我们可以通过以下 Maven 命令启动应用程序apache-tapestry ：

```shell
$ mvn jetty:run
```

默认情况下，可以在localhost:8080/apache-tapestry访问该应用程序：

[![主页](https://www.baeldung.com/wp-content/uploads/2019/11/homepage-1-1024x730.png)](https://www.baeldung.com/wp-content/uploads/2019/11/homepage-1.png)

## 4.项目结构

让我们探索 Apache Tapestry 创建的项目布局：

[![树结构](https://www.baeldung.com/wp-content/uploads/2019/11/tree_structure.png)](https://www.baeldung.com/wp-content/uploads/2019/11/tree_structure.png)

我们可以看到一个类似 Maven 的项目结构，以及一些基于约定的包。

Java 类位于src/main/java中，并分为组件、页面和服务。

同样，src/main/resources包含我们的模板(类似于 HTML 文件)——它们具有.tml扩展名。

对于位于components和pages目录下的每个Java类，应该创建一个同名的模板文件。


src /main/webapp目录包含图像、样式表和 JavaScript 文件等资源。同样，测试文件放在src/test中。

最后，src/site将包含文档文件。

为了更好的理解，我们来看看在 Eclipse IDE 中打开的项目结构：

[![项目结构](https://www.baeldung.com/wp-content/uploads/2019/11/project_structure.png)](https://www.baeldung.com/wp-content/uploads/2019/11/project_structure.png)

## 5.注解

让我们讨论Apache Tapestry 提供的一些日常使用的便利[注解。](https://tapestry.apache.org/annotations.html)展望未来，我们将在我们的实现中使用这些注解。

### 5.1. @注入

@Inject注解在org.apache.tapestry5.ioc.annotations包中可用，它提供[了](https://tapestry.apache.org/current/apidocs/org/apache/tapestry5/ioc/annotations/Inject.html)一种在Java类中注入依赖项的简单方法。

这个注解对于注入资产、块、资源和服务来说非常方便。

### 5.2. @注入页面

在org.apache.tapestry5.annotations包中可用，[@InjectPage](https://tapestry.apache.org/current/apidocs/org/apache/tapestry5/annotations/InjectPage.html)注解允许我们将页面注入另一个组件。此外，注入的页面始终是只读属性。

### 5.3. @注入组件

同样，[@InjectComponent](https://tapestry.apache.org/current/apidocs/org/apache/tapestry5/annotations/InjectComponent.html)注解允许我们注入模板中定义的组件。

### 5.4. @日志

@Log注解在org.apache.tapestry5.annotations包[中](https://tapestry.apache.org/current/apidocs/org/apache/tapestry5/annotations/Log.html)可用，并且可以方便地在任何方法上启用 DEBUG 级别日志记录。它记录方法进入和退出，以及参数值。

### 5.5. @财产

[@Property](https://tapestry.apache.org/current/apidocs/org/apache/tapestry5/annotations/Property.html)注解在org.apache.tapestry5.annotations包中可用，将字段标记为属性。同时，它会自动为属性创建 getter 和 setter。

### 5.6. @范围

类似地，[@Parameter](https://tapestry.apache.org/current/apidocs/org/apache/tapestry5/annotations/Parameter.html)注解表示一个字段是一个组件参数。

## 6.页面

因此，我们都准备好探索该框架的基本功能。让我们在我们的应用程序中创建一个新的主页。

首先，我们将在src/main/java的页面目录中定义一个Java类Home：

```java
public class Home {
}
```

### 6.1. 模板

然后，我们将在src/main/resources下的pages目录中创建一个相应的Home.tml[模板](https://tapestry.apache.org/component-templates.html)。

扩展名为.tml(Tapestry 标记语言)的文件类似于 Apache Tapestry 提供的带有 XML 标记的 HTML/XHTML 文件。

例如，让我们看一下Home.tml模板：

```html
<html xmlns:t="http://tapestry.apache.org/schema/tapestry_5_4.xsd">
    <head>
        <title>apache-tapestry Home</title>
    </head>
    <body>
        <h1>Home</h1>
    </body>   
</html>
```

瞧！只需重启 Jetty 服务器，我们就可以访问位于localhost:8080/apache-tapestry/home 的主页：

[![首页1](https://www.baeldung.com/wp-content/uploads/2019/11/home1-1024x197.png)](https://www.baeldung.com/wp-content/uploads/2019/11/home1.png)

### 6.2. 财产

让我们探讨如何在主页上呈现属性。

为此，我们将在Home类中添加一个属性和一个 getter 方法：

```java
@Property
private String appName = "apache-tapestry";

public Date getCurrentTime() {
    return new Date();
}
```

要在主页上呈现appName属性，我们可以简单地使用${appName}。

同样，我们可以编写${currentTime}从页面访问getCurrentTime方法。

### 6.3. 本土化

Apache Tapestry 提供集成的[本地化](https://tapestry.apache.org/localization.html)支持。按照惯例，页面名称属性文件保留要在页面上呈现的所有本地消息的列表。

例如，我们将在主页的pages目录中创建一个home.properties文件，其中包含一条本地消息：

```plaintext
introMsg=Welcome to the Apache Tapestry Tutorial
```

消息属性不同于Java属性。

出于同样的原因，带有消息前缀的键名用于呈现消息属性——例如，${message:introMsg}。

### 6.4. 布局组件

让我们通过创建Layout.java类来定义一个基本的布局组件。我们将该文件保存在src/main/java的组件目录中：

```java
public class Layout {
    @Property
    @Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
    private String title;
}
```

这里，title属性被标记为必需，绑定的默认前缀设置为文字String。

然后，我们在src/main/resources的components目录下写一个对应的模板文件Layout.tml：

```html
<html xmlns:t="http://tapestry.apache.org/schema/tapestry_5_4.xsd">
    <head>
        <title>${title}</title>
    </head>
    <body>
        <div class="container">
            <t:body />
            <hr/>
            <footer>
                <p>© Your Company</p>
            </footer>
        </div>
    </body>
</html>
```

现在，让我们使用主页上的布局：

```html
<html t:type="layout" title="apache-tapestry Home" 
    xmlns:t="http://tapestry.apache.org/schema/tapestry_5_4.xsd">
    <h1>Home! ${appName}</h1>
    <h2>${message:introMsg}</h2>
    <h3>${currentTime}</h3>
</html>
```

请注意，[命名空间](https://tapestry.apache.org/schema/tapestry_5_4.xsd)用于标识Apache Tapestry 提供的元素( t:type和t:body )。同时命名空间还提供组件和属性。

在这里，t:type将设置主页上的布局。而且，t:body元素将插入页面的内容。

让我们看一下主页的布局：

[![首页2-1](https://www.baeldung.com/wp-content/uploads/2019/11/homepage2-1-1024x302.png)](https://www.baeldung.com/wp-content/uploads/2019/11/homepage2-1.png)

## 7.表格

让我们创建一个带有表单的登录页面，以允许用户登录。

如前所述，我们将首先创建一个Java类Login：

```java
public class Login {
    // ...
    @InjectComponent
    private Form login;

    @Property
    private String email;

    @Property
    private String password;
}
```

在这里，我们定义了两个属性—— email和password。此外，我们还为登录注入了一个Form组件。

然后，让我们创建一个相应的模板login.tml：

```html
<html t:type="layout" title="apache-tapestry com.example"
      xmlns:t="http://tapestry.apache.org/schema/tapestry_5_3.xsd"
      xmlns:p="tapestry:parameter">
    <t:form t:id="login">
        <h2>Please sign in</h2>
        <t:textfield t:id="email" placeholder="Email address"/>
        <t:passwordfield t:id="password" placeholder="Password"/>
        <t:submit class="btn btn-large btn-primary" value="Sign in"/>
    </t:form>
</html>
```

现在，我们可以在localhost:8080/apache-tapestry/login访问登录页面：

[![登录-1](https://www.baeldung.com/wp-content/uploads/2019/11/login-1-1024x334.png)](https://www.baeldung.com/wp-content/uploads/2019/11/login-1.png)

## 8. 验证

[Apache Tapestry 提供了一些用于表单验证](https://tapestry.apache.org/forms-and-validation.html)的内置方法。它还提供了处理表单提交成功或失败的方法。

内置方法遵循事件和组件名称的约定。例如，方法onValidationFromLogin将验证登录组件。

同样，像onSuccessFromLogin和onFailureFromLogin这样的方法分别用于成功和失败事件。

因此，让我们将这些内置方法添加到Login类中：

```java
public class Login {
    // ...
    
    void onValidateFromLogin() {
        if (email == null)
            System.out.println("Email is null);

        if (password == null)
            System.out.println("Password is null);
    }

    Object onSuccessFromLogin() {
        System.out.println("Welcome! Login Successful");
        return Home.class;
    }

    void onFailureFromLogin() {
        System.out.println("Please try again with correct credentials");
    }
}
```

## 9. 警报

没有适当的警报，表单验证是不完整的。更不用说，该框架还内置了对警报消息的支持。

为此，我们将首先在Login类中注入[AlertManager](https://tapestry.apache.org/current/apidocs/org/apache/tapestry5/alerts/AlertManager.html)的实例来管理警报。然后，将现有方法中的println语句替换为警报消息：

```java
public class Login {
    // ...
    @Inject
    private AlertManager alertManager;

    void onValidateFromLogin() {
        if(email == null || password == null) {
            alertManager.error("Email/Password is null");
            login.recordError("Validation failed"); //submission failure on the form
        }
    }
 
    Object onSuccessFromLogin() {
        alertManager.success("Welcome! Login Successful");
        return Home.class;
    }

    void onFailureFromLogin() {
        alertManager.error("Please try again with correct credentials");
    }
}
```

让我们看看登录失败时的警报：

[![登录失败-1](https://www.baeldung.com/wp-content/uploads/2019/11/loginfail-1-1024x456.png)](https://www.baeldung.com/wp-content/uploads/2019/11/loginfail-1.png)

## 10. 阿贾克斯

到目前为止，我们已经探索了使用表单创建简单主页的方法。同时，我们看到了对警报消息的验证和支持。

接下来，让我们探索 Apache Tapestry 对 Ajax 的内置支持。

首先，我们将在Home类中注入[AjaxResponseRenderer](https://tapestry.apache.org/current/apidocs/org/apache/tapestry5/services/ajax/AjaxResponseRenderer.html)和[Block](https://tapestry.apache.org/current/apidocs/org/apache/tapestry5/Block.html)组件的实例。然后，我们将创建一个方法onCallAjax来处理 Ajax 调用：

```java
public class Home {
    // ....

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;
    
    @Inject
    private Block ajaxBlock;

    @Log
    void onCallAjax() {
        ajaxResponseRenderer.addRender("ajaxZone", ajaxBlock);
    }
}
```

此外，我们需要对Home.tml进行一些更改。

首先，我们将添加[eventLink](https://tapestry.apache.org/current/apidocs/org/apache/tapestry5/corelib/components/EventLink.html)以调用onCallAjax方法。然后，我们将添加一个带有 id ajaxZone的[区域](https://tapestry.apache.org/current/apidocs/org/apache/tapestry5/corelib/components/Zone.html)元素来呈现 Ajax 响应。

最后，我们需要一个块组件，它将被注入到Home 类中并呈现为 Ajax 响应：

```html
<p><t:eventlink event="callAjax" zone="ajaxZone" class="btn btn-default">Call Ajax</t:eventlink></p>
<t:zone t:id="ajaxZone"></t:zone>
<t:block t:id="ajaxBlock">
    <hr/>
    <h2>Rendered through Ajax</h2>
    <p>The current time is: <strong>${currentTime}</strong></p>
</t:block>
```

我们来看看更新后的主页：

[![首页1](https://www.baeldung.com/wp-content/uploads/2019/11/home-1-1024x338.png)](https://www.baeldung.com/wp-content/uploads/2019/11/home-1.png)

然后，我们可以单击 Call Ajax 按钮并查看ajaxResponseRenderer 的运行情况：

[![主页Ajax](https://www.baeldung.com/wp-content/uploads/2019/11/homeAjax-1024x419.png)](https://www.baeldung.com/wp-content/uploads/2019/11/homeAjax.png)

## 11. 记录

要启用内置的日志记录功能，需要注入[Logger的实例。](http://www.slf4j.org/api/org/slf4j/Logger.html)然后，我们可以使用它来记录任何级别的日志，如 TRACE、DEBUG 和 INFO。

因此，让我们在Home 类中进行必要的更改：

```java
public class Home {
    // ...

    @Inject
    private Logger logger;

    void onCallAjax() {
        logger.info("Ajax call");
        ajaxResponseRenderer.addRender("ajaxZone", ajaxBlock);
    }
}
```

现在，当我们点击 Call Ajax 按钮时，记录器将在 INFO 级别记录：

```shell
[INFO] pages.Home Ajax call

```

## 12.总结

在本文中，我们探讨了 Apache Tapestry Web 框架。

首先，我们创建了一个快速入门 Web 应用程序并使用 Apache Tapestry 的基本功能添加了一个主页，例如组件、页面和模板。

然后，我们检查了 Apache Tapestry 提供的一些方便的注解来配置属性和组件/页面注入。

最后，我们探讨了框架提供的内置 Ajax 和日志记录支持。