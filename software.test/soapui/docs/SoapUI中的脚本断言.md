在 SoapUI 的测试开发过程中，会出现使用 SoapUI 的测试断言不足以进行所有验证的特定场景。因此，为了处理这种情况，SoapUI 提供了使用Groovy 脚本实现验证的功能。这些验证被称为“ SoapUI 中的脚本断言”。在本文中，我们将通过涵盖以下主题下的详细信息，深入了解SoapUI 中用于 SOAP 服务的脚本断言的实现：

-   SoapUI 中的脚本断言是什么？
-   如何在 SoapUI 中添加脚本断言？
    -   如何检查 SOAP 响应中是否存在字符串？
    -   另外，如何验证 SOAP 响应中不存在字符串？
    -   如何验证 SOAP 响应中节点的值？
    -   另外，如何验证 SOAP 响应的标头？
    -   如何验证 SOAP 响应的响应时间？
    -   如何验证 SOAP 响应中某个范围的值？

## SoapUI 中的脚本断言是什么？

SoapUI 中的脚本断言是最常用的断言技术之一，它有助于非常轻松地实现和管理断言。它将编程控制权交给了测试开发人员，这反过来又使他们更有能力以编程方式控制和验证测试流程。此外，SoapUI 主要使用“ Groovy Script ”来执行脚本断言，但也提供了对“ [JavaScript](https://www.toolsqa.com/javascript/what-is-javascript/) ”的支持。使用脚本实施断言为测试开发人员提供了以下扩展功能：

| 能力   | 描述                                                   |
| ------------ | ------------------------------------------------------------ |
| 前后操作     | 脚本允许在 TestCase 之前和之后执行某些操作的自由。           |
| 处理动态响应 | 脚本提供了处理和验证动态变化的 API 响应的能力。              |
| 自定义断言   | 使用脚本，你可以创建特定于项目的自定义断言，并根据需要使用它们。 |

注意：如上所述，在本文中，我们将仅针对 SOAP 服务介绍 SoapUI 中脚本断言的实现细节，为此，我们将使用[ToolsQA BookStore WSDL](https://bookstore.toolsqa.com/BookStoreService.wsdl)。此外，要创建新的 SOAP 项目，你可以参考文章[SoapUI Projects](https://www.toolsqa.com/soapui/soapui-project/)。

现在让我们看看如何在SOAP Web 服务的响应上实现各种脚本断言：

## 如何在 SoapUI 中添加脚本断言？

要添加脚本断言，只需转到我们要添加断言的SOAP请求，然后单击请求窗口左上角的“ + ”图标，如下所示：

![如何在 SoapUI 中添加脚本断言](https://www.toolsqa.com/gallery/SoapUI/1.how%20to%20add%20script%20assertions%20in%20SoapUI.png)

让我们了解上面突出显示的步骤：

1.  首先，转到你的 SOAP 请求并双击打开请求的步骤。
2.  其次，单击请求窗口左上角或左下角的“+”图标。你可以在上面的代码片段中看到它突出显示。
3.  最后，选择脚本断言并单击“添加”。

现在让我们看看 SOAP 服务响应的几种断言类型，我们可以借助SoapUI 中的脚本断言来实现它们：

### 如何检查 SOAP 响应中是否存在字符串？

假设我们需要验证[BookStore SOAP 服务](https://www.toolsqa.com/soapui/soapui-project/)的响应是否包含一本作者名为“ Richard E. Silverman ”的书。让我们在脚本断言的帮助下验证相同的内容。

我们可以使用以下代码片段来验证服务响应：

```java
import com.eviware.soapui.support.XmlHolder

def holder = new XmlHolder( messageExchange.responseContentAsXml )
def responseXml = holder.getPrettyXml()
assert responseXml.contains('Richard E. Silverman')
```

让我们详细了解一下上面的代码片段：

1.  第一行导入所需的库XmlHolder ，它在SoapUI中解析XML。
2.  然后我们初始化一个XmlHolder对象并将服务的响应作为参数传递给它，如下所示：

```java
def holder = new XmlHolder( messageExchange.responseContentAsXml )
```

messageExchange.responseContentAsXml以XML 格式返回SOAP调用的响应。

\3. 接下来，我们需要将 XML 响应转换为字符串，我们可以在getPrettyXml()的帮助下实现相同的效果，如下所示：

```java
def responseXml = holder.getPrettyXml()
```

\4. 然后我们可以使用 Groovy Script 的assert方法来检查响应中的字符串值是否存在。

```java
assert responseXml.contains('Richard E. Silverman')
```

我们可以将“ assert ”表达式评估为布尔表达式。如果表达式的值返回false，则脚本断言失败，反之亦然。

下图显示了在 SoapUI 窗口中放置和执行上述脚本断言的示例：

![脚本断言 2Scripted 包含 SoapUI 中的断言](https://www.toolsqa.com/gallery/SoapUI/2.Script%20Assertion%202Scripted%20contains%20assertion%20in%20SoapUI.png)

如我们所见，脚本已添加到步骤 2 突出显示的脚本区域中，我们可以使用播放图标(步骤 3 突出显示)来执行它。我们可以看到脚本通过了标记为 1 的步骤突出显示的断言。

### 如何验证 SOAP 响应中不存在字符串？

接下来，如果我们想要验证特定[字符串不应该是](https://www.toolsqa.com/soapui/assertions-in-soapui/)目标服务响应的一部分，我们可以使用“不包含”断言来评估相同的字符串。不包含断言，顾名思义，与包含断言正好相反。它检查是否缺少特定字符串。

假设我们需要验证[BookStore SOAP 服务](https://bookstore.toolsqa.com/BookStoreService.wsdl)的响应是否包含一本作者名为“ Mark Twain ”的书。让我们在脚本断言的帮助下验证相同的内容。

我们可以使用以下代码片段来验证服务响应：

```java
import com.eviware.soapui.support.XmlHolder

def holder = new XmlHolder( messageExchange.responseContentAsXml )
def responseXml = holder.getPrettyXml()
assert !responseXml.contains('Mark Twain')
```

正如我们所看到的，上面的代码还使用包含字符串的方法查找字符串“ Mark Twain ”。我们将发现的唯一区别是在contains()方法之前使用的逻辑运算符 (!) 。只有当响应中不存在预期的字符串时，它才会否定结果并通过断言。

### 如何验证 SOAP 响应中节点的值？

正如我们所知，XPath Match断言允许你使用XPath 表达式从目标响应节点中选择内容并将其与你期望的值进行比较。为了解释如何在脚本断言的帮助下实现此断言的用法，我们将使用[BookStore SOAP 服务](https://bookstore.toolsqa.com/BookStoreService.wsdl)。

假设你要验证BookStore API响应中第二本书的标题不为 null。我们可以使用脚本断言对其进行验证。让我们看看如何实现相同的目标。

在编写断言之前，让我们快速浏览一下[BookStore SOAP 服务](https://bookstore.toolsqa.com/BookStoreService.wsdl)的 XML 响应，以确定书名的 XPath。

![BookStore SOAP 服务的 XML 响应](https://www.toolsqa.com/gallery/SoapUI/3.XML%20response%20of%20BookStore%20SOAP%20service.png)

我们需要编写以下脚本断言来验证 XML 响应中的标题：

```java
import com.eviware.soapui.support.XmlHolder


def holder = new XmlHolder( messageExchange.responseContentAsXml )
def responseXml = holder.getPrettyXml()
def title = holder.getNodeValue('//ns1:BooksResult/ns1:Books/ns1:CustomBookModel[2]/ns1:Title')
assert title.equals('Learning JavaScript Design Patterns')
```

让我们更详细地理解上面的代码片段：

除了使用 Xmlholder 类的其他方法外，我们还使用了“ getNodeValue ”方法来获取特定节点的值，如下所示：

```java
def title = holder.getNodeValue('//ns1:BooksResult/ns1:Books/ns1:CustomBookModel[2]/ns1:Title')
```

在这里，我们提到了第二本书的标题的XPATH。“ ns1 ”指的是 XML 响应中的命名空间。默认情况下，XML 中的第一个命名空间称为“ ns1 ”。如果有第二个命名空间，那么它应该是“ ns2 ”等等。

我们使用“ ns1: CustomBookModel[2] ”来获取第二本书的标题。请注意，列表的第一个元素从索引 1 而不是索引 0 开始。

然后我们使用 equals() 来验证标题的值，如下所示：

```java
title.equals()
```

因此，通过这种方式，我们可以验证SOAP服务的XML响应中任何节点的值。

### 如何验证 SOAP 响应的标头？

与其他断言一样，脚本断言可以断言SoapUI 中的响应标头。让我们添加另一个脚本来验证来自[BookStore SOAP 服务](https://bookstore.toolsqa.com/BookStoreService.wsdl)的标头的响应状态。

在为标头添加断言之前，让我们看一下bookstoreAPI 响应标头，如下所示：

![在 soapUI 中使用脚本的标头断言](https://www.toolsqa.com/gallery/SoapUI/4.Header%20assertions%20using%20script%20in%20soapUI.png)

下面的代码片段验证了标头“ #status# ”，其中包含书店 API 的响应代码。

```java
def status = messageExchange.responseHeaders["#status#"]
assert status.toString().equals('[HTTP/1.1 200 OK]')
```

如我们所见，messageExchange接口的responseHeaders方法获取响应标头。曾经，我们拥有所有标题；我们可以放置任何断言来验证值，如上例所示，我们将其转换为字符串并检查值是否等于“ [HTTP/1.1 200 OK] ”

### 如何验证 SOAP 响应的响应时间？

我们还可以使用脚本断言来验证服务的响应时间。正如我们所知，响应时间是响应服务请求所花费的总时间。让我们看看如何在 SoapUI 中使用脚本来做到这一点：

```java
assert messageExchange.timeTaken < 900
```

messageExchange.timeTaken是用于获取SOAP请求的响应时间的属性。

### 如何验证 SOAP 响应中某个范围的值？

虽然我们可以添加断言来验证值的精确匹配，但也可以应用模糊断言。换句话说，我们可以添加一个断言来检查特定限制之间的值，我们可以借助 SoapUI 中的脚本断言来实现这一点。

假设我们需要检查我们的书的页数是否在 200-300 页的范围内。我们可以使用以下脚本断言来验证它：

```java
import com.eviware.soapui.support.XmlHolder


def holder = new XmlHolder( messageExchange.responseContentAsXml )
def responseXml = holder.getPrettyXml()
def pages = holder.getNodeValue('//ns1:BooksResult/ns1:Books/ns1:CustomBookModel[2]/ns1:Pages')
log.info pages
assert pages.toInteger() > 200 && pages.toInteger() < 300
```

让我们了解我们在这里尝试做的是：

1.  我们使用“ Pages ”的“ getNodeValue ”方法获得了 XML 响应中的所有页面，如下所示：

```java
def pages = holder.getNodeValue('//ns1:BooksResult/ns1:Books/ns1:CustomBookModel[2]/ns1:Pages')
```

1.  然后我们使用“ toInteger ”方法将该值转换为“整数” ，并与值 200 和 300 进行比较，如下所示：

```java
assert pages.toInteger() > 200 && pages.toInteger() < 300
```

因此，我们可以从 XML 响应中检索任何值，并使用 SoapUI 中的脚本断言根据任何预期值对其进行验证。

## 关键要点

-   首先，脚本断言可用于以编程方式对 SOAP 服务响应的任何值进行验证。
-   此外，使用脚本语言进行断言，为测试开发人员提供更多控制和自由，以进行各种断言。
-   此外，脚本中可用的messageExchange对象公开了一堆与最后请求/响应消息相关的属性，可用于获取响应主体和标头。
-   最后，脚本断言可用于验证响应的所有部分，例如正文、标头等。

最后，我们都应该充分理解如何在 SoapUI中为SOAP服务实现脚本断言。现在让我们转到下一篇文章，我们将了解如何在 SoapUI 中为 REST 服务应用脚本断言。