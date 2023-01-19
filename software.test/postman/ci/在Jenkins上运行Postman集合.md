到目前为止，我们已经讨论了[什么是 Jenkins？](https://toolsqa.com/postman/postman-with-newman-jenkins/)以及[如何在 Jenkins 上运行批处理命令？](https://toolsqa.com/postman/configure-jenkins-job-to-run-batch-command/). 我希望你自己练习过一些批处理命令。在本教程中，我们将重点关注：

-   在 Jenkins 上运行 Postman 集合
-   从 Jenkins 控制台的日志中删除 unicode

## 在 Jenkins 上运行 Postman Collection

在 Jenkins 上运行 Postman Collection与在 shell/命令提示符下运行它相同。

先决条件

-   通过至少 1 次测试在 Postman 中正确运行的集合(请参阅[如何在 Postman 中创建和运行集合](https://toolsqa.com/postman/collections-in-postman/))
-   纽曼安装及纽曼知识(参考[纽曼教程](https://toolsqa.com/postman/what-is-newman-in-postman/))
-   用于运行集合的 Newman 命令的知识(请参阅[使用 Newman 运行集合](https://toolsqa.com/postman/running-collection-using-newman/))

### 如何使用 Newman 命令在 Jenkins 上运行 Postman Collection？

1.  要运行收藏集，请转到你的Postman 收藏集并获取共享链接。请参阅 [使用 Newman 运行集合](https://toolsqa.com/postman/running-collection-using-newman/) 以了解更多信息。
2.  转到Jenkins Job并在构建部分下，编写运行集合的命令： newman run "`<link>` "

![Newman_Collection_Run_Jenkins](https://www.toolsqa.com/gallery/Postman/1.Newman_Collection_Run_Jenkins.png)

1.  保存更改并单击立即构建。这将启动 Jenkins 作业，它将执行作业中定义的所有操作。由于我们已经指定了使用 Newman 命令运行 Postman Collection的操作，因此这将执行相同的操作。
2.  检查 Jenkins 中的控制台输出。

在我的系统中，我在运行集合时得到了这个输出。 这是一些文本的正确输出加上 unicode。如果仔细查看输出，你会注意到它也产生了一些不错的日志，这些日志告诉了执行结果。它显示 API 详细信息、状态、花费的时间和大小。

![在 Jenkins 上运行 Postman Collection](https://www.toolsqa.com/gallery/Postman/2.Run%20Postman%20Collection%20on%20Jenkins.png)

注意：你必须熟悉计算机中的Unicode系统。Unicode 系统是用于将要传播的文本转换为另一个系统的基本标准。有这么多语言，很难让每个系统都与那里的所有其他语言兼容。因此，为传播创建了一个基本的标准 Unicode 系统。但是在这里，我们看到了我们不理解的 Unicode，它看起来真的很乱。在下一节中，我们将清除它。如果你得到了结构化的结果，你可以跳过教程或学习知识，以防万一。

### 如何从 Jenkins 控制台输出日志中删除 Unicode？

从控制台输出日志中删除 Unicode 非常容易。我们需要过滤掉 Unicode 符号，因此我们在命令中定义了一个标志。有些人在 Jenkins 中也将标志称为“参数”。Newman 提供标志--disable-unicode以从日志中删除 Unicode 文本。

1.转到构建部分。

1.  在上一节中已经写入的命令之后写入标志。

![禁用_Unicode_jenkins](https://www.toolsqa.com/gallery/Postman/3.Disable_Unicode_jenkins.png)

1.  保存 更改并再次单击 立即构建 以运行新命令。
2.  现在检查控制台输出。

![Console_Without_Unicode](https://www.toolsqa.com/gallery/Postman/4.Console_Without_Unicode.png)

现在，unicode 文本被删除了。但是控制台中的输出并不像我们在 shell/命令提示符中运行集合时那样整洁。你们中的一些人一定已经以更好的表格方式得到了回复。但如果没有，你可以尝试在之前的命令中添加额外的参数--no-color：

![No_Color_Flag_Jenkins](https://www.toolsqa.com/gallery/Postman/5.No_Color_Flag_Jenkins.png)

保存 更改并再次单击 Build Now。现在，控制台提供了预期的视觉结构化输出。

![No_Color_Output_Jenkins](https://www.toolsqa.com/gallery/Postman/6.No_Color_Output_Jenkins.png)

注意：虽然 --no-color 用于禁用颜色输出，但这些东西的概要是 Jenkins 在不同系统上的行为不同。

### Newman 有哪些不同的可用参数？

Newman 提供了一组丰富的选项来自定义运行。`-h` 你可以通过使用标志运行它来检索选项列表 。没必要全部学一遍，但最好知道这些参数的能力。这样当需要出现时，你就知道该做什么以及该怎么做。

```java
Options:

Utility:
-h, --help                      output usage information
-v, --version                   output the version number

Basic setup:
--folder [folderName]           Specify a single folder to run from a collection.
-e, --environment [file|URL]    Specify a Postman environment as a JSON [file]
-d, --data [file]               Specify a data file to use either json or csv
-g, --globals [file]            Specify a Postman globals file as JSON [file]
-n, --iteration-count [number]  Define the number of iterations to run

Request options:
--delay-request [number]        Specify a delay (in ms) between requests [number]
--timeout-request [number]      Specify a request timeout (in ms) for a request

Misc.:
--bail                          Stops the runner when a test case fails
--silent                        Disable terminal output
--no-color                      Disable colored output
-k, --insecure                  Disable strict ssl
-x, --suppress-exit-code        Continue running tests even after a failure, but exit with code=0
--ignore-redirects              Disable automatic following of 3XX responses
```