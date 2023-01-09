## 1. 概述

在本快速教程中，我们将了解调用HttpServletRequest# getSession()和HttpServletRequest# getSession(boolean)之间的区别。

## 2.有什么区别？

方法 getSession()和getSession(boolean)非常相似。不过，还是有一点不同。不同之处在于，如果会话不存在，是否应该创建它。

调用 getSession() 和 getSession(true) 在功能上是相同的：检索当前会话，如果尚不存在，则创建它。

不过，调用getSession(false)会检索当前会话，如果尚不存在，则返回null。除此之外，当我们想询问会话是否存在时，这很方便。

## 3.例子

在这个例子中，我们正在考虑这样的场景：

-   用户输入用户 ID 并登录到应用程序
-   然后用户输入用户名 和年龄，并希望为登录用户更新这些详细信息

我们将在会话中存储用户值，以了解HttpServletRequest#getSession()和HttpServletRequest#getSession(boolean)的用法。

首先，让我们创建一个 servlet，我们在其doGet()方法中使用HttpServletRequest#getSession()：

```java
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    session.setAttribute("userId", request.getParameter("userId"));
}

```

此时，servlet 将检索现有会话或为登录用户创建一个新会话(如果不存在)。

接下来，我们将在会话中设置userName属性。

由于我们要为各自的用户 ID 更新用户的详细信息，我们需要相同的会话，而不想创建一个新的会话来存储用户名。

所以现在，我们将使用HttpServletRequest#getSession(boolean)和false值：

```java
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session != null) {
        session.setAttribute("userName", request.getParameter("userName"));
    }
}
```

这将导致在先前设置userId的同一会话中设置userName属性。

## 4. 总结

在本教程中，我们解释了HttpServletRequest#getSession()和HttpServletRequest#getSession(boolean)方法之间的区别。