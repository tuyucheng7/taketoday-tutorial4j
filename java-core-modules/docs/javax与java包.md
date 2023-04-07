## 一、概述

**Java 中的包是一组相关接口和类的范围**。应用程序可以包含成百上千个单独的类。将类和接口放入包中可以很容易地使用它。

*在本教程中，我们将探索java*和*javax*包的一系列示例。此外，我们将深入研究它们之间的主要区别，并更好地了解它们的功能。

## 2.java包*_*

java包包含 Java 编程语言*中*的核心类或接口。**编写基本 Java 程序所需的大多数类都在\*java\*包**中。

***最常用的java\*包之一是\*java.lang\***。**它包含对 Java 编程语言本身的设计必不可少的关键类**。

这个包中最重要的类是[*Object*](https://www.baeldung.com/java-classes-objects)类，它是类层次结构的基础。此外，我们还有[*Math*](https://www.baeldung.com/java-lang-math)类，它提供余弦、正弦、平方根等数学函数。

此外，*java*包的另一个示例是 *java.net*。该包包含用于开发网络应用程序的类。此包中的示例类是*Inet4Address* ，它表示 Internet 协议版本 4 (IPv4) 地址。

此外，***java.awt\* （抽象窗口工具包）包提供了用于在桌面应用程序中开发图形用户界面 (GUI) 的类和接口**。*这个包是javax.swing*包的基础。*TextField* 是一个示例类。它是一个 GUI 组件，为用户输入提供了一个区域。

最后，**另一个常见的例子是\*java.io\*。它是一组类和接口，为 Java 中的输入/输出 (I/O) 操作提供功能**。此外，它有助于处理来自不同来源的数据，例如文件、用户输入/输出等。java.io*包*中的一个示例类是*FileInputStream。* 该类对于读取任何类型的文件（包括图像、文本和二进制文件）都是必不可少的。

## 3.javax包*_*

**javax包包含扩展\*java\*包功能的类或\*接口\***。**它也被称为扩展包**。

它为核心*java*包中已有的一些类提供了附加功能。有几个*javax*包，但我们将回顾几个例子。

最常见的 javax 包之一是*javax.swing*。它提供了一组用于在 Java 中创建图形用户界面 (GUI) 的类。[它建立在Abstract Window Toolkit (AWT)](https://www.baeldung.com/java-images#:~:text=AWT is a built-in,it is shipped with Java.)之上。这个包中的一些关键类是*JPanel*、*JFrame*、*JComponent*、*JButton*和*JLabel*。它帮助我们创建 GUI 组件、定制它们的外观并添加事件监听器。

另一个例子是*javax.tool*。这个包包含可以从 Java 程序调用的接口和类。一个通用接口是*JavaCompiler，*它有助于从程序中调用 Java 编译器。此外，我们还有基于java.io.File 的*StandardJavaFileManager* *。* 它有助于管理文件。

此外，***javax.net\*是另一个\*javax\*包。** **此包提供使用 Java 安全套接字扩展 (JSSE) 框架进行网络通信的类**。它的一些类有助于保护套接字通信。

此外，***[javax.servlet](https://www.baeldung.com/intro-to-servlets)\* 是另一个流行的例子。这个包提供了一组类和接口，用于开发在 Web 服务器中运行的 Web 应用程序**。它包含可以处理请求和响应的类。一个示例类是*ServletInputStream*，它有助于从客户端请求中读取二进制数据。

最后，***javax.crypto*** **为加密过程提供了一组类和接口**。一个示例类是*[Cipher](https://www.baeldung.com/java-cipher-class)。* Cipher提供加密和解密功能*。* 它是 Java 密码扩展 (JCE) 框架的核心。

## *4、 java*和*javax*包比较

*javax*和*java*包都提供类和接口来编写有效的 Java 程序。

***java\*包包含核心 Java 应用程序编程接口 (API)**。它有助于引导任何 Java 程序，并作为大多数 Java API 的基础。

另一方面，**javax包是核心\*java\*包\*的\*扩展**。***它提供了构建在java\*****包之上的附加类以添加高级特性和功能**。

随着 Java 的发展，需要修改核心*java*包的新功能作为扩展包引入。Java 是一种向后兼容的语言。引入新功能作为扩展使旧程序运行。

## 5.结论

在本文中，我们看到了*java*和*javax*包的不同示例。我们深入研究了这些包的示例并研究了它们的用例。

**我们了解到\*javax包是核心\**java\*包的扩展**。这两个包都可以顺利导入到任何 Java 程序中。