## **一、概述**

*[nudge4j](https://lorenzoongithub.github.io/nudge4j/)*允许开发人员直接查看任何操作的影响，并提供一个环境，他们可以在其中探索、学习并最终花更少的时间调试和重新部署他们的应用程序。

在本文中，我们将探索*nudge4j*是什么、它是如何工作的，以及开发中的任何 Java 应用程序如何从中受益。

## **2. \*nudge4j\*是如何工作的**

### **2.1. 伪装的 REPL**

*nudge4j*本质上是一个读取-评估-打印-循环 (REPL)，您可以在其中通过一个仅包含两个元素的简单页面**在浏览器窗口中与您的 Java 应用程序对话：**

-   一位编辑
-   *在 JVM*按钮上执行

[![nudge4j.in_.action](https://www.baeldung.com/wp-content/uploads/2017/02/nudge4j.in_.action.499x439.png)](https://www.baeldung.com/wp-content/uploads/2017/02/nudge4j.in_.action.499x439.png)

您可以在典型的 REPL 周期中与您的 JVM 对话：

-   在编辑器中输入任何代码，然后*在 JVM 上按执行*
-   浏览器将代码发布到您的 JVM，然后运行该代码
-   结果返回（作为字符串）并显示在按钮下方

*nudge4j*附带了一些可以直接尝试的示例，比如查询 JVM 运行了多长时间以及当前有多少内存可用。我建议您在编写自己的代码之前先从这些开始。

### **2.2. JavaScript 引擎**

浏览器发送给 JVM 的代码是操纵 Java 对象的 JavaScript（不要与浏览器上运行的任何 JavaScript 混淆）。JavaScript 由内置的 JavaScript 引擎[*Nashorn*](http://openjdk.java.net/projects/nashorn/)执行。

如果您不了解（或不喜欢）JavaScript，请不要担心 – 对于您的*nudge4j*需求，您可以将其视为一种无类型的 Java 方言。

请注意，我知道说*“JavaScript 是无类型的 Java”*是一个巨大的简化。但我希望 Java 开发人员（他们可能对 JavaScript 有偏见）给*nudge4j*一个公平的机会。

### **2.3. JVM 交互的范围**

*nudge4j*允许您**访问任何可从 JVM 访问的 Java 类**，允许您调用方法、创建对象等。这非常强大，但在处理您的应用程序时可能还不够。

在某些情况下，您可能希望访问一个或多个仅特定于您的应用程序的对象，以便您可以操作它们。*nudge4j*允许这样做。任何需要公开的对象都可以在实例化时作为参数传递。

### **2.4. 异常处理**

*nudge4j*的设计认识到该工具的用户可能会犯错误或导致 JVM 出错的可能性。在这两种情况下，该工具都旨在报告完整的堆栈跟踪，以指导用户纠正错误或错误。

让我们看一下屏幕截图，其中已执行的一段代码导致抛出异常：

## [![nudge4j.异常](https://www.baeldung.com/wp-content/uploads/2017/02/nudge4j.exception.488x711.png)](https://www.baeldung.com/wp-content/uploads/2017/02/nudge4j.exception.488x711.png)

## **3. 将\*nudge4j\*添加到您的应用程序**

### **3.1. 只需复制和粘贴**

*与nudge4j*的集成有些不同寻常，因为没有要添加到类路径的*jar*文件，也没有要添加到 Maven 或 Gradle 构建的依赖项。

相反，您需要**简单地复制一小段 Java 代码**（大约 100 行）并将其粘贴到您自己的代码中，然后再运行它。

[*您会在nudge4j*主页](https://lorenzoongithub.github.io/nudge4j/)上找到该片段——页面上什至有一个按钮，您可以单击该按钮将片段复制到剪贴板。

这段代码初看起来可能很深奥。有几个原因：

-   *nudge4j*片段可以放入任何类；因此，它不能对*import*做任何假设，并且它包含的任何类都必须是完全合格的
-   为了避免与已经定义的变量发生潜在的冲突，代码被包装在一个函数中
-   对内置 JDK HttpServer 的访问是通过内省来完成的，以避免某些 IDE（例如 Eclipse）对以“ *com.sun.\*”开头的包存在的限制*

因此，即使 Java 已经是一种冗长的语言，它也必须变得更加冗长以提供无缝集成。

### **3.2. 示例应用程序**

让我们从一个标准的 JVM 应用程序开始，我们假设一个简单的*java.util.HashMap*包含我们想要使用的大部分信息：

```java
public class MyApp {
    public static void main(String args[]) {
        Map map = new HashMap();
        map.put("health", 60);
        map.put("strength", 4);
        map.put("tools", Arrays.asList("hammer"));
        map.put("places", Arrays.asList("savannah","tundra"));
        map.put("location-x", -42 );
        map.put("location-y", 32);
 
        // paste original code from nudge4j below
        (new java.util.function.Consumer<Object[]>() {
            public void accept(Object args[]) {
                ...
                ...
            }
        }).accept(new Object[] { 
            5050,  // <-- the port
            map    // <-- the map is passed as a parameter.
        });
    }
}复制
```

从此示例中可以看出，您只需将*nudge4j*片段粘贴到您自己的代码末尾即可。此处示例中的第 12-20 行用作代码片段缩写版本的占位符。

现在，让我们将浏览器指向*http://localhost:5050/。*地图现在可以在浏览器的编辑器中作为*args[1]*访问，只需键入：

```java
args[1];复制
```

*这将提供我们的Map*的摘要（在本例中依赖于*Map的**toString()*方法及其键和值）。

假设我们要检查和修改键值为*“tools”的**Map*条目。

*要获取Map*中所有可用*工具*的列表，您可以编写：

```java
map = args[1];
map.get("tools");复制
```

要向 Map 添加一个新工具*，您可以**这样*写：

```java
map = args[1];
map.get("tools").add("axe");复制
```

通常，几行代码就足以探测任何 Java 应用程序。

## **4。结论**

通过在 JDK（*Nashorn*和*Http 服务器*）中组合两个简单的 API， *nudge4j*使您能够探测任何 Java 8 应用程序。

在某种程度上，*nudge4j*只是一个旧想法的现代截断：让开发人员通过脚本语言访问现有系统的设施——这个想法可以对 Java 开发人员如何度过一天的编码产生影响。