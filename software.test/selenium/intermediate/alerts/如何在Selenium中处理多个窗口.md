在自动化任何网站或 Web 应用程序时，我们一定见过这样的场景：当单击一个按钮时，应用程序中会打开多个窗口，并且用户必须在所有打开的窗口上执行一些操作。现在用户可能无法同时在所有窗口上工作，因此需要一些机制来控制父窗口和子窗口，或者如果我可以从 QA 的角度说，我们如何使用 Selenium 处理窗口？。在本教程中，我们将了解如何使用 Selenium WebDriver 实现这一点。我们将专注于以下关键主题：

-   Selenium 中的窗口是什么？
    -   我们如何识别父窗口和子窗口呢？
-   为什么我们需要在 Selenium 中处理多个窗口？
-   Selenium 中的窗口句柄是什么？
    -   Selenium 中用于窗口处理的不同方法有哪些？
-   我们如何处理 Selenium 中的子窗口？
    -   如何在 Selenium 中处理多个窗口？
    -   还有，在Selenium中我们如何从子窗口切换回父窗口呢？
    -   如何关闭 Selenium 中的所有窗口？

## Selenium 中的窗口是什么？

任何浏览器中的窗口都是用户点击链接/URL 后登陆的主要网页。[Selenium](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)中的此类窗口称为父窗口，也称为主窗口，它在创建Selenium WebDriver会话 时打开，并拥有WebDriver 的所有焦点。

要查看主窗口的示例，你可以访问[ToolsQA 演示站点](https://demoqa.com/browser-windows)并进行检查。下面的屏幕截图显示了相同的内容：

![硒窗口](https://www.toolsqa.com/gallery/selnium%20webdriver/1.window%20in%20selenium.png)

正如你在上图中所看到的，尽管网页有多个元素，但它们都是主窗口的一部分。使用Selenium WebDriver导航的URL 将始终具有主窗口的上下文。但是当我们点击“New Window”和“New Window Message”按钮时，它会在父窗口中打开其他窗口。这些窗口是子窗口。让我们了解如何定位父子窗口？

### 我们如何识别父窗口和子窗口呢？

当用户点击 URL 时，将打开一个网页。该主页是父窗口 ，即用户当前登陆的主窗口，将执行任何操作。这与我们的Selenium自动化脚本执行时将打开的网页相同。所有将在主窗口内打开的窗口都将被称为子窗口。

[以ToolsQA演示站点](https://demoqa.com/browser-windows)为例， 主页面有新标签，新窗口等元素是我们的父窗口，显示的两个子窗口是我们点击“新窗口” 时将打开的子窗口，“新窗口消息”按钮，如下图：

![多个窗口](https://www.toolsqa.com/gallery/selnium%20webdriver/2.mulitple%20windows.png)注意：你的父窗口中可以有单个子窗口或多个子窗口。

子窗口可能有也可能没有任何URL。如上所示，Child Window 1没有任何明确的 URL，而Child Window 2有明确的URL。

因此，当我们手动测试 Web 应用程序时，检查子窗口的行为非常容易，因为它们在主窗口的上下文中很容易看到。但使用Selenium进行自动化时情况并非如此。让我们了解在使用Selenium WebDriver 自动化应用程序时需要什么来处理不同的窗口类型？

## 为什么我们需要在 Selenium 中处理多个窗口？

当用户在 Web 应用程序上工作时，可能会出现一个新窗口将在你的主窗口中打开的情况。考虑一个销售服装的电子商务网站的场景，该网站将有一个尺寸表链接到每件服装，点击后，一个子窗口打开。现在，据我们了解，Selenium仅在特定上下文中工作，并且每个子窗口都将有一个单独的上下文。因此，为了使用Selenium WebDriver 自动化此类场景，我们必须相应地引导WebDriver，以便Selenium可以获取特定窗口的上下文并在该窗口中执行所需的操作。

让我们尝试借助“https://demoqa.com/browser-windows”上的自动化场景来理解每个窗口的不同上下文的概念。打开网址后，我们将点击应用程序内的“新窗口” 按钮，一个新的浏览器窗口打开。我们将从新打开的窗口中读取文本，即“This is sample page”并将打印它。

![Selenium 中的子窗口](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Child%20windows%20in%20Selenium.png)

下面的代码片段将单击“新窗口”按钮，并尝试从新窗口中读取文本。

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
public class childWindow {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver","./src/resources/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://demoqa.com/browser-windows");

        // Open new window by clicking the button
         driver.findElement(By.id("windowButton")).click();

         // Click on the new window element and read the text displayed on the window
         WebElement text = driver.findElement(By.id("sampleHeading"));
   
         // Fetching the text using method and printing it     
         System.out.println("Element found using text: " + text.getText());
         driver.quit();
    }
}
```

输出：一旦我们执行代码，就会遇到“没有这样的元素：无法定位元素”错误，因为WebDriver无法定位元素并获取显示在新窗口上的文本。

![Selenium 中未处理的窗口](https://www.toolsqa.com/gallery/selnium%20webdriver/4.unhandled%20windows%20in%20Selenium.png)

如我们所见，即使文本在屏幕上可见，但Selenium WebDriver无法找到它，因为它不在Selenium WebDriver执行的同一上下文中。

为了切换上下文，Selenium WebDriver使用窗口的特定 ID，称为窗口句柄。让我们了解一下Selenium 上下文中的窗口句柄到底是什么？

## Selenium 中的窗口句柄是什么？

窗口句柄存储浏览器窗口的唯一地址。它只是一个指向窗口的指针，其返回类型是字母数字。Selenium中的窗口句柄有助于处理多个窗口和子窗口。每个浏览器都会有一个唯一的窗口句柄值，我们可以用它来唯一地标识它。

例如：当你打开一个网站说“https://demoqa.com/browser-windows”并点击里面的浏览器窗口时，每个打开的窗口都会有一个 ID，我们可以使用它来将 Selenium WebDriver 上下文切换到那个窗口并在该窗口内执行任何操作。在下图中，你可以看到三个窗口 - 一个父窗口和两个子窗口。他们三个都有一个唯一的窗口句柄，即一个 ID。

![多个窗口](https://www.toolsqa.com/gallery/selnium%20webdriver/5.mulitple%20windows.png)

这里的每个窗口都会有一个唯一的ID，我们可以使用Selenium Webdriver提供的方法获取该 ID，然后使用它来将上下文切换到指定的窗口。我们先来了解一下Selenium WebDriver提供的处理windows的不同方法有哪些？

### Selenium 中用于窗口处理的不同方法有哪些？

Selenium WebDriver提供了多种处理窗口的方法。其中很少有：

-   getWindowHandle( )：当一个网站打开时，我们需要使用 driver.getWindowHandle( ) 处理主窗口，即父窗口；方法。使用此方法，我们可以获得当前窗口的唯一 ID，该 ID 将在此驱动程序实例中识别它。此方法将返回[String 类型的值。](https://www.toolsqa.com/java/string-class/)
-   getWindowHandles( )：为了处理所有打开的窗口，它们是网络驱动程序的子窗口，我们使用 driver.getWindowHandles( ); 方法。Windows 存储在一组字符串类型中，在这里我们可以看到 Web 应用程序中从一个窗口到另一个窗口的转换。它的返回类型是[Set](https://www.toolsqa.com/java/list-interface/#:~:text=Method%3A iterator()&text=For the reader unfamiliar with,point to the next page.&text=%2F%2F Get a list iterator over the elements in the list.,-ListIterator listIterator) `<String>`。
-   switchto()：使用这个方法我们在窗口中执行切换操作。
-   action：此方法有助于在窗口上执行某些操作。

## 我们如何处理 Selenium 中的子窗口？

如上例所示，如果我们在任何 Web 应用程序中有子窗口，那么在没有适当窗口处理的情况下与它们交互将导致异常。为此，我们在上面解释了不同的方法，我们将在这里用一个实际的例子来使用它们。

我们将在这里使用getWindowHandle( ) 和 getWindowHandles( )方法以及switchto()方法。在下面的快照中，我们突出显示了 Selenium 中处理窗口的两种主要方法。

![窗口句柄方法](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Window%20handle%20methods.png)

以上面遇到异常的[“ToolsQA 演示站点”](https://demoqa.com/browser-windows)为例，我们将展示如何成功执行它。打开网址后，我们将点击应用程序内的“新窗口” 按钮，一个新的浏览器窗口打开。我们将从新打开的窗口中读取文本，即“This is sample page”并将打印它。

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.Iterator;
import java.util.Set;

public class childWindow {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "./src/main/resources/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://demoqa.com/browser-windows");

        // Open new child window within the main window
        driver.findElement(By.id("windowButton")).click();

        //Get handles of the windows
        String mainWindowHandle = driver.getWindowHandle();
        Set<String> allWindowHandles = driver.getWindowHandles();
        Iterator<String> iterator = allWindowHandles.iterator();

        // Here we will check if child window has other child windows and will fetch the heading of the child window
        while (iterator.hasNext()) {
            String ChildWindow = iterator.next();
                if (!mainWindowHandle.equalsIgnoreCase(ChildWindow)) {
                driver.switchTo().window(ChildWindow);
                WebElement text = driver.findElement(By.id("sampleHeading"));
                System.out.println("Heading of child window is " + text.getText());
            }
        }
    }
}
```

-   启动网站“https://demoqa.com/browser-windows”并点击窗口 - “windowbutton”。
-   String mainwindow = driver.getWindowHandle()：将父窗口值存储在字符串类型的唯一标识符中。
-   Set<String> s = driver.getWindowHandles()：所有子窗口都存储在一组字符串中。
-   Iterator<String> i = s.iterator() ：这里我们将遍历所有子窗口。
-   if (!MainWindow.equalsIgnoreCase(ChildWindow)) ：现在通过比较主窗口和子窗口来检查它们。
-   driver.switchTo().window (ChildWindow)：切换到子窗口并阅读标题。
-   WebElement text = driver.findElement (By.id("sampleHeading"))：找到元素并存储在一个web元素中，我们将通过该元素使用gettext()方法获取标题的文本。

代码的输出将打印文本“This is a sample page”，如下所示：

![输出](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Output.png)

所以这样一来，我们就把上下文切换到子窗口，然后打印子窗口中的文本。

### 如何在 Selenium 中处理多个窗口？

在Selenium 中，当我们在任何 Web 应用程序中有多个窗口时，该方法可能需要在多个窗口之间切换控制，即从一个窗口切换到另一个窗口以执行任何操作，我们可以通过使用switchto() 来实现；方法。此外，我们将在此处使用窗口句柄来存储窗口的唯一值并使用Selenium 执行窗口处理。

注意：如果你必须在选项卡之间切换，那么你也必须使用相同的方法。

让我们借助下面的代码来理解这一点：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.Iterator;
import java.util.Set;		

public class multipleChildWinows {				
  public static void main(String[] args) throws InterruptedException {									
    System.setProperty("webdriver.chrome.driver","./src/main/resources/chromedriver");
    WebDriver driver = new ChromeDriver();
    driver.manage().window().maximize();
    driver.get("https://demoqa.com/browser-windows");

    // Opening all the child window
    driver.findElement(By.id("windowButton")).click();
    driver.findElement(By.id("messageWindowButton")).click();
    
    String MainWindow = driver.getWindowHandle();
    System.out.println("Main window handle is " + MainWindow);

    // To handle all new opened window
    Set<String> s1 = driver.getWindowHandles();
    System.out.println("Child window handle is" + s1);
    Iterator<String> i1 = s1.iterator();

    // Here we will check if child window has other child windows and when child window
    //is the main window it will come out of loop.
      while (i1.hasNext()) {
          String ChildWindow = i1.next();
          if (!MainWindow.equalsIgnoreCase(ChildWindow)) {
              driver.switchTo().window(ChildWindow);
              driver.close();
              System.out.println("Child window closed");
           }
       }
    }
}
```

-   启动网站“https://demoqa.com/browser-windows”并单击两个弹出窗口 - “windowbutton”和“messagewindowbutton”。
-   String Mainwindow = driver.getWindowHandle()：将父窗口值存储在字符串类型的唯一标识符中。
-   Set<String> s1 = driver.getWindowHandles()：所有子窗口都存储在一组字符串中。
-   Iterator<String> i1 = s1.iterator() ：这里我们将遍历所有子窗口。
-   if (!MainWindow.equalsIgnoreCase(ChildWindow)) ：现在通过比较主窗口和子窗口来关闭它们。
-   driver.switchTo().window (ChildWindow)：它也会在子窗口关闭时打印。
-   driver.close()： 当主窗口是唯一的活动窗口时，它将退出循环并关闭窗口。

![如何在 Selenium 中处理窗口](https://www.toolsqa.com/gallery/selnium%20webdriver/8.How%20To%20Handle%20Window%20in%20Selenium.png)

一旦Selenium WebDriver 实例化，一个唯一的字母数字 id 分配给称为窗口句柄的窗口，它标识浏览器窗口。在上面的代码中，父窗口和其中一个子窗口具有相同的ID，另外两个窗口具有不同的ID。这是因为父窗口是其自身的子窗口。但请注意，由于相同的原因，只有两个关闭。由于这个 id 是独特的，Selenium WebDriver 使用它在不同的窗口(或选项卡)之间切换。该 ID 会一直保留到会话关闭为止。

### Selenium中如何从子窗口切换回父窗口？

一旦切换到子窗口，  Selenium WebDriver 将保存它的当前上下文，你将无法识别父窗口中存在的元素。要访问父窗口的元素，我们必须将焦点移回它。我们可以实现这一点，如下面的代码片段所示：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.Iterator;
import java.util.Set;

public class switchbackParentWinow {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "./src/main/resources/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://demoqa.com/browser-windows");
        
        driver.findElement(By.id("windowButton")).click();
        String mainwindow = driver.getWindowHandle();
        Set<String> s1 = driver.getWindowHandles();
        Iterator<String> i1 = s1.iterator();
        
        while (i1.hasNext()) {
            String ChildWindow = i1.next();
                if (!mainwindow.equalsIgnoreCase(ChildWindow)) {
                driver.switchTo().window(ChildWindow);
                WebElement text = driver.findElement(By.id("sampleHeading"));
                System.out.println("Heading of child window is " + text.getText());
                driver.close();
                System.out.println("Child window closed");
            }
        }    
  
        //  Switch back to the main window which is the parent window.
        driver.switchTo().window(mainwindow);
        driver.quit();
    }
}
```

上述代码的输出如下所示：

![输出](https://www.toolsqa.com/gallery/selnium%20webdriver/9.output.png)

练习 Exercise :尝试实现以下场景，在切换回父窗口后，你可以从主窗口打印文本。

1.  打开 URL [“https://demoqa.com/browser-windows”](https://demoqa.com/browser-windows)。
2.  单击所有子窗口
3.  打印控制台中所有子窗口上的文本。
4.  在控制台中打印父窗口的标题。

### 如何关闭 Selenium 中的所有窗口？

当我们在多个窗口上工作时，完成操作后同时关闭窗口很重要。为了关闭WebDriver具有当前焦点的窗口，我们有driver.close(); 方法。当我们有多个窗口并且我们希望关闭一个选择性窗口时，我们主要使用这种方法。

另一种关闭窗口的方法是driver.quit()，它将关闭在特定会话中打开的所有窗口。它基本上停止了驱动程序实例，对WebDriver的任何进一步操作都可能导致异常。它通常是任何代码的最后一条语句。

在上面给出的示例中，我们使用了这两种方法。

注意：关闭子窗口后，我们必须在使用任何 WebDriver 命令之前显式切换回父窗口以避免“nosuchwindow”异常。

## 关键要点

-   窗口是当用户点击 URL 时将打开的网页。Selenium 中有两种类型的窗口——父窗口及其子窗口。
-   窗口句柄是一个唯一标识符，用于存储在网页上打开的窗口的值，并有助于 Selenium 中的窗口处理。
-   getWindowHandles( ) 和 getWindowHandles( ) 处理 Selenium 中的窗口。
-   用户必须使用switchTo()方法从父窗口切换到子窗口才能对其进行处理。
-   要关闭窗口，存在两种方法driver.close()和driver.quit()。