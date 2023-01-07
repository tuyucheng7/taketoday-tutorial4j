## 1. 概述

在本文中，我们将探索Java NIO.2 文件系统 API 的WatchService接口。这是Java7 中与FileVisitor接口一起引入的较新 IO API 中鲜为人知的特性之一。

要在你的应用程序中使用WatchService接口，你需要导入适当的类：

```java
import java.nio.file.;
```

## 2. 为什么要使用WatchService

了解服务功能的一个常见示例实际上是 IDE。

你可能已经注意到，IDE 始终会检测到发生在其外部的源代码文件中的更改。一些 IDE 使用对话框通知你，以便你可以选择是否从文件系统重新加载文件，其他 IDE 只是在后台更新文件。

同样，较新的框架(如 Play)也会默认热重新加载应用程序代码——无论何时你从任何编辑器执行编辑。

这些应用程序采用了一种称为文件更改通知的功能，该功能在所有文件系统中都可用。

基本上，我们可以编写代码来轮询文件系统以了解特定文件和目录的更改。但是，此解决方案不可扩展，尤其是当文件和目录达到成百上千时。

在Java7 NIO.2 中，WatchService API 提供了一个可扩展的解决方案来监控目录的变化。它有一个干净的 API，并且针对性能进行了很好的优化，我们不需要实施我们自己的解决方案。

## 3. Watchservice 是如何工作的？

要使用WatchService功能，第一步是使用java.nio.file.FileSystems类创建一个WatchService实例：

```java
WatchService watchService = FileSystems.getDefault().newWatchService();
```

接下来，我们必须创建要监视的目录的路径：

```java
Path path = Paths.get("pathToDir");
```

完成此步骤后，我们必须向手表服务注册路径。在这个阶段有两个重要的概念需要理解。StandardWatchEventKinds类和WatchKey类。看看下面的注册码，就可以了解每个跌倒的地方。我们将遵循相同的解释：

```java
WatchKey watchKey = path.register(
  watchService, StandardWatchEventKinds...);
```

这里只注意两件重要的事情：首先，路径注册 API 调用将监视服务实例作为第一个参数，后跟StandardWatchEventKinds的变量参数。其次，注册过程的返回类型是WatchKey实例。

### 3.1. StandardWatchEventKinds _

这是一个类，其实例告诉监视服务要在注册目录上监视的事件类型。目前有四种可能的事件需要关注：

-   StandardWatchEventKinds.ENTRY_CREATE – 在监视目录中创建新条目时触发。这可能是由于创建新文件或重命名现有文件所致。
-   StandardWatchEventKinds.ENTRY_MODIFY – 当监视目录中的现有条目被修改时触发。所有文件编辑都会触发此事件。在某些平台上，即使更改文件属性也会触发它。
-   StandardWatchEventKinds.ENTRY_DELETE – 在监视目录中删除、移动或重命名条目时触发。
-   StandardWatchEventKinds.OVERFLOW – 触发以指示丢失或丢弃的事件。我们不会过多关注它

### 3.2. 手表钥匙

此类表示向监视服务注册目录。当我们注册目录时以及当我们询问监视服务是否发生了我们注册的任何事件时，监视服务会将其实例返回给我们。

Watch 服务没有为我们提供任何事件发生时调用的回调方法。我们只能通过多种方式轮询它来找到这些信息。

我们可以使用轮询API：

```java
WatchKey watchKey = watchService.poll();
```

此 API 调用立即返回。它返回下一个排队的监视键，其任何事件已发生，如果没有注册事件发生则返回 null。

我们还可以使用带有超时参数的重载版本：

```java
WatchKey watchKey = watchService.poll(long timeout, TimeUnit units);
```

此 API 调用在返回值方面与上一个类似。但是，它会阻塞超时时间单位，以提供更多事件可能发生的时间，而不是立即返回 null。

最后，我们可以使用take API：

```java
WatchKey watchKey = watchService.take();
```

最后一种方法只是阻塞，直到事件发生。

这里我们必须注意一些非常重要的事情：当WatchKey实例由poll或take API返回时，如果不调用 reset API，它将不会捕获更多事件：

```java
watchKey.reset();
```

这意味着每次轮询操作返回 watch key 实例时，它都会从 watch 服务队列中删除。重置API调用将其放回队列中以等待更多事件。

观察者服务最实际的应用需要一个循环，在这个循环中我们不断地检查被观察目录的变化并进行相应的处理。我们可以使用以下成语来实现这一点：

```java
WatchKey key;
while ((key = watchService.take()) != null) {
    for (WatchEvent<?> event : key.pollEvents()) {
        //process
    }
    key.reset();
}
```

我们创建一个监视键来存储轮询操作的返回值。while 循环将阻塞，直到条件语句返回 watch key 或 null。

当我们得到一个 watch key 时，while 循环就会执行其中的代码。我们使用WatchKey.pollEvents API 返回已发生事件的列表。然后我们使用for each循环来逐个处理它们。

所有事件处理完毕后，我们必须调用reset API重新入队watch key。

## 4.目录监视示例

由于我们在上一小节中介绍了WatchService API 及其内部工作方式以及我们如何使用它，我们现在可以继续查看一个完整且实用的示例。

出于可移植性原因，我们将监视用户主目录中的活动，该目录应该在所有现代操作系统上都可用。

该代码仅包含几行代码，因此我们将其保留在 main 方法中：

```java
public class DirectoryWatcherExample {

    public static void main(String[] args) {
        WatchService watchService
          = FileSystems.getDefault().newWatchService();

        Path path = Paths.get(System.getProperty("user.home"));

        path.register(
          watchService, 
            StandardWatchEventKinds.ENTRY_CREATE, 
              StandardWatchEventKinds.ENTRY_DELETE, 
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println(
                  "Event kind:" + event.kind() 
                    + ". File affected: " + event.context() + ".");
            }
            key.reset();
        }
    }
}
```

这就是我们真正需要做的。现在你可以运行该类以开始监视目录。

当你导航到用户主目录并执行任何文件操作活动(如创建文件或目录、更改文件内容甚至删除文件)时，这些操作都将记录在控制台中。

例如，假设你转到用户主页，在空格处单击鼠标右键，选择new -> file以创建一个新文件，然后将其命名为testFile。然后添加一些内容并保存。控制台的输出将如下所示：

```plaintext
Event kind:ENTRY_CREATE. File affected: New Text Document.txt.
Event kind:ENTRY_DELETE. File affected: New Text Document.txt.
Event kind:ENTRY_CREATE. File affected: testFile.txt.
Event kind:ENTRY_MODIFY. File affected: testFile.txt.
Event kind:ENTRY_MODIFY. File affected: testFile.txt.
```

随意编辑路径以指向你要观看的任何目录。

## 5.总结

在本文中，我们探讨了Java7 NIO.2 文件系统 API 中一些不太常用的功能，尤其是WatchService接口。

我们还设法通过构建目录监视应用程序来演示功能的步骤。