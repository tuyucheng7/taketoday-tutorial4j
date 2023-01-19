作为质量工程师，我们都知道要测试基于 Web 的应用程序，我们需要对HTML元素执行特定的操作(例如单击、键入等) 。现在，当我们实现这些应用程序的自动化时，自动化工具还应该能够对HTML元素执行与人类相同的操作。那么，现在问题来了，这些自动化工具如何识别它们需要在哪个 HTML 元素上执行特定操作？答案是“ Selenium 中的定位器”。

定位器是识别网页上HTML元素的方式，几乎所有的 UI 自动化工具都提供使用定位器来识别网页上HTML元素的能力。顺应同样的趋势，Selenium 也拥有使用“定位器”来识别 HTML 元素的能力，通常被称为“ Selenium Locators ”。Selenium 支持各种定位器。让我们通过涵盖以下主题下的详细信息来深入了解“ Selenium Locators ”的概念：

-   Selenium 中的定位器是什么？
    -   如何在 DOM 中定位 Web 元素？
    -   Selenium 支持哪些定位器？
-   如何使用定位器通过 Selenium 查找 Web 元素？
    -   如何使用 By、ID、Name、Xpath 等在 Selenium 中定位元素
    -   显示所有定位器用法的示例？
-   在 Selenium 中使用定位器的最佳实践

## Selenium 中的定位器是什么？

如上所述，识别网页上正确的 GUI 元素是创建任何成功的自动化脚本的先决条件。这就是定位器发挥作用的地方。定位器是 Selenium 基础设施的重要组成部分之一，它帮助 Selenium 脚本唯一地标识网页中存在的 WebElements (例如文本框、按钮等)。那么，我们如何获得这些定位器的值呢？以及如何在自动化框架中使用它？让我们首先了解整个页面，我们如何识别[DOM](https://www.w3.org/TR/WD-DOM/introduction.html)中的特定 Web 元素，然后我们将尝试获取其定位器：

### 如何在 DOM 中定位 Web 元素？

众所周知，首先要在[DOM](https://www.w3.org/TR/WD-DOM/introduction.html)(文档对象模型)中找到HTML元素，为此我们需要抓取定位器。我们可以按照以下步骤在网络浏览器上识别DOM中的网络元素：

注意：我们展示的是在Google chrome中识别网页元素的步骤。其他浏览器的步骤几乎相同，除了在该浏览器中打开 DOM 的方式不同。我们使用 ToolsQa 演示站点“ [https://demoqa.com/](https://demoqa.com/) ”作为参考。

\1. 可以通过按F12或右键单击网页然后选择检查(如下面的屏幕截图所示)在 Google Chrome 中访问DOM。

![检查](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Inspection.png)

1.  一旦我们点击“ Inspect option ”，就会打开 Developer Tools控制台，如下图。默认情况下，它会打开“元素”选项卡，它代表了网页的完整DOM结构。现在，如果我们将鼠标指针悬停在 DOM 中的HTML标记上，它将突出显示它在网页上代表的相应元素。

![检查元素](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Inspect%20element.png)

\3. 现在，重点是，我们如何在DOM中找到 web 元素。单击“鼠标图标”箭头(如以下屏幕截图中的 Marke 1 所示)，然后选择网页上的 Web 元素。它会自动高亮显示DOM中相应的HTML元素。假设我们要找到与横幅图像对应的HTML元素(如下面标记 3 所示)。当我们选择鼠标点并点击横幅图片时，它会自动高亮显示相应的 HTML 元素，如下图标记 2所示：

![检查一个元素](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Inspecting%20an%20element.png)

那么，这样一来，我们就可以很方便的在DOM中搜索到网页中某个web元素对应的HTML元素了。现在，让我们尝试了解如何为这些 Web 元素获取定位器，Selenium 可以直接使用它来唯一地标识这些 Web 元素：

### Selenium 支持哪些定位器？

有多种类型的定位器，使用它们我们可以在网页上唯一地标识一个网络元素。下图很好地描述了 Selenium 支持的几种类型的定位器。

![定位器](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Locators.png)

为了访问所有这些定位器，Selenium 提供了“ [By](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/By.html#:~:text=Find elements based on the value of the "class" attribute,and "two" will match.) ”类，它有助于在 DOM 中定位元素。它提供了几种不同的方法(其中一些在下图中)，如className、cssSelector、id、linkText、name、partialLinkText、taName 和 xpath 等，它们可以根据相应的定位器策略识别 Web 元素。

你可以通过浏览“ By ”类上所有可见的方法来快速识别所有 Selenium 支持的定位器，如下所示：

![定位器](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Locators.jpg)

正如我们所见，Selenium 支持以下定位器：

-   ClassName – ClassName 运算符使用类属性来标识对象。
-   cssSelector – CSS 用于为网页创建样式规则，可用于识别任何网络元素。
-   Id – 与类类似，我们也可以使用“id”属性来标识元素。
-   linkText – 超链接中使用的文本也可以定位元素
-   name – 名称属性也可以标识一个元素
-   partialLinkText – 链接中的部分文字也可以标识一个元素
-   tagName – 我们也可以使用标签来定位元素
-   xpath – Xpath 是用于查询 XML 文档的语言。同样可以唯一标识任意页面上的web元素。

现在，让我们了解一下 Selenium 框架中所有这些类型定位器的用法：

## 如何使用定位器通过 Selenium 查找 Web 元素？

正如我们上面提到的，Selenium 支持各种类型的定位器。让我们了解所有这些的详细信息和用法：

### 如何使用“id”属性定位网页元素？

“ ID ”作为定位符是识别网页上元素的最常用方法之一。根据[W3C](https://www.w3.org/)，网络元素的 ID 始终需要是唯一的。ID是定位 Web 元素的最快和独特的方法之一，并且被认为是确定元素的最可靠方法之一。但是随着技术的进步和更多的[动态网页](https://en.wikipedia.org/wiki/Dynamic_web_page)，“ ID ”是动态生成的，通常不是定位 Web 元素的可靠方法，因为它们会随着不同的用户而变化。

让我们通过一个示例来了解“ id ”属性在定位 Web 元素时的用法。

假设我们在网页“ [https://demoqa.com/automation-practice-form](https://demoqa.com/automation-practice-form) ”上找到了 First Name 的文本框。当我们在DOM中检查它时，我们看到以下DOM结构：

![元素检查](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Element%20Inspection.png)

正如我们所看到的，“ Input ” HTML 标记具有以下属性和属性：

```java
<input required="" autocomplete="off" placeholder="First Name" type="text" id="firstName" class=" mr-sm-2 form-control">
```

正如我们所见，HTML 标签在 input 标签中包含属性“ id ”。这里使用的id是我们可以用来在网页中定位该元素的“ firstName \”。现在，要在网页上找到“名字”文本框，我们可以使用以下语法：

```java
By.id("firstName");
```

如我们所见，我们使用了“ By.id() ”方法，并且传递了“ firstName ”，这是我们试图定位的文本框的“ id ”。因此，通过这种方式，我们可以找到任何具有与之关联的指定“ id ”属性的 Web 元素。

### 如何使用“名称”属性定位网络元素？

与id属性类似，Selenium 也为用户提供了一种使用name属性来识别元素的方法。但与id相反，一个网页可以有多个具有相同“ name ”属性的元素。所以，如果我们打算使用 name 属性来标识 Web 元素，我们应该始终确保 name 属性必须包含唯一值。否则，它可能会识别同一页面上的几个不同元素，这些元素可能具有相同的名称值。

如果多个元素具有相同的“ name ”属性值，那么，Selenium 将只选择页面中与搜索条件匹配的第一个值。因此，我们总是建议在选择用于定位 Web 元素时确保名称属性的值应该是唯一的。

让我们通过一个示例来了解“ name ”属性在定位 Web 元素时的用法。

例如，假设我们需要在网页“ [https://demoqa.com/automation-practice-form](https://demoqa.com/automation-practice-form) ”的性别部分找到复选框，如下高亮显示：

![单选按钮检查](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Radio%20Button%20Inspection.png)

正如我们所看到的，代表男性复选框的“输入” HTML标记具有以下属性和属性：

```java
<input name="gender" required="" type="radio" id="gender-radio-1" class="custom-control-input" value="Male">
```

我们可以看到属性“ name ”的值为“ gender ”。我们可以使用以下语法来定位使用“ By ”类的 Web 元素。

```java
By.name("gender");
```

正如我们所见，“ By ”类提供了“ name ”方法，它接受web元素的“ name ”属性的值。因此，使用它，我们可以找到具有唯一名称属性的 Web 元素。

### 如何使用“ClassName”属性定位网络元素？

ClassName允许 SeleniumSelenium 根据 DOM 的类值查找 Web 元素。例如，如果我们要识别或对表单元素执行任何操作，我们可以使用类来识别它。

![类属性](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Class%20Attribute.png)

如果我们查看表单的DOM结构，我们可以看到以下代码片段包围了整个表单。

```java
<div class="practice-form-wrapper">
```

我们可以使用上述 DOM 中的类属性值来识别表单。要在网页上识别相同的内容，我们可以使用以下语法。

```java
By.className("practice-form-wrapper");
```

要成功识别它，我们需要确保我们用于定位 Web 元素(即 Web 表单)的类名值是唯一的，并且任何其他类都没有相同的值。如果任何其他类包含与此相同的值，则 Selenium 将在浏览网页时选择最先出现的元素。

### 如何使用“LinkText”和“partialLinkText”属性定位网页元素？

LinkText和partialLinkText在功能上非常相似，都是通过使用超链接文本来定位 Web 元素。我们只能将它们用于包含anchor( `<a>`) 标签的元素。与其他定位器策略类似，如果页面上存在多个具有相同文本的超链接，那么 Selenium 将始终选择第一个。

假设我们需要为“首页”链接点击如下所示的链接：

![链接定位器](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Link%20Locator.png)

正如我们所看到的，锚元素具有以下属性和属性：

```java
<a id="simpleLink" href="https://www.demoqa.com" target="_blank">Home</a>
```

为了识别使用链接文本或部分链接文本的元素，我们需要使用超链接文本，如下所示：

```java
By.linkText("Home");
```

同样，部分链接文本也可以通过使用部分超链接文本来识别元素，如下图：

```java
By.partialLinkText("Ho");
```

它将识别超链接文本中包含“ Ho ”的任何链接。同样，如果多个链接都包含“ Ho ”，那么 Selenium 将选择第一个。

### 如何使用“TagName”定位网页元素？

此定位器类型使用标记名称来标识网页中的元素。标签名称为HTML标签，如input、div、anchor标签、button等。

------

以下语法查找带有tagName的 Web 元素：

```java
By.tagName("a");
```

tagName定位器返回页面中包含指定标记的所有元素。

### 如何使用“CSSselector”定位网页元素？

CSS 或级联样式表被广泛用于设置网页样式，因此可以成为定位各种 Web 元素的有效媒介。如今，大多数网页都是动态设计的。因此，获取唯一的 id、名称或类来定位元素非常具有挑战性。在这种情况下， CSS 选择器可能是一个很好的选择，因为与其他定位器策略相比，它们要快得多。

使用CSS识别网页元素的基本语法如下：

```java
css=(HTML Page)[Attribute=Value]
```

例如，假设我们要使用 CSS 选择器查找以下输入文本框。

![CSS 选择器](https://www.toolsqa.com/gallery/selnium%20webdriver/10.CSS%20Selector.png)

正如我们所见，输入元素具有以下属性和属性：

```java
<input autocomplete="off" placeholder="Full Name" type="text" id="userName" class=" mr-sm-2 form-control">
```

现在，要使用 CSS 选择器查找元素，我们必须使用以下语法：

```java
By.cssSelector("input[id= ‘userName’]");
```

同样，我们可以使用其他属性和标签来定义不同的 CSS 定位器。我们将在另一个教程中讨论有关 CSS 选择器的更多细节。

### 如何使用“xpath”属性定位网页元素？

XPath使用XML表达式来定位网页上的元素。与 CSS 选择器类似，Xpath 在定位网页上的动态元素方面非常有用。Xpath 可以访问网页中存在的任何元素，即使它们具有动态属性。

使用 XPath 定位器策略识别 Web 元素的基本语法如下：

```java
//tag_name[@attribute_value]
```

其中tag_name是标签在DOM结构中的名称，attribute是目标元素的一个属性，可以唯一标识web元素。

例如，假设我们要使用XPath查找以下输入文本框：

![CSS 选择器](https://www.toolsqa.com/gallery/selnium%20webdriver/11.CSS%20Selector.png)

上述示例的 XPath 将是：

```java
//input[@id='userName']
```

因此，使用 XPath 查找元素的示例代码如下：

```java
By.xpath("//input[@id='userName']");
```

这里我们使用了 input标签和 id 属性来标识元素。我们将在另一篇文章中介绍“ XPath 定位器”策略的更深入细节。

现在让我们看看如何使用这些定位器的策略来识别各种网络元素。

### 显示在 Selenium 中使用所有定位器策略的示例：

假设我们要在网页上自动执行特定操作：在“ [https://demoqa.com/automation-practice-form](https://demoqa.com/automation-practice-form) ”上。以下代码片段显示了各种类型的 Selenium 定位器在网页上识别和搜索元素的用法：

```java
package TestPackage;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Selenium {

	public static void main(String[] args) {
		
		 String exePath = "C:\\Selenium\\chromedriver\\chromedriver.exe";
		 System.setProperty("webdriver.chrome.driver", exePath);
		 WebDriver driver = new ChromeDriver();
		 driver.get("https:\\demoqa.com\\");
		 
		 
		 /Locate by ID Attribute
		   URL - https://demoqa.com/automation-practice-form
		  /
		 
		 driver.get("https://demoqa.com/automation-practice-form");
		 driver.findElement(By.id("firstName"));
		
		 /
		    Locate by Name attribute
		    URL - https://demoqa.com/automation-practice-form
		  /
		 
		 driver.get("https://demoqa.com/automation-practice-form");
		 driver.findElement(By.name("gender"));
		 
		 /
		    Locate by className attribute
		    URL - https://demoqa.com/automation-practice-form
		  /
		 
		 driver.get("https://demoqa.com/automation-practice-form");
		 driver.findElement(By.className("practice-form-wrapper"));
		 
		 /
		    Locate by linkText and ParticalLinkText attribute
		    URL - https://demoqa.com/links
		  /
		 
		 driver.get("https://demoqa.com/links");
		 //linkText
		 driver.findElement(By.linkText("Home"));
		 //partialLinkText
		 driver.findElement(By.partialLinkText("Ho"));
		 
		 /
		    Locate by tagName attribute
		    URL - https://demoqa.com/links
		  /
		 
		 driver.get("https://demoqa.com/links");
		 List <WebElement> list = driver.findElements(By.tagName("a"));
		 
		 
		 /
		    Locate by cssSelector attribute
		    URL - https://demoqa.com/text-box
		  /
		 
		 driver.get("https://demoqa.com/text-box");
		 driver.findElement(By.cssSelector("input[id= ‘userName’]"));
		 
		 
		 /
		    Locate by xpath attribute
		    URL - https://demoqa.com/text-box
		  /
		 
		 driver.get("https://demoqa.com/text-box");
		 driver.findElement(By.xpath("//input[@id='userName']"));
		 
		 

	}

}
```

现在，与任何 Web 应用程序的交互都需要Selenium 驱动程序来识别页面上的Web 元素。直到并且除非一个元素被正确识别，否则不可能触发 点击、输入等事件。在任何网络元素上。现在要搜索网页元素，Selenium 提供了两种方法：

-   [findElement](https://www.toolsqa.com/selenium-webdriver/find-element-selenium/)：根据定位器的搜索条件查找并返回单个 Web 元素。
-   [findElements](https://www.toolsqa.com/selenium-webdriver/find-element-selenium/)：根据定位器的搜索条件查找并返回所有网页元素。

这两种方法都接受“ By ”类的对象，然后返回相应的网页元素。我们使用了Selenium WebDriver的findElement 来搜索各种 Web 元素，并通过调用“ By ”类的各种方法传递了不同类型的定位器。

## Selenium 定位器的最佳实践

选择正确的定位器来识别 Web 元素在 Selenium 中非常重要。下面列出了质量工程师需要遵循的一些最佳实践，以便在基于 Selenium WebDriver 的自动化框架中有效地使用定位器。

-   不要使用动态属性值来定位元素，因为它们可能会频繁更改并导致定位器脚本损坏。它还严重影响了自动化脚本的可维护性、可靠性和效率。
-   如果你的网页包含唯一ID和name ， ID和name属性优先于其他定位器，那么始终建议使用它们而不是XPath，因为它们更快、更高效。
-   使用定位器时，请确保你的定位器精确指向所需的元素。如果所需场景需要对单个元素执行某些操作，请确保你的定位器仅与一个元素完全匹配。如果定位器指向多个不同的元素，则可能会导致脚本中断。
-   切勿使用定位器在网页上定位自动生成的元素。有时在动态 Web 环境中，元素属性属性是在运行时生成的。应该避免这些元素，因为它们可能会在脚本执行期间造成破坏。
-   在使用XPath或CSS 定位器时，应避免直接使用Chrome 开发工具生成的定位器。这似乎是生成XPath的最简单方法之一，但从长远来看，它会引发可靠性问题、代码损坏、可维护性问题等。使用这些看起来很诱人，但最好还是在以下位置创建自定义XPath运行时间越长。

## 关键要点

-   Selenium 支持 8 种不同类型的定位器，即 id、name、className、tagName、linkText、partialLinkText、CSS 选择器和 xpath。
-   使用 id 是最可靠和快速的元素识别方法之一。通常，id 在给定的网页上始终是唯一的。
-   CSS 选择器和 XPath 可以识别网页上的动态元素。不同属性和标签名称的组合可以与 CSS 和 xpath 一起使用来标识任何给定的元素。

最后，我们现在了解了在 Selenium 中使用不同定位器的各种方式。在下一篇文章中，我们将深入探讨用于在网页上定位 Web 元素的 CSS 选择器。