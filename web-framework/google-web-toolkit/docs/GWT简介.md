## 1. 简介

GWT 或 Google Web Toolkit 是一个用Java构建高性能 Web 应用程序的框架。

在本教程中，我们将重点介绍并涵盖其一些关键功能。

## 2.GWT SDK

SDK 包含JavaAPI 库、编译器和开发服务器。

### 2.1. Java接口

GWT API 具有用于构建用户界面、进行服务器调用、国际化、执行单元测试的类。[要了解更多信息，请在此处](http://www.gwtproject.org/javadoc/latest/index.html)查看 java 文档。

### 2.2. 编译器

简单地说，GWT 编译器是一个从Java代码到 Javascript 的源代码翻译器。编译的结果是一个 Javascript 应用程序。

其工作逻辑包括从代码中修剪未使用的类、方法、字段并缩短 Javascript 名称。

由于这个优势，我们不再需要在我们的 Javascript 项目中包含 Ajax 库。当然，也可以在编译代码的时候设置提示。

这里有一些有用 的 GWTCompiler参数：

-   -logLevel – 设置ERROR、WARN、INFO、TRACE、DEBUG、SPAM、ALL日志记录级别之一
-   -workdir – 编译器的工作目录
-   -gen – 写入生成文件的目录
-   -out – 输出文件目录
-   -optimize – 将编译器优化级别设置为 0 到 9
-   -style – 脚本输出样式OBF、PRETTY或DETAILED
-   -module[s] – 要编译的模块的名称

## 3.设置

[下载](http://www.gwtproject.org/download.html)页面上提供了最新的 SDK 。其余设置在[入门](http://www.gwtproject.org/gettingstarted.html)页面上可用。

### 3.1. 行家

要使用 Maven 设置项目，我们需要将以下依赖项添加到pom.xml：

```xml
<dependency>
    <groupId>com.google.gwt</groupId>
    <artifactId>gwt-servlet</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>com.google.gwt</groupId>
    <artifactId>gwt-user</artifactId>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>com.google.gwt</groupId>
    <artifactId>gwt-dev</artifactId>
    <scope>provided</scope>
</dependency>
```

gwt-servlet 库支持用于调用 GWT-RPC 端点的服务器端组件。gwt-user包含我们将用于构建 Web 应用程序的JavaAPI。gwt-dev具有用于编译、部署或托管应用程序的代码。

为了确保所有依赖项使用相同的版本，我们需要包含父 GWT 依赖项：

```xml
<dependency>
    <groupId>com.google.gwt</groupId>
    <artifactId>gwt</artifactId>
    <version>2.8.2</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

所有工件都可以在[Maven Central](https://mvnrepository.com/artifact/com.google.gwt)上下载。

## 4.申请

让我们构建一个简单的 Web 应用程序。它将向服务器发送消息并显示响应。

一般而言，GWT 应用程序由服务器和客户端部分组成。客户端发出 HTTP 请求与服务器连接。为了使之成为可能，GWT 使用远程过程调用或简单的 RPC 机制。

## 5.GWT和RPC

回到我们的应用程序，让我们看看 RPC 通信是如何进行的。为此，我们创建了一个服务来接收来自服务器的消息。

让我们首先创建一个接口：

```java
@RemoteServiceRelativePath("greet")
public interface MessageService extends RemoteService {
    String sendMessage(String message) throws IllegalArgumentException;
}
```

@RemoteServiceRelativePath 注解将服务映射到模块的 /message相对 URL。MessageService应该扩展自RemoteService标记接口以执行 RPC 通信。

MessageService的实现在服务器端：

```java
public class MessageServiceImpl extends RemoteServiceServlet 
  implements MessageService {

    public String sendMessage(String message) 
      throws IllegalArgumentException {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }

        return "Hello, " + message + "!<br><br> Time received: " 
          + LocalDateTime.now();
    }
}
```

我们的服务器类扩展自 RemoteServiceServlet基 servlet 类。 它将自动反序列化来自客户端的传入请求并序列化来自服务器的传出响应。

现在让我们看看我们如何从客户端使用它。MessageService 只是我们服务的最终版本。

要在客户端执行，我们需要创建服务的异步版本：

```java
public interface MessageServiceAsync {
    void sendMessage(String input, AsyncCallback<String> callback) 
      throws IllegalArgumentException;
}
```

在这里我们可以在getMessage()方法中看到一个额外的参数。我们需要async在异步调用完成时通知 UI。这样我们就可以防止阻塞正在工作的 UI 线程。

## 6. 组件及其生命周期

SDK 提供了一些用于设计图形界面的 UI 元素和布局。

一般来说，所有的 UI 组件都继承自Widget类。在视觉上，我们有可以在屏幕上看到、单击或移动的元素小部件：

-   组件小部件– TextBox、TextArea、Button、RadioButton、CheckBox等……

并且有组成和组织屏幕的布局或面板小部件：

-   面板小部件 – Horizo ntalPanel 、VerticalPanel、PopupPanel、TabPanel等……

每次我们在代码中添加小部件或任何其他组件时，GWT 都会努力将视图元素与浏览器的 DOM 链接起来。

构造函数总是初始化根 DOM 元素。当我们将子部件附加到父组件时，它也会导致在 DOM 级别进行绑定。入口点类包含将首先调用的加载函数。这是我们定义小部件的地方。

## 7. 切入点

让我们仔细看看应用程序的主要入口点：

```java
public class Google_web_toolkit implements EntryPoint {

    private MessageServiceAsync messageServiceAsync = GWT.create(MessageService.class);

    public void onModuleLoad() {
        Button sendButton = new Button("Submit");
        TextBox nameField = new TextBox();
        nameField.setText("Hi there");

        sendButton.addStyleName("sendButton");

        RootPanel.get("nameFieldContainer").add(nameField);
        RootPanel.get("sendButtonContainer").add(sendButton);
    }
}
```

每个 UI 类都实现com.google.gwt.core.client.EntryPoint接口以将其标记为模块的主条目。它连接到相应的 HTML 文档，Java 代码将在其中执行。

我们可以定义 GWT UI 组件，然后分配给具有相同 ID 的 HTML 标签。入口点类重写入口点onModuleLoad()方法，加载模块时自动调用该方法。

在这里我们创建 UI 组件，注册事件处理程序，修改浏览器 DOM。

现在，让我们看看如何创建远程服务器实例。为此，我们使用GWT.create(MessageService.class)静态方法。

它在编译时确定请求的类型。看到这种方法，GWT 编译器在编译时生成许多版本的代码，只有其中一个需要在运行时引导期间由特定客户端加载。此功能广泛用于 RPC 调用。

在这里，我们还定义了Button和TextBox小部件。要将它们附加到 DOM 树中，我们使用RootPanel类。它是根面板并返回一个单例值以绑定小部件元素：

```java
RootPanel.get("sendButtonContainer").add(sendButton);
```

首先，它获取标有sendButtonContainer id 的根容器。在我们将sendButton 附加到容器之后。

## 8.HTML

在/webapp文件夹中，我们有Google_web_toolkit.html文件。

我们可以用特定的 id 标记标签元素，以便框架可以将它们绑定到Java对象中：

```html
<body>
    <h1>Sample GWT Application</h1>
    <table align="center">
        <tr>
            <td colspan="2" style="font-weight:bold;">Please enter your message:</td>
        </tr>
        <tr>
            <td id="nameFieldContainer"></td>
            <td id="sendButtonContainer"></td>
        </tr>
    </table>
</body>
```

具有nameFieldContainer和 sendButtonContainer id的<td>标签将映射到Button和TextBox组件。

## 9. 主模块描述符

让我们看一下Google_web_toolkit.gwt.xml主模块描述符文件的典型配置：

```xml
<module rename-to='google_web_toolkit'>
    <inherits name='com.google.gwt.user.User'/>
    <inherits name='com.google.gwt.user.theme.clean.Clean'/>
    <entry-point class='com.baeldung.client.Google_web_toolkit'/>
</module>
```

我们通过包含com.google.gwt.user.User接口使核心 GWT 内容可访问 。此外，我们可以为我们的应用程序选择默认样式表。在这种情况下，它是.clean.Clean。

其他可用的样式选项是.dark.Dark、.standard.Standard、.chrome.Chrome。com.baeldung.client.Google_web_toolkit 在这里也标有<entry - point />标签。

## 10. 添加事件处理程序

为了管理鼠标或键盘键入事件，GWT 将使用一些处理程序。它们都从 EventHandler 接口扩展而来，并有一个带有事件类型参数的方法。

在我们的示例中，我们注册了鼠标单击事件处理程序。

这将在每次按下按钮时触发onClick()方法： 

```java
closeButton.addClickHandler(new ClickHandler() {
    public void onClick(ClickEvent event) {
        vPanel.hide();
        sendButton.setEnabled(true);
        sendButton.setFocus(true);
    }
});
```

在这里我们可以修改小部件的状态和行为。在我们的示例中，我们隐藏了vPanel并启用了sendButton。

另一种方法是定义一个内部类并实现必要的接口：

```java
class MyHandler implements ClickHandler, KeyUpHandler {

    public void onClick(ClickEvent event) {
        // send message to the server
    }

    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            // send message to the server
        }
    }
}
```

除了 ClickHandler之外，我们还在此处包含KeyUpHandler接口以捕获按键事件。在这里，在onKeyUp()方法内部 ，我们可以使用KeyUpEvent来检查用户是否按下了 Enter 键。

在这里我们如何使用MyHandler类来注册两个事件处理程序：

```java
MyHandler handler = new MyHandler();
sendButton.addClickHandler(handler);
nameField.addKeyUpHandler(handler);
```

## 11.调用服务器

现在，我们已准备好将消息发送到服务器。我们将使用异步sendMessage()方法执行远程过程调用。

该方法的第二个参数是 AsyncCallback<String>接口，其中String是相应同步方法的返回类型：

```java
messageServiceAsync.sendMessage(textToServer, new AsyncCallback<String>() {
    public void onFailure(Throwable caught) {
        serverResponseLabel.addStyleName("serverResponseLabelError");
        serverResponseLabel.setHTML("server error occurred");
        closeButton.setFocus(true);
    }

    public void onSuccess(String result) {
        serverResponseLabel.setHTML(result);
        vPanel.setVisible(true);
    }
});
```

正如我们所见，接收者为每个响应类型实现了 onSuccess(String result) 和onFailure(Throwable) 方法。

根据响应结果，我们要么设置错误消息“发生服务器错误”，要么在容器中显示结果值。

## 12. CSS 样式

使用eclipse插件创建项目时，会自动在 /webapp目录下生成Google_web_toolkit.css文件 ，并链接到主HTML文件。

```html
<link type="text/css" rel="stylesheet" href="Google_web_toolkit.css">
```

当然，我们可以通过编程方式为特定的 UI 组件定义自定义样式：

```java
sendButton.addStyleName("sendButton");
```

在这里，我们将类名为sendButton 的CSS 样式分配给我们的sendButton组件：

```css
.sendButton {
    display: block;
    font-size: 16pt;
}
```

## 13. 结果

结果，我们有了这个简单的 Web 应用程序：

[![简单应用](https://www.baeldung.com/wp-content/uploads/2018/07/simpleApplication-300x259-300x259.png)](https://www.baeldung.com/wp-content/uploads/2018/07/simpleApplication-300x259.png)

在这里，我们向服务器提交一条“你好”消息并打印“你好，你好！” 屏幕上的响应。

## 14.总结

在这篇简短的文章中，我们了解了 GWT 框架的基础知识。之后，我们讨论了其 SDK 的架构、生命周期、功能和不同组件。

因此，我们学会了如何创建一个简单的 Web 应用程序。