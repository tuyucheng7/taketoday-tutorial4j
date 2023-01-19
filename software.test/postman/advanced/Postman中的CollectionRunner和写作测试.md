在上一个教程中，我们了解了如何[保存集合](https://toolsqa.com/postman/collections-in-postman/)和[发送 POST 请求](https://toolsqa.com/postman/post-request-in-postman/)。我们在上一个教程中使用的请求只有一个请求。在现实世界中，当我们工作时，我们会收到大量不同的请求和响应。使用 Postman 测试有助于建立高效的请求和集合。例如，如果我们作为位于不同地点的许多其他图书馆的集中式图书馆，我们需要跟踪所有地方的每一本书。我们需要在我们的系统中列出每本书，那么我们如何知道我们是否有某本书以及它在哪个图书馆可用？

当我们在集合中工作时，我们会一起运行许多请求，这些请求可能大到同时运行大约 200 个请求。我们如何设法确保每个请求都成功运行？或者哪一个没有给出回应？这些都是我们将在本教程中学到的东西。在本教程中，我们将重点关注 postman 中最重要的两件事

-   测试
-   收藏赛跑者

## 在邮递员中测试

Postman 中的TEST类似于一般测试的定义。在 Postman 中，我们测试我们的请求，了解我们需要了解的关于请求的任何信息。例如，如果我需要知道我的请求是否给出了状态码 201。这可以在 Postman 中进行管理。此外，根据一个请求在 Postman 中运行测试没有任何限制。一个请求可以通过多个测试，所有测试都可以同时看到。

不过要记住一件事。测试并不总是运行。Postman 中的测试仅在请求成功时运行。如果对你的请求不正确这一事实没有任何回应，我们将无法对其进行测试。此外，你需要知道测试是在 Postman 中用 Javascript 编写的。虽然你不需要成为专家，但你应该稍微熟悉 Javascript 以便它有所帮助。在本教程中，我们将尝试解释所有内容，这样你就不需要了解 Javascript 并可以自己编写测试。

邮递员中的TEST可以用两种方式编写，即

-   Javascript方法
-   功能方法

今天在Postman中使用这两种方法来编写测试，这两种方法都使用javascript作为基础语言。JavaScript 方法是旧方法，而功能方法是新方法。尽管 Postman 并未表示将终止对旧方法的支持，但它建议使用函数式方法，正如 Postman 官方网站上所说，“函数式方法是两者中更强大的方法”。但由于你需要同时了解两者，我们将向你介绍功能方法。

所以现在，我们可以进行第一个测试了。

### 如何使用 JavaScript 方法在 Postman 中设置测试？

1.  使用我们在[POST 请求](https://toolsqa.com/postman/post-request-in-postman/)教程中使用的用于在客户注册中创建条目的 API(带有正文参数)，不用担心条目是否被创建。

![在 Postman 中测试](https://www.toolsqa.com/gallery/Postman/1.Test%20in%20Postman.png)

1.  打开测试选项卡。

![邮递员中的测试选项卡](https://www.toolsqa.com/gallery/Postman/2.Test%20Tab%20In%20Postman.png)

1.  编写以下 Javascript 代码，如下所示

tests["状态代码为 200"] = responseCode.code === 200

![Postman 测试状态码 200](https://www.toolsqa.com/gallery/Postman/3.Postman%20Test%20Status%20Code%20200.png)

现在我们将通过上面的行来了解其含义。

-   tests：我们看到的第一个词是“tests”，它是一个数组类型的变量。该数组可以包含任何数据类型，例如字符串和整数，甚至布尔值。
-   Status Code is 20： Status code is 200 只是一个名称或一个简单的字符串。我们定义这个名字是为了知道我们执行的测试是什么，因此这个名字应该是有意义的。如果我写 tests[" Passed "]，那么我将无法知道哪种测试已经通过，如果我们运行不止一个测试，比如 20 个和一个测试失败，这也会变得越来越复杂。你也可以写“状态代码正常”。
-   responseCode.code：响应码是指我们在响应框中收到的响应状态码。我们可以在 Postman 中对响应代码运行多个测试，例如了解状态代码是否具有字符串。第二个是一个对象，被调用以了解测试的状态代码(不是完整信息，只是代码)。一旦我们调用responseCode.code，首先会暂时保存状态码的所有信息，然后调用对象来检查状态码。如果状态代码等于 200，则测试将值 True 保存在其中。

在测试选项卡下的响应框中，那些具有TRUE值的测试显示PASS并写入数组名称，否则显示FAIL。

1.  单击发送并查看响应框中的测试结果选项卡。

![邮递员测试结果](https://www.toolsqa.com/gallery/Postman/4.Postman%20test%20results.png)

结果说我们的测试通过了。这意味着我们正在检查我们是否得到 200 作为状态代码，并且在这个请求中，我们得到了它。

1.  将请求保存在Myfolder中的MyFirstCollection 中

![保存_Request_In_My_Folder](https://www.toolsqa.com/gallery/Postman/5.Save_Request_In_My_Folder.png)

至此，你已经成功执行了你的第一个 Request with Test。

### 如何使用 JavaScript 方法在 Postman 中为请求设置多个测试？

正如我们之前了解到的，我们可以对单个请求使用多个测试，所有这些测试都会同时显示在响应框中。我们将对上面使用的同一请求执行多项测试。在文本编辑器中编写以下代码。

tests["状态码为 200"] = responseCode.code === 200;

tests["Body contains Fault"] = responseBody.has("ToolsQA");

tests["响应时间小于500ms"] = responseTime <1500;

![邮递员中的多重测试](https://www.toolsqa.com/gallery/Postman/6.Multiple%20Test%20in%20Postman.png)

注意：第二个测试检查响应正文中是否有字符串ToolsQA ，第三个测试检查响应时间是否小于 500 毫秒。

现在看看 Postman 中的响应框，我们编写了三个测试，其中一个失败了，这是第二个。因为我们的响应主体不包含ToolsQA。这样我们就可以根据一个请求在 Postman 中同时执行多个测试。记得保存请求。

![邮递员中的多重测试响应](https://www.toolsqa.com/gallery/Postman/7.Multiple%20Test%20Response%20in%20Postman.png)

我们的第一个测试通过了，因为我们的状态代码为 200，而我们的第三个测试通过了，因为我们的响应时间为 456 毫秒，小于 500。你的响应时间可能会有所不同。

### 如何使用函数式方法在 Postman 中设置测试？

下面写了一个简单的测试状态码是否为200的函数式方法

pm.test("状态码为 200", function () { pm.response.to.have.status(200); });

![Postman 中的函数方法测试](https://www.toolsqa.com/gallery/Postman/8.Function%20Method%20Test%20In%20Postman.png)

在上图中，工作与我们在JavaScript Test 中所做的相同。我们正在检查状态是否为 200。我们将看看上面写测试的代码。

-   pm.test : 这个是写测试规范的函数， 这里的pm指的是Postman api， test指的是函数的规范，是测试用的
-   状态代码是 200 ： 这一行只是一个字符串，它是测试名称。执行测试时，此字符串将写在结果前面。就像在 JS 中一样知道测试是关于什么的
-   function(){}：下一个参数是传递给执行测试的函数
-   pm.response ： 这用于捕获接收到的响应并对其执行断言，例如状态代码，标头等。这与 JS 格式的 responseCode 相同。

### Postman 中的片段以添加快速测试

由于测试多次用于不同的请求，并且一个集合中存在许多请求，因此需要编写一些预定义的测试代码，这些代码会被反复或最频繁地使用。在 Postman 中，此部分称为片段。Snippets 是 Postman 中预定义的测试代码，预先编写好使用，无需编写整个代码。片段可以节省大量时间并防止手动编写代码时可能发生的错误。

片段位于测试编辑器旁边。

![Postman 中的测试代码片段](https://www.toolsqa.com/gallery/Postman/9.Snippets%20For%20Test%20Code%20In%20Postman.png)

单击状态代码：代码为 200

![Snippets Status Code 200 测试 Postman](https://www.toolsqa.com/gallery/Postman/10.Snippets%20Status%20Code%20200%20Test%20Postman.png)

现在，看看编辑器

![测试功能方法](https://www.toolsqa.com/gallery/Postman/11.tests_functional_method.png)

这段代码和我们写的函数式方法测试状态码完全一样。

注意：由于 Postman 更喜欢函数式方法，因此这些片段仅在函数式方法中可用。

你可以探索不同的片段以更好地理解不同断言的测试代码。

## Postman 中的 Collection Runner

之前介绍的 Postman中的集合运行程序用于一起运行整个集合。集合运行器一次运行集合或文件夹(无论你选择什么)中的所有请求。Postman 中的 Collection runner 不显示任何响应，用于检查测试用例是否通过。集合运行器控制台显示一个位置的所有测试及其结果。要运行集合运行程序，请首先确保在MyFirstCollection中的文件夹MyFolder中至少有两个请求，如图所示。

这两个请求是[天气 api](https://bookstore.demoqa.com/swagger/#/BookStore/BookStoreV1BooksGet) (我们在[GET 请求](https://toolsqa.com/postman/get-request-in-postman/)章节中使用它)和[客户注册 api](https://bookstore.demoqa.com/swagger/#/BookStore/BookStoreV1BooksGet) (我们在[POST 请求](https://toolsqa.com/postman/post-request-in-postman/)章节中使用它)。请记住，客户 API 是一个 Post 请求，因此它也包含正文参数。

注意：在天气 API 中，我们使用了名为Response Time is less than 200ms的片段，这是自我描述的。

### 如何在 Postman 中运行 Collection Runner？

现在我们将看看如何使用 Collection Runner 在 Postman 中同时运行多个请求。

1.  点击亚军

![赛跑者](https://www.toolsqa.com/gallery/Postman/12.Runner.png)

1.  单击MyFirstCollection，然后单击MyFolder

![选择_Collection_In_Runner2](https://www.toolsqa.com/gallery/Postman/13.Select_Collection_In_Runner2.png)

注意：我希望你已将请求保存在上面显示的集合中。

在控制台中，你会看到两个选项：

-   迭代次数：迭代次数是相同请求运行的次数。例如，迭代设置为 3 将运行所有请求 3 次。将其设置为 2。
-   延迟：延迟时间是在任意两次迭代之间等待的时间。10ms 的延迟时间意味着 Postman 将在第二次迭代之前运行一次迭代后等待 10ms。设置为 5ms。

1.  单击运行我的文件夹

![Runner_Run_MyFolder](https://www.toolsqa.com/gallery/Postman/14.Runner_Run_MyFolder.png)

1.  如你所见，所有测试及其结果都可用。

![Runner_Console](https://www.toolsqa.com/gallery/Postman/15.Runner_Console.png)

-   每个请求有两次迭代。在第一次迭代中，我得到的响应时间为 761 毫秒，大于 500 毫秒，但在第二次迭代中，我得到的响应时间为 392 毫秒，小于 500 毫秒，这导致相应测试失败。你的测试结果可能会有所不同。
-   由于我们没有对天气 API 请求执行任何测试，因此控制台显示The request does not have any test。

继续对 weather API 执行多个测试，然后尝试在 collection runner 中运行它们。

你还可以选择运行请求的环境，但环境应该对请求有效，因此它应该包含与请求中使用的相同变量和相同的值。你可以在[环境和变量](https://toolsqa.com/postman/environment-variables-in-postman/)一章中了解更多信息。

![选择原始方法](https://www.toolsqa.com/gallery/Postman/16.Select_Raw_Method.png)

这样你就可以在一个集合或一个文件夹中运行所有保存在 Postman 中的请求。在下一章中，我们将了解 Postman 中的环境和变量。

### 练习练习

1.  [为天气 api](https://bookstore.demoqa.com/swagger/#/BookStore/BookStoreV1BooksGet)创建两个测试。
2.  JS 方法中的一项测试将测试请求的方法是否为 POST。
3.  功能方法中的第二个测试将检查响应正文中是否包含字符串 INVALID。
4.  将请求保存在 MyFirstCollection 内的 Myfolder 文件夹中。
5.  在 Collection Runner 中运行 MyFolder。