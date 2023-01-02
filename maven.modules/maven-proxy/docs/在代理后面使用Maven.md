## 1. 概述

在本教程中，我们将**配置**[Maven](https://www.baeldung.com/maven)**以在代理后面工作**，这是我们不直接连接到Internet的环境中的常见情况。

在我们的示例中，我们的代理在机器“proxy.tuyucheng.com”上运行，它通过端口“80”上的HTTP监听代理请求。我们还将在internal.tuyucheng.com使用一些不需要通过代理的内部站点。

## 2. 代理配置

首先，**让我们设置一个没有任何凭据的基本代理配置**。

让我们编辑通常位于我们的“<user_home>/.m2′目录中的Maven [settings.xml](https://maven.apache.org/ref/3.6.3/maven-settings/settings.html)，如果那里没有，那么我们可以从“<maven_home>/conf”目录的全局设置中复制它。

现在让我们在\<[proxies](https://maven.apache.org/settings.html#Proxies)>部分中创建一个<proxy\>条目：

```xml
<proxies>
    <proxy>
        <host>proxy.tuyucheng.com</host>
        <port>80</port>
    </proxy>
</proxies>
```

由于我们还使用不需要通过代理的本地站点，因此让我们在<nonProxyHosts\>中使用“|”指定它与我们的本地主机分开的列表：

```xml
<nonProxyHosts>internal.tuyucheng.com|localhost|127.*|[::1]</nonProxyHosts>
```

## 3. 添加凭据

如果我们的代理不安全，这就是我们所需要的；然而，我们的是，因此**让我们将我们的凭据添加到代理定义中**：

```xml
<username>tuyucheng</username>
<password>changeme</password>
```

**如果我们不需要username/password条目(即使是空条目)，我们不会添加它们**，因为当代理不需要它们时让它们出现会导致我们的请求被拒绝。

我们的最小身份验证配置现在应该如下所示：

```xml
<proxies>
    <proxy>
        <host>proxy.tuyucheng.com</host>
        <port>80</port>
        <username>tuyucheng</username>
        <password>changeme</password>
        <nonProxyHosts>internal.tuyucheng.com|localhost|127.*|[::1]</nonProxyHosts>
    </proxy>
</proxies>
```

现在，当我们运行mvn命令时，我们将通过代理连接到我们想要的站点。

### 3.1 可选条目

让我们给它一个可选的id 'TuyuchengProxy_Authenticated'以使其更容易引用，以防我们需要切换代理：

```xml
<id>TuyuchengProxy_Authenticated</id>
```

现在，如果我们有另一个代理，我们可以添加另一个代理定义，但只有一个可以处于激活状态。**默认情况下，Maven将使用它找到的第一个激活的代理定义**。

默认情况下，代理定义处于激活状态，并获取隐式定义：

```xml
<active>true</active>
```

如果我们想让另一个代理成为激活的代理，那么我们将通过将<active\>设置为false来停用我们的原始条目：

```xml
<active>false</active>
```

**Maven对代理协议的默认值为HTTP**，适用于大多数情况。如果我们的代理使用不同的协议，我们会在这里声明它并用我们的代理需要的协议替换http：

```xml
<protocol>http</protocol>
```

请注意，这是代理使用的协议-我们请求的协议(ftp://、http://、https://)与此无关。

**这是我们扩展的代理定义的样子**，包括可选元素：

```xml
<proxies>
    <proxy>
        <id>TuyuchengProxy_Authenticated</id>
        <active>true</active>
        <protocol>http</protocol>
        <host>proxy.tuyucheng.com</host>
        <port>80</port>
        <username>tuyucheng</username>
        <password>changeme</password>
        <nonProxyHosts>internal.tuyucheng.com|localhost|127.*|[::1]</nonProxyHosts>
    </proxy>
</proxies>
```

所以，这就是我们的基本代理条目，但它对我们来说足够安全吗？

## 4. 保护我们的配置

现在，假设我们的一位同事希望我们向他们发送代理配置。

我们不太热衷于以纯文本形式发送我们的密码，所以让我们看看Maven如何轻松地[加密我们的密码](https://maven.apache.org/guides/mini/guide-encryption.html)。

### 4.1 创建主密码

首先，让我们选择一个主密码，比如“te!st!ma$ter”。

现在**让我们加密我们的主密码**，在运行时在提示符下输入它：

```bash
mvn --encrypt-master-password
Master Password:
```

按下回车键后，我们会看到我们的加密密码包含在花括号中：

```bash
{QFMlh/6WjF8H9po9UDo0Nv18e527jqWb6mUgIB798n4=}
```

### 4.2 密码生成疑难解答

如果我们看到{}而不是Master Password:提示符(使用bash时可能会发生这种情况)，那么我们需要在命令行上指定密码。

**让我们将密码用引号括起来，以确保任何特殊字符(如“!“)都不会产生不必要的影响**。

因此，如果我们使用bash，让我们使用单引号：

```bash
mvn --encrypt-master-password 'te!st!ma$ter'
```

或者，如果使用Windows命令提示符，则使用双引号：

```bash
mvn --encrypt-master-password "te!st!ma$ter"
```

现在，**有时我们生成的主密码包含花括号**，就像这个例子，在“UD”之后有一个右花括号“}”：

```bash
{QFMlh/6WjF8H9po9UD}0Nv18e527jqWb6mUgIB798n4=}
```

在这种情况下，我们可以：

-   再次运行mvn-encrypt-master-password命令生成另一个密码(希望没有大括号)
-   通过在“{”或“}”前面添加反斜杠来转义我们密码中的花括号

### 4.3 创建一个settings-security.xml文件

现在让我们将带有转义'}'的加密密码**放入.m2目录中名为settings-security.xml的文件中**：

```xml
<settingsSecurity>
    <master>{QFMlh/6WjF8H9po9UD}0Nv18e527jqWb6mUgIB798n4=}</master>
</settingsSecurity>
```

最后，Maven允许我们在<master\>元素中添加注释。

让我们在密码“{”分隔符之前添加一些文本，注意不要在我们的注释中使用”{“或”}“，因为Maven使用它们来查找我们的密码：

```xml
<master>We escaped the curly brace with '\' {QFMlh/6WjF8H9po9UD\}0Nv18e527jqWb6mUgIB798n4=}</master>
```

### 4.4 使用可移动驱动器

假设我们需要额外的安全性并**希望将我们的主密码存储在单独的设备上**。

首先，我们将settings-security.xml文件放在可移动驱动器“R:”的配置目录中：

```bash
R:\config\settings-security.xml
```

现在，我们将更新.m2目录中的settings-security.xml文件，以将Maven重定向到可移动驱动器上的真实settings-security.xml：

```xml
<settingsSecurity>
    <relocation>R:\config\settings-security.xml</relocation>
</settingsSecurity>
```

**Maven现在将从我们在可移动驱动器上的重定位元素中指定的文件中读取我们的加密主密码**。

## 5. 加密代理密码

现在我们有了一个加密的主密码，我们可以**加密我们的代理密码**了。

让我们运行以下命令并在提示符处输入我们的密码“changeme”：

```bash
mvn --encrypt-password
Password:
```

显示我们的加密密码：

```bash
{U2iMf+7aJXQHRquuQq6MX+n7GOeh97zB9/4e7kkEQYs=}
```

我们的最后一步是**编辑settings.xml文件中的代理部分，并输入我们的加密密码**：

```xml
<proxies>
    <proxy>
        <id>TuyuchengProxy_Encrypted</id>
        <host>proxy.tuyucheng.com</host>
        <port>80</port>
        <username>tuyucheng</username>
        <password>{U2iMf+7aJXQHRquuQq6MX+n7GOeh97zB9/4e7kkEQYs=}</password>
    </proxy>
</proxies>
```

保存此内容，Maven现在应该能够使用我们的加密密码通过我们的代理连接到互联网。

## 6. 使用系统属性

虽然**通过settings文件配置Maven是推荐的方法**，但我们可以[通过Java系统属性声明我们的代理配置](https://docs.oracle.com/javase/8/docs/technotes/guides/net/proxies.html)。

如果我们的操作系统已经配置了代理，我们可以设置：

```bash
-Djava.net.useSystemProxies=true
```

或者，为了始终启用它，如果我们具有管理员权限，我们可以在<JRE\>/lib/net.properties文件中进行设置。

但是，请注意，虽然Maven本身可能会遵守此设置，但并非所有插件都遵守此设置，因此使用此方法我们仍然可能会获得失败的连接。

即使启用，我们也可以**通过在http.proxyHost系统属性上设置HTTP代理的详细信息来覆盖它**：

```bash
-Dhttp.proxyHost=proxy.tuyucheng.com
```

我们的代理在默认端口80上监听，但如果它在端口8080上监听，我们将**配置http.proxyPort属性**：

```bash
-Dhttp.proxyPort=8080
```

**对于我们不需要代理的网站**：

```bash
-Dhttp.nonLocalHosts="internal.tuyucheng.com|localhost|127.*|[::1]"
```

因此，如果我们的代理位于10.10.0.100，我们可以使用：

```bash
mvn compile -Dhttp.proxyHost=10.10.0.100 -Dhttp.proxyPort=8080 -Dhttp.nonProxyHosts=localhost|127.0.0.1
```

当然，**如果我们的代理需要身份验证，我们还将添加**：

```bash
-Dhttp.proxyUser=tuyucheng
-Dhttp.proxyPassword=changeme
```

如果我们希望其中一些设置应用于我们所有的Maven调用，我们可以在MAVEN_OPTS环境变量中定义它们：

```bash
set MAVEN_OPTS= -Dhttp.proxyHost=10.10.0.100 -Dhttp.proxyPort=8080
```

现在，无论何时我们运行“mvn”，这些设置都会自动应用-直到我们退出。

## 7. 总结

在本文中，我们使用和不使用凭据配置了一个Maven代理，并加密了我们的密码。我们了解了如何将主密码存储在外部驱动器上，还了解了如何使用系统属性配置代理。

现在我们可以与我们的同事共享我们的settings.xml文件，而无需以纯文本形式向他们提供我们的密码，并向他们展示如何加密他们的密码！