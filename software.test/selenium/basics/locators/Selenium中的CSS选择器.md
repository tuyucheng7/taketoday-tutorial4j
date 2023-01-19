在使用Web 元素时，我们需要对元素执行特定的操作，例如单击、键入等。但在继续执行这些操作之前，我们需要在 网页的HTML DOM中找到元素的确切位置。当我们自动化任何场景时，这意味着使用自动化工具模仿 Web 元素上的手动操作。但在自动化工具执行任何操作之前，它需要定位元素。这正是定位器或选择器进入场景的地方。[Selenium](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)提供了多种方法来使用诸如id、name、className、等。但有时，在动态环境中工作时，不可能使用简单的属性定位器定位 Web 元素。在这种不屈不挠的情况下，Selenium 使用CSS或层叠样式表选择器来拯救。

由于大多数网页的设计都是使用CSS，因此基于 CSS 的定位策略似乎更加可行且高效。随后，我们将深入探讨CSS 选择器的世界，以了解和实现具有以下主题的不同场景。

-   Selenium 中的 CSS 选择器是什么？
    -   为什么我们需要 Selenium 中的 CSS 选择器？
-   如何创建 CSS 选择器？
    -   创建 CSS 选择器有哪些不同的方法？
-   如何组合多个 CSS 选择器？
-   如何使用 CSS 选择器定位动态网页元素？

## Selenium 中的 CSS 选择器是什么？

CSS 选择器是 Selenium 提供的一种定位器策略，用于识别 Web 元素。CSS 选择器主要使用字符序列模式，它根据HTML 结构来识别 Web 元素。使用CSS 选择器定位元素可能看起来比使用id、name、link等属性有点困难但它是定位不具有一致HTML 属性的动态元素的最有效策略之一。让我们首先了解为什么我们需要CSS选择器？

### 为什么我们需要 Selenium 中的 CSS 选择器？

在网页上定位元素可能具有挑战性，尤其是在当前场景中，每个其他网页都是动态编程的，并根据需要动态创建/呈现网络元素。这使得为动态元素找到唯一的静态属性变得非常棘手。大多数时候，这些元素没有一致的属性值。因此，无法直接使用id、name、link、partial link等定位器。

但是如果我们看到，找到正确的 Web 元素是创建任何基于UI的自动化测试脚本的先决条件 ，特别是 Selenium 自动化脚本。这是CSS 选择器来拯救的地方，因为CSS 足够强大，可以识别网页上存在的大多数网络元素。它还可以识别不具有常量属性值的元素，因此成为所有自动化开发人员的首选。此外，由于它更接近代码，它被认为是在网页上定位元素的最快方法之一。现在让我们看看如何从语法上为 Web 元素创建 CSS 选择器。

## 如何创建 CSS 选择器？

CSS 选择器语法与XPath 语法非常相似。它可以在句法上表示如下：

节点[attribute_name = 'attribute_value']

```java
node[attribute_name = ‘attribute_value’]
```

在哪里，

-   node是需要定位的HTML元素的标签名。
-   attribute_name是可以定位元素的属性的名称。
-   attribute_value是属性的值，可以定位元素。

现在让我们看看在 Selenium 中创建 CSS 选择器的不同方法是什么。

### 创建 CSS 选择器有哪些不同的方法？

如下图所示，CSS Selectors可以基于以下策略创建：

![各种类型的 CSS 选择器](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Various%20types%20of%20CSS%20Selectors.png)

让我们了解如何使用上述方法创建 CSS 选择器：

#### 如何使用 ID 属性创建 CSS 选择器？

我们可以使用CSS Selector中的ID来识别和定位 Web 元素。让我们试着用一个例子来理解这一点。在演示页面[“https://demoqa.com/automation-practice-form”](https://demoqa.com/automation-practice-form)上，我们将尝试找到“名字”文本框。正如我们在下面的截图中看到的，如果我们检查元素，我们可以看到元素的HTML标签是“input”，“id”属性的值是“firstName”。

![将 ID 与 CSS 选择器一起使用](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Using%20ID%20with%20CSS%20Selector.png)

因此，如果我们使用相同的数据根据我们之前讨论的语法创建一个CSS 选择器，我们将得到如下的CSS 选择器表达式：

```java
input[id='firstName']
```

或者，我们也可以再次使用不同的语法来编写它。“ # ”符号代表 CSS 选择器中的“ ID ”。所以，我们可以直接在“id”属性的值后面写' # '。它将为我们提供一个简化的CSS 表达式。例如，我们也可以将上面的表达式写成：

```java
input#firstName
```

这是编写 CSS 选择器的另一种方式。首先，我们提供 HTML 标签名称，后跟“ # ”符号(如果我们使用 ID)，然后是HTML标签中id属性的值。

#### 如何使用类属性创建 CSS 选择器？

HTML标签的类属性也可以标识网页上的元素。假设我们举个例子，我们将查看页面[https://demoqa.com/automation-practice-form](https://demoqa.com/automation-practice-form)上的“当前地址” Textarea元素， 如下所示：

![使用类作为 CSS 选择器](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Using%20class%20as%20CSS%20Selector.png)

如果我们查看HTML标记，我们可以看到该元素具有一个类属性，我们可以将其与CSS 选择器一起使用来标识该元素。因此，上述 Web 元素的CSS 选择器 将是：

```java
textarea[class='form-control']
```

为类属性编写 CSS 表达式的另一种方法是使用点 (.) 符号来表示类。它将有助于简化CSS 选择器，如下所示：

```java
textarea.form-control
```

上面的表达式以HTML 标记开头，后跟一个点(.)，然后是类值。使用 class 属性有时会产生多个结果，因为多个元素可能具有相同的类名。因此，确保仅当类属性具有唯一值时才使用它。

#### 如何使用其他属性创建 CSS 选择器？

除了id和class属性之外，元素的HTML标记中存在的所有其他属性也可用于使用CSS 选择器定位 Web 元素。让我们以下面的截图为例，我们可以看到HTML标签 ( "textarea" ) 有几个不同的属性，如占位符、行、列、id 和类。对于这个例子，我们将使用“占位符”属性。(我们可以使用任何独特的属性)。

![使用其他 HTML 属性](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Using%20other%20HTML%20attributes.png)

因此，通过遵循CSS 选择器的通用语法，我们可以快速得出文本区域的表达式，即“Current Address”元素：

```java
textarea[placeholder='Current Address']
```

在上面的表达式中，我们从HTML 标记开始，即textarea，然后我们使用方括号 []，在其中我们提供了属性(此处为 placeholder )及其值。

因此，通过这种方式，我们可以使用HTML元素的任何独特属性来使用CSS 选择器定位这些元素。

## 如何组合多个 CSS 选择器？

如上所述，[CSS 选择器](https://www.w3schools.com/cssref/css_selectors.asp)可用于唯一定位 Web 元素。但有时，使用单一属性可能不会带来预期的结果；在这种情况下，我们结合多个属性来精确定位网页上存在的任何元素。

我们可以将HTML 标记与 ID、Class 和任何其他属性结合起来，以获得 Web 元素的准确位置。例如，假设我们要定位“Current Address” 文本区域元素，如下所示：

![组合多个定位器](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Combining%20multiple%20locators.png)让我们看看如何组合 web 元素的多个属性来为该元素创建一个 CSS 选择器：

### 如何结合 web 元素的 ID 和其他属性来创建 CSS 选择器？

在给定的元素中，HTML结构包含文本区域标记、id 和占位符属性。我们将一起使用这些来创建可以轻松识别该元素的CSS Selector语句。因此，上述元素的CSS Selector元素将是：

```java
textarea#currentAddress[placeholder='Current Address']
```

-   我们从 HTML 标签开始，即 textarea
-   然后我们使用ID的符号，即“#”
-   然后我们提供了 id 属性的值。
-   最后，在方括号内，我们提供了占位符属性及其值。

### 如何结合 Class 和 web 元素的其他属性来创建 CSS 选择器？

使用与上面相同的示例，我们有一个包含文本区域标记、类和占位符属性的HTML结构。随后，我们可以将它们一起使用来创建一个 CSS Selector 来定位 Web 元素，如下所示：

```java
textarea.form-control[placeholder='Current Address']
```

-   我们从 HTML 标签开始，即 textarea
-   然后我们使用类的符号，即'.'。或点
-   然后我们提供了类属性的值。
-   最后，在方括号内，我们提供了占位符属性及其值。

所以通过这种方式，我们可以结合一个HTML元素的各种属性来在Selenium中唯一定位到这个web元素。

## 如何使用 CSS 选择器定位动态网页元素？

随着网络开发新技术的不断发展，多次动态创建网页。因此，当某些 Web 元素仅基于特定条件/操作出现在网页上时，将会出现各种情况，并且没有可用于这些元素的直接定位器。为了定位这样的动态网页元素，Selenium 提供了多种策略，例如：

-   使用父/子层次结构定位元素
-   使用文本字符串定位元素

让我们看看如何使用这些策略在网页上定位动态网页元素：

### 如何使用父/子层次结构定位 Web 元素？

Selenium 提供了使用元素 与其他HTML元素的相关性在HTML DOM中定位元素的能力。它提供了两种策略来定位与其他HTML元素相关的 Web元素：

-   一个元素是另一个元素的直接父/子。
-   一个元素存在于另一个元素的层次结构中。

让我们通过以下示例来了解这两种策略的实现：

#### 当一个元素是另一个元素的直接父/子时如何定位 Web 元素？

CSS 选择器允许你使用父元素的定位器选择一个元素，然后移动到子元素。用于定位子元素的CSS Selector在语法上可以表示如下：

```java
Parent_locator > child_locator
```

让我们看一个例子来更清楚地理解这一点。

![使用 Child 和 Sub 子关系](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Using%20Child%20and%20Sub%20child%20relationship.png)

在上面的例子中，我们有一个包含在括号中的“textarea” HTML标签，它是“div”的子标签。假设这样一个场景，我们无法通过属性识别“textarea” ，但是我们可以识别它的父 HTML 标签，然后我们可以使用它来访问子标签。让我们创建用于定位textarea元素的 CSS 选择器：

```java
div>textarea[placeholder='Current Address']
```

在这里，我们首先使用父定位器，然后是“>”，然后是子定位器。同样，这也可以通过添加另一个“>”后跟另一个定位符来扩展到子子项。

#### 当元素存在于层次结构中时，如何定位 Web 元素？

与子子项类似，我们也可以使用CSS 选择器来选择HTML 标签的第 n 个子项。它在识别列表元素或父元素具有多个具有不一致属性的子元素的场景中非常有用。

定位第 n 个孩子的语法是：

```java
Parent CSS locator > Child HTML tag : nth-of-type(index)
```

使用CSS 选择器选择第 nth-child ，为此，我们将使用以下站点链接：[https://www.demoqa.com/select-menu。](https://www.demoqa.com/select-menu)

![选择第 n 个孩子](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Selecting%20nth%20child.png)

让我们以上面的例子为例；我们将尝试为“ul” HTML标签的子元素找到CSS 选择器，即  “li”说，我们想找到“ul”的第二个子元素，那么相同的CSS 选择器表达式将是：

```java
select#oldSelectMenu>option:nth-of-type(2)
```

在这里，我们从父级CSS 选择器标签开始，然后是“>”，然后是子级的HTML 标签。然后，子HTML 标记后跟 id 符号 -“ : ”，后跟“nth-of-type(index)”，其中括号接受所需元素的索引。

这样，我们就可以在层次结构中定位任何HTML元素。

### 如何使用文本字符串定位网络元素？

与XPath 类似， CSS Selector也允许用户使用部分字符串来定位元素。它使用不同的符号来表示文本中的开始、结束和内容。让我们看一些示例，以详细了解CSS 子字符串。

例如，我们将使用以下元素，即“全名”，如下图所示，用于在页面上定位文本框。

![在 CSS 选择器中使用文本字符串](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Using%20text%20strings%20in%20CSS%20Selectors.png)

#### 如何使用起始文本定位网络元素？

我们可以使用元素的起始文本来定位元素。如果你知道元素属性的起始文本，这将非常有用。我们可以使用属性值的起始字符序列来使用 CSS 选择器定位元素。

表示字符串起始文本的符号是： '^'

在CSS 选择器中使用此符号，定位 Web 元素的表达式将是：

```java
input[id^='userN']
```

在这里，我们使用了id属性。HTML中 id 属性的值为“userName”。在此表达式中，我们使用了表达式的前五个字符。我们可以从一开始就使用任意数量的字符。

#### 如何使用结束文本定位网络元素？

与开始文本类似，我们也可以使用结束文本来识别元素。属性值的结束字符序列可以定位任何网络元素。

表示字符串结束文本的符号是： '$'

在CSS 选择器中使用此符号，定位 Web 元素的表达式将是：

```java
input[id$='ame']
```

在这里，我们再次使用了值为“username”的id属性。这里我们使用了属性值的最后三个字符。属性和值可以根据场景更改。

#### 如何使用包含文本定位网络元素？

除了开始和结束之外，Selenium 中的CSS 选择器还包含 text()。它可以使用属性值中的任何连续字符来定位元素。

表示的符号包含文本：' '

在CSS 选择器中使用相同的符号，上述元素的表达式将是：

```java
input[id='erNa']
```

在这里，我们使用了id属性值的中间字符，即HTML中的“用户名”来定位元素。

注意：始终确保你的 CSS 表达式指向唯一元素。有时选择一个通用的 CSS 选择器可能会指向几个不同的元素。如果发生这种情况，Selenium 将始终识别 CSS 定位器指向的第一个元素。因此，如果它不是必需的元素，自动化脚本将失败。

## 关键要点

-   CSS 选择器是 Selenium 提供的最强大的工具之一，用于识别网页中的元素。它使用网页的级联样式表来定位和识别元素。
-   CSS 选择器更快、更可靠并且需要更少的维护。
-   它可以识别动态网络环境中没有唯一可识别属性值的元素。
-   它还可以结合几种不同的CSS表达式、属性和文本来准确指向需要的元素。