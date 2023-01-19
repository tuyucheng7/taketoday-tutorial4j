你一定一直渴望从 Webdriver 获得更多日志，以便你可以调试脚本或记录有关测试的更多信息。这是你的答案，EventFiringWebDriver和 WebDriverEventListner。EventFiringWebDriver 是一个类，用于包装你的webdriver 以引发事件，而WebDriverEventListner是你必须实现以捕获webdriver事件的接口。如果你不明白我刚刚写的内容，请不要担心，我会逐步向你解释。

首先要了解的是你的EventFiringWebDriver类。这是一个实现 WebDriver 接口的类。这到底意味着什么？这意味着你将获得所有常规的webdriver方法，例如

-   通过 Id 查找元素
-   通过标签名查找元素

除此之外，你还有两种方法

\- 注册(WebDriverEventListener 事件监听器)

-   注销(WebDriverEventListener 事件监听器)

Register方法将允许你注册我们的 WebDriverEventListner实现 以侦听WebDriver 事件，并且通过注销你将能够分离。

## 第 1 步：实现 Selenium WebDriver 事件监听器接口

我创建了一个名为EventHandler的类并实现了WebDriverEventListner。实现WebDriverEventListner后，你将必须实现方法列表，如下面的代码所示。

```java
package test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

public class EventHandler implements WebDriverEventListener{

	public void afterChangeValueOf(WebElement arg0, WebDriver arg1) {
		// TODO Auto-generated method stub

	}

	public void afterClickOn(WebElement arg0, WebDriver arg1) {
		// TODO Auto-generated method stub

	}

	public void afterFindBy(By arg0, WebElement arg1, WebDriver arg2) {
		// TODO Auto-generated method stub

	}

	public void afterNavigateBack(WebDriver arg0) {
		// TODO Auto-generated method stub

	}

	public void afterNavigateForward(WebDriver arg0) {
		// TODO Auto-generated method stub

	}

	public void afterNavigateTo(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub

	}

	public void afterScript(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub

	}

	public void beforeChangeValueOf(WebElement arg0, WebDriver arg1) {
		// TODO Auto-generated method stub

	}

	public void beforeClickOn(WebElement arg0, WebDriver arg1) {
		// TODO Auto-generated method stub

	}

	public void beforeFindBy(By arg0, WebElement arg1, WebDriver arg2) {
		// TODO Auto-generated method stub

	}

	public void beforeNavigateBack(WebDriver arg0) {
		// TODO Auto-generated method stub

	}

	public void beforeNavigateForward(WebDriver arg0) {
		// TODO Auto-generated method stub

	}

	public void beforeNavigateTo(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub

	}

	public void beforeScript(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub

	}

	public void onException(Throwable arg0, WebDriver arg1) {
		// TODO Auto-generated method stub

	}

}
```

这里每个方法对应一个事件。例如。afterNavigateTo ()方法。每次导航到页面完成时都会调用此方法。

```java
public void afterNavigateTo(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
	}
```

我们所要做的就是将一些代码放入方法中，以便每次页面导航到其他页面时执行我们的代码。理想情况下，你将在此处添加日志记录语句。我已经实现了简单的打印语句，如下面更有意义的实现所示

```java
package test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

public class EventHandler implements WebDriverEventListener{

	public void afterChangeValueOf(WebElement arg0, WebDriver arg1) {
		// TODO Auto-generated method stub

		System.out.println("inside method afterChangeValueOf on " + arg0.toString());
	}

	public void afterClickOn(WebElement arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		System.out.println("inside method afterClickOn on " + arg0.toString());
	}

	public void afterFindBy(By arg0, WebElement arg1, WebDriver arg2) {
		// TODO Auto-generated method stub
		System.out.println("Find happened on " + arg1.toString() 
				+ " Using method " + arg0.toString());
	}

	public void afterNavigateBack(WebDriver arg0) {
		// TODO Auto-generated method stub

		System.out.println("Inside the after navigateback to " + arg0.getCurrentUrl());
	}

	public void afterNavigateForward(WebDriver arg0) {
		// TODO Auto-generated method stub
		System.out.println("Inside the afterNavigateForward to " + arg0.getCurrentUrl());
	}

	public void afterNavigateTo(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		System.out.println("Inside the afterNavigateTo to " + arg0);
	}

	public void afterScript(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		System.out.println("Inside the afterScript to, Script is " + arg0);
	}

	public void beforeChangeValueOf(WebElement arg0, WebDriver arg1) {
		// TODO Auto-generated method stub

		System.out.println("Inside the beforeChangeValueOf method");
	}

	public void beforeClickOn(WebElement arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		System.out.println("About to click on the " + arg0.toString());

	}

	public void beforeFindBy(By arg0, WebElement arg1, WebDriver arg2) {
		// TODO Auto-generated method stub
		System.out.println("Just before finding element " + arg1.toString());

	}

	public void beforeNavigateBack(WebDriver arg0) {
		// TODO Auto-generated method stub
		System.out.println("Just before beforeNavigateBack " + arg0.getCurrentUrl());

	}

	public void beforeNavigateForward(WebDriver arg0) {
		// TODO Auto-generated method stub
		System.out.println("Just before beforeNavigateForward " + arg0.getCurrentUrl());

	}

	public void beforeNavigateTo(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		System.out.println("Just before beforeNavigateTo " + arg0);
	}

	public void beforeScript(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		System.out.println("Just before beforeScript " + arg0);
	}

	public void onException(Throwable arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		System.out.println("Exception occured at " + arg0.getMessage());

	}

}
```

一旦你完成了你的事件处理程序，就可以抛出一些事件了。

## 第 2 步：创建事件抛出 WebDriver

1) 创建一个常规的WebDriver。

```java
FirefoxDriver driver = new FirefoxDriver();
```

2) 现在使用我们上面创建的常规WebDriver创建一个EventThrowingWebDriver。

```java
EventFiringWebDriver eventDriver = new EventFiringWebDriver(driver);
```

1.  创建你的eventHandler类的实例，并使用上面创建的EventFiringWebDriver对象的注册方法为事件注册它。这是代码

```java
 EventHandler handler = new EventHandler();
 eventDriver.register(handler);
```

现在执行常规操作，你将看到如何捕获所有事件以及我们设置的打印语句如何打印相应的日志。

这是测试，请按照步骤修改你的日志记录或你希望在事件调用中执行的任何操作。

```java
package test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public class programMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

         FirefoxDriver driver = new FirefoxDriver();
         EventFiringWebDriver eventDriver = new EventFiringWebDriver(driver);

		EventHandler handler = new EventHandler();
		eventDriver.register(handler);
		eventDriver.get("https://toolsqa.com/automation-practice-switch-windows/");
		WebElement element = eventDriver.findElement(By.id("target"));
		element.click();

	}

}
```

运行此代码后，控制台屏幕上的输出将如下所示：

![事件](https://www.toolsqa.com/gallery/selnium%20webdriver/1.events.png)

现在一旦你完成了事件，不要忘记使用 Unregister 方法注销你的类。