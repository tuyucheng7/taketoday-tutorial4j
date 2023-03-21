## 1. 概述

在本文中，我们将说明如何使用Spring MVC为在后端使用Spring Security处理身份验证的应用程序实现一个简单的登录页面。

有关如何使用Spring Security处理登录的完整详细信息，请参阅[本文](https://www.baeldung.com/spring-security-login)深入了解其配置和实现的文章。

## 2. 登录页面

让我们从定义一个**非常简单的登录页面**开始：

```html
<html>
<head></head>
<body>
<h1>Login</h1>
<form name='f' action="login" method='POST'>
    <table>
        <tr>
            <td>User:</td>
            <td><input type='text' name='username' value=''></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type='password' name='password' /></td>
        </tr>
        <tr>
            <td><input name="submit" type="submit" value="submit" /></td>
        </tr>
    </table>
</form>
</body>
</html>
```

现在，让我们包括一个客户端检查，以确保在我们提交表单之前已经输入了用户名和密码。在这个例子中，我们将使用纯Javascript，但JQuery也是一个很好的选择：

```javascript
<script type="text/javascript">
function validate() {
    if (document.f.username.value == "" && document.f.password.value == "") {
        alert("Username and password are required");
        document.f.username.focus();
        return false;
    }
    if (document.f.username.value == "") {
        alert("Username is required");
        document.f.username.focus();
        return false;
    }
    if (document.f.password.value == "") {
	alert("Password is required");
	document.f.password.focus();
        return false;
    }
}
</script>
```

如你所见，我们只是检查username或password字段是否为空；如果是-将弹出一个带有相应消息的JavaScript消息框。

## 3. 消息国际化

接下来-让我们国际化我们在前端使用的消息。此类消息有多种类型，每种消息都以不同的方式进行国际化：

1.  在表单由Spring的控制器或处理程序处理之前生成的消息。这些消息可以在JSP页面中引用，并使用**Jsp/Jslt localization**进行国际化(请参阅第4.3节)
2.  一旦页面提交给Spring处理(提交登录表单后)消息就被国际化；这些消息使用**Spring MVC localization**进行国际化(请参阅第4.2节)

### 3.1 message.properties文件

无论哪种情况，我们都需要为我们想要支持的每种语言创建一个message.properties文件；文件的名称应遵循以下约定：messages_\[localeCode\].properties。

例如，如果我们想要支持英语和西班牙语错误消息，我们将拥有文件：**messages_en.properties和messages_es_ES.properties**。请注意，对于英语-messages.properties也是有效的。

我们将把这两个文件放在项目的类路径(src/main/resources)中。这些文件仅包含我们需要以不同语言显示的错误代码和消息-例如：

```properties
message.username=Username required
message.password=Password required
message.unauth=Unauthorized access!!
message.badCredentials=Invalid username or password
message.sessionExpired=Session timed out
message.logoutError=Sorry, error login out
message.logoutSucc=You logged out successfully
```

### 3.2 配置Spring MVC国际化

Spring MVC提供了一个LocaleResolver，它与其LocaleChangeInterceptor API结合使用，可以根据语言环境设置以不同的语言显示消息。要配置国际化-我们需要在MVC配置中定义以下bean：

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setParamName("lang");
    registry.addInterceptor(localeChangeInterceptor);
}

@Bean
public LocaleResolver localeResolver() {
    CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
    return cookieLocaleResolver;
}
```

默认情况下，LocaleResolver将从HTTP标头中获取locale代码。要强制使用默认语言环境，我们需要在localeResolver()上进行设置：

```java
@Bean
public LocaleResolver localeResolver() {
    CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
    cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
    return cookieLocaleResolver;
}
```

这个语言环境解析器是一个CookieLocaleResolver，这意味着它将语言环境信息存储在客户端cookie中；因此-它会在用户每次登录时以及整个访问期间记住用户的区域设置。

或者，有一个SessionLocaleResolver，它会在整个会话期间记住语言环境。要改为使用此LocaleResolver，我们需要将上述方法替换为以下内容：

```java
@Bean
public LocaleResolver localeResolver() {
    SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
    return sessionLocaleResolver;
}
```

最后，请注意LocaleChangeInterceptor将根据通过简单链接与登录页面一起发送的lang参数值更改语言环境：

```html
<a href="?lang=en">English</a> |
<a href="?lang=es_ES">Spanish</a>
```

### 3.3 JSP/JSLT国际化

JSP/JSLT API将用于显示在jsp页面本身中捕获的国际化消息。要使用jsp国际化库，我们应该将以下依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>javax.servlet.jsp</groupId>
    <artifactId>javax.servlet.jsp-api</artifactId>
    <version>2.3.2-b01</version>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
    <version>1.2</version>
</dependency>
```

## 4. 显示错误信息

### 4.1 登录验证错误

为了使用JSP/JSTL支持并在login.jsp中显示国际化消息，让我们在页面中实现以下更改：

1. 将以下标签lib元素添加到login.jsp中：

```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
```

2. 添加将指向messages.properties文件的jsp/jslt元素：

```jsp
<fmt:setBundle basename="messages" />
```

3. 添加以下\<fmt:...\>元素以将消息存储在jsp变量上：

```jsp
<fmt:message key="message.password" var="noPass" />
<fmt:message key="message.username" var="noUser" />
```

4. 修改我们在第3节中看到的登录验证脚本，以便国际化错误消息：

```javascript
<script type="text/javascript">
function validate() {
    if (document.f.username.value == "" && document.f.password.value == "") {
        alert("${noUser} and ${noPass}");
	    document.f.username.focus();
	    return false;
    }
    if (document.f.username.value == "") {
	    alert("${noUser}");
	    document.f.username.focus();
	    return false;
     }
    if (document.f.password.value == "") {
	    alert("${noPass}");
	    document.f.password.focus();
	    return false;
    }
}
</script>
```

### 4.2 登录前错误

有时，如果之前的操作失败，登录页面将传递一个错误参数。例如，注册表单提交按钮将加载登录页面。如果注册成功，那么最好在登录表单中显示成功消息，如果相反则显示错误消息。

在下面的示例登录表单中，我们通过拦截regSucc和regError参数并根据它们的值显示国际化消息来实现这一点。

```html
<c:if test="${param.regSucc == true}">
    <div id="status">
        <spring:message code="message.regSucc">
        </spring:message>
    </div>
</c:if>
<c:if test="${param.regError == true}">
    <div id="error">
        <spring:message code="message.regError">
        </spring:message>
    </div>
</c:if>
```

### 4.3 登录安全错误

如果登录过程由于某种原因失败，Spring Security将重定向到登录错误URL，我们将其定义为/login.html?error=true。

因此-类似于我们在页面中显示注册状态的方式，我们需要在出现登录问题时执行相同的操作：

```html
<c:if test="${param.error != null}">
    <div id="error">
        <spring:message code="message.badCredentials">
        </spring:message>
    </div>
</c:if>
```

请注意，我们使用的是\<spring:message...\>元素。这意味着错误消息是在Spring MVC处理期间生成的。

完整的登录页面-包括js验证和这些额外的状态消息可以在[github项目](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-security-modules/spring-security-login-and-registration)中找到。

### 4.4 注销错误

在下面的示例中，logout.html页面中的jsp代码\<c:iftest="${notemptySPRING_SECURITY_LAST_EXCEPTION}"\>将检查注销过程中是否存在错误。

例如-如果在自定义注销处理程序尝试在重定向到注销页面之前存储用户数据时出现持久层异常。虽然这些错误很少见，但我们也应该尽可能巧妙地处理它们。

让我们看一下完整的logout.jsp：

```html
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
uri="http://www.springframework.org/security/tags"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
    <div id="error">
        <spring:message code="message.logoutError">
        </spring:message>
    </div>
</c:if>
<c:if test="${param.logSucc == true}">
    <div id="success">
        <spring:message code="message.logoutSucc">
        </spring:message>
    </div>
</c:if>
<html>
<head>
    <title>Logged Out</title>
</head>
<body>
<a href="login.html">Login</a>
</body>
</html>
```

请注意，注销页面还会读取查询字符串参数logSucc，如果其值等于true，则会显示国际化的成功消息。

## 5. Spring Security配置

本文的重点是登录过程的前端，而不是后端-所以我们只简要地看一下安全配置的要点；对于完整的配置，你应该阅读[之前的文章](https://www.baeldung.com/spring-security-login)。

### 5.1 重定向到登录错误URL

<form-login.../>元素中的以下指令将应用程序流定向到将处理登录错误的url：

```html
authentication-failure-url="/login.html?error=true"
```

### 5.2 注销成功重定向

```xml
<logout invalidate-session="false"
        logout-success-url="/logout.html?logSucc=true"
        delete-cookies="JSESSIONID"/>
```

logout-success-url属性只是重定向到注销页面，其中包含一个确认注销成功的参数。

## 6. 总结

在本文中，我们说明了如何为Spring Security支持的应用程序实现登录页面-处理登录验证、显示身份验证错误和消息国际化。