## 如何使用 Selenium WebDriver 处理 IFrame / IFrame

在本教程中，我们将学习使用 Selenium Webdriver 处理 iFrame。iFrame 是嵌入在 HTML 文档中的 HTML 文档。iFrame 由`<iframe></iframe>`HTML 中的标记定义。使用此标记，你可以在检查 HTML 树时识别 iFrame。

下面是包含两个 iFrame 的 HTML 页面的示例 HTML 代码。

```java
<html>
  <body>
    <div class="box">
      <iframe name="iframe1" id="IF1" height="600" width="400" src="https://toolsqa.com"></iframe>
    </div>
    <div class="box">
      <iframe name="iframe2" id="IF2" height="600" width="400" 	align="left" src="https://demoqa.com"></iframe>
    </div>
   </body>
</html>
```

你可以在此处找到 iFrame 的测试页面[https://demoqa.com/frames](https://demoqa.com/frames)我们将使用它来学习 iFrame 处理逻辑。在开始之前，我们必须了解要在页面上使用不同的 iFrame，我们必须在这些 iFrame 之间切换。要在 iFrame 之间切换，我们必须使用驱动程序的switchTo().frame命令。我们可以通过三种方式使用switchTo().frame()：

-   switchTo.frame ( int frameNumber )：传递帧索引，驱动程序将切换到该帧。
-   switchTo.frame ( string frameNameOrId )：传递框架元素名称或 ID，驱动程序将切换到该框架。
-   switchTo.frame ( WebElement frameElement )：传递框架 web 元素，驱动程序将切换到该框架。

这是显示所有三个重载的图像：

![Iframe 超载](https://www.toolsqa.com/gallery/selnium%20webdriver/1.IframeOverload.png)

让我们看看这些是如何工作的，但在此之前，我们必须知道以下问题的答案

-   什么是帧索引？
-   如何获取网页上的总帧数？

\##如何查找网页上的 iFrame 总数

有两种方法可以找到网页上的 iFrame 总数。首先是执行 JavaScript，其次是查找标签名称为 iFrame 的 Web 元素的总数。下面是使用这两种方法的代码：

```java
		WebDriver driver = new FirefoxDriver();
		driver.get("https://demoqa.com/frames");

		//By executing a java script
		JavascriptExecutor exe = (JavascriptExecutor) driver;
		Integer numberOfFrames = Integer.parseInt(exe.executeScript("return window.length").toString());
		System.out.println("Number of iframes on the page are " + numberOfFrames);

		//By finding all the web elements using iframe tag
		List<WebElement> iframeElements = driver.findElements(By.tagName("iframe"));
		System.out.println("The total number of iframes are " + iframeElements.size());
```

## 按索引切换到帧

iFrame 的索引是它在 HTML 页面中出现的位置。在上面的示例中，我们找到了 iFrame 的总数。在示例页面中，我们有两个 iFrame，iFrame 的索引从 0 开始。因此页面上有两个 iFrame，索引为 0 和 1。索引 0 将是 HTML 页面中较早存在的 iFrame。请参考下图：

![FramePageDesc](https://www.toolsqa.com/gallery/selnium%20webdriver/2.FramePageDesc.png)

要切换到第 0 个 iframe，我们可以简单地编写driver.switchTo().frame(0)。这是示例代码：

```java
	public static void main(String[] args) throws InterruptedException {
		WebDriver driver = new FirefoxDriver();
		driver.get("https://demoqa.com/frames");

		//Switch by Index
		driver.switchTo().frame(0);
		driver.quit();
	}
```

## 按名称切换到框架

现在，如果你查看 iFrame 的 HTML 代码，你会发现它具有Name属性。Name 属性有一个值 iframe1。我们可以使用命令driver.switchTo().frame("iframe1")切换到使用名称的 iFrame。这是示例代码

```java
		WebDriver driver = new FirefoxDriver();
		driver.get("https://demoqa.com/frames");

		//Switch by frame name
		driver.switchTo().frame("iframe1");
		driver.quit();
```

## 按 ID 切换到帧

与 iFrame 标签中的名称属性类似，我们也有ID属性。我们也可以使用它来切换到框架。我们所要做的就是像这个 driver.SwitchTo ().frame("IF1")一样将 id 传递给switchTo命令。这是示例代码：

```java
		WebDriver driver = new FirefoxDriver();
		driver.get("https://demoqa.com/frames");

		//Switch by frame ID
		driver.switchTo().frame("IF1");
		driver.quit();
```

## 通过 WebElement 切换到框架

现在我们可以通过简单地将 iFrame WebElement 传递给driver.switchTo().frame()命令来切换到 iFrame。首先，使用任何定位器策略找到 iFrame 元素，然后将其传递给switchTo命令。这是示例代码：

```java
		WebDriver driver = new FirefoxDriver();
		driver.get("https://demoqa.com/frames");
		//First find the element using any of locator stratedgy
		WebElement iframeElement = driver.findElement(By.id("IF1"));

		//now use the switch command
		driver.switchTo().frame(iframeElement);
		driver.quit();
```

## 从 Frame 切换回主页

有一个非常重要的命令可以帮助我们回到主页。主页是嵌入了两个 iFrame 的页面。完成特定 iFrame 中的所有任务后，你可以使用switchTo().defaultContent()切换回主页。这是将驱动程序切换回主页的示例代码。

```java
		WebDriver driver = new FirefoxDriver();
		driver.get("https://demoqa.com/frames");
		//First find the element using any of locator stratedgy
		WebElement iframeElement = driver.findElement(By.id("IF1"));

		//now use the switch command
		driver.switchTo().frame(0);

		//Do all the required tasks in the frame 0
		//Switch back to the main window
		driver.switchTo().defaultContent();
		driver.quit();
```

现在我们知道什么是 iFrame 以及如何在 iFrame 之间切换，让我们学习如何与 iFrame 中的元素交互。在下一个教程中，我们将详细讨论如何与 iFrame 中的元素进行交互。