到目前为止，我们已经[了解了如何对](https://www.toolsqa.com/cross-browser-testing/live-testing-on-crossbrowsertesting/)实时测试环境中可用的 CrossBrowserTesting和[实时测试设置执行实时测试。](https://www.toolsqa.com/cross-browser-testing/live-test-settings/)除此之外，我们还了解了同一环境中可用的高级选项。CrossBrowserTesting 中的实时测试是一个非常强大的元素，它包含大量功能以提供高效和简单的跨浏览器测试，并且可以从实时测试工具面板访问。一些设置只是取决于用户本身的偏好。由于存在各种类型的用户，因此有必要照顾到他们中的每一个。上一篇文章专门介绍了在测试运行之前可以更改或设置的设置。

在本教程中，我们将看到运行测试后可用的设置，并且当前正在远程查看网站。此设置面板称为工具面板，并在测试运行时自动出现在屏幕上。这篇文章专门介绍工具面板中的每个可用设置：

-   从工具面板更改规格
-   在实时测试中捕获屏幕截图
-   录制实时测试会话的视频
-   在 CrossBrowserTesting 中执行实时测试时捕获网络流量
-   在实时测试中将文本从键盘粘贴到远程网站
-   在 CrossBrowserTesting 的实时测试中上传文件
-   Toggle 实时测试中的开发人员工具
-   在实时测试时更改演员表的比例
-   实时测试中的实时测试设置

## 实时测试工具面板中的设置

用户运行测试后，实时测试中的工具面板可用。工具面板包含一些设置，方便用户在执行测试时执行某些功能，例如捕获屏幕截图等。要查看测试面板，只需运行具有所需规格的测试。工具面板将出现在网站的主屏幕上。

![实时测试工具面板](https://toolsqa.com/gallery/CrossBrowser%20Testing/1%20Live%20Testing%20Tool%20Panel.png)

我们将从顶部看到每个选项。

### 更改实时测试中的规范

在工具面板最上面的栏中，你会注意到窗口和浏览器类型和版本。将鼠标悬停在相同的位置以切换操作系统或浏览器或其版本。

![实时测试工具面板 - 切换悬停工具面板](https://toolsqa.com/gallery/CrossBrowser%20Testing/2%20Live%20Testing%20Tool%20Panel%20-%20Switch%20Hover%20Tool%20Pane.png)

单击切换将打开面板，你可以在其中切换操作系统和浏览器。

![现场测试工具面板 - 开关面板](https://toolsqa.com/gallery/CrossBrowser%20Testing/3%20Live%20Testing%20Tool%20Panel%20-%20Switch%20panel.webp)

正如突出显示的那样，切换规范将结束当前规范，并会立即使用新规范启动新会话，而无需重定向到实时测试仪表板屏幕。有关规范的详细信息已在[如何执行实时测试](https://www.toolsqa.com/cross-browser-testing/live-testing-on-crossbrowsertesting/)教程中进行了讨论。

进入工具面板，第一个选项是捕获屏幕截图。

### 在 CrossBrowserTesting 中捕获屏幕截图

要捕获你当前所在页面的屏幕截图，请单击工具面板上的快照按钮。

![实时测试工具面板 - 截屏](https://toolsqa.com/gallery/CrossBrowser%20Testing/4%20Live%20Testing%20Tool%20Panel%20-%20Capture%20Sscreenshot.webp)

按下后，你将收到截取屏幕截图的通知。可以从测试摘要部分的测试结果中查看快照，你可以在[结果和摘要](https://www.toolsqa.com/cross-browser-testing/live-testing-on-crossbrowsertesting/)中了解更多信息。

### 在实时测试工具面板中录制视频

录制视频选项是工具面板中的下一个选项。![实时测试工具面板 - 录制视频](https://toolsqa.com/gallery/CrossBrowser%20Testing/5%20Live%20Testing%20Tool%20Panel%20-%20Record%20video.webp)

选择此选项可录制最多十分钟的整个测试会话的视频。录制视频后，可以从测试摘要中查看。

![recorded_video_test_summary](https://toolsqa.com/gallery/CrossBrowser%20Testing/6%20recorded_video_test_summary.png)

此摘要将包含有关测试的所有详细信息以及你刚刚录制的视频。应该注意的是，此选项与高级选项中的可用选项相同。

### 在实时测试工具面板中记录网络流量

记录网络流量记录访问网站及其元素时遇到的网络流量。它已在高级选项部分进行了讨论。这是相同的设置，可以在测试后从工具面板启用。

![record_network_traffic_live_testing](https://toolsqa.com/gallery/CrossBrowser%20Testing/7%20record_network_traffic_live_testing.png)

可以从测试摘要中进行网络分析。

### 在 CrossBrowserTesting 的实时屏幕上粘贴文本

这是[CrossBrowserTesting](https://crossbrowsertesting.com/)工具面板中的下一个选项。

![粘贴文本图标](https://toolsqa.com/gallery/CrossBrowser%20Testing/9.1%20paste_text_icon.png)

有时用户需要在网站上可用的文本字段中输入一些文本。例如，电子商务网站上的文本字段，用户可以在其中输入他们想要搜索或购买的商品。虽然这可以通过单击来完成，但速度太慢并且会给用户带来不好的体验。这是因为截屏视频占用带宽大，对网络稳定性要求高。CrossBrowserTesting 通过提供将文本粘贴到文本字段的选项解决了这个问题。

我们将在www.amazon.com上测试此选项以提供清晰的画面。

要使用此选项，请在实时测试屏幕上运行www.amazon.com以打开实时测试。

在上一节中讨论的 Record Network 选项旁边，选择Paste Text to Send to your test 选项。它将打开一个弹出窗口。

![paste_text_popup_live_testing](https://toolsqa.com/gallery/CrossBrowser%20Testing/9%20paste_text_popup_live_testing.png)

现在，你一定想知道，如果不是通过单击，那么你将如何将文本粘贴到网站的文本字段中。好吧，要实现这一点，请将光标移至要在其上输入文本的 Amazon 文本字段，然后在弹出窗口中通过键盘键入。

![paste_text_popup_live_testing_success](https://toolsqa.com/gallery/CrossBrowser%20Testing/10%20paste_text_popup_live_testing_success.png)

相同的文本将被复制并粘贴到搜索字段中。这个过程更快更容易。你现在可以像往常一样继续测试。

### 在 CrossBrowserTesting 的实时测试中上传文件

上传文件是你正在浏览或开发的网站的一个非常重要的组成部分。此元素的正确工作应由开发人员(即你)100% 保证。因此，在 CrossBrowserTesting 的实时测试中测试此选项变得非常重要。在远程环境中上传文件很困难。由于CrossBrowserTesting有设备实验室，点击上传不会打开你自己系统的文件夹远程上传。因此，你需要一种方法来从你的网站当前运行的设备本身上传它。让我们看看如何去做。

由于我们需要一个从系统上传的网站，因此对于此选项，我们将使用[ilovepdf](https://www.ilovepdf.com/word_to_pdf)网站。这允许用户上传文档文件并转换为 pdf 格式。

单击工具面板中的上传文件。

![上传文件工具面板](https://toolsqa.com/gallery/CrossBrowser%20Testing/11%20Upload_Files_Tool_Panel.png)

单击此按钮将打开弹出窗口以上传文件。

![1_select_upload_now](https://toolsqa.com/gallery/CrossBrowser%20Testing/12%201_select_upload_now.png)

选择立即上传，将文件从你的系统上传到 SmartBear 系统。选择文件后，它将出现在弹出窗口中。选择上传新文件以上传文件。

![1_file_uploaded](https://toolsqa.com/gallery/CrossBrowser%20Testing/13%201_file_uploaded.png)

上传后你将能够在 SmartBear 服务器上看到该文件。

![File_On_SmartBear_Server](https://toolsqa.com/gallery/CrossBrowser%20Testing/14%20File_On_SmartBear_Server.png)

关闭此框并选择网站上的“选择 Word 文件”。

![选择_word_file_ilovepdf](https://toolsqa.com/gallery/CrossBrowser%20Testing/15%20select_word_file_ilovepdf.png)

该文件夹将像你自己的 Windows 系统一样在 SmartBear 服务器上打开(因为我们选择了 Windows8)。从侧面板转到桌面(突出显示)。你会注意到一个名为uploaded_files的文件夹。选择文件夹并按打开。

![上传文件文件夹](https://toolsqa.com/gallery/CrossBrowser%20Testing/16%20uploaded_files_folder.png)

上传的文件文件夹将打开，你将能够看到所有上传的文件。因为我们只上传了一个文件，所以我们只会看到那个。选择文件并按打开。

![打开上传的文件](https://toolsqa.com/gallery/CrossBrowser%20Testing/17%20open_uploaded_files.png)

该文件将成功上传到网站上，因此测试执行良好。

![file_uploaded_ilovepdf](https://toolsqa.com/gallery/CrossBrowser%20Testing/18%20file_uploaded_ilovepdf.png)

### 在 CrossBrowserTesting 的实时测试中切换开发人员工具

位于 Live Testing 的工具面板中的下一个选项是toggle dev tools。

![toggle_dev_tools_tool_panel](https://toolsqa.com/gallery/CrossBrowser%20Testing/19%20toggle_dev_tools_tool_panel.png)

作为 Web 开发人员，你可能已经熟悉浏览器的开发人员工具，无需介绍。但简而言之，浏览器包含强大的内置工具来测试网站的元素。它有助于检查 HTML 组件、JavaScript 模块等。例如，开发人员工具可帮助开发人员检查其元素的加载方式以及变量在后端的行为方式。既然开发者没有安装浏览器，怎么打开里面的工具呢？因此，为了解决这个问题，CrossBrowserTesting 提供了这个选项。只需点击按钮，浏览器的开发者工具就会打开。

![开发工具](https://toolsqa.com/gallery/CrossBrowser%20Testing/20%20dev_tools.png)

现在开发人员可以像在个人系统上一样进行检查。

### 在 CrossBrowserTesting 中进行实时测试缩放

就计算机而言，缩放意味着增加或减少某物的大小。基本上，用户会尝试更改其原始大小的大小。缩放可以在页面的任何元素或对象上完成。当原始视频在较小的观看区域开始时，它类似于 YouTube 中的缩放以适合选项。缩放选项是开发人员工具选项之后的下一个选项。

![实时测试工具面板 - 缩放最大化](https://toolsqa.com/gallery/CrossBrowser%20Testing/21%20Live%20Testing%20Tool%20Panel%20-%20Scaling%20Maximise.webp)

要查看此设置的工作原理，请按照以下步骤操作。

1.  从实时测试仪表板屏幕中选择你选择的分辨率。我选择了1024x768分辨率。

![选择一个分辨率](https://toolsqa.com/gallery/CrossBrowser%20Testing/22%20choose_a_resolution.png)

1.  在网站上使用相同的选定分辨率运行测试。
2.  你将能够在远程系统上以相同的分辨率查看网站。运行测试时，观察在我的系统上可以看到的屏幕和网站区域。

![home_page_without_scaling](https://toolsqa.com/gallery/CrossBrowser%20Testing/23%20home_page_without_scaling.png)

1.  选择将在远程系统上启用Scale-to-fit设置的 Scaling 选项。

再次观察屏幕以及这次网站的显示方式。

![实时测试工具面板 - 缩放](https://toolsqa.com/gallery/CrossBrowser%20Testing/24.1%20Live%20Testing%20Tool%20Panel%20-%20Scaling.webp)

因此，正如你可能已经猜到的那样，工具面板中的缩放选项有助于缩放以适合远程系统的屏幕。这意味着你实际运行网站的远程系统将与你查看网站的系统相对于你当前的查看区域进行缩放。这种差异可以在上面看到。它将适合比赛并使网站适合你当前的查看屏幕。你可以将其关闭以查看原始缩放比例的网站。

### CrossBrowserTesting 实时测试中的设置

实时测试面板中的设置选项打开实时测试设置面板。这与我们在实时测试设置部分讨论的面板相同。

选择按钮，弹出设置菜单。

![实时测试工具面板 - 设置](https://toolsqa.com/gallery/CrossBrowser%20Testing/25%20Live%20Testing%20Tool%20Panel%20-%20Settings.webp)

按停止停止实时测试会话。工具面板中的所有设置对于测试目的都非常重要。一旦你开始在 CrossBrowserTesting 上测试网站，你就会知道如何有效地使用它们中的每一个。我相信你会对[CrossBrowserTesting](https://crossbrowsertesting.com/)中实时测试的功能感到惊讶。你可能已经注意到，为了提供良好的画面，我们使用了三个不同的网站并对它们进行了实时测试。因此，最好继续在各种网站上尽可能多地使用该软件，以充分了解每个设置以及如何使用它们。