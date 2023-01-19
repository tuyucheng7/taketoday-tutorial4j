Internet Explorer 驱动程序服务器是你的测试和 IE 浏览器之间的链接。如果你的系统上没有运行服务器，你将无法与 IE 浏览器通信。那么这个服务器到底是什么？我们可以玩一下吗？

是的，我们可以，让我们看看如何！！！

基本上，IE 驱动程序服务器是由创建 Selenium WebDriver 的出色团队创建的小型应用程序。由于 IE 在 java 中没有本地实现或 API，因此他们别无选择，只能创建一个服务器。IE Driver 服务器实现了 WebDriver 协议。WebDriver 协议是一个 W3 标准，它提供了有关浏览器如何公开自身以进行编程访问的基本指导。它还强制要求一个独立于语言的界面，以便可以通过选择的任何编程语言从外部控制浏览器。[你可以在此处](https://www.w3.org/TR/2015/WD-webdriver-20150921/)阅读更多信息。

假设你将点击上面给出的链接并阅读一些内容，让我直接进入细节。简而言之，WebDriver 实现表示你可以通过发送 HTTP 命令请求来控制 Web 浏览器。每个命令都可以指示浏览器做某事。这正是 IE 驱动程序服务器所做的，它启动服务器然后等待命令。这些命令由你的测试以各种 WebDriver 点操作的形式发出。像 WebDriver.get、WebDriver.findElement 等。

## 从命令行启动 Internet Explorer 驱动程序服务器

要下载服务器，你可以到 [这里](https://selenium-release.storage.googleapis.com/index.html)。只需选择最新版本并根据你使用的是 32 位还是 64 位操作系统下载即可。

![下载IE服务器](https://www.toolsqa.com/gallery/selnium%20webdriver/1.DownloadIEServer.jpg)

将下载的文件解压缩到计算机上的已知位置。使用命令提示符简单地转到你解压缩文件的位置。如下图所示

![CMDIe驱动程序](https://www.toolsqa.com/gallery/selnium%20webdriver/2.CMDIeDriver.jpg)

简单键入以下命令以了解 IE 驱动程序服务器的参数

-   IeDriverServer.exe -帮助

你将获得所有可用开关的列表，如下图所示。记下所有可用选项，我们将使用它们来指定不同的参数。

![CMD帮助](https://www.toolsqa.com/gallery/selnium%20webdriver/3.CMDHelp.jpg)

我们可以看到我们有

1.  /port：指定此服务器应在哪个端口上运行
2.  /host:host ip 指定主机详情
3.  /log-level：根据你想要的调试类型，你可以指定日志级别。类似于你在 Log4j 中所做的事情
4.  /log-file：指定日志文件的位置，所有日志都将定向到该位置。
5.  /实现：我不知道它是做什么的？有人吗？
6.  /extract-path：它是我们可以存储支持文件的位置。我无法将它用于任何目的。
7.  /silent：如果你不想显示所有的启动日志。

让我们在端口 1080 上启动服务器，日志文件和日志级别为 Debug。你可以像这样在一个命令中指定所有这些信息

IEDriverServer.exe /log-file=c:\users\abc\desktop\ielogs.txt /port=1080 /log-level=DEBUG

或者

IEDriverServer.exe /port=1080 /log-level=DEBUG

服务器将从端口 1080 启动，服务器的 IP 地址将列在命令提示符中出现的日志中。如下所示

![CMD服务器启动](https://www.toolsqa.com/gallery/selnium%20webdriver/4.CMDServerStart.jpg)

这就是你可以启动具有特定配置的服务器的方法。

## 连接到现有的 IE Driver 服务器连接

现在让我们连接到这个服务器并启动 IE。这样做的代码很简单。你所要做的就是在测试代码中指定要连接的服务器。这是一个工作示例，请确保你最终替换了 IP 和其他详细信息

```java
	public static void main(String[] args) {

		String exePath = "C:\\Users\\abc\\Documents\\IEDriverServer\\IEDriverServer.exe";
		InternetExplorerDriverService.Builder serviceBuilder = new InternetExplorerDriverService.Builder();
		serviceBuilder.usingPort(1080); // This specifies that sever should start at this port
		serviceBuilder.usingDriverExecutable(new File(exePath)); //Tell it where you server exe is
		serviceBuilder.withHost("2.45.0.0");
		InternetExplorerDriverService service = serviceBuilder.build(); //Create a driver service and pass it to Internet explorer driver instance
		InternetExplorerDriver driver = new InternetExplorerDriver(service);
		driver.get("https://toolsqa.com");
	}
```

运行此代码并查看服务器窗口中发生的情况。你会看到日志将流入。所有这些日志都将来自上面代码的传入连接。你还将看到服务器将启动浏览器并将其导航到命令指定的 toolsqa.com。如果你选择了一个日志文件，你将获得日志文件中的所有日志，否则所有日志将出现在命令提示符中。这是你的日志文件的样子

![IE日志记事本](https://www.toolsqa.com/gallery/selnium%20webdriver/5.IELogsNotepad.jpg)

并且在命令提示符窗口中将出现相同的日志，如下所示

![IE日志CMD](https://www.toolsqa.com/gallery/selnium%20webdriver/6.IELogsCMD.jpg)

所以基本上这就是启动 IE 服务器、连接到它然后查看一些日志的方法。使用不同的可用选项来启动 IE 驱动程序服务器。