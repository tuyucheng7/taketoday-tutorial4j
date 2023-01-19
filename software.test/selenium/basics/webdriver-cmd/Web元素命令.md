到目前为止，在我们的 Selenium 学习之旅中，我们已经完成了WebDriver 命令和导航命令。很快我们将识别网页上的不同 WebElement并对其执行各种操作。本章都是关于Selenium WebDriver WebElement 命令的。但在继续寻找不同的WebElements之前，最好涵盖我们可以在 WebElement 上执行的所有操作。在本章中，我们将学习什么是 WebElement以及可以在各种WebElement上执行的操作列表。

## 什么是网络元素？

WebElement 表示一个HTML 元素。HTML 文档由HTML 元素组成。HTML 元素由一个开始标记、一个结束标记组成，中间是内容：`<tagname>`content`</tagname>`

HTML 元素是从开始标记到结束标记的所有内容：`<p>` 我的第一个 HTML 段落。`</p>`

HTML 元素可以嵌套(元素可以包含元素)。所有 HTML 文档都由嵌套的 HTML 元素组成。

```java
<html>
	<body>
		<h1> My First Heading </h1>
		<p> My first paragraph. </p>
	</body>
</html>
```

## WebElement 命令/操作列表

所有与页面交互有关的有趣操作都将通过此WebElement Interface执行。

![WebElementCommands_01](https://www.toolsqa.com/gallery/selnium%20webdriver/1.WebElementCommands_01.png)

注意：方法后跟Object关键字是从 Java 中的对象类获取的通用方法。你会发现这些方法适用于 java 语言的每个对象。

在了解 WebElement 的每一个动作之前，让我们了解一下我们如何获得 WebElement 对象/元素。在前面的章节中，我们了解到WebDriver的每个方法要么返回一些东西，要么返回 void(意味着什么都不返回)。与WebDriver的findElement命令返回WebElement的方式相同。

![WebElementCommands_02](https://www.toolsqa.com/gallery/selnium%20webdriver/2.WebElementCommands_02.png)

因此，要获取 WebElement 对象，请编写以下语句：

WebElement 元素 = driver.findElement(By.id("UserName"));

现在，如果你键入element dot，Eclipse 的智能将填充完整的操作列表，就像上图一样。

还有一点需要注意，WebElement可以是任何类型，比如它可以是文本、链接、单选按钮、下拉列表、WebTable或任何HTML 元素。但是，无论该操作在 WebElement 上是否有效，所有操作都将始终针对任何元素进行填充。例如clear() 命令，即使你有一个链接元素，你仍然可以选择在它上面选择clear() 命令，如果你选择可能会导致一些错误或者可能什么都不做。

## 清除指令

clear( ) : void - 如果这个元素是一个文本输入元素，这将清除该值。此方法不接受任何参数并且不返回任何内容。

命令 - element.clear();

此方法对其他元素没有影响。文本输入元素是INPUT和TEXTAREA元素。

```java
WebElement element = driver.findElement(By.id("UserName"));
element.clear();

//Or can be written as

driver.findElement(By.id("UserName")).clear();
```

## 发送键命令

sendKeys(CharSequence... keysToSend ) : void - 这模拟输入一个元素，它可以设置它的值。此方法接受 CharSequence 作为参数并且不返回任何内容。

命令 - element.sendKeys("text");

此方法适用于文本输入元素，如 INPUT和TEXTAREA元素。

```java
WebElement element = driver.findElement(By.id("UserName"));
element.sendKeys("ToolsQA");

//Or can be written as

driver.findElement(By.id("UserName")).sendKeys("ToolsQA");
```

## 单击命令

click( ) : void  - 这模拟任何元素的点击。不接受任何参数并且不返回任何内容。

命令 - element.click();

单击可能是与文本元素、链接、单选框等 Web 元素交互的最常见方式。

```java
WebElement element = driver.findElement(By.linkText("ToolsQA"));
element.click();

//Or can be written as

driver.findElement(By.linkText("ToolsQA")).click();
```

注意：大多数时候我们单击链接并加载新页面，此方法将尝试等到页面正确加载后再将执行移交给下一个语句。但是，如果 click() 导致通过事件加载新页面或通过发送本机事件(例如通过 javascript)来完成，则该方法不会等待它加载。

单击元素有一些先决条件。该元素必须是可见的，并且它的高度和宽度必须大于 0。

## 显示命令

isDisplayed( ) : boolean - 此方法确定元素当前是否正在显示。这不接受任何参数，但返回一个布尔值(真/假)。

命令 - element.isDisplayed();

```java
WebElement element = driver.findElement(By.id("UserName"));
boolean status = element.isDisplayed();

//Or can be written as

boolean staus = driver.findElement(By.id("UserName")).isDisplayed();
```

注意：不要将此方法与页面上存在或不存在的元素混淆。如果元素存在于页面上，这将返回true；如果元素不存在于页面上，则抛出NoSuchElementFound异常。这是指元素的属性，有时元素存在于页面上但元素的属性设置为hidden，在这种情况下，这将返回false，因为元素存在于 DOM 中但我们不可见.

## IsEnabled 命令

isEnabled( ) : boolean - 这确定元素当前是否已启用？ 这不接受任何参数但返回布尔值 ( true/false )。

命令 - element.isEnabled();

这通常会为所有内容返回 true，但我相信你一定已经注意到网页中有许多禁用的输入元素。

```java
WebElement element = driver.findElement(By.id("UserName"));
boolean status = element.isEnabled();

//Or can be written as

boolean staus = driver.findElement(By.id("UserName")).isEnabled();

//Or can be used as
WebElement element = driver.findElement(By.id("userName"));
boolean status = element.isEnabled();
// Check that if the Text field is enabled, if yes enter value
if(status){
    element.sendKeys("ToolsQA");
}
```

## IsSelected 命令

isSelected( ) : boolean - 确定该元素是否被选中。这不接受任何参数但返回布尔值 ( true/false )。

命令 - element.isSelected();

此操作仅适用于输入元素，例如Checkboxes、Select Options和Radio Buttons。如果元素当前被选中或选中，则返回True ，否则返回false。

```java
WebElement element = driver.findElement(By.id("Sex-Male"));
boolean status = element.isSelected();

//Or can be written as

boolean staus = driver.findElement(By.id("Sex-Male")).isSelected();
```

注意：在后面的 [复选框和单选按钮](https://www.toolsqa.com/selenium-webdriver/selenium-checkbox/) 和[下拉和多选](https://www.toolsqa.com/selenium-webdriver/dropdown-in-selenium/)章节中，我们已经介绍了很多围绕它的例子。

## 提交命令

submit( ) : void- 如果当前元素是表单或表单中的元素，则此方法比 click() 效果好/更好。这不接受任何参数并且不返回任何内容。

命令 - element.submit();

如果这导致当前页面发生变化，则此方法将等待直到加载新页面。

```java
WebElement element = driver.findElement(By.id("SubmitButton"));
element.submit();

//Or can be written as

driver.findElement(By.id("SubmitButton")).submit();
```

## 获取文本命令

getText( ) : String -此方法将获取元素的可见(即未被 CSS 隐藏)innerText。这不接受任何参数，但返回一个字符串值。

命令 - element.getText();

这将返回元素的 innerText，包括子元素，没有任何前导或尾随空格。

```java
WebElement element = driver.findElement(By.xpath("anyLink"));
String linkText = element.getText();
```

## getTagName 命令

getTagName( ) : String - 此方法获取此元素的标签名称。这不接受任何参数并返回一个字符串值。

命令 - element.getTagName();

这不会返回 name 属性的值，但会返回元素的标记，例如“ input`<input name="foo"/>` ” 。

```java
WebElement element = driver.findElement(By.id("SubmitButton"));
String tagName = element.getTagName();

//Or can be written as

String tagName = driver.findElement(By.id("SubmitButton")).getTagName();
```

## getCssValue 命令

getCssvalue( ) : String - 此方法获取给定元素的 CSS 属性值。这不接受任何参数并返回一个字符串值。

命令 - element.getCssValue();

颜色值应作为 rgba 字符串返回，因此，例如，如果在 HTML 源代码中将“ background-color ”属性设置为“ green ”，则返回值将为“ rgba(0, 255, 0, 1) ”。

## getAttribute 命令

getAttribute ( String Name ) : String - 此方法获取元素的给定属性的值。这接受 String 作为参数并返回 String 值。

命令 - element.getAttribute();

属性是 ID、名称、额外的类，使用此方法你可以获得任何给定元素的属性值。

```java
WebElement element = driver.findElement(By.id("SubmitButton"));
String attValue = element.getAttribute("id"); //This will return "SubmitButton"
```

## getSize 命令

getSize( ) : Dimension - 此方法获取渲染元素的宽度和高度。这不接受任何参数，但返回 Dimension 对象。

命令 - element.getSize();

这将返回页面上元素的大小。

```java
WebElement element = driver.findElement(By.id("SubmitButton"));
Dimension dimensions = element.getSize();
System.out.println(“Height :” + dimensions.height + ”Width : "+ dimensions.width);
```

## getLocation 命令

getLocation( ) : Point - 此方法定位元素在页面上的位置。这不接受任何参数但返回 Point 对象。

命令 - element.getLocation();

这将返回Point 对象，我们可以从中获取特定元素的 X 和 Y 坐标。

```java
WebElement element = driver.findElement(By.id("SubmitButton"));
Point point = element.getLocation();
System.out.println("X cordinate : " + point.x + "Y cordinate: " + point.y);
```