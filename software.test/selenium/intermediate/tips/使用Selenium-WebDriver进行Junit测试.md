欢迎阅读有关 JUnit 的后续系列中的这个新教程。到目前为止，我们讨论了以下有关 JUnit 的主题：

[介绍](https://toolsqa.com/java/junit-framework/junit-introduction/)

[第一次 Junit 测试](https://toolsqa.com/java/junit-framework/first-junit-test/)

[Junit 测试运行程序](https://toolsqa.com/java/junit-framework/junit-test-runner/)

[多种测试方法和测试类](https://toolsqa.com/java/junit-framework/adding-multiple-tests-test-classes/)

[JUnit 测试套件](https://toolsqa.com/java/junit-framework/junit-test-suite/)

[使用 Eclipse 进行 JUnit 测试](https://toolsqa.com/java/junit-framework/running-junit-tests-eclipse/)

现在让我们谈谈如何在 Junit 中编写我们的 Selenium 测试？或者在回答这个问题之前，我们是否应该将 Selenium 测试编写为 Junit 的一部分？

Junit 是单元级测试的框架，Selenium 是功能级测试。我建议不要混合使用它们，因为单元和功能级别的测试是完全不同的。话虽如此，但这并不意味着我们不能使用 Junit 来编写我们的 Selenium 测试。或者我宁愿建议你使用 TestNG Framework，因为它比 JUnit 强大得多，并且它提供了我们在 Selenium 中实际需要的漂亮的 HTML 报告。

为了做到这一点，让我们在 Junit 中编写两个 Selenium 测试，让我们探索更多的注释。

### JUnit Selenium 测试用例

```java
package unitTests;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumTest {
	 	private static FirefoxDriver driver;
	 	WebElement element;

	 @BeforeClass
     public static void openBrowser(){
         driver = new FirefoxDriver();
         driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		} 

	 @Test
     public void valid_UserCredential(){

		 System.out.println("Starting test " + new Object(){}.getClass().getEnclosingMethod().getName());
	     driver.get("https://www.store.demoqa.com");	
	     driver.findElement(By.xpath(".//[@id='account']/a")).click();
	     driver.findElement(By.id("log")).sendKeys("testuser_3");
	     driver.findElement(By.id("pwd")).sendKeys("Test@123");
	     driver.findElement(By.id("login")).click();
	     try{
			 element = driver.findElement (By.xpath(".//[@id='account_logout']/a"));
		 }catch (Exception e){
			}
	     Assert.assertNotNull(element);
	     System.out.println("Ending test " + new Object(){}.getClass().getEnclosingMethod().getName());
     }

	 @Test
     public void inValid_UserCredential()
     {
		 System.out.println("Starting test " + new Object(){}.getClass().getEnclosingMethod().getName());
	     driver.get("https://www.store.demoqa.com");	
	     driver.findElement(By.xpath(".//[@id='account']/a")).click();
	     driver.findElement(By.id("log")).sendKeys("testuser");
	     driver.findElement(By.id("pwd")).sendKeys("Test@123");
	     driver.findElement(By.id("login")).click();
	     try{
			element = driver.findElement (By.xpath(".//[@id='account_logout']/a"));
	     }catch (Exception e){
			}
	     Assert.assertNotNull(element);
	     System.out.println("Ending test " + new Object(){}.getClass().getEnclosingMethod().getName());
     }

	 @AfterClass
	 public static void closeBrowser(){
		 driver.quit();
	 }
}
```

## 刚才发生了什么...

让我们逐块了解它

1.  课前注解

```java
 @BeforeClass
     public static void openBrowser()
     {
         driver = new FirefoxDriver();
         driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
     }
```

类前注释将告诉 Junit 在开始任何测试之前运行这段代码。如你所见，我们没有在测试方法'valid_UserCredential()' & 'Invalid_UserCredential()'中启动任何浏览器。所以我们的测试需要一个浏览器来执行步骤。在BeforeClass的帮助下，我们将能够在运行测试之前打开浏览器。

1.  测试方法

```java
 @Test
     public void valid_UserCredential(){

		 System.out.println("Starting test " + new Object(){}.getClass().getEnclosingMethod().getName());
	     driver.get("https://www.store.demoqa.com");	
	     driver.findElement(By.xpath(".//[@id='account']/a")).click();
	     driver.findElement(By.id("log")).sendKeys("testuser_3");
	     driver.findElement(By.id("pwd")).sendKeys("Test@123");
	     driver.findElement(By.id("login")).click();
	     try{
			 element = driver.findElement (By.xpath(".//[@id='account_logout']/a"));
		 }catch (Exception e){
			}
	     Assert.assertNotNull(element);
	     System.out.println("Ending test " + new Object(){}.getClass().getEnclosingMethod().getName());
     }
```

这是对我们的演示应用程序登录功能的简单测试。但是你可以在其中注意到的是 try/catch block & assert 语句。此 try 块用于测试元素是否存在，如果未找到该元素，则该元素将保持为 null。使用该断言语句将验证元素不为空。assert 语句的基本规则是它们仅在失败时起作用。我们很快就会写一个完整的关于 Junit 断言的章节。

1.  课后法

```java
@AfterClass
 public static void closeBrowser(){
	 driver.quit();
       }
```

这是为了告诉 Junit 引擎在所有测试执行完毕后执行这段代码。这将最终关闭浏览器，因为在此之后，任何浏览器都不需要测试来执行任何步骤。

还有其他注释以及@BeforeTest和@AfterTest。这是你在 selenium 示例中使用此注释的练习。

感谢你阅读本文。欢迎通过virender@toolsqa.com提出任何改进我的教程的建议