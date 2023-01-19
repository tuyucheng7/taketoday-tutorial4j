[定位 Web 元素](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)一直是自动化脚本开发中最重要的部分。寻找正确、有效和准确的定位器一直是任何自动化测试开发过程的痛点。它迫使 QA工程师超越 id、name、class、link 或tagName作为定位器的考虑。XPath一直是QA 最喜欢的定位器之一，专门用于定位动态元素。[Selenium 中的 XPath](https://www.toolsqa.com/selenium-webdriver/xpath-in-selenium/)提供了各种xpath 函数和 axes(relationships)，有助于编写有效的 XPath的网络元素，并为网络元素定义一个唯一的定位器。在本文中，我们将介绍所有这些函数和轴的详细信息，通过涵盖以下主题下的详细信息，我们可以使用它们非常有效且独特地选择 Web 元素的XPath ：

-   Selenium 中的 XPath 函数是什么？
    -   XPath Contains() 函数
    -   XPath Starts-with() 函数
    -   XPath Text() 函数
    -   AND & OR 运算符
-   Selenium 中的 XPath 轴是什么？
    -   祖先轴
    -   子轴
    -   后代轴
    -   父轴
    -   跟随轴
    -   跟随兄弟轴
    -   前轴
-   XPath 示例：在 Selenium 中使用 XPath 函数和轴

## Selenium 中的 XPath 函数是什么？

有时在动态 Web 环境中工作时，使用名称、类等一般属性来定位特定 Web 元素变得具有挑战性。几个不同的元素可能具有相似的属性，例如，相似的名称或类名。即使是我们在[上一章](https://www.toolsqa.com/selenium-webdriver/xpath-in-selenium/)讨论的简单XPath策略也可能不是很有效，因为在这种情况下，一个简单的 XPath 可能会返回多个元素。为了克服这种情况，Selenium中的 XPath提供了XPath 函数，可以编写有效的 XPath来唯一标识元素。让我们了解一下 XPath在 Selenium 中提供了哪些不同的功能，这有助于唯一定位网络元素：

### Xpath Contains() 函数

XPath Contains()是创建XPath表达式时使用的方法之一。如果任何属性的部分值动态变化，我们可以使用它它可以通过使用其部分值来识别任何属性。使用 XPath contains()方法的语法是：

```java
//tag_name[contains(@attribute,'value_of_attribute')]
```

其中contains()方法接受两个参数：

-   需要验证以定位 Web 元素的标记的属性。
-   属性应包含的属性的部分值。

[让我们看一下“https://demoqa.com/text-box”](https://demoqa.com/text-box)页面上的示例，我们将尝试使用部分属性值来查找元素。在这里，我们试图找到Full Name的文本框，如下所示：

![Selenium 中的 XPath contains() 函数](https://www.toolsqa.com/gallery/selnium%20webdriver/1.XPath%20contains()%20function%20in%20Selenium.png)

让我们看看元素的DOM。

```java
<input autocomplete="off" placeholder="Full Name" type="text" id="userName" class=" mr-sm-2 form-control">
```

在这里，我们有一个值为“username”的“id”属性。我们可以使用该值的一部分并将其与contains()一起使用来标识元素，而不是使用完整的值“username” 。

因此，可以定位元素的XPath将是：

```java
//input[contains(@id, "userN")]
```

这里我们使用“userN”作为部分值。我们可以使用似乎合适的属性值的任何部分。

同样，我们也可以编写Email文本框的XPath 。这个元素的DOM是：

```java
<input autocomplete="off" placeholder="name@example.com" type="email" id="userEmail" class="mr-sm-2 form-control">
```

我们可以使用任何属性来标识元素。例如，在这里，让我们将占位符作为标识元素的属性。因此，XPath 将是：

```java
//input[contains(@placeholder, "example")]
```

这样一来，我们就可以通过使用属性的部分验证来定位任何 Web 元素。当 Web 元素的属性在运行时通过保持属性的一部分静态而改变时，这种方法变得非常方便。

### XPath Starts-with() 函数

顾名思义，XPath starts-with()函数查找具有以特定给定字符或字符序列开头的属性值的元素。这个函数在处理动态网页时非常有用。想象一个元素，它的属性值随着每次页面加载或页面操作而不断变化。通常，这些动态元素很少有共同的起始字符，后面是随机的动态文本。除了动态属性外，这还可以识别静态元素。

XPath的starts-with()方法的语法是：

```java
//tag_name[starts-with(@attribute,'Part_of_Attribute_value')]
```

其中starts-with()方法接受两个参数：

-   需要验证以定位 Web 元素的标记的属性。
-   属性的部分值，我们期望从属性开始。

让我们看一下“https://demoqa.com/text=box”页面上的示例，我们将在其中考虑元素“全名”并尝试使用 XPath 定位它：

![starts-with() 函数](https://www.toolsqa.com/gallery/selnium%20webdriver/2.starts-with()%20function.png)

Xpath 示例：

如果我们查看 web 元素的DOM，它是：

```java
<input autocomplete="off" placeholder="Full Name" type="text" id="userName" class=" mr-sm-2 form-control">
```

因此，使用starts-with()函数的以下元素的XPath 表达式将是：

```java
//input[starts-with(@placeholder,"Fu")]
```

这里我们使用了“占位符”属性来标识文本框元素。因此，元素的实际占位符值是“Full Name”，但是通过starts-with()函数，我们可以使用开头的几个字符来识别元素。

### XPath Text() 函数

此函数使用网页元素的文本在网页上定位元素。如果你的元素包含文本(例如始终包含静态文本的标签)，则此功能非常有用。

XPath中text()函数的语法 是：

```java
//tag_name[text()='Text of the element']
```

其中text()方法返回由tag_name标识的 Web 元素的文本，并将该值与右侧提供的字符串进行比较。

让我们看一下网页 [“https://demoqa.com/text-box”](https://demoqa.com/text-box)并尝试使用text()函数定位“Email”标签。

![文本() 函数](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Text()%20function.png)

在这里我们可以看到，元素的DOM结构是：

```java
<label class="form-label" id="userEmail-label">Email</label>
```

Xpath 示例：

我们将使用UI中的文本来识别元素。UI上的文本是“Email”，元素标签之间也存在同样的内容，即，`<label> ….</label>`

因此标签元素的XPath 表达式将是：

```java
//label[text()=”Email”]
```

这里我们直接使用网页上的文本来识别元素，而不是使用属性和值来识别相同的元素。

### AND & OR 运算符

“ and”运算符用于组合两个不同的条件或属性，以有效地使用 XPath 识别网页中的任何元素。例如，如果我们有两个属性 a 和 b，我们可以使用“and”运算符将两者结合起来以唯一标识网页上的元素。

使用“and”运算符的语法是：

```java
//tag_name[@name = 'Name value' and @id = ‘ID value’]
```

在这里，我们使用 name 和 id 属性；你可以使用唯一标识元素所需的任何属性。

“或”运算符用于根据“或”运算符分隔的任何条件来定位元素。我们主要根据某些运行时条件使用它。元素的属性可以包含任何值，因此用“或”放置条件将确保元素可以通过这些条件中的任何一个定位。

使用“或”运算符的语法是：

```java
//tag_name[@name = 'Name value' or @id = ‘ID value’]
```

因此，如果任一条件为真，则XPath将成功定位元素。

让我们尝试借助网页“https://demoqa.com/text-box”上的示例来理解相同的内容。

在这里，我们要使用AND 和 OR运算符来识别和定位元素“Full Name”。

![AND 运算符](https://www.toolsqa.com/gallery/selnium%20webdriver/4.AND%20operator.png)

使用 AND 运算符的 XPath：

```java
//input[@placeholder ='Full Name' and @type = 'text']
```

在这里，我们结合了占位符和类型属性来定位元素。这两个条件，即属性值，都必须可用于定位元素。

使用 OR 运算符的 XPath：

```java
//input[@placeholder ='Full Name' or @type = 'text']
```

在此示例中，我们使用了属性占位符和带有“或”运算符的类型。这里占位符或类型属性需要与网页上存在的元素属性相匹配。如果满足任何条件(属性)，则XPath将定位该元素。

现在，可能会出现这样的情况，即使在使用了上述功能之后，用户有时也无法唯一定位到网页元素。在这些情况下，XPath轴作为救援工具出现，并借助其与网页上其他 Web 元素的关系帮助定位 Web 元素。让我们了解一下XPath在Selenium中提供了所有不同类型的轴。

## Selenium 中的 XPath 轴是什么？

我们读到[Selenium](https://www.toolsqa.com/selenium-webdriver/xpath-in-selenium/)中的 XPath 使用绝对路径和相对路径来定位 Web 元素。此外， XML DOM中的所有 Web 元素都通过层次结构相互关联。XPath提供了称为“XPath Axis”的特定属性，这些属性使用各种节点之间的关系来定位DOM 结构中的那些节点。下表显示了其中一些Axis，它们可以使用Selenium 中的 XPath定位网页上的元素。

| 轴         | 描述                                                   |
| ---------------- | ------------------------------------------------------------ |
| 祖先       | 该轴定位当前节点的祖先，包括直到根节点的父节点。             |
| 祖先或自我 | 该轴定位当前节点及其祖先。                                   |
| 属性       | 该轴指定当前节点的属性。                                     |
| 孩子       | 该轴定位当前节点的子节点                                     |
| 后裔       | 该轴定位当前节点的后代，即该节点的子节点直至叶节点。         |
| 后代或自我 | 该轴定位当前节点及其后代。                                   |
| 下列的     | 该轴定位当前节点之后的所有节点。                             |
| 继兄弟     | 该轴定位上下文节点的下方兄弟节点。兄弟姐妹与当前节点处于同一级别并共享其父节点。 |
| 父母       | 该轴定位当前节点的父节点。                                   |
| 前         | 该轴定位当前节点之前的所有节点。                             |
| 自己       | 该轴定位当前节点。                                           |

让我们通过以下部分中的示例更详细地了解其中的一些：

### Xpath 祖先轴

祖先轴在XPath中用于选择当前节点的所有父元素(以及父元素的父元素) 。这在我们可以识别子元素的节点但无法识别其父节点或祖父节点的场景中很有用。对于这些场景，我们可以以子节点为参考点，利用“祖先”轴来识别其父节点。

XPath 中祖先的语法和用法：

```java
//tag[@attribute ='Attribute_Value']//ancestor::parent_node
```

让我们看看下面的例子。假设我们要在网页“https://demoqa.com/text-box”上定位用户窗体(如标记 1 所示)，但由于其动态属性，我们无法直接识别它。但是我们可以识别页面上存在的“全名”标签(由标记 2 显示)。这里我们可以使用祖先轴来标识父节点。

![Selenium 中的 XPath 祖先轴](https://www.toolsqa.com/gallery/selnium%20webdriver/5.XPath%20Ancestor%20axes%20in%20Selenium.png)

Xpath 示例：

全名标签的XPath 表达式是：

```java
//label[text()=”Full Name”]
```

但是我们想要定位`<form>`标签，它是这个元素之上的两个层次结构。让我们编写可以定位表单节点的XPath 表达式。

```java
//label[text()="Full Name"]/ancestor::form
```

一开始我们写XPath定位子节点，然后用祖先轴找表单节点。我们应该记住的一件事是，如果有多个父节点具有相同的标签，那么这个相同的XPath将标识几个不同的元素。

### XPath 子轴

XPath中的Child Axis用于查找当前节点的所有子节点。与祖先轴相反，如果我们能够定位所需节点的父元素，则子节点对于定位元素非常有帮助。

XPath 中子轴的语法和用法是：

```java
//tag[@attribute ='Attribute_Value']//child::child_node
```

让我们看看前面的例子；假设我们可以找到“form”元素但找不到“Full Name”标签。

![Selenium 中的 XPath 子项](https://www.toolsqa.com/gallery/selnium%20webdriver/6.XPath%20Child%20in%20Selenium.png)

Xpath 示例：

表单 节点的XPath 表达式为：

```java
//form[@id='userForm']
```

但是我们想要定位`<label>`标签，它是这个元素下面的两个层次结构。让我们编写可以定位标签节点的XPath 表达式。

```java
//form[@id='userForm']/child::div[1]//label
```

在这里我们可以看到这个XPath包括两部分，第一部分在“child”之前，第二部分在 child 之后。两部分都必须采用正确的格式。在这里，我们首先使用'id'定位了用户表单。然后我们使用“child”轴导航到子节点。这里我们使用了索引“[1]”，因为我们想从第一个“div”开始，如果我们想从序列中的第二个 div 开始，我们可以将其索引为“[2]”。我们还使用了双斜杠，因为我们想通过使用Relative XPath转义下一个 div 以直接移动到label 标签。

### XPath 后代轴

Descendant Axis用于访问当前节点的所有子节点和子节点。它类似于child轴，但descendant和child的根本区别是；descendant 仅标识同一节点的子节点和子节点。

XPath 中后代轴的语法和用法：

```java
//node[attribute='value of attribute']//descendant::attribute
```

让我们看一下网页[“https://demoqa.com/radio-button”上的一个例子：](https://demoqa.com/radio-button)

![Selenium 中的 XPath 后裔](https://www.toolsqa.com/gallery/selnium%20webdriver/7.XPath%20Decedent%20in%20Selenium.png)

在上图中，我们可以看到父节点，即div包含两个不同的后代节点或子节点。第一个是由input标签定义的单选按钮，第二个是带有单选按钮标签的标签标签。

Xpath 示例：假设我们可以识别父标签，但需要定位单选按钮。我们可以通过使用后代轴创建以下XPath 来完成此操作。

```java
//div[@class= 'custom-control custom-radio custom-control-inline']/descendant::input
```

因此，此Xpath将首先搜索具有给定类的节点“div”，然后使用后代轴搜索输入标签。

### XPath 父轴

父轴类似于祖先轴。它识别当前节点的直接父节点。父母和祖先之间的显着区别是；ancestor将识别所有上层节点，而 parent axis 将仅定位直接父节点。

XPath 中父轴的语法和用法：

```java
//tag[@attribute ='Attribute_Value']//ancestor::parent_node
```

让我们以上一节中使用的元素为例：

![Selenium 中的 XPath 父级](https://www.toolsqa.com/gallery/selnium%20webdriver/8.XPath%20Parent%20in%20Selenium.png)

Xpath 示例：这里我们有一个可以识别的输入元素。但是假设我们想要定位这个节点的父元素。为此，我们将使用带有XPath的父轴。因此，上述元素的XPath 表达式将变为：

//输入[@id='yesRadio']/parent::div

如果我们在这里使用 ancestor，则XPath将返回所有上层节点，但由于我们使用了parent，它将返回当前标识节点的直接父节点。

### XPath 跟随轴

跟随轴定位当前节点之后的元素。它将当前节点标记为基节点，并在当前节点之后找到DOM中存在的所需元素 。

XPath 中以下轴的语法和用法：

```java
//node[attribute='value of attribute']//following::attribute
```

让我们看一下网页[“https://demoqa.com/text-box”上的以下示例：](https://demoqa.com/text-box)

![Selenium 中的 XPath 跟随轴](https://www.toolsqa.com/gallery/selnium%20webdriver/9.XPath%20following%20axis%20in%20Selenium.png)

Xpath 示例：在上图中，假设我们必须使用id定位全名文本框。尽管如此，我们还希望定位该文本框之后出现的元素，例如， “Current Address”多行文本框或文本区域。因此，当前地址的 XPath 表达式 将是：

```java
//input[@id="userName"]/following::textarea
```

我们应该注意的一件事是，当我们使用跟随轴时，如果 DOM 中存在多个标签，它将识别多个标签。如果给定文本框后有多个“Textarea”节点，则 XPath 将指向所有这些节点。最佳实践是始终创建唯一的 XPath，如果不可能，我们可以使用索引。

### XPath 跟随兄弟轴

Xpath following sibling axis类似于跟随轴。Following-sibling 只识别兄弟节点，即与当前节点处于同一级别的节点。例如，在下图中，两个突出显示的“ div ”都在同一级别，即是兄弟姐妹，而它们正上方的“ div ”是父级。

![Selenium 中的跟随兄弟轴](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Following-Sibling%20axis%20in%20Selenium.jpg)

XPath 中以下同级轴的语法和用法：

```java
//node[attribute='value of attribute']//following-sibling::attribute
```

让我们借助网页[“https://demoqa.com/text-box”上的以下示例来了解详细信息：](https://demoqa.com/text-box)

假设我们已经确定了由元素表示的以下标签“Email” ：`<div>`

![电子邮件 div 元素](https://www.toolsqa.com/gallery/selnium%20webdriver/11.Email%20div%20element.png)

我们需要识别它的兄弟“div”元素，如下所示：

![电子邮件文本框](https://www.toolsqa.com/gallery/selnium%20webdriver/12.Email%20Textbox.png)

Xpath 示例：为了识别这一点，我们可以使用跟随兄弟轴的 XPath。因此，此场景的 XPath 表达式将是：

```java
//div[@class='col-md-3 col-sm-12']/following-sibling::div
```

但是如果同一个节点有多个兄弟节点，那么 XPath 将识别所有这些不同的元素。

### XPath 前导轴

前面的轴在XPath中用于选择当前节点之前存在的所有节点。当我们可以识别上述元素之上的任何元素时，我们会使用它。

XPath 中 Preceding Axis 的语法和用法为：

```java
//node[attribute='value of attribute']//preceding::attribute
```

让我们尝试在以下示例的帮助下了解Preceding Axis的用法：

![XPath 包含()](https://www.toolsqa.com/gallery/selnium%20webdriver/1.XPath%20contains()%20function%20in%20Selenium.png)

在上面的场景中，由于突出显示的文本框包含“id”，我们可以快速定位它，但假设我们需要找到就在当前节点之前的元素的标签。为了处理这些场景，我们使用“前轴”。现在，我们可以编写一个 XPath 表达式来定位标签元素，如下所示：

```java
//input[@id='userName']/preceding::label
```

与我们之前讨论的类似，如果当前元素上方有多个具有相同节点的元素，则 XPath 将返回多个元素。我们始终需要确保编写唯一的 XPath 以进行有效的元素识别。

## XPath 示例：在 Selenium 中使用 XPath 函数和轴

以下代码片段显示了一个示例示例，说明我们如何使用 Selenium 中 XPath 提供的各种函数和轴来唯一地识别和定位 Web 元素：

```java
package TestPackage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class XPathDemo {
	
	public static void main(String args[]) {
		
	System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\chromedriver\\chromedriver.exe");
	WebDriver driver = new ChromeDriver();
	
	driver.get("https://demoqa.com/text-box");
	
	//Using contains() to locate full name and enter data
	driver.findElement(By.xpath("//input[contains(@id, 'userN')]")).sendKeys("User Name");

	//using placeholder
	driver.findElement(By.xpath("//input[contains(@placeholder, 'example')]")).sendKeys("Using Placeholder");
	
	//using start-with() 
	driver.findElement(By.xpath("//input[starts-with(@placeholder,'Fu')]")).sendKeys("Using start with");
	
	//using text() to get label
	String text = driver.findElement(By.xpath("//label[text()='Email']")).getText();
	System.out.println(text);
	
	//using AND operator to locate full name
	driver.findElement(By.xpath("//input[@placeholder ='Full Name' and @type = 'text']")).sendKeys("AND operator");
	
	//using OR operator to locate full name
	driver.findElement(By.xpath("//input[@placeholder ='Full Name' or @type = 'text']")).sendKeys("OR operator");
	
	//using ancestor to locate form tag
	boolean bol =driver.findElement(By.xpath("//label[text()='Full Name']/ancestor::form")).isDisplayed();
	System.out.println("Form is displayed : "+bol);
	
	//using child to locate full name textbox from form
	String label = driver.findElement(By.xpath("//form[@id='userForm']/child::div[1]//label")).getText();
	System.out.println("The label text is : "+ label);
	
	
	//using decendent axis to locate yes radio
	driver.get("https://www.demoqa.com/radio-button");
	driver.findElement(By.xpath("//div[@class= 'custom-control custom-radio custom-control-inline']/descendant::input/following-sibling::label")).click();
	
	//using parent axis to locate yes radio
	boolean bo = driver.findElement(By.xpath("//input[@id='yesRadio']/parent::div")).isSelected();
	System.out.println("The Yes radio is selected : "+bo);
	
	//using following axis to locate current address
	driver.get("https://demoqa.com/text-box");
	driver.findElement(By.xpath("//input[@id=\"userName\"]/following::textarea")).sendKeys("Text Area locate following");
	
	//using following-sibling to locate email 
	driver.findElement(By.xpath("(//div[@class='col-md-3 col-sm-12']/following-sibling::div/input)[2]")).sendKeys("abc@xyz.com");
	
	//using preceding-axis to locate full name
	String preceding = driver.findElement(By.xpath("//input[@id='userName']/preceding::label")).getText();
	System.out.println("The value of preceding : "+preceding);
	}

}
```

当我们运行上面的测试脚本时，它会唯一定位所有提到的网页元素。这样，我们就可以利用 XPath 函数和轴来选择有效的 XPath 来唯一定位网页上的元素。

## 关键要点

-   XPath 甚至可以使用其函数和轴在动态 Web 环境中定位元素，使其成为最流行的 Selenium 定位器技术之一。
-   我们可以使用 Web 元素的 ID、类、名称、子字符串或内部文本来创建 XPath，或者我们甚至可以使用“AND”或“OR”运算符组合使用它们。