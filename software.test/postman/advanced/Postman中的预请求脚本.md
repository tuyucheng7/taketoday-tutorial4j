在前面的教程中，我们介绍了测试以及如何使用 Runner 运行它们。我们了解到只有在请求成功并且我们收到响应后才会运行测试。但是，在收到响应之前，我们可能必须运行许多操作。这些在 Postman 中被称为预请求脚本。本教程将向你介绍以下内容

-   Postman 中的脚本
-   邮递员沙箱
-   邮递员控制台
-   通过脚本创建变量

在了解 Postman 中的预请求脚本之前，我们会快速看到脚本。

### Postman 中的脚本是什么？

脚本是一段代码，你可以编写并让 Postman 在测试生命周期的特定点执行它。Postman 允许你编写预请求脚本，该脚本将在 Request 之前运行，而测试脚本将在Response之后运行。Postman 中使用脚本来启用请求和集合的动态行为。它允许你编写测试、更改参数甚至在请求之间传递数据。可以将脚本添加到请求、集合、文件夹或独立请求中。Postman 中的脚本是在Postman Sandbox 中编写的。

![Scripts_diagram](https://www.toolsqa.com/gallery/Postman/1.Scripts_diagram.png)

### 什么是邮递员沙箱？

Postman Sandbox 是一个用 Javascript 编写的强大的执行环境，因此你编写的要在 Postman 中运行的任何脚本都必须使用 Javascript，就像我们在[测试教程](https://toolsqa.com/postman/test-and-collection-runner-in-postman/)中运行的测试一样。然后在这个环境中执行这些脚本，然后我们会看到结果。我希望你一定在生活中的某个时刻使用过编译器。你需要使用编译器设计为 Turbo C 的相同语言进行编码，你可以在 Turbo C 编译器中编写和运行 C 代码，但不能编写 python 代码。沙箱也是如此，这就是为什么你需要用 JavaScript 编写的原因。

## 什么是邮递员控制台？

正如 Postman 官方博客中所述，“ Postman 控制台类似于开发者控制台的浏览器版本，只是它针对 API 开发进行了调整”。在某些时候，我们可能无法在 Postman 中执行预请求脚本时看到问题出在哪里。Postman 控制台记录了请求中发生的所有事情，因此我们可以查看控制台并查看错误。可以参考下图查看用于许多请求的典型 Postman 控制台。

![Postman_Console_Example](https://www.toolsqa.com/gallery/Postman/2.Postman_Console_Example.png)

虽然可以通过下面介绍的快捷命令打开 Postman 控制台，但 Postman 也有一个专门用于打开 Postman 控制台的图标。此图标位于侧边栏([邮递员导航](https://toolsqa.com/postman/postman-navigation/))

![Postman_Console_Icon](https://www.toolsqa.com/gallery/Postman/3.Postman_Console_Icon.png)

它的行为类似于浏览器开发控制台，其中所有内容都是可见的，你在该网站中发送的所有请求或页面代码也是如此。如果我们需要捕获错误或查看我们的执行有多正确，我们使用console.log功能。通过这个我们可以在特定于控制台的日志语句上打印，这可以帮助我们跟踪执行并发现代码中的问题。这个简单的例子将帮助你理解这个概念。

### 如何在 Postman 控制台中查看预请求脚本日志

1.创建一个名为Scripts的新集合(见 [集合](https://toolsqa.com/postman/collections-in-postman/) 章节)

1.  在里面写[天气api](https://bookstore.demoqa.com/swagger/#/BookStore/BookStoreV1BooksGet)请求。

![Scripts_Collection](https://www.toolsqa.com/gallery/Postman/4.Scripts_Collection.png)

3.在 Windows 上按Ctrl+Alt+C(在 mac 上按 Cmd + Alt+ C )打开Postman 控制台。

![邮递员控制台](https://www.toolsqa.com/gallery/Postman/5.Postman_Console.png)

注意：始终记得在发送请求之前先打开控制台，否则你的请求将不会记录在控制台中。

4..按发送 并查看 Postman 控制台上可见的内容。

![Postman_Console_Log](https://www.toolsqa.com/gallery/Postman/6.Postman_Console_Log.png)

可以看出，请求已登录到控制台。登录控制台由 Postman 自动完成，但如果你想检查你的代码，你也可以自己完成。如上所述，console.log功能就是用于此目的。当我们执行console.log ( string ) 时，字符串会按原样打印在控制台上。我们也可以传递变量而不是字符串。这很有帮助。假设我们有一个函数没有给我们正确的输出。如果我们在控制台中写入 console.log(variable_name) ，我们可以很容易地看到我们正在处理的变量是否具有与我们预期相同的值。在下一节中，我们将使用console.log来清除所有疑虑。

## Postman 中的 Pre Requests 脚本是什么？

如上所述，Postman 中的预请求脚本是在执行请求之前运行的脚本。它在 Postman 沙箱中运行，当我们必须在执行过程中动态执行某些操作时，它会非常方便。这些可以设置变量或清除它们，我们将在本教程后面看到。Postman 中的预请求脚本可以在文件夹、请求或集合上运行，但如果我们在所有这三个中都指定了脚本，则存在执行脚本的顺序

-   与集合关联的预请求脚本将在集合中的每个请求之前运行。
-   与文件夹关联的预请求脚本将在文件夹中的每个请求之前运行。

为了演示使用 Postman 在执行之前运行预请求脚本并在执行之后运行测试脚本，我们将在此处查看一个非常简单的示例。

1.  转到 我们在上面创建的同一集合中的天气 api 中的预请求选项卡。

![Pre_Request_Script_Tab](https://www.toolsqa.com/gallery/Postman/7.Pre_Request_Script_Tab.png)

2.Write console.log("This is a pre request script");

![邮递员中的预请求脚本](https://www.toolsqa.com/gallery/Postman/8.Pre-Request%20Script%20in%20Postman.png)

3.转到测试选项卡并编写

console.log("这是一个测试脚本");

![Tests_Tab_Console_Log](https://www.toolsqa.com/gallery/Postman/9.Tests_Tab_Console_Log.png)

4.按Send，打开Postman Console看看。

![控制台_脚本_测试](https://www.toolsqa.com/gallery/Postman/10.Console_Scripts_Testing.png)

预请求脚本在请求执行之前运行，而测试脚本在请求之后运行。

### 在 Postman 中使用预请求脚本创建变量

我们在 Postman 中使用预请求脚本来处理请求执行之前需要做的所有事情，例如设置变量、清除变量或获取值等。在本教程中，我们将尝试在我们在学习[环境](https://toolsqa.com/postman/environment-variables-in-postman/)时创建和使用的环境Weather Api。

1.转到天气 api请求中的预请求脚本选项卡。

2.确认你选择了Weather Api环境(从[环境和变量](https://toolsqa.com/postman/environment-variables-in-postman/)章节学习)。

![天气_环境](https://www.toolsqa.com/gallery/Postman/12.Weather_Environment.png)

3.在编辑器中编写如下代码 postman.setEnvironmentVariable('username','Harish') ;

![Set_Envrionment_Variable_Pre_Request_Script](https://www.toolsqa.com/gallery/Postman/13.Set_Envrionment_Variable_Pre_Request_Script.png)

这将在环境中创建一个名为“ username ”和值为“ Harish ”的变量。

4.按“发送”并单击眼睛图标查看当前变量(在“[环境和变量](https://toolsqa.com/postman/environment-variables-in-postman/)”一章中了解)

![新变量创建](https://www.toolsqa.com/gallery/Postman/14.New_Variable_Created.png)

查看变量，我们在通过脚本创建的环境中存在变量用户名。

这样我们就可以在脚本执行之前在Postman中的pre-request脚本中执行各种任务，而无需经历一次又一次创建和删除变量的过程。你可以在练习部分找到预请求脚本中要执行的许多其他任务。我们将从这里进入下一章。

### 练习练习

-   使用以下代码设置全局变量

postman.setGlobalVariable(variableName, variableValue);

-   使用以下语法获取环境变量的值

postman.getEnvironmentVariable(变量名);

-   使用以下代码语法清除全局变量

postman.clearGlobalVariable(变量名);