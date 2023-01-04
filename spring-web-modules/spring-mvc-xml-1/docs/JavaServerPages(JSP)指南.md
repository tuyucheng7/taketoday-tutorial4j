## 1. 概述

JavaServer Pages(JSP)允许使用Java和JavaServlet将动态内容注入到静态内容中。我们可以向Java Servlet发出请求，执行相关逻辑，并在服务器端呈现特定视图以供客户端使用。本文将全面概述使用Java 8和Java 7 EE的JavaServer Pages。

我们将从探索与JSP相关的几个关键概念开始：即动态内容和静态内容之间的区别、JSP生命周期、JSP语法以及编译时创建的指令和隐式对象！

## 2. Java服务器页面

JavaServer Pages(JSP)使特定于Java的数据能够传递到.jsp视图或放置在.jsp视图中并在客户端使用。

JSP文件本质上是带有一些额外语法的.html文件，以及一些初始的小差异：

1.  .html后缀替换为.jsp(它被认为是.jsp文件类型)并且
2.  以下标签被添加到.html标签元素的顶部：

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
```

让我们回顾一下JSP中的一些关键概念。

### 2.1 JSP语法

有两种方法可以将Java代码添加到.jsp。首先，我们可以使用基本的JavaScriptlet语法，其中涉及将Java代码块放置在两个Scriptlet标签中：

```html
<% Java code here %>
```

第二种方法是特定于XML的：

```html
<jsp:scriptlet>
    Java code here
</jsp:scriptlet>
```

重要的是，可以通过使用if、then和else子句将条件逻辑客户端与JSP一起使用，然后用这些括号将相关的标签块包装起来。

```html
<% if (doodad) {%>
    <div>Doodad!</div>
<% } else { %>
    <p>Hello!</p>
<% } %>
```

例如，如果doodad为真，我们将显示第一个div元素，否则我们将显示第二个p元素！

### 2.2 静态和动态内容

静态Web内容是独立于RESTful、SOAP、HTTP、HTTPS请求或其他用户提交的信息使用的固定资产。

然而，静态内容是固定的，不会被用户输入修改。动态Web内容是那些响应用户操作或信息、根据用户操作或信息进行修改或更改的资产！

JSP技术允许将动态内容和静态内容之间的责任完全分离。

服务器(servlet)管理动态内容，而客户端(实际的.jsp页面)是注入动态内容的静态上下文。

让我们看一下由JSP创建的隐式对象，它们允许你访问与JSP相关的服务器端数据！

### 2.3 隐式对象

隐式对象由JSP引擎在编译期间自动生成。

隐式对象包括HttpRequest和HttpResponse对象，并公开各种服务器端功能以供在你的Servlet中使用并与你的.jsp进行交互！这是创建的隐式对象的列表：

request：request属于javax.servlet.http.HttpServletRequest类。请求对象公开所有用户输入数据并使其在服务器端可用。

response：response属于javax.servlet.http.HttpServletResponse类，并确定发出请求后传回客户端的内容。

让我们仔细看看请求和响应隐式对象，因为它们是最重要和最常用的对象。

下面的示例演示了一个非常简单、不完整的Servlet方法来处理GET请求。我省略了大部分细节，以便我们可以专注于如何使用请求和响应对象：

```java
protected void doGet(HttpServletRequest request, 
  HttpServletResponse response) throws ServletException, IOException {
    String message = request.getParameter("message");
    response.setContentType("text/html");
    // ...
}
```

首先，我们看到请求和响应对象作为参数传入方法，使它们在其范围内可用。

我们可以使用.getParameter()函数访问请求参数。上面，我们获取消息参数并初始化一个字符串变量，以便我们可以在服务器端逻辑中使用它。我们还可以访问响应对象，它决定了传递到视图中的数据是什么以及如何传递。

上面我们在上面设置了内容类型。我们不需要返回响应对象就可以在渲染时将其有效负载显示在JSP页面上！

out：out属于javax.servlet.jsp.JspWriter类，用于向客户端写入内容。

至少有两种打印到JSP页面的方法，值得在这里讨论。out是自动创建的，并允许你写入内存，然后写入响应对象：

```java
out.print(“hello”);
out.println(“world”);
```

而已！

第二种方法可以更高效，因为它允许你直接写入响应对象！在这里，我们使用PrintWriter：

```java
PrintWriter out = response.getWriter();
out.println("Hello World");
```

### 2.4 其他隐式对象

这里有一些其他的隐式对象也很值得了解！

session：session属于javax.servlet.http.HttpSession类，在会话期间维护用户数据。

application：application属于类javax.servlet.ServletContext存储在初始化时设置的应用程序范围的参数或需要在应用程序范围内访问的参数。

exception：exception属于javax.servlet.jsp.JspException类，用于在带有<%@ page isErrorPage="true" %>标签的JSP页面上显示错误消息。

page：page属于java.lang.Object类，允许访问或引用当前Servlet信息。

pageContext：pageContext属于类javax.servlet.jsp.PageContext默认为页面范围，但可用于访问请求、应用程序和会话属性。

config：config属于类javax.servlet.ServletConfig是Servlet配置对象，允许获取Servlet上下文、名称和配置参数。

现在我们已经介绍了JSP提供的隐式对象，让我们转向允许.jsp页面(间接)访问其中一些对象的指令。

### 2.5 指令

JSP提供开箱即用的指令，可用于为我们的JSP文件指定核心功能。JSP指令有两个部分：(1)指令本身和(2)分配了一个值的指令的属性。

可以使用指令标签引用的三种指令是<%@ page ... %>定义JSP的依赖项和属性，包括内容类型和语言，<%@ include ... %>指定要使用的导入或文件，和<%@ taglib ...%>指定一个标签库，定义页面使用的自定义操作。

因此，作为示例，将使用JSP标签以下列方式指定页面指令：<%@ page attribute="value" %>

而且，我们可以使用XML来做到这一点，如下所示：<jsp:directive.page attribute="value" />

### 2.6 页面指令属性

可以在页面指令中声明很多属性：

autoFlush <%@ page autoFlush="false" %>


autoFlush控制缓冲区输出，在达到缓冲区大小时将其清除。默认值为true。

缓冲区<%@ page buffer="19kb" %>


buffer设置我们的JSP页面使用的缓冲区的大小。默认值为8kb。

errorPage <%@ page errorPage="errHandler.jsp" %>


errorPage将JSP页面指定为错误页面。

extends <%@ page extends="org.apache.jasper.runtime.HttpJspBase" %>


extends指定相应Servlet代码的超类。

info <%@ page info="这是我的 JSP!" %>


info用于为JSP设置基于文本的描述。

isELIgnored <%@ page isELIgnored="true" %>

isELIgnored表示页面是否将忽略JSP中的表达式语言(EL)。EL使表示层能够与Java托管bean进行通信，并使用${...}语法进行通信，虽然我们不会在这里深入探讨EL的本质，但下面有几个示例足以构建我们的示例JSP应用程序！isELIgnored的默认值为false。

isErrorPage <%@ page isErrorPage="true" %>

isErrorPage表示页面是否为错误页面。如果我们在应用程序中为页面创建错误处理程序，则必须指定错误页面。

isThreadSafe <%@ page isThreadSafe="false" %>

isThreadSafe的默认值为true。isThreadSafe确定JSP是否可以使用Servlet多线程。通常，你永远不想关闭该功能。

语言<%@ page language="java" %>

language确定在JSP中使用的脚本语言。默认值为Java。

session <%@ page session="value"%>

session决定是否保持HTTP会话。它默认为true并接受true或false的值。

trimDirectiveWhitespaces <%@ page trimDirectiveWhitespaces ="true" %>

trimDirectiveWhitespaces去除JSP页面中的空白，在编译时将代码压缩到更紧凑的块中。将此值设置为true可能有助于减小JSP代码的大小。默认值为false。

## 3. 三个例子

现在我们已经回顾了JSP的核心概念，让我们将这些概念应用到一些基本示例中，这些示例将帮助你启动并运行你的第一个JSP服务Servlet！

将Java注入.jsp有三种主要方法，我们将在下面使用Java 8和Jakarta EE中的原生功能探索其中的每一种方法。

首先，我们将在服务器端呈现我们的标签以在客户端显示。其次，我们将了解如何独立于javax.servlet.http的请求和响应对象将Java代码直接添加到我们的.jsp文件中。

第三，我们将演示如何将HttpServletRequest转发到特定的.jsp并将服务器端处理的Java绑定到它。

让我们使用File/New/Project/Web/Dynamic web project/类型在Eclipse中设置我们的项目以托管在Tomcat中！创建项目后你应该看到：

```bash
|-project
  |- WebContent
    |- META-INF
      |- MANIFEST.MF
    |- WEB-INF
      |- lib
      |- src
```

我们将向应用程序结构中添加一些文件，以便我们最终得到：

```bash
|-project
  |- WebContent
    |- META-INF
      |- MANIFEST.MF
    |- WEB-INF
      |-lib
      -web.xml
        |- ExampleTree.jsp
        |- ExampleTwo.jsp
        - index.jsp
      |- src
        |- cn
          |- tuyucheng
            |- taketoday
              - ExampleOne.java
              - ExampleThree.java
```

让我们设置index.jsp，当我们访问Tomcat 8中的URL上下文时将显示它：

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>JSP Examples</title>
</head>
<body>
<h1>Simple JSP Examples</h1>
<p>Invoke HTML rendered by Servlet: <a href="ExampleOne" target="_blank">here</a></p>
<p>Java in static page: <a href="ExampleTwo.jsp" target="_blank">here</a></p>
<p>Java injected by Servlet: <a href="ExampleThree?message=hello!" target="_blank">here</a></p>
</body>
</html>
```

共有三个a，每个都链接到我们将在下面4.1到4.4节中介绍的示例之一。

我们还需要确保我们已经设置了web.xml：

```html
<welcome-file-list>
    <welcome-file>index.html</welcome-file>
    <welcome-file>index.htm</welcome-file>
    <welcome-file>index.jsp</welcome-file>
</welcome-file-list>
<servlet>
    <servlet-name>ExampleOne</servlet-name>
    <servlet-class>cn.tuyucheng.taketoday.ExampleOne</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>ExampleOne</servlet-name>
    <url-pattern>/ExampleOne</url-pattern>
</servlet-mapping>
```

这里的一个主要注意事项是——如何正确地将我们的每个Servlet映射到特定的servlet-mapping。这样做会将每个Servlet与它可以使用的特定端点相关联！现在，我们将浏览下面的每个其他文件！

### 3.1 在Servlet中呈现的HTML

在此示例中，我们实际上将跳过构建.jsp文件！

相反，我们将创建标签的字符串表示，然后在ExampleOne Servlet收到GET请求后使用PrintWriter将其写入GET响应：

```java
public class ExampleOne extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(
              "<!DOCTYPE html><html>" +
                    "<head>" +
                    "<meta charset=\"UTF-8\" />" +
                    "<title>HTML Rendered by Servlet</title>" +
                    "</head>" +
                    "<body>" +
                    "<h1>HTML Rendered by Servlet</h1></br>" +
                    "<p>This page was rendered by the ExampleOne Servlet!</p>" +
                    "</body>" +
                    "</html>"
        );
    }
}
```

我们在这里所做的是直接通过我们的servlet请求处理注入我们的标签。我们生成HTML，以及要插入的任何和所有特定于Java的数据，而不是JSP标签，纯服务器端没有静态JSP！

早些时候，我们回顾了out对象，它是JspWriter的一个特性。

在上面，我使用了直接写入响应对象的PrintWriter对象。

JspWriter实际上将要写入内存的字符串缓存起来，然后在刷新内存缓冲区后将其写入响应对象。

PrintWriter已附加到响应对象。出于这些原因，我更喜欢直接写入上面和下面示例中的响应对象。

### 3.2 JSP静态内容中的Java

这里我们创建一个名为ExampleTwo.jsp的JSP文件，带有一个JSP标签。如上所示，这允许将Java直接添加到我们的标签中。在这里，我们随机打印String[]的一个元素：

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
    <head>
        <title>Java in Static Page Example</title>
    </head>
    <body>
        <h1>Java in Static Page Example</h1>
	    <% 
              String[] arr = {"What's up?", "Hello", "It's a nice day today!"}; 
	      String greetings = arr[(int)(Math.random() * arr.length)];	
            %>
        <p><%= greetings %></p>
    </body>
</html>
```

在上面，你将看到JSP标签对象中的变量声明：类型variableName和像常规Java一样的初始化。

我包含了上面的示例来演示如何在不求助于特定servlet的情况下将Java添加到静态页面。在这里，Java只是简单地添加到页面中，JSP生命周期负责其余的工作。

### 3.3 带转发的JSP

现在，对于我们最后的也是最复杂的例子！在这里，我们将在ExampleThree上使用@WebServlet注解，这消除了server.xml中对servlet映射的需要。

```java
@WebServlet(
      name = "ExampleThree",
      description = "JSP Servlet With Annotations",
      urlPatterns = {"/ExampleThree"}
)
public class ExampleThree extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
        String message = request.getParameter("message");
        request.setAttribute("text", message);
        request.getRequestDispatcher("/ExampleThree.jsp").forward(request, response);
    }
}
```

ExampleThree接受作为消息传入的URL参数，将该参数绑定到请求对象，然后将该请求对象重定向到ExampleThree.jsp。

因此，我们不仅实现了真正的动态Web体验，而且还在包含多个.jsp文件的应用程序中实现了这一点。

getRequestDispatcher().forward()是一种确保呈现正确.jsp页面的简单方法。

所有绑定到请求对象的数据都将显示出来！以下是我们处理最后一部分的方式：

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
    <head>
        <title>Java Binding Example</title>
    </head>
    <body>
        <h1>Bound Value</h1>
	    <p>You said: ${text}</p>
    </body>
</html>
```

请注意添加到ExampleThree.jsp顶部的JSP标签。你会注意到我在这里切换了JSP标签。我正在使用表达式语言(我之前提到过)来呈现我们的设置参数(绑定为${text})！

### 3.4 试试看！

现在，我们将把我们的应用程序导出到.war中，以便在Tomcat 8中启动和托管！找到你的server.xml，我们会将上下文更新为：

```xml
<Context path="/spring-mvc-xml" docBase="${catalina.home}/webapps/spring-mvc-xml">
</Context>
```

这将允许我们在localhost:8080/spring-mvc-xml/jsp/index.jsp上访问我们的servlet和JSP。

## 4. 总结

我们已经涵盖了相当多的内容！我们已经了解了JavaServer Pages是什么、它们的引入是为了完成什么、它们的生命周期、如何创建它们，以及最后几种不同的实现它们的方法！