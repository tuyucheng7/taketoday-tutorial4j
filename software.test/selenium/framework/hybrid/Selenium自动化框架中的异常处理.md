在进入“Selenium 自动化框架中的异常处理”主题之前，最好对错误、异常、异常处理、Try、Catch、Throw 和 Throws 语句有基本的了解。

## 什么是例外

异常 是在程序执行期间发生的事件，它会破坏程序指令的正常流程，或者简单地说，任何使你的测试用例在执行之间停止的问题都是异常。

## 错误和异常之间的区别

错误“表示合理的应用程序不应尝试捕获的严重问题。”

异常“表示合理的应用程序可能想要捕获的条件” 。

每当执行语句时发生错误，它都会创建一个异常对象，然后程序的正常流程停止并尝试找到可以处理引发的异常的人。异常对象包含了很多调试信息，比如方法层次结构、异常发生的行号、异常类型等。当一个方法发生异常时，创建异常对象并交给运行环境的过程是称为  “抛出异常”。

## 什么是异常处理

异常处理是指对编程应用程序和通信错误的预测、检测和解决。它是处理异常对象并通过让我们有机会对其采取行动来帮助我们的代码块。

## 为什么异常处理很重要

1.  想一想你遇到异常并且想要在日志中打印一些自定义消息以便整个团队都能理解的情况。
2.  在某些情况下，你可能只想吃掉异常并希望你的测试在执行的其余部分继续进行。
3.  如果你想在发生特定异常时执行一系列步骤，例如，如果你因为产品缺货而出现异常，则该产品不再显示在页面上并且你希望你使用其他可用产品在页面上。
4.  如果你想在 Selenium 中处理某种异常，例如 ElementNotSelectableException、ElementNotVisibleException、NoSuchElementException 等异常。

## Selenium 中的不同异常

Selenium 文档中提到了完整的异常列表，你在测试过程中可能会遇到也可能不会遇到这些异常。

最常见的异常：

1) NoSuchElementException : FindBy 方法找不到元素。

2) StaleElementReferenceException ：这表明该元素不再出现在 DOM 页面上。

3) TimeoutException：这表明执行失败，因为命令没有在足够的时间内完成。

4) ElementNotVisibleException: 抛出表明虽然元素存在于 DOM 上，但它不可见，因此无法与之交互

5) ElementNotSelectableException：抛出表示可能是元素被禁用，所以无法选择。

## 如何处理异常

Try/Catch：一种方法使用 try 和 catch 关键字的组合来捕获异常。Try是块的开始， Catch在 try 块的末尾以处理异常。try/catch 块放置在可能产生异常的代码周围。try/catch 块中的代码称为受保护代码，使用 try/catch 的语法如下所示：

```java
try

	{

		// Some code

	}catch(Exception e){

		// Code for Handling the exception

	}
```

多个 Catch 块：一个 try 块后面可以跟多个 catch块。就像我之前说的，有多个异常，你可以在一个代码块上预期不止一种类型的异常，如果你想用单独的代码块分别处理每种类型的异常。多个 catch 块的语法如下所示：

```java
try

	{

	   //Some code

	}catch(ExceptionType1 e1){

	   //Code for Handling the Exception 1

	}catch(ExceptionType2 e2){

	   //Code for Handling the Exception 2

	}
```

catch 块的数量没有限制，可以使用两个以上。你可能想知道它是如何工作的。很简单，如果受保护的代码发生异常，异常会被抛到列表中的第一个catch块。如果抛出的异常与 ExceptionType1 匹配，它会在那里被捕获并执行同一异常块下的代码。如果不是，则异常传递到第二个 catch 语句并继续这样。

注意：如果异常与任何异常类型都不匹配并且落在所有捕获中，则当前方法停止执行并抛出异常。这就是为什么建议最后也包含默认异常，这样万一异常失败，它可以由默认异常处理。

Throw : 有时我们希望在我们的代码中显式地产生异常，例如在 Selenium Automation Framework 中，大多数时候我们打印自己编写的日志，一旦我们捕获到一个异常，然后我们需要将该异常抛回给系统，以便测试用例可以终止。throw 关键字用于将异常抛出给运行时处理。

Throws：当我们在方法中抛出任何异常而不处理它时，我们需要在方法签名中使用throws 关键字来让调用程序知道该方法可能抛出的异常。

```java
// Method Signatur\

public static void anyFunction() throws Exception{

    try{

		// write your code here    

	}catch (Exception e){

		// Do whatever you wish to do here

		// Now throw the exception back to the system

        throw(e);

        }

    }
```

多个异常：我们可以在 throws 子句中提供多个异常。

```java
public static void anyFunction() throws ExceptionType1, ExceptionType2{

	try	{

		//Some code 

	}catch(ExceptionType1 e1){ 

		//Code for Handling the Exception 1

	}catch(ExceptionType2 e2){

		//Code for Handling the Exception 2

	}
```

Finally：finally 关键字用于创建跟在 try 块之后的代码块。finally 代码块始终执行，无论是否发生异常。

```java
try

	{

	   //Protected code

	}catch(ExceptionType1 e1)

	{

	   //Catch block

	}catch(ExceptionType2 e2)

	{

	   //Catch block

	}catch(ExceptionType3 e3)

	{

	   //Catch block

	}finally

	{

	   //The finally block always executes.

	}
```

# Selenium 中的异常处理

你的 Selenium 测试应该能够失败，但不是因为抛出的异常。如果你的测试因异常而失败，那么很可能你没有异常处理。通过这样做，你没有机会在测试结束时清理 WebDriver 对象。

测试应该仅在你的条件下失败，例如，你永远不应该得到像 NullPointerException 这样的异常，但是如果你得到像 ElementNotFoundException 这样的异常，那么捕获异常、停止进一步执行并以合乎逻辑的方式。

示例 1：我不使用任何页面对象工厂，但我使用我自己的页面对象模式，并且我总是打印错误日志并截取我遇到的任何异常。请看下面的代码：

```java
public static WebElement btn_ReportCategory(WebDriver driver) throws Exception{

        try{

            WebElement element = driver.findElement(By.linkText("+ Report Categories"));

        }catch (Exception e){

			// Printing logs for my report

            Log.error("Report Category button element is not found.");

			// Taking screenshot for defect reporting

			Utils.captureScreenShot();

			// After doing my work, now i want to stop my test case

            throw(e);

        }

		// This will return the Element in case of no Exception

        return element;

    }
```

示例 2：使用 Selenium WebDriver 的 TimeoutException。

```java
try{

    myTestDriver.findElement(By.xpath("//[@id='register']")).click();

}catch (TimeoutException toe) {

	wait.until( ExpectedConditions.elementToBeClickable(By.xpath("//[@id='register']")));

	myTestDriver.findElement(By.xpath("//[@id='register']")).click();

}catch (Exception e) {

	Log.error("Register element is not found.");

	throw(e);

    }

}
```

示例 3：假设你要在 Selenium WebDriver 中验证页面上是否存在任何元素。你将无法使用元素定位器获得此信息，因为如果该元素存在，你的定位器将起作用，并且你可以轻松地打印出该元素存在，但如果你的元素不在页面上，你的定位器将失败并简单地抛出异常。这种情况用自写函数很容易处理。

```java
public static boolean verifyObjectPresent(WebDriver driver) {

	    try {

	    	driver.findElement(By.linkText("+ Report Categories"));

	    	return true;

	    } catch (Exception e) { 

	    	return false;

	    }

	}
```