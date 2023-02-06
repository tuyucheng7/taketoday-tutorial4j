## 1. 概述

[JDK 11](https://openjdk.java.net/projects/jdk/11/)是Java SE 11的实现，于2018年9月发布。

在本教程中，我们将介绍启动单文件源代码程序的Java 11新特性。

## 2. Java 11之前

**单文件程序是程序适合单个源文件的程序**。

在Java 11之前，即使是单文件程序，我们也必须遵循两步流程来运行该程序。

例如，如果一个名为HelloWorld.java的文件包含一个名为HelloWorld的类，并带有main()方法，**我们必须首先编译它**：

```bash
$ javac HelloWorld.java
```

这将生成一个class文件，我们必须使用以下命令运行它：

```bash
$ java HelloWorld
Hello Java 11!
```

请注意，由于我们已经通过编译创建了.class文件，因此java命令会运行它。作为证明，我们可以更改我们在原始文件中打印的内容，但如果我们不再次编译它，再次运行相同的java命令仍将打印“Hello Java 11!”。

在学习Java的早期阶段或编写小型实用程序时，此类程序是标准的。在这种情况下，在运行程序之前必须对其进行编译有点仪式感。

**但是，如果只用一个步骤来代替，不是很好吗？**Java 11试图通过允许我们直接从源代码运行此类程序来解决这个问题。

## 3. 启动单文件源代码程序

**首先，让我们指出，在Java 11中，我们仍然可以像过去使用早期Java版本一样**[编译](https://www.baeldung.com/javac)**和运行我们的Java程序**。

此外，从Java 11开始，我们可以使用以下命令来执行单文件程序：

```bash
$ java HelloWorld.java
Hello Java 11!
```

**请注意我们如何将Java源代码文件名而不是Java类传递给java命令**。

**JVM将源文件编译到内存中，然后运行它找到的第一个公共main()方法**。

如果源文件包含错误，我们将得到编译错误，否则，它将像我们已经编译它一样运行。

我们还要注意，此命令在文件名和类名兼容性方面更为宽松。

例如，如果我们重命名文件WrongName.java而不更改其内容，我们可以运行它：

```shell
java WrongName.java
```

这将起作用，并将预期结果打印到控制台。但是，如果我们尝试使用“javac”命令编译WrongName.java，我们会收到一条错误消息，因为在文件中定义的类名与文件名不一致。

话虽如此，仍然不鼓励不遵循几乎通用的命名约定。相应地重命名我们的文件或类应该是要走的路。

## 4. 命令行选项

Java启动器引入了一种新的源文件模式来支持此功能。如果满足以下两个条件之一，则启用源文件模式：

1.  命令行上的第一项后跟JVM选项是带有.java扩展名的文件名
2.  命令行包含–source版本选项

**如果文件不遵循Java源文件的标准命名约定，我们需要使用–source选项**。我们将在下一节中详细讨论此类文件。

在原始命令行中源文件名称之后放置的任何参数都会在执行时传递给已编译的类。

例如，我们有一个名为Addition.java的文件，其中包含一个Addition类。此类包含一个计算其参数总和的main()方法：

```bash
$ java Addition.java 1 2 3
```

此外，我们可以在文件名之前传递–class-path之类的选项：

```bash
$ java --class-path=/some-path Addition.java 1 2 3
```

现在，**如果应用程序类路径中有一个与我们正在执行的类同名的类，我们将得到一个错误**。

例如，假设在开发过程中的某个时刻，我们使用javac编译了当前工作目录中存在的文件：

```bash
$ javac HelloWorld.java
```

我们现在在当前工作目录中同时拥有HelloWorld.java和HelloWorld.class：

```bash
$ ls
HelloWorld.class  HelloWorld.java
```

但是，如果我们尝试使用源文件模式，我们会得到一个错误：

```bash
$ java HelloWorld.java                                            
error: class found on application class path: HelloWorld
```

## 5. Shebang文件

在Unix派生的系统(如macOS和Linux)中，使用“#!”指令运行可执行脚本文件是很常见的。

例如，shell脚本通常以以下内容开头：

```bash
#!/bin/sh
```

然后我们可以执行脚本：

```bash
$ ./some_script
```

此类文件称为“shebang文件”。

我们现在可以使用相同的机制执行Java单文件程序。

如果我们将以下内容添加到文件的开头：

```bash
#!/path/to/java --source version
```

例如，让我们在名为add的文件中添加以下代码：

```java
#!/usr/local/bin/java --source 11

import java.util.Arrays;

public class Addition {
    public static void main(String[] args) {
        Integer sum = Arrays.stream(args)
              .mapToInt(Integer::parseInt)
              .sum();

        System.out.println(sum);
    }
}
```

并将文件标记为可执行文件：

```bash
$ chmod +x add
```

然后，我们可以像执行脚本一样执行文件：

```bash
$ ./add 1 2 3
6
```

我们还可以显式使用启动器来调用shebang文件：

```bash
$ java --source 11 add 1 2 3
6
```

**即使文件中已经存在–source选项，它也是必需的**。文件中的shebang被忽略，并被视为没有.java扩展名的普通java文件。

但是，**我们不能将.java文件视为shebang文件，即使它包含有效的shebang**。因此，以下情况将导致错误：

```bash
$ ./Addition.java
./Addition.java:1: error: illegal character: '#'
#!/usr/local/bin/java --source 11
^
```

关于shebang文件要注意的最后一件事是该指令使文件依赖于平台。**该文件将无法在Windows等平台上使用，Windows本身不支持它**。

## 6. 总结

在本文中，我们看到了Java 11中引入的新的单文件源代码特性。