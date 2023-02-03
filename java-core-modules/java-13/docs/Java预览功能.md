## 1. 概述

在本教程中，我们将探索Java预览功能背后的动机、它们与实验性功能的区别，以及如何使用不同的工具启用它们。

## 2. 为什么需要预览功能

现在每个人可能都清楚，Java功能版本每六个月发布一次。这意味着等待新Java特性的时间更少，但与此同时，它也意味着对新特性的反馈做出反应的时间更少。

这就是我们在这里谈论的Java。它用于开发大量的生产系统。结果，即使是一个实现中的小故障或糟糕的功能设计也可能导致非常昂贵的代价。

必须有一种方法来确保新功能的稳定性。更重要的是，它们必须适合社区的需要。但是怎么做呢？

借助[JEP-12](https://openjdk.java.net/jeps/12)，“审查语言和VM功能”可以包含在交付中。这样，社区就可以在现实生活场景中检查新功能-当然不是在生产环境中。

根据社区反馈，可以改进预览功能，可能会在多个版本中进行多次改进。最终，该功能可能会成为永久性的。但在某些情况下，提供的评论可能会导致完全取消预览功能。

## 3. 预览与实验功能

**Java预览功能是经过评估的完全指定和开发的功能**。因此，他们还没有达到最终状态。

由于它们的高质量，不同的JDK实现必须包括每个Java交付中计划的所有预览功能。但是，**Java版本仍然不支持早期版本的预览功能**。

预览功能本质上只是一种鼓励社区审查和提供反馈的方式。此外，并非每个Java功能都必须经过预览阶段才能成为最终功能。

以下是JEP-12对预览功能的描述：

>   预览语言或VM功能是一种新功能，其设计、规范和实现都已完成，但在Java SE平台中实现最终和永久状态之前，需要经过一段时间的广泛曝光和评估，或者进行完善或改进删除。

另一方面，**实验性功能远未完成**。它们的工件与JDK工件明显分开。

**实验特性是不稳定的**，因此，它们会给语言带来风险。因此，不同的JDK实现可能包含不同的实验特性集。

## 4. 使用预览功能

**默认情况下，预览功能处于禁用状态**。要启用它们，我们必须使用enable-preview参数，它会同时启用所有预览功能。

**Java编译器和JVM必须是包含我们要使用的预览功能的相同Java版本**。

让我们尝试编译并运行一段使用文本块的代码，这是JDK 13中的一个预览功能：

```java
String query = """
    SELECT 'Hello World'
    FROM DUAL;
    """;
System.out.println(query);
```

当然，我们需要确保我们将JDK 13与我们最喜欢的IDE一起使用。例如，我们可以下载OpenJDK [版本13](https://jdk.java.net/13/)并将其添加到我们的IDE的Java运行时。

### 4.1 使用Eclipse

起初，Eclipse会将代码标记为红色，因为它不会编译。错误消息将告诉我们启用预览功能以便使用文本块。

我们需要右键单击项目并从弹出菜单中选择Properties。接下来，我们转到Java Compiler。现在，我们可以选择为此特定项目或整个工作区启用预览功能。

接下来，我们必须取消选中Use default compliance settings，然后我们才能选中Enable preview features for Java 13：

<img src="../assets/img.png">

### 4.2 使用IntelliJ IDEA

正如我们所料，默认情况下代码也不会在IntelliJ中编译，即使使用Java 13，我们也会收到一条类似于我们在Eclipse中看到的错误消息。

我们可以在“File”菜单中的“Project Structure”中启用预览功能。从Project中，我们需要选择13 (Preview)作为项目语言级别：

<img src="../assets/img_1.png">

这应该可以做到。但是，如果错误仍然存在，我们必须手动添加编译器参数以启用预览功能。假设它是一个Maven项目，pom.xml中的编译器插件应该包含：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>13</source>
                <target>13</target>
                <compilerArgs>
                    --enable-preview
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

如果需要，我们可以以类似的方式为其他Maven插件在其各自的配置中启用预览功能。

### 4.3 从命令行

在编译时，javac命令需要两个参数--enable-preview和release：

```shell
javac --release 13 --enable-preview ClassUsingTextBlocks.java
```

让我们回想一下，JDK版本N不支持版本N-1或任何先前版本的预览功能。因此，如果我们尝试使用JDK 14执行之前的命令，将会出现错误。

长话短说，**release参数必须将N设置为正在使用的编译器(和JVM)的JDK发布版本，以便启用预览功能**。

release参数只是一个额外的保护措施，以确保使用预览功能的代码不会急于在生产中使用。

在运行时，java命令只需要enable-preview参数：

```shell
java --enable-preview ClassUsingTextBlocks
```

但是，只有使用该特定JDK版本的预览功能的代码才能运行。

## 5. 总结

在本文中，我们介绍了Java中的预览功能、我们为什么要使用它们以及它们与实验性功能的区别。

然后，使用JDK 13中的文本块预览功能，我们逐步解释了如何从Eclipse、IntelliJ、Maven和命令行使用预览功能。