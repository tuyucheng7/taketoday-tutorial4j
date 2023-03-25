## 1. 概述

使用参数从命令行运行应用程序是很常见的。特别是在服务器端。通常，我们不希望应用程序在每次运行时都做同样的事情：我们希望以某种方式配置它的行为。

在这个简短的教程中，我们将探讨如何在Java中处理命令行参数。

## 2. 在Java中访问命令行参数

由于main方法是Java应用程序的入口点，因此JVM通过其参数传递命令行参数。

传统的方法是使用String数组：

```java
public static void main(String[] args) {
    // handle arguments
}
```

然而，Java 5引入了可变参数，它是披着羊皮的数组。因此，我们可以使用String可变参数定义我们的main方法：

```java
public static void main(String... args) {
    // handle arguments
}
```

它们是相同的，因此在它们之间进行选择完全取决于个人品味和偏好。

main方法的方法参数包含命令行参数，其顺序与我们在执行时传递的顺序相同。如果我们想知道参数的数量，我们只需要检查数组的长度。

例如，我们可以在标准输出上打印参数的数量和它们的值：

```java
public static void main(String[] args) {
    System.out.println("Argument count: " + args.length);
    for (int i = 0; i < args.length; i++) {
        System.out.println("Argument " + i + ": " + args[i]);
    }
}
```

请注意，在某些语言中，第一个参数将是应用程序的名称。另一方面，在Java中，这个数组只包含参数。

## 3. 如何传递命令行参数

现在我们有了一个处理命令行参数的应用程序，让我们看看有哪些传递参数的选择。

### 3.1 命令行

最明显的方法是命令行。假设我们编译了cn.tuyucheng.taketoday.commandlinearguments.CliExample类，其中包含我们的main方法。

然后我们可以使用以下命令运行它：

```shell
java cn.tuyucheng.taketoday.commandlinearguments.CliExample
```

它产生以下输出：

```plaintext
Argument count: 0
```

现在，我们可以在类名后传递参数：

```shell
java cn.tuyucheng.taketoday.commandlinearguments.CliExample Hello World!
```

输出是：

```plaintext
Argument count: 2
Argument 0: Hello
Argument 1: World!
```

通常，我们将应用程序发布为jar文件，而不是一堆.class文件。比方说，我们将它打包在cli-example.jar中，并将cn.tuyucheng.taketoday.commandlinearguments.CliExample设置为主类。

现在我们可以通过以下方式在没有参数的情况下运行它：

```shell
java -jar cli-example.jar
```

或者带参数：

```shell
java -jar cli-example.jar Hello World!
Argument count: 2 
Argument 0: Hello 
Argument 1: World!
```

请注意，Java会将我们在类名或jar文件名之后传递的每个参数都视为应用程序的参数。因此，我们之前传递的所有内容都是JVM本身的参数。

### 3.2 Eclipse

当我们处理我们的应用程序时，我们需要检查它是否按我们想要的方式工作。

在Eclipse中，我们可以借助运行配置来运行应用程序。例如，运行配置定义要使用的JVM、入口点、类路径等。当然，我们可以指定命令行参数。

创建适当运行配置的最简单方法是右键单击我们的主要方法，然后 从上下文菜单中选择Run As >JavaApplication ：

[![日食运行](https://www.baeldung.com/wp-content/uploads/2019/09/eclipse-run.png)](https://www.baeldung.com/wp-content/uploads/2019/09/eclipse-run.png)

有了这个，我们立即使用符合我们项目设置的设置运行我们的应用程序。

为了提供参数，我们应该编辑该运行配置。我们可以通过Run > Run Configurations…菜单选项来完成。在这里，我们应该单击Arguments选项卡并填写Program arguments文本框：

[![日食配置](https://www.baeldung.com/wp-content/uploads/2019/09/eclipse-configure.png)](https://www.baeldung.com/wp-content/uploads/2019/09/eclipse-configure.png)

点击 运行将运行应用程序并传递我们刚刚输入的参数。

### 3.3. IntelliJ

IntelliJ使用类似的过程来运行应用程序。它将这些选项简单地称为配置。

首先，我们需要右键单击 main方法，然后选择 Run 'CliExample.main()'：

[![智能运行](https://www.baeldung.com/wp-content/uploads/2019/09/intellij-run.png)](https://www.baeldung.com/wp-content/uploads/2019/09/intellij-run.png)

这将运行我们的程序，但它还会将其添加到运行列表以进行进一步配置。

因此，要配置参数，我们应该选择Run > Edit Configurations…并编辑 Program arguments文本框：

[![智能配置](https://www.baeldung.com/wp-content/uploads/2019/09/intellij-configure-1024x646.png)](https://www.baeldung.com/wp-content/uploads/2019/09/intellij-configure-1024x646.png)

之后，我们应该点击确定并重新运行我们的应用程序，例如使用工具栏中的运行按钮。

### 3.4 NetBeans

NetBeans也符合其运行和配置过程。

我们应该首先通过右键单击 main方法并选择Run File来运行我们的应用程序：

[![netbeans 运行](https://www.baeldung.com/wp-content/uploads/2019/09/netbeans-run.png)](https://www.baeldung.com/wp-content/uploads/2019/09/netbeans-run.png)

和以前一样，这会创建一个运行配置并运行程序。

接下来，我们必须在该运行配置中配置参数。我们可以通过选择Run > Set Project Configuration > Customize...来做到这一点，然后我们应该在左侧 运行并填写Arguments文本字段：

[![netbeans 配置](https://www.baeldung.com/wp-content/uploads/2019/09/netbeans-configure.png)](https://www.baeldung.com/wp-content/uploads/2019/09/netbeans-configure.png)

之后，我们应该点击确定并启动应用程序。

## 4. 第三方库

在简单的场景中手动处理命令行参数很简单。但是，随着我们的需求越来越复杂，我们的代码也越来越复杂。因此，如果我们要创建具有多个命令行选项的应用程序，使用第三方库会更容易。

幸运的是，有大量支持大多数用例的库。两个流行的例子是[Picocli](https://www.baeldung.com/java-picocli-create-command-line-program)和[Spring Shell](https://www.baeldung.com/spring-shell-cli)。

## 5. 总结

使你的应用程序的行为可配置始终是一个好主意。在本文中，我们看到了如何使用命令行参数来做到这一点。此外，我们介绍了传递这些参数的各种方法。