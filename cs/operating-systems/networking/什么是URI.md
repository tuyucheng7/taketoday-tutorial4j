## 1. 概述

在本教程中，我们将介绍 URI(统一资源标识符)的概念。我们将分析和解释它们的组成部分并讨论它们的用途。此外，我们将描述[URI 如何与统一资源定位符 (URL) 和统一资源名称 (URN) 相关](https://www.baeldung.com/java-url-vs-uri)。

## 2.统一资源标识符(URI)

统一资源标识符 (URI) 是标识超文本资源的字符序列。资源可以是抽象的或物理的，现有的或将来尚未创建的。URI 语法足够灵活，可以涵盖所有这些情况。

### 2.1. URI 一般语法

[通用 URI 的语法将 URI](https://datatracker.ietf.org/doc/html/rfc1630)定义为我们称为方案、权限、路径、查询和片段的一系列组件：

![URI语法图](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/URI_syntax_diagram.svg-1.png)

### 2.2. URI 组件

现在，让我们更详细地介绍这些组件。

该方案是第一个组成部分。它是以字母开头的字符序列，后跟字母、数字、加号 (+)、句点 (.) 或连字符 (-) 的任意组合。在规范形式中，字母是小写的，尽管语法不区分大小写。该方案是强制性的，所以我们不能省略它。

第二个组件，权限，由三部分组成：用户身份验证信息、主机和端口，语法如下：

```
[用户名"@"]主机[":"端口]
```

第三个组成部分是路径。它是由斜杠 ( / )分隔的一系列路径段。

然后是查询，它是一个可选组件，包含一个以问号开头的查询字符串。通常，它由一系列属性值对组成，这些属性值对由分隔符分隔为与符号 ( & ) 或分号 ( ; )。

最后，该片段是一个可选组件，它包含以散列符号开头的辅助资源的标识符。例如，我们可以使用片段来引用网页上的部分标题。

## 3. URI 示例

现在让我们检查一些示例，了解如何将 URI 用作定位符、名称或两者。

### 3.1. 网址示例

让我们看一下以下 URI：

![URI 示例及其组件](https://www.baeldung.com/wp-content/uploads/sites/4/2022/04/URI-example-with-its-cmponents-1.png)

它是统一资源定位符 (URL) 的示例。[这些 URL 构成了 URI 的一个子集，URI](https://www.baeldung.com/java-url-vs-uri)通过其网络位置来标识资源。它们还指定了一种检索资源的机制。例如，网址：

```
http://example.org/wiki/Main_Page
```

指资源/wiki/Main_Page。该资源采用 HTML 格式，可通过超文本传输协议(http:)从名为example.org的网络主机获取。

某些语言(如 Java)需要特殊的[过程来编码/解码 URL](https://www.baeldung.com/java-url-encoding-decoding)。

### 3.2. 骨灰盒示例

另一方面，统一资源名称 (URN) 是全球唯一的 URI 子集。即使资源变得不可用或不复存在，它们也会持续存在。

URN 的一般格式是：

```
urn:<命名空间>:<特定部分>
```

例如，URN：

```
瓮：ISBN：0451450523
```

通过书号 ( isbn )识别书“[最后的独角兽”。](https://www.bookfinder.com/search/?author=&title=&lang=en&isbn=0451450523&new_used=&destination=es&currency=EUR&mode=basic&st=sr&ac=qr)

同样，URN

```
瓮：isan：0000-0000-2CEA-0000-1-0000-0000-Y
```

通过其视听编号 ( isan )来标识 2001 年的电影“[蜘蛛侠”。](https://web.isan.org/public/en/search?isan=0000-0000-2CEA-0000-1-0000-0000-Y)

### 3.3. 其他 URI 方案

两个最著名的方案可能是http和https。

超文本传输协议 (HTTP) 方案http运行在应用层，使用端口号 80 进行通信。它不使用加密，也不需要证书来验证身份。因此，它存在安全问题并且容易受到网络攻击。

相比之下，[安全超文本传输协议(HTTPS)](https://www.baeldung.com/cs/https-urls-encrypted)方案https使用加密-解密，需要证书来验证网站的身份。因此，它是安全的，旨在抵御网络攻击。

还有许多其他方案：电话号码用tel ，电子邮件地址用mailto ，Skype 通话用skype，等等。

## 4. 关于 URI 方案的更多信息

正如我们所说，URI 方案是 URI 中的第一个组件。它允许解析器识别URI 表示的资源类型。此外，该方案指示如何进行URI的语法分析以及适用于它的语义。

在许多情况下，命名机构定义 URI 方案以及描述和解释其 URI 类型的规则。同一机构还定义了与 URI 方案相关的语义以及如何解释它。
[许多 URI 方案已在 Internet 编号分配机构 (IANA](https://en.wikipedia.org/wiki/List_of_URI_schemes) ) 注册，该机构负责协调各种 Internet 标准的元素。但是，并非所有当前使用的方案都在那里注册。

## 5.总结

在本文中，我们解释了 URI、URL 和 URN，以及它们的组成部分。

其中最重要的是 URI 方案，它是 URI 定义中的第一个组成部分。它 允许我们确定处理 URI和访问与其关联的资源的应用程序和规则。 