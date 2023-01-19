### 问题陈述

我们经常会在公司或团队中遇到不同的服务器。这些可以是开发服务器、生产服务器或测试服务器。每个服务器都有不同类型的请求 API。既然我们知道一个集合中可以包含很多请求，那么如果 URL 发生变化怎么办？例如，他们更改了服务器请求 URL。如果管理服务器的团队更改请求 API 并通知我们，我们将面临很多麻烦。为了成功运行请求，现在我们必须对每个请求进行更改。对于 300 个请求，我们将不得不更改 300 次。这纯粹是浪费时间和资源。但由于这种情况经常发生，Postman 有一个功能可以在几秒钟内处理这个问题，我们将很高兴再次使用请求。

### Postman 中的环境是什么？

Postman 中的环境是一组键值对。环境帮助我们区分请求。当我们在 Postman 中创建一个环境时，我们可以更改键值对的值，并且更改会反映在我们的请求中。环境只是为变量提供边界。当我们创建不同的环境时，我们可以跟踪所有变量以及如何在我们的请求中使用它们。一个环境中可以有很多变量。虽然我们可以在 Postman 中创建任意数量的环境，但我们一次只能在一个环境中工作。下面的屏幕截图显示了我们创建的三个环境。

![Postman 中的环境变量](https://www.toolsqa.com/gallery/Postman/1.Environment%20Variables%20in%20Postman.png)

### Postman 中的环境变量是什么？

Postman 中的变量与任何编程语言中的变量相同。变量是其值可以更改的实体。环境中键值对集合中的关键部分称为变量。这个变量可以有任何值，我们可以在每个请求中使用变量名来代替键。通过下面显示的示例和随后显示的步骤，这将很清楚。

![变量_例子](https://www.toolsqa.com/gallery/Postman/2.Variable_Example.png)

上图显示了环境测试环境 1中的三个变量

## Postman 中的环境变量

现在，我们将使用 Postman 创建一个环境和环境变量，这很容易做到，但在这个过程中涉及三个步骤：

1.  创建环境
2.  创建环境变量
3.  在请求中使用环境变量

### 第 1 步：如何在 Postman 中创建环境

1.[创建一个新的 Collection](https://toolsqa.com/postman/collections-in-postman/) 并将其命名为 EnvironmentChapter。

1.  [在Get Request](https://toolsqa.com/postman/get-request-in-postman/) 章节中使用的集合中添加Weather Api Request 。

![环境_章节_合集](https://www.toolsqa.com/gallery/Postman/3.Environment_Chapter_Collection.png)

1.  单击显示管理环境的齿轮图标。

![齿轮_图标_管理_环境](https://www.toolsqa.com/gallery/Postman/4.Gear_Icon_Manage_Environment.png)

1.  单击添加。

![添加_环境](https://www.toolsqa.com/gallery/Postman/5.Add_Environment.png)

1.  将环境命名为Weather API

![Weather_API_Envrionment](https://www.toolsqa.com/gallery/Postman/6.Weather_API_Envrionment.png)

### 第二步：如何在Postman中创建环境变量

1.现在在同一窗口中，输入以下键值对。其中Key 是变量的名称，Value 是文本字符串。

关键：网址

价值：http://restapi.demoqa.com

![Weather_API_Envrionment](https://www.toolsqa.com/gallery/Postman/7.Weather_API_Envrionment.png)

单击添加并关闭面板。

### 第 3 步：如何在 Postman 中使用环境变量

1.  选择显示 No Environment 的下拉菜单，然后在其中选择Weather API 环境。

![Dropdown_For_Environment](https://www.toolsqa.com/gallery/Postman/8.Dropdown_For_Environment.png)

现在我们可以访问这个环境的所有变量。

1.  在地址栏中将 http://restapi.demoqa.com更改为{{url}}

![Change_to_URL_Variable](https://www.toolsqa.com/gallery/Postman/9.Change_to_URL_Variable.png)

1.  点击发送。

现在，我们已经创建了一个环境并在这里使用了一个名为URL的变量。现在可以使用此变量代替实际的 URL。你可以看到与我们使用完整 URL 之前相同的响应。

因此，如果 URL 发生任何变化，我们只需转到环境并更改 URL 值，它就会反映在每个请求中。

注意：请记住通过单击“保存”按钮来保存请求。在以后的章节中，我们将只使用这个修改后的请求。

## Postman 中的变量范围

任何事物的范围都是可以访问和执行该事物的边界。例如，如果你是一名工程师并且没有护照，那么你的范围仅限于印度，因为你不能出门。拥有护照会改变你的世界视野。同样，Postman 中的变量有两个作用域

-   本地范围
-   全球范围

### 本地范围

局部范围变量只能在创建它的环境中工作。更改环境将停止对该变量的访问，我们将遇到错误。

我们刚刚在上面创建的变量 URL 是局部变量，因为它的范围仅限于环境 Weather API。在接下来的步骤中，我们将通过在不存在局部变量的其他环境中访问局部变量来探索局部变量的局限性。

1.转到我们选择Weather API的下拉列表，然后选择任何其他值(如果有)或No Environment。

![Dropdown_For_No_Environment](https://www.toolsqa.com/gallery/Postman/10.Dropdown_For_No_Environment.png)

1.  单击发送。

![错误_环境_错误](https://www.toolsqa.com/gallery/Postman/11.Wrong_Environment_Error.png)

发生此错误是因为 Postman 不知道URL变量，因为我们已经更改了环境。因此，URL 是一个局部变量，其作用域仅限于Weather API环境。

### 全球范围

全局范围变量也可以在环境之外工作。它们是全局的，选择哪个环境并不重要。在下图中，你可以通过单击眼睛图标看到三个全局变量。

![全局变量示例](https://www.toolsqa.com/gallery/Postman/12.Global_Variable_Example.png)

### Postman 中的全局变量

现在，我们将使用 Postman 创建全局变量。

创建环境：仅仅因为全局变量不与任何特定环境相关联，所以不需要为全局变量创建环境。

1.  创建全局变量
2.  在请求中使用全局变量

### 第 1 步：如何在 Postman 中创建全局变量

1.转到相同的 齿轮图标以打开我们在创建局部变量时所做的环境面板。

1.  选择Globals添加全局变量。

![Adding_Global_Variable](https://www.toolsqa.com/gallery/Postman/13.Adding_Global_Variable.png)

1.  添加以下键值对

关键：网址

价值：http://restapi.demoqa.com

![全局变量](https://www.toolsqa.com/gallery/Postman/14.Global_Variable.png)

1.  保存并关闭面板。

### 第二步：如何在Postman Request中使用全局变量

1.我们上面创建的请求，只需 从环境下拉列表中选择无环境。

![No_Environment_Selected](https://www.toolsqa.com/gallery/Postman/15.No_Environment_Selected.png)

1.  按发送，现在看到结果。

![全局变量响应](https://www.toolsqa.com/gallery/Postman/16.Global_Variable_Response.png)

它现在可以工作了，因为我们已经创建了一个可以在每个环境中使用的全局变量。

注意：全局作用域不能有重复/相同的名称，而具有局部作用域的变量在不同环境中可以有相同的名称。

为方便起见，Postman 还有一项功能可以让你查看所有当前变量和环境。只需单击眼睛图标，它就会列出所有环境和全局变量。

![眼睛图标](https://www.toolsqa.com/gallery/Postman/17.Eye_Icon.png)

可以看到Globals下写的全局变量。我们没有选择任何环境，因此没有关于环境的信息。你可以自己尝试一下。

## 变量的优先级

正如我们所讨论的，两个全局变量不能同名，而两个局部变量可以同名，前提是它们处于不同的环境中。但是，如果一个局部变量和一个全局变量同名怎么办？例如，你将局部变量命名为 ABC，将全局变量命名为 ABC。现在，当你选择相应的环境时，两个变量都将被激活。那么，哪些会体现它的价值呢？这种混淆通过优先解决。

Precedence 通常表示优先权。当两个或多个事物同时发生时，具有更高优先级(precedence)的是首选。在 Postman 中对于环境特定变量和全局变量的同名，环境特定变量或局部变量具有更高的优先级。它将覆盖全局的。

1.现在在下拉面板中选择Weather API而不是No Environment

![Weather_API_Envrionment_Selected](https://www.toolsqa.com/gallery/Postman/18.Weather_API_Envrionment_Selected.png)

现在我们有两个可访问的同名变量。一个在 Weather API 环境中，一个是全局的。

1.  单击眼睛图标查看

![Eye_Icon_With_Same_Variables](https://www.toolsqa.com/gallery/Postman/19.Eye_Icon_With_Same_Variables.png)

这里我们有一个问题，两个变量具有相同的值。但是如果你看上面的图片，全局 URL 已经被一条线切掉了。发生这种情况是因为两个变量具有相同的名称，并且优先级将赋予局部变量，因此不会使用全局变量。

3.转到管理环境(齿轮图标)并单击Weather API环境

![Change_The_Weather_API](https://www.toolsqa.com/gallery/Postman/20.Change_The_Weather_API.png)

1.  将url值更改为你喜欢的任何值。这里我们把它改成了anonymous。

![编辑环境](https://www.toolsqa.com/gallery/Postman/21.Edit_Environment.png)

1.  关闭面板并通过选择眼睛图标再次查看当前环境

![Eye_Icon_All_Variables](https://www.toolsqa.com/gallery/Postman/22.Eye_Icon_All_Variables.png)

这两个变量现在都可以访问并可以使用。如果你现在按发送，你将从全局变量获得正确的响应，如果它们具有相同的名称，你将从局部变量获得正确的响应。这就是优先级的工作原理。

这全部来自环境和变量章节。现在你可以高效地使用 Postman 并像专业人士一样管理请求。我们现在将继续下一章。