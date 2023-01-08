## 1. 概述

在本教程中，我们将考虑如何根据 Locale本地化和格式化消息。

我们将同时使用Java的MessageFormat和第三方库ICU。

## 2.本地化用例

当我们的应用程序获得来自世界各地的广泛用户时，我们可能很自然地希望根据用户的喜好显示不同的消息。

第一个也是最重要的方面是用户使用的语言。其他可能包括货币、数字和日期格式。最后但并非最不重要的是文化偏好：一个国家的用户可以接受的东西可能对其他国家的用户来说是无法容忍的。

假设我们有一个电子邮件客户端，我们希望在新邮件到达时显示通知。

此类消息的一个简单示例可能是：

```plaintext
Alice has sent you a message.
```

这对说英语的用户来说很好，但非英语用户可能就不那么高兴了。例如，讲法语的用户更愿意看到这条消息：

```plaintext
Alice vous a envoyé un message.

```

波兰人看到这个会很高兴：

```plaintext
Alice wysłała ci wiadomość.

```

如果我们想要一个格式正确的通知，即使在 Alice 发送的不是一条消息，而是几条消息的情况下呢？

我们可能会试图通过将各个部分连接成一个字符串来解决这个问题，如下所示：

```java
String message = "Alice has sent " + quantity + " messages";

```

当我们需要通知时，情况很容易失控，因为 Alice 和 Bob 都可能发送消息：

```java
Bob has sent two messages.
Bob a envoyé deux messages.
Bob wysłał dwie wiadomości.
```

请注意，在波兰语 ( wysłała与wysłał ) 语言中，动词是如何变化的。它说明了这样一个事实，即平庸的字符串连接很少被本地化消息所接受。

如我们所见，我们遇到两种类型的问题：一种与翻译有关，另一种与格式有关。让我们在以下部分中解决它们。

## 3.消息本地化

我们可以将应用程序的本地化或l10n定义为使应用程序适应用户舒适度的过程。有时，也使用术语 内部化或i18n。

为了本地化应用程序，首先，让我们通过将所有硬编码消息移至我们的资源文件夹中来消除它们：

[![消息本地化](https://www.baeldung.com/wp-content/uploads/2019/05/messages-localization.png)](https://www.baeldung.com/wp-content/uploads/2019/05/messages-localization.png)

每个文件都应包含键值对以及相应语言的消息。例如，文件messages_en.properties应包含以下对：

```plaintext
label=Alice has sent you a message.
```

messages_pl.properties应包含以下对：

```plaintext
label=Alice wysłała ci wiadomość.
```

类似地，其他文件为键标签分配适当的值。现在，为了获取通知的英文版本，我们可以使用ResourceBundle：

```java
ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.UK);
String message = bundle.getString("label");
```

变量消息的值将是“爱丽丝给你发了一条消息”。

Java 的Locale类包含常用语言和国家/地区的快捷方式。

对于波兰语，我们可以这样写：

```java
ResourceBundle bundle
  = ResourceBundle.getBundle("messages", Locale.forLanguageTag("pl-PL"));
String message = bundle.getString("label");
```

让我们提一下，如果我们不提供语言环境，那么系统将使用默认语言环境。[我们可能会在我们的文章“Java8 中的国际化和本地化](https://www.baeldung.com/java-8-localization)”中详细介绍此问题。然后，在可用翻译中，系统将选择与当前活动语言环境最相似的翻译。

将消息放在资源文件中是使应用程序更加用户友好的良好步骤。由于以下原因，它可以更轻松地翻译整个应用程序：

1.  翻译人员不必浏览应用程序来搜索消息
2.  译者可以看到整个短语，这有助于掌握上下文，从而有助于更好地翻译
3.  当新语言的翻译准备就绪时，我们不必重新编译整个应用程序

## 4. 消息格式

即使我们已将消息从代码中移到一个单独的位置，它们仍然包含一些硬编码信息。如果能够以保持语法正确的方式自定义消息中的名称和数字，那就太好了。

我们可以将格式化定义为通过用值替换占位符来呈现字符串模板的过程。

在以下部分中，我们将考虑允许我们格式化消息的两种解决方案。

### 4.1. Java的消息格式

为了格式化字符串，Java在[java.lang.String](https://www.baeldung.com/string/format)[中定义了许多格式化方法](https://www.baeldung.com/string/format)。[但是，我们可以通过java.text.format.MessageFormat](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/MessageFormat.html)获得更多支持 。

为了说明，让我们创建一个模式并将其提供给MessageFormat实例：

```java
String pattern = "On {0, date}, {1} sent you "
  + "{2, choice, 0#no messages|1#a message|2#two messages|2<{2, number, integer} messages}.";
MessageFormat formatter = new MessageFormat(pattern, Locale.UK);

```

模式字符串具有用于三个占位符的槽。

如果我们提供每个值：

```java
String message = formatter.format(new Object[] {date, "Alice", 2});
```

然后 MessageFormat 将填充模板并呈现我们的消息：

```java
On 27-Apr-2019, Alice sent you two messages.
```

### 4.2. 消息格式语法

从上面的例子中，我们看到消息模式：

```java
pattern = "On {...}, {..} sent you {...}.";
```

包含占位符，这些占位符是大括号{...}，带有必需的参数索引和两个可选参数type和style：

```java
{index}
{index, type}
{index, type, style}
```

占位符的索引对应于我们要插入的对象数组中元素的位置。

如果存在，类型和样式可能采用以下值：

| 类型   | 风格                             |
| :----- | -------------------------------- |
| 数字 | 整数、货币、百分比、自定义格式 |
| 日期 | 短、中、长、全、自定义格式     |
| 时间 | 短、中、长、全、自定义格式     |
| 选择 | 自定义格式                     |

类型和样式的名称在很大程度上不言而喻，但我们可以查阅[官方文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/MessageFormat.html)以获取更多详细信息。

不过，让我们仔细看看自定义格式。 

在上面的示例中，我们使用了以下格式表达式：

```java
{2, choice, 0#no messages|1#a message|2#two messages|2<{2, number, integer} messages}
```

通常，选择样式具有由竖线(或竖线)分隔的选项的形式：

[![消息格式语法](https://www.baeldung.com/wp-content/uploads/2019/05/message-format-syntax.png)](https://www.baeldung.com/wp-content/uploads/2019/05/message-format-syntax.png)

在选项中，除了最后一个选项外，匹配值ki和字符串v i由#分隔。请注意，我们可以将其他模式嵌套到字符串v i中，就像我们对最后一个选项所做的那样：

```java
{2, choice, ...|2<{2, number, integer} messages}
```

选择类型是基于数字的类型，因此匹配值k i 有一个自然排序，将数字行分成间隔：

[![选择样式排序](https://www.baeldung.com/wp-content/uploads/2019/05/choice-style-ordering.png)](https://www.baeldung.com/wp-content/uploads/2019/05/choice-style-ordering.png)

如果我们给一个属于区间[ki , k i +1 )的值k (包括左端，不包括右端)，则选择值vi。

让我们更详细地考虑所选样式的范围。为此，我们采用这种模式：

```java
pattern = "You''ve got "
  + "{0, choice, 0#no messages|1#a message|2#two messages|2<{0, number, integer} messages}.";
```

并为其唯一的占位符传递各种值：

| n            | 信息                  |
| :----------- | --------------------- |
| -1, 0, 0.5 | 你没有消息。        |
| 1, 1.5     | 你有一条留言。      |
| 2          | 你有两条消息。      |
| 2.5        | 你收到了 2 条消息。 |
| 5          | 你收到了 5 条消息。 |

### 4.3. 让事情变得更好

所以，我们现在正在格式化我们的消息。但是，消息本身仍然是硬编码的。

从上一节中，我们知道我们应该将字符串模式提取到资源中。为了分离我们的关注点，让我们创建另一组名为formats的资源文件：

[![消息格式](https://www.baeldung.com/wp-content/uploads/2019/05/messages-format.png)](https://www.baeldung.com/wp-content/uploads/2019/05/messages-format.png)

在这些中，我们将创建一个名为label的键，其中包含特定于语言的内容。

例如，在英文版中，我们将输入以下字符串：

```java
label=On {0, date, full} {1} has sent you 
  + {2, choice, 0#nothing|1#a message|2#two messages|2<{2,number,integer} messages}.
```

由于零消息的情况，我们应该稍微修改法语版本：

```java
label={0, date, short}, {1}{2, choice, 0# ne|0<} vous a envoyé 
  + {2, choice, 0#aucun message|1#un message|2#deux messages|2<{2,number,integer} messages}.
```

我们还需要在波兰语和意大利语版本中进行类似的修改。

事实上，波兰语版本还存在另一个问题。根据波兰语(以及许多其他语言)的语法，动词的性别必须与主语一致。我们可以通过使用选择类型来解决这个问题，但让我们考虑另一种解决方案。

### 4.4. ICU 的消息格式

让我们使用International Components for Unicode (ICU) 库。我们已经在[将字符串转换为首字母大写](https://www.baeldung.com/java-string-title-case#icu4j)教程中提到过它。这是一个成熟且广泛使用的解决方案，允许我们为各种语言定制应用程序。

在这里，我们不打算详细探讨它。我们将仅限于我们的玩具应用程序需要的内容。要获得最全面和最新的信息，我们应该查看[ICU 的官方网站](http://site.icu-project.org/)。

在撰写本文时，最新版本的[ICU forJava( ICU4J )](https://search.maven.org/search?q=g:com.ibm.icu AND a:icu4j)是 64.2。像往常一样，为了开始使用它，我们应该将它作为依赖项添加到我们的项目中：

```java
<dependency>
    <groupId>com.ibm.icu</groupId>
    <artifactId>icu4j</artifactId>
    <version>64.2</version>
</dependency>
```

假设我们想要以各种语言和不同数量的消息获得格式正确的通知：

| 否    | 英语                                                | 抛光                                                       |
| :---- | --------------------------------------------------- | ---------------------------------------------------------- |
| 0   | 爱丽丝没有给你发消息。 Bob 没有给你发送消息。     | 爱丽丝还没有给你发任何消息。 鲍勃还没有给你发任何消息。  |
| 1   | 爱丽丝给你发了一条消息。 鲍勃给你发了一条消息。   | 爱丽丝给你发了一条消息。 鲍勃给你发了一条消息。          |
| > 1 | 爱丽丝给你发了 N 条消息。 Bob 给你发了 N 条消息。 | 爱丽丝给你发了 N 条信息。 Bob 给你发了 N 条消息。        |

首先，我们应该在特定于语言环境的资源文件中创建一个模式。

让我们重新使用文件formats.properties并在其中添加具有以下内容的密钥标签 icu ：

```java
label-icu={0} has sent you
  + {2, plural, =0 {no messages} =1 {a message}
  + other {{2, number, integer} messages}}.
```

它包含三个占位符，我们通过传递一个三元素数组来提供它们：

```java
Object[] data = new Object[] { "Alice", "female", 0 }
```

我们看到在英文版中，性别占位符是没有用的，而在波兰文版中：

```java
label-icu={0} {2, plural, =0 {nie} other {}}
+  {1, select, male {wysłał} female {wysłała} other {wysłało}} 
+  ci {2, plural, =0 {żadnych wiadomości} =1 {wiadomość}
+  other {{2, number, integer} wiadomości}}.
```

我们使用它来区分sent/sent/sent。

## 5.总结

在本教程中，我们考虑了如何本地化和格式化我们向应用程序用户展示的消息。