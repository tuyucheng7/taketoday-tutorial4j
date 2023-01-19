在多个顶级组织中，Apache Tomcat被用作部署各种基于 Java 的应用程序的主要应用程序服务器之一。为了获得统一的体验，这些组织更喜欢将 Jenkins 部署在同一台 tomcat 服务器上，并且作为基于Java 的应用程序， Jenkins也很容易部署在 tomcat 服务器上并提供所需的服务。让我们快速了解Jenkins Tomcat的集成并了解详细信息，我们如何设置 tomcat 服务器，然后在该 tomcat 服务器上部署 Jenkins。我们将在本文中介绍以下详细信息，以了解Jenkins 在 Apache Tomcat 服务器上的部署：

-   什么是 Tomcat 服务器？
    -   Tomcat 设置的先决条件是什么？
    -   如何在 Windows 上安装 Tomcat 服务器？
-   如何在 Tomcat 服务器上部署 Jenkins？

## 什么是 Tomcat 服务器？

[Apache Tomcat](https://tomcat.apache.org/index.html)是一个功能强大/强大的 servletJava容器，用于运行 Web 应用程序。它是由Apache 软件基金会开发和维护的。Apache tomcat的最初想法是托管和部署 Java servlet，它是服务器端 Java 代码，用于管理来自使用 Java 构建的客户端应用程序的 HTTP 结果。Apache Tomcat 为相关的 servlet 提供了 Web 服务器处理的基本功能。它支持 java servlet 生命周期，即init()、service() 和 destroy()阶段。它是部署 Java 应用程序的首选 Web 服务器之一。你可以在“[什么是 Tomcat？](https://www.educba.com/what-is-apache-tomcat/) ”中阅读Tomcat 服务器的完整详细信息”。现在要安装Tomcat服务器，有几个先决条件。让我们快速了解一下：

### Tomcat 设置的先决条件是什么？

众所周知，在安装之前，我们需要检查一些先决条件。所以，在安装Tomcat Server之前，让我们了解一些前置要求。

-   我们的系统上应安装最低Java 8版本。如果未安装，请访问“[如何安装 java ”。](https://www.toolsqa.com/selenium-webdriver/install-java/)
-   安装 Java 后，在系统中设置环境变量。请访问“[如何安装 java](https://www.toolsqa.com/selenium-webdriver/install-java/) ”一文来设置环境变量。

满足上述先决条件后，我们就可以安装和设置Tomcat 服务器了。我们将介绍在 Windows 操作系统上安装的详细信息。让我们看看如何在 Windows 上安装 Tomcat 服务器：

### 如何在 Windows 上安装 Tomcat 服务器？

好了，我们可以在 Windows 上安装Tomcat 服务器了。在 Windows 上安装 Tomcat 服务器，请按照以下步骤操作：

第一步：首先到[Apache Tomcat官网](https://tomcat.apache.org/) ，选择最新的稳定版，我们选择的是Tomcat 9。

![Tomcat Apache 官方页面](https://www.toolsqa.com/gallery/Jenkins/1.Tomcat%20Apache%20Official%20Page.jpg)

第 2 步：其次，根据你的机器配置选择合适的二进制发行版，因为我将选择64 位 Windows zip。

![Apache Tomcat 下载页面](https://www.toolsqa.com/gallery/Jenkins/2.Apache%20Tomcat%20Download%20Page.jpg)

第 3 步：第三，下载 zip 文件并将其存储在你的任何工作目录中。另外，将此文件存储在工作目录中后解压缩。

![下载开始](https://www.toolsqa.com/gallery/Jenkins/3.Downloading%20Started.jpg)

第四步：现在，打开命令提示符，转到 bin 文件夹路径，然后键入以下命令：

```
startup.bat
```

![启动tomcat服务器](https://www.toolsqa.com/gallery/Jenkins/4.Starting%20tomcat%20server.jpg)

第 5 步：命令执行后，将打开一个新窗口“ Tomcat ”，其中将进行一些处理。

![Tomcat服务器的处理](https://www.toolsqa.com/gallery/Jenkins/5.Processing%20of%20Tomcat%20server.jpg)

第 6 步：一旦处理完成，只是为了验证我们的系统上是否安装了 tomcat 服务器，打开浏览器，然后点击 URL http://localhost:8080/。点击URL后，我们将看到如下所示的窗口，如果我们的机器上成功安装了 tomcat。

![验证 Tomcat 服务器安装](https://www.toolsqa.com/gallery/Jenkins/6.Validation%20of%20Tomcat%20server%20installation.jpg)

那么，这样，我们就可以在系统中安装tomcat服务器了。在下一节中，我们将看到如何在Tomcat服务器上部署 Jenkins 。

## 如何在 Tomcat 服务器上部署 Jenkins？

好了，现在让我们看看安装 Tomcat 服务器后的 Jenkins 设置。请按照以下步骤在 Tomcat 服务器上部署 Jenkins：

第一步：进入webapps文件夹，在里面粘贴[我们在上一篇文章Jenkins安装](https://www.toolsqa.com/jenkins/install-jenkins/)中下载的jenkins.war文件。

![将 jenkins.war 文件存储在 webapps 文件夹中](https://www.toolsqa.com/gallery/Jenkins/7.Storing%20jenkins.war%20file%20in%20webapps%20folder.jpg)

第二步：打开命令提示符，进入tomcat目录下的bin文件夹，再次运行startup.bat命令。

![命令提示符中的 startup.bat 命令](https://www.toolsqa.com/gallery/Jenkins/8.startup.bat%20command%20in%20command%20prompt.jpg)

第三步：一旦startup.bat 文件运行，除了上面的命令提示符窗口外，还会有一个名为tomcat的窗口将在另一个命令提示符中打开。

![命令提示符中的 Tomcat 窗口](https://www.toolsqa.com/gallery/Jenkins/9.Tomcat%20window%20in%20command%20prompt.jpg)

在配置过程中，你可能还会弹出 Windows 安全警报。单击允许访问按钮。

![Windows Defender 防火墙设置](https://www.toolsqa.com/gallery/Jenkins/10.Windows%20Defender%20Firewall%20Setup.jpg)

第 4 步：完成所有设置后，你将在命令提示符的最后一行收到消息“ Jenkins is fully up and running ”。

![Tomcat 设置完成](https://www.toolsqa.com/gallery/Jenkins/11.Tomcat%20setup%20completed.jpg)

第 5 步：现在打开浏览器并输入 URL： http://localhost:8080/jenkins/ 并按ENTER键，默认情况下你将第一次看到以下页面。

注意：如果你已经通过 jenkins.war 文件在你的机器上安装了 Jenkins，那么你将被直接重定向到 Jenkins 登录页面，输入用户名和密码后，你将直接到达 Jenkins 仪表板，即第 12 步。

![Jenkins 默认屏幕](https://www.toolsqa.com/gallery/Jenkins/12.Jenkins%20By%20default%20screen.jpg)

第六步：在这里你需要输入你在处理jenkins.war文件时得到的管理员密码(请看教程“[安装Jenkins](https://www.toolsqa.com/jenkins/install-jenkins/) ”了解这个管理员密码是如何生成的)然后点击继续按钮。

![输入管理员密码](https://www.toolsqa.com/gallery/Jenkins/13.Put%20Admin%20Password.jpg)

第 7 步：单击“继续”按钮后，你将重定向到建议你安装建议插件的页面。现在点击“安装建议的插件”。

![安装建议的插件](https://www.toolsqa.com/gallery/Jenkins/14.Install%20suggested%20plugin.png)

第八步：点击后，标准插件安装将自动开始，如下图所示。

![插件安装开始](https://www.toolsqa.com/gallery/Jenkins/15.Plugin%20installation%20started.png)

第 9 步：安装所有建议的插件后，你将重定向到用户帐户页面，如下所示。

![账户页面](https://www.toolsqa.com/gallery/Jenkins/16.Account%20Page.png)

在这里你需要输入用户名、你要输入的新密码、确认密码、全名、电子邮件地址。之后，单击“保存并继续”按钮。你可以选择“跳过并以管理员身份继续”选项，在这种情况下，你需要输入相同的管理员密码。我们始终建议你根据自己的选择输入新密码。

第 10 步：单击“保存并继续按钮”后，你将重定向到实例配置屏幕。这里只需点击“保存并完成按钮”。

![实例配置](https://www.toolsqa.com/gallery/Jenkins/17.Instance%20Configuration.png)

第 11 步：点击后，你将重定向到一个新屏幕。它将显示一条消息，如“ Jenkins 已准备就绪”。

![Jenkins就绪消息](https://www.toolsqa.com/gallery/Jenkins/18.Jenkins%20Ready%20Message.png)

第 12 步：现在单击“开始使用 Jenkins ”按钮，你将重定向到Jenkins 仪表板。

![Jenkins仪表板](https://www.toolsqa.com/gallery/Jenkins/19.Jenkins%20Dashboard.png)

因此，Jenkins仪表板已准备就绪，我们将探索和配置各种可用选项。

## 要点：

-   Tomcat是一个用于运行 Web 应用程序的 Servlet Java 容器。
-   此外，Java 和设置路径环境变量是安装 Tomcat 服务器的先决条件。
-   此外，下载并运行 tomcat 服务器后，我们需要将 jenkins.war 文件放入 webapps 文件夹中。之后，只需运行 startup.bat 命令