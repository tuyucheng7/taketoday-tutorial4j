要理解本章，你必须先了解前面的[WebDriver Waits](https://toolsqa.com/selenium-webdriver/wait-commands/)一章中讨论的概念。另外，最好学习[How to Handle Ajax Wait in Selenium](https://toolsqa.com/selenium-cucumber-framework/handle-ajax-call-using-javascriptexecutor-in-selenium/)。

在本章中，我们将更多地探索Fluent Waits并了解如何创建我们自己的Custom Waits 或 Advance WebDriver Waits。流畅的等待看起来像这样：

```java
		//Declare and initialise a fluent wait
		FluentWait wait = new FluentWait(driver);
		//Specify the timout of the wait
		wait.withTimeout(5000, TimeUnit.MILLISECONDS);
		//Sepcify polling time
		wait.pollingEvery(250, TimeUnit.MILLISECONDS);
		//Specify what exceptions to ignore
		wait.ignoring(NoSuchElementException.class)

		//This is how we specify the condition to wait on.
		//This is what we will explore more in this chapter
		wait.until(ExpectedConditions.alertIsPresent());
```

流畅的等待也称为智能等待。它们被称为智能主要是因为它们不等待最大超时，在.withTimeout(5000, TimeUnit.MILLISECONDS)中指定。相反，它等待时间直到.until(YourCondition)方法中指定的条件变为true。

下图解释了解释Fluent 等待工作的流程图。

![隐式等待](https://www.toolsqa.com/gallery/selnium%20webdriver/1.mplicitWait.png)

当until方法被调用时，下面的事情严格按照这个顺序发生

-   第 1 步：在此步骤中，fluent wait 捕获等待开始时间。
-   第 2 步：Fluent wait 检查 .until() 方法中提到的条件
-   第 3 步：如果不满足条件，则应用线程休眠，超时时间超出 .pollingEvery(250, TimeUnit.MILLISECONDS) 方法调用中提到的值。在上面的示例中，它是 250 毫秒。
-   第 4 步：一旦第 3 步的线程睡眠期满，将使用当前时间检查开始时间。如果在步骤 1 中捕获的等待开始时间与当前时间之间的差异小于 .withTimeout(5000, TimeUnit.MILLISECONDS) 中指定的时间，则重复步骤 2。

这个过程一直持续到超时到期或条件成立为止。

在此示例中，我们使用了ExpectedConditions类。这是一个非常方便的课程，可以满足你的大部分需求。但是，有时我们可能需要的不仅仅是ExpectedConditions中的内容。如果你密切关注.until()方法，你会发现这是一个可以接受两种类型参数的重载函数。

-   函数()
-   一个谓词()

让我们看一个这样的例子。我们将采用此页面上的一个简单场景[http://demoqa.com/browser-windows/](https://demoqa.com/browser-windows/)

问题：在此页面上有一个“更改颜色”按钮，几秒钟后该按钮变为红色。让我们写一个简单的等待，它可以等待按钮的颜色改变。颜色改变后，单击按钮。

在我们开始之前，让我们先看看什么是 .until() 方法重载的签名。

![直到签名](https://www.toolsqa.com/gallery/selnium%20webdriver/2.UntilSignature.jpg)

注意 .until() 方法可以接受Function< ? super WebDriver, V>或Predicate ;

## 什么是函数？

包：com.google.common.base.Function

这里的函数是一个通用接口，它要求你实现以下方法 - apply(F from) - equals(Object obj) equals 的实现可以忽略，它将从基本实现中挑选出来。

如何定义一个？

```java
	Function<WebDriver, Boolean> function = new Function<WebDriver, Boolean>()
				{

					public Boolean apply(WebDriver arg0) {
						return null;
					}

				};
```

注意我们必须自己定义apply方法。现在这里要注意的重点是apply方法的签名。方法将接受一个WebDriver作为输入参数，并将返回一个布尔值。这是因为我们定义函数 ( Function ) 的方式。提到的第一个参数将是输入参数，第二个将是应用方法的返回值。

现在实现 apply 方法，以便在条件失败时返回 false，在满足条件时返回 true。在我们的例子中，如果按钮的颜色不是红色，我们将返回 false，否则我们将返回 true。这是代码示例

```java
Function<WebDriver, Boolean> function = new Function<WebDriver, Boolean>()
				{
					public Boolean apply(WebDriver arg0) {
						WebElement element = arg0.findElement(By.id("colorVar"));
						String color = element.getAttribute("color");

						if(color.equals("red"))
						{
							return true;
						}
						return false;
					}
				};
```

现在我们有了等待条件，我们可以简单地在FluentWait中应用等待条件。像这样，注意我已经添加了一些打印语句来查看代码中发生了什么

```java
public static void main(String[] args) throws InterruptedException {

		WebDriver driver = new FirefoxDriver();
		driver.get("https://demoqa.com/browser-windows/");

		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
		wait.pollingEvery(250,  TimeUnit.MILLISECONDS);
		wait.withTimeout(2, TimeUnit.SECONDS);

		Function<WebDriver, Boolean> function = new Function<WebDriver, Boolean>()
				{
					public Boolean apply(WebDriver arg0) {
						WebElement element = arg0.findElement(By.id("colorVar"));
						String color = element.getAttribute("color");
						System.out.println("The color if the button is " + color);
						if(color.equals("red"))
						{
							return true;
						}
						return false;
					}
				};

		wait.until(function);
	}
```

这是你在控制台窗口中获得的输出： 按钮的颜色是 color: white; 按钮的颜色是color: white; 按钮的颜色是color: white; 按钮的颜色是color: white; 按钮的颜色是color: white; 按钮的颜色是color: red;

看到该函数对控件的颜色属性进行了大约 5 次测试，在最后一次尝试中它能够找到红色的颜色属性。这是等待退出的时候。

函数也可用于返回对象而不是布尔值。在对象的情况下，确保你的 apply 方法的实现返回 null，直到找不到时间对象。让我们用这个例子来看看。在上面的测试页面中，每隔几秒我们就会在 HTML 页面中添加一个新对象。这个对象有一个 id = target。让我们使用流畅的等待来等待这个对象。我直接把代码分享给你，希望你现在能看懂细节。

```java
public static void main(String[] args) throws InterruptedException {

		WebDriver driver = new FirefoxDriver();
		driver.get("https://demoqa.com/browser-windows/");

		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
		wait.pollingEvery(250,  TimeUnit.MILLISECONDS);
		wait.withTimeout(2, TimeUnit.MINUTES);
		wait.ignoring(NoSuchElementException.class); //make sure that this exception is ignored
		Function<WebDriver, WebElement> function = new Function<WebDriver, WebElement>()
				{
					public WebElement apply(WebDriver arg0) {
						System.out.println("Checking for the element!!");
						WebElement element = arg0.findElement(By.id("target"));
						if(element != null)
						{
							System.out.println("Target element found");
						}
						return element;
					}
				};

		wait.until(function);
	}

}
```

## 什么是谓词？

谓词类似于函数，但它总是返回一个布尔表达式。你可以像这样定义谓词

```java
		Predicate<WebDriver> predicate = new Predicate<WebDriver>()
				{

					public boolean apply(WebDriver arg0) {

						return false;
					}

				};
```

在 apply 方法中实现一些条件，以便它在成功时返回 true，否则返回 false，这只是时间问题。可以使用这样的谓词等待变色按钮

```java
public static void main(String[] args) throws InterruptedException {

		WebDriver driver = new FirefoxDriver();
		driver.get("https://demoqa.com/browser-windows/");

		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
		wait.pollingEvery(250,  TimeUnit.MILLISECONDS);
		wait.withTimeout(2, TimeUnit.MINUTES);
		wait.ignoring(NoSuchElementException.class); //make sure that this exception is ignored

		Predicate<WebDriver> predicate = new Predicate<WebDriver>()
				{

					public boolean apply(WebDriver arg0) {
						WebElement element = arg0.findElement(By.id("colorVar"));
						String color = element.getAttribute("color");
						System.out.println("The color if the button is " + color);
						if(color.equals("red"))
						{
							return true;
						}
						return false;
					}
				};
				wait.until(predicate);
	}
```