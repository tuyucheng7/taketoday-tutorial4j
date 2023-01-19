[在之前的教程中，我们在“测试和收集](https://toolsqa.com/postman/test-and-collection-runner-in-postman/)”一章中学习了如何针对 API 端点设置测试，除了简单的代码片段或简单的JavaScript之外，还有更多的测试内容。编程语言中的断言是一种语句，用于验证给定谓词是真还是假。谓词是一个表达式，它只给出布尔表达式作为输出即。对或错。因此，在本教程中，我们将进一步了解断言

-   为什么要使用断言？
-   柴断言图书馆
-   如何使用 Chai 断言库在 Postman 中编写断言？

### 为什么要使用断言？

测试的唯一目的是确定在某种情况下系统的给定参数是否符合预期。为了强制系统的参数正确，我们在测试运行期间将预期值与实际值断言。断言用于在测试运行匹配期间断言预期值和实际值。如果它们不匹配，则测试失败，输出直接指向失败。

断言可以将你的测试写作技能提高到一个更高的水平。Postman 提供 JavaScript 支持来编写在 Postman Sandbox 下运行的测试。正如我们在 [设置 Postman 测试](https://toolsqa.com/postman/test-and-collection-runner-in-postman/)教程中了解到的，很难在 JavaS 中编写断言或函数式方法。在本教程中，我们将学习如何使用名为Chai - Assertion Library的外部 JavaScript 库编写断言。 与我们直接用 Javascript 编写的断言相比，我们将使用此断言库编写的断言花费的精力要少得多。下图显示了与一个非常基本的示例的区别。

![Javascript 代码](https://www.toolsqa.com/gallery/Postman/1.Javascript%20Code.jpg)

上图包含我们检查 a 是否等于 b 的代码。可以通过以下方式使用 chai 断言库编写相同的内容。

![断言_期望_100_101](https://www.toolsqa.com/gallery/Postman/2.Assertion_Expect_100_101.png)

是不是很简洁易读？想想我们可以通过这个库轻松编写的复杂问题。

断言对于查找代码中的缺陷非常有用，因为你可以像编写测试一样编写断言，尽管它们是不同的。测试执行所有步骤以达到应用程序的特定状态，断言可以验证应用程序在该点的状态。断言对于查找应用程序代码中的缺陷非常有用。如果在测试中添加断言，一旦断言失败，测试就会失败。但是，以简单的方式定义更复杂的测试，例如在数组中查找元素，在断言中只需要 2 行代码，而在JavaScript测试中至少需要 5-10 行代码。当我们编写断言而不是在测试中编写相同的东西时，阅读代码也变得非常容易。

在 Postman 中编写断言时，涉及两个主要步骤：

-   解析响应主体： 重要的是要知道你将获得什么样的响应以对其进行测试。最受欢迎的响应是JSON，仅仅是因为它很容易被人类阅读并且也是机器可读的。可能你们中的大多数人甚至不必处理任何其他响应，但这并不会破坏响应可以是任何格式的事实。HTTP 响应还有许多其他格式：
    -   XML
    -   HTML
    -   文本
-   编写测试代码： 由于我们已经在[测试和收集](https://toolsqa.com/postman/test-and-collection-runner-in-postman/)运行器教程中讨论了如何编写测试，因此我们不会在此处进行介绍。但是，我们在那里研究了用 Javascript 方法或函数方法编写测试。虽然由于 Postman Sandbox，我们只能用 Javascript 编写，但存在一个库可以让我们更轻松地编写一个测试，如果用 Javascript 编写，则需要更多代码行。这个库就是我们现在要讲的Chai Assertion Library。

但断言部分并不局限于Chai Assertion Library。Chai Assertion 只是 Postman 提供的众多断言的一部分，也是唯一一个在 Postman 之外的断言。所有其他断言都在 Postman Sandbox 下工作，这当然是 Postman 的。考虑到难度级别，chai 断言库比其他断言要容易得多，因此在本教程中我们将学习相同的内容。本教程将帮助你熟悉断言的概念，以便在下一个教程中我们可以执行一些困难的断言。

## 柴断言图书馆

Chai 断言库默认包含在 Postman 的应用程序中，因此当你编写 chai 断言时，你不必担心任何其他安装过程。关于 Postman 中的断言，最令人惊奇的事实是它们编写了人类可读的测试。用断言编写的测试非常易于阅读，你可能会发现它是一个英文句子。所有这些都使你的测试更易于阅读并且对人类更友好。虽然我们不需要编写非常复杂的 chai 断言，因为这不是必需的，但我们将介绍 Postman 中最常见和最常需要的断言，这将使你在使用该软件时的方式更加完整。

![使用 Chai Assertion Library 在 Postman 中进行断言](https://www.toolsqa.com/gallery/Postman/3.Assertions%20in%20Postman%20with%20Chai%20Assertion%20Library.png)

不过，如果你想了解有关 Chai Assertion Library 的更多信息，可以访问此[链接](https://www.chaijs.com/)。在下一节中，我们将了解一些断言。

### 如何使用 Chai 断言库在 Postman 中编写断言？

如果你访问过上面的链接，你会发现 Chai 库中有许多可用的断言。我们将在后面的部分中使用其中的一些，但在断言的这一部分中，我们将使你理解概念和断言。

断言：数字是否在数组中

1.在Postman中打开[天气api](https://bookstore.demoqa.com/swagger/#/BookStore/BookStoreV1BooksGet)

2.在测试选项卡中编写以下代码

pm.test("包含数", function(){ pm.expect([1,2,3]).to.include(3); });

![Assertion_Include_Number](https://www.toolsqa.com/gallery/Postman/4.Assertion_Include_Number.jpg)

按回车 ，你会看到明显的反应。

![Assertion_Include_Number_Response](https://www.toolsqa.com/gallery/Postman/5.Assertion_Include_Number_Response.jpg)

是的，我们看到这个数字被包括在内，因为 1,2,3 有 3。很明显。

断言：数组为空

1.在weather api(或你选择的任何api)的测试选项卡中编写以下代码

pm.test("空数组", function(){ pm.expect([2]).to.be.an('array').that.is.empty; });

![Expect_Array_To_Be_Empty](https://www.toolsqa.com/gallery/Postman/6.Expect_Array_To_Be_Empty.png)

在按回车之前猜测响应

![期望_Array_To_Be_Empty_Response](https://www.toolsqa.com/gallery/Postman/7.Expect_Array_To_Be_Empty_Response.png)

好的。所以你现在一定已经非常熟悉 Chai Assertion Library。我们现在将向你展示另一个断言来结束本教程。

断言：验证对象

pm.test("测试名称", function(){

让一个= {

“名称”：“哈里斯”

};

让 b = {

“名称”：“哈里斯”

};  pm.expect(a).to.eql(b);

});

![断言变量](https://www.toolsqa.com/gallery/Postman/8.Assertion_variable.png)

按发送并查看结果。

![断言变量响应](https://www.toolsqa.com/gallery/Postman/9.Assertion_variable_Response.png)

它通过了，因为名字是相等的。但是你可能想知道我们上面使用的 equal 和 eql。在清除空气之前，让我们看看相同但相等的响应。

编写与上面相同的代码，并将 eql 替换为 equal。

![断言变量等于](https://www.toolsqa.com/gallery/Postman/10.Assertion_variable_equal.png)

你是否得到了与 eql 中相同的响应？

![Assertion_variable_equal_Response](https://www.toolsqa.com/gallery/Postman/11.Assertion_variable_equal_Response.png)

尽管我们有相同的代码，但eql 和 equal 会产生不同的响应。当我们使用equal时，我们比较创建的对象，这里是不同的，即 a 和 b。在使用eql时，我们比较对象的属性，在本例中是名称。由于两个名称相同，比较通过。equal 使用 === 运算符，称为严格相等。而 eql 是比较对象的各个属性的深度相等。

尝试编辑上面代码的最后一行并编写pm.expect(a).to.equal(a); 看看你得到了什么作为回应。

所以在这里我们想结束Chai Assertion Library的话题。在 Chai Library 网站上练习越来越多。我们将继续我们的下一个教程，现在关于编写在 Postman 沙盒下执行的断言。