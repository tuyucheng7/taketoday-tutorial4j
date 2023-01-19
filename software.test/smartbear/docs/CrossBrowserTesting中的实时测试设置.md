在上一篇关于[CrossBrowserTesting 中的实时测试的文章中](https://www.toolsqa.com/cross-browser-testing/live-testing-on-crossbrowsertesting/)，我们在网站上执行了实时测试，并且第一次体验了在远程系统上查看网站。在不安装我们任何个人系统的情况下在数千个浏览器上查看和测试网站真是太神奇了。[实时测试是SmartBear 的 CrossBrowserTesting](https://crossbrowsertesting.com/)最强大的功能之一，仅在远程屏幕上查看网站是不够的。CrossBrowserTesting 中的实时测试加载了跨浏览器测试行业的顶级功能，本教程专门介绍 CrossBrowserTesting 中的实时测试设置。这些功能是你在互联网上可能遇到的任何其他平台无法比拟的。

在本教程中，我们将探索通过实时测试提供的高级工具和选项，以实现更好、更高效的测试。本教程分为两部分：

-   CrossBrowserTesting 中的实时测试设置。
-   SmartBear CrossBrowserTesting 实时测试中的高级选项。

让我们一一查看每个设置和工具。

## CrossBrowserTesting 中的实时测试设置

Live Testing 部分中的 Live Test 设置为用户提供了不同的选项和首选项，以便用户根据它们执行 Live Testing。可以从实时测试部分中的运行测试按钮上方访问实时测试设置。

![实时测试设置](https://toolsqa.com/gallery/CrossBrowser%20Testing/1%20Live%20Test%20Settings.webp)

单击下拉菜单，你将能够看到此设置中可用的不同选项和首选项。![基本 - CrossBrowserTesting 中的实时测试设置](https://toolsqa.com/gallery/CrossBrowser%20Testing/2%20Basic%20-%20Live%20Test%20Settings%20in%20CrossBrowserTesting.webp)

### 1. 在线测试设置中的客户端设置

客户端是指用于在浏览器上测试网站的客户端-服务器流技术。它包含两个选项，即 WebRTC 和 HTML5。

1.  WebRTC： WebRTC 或Web 实时通信是一项新技术，由于减少了延迟并提高了准确性和效率，现在已经流行了几年。顾名思义，它用于实时通信，这些类型的通信通常包括视频、图像等。因此，如果你使用 WebRTC 测试你的网站，你会注意到服务器的响应速度更快，滞后现象减少在你发送的命令和你看到的输出之间，就像滚动网站的网页一样。它还会影响用户在测试时在远程系统上播放视频的帧速率。
2.  HTML5：这是我们今天在大多数流媒体设施(例如流媒体视频)中使用的常规技术。实际上，WebRTC 使用 HTML API 只是为了提供实时通信，但在它上面有更多的代码来提高性能。因此，与 webRTC 相比，此选项将提供低质量的体验。在 CrossBrowserTesting 中，当你在实时设置中选择 HTML5 时，你将获得另一个首选项，如图所示。

![1_speed_quality_preference_lise_test_setting](https://toolsqa.com/gallery/CrossBrowser%20Testing/3%201_speed_quality_preference_lise_test_setting.png)

这是速度与质量的权衡。由于 HTML5 技术在向客户端提供转换服务方面效率不高，因此用户可以选择在速度或质量上做出妥协以进行测试。因此，如果用户选择“速度”，则质量会很差，反之亦然。

### 2. 在线测试设置中的超时设置

超时是用户选择实时测试将自动关闭的时间的首选项。在给定的选项中，用户可以为会话超时选择 5 分钟、10 分钟或 15 分钟。

### 3.实时测试设置中的反馈弹出窗口

顾名思义，反馈弹窗是在用户关闭实时测试窗口或超时时出现的弹窗。它用于 SmartBear 的软件改进。所以如果你打开它，每次测试完成时你都会收到弹出窗口。可以在[实时测试介绍](https://www.toolsqa.com/cross-browser-testing/live-testing-on-crossbrowsertesting/)帖子中看到反馈弹出窗口。

### 4. 现场测试设置中的高速网络设置

打开高速网络通过Akamai 技术网络重定向流量，该网络更稳定，并在实时测试中提供更好的质量性能。出于更好的测试目的，强烈建议使用此功能，但由于此技术的工作方式类似于代理服务器，因此防火墙限制可能不允许连接。

这些选项是用户倾向的，用户可能希望也可能不想根据它们更改这些设置。为了方便用户，最好将它们显示在屏幕上。用户在执行实时测试时遇到的下一个设置是高级选项。

## CrossBrowserTesting 实时测试中的高级选项

Live Testing 中的高级选项包含更多设置，以方便用户。通过使用实时测试中的高级选项，用户可以使用 CrossBrowserTesting 中可用的其他功能，例如在执行测试时录制视频等。高级选项选项卡位于地址栏旁边。

![Advance - CrossBrowserTesting 中的实时测试设置](https://toolsqa.com/gallery/CrossBrowser%20Testing/4%20Advance%20-%20Live%20Test%20Settings%20in%20CrossBrowserTesting.png)

如上图所示，实时测试中的高级选项包含三个额外的切换选项。

### 1.录制视频

录制视频选项将完整的测试体验记录在视频中，测试完成后在测试结果中提供给用户。要了解测试结果，你可以访问此[链接](https://www.toolsqa.com/cross-browser-testing/live-testing-on-crossbrowsertesting/)。当我们运行测试时，此选项也可在实时屏幕上可用的工具面板中使用。稍后将讨论。需要注意的是，视频最多会录制 10 分钟。

![1_recorded_video_test_summary](https://toolsqa.com/gallery/CrossBrowser%20Testing/5%201_recorded_video_test_summary.png)

### 2.记录网络

记录网络选项记录在整个测试会话期间遇到的网络流量。会话结束后，可以在测试结果中查看网络流量。与录制视频选项类似，当你运行实时测试时，此选项在实时屏幕面板上也可用。

![track_network_live_testing](https://toolsqa.com/gallery/CrossBrowser%20Testing/6%20track_network_live_testing.png)

### 3.指定本地连接

此选项将提示你输入必须运行测试的本地连接。默认情况下，该选项会选择最近的设置，一般不需要用户手动输入。但是，如果用户正在运行多个本地连接，则有必要指定它。

在用户运行测试之前，这些是[CrossBrowserTesting](https://crossbrowsertesting.com/)中可用的一些方便的功能和选项。测试开始后，实时屏幕上还有一些可用选项。用户运行测试后，他们将在屏幕上看到一个工具面板，其中包含非常有用且必不可少的工具。我相信你不会在其他跨浏览器测试平台上找到此类工具。工具面板中每个选项的详细信息将在下一个教程中讨论。