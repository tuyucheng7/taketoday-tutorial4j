## 1. 概述

在开发任何应用程序时，选择正确的技术都起着重要作用。然而，决定并不总是直截了当的。

在本文中，我们将对三种流行的 Java 技术进行比较。在进行比较之前，我们将首先探讨每项技术的用途及其生命周期。然后，我们看看它们的突出特点是什么，并在几个特点的基础上进行比较。

## 2.JSF

Jakarta Server Faces，以前称为[JavaServer Faces](https://www.baeldung.com/spring-jsf)，是一个 Web 框架，用于为 Java 应用程序构建基于组件的用户界面。与许多其他人一样，它也遵循[MVC 方法](https://www.baeldung.com/mvc-servlet-jsp)。MVC 的“视图”借助可重用的 UI 组件简化了用户界面的创建。

JSF 具有广泛的标准 UI 组件，还提供了通过外部 API 定义新组件的灵活性。

任何应用程序的生命周期都是指从启动到结束的各个阶段。同样，JSF 应用程序的生命周期从客户端发出 HTTP 请求开始，到服务器响应响应结束。JSF 生命周期是一个请求-响应生命周期，它处理两种请求：初始请求和回发。

JSF 应用程序的生命周期包括两个主要阶段：执行和呈现。

执行阶段又分为六个阶段：

-   恢复视图：当 JSF 收到请求时启动
-   应用请求值：在回发请求期间恢复组件树
-   Process Validations：处理组件树上注册的所有验证器
-   Update Model Values：遍历组件树，设置对应的服务端对象属性
-   调用应用程序：处理应用程序级事件，例如提交表单
-   呈现响应：构建视图并呈现页面

在渲染阶段，系统将请求的资源渲染为对客户端浏览器的响应。

JSF 2.0 是一个主要版本，其中包括 Facelets、复合组件、AJAX 和资源库。

在 Facelets 之前，JSP 是 JSF 应用程序的默认模板引擎。在 JSF 2.x 的旧版本中，引入了许多新功能，使框架更加健壮和高效。这些功能包括对注释、HTML5、Restful 和无状态 JSF 等的支持。

## 3.服务小程序

Jakarta Servlets，以前称为[Java Servlets](https://www.baeldung.com/intro-to-servlets)，扩展了服务器的能力。通常，servlet 使用容器实现的请求/响应机制与 Web 客户端交互。

[servlet 容器](https://www.baeldung.com/java-servlets-containers-intro)是 Web 服务器的重要组成部分。它管理 servlet 并根据用户请求创建动态内容。每当 Web 服务器收到请求时，它会将请求定向到[已注册的 servlet](https://www.baeldung.com/register-servlet)。

生命周期仅包含三个阶段。首先，调用init()方法来启动 servlet 。然后，容器将传入请求发送到 执行所有任务的service()方法。最后，destroy()方法清理一些东西并拆除 servlet。

Servlet 具有许多重要的特性，包括对 Java 及其库的本地支持、用于 Web 服务器的标准 API 以及 HTTP/2 的强大功能。此外，它们允许异步请求并为每个请求创建单独的线程。

## 4. 网页

Jakarta Server Pages，以前称为[JavaServer Pages](https://www.baeldung.com/jsp)，使我们能够将动态内容注入静态页面。JSP 是 servlet 的高级抽象，因为它们在执行开始之前被转换为 servlet。

变量声明和打印值、循环、条件格式化和异常处理等常见任务都是通过[JSTL 库](https://www.baeldung.com/jstl)执行的。

JSP 的生命周期类似于 servlet，只是多了一个步骤——编译步骤。当浏览器请求页面时，JSP 引擎首先检查是否需要编译该页面。编译步骤包括三个阶段。

最初，引擎解析页面。然后，它将页面转换为 servlet。最后，生成的 servlet 编译成 Java 类。

JSP 具有许多值得注意的特性，例如跟踪会话、良好的表单控件以及向服务器发送数据/从服务器接收数据。因为 JSP 构建在 servlet 之上，所以它可以访问所有重要的 Java API，例如 JDBC、JNDI 和 EJB。

## 5. 主要区别

Servlet 技术是 J2EE 中 Web 应用程序开发的基础。但是，它没有视图技术，开发人员必须将标记与 Java 代码混合在一起。此外，它缺少用于构建标记、验证请求和启用安全功能等常见任务的实用程序。

JSP 填补了 servlet 的标记空白。在 JSTL 和 EL 的帮助下，我们可以定义任何自定义 HTML 标记来构建良好的 UI。不幸的是，JSP 编译速度慢，调试困难，将基本的表单验证和类型转换留给开发人员，并且缺乏对安全性的支持。

JSF 是一个适当的框架，它将数据源与可重用的 UI 组件连接起来，提供对多个库的支持，并减少构建和管理应用程序的工作量。由于基于组件，JSF 始终比 JSP 具有更好的安全优势。尽管有很多好处，但 JSF 很复杂并且学习曲线很陡。

根据 MVC 设计模式，servlet 充当控制器，JSP 充当视图，而 JSF 是一个完整的 MVC。

正如我们已经知道的，servlet 将需要 Java 代码中的手动 HTML 标记。出于同样的目的，JSP 使用 HTML 而 JSF 使用 Facelets。此外，两者也都提供对自定义标签的支持。

在 servlet 和 JSP 中没有默认的错误处理支持。相反，JSF 提供了一堆预定义的验证器。

在通过 Web 传输数据的应用程序中，安全性一直是一个问题。仅支持基于角色和基于表单的身份验证的 JSP 在这方面是缺乏的。

说到协议，JSP 只接受 HTTP，而 servlet 和 JSF 支持多种协议，包括 HTTP/HTTPS、SMTP 和 SIP。所有这些技术都提倡多线程，并且需要一个 Web 容器来运行。

## 六，总结

在本教程中，我们比较了 Java 世界中的三种流行技术：JSF、Servlet 和 JSP。首先，我们看到了每种技术代表什么以及它的生命周期是如何进行的。然后，我们讨论了每种技术的主要特点和局限性。最后，我们根据几个特点对它们进行了比较。

应该选择什么技术而不是其他技术完全取决于上下文。应用程序的性质应该是决定因素。