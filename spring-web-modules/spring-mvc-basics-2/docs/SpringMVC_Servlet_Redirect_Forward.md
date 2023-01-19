## 1. 概述

有时候，Java Servlet中的初始HTTP请求处理程序需要将请求委托给另一个资源。
在这些情况下，我们可以进一步转发请求，也可以将其重定向到其他资源。

在本文中我们介绍这两种机制，并讨论它们的差异和最佳实践。

## 2. Maven依赖

```xml

<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
</dependency>
```

## 3. Forward

让我们看看如何做一个简单的Forward：

```java
/*@formatter:off*/
protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/forwarded");
    dispatcher.forward(req, resp);
}
/*@formatter:on*/
```

**我们从父Servlet获取RequestDispatcher引用，并将其指向另一个服务器资源**。

简单地说，这会转发请求，当客户端向“http://localhost:8080/hello?name=Dennis”发送请求，此逻辑将运行，请求将转发到“/forwarded”。

## 4. Redirect

下面是使用重定向的代码片段：

```java
/*@formatter:off*/
protected void doGet(HttpServletRequest req, HttpServletResponse resp){
    resp.sendRedirect(req.getContextPath() + "/redirected");
}
/*@formatter:on*/
```

**我们使用原始Response对象将此请求重定向到另一个URL：“/redirected”**。

当客户向“http://localhost:8080/welcome?name=Dennis”发送请求时，请求将被重定向到“http://localhost:8080/redirected”。

## 5. 差异

在这两种情况下，我们都传递了带有值的参数“name”。
简单地说，转发的请求仍然具有这个值，但重定向的请求没有。

这是因为使用通过重定向时，Request对象与原始对象不同，如果我们仍然想使用这个参数，则需要将其保存在HttpSession对象中。

下面列出了Servlet转发和重定向之间的主要区别：

**Forward**:

+ 请求将在服务器端进一步处理
+ 客户端不受转发的影响，浏览器中的URL保持不变
+ Request和Response对象在转发后将保持相同的对象，请求范围内对象仍然可用

**Redirect**:

+ 请求被重定向到其他资源
+ 客户端将在重定向后看到URL的改变
+ 创建一个新请求
+ 重定向通常在Post/Redirect/Get web开发模式中使用

## 6. 总结

转发和重定向都是关于将用户请求发送到不同的资源，尽管它们具有完全不同的语义。

如何选择它们进行使用很简单：如果需要上一个Request作用域，或者不需要通知用户，但应用程序也希望执行内部操作，则使用转发。

要放弃Request作用域，或者如果新内容与原始请求没有关联(例如重定向到登录页面或完成表单提交)，则使用重定向。