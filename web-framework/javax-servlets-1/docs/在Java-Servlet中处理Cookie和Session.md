## 1. 概述

在本教程中，我们将介绍使用 Servlet 在Java中处理 cookie 和会话。

此外，我们将简要介绍什么是 cookie，并探索它的一些示例用例。

## 2. 饼干基础

简单地说，cookie 是存储在客户端的一小段数据，供服务器在与客户端通信时使用。

它们用于在发送后续请求时识别客户端。它们还可以用于将一些数据从一个 servlet 传递到另一个 servlet。

更多详情，请参考[这篇文章](https://pl.wikipedia.org/wiki/HTTP_cookie)。

### 2.1. 创建一个 Cookie

Cookie类在javax.servlet.http包中定义。

要将其发送给客户端，我们需要创建一个并将其添加到响应中：

```java
Cookie uiColorCookie = new Cookie("color", "red");
response.addCookie(uiColorCookie);

```

然而，它的 API 更广泛——让我们来探索一下。

### 2.2. 设置 Cookie 过期日期

我们可以设置最大年龄(使用方法maxAge(int))，它定义给定 cookie 应该有效的秒数：

```java
uiColorCookie.setMaxAge(6060);

```

我们将最大年龄设置为一小时。在此时间之后，客户端(浏览器)在发送请求时将无法使用该 cookie，并且还应将其从浏览器缓存中删除。

### 2.3. 设置 Cookie 域

Cookie API 中另一个有用的方法是setDomain(String)。

这使我们能够指定客户端应将其传送到的域名。它还取决于我们是否明确指定域名。

让我们为 cookie 设置域：

```java
uiColorCookie.setDomain("example.com");
```

cookie 将传送到example.com及其子域发出的每个请求。

如果我们没有明确指定域，它将被设置为创建 cookie 的域名。

例如，如果我们从example.com创建一个 cookie并将域名留空，那么它将被传送到www.example.com(没有子域)。

除了域名，我们还可以指定路径。接下来让我们看一下。

### 2.4. 设置 Cookie 路径

路径指定 cookie 将被传送到哪里。

如果我们明确指定一个路径，那么Cookie将被传送到给定的 URL 及其所有子目录：

```java
uiColorCookie.setPath("/welcomeUser");
```

隐含地，它将被设置为创建 cookie 及其所有子目录的 URL。

现在让我们关注如何在Servlet中检索它们的值。

### 2.5. 读取 Servlet 中的 Cookie

Cookie 由客户端添加到请求中。客户端检查其参数并决定是否可以将其传递到当前 URL。

我们可以通过对传递给Servlet 的请求 ( HttpServletRequest ) 调用getCookies()来获取所有 cookie 。

我们可以遍历这个数组并搜索我们需要的那个，例如，通过比较它们的名字：

```java
public Optional<String> readCookie(String key) {
    return Arrays.stream(request.getCookies())
      .filter(c -> key.equals(c.getName()))
      .map(Cookie::getValue)
      .findAny();
}
```

### 2.6. 删除 Cookie

要从 浏览器中删除 cookie，我们必须向响应中添加一个同名的新 cookie，但maxAge值设置为 0：

```java
Cookie userNameCookieRemove = new Cookie("userName", "");
userNameCookieRemove.setMaxAge(0);
response.addCookie(userNameCookieRemove);
```

删除 cookie 的示例用例是用户注销操作——我们可能需要删除为活动用户会话存储的一些数据。

现在我们知道如何在Servlet中处理 cookie 。

接下来，我们将介绍另一个我们经常从Servlet访问的重要对象——Session对象。

## 3.HttpSession对象_

HttpSession是跨不同请求存储用户相关数据的另一种选择。会话是保存上下文数据的服务器端存储。

不同会话对象之间不共享数据(客户端只能从其会话访问数据)。它还包含键值对，但与 cookie 相比，会话可以包含对象作为值。存储实现机制是依赖于服务器的。

会话通过 cookie 或请求参数与客户端匹配。[可以在此处](https://en.wikipedia.org/wiki/Session_(computer_science))找到更多信息。

### 3.1. 获取会话

我们可以直接从请求中获取HttpSession ：

```java
HttpSession session = request.getSession();

```

如果会话不存在，上面的代码将创建一个新会话。我们可以通过调用来实现相同的目的：

```java
request.getSession(true)
```

如果我们只想获取现有会话而不创建新会话，我们需要使用：

```java
request.getSession(false)

```

如果我们第一次访问 JSP 页面，则会默认创建一个新会话。我们可以通过将会话属性设置为false来禁用此行为：

```java
<%@ page contentType="text/html;charset=UTF-8" session="false" %>
```

在大多数情况下，Web 服务器使用 cookie 进行会话管理。创建会话对象时，服务器会创建一个带有JSESSIONID键和值的 cookie，用于标识会话。

### 3.2. 会话属性

会话对象提供了一组方法来访问(创建、读取、修改、删除)为给定用户会话创建的属性：

-   setAttribute(String, Object)用键和新值创建或替换会话属性
-   getAttribute(String)读取具有给定名称(键)的属性值
-   removeAttribute(String)删除具有给定名称的属性

我们还可以通过调用getAttributeNames()轻松检查已经存在的会话属性。

正如我们已经提到的，我们可以从请求中检索会话对象。当我们已经有了它时，我们可以快速执行上面提到的方法。

我们可以创建一个属性：

```java
HttpSession session = request.getSession();
session.setAttribute("attributeKey", "Sample Value");

```

属性值可以通过其键(名称)获取：

```java
session.getAttribute("attributeKey");

```

当我们不再需要某个属性时，我们可以删除它：

```java
session.removeAttribute("attributeKey");

```

用户会话的一个众所周知的用例是在用户从我们的网站注销时使它存储的全部数据无效。session对象为其提供了解决方案：

```java
session.invalidate();

```

此方法从 Web 服务器中删除整个会话，因此我们无法再从中访问属性。

HttpSession对象的方法较多，但我们提到的是最常见的方法。

## 4. 总结

在本文中，我们介绍了两种允许我们在对服务器的后续请求之间存储用户数据的机制——cookie 和会话。

请记住，HTTP 协议是无状态的，因此必须跨请求维护状态。