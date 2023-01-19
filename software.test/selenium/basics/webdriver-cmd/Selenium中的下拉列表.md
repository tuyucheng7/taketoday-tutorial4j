网页由各种 Web 元素组成，包括文本字段、按钮、复选框、下拉菜单等。如今，随着网站上表单的广泛使用，我们时不时会遇到DropDowns。现在有多种类型的下拉菜单可用，其中主要分为单选(只允许选择一个值)或多选(允许选择多个值)。 [Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)提供了一个名为“Select”的类，它提供了多种方法来处理下拉菜单，无论是单选还是多选下拉菜单。在本文中，我们将了解“选择”类Selenium WebDriver并 通过涵盖以下主题下的详细信息来了解我们如何处理Selenium 中的下拉列表：

-   Selenium 中的 Select 类是什么？
    -   如何从 Selenium 的下拉列表中选择一个值？
    -   如何从 Selenium 的下拉列表中选择多个值？
    -   另外，如何从 Selenium 的下拉列表中获取选项？
-   如何从下拉列表中取消选择一个值 Selenium？
-   说明 Selenium 中 Select 类用法的示例。
    -   示例 1 - 使用 Selenium WebDriver 处理下拉菜单。
    -   示例 2 - 使用 Selenium WebDriver 处理多选。

## Selenium 中的 Select Class 是什么？

在HTML 中，下拉菜单通常使用`<select>`标签或`<input>`标签来实现。为了对使用HTML 标记声明的下拉菜单执行某些操作， Selenium WebDrivers提供了一个名为“Select”类的类。一旦你开始在[IDE](https://www.toolsqa.com/selenium-webdriver/download-and-start-eclipse/)中键入“Select” ，它就会显示如下所示的详细信息：`<select>`

![在 Selenium 中选择类](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Select%20class%20in%20Selenium.png)

从上面的截图我们可以看出，“Select”类是由Selenium WebDriver的“org.openqa.selenium.support.ui”包提供的。你可以创建 Select 类的对象，绕过[“WebElement”](https://www.toolsqa.com/selenium-webdriver/webelement-commands/)类的对象，这显示了WebElement的相应[定位器](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)返回的对象。

因此，你可以 使用以下语法创建Select类的对象：

```java
Select select = new Select(WebElement webelement);
```

Select 类构造函数接受一个参数：由指定元素的定位器返回的 WebElement 对象。

“ Select”类提供了多种处理下拉操作的方法。下图列出了其中的一些：

![选择类的方法](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Methods%20of%20Select%20class.png)

现在让我们了解Selenium WebDriver中“选择”类提供的各种选择和取消选择方法的语法和用法。

### 如何从 Selenium 的下拉列表中选择一个值？

如上图突出显示的，Selenium WebDriver的Select 类 提供了以下方法来从下拉列表中选择一个选项/值(如上图中标记 1 突出显示的)：

-   按索引选择
-   按值选择
-   selectByVisibleText

让我们了解所有这些方法的语法和用法：

#### 选择索引：

此方法通过其索引号选择下拉选项。我们提供一个整数值作为索引号作为参数。它具有以下语法：

```java
selectByIndex(int arg0) : void
```

即，它接受需要选择的下拉值的索引。索引从 0 开始。

假设在网页[“https://demoqa.com/select-menu”](https://demoqa.com/select-menu)上，我们选择了下拉列表的第 4 个值，如下所示：

![使用选择标签的下拉菜单](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Dropdown%20using%20select%20tag.png)

如我们所见，上面的下拉列表是使用`<select>`HTML标记实现的，因此我们可以使用Selenium WebDriver的“Select”类来使用索引选择选项“Yellow”，如下所示：

```java
// Create object of the Select class
Select se = new Select(driver.findElement(By.xpath("//[@id='oldSelectMenu']")));
			
// Select the option by index
se.selectByIndex(3);
```

正如我们提到的，下拉列表的索引从 3 开始，因此可以使用索引 3 选择值“黄色” 。

#### 按值选择

此方法通过其值选择下拉选项。我们提供一个字符串值作为参数值。它具有以下语法：

```java
selectByValue(String arg0) : void
```

如果我们考虑页面[“https://demoqa.com/select-menu”](https://demoqa.com/select-menu)上的相同下拉菜单，如上一节所示，我们可以看到下拉菜单的每个选项都有一个分配值，如下所示：

![下拉列表的值](https://www.toolsqa.com/gallery/selnium%20webdriver/4.values%20of%20a%20dropdown.png)

现在，如果我们必须选择选项“White”，我们可以使用它的值“6”，如以下代码片段所示：

```java
// Create object of the Select class
Select se = new Select(driver.findElement(By.xpath("//[@id='oldSelectMenu']")));
			
// Select the option with value "6"
se.selectByValue("6");
```

由于值“6”对应于选项“White”，因此它将从下拉列表中选择值“White” 。

#### selectByVisibleText

此方法使人们能够根据下拉文本从下拉列表或多选下拉列表中选择一个选项。你需要将元素的字符串值作为参数传递。它具有以下语法：`<select>`

```java
selectByVisibleText(String arg0): void
```

如果我们考虑页面[“https://demoqa.com/select-menu”](https://demoqa.com/select-menu)上的相同下拉菜单，如上一节所示，我们可以看到下拉菜单的每个选项都有一个文本值，显示在网页也是，所以我们可以用那个文本来选择相应的选项，如下图：

```java
// Create the object of the Select class
Select se = new Select(driver.findElement(By.xpath("//[@id='oldSelectMenu']")));
			
// Select the option using the visible text
se.selectByVisibleText("White");
```

由于下拉菜单中有一个选项的文本为"White"，因此将使用上面的代码选择相同的选项。

除了上面介绍的下拉列表类型之外，HTML`<select>`标记还提供了定义下拉列表的方法，允许选择多个值。让我们看看如何声明多选下拉列表，以及如何使用Selenium WebDriver的“选择”类在下拉列表中选择多个选项。

### 如何从 Selenium 的下拉列表中选择多个值？

如果`<select >`标签包含多个属性，则意味着下拉列表允许选择多个值。正如我们在网页[“https://demoqa.com/select-menu”的以下截图中看到的：](https://demoqa.com/select-menu)

![多选HTML代码](https://www.toolsqa.com/gallery/selnium%20webdriver/5.HTML%20Code%20for%20Multi%20Select.png)

我们可以使用我们用于从下拉列表中选择一个值的任何方法，通过为不同的值多次调用这些方法来选择多个值。“ Select”类提供了一个方法，isMultiple()，我们可以使用它首先验证下拉列表是否允许我们选择多个值。让我们看看如何使用isMultiple()方法：

#### 如何检查下拉列表是否为多选？

正如我们所讨论的，Select 类提供了“isMultiple()”方法，该方法确定 say 中的 Web 元素是否支持多选。它返回一个布尔值，即True/False，不带任何参数。 它检查 Web 元素的HTML代码中的属性“multiple” 。因此，它具有以下语法：

```java
isMultiple(): boolean
```

一旦确定 Web 元素是否为多选，就可以对要选择的多个值使用 Select 类的各种选择方法。下面的示例代码显示了相同的 -

```java
Select oSel = new Select(driver.findElement(By.xpath(//[@id='cars']);

if(oSel.isMultiple()){
	
	//Selecting multiple values by index
	oSel.selectByIndex(1);
	oSel.selectByIndex(2);

	//Or selecting by values
	oSel.selectByValue("volvo");
	oSel.selectByValue("audi");

	//Or selecting by visible text
	oSel.selectByVisibleText("Volvo");
	oSel.selectByVisibleText("Opel");
}
```

这就是你如何从多选下拉列表中选择多个值。

现在我们已经了解了如何从下拉列表中选择值，无论是单选还是多选，我们应该有一些方法来检查下拉列表包含哪些值以及在下拉列表中选择了哪些所有值。“选择”类提供了从下拉列表中获取选项的方法。让我们了解一下这些方法的细节和用法：

### 如何从 Selenium 的下拉列表中获取选项？

正如标记 2 突出显示的那样，在上面“Select”类部分下的图像中，Select类提供了以下方法来获取下拉列表的选项：

-   获取选项()
-   getFirstSelectedOption() 方法
-   getSelectedOptions()

让我们了解所有这些方法的细节：

#### 获取选项

有时你需要在下拉列表或多选框中获取所有选项。这是你可以使用Select类的getOptions()方法的地方。它具有以下语法：

```java
getOptions(): List<WebElement>
```

如我们所见，此方法将下拉列表的所有选项作为WebElement 列表返回。下面的代码片段展示了我们如何获取页面“https://demoqa.com/select-menu”下拉菜单的所有选项：

```java
Select select = new Select(driver.findElement(By.id("oldSelectMenu")));
        
// Get all the options of the dropdown
List<WebElement> options = select.getOptions();
```

使用此方法，我们可以检索下拉列表的所有选项(无论是单选还是多选)。

#### getFirstSelectedOption() 方法

此方法返回下拉列表中第一个选择的选项。如果是单选下拉，该方法返回下拉选中的值，如果是多选下拉，该方法返回下拉选中的第一个值。它具有以下语法：

```java
getFirstSelectedOption(): WebElement
```

如我们所见，此方法返回一个WebElement。下面的代码片段显示了我们如何获得页面“https://demoqa.com/select-menu”上下拉列表的第一个选定选项：

```java
Select select = new Select(driver.findElement(By.id("oldSelectMenu")));
        
// Get the first selected option of the dropdown
WebElement firstSelectedOption = select.getFirstSelectedOption();
```

使用此方法，我们可以检索下拉列表的第一个选定选项(无论是单选还是多选)。

#### getAllSelectedOptions() 方法

此方法返回下拉列表中所有选定的选项。如果是单选下拉，该方法返回下拉唯一选中的值，如果是多选下拉，该方法返回下拉所有选中的值。它具有以下语法：

```java
getAllSelectedOptions():List<WebElement>
```

如我们所见，此方法返回WebElements列表。以下代码片段展示了我们如何获取页面“https://demoqa.com/select-menu”上下拉列表的所有选中选项：

```java
Select select = new Select(driver.findElement(By.id("oldSelectMenu")));
        
// Get all the selected option of the dropdown
List<WebElement> selectedOptions = select.getAllSelectedOptions();
```

使用此方法，我们可以检索下拉列表的所有选定选项(无论是单选还是多选)。

### 如何从 Selenium 的下拉列表中取消选择值？

就像我们在DropDown & Multi-Select 中选择值一样， 我们也可以取消选择值。但取消选择方法仅适用于多选。你可以使用此处讨论的不同取消选择方法从多选元素中取消选择预选选项。正如我们在显示“Select”类方法的屏幕截图中所观察到的(由标记 3 所示)， Select 类提供了以下方法来取消选择下拉列表的值：

-   取消全选()
-   取消选择索引()
-   取消选择值()
-   取消选择可见文本()

让我们了解所有这些方法的细节和用法：

#### 取消全选

此方法将清除下拉列表中所有选定的条目。它具有以下语法：

```java
deselectAll(): void
```

如果下拉列表中已经选择的选项很少，你可以使用方法deselectAll() 取消选择所有选项。 以下代码片段显示了一个示例示例，我们如何从下拉列表中取消选择所有值：

```java
Select select = new Select(driver.findElement(By.id("oldSelectMenu")));

//Deselect all the options
select.deselectAll();
```

它将取消选择下拉列表中的所有选项。

#### 取消选择索引

与selectByIndex()方法类似，Select 类还提供了使用deselectByIndex()方法从下拉列表中取消选择选项的方法。你可以使用选项的索引号来取消选择它。它具有以下语法：

```java
deselectByIndex(int arg0): void
```

因此，如果下拉列表中已选择的选项很少，你可以使用deselectByIndex ()方法取消选择其中一个选项。以下代码片段显示了一个示例示例，我们如何通过指定索引从下拉列表中取消选择其中一个值：

```java
Select select = new Select(driver.findElement(By.id("oldSelectMenu")));

//Deselect first value by index
select.deselectByIndex(1);
```

它将取消选择下拉列表中索引 1 处的选项。

#### 取消选择值

与selectByValue()方法类似，Select类还提供了使用deselectByValue()方法从下拉列表中取消选择选项的方法。你可以使用该选项的值来取消选择它。它具有以下语法：

```java
deselectByValue(String arg0): void
```

因此，如果下拉列表中已选择的选项很少，你可以使用 deselectByValue() 取消选择其中一个选项。以下代码片段显示了一个示例示例，我们如何通过指定值从下拉列表中取消选择其中一个值：

```java
Select select = new Select(driver.findElement(By.id("oldSelectMenu")));

//Deselect option with value "6"
select.deselectByValue("6");
```

它将在下拉列表中取消选择具有值的选项。

#### 取消选择可见文本

与selectByVisibleText()方法类似，Select类还提供了使用deselectByVisibleText()方法从下拉列表中取消选择选项的方法。你可以使用选项的文本来取消选择它。它具有以下语法：

```java
deselectByVisibleText(String arg0): void
```

因此，如果下拉列表中已选择的选项很少，你可以使用deselectByVisibleText ()方法取消选择其中一个选项。以下代码片段显示了一个示例示例，我们如何通过指定其文本从下拉列表中取消选择其中一个值：

```java
Select select = new Select(driver.findElement(By.id("oldSelectMenu")));

 //Deselect option with text "White"
select.deselectByVisibleText("White");
```

它将在下拉列表中取消选择带有文本“白色”的选项。

## 说明 Selenium 中 Select 类用法的示例

我们将介绍两个示例，一个是简单的下拉菜单，另一个是多选下拉菜单。我们将使用[ToolsQA 演示](https://demoqa.com/select-menu)网站来自动化这些场景。现在让我们快速启动 dropdown/multi-select 自动化。

### 示例 1- 使用 Selenium WebDriver 处理下拉菜单

我们的用例将遵循以下步骤 -

-   启动浏览器。
-   打开“https://demoqa.com/select-menu”。
-   使用元素 id 选择旧样式选择菜单。
-   打印下拉列表的所有选项。
-   使用索引选择“紫色”。
-   之后，使用可见文本选择“洋红色”。
-   使用值选择一个选项。
-   关闭浏览器

使用上面讨论的 Select 类的方法，代码如下所示 -

```java
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class DropDown {

    public static void main(String[] args) throws InterruptedException {

        //Creating instance of Chrome driver
        WebDriver driver = new ChromeDriver();

        //Step#2- Launching URL
        driver.get("https://demoqa.com/select-menu");

        //Maximizing window
        driver.manage().window().maximize();

        //Step#3- Selecting the dropdown element by locating its id
        Select select = new Select(driver.findElement(By.id("oldSelectMenu")));

        //Step#4- Printing the options of the dropdown
        //Get list of web elements
        List<WebElement> lst = select.getOptions();

        //Looping through the options and printing dropdown options
        System.out.println("The dropdown options are:");
        for(WebElement options: lst)
            System.out.println(options.getText());

        //Step#5- Selecting the option as 'Purple'-- selectByIndex
        System.out.println("Select the Option by Index 4");
        select.selectByIndex(4);
        System.out.println("Select value is: " + select.getFirstSelectedOption().getText());

        //Step#6- Selecting the option as 'Magenta'-- selectByVisibleText
        System.out.println("Select the Option by Text Magenta");
        select.selectByVisibleText("Magenta");
        System.out.println("Select value is: " + select.getFirstSelectedOption().getText());

        //Step#7- Selecting an option by its value
        System.out.println("Select the Option by value 6");
        select.selectByValue("6");
        System.out.println("Select value is: " + select.getFirstSelectedOption().getText());

        driver.quit();
    }

}
```

在执行代码时，你会注意到下拉选择是根据使用的选择方法进行的，控制台窗口将打印选项，如下所示：

![Selenium中处理Dropdown的执行结果](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Execution%20results%20for%20handling%20Dropdown%20in%20Selenium.png)

因此，通过这种方式，我们可以选择并验证允许单选的下拉列表中的值。

### 示例 2 - 使用 Selenium WebDriver 处理多选

要使用Selenium WebDriver 的Select 类自动进行多选，我们将使用以下用例：

-   启动浏览器。
-   打开“https://demoqa.com/select-menu”。
-   使用元素 id 选择标准多选。
-   验证元素是多选的。
-   使用索引选择“Opel”并使用索引取消选择。
-   使用值选择“Saab”并取消选择相同的使用值。
-   取消选择所有选项。
-   关闭浏览器。

我们将使用 Select 类的选择和取消选择方法来自动化多选元素。代码如下所示 -

```java
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class MultiSelect {

    public static void main(String[] args) throws InterruptedException {

        //Creating instance of Chrome driver
        WebDriver driver = new ChromeDriver();

        // Navigate to the URL
        driver.get("https://demoqa.com/select-menu");

        //Maximizing window
        driver.manage().window().maximize();

        //Selecting the multi-select element by locating its id
        Select select = new Select(driver.findElement(By.id("cars")));

        //Get the list of all the options
        System.out.println("The dropdown options are -");

        List<WebElement> options = select.getOptions();

        for(WebElement option: options)
            System.out.println(option.getText());

        //Using isMultiple() method to verify if the element is multi-select, if yes go onto next steps else eit
        if(select.isMultiple()){

            //Selecting option as 'Opel'-- ByIndex
            System.out.println("Select option Opel by Index");
            select.selectByIndex(2);

            //Selecting the option as 'Saab'-- ByValue
            System.out.println("Select option saab by Value");
            select.selectByValue("saab");

            // Selecting the option by text
            System.out.println("Select option Audi by Text");
            select.selectByVisibleText("Audi");

            //Get the list of selected options
            System.out.println("The selected values in the dropdown options are -");

            List<WebElement> selectedOptions = select.getAllSelectedOptions();

            for(WebElement selectedOption: selectedOptions)
                System.out.println(selectedOption.getText());


            // Deselect the value "Audi" by Index
            System.out.println("DeSelect option Audi by Index");
            select.deselectByIndex(3);

            //Deselect the value "Opel" by visible text
            System.out.println("Select option Opel by Text");
            select.deselectByVisibleText("Opel");

            //Validate that both the values are deselected
            System.out.println("The selected values after deselect in the dropdown options are -");
            List<WebElement> selectedOptionsAfterDeselect = select.getAllSelectedOptions();

            for(WebElement selectedOptionAfterDeselect: selectedOptionsAfterDeselect)
                System.out.println(selectedOptionAfterDeselect.getText());

            //Step#8- Deselect all values
            select.deselectAll();
        }

        driver.quit();
    }

}
```

上面的执行代码会从多选中选择和取消选择多个选项并打印多选选项，如下所示：

![处理 Selenium 执行结果中的多选](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Handling%20Multi%20Select%20in%20Selenium%20execution%20results.png)

你可以选择多个选项，就像我们根据你的要求从多选框中选择了所有选项一样。现在，你可以在Selenium自动化中使用不同的Select类方法，并轻松地自动化下拉列表或多选框以简化你的执行。

## 关键要点

-   selenium 中的 Select 类可以通过导入org.openqa.selenium.support.ui.Select 包来使用。
-   此外，选择类提供了不同的方法来从下拉/多选中选择值。
-   Select 类还提供取消选择方法以从下拉列表中取消选择某些值。
-   多个属性可以区分通过`<select>`标签提供的多选元素。
-   选择和取消选择方法可以使用索引、可见文本或值来从下拉列表中选择/取消选择值。