## 如何在 Selenium WebDriver 中创建对象存储库

我们收到了很多问题，我们的读者希望了解更多关于Selenium WebDriver 中的对象存储库以及如何创建对象存储库的信息。我想现在是时候写一篇关于这个主题的文章了。

### 什么是对象存储库？

对象存储库是 UI 元素与其定位器之间的映射。这也可以写成 UI 元素和查找它的方式之间的对象映射。在Selenium WebDriver 的上下文中，它表示WebElement与相应定位器类型和值之间的映射。让我们通过查看查找元素的典型 WebDriver 代码来更详细地了解它。

```java
WebDriver browser = new FirefoxDriver();
driver.get("https://toolsqa.com");
WebElement loginElement = driver.findElement(By.id("Element01"));
```

我们可以看到findElement命令接受WebElement的定位器。在上面的示例中，定位器是按 Id ， Id的值 是Element01。元素的名称是loginElement。我们需要三个不同的值来标识 Web 元素：

1.  元素名称
2.  元素的定位器类型。基本上我们正在使用什么
3.  定位器的价值

给出这三个值可以帮助我们在网页上找到唯一的元素。这就是对象存储库的构成。

### 对象存储库的存储

现在问题来了，我们应该在哪里存储应用程序中存在的各种元素的映射。答案并不简单，这取决于你使用的是哪种框架。有些框架将对象存储库存储在属性文件中，有些则将其存储在 XML 或 Excel 文件中。今天我们将学习如何将这些值存储在属性文件中。我们还将学习如何读取这些值并在测试代码中使用它。

### 属性文件中的对象存储库

我在本教程中使用的测试页面是[“https://demoqa.com/automation-practice-form”](https://demoqa.com/automation-practice-form)，在这个页面上我们有三个元素名字和姓氏字段以及一个链接元素，其中“部分链接文本”作为链接文本。让我们尝试为这三个元素创建一个存储库。属性文件将信息存储在键值对中。 键值对由两个由等号分隔的字符串值表示。典型的属性文件如下所示：

![属性文件](https://www.toolsqa.com/gallery/selnium%20webdriver/1.PropertiesFile.png)

在我们的例子中，我们必须存储三个值而不是两个。元素名称、定位器类型和定位器值。我们将简单地用' : '分隔两个值并使用它。这是我们将创建的示例对象存储库文件，并将其命名为ObjectRepo.properties。

![属性或](https://www.toolsqa.com/gallery/selnium%20webdriver/2.PropertiesOR.png)

注意：在属性文件中，可以使用# 关键字添加注释。如上所示，显示为绿色的部分基本上是注释，不会被属性文件阅读器读取。

### 读取对象存储库属性文件

Java 有支持或.properties文件。一个人可以通过三个简单的步骤轻松读取属性文件

1.  在.properties文件上创建一个FileInputStream对象
2.  在步骤 1 中创建的文件输入流上创建 Properties 对象
3.  只需使用getProperty("Property name");读取键值对 Properties 类上的方法。

为了读取对象存储库，我们将创建一个简单的类并将其命名为RepositoryParser.java。 这是代码

```java
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.openqa.selenium.By;

public class RespositoryParser {

	private FileInputStream stream;
	private String RepositoryFile;
	private Properties propertyFile = new Properties();

	public RespositoryParser(String fileName) throws IOException
	{
		this.RepositoryFile = fileName;
		stream = new FileInputStream(RepositoryFile);
		propertyFile.load(stream);
	}

	public By getbjectLocator(String locatorName)
	{
		String locatorProperty = propertyFile.getProperty(locatorName);
		System.out.println(locatorProperty.toString());
		String locatorType = locatorProperty.split(":")[0];
		String locatorValue = locatorProperty.split(":")[1];

		By locator = null;
		switch(locatorType)
		{
		case "Id":
			locator = By.id(locatorValue);
			break;
		case "Name":
			locator = By.name(locatorValue);
			break;
		case "CssSelector":
			locator = By.cssSelector(locatorValue);
			break;
		case "LinkText":
			locator = By.linkText(locatorValue);
			break;
		case "PartialLinkText":
			locator = By.partialLinkText(locatorValue);
			break;
		case "TagName":
			locator = By.tagName(locatorValue);
			break;
		case "Xpath":
			locator = By.xpath(locatorValue);
			break;
		}
		return locator;
	}
}
```

代码说明： RepositoryParser类在构造函数中接受.properties文件路径。一旦收到文件路径，就会创建一个FileInputStream对象。然后将其提供给Java.Util.Properties类以创建键值对之间的属性映射。一旦我们生成了键值映射，我们就可以从对象存储库中查询值。所有核心逻辑都发生在getbjectLocator(String elementName)方法中。这里的elementName 只是你要查找的元素。这个方法最后返回一个 By 定位器，具有正确的定位器类型和定位器值。

### 对象存储库解析器在测试中的使用

让我们创建一个简单的 TestNg 测试类来展示用法。我们只需要一个RepositoryParser类。这是测试。

```java
package Tests;

import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import RepoUtils.RespositoryParser;

public class SampleTests {

	private RespositoryParser parser;
	private WebDriver driver;

	@BeforeClass
	public void setUp() throws IOException
	{
		driver = new FirefoxDriver();
		driver.get("https://demoqa.com/automation-practice-form");
		parser = new RespositoryParser("ObjectRepo.properties");
	}

	@Test
	public void EnterValue()
	{
		//Lets see how we can find the first name field
		WebElement FirstName = driver.findElement(parser.getbjectLocator("FirstName"));
		WebElement LastName = driver.findElement(parser.getbjectLocator("LastName"));
		FirstName.sendKeys("Virender");
		LastName.sendKeys("Singh");
	}

	@Test
	public void FindPartialLink()
	{
		WebElement link = driver.findElement(parser.getbjectLocator("PartialLink"));
		link.click();
	}

	@AfterClass
	public void tearDown()
	{
		driver.quit();
	}

}
```

我希望这能给你一个理解和创建对象存储库的起点 。我们将在接下来的教程中使用基于 Excel 的对象存储库。