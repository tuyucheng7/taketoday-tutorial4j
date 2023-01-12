## 1. 概述

在本文中，我们将了解 Tomcat 服务器的基础知识、它的工作原理以及如何启用 Tomcat 的单点登录 ( [SSO](https://tomcat.apache.org/tomcat-10.0-doc/config/host.html#Single_Sign_On) ) 功能。我们将探讨 Tomcat 服务器和 Web 应用程序所需的配置。

## 2. Tomcat架构

构成 Catalina servlet 容器的主要部分是包含定义连接器的服务的服务器和由主机构建的引擎，最后，这些主机将包含上下文或 Web 应用程序。

连接器侦听客户端的请求并发回响应。在 Tomcat 10 中，我们可以找到以下协议的连接器：[HTTP/1.1](https://tomcat.apache.org/tomcat-10.0-doc/config/http.html)、[HTTP/2](https://tomcat.apache.org/tomcat-10.0-doc/config/http2.html)和[AJP](https://tomcat.apache.org/tomcat-10.0-doc/config/ajp.html)。

引擎将处理连接器收到的请求并生成输出。它将包含一个 [处理管道](https://en.wikipedia.org/wiki/Pipeline_(software))，这是一个进程链，将根据请求执行以产生响应。这些进程就是 Tomcat 的[阀门](https://tomcat.apache.org/tomcat-10.0-doc/config/valve.html#Introduction)。例如，Tomcat 上的 SSO 是作为阀门实现的。

之后，我们找到将定义虚拟主机的主机，这些虚拟主机将网络名称与服务器相关联。这是将定义 SSO 阀的级别，因此主机的所有上下文都将在 SSO 下。

最后，我们将拥有与主机关联的上下文元素。这些上下文是将在服务器上运行的 Web 应用程序。上下文必须遵循 servlet 规范 2.3 或更高版本。

## 3. Tomcat单点登录

Tomcat 在必须在主机级别配置的阀中实现单点登录功能。它的工作方式是 SSO 阀将存储用户凭据并在需要时传递它们，因此用户无需再次登录。

SSO 阀需要满足以下要求：

-   [Realm](https://tomcat.apache.org/tomcat-10.0-doc/config/realm.html#Introduction)或“用户数据库”必须由虚拟主机下的所有 Web 应用程序共享。
-   Web 应用程序身份验证机制必须是标准身份验证器之一：[Basic](https://tomcat.apache.org/tomcat-10.0-doc/config/valve.html#Basic_Authenticator_Valve)、[Digest](https://tomcat.apache.org/tomcat-10.0-doc/config/valve.html#Digest_Authenticator_Valve)、[Form](https://tomcat.apache.org/tomcat-10.0-doc/config/valve.html#Form_Authenticator_Valve)、[SSL](https://tomcat.apache.org/tomcat-10.0-doc/config/valve.html#SSL_Authenticator_Valve)或[SPNEGO](https://tomcat.apache.org/tomcat-10.0-doc/config/valve.html#SPNEGO_Valve)。
-   当客户端请求受保护的资源时，服务器将执行 Web 应用程序的身份验证机制。
-   服务器将使用认证用户的角色访问虚拟主机下Web应用程序的受保护资源，而无需再次登录。
-   当用户退出一个网络应用程序时，服务器将使所有网络应用程序中的用户会话失效。
-   客户端必须接受 cookie。cookie 存储将请求与用户凭据相关联的令牌。

### 3.1. Tomcat 服务器配置

在服务器端，我们需要配置SingleSignOn阀和领域或“用户数据库”。这些配置在 Tomcat 安装的 conf 文件夹下的 server.xml 文件中。要添加 SSO 阀，我们需要取消注解以下行：

```xml
<Valve className="org.apache.catalina.authenticator.SingleSignOn" />
```

对于本文的示例，我们将依赖默认配置的 Realm，我们只需要将用户添加到数据库中。领域定义如下所示：

```xml
<Realm
  className="org.apache.catalina.realm.UserDatabaseRealm"
  resourceName="UserDatabase"/>

```

此配置使用全局 JNDI 资源来定义用户数据库的来源：

```xml
<Resource name="UserDatabase" auth="Container"
  type="org.apache.catalina.UserDatabase"
  description="User database that can be updated and saved"
  factory="org.apache.catalina.users.MemoryUserDatabaseFactory"
  pathname="conf/tomcat-users.xml" />
```

该资源将实例化一个 org.apache.catalina.UserDatabase 类型的对象，并使用工厂类org.apache.catalina.users.MemoryUserDatabaseFactory从 tomcat-users.xml 文件填充它 。

最后，我们在这里看到如何添加具有文章示例所需的管理员角色的用户。我们需要修改tomcat-users.xml文件：

```xml
<tomcat-users xmlns="http://tomcat.apache.org/xml"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://tomcat.apache.org/xml tomcat-users.xsd"
  version="1.0">
    <role rolename="admin"/>
    <user username="demo" password="demo" roles="admin"/>
</tomcat-users>

```

### 3.2. 网络应用程序配置

配置服务器后，让我们通过每个 servlet 的 WEB-INF 文件夹内的 web.xml 配置文件配置 servlet。

所有需要 SSO 的 Web 应用程序都必须具有受保护的资源并使用其中一种 Tomcat 身份验证方法。正如 Servlet API 规范 2.3 中所定义的，Web 应用程序的身份验证机制是在web-app元素内的 login-config 元素中定义的。该元素将包含需要使用以下值之一的 auth-method 表单：BASIC、DIGEST、FORM 或 CLIENT-CERT。每种身份验证方法都有不同的配置，但我们将在[Tomcat Web 应用程序配置](https://www.baeldung.com/apache-tomcat-sso#Tomcat-Web-Apps-Configurations)部分中仅讨论 DIGEST 和 FORM 身份验证方法。

要完成 Web 应用程序配置，我们需要设置保护区。在 web-app 元素下的 web.xml 文件中，我们可以根据需要添加任意数量的安全约束元素。每个安全约束都定义了受保护资源的 URL 模式，并将设置允许的角色。此外，我们需要为所有角色定义 security-role 元素，并且它们必须与 tomcat-users.xml 文件中的定义相匹配。我们将在下一节中看到一个示例。

## 4. 认证机制示例

现在我们知道如何配置 Web 应用程序，让我们看两个示例：Ping 和 Pong。我们选择了不同的身份验证机制来展示 SSO 在不同的机制下都能很好地工作。

### 4.1. Ping认证机制

在 ping web 应用程序中，我们使用 FORM 身份验证方法。FORM认证方式需要登录表单，登录失败的网页。例如，当我们想要自定义登录页面使其看起来像 Web 应用程序时，此方法将很有用，配置如下所示：

```xml
<login-config>
    <auth-method>FORM</auth-method>
    <form-login-config>
        <form-login-page>/logging.html</form-login-page>
        <form-error-page>/logging_error.html</form-error-page>       
    </form-login-config>
</login-config>
```

登录页面必须遵循 servlet 规范 2.3 的登录表单注解中定义的一些严格规则，因为我们既不能选择表单名称，也不能选择输入字段。它们必须是j_security_check、 j_username和j_password。这是为了实现登录表单与各种资源一起工作，并且无需在服务器中配置出站表单的操作字段。在这里我们可以看到它必须是什么样子的示例：

```xml
<!DOCTYPE html>
<html>
<head>
    <title>Ping - Login</title>
</head>
<body>
    <form method="post" action="j_security_check">
        <table >
            <tr>
                <td>User name: </td>
                <td><input type="text" name="j_username" size="20"/></td>
            </tr>
            <tr>
                <td>Password: </td>
                <td><input type="password" name="j_password" size="20"/></td>
            </tr>
        </table>
        <p></p>
        <input type="submit" value="Submit"/>
         
        <input type="reset" value="Reset"/>
    </form>
</body>
</html>
```

要了解当服务器收到来自经过 FORM 身份验证的 Web 应用程序的受保护资源的请求时会发生什么，让我们总结一下此身份验证机制的流程。

首先，客户端请求受保护的资源。如果服务器不包含有效的 SSO 会话 ID，服务器会将客户端重定向到日志表单。用户填写表单并将其凭据发送到服务器后，身份验证机制将启动。

用户身份验证成功后，服务器将检查用户的角色，如果安全约束至少允许其中一项，则服务器会将客户端重定向到请求的 URL。在另一种情况下，服务器会将客户端重定向到错误页面。

### 4.2. 乒乓认证机制

在 Pong Web 应用程序中，我们使用 DIGEST 身份验证机制，配置如下所示：

```xml
<login-config>
    <auth-method>DIGEST</auth-method>
</login-config>
```

DIGEST 认证机制流程类似于 BASIC 认证：当客户端请求受保护的资源时，服务器返回一个对话框来请求用户凭据。如果认证成功，则服务器返回请求的资源，但在另一种情况下，服务器再次发送认证对话框。

尽管 DIGEST 和 BASIC 身份验证方法相似，但有一个重要区别：密码保留在服务器中。

### 4.3. Web 应用程序安全约束配置

在这一点上，我们不打算区分 Ping 和 Pong。尽管它们具有不同值的元素，但配置的重要部分在两个应用程序中将保持不变：

```xml
<security-constraint>
    <display-name>Ping Login Auth</display-name>
    <web-resource-collection>
        <web-resource-name>PingRestrictedAccess</web-resource-name>
        <url-pattern>/private/</url-pattern>
    </web-resource-collection>
    <auth-constraint>
        <role-name>admin</role-name>
    </auth-constraint>
    <user-data-constraint>
        <transport-guarantee>NONE</transport-guarantee>
    </user-data-constraint>
</security-constraint>
```

安全约束定义了私有文件夹下的所有内容都是受保护的资源，还定义了具有管理员角色才能访问资源的需要。

## 5.运行示例

现在我们需要安装一[台Tomcat 10](https://tomcat.apache.org/download-10.cgi)服务器，按照文章前面的方式调整配置，将Ping和Pong web apps放在Tomcat的web app文件夹下。

一旦服务器启动并运行，并且两个应用程序都已部署，请请求资源 http://localhost:8080/ping/private。服务器将显示登录身份验证，因为我们没有登录：

[![ping 应用登录请求](https://www.baeldung.com/wp-content/uploads/2022/03/ping_app_login_request.png)](https://www.baeldung.com/wp-content/uploads/2022/03/ping_app_login_request.png)

然后我们需要引入在[Tomcat服务器配置](https://www.baeldung.com/apache-tomcat-sso#Tomcat-Server-Configurations)部分配置的凭据并提交表单。如果服务器验证了凭据，那么我们将看到一个网页，其中包含指向 pong 的私人部分的链接：

[![ping 应用私有页面](https://www.baeldung.com/wp-content/uploads/2022/03/ping_app_private_page.png)](https://www.baeldung.com/wp-content/uploads/2022/03/ping_app_private_page.png)

如果服务器未验证访问，我们将看到登录错误页面。

[![ping 应用程序登录错误](https://www.baeldung.com/wp-content/uploads/2022/03/ping_app_login_error.png)](https://www.baeldung.com/wp-content/uploads/2022/03/ping_app_login_error.png)

成功登录到 Ping 应用程序后，我们可以看到 SSO 机制在运行，单击指向 pong 私有部分的链接。如果会话已经处于活动状态，服务器将发送 Pong 的受保护资源，而无需我们再次登录。

[![pong 应用私人页面](https://www.baeldung.com/wp-content/uploads/2022/03/pong_app_private_page.png)](https://www.baeldung.com/wp-content/uploads/2022/03/pong_app_private_page.png)

最后，我们可以检查会话过期后，服务器是否会再次显示登录页面。我们可以通过等待几分钟并单击指向 ping 的私人部分的链接来做到这一点。

## 6.其他SSO解决方案

在本文中，我们介绍了由 Tomcat 服务器实现的 Web-SSO。如果我们想探索其他 SSO 选项，这里有一些流行的选项：

-   [Spring Security 和 OpenID Connect](https://www.baeldung.com/spring-security-openid-connect)
-   [带有KeyCloak的 Spring Security OAuth](https://www.baeldung.com/sso-spring-security-oauth2)
-   [带有 Spring Security 的 SAML](https://www.baeldung.com/spring-security-saml)
-   [Apereo 中央认证服务](https://www.baeldung.com/spring-security-cas-sso)

## 七. 总结

在本教程中，我们学习了 Tomcat 体系结构的基础知识。稍后，我们回顾了如何配置服务器。最后，我们审查了必须包含在 SSO 下的 servlet 或 Web 应用程序的配置。