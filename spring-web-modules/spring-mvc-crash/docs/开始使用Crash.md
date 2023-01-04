## 1. 简介

Crash 是一个可重用的shell，它部署在JVM中并帮助我们与JVM进行交互。

在本教程中，我们将了解如何将Crash安装为独立应用程序。此外，我们将嵌入Spring Web应用程序并创建一些自定义命令。

## 2. 单机安装

让我们通过从[Crash的官方网站](https://www.crashub.org/)下载发行版来将Crash安装为独立的应用程序。

Crash目录结构包含三个重要目录cmd、bin、conf：

<img src="../assets/img.png">

bin目录包含用于启动Crash的独立CLI脚本。 

cmd目录包含它支持的所有开箱即用的命令。此外，这是我们可以放置自定义命令的地方。我们将在本文的后面部分对此进行研究。

要启动CLI，我们转到bin并使用crash.bat或crash.sh启动独立实例：

<img src="../assets/img_1.png">

## 3. 在Spring Web应用程序中嵌入Crash

让我们将Crash嵌入到Spring Web应用程序中。首先，我们需要一些依赖：

```xml
<dependency>
    <groupId>org.crashub</groupId>
    <artifactId>crash.embed.spring</artifactId>
    <version>1.3.2</version>
</dependency>
<dependency>
    <groupId>org.crashub</groupId>
    <artifactId>crash.cli</artifactId>
    <version>1.3.2</version>
</dependency>
<dependency>
    <groupId>org.crashub</groupId>
    <artifactId>crash.connectors.telnet</artifactId>
    <version>1.3.2</version>
</dependency>
```

我们可以在[Maven Central](https://search.maven.org/search?q=g:org.crashub)查看最新版本。

Crash同时支持Java和Groovy，因此我们需要添加Groovy以使Groovy脚本工作：

```xml
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy</artifactId>
    <version>3.0.0-rc-3</version>
</dependency>
```

它的最新版本也在[Maven Central](https://search.maven.org/search?q=groovy)中。

接下来，我们需要在我们的web.xml 中添加一个监听器：

```xml
<listener>
    <listener-class>org.crsh.plugin.WebPluginLifeCycle</listener-class>
</listener>
```

现在侦听器准备就绪，让我们在WEB-INF目录中添加属性和命令。我们将创建一个名为crash的目录，并将命令和属性放入其中：

<img src="../assets/img_2.png">

部署应用程序后，我们可以通过telnet连接到shell：

```bash
telnet localhost 5000
```

我们可以 使用crash.telnet.port属性更改crash.properties文件中的telnet端口。

或者，我们也可以创建一个Spring bean来配置属性并覆盖命令的目录位置：

```xml
<bean class="org.crsh.spring.SpringWebBootstrap">
    <property name="cmdMountPointConfig" value="war:/WEB-INF/crash/commands/" />
    <property name="confMountPointConfig" value="war:/WEB-INF/crash/" />
    <property name="config">
        <props>
             <prop key="crash.telnet.port">5000</prop>
         </props>
     </property>
</bean>
```

## 4. Crash和Spring Boot

Spring Boot曾经通过其远程shell将Crash作为嵌入式销售提供：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-remote-shell</artifactId>
</dependency>
```

不幸的是，该支持现已弃用。如果我们仍然想将shell与Spring Boot应用程序一起使用，我们可以使用附加模式。在附加模式下，Crash挂钩到Spring Boot应用程序的JVM而不是它自己的：

```bash
crash.sh <PID>
```

此处，<PID\>是该JVM实例的进程ID，我们可以使用jps命令检索在主机上运行的JVM的进程ID。

## 5. 创建自定义命令

现在，让我们为崩溃shell创建一个自定义命令。我们可以通过两种方式创建和使用命令；一个使用Groovy，还有一个使用Java。我们将一一研究。

### 5.1 使用Groovy命令

首先，让我们使用Groovy创建一个简单的命令：

```groovy
class message {
	
    @Usage("show my own message")
    @Command
    Object main(@Usage("custom message") @Option(names=["m","message"]) String message) {
        if (message == null) {
            message = "No message given...";
        }
        return message;
    }
}
```

@Command注解将方法标记为命令，@Usage用于显示命令的用法和参数，最后，@Option用于将任何参数传递给命令。

让我们测试命令：

<img src="../assets/img_3.png">

### 5.2 使用Java命令

让我们用Java创建相同的命令：

```java
public class message2 extends BaseCommand {
    @Usage("show my own message using java")
    @Command
    public Object main(@Usage("custom message")
                       @Option(names = { "m", "message" }) String message) {
        if (message == null) {
            message = "No message given...";
        }
        return message;
    }
}
```

该命令类似于Groovy，但这里我们需要扩展org.crsh.command.BaseCommand。

那么，让我们再次测试：

<img src="../assets/img_4.png">

## 6. 总结

在本教程中，我们着眼于将Crash安装为独立应用程序，并将其嵌入到Spring Web应用程序中。此外，我们还使用Groovy和Java创建了自定义命令。