正如我们所知，要使任何 Web 应用程序自动化，[定位 Web 元素](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)是第一步。我们需要一些独特的属性，如id、name、className等来标识HTML元素。有时，由于动态 Web 元素或糟糕的开发实践，没有与Web 元素关联的唯一属性，这时XPath的作用就出现了。众所周知，当网页在浏览器中加载时，它会生成一个[DOM](https://www.w3schools.com/whatis/whatis_htmldom.asp)(文档对象模型)结构。XPath是查询DOM中对象的查询语言. 随后，在本文中，我们将通过涵盖以下主题下的详细信息来了解Selenium 中 XPath 在网页上定位Web 元素的复杂性和详细用法：

-   什么是 XPath？
    -   XPath 的语法。
    -   什么是 HTML/XML DOM？
    -   如何在 Selenium 中使用 XPath 定位 Web 元素？
-   Selenium 中有哪些不同类型的 XPath？
    -   Selenium 中的绝对 XPath 是什么？
    -   还有，Selenium 中的 Relative XPath 是什么？

## 什么是 XPath？

XML Path或通常称为XPath ，是一种用于XML文档的查询语言它由受一些预定义条件约束的路径表达式组成。此外， XPath可以有效地用于识别和定位网页上几乎所有的元素。在 Web 元素的唯一属性不是恒定的动态网页上工作时，它非常有用。此外，它允许我们编写用于定位 Web 元素的XML 文档导航流程。因此，让我们首先了解XPath使用什么语法来定位 Web 元素：

### XPath 的语法

XPath使用DOM查找网页中的任何元素。所以，它的语法也是由DOM attributes 和 tags组成的，如下所示：

```java
XPath = //tag_name[@Attribute_name = “Value of attribute”]
```

![XPath 语法](https://www.toolsqa.com/gallery/selnium%20webdriver/1.XPath%20Syntax.png)

-   XPath 语法一般以“ // ”双斜杠开头。也就是说，它会从标签名定义的当前节点开始。
-   下一部分是tag_name；它表示节点的 HTML 标记名称。
-   随后，节点内出现的任何内容都包含在方括号中。
-   此外，“ @ ”符号选择属性。
-   此外，“ Attribute_name ”是节点的属性名称。
-   并且，“属性值”表示来自节点的属性值。

它是XPath的基本结构。随着我们继续学习本教程，我们将学习更多关于XPath的知识并编写更好的XPath表达式。随后，我们先了解什么是XML DOM，以及网页与XML结构的关系：

### 什么是 HTML/XML DOM？

所有[HTML](https://en.wikipedia.org/wiki/HTML)网页在内部都表示为[XML](https://en.wikipedia.org/wiki/XML#:~:text=Extensible Markup Language (XML) is,free open standards—define XML.)文档。此外，XML 文档具有树状结构，其中我们有不同的标签和属性。例如，如果我们通过右键单击页面“ [https://demoqa.com/](https://demoqa.com/) ”并选择“ Inspect ”选项打开Chrome 开发工具部分，HTML 结构将如下所示：

![XML 文档结构](https://www.toolsqa.com/gallery/selnium%20webdriver/2.XML%20Document%20Structure.png)

它以一个标签开始，该标签`<html>`有两个子节点<`head>`和`<body>`。这里`<body>`又有一个子节点，如`<div>`，它有自己的子节点。此外，你还可以看到第一个`<div>`标签有一个“ id ”属性，它的值为“ app ”。同样， 的子节点`<div>`也有自己的一组属性和值。

我们在这里可以注意到的一件事是，每个打开的节点都通过在标签名称之前使用正斜杠再次关闭，例如`<body>`使用 关闭标签`</body>`。所以，这样一来，我们可以看到所有的HTML页面在内部都表示为一个XML文档，并构成了XML DOM(文档对象模型)。

### 如何在 Selenium 中使用 XPath 定位 Web 元素？

在上一节中，我们了解了XPath 的基本语法。但是你有没有注意到一些特殊的字符，比如双斜杠“//”和“@”可以帮助我们定位和选择所需的节点/元素？除了“ // ”和“ @ ”之外，Selenium 还提供了各种其他语法元素和属性来使用 XPath 定位 Web 元素。其中很少有：

| 语法元素          | 细节                                                   | 例子     | 示例详细信息                                           |
| ----------------------- | ------------------------------------------------------------ | -------------- | ------------------------------------------------------------ |
| 单斜杠“/”         | 它从根中选择节点。换句话说，如果要选择第一个可用节点，则使用此表达式。 | /html          | 它将在文档的开头查找HTML元素。                             |
| 双斜杠“//”        | 它选择 DOM 中与选择匹配的任何元素。此外，它不必是确切的下一个节点，可以出现在 DOM 中的任何位置。 | //输入         | 它将选择存在于DOM中任何位置的输入节点。                    |
| 地址符号“@”       | 它选择节点的特定属性                                   | //@文本        | 它将选择所有具有文本属性的元素。                             |
| 点“.”             | 它选择当前节点。                                       | //h3/。        | 它将给出当前选择的节点，即h3。                         |
| 双点“..”          | 它选择当前节点的父节点。                               | //分区/输入/.. | 它将选择当前节点的父节点。输入当前节点，以便它将选择父节点，即“div”。 |
| 星号 "\ "        | 它选择节点中存在的任何元素                             | //分区/       | 这与"div"的任何子节点相匹配。                          |
| 地址和星号“@\ ”  | 它选择给定节点的任何属性。                             | //分区[@]     | 它匹配包含至少一个任何类型属性的任何div节点。          |
| 管道“\|”          | 此表达式用于选择不同的路径。                           | //分区/h5      | //分区/表单                                                  |



除了上述组件的语法外，Selenium 中的 XPath还提供了一些高级概念。此外，如果定位器的XPath导致多个元素，我们可以在指定位置找到一个 Web元素。这些是谓词。接下来，我们来了解一下如何使用Predicates来定位网页元素？

#### 如何使用谓词定位网页元素？

谓词通过其索引查找特定节点/元素。例如：//div/input[1]。此外，它选择第一个输入元素，它是div元素的子元素。一些最常用的谓词是：

| 谓词               | 细节                                                   | 例子             | 示例详细信息                                           |
| ------------------------ | ------------------------------------------------------------ | ---------------------- | ------------------------------------------------------------ |
| 获取最后一个节点   | 我们可以使用方括号内的函数“ last() ”获取最后一个节点。 | //div/输入[last()]     | 它会给我们最后一个输入节点，它是 div 节点的子节点。          |
| 获取指定位置的节点 | 我们可以通过在方括号内使用“ position() ”从特定位置获取节点。 | //div/输入[位置()='2'] | 它将为我们提供 div 的子节点。换句话说，输入出现在层次结构的第二个位置。 |

让我们借助以下代码片段了解XPath所有这些语法的用法：

```java
package TestPackage;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class xPathDemo {

	public static void main(String args[]) {

		System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\chromedriver\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();

		driver.get("https://demoqa.com/text-box");

		// Single slash “/” to validate image at start of page
		Boolean imgFlag = driver.findElement(By.xpath("/html/body/div/header/a/img")).isDisplayed();
		System.out.println("The image is displayed : " + imgFlag);

		// Double slash “//” to validate image
		Boolean img_Flag = driver.findElement(By.xpath("//img")).isDisplayed();
		System.out.println("The image is displayed (located by //) : " + img_Flag);

		// Address sign “@” full name textbox
		driver.findElement(By.xpath("//input[contains(@id, 'userN')]")).sendKeys("Full Name");

		// Dot “.” - Full name texbox
		driver.findElement(By.xpath("//input[contains(@id, 'userN')]/.")).sendKeys("Full Name");

		// Double dot “..” - Full name label
		String label = driver.findElement(By.xpath("//input[contains(@id, 'userN')]/../../div/label")).getText();
		System.out.println("The label of full text is : " + label);

		// Asterisk “” - Full Name textbox
		driver.findElement(By.xpath("//div[contains(@id, 'userName-wrapper')]/div[2]/")).sendKeys("Full Name");

		// Address and Asterisk “@” - full name text box
		driver.findElement(By.xpath("//input[@= 'userName']")).sendKeys("Full Name");

		// Pipe “|” - to locate both full name and Email label
		List<WebElement> lst = driver.findElements(By.xpath("//label[@= 'userName-label']|//label[@= 'userEmail-label']"));
		
		// Iterating and printing both labels
		for (WebElement e : lst) {
			System.out.println(" The label is : " + e.getText());
		}

		/
		  Opening web table page
		 /

		driver.get("https://www.demoqa.com/webtables");

		// Get the last node - Last val in table
		boolean lstCol = driver.findElement(By.xpath("//div[@class='rt-tr -odd']/div[last()]")).isDisplayed();
		System.out.println("The last table element is displayed : " + lstCol);

		// Get the 2 node - validate 2 position in table
		boolean positionCol = driver.findElement(By.xpath("//div[@class='rt-tr -odd']/div[position()='2']")).isDisplayed();
		System.out.println("The 2nd table element is displayed : " + positionCol);

		driver.quit();
	}

}
```

到这里，我们已经涵盖了Selenium中XPath的所有语法，它可以定位网页上的网页元素。

## Selenium 中有哪些不同类型的 XPath？

Selenium 使用两种策略来使用 XPaths 定位元素：

-   使用绝对 XPath 定位 Web 元素。
-   使用相对 XPath 定位 Web 元素。

让我们在以下部分中了解这两种类型：

### Selenium 中的绝对 XPath 是什么？

绝对 XPath是查找元素的直接方法。此外，它从XML/HTML文档的第一个/根节点开始，一直到一个节点之后的所需节点。为了更好地理解，让我们举个例子，显示页面“ https://demoqa.com/ ”的DOM：

![Selenium 中的绝对 XPath](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Absolute%20XPath%20in%20Selenium.png)

在上面的文档中，如果我们要找到节点的绝对路径`<img>`，那么我们就必须从根节点(`<html>`节点，如上图中箭头高亮处)开始横向。因此，元素的最终绝对XPath将如下所示：

```
/html/body/div/header/a/img
```

让我们看看在Selenium 中使用绝对XPath来定位网页“ https://demoqa.com/ ”的标题图片。

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class XPathDemo {
    public static void main(String[] args) throws InterruptedException{

        System.out.println("Absolute XPath in Selenium");
        WebDriver driver = new ChromeDriver();
        driver.get("https://demoqa.com");

        //Locate the web element using absolute xpath
        WebElement headerImage = driver.findElement(By.xpath("/html/body/div/header/a/img"));

        // Validate that the header image is displayed on the web page
        System.out.println("The image is displayed : " + headerImage.isDisplayed());

        driver.close();
        System.out.println("Execution complete!!");

    }
}
```

当你执行上面的代码片段时，它会显示如下输出：

![使用绝对 XPath 运行 Selenium 测试的控制台输出](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Console%20Output%20of%20running%20Selenium%20tests%20with%20Absolute%20XPath.png)

绝对 XPath的一个主要缺点或限制是，如果网页上的任何元素发生任何变化，那么任何后续元素的XPath都会发生变化。因此，导致尝试定位 Web 元素的测试脚本失败。因此，通常不建议使用绝对XPath来定位 Web 元素。

### Selenium 中的相对 XPath 是什么？

相对 XPath从HTML DOM中的任何节点开始；它不需要从根节点开始。它带有双正斜杠。为了更好的理解，还是拿上面的绝对路径来举例。

![Selenium 中的相对 XPath](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Relative%20XPath%20in%20Selenium.png)

在前面的示例中，我们遵循了从“ `<html>`”开始到“ `<img?`”标记的整个节点层次结构。它允许我们跟踪整个路径并跟踪元素在页面上的确切位置。但是想象一下，一段时间后如果再有一个节点发生变化，之前的绝对XPath将变得多余，所以这就是相对 XPath派上用场的地方。

相对XPath使用页面上特定节点的相对位置。例如，如果我们要为在绝对XPath部分中定义的同一图像元素编写相对XPath ，则不需要编写所有节点。同样，如果我们想为元素编写XPath，我们不需要从标签开始。我们可以直接从标签开始，如下图：`<img>``<html>``<img>`

```
//img[@src = "/images/Toolsqa.jpg"]
```

正如我们所见，相对XPath可以从任何节点开始。它还克服了Absolute XPath的脆弱性。即使前面的任何元素发生更改或删除，也不会影响Relative XPath。

让我们修改上面的代码片段，使用Selenium中的相对XPath定位图像：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class XPathDemo {
    public static void main(String[] args) throws InterruptedException{

        System.out.println("Relative XPath in Selenium");
        WebDriver driver = new ChromeDriver();
        driver.get("https://demoqa.com");

        //Locate the web element using relative XPath
        WebElement headerImage = driver.findElement(By.xpath("//img[@src = '/images/Toolsqa.jpg']"));

        // Validate that the header image is displayed on the web page
        System.out.println("The image is displayed : " + headerImage.isDisplayed());

        driver.close();
        System.out.println("Execution complete!!");

    }
}
```

当你执行上面的代码片段时，它会显示如下输出：

![使用相对 XPath 运行 Selenium 测试的控制台输出](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Console%20Output%20running%20Selenium%20tests%20with%20Relative%20XPath.png)

因此，通过这种方式，我们可以使用Selenium 中的 XPath定位网页上的任何 Web 元素，当我们没有任何 Web 元素的标准属性不可用时，它始终是一种救星定位器策略。

## 关键要点

-   XPath 是一种功能强大的选择器类型，用于在网页上定位元素。
-   此外，XPath 提供了多种语法来定位网页上的 Web 元素。
-   此外，XPath 中的谓词使用 Web 元素的索引来定位特定节点。
-   最后，XPath 的重要类别是绝对和相对 XPath，主要是相对 Xpath 是推荐的定位器策略。