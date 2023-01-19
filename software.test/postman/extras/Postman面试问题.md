我们现在已经非常详细地学习了 Postman。可以在[此处](https://toolsqa.com/postman/postman-tutorial/)查看我们已完成的主题的详细信息。由于我们已经完成了这么多并添加了你自己的练习时间，因此是时候在 Postman 上练习一些面试问题了。我们在这里讨论了最重要的Postman 面试问题，但没有给出太多细节，因为我们已经在课程中描述了所有内容。我希望这会证明对你的职业生涯是富有成果的。

## 邮递员面试问题

### 邮递员是什么？

Postman 是一款休息客户端软件，最初作为 chrome 扩展，但现在仅作为本机应用程序提供。Postman 基本上用于 API 测试，你可以在其中使用不同类型的请求方法类型(如 post、put 等)以及参数、标头和 cookie 来测试你的 API。除了设置查询参数和检查响应之外，postman 还让我们看到不同的响应统计信息，如时间、状态、标头、cookie 等，以及一些可以轻松使用的额外优秀功能。

### 什么是 API？

API代表 应用程序编程接口。用技术术语来说，API 是一组程序、函数和其他访问点，应用程序、操作系统、库等为程序员提供这些访问点，以便与其他软件进行交互。它可以被认为是服务员，充当你的请求和厨师之间的中间人。类似地，API 指的是客户端和服务器之间的中间人。(参考[教程](https://toolsqa.com/postman/api-testing-with-postman/))

### 用于 API 测试的工具有哪些？

API测试工具有很多。根据用户/下载量，以下六个是最高的。这些不是排名。

-   邮差
-   肥皂用户界面
-   卡塔隆工作室
-   Tricentis 托斯卡
-   给他打电话
-   Jmeter

### 邮递员接受哪种编码类型的授权凭证？为什么？

Postman 只接受 Base64 编码的授权。这是在 Postman 中内置提供的，否则你也可以参考第三方网站来转换 base64 中的凭据。我们特别使用 base64，因为它将数据传输为文本形式，并以更简单的形式发送，例如 HTML 表单数据。我们特别使用 Base64 是因为我们可以在我们使用的任何编码语言中依赖相同的 64 个字符。(参考[教程](https://toolsqa.com/postman/api-testing-with-postman/))

### 关于邮递员的环境一词是什么意思？

Postman 中的环境是一组键值对。你可以在邮递员中创建多个环境，只需按一下按钮即可快速切换。环境有两种类型，全局和局部。他们定义变量的范围以在请求中使用它。出于相同的目的，最常见的是在环境中定义变量。我们使用的最常见的变量是 url，因为 url 在每个请求中都会使用，更改它可能非常耗时。当我们在 Postman 中创建一个环境时，我们可以更改键值对的值，并且更改会反映在我们的请求中。环境只是为变量提供边界。(参考[教程](https://toolsqa.com/postman/environment-variables-in-postman/))

### 我们可以在 Postman 中有两个同名的全局范围变量吗？

由于全局变量是全局的，即没有任何环境，它们不能有重复的名称，因为它会给软件造成混淆。局部变量可以具有相同的名称但在不同的环境中。(参考[教程](https://toolsqa.com/postman/environment-variables-in-postman/))

### Postman 中哪个优先级高？全局变量还是局部变量？

在 Postman 中，如果两个变量同名(一个是局部的，一个是全局的)，那么局部变量的优先级更高。它将覆盖全局变量。(参考[教程](https://toolsqa.com/postman/environment-variables-in-postman/))

### 在 Postman 中定义团队工作区

工作区是一组用户开发和测试 API 的协作环境。团队工作区是由处理相同请求集合的整个团队共享的工作区。由于通过外部驱动器或其他共享方式共享集合非常耗时且困难，因此团队工作区将所有团队的工作同步和协作在一个地方。(参考[教程](https://toolsqa.com/postman/collections-in-postman/))

### 在Postman中解释如下一段测试代码

测试[“状态代码为 200”] = responseCode.code === 200

-   测试- 类型数组之一的变量
-   状态代码是 200 - 将在测试结果框中表示的字符串或测试名称，以便我们可以知道它是什么测试。这很重要，因为我们对一个请求使用许多测试。
-   responseCode.code = responseCode 用于保存我们从服务器获得的所有响应。由于我们不需要完整的响应，因此我们需要创建一个对象来提取我们所需的信息。然后调用代码对象以从我们保存的整个响应中输出状态代码(如 200)。(参考[教程](https://toolsqa.com/postman/test-and-collection-runner-in-postman/))

### Postman Monitors 和 Postman Collection Runner 有什么区别？

邮递员监视器是一种运行集合的自动化方式。集合根据指定参数自动触发，而 Postman 集合需要一些手动操作来启动和监视执行。邮递员收集运行器为你想要的迭代运行收集。当你停止软件时它会停止并且不是自动的。邮递员监视器将以用户定义的固定时间间隔运行你的收藏，直到你指定的时间。即使你的系统已关闭，你的收藏也会运行，因为它是通过邮递员云连接的。(参考[教程](https://toolsqa.com/postman/monitor-collections-in-postman/))

### 我们可以在 Postman Monitors 中导入局部变量吗？

是的。Postman 监视器允许导入局部变量，但不允许导入全局变量。我相信它可以在 json 中导入，如果是，请提及。(参考[教程](https://toolsqa.com/postman/monitor-collections-in-postman/))

### 描述你从响应中收到的任何四个响应事物(正确或不正确)

-   状态码
-   响应状态
-   响应时间
-   响应大小
-   响应头
-   回应饼干
-   响应日期和时间
-   响应会话限制
-   回应饼干
-   响应服务器
-   响应类型

(参考[教程](https://toolsqa.com/postman/response-in-postman/))

### Postman 中的集合是什么？

可以将 Postman 中的集合想象成类似于系统中的文件夹。集合是请求的分组，最好是相似类型的。可以将其与系统中具有一种文件类型的文件夹进行比较。它是 Postman 最重要的功能之一，它还提供了一些不错的功能，例如只需单击一下即可运行一整组请求。集合对于一次共享许多请求也很重要，并且包含更多可以通过给定链接引用的功能。(参考[教程](https://toolsqa.com/postman/collections-in-postman/))

### 如果我们在公司工作，我们是否应该将我们的工作保存在 Postman 云中？为什么？

Postman 云是 Postman 公司的存储库，就像 Microsoft 的 One Drive 等一样。在 Postman 云中，你可以在登录后立即保存你的工作，也可以从任何你想要的地方检索它。不建议将你的工作保存在 Postman 云中，因为公司的工作通常是机密的，不应泄露出去。Postman Cloud 需要登录，因此安全性可能会受到影响，因此首选团队工作区而不是将工作保存在 Postman Cloud 中。(参考[教程](https://toolsqa.com/postman/collections-in-postman/))

### 陈述任何 5 种类型的请求方法类型。

-   得到
-   邮政
-   放
-   删除
-   修补
-   头
-   删除

(参考[教程](https://toolsqa.com/postman/get-request-in-postman/))

### 请定义状态代码 401。另外，我们可能会遇到这种状态代码的情况。

状态代码 401 表示未授权请求。未经授权的请求是你未被授权的请求。当你无权访问服务器或输入了错误的凭据时，我们可能会产生此类状态代码。

其他常见的状态代码是

-   200 (OK) ：定义请求是正确的。
-   201 (Created) : 请求包裹的值已经在数据库中创建。不用说，请求是正确的。
-   204(无内容)：此状态代码表示请求正确并已收到，但服务器没有响应发送给客户端。
-   400 (Bad Request) ：错误请求意味着请求的语法不正确。如果你在请求 URL 或请求正文中发送了错误的参数，则可能会发生这种情况。
-   404(未找到)：响应代码 404 表示服务器已连接但找不到请求的内容。当你请求一个不可用的网页时，你通常可以看到此状态代码。

(参考[教程](https://toolsqa.com/postman/response-in-postman/))

### 我们可以在 Postman 中看到响应主体的不同类型。解释。

在Postman中，一个response body可以被三种不同的类型看到

-   漂亮的
-   生的
-   预习

虽然这三者在 Postman 中各有重要性和价值，但最常用的是 Pretty，因为它以不同的格式和颜色显示响应代码，便于阅读和分析响应。它就像任何用于编码的优秀文本编辑器一样。(参考[教程](https://toolsqa.com/postman/response-in-postman/))

### Postman 的“批量编辑”功能是做什么用的？

Postman 的 Bulk Edit 功能用于方便地从先前的请求中向新请求添加参数。由于一个请求可以有很多参数，并且很难一个一个地复制和粘贴，批量编辑功能帮助我们一次复制所有键和它们各自的值并粘贴它们。(参考[教程](https://toolsqa.com/postman/request-parameters-in-postman/))

### Postman 的 Post 方法中的“x-www-urlencoded”是什么？

表单数据 和 x-www-form-urlencoded 非常相似。它们都用于几乎相同的目的。但是表单数据和x-www-form-urlencoded的区别在于，通过x-www-form-urlencoded发送时url会被编码。编码意味着发送的数据将被编码为不同的字符，即使受到攻击也无法识别。(参考[教程](https://toolsqa.com/postman/post-request-in-postman/))

### Postman 的 Post 方法中的二进制是什么？

Postman 中的二进制形式旨在以无法手动输入的格式发送信息。由于计算机中的所有内容都转换为二进制，因此我们使用无法手动编写的这些选项，例如图像、文件等。(参考[教程](https://toolsqa.com/postman/post-request-in-postman/))

### Postman 中的预请求脚本是什么？

预请求脚本是在执行请求之前运行的脚本。(参考[教程](https://toolsqa.com/postman/pre-request-script-in-postman/))

### 授权和认证有什么区别？

身份验证是向系统出示你的凭据并且系统验证你的凭据的过程。这些凭据告诉系统你是谁。授权是在身份验证完成后允许或拒绝某人访问某物的过程。(参考[教程](https://toolsqa.com/postman/basic-authentication-in-postman/))

### Postman 中环境变量的不同范围是什么？

变量的范围被定义为可以访问它的边界。他们是

-   Local Scope：只能在创建它的环境中访问
-   Global Scope : 可以在任何环境或无环境下全局访问。

(参考[教程](https://toolsqa.com/postman/environment-variables-in-postman/))

### 当集合已经是一组请求时，为什么我们将请求分组在集合下？

一个集合下可能有数百个请求。我们需要根据更具体的类别对请求进行子分类，以便我们更容易找到它们、编辑它们或修改它们。为此，我们使用集合中的文件夹。一个集合中可能有很多文件夹，一个文件夹可能有很多请求。通过这种方式，我们可以将请求类型泛化到比已经泛化的集合更深的层次。为了简单起见，可以将集合视为系统中包含所有电影的文件夹“电影”。文件夹可以被认为是“电影”中的不同文件夹，例如好莱坞，宝莱坞等，它们具有各自类型的电影。

(参考[教程](https://toolsqa.com/postman/collections-in-postman/))

### 在 Postman 中可以通过哪两种方式编写测试？

在邮递员中，我们可以用 Javascript 方法或函数方法编写测试。虽然函数式方法也使用 javascript 但语法不同。函数式方法是Postman官方推荐使用的方法。还可以注意到，Postman 中的所有代码片段都只是函数式方法。它还有一个内置的库，即 Chai。Chai 还以一种非常漂亮的方式使用函数式方法，使其更具可读性和更短。(参考[教程](https://toolsqa.com/postman/test-and-collection-runner-in-postman/))

### 你更喜欢哪种方法？Javascript 还是 Functional 来编写测试？

建议并推荐在 Postman 中编写测试时使用功能方法。虽然一直没有通知结束对 JS 方法的支持。(参考[教程](https://toolsqa.com/postman/test-and-collection-runner-in-postman/))

### 编写测试代码，检查响应状态是否为200。

检查响应状态是否为200的测试代码如下

tests[“状态代码为 200”] = responseCode.code === 200;

### 在 Postman 中监控集合有什么需要？

确保你的 API 的响应和性能在一天中保持符合标准非常重要。监视器可以帮助你安排一组测试运行以监视 API 的性能和响应，即使你不在或不处理它们。(参考[教程](https://toolsqa.com/postman/test-and-collection-runner-in-postman/))

### 我们可以在不登录的情况下在 Postman 中运行监视器吗？

不，没有登录就无法运行监视器，因为即使你的系统关闭，监视器也会运行你的收藏。因此，你需要一个地方来存储集合并让它自动运行。你还需要一个地方来存储报告，以便你有空时可以查看它们。这一切都需要保存到你的邮递员帐户中，因此你需要登录。(参考[教程](https://toolsqa.com/postman/monitor-collections-in-postman/))

### Postman 中 setNextRequest 的重要性是什么？

Postman 中的 setNextRequest 用于定义工作流。需要 setNextRequest 来更改正在执行的请求的顺序。(参考[教程](https://toolsqa.com/postman/workflows-in-postman/))

### Postman 中的两种脚本是什么？

我们可以在 Postman 中编写两种类型的脚本

-   测试脚本
-   预请求脚本

(参考[教程](https://toolsqa.com/postman/pre-request-script-in-postman/))

### 什么是 Chai 断言库？

Chai assertion library 是一个预先安装好的断言库，可以在 Postman 中使用。这用于在 Postman 中编写断言非常有用。Chai 断言帮助我们在几行代码中编写多行测试代码，这既易于理解又易于阅读。Chai 使用 BDD 方法，这意味着 Chai 库的代码更加用户友好。

用 chai 库编写的一个简单代码，用于测试数字 3 是否已经在数组中。

pm.test(“包含的数字”, function(){ pm.expect([1,2,3]).to.include(3); });

(参考[教程](https://toolsqa.com/postman/assertions-in-postman-with-chai-assertion-library/))

### Postman 通常使用什么命令行界面来服务于持续集成。

Newman 通常与 Postman 一起用作命令行界面来服务于持续集成。(参考[教程](https://toolsqa.com/postman/what-is-newman-in-postman/))

### 在 Newman 中编写运行文件夹的命令。

在 Newman 中，不需要运行完整的集合来检查一堆请求。这显然很耗时，不推荐。我们也可以只运行位于 Newman 集合中的一个文件夹。要在 Newman 中运行文件夹，使用以下命令

纽曼运行`<collection_name>`-文件夹`<folder name>`

(参考[教程](https://toolsqa.com/postman/newman-optional-parameters-configurations/))

### 什么是詹金斯？

Jenkins 用于持续构建和测试你的项目，从而使软件开发人员和测试人员的工作变得容易。Jenkins 使用持续集成和持续开发来开发和部署软件。(参考[教程](https://toolsqa.com/postman/postman-with-newman-jenkins/))

### 詹金斯是用什么语言写的？

Jenkins 是一个用 Java 编写的开源自动化服务器。(参考[教程](https://toolsqa.com/postman/postman-with-newman-jenkins/))

### 你对持续交付的理解是什么？

持续交付是持续集成的下一步。持续交付是一种 DevOps 软件开发实践，其中自动构建、测试(单元测试)代码更改，并为发布到环境做好准备。(参考[教程](https://toolsqa.com/postman/postman-with-newman-jenkins/))

### 持续交付和持续部署之间的主要区别是什么？

持续交付 和 持续部署之间的主要区别在于 是否需要手动批准更新到生产环境。通过持续部署，生产部署会自动发生，无需明确批准。(参考[教程](https://toolsqa.com/postman/postman-with-newman-jenkins/))

### 陈述 Jenkins 的任何 2 个优点。

-   它有巨大的插件支持。
-   它是用 Java 构建的，因此与平台无关。
-   它是一个开源工具，具有强大的社区支持
-   它很容易安装。
-   它是免费的。

(参考[教程](https://toolsqa.com/postman/postman-with-newman-jenkins/))

### 为什么 freestyle job 是 Jenkins 开发者最喜欢的工作？

自由式项目是你可以在其中运行任何类型的构建的项目。这使开发人员能够为此类项目开发大量插件，因为它具有灵活性。此外，它让我们可以开发任何类型的构建，因此它成为一种原始选择。(参考[教程](https://toolsqa.com/postman/configure-jenkins-job-to-run-batch-command/))

### 为什么插件没有预装在 Jenkins 中？

插件用于扩展软件的功能。由于这些插件的大小可能相当大，因此会增加软件的整体大小。此外，很明显没有一个开发人员会使用其软件中的所有功能。因此，插件不会预先安装，开发人员会自行安装他们需要的任何插件。

### Post-Build 和 Build 相对于 Jenkins 有什么区别？

Build in Jenkins 接受需要执行的批处理命令。例如，newman run`<link>`是通过 Newman 运行集合所需的批处理命令，因此写在构建部分。

Post-Build 部分用于告诉 jenkins 在执行批处理命令后是否需要执行任何操作。这些可以包括在执行命令和运行测试后发布报告时发布报告等。(参考[教程](https://toolsqa.com/postman/configure-jenkins-job-to-run-batch-command/))

### 什么标志用于从 Jenkins 控制台输出中删除 unicode？

Jenkins 中使用了一个标志来在输出控制台中进行一些更改。标志还提供选项，以便可以过滤掉完整的响应。为了删除 unicode，我们使用--disable-unicode标志。(参考[教程](https://toolsqa.com/postman/configure-jenkins-job-to-run-batch-command/))

### 为什么我们需要 Jenkins 中的报告？

Jenkins 中的报告是一种结构化和图形化的方式，我们可以在其中查看执行结果或测试输出。当我们必须与我们的团队成员或其他利益相关者交流结果时，报告也更容易发挥作用。通过报告查看 API 的完整轨迹总是比比较控制台输出更方便。(参考[教程](https://toolsqa.com/postman/generate-newman-reports-on-jenkins/))

### 在 Jenkins 中生成 junit xml 报告的命令是什么？

要生成 junit xml 报告，我们使用--reporters junit命令。(参考[教程](https://toolsqa.com/postman/generate-newman-reports-on-jenkins/))

### Jenkins 中的 --reporters 和 --reporter 标志有什么区别？

--reporters 用于告诉 jenkins 我们需要发布报告。然后我们需要指定我们想要的报告类型。

--reporter 用作对报告进行其他操作的选项。这是由我们在此之后使用的标志定义的。例如 --reporter-export 会将报告导出到给定位置。(参考[教程](https://toolsqa.com/postman/generate-newman-reports-on-jenkins/))