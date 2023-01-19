在当前的 IT 世界中，大量人员在组织中工作，并使用组织授权的工具。这些人通常被称为那些工具的用户。就Jenkins而言，大量用户每天使用此工具来运行作业，但出于安全和授权目的，Jenkins管理员无法为所有用户提供所有角色。所以强烈建议根据他们的角色来管理这些用户。在这里，用户管理在Jenkins中发挥作用，Jenkins提供添加用户、编辑用户、为每个用户提供不同角色的功能。 Jenkins使用各种插件为基于角色的策略提供这些功能。让我们通过涵盖以下主题下的详细信息来了解所有这些策略以及此用户管理所需的相应插件：

-   Jenkins 中的角色策略是什么？
    -   如何在 Jenkins 中安装 Role-based strategy 插件？
    -   以及，如何在 Jenkins 上启用基于角色的策略？
-   如何在 Jenkins 中创建用户？
    -   如何在 Jenkins 中管理用户角色？
    -   以及，如何在 Jenkins 中为用户分配角色？
-   Jenkins 中基于项目的矩阵授权策略是什么？
    -   如何在 Jenkins 中分配特定于工作的权限？

## Jenkins 中的角色策略是什么？

基于角色的策略有助于定义可以将哪些不同的访问类型分配给各个用户。正如我们所讨论的，并非所有用户都可以获得管理权限，因此[Jenkins](https://www.toolsqa.com/jenkins/what-is-jenkins/)提供了为各个用户提供不同角色的能力。此功能是使用[“基于角色的策略插件”实现的。](https://plugins.jenkins.io/role-strategy/)

Role-based strategy plugin是Jenkins中一种特殊的插件，它基于role-based机制，让我们可以管理用户的权限。此插件提供的一些主要功能是：

-   全局角色，如管理员、匿名、作业创建者，允许设置作业、视图、代理、运行和 SCM 等权限。
-   允许我们访问具有作业和运行权限的特定项目的项目角色。
-   用于设置节点相关权限的代理角色。
-   借助此插件，可以将角色分配给用户和用户组。

那么，让我们了解如何在Jenkins中安装这个插件并开始使用它的功能来管理用户角色？

### 如何在 Jenkins 中安装 Role-based strategy 插件？

Role-based Strategy 插件允许我们分配角色并授予用户特权。要在Jenkins中安装此插件，请按照以下步骤操作：

第 1步：转到“管理 Jenkins ”页面，然后单击“系统配置”部分 下的“管理插件”，如下突出显示：

![Jenkins 用户管理 - 管理插件部分](https://www.toolsqa.com/gallery/Jenkins/1.Jenkins%20User%20Management-%20Manage%20Plugin%20section.jpg)

一旦我们点击“管理插件”部分，我们将重定向到“插件管理器”页面。

第 2 步：要安装插件，请按照以下突出显示的步骤操作：

![Jenkins 用户管理——插件的安装](https://www.toolsqa.com/gallery/Jenkins/2.Jenkins%20User%20Management%20-%20Installation%20of%20plugin.jpg)

-   单击可用选项卡。
-   在搜索框中搜索文本“角色”。
-   一旦出现“基于角色的授权策略” 插件，请单击它的复选框。
-   单击“无需重新启动即可安装” 按钮。我们也可以选择/选择“立即下载并在重启后安装” 按钮。

第 3 步：完成以下步骤并成功安装插件后，我们将看到以下屏幕。

![Jenkins 用户管理 - 成功安装插件](https://www.toolsqa.com/gallery/Jenkins/3.Jenkins%20User%20Management%20-%20successful%20installation%20of%20plugin.jpg)

那么，这样，我们就可以安装Role-based strategy插件了。现在，让我们看看如何使用这个插件的功能？

### 如何在 Jenkins 上启用基于角色的策略？

在 Jenkins 上安装Role-based Strategy 插件后，下一步就是在 Jenkins 上启用它。为此，请按照以下步骤操作：

第 1 步：转到“管理 Jenkins ”页面，然后单击“安全”部分下的“配置全局安全” 选项，如下突出显示：

![Jenkins 用户管理 - 配置全局安全部分](https://www.toolsqa.com/gallery/Jenkins/4.Jenkins%20User%20Management%20-%20Configure%20Global%20Security%20section.jpg)

第 2 步：要在 Jenkins 上启用基于角色的策略，请按照以下步骤操作，如下所示：

![启用基于角色的策略](https://www.toolsqa.com/gallery/Jenkins/5.Enabling%20Role%20based%20Strategy.jpg)

-    如果默认情况下未选中，请选择“Jenkins 的用户数据库”选项。
-   选择“基于角色的策略” 选项。
-   单击保存按钮。

因此，通过这种方式，我们可以在 Jenkins 上启用基于角色的策略。现在Jenkins已准备好为不同的用户提供基于角色的访问。让我们创建一个新用户并了解我们如何为该用户提供不同的权限？

## 如何在 Jenkins 中创建用户？

当我们完成Jenkins的安装过程后，Jenkins默认创建了一个具有所有访问权限的管理员用户。要在Jenkins中创建新用户，请按照以下步骤操作：

第 1 步：转到Jenkins仪表板并单击“管理 Jenkins”链接，如下突出显示：

![点击管理Jenkins](https://www.toolsqa.com/gallery/Jenkins/6.Click%20on%20Manage%20Jenkins.jpg)

第 2 步：一旦我们单击“管理 Jenkins”，我们将被重定向到“管理 Jenkins ”页面。

![管理Jenkins页面](https://www.toolsqa.com/gallery/Jenkins/7.Manage%20Jenkins%20page.jpg)

现在，单击“管理 Jenkins ”页面 上“安全”部分下的“管理用户” 。

第 3 步：只要我们在“管理 Jenkins ”页面 的“安全”部分下单击“管理用户”，我们就会被重定向到“用户”页面。

![创建新用户](https://www.toolsqa.com/gallery/Jenkins/8.Create%20new%20user.jpg)

我们可以通过单击 上面突出显示的“创建用户”链接来创建新用户。

第 4 步：一旦我们单击“创建用户”链接，我们将被重定向到“创建用户”页面，我们需要在该页面上填写用户名、密码、确认密码、全名和电子邮件地址信息。

![填写新用户信息](https://www.toolsqa.com/gallery/Jenkins/9.Fill%20information%20of%20new%20user.jpg)

填写所有信息后，单击“创建用户”按钮。

第 5 步：只要我们点击“创建用户”按钮，就会创建一个新用户并显示在“管理用户”页面上。

![在 Jenkins 中创建的用户](https://www.toolsqa.com/gallery/Jenkins/10.User%20created%20in%20Jenkins.jpg)

因此，通过这种方式，我们可以在Jenkins中创建尽可能多的用户。现在，让我们看看如何为这些用户分配角色？

### 如何在 Jenkins 中管理用户角色？

好的，所以在本节中，我们将看到如何创建角色并为用户分配权限。为此，请按照以下步骤操作：

第一步：首先，进入Manage Jenkins页面，点击“Manage and Assign Roles”。

注意：此选项仅在安装“基于角色的策略插件”后可见。

![管理和分配角色](https://www.toolsqa.com/gallery/Jenkins/11.Manage%20and%20Assign%20Roles.jpg)

一旦我们单击“管理和分配角色”，我们将被重定向到“管理和分配角色”页面。

第 2 步：其次，单击“管理角色” 选项，如下突出显示：

![管理角色选项](https://www.toolsqa.com/gallery/Jenkins/12.Manage%20Roles%20option.jpg)

单击“管理角色”选项后，我们将转到“管理角色”页面。

第 3 步：要创建新角色，请按照以下突出显示的步骤操作：

![创建角色](https://www.toolsqa.com/gallery/Jenkins/13.Creating%20a%20Role.jpg)

-   在“要添加的角色”文本框中键入我们要添加的角色 。在这里，我们将创建一个名为“developer”的角色。
-   单击添加按钮。
-   只要我们单击“添加”按钮，所需的角色就会添加到“全局角色”网格中。

所以通过这种方式，我们可以创建任意多个角色。现在是时候允许新添加的角色了。

第 4 步：检查我们要赋予新添加的角色的权限，然后单击“保存”按钮。

![为角色分配权限](https://www.toolsqa.com/gallery/Jenkins/14.Assigning%20permissions%20to%20roles.jpg)

所以，现在我们在系统中有了一个新角色，它具有与之关联的某些权限。现在，让我们看看如何将这些角色分配给特定用户，以便该用户也获得与上述角色相同的权限。

### 如何为 Jenkins 中的用户分配角色？

在上一节中，我们了解了如何创建角色并向这些角色授予权限。在本节中，我们将了解如何将这些角色分配给用户。出于演示目的，我们在Jenkins 上创建了一个用户。请按照以下步骤将角色分配给特定用户：

第 1 步：转到“管理和分配角色”页面，然后单击“分配角色” 链接，如下突出显示：

![分配角色](https://www.toolsqa.com/gallery/Jenkins/15.Assign%20Roles.jpg)

第 2 步：单击“分配角色”后，我们将转到“分配角色”页面。在这里，在全局角色部分下，我们可以看到我们在上一节中添加的新角色(“开发人员”) 。现在我们可以将此角色分配给相应的用户。请执行以下步骤来实现它：

![为用户分配角色](https://www.toolsqa.com/gallery/Jenkins/16.Assigning%20roles%20to%20user.jpg)

-   在“要添加的用户/组”文本框中键入我们要添加的用户 。在这里，我们将添加一个名为“toolsqadev”的用户。
-   单击添加按钮。
-   只要我们单击“添加”按钮，所需的用户就会添加到“全局角色”网格中。现在，选中针对此用户的开发人员复选框。
-   单击保存按钮。

第 3 步：由于我们在上一步中为用户分配了一个角色，所以现在是时候验证我们为任何特定用户分配给该角色的权限了。为此，请使用该特定用户登录。在上一节中，我们没有将“Create Job” 角色分配给用户“toolsqadev”  ，因此当我们使用该用户登录时， “Create a new Item” 链接不应出现在 Jenkins 仪表板中，我们可以在下面看到图片。

![特定用户的权限验证](https://www.toolsqa.com/gallery/Jenkins/17.Permissions%20verification%20for%20specific%20user.jpg)

因此，概括地说，无论我们将分配给角色什么权限，都只有用户将执行的那些操作。这就是本节的全部内容。在下一节中，我们将讨论 Jenkins 中基于项目的矩阵授权策略。

## Jenkins 中基于项目的矩阵授权策略是什么？

基于项目的矩阵策略是一种授权策略，定义了用户或组对特定项目的权限。它还定义了任何用户或组可以对作业执行的操作的权限。此选项位于授权下，当我们选择此选项时，将打开一个网格，我们可以在其中添加用户并通过选中复选框为他们分配权限。

这些权限组属于Overall、credentials、agent、job、run、view 和 SCM 类别。在下一小节中，让我们看看如何在Jenkins 中执行基于项目的矩阵策略。

### 如何在 Jenkins 中分配特定于工作的权限？

在上一节中，我们看到了基于项目的矩阵授权策略的介绍方面。现在，在本小节中，让我们看看如何在 Jenkins 中实施此策略。请按照以下步骤实施它：

第 1 步： 在Jenkins仪表板单击“管理 Jenkins” 。

![点击管理Jenkins](https://www.toolsqa.com/gallery/Jenkins/18.Click%20on%20Manage%20Jenkins.jpg)

一旦我们点击它，我们将重定向到管理Jenkins页面。

第 2 步：单击“安全”部分 下的“配置全局安全”  。

![点击配置全局安全](https://www.toolsqa.com/gallery/Jenkins/19.Click%20on%20configure%20global%20security.jpg)

我们将重定向到配置全局安全页面。

第三步： 在授权部分选择“基于项目的矩阵授权策略”选项。

![选择基于项目的矩阵选项](https://www.toolsqa.com/gallery/Jenkins/20.selection%20of%20project%20based%20matrix%20option.jpg)

选择所需的选项后，我们可以看到基于网格的结构，我们需要在其中添加已经创建的用户。

第 4步：按照以下步骤添加要添加的用户：

![将用户添加到网格](https://www.toolsqa.com/gallery/Jenkins/21.Add%20user%20to%20grid.jpg)

-   单击“添加用户或组” 按钮。
-   放置要添加的用户“jagratgupta17” 。
-   单击确定按钮。

一旦我们单击“确定”按钮，用户将添加到下面的网格状图像中。

![用户添加到网格内](https://www.toolsqa.com/gallery/Jenkins/22.User%20added%20inside%20grid.jpg)

第 5 步：现在将所有权限分配给该用户，就像我们以管理员身份创建的一样，然后单击“保存”按钮。

![Jenkins 用户管理 - 为管理员用户分配权限](https://www.toolsqa.com/gallery/Jenkins/23.Jenkins%20User%20Management%20-%20assign%20privileges%20to%20admin%20user.jpg)

第 6 步：现在，我们已经创建了另一个用户“toolsqadev”  。当我们使用该用户登录时，我们将在 Jenkins 仪表板中看到“访问被拒绝”消息，因为我们没有在网格中添加该用户，也没有为该用户分配权限。

![Jenkins 用户管理 - 访问被拒绝消息](https://www.toolsqa.com/gallery/Jenkins/24.Jenkins%20User%20Management%20-%20access%20denied%20message.jpg)

现在，在下一步中，我们将从 admin 用户再次登录，我们将把这个用户添加到那个网格中，并为这个用户分配 READ 权限。

第 7 步：现在，像我们在前面的步骤中所做的那样，将此用户添加到网格中，并为该用户分配读取权限。

![Jenkins 用户管理 - 将 READ 权限分配给其他用户](https://www.toolsqa.com/gallery/Jenkins/25.Jenkins%20User%20Management%20-%20Assign%20READ%20privilege%20to%20other%20user.jpg)

所以通过这种方式，我们将 READ 权限分配给用户“toolsqadev”。

第 8 步：现在，当我们再次使用该用户登录时，我们可以在Jenkins仪表板中看到所有作业。

![Jenkins 用户管理 - 其他用户的 Jenkins 仪表板](https://www.toolsqa.com/gallery/Jenkins/26.Jenkins%20User%20Management%20-%20Jenkins%20dashboard%20for%20other%20user.jpg)

这里要注意的重点是，我们没有在左侧看到该用户的“Build Now”选项，因为我们只为该用户分配了READ only 权限。

## 要点：

-   Role-based strategy plugin是Jenkins中一种特殊的插件，它基于role-based机制，允许我们管理用户的权限。
-   如果我们想要创建角色并为用户分配角色，那么我们需要安装“基于角色的授权策略”插件。
-   单击管理和分配角色下的管理角色后，我们可以创建角色并为这些角色分配权限。
-   创建角色后，我们可以在管理和分配角色下点击分配角色后将这些角色分配给用户。
-   基于项目的矩阵策略是一种授权策略，它定义了用户或组对特定项目的权限。它还定义了任何用户或组可以对作业执行的操作的权限。