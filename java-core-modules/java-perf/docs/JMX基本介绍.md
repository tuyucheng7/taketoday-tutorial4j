## 1. 概述

Java Management Extensions (JMX) 框架是在Java1.5 中引入的，自一开始就得到了Java开发人员社区的广泛认可。

它提供了一个易于配置、可扩展、可靠且或多或少友好的基础架构，用于在本地或远程管理Java应用程序。该框架引入了用于实时管理应用程序的 MBean 的概念。

本文是初学者的分步指南，用于创建和设置基本 MBean 并通过 JConsole 管理它。

## 2.JMX架构

JMX 体系结构遵循三层方法：

1.  仪表层：向 JMX 代理注册的 MBean，通过它管理资源
2.  JMX 代理层：核心组件 (MbeanServer)，维护托管 MBean 的注册表并提供访问它们的接口
3.  远程管理层：通常是客户端工具，如 JConsole

## 3.创建 MBean 类

在创建 MBean 时，我们必须遵守一个特定的设计模式。模型 MBean 类必须实现具有以下名称的接口：“模型类名称”加上 MBean。

因此，让我们定义我们的 MBean 接口和实现它的类：

```java
public interface GameMBean {

    public void playFootball(String clubName);

    public String getPlayerName();

    public void setPlayerName(String playerName);

}
public class Game implements GameMBean {

    private String playerName;

    @Override
    public void playFootball(String clubName) {
        System.out.println(
          this.playerName + " playing football for " + clubName);
    }

    @Override
    public String getPlayerName() {
        System.out.println("Return playerName " + this.playerName);
        return playerName;
    }

    @Override
    public void setPlayerName(String playerName) {
        System.out.println("Set playerName to value " + playerName);
        this.playerName = playerName;
    }
}
```

Game类覆盖了父接口的playFootball ()方法。除此之外，该类还有一个成员变量playerName和它的 getter/setter。

请注意，getter/setter 也在父接口中声明。

## 4. 使用 JMX 代理进行检测

JMX 代理是本地或远程运行的实体，它们提供对向其注册的 MBean 的管理访问。

让我们使用PlatformMbeanServer ——JMX 代理的核心组件并向其注册Game MBean。

我们将使用另一个实体——ObjectName——向PlatformMbeanServer注册Game类实例；这是一个由两部分组成的字符串：

-   domain：可以是任意字符串，但根据 MBean 命名约定，它应该具有Java包名称(避免命名冲突)
-   key：由逗号分隔的“ key=value ”对列表

在这个例子中，我们将使用：“com.baledung.tutorial:type=basic,name=game”。


我们将从工厂类 java.lang.management.ManagementFactory 中获取MBeanServer 。

然后我们将使用创建的 ObjectName 注册模型 MBean ：

```java
try {
    ObjectName objectName = new ObjectName("com.baeldung.tutorial:type=basic,name=game");
    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
    server.registerMBean(new Game(), objectName);
} catch (MalformedObjectNameException | InstanceAlreadyExistsException |
        MBeanRegistrationException | NotCompliantMBeanException e) {
    // handle exceptions
}
```

最后，为了能够对其进行测试——我们将添加一个while循环以防止应用程序在我们可以通过 JConsole 访问 MBean 之前终止：

```java
while (true) {
}
```

## 5. 访问 MBean

### 5.1. 从客户端连接

1.  在 Eclipse 中启动应用程序
2.  启动Jconsole(位于本机JDK安装目录bin文件夹下)
3.  Connection -> new Connection -> 选择本教程的本地Java进程 -> Connect -> Insecure SSl connection warning -> Continue with insecure connection
4.  建立连接后，单击“查看”窗格右上角的“MBean”选项卡
5.  已注册的 MBean 列表将显示在左栏中
6.  点击 com.baeldung.tutorial -> basic -> game
7.  game下面会有两行，属性和操作各一行

下面是该过程的 JConsole 部分的快速浏览：

[![编辑的 jmx 教程](https://www.baeldung.com/wp-content/uploads/2016/12/edited_jmx_tutorial-1024x575.gif)](https://www.baeldung.com/wp-content/uploads/2016/12/edited_jmx_tutorial.gif)

### 5.2. 管理 MBean

MBean 管理的基础很简单：

-   属性可以读或写
-   可以调用方法并向它们提供参数或从它们返回值

让我们看看这对Game MBean 在实践中意味着什么：

-   attribute :为playerName属性键入一个新值——例如“Messi”，然后单击Refresh 按钮
    

以下日志将出现在 Eclipse 控制台中：

将 playerName 设置为值 Messi

-   操作：为方法playFootBall()的字符串参数键入一个值——例如“巴塞罗那”，然后单击方法按钮。将出现成功调用的窗口警报

eclipse控制台中会出现如下日志：

梅西为巴萨踢球

## 六，总结

本教程介绍了使用 MBean 设置支持 JMX 的应用程序的基础知识。此外，它还讨论了如何使用典型的客户端工具(如 JConsole)来管理经过检测的 MBean。

JMX 技术的领域在范围和范围上都非常广泛。本教程可以被视为初学者迈向该目标的一步。