假设你想要建立一个显示附近餐馆路线的网站，并且你想要集成Google 地图来帮助客户寻找路线。谷歌地图是一种嵌入到你网站中的外部网络资源。同样，如果用户想要将YouTube 视频嵌入到网页中，他们可以在页面的HTML中提及视频链接，视频就会被添加。是的，这就是在 Web 开发中使用 iframe 的用武之地，将外部网页或各种文档、图像、视频等对象嵌入到单个网页中。这些嵌入对象可以是地图、视频、图像或任何外部HTML文档，以及如何在 Selenium 中定位和自动化这些 iframe是这篇文章的议程。

在本教程中，我们将使用我们的[ToolsQA 演示网站](https://demoqa.com/)来学习使用[Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)处理 iframe 的不同方法，方法包括以下主题下的详细信息：

-   什么是 iframe？
    -   框架和 iframe 有什么区别？
-   如何使用 Selenium WebDriver 自动化 iframe？
    -   如何在 Selenium 中通过索引切换到 iframe？
    -   而且，如何在 Selenium 中按名称或 ID 切换到 iframe？
-   如何在 Selenium 中通过 WebElement 切换到 iframe？
-   如何处理 Selenium WebDriver 中的嵌套 iframe？
    -   另外，如何将上下文从子 iframe 切换回父 iframe？
    -   如何将上下文从子 iframe 切换回主网页？
-   如何使用 Selenium WebDriver 处理动态 iframe？

## 什么是 iframe？

iframe是网页上的一个空间，可在主网页内嵌入不同类型的媒体，如图像、文档、视频。媒体可以是与网站相关的内部图像或文档，也可以是来自其他网站的外部资源。iframe的完整形式是Inline Frame ，在 HTML 中使用标签定义。`<iframe>`

你可以使用HTML文档`<iframe>`中的标记插入 iframe 元素。标签的“src”属性指定了我们必须嵌入到 iframe 中的媒体的URL 。 在 HTML 文档中使用iframe的语法是：

```java
<iframe src="URL"></iframe>
```

尽管网站广泛使用iframe，但它们也带来了安全风险。此外，网站可能容易受到跨站点攻击，因为页面通常托管在外部域上。因此，iframe要求开发人员信任他/她嵌入到iframe 中的内容。

要在网页上查看iframe示例，你可以访问[ToolsQA 演示站点，](https://demoqa.com/frames)其中使用iframe标记嵌入文档，如下面的屏幕截图所示 -

![网页中的 iframe 排列](https://www.toolsqa.com/gallery/selnium%20webdriver/1.iframe%20arrangement%20in%20a%20web%20page.png)

在深入了解iframe的细节之前，让我们再了解一个术语“框架”。它在 web 开发中使用非常广泛，通常与iframe 混淆。然而，这两者有很大的不同并且服务于不同的目的。

### 框架和 iframe 有什么区别？

在Web开发中，有两个术语：frame和iframe，这两个术语使用广泛，听起来也差不多。但是这两者都有不同的用途和目的。

框架是将浏览器窗口分成多个部分的 HTML标签，每个部分可以加载一个单独的HTML文档。每个框架都分配有其网页。`<frame>`标签表示一个框架，所有的框架都存在于 HTML 中的一个标签中`<frameset>`。框架集 被定义为浏览器窗口中的框架集合，并允许你将屏幕拆分为不同的页面，如下图所示：

![网页上的框架](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Frames%20on%20a%20Web%20Page.png)

 在HTML文档中定义和使用框架的基本语法如下所示：

```java
<frameset rows="25%,25%",50%,50%>

 <frame name="frame1" src="frame_1.html" />
 <frame name="frame2" src="frame_2.html" />
 <frame name="frame2" src="frame_3.html" />
 <frame name="frame2" src="frame_4.html" />

</frameset>
```

相反，如前所述， iframe是内联框架，用于嵌入来自内部/外部网站的内容并由`<iframe>`标签表示。与框架不同，它们不需要框架集标签，并且可以放置在`<body>`标签内区域的网页上的任何位置，如下图所示：

![网页上的 iframe](https://www.toolsqa.com/gallery/selnium%20webdriver/3.iframe%20on%20a%20web%20page.png)

通常，iframe 中的内容嵌入自在你域外托管的第三方网站，因此它们被认为不如框架安全。如果iframe中的内容不是第三方内容而是网站内部内容，则很少会出现安全问题。

## 如何使用 Selenium WebDriver 自动化 iframe？

众所周知，要使用Selenium WebDriver 自动化网页，首要任务是在页面上定位网页元素。但是，Selenium WebDriver无法直接访问和定位Selenium 中的 iframe 中的 Web 元素。Selenium只能访问特定上下文中的元素，主页面上下文和嵌入iframe是不同的。Selenium 明确需要切换上下文，以便它可以访问iframe 中的元素。

让我们借助网页[“https://demoqa.com/frames”上的以下示例来理解相同的内容，](https://demoqa.com/frames)我们将在其中尝试打印文本 - “This is a sample page”，如下面的屏幕截图中突出显示的那样：

![Selenium iframe 访问 iframe 中的元素](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Selenium%20iframes%20Accessing%20an%20element%20inside%20iframe.png)

现在，如果我们尝试使用其id“sampleHeading”`<h1>`访问元素(由标记 2 显示) ，如以下代码片段所示：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
public class IFrameDemo {
        public static void main(String[] args){
                //set the location of chrome browser from the local machine path
                System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
                
                // Initialize browser
                WebDriver driver=new ChromeDriver();
                
                //navigate to url
                driver.get("https://demoqa.com/frames");
                
                //Locate the frame1 heading
                WebElement frame1Heading= driver.findElement(By.id("sampleHeading"));
                
                //Finding the text of the frame1 heading
                String frame1Text=frame1Heading.getText();
                
                //Print the heading
                System.out.println("Text of the frame1 heading is:"+frame1Text);
                
                //closing the driver
                driver.close();
        }
}
```

上面代码的输出将呈现“NoSuchElementException”，如下面的屏幕截图所示，因为Selenium WebDriver无法在当前上下文中找到具有上述ID的 Web 元素。

![代码输出异常](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Exception%20in%20Code%20output.png)

现在，从上面的屏幕截图可以明显看出，Selenium 无法访问包装在iframe中的元素(如上图中的标记 1 所示)。 Selenium首先需要将上下文切换到 iframe  以访问iframe 内的所有 Web 元素。 Selenium WebDriver 提供了三种方式将焦点切换到指定的iframe：

-   使用 iframe 的索引。
-   使用 iframe 的名称或 ID
-   并且，使用 iframe 的 Web 元素对象。

Selenium WebDriver提供了switchTo().frame()方法来将执行上下文切换到标识的iframe。让我们了解如何将此方法与上面提到的将上下文切换到指定的iframe 的所有方式一起使用：

### 如何在 Selenium 中通过索引切换到 iframe？

如果一个网页上有多个iframe ，我们可以通过Selenium中 iframe 的索引来切换到每个 iframe。前提是他们在网页上有静态位置。该指数是从零开始的。这意味着如果页面上有 2 个框架，索引将从 0 开始。

要切换到网页上的特定 iframe，  Selenium WebDriver使用以下语法：

```java
driver.switchTo().frame(int index);
```

其中 index 是Selenium需要将上下文切换到的iframe的索引。

例如，如果你查看ToolsQA 演示站点，它有两个框架，因此要在第一个iframe中获取标题文本，我们可以使用其索引 =0切换到它，如以下代码片段所示：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
public class IFrameDemo {
        public static void main(String[] args){
                //set the location of chrome browser from the local machine path
                System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
                
                // Initialize browser
                WebDriver driver=new ChromeDriver();
                
                //navigate to url
                driver.get("https://demoqa.com/frames");
                
                //Switch to Frame using Index
                driver.switchTo().frame(0);
                
                //Identifying the heading in webelement
                WebElement frame1Heading= driver.findElement(By.id("sampleHeading"));
                
                //Finding the text of the heading
                String frame1Text=frame1Heading.getText();
                
                //Print the heading text
                System.out.println(frame1Text);
                
                //closing the driver
                driver.close();
        }
}
```

代码的输出将打印文本“This is a sample page”，如下所示：

![使用索引切换到 iframe 的代码](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Code%20to%20switch%20to%20iframe%20using%20Index.png)

### 如何在 Selenium 中按名称或 ID 切换到 iframe？

Selenium 还提供了重载的方法来将上下文切换到Selenium 中指定的iframe ，它接受iframe 的名称或id。它可以在句法上表示如下：

```java
driver.switchTo().frame(String name);

or 

driver.switchTo().frame(String id);
```

其中 iframe 的id或 name是iframe的属性，具有唯一值来标识iframe。

我们可以通过检查页面的DOM来获取iframe的 id 或名称，如下所示：

![Selenium iframe 使用 id 或 name 访问 iframe](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Selenium%20iframes%20Accessing%20iframe%20using%20id%20or%20name.png)

在上面的截图中，我们看到框架的 id 是“frame1”，所以我们可以在代码中使用它，如下所示 -

```java
driver.switchTo().frame("frame1");
```

其中“frame1”是Selenium WebDriver需要将上下文切换到的iframe的ID 或名称。

例如，如果你查看ToolsQA 演示站点，它有两个框架，因此要在第一个iframe中获取标题文本，我们可以使用其id切换到它，如以下代码片段所示：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
public class IFrameDemo {
        public static void main(String[] args){
                //set the location of chrome browser from the local machine path
                System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
                
                // Initialize browser
                WebDriver driver=new ChromeDriver();
                
                //navigate to url
                driver.get("https://demoqa.com/frames");
                
                //Switch to Frame using id of the frame
                driver.switchTo().frame("frame1");
                
                //Identifying the heading in webelement
                WebElement frame1Heading= driver.findElement(By.id("sampleHeading"));
                
                //Finding the text of the heading
                String frame1Text=frame1Heading.getText();
                
                //Print the heading text
                System.out.println(frame1Text);
                
                //closing the driver
                driver.close();
        }
}
```

### 如何在 Selenium 中通过 WebElement 切换到 iframe？

Selenium将每个HTML元素标识为WebElements，一个表示单个 Web 元素的对象。iframe也是如此；WebDriver可以使用任何[定位器策略](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)搜索iframe 。然后可以使用相同的WebElement对象将上下文切换到iframe。WebDriver使用以下语法将上下文切换到使用WebElement对象的iframe ：

```java
driver.switchTo().frame(WebElement iframeElement)
```

其中iframeElement是Selenium通过使用任何给定的定位器策略搜索相同内容而返回的WebElement 。

为了更好地理解它，让我们考虑演示站点(https://demoqa.com/frames)中的一个示例，以获取id 为“frame1”的iframe中的文本。在这里，首先，我们将 使用iframe的“id”获取WebElement  ，然后使用它来切换到iframe：

![Selenium iframe 使用 Object 切换到 iframe](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Selenium%20iframes%20Switch%20to%20iframe%20using%20Object.png)

1.  将浏览器导航至https://demoqa.com/frames
2.  使用 iframe 的“id”获取 iframe 的 WebElement 对象。
3.  使用 WebElement 对象切换到 iframe
4.  找到`<h1>`iframe 内的元素
5.  获取并打印出现在 iframe 上的文本，即“This is a sample page”。

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
public class IFrameDemo {
        public static void main(String[] args){
                //set the location of chrome browser from the local machine path
                System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
                
                // Initialize browser
                WebDriver driver=new ChromeDriver();
                
                //navigate to url
                driver.get("https://demoqa.com/frames");
                
                //Locating frame1 using its id
                WebElement frame1=driver.findElement(By.id("frame1"));
                
                //Switching the WebDriver context to frame1
                driver.switchTo().frame(frame1);
                
                //Identifying the frame heading in a WebElement
                WebElement frame1Heading= driver.findElement(By.id("sampleHeading"));
                
                //Finding the text of the frame1 heading
                String frame1Text=frame1Heading.getText();
                
                //Print the heading
                System.out.println("Text of the frame1 heading is:"+frame1Text);
                
                //closing the driver
                driver.close();
        }
}
```

上面代码的输出将打印文本“This is a sample page”，如下所示 -

![打印标题的代码输出](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Code%20output%20to%20print%20the%20heading.png)

## 如何处理 Selenium WebDriver 中的嵌套 iframe？

浏览器窗口中可以有多个iframe，例如iFrame f1 和iFrame f2。这些iframe还可以有子iframe，例如iframe cf1 在iframe f1 内。这种安排是嵌套的 iframe。

在下图中，iframe f1 是iframe cf1 的父级。

![Selenium webdriver 中的嵌套 iFrame](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Nested%20iFrames%20in%20Selenium%20webdriver.png)

如果你想使用子iframe，你首先必须将Selenium WebDriver上下文切换到父iframe f1，然后找到它的子iframe。一旦我们识别出子iframe，你就可以使用 上一节中提到的任何switchTo.frame()方法切换到它们。

让我们以[ToolsQA Demo 站点](https://demoqa.com/nestedframes) (如下图)为例，学习在嵌套的iframe之间切换。

![显示嵌套 iFrame 的 Selenium iframe 示例](https://www.toolsqa.com/gallery/selnium%20webdriver/11.Selenium%20iframes%20Example%20showing%20Nested%20iFrames.png)

1.  浏览浏览器：- https://demoqa.com/nestedframes
2.  在此网页中使用 Selenium WebDriver 打印帧数。我们应该将计数设为 1，因为我们在网页中只有一个父框架。
3.  使用框架的 id 切换到父框架。
4.  使用父框架内的 Selenium WebDriver 打印框架数。我们应该再次将计数设为 1，因为它只有一个子帧。

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
public class IFrameDemo {
        public static void main(String[] args){
                //set the location of chrome browser from the local machine path
                System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
                
                // Initialize browser
                WebDriver driver=new ChromeDriver();        
                
                //Navigate to the demo site
                driver.get("https://demoqa.com/nestedframes");
                
                //Number of Frames on a Page
                int countIframesInPage = driver.findElements(By.tagName("iframe")).size();
                System.out.println("Number of Frames on a Page:" + countIframesInPage);
                
                //Locate the frame1 on the webPage
                WebElement frame1=driver.findElement(By.id("frame1"));
                
                //Switch to Frame1
                driver.switchTo().frame(frame1);
                
                //Locate the Element inside the Frame1
                WebElement frame1Element= driver.findElement(By.tagName("body"));
                
                //Get the text for frame1 element
                String frame1Text=frame1Element.getText();
                System.out.println("Frame1 is :"+frame1Text);
                
                //Number of Frames on a Frame1
                int countIframesInFrame1 =driver.findElements(By.tagName("iframe")).size();
                System.out.println("Number of iFrames inside the Frame1:" + countIframesInFrame1);
                
                //switch to child frame
                driver.switchTo().frame(0);
                
                int countIframesInFrame2 =driver.findElements(By.tagName("iframe")).size();
                System.out.println("Number of iFrames inside the Frame2:" + countIframesInFrame2);
                
                driver.close();
        }
}
```

执行片段后的输出：

![打印页面上的 iFrame 数](https://www.toolsqa.com/gallery/selnium%20webdriver/12.Print%20Number%20of%20iFrames%20on%20page.png)

在上面的输出截图中，我们可以看到网页上显示的iframe数量打印为 1。由于我们在代码中切换到frame1 ，它还会打印WebDriver的当前上下文，然后打印显示的iframe数量在父框架中，因为子框架中没有框架，所以frame2 (子框架)内的iframe计数为 0。

### 如何将上下文从子 iframe 切换回父 iframe？

一旦切换到子iframe，  Selenium WebDriver将保留子iframe 的当前上下文，你将无法识别父iframe 中存在的元素。例如，如果你切换到https://demoqa.com/nestedframes上的子框架，然后尝试执行以下代码。你希望它打印“Parent Frame”，但在输出中，你将得到“Child Frame”。

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class IFrameDemo {

public static void main(String[] args){
        //set the location of chrome browser from the local machine path 
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
        // Initialize browser
        WebDriver driver=new ChromeDriver();       
        driver.get("https://demoqa.com/nestedframes");
        
        //Number of Frames on a Page
        int countIframesInPage =driver. findElements(By. tagName("iframe")). size();
        System.out.println("Number of Frames on a Page:"+countIframesInPage);
        
        //Locate the frame1 on the webPage
        WebElement frame1=driver.findElement(By.id("frame1"));
        
        //Switch to Frame1
        driver.switchTo().frame(frame1);
        
         //Number of Frames on a Frame1
        int countIframesInFrame1 =driver. findElements(By. tagName("iframe")). size();
        System.out.println("Number of Frames inside the Frame1:"+countIframesInFrame1);
        
        //Switch to child frame
        driver.switchTo().frame(0);
        int countIframesInFrame2 =driver. findElements(By. tagName("iframe")). size();
        System.out.println("Number of Frames inside the Frame2:"+countIframesInFrame2);
        
        //Locate the Element inside the Frame1
        WebElement frame1Element= driver.findElement(By.tagName("body"));
       
        //Get the text for frame1 element
        String frame1Text=frame1Element.getText();
        
        //Try to Print the text present inside parent frame
        System.out.println("Frame1 is :"+frame1Text);
        driver.close();
   }
}
```

上述代码的输出/结果如下：

![示例在不切换回的情况下访问父 iframe](https://www.toolsqa.com/gallery/selnium%20webdriver/13.Example%20Accessing%20parent%20iframe%20without%20Switching%20back.png)

现在，如果你想将输出打印为“父框架”，则必须将SeleniumWebDriver的上下文切换回父iframe。这可以使用方法switchTo().parentFrame()来完成

让我们对上面的代码进行更改。我们可以通过让Selenium WebDriver将上下文切换回父iframe 来实现。

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class IFrameDemo {
        public static void main(String[] args){
                //set the location of chrome browser from the local machine path 
                System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
                
                // Initialize browser
                WebDriver driver=new ChromeDriver();
                
                //Navigate to the demo site
                driver.get("https://demoqa.com/nestedframes");
                
                //Number of Frames on a Page
                int countIframesInPage =driver. findElements(By. tagName("iframe")). size();
                System.out.println("Number of Frames on a Page:"+countIframesInPage);
                
                //Locate the frame1 on the webPage
                WebElement frame1=driver.findElement(By.id("frame1"));
                
                //Switch to Frame1
                driver.switchTo().frame(frame1);
                
                //Number of Frames on a Frame1
                int countIframesInFrame1 =driver. findElements(By. tagName("iframe")). size();
                System.out.println("Number of Frames inside the Frame1:"+countIframesInFrame1);
                
                //Swiitch to child frame
                driver.switchTo().frame(0);
                int countIframesInFrame2 =driver. findElements(By. tagName("iframe")). size();
                System.out.println("Number of Frames inside the Frame2:"+countIframesInFrame2);

                //Switch to Parent iFrame
                driver.switchTo().parentFrame();
                
                //Locate the Element inside the Frame1
                WebElement frame1Element= driver.findElement(By.tagName("body"));
                
                //Get the text for frame1 element
                String frame1Text=frame1Element.getText();
                
                //Try to Print the text present inside parent frame
                System.out.println("Frame1 is :"+frame1Text);
                driver.close();
        }
}
```

以上代码的输出将打印父框架文本：

![切换回父 iframe 后访问父 iframe 的示例](https://www.toolsqa.com/gallery/selnium%20webdriver/14.Example%20Accessing%20parent%20iframe%20after%20switching%20back%20to%20parent%20iframe.png)

同样，要在父框架内使用 Selenium 处理多个子iframe，你将首先将驱动程序上下文切换到父框架。进入父框架后，你可以切换到父 iframe 内所需的子iframe。

### 如何将上下文从子 iframe 切换回主网页？

在上一节中，我们介绍了使用 Selenium 在帧之间进行切换。但是在某些情况下，你必须在使用页面内的iframe后返回到主网页。

Selenium WebDriver提供方法switchTo().defaultContent()。它将上下文切换回主页，无论WebDriver当前的上下文有多深？

让我们使用演示站点https://demoqa.com/nestedframes 来理解这一点：

下面的代码片段尝试访问主页上的内容，而不将上下文切换回主页：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class IFrameDemo {
     public static void main(String[] args){
        //set the location of chrome browser from the local machine path 
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
        
        // Initialize browser
        WebDriver driver=new ChromeDriver();
        
        //Navigate to URL
        driver.get("https://demoqa.com/nestedframes");
        
        //Locate the webelement page heading
        WebElement pageHeadingElement=driver.findElement(By.xpath("//div[@class='main-header']"));
        
        //Find text of the page heading        
        String pageHeading=pageHeadingElement.getText();
        
        //Print the page heading
        System.out.println("Page Heading is :"+pageHeading);
        
        //Switch to Parent iframe
        WebElement frame1=driver.findElement(By.id("frame1"));
        driver.switchTo().frame(frame1);
        WebElement frame1Element= driver.findElement(By.tagName("body"));
        String frame1Text=frame1Element.getText();
        System.out.println("Frame1 is :"+frame1Text);
        
        //Switch to child frame
        driver.switchTo().frame(0);
        WebElement frame2Element= driver.findElement(By.tagName("p"));
        String frame2Text=frame2Element.getText();
        System.out.println("Frame2 is :"+frame1Text);

        //Try to print the heading of the main page without swithcing
        WebElement mainPageText=driver.findElement(By.xpath("//[@id=\"framesWrapper\"]/div[1]/text()"));
        System.out.println(mainPageText);
        driver.close();
    }
}
```

输出： 我们得到NoSuchElementException因为当前WebDriver在子iframe 中。 在子iframe 中，没有我们可以使用提到的XPath 识别的 Web 元素。

![示例在不设置上下文的情况下访问主页](https://www.toolsqa.com/gallery/selnium%20webdriver/15.Example%20Accessing%20main%20page%20without%20setting%20the%20context%20back.png)

让我们修改上面的代码片段。在这里，我们首先将上下文切换回主页面。之后，我们将尝试访问网页上的 web 元素：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class IFrameDemo {
public static void main(String[] args){
       //set the location of chrome browser from the local machine path 
       System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
        
        // Initialize browser
        WebDriver driver=new ChromeDriver();        
        driver.get("https://demoqa.com/nestedframes");
        WebElement pageHeadingElement=driver.findElement(By.xpath("//div[@class='main-header']"));
        String pageHeading=pageHeadingElement.getText();
        System.out.println("Page Heading is :"+pageHeading);
        
        //Switch to Parent frame
        WebElement frame1=driver.findElement(By.id("frame1"));
        driver.switchTo().frame(frame1);
        WebElement frame1Element= driver.findElement(By.tagName("body"));
        String frame1Text=frame1Element.getText();
        System.out.println("Frame1 is :"+frame1Text);
        
        //Switch to child frame
        driver.switchTo().frame(0);
        WebElement frame2Element= driver.findElement(By.tagName("p"));
        String frame2Text=frame2Element.getText();
        System.out.println("Frame2 is :"+frame2Text);

        //Switch to default content
        driver.switchTo().defaultContent();

        //Try to print the heading of the main page without swithcing
        WebElement mainPageText=driver.findElement(By.xpath("//[@id='framesWrapper']/div[1]"));
        System.out.println(mainPageText.getText());
        driver.close();
    }
}
```

上述代码片段的输出/结果将是：

![示例 设置回上下文后访问主页](https://www.toolsqa.com/gallery/selnium%20webdriver/16.Example%20Accessing%20the%20main%20page%20after%20setting%20the%20context%20back.png)

这样，我们就可以将Selenium的焦点切换回主网页。此外，我们可以对网页元素执行所需的操作。

用户可能想知道driver.switchTo().parentFrame()和 driver.switchTo().defaultContent() 方法有什么区别？乍一看，它们可能看起来有些相似。

-   driver.switchTo().defaultContent()：这会将控制传递给包含 iframe 的主文档。
-   driver.switchTo().parentFrame()：这会将控制传递给当前框架的直接父框架。

## 如何使用 Selenium WebDriver 处理动态 iframe？

动态 iframe是iframe id和iframe name 等属性在网页上动态变化的 iframe，而iframe位置没有任何变化。在这种情况下，为iframe id或name指定的固定值将无法识别iframe。

因此，要识别动态帧，我们可以使用以下方法：

-   iframe的索引可以根据frame的位置唯一标识它。

在下面的截图中，我们在页面上有两个 iframe，如图所示。让我们尝试查找网页上 iFrame 的总数。之后，我们将借助以下代码片段使用索引切换到它们。

![Selenium iframes 使用索引处理动态 iFrame](https://www.toolsqa.com/gallery/selnium%20webdriver/17.Selenium%20iframes%20Handling%20dynamic%20iFrames%20using%20Index.png)

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class IFrameDemo {
        public static void main(String[] args){
                //set the location of chrome browser from local machine
                System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
                
                // Initialize browser
                WebDriver driver=new ChromeDriver();
                
                //navigate to url
                driver.get("https://demoqa.com/frames");
                
                //Find the total number of iframes on the page
                int totalIFramesOnPage=driver.findElements(By.tagName("iframe")).size();
                
                //Print the total iframes on page
                System.out.println("Total iframes on Page:"+totalIFramesOnPage);
                
                //switch to first frame using index=0
                driver.switchTo().frame(0);
                
                driver.close();
        }
}
```

上面代码的输出将打印位于页面上的 2 个 iframe -

![获取页面上 iFrame 总数的示例](https://www.toolsqa.com/gallery/selnium%20webdriver/18.Example%20getting%20total%20number%20of%20iFrames%20on%20a%20page.png)

-   在某些情况下，name 或 id属性可能包含前缀值。例如，在下面的代码中：

<iframe id = ‘iframe_1001>…</iframe>  OR <iframe name= ‘iframe_ABC'>…</iframe>.

“iframe_”的值是恒定的，不会随着不同的框架而改变。你可以使用“contains”关键字构建[有效的 XPath](https://www.toolsqa.com/selenium-webdriver/xpath-in-selenium/) ，例如//iframe [contains(@id,'frame')] 或//iframe[contains(@name,'frame')]，然后使用该 XPath 定位 iframe .

这样，我们就可以在 Selenium 中定位到动态 iframe。然后将焦点切换到它们以定位iframe 内的 Web 元素。

## 关键要点

-   iframe 使用`<iframe>`标签嵌入到 HTML 页面中。
-   要使用不同的嵌套 iframe，我们可以使用switchTo()方法使用索引、名称或 ID 和 Web 元素切换框架。
-   此外，你可以使用方法switchTo().parentFrame() 切换到直接父 iframe。
-   你可以使用方法switchTo().defaultContent()从子 iframe 切换到主网页。
-   此外，你可以使用帧索引切换到动态帧。因为它们没有固定的 id 或 name 属性值。