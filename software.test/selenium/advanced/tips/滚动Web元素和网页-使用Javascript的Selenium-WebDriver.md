## 将元素滚动到视图中

你一定遇到过将元素滚动到用户视图中的问题。只是为了截图或启用元素可见性，你最终需要滚动元素。

Selenium 通常在执行任何操作之前执行此操作。然而，以非确定性方式滚动，从某种意义上说，你必须执行一个操作来滚动要查看的元素。这种方式在大多数情况下就足够了，但我们仍然可以理解如何使用 javascript 更确定地执行滚动操作。

让我们从一种情况开始，我们将看到解决这种情况的不同方法

情况

假设在你的页面上，你有一个元素位于网页末尾附近的某处。你想让这个元素进入视图。你希望确定性地执行此操作，以便你始终可以控制元素的位置。

方法一

在 JavaScript 中使用元素对象的 element.scrollIntoView() 方法。该方法的签名是

元素.scrollIntoView(alignWithTop)

alignWithTop 是一个布尔值，如果你想让你的元素出现在视图中，元素的顶部接触浏览器视口的顶部，则将其设置为 true，否则为 false。

这是代码清单

```java
	public static void main(String[] args) throws Exception {

		// TODO Auto-generated method stub

		FirefoxDriver ff = new FirefoxDriver();

		ff.get("https://toolsqa.com");

		Thread.sleep(5000);

		ff.executeScript("document.getElementById('text-8').scrollIntoView(true);");

        }
```

方法二

我们逐行滚动页面，直到我们的网络元素可见。为此，我们将使用 JavaScript 函数

window.scrollByLines(行数)

NumberofLines 是一个整数值，指定要滚动的行数。正值表示向下滚动，负值表示向上滚动。

这是这个的代码清单

```java
public static void main(String[] args) throws Exception {

		// TODO Auto-generated method stub		

		FirefoxDriver ff = new FirefoxDriver();

		ff.get("https://toolsqa.com");

		Thread.sleep(5000);

		//ff.executeScript("document.getElementById('text-8').scrollIntoView(false);");

		WebElement targetElement = ff.findElementById("text-8");

		int x = 0;

		while((Double) ff.executeScript("return document.getElementById('text-8').getBoundingClientRect().top") > 0 )

		{

			x = x + 2;

			ff.executeScript("window.scrollByLines(2)");

			System.out.println("Client top is = " + (Double) ff.executeScript("return document.getElementById('text-8').getBoundingClientRect().top"));			

		}

		System.out.println("Element is visible after  " + x + " scrolls");

}
```

这是一种垂直滚动方法。这在 100% 缩放时应该足够了。但是，如果你的页面不是 100% 或大于浏览器的视口，你可以执行类似的水平滚动。如果你在第二种方法中看到，我们假设该元素位于页面视图的下方。情况可能并非总是如此，请随时修改方法以适应你也必须向上滚动的情况。将其作为作业并在评论中发布你的代码。

如果通过 javaScript 可以使用滚动选项，我希望本文能让你有一个基本的了解。通过更详细的 JavaScript 函数，你可能会得到一些真正值得使用的东西。