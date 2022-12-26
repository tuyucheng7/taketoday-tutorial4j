## 1. 概述

在本教程中，我们将介绍[Spring Security Taglibs](https://docs.spring.io/spring-security/site/docs/5.0.7.RELEASE/reference/htmlsingle/#taglibs)，它为访问安全信息和在JSP中应用安全约束提供基本支持。

## 2. Maven依赖

首先，让我们将[spring-security-taglibs](https://search.maven.org/search?q=a:spring-security-taglibs)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-taglibs</artifactId>
    <version>5.2.2.RELEASE</version>
</dependency>
```

## 3. 声明标签库

现在，在我们可以使用这些标签之前，我们需要在JSP文件的顶部导入标签库：

```html
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
```

添加此内容后，我们将能够使用sec前缀指定Spring Security的标签。

## 4. authorize标签

### 4.1 访问表达式

在我们的应用程序中，我们可能具有仅应针对某些角色或用户显示的信息。

在这种情况下，我们可以使用authorize标签：

```xml
<sec:authorize access="!isAuthenticated()">
  Login
</sec:authorize>
<sec:authorize access="isAuthenticated()">
  Logout
</sec:authorize>
```

此外，我们可以检查经过身份验证的用户是否具有特定角色：

```xml
<sec:authorize access="hasRole('ADMIN')">
    Manage Users
</sec:authorize>
```

我们可以使用任何[Spring Security表达式](https://www.baeldung.com/spring-security-expressions)作为access的值：

-   hasAnyRole('ADMIN','USER')：如果当前用户具有任何列出的角色，则返回true
-   isAnonymous()：如果当前主体是匿名用户，则isAnonymous()返回true
-   isRememberMe()：如果当前主体是Remember-me用户，则isRememberMe()返回true
-   isFullyAuthenticated()：如果用户已通过身份验证并且既不是匿名用户也不是Remember-me的用户，则isFullyAuthenticated()返回true

### 4.2 url

除此之外，我们可以检查有权向特定URL发送请求的用户：

```xml
<sec:authorize url="/userManagement">
    <a href="/userManagement">Manage Users</a>
</sec:authorize>
```

### 4.3 调试

在某些情况下，我们可能希望对UI进行更多控制，例如在测试场景中。我们可以在我们的application.properties文件中设置spring.security.disableUISecurity=true，而不是让Spring Security跳过渲染这些未经授权的部分。

**当我们这样做时，authorize标签不会隐藏其内容**。相反，它将用<span class="securityHiddenUI"\>...</span\>标签包装内容。然后，我们可以使用一些CSS自定义渲染。

**请记住，通过CSS隐藏内容并不安全**！用户只需查看源代码即可查看未经授权的内容。

## 5. authentication标签

在其他时候，我们希望显示有关登录用户的详细信息，比如说在网站上显示“Welcome Back, Tuyucheng!”。

为此，我们使用authentication标签：

```html
<sec:authorize access="isAuthenticated()">
    Welcome Back, <sec:authentication property="name"/>
</sec:authorize>
```

## 6. csrfInput标签

希望我们的应用程序中启用了Spring Security的CSRF防御！

如果我们这样做，那么Spring Security已经为我们在\<form:form\>标签中插入了一个CSRF隐藏表单输入。

但是如果我们想使用<form\>代替，**我们可以使用csrfInput手动指示Spring Security应该将这个隐藏的输入字段放在哪里**：

```html
<form method="post" action="/do/something">
    <sec:csrfInput />
    Text Field:<br />
	<input type="text" name="textField" />
</form>
```

如果未启用CSRF保护，则此标记不输出任何内容。

## 7. csrfMetaTags标签

或者，**如果我们想要在Javascript中访问CSRF令牌**，我们可能希望将令牌作为元标记插入。

我们可以使用csrfMetaTags标签来做到这一点：

```html
<html>
<head>
	<title>JavaScript with CSRF Protection</title>
	<sec:csrfMetaTags/>
	<script type="text/javascript" language="javascript">
		var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		var csrfToken = $("meta[name='_csrf']").attr("content");
	</script>
</head>
<body>
...
</body>
</html>
```

同样，如果未启用CSRF保护，则此标签将不会输出任何内容。

## 8. 总结

在这篇快速文章中，我们重点介绍了一些常见的Spring Security标签库用例。而且，正如我们了解到的，它们对于呈现身份验证和授权感知JSP内容非常有用。