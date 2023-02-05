## 1. 概述

通常，每个有意义的应用程序都包含一个或多个JAR文件作为依赖项。但有时JAR文件本身代表一个独立应用程序或Web应用程序。

在这里，我们将重点介绍独立的应用程序方案。从现在起，我们将其称为JAR应用程序。

在本教程中，我们将首先学习如何创建JAR应用程序。然后，我们将学习如何**使用或不使用命令行参数运行JAR应用程序**。

## 2. 创建JAR应用程序

一个[JAR文件](https://www.baeldung.com/java-create-jar)可以包含一个或多个主类，**每个主类都是应用程序的入口点**。因此，一个JAR文件理论上可以包含多个应用程序，但它必须包含至少一个主类才能运行。

**JAR文件可以在其**[manifest文件](https://www.baeldung.com/java-jar-executable-manifest-main-class)**中设置一个入口点**。在这种情况下，JAR文件是一个[可执行的JAR](https://www.baeldung.com/executable-jar-with-maven)，主类必须包含在该JAR文件中。

首先，让我们看一个快速示例，说明如何编译我们的类并使用manifest文件创建可执行JAR：

```bash
$ javac cn/tuyucheng/taketoday/jarArguments/*.java
$ jar cfm JarExample.jar ../resources/example_manifest.txt cn/tuyucheng/taketoday/jarArguments/*.class
```

不可执行的JAR只是一个没有在manifest文件中定义Main-Class的JAR文件。正如我们稍后将看到的，我们仍然可以运行包含在JAR文件本身中的主类。

以下是我们如何创建不带manifest文件的不可执行JAR：

```bash
$ jar cf JarExample2.jar cn/tuyucheng/taketoday/jarArguments/*.class
```

## 3. Java命令行参数

就像任何应用程序一样，JAR应用程序接收任意数量的参数，包括零参数。这一切都取决于应用程序的需要。

这允许用户**在启动应用程序时指定配置信息**。

因此，应用程序可以避免硬编码值，并且它仍然可以处理许多不同的用例。

参数可以包含任何字母数字字符、unicode字符和可能的一些shell允许的特殊字符，例如@。

**参数由一个或多个空格分隔**，如果参数需要包含空格，则必须将空格括在引号之间。单引号或双引号都可以正常工作。

通常，对于典型的Java应用程序，在调用应用程序时，用户会在类名之后输入命令行参数。

但是，对于JAR应用程序，情况并非总是如此。

正如我们所讨论的，Java主类的入口点是main方法。参数都是String，并作为String数组传递给main方法。

也就是说，在应用程序内部，我们可以将String数组的任何元素转换为其他数据类型，例如char、int、double、它们的[包装类](https://www.baeldung.com/java-wrapper-classes)或其他适当的类型。

## 4. 运行带参数的可执行JAR

让我们看看运行带参数的可执行JAR文件的基本语法：

```shell
java -jar jar-file-name [args...]
```

上面创建的可执行JAR是一个简单的应用程序，它只打印出传入的参数。我们可以使用任意数量的参数运行它。

以下是一个传递两个参数的示例：

```bash
$ java -jar JarExample.jar "arg 1" arg2@
```

控制台的输出为：

```shell
Hello Tuyucheng Reader in JarExample!
There are 2 argument(s)!
Argument(1):arg 1
Argument(2):arg2@
```

因此，**在调用可执行JAR时，我们不需要在命令行中指定主类名**。我们只需在JAR文件名之后添加我们的参数。如果我们确实在可执行JAR文件名之后提供了一个类名，那么它只会成为实际主类的第一个参数。

大多数情况下，JAR应用程序是一个可执行的JAR。一个可执行JAR最多可以在[manifest文件](https://www.baeldung.com/java-jar-executable-manifest-main-class)中定义一个主类。

因此，不能在manifest文件中设置同一可执行JAR文件中的其他应用程序，但我们仍然可以从命令行运行它们，就像我们对不可执行JAR所做的那样。我们将在下一节中看到具体情况。

## 5. 运行带参数的不可执行的JAR

要在不可执行的JAR文件中运行应用程序，我们必须使用-cp选项而不是-jar。

我们将使用-cp选项(cp为classpath的缩写)来指定包含我们要执行的类文件的JAR文件：

```shell
java -cp jar-file-name main-class-name [args...]
```

如我们所见，**在这种情况下，我们必须在命令行中包含主类名，然后是参数**。

前面创建的不可执行JAR包含相同的简单应用程序。我们可以使用任何(包括零)参数来运行它。

以下是一个传递两个参数的示例：

```bash
$ java -cp JarExample2.jar jarArguments.cn.tuyucheng.taketoday.JarExample "arg 1" arg2@
```

得到的输出为：

```shell
Hello Baeldung Reader in JarExample!
There are 2 argument(s)!
Argument(1):arg 1
Argument(2):arg2@
```

## 6. 总结

在本文中，我们学习了两种在命令行上使用或不使用参数运行JAR应用程序的方法。

我们还演示了在参数中包含空格和特殊字符(当shell允许时)。
