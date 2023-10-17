## **一、简介**

**在线[编译器](https://www.baeldung.com/cs/how-compilers-work)** **是一种允许我们编译源代码并在线执行的工具**。这是一个很好的选择，特别是对于新的 Java 学生、导师或面试官。此外，共享代码或在私人或小组会议中进行协作非常容易。

除此之外，**我们不需要安装 JDK 或 IDE**。

在本文中，我们将探索目前最流行的 Java 在线编译器。

## **2. J涂鸦**

[JDoodle](https://www.jdoodle.com/online-java-compiler)是一种使用包括 Java 在内的多种编程语言编译和运行源代码的简便快捷方式。更重要的是，JDoodle 提供了与 MySql 和 MongoDB 一起工作的终端、对等编程工具、远程执行代码的 API 以及博客或网站的嵌入功能。

它的用户界面有一个用户友好的编辑器和输入字段，包括：

-   命令行参数
-   互动模式
-   **JDK 版本 8 到 17 可用**
-   **直接从 Maven 存储库附加外部库**。我们只需要指定组、工件 ID 和库的版本。

[![ojc 涂鸦](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-jdoodle.png)](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-jdoodle.png)

此外，我们还可以打开、保存和下载源文件。

此外，该工具以秒为单位显示编译和执行以及 CPU 时间。内存使用情况以千字节为单位显示。

需要注意的重要一点是，JDoodle 有一个用于简单的单文件程序和更快执行的基本 IDE，还有一个用于多个文件、自定义库和文件读/写的高级 IDE。

## **3.科迪瓦**

**由于其独特的功能，此在线编译器绝对名列前茅**。要开始在 [Codiva](https://www.codiva.io/)工作，我们需要开始一个新项目。

它的设计类似于常见的 IDE 界面。左边是项目结构，右边是源文件编辑器。默认情况下，工作目录是*src > hello > com > example*。我们可以通过单击文件名旁边的*+号轻松添加新的 java 文件。*

源文件立即添加到工作树中：

[![ojc 科迪瓦](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-codiva.png)](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-codiva.png)
首先，让我们单击右上角的*“运行”按钮。***它在我们开始输入时编译代码**。此外，**Codiva 会显示编译期间的所有错误**，并在我们完成输入时显示程序执行结果。

此外，**它还提供语法突出显示和自动完成功能以节省时间**。

最后但同样重要的是，我们可以将 Java 项目嵌入到任何博客或网站中。首先，我们需要按下*共享*按钮并公开项目。之后，Codiva 给出了两种分享方式：

-   公共网址
-   复制并粘贴到我们网站的 HTML 代码

作为一个缺点，该工具不支持编译器设置，并且仅适用于 Java、C、C++ 语言。

## **4.布朗西**

[与其他在线编译器相比， Browxy 的](http://www.browxy.com/) 运行速度相对较快。可用的语言有 C、C++、C#、Java、Python 和 PHP。

我们可以轻松地在网络上共享源文件。*GetUrl*按钮生成文件的 URL，允许我们共享文件。更重要的是，**登录用户可以公开发布他们的代码**。

点击*Publish*按钮后，项目出现在*Published Code*部分：

[![ojc 布朗尼](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-browxy.png)](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-browxy.png)

它具有用于控制台、小程序和日志输出的单独视图。

**另一个优点是对运行代码的限制最少**。我们可以进行外部 URL 调用来测试一些 API。

## **5. Rex测试仪**

最初，[Rextester](http://rextester.com/l/java_online_compiler)被设计为正则表达式测试器。后来，它成长为支持绝大多数编程语言的在线 IDE。

它支持不同的编辑器和布局视图：

[![ojc rexter](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-rexter.png)](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-rexter.png)

还值得一提的是现场合作功能。按下适当的按钮后，Rextester 会生成一个可共享的 URL。

拥有 URL 的任何人都可以在永久实时会话中编码。我们可以看到其他人做出的改变，也可以做出自己的改变。

## **6.在线GDB**

**[OnlineGDB](https://www.onlinegdb.com/online_java_compiler)是许多编码语言（包括 Java）的编译器和调试器**。代码格式也可用于使编码更舒适。

更重要的是，**我们可以通过简单地点击我们想要观察的行来添加断点**。按下*调试*按钮后，调试模式开始。它将一步步越过之前设置的断点。

用户可以在适当的窗口中查看局部变量值或调用堆栈：

[![ojc 在线gdb](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-onlinegdb.png)](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-onlinegdb.png)

## **7.编译java**

[Compilejava ](https://www.compilejava.net/)是运行 Java 代码的简单在线工具。它有许多主题，包括屏幕截图中显示的*午夜。*Java 初学者、学生，可以将其用于教育目的。

它始终在最新版本的 Java 上运行。我们可以通过提供 URL**从 Gist 导入代码片段：**

[![ojc编译Java](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-compileJava.png)](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-compileJava.png)

## **8. Paiza.io**

该在线编译器具有友好且易于使用的界面。与其他高级编译器一样，[Paiza](https://paiza.io/en/projects/new?language=java)具有以下所有功能：

-   支持多文件
-   快速自动完成
-   在 GitHub 或 Gist 中链接和共享代码
-   通过网络公开或私人共享
-   与用户组协作
-   任务调度

[![ojc派萨](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-paiza.png)](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-paiza.png)

它支持大多数顶级编程语言，包括 C、C++、C#、JVM 语言和 Python。

## **9.远程面试**

[RemoteInterview](https://www.remoteinterview.io/online-java-compiler) 专注于协助大多数常见编程语言的在线面试过程。**它提供实时结对编程、**共享代码片段、**进行视频通话、录制和保存采访**。

面试官可以通过分享邀请链接邀请任何人参加现场会议：

[![ohc面试](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-interview.png)](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-interview.png)

## **10. 意念**

[Ideone](https://ideone.com/)编译和执行 60 多种编程语言的代码。它允许代码共享为：

-   public——所有人都可以使用
-   secret – 仅对与我们共享 URL 的人可用
-   私有——仅对所有者可用

[![ojc的想法](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-ideone.png)](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-ideone.png)

Ideone 允许我们提交的程序有时间限制和内存使用限制：

-   10秒编译时间
-   5 秒执行时间（未登录用户）和 15 秒登录用户
-   256 MB 用于内存使用

最后，在*示例*部分下，我们可以找到最新的运行和执行的代码片段。我们可以自己分叉并尝试它们。

## **11. Repl.it**

[Repl.it](https://repl.it/)有一个支持多种语言的交互式编程环境。此外，我们可以创建一个包含 HTML 和 JS 文件的简单 Web 项目来练习基本的 Web 技能。它在教师、面试官、大学和学院中非常受欢迎。

首先，我们需要在平台上注册。值得一提的是，这个要求在之前列出的任何编译器中都不存在：

[![ojc代表](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-repl.png)](https://www.baeldung.com/wp-content/uploads/2018/09/ojc-repl.png)

**一个重要的功能是内置的版本控制**，它允许保存当前工作、稍后使用会话或将更改与每个版本进行比较。

此外，**它还有一个多功能的终端仿真器**，提供丰富的API和代码共享/协作工具，第三方库支持。

## **12.总结**

在本指南中，我们简要介绍了许多可用的 Java 在线编译器。

其中一些提供了独特的功能，如边输入边编译、代码提示、自动完成、内置调试或代码格式化。

尽管如此，**与桌面版本相比，现代在线编译器的功能仍然有限。**