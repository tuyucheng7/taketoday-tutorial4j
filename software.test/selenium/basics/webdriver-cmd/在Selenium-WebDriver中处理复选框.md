[复选框](https://en.wikipedia.org/wiki/Checkbox)广泛用于网页上，为用户提供多种选择。但是，当质量工程师使用复选框时，他/她有哪些选择？在本教程中，我们将深入研究如何在 Selenium WebDriver 的复选框上定位和自动执行操作和验证。[HTML](https://en.wikipedia.org/wiki/HTML)页面上的CheckBox提供 了各种独特的属性，可以识别和自动化它们在[Selenium WebDriver 中的行为。](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)在本教程中，我们将了解复选框的复杂性 以及如何使用Selenium WebDriver将其自动化 通过涵盖以下主题下的详细信息：

-   什么是复选框？
-   如何使用 Selenium WebDriver 处理复选框？
    -   如何使用 ID 定位器在 Selenium 中定位和选择复选框？
    -   还有，如何使用 XPath 定位器在 Selenium 中定位和选择复选框？
    -   另外，如何使用 CSS 选择器定位器在 Selenium 中定位和选择复选框？
-   如何使用 Selenium WebDriver 对 CheckBox 执行验证？
    -   如何使用 isSelected() 方法来验证复选框？
    -   以及，如何使用 isDisplayed() 方法来验证复选框？
    -   如何使用 isEnabled() 方法来验证复选框？

## 什么是复选框？

复选框是一个 GUI 元素，允许用户为给定的选项做出某些选择。用户可能会得到一个选择列表，复选框记录了用户所做的选择。该复选框允许用户从给定列表中选择单个或多个选项。

我们可以使用标签在HTML中定义一个复选框。任何 使用[DOM定位 Web 元素的定位](https://en.wikipedia.org/wiki/Document_Object_Model)[器策略](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)都应该使用此标记和属性来识别复选框。`<input type="checkbox">`

除了“检查”和“未检查”之外，有时应用程序提供了三态/中级，当中性答案需要提供一个选项或选择儿童复选框时，我们通常会使用该应用. 基于这些，复选框 通常会有以下状态：

![复选框的状态](https://www.toolsqa.com/gallery/selnium%20webdriver/1.States%20of%20CheckBox.png)

要了解有关复选框的更多信息，让我们考虑页面[“http://www.demoqa.com/automation-practice-form”](https://www.demoqa.com/automation-practice-form)上给出的复选框示例(如下突出显示) 。

![网页上的复选框示例](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Checkboxes%20Example%20on%20a%20web%20page.png)

如果我们查看上面元素的HTML结构，我们会发现它以`<input type="checkbox">` 节点开头。我们在下图中突出显示了复选框的HTML 结构 ：

![DOM 在 HTML 代码中显示复选框](https://www.toolsqa.com/gallery/selnium%20webdriver/3.DOM%20showing%20Checkbox%20in%20HTML%20Code.png)

正如我们所看到的，所有的复选框都是使用HTML 标签`<input>`创建的 ，并且有一个名为“type”的属性，它的值是“checkbox”，表示输入元素的类型是一个复选框。

现在，让我们看看如何 使用Selenium WebDriver在CheckBox上定位和执行特定操作？

## 如何处理 Selenium WebDriver 中的复选框？

我们知道，Selenium 提供了多种[定位器策略，](https://www.toolsqa.com/selenium-webdriver/selenium-locators/) 几乎所有的策略都可以 使用Selenium WebDriver来定位CheckBox 。 让我们了解其中的一些：

### 如何使用 ID 定位器在 Selenium 中定位和选择复选框？

如果复选框有一个包含唯一值的id属性，那么我们可以使用Selenium WebDriver提供的ID 定位器 来定位和选择元素。要选中 复选框，需要执行单击 操作。因此，一旦我们找到元素，我们需要执行单击以选择它。

让我们看看下面的 [ToolsQA 演示页面](https://demoqa.com/automation-practice-form)，以了解 具有id属性的复选框的详细信息：

![使用 ID 属性的复选框选择](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Checkbox%20selection%20using%20ID%20attribute.png)

在上面的DOM 中，我们可以看到input标签有一个id属性。现在，如果我们使用ID 定位器 来识别元素并执行点击 操作，我们将需要使用以下Selenium 代码：

```java
/
 Locating and Clicking a CheckBox By using ID
 /

driver.findElement(By.id("hobbies-checkbox-1")).click();
```

使用上面的代码行，Selenium 将找到“id”为“hobbies-checkbox-1”的网页元素，并对其执行点击操作。上面这行代码的执行会导致网页出现如下状态：

![选中状态的复选框](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Checkbox%20in%20Checked%20State.png)

这样，我们就可以选择一个具有唯一“id”属性的CheckBox，并使用“click”操作选中它。

### 如何使用 XPath 定位器在 Selenium 中定位和选择复选框？

Selenium WebDriver可以使用 XPath 定位器策略定位CheckBox，就像它定位其他 Web 元素一样。

在下图中，我们可以看到DOM中突出显示的复选框的HTML 结构。我们可以使用它来编写XPath并识别/定位元素。

让我们考虑一个示例，我们将尝试使用 XPath 选择多个复选框。我们将同时选中“体育”和“阅读”复选框以展示多个选择场景。

![使用 XPath 选择复选框](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Selecting%20a%20CheckBox%20using%20XPath.png)

一旦我们可以使用XPath唯一标识复选框，我们就可以使用Selenium WebElement执行所需的操作，例如在复选框上“单击” 。使用 Xpath 定位复选框 并选择它的代码如下所示：

```java
//Selecting the first checkbox using XPath
driver.findElement(By.xpath("//label[text()='Sports']")).click();

//Selecting the second checkbox using Xpath
driver.findElement(By.xpath("//label[text()='Reading']")).click();
```

注意：在上面的代码中，我们点击了与复选框关联的标签。通常，可以通过单击复选框本身或与复选框关联的标签来选中/取消选中复选框。

使用上面的代码行，Selenium 将定位具有指定XPath的 Web 元素，并将对其执行单击操作。上面这行代码的执行会导致网页出现如下状态：

![示例：使用 XPath 选择复选框](https://www.toolsqa.com/gallery/selnium%20webdriver/7..Example%20Selecting%20Checkboxes%20using%20XPath.png)

因此，通过这种方式，我们可以使用唯一的XPath选择一个CheckBox ，并使用“单击”操作选择它。

### 如何使用 CSS 选择器定位器在 Selenium 中定位和选择复选框？

正如上一节中所讨论的，有时使用其标识符甚至属性值在网页上定位元素并不容易。最有效的方法是使用CSS 选择器来处理动态元素。

在给定的图像中，我们可以 通过突出显示元素来查看DOM中存在的元素。我们可以使用给定的HTML结构来创建可以识别元素的CSS 表达式。我们将尝试使用CSS 选择器选择所有三个复选框。

![使用 CSS 选择器选择复选框](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Selecting%20Checkboxes%20using%20CSS%20Selector.png)

要定位复选框，我们首先需要为每个元素编写CSS 选择器表达式。然后我们可以在Selenium WebElement中使用这些，然后执行选择或单击操作。

如前所述，可以通过定位并单击输入元素来选择复选框，也可以通过单击与复选框关联的标签来选择复选框。因此，如果我们使用与复选框关联的标签，下面的代码片段将使用CSS 选择器定位元素并单击它们以选择相同的元素：

```java
//Selecting the first checkbox
driver.findElement(By.cssSelector("label[for='hobbies-checkbox-1']")).click();

//Selecting the second checkbox
driver.findElement(By.cssSelector("label[for='hobbies-checkbox-2']")).click();
		
//Selecting the last check box
driver.findElement(By.cssSelector("label[for='hobbies-checkbox-3']")).click();
```

使用上面的代码行，Selenium 将定位具有指定CSS Locator的 Web 元素，并将对其执行点击操作。上面这行代码的执行会导致网页出现如下状态：

![示例：使用 CSS 选择器选择复选框](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Example%20Selecting%20a%20checkbox%20using%20CSS%20Selector.png)

这样，我们可以使用唯一的CSS 定位器选择一个复选框，并使用“单击”操作选中它。

## 如何使用 Selenium WebDriver 对 CheckBox 执行验证？

Selenium WebDriver提供了某些方法，我们可以使用这些方法对CheckBox的状态进行前后验证。这些方法很少是：

-   isSelected()：检查复选框是否被选中。
-   isDisplayed()：检查复选框是否显示在网页上。
-   isEnabled()：检查复选框是否启用

我们可以使用这些方法来验证复选框的当前状态。例如，要验证在单击复选框后，我们是否已选中，我们可以使用“isSelected()”方法。换句话说，它有助于验证复选框的当前状态。同理，在点击一个checkbox之前，我们可以验证checkbox是否显示在页面上，是否启用，然后才点击checkbox。因此，可以使用“isDisplayed()”和“isEnabled()”方法完成此类预验证。

让我们看一个例子来理解我们如何使用所有这些验证。我们将以下面的复选框 为例：

![Selenium Webdriver 中的 isDisplayed 方法](https://www.toolsqa.com/gallery/selnium%20webdriver/10.isDisplayed%20method%20in%20Selenium%20Webdriver.png)

### 如何使用 isSelected() 方法来验证 CheckBox 是否被选中？

我们可以使用isSelected()方法来验证复选框的当前状态，无论我们是否选中它。我们可以在验证前和验证后使用它。例如，我们可以在复选框未被选中时对其进行点击操作；否则，我们可以跳过该操作，如下面的代码片段所示：

```java
/
 Validate Checkbox isSelected method and click
/

WebElement checkBoxElement = driver.findElement(By.cssSelector("label[for='hobbies-checkbox-1']"));
boolean isSelected = checkBoxElement.isSelected();
		
//performing click operation if element is not checked
if(isSelected == false) {
	checkBoxElement.click();
}
```

一旦我们运行这段代码，代码将首先检查是否有复选框。然后if 条件将验证返回值是真还是假。如果它是假的，即复选框将显示为未选中。if 条件中的代码将执行，复选框将被选中。

上述代码的输出将是：

![Selenium Webdriver 中的 isSelected 方法](https://www.toolsqa.com/gallery/selnium%20webdriver/11.isSelected%20method%20in%20Selenium%20Webdriver.png)

这样，我们可以通过首先检查CheckBox 的状态来有条件地选择复选框。

### 如何使用 isDisplayed() 方法来验证 CheckBox 是否显示？

以与上面相同的示例为例，让我们编写一个简单的 selenium 代码来验证给定的复选框是否显示。如果我们可以看到它，那么我们将单击并选择它。

如上所述，该场景的代码片段将是：

```java
/
 Validate Checkbox using isDisplayed() and click
/
WebElement checkBoxElement = driver.findElement(By.cssSelector("label[for='hobbies-checkbox-1']"));
boolean isDisplayed = checkBoxElement.isDisplayed();

// performing click operation if element is displayed
if (isDisplayed == true) {
	checkBoxElement.click();
}
```

上面的测试将验证给定的复选框是否显示在页面上。如果显示，则它将进行选择。当显示指定的复选框时，上面代码的输出将与“isSelected()”的情况相同，并且它将使用单击选项进行检查。

### 如何使用 isEnabled() 方法来验证 CheckBox 是否启用？

假设我们想要勾选一个复选框，但是在运行时，可能会出现它被禁用的场景。为了处理这种情况，Selenium 提供了一个名为“isEnabled()”的方法。顾名思义，此方法验证给定的 Web 元素是否已启用。此方法将根据元素的状态返回布尔值。如果元素处于启用状态，它将返回“true” ，否则返回“false”。

例如，我们首先要检查“体育”复选框是否处于启用状态。然后我们将继续进行其他操作。

![Selenium Webdriver 中的 isEnabled 方法](https://www.toolsqa.com/gallery/selnium%20webdriver/12.isEnabled%20method%20in%20Selenium%20Webdriver.png)

下面的代码片段只有在启用时才会点击复选框：

```java
/
 Validate checkbox using isEnabled() and then click
/

WebElement checkBoxElement = driver.findElement(By.cssSelector("label[for='hobbies-checkbox-1']"));
boolean isEnabled = chckBxEnable.isEnabled();

// performing click operation if element is enabled
if (isEnabled == true) {
	checkBoxElement.click();
}
```

上面的代码将首先检查元素是否处于启用状态；如果启用，它将执行点击操作。如果禁用，它将不会执行任何操作。当复选框启用时，它将单击该复选框并显示输出，如下所示：

![Selenium Webdriver 中的 isEnabled 方法](https://www.toolsqa.com/gallery/selnium%20webdriver/13.isEnabled%20method%20in%20Selenium%20Webdriver.png)

上面的代码将首先检查我们是否启用了该元素；如果是，它将执行点击操作。如果禁用，则不会执行任何操作。

现在让我们尝试编写一个测试，它将执行上面提到的所有操作，并在ToolsQA 演示站点上对单选按钮进行验证：

```java
package TestPackage;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class CheckBoxOperations {

	public static void main(String[] args) {

		String exePath = "C:\\Selenium\\chromedriver\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", exePath);
		WebDriver driver = new ChromeDriver();
		driver.get("https://www.demoqa.com/automation-practice-form");
		driver.manage().window().maximize();


		/
		  Validate isSelected and click
		 /

		WebElement checkBoxSelected = driver.findElement(By.cssSelector("label[for='hobbies-checkbox-1']"));
		boolean isSelected = checkBoxSelected.isSelected();

		// performing click operation if element is not selected 
		if(isSelected == false) {
			checkBoxSelected.click();
		}

		/
		  Validate isDisplayed and click
		 /
		WebElement checkBoxDisplayed = driver.findElement(By.cssSelector("label[for='hobbies-checkbox-1']"));
		boolean isDisplayed = checkBoxDisplayed.isDisplayed();

		// performing click operation if element is displayed
		if (isDisplayed == true) {
			checkBoxDisplayed.click();
		}

		/
		  Validate isEnabled and click
		 /
		WebElement checkBoxEnabled = driver.findElement(By.cssSelector("label[for='hobbies-checkbox-1']"));
		boolean isEnabled = checkBoxEnabled.isEnabled();

		// performing click operation if element is enabled
		if (isEnabled == true) {
			checkBoxEnabled.click();
		}

	}

}
```

通过在你的 eclipse 项目中创建一个新类，可以直接执行此代码片段。在新课程中，你可以复制粘贴所有这些代码来运行。[你还可以从我们的教程“创建 Selenium 测试”](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)中详细了解如何创建和执行 selenium 测试。

## 关键要点

-   复选框由 <input type= "checkbox" >` HTML 标记表示
-   我们可以通过定位器策略来识别 Checkbox 元素，例如 id、XPath、CSS 选择器或任何其他可以唯一标识复选框的 selenium 定位器
-   我们可以通过在输入节点或代表复选框的标签节点上使用 click() 方法来选择一个复选框。
-   Selenium 还提供了验证方法，如 isSelected、isEnabled 和 isDisplayed。我们可以使用这些方法来确保复选框在执行任何操作之前处于正确的状态。