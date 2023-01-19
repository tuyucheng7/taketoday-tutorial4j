在上一章中，我们了解了[什么是 Selenium Grid，](https://toolsqa.com/selenium-webdriver/selenium-grid/)它的好处和它的架构。在本章中，我们将学习使用 Grid 并完成 Selenium Grid 的过程——如何轻松设置集线器和节点

-   首先，配置集线器。
-   其次，配置节点。
-   然后我们将开发脚本并执行

为了简单起见，我们将只使用一台机器来设置 Hub，并在同一台机器上设置 Node 来运行测试。但我也会提到更改，这是在不同机器上运行测试所必需的。

### 第 1 步：下载 Selenium 服务器并设置 Selenium GRID Hub

1.  [从http://docs.seleniumhq.org/download/](https://docs.seleniumhq.org/download/)下载最新的 Selenium 服务器文件。

![Selenium Grid——如何轻松设置集线器和节点](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Selenium%20Grid%20%E2%80%93%20How%20to%20Easily%20Setup%20a%20Hub%20and%20Node.png)

2) 你可以将Selenium Server jar文件放在硬盘中的任何位置。但出于本教程的目的，将其放在Hub Machine的C: 驱动器上。 这样做之后，你就完成了 Selenium Grid 的安装。

![硒网格_7](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Selenium%20Grid_7.png)

3) 我们现在要启动一个中心。打开命令提示符并导航到C: 驱动器，因为这是我们放置 Selenium 服务器的目录。在命令提示符下，键入 java -jar selenium-server-standalone-3.3.1.jar -role hub

命令表示启动 Selenium 服务器并赋予其 Hub 角色。 集线器将启动，命令提示符应类似于上图。

![硒网格_7](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Selenium%20Grid_7.png)

笔记：

-   确保相应地更改命令中的版本号。
-   你的网格服务器将启动并运行，直到时间命令提示符窗口打开，如果你关闭它，这也将停止 selenium 服务器。
-   Selenium Grid 默认使用端口 4444 作为其 Web 界面。要在其他端口上启动相同的端口，请使用此命令： java -jar selenium-server-standalone-3.3.1.jar -port 4455 -role hub

1.  要验证集线器是否正在运行，请打开浏览器并导航到http://localhost:4444

控制台提供集线器上可用的信息。到目前为止，它将是空白的，因为没有机器连接到它。

![硒网格_7](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Selenium%20Grid_7.png)

1.  现在单击控制台链接，然后单击查看配置。集线器的配置将显示如下：

![硒网格_7](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Selenium%20Grid_7.png)

此页面包含许多详细信息，你的测试可能需要也可能不需要。我们将在后面的 Grid 教程中介绍这些内容。

### 第 2 步：设置节点机

理想情况下，Node 机器必须与 Hub 机器不同，但为了保持本教程的简单性，我在运行 Hub 的同一台机器上设置 Node。但是步骤完全相同，IP 地址更改为节点机器 IP 地址。

1.  我们还需要找出集线器机器的 IP 地址。转到命令提示符并键入IPCONFIG以找出 IP 地址。

![硒网格_7](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Selenium%20Grid_7.png)

这意味着 Hub 机器的 IP 地址将变为http:// + Hub 机器 IP 地址 + Hub 端口 =  http://192.168.1.164:4444

1.  还需要在 Node Machine 上下载 Selenium Server jar。因为我在同一台机器上设置节点，所以我不需要再次下载 Selenium Server jar。
2.  打开命令提示符。如果你在不同的机器上设置 Node，请登录到该机器并打开命令提示符。
3.  要向 Node Machine 注册 Hub Machine，请键入；

java -jar selenium-server-standalone-3.3.1.jar -role node -hub http://192.168.1.164:4444/grid/register -port 5555

![硒网格_7](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Selenium%20Grid_7.png)

1.  执行命令后返回到集线器并导航 URL http://localhost:4444 或 http://192.168.1.164:4444 集线器现在将显示连接到它的节点。

![硒网格_7](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Selenium%20Grid_7.png)

注意：上面的控制台页面提供了有关节点机器的信息，这些机器都连接到集线器。它提供有关节点机器 IP 地址、操作系统类型、浏览器等的信息。你将在上面的浏览器部分找到 5 个 Chrome、5 个 Firefox 和 1 个 IE 浏览器。这表示默认情况下你可以使用 5 个 Chrome、5 个 Firefox 和 1 个 IE 浏览器。

如果连接了更多机器，你会在控制台页面上看到更多块。

### 第 3 步：编写测试脚本

下面是你可以在 Eclipse 中创建的简单 WebDriver 代码。一旦你运行它，自动化将在节点机器上执行。

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class Grid_SetUp {
	public static WebDriver driver;

	public static void main(String[]  args) throws MalformedURLException, InterruptedException{

 		String URL = "https://www.DemoQA.com";
 		String Node = "https://192.168.1.164:4444/wd/hub";
 		DesiredCapabilities cap = DesiredCapabilities.firefox();

 		driver = new RemoteWebDriver(new URL(Node), cap);

 		driver.navigate().to(URL);
 		Thread.sleep(5000);
 		driver.quit();
 	}		
}
```

### 解释

1.  URL 将是集线器机器的IP 地址+ 集线器端口 + /wd/hub = "https://192.168.1.164:5555/wd/hub";

2) RemoteWebDriver接受URL类型的RemoteAddress ，它也接受Desired Capabilities。

WebDriver driver = RemoteWebDriver(new URL(Node), cap);

注意：如果有许多节点机器连接到 Hub 机器，Hub 有责任决定应该在哪个节点上执行测试。用户只能在 Desired Capabilities 的帮助下通过测试脚本中的要求。Hub 将自动检查每个节点是否符合指定要求，并在第一个满足要求的节点上执行测试。