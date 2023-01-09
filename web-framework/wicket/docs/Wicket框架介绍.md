## 1. 概述

[Wicket](https://wicket.apache.org/)是一个Java服务器端面向 Web 组件的框架，旨在通过引入桌面 UI 开发中已知的模式来简化 Web 界面的构建。

使用 Wicket 可以仅使用Java代码和 XHTML 兼容的 HTML 页面来构建 Web 应用程序。不需要 Javascript，也不需要 XML 配置文件。

它在请求-响应周期之上提供了一个层，避免在低级别工作，并允许开发人员专注于业务逻辑。

在本文中，我们将通过构建HelloWorld Wicket 应用程序来介绍基础知识，然后是一个使用两个相互通信的内置组件的完整示例。

## 2.设置

要运行 Wicket 项目，让我们添加以下依赖项：

```xml
<dependency>
    <groupId>org.apache.wicket</groupId>
    <artifactId>wicket-core</artifactId>
    <version>7.4.0</version>
</dependency>
```

[你可能想在Maven 中央存储库](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.wicket" AND a%3A"wicket")中查看最新版本的 Wicket，在你阅读时它可能与此处使用的版本不一致。

现在我们准备构建我们的第一个 Wicket 应用程序。

## 3.你好世界检票口

让我们从子类化 Wicket 的WebApplication类开始，它至少需要重写 Class <? 扩展 Page> getHomePage()方法。

Wicket 将使用此类作为应用程序的主要入口点。在方法内部，简单地返回一个名为HelloWorld的类的类对象：

```java
public class HelloWorldApplication extends WebApplication {
    @Override
    public Class<? extends Page> getHomePage() {
        return HelloWorld.class;
    }
}
```

Wicket 倾向于约定优于配置。向应用程序添加新网页需要在同一目录下创建两个文件：一个Java文件和一个 HTML 文件，名称相同(但扩展名不同)。仅当你想要更改默认行为时才需要额外的配置。

在源代码的包目录中，首先添加HelloWorld.java：

```java
public class HelloWorld extends WebPage {
    public HelloWorld() {
        add(new Label("hello", "Hello World!"));
    }
}
```

然后是 HelloWorld.html：

```html
<html>
    <body>
        <span wicket:id="hello"></span>
    </body>
</html>
```

最后一步，在web.xml 中添加过滤器定义：

```xml
<filter>
    <filter-name>wicket.examples</filter-name>
    <filter-class>
      org.apache.wicket.protocol.http.WicketFilter
    </filter-class>
    <init-param>
        <param-name>applicationClassName</param-name>
        <param-value>
          com.baeldung.wicket.examples.HelloWorldApplication
        </param-value>
    </init-param>
</filter>
```

而已。我们刚刚编写了第一个 Wicket Web 应用程序。

通过构建war文件(命令行中的mvn package )运行项目，并将其部署在 servlet 容器(例如 Jetty 或 Tomcat)上。

让我们在浏览器中访问[http://localhost:8080/HelloWorld/ 。](http://localhost:8080/HelloWorld/)带有消息Hello World! 的空白页面 应出现。

## 4. 检票口组件

Wicket 中的组件是三元组，由Java类、HTML 标记和模型组成。模型是组件用来访问数据的外观。

这种结构提供了很好的关注点分离，并且通过将组件与以数据为中心的操作分离，增加了代码重用。

下面的示例演示了如何向组件添加 Ajax 行为。它由一个包含两个元素的页面组成：下拉菜单和标签。当下拉选择发生变化时，标签(并且只有标签)将被更新。

HTML 文件CafeSelector.html的主体将是最小的，只有两个元素，一个下拉菜单和一个标签：

```html
<select wicket:id="cafes"></select>
<p>
    Address: <span wicket:id="address">address</span>
</p>
```

在Java端，让我们创建标签：

```java
Label addressLabel = new Label("address", 
  new PropertyModel<String>(this.address, "address"));
addressLabel.setOutputMarkupId(true);
```

Label构造函数中的第一个参数与 HTML 文件中分配的wicket:id匹配。第二个参数是组件的模型，它是组件中显示的基础数据的包装器。

setOutputMarkupId方法使组件有资格通过 Ajax 进行修改。现在让我们创建下拉列表并向其添加 Ajax 行为：

```java
DropDownChoice<String> cafeDropdown 
  = new DropDownChoice<>(
    "cafes", 
    new PropertyModel<String>(this, "selectedCafe"), 
    cafeNames);
cafeDropdown.add(new AjaxFormComponentUpdatingBehavior("onchange") {
    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        String name = (String) cafeDropdown.getDefaultModel().getObject();
        address.setAddress(cafeNamesAndAddresses.get(name).getAddress());
        target.add(addressLabel);
    }
});
```

创建类似于标签，构造函数接受 wicket id、模型和咖啡馆名称列表。

然后，AjaxFormComponentUpdatingBehavior添加了onUpdate回调方法，一旦发出 ajax 请求，该回调方法就会更新标签的模型。最后将标签组件设置为刷新目标。

最后将标签组件设置为刷新目标。

如你所见，一切都是 Java，不需要一行 Javascript。为了改变标签显示的内容，我们简单地修改了一个 POJO。修改Java对象转化为网页更改的机制发生在幕后，与开发人员无关。

Wicket 提供了大量开箱即用的支持 AJAX 的组件。带有现场示例的组件目录可[在此处](https://wicket.apache.org/learn/examples/index.html)获得。

## 5.总结

在这篇介绍性文章中，我们介绍了 Wicket 的基础知识，这是Java中基于组件的 Web 框架。

Wicket 提供了一个抽象层，旨在完全消除管道代码。