## 使用 Postman 的 POST 请求

在前面的教程中，我们学习了如何发送[GET 请求](https://toolsqa.com/postman/get-request-in-postman/)，还学习了[请求参数](https://toolsqa.com/postman/request-parameters-in-postman/)。从我们的 Rest 基础知识我们已经知道什么是[HTTP 请求](https://toolsqa.com/client-server/http-request/)和[HTTP 响应](https://toolsqa.com/client-server/http-response/)。如果你还没有阅读这两个教程，请在继续之前阅读。在本章中，我们将重点介绍另一种请求方法，即Postman 中的 POST 请求。

### 什么是 POST 请求？

POST是类似于GET请求的HTTP 动词，它指定客户端正在给定的Endpoint上发布数据。POST请求是一种方法，当我们需要将请求正文中的一些附加信息发送到服务器时使用。当我们发送 POST 请求时，我们通常打算在服务器上进行一些修改，例如更新、删除或添加。POST 请求的经典示例之一是登录页面。当你第一次注册任何东西时，比方说 Facebook，你会将你的个人信息(例如密码)发送到服务器。服务器创建一个具有相同详细信息的新帐户，该帐户将永久添加到 Facebook 服务器上。你刚刚在服务器上创建了一个新资源。POST请求非常流行，主要用于发送一些敏感信息，例如提交表单或向服务器发送敏感信息。

在本教程中，我们将探讨POST 请求的不同功能以及我们如何在 Postman 中创建它们。在我们将尝试使用一个示例来清楚地了解POST 请求之前。

### Postman 中的 POST 请求

每个REST 端点都有自己的与之关联的HTTP 动词。如果端点指定应使用 POST HTTP 动词调用它，则客户端只能使用POST HTTP 动词调用端点。让我们首先检查当我们为POST 端点请求GET方法而不是POST方法时会发生什么。还要检查当我们执行没有Body的POST请求时会发生什么。

##### POST 端点上的 GET 请求

1.  在 Postman 端点栏中使用 API http://restapi.demoqa.com/customer/register(此 API 用于注册新客户)并按发送。确保在方法类型下拉列表中选择了GET 。

![Postman 中的 POST 请求](https://www.toolsqa.com/gallery/Postman/1.POST%20Request%20in%20Postman.png)

1.  查看 HTTP 状态码，将是405 Method not allowed。 这意味着我们正在使用不正确的方法类型访问端点。下图显示了详细信息。

![不正确的方法类型状态代码](https://www.toolsqa.com/gallery/Postman/2.IncorrectMethodTypeStatusCode.png)

1.  请参阅下面Body选项卡下的响应并关注fault error。

![Customer_API_Example_Response](https://www.toolsqa.com/gallery/Postman/3.Customer_API_Example_Response.png)

这意味着我们使用的方法类型无效，需要另一种方法类型。所以我们将尝试改变它，看看我们是否得到正确的回应。

##### 无正文的 POST 请求

1.  将方法类型更改为POST并按SEND

![Customer_API_Example_Change_Method_Type](https://www.toolsqa.com/gallery/Postman/4.Customer_API_Example_Change_Method_Type.png)

1.  现在，查看Response Body和Response Status 代码。

![Customer_API_Example_POST_Response](https://www.toolsqa.com/gallery/Postman/5.Customer_API_Example_POST_Response.png)

Fault Invalid Post Request表示我们输入的post数据无效。回想一下，我们在请求正文中添加了信息，因此我们需要在请求正文中输入一些内容并查看该格式是否与预期格式匹配。此外，你还可以看到显示400 BAD Request的状态代码。这意味着请求参数与服务器参数不匹配以获得响应。

##### 在 Postman 中发布请求

1.  现在让我们 向 POST 请求添加一个请求主体。每个端点都将记录其期望的方法类型和主体格式。让我们看看这个请求需要什么主体以及如何添加它。为此，请单击“正文”选项卡。

![Customer_API_Example_Body](https://www.toolsqa.com/gallery/Postman/6.Customer_API_Example_Body.png)

1.  单击 raw 并选择格式类型作为JSON，因为我们必须发送服务器期望的不正确格式。

![Raw_Json_Select](https://www.toolsqa.com/gallery/Postman/7.Raw_Json_Select.png)

1.  此端点需要一个包含新用户详细信息的Json正文。下面是一个示例Json正文。将以下内容复制并粘贴到 Postman 的正文选项卡中。

{

“名字”：“价值”

“姓氏：“价值”，

“用户名：“价值”，

“密码”：“值”，

“电子邮件”：“价值”

}

将属性值更改为 你想要的任何值(参考下图)。

![Customer_API_Example_JSON_String](https://www.toolsqa.com/gallery/Postman/8.Customer_API_Example_JSON_String.png)

1.  按发送并查看响应正文和响应状态。

![Customer_API_Example_User_Already_Exist](https://www.toolsqa.com/gallery/Postman/9.Customer_API_Example_User_Already_Exist.png)

Fault User Already Exits错误 意味着在数据库中，你或其他任何人之前已经创建了一个类似的条目。而如果你看到响应状态为200 OK，这意味着服务器接受了请求并发回了成功的响应。我们也可以由此推断响应主体是正确的并且服务器能够解释响应主体。现在在这个 API 请求中，Email 和 Username 应该是唯一的。所以你可以改变这些值(任何人都可以)。

如果值是唯一的，你将得到此响应

![Customer_API_Example_Success](https://www.toolsqa.com/gallery/Postman/10.Customer_API_Example_Success.png)

操作成功完成 意味着你的条目已在数据库中成功创建。

所以，通过这个例子，很明显，无论何时我们需要发送POST 请求，它都应该伴随着 Body。正文应采用正确的格式并使用正确的密钥才能从服务器获得正确的响应。现在，我们将详细了解 Postman 中 Post 请求的每一个特性。

## 在 Postman 的 POST 请求中发送数据的不同方式

正如我们之前讨论的那样，发送 POST 请求意味着发送一个请求，其中包含包含在请求正文中的数据。可以有不同类型的数据，同样，也有不同的发送数据的方式。当你按照这些步骤操作时，你将详细了解它。

1.  如图所示，在构建器中将方法请求类型选择为POST 。

![发布_方法](https://www.toolsqa.com/gallery/Postman/11.Post_Method.png)

在 Postman 中选择 POST 请求类型后，你将看到 Body 选项已启用，它有不同的选项可以在 body 内发送数据。这些选项是：

-   表单数据
-   X-www-form-urlencoded
-   生的
-   二进制

![Body_Request_Types](https://www.toolsqa.com/gallery/Postman/12.Body_Request_Types.png)

### 表单数据

顾名思义，表单数据用于发送你包装在表单中的数据，例如你在填写表单时输入的详细信息。这些详细信息通过将它们写入KEY-VALUE对来发送，其中键是你要发送的条目的“名称”，值是它的值。下面的步骤将一目了然。

1.选择表单数据

![表单数据](https://www.toolsqa.com/gallery/Postman/13.form_data.png)

1.  添加以下KEY-VALUE对

-   名字：哈里斯
-   姓氏：拉乔拉

![表单数据填充](https://www.toolsqa.com/gallery/Postman/14.form_data_fill.png)

在这里，需要输入的某种形式的字段(此处为文本字段)中的名字和Harish 是其值，即用户输入的值。同样适用于姓氏 ID。

### x-www-form-urlencoded

表单数据和x-www-form-urlencoded非常相似。它们都用于几乎相同的目的。但是表单数据和x-www-form-urlencoded的区别在于，通过x-www-form-urlencoded发送的 URL 会被编码。编码意味着发送的数据将被编码为不同的字符，即使受到攻击也无法识别。

![x-www-form-urlencoded](https://www.toolsqa.com/gallery/Postman/15.x-www-form-urlencoded.png)

### 生的

Raw 是在 POST 方法中发送正文时最常用的部分或选项。从 Postman 的角度来看，这很重要。原始意味着正文消息显示为表示请求正文的位流。这些位将被解释为字符串服务器。

1.  单击二进制旁边的下拉菜单 ，可以看到可以发送请求的所有选项

![选择原始方法](https://www.toolsqa.com/gallery/Postman/16.Select_Raw_Method.png)

1.  点击JSON(application/json)

![Raw_Json_Select](https://www.toolsqa.com/gallery/Postman/17.Raw_Json_Select.png)

1.  在下面的编辑器中复制并粘贴这个

{

“名字”：“Harish”，

“姓氏”：“拉乔拉”

}

![原始类型_Json](https://www.toolsqa.com/gallery/Postman/18.Raw_Type_Json.png)

这与之前使用表单数据发送的数据相同，但现在使用 JSON 格式发送。

### 二进制

二进制旨在以无法手动输入的格式发送信息。由于计算机中的所有内容都转换为二进制，因此我们使用这些无法手动编写的选项，例如图像，文件等。要使用此选项

1.  点击binary，一个CHOOSE FILES 选项将可用

![二进制_选择_文件](https://www.toolsqa.com/gallery/Postman/19.Binary_Choose_File.png)

1.  选择任何文件，例如图像文件。

![Binary_File_Chosen](https://www.toolsqa.com/gallery/Postman/20.Binary_File_Chosen.png)

注意：如果你希望将一些数据与文件一起发送到服务器，那么也可以在表单数据中完成。

单击表单数据

![表单数据_2](https://www.toolsqa.com/gallery/Postman/21.form_data_2.png)

输入文件 作为密钥

![file_key_form_data](https://www.toolsqa.com/gallery/Postman/22.file_key_form_data.png)

你会看到一个隐藏的下拉菜单，上面写着默认文本。你可以选择任何文件格式，然后从系统中选择文件。

永远记住你的服务器期望什么。你不能发送不同于你的服务器期望的格式，否则，将没有响应或不正确的响应，这可以从响应的状态代码明显看出。所以现在，我们已经了解了 POST 方法以及如何在 Postman 中使用它。我们现在将继续学习下一个教程，即Collections。