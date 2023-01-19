在上一个教程中，我们讨论了一般的[GUID](https://www.toolsqa.com/postman/guid/)以及它如何影响将数据保存到数据库的整个 Internet 机制。事实证明，它是解决已存在一段时间的问题的令人满意的革命性解决方案。我们讨论过使用 GUID 的最重要原因是使用GUID时几乎不可能发生冲突。此外，数据库转换变得顺畅，服务器功能也变得简单。现在我们已经熟悉了GUID 的概念，我们将在本教程中实际实现它。

先决条件：

-   GUID 知识 - 什么是 GUID 和 GUID 的结构([参考教程](https://www.toolsqa.com/postman/guid/))。
-   Knowledge of Environment and Variables in Postman - What is environments and variables in Postman([参考教程](https://www.toolsqa.com/postman/environment-variables-in-postman/))。
-   Knowledge of Pre-request script - 什么是 Postman 中的预请求脚本([参考教程](https://www.toolsqa.com/postman/pre-request-script-in-postman/))。

### 什么是动态变量？

在计算机科学中，有两种变量，即静态变量和动态变量。

静态变量是那些在程序执行之前分配内存的变量。例如，当我们声明一个像var name这样的变量时；我们告诉操作系统为变量名保留内存。如果你使用一个变量而不像name = "Harish" 这样声明它，计算机会感到困惑。没有分配给这样一个变量的内存，所以计算机认为不存在这样的变量，因此它会抛出错误。

另一种类型的变量是动态变量。与静态变量不同，动态变量在程序运行时而不是事先分配内存。这就是为什么它们被命名为动态的。这可能发生在诸如 Perl 之类的动态编程语言中。在这里，你可以确定一个变量而不指定其类型，并在跨程序的任何地方使用它。

Postman 提供动态变量支持。一个这样的变量是GUID。 在 Postman 中使用 GUID 时，不需要特别指定变量的内存或值。只需在双花括号中指定guid (在下一节中显示)，Postman 将处理其余部分。还有其他变量，例如Postman 中的时间戳，所有这些变量的工作方式与GUID 类似。让我们看看GUID在[Postman](https://www.getpostman.com/)中是如何实现的。

## 邮递员中的 GUID

正如我们上面所讨论的， GUID属于动态变量类别。它不需要预定义的内存使用或声明。

在本教程中，我们将向你展示如何在 Postman 中生成 GUID以及如何将其作为标头传递。我们没有为此使用任何实际请求，因为我们没有在此处分析响应。这里的目的是了解How to create dynamic GUID？稍后可以在请求 URL、正文或标头中使用。因此，在本教程的这一部分中，我们将逐步涵盖以下内容：

-   使用 Postman 提供的动态 GUID
    -   URL 中的 GUID
    -   正文中的 GUID
    -   标头中的 GUID
-   在 Postman 中手动生成 GUID
    -   在 Postman 中生成 GUID
    -   将 GUID 保存到环境变量

### 如何在 Postman 中使用动态 GUID？

postman中的GUID可以直接使用，不需要写任何代码。GUID的主要用途是直接在 Postman 中的 URL 和 Headers 中。因为通过这两件事，你可以将数据映射到数据库。例如，你可以在 URL 中传递 GUID 以获取与该键关联的数据。也可以通过在标头内发送GUID来完成同样的操作。你发送GUID的位置完全取决于你正在处理的请求类型。它可以要求 GUID 出现在标头、正文或代码中。

#### 如何在 Postman 中使用 GUID 作为查询参数？

GUID也可以在 URL 中使用，无需任何初始化或任何预先处理。直接在request中写这个变量就可以顺利运行了。如下所示。

![邮递员请求中的 GUID](https://www.toolsqa.com/gallery/Postman/1.GUID%20in%20Postman%20Request.webp)

是的，就是这么简单。由于这个机制是Postman自己提供的，所以不需要做任何其他的事情。只需将其作为变量传递并使用它即可。

#### 如何在 Postman 中使用 GUID 作为 Request Body？

在 Postman 中，你可以通过选择适当的正文格式将GUID直接实现到请求正文中。请求类型为JSON 的情况如下所示。

![邮递员正文中的 GUID](https://www.toolsqa.com/gallery/Postman/2.GUID%20in%20Postman%20Body.webp)

#### 如何在 Postman 中使用 GUID 作为 Header？

在Postman中，GUID可以用在Header中，如下图：

![标头中的 GUID](https://www.toolsqa.com/gallery/Postman/3.GUID%20in%20Header.webp)

标头将包含请求中 GUID 的实际值。

### 在 Postman 中手动生成 GUID

在预请求脚本中使用GUID并不是主流。在脚本中使用GUID的次数较少，因为它每次都会为你提供不同的值，这些值最终将映射到标头或数据库查询。

#### 如何在 Postman 中手动生成 GUID？

为此，请在地址栏中输入网址www.google.com。

![Google_端点](https://www.toolsqa.com/gallery/Postman/4.Google_Endpoint.webp)

转到Postman 中的预请求脚本。

![预请求](https://www.toolsqa.com/gallery/Postman/5.Pre_Request.webp)

在预请求脚本中键入以下代码：

-   var uuid = require ('uuid'); - 在变量uuid 中加载JS 的uuid 模块。Require 在 JS 中用于加载模块。
-   var myUUID = uuid.v4(); - 上一步加载的模块的 UUID 版本 4 保存在变量 myUUID 中。
-   控制台日志(myUUID)；- 这一行是在控制台打印 myUUID 的值。

![UUID代码](https://www.toolsqa.com/gallery/Postman/6.UUID%20code.webp)

注意：重要的是要注意名称为 guid 的变量不能在 Postman 的预请求脚本中使用。你必须使用 uuid 。虽然 guid 可以直接在 URL 和 Headers 中使用。另外需要注意的是，guid可以直接作为环境变量使用，不需要单独设置为变量。

现在我们知道 Postman 获取到了GUID 的值。因此，我们将尝试在控制台上打印它。打开控制台以查看我们将发送的请求。

![邮递员控制台](https://www.toolsqa.com/gallery/Postman/7.Postman%20Console.webp)

现在，按发送执行请求。 查看控制台。它将向你显示收到的uuid值。

![UUID控制台](https://www.toolsqa.com/gallery/Postman/8.UUID%20console.webp)

请参阅[GUID 教程](https://www.toolsqa.com/postman/guid/)以了解更多有关我们在此处收到的 GUID 格式的信息。

重要的提示：

[动态变量在Postman Sandbox](https://www.toolsqa.com/postman/pre-request-script-in-postman/)下不起作用。所以，如果你想使用普通的 javascript 保存 guid 的值(使用全局变量格式{{variable}})，你将无法这样做。这也已在下面显示，因为你可以在第一行代码中注意到错误。

![使用 Guid 作为变量](https://www.toolsqa.com/gallery/Postman/9.Using%20Guid%20As%20A%20Variable.webp)

现在让我们再次发送请求，并将上次结果中得到的 GUID 与现在得到的 GUID 进行比较。

![比较 GUID](https://www.toolsqa.com/gallery/Postman/10.Comparing%20GUIDs.webp)

你可以看到，对于你发送的每个请求，该值都在变化。这是它可以用作在数据库中保存数据的键的主要原因。

这是关于预请求脚本。我们现在将看到如何在 Postman 的标头和 URL 中使用 GUID。

#### 如何将 GUID 保存在 Postman 的环境变量中？

现在我们已经在 Postman 中生成了GUID，我们可以直接在 headers 中使用它，或者将它保存在环境变量中。虽然你应该知道GUID是 Postman 中预定义的全局变量，因此不需要显式保存它。但是，请求可能会提出任何要求，你应该为此做好准备。

要保存我们生成的GUID，请回顾我们在环境和变量教程中的课程。你可以使用以下代码保存GUID：

从面板中选择一个环境(如果不存在则创建一个)。

![GUID 环境](https://www.toolsqa.com/gallery/Postman/11.GUID%20Environment.webp)

在预请求脚本中输入以下内容以及我们在上一节中编写的代码：

pm.environment.set("myGUID", myUUID);

![设置 GUID 变量](https://www.toolsqa.com/gallery/Postman/12.Setting%20GUID%20Variable.webp)

注意：这已经在环境和变量教程中讨论过了。在这里，我们正在设置一个名为 myGUID 的新环境变量，并将 myUUID 值保存到其中。

现在再次执行请求并通过单击眼睛图标查看环境变量。

![myGUID 变量](https://www.toolsqa.com/gallery/Postman/13.myGUID%20Variable.webp)

如你所见，你的变量现在已保存并可以用作普通变量。这就像 {{myGUID}}。

postman中的GUID也可以直接使用我上面说的。它是一个预定义的全局变量，其值不需要初始化。Postman 为我们提供了极大的便利，只需在必要和允许的情况下提及名称并使用GUID 。

GUID 是 Postman 和 API 世界中非常有用和重要的部分。当你每次都获得不同的值而不实际使用任何代码时，还需要什么？GUID 有助于将数据关联到数据库和其他存储并将其保存为密钥。它有助于将数据迁移到另一台服务器或合并两个数据库，因为这些GUID值在世界上的每个数据库中始终不同。如果你在SQL方面有良好的实践，那么你可以随时尝试进行试验。对于本教程，这一切都来自我这边。保持学习。保持练习。