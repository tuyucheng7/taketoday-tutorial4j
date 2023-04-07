## 一、简介

在本教程中，我们将讨论反编译 Java 类。当源代码不可用时，反编译 Java 类有助于调试和理解源代码行为。

让我们来看看可用的不同选项。

## 2.IDE反编译

由于大多数开发是在集成开发环境 (IDE) 中完成的，因此反编译也应该在 IDE 中进行。

有关我们将使用的 IDE 的更多信息，请查看我们关于[如何在 Eclipse 中调试](https://www.baeldung.com/eclipse-debugging)和[配置 IntelliJ IDEA 的](https://www.baeldung.com/intellij-basics)文章。

### 2.1. 蚀

首先，在 Eclipse 中我们需要一个插件，例如[Enhanced Class Decompiler (ECD)](https://marketplace.eclipse.org/content/enhanced-class-decompiler)。这个插件使用了五个不同的反编译器。我们可以从 Eclipse Marketplace 安装它，然后我们需要重新启动 Eclipse。

接下来，ECD 需要少量设置才能将类文件与类反编译器查看器相关联：

[![蚀类](https://www.baeldung.com/wp-content/uploads/2020/01/Eclipse_class.jpg)](https://www.baeldung.com/wp-content/uploads/2020/01/Eclipse_class.jpg)

此外，我们需要关联“没有源代码的*.class* ”文件：

[![Eclipse类WithoutSource](https://www.baeldung.com/wp-content/uploads/2020/01/Eclipse_classWithoutSource.jpg)](https://www.baeldung.com/wp-content/uploads/2020/01/Eclipse_classWithoutSource.jpg)

最后，我们可以通过在*类名上按**Ctrl+左键单击* 来使用反编译器。我们在括号中的文件选项卡上看到使用的反编译器。

在这个例子中，我们使用 FernFlower：

[![蚀](https://www.baeldung.com/wp-content/uploads/2020/01/Eclipse.jpg)](https://www.baeldung.com/wp-content/uploads/2020/01/Eclipse.jpg)

### 2.2. 我明白这个想法

与 Eclipse 不同，**IntelliJ IDEA 默认提供 FernFlower 反编译器**。

要使用它，我们只需*Ctrl+左键单击*类名并查看代码：

[![IntelliJIDEA](https://www.baeldung.com/wp-content/uploads/2020/01/IntelliJIDEA.png)](https://www.baeldung.com/wp-content/uploads/2020/01/IntelliJIDEA.png)

另外，我们可以下载源码。下载源代码将提供实际代码和注释。

例如，上面屏幕截图中的*Component*注释类包括关于使用*Component*的 Javadoc 。我们可以注意到不同之处：

[![IntelliJIDEA.2](https://www.baeldung.com/wp-content/uploads/2020/01/IntelliJIDEA.2.jpg)](https://www.baeldung.com/wp-content/uploads/2020/01/IntelliJIDEA.2.jpg)

**虽然反编译非常有用，但它并不总能提供完整的图片**。完整的源代码为我们提供了完整的画面。

## 3.命令行反编译

在 IDE 插件之前，命令行用于反编译类。命令行反编译器也可用于在无法使用 IDE 或 GUI 访问的远程服务器上调试 Java 字节码。

例如，我们可以 使用一个简单的 jar 命令使用[JDCommandLine进行反编译：](https://github.com/betterphp/JDCommandLine)

```bash
java -jar JDCommandLine.jar ${TARGET_JAR_NAME}.jar ./classes复制
```

**不要遗漏 ./classes 参数。**它定义了输出目录。

反编译成功后，我们可以访问输出目录中包含的源文件。他们现在已准备好通过[Vim 等文本编辑器](https://www.baeldung.com/linux/files-vi-nano-emacs)进行查看。

## 4。结论

我们研究了 Eclipse 和 IntelliJ IDEA IDE 中的反编译以及它们不可用时的命令行选项。

我们还研究了链接源代码和反编译之间的区别。