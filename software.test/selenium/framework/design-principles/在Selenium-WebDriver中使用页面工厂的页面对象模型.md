[Selenium Automation](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)使你能够减少手动工作，但你可能会遇到许多在测试执行中重复的功能。因此，你可能不得不多次使用同一个定位器。现在，如果你多次复制相同的代码，可能会导致项目难以管理。最后，如果定位器发生变化，你将不得不再次检查整个代码并在使用该定位器的所有地方进行更改。因此，为了克服这个问题，Selenium自动化框架中实现了使用页面工厂的页面对象模型。它通过将测试对象与测试脚本分开来帮助维护测试脚本。随后，在本文中，你将了解PageFactory如何 可以在你的Selenium测试自动化中使用以创建优化的框架。我们将涵盖以下几点 -

-   什么是页面对象模式？
    -   如何实现页面对象模式？
-   Selenium 中的 PageFactory 是什么？
    -   什么是 PageFactory 注解？
-   如何使用PageFactory实现页面对象模型？
    -   如何在 Selenium 项目中创建页面工厂模式？

## 什么是页面对象模式？

Page Object Model /Pattern 是Selenium中使用的一种设计模式，我们在其中创建一个对象存储库来存储 Web 元素。创建一个java类对应每个网页，由页面上的[WebElements](https://www.toolsqa.com/selenium-webdriver/webelement-commands/)和相应的作用于元素的方法组成。简而言之，所有网页元素都在一个 java 类中，通过它们的[定位器](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)来识别它们。此外，网站的不同网页会创建多个 java 类。这些 java 类充当存储库，用于存储我们可以在测试用例中与之交互的各种元素。因此，一个简单的页面对象模型/模式的项目结构通常如下所示：

![页面对象模型项目的结构](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Structure%20of%20a%20Page%20Object%20Model%20Project.png)

你可以看到，在上图中，不同的页面类与不同包中的测试类保持分离。

### 如何实现页面对象模式？

Page Object Patten的简单实现可以发生，如下所示：

![页面对象模式设计](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Page%20Object%20Pattern%20Design.png)

你可以看到页面元素如何从页面对象模式中的测试用例中分离出来。现在我们将看到如何实现相同的。有两种方法可以在你的测试项目中实现页面对象模式：

1.  首先，使用常规 Java 类- 在这种方法中，我们为包含该页面上各种 Web 元素的不同页面创建 Java 类。此外，我们在驻留在不同类中的测试用例中使用这些 Web 元素。此外，你可以查看[页面对象模型](https://www.toolsqa.com/selenium-webdriver/page-object-model/)文章以了解有关此方法的更多信息。
2.  其次，使用页面工厂类——它是对页面对象设计模式的扩展，我们将在本文中对此进行讨论。

## Selenium 中的页面工厂是什么？

[Page Factory](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/support/PageFactory.html)是[Selenium WebDriver](https://toolsqa.com/selenium-webdriver/selenium-testing/)提供的一个实现Page Object Model的类。页面对象存储库使用页面工厂概念测试方法分开使用它，你可以初始化页面对象或直接实例化它们。简而言之，页面对象模型允许你为网站的不同页面创建单独的 java 类。这些不同的类包含不同网络元素的定位器(如按钮、文本字段、下拉列表等.) 出现在页面上以及对这些元素执行操作的方法。通过这样做，你可以简化代码并分离测试方法和对象存储库。随后，我们需要通过两个简单的步骤在 Selenium 项目中定义和使用页面工厂：

1.  使用@FindBy 注释- 与使用[FindElement](https://www.toolsqa.com/selenium-webdriver/find-element-selenium/)或 FindElements初始化网页元素的常规方法不同，页面工厂使用 @FindBy 注释。Page Factory 中使用的注释是描述性的。此外，它们有助于提高代码的可读性，我们将在下一节中讨论这一点。它提供了以下语法来定位网络元素：

```java
@FindBy(id="userName") 
WebElement username;
```

1.  使用 initElements() 初始化元素- 这是一个静态方法，用于初始化我们使用@FindBy或其他注释定位的 Web 元素，从而实例化页面类。

```java
PageFactory.initElements(WebDriver driver, java.lang.Class.pageObjectClass);
```

Page Factory 提供的另一个有趣的概念是使用AjaxElementLocatorFactory的延迟加载概念。当你的应用程序使用 Ajax 元素时可以使用它。此外，你可以在尝试查找元素以执行操作并传递超时值时使用它，驱动程序将等待该值直到抛出异常。换句话说，它是 使用类AjaxElementLocatorFactory的[隐式等待](https://www.toolsqa.com/selenium-webdriver/wait-commands/)的变体。随后，语法如下 -

```java
PageFactory.initElements(new AjaxElementLocatorFactory(driver, 20), this);
```

### 什么是页面工厂注解？

正如上面所讨论的，Page Factory 注释非常具有描述性并且使代码非常可读。此外，注释有助于为网络元素设置位置策略。Page Factory中使用的重要注解讨论如下：

#### @FindBy

@FindBy注释是页面工厂方法的本质。它用于使用不同的定位器策略来定位 Web 元素。此外，它有助于使用一个搜索条件快速定位 Web 元素。在声明WebElement之前，我们传递它的属性和相应的值。随后，有两种方法可以使用这个注解：

-   首先，通过指定“如何”和“使用”

```java
@FindBy(how = How.ID, using = "userName")
WebElement username;
```

-   其次，通过直接使用定位器(id、XPath、CSS 等)，你可以探索更多关于[使用 Selenium 查找元素](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)的方法。

```java
@FindBy(id="userName")
WebElement username;
```

以上两个注解都指向同一个 web 元素。

注意：“How”可以与任何定位器一起使用，即“id”、“name”、“XPath”等。

#### @FindBys

要定位具有多个搜索条件的 Web 元素，你可以使用@FindBys注释。此注释通过 在搜索条件上使用AND条件来定位 Web 元素。简单来说，@FindBys为每个搜索条件使用多个@FindBy 。

考虑以下HTML代码：

```java
<div class = "custom-control-check-box">
   <input type="checkbox" id="game-chk-box" class="custom-control-input" value="1"/>
</div>
```

对上述元素使用@FindBys注释的 Web 元素定位器如下所示：

```java
@FindBys({
 @FindBy(class="custom-control-check-box"),
 @FindBy(id="game-chk-box")
})

WebElement chkBox;
```

只有当两个条件都满足时，我们才能找到 web 元素' chkbox ' ，即类值为“ custom-control-check-box ”，id 值为“ game-chk-box ”。另请注意，标准是在父子关系中提到的。class 标准来自父标签，而 id 标准来自子标签。`<div>``<input>`

#### @找到所有

@FindAll注释使用多个条件定位 Web 元素，前提是至少有一个条件匹配。与@FindBys相反，它在多个@FindBy之间使用OR 条件关系。

考虑下面的HTML代码：

```java
<button id = "submit" type = "submit" name= "sbmtBtn" class =" btn btn-primary">Submit</button>
```

可以使用@FindAll注释定位元素，如下所示：

```java
@FindAll({
 @FindBy(id="btn", //doesn't match
 @FindBy(name="sbmtBtn"), //Matches
 @FindBy(class="btn-primary") //doesn't match
})
```

WebElement 提交按钮；

即使只有一个条件匹配@FindAll适用于一个或多个条件，上面的注释也会定位提交按钮。

#### @CacheLookUp

当你多次引用同一个 Web 元素时， @CacheLookUp注释非常有用。考虑一个应用程序，其中每个测试用例都需要登录操作。在这种情况下，使用@CacheLookUp ，我们可以在第一次读取后立即将 Web 元素存储在缓存内存中。它紧固我们的执行和代码，无需在网页上查找元素并直接从内存中引用它。

@CacheLookUp可以使用上面讨论的任何注释作为前缀，即@ FindBy、@FindBys 和@FindAll。

```java
@CacheLookUp
@FindBys({
 @FindBy(class="custom-control-check-box"),
 @FindBy(id="game-chk-box") 
})

WebElement chkBox;
```

注意：它总是将@CacheLookUp 与属性值很少变化的网络元素一起使用。由于如果定位器值更改，它会在内存中维护一个引用，因此你的代码将无法定位该元素，因为它始终会引用缓存内存。

在下一节中，你将看到在使用Selenium 中的 Page Factory自动化测试时使用上述所有注释。

## 如何使用PageFactory实现页面对象模型？

知道页面工厂是什么了吗？我们如何处理它的各种注释？我们将通过页面对象模型在一个简单的用例中实现页面工厂。

考虑以下自动化场景：

-   打开[演示网站](https://demoqa.com/login)的 URL 。
-   输入用户名和密码。
-   单击“登录”按钮。
-   验证登录的用户名。
-   单击注销按钮。

要使用Selenium 中的页面工厂自动化上述场景，我们将遵循以下描述的一系列步骤 -

### 如何在 Selenium 项目中创建页面工厂模式？

按照下面提到的步骤创建页面工厂并自动化上面提到的场景：

1.  首先，为每个页面创建一个 Java 类。

在我们的示例中，我们将使用两个页面 -[登录页面](https://demoqa.com/login)和[配置文件页面](https://demoqa.com/books)。我们将按照页面对象模型方法为每个页面创建两个单独的类。

![使用页面工厂的页面对象模型的 Java 类](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Java%20classes%20for%20Page%20Object%20Model%20using%20Page%20Factory.png)

1.  其次，我们将使用@FindBy、@FindBys 和@FindAll注释定位 Web 元素，并在我们在步骤#1 中创建的 Java 类中为将在这些 Web 元素上执行的操作创建方法。此外，我们还为注销按钮使用了@CacheLookUp注释，将引用存储在缓存内存中以备将来使用。请注意，对应于页面的元素将保存在它们各自的类中。

这两个 java 类的代码如下所示：

登录类

```java
package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

public class Login {
	
	final WebDriver driver;
	
	//Constructor, as every page needs a Webdriver to find elements
	public Login(WebDriver driver){
			this.driver=driver;
		}
		
	//Locating the username text box
	@FindAll({
		@FindBy(id="wrapper"),
		@FindBy(id="userName")
	})
	WebElement username;
	
	//Locating the password text box
	@FindBy(id="password")
	WebElement pswd;
	
	//Locating Login Button
	@FindBy(id="login")
	WebElement loginBtn;
	
	
	//Method that performs login action using the web elements
	public void LogIn_Action(String uName, String pwd){
		username.sendKeys(uName);
		pswd.sendKeys(pwd);
		loginBtn.click();
	}
}
```

配置文件类

```java
package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

public class Profile {

final WebDriver driver;
	
	//Constructor, as every page needs a Webdriver to find elements
	public Profile(WebDriver driver){
			this.driver=driver;
		}
		
	@FindBys({
		@FindBy(id="books-wrapper"),
		@FindBy(id="userName-value")
	})
	WebElement user;
	
	@CacheLookup
	@FindBy(id="submit")
	WebElement logoutBtn;
	
	//Method to check logged in username
	public void verifyUser(String usrNm){

		if(user.getText().equalsIgnoreCase(usrNm))
			System.out.println("Correct username, ie " +user.getText());
		
		else
			System.out.println("Incorrect username..." +user.getText());
	}
	
	//method to logout
	public void logout_Action(){
		System.out.println("Let's log off now!!!!");
		logoutBtn.click();
	}
}
```

3. 第三，定位到web元素，并写好对应的方法，接下来就是写测试类了。我们将创建一个名为“testCases”的包，它将包含不同的测试用例类。项目结构如下所示 -

![项目结构_页面工厂](https://www.toolsqa.com/gallery/selnium%20webdriver/4.ProjectStructure_PageFactory.png)

对于我们的示例，我们将创建一个名为“ Login_TC.java ”的测试类，如上面的快照所示。

测试用例

```java
package testCases;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import pages.Login;
import pages.Profile;

public class Login_TC {

static WebDriver driver;
	
public static void main(String[] args) {
		
		driver = new ChromeDriver();
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://demoqa.com/login");
		
		//Instantiating Login & Profile page using initElements()
		Login loginPg = PageFactory.initElements(driver, Login.class);
		Profile profilePg = PageFactory.initElements(driver, Profile.class);
		 
		//Using the methods created in pages class to perform actions
		loginPg.LogIn_Action("---your username---", "---your password---");
		profilePg.verifyUser("---your username---");
		profilePg.logout_Action();
		 
		driver.quit();		
						}

}
```

现在让我们了解实现不同页面类的代码。

-   登录loginPg = PageFactory.initElements (driver, Login.class); & Profile profilePg = PageFactory.initElements (driver, Profile.class); - 我们为每个 Login 和 Profile 类创建一个实例，并使用它来引用其对象。
-   一旦类实例化，你就可以使用相应类的方法来执行特定的操作。此外，要查看类方法，你需要写入实例名称，即“loginPg”后跟一个点，Eclipse 会建议公共方法和其中存在的对象。你可以根据需要使用这些方法中的任何一种。
-   loginPg.LogIn_Action ("---你的用户名---", "---你的密码---"); , profilePg.verifyUser ("---你的用户名---"); & profilePg.logout_Action() ; --我们直接依次调用了'LogIn_Action'、'verifyUser'和'logout_Action'方法来执行我们的测试步骤。
-   我们最终使用“driver.quit()”方法关闭我们的浏览器会话。

注意：你可以通过单击登录屏幕上的“注册”按钮来创建自己的用户进行测试，并在测试脚本中使用它。

随后，让我们执行相同的操作，看看我们的页面工厂实现的结果是怎样的——

![使用页面工厂的页面对象模型的执行结果](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Execution%20Results%20of%20the%20Page%20Object%20Model%20using%20Page%20Factory.png)

正如我们的元素操作方法中的代码一样，我们可以看到打印在控制台上的打印语句。

好了，你现在已准备好在页面对象模型中使用和实施页面工厂，并利用其功能使你的代码可读、可维护和快速。

## 关键要点

-   你现在知道了 Selenium 中的页面工厂类如何增强常规页面对象模型。
-   此外，你有足够的理由使用页面工厂模型/模式，了解它的众多优点。
-   此外，你还学习了不同的 Page Factory 注释，并知道哪一个最适合你的需要。
-   此外，为你的测试方法构建 Page Factory Design 有两个重要步骤 - 定位元素 (@FindBy(s)/@FindAll) 和初始化元素 (initElements())。
-   为了加快执行速度，你现在知道可以在哪里使用@CacheLookUp注释。