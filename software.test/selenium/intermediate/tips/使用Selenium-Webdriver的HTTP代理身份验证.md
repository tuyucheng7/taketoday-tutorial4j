让我们花几分钟来了解什么是Proxy ，以及Socks Proxy 和HTTP Proxy之间的区别。SOCKS使用握手协议通知代理软件有关客户端正在尝试建立的连接，并可用于任何形式的 TCP 或 UDP 套接字连接，而HTTP代理分析通过它发送的 HTTP 标头以推断出服务器的地址，因此只能用于 HTTP 流量。以下示例演示了 SOCKS 和 HTTP 代理协议之间的区别：

## 袜子

Lakshay 希望通过互联网与 Viru 通信，但他们之间的网络上存在防火墙，Lakshay 无权通过它自己进行通信。因此，他连接到他网络上的 SOCKS 代理，并将有关他希望建立的连接的信息发送给 Viru。SOCKS 代理通过防火墙打开连接并促进 Lakshay 和 Viru 之间的通信。

## HTTP

Lakshay 希望从运行Web 服务器的 Viru 下载网页。Lakshay 无法直接连接到 Viru 的服务器，因为他的网络上安装了防火墙。为了与服务器通信，Lakshay 连接到他的网络的 HTTP 代理。他的互联网浏览器以与目标服务器完全相同的方式与代理通信——它发送标准的 HTTP 请求标头。HTTP 代理读取请求并查找主机标头。然后它连接到标头中指定的服务器，并将服务器回复的任何数据传输回 Lakshay。

我解释这个是因为大多数人和我一样对上述术语感到困惑。直到并且除非你不知道你获得的是哪种身份验证，否则很难找到正确的解决方案。

我的分析： 我可能正确也可能不正确，所以如果你不同意我的观点，请原谅我，因为我不是那么专业。要处理 Socks 代理，我们需要提供我们用于登录 Windows 系统的系统凭据，要处理 HTTP 代理，我们需要提供已在服务器端设置的站点凭据。

# 如何使用用户名和密码设置 HTTP 代理

让我们看看身份验证窗口在不同浏览器中的外观：

Firefox 浏览器身份验证窗口

![火狐认证](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Firefox%20Auth.png)

Internet Explorer 浏览器身份验证窗口

![IE认证](https://www.toolsqa.com/gallery/selnium%20webdriver/2.IE%20Authentication.png)

![Chrome 身份验证](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Chrome%20Authentication.png)

Chrome 浏览器身份验证窗口

我的想法是，如果我们想要处理HTTP 代理身份验证，我们可以简单地通过URL发送用户名和密码，并且在大多数情况下它工作得很好。

```
driver.get("https://UserName:Password@Example.com");
```

如果上述代码不起作用，则可能是代理设置问题，可以手动处理。请参阅以下部分。

## Firefox 浏览器配置文件设置

在更改默认的 Firefox 配置文件设置之前，始终建议为自动化测试创建一个单独的配置文件。请阅读[Selenium 的自定义 Firefox 配置文件](https://toolsqa.com/selenium-webdriver/custom-firefox-profile/)部分， 以更多地了解我们为什么需要它、如何创建它以及如何使用它。

1.  在浏览器中输入“about:config”，然后按 Enter。

![Auth-4](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Auth-4.png)

1.  按下 Enter 的那一刻，你将收到以下警告消息。点击“我会小心的，我答应过的！” 并进一步进行。

![Auth-5](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Auth-5.png)

1.  现在在“搜索”字段中键入“browser.safebrowsing.malware.enabled”并通过双击将值设置为“true” 。

![Auth-6](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Auth-6.png)

1.  从现在开始，Firefox 应该允许你使用 Url 中的用户和密码通过 HTTP 身份验证。现在你需要做的就是将新创建的自定义 Firefox 配置文件 ( profileToolsQA ) 实例化到你的 Selenium 测试脚本中。

```java
ProfilesIni profile = new ProfilesIni();

FirefoxProfile myprofile = profile.getProfile("profileToolsQA");

WebDriver driver = new FirefoxDriver(myprofile);
```

注意： 如果你的身份验证服务器需要带有“ domainuser ”之类域的用户名，你需要在 Url 中添加双斜杠“”。

```java
http://localdomain\user:password@example.com
```

如果问题仍未解决，请通过以下SOCKS 代理部分以其他方式解决。这可以在不提示任何身份验证的情况下正常工作。

## Internet Explorer 浏览器设置

1.  按“+ R” 或单击 Windows 开始菜单(左下按钮)，然后选择 “运行”。

![FF-配置文件-2](https://www.toolsqa.com/gallery/selnium%20webdriver/7.FF-Profile-2.png)

1.  在“ 运行” 对话框中，键入：“regedit.exe” ，然后单击 “确定”。
2.  现在导航到“ HKEY_LOCAL_MACHINE Software Microsoft Internet Explorer Main FeatureControl FEATURE_HTTP_USERNAME_PASSWORD_DISABLE ”。
3.  在右侧，执行Right click 并选择New > DWORD VALUE并将其命名为'iexplorer.exe'。

![Auth-7](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Auth-7.png)

1.  现在将'iexplorer.exe'的值修改为'0'。

![Auth-8](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Auth-8.png)

![Auth-9](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Auth-9.png)

重新启动浏览器，该功能现在应该可用。

注意：如果你的身份验证服务器需要带有“ domainuser ”之类域的用户名，你需要将“%5C” 符号添加到 Url。

```java
http://domain%5Cuser:password@example.com
```

如果问题仍未解决，请通过以下部分以其他方式解决。这可以在不提示任何身份验证的情况下正常工作。

## 如何使用用户名和密码设置 SOCKS 代理

## Firefox 浏览器配置文件设置

在更改默认的 Firefox 配置文件设置之前，始终建议为自动化测试创建一个单独的配置文件。请阅读 [Selenium 的自定义 Firefox 配置文件](https://toolsqa.com/selenium-webdriver/custom-firefox-profile/)部分， 以更多地了解我们为什么需要它、如何创建它以及如何使用它。

1.  在浏览器中输入“about:config”，然后按 Enter。

![Auth-4](https://www.toolsqa.com/gallery/selnium%20webdriver/11.Auth-4.png)

1.  按下Enter的那一刻，你将收到以下警告消息。点击“我会小心的，我答应过的！” 并进一步进行。

![Auth-5](https://www.toolsqa.com/gallery/selnium%20webdriver/12.Auth-5.png)

1.  现在在“ 搜索” 字段中 键入“signon.autologin.proxy”并通过双击将值设置 为“true” 。

![Auth-10](https://www.toolsqa.com/gallery/selnium%20webdriver/13.Auth-10.png)

4) 现在你需要做的就是将新创建的自定义Firefox 配置文件 ( profileToolsQA ) 实例化到你的 Selenium 测试脚本中。

```java
ProfilesIni profile = new ProfilesIni();

FirefoxProfile myprofile = profile.getProfile("profileToolsQA");

WebDriver driver = new FirefoxDriver(myprofile);
```



这个问题可以借助 AutoIt 工具来解决。AutoIt 是用于桌面自动化的第三方工具。请访问[Selenium Webdriver 中 AutoIt 的使用。](https://toolsqa.com/selenium-webdriver/autoit-selenium-webdriver/)