## 邮递员中的饼干

由于cookie首先从服务器返回，让我们看看当我们访问 Google 服务器时返回了哪些 cookie。转到 Postman 应用程序，在 Postman 中点击以下 API www.google.com。

![google_端点](https://www.toolsqa.com/gallery/Postman/1.google_Endpoint.png)

现在转到响应部分中的标题选项卡。

![Header_Response_2](https://www.toolsqa.com/gallery/Postman/2.Header_Response_2.png)

在这里你会找到Set_Cookie，这是由 google 服务器发送的 cookie。

![Cookie_Response](https://www.toolsqa.com/gallery/Postman/3.Header_Response_2.png)

标头包含太多值，cookie 是标头中非常重要的部分。因此，Postman 也给了我们单独的 Cookies 选项。

![Cookie_Option_Response](https://www.toolsqa.com/gallery/Postman/4.Cookie_Option_Response.png)

注意：这将显示与我们在标题部分看到的相同的 cookie。此部分显示的 Cookie 是与 Google 相关的 Cookie。站点特定的 cookie。

### 在 Postman 中管理 Cookie

这就是我们如何查看从我们已响应的服务器接收到的 cookie。Postman 还单独提供了一个Cookie 管理器，你可以在其中添加、删除或修改 Cookie。

点击右上角的“ Cookies ”。

![Cookie_Manager](https://www.toolsqa.com/gallery/Postman/5.Cookie_Manager.png)

这将打开 cookie 管理器面板，你可以在其中查看所有 cookie 所在的位置。

![邮递员中的饼干](https://www.toolsqa.com/gallery/Postman/6.Cookies%20in%20Postman.png)

注意：本节中显示的 Cookie 是特定于浏览器的 Cookie，是指从你之前发出的请求中保存的 Cookie，与网站无关。

此 cookie 管理器的工作方式与浏览器的相同。无论你当前正在做什么，它都会保存所有 cookie。正如你在我的 cookie 管理器中看到的那样，它有来自imgur.com网站的 cookie，我在 [ OAuth 2.0 教程](https://OAuth 2.0) 中使用了它，从那以后我多次使用 Postman。我还清除/删除了所有与Imgur相关的集合，但 cookie 仍然由 Postman 维护，就像浏览器一样。

### 在 Postman 中添加 Cookie

添加cookie。转到管理器中的google.com域，然后单击添加 Cookie。

![添加_Cookie](https://www.toolsqa.com/gallery/Postman/7.Add_Cookie.png)

将打开一个新的文本框，其中已经写入了一些值。如下图所示更改这些值。

![添加_新_Cookie](https://www.toolsqa.com/gallery/Postman/8.Adding_New_Cookie.png)

现在你已经向域google.com添加了一个新的 cookie 。此 cookie 现在将与请求一起发送到服务器。按保存并关闭面板。再次点击端点，现在可以看到标题部分。

![New_Cookie_Response](https://www.toolsqa.com/gallery/Postman/9.New_Cookie_Response.png)

你可以在此处看到我们添加的 cookie。这显示了多次，因为谷歌服务器无法识别此 cookie，因此过期日期也设置为 1990。请注意其他 cookie 的过期日期。

同样，也可以通过在cookie管理器中打开已经保存的cookie来修改cookie。请自己尝试作为练习。

## 对 Cookie 执行测试

在 Postman 中，还可以检查 cookie，即是否返回预期的 cookie 或预期的值。如果我们收到太多 cookie，这对我们有很大帮助。为此，你需要一些先决条件。

先决条件

-   测试知识-参考 [How to set Tests in Postman](https://toolsqa.com/postman/test-and-collection-runner-in-postman/)
-   断言知识- 参考 [How to write Assertions in Postman](https://toolsqa.com/postman/test-and-collection-runner-in-postman/)
-   Chai断言库知识-参考 [Postman中的Chai Assertion Library](https://toolsqa.com/postman/test-and-collection-runner-in-postman/)

### 断言：检查 Cookie 是否存在

在这里，我们将检查是否正在获取我们期望的 cookie。在测试选项卡中，编写以下测试

pm.test("Cookies_Check", function(){ pm.expect(pm.cookies.has('NID')).to.be.true; });

![检查_Cookie_可用性](https://www.toolsqa.com/gallery/Postman/10.Check_Cookie_Availability.png)

注意：我们已经知道 google.com 保存了 NID cookie。所以我们只是通过测试来检查相同的内容。其他服务器不会出现这种情况。因此，请事先检查其他域。

测试结果将传递信号，表明请求中存在名称为 NID 的 cookie。

![Check_Cookie_Availability_Result](https://www.toolsqa.com/gallery/Postman/11.Check_Cookie_Availability_Result.png)

### 断言：检查 Cookie 的值

我们还可以检查 cookie 中的特定值。通过此测试，我们确认 cookie 包含我们希望看到的相同值。

在你的测试选项卡中编写以下代码

pm.test("Cookies_Value_Check", function(){ pm.expect(pm.cookies.get('NID')).to.eql('abc'); });

![检查_Cookie_值](https://www.toolsqa.com/gallery/Postman/12.Check_Cookie_Value.png)

此代码将检查 cookie NID 是否具有值abc。

由于这不是 NID 的值，我们将获得失败状态。此外，Postman 会告诉我们预期值，即 NID cookie 的正确值。

![检查_Cookie_Value_Result](https://www.toolsqa.com/gallery/Postman/13.Check_Cookie_Value_Result.png)

至此，我们可以在这里结束本教程。Cookie 是当今 IT 行业的一个非常重要的方面。由于软件和应用程序依赖于用户，因此 cookie 使用户体验比以往任何时候都更好。也继续在其他域中练习 cookie。我们现在将进入下一个教程。