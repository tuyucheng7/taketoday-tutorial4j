## Postman 中的集合

到现在为止，我们已经深入学习了[Postman 的术语，](https://toolsqa.com/postman/postman-navigation/)也做了一些[GET 请求](https://toolsqa.com/postman/get-request-in-postman/)。在前面的教程中，我们提到并简要解释了集合。回想一下，集合是分组到一个文件夹中的一组请求。这样我们就可以轻松地使用和共享它们。但在本教程中，我们将了解Collection远不止于此。

集合的定义仅限于一组请求。但是，一组请求可以具有无可比拟的优势，并使软件开发人员的工作轻松许多。我们现在将学习如何逐步保存Collection。

你还可以查看 Postman 教程的录音，我们的专家在其中深入解释了这些概念。

<iframe class="embed-responsive-item youtube-player" type="text/html" width="640" height="390" src="https://www.youtube.com/embed/-reBp2zIrBc?enablejsapi=1&amp;origin=https%3A%2F%2Fwww.toolsqa.com" frameborder="0" webkitallowfullscreen="" mozallowfullscreen="" allowfullscreen="" data-gtm-yt-inspected-31117772_24="true" id="281740213" title="Postman 教程 #28 - 在 Postman 中创建集合" style="box-sizing: border-box; position: absolute; inset: 0px; width: 730.344px; height: 410.812px;"></iframe>



### 如何在 Postman 中创建和保存收藏夹？

1.首先，点击端点http://restapi.demoqa.com/utilities/weatherfull/city/Hyderabad并在响应框中将格式更改为文本以查看结果。

![Request_ToolsQA_Weather](https://www.toolsqa.com/gallery/Postman/1.Request_ToolsQA_Weather.png)

1.  现在转到Collections选项卡，然后按显示New Collection的图标。

![New_Collection_icon](https://www.toolsqa.com/gallery/Postman/2.New_Collection_icon.png)

1.  为你的第一个系列写下你选择的名称及其描述。在下图中，你可以看到我选择了名称MyFirstCollection 和一个简单的描述。

![命名_集合_MyFirstCollection](https://www.toolsqa.com/gallery/Postman/3.Naming_Collection_MyFirstCollection.png)

1.  按创建创建你的第一个集合。
2.  现在，你已经创建了第一个集合，但它现在是空的。按地址栏侧面的保存按钮。

![保存请求](https://www.toolsqa.com/gallery/Postman/4.Save_Request.png)

1.  如图所示，在面板中选择你的集合名称。按保存。

![保存到我的收藏](https://www.toolsqa.com/gallery/Postman/5.Save_to_my_collection.png)

现在查看收藏选项卡，你的请求将保存在你的收藏名称下方。

![Request_Saved_To_Collection](https://www.toolsqa.com/gallery/Postman/6.Request_Saved_To_Collection.png)

你可以点击 收藏名称旁边的肉丸菜单，你会看到一些选项。我们将在本教程中讨论以下选项。

-   分享收藏
-   出口代收
-   将文件夹添加到集合
-   复制集合
-   从工作区中删除集合
-   删除集合
-   对收藏发表评论

### 分享收藏

共享收藏选项用于将收藏分享给其他人，例如你的团队成员。

![Share_Collection_Option](https://www.toolsqa.com/gallery/Postman/7.Share_Collection_Option.png)

当你在公司或团队中工作时，共享集合非常重要。有时开发人员在开发 API 来测试它时会创建一堆Postman 请求。你可以请求开发人员共享他们的收藏，你可以从中受益。或者甚至你创建了一堆Postman Requests，将其保存到Collections中并与你的团队共享。不值得一一分享每个请求。相反，我们将我们的收藏集作为一个整体分享给团队成员或我们希望通过下面列出的不同方法分享给他们的任何人。

比如你是一个测试人员，发现了一些bug，你可以把重现的步骤保存在一个Collection中，附在bug上，供开发者作为重现issue的证据。要使用此选项，你必须登录邮递员。登录 Postman 后，你将获得两个共享收藏的选项。

-   通过工作区
-   通过链接分享

#### 工作区

工作区是一组用户开发和测试 API 的协作环境。简单来说，工作区是你工作的项目。在工作区中完成的设置保留在同一工作区中。它就像一个外壳，你可以在其中工作。在 Postman 中必须有一个工作区才能工作，你不能孤立地工作。Postman 内部有两种类型的工作区

1.  团队工作区：要使用团队工作区选项，你的公司或你必须购买 Postman Pro 版本，否则此功能将无法使用。在团队工作区选项中，你的团队可以在一个工作区上同时测试 API，并且任何人都可以编辑和更新(有权限)。当团队位于不同地点或不同建筑物时，这会很方便。编辑后，每个人的 API 都会自动更新。可以有任意数量的团队工作区。
2.  个人工作区：个人工作区类似于团队工作区，但不同之处在于此工作区对于创建它的用户来说是完全个人化的。如果存在，Postman 将不会在任何机器上更新它。个人工作区中存在的集合通过链接共享，详情如下。

个人可以在个人工作空间中组织他们的工作，团队可以在团队工作空间中进行协作。当你启动 Postman 时，你已经在个人工作区内，如图所示。

![我的工作区标题](https://www.toolsqa.com/gallery/Postman/8.My_Workspace_Header.png)

你可以创建无限的个人工作区，这将是你机器的个人工作区，并且可以测试任意数量的 API。

#### 通过链接分享收藏

第二种选择是创建链接并将其发送给你团队的人员。此链接将属于Postman Cloud。因此，你的收藏将首先上传到Postman 云，然后任何人都可以通过该链接访问它。但是，有时在公司工作时不建议使用此功能，因为 API 是个人的，公司不希望它们位于不安全的其他云中。

### 在 Postman 中导出集合

导出功能用于通过将集合保存到你的计算机来将其作为一个整体导出，稍后可以通过电子邮件以 zip 文件的形式与团队共享。或者它也可以通过网络共享来共享。

![导出_集合](https://www.toolsqa.com/gallery/Postman/9.Export_Collection.png)

按照以下步骤了解如何导出集合。

1.如上图所示点击导出。

2.你将看到两个或三个选项(取决于你的 Postman 版本)。对于本教程，我们使用 Postman 版本 6.0.10。我们将选择Collection v2.1选项并按Export。

![Collection_v2.1](https://www.toolsqa.com/gallery/Postman/10.Collection_v2.1.png)

这将打开一个框，以将集合以 JSON 格式保存在你计算机上的任何位置。然后这个JSON文件可以像文件一样通过任何方式分享给你的队友。

### 新增文件夹

集合还允许你在集合中创建文件夹，然后将请求保存在文件夹中。这可以进一步帮助你对请求进行子分类。例如，在前面的章节中，我们以电影文件夹为例来解释收藏，你可以在其中存储所有电影。制作一个文件夹就像在电影文件夹中制作另一个文件夹，例如“英文电影”将包含所有英文电影，但它们仍然是电影。同样，在这里我们可以在集合中创建文件夹并存储我们的请求。

1.选择添加文件夹选项。

![添加文件夹集合](https://www.toolsqa.com/gallery/Postman/11.Add_Folder_Collection.png)

1.  命名你的文件夹并按创建。

![创建_Folder_In_Collection](https://www.toolsqa.com/gallery/Postman/12.Create_Folder_In_Collection.png)

现在你已经创建了文件夹，但到目前为止它是空的。

3.将请求拖放到文件夹名称上。

![拖放请求](https://www.toolsqa.com/gallery/Postman/13.Drag_Drop_Request.png)

这会将你的请求移动到你的文件夹中，你已准备就绪。

### 复制

 顾名思义，选项复制将集合复制到新集合中。这意味着当你单击复制时，你会在工作区中制作同一集合的另一个副本。

1.点击复制。

![重复 _Collection](https://www.toolsqa.com/gallery/Postman/14.Duplicate%20_Collection.png)

2.你可以在原始收藏下方的侧边栏中看到副本。

![Duplicate_Copy_Collection](https://www.toolsqa.com/gallery/Postman/15.Duplicate_Copy_Collection.png)

### 从工作区中删除

从工作区中删除选项从你正在处理的工作区中删除集合。

![Remove_From_Workspace](https://www.toolsqa.com/gallery/Postman/16.Remove_From_Workspace.png)

如果它是个人工作区，那么你可以通过选择该选项将其删除，而如果它是团队工作区，则你需要具有从工作区中删除任何集合或请求的权限。

### 删除

Delete 和 remove from workspace 选项可能看起来相似，但它们仅在一个方面有所不同。Remove from workspace 从当前工作区删除集合，但删除选项将从所有存在的工作区中删除集合或请求。不用说，你需要为此获得许可。

### 对 Postman 中的收藏发表评论

很明显，当我们在团队中工作时，我们的工作空间面临着大量的编辑。这可以是任何事情，从编辑一个简单的请求到编辑该请求中的一个测试。现在，由于你需要通知每个人你做了什么，Postman 为你提供了评论功能。通过这个功能你可以在收藏里留言，让大家可以按时间顺序知道你刚刚上传的内容。

为此，请按集合名称旁边的箭头按钮

![选项_集合](https://www.toolsqa.com/gallery/Postman/17.Options_Collection.png)

从选项中选择在 Web 中查看

![查看_In_Web](https://www.toolsqa.com/gallery/Postman/18.View_In_Web.png)

这将在你的浏览器中打开该集合。在同一工作区工作的每个人都可以看到这一点。选择此页面上的评论。

![评论_收藏](https://www.toolsqa.com/gallery/Postman/19.Comment_Collection.png)

这将向你显示用于输入评论的弹出窗口。

我们将在下一节中学习如何在 Postman 中导入集合。

### 如何在 Postman 中导入收藏

如果我们可以导出我们的集合，那么不用说它必须在其他 Postman 中导入。因此，与导出一样，我们还有另一个选项导入，但它不在肉丸菜单中。导入集合是标题的一部分，如下所示。

![导入头](https://www.toolsqa.com/gallery/Postman/20.Import_Header.png)

在 Postman 中导入集合很容易。当你单击导入时，你将看到前面的导入面板。

![导入面板](https://www.toolsqa.com/gallery/Postman/21.import%20panel.png)

在这里，正如你所看到的，可以使用不同的方法来导入集合。这些方法是

-   通过拖放方式导入
-   导入文件夹
-   通过链接导入
-   粘贴原始文本

#### 通过拖放导入

通过拖放导入非常简单。它的工作原理与许多网站(例如 Google Drive)中的上传功能相同。如果你的系统中有一个集合文件，只需单击该文件并将其拖动到此面板并释放鼠标(或放下文件)。这样文件/集合将自动上传到你的 Postman 中。

#### 导入文件夹

导入文件夹与上一个选项相同，不同之处在于，在上一个选项中，我们上传单个集合，但在这个选项中，我们可以一次上传多个集合。只需在你的系统中创建一个文件夹，然后在其中粘贴或导出许多集合。当你在 Postman 中导入相同的文件夹时，你将看到所有正在上传的收藏。如果你已经拥有该文件夹中的任何收藏，你将被要求替换它或形成它的副本。

#### 通过链接导入

正如我们在之前的教程中所讨论的，当我们在团队中工作时，我们经常使用 Postman 云或团队工作区，它们为我们提供了集合的链接，以便每个人都可以毫无问题地使用它。因此通过链接导入是相同的功能。我们可以在框中提供相同集合的链接，集合将被导入。

#### 粘贴原始文本

在上图中，面板的第一行告诉我们在 Postman 中可以导入哪些文件。除了 curl 或 RAML 等集合之外，还有许多文件。这些文件可以像 curl 文件一样通过原始文本编码导入。虽然此功能不在本课程的范围内，因此我们不会详细讨论它。

你可能想知道我们之前介绍的用于一次运行所有收集请求的收集运行器。我们将在结束“测试”话题后进行讨论。在下一个教程中它会更有意义。

### 练习练习

既然我们已经在 Postman 中走了这么远，如果你自己尝试一些场景会更好。

1.  创建一个名为PracticeExercises 的集合。
2.  在集合中创建一个文件夹并将其命名为Post Requests
3.  只有在获得正确响应后，文件夹内才会保存上一章中使用的请求([POST 请求)。](https://toolsqa.com/postman/post-request-in-postman/)
4.  将你的计算机中的集合保存为 JSON 文件。