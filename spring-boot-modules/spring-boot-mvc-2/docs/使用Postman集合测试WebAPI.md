##  1. 简介

要彻底测试Web API，我们需要某种Web客户端来访问API的端点。**Postman是一个独立的工具，它通过从服务外部发出HTTP请求来执行Web API**。

在使用Postman时，我们不需要仅仅为了测试而编写任何HTTP客户端基础架构代码。相反，我们创建称为集合的测试套件并让Postman与我们的API交互。

在本教程中，我们将了解如何创建可以测试REST API的Postman集合。

## 2. 设置

### 2.1 安装Postman

Postman可用于Linux、Mac和Windows，该工具可以从[Postman网站](https://www.getpostman.com/downloads/)下载和安装。

关闭启动画面后，我们可以看到用户界面：

<img src="../assets/img.png">

### 2.2 运行服务器

Postman需要一个实时的HTTP服务器来处理它的请求，对于本教程，我们将使用之前的项目spring-boot-rest，该项目可在[GitHub](https://github.com/tu-yucheng/taketoday-tutorials4j/tree/master/spring-boot-modules/spring-boot-mvc-2)上找到。

正如我们可能从标题中猜到的那样，spring-boot-rest是一个Spring Boot应用程序，我们使用Maven目标install构建应用程序。构建完成后，我们使用自定义Maven目标spring-boot:run启动服务器。

要验证服务器是否正在运行，我们可以在浏览器中访问此URL：

```bash
http://localhost:8082/spring-boot-rest/auth/foos
```

该服务使用内存数据库。当服务器停止时，所有记录都会被清除。

## 3. 创建Postman集合

Postman中的集合是一系列HTTP请求，Postman保存请求的各个方面，包括标头和消息正文。因此，我们可以将请求按顺序运行为半自动化测试。

让我们从创建一个新集合开始，我们可以单击New按钮上的下拉箭头并选择Collection：

[![Postman新菜单](https://www.baeldung.com/wp-content/uploads/2019/02/postman-new-menu-216x300.png)](https://www.baeldung.com/wp-content/uploads/2019/02/postman-new-menu.png)

当CREATE A NEW COLLECTION对话框出现时，我们可以将我们的集合命名为“ fooAPItest ”。最后，我们点击Create按钮，看到我们的新集合出现在左侧的列表中：

[![已创建集合](https://www.baeldung.com/wp-content/uploads/2019/02/collection-created-276x300.png)](https://www.baeldung.com/wp-content/uploads/2019/02/collection-created.png)

创建集合后，我们可以将光标悬停在它上面以显示两个菜单按钮。箭头按钮打开一个右拉面板，提供对Collection Runner的访问。相反，省略号按钮会打开一个下拉菜单，其中包含对集合的许多操作。

## 4. 添加POST请求

### 4.1。创建新请求

现在我们有一个空集合，让我们添加一个命中我们API的请求。具体来说，让我们向 URI /auth/foos 发送一条POST消息。为此，我们打开集合上的省略号菜单并选择添加请求。

当SAVE REQUEST对话框出现时，让我们提供一个描述性名称，例如“ add a foo”。然后，单击按钮Save to fooAPItest。

创建请求后，我们可以看到我们的集合表示一个请求。但是，如果我们的集合还没有扩展，那么我们还看不到请求。在这种情况下，我们可以单击集合来展开它。

现在，我们应该看到在我们的集合下列出的新请求。我们可以观察到，默认情况下，新请求是一个HTTPGET，这不是我们想要的。我们将在下一节中解决这个问题：

[![新请求](https://www.baeldung.com/wp-content/uploads/2019/02/new-request-294x300.png)](https://www.baeldung.com/wp-content/uploads/2019/02/new-request.png)

### 4.2. 编辑请求

要编辑请求，让我们单击它，从而将其加载到请求编辑器选项卡中：

[![Postman9](https://www.baeldung.com/wp-content/uploads/2019/02/postman9.png)](https://www.baeldung.com/wp-content/uploads/2019/02/postman9.png)

尽管请求编辑器有很多选项，但我们现在只需要其中的几个。

首先，让我们使用下拉菜单将方法从GET更改为 POST。

其次，我们需要一个 URL。方法下拉列表的右侧是请求URL的文本框。所以，让我们现在输入：

```plaintext
http://localhost:8082/spring-boot-rest/auth/foos
```

最后一步是提供消息正文。URL 地址下方是一排选项卡标题。我们将单击“正文”选项卡标题以进入正文编辑器。

在正文选项卡中，就在文本区域的上方，有一排单选按钮和一个下拉菜单。这些控制请求的格式和内容类型。

我们的服务接受 JSON 数据，因此我们选择原始单选按钮。在右侧的下拉列表中，我们应用JSON (application/json)内容类型。

设置好编码和内容类型后，我们将 JSON 内容添加到文本区域：

```json
{
    "name": "Transformers"
}
```

最后，让我们确保通过按Ctrl-S或点击Save按钮来保存我们的更改。保存按钮位于发送按钮的 右侧。保存后，我们可以看到左侧列表中的请求已更新为 POST：

[![后方法](https://www.baeldung.com/wp-content/uploads/2019/02/post-method-1024x262.png)](https://www.baeldung.com/wp-content/uploads/2019/02/post-method.png)

## 5. 运行请求

### 5.1。运行单个请求

要运行单个请求，我们只需单击URL 地址右侧的发送按钮。单击发送后，响应面板将在请求面板下方打开。可能需要向下滚动才能看到它：

[![回复](https://www.baeldung.com/wp-content/uploads/2019/02/post-response.png)](https://www.baeldung.com/wp-content/uploads/2019/02/post-response.png)

让我们检查一下我们的结果。具体来说，在标题栏中，我们看到我们的请求成功，状态为201 Created。此外，响应正文显示我们的Transformers记录收到的 id 为 1。

### 5.2. 使用收集运行器

与发送按钮相比，集合运行器可以执行整个集合。要启动集合运行程序，我们将光标悬停在我们的fooAPI测试集合上，然后单击向右拉箭头。在右拉面板中，我们可以看到一个运行按钮，所以让我们点击它：

[![收藏拉右](https://www.baeldung.com/wp-content/uploads/2019/02/collection-pull-right.png)](https://www.baeldung.com/wp-content/uploads/2019/02/collection-pull-right.png)

当我们单击“运行”按钮时，集合运行器会在新窗口中打开。因为我们是从集合中启动它的，所以跑步者已经初始化为我们的集合：

[![Postman1](https://www.baeldung.com/wp-content/uploads/2019/02/postman1.png)](https://www.baeldung.com/wp-content/uploads/2019/02/postman1.png)

集合运行器提供了影响测试运行的选项，但我们在本练习中不需要它们。让我们直接进入底部的Run fooAPItest按钮并单击它。

当我们运行集合时，视图变为Run Results。在这个视图中，我们看到一个测试列表，标记为绿色表示成功，红色表示失败。

即使发送了我们的请求，运行程序也会指示零测试通过并且零测试失败。这是因为我们还没有在我们的请求中添加测试：

[![Postman2-1](https://www.baeldung.com/wp-content/uploads/2019/02/postman2-1.png)](https://www.baeldung.com/wp-content/uploads/2019/02/postman2-1.png)

## 6. 测试响应

### 6.1。向请求添加测试

要创建测试，让我们返回到构建POST方法的请求编辑面板。我们单击位于URL下的测试选项卡。当我们这样做时，会出现测试面板：

[![Postman3](https://www.baeldung.com/wp-content/uploads/2019/02/postman3.png)](https://www.baeldung.com/wp-content/uploads/2019/02/postman3.png)

在“测试”面板中，我们编写了 JavaScript，当从服务器接收到响应时将执行该 JavaScript。

Postman提供了提供对请求和响应的访问的内置变量。此外，可以使用require()语法导入许多 JavaScript 库。

本教程中涉及的脚本功能太多了。但是，[官方Postman文档](https://learning.getpostman.com/docs/postman/scripts/test_scripts)是有关此主题的极好资源。

让我们继续向我们的请求添加三个测试：

```javascript
pm.test("success status", () => pm.response.to.be.success );
pm.test("name is correct", () => 
  pm.expect(pm.response.json().name).to.equal("Transformers"));
pm.test("id was assigned", () => 
  pm.expect(pm.response.json().id).to.be.not.null );
```

正如我们所见，这些测试使用了Postman提供的全局pm模块。特别是，测试使用pm.test()、pm.expect()和pm.response。

pm.test ()函数接受一个标签和一个断言函数，例如expect()。我们使用pm.expect()来断言响应 JSON 内容的条件。

pm.response对象提供对从服务器返回的响应的各种属性和操作的访问。可用属性包括响应状态和 JSON 内容等。

与往常一样，我们使用Ctrl-S或Save按钮保存我们的更改。

### 6.2. 运行测试

现在我们已经完成了测试，让我们再次运行请求。按下发送按钮会在响应面板的测试结果选项卡中显示结果：

[![img](https://www.baeldung.com/wp-content/uploads/2019/02/postman4.png)](https://www.baeldung.com/wp-content/uploads/2019/02/postman4.png)

同样，collection runner 现在显示我们的测试结果。具体来说，左上角的摘要显示更新的通过和失败总数。摘要下方是一个列表，其中显示了每个测试及其状态：

[![Postman5](https://www.baeldung.com/wp-content/uploads/2019/02/postman5.png)](https://www.baeldung.com/wp-content/uploads/2019/02/postman5.png)

### 6.3. 查看Postman控制台

Postman控制台是用于创建和调试脚本的有用工具。我们可以在View菜单下找到名为ShowPostmanConsole的控制台。启动时，控制台会在新窗口中打开。

当控制台打开时，它会记录所有HTTP请求和响应。此外，当脚本使用console.log() 时，Postman控制台会显示这些消息：

[![Postman6](https://www.baeldung.com/wp-content/uploads/2019/02/postman6.png)](https://www.baeldung.com/wp-content/uploads/2019/02/postman6.png)

## 7. 创建请求序列

到目前为止，我们只关注单个HTTP请求。现在，让我们看看我们可以对多个请求做什么。通过将一系列请求链接在一起，我们可以模拟和测试客户端-服务器工作流。

在本节中，让我们应用所学知识来创建请求序列。具体来说，我们将在我们已经创建的POST请求之后再添加三个要执行的请求。这些将是 GET、DELETE，最后是另一个 GET。

### 7.1。捕获变量中的响应值

在我们创建新请求之前，让我们对现有的POST请求进行修改。因为我们不知道服务器会为每个foo实例分配哪个 id，所以我们可以使用一个变量来捕获服务器返回的 id。

为了捕获该 id，我们将在POST请求的测试脚本的末尾再添加一行：

```javascript
pm.variables.set("id", pm.response.json().id);
```

pm.variables.set ()函数接受一个值并将其分配给一个临时变量。在这种情况下，我们正在创建一个id变量来存储我们对象的 id 值。设置后，我们可以在以后的请求中访问此变量。

### 7.2. 添加GET请求

现在，使用前面部分中的技术，让我们在POST请求之后添加一个GET请求。

通过这个GET请求，我们将检索POST 请求创建的同一个foo实例。让我们将此GET请求命名为“ get a foo ”。

GET 请求的URL是：

```plaintext
http://localhost:8082/spring-boot-rest/auth/foos/{{id}}
```

在这个URL中，我们引用了我们之前在POST请求期间设置的id变量。因此，GET 请求应该检索由POST创建的相同实例。

变量出现在脚本之外时，使用双括号语法{{id}}引用。

由于GET请求没有正文，让我们直接进入“测试”选项卡。因为测试相似，我们可以从POST请求中测试，然后进行一些更改。

首先，我们不需要再次设置id变量，所以我们不要该行。

其次，我们知道这次期待哪个 id，所以让我们验证那个 id。我们可以使用id变量来做到这一点：

```javascript
pm.test("success status", () => pm.response.to.be.success );
pm.test("name is correct", () => 
  pm.expect(pm.response.json().name).to.equal("Transformers"));
pm.test("id is correct", () => 
  pm.expect(pm.response.json().id).to.equal(pm.variables.get("id")) );
```

由于双括号语法不是有效的 JavaScript，我们使用pm.variables.get()函数来访问id变量。

最后，让我们像以前一样保存更改。

### 7.3. 添加删除请求

接下来，我们将添加一个DELETE请求，该请求将从服务器中删除foo对象。

我们将继续在GET之后添加一个新请求，并将其方法设置为 DELETE。我们可以将这个请求命名为“ delete a foo ”。

删除的URL与GETURL 相同：

```plaintext
http://localhost:8082/spring-boot-rest/auth/foos/{{id}}
```

响应不会有要测试的主体，但我们可以测试响应代码。因此，DELETE 请求将只有一个测试：

```plaintext
pm.test("success status", () => pm.response.to.be.success );
```

### 7.4. 验证删除

最后，让我们添加另一个GET请求副本，以验证DELETE是否确实有效。这一次，让我们我们的第一个GET请求，而不是从头开始创建请求。

要请求，我们右键单击请求以显示下拉菜单。然后，我们选择Duplicate。

重复的请求将在其名称后附加“”一词。让我们将其重命名为“验证删除”以避免混淆。通过右键单击请求可以使用重命名选项。

默认情况下，重复请求会立即出现在原始请求之后。因此，我们需要将其拖到DELETE请求下方。

最后一步是修改测试。但是，在我们这样做之前，让我们借此机会看看失败的测试。

我们已经了GET请求并将其移到DELETE之后，但我们还没有更新测试。由于DELETE请求应该删除了对象，因此测试应该失败。

让我们确保保存所有请求，然后在收集运行器中点击重试。正如预期的那样，我们的测试失败了：

[![Postman7](https://www.baeldung.com/wp-content/uploads/2019/02/postman7.png)](https://www.baeldung.com/wp-content/uploads/2019/02/postman7.png)

现在我们的简短绕道已经完成，让我们修复测试。

通过查看失败的测试，我们可以看到服务器以 500 状态响应。因此，我们将在测试中更改状态。

此外，通过在PostmanConsole中查看失败的响应，我们了解到该响应包含一个原因属性。此外，原因属性包含字符串“ No value present ”。我们也可以对此进行测试：

```javascript
pm.test("status is 500", () => pm.response.to.have.status(500) );
pm.test("no value present", () => 
  pm.expect(pm.response.json().cause).to.equal("No value present"));
```

### 7.5。运行完整集合

现在我们已经添加了所有请求，让我们在集合运行器中运行完整集合：

[![Postman8](https://www.baeldung.com/wp-content/uploads/2019/02/postman8.png)](https://www.baeldung.com/wp-content/uploads/2019/02/postman8.png)

如果一切都按计划进行，我们应该有九次成功的测试。

## 8. 导出和导入集合

虽然Postman将我们的收藏存储在一个私人的本地位置，但我们可能希望共享该收藏。为此，我们将集合导出到 JSON 文件。

导出命令在集合的省略号菜单中可用。当提示输入 JSON 文件版本时，让我们选择最新的推荐版本。

选择文件版本后，Postman将提示你输入导出集合的文件名和位置。例如，我们可以在 GitHub 项目中选择一个文件夹。

要导入以前导出的集合，我们使用Import按钮。我们可以在Postman主窗口的工具栏中找到它。当Postman提示输入文件位置时，我们可以导航到我们希望导入的 JSON 文件。

值得注意的是，Postman不会跟踪导出的文件。因此，在我们重新导入集合之前，Postman不会显示外部更改。

## 9. 总结

在本文中，我们使用Postman为REST API创建了半自动化测试。虽然本文介绍了Postman的基本功能，但我们几乎没有触及其功能的表面。[Postman在线文档](https://learning.getpostman.com/docs/postman/launching_postman/installation_and_updates/)是深入探索的宝贵资源。