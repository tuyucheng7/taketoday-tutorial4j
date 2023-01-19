我们知道网页由许多WebElements组成，例如文本框、按钮、列表等。我们可以使用[Selenium](https://www.selenium.dev/documentation/en/)命令对这些WebElements执行各种操作，例如搜索元素、将事件与 Web 元素相关联等。要执行这些操作我们首先需要与网页进行交互，以便我们可以使用WebElement 命令/操作。在本主题中，我们将讨论使用 Selenium 在网页上查找元素的不同方法，以便我们可以对这些元素执行操作。我们将在本文中介绍以下主题。

-   使用 Selenium WebDriver 查找元素？
-   为什么我们需要在 Selenium 中找到一个 web 元素？
-   如何在 Selenium 中查找元素？
-   Selenium 中的 By class 是什么？
-   在 Selenium 中查找元素和查找元素之间的区别。

## 使用 Selenium WebDriver 查找元素？

如上所述，要与WebElements 进行交互，我们首先必须在网页上找到或定位这些元素。我们可以通过指定元素的Id或元素的类名等属性来查找网页上的元素。这些我们可以在网页上找到元素的替代方法称为[定位器策略。](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)

以下是我们在定位元素时可以使用的定位器策略。

| 定位器       | 描述                                                   |
| ------------------ | ------------------------------------------------------------ |
| ID           | 通过 ID 属性查找元素。给定的搜索值应与 ID 属性相匹配。       |
| 姓名         | 根据 NAME 属性查找或定位元素。name 属性用于匹配搜索值。      |
| 班级名称     | 查找与指定的类名匹配的元素。请注意，不允许将复合类作为策略名称。 |
| 标签名       | 查找或定位标签名称与搜索值匹配的元素。                       |
| CSS 选择器   | 匹配 CSS 选择器以查找元素。                                  |
| 路径         | 将 XPath 表达式与搜索值匹配，并基于该元素所在的位置。        |
| 链接文字     | 此处要找到其锚元素的可见文本与搜索值相匹配。                 |
| 部分链接文本 | 在这里，我们还将可见文本与搜索值进行匹配并找到锚值。如果我们匹配多个元素，则只会选择第一个条目。 |

现在，在介绍如何使用这些不同类型的定位器来定位元素之前，让我们首先了解为什么需要在Selenium 中查找元素？

### 为什么我们需要在硒中找到一个元素？

我们知道我们主要将 Selenium 用于基于 Web 的应用程序的UI测试。由于我们需要与网页进行自动功能交互，因此我们需要定位网页元素，以便我们可以在网页元素上触发一些JavaScript事件，如点击、选择、输入等，或者在文本字段中添加/更新值。要执行这些活动，重要的是首先在网页上找到元素，然后执行所有这些操作。

例如，假设给定一个网页[“demoqa.com”](https://demoqa.com/)，如下所示。

![demoqa.com 示例网站](https://www.toolsqa.com/gallery/selnium%20webdriver/1.demoqa.com%20example%20website.png)

现在，假设我们需要对“立即加入”按钮执行一些操作。因此，在为该按钮实现 say click事件的代码之前，我们必须首先在网页上找到该元素。那么，我们将如何找到该元素以便我们可以继续我们的行动呢？

为此，我们将使用Selenium WebDriver提供的两种方法“findElement”和“findElements”。现在让我们继续了解这些方法的细节。

### 如何在 Selenium 中查找元素？

如前所述，Selenium WebDriver提供了两种方法，我们可以使用它们在网页上查找元素或元素列表。这些是：

findElement()：此方法在网页上唯一地找到一个网页元素。

findElements()：此方法在网页上查找网页元素列表。

让我们在以下部分中了解这些方法的用法和详细信息：

#### Selenium 中的 findElement()

Selenium WebDriver的findElement()方法在网页中查找唯一的 Web 元素。

它的语法如下所示：

```java
WebElement elementName = driver.findElement
                  (By.LocatorStrategy("LocatorValue"));
```

如以上语法所示，此命令接受“By”对象作为参数并返回一个WebElement对象。

“ By”是一个定位器或查询对象，并接受我们上面讨论的定位器说明符或策略。因此，如果我们编写“driver.findElement(By.)”行，则Eclipse IntelliSense将提供以下我们可以与By 对象关联的定位器策略。

![班级成员](https://www.toolsqa.com/gallery/selnium%20webdriver/2.By%20class%20members.png)

上面的屏幕截图显示了我们在编写'By'时获得的所有选项。我们将在本章后面的部分解释这些策略中的每一个。

注意：如果没有找到匹配的元素，findElement 命令会抛出 NoSuchElementException。

但是，如果有多个元素符合findElement()方法中提供的条件，会发生什么情况？发生这种情况时，findElement() 方法会返回网页中最靠前的元素。

#### Selenium 中的 findElements()

命令findElements()返回符合指定条件的 Web 元素列表，这与返回唯一元素的findElement()不同。如果没有匹配的元素，则返回一个空列表。

Selenium WebDriver中findElements()命令的一般语法如下：

```java
List<WebElement> elementName = driver.findElements(By.LocatorStrategy("LocatorValue"));
```

与findElement()命令一样，此方法也接受“By”对象作为参数并返回一个WebElement 列表。

让我们考虑一个示例，其中我们需要在[DemoQA 文本框页面](https://demoqa.com/text-box/)上找到标签名称为“input”的元素的数量。检查面板如下所示。

![demoqa.com 找元素](https://www.toolsqa.com/gallery/selnium%20webdriver/3.demoqa.com%20to%20find%20elements.png)

在上面的屏幕截图中，当我们搜索标签名称“input”时，会 返回两个条目(由搜索工具周围的红色矩形显示，表示 1/2)。

以下程序显示了findElements()方法的示例，我们在其中为By 对象提供了 tagName。

```java
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FindElementByTagName {
   public static void main(String[] args) {
	   System.setProperty("webdriver.chrome.driver", "C:/testSelenium/chromedriver.exe");		

	   WebDriver driver = new ChromeDriver();

	   driver.get("https://demoqa.com/text-box/");
		
	   // Find elements using tag name
	   List<WebElement> allInputElements = driver.findElements(By.tagName("input"));
		
	   if(allInputElements.size() != 0) 
	   {
		   System.out.println(allInputElements.size() + " Elements found by TagName as input \n");
			
		   for(WebElement inputElement : allInputElements) 
		   {
			   System.out.println(inputElement.getAttribute("placeholder"));
		   }
	   }
   }
}
```

以下是程序输出。

![findElements 程序输出](https://www.toolsqa.com/gallery/selnium%20webdriver/4.findElements%20program%20output.png)

接下来，让我们了解如何通过findElement()和findElements()命令使用不同的定位器策略。

### Selenium 中的 By class 是什么？

在本节中，我们将了解如何使用By 类以不同的策略使用Selenium WebDriver 的 findElement()和findElements() 。'By'类接受上面解释的各种定位器策略以在网页上查找一个或多个元素。让我们讨论所有的按类定位器策略。

#### 如何在 Selenium 中使用属性“id”查找元素？

使用“id”来查找元素是目前最常用的查找元素的策略。假设如果网页使用动态生成的id，则此策略返回与 id 匹配的第一个 web 元素。

这种策略是首选，因为大多数网页都是通过将 id 与元素相关联来设计的。这是因为使用ID是定位元素的最简单和最快捷的方法，因为它在编写网页时非常简单。id属性的值是一个String类型的参数。

使用By id策略的findElement()命令的一般语法是：

```java
WebElement elm = driver.findElement(By.id("Element_Id"));
```

例如，考虑[DemoQA 文本框页面中的以下元素：](https://demoqa.com/text-box/)

![通过 Id 在 Selenium 中查找元素](https://www.toolsqa.com/gallery/selnium%20webdriver/5.find%20an%20element%20in%20Selenium%20by%20Id.png)

这里我们选择了“提交”按钮(标记为 1)。此元素代码在上面的屏幕截图中标记为 2 。

上述元素对应的findElement()命令：

```java
WebElement element = driver.findElement(By.id("submit"));
// Action can be performed on Button element
element.submit();
```

注意：如果网页中没有任何 Web 元素与 id 属性匹配，则会引发“NoSuchElementException” 。

注意：  UI 开发人员必须确保页面上的 ID 是唯一的。自动生成或动态生成的 ID 通常不是唯一的。

使用“By.id”对象查找元素的完整程序如下所示：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FindElementById {

     public static void main(String[] args) {
		
	System.setProperty("webdriver.chrome.driver", "C:/testSelenium/chromedriver.exe");		
		
	WebDriver driver = new ChromeDriver();
		
	driver.get("https://demoqa.com/text-box/");
        WebElement element = driver.findElement(By.id("submit"));
		
		
	if(element != null) {
	    System.out.println("Element found by ID");
	}
   }
}
```

该程序提供以下输出。

![按 Id 输出程序](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Program%20Output%20By%20Id.png)

上面的程序是一个使用该元素的 id (By.Id)查找该元素的程序。我们提供了一个适当的URL，我们需要从中搜索元素，然后使用参数By.id( "elementID" ) 调用“findElement()”。此调用返回具有指定 ID 的给定元素。

#### 如何在 Selenium 中使用属性“名称”查找元素？

此策略与id相同，只是定位器使用“name”而不是“id”来定位元素。

接受的 NAME属性的值是 String 类型。使用 By Name 策略的findElement()方法的一般语法如下所示。

```java
WebElement elm = driver.findElement(By.name("Element_NAME"));
```

例如，考虑[DemoQAAutomationPracticeForm 页面上的以下元素：](https://demoqa.com/automation-practice-form)

![按名称在 Selenium 中查找元素](https://www.toolsqa.com/gallery/selnium%20webdriver/7.find%20an%20element%20in%20Selenium%20by%20Name.png)

在上面的截图中，我们选择了第一个性别值(标记为 1)。它在DOM中的相应元素 被突出显示(标记为 2)。

上述元素对应的findElement()方法调用是：

```java
WebElement element = driver.findElement(By.name("gender"));
// Action can be performed on Input Text element
element.sendKeys("ToolsQA");
```

作为此方法调用的结果，将返回与给定名称属性值匹配的第一个元素。如果我们找不到匹配项，则会 引发NoSuchElementException 。

提供名称作为策略也是查找元素的有效方法，但如果名称不唯一，则该方法也会受到影响。

例如，考虑以下元素：

![通过多个条目在 Selenium 中查找一个元素](https://www.toolsqa.com/gallery/selnium%20webdriver/8.find%20an%20element%20in%20Selenium%20by%20Name%20more%20than%20one%20entries.png)

在上面的截图中，有两个同名的元素=性别。在这种情况下，findElement()方法返回第一个元素。

以下代码显示了使用名称(By.name)查找元素的程序：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FindElementByName {
     public static void main(String[] args) {
	   System.setProperty("webdriver.chrome.driver", "C:/testSelenium/chromedriver.exe");		
      	   WebDriver driver = new ChromeDriver();

	   driver.get("https://demoqa.com/automation-practice-form");
		
	   WebElement element = driver.findElement (By.name("gender"));
	   if(element != null) {
	 	System.out.println("Element found by Name");
	   }
	}
}
```

该程序提供以下输出。

![按名称程序输出](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Program%20Output%20By%20name.png)

上面的程序使用名称在 Selenium 中找到一个元素。我们提供了我们必须搜索的元素的名称，作为“findElement()”调用中By 对象的参数。

#### 如何在 Selenium 中使用属性“类名”查找元素？

这里“class”属性的值作为定位器传递。此策略主要用于查找使用相似CSS 类的多个元素。

定位器策略“按类名”根据CLASS 属性值查找网页上的元素。该策略接受 String 类型的参数。类名策略的一般语法如下：

```java
List<WebElement> webList = driver.findElements(By.className(<Element_CLASSNAME>)) ;
```

或者

```java
WebElement elm = driver.findElement(By.className(<Element_CLASSNAME>)) ;
```

第一种语法是获取基于类名的匹配元素列表，而第二种语法是只获取一个匹配元素。

如果元素有很多类，则此策略将匹配每个类。

[考虑DemoQAAutomationPracticeForm](https://demoqa.com/automation-practice-form)上的以下元素(提交按钮) ：

![通过类名在 Selenium 中查找元素](https://www.toolsqa.com/gallery/selnium%20webdriver/10.find%20an%20element%20in%20Selenium%20by%20className.png)

查找上面标记的元素对应的命令是：

```java
WebElement parentElement = driver.findElement(By.className("button"));
parentElement.submit();
```

注意：当我们以非唯一 ID 和名称结束时，使用类名策略查找元素会很有帮助。那个时候我们只是采用类名策略并尝试找到元素。当我们使用 Class Name 策略时，一旦 Selenium 找到特定的类，它就会在指定的类中查找 ID。

使用 By.className 查找元素的程序如下：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FindElementByClassName {
	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "C:/testSelenium/chromedriver.exe");		

		WebDriver driver = new ChromeDriver();

		driver.get("https://demoqa.com/automation-practice-form");
		
		WebElement parentElement = driver.findElement (By.className("button"));
		
		if(parentElement != null) {
			System.out.println("Element found by ClassName");
		}
		
	}
}
```

该程序提供以下输出。

![按类名输出程序](https://www.toolsqa.com/gallery/selnium%20webdriver/11.Program%20Output%20By%20className.png)

在这个程序中，我们提供了一个类名“button”作为“findElement()”调用中的By object参数。它扫描页面并返回一个带有className = "button"的元素。

#### 如何在 Selenium 中使用属性“HTML 标签名称”查找元素？

标记名称策略使用HTML标记名称来定位元素。我们很少使用这种方法，只有在我们无法使用任何其他策略找到元素时才使用它。

TAG属性的值是一个String类型的参数。使用此策略的findElement()方法的语法如下所示。

```java
WebElement elem =&nbsp; driver.findElement(By.tagName(“Element_TAGNAME”));
```

如前所述，请注意此策略不是很流行，我们仅在没有其他选择来定位元素时才使用它。

例如，考虑[DemoQAAutomationPracticeForm 上的以下元素：](https://demoqa.com/automation-practice-form)

![通过 tagName 在 Selenium 中查找元素](https://www.toolsqa.com/gallery/selnium%20webdriver/12.find%20an%20element%20in%20Selenium%20by%20tagName.png)

上述元素(输入标签)对应的命令如下：

```java
WebElement element = driver.findElement(By.tagName("input"));
```

以下是使用By.tagName对象查找元素的程序。

```java
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FindElementByTagName {

     public static void main(String[] args) {
	  System.setProperty("webdriver.chrome.driver", "C:/testSelenium/chromedriver.exe");		

	  WebDriver driver = new ChromeDriver();
          driver.get("https://demoqa.com/automation-practice-form");
	  WebElement element = driver.findElement (By.tagName("input"));
	  if(element != null) {
	       System.out.println("Element found by tagName");
	  }
     }
}
```

该程序的输出如下所示。

![按 tagName 的程序输出](https://www.toolsqa.com/gallery/selnium%20webdriver/13.Program%20Output%20By%20tagName.png)

上面的程序在'findElement()'调用中使用By.tagName对象来查找基于tagName = "input"的元素。

#### 如何使用 Selenium 中的“CSS 选择器”查找元素？

我们还可以在查找元素时使用[CSS Selector 策略](https://www.toolsqa.com/selenium-webdriver/css-selectors-in-selenium/)作为By object的参数。由于CSS Selector具有原生浏览器支持，因此有时CSS Selector策略比[XPath](https://www.toolsqa.com/selenium-webdriver/write-effective-xpaths/)策略更快。

[我们将再次从DemoQAAutomationPracticeForm](https://demoqa.com/automation-practice-form)页面中选择一个元素：

![通过 CSS 选择器在 Selenium 中查找元素](https://www.toolsqa.com/gallery/selnium%20webdriver/14.find%20an%20element%20in%20Selenium%20by%20CSS%20selector.png)

上述输入字段的CSS 选择器是#firstName。所以CSS Selector查找元素对应的命令是：

```java
WebElement inputElem = driver.findElement(By.cssSelector("input[id = 'firstName']"));
inputElem.SendKeys("demoQA");
```

以下程序展示了如何使用By.cssSelector构造查找元素。

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FindElementByCssSelector {
	public static void main(String[] args) {
	     System.setProperty("webdriver.chrome.driver", "C:/testSelenium/chromedriver.exe");		
    	     WebDriver driver = new ChromeDriver();
             driver.get("https://demoqa.com/automation-practice-form");
	     WebElement inputElem = driver.findElement (By.cssSelector("input[id = 'firstName']"));
	     if(inputElem != null) {
		 System.out.println("Element found by cssSelector");
	     }
	}
}
```

该程序提供以下输出。

![cssSelector 的程序输出](https://www.toolsqa.com/gallery/selnium%20webdriver/15.Program%20Output%20By%20cssSelector.png)

 上面的程序通过使用By.cssSelector定位器策略使用CSS 选择器为字段“firstNam”找到一个元素。该程序返回一个具有指定CSS 选择器的元素。

#### 如何使用 Selenium 中的“XPath”查找元素？

这种策略是最流行的查找元素的策略。使用这种策略，我们可以浏览HTML或XML文档的结构。

该策略接受一个 String 类型参数，即XPath Expression。使用此策略的一般语法如下所示：

```java
WebElement elem = driver.findElement(By.xpath(“ElementXPathExpression”));
```

使用[XPath](https://www.toolsqa.com/selenium-webdriver/write-effective-xpaths/)，我们可以使用多种方式定位单个元素。它提供了许多不同且简单的方法来定位元素。

让我们以页面[DemoQAAutomationPracticeForm 中的以下元素为例：](https://demoqa.com/automation-practice-form)

![通过 XPath 在 Selenium 中查找元素](https://www.toolsqa.com/gallery/selnium%20webdriver/16.find%20an%20element%20in%20Selenium%20by%20XPath.png)

上述按钮元素的XPath是[@id="submit"]。请参阅[如何](https://www.toolsqa.com/selenium-webdriver/inspect-elements-using-browser-inspector/)使用 Web 检查器检查元素以获取更多信息。所以我们在findElement()命令中使用它，如下所示：

```java
WebElement buttonLogin = driver.findElement(By.xpath("//button[@id = 'submit']"));
```

使用By.XPath查找元素的程序如下：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FindElementByXpath {
      public static void main(String[] args) {
	   System.setProperty("webdriver.chrome.driver", "C:/testSelenium/chromedriver.exe");		
           WebDriver driver = new Chrome
Driver();
   	   driver.get("https://demoqa.com/automation-practice-form");
	   WebElement buttonSubmit = driver.findElement( By.xpath("//button[@id = 'submit']"));
	   if(buttonSubmit != null) {
		System.out.println("Element found by xpath");
	   }
	}
}
```

该程序显示以下输出。

![XPath 的程序输出](https://www.toolsqa.com/gallery/selnium%20webdriver/17.Program%20Output%20By%20XPath.png)

这里我们提供了“提交”按钮的XPath作为By.xpath定位器的参数。该程序返回与指定的XPath 匹配的元素。

#### 如何使用 Selenium 中的“链接文本/部分链接文本”查找元素？

该策略在网页中查找链接。它专门查找具有链接的元素。这意味着我们可以使用此策略来查找具有匹配链接名称或部分链接名称的“a” (链接)标签的元素。

该策略接受LINKTEXT 属性的值作为 String 类型参数。

使用此策略的 findElement 语法如下所示。

```java
WebElement elem = driver.findElement(By.linkText(“Element LinkText”));
```

上面的语法用于使用完整链接文本查找元素。当我们知道在锚( a ) 标签中使用的链接文本时使用。

我们还可以使用部分链接和查找元素。以下是语法：

```java
WebElement elem = driver.findElement(By.partialLinkText(“ElementLinkText”));
```

在这里我们可以提供部分链接名称。

例如，我们可以采用以下元素 ( [DemoQAHomeLink](https://demoqa.com/links) )。我们突出显示了该元素，如下所示：

![通过 linkText partialLinkText 在 Selenium 中找到一个元素](https://www.toolsqa.com/gallery/selnium%20webdriver/18.find%20an%20element%20in%20Selenium%20by%20linkText%20partialLinkText.png)

如果目标文本是链接文本，我们可以使用链接策略。所以对于上面的链接元素，链接和部分链接策略的findElement()命令如下：

```java
WebElement element = driver.findElement(By.linkText("Home"));

//Or can be identified as 
WebElement element = driver.findElement(By.partialLinkText("HomehY");
```

在第一个示例中，我们使用By.linkText策略并提供整个'linkname'。这将查找带有“主页”字样的链接。在第二个示例中，我们使用By.partialLinkText并仅提供一部分'linkname' ('HomehY')。由于这是一个部分链接，它将查找以'HomehY'开头的链接。如上所示，页面上有一个链接'HomehYtil'。所以By.partialLinkText会找到这个链接。

让我们使用 By.linkText/By.partialLinkText 实现一个代码来查找元素。

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FindElementByLinkTextAndPartialLinkText {
	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "C:/testSelenium/chromedriver.exe");		
		WebDriver driver = new ChromeDriver();
		driver.get("https://demoqa.com/links");
		WebElement element = driver.findElement (By.linkText("Home"));
		
		if(element != null) {
			System.out.println("Element found by LinkText");
		}
		
		element= driver.findElement (By.partialLinkText("HomehY");
		
		if(element!= null) {
			System.out.println("Element found by PartialLinkText");
		}
	}
}
```

输出：

![程序输出通过linkTextpartialLinkText](https://www.toolsqa.com/gallery/selnium%20webdriver/19.Program%20Output%20By%20linkTextpartialLinkText.png)

该程序使用By.linkText和By.partialLinkText定位器查找元素。指定By.linkText时，“findElement”匹配指定的整个linkText。当指定By.partialLinkText时，它匹配部分文本。

## 在 Selenium 中查找元素和查找元素之间的区别

让我们讨论一下Selenium WebDriver提供的findElement()和findElements()方法之间的一些区别。

| 查找元素()                                             | 查找元素()                                           |
| ------------------------------------------------------------ | ---------------------------------------------------------- |
| 返回同一定位器找到的所有元素中的第一个 Web 元素。            | 查找并返回网络元素列表。                                   |
| 此方法仅查找一个元素。                                       | 此方法返回与定位器匹配的元素集合。                         |
| 如果没有元素与定位器匹配，则抛出异常“NoSuchElementException”。 | 如果没有找到匹配的元素，则不会抛出异常。只返回一个空列表。 |
| N不需要索引，因为只返回一个元素。                            | 每个 Web 元素都从 0 开始索引。                             |

## 关键要点

我们需要在网页中查找或定位网络元素，以便我们可以对这些元素执行操作。

此外，我们在 Selenium WebDriver 中有两种方法 find Element 和 find Elements 方法，我们可以使用它们来定位元素。

此外，Selenium 中的 findElement() 方法从网页返回一个唯一的 Web 元素。

Selenium 中的 findElements() 方法返回一个网页元素列表

最后，为了定位或查找元素，每个方法都使用定位器策略，该策略通过“By”对象的参数提供。这些策略是按 Id、按名称、按类名、按 XPath、按链接等。