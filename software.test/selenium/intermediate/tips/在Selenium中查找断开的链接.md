在一个网页上，通常有多个链接/超链接，它们有特定的用途。一些链接重定向到第三方网站，如维基百科，一些链接重定向到同一网站。它们是减少搜索时间的好方法。例如，如果我正在阅读一篇关于 selenium 的文章并说[Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)用于自动化测试，我可以链接自动化测试词，这将重定向到一篇关于自动化测试的详细文章。这将为不了解自动化测试并单独搜索它的人节省时间。因此，它们很重要，Selenium 中的这些失效链接需要在发布网页之前进行测试和验证。

有效的 URL 将始终具有[HTTP 响应](https://www.toolsqa.com/client-server/http-response/)代码 200，这是成功且有效的。在测试这些链接时，用户很难手动点击并检查网页上所有损坏的链接。因此，我们可以搜索网页上的所有链接，并检查每个链接的状态，以验证其是否为有效链接。Images 也是如此，我们可以通过验证图像的src 链接是否有效来检查有效和可见的图像。本文将解释在 selenium 测试中浏览所有/损坏的链接时验证某些图像和链接是否有效或处于损坏状态的各种方法。

-   什么是网页上的链接？
    -   什么是 HTTP 状态代码？
    -   另外，什么是网页上的断开链接？
    -   什么是网页上损坏的图像？
-   如何在 Selenium 中查找网页上的所有链接？
    -   如何在 Selenium 测试中找到断开的链接？
    -   以及如何在 Selenium 测试中找到损坏的图像？

## 什么是网页上的链接？

超链接，通常称为链接，是网页上的那些 HTML 标记/元素，我们使用它们重定向到另一个网页。当用户对这些超链接执行点击操作时会发生这种情况。用户可以通过点击链接立即到达目标页面，并且链接被激活。每个链接/超链接始终包含一个目标或URL，即单击链接时将打开的页面的URL 。这个链接应该是有效的，所以当有人点击提到的超链接时我们可以打开所需的页面。

现在我们如何分类链接是否有效？当我们点击任何URL 时，我们知道它会返回一些HTTP代码，这取决于返回值，表示提到的链接是否有效。让我们快速了解在点击URL时可以返回的各种HTTP状态代码的含义：

### 什么是 HTTP 状态代码？

服务器生成 HTTP 状态代码以响应客户端向服务器提交的请求。我们可以将HTTP 响应状态代码分为五种类型的响应。状态码的第一位是响应类型，最后两位有与状态码相关的不同解释。有不同的HTTP状态代码，其中一些如下所示：

-   200 – 有效链接/成功
-   301/302 - 页面重定向临时/永久
-   404——找不到页面
-   400——错误的请求
-   401——未经授权
-   500——内部服务器错误

我们将在测试中使用这些HTTP 代码来确保链接是否有效。

### 什么是网页上的损坏链接？

断开的链接，通常称为死链接，是网页上不再有效的任何链接，因为该链接存在潜在问题。当有人点击这样的链接时，有时会显示一条错误消息，如找不到页面。可能根本没有任何错误消息。这些本质上是无效的 HTTP请求，并且具有4xx和5xx状态代码。网页上链接断开的一些常见原因可能是：

-   目标网页已关闭、移动或不再存在。
-   移动网页但未添加重定向链接。
-   用户输入了不正确/拼写错误的 URL。
-   从网站中删除的网页链接。
-   使用激活的防火墙设置，浏览器有时也无法访问目标网页。

### 什么是网页上损坏的图像？

在某些情况下，网页上的图像无法正确加载，我们会看到“无法加载图像”或类似的错误消息。在这种情况下，图像要么已损坏，要么图像不在指定路径中。网页上的损坏图像是与图像相关联的链接，并且该链接无效。图片不显示在网页上可能有以下三种可能的原因：

-   首先，图像文件不在你的`<img src " ">`代码中指定的相同路径中。
-   其次，图像没有相同的路径或文件名。
-   第三，该位置的图像文件已损坏或受到损坏，或者它可能与特定浏览器不兼容，并且仅在该浏览器中渲染失败。

下图显示了损坏的图像的外观：

![网页上的损坏图像](https://www.toolsqa.com/gallery/selnium%20webdriver/1.broken%20image%20on%20a%20Web%20Page.png)

注意：图像在网页上可能会损坏，即使链接在页面上有效。在这种情况下，问题出在图像文件本身或浏览器的图像渲染上。

## 如何在 Selenium 中查找网页上的所有链接？

在找出 Selenium 中的损坏链接之前，最好通过查找网页上的所有链接来了解整体通用概念。超链接通常使用HTML Anchor ( `<a>`)标记在网页上实现。所以，如果识别并定位一个网页上所有的锚标签，然后得到对应的URL，我们就可以遍历该网页上的所有链接。让我们使用以下示例来理解这一点：

1.  导航到所需的网页[“https://demoqa.com/links”](https://demoqa.com/links)。
2.  右键单击 Web 元素，然后从下拉列表中单击检查选项。

![检查元素的链接](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Check%20The%20Link%20Of%20An%20Element.png)

1.  获取标签名称为“ a ”的元素，我们将使用此标签来检查所有链接

![检查元素以查找元素链接](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Inspect%20Element%20to%20Find%20Element%20Link.png)

下面提到的代码将帮助你从上述网页中获取链接(标签)并进行测试。

```java
import org.openqa.selenium.By; 
import org.openqa.selenium.WebDriver; 
import org.openqa.selenium.WebElement; 
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.Iterator; 
import java.util.List;

public class GetAllURLs {
   public static void main(String[] args) {

      //Create WebDriver instance and open the website.
      System.setProperty("webdriver.chrome.driver","./src/main/resources/chromedriver");
      WebDriver driver = new ChromeDriver();
      driver.manage().window().maximize();
      driver.get("https://demoqa.com/links");
      
      String url="";
      List<WebElement> allURLs = driver.findElements(By.tagName("a"));
      System.out.println("Total links on the Wb Page: " + allURLs.size());

      //We will iterate through the list and will check the elements in the list.
      Iterator<WebElement> iterator = allURLs.iterator();
      while (iterator.hasNext()) {
    	  url = iterator.next().getText();
    	  System.out.println(url);
      }
      
     //Close the browser session
      driver.quit();
    }
}
```

代码演练：

-   打开 URL 并检查所需的元素。
-   列出`<WebElement>`allURLss = driver.findElements (By.tagName("a")); 在此我们将获得带有标记名“a”的 WebElements 列表。
-   [使用迭代器](https://www.toolsqa.com/java/list-interface/#:~:text=Method%3A iterator()&text=For the reader unfamiliar with,point to the next page.&text=%2F%2F Get a list iterator over the elements in the list.,-ListIterator listIterator)遍历列表。
-   使用getText()方法打印链接文本。
-   使用driver.quit()方法关闭浏览器会话。

![在 Selenium 测试中查找网页上的所有链接](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Find%20all%20the%20Links%20on%20a%20Webpage%20in%20Selenium%20Tests.png)

执行上面的代码后，我们收到了一个链接数，即 11。此外，我们还检索了每个链接的标签并在控制台中打印出来。

### 如何在 Selenium 测试中找到损坏的链接？

正如我们所讨论的，我们可以检查链接 URL 的状态代码以验证它是否是有效链接。让我们修改上面的代码片段来检查我们如何验证某个链接是否有效：

```java
package testCases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class BrokenLinks {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver","./src/resources/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://demoqa.com/broken");

        //Storing the links in a list and traversing through the links
        List<WebElement> links = driver.findElements(By.tagName("a"));

        // This line will print the number of links and the count of links.
        System.out.println("No of links are "+ links.size());  
      
        //checking the links fetched.
        for(int i=0;i<links.size();i++)
        {
            WebElement E1= links.get(i);
            String url= E1.getAttribute("href");
            verifyLinks(url);
        }
        
        driver.quit();
    }
    
    
    public static void verifyLinks(String linkUrl)
    {
        try
        {
            URL url = new URL(linkUrl);

            //Now we will be creating url connection and getting the response code
            HttpURLConnection httpURLConnect=(HttpURLConnection)url.openConnection();
            httpURLConnect.setConnectTimeout(5000);
            httpURLConnect.connect();
            if(httpURLConnect.getResponseCode()>=400)
            {
            	System.out.println(linkUrl+" - "+httpURLConnect.getResponseMessage()+"is a broken link");
            }    
       
            //Fetching and Printing the response code obtained
            else{
                System.out.println(linkUrl+" - "+httpURLConnect.getResponseMessage());
            }
        }catch (Exception e) {
      }
   }
}
```

-   创建一个 WebDriver 实例并在浏览器中打开 URL [“https://demoqa.com/broken”](https://demoqa.com/broken)。
-   HttpURLConnection httpURLConnect= (HttpURLConnection)url.openConnection()：我们将检查每个使用 Java 中的 HttpURLConnection 类的 HTTP 状态。
-   httpURLConnect.setConnectTimeout (5000)：在创建连接之前等待很重要，因为加载 URL 可能需要时间。我们已将连接超时设置为 5 秒。
-   httpURLConnect.connect()：现在创建连接。
-   getResponsecode()：如果 URL 工作正常，我们将获取响应代码并打印OK 。否则会报错。

控制台输出： 我们从网页中获取了4 个链接，并显示了每个链接的HTTP 状态代码。

![使用 Selenium 查找链接](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Use%20Selenium%20to%20Find%20the%20links.png)

所有网页链接都应正常工作，以避免糟糕的用户体验并保持用户参与。所以这样，通过检查每个链接的状态代码，我们可以识别特定链接是否处于损坏状态。

### 如何在 Selenium 测试中找到损坏的图像？

如前所述，由于图像的 src 链接无效或图像渲染不佳，图像看起来已损坏。要 100% 确定图像是否损坏，我们需要验证图像的视角，即图像的 URL 应该有效，即应该返回状态码为 200，并且图像应该在浏览器窗口上正确呈现，我们可以使用JavaScript 对其进行验证。标记 1突出显示上图中的有效图像，标记 2 突出显示无效/损坏的图像。现在让我们看看如何在 Selenium 测试中定位和识别相同的内容：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;

public class BrokenImages {

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "./src/main/resources/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.demoqa.com/broken");

        // Storing all elements with img tag in a list of WebElements
        List<WebElement> images = driver.findElements(By.tagName("img"));
        System.out.println("Total number of Images on the Page are " + images.size());
        
        
        //checking the links fetched.
        for(int index=0;index<images.size();index++)
        {
            WebElement image= images.get(index);
            String imageURL= image.getAttribute("src");
            System.out.println("URL of Image " + (index+1) + " is: " + imageURL);
            verifyLinks(imageURL);
          
            //Validate image display using JavaScript executor
            try {
                boolean imageDisplayed = (Boolean) ((JavascriptExecutor) driver).executeScript("return (typeof arguments[0].naturalWidth !=\"undefined\" && arguments[0].naturalWidth > 0);", image);
                if (imageDisplayed) {
                    System.out.println("DISPLAY - OK");
                }else {
                     System.out.println("DISPLAY - BROKEN");
                }
            } 
            catch (Exception e) {
            	System.out.println("Error Occured");
            }
        }
        
        
     driver.quit();
   }
    
    public static void verifyLinks(String linkUrl)
    {
        try
        {
            URL url = new URL(linkUrl);

            //Now we will be creating url connection and getting the response code
            HttpURLConnection httpURLConnect=(HttpURLConnection)url.openConnection();
            httpURLConnect.setConnectTimeout(5000);
            httpURLConnect.connect();
            if(httpURLConnect.getResponseCode()>=400)
            {
            	System.out.println("HTTP STATUS - " + httpURLConnect.getResponseMessage() + "is a broken link");
            }    
       
            //Fetching and Printing the response code obtained
            else{
                System.out.println("HTTP STATUS - " + httpURLConnect.getResponseMessage());
            }
        }catch (Exception e) {
      }
   }
    
 }
```

当我们运行上面的测试时，我们将看到如下所示的输出：

![在 Selenium 中定位损坏的图像](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Locating%20broken%20images%20in%20Selenium.png)

正如我们所看到的，即使图像的 URL 返回了有效的状态代码，它仍然显示为已损坏，我们可以使用相应的JavaScript代码来识别它。

## 关键要点

-   重要的是要找到所有网页的链接，以确保没有链接断开，从而给用户带来糟糕的体验。
-   在任何网页上，都有损坏的链接和损坏的图像。图片不会显示；但是，该 URL 可能是可点击的并且工作正常。
-   不同的 HTTP 状态代码表示不同的含义。对于无效请求，4xx类HTTP状态码主要是客户端错误，5xx类状态码主要是服务端响应错误。
-   除了最终用户的观点之外，网页上的任何 URL 都不应被破坏，从多个角度来看也很重要。