## 一、概述

调试对于开发人员来说可能很耗时。使调试过程更高效的方法之一是使用 JMX（Java 管理扩展），这是一种用于 Java 应用程序的监视和管理技术。

在本教程中，我们将了解如何使用 JMXTerm 对 Java 应用程序执行外部调试。

## 2.JMX术语

JMX 有几个可用于 Java 应用程序的工具，例如[JConsole](https://www.baeldung.com/java-management-extensions#accessing-the-mbean)、[VisualVM](https://www.baeldung.com/visualvm-jmx-remote)和 JMXTerm 。JConsole 是一个用于监控性能的图形工具。VisualVM 提供高级调试和分析功能，需要一个插件才能与 MBean 一起工作。**虽然这些都是有用的工具，但 JMXTerm 是一个轻量级、灵活的命令行选项，可用于[自动化](https://www.baeldung.com/jmx-mbean-shell-access#use-jmxterm-from-a-shell-script)。**

### 2.1. 设置

要使用 JMXTerm，我们首先需要下载并安装它。最新版本的 JMXTerm 可在官方[网站](https://docs.cyclopsgroup.org/jmxterm)上获得。它被打包在一个简单的*jar*文件中：

```bash
$ java -jar jmxterm.jar复制
```

请注意，可以使用更高版本的 Java。**这可能是由于模块化 JDK 中对[反射访问的](https://www.baeldung.com/java-illegal-reflective-access)[限制](https://blogs.oracle.com/javamagazine/post/a-peek-into-java-17-continuing-the-drive-to-encapsulate-the-java-runtime-internals)而发生的。要解决这个问题，我们可以使用\*–add-exports\*：**

```bash
$ java --add-exports jdk.jconsole/sun.tools.jconsole=ALL-UNNAMED -jar jmxterm.jar复制
```

### 2.2. 联系

启动 JMXTerm 后，我们可以使用主机和端口连接到 Java 应用程序。请注意，此选项可能需要[额外的步骤](https://www.baeldung.com/jmx-ports)来配置或查找所需的端口：

```bash
$ open [host]:[port]复制
```

此外，可以在启动时传递地址：

```bash
$ java -jar jmxterm.jar -l [host]:[port]复制
```

或者，我们可以使用 PID 来访问和打开连接。*JMXTerm 允许我们使用jvms*命令直接查找它：

```bash
$ jvms
83049    (m) - com.baeldung.jmxterm.GameRunner
$ open 83049
#Connection to 83049 is opened复制
```

### 2.3. MBean

MBean 是我们可以通过 JMX 管理和监控的 Java 对象。它们公开了一个可以通过[JMX 代理](https://www.baeldung.com/java-management-extensions#jmx-architecture)访问和控制的接口，使诊断和故障排除变得更加容易。

**要创建 MBean，我们应该遵循约定，首先创建一个名称以[MBean 结尾的](https://www.baeldung.com/java-management-extensions#creating-an-mbean-class)接口。**在我们的例子中，它将是*GuessGameMBean。*这意味着该类的名称应该是*GuessGame，*而不是其他名称。或者，在某些情况下，可以使用[MXBeans](https://docs.oracle.com/javase/tutorial/jmx/mbeans/mxbeans.html)。该接口包含我们要公开给 JMX 的操作：

```java
public interface GuessGameMBean {
    void finishGame();
    void pauseGame();
    void unpauseGame();
}复制
```

游戏本身是一个简单的猜数字游戏：

```java
public class GuessGame extends GuessGameMBean {
    //...
    public void start() {
        int randomNumber = randomNumbergenerator.getLastNumber();
        while (!isFinished) {
            waitASecond();
            while (!isPaused && !isFinished) {
                log.info("Current random number is " + randomNumber);
                waitASecond();
                for (Player player : players) {
                    int guess = player.guessNumber();
                    if (guess == randomNumber) {
                        log.info("Players " + player.getName() + " " + guess + " is correct");
                        player.incrementScore();
                        notifyAboutWinner(player);
                        randomNumber = randomNumbergenerator.generateRandomNumber();
                        break;
                    }
                    log.info("Player " + player.getName() + " guessed incorrectly with " + guess);
                }
                log.info("\n");
            }
            if (isPaused) {
                log.info("Game is paused");
            }
            if (isFinished) {
                log.info("Game is finished");
            }
        }
    }
    //...
}复制
```

我们还将通过 JMX 跟踪玩家：

```java
public interface PlayerMBean {
    int getGuess();
    int getScore();
    String getName();
}
复制
```

## 3. 使用 JMXTerm 调试

使用 JMXTerm 连接到 Java 应用程序后，我们可以查询可用域：

```bash
$ domains
#following domains are available
JMImplementation
com.baeldung.jmxterm
com.sun.management
java.lang
java.nio
java.util.logging
jdk.management.jfr复制
```

### 3.1. 记录器级别

让我们尝试更改正在运行的应用程序的日志记录级别。**我们将使用\*java.util.logging.Logger\*进行演示，但请注意 JUL 有[很大的缺点](https://stackoverflow.com/questions/11359187/why-not-use-java-util-logging)。**JUL 提供开箱即用的 MBean：

```bash
$ domain java.util.logging
#domain is set to java.util.logging复制
```

现在我们可以检查域中可用的 MBean：

```bash
$ beans
#domain = java.util.logging:
java.util.logging:type=Logging复制
```

下一步，我们需要检查记录 bean 提供的信息：

```bash
$ bean java.util.logging:type=Logging
#bean is set to java.util.logging:type=Logging
$ info
#mbean = java.util.logging:type=Logging
#class name = sun.management.ManagementFactoryHelper$PlatformLoggingImpl
# attributes
  %0   - LoggerNames ([Ljava.lang.String;, r)
  %1   - ObjectName (javax.management.ObjectName, r)
# operations
  %0   - java.lang.String getLoggerLevel(java.lang.String p0)
  %1   - java.lang.String getParentLoggerName(java.lang.String p0)
  %2   - void setLoggerLevel(java.lang.String p0,java.lang.String p1)
#there's no notifications复制
```

*要访问我们的GuessGame*对象中的记录器，我们需要找到记录器的名称：

```bash
$ get LoggerNames
#mbean = java.util.logging:type=Logging:
LoggerNames = [ ..., com.baeldung.jmxterm.GuessGame, ...];复制
```

最后，检查日志记录级别：

```bash
$ run getLoggerLevel com.baeldung.jmxterm.GuessGame
#calling operation getLoggerLevel of mbean java.util.logging:type=Logging with params [com.baeldung.jmxterm.GuessGame]
#operation returns:
WARNING复制
```

要更改它，我们只需调用带有参数的 setter 方法：

```bash
$ run setLoggerLevel com.baeldung.jmxterm.GuessGame INFO
复制
```

之后，我们可以观察应用程序的日志：

```bash
...
Apr 14, 2023 12:04:30 PM com.baeldung.jmxterm.GuessGame start
INFO: Current random number is 7
Apr 14, 2023 12:04:31 PM com.baeldung.jmxterm.GuessGame start
INFO: Player Bob guessed incorrectly with 10
Apr 14, 2023 12:04:31 PM com.baeldung.jmxterm.GuessGame start
INFO: Player Alice guessed incorrectly with 5
Apr 14, 2023 12:04:31 PM com.baeldung.jmxterm.GuessGame start
INFO: Player John guessed incorrectly with 4
...复制
```

### 3.2. 使用领域 Bean

让我们尝试从我们的应用程序外部停止我们的游戏。这些步骤与记录器示例中的步骤相同：

```bash
$ domain com.baeldung.jmxterm
#domain is set to com.baeldung.jmxterm
$ beans
#domain = com.baeldung.jmxterm:
com.baeldung.jmxterm:id=singlegame,type=game
$ bean com.baeldung.jmxterm:id=singlegame,type=game
#bean is set to com.baeldung.jmxterm:id=singlegame,type=game
$ info
#mbean = com.baeldung.jmxterm:id=singlegame,type=game
#class name = com.baeldung.jmxterm.GuessGame
#there is no attribute
# operations
  %0   - void finishGame()
  %1   - void pauseGame()
  %2   - void unpauseGame()
#there's no notifications
$ run pauseGame
#calling operation pauseGame of mbean com.baeldung.jmxterm:id=singlegame,type=game with params []复制
```

我们应该在输出中看到游戏已暂停：

```bash
...
Apr 14, 2023 12:17:01 PM com.baeldung.jmxterm.GuessGame start
INFO: Game is paused
Apr 14, 2023 12:17:02 PM com.baeldung.jmxterm.GuessGame start
INFO: Game is paused
Apr 14, 2023 12:17:03 PM com.baeldung.jmxterm.GuessGame start
INFO: Game is paused
Apr 14, 2023 12:17:04 PM com.baeldung.jmxterm.GuessGame start
INFO: Game is paused
...复制
```

另外，我们可以完成游戏：

```bash
$ run finishGame复制
```

输出应该包含游戏结束的信息：

```bash
...
Apr 14, 2023 12:17:47 PM com.baeldung.jmxterm.GuessGame start
INFO: Game is finished复制
```

### 3.3. *手表*

*此外，我们可以使用watch*命令跟踪属性的值：

```bash
$ info
# attributes
#mbean = com.baeldung.jmxterm:id=Bobd661ee89-b972-433c-adff-93e7495c7e0a,type=player
#class name = com.baeldung.jmxterm.Player
#there's no operations
#there's no notifications
  %0   - Guess (int, r)
  %1   - Name (java.lang.String, r)
  %2   - Score (int, r)
$ watch Score
#press any key to stop. DO NOT press Ctrl+C !!!
683683683683683683683复制
```

原始*watch*输出很难阅读，但我们可以为其提供一种格式：

```bash
$ watch --format Score\\ {0}\\  Score
#press any key to stop. DO NOT press Ctrl+C !!!
Score 707 Score 707 Score 707 Score 707 Score 707 复制
```

*但是，我们可以通过–report*和*–stopafter*选项进一步改进它：

```bash
$ watch --report --stopafter 10 --format The\\ score\\ is\\ {0} Score
The score is 727
The score is 727
The score is 727
The score is 728
The score is 728复制
```

### 3.4. 通知

另一个重要的调试功能是 MBean 通知。但是，这需要对我们的代码进行最少的更改。首先，我们必须实现*javax.management.NotificationBroadcaster*接口：

```java
public interface NotificationBroadcaster {
    void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
      throws java.lang.IllegalArgumentException;
    void removeNotificationListener(NotificationListener listener)
      throws ListenerNotFoundException;
    MBeanNotificationInfo[] getNotificationInfo();
}复制
```

要发送有关获胜者的通知，我们将使用*javax.management.NotificationBroadcasterSupport*：

```java
public abstract class BroadcastingGuessGame implements NotificationBroadcaster, GuessGameMBean {
    private NotificationBroadcasterSupport broadcaster =
      new NotificationBroadcasterSupport();

    private long notificationSequence = 0;

    private MBeanNotificationInfo[] notificationInfo;

    public BroadcastingGuessGame() {
        this.notificationInfo = new MBeanNotificationInfo[]{ 
            new MBeanNotificationInfo(new String[]{"game"}, Notification.class.getName(),"Game notification") 
        };
    }

    protected void notifyAboutWinner(Player winner) {
        String message = "Winner is " + winner.getName() + " with score " + winner.getScore();
        Notification notification = new Notification("game.winner", this, notificationSequence++, message);
        broadcaster.sendNotification(notification);
    }

    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) {
        broadcaster.addNotificationListener(listener, filter, handback);
    }

    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        broadcaster.removeNotificationListener(listener);
    }

    public MBeanNotificationInfo[] getNotificationInfo() {
        return notificationInfo;
    }
}
复制
```

之后，我们可以在 bean 上看到通知：

```bash
$ bean com.baeldung.jmxterm:id=singlegame,type=game
#bean is set to com.baeldung.jmxterm:id=singlegame,type=game
$ info
#mbean = com.baeldung.jmxterm:id=singlegame,type=game
#class name = com.baeldung.jmxterm.GuessGame
#there is no attribute
# operations
  %0   - void finishGame()
  %1   - void pauseGame()
  %2   - void unpauseGame()
# notifications
  %0   - javax.management.Notification(game.winner)
复制
```

随后，我们可以订阅通知：

```bash
$ subscribe
#Subscribed to com.baeldung.jmxterm:id=singlegame,type=game
notification received: ...,message=Winner is John with score 10
notification received: ...,message=Winner is Alice with score 9
notification received: ...,message=Winner is Bob with score 13
notification received: ...,message=Winner is Bob with score 14
notification received: ...,message=Winner is John with score 11
复制
```

通过提供 *–domain* 和 –bean*选项* ，我们可以订阅多个 bean。

## 4。结论

JMXTerm 是一个强大而灵活的工具，用于通过 JMX 管理和监视 Java 应用程序。通过为 JMX 操作提供命令行界面，JMXTerm 允许开发人员和管理员快速轻松地执行监控属性值、调用操作和更改配置设置等任务。