## 什么是JavaScript？

JavaScript 是浏览器内部与 HTML dom 交互的首选语言。这意味着浏览器中有 JavaScript 实现并理解 JavaScript 命令。你可以使用浏览器中的浏览器选项禁用它。

JavaScript 也是早期 Selenium 版本使用的语言，Selenium Web 驱动程序仍然使用它来执行某些操作。例如，Selenium 在 IE 中用 JavaScript 实现了 Xpath，以克服 IE 中缺少 Xpath 引擎的问题。理解 JavaScript 很有趣，它可以让你做很多很酷的事情，否则你可能会觉得很棘手。让我们了解如何

WebDriver 为你提供了一个名为 Driver.executeScript 的方法，它在加载的浏览器页面的上下文中执行 JavaScript。

### JavaScript 和 Selenium JavaScriptExecutor 能为我们做什么？

首先要知道JavaScriptExecutor是单独出现的，也属于 WebDriver 下，但两者做的是同样的事情。在 WebDriver 中，它被命名为ExecuteScript。你将在下面找到两者的示例。你可以使用其中的任何一个。

嗯，几乎任何你想在浏览器中做的事情。让我们看看如何尝试将 selenium 命令与 JavaScript 并行。

评估 Xpath

你可以使用 javascripts 通过 Xpath 查找元素。这是跳过内部 Xpath 引擎的绝妙方法。这是你必须做的

```java
WebElement value = (WebElement) Driver.executeScript("return document.evaluate( '//body//div/iframe' ,document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue;");
```

注意命令 document.evaluate()。这是 javascript 中的 Xpath 求值器。该函数的签名是

```java
document.evaluate( xpathExpression, contextNode, namespaceResolver, resultType, result );
```

-   xpathExpression：包含要计算的 XPath 表达式的字符串。
-   contextNode：文档中的一个节点，xpathExpression 应该根据该节点进行评估，包括它的任何和所有子节点。文档节点是最常用的。
-   NamespaceResolver：一个函数，它将传递包含在 xpathExpression 中的任何命名空间前缀，它返回一个字符串，表示与该前缀关联的命名空间 URI。
-   resultType：一个常量，指定要作为评估结果返回的所需结果类型。最常传递的常量是 XPathResult.ANY_TYPE，它将 XPath 表达式的结果作为最自然的类型返回。
-   result：如果指定了现有的 XPathResult 对象，它将被重用以返回结果。指定 null 将创建一个新的 XPathResult 对象

### 查找元素

使用 selenium 我们可以使用类似于以下的方法在页面上找到任何元素

```java
Driver.findElementById("Id of the element");
```

我们可以像这样使用 JavaScript 做同样的事情

```java
WebElement searchbox = null;

searchbox = (WebElement) ff.executeScript("return document.getElementById('gsc-i-id1');", searchbox);
```

你将获得搜索框变量中的元素。

查找元素Selenium 命令

```java
List\<WebElement\> elements = List\<WebElement\> Driver.findElementByTagName("div");
```

使用 JavaScript

```java
List elements = (List') ff.executeScript("return document.getElementsByTagName('div');", searchbox);
```

### 改变元素的样式属性

你可以更改元素的样式属性以更改元素的渲染视图。例如，这就是如何在 toolsQa 主页上的关于我的元素周围创建边框。在创建边框之前和之后查看相同元素的以下图像

![前后](https://www.toolsqa.com/gallery/selnium%20webdriver/1.beforeafter.png)

这是代码。

```java
Driver.executeScript("document.getElementById('text-4').style.borderColor = 'Red'");
```

着色元素还可以帮助我们使用视觉标记截取屏幕截图，以识别有问题的元素。

### 获取元素属性

你可以获得元素的有效属性的任何值。例如

```java
Driver.findElementById("gsc-i-id1").getAttribute("Class");
```

此代码将获取 id = gsc-i-id1 的元素的类属性值。

在 Javascript 中，可以使用

```java
String className = Driver.executeScript("return document.getElementById('gsc-i-id1').getAttribute('class');"));
```

### 浏览器中的总帧数

假设你想知道网页内的框架总数，包括 Iframe。你不能直接使用 selenium 来完成，你可能需要创建自己的逻辑来解析和查找框架。然而，在 JavaScript 中很简单，就是这样完成的。

```java
ff.executeScript("document.frames.length;");
```

小心这个。它只能在 IE 中运行，无法在 Mozilla 中运行。

### 在 DOM 中添加元素

如果你想在 DOM 中添加一个元素，只是为了好玩。让我们添加一个按钮 HTML 对象

```java
Driver.executeScript("var btn=document.createElement('BUTTON');"

                     + "document.body.appendChild(btn);");
```

### 窗口大小

查找内部浏览器窗口的大小。它是你在其中查看网页的窗口的大小。

```java
Driver.executeScript("return window.innerHeight;")

Driver.executeScript("return window.innerWidth;")
```

### 导航到不同的页面

```java
ff.executeScript("window.location = 'https://yahoo.com'");
```

## 什么是 JavaScriptExecutor？

JavaScriptExecutor是一个接口，它提供了一种通过 selenium 驱动程序执行 Javascript 的机制。它提供“ executescript ”和“ executeAsyncScript ”方法，在当前选定的框架或窗口的上下文中运行 JavaScript。

### 生成警报弹出窗口

```java
JavascriptExecutor js = (JavascriptExecutor)driver;
Js.executeScript("alert('hello world');");
```

### 单击操作

```java
JavascriptExecutor js = (JavascriptExecutor)driver;
js.executeScript("arguments[0].click();", element);
```

刷新浏览器

```java
JavascriptExecutor js = (JavascriptExecutor)driver;
driver.executeScript("history.go(0)");
```

### 获取网页的内部文本

```java
JavascriptExecutor js = (JavascriptExecutor)driver;
string sText =  js.executeScript("return document.documentElement.innerText;").toString();
```

### 获取网页的标题

```java
JavascriptExecutor js = (JavascriptExecutor)driver;
string sText =  js.executeScript("return document.title;").toString();
```



### 滚动页面

```java
 JavascriptExecutor js = (JavascriptExecutor)driver;
  //Vertical scroll - down by 150  pixels
  js.executeScript("window.scrollBy(0,150)");
```

同样，你几乎可以使用 Selenium 执行任何 JavaScript 命令。我希望这篇博客能让你了解可以做什么，并且你可以通过尝试不同的 JavaScript 来推进它。