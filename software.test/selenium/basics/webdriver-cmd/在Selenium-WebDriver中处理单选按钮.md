单选按钮是使用[Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)定位和自动化的最易于管理的元素之一。我们可以使用任何[Selenium Locator Strategies](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)轻松找到。但正如我们所知，除了在网页上定位元素外，我们还需要执行一些操作。例如，单击、选择或取消选择。此外，我们还需要验证网页元素的状态，以确保在指定的网页元素上正确执行所需的操作。每个网络元素都有其特性/属性。此外，它指定 Web 元素的特定状态并在使用Selenium WebDriver执行UI 自动化时验证该状态. 这正是“ Selenium Radio Button ”所发生的事情。

它们提供特定的独特特征，帮助它们执行某些操作并进行验证，同时使用Selenium WebDriver将它们自动化。在本文中，我们将了解单选按钮的这些行为，并将介绍使用Selenium WebDriver自动化单选按钮的详细信息。

-   什么是单选按钮？
-   如何使用 Selenium WebDriver 选择单选按钮？
    -   如何使用 ID 定位器定位单选按钮？
    -   另外，如何使用名称定位器定位单选按钮？
    -   如何使用 XPath 定位器定位单选按钮？
    -   还有，如何使用 CSS Selector 定位器定位单选按钮？
-   如何对 Selenium WebDriver 中的单选按钮执行验证？
    -   如何验证是否使用 Selenium isSelected() 方法选择了单选按钮？
    -   另外，如何使用 Selenium isDisplayed 方法验证单选按钮是否显示？
    -   如何使用 Selenium isEnabled() 方法验证是否启用了单选按钮？

## 什么是单选按钮？

单选按钮是一个HTML[元素](https://en.wikipedia.org/wiki/HTML)，它允许用户仅选择一个给定的选项。我们通常组织包含互斥选项的组中的单选按钮集会。也就是说，我们只能从给定的选项中选择一个选项。

HTML中的单选按钮是使用`<input>`标记和属性“ type ”定义的，其值为“ radio ”。因此，任何使用[DOM](https://en.wikipedia.org/wiki/Document_Object_Model)来识别和定位元素的[定位器策略](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)都将使用标签来识别单选按钮。`<input>`

下面以页面为例：[https](https://demoqa.com/radio-button) ://demoqa.com/radio-button (如下图)更详细的了解Radio Buttons的细节：

![HTML 页面上的单选按钮](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Radio%20buttons%20on%20an%20HTML%20Page.png)

如果我们查看上面元素的 HTML 结构，我们会发现它以`<input>`标签开头。我们在下图中突出显示了单选按钮的HTML 结构。

![HTML 代码中的单选按钮](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Radio%20buttons%20in%20HTML%20code.png)

如我们所见，所有单选按钮都是使用HTML标记创建的`<input>`，并具有名为“ type ”的属性，该属性的值为“ radio ”，表示输入元素的类型是单选按钮。

现在，让我们看看如何使用Selenium WebDriver在Radion Buttons上定位和执行特定操作？

## 如何使用 Selenium WebDriver 选择单选按钮？

我们知道，Selenium 提供了多种[定位器策略](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)，几乎所有的策略都可以使用Selenium WebDriver来定位Radio Buttons。不同的定位器处理单选按钮。让我们了解其中的一些：

### 如何使用 ID 定位器定位单选按钮？

如果单选按钮具有包含唯一值的id属性，那么我们可以使用Selenium WebDriver提供的ID 定位器来定位和选择元素。我们可以选择一个单选按钮；单击操作需要执行。因此，一旦我们找到元素，我们需要单击以选择它。

让我们看看下面的[ToolsQA 演示页面](https://demoqa.com/radio-button)，以了解具有id属性的单选按钮的详细信息：

![使用 ID 属性和 HTML 结构的单选按钮选择](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Radio%20Button%20selection%20using%20ID%20attribute%20and%20HTML%20structure.png)

现在，如果我们使用ID 定位器来识别元素并执行点击操作，我们将需要使用以下Selenium 代码：

```java
/
 Locating and Clicking Radio Button By using ID
/

driver.findElement(By.id("yesRadio")).click();
```

使用上面的代码行，Selenium 将定位到“ id ”为“ yesRadio ”的 web 元素，并对其执行点击操作。上面这行代码的执行会导致网页出现如下状态：

![选中状态的单选按钮](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Radio%20Button%20in%20Selected%20State.png)

因此，我们可以选择一个具有唯一“ id ”属性的Radio Button 。并且，通过使用“点击”操作来选择相同的。

### 如何使用名称定位器定位单选按钮？

如果单选按钮在“ name ”属性中包含唯一值，那么我们可以使用Selenium的名称定位器来定位单选按钮。定位到后，我们就可以对单选按钮元素进行点击操作，选中该单选按钮。

让我们看看下面的ToolsQA 演示页面 ，以了解具有唯一名称属性的单选按钮的详细信息：

![使用名称属性和 HTML 结构的单选按钮选择](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Radio%20Button%20selection%20using%20Name%20Attribute%20and%20HTML%20structure.png)

所以，让我们写下简单的 selenium 代码来定位并选择“是”单选按钮。

```java
/
 By using Name
/

driver.findElement(By.name("like")).click();
```

使用上面的代码行，Selenium 将定位到“ name ”为“ like ”的 web 元素，并对其执行点击操作。上面这行代码的执行会导致网页出现如下状态：

![Selenium 一个使用名称属性的单选按钮](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Selenium%20a%20radio%20button%20using%20name%20attribute.png)

因此，通过这种方式，我们可以选择具有“ name ”属性唯一值的Radio Button ，并使用“ click ”操作选择它。

### 如何使用 XPath 定位器定位单选按钮？

与其他 UI 元素类似，我们也可以使用XPath 定位器策略来定位单选按钮。有时，不可能为单选按钮找到唯一的ID或名称；在那种情况下，XPath可以有效地定位元素。

让我们看看下面的[ToolsQA 演示页面](https://demoqa.com/radio-button)，以了解单选按钮的DOM结构的详细信息，我们可以使用它来构造用于定位单选按钮的XPath 。

![使用 XPath 和 HTML 结构的单选按钮选择](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Radio%20Button%20selection%20using%20XPath%20and%20HTML%20structure.png)

一旦我们使用XPath唯一标识元素，我们就可以使用这个Selenium WebElement来执行所需的操作，例如在单选按钮上“单击”。因此，selenium 代码将通过使用XPath定位单选按钮来执行所需的操作：

```java
/
 By using XPath
/
		
driver.findElement(By.xpath("//div/input[@id='yesRadio']")).click();
```

使用上面的代码行，Selenium 将定位具有指定XPath的 Web 元素，并将对其执行单击操作。上面这行代码的执行会导致网页出现如下状态：

![使用 XPath 定位和选择单选按钮](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Locating%20and%20Selecting%20a%20radio%20button%20using%20XPath.png)

因此，我们可以使用唯一的XPath选择一个单选按钮， 然后使用“单击”操作选择它。

### 如何使用 CSS 选择器定位器定位单选按钮？

正如我们在上一节中讨论的那样，有时由于缺少id或name属性，很难在网页上定位元素。要处理此类元素或属性在运行时更改的动态元素，定位元素的最有效方法是使用[CSS 选择器](https://www.toolsqa.com/selenium-webdriver/css-selectors-in-selenium/)。

让我们看看下面的ToolsQA 演示页面，以了解单选按钮的DOM 结构的详细信息，我们可以使用它来构建用于定位单选按钮的CSS 定位器。

![使用 CSS 定位器和 HTML 结构选择单选按钮](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Radio%20Button%20selection%20using%20CSS%20Locator%20and%20HTML%20structure.png)

一旦我们使用CSS Locator唯一地标识了元素，我们就可以使用这个Selenium WebElement来执行所需的操作，例如在单选按钮上“单击”。因此，selenium 代码将通过使用XPath定位单选按钮来执行所需的操作：

```java
/
 By using CSS selector
/
		
driver.findElement(By.cssSelector("input[id='yesRadio']")).click();
```

注意：在定位单选按钮时，请确保定位器指向一个唯一的元素，而不是几个具有相同属性的不同元素。它会导致 Selenium 挑选第一个可用的元素并执行操作，导致损坏并影响代码的可维护性。

使用上面的代码行，Selenium 将定位具有指定CSS Locator的 Web 元素，并将对其执行点击操作。上面这行代码的执行会导致网页出现如下状态：

![使用 CSS 选择器选择单选按钮](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Selecting%20a%20radio%20button%20using%20CSS%20Selector.png)

因此，我们可以使用唯一的CSS 定位器选择一个单选按钮，然后使用“单击”操作选择它。

## 如何使用 Selenium WebDriver 对单选按钮执行验证？

Selenium WebDriver提供了某些方法，可以对Radion Buttons的状态进行前后验证。这些方法很少是：

-   isSelected()：检查单选按钮是否被选中。
-   isDisplayed()：检查单选按钮是否显示在网页上。
-   isEnabled()：检查单选按钮是否启用

我们可以使用这些方法来验证单选按钮的当前状态。例如，要在单击单选按钮后验证它是否被选中，我们可以使用“ isSelected() ”方法。同样，在点击单选按钮之前，我们可以验证单选按钮是否显示在页面上并且处于启用状态。验证后，才单击单选按钮。因此，我们可以使用“ isDisplayed() ”和“ isEnabled() ”方法进行此类预验证。

让我们看一个例子来理解我们如何使用所有这些验证。我们将以下面的单选按钮为例：

![使用 Selenium WebDriver 的单选按钮验证](https://www.toolsqa.com/gallery/selnium%20webdriver/11.Radio%20Buttons%20validations%20using%20Selenium%20WebDriver.png)

### 如何验证是否使用 Selenium isSelected() 方法选择了单选按钮？

我们可以使用isSelected() 方法来验证单选按钮的当前状态，无论它是否被选中。我们可以在验证前和验证后使用它。例如，我们可以在单选按钮还没有被选中时对其进行点击操作。否则，我们可以跳过该操作，如下面的代码片段所示：

```java
/
 Validate Radio button using isSelected() methiod
/

WebElement radioElement = driver.findElement(By.id("impressiveRadio"));
boolean selectState = radioElement.isSelected();
		
//performing click operation only if element is not selected
if(selectState == false) {
	radioElement.click();
}
```

一旦我们运行这段代码，代码将首先检查单选按钮是否被选中。然后if 条件将验证返回值是真还是假。如果它是假的，即没有选择单选按钮。此外，if 条件中的代码将执行，单选按钮将被选中。

上述测试在网页上的输出将是：

![选中的单选按钮(如果尚未选中)](https://www.toolsqa.com/gallery/selnium%20webdriver/12.Selected%20radio%20buttons%20if%20it%20is%20not%20already%20selected.png)

最后，我们可以通过首先检查单选按钮的状态来对单选按钮进行条件选择。

### 如何使用 Selenium isDisplayed() 方法验证单选按钮是否显示？

以上面的例子为例，让我们编写一个简单的 selenium 代码来验证给定的单选按钮是否显示。如果显示，那么我们将单击并选择单选按钮。

如上所述，该场景的代码片段将是：

```java
/
 Validate Radio button using isDisplayed() methiod
/

WebElement radioElement = driver.findElement(By.id("impressiveRadio"));
boolean selectState = radioElement.isDisplayed();
		
//performing click operation only if element is not selected
if(selectState == false) {
	radioElement.click();
}
```

上面的测试将验证给定的单选按钮是否显示在页面上。如果显示，则它将进行选择。上述代码的输出将与“ isSelected() ”的情况相同。因为显示了指定的单选按钮，并且将使用单击选项将其选中。

### 如何使用 Selenium isEnabled() 方法验证是否启用了单选按钮？

假设我们想要选择一个单选按钮，但在运行期间，它可能被禁用。为了处理这种情况，Selenium 提供了一个名为“ isEnabled() ”的方法。此方法验证给定的 Web 元素是否已启用。此方法将根据元素的状态返回布尔值。如果元素已启用，它将返回“ true ”，如果未启用，它将返回“ false ”。

让我们看看下面的例子。在这里，我们将考虑测试场景的第三个单选按钮(禁用)：

![处于禁用状态的单选按钮](https://www.toolsqa.com/gallery/selnium%20webdriver/13.Radio%20buttons%20in%20disabled%20state.png)

在上图中，我们有一个单选按钮“ No ”，它被禁用了。如果我们想要验证这个元素，我们将不得不使用下面的 selenium 代码。

```java
/
 Validate Radio button using isEnabled() methiod
/

WebElement radioElement = driver.findElement(By.id("noRadio"));
boolean selectState = radioElement.isEnabled();
		
//performing click operation only if element is not selected
if(selectState == false) {
	radioElement.click();
}
```

上面的代码将首先检查元素是否处于启用状态。如果启用，那么它将执行点击操作。否则，将不执行任何操作。因此，在这种情况下，单选按钮不会启用。因此，不会对指定的单选按钮执行任何点击操作。

现在让我们尝试编写一个测试，它将执行上面提到的所有操作，并在ToolsQA 演示站点上对单选按钮进行验证：

```java
package TestPackage;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Selenium {

	public static void main(String[] args) {

		System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\chromedriver\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.get("https://demoqa.com/radio-button");
		driver.manage().window().maximize();

		

		/
		  Find radio button using ID, Validate isSelected and then click to select
		 /
		WebElement radioEle = driver.findElement(By.id("yesRadio"));
		boolean select = radioEle.isSelected();
		System.out.print(select);
		// performing click operation if element is not already selected
		if (select == false) {
			radioEle.click();
		}

		/
		  Find radio button using Xpath, Validate isDisplayed and click to select
		 /
		WebElement radioElem = driver.findElement(By.xpath("//div/input[@id='impressiveRadio']"))
		boolean sel = radioEle.isDisplayed();

		// performing click operation if element is displayed
		if (sel == true) {
			radioElem.click();
		}

		/
		  Find radio button using CSS Selector, Validate isEnabled and click to select
		 /
		WebElement radioNo = driver.findElement(By.cssSelector("input[id='noRadio']"));
		boolean selectNo = radioEle.isDisplayed();

		// performing click operation if element is enabled
		if (selectNo == true) {
			radioNo.click();
		}

	}

}
```

通过在你的 eclipse 项目中创建一个新类，可以直接执行此代码片段。在新课程中，你可以复制粘贴所有这些代码来运行。[你还可以从我们的教程“创建 Selenium 测试](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)”中详细了解如何创建和执行 selenium 测试。

## 关键要点

单选按钮是 GUI 元素，允许用户从多个相互排斥的选项中选择一个选项。

-   单选按钮由具有“ type`<input>` ”的HTML 标记表示为“ radio ”
-   此外，我们可以通过 id、名称、XPath 或 CSS 选择器来定位单选按钮元素。
-   此外，我们可以使用 click() 方法来选择单选按钮。
-   Selenium 还提供验证方法，如 isSelected()、isEnabled() 和 isDisplayed()。我们可以在执行任何操作之前使用这些方法来确保单选按钮处于正确的状态。