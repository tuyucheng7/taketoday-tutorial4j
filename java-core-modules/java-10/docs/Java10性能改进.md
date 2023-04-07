## **一、概述**

在本快速教程中，我们将讨论最新 Java 10 版本带来的性能改进。

这些改进适用于在 JDK 10 下运行的所有应用程序，无需更改任何代码即可利用它们。

## **2. G1 的并行 Full GC**

G1 垃圾收集器是自 JDK 9 以来的默认垃圾收集器。但是，G1 的完整 GC 使用单线程标记-清除-压缩算法。

这已**更改为 Java 10 中的并行 mark-sweep-compact 算法，** 有效减少了 full GC 期间的 stop-the-world 时间。

## **3. 应用类-数据共享**

JDK 5 中引入的类数据共享允许将一组类预处理为共享存档文件，然后可以在运行时进行内存映射以减少启动时间，这也可以减少多个 JVM 共享时的动态内存占用同一个归档文件。

CDS 只允许引导类加载器，将功能限制为仅系统类。应用程序 CDS (AppCDS) 扩展 CDS 以允许内置系统类加载器（又名“应用程序类加载器”）、内置平台类加载器和自定义类加载器加载归档类。**这使得可以将该功能用于应用程序类。**

我们可以通过以下步骤来使用此功能：

**1.获取要归档的类列表**

以下命令会将*HelloWorld* 应用程序加载的类转储到*hello.lst*中：

```bash
$ java -Xshare:off -XX:+UseAppCDS -XX:DumpLoadedClassList=hello.lst \ 
    -cp hello.jar HelloWorld复制
```

**2. 创建 AppCDS 存档**

以下命令使用*hello.lst 作为输入创建**hello.js* ：

```bash
$ java -Xshare:dump -XX:+UseAppCDS -XX:SharedClassListFile=hello.lst \
    -XX:SharedArchiveFile=hello.jsa -cp hello.jar复制
```

**3.使用AppCDS存档**

以下命令以*hello.jsa 作为输入启动**HelloWorld* 应用程序：

```bash
$ java -Xshare:on -XX:+UseAppCDS -XX:SharedArchiveFile=hello.jsa \
    -cp hello.jar HelloWorld复制
```

**AppCDS 是 Oracle JDK 中用于 JDK 8 和 JDK 9 的商业功能。现在它是开源的并公开可用。**

## **4. 实验性的基于 Java 的 JIT 编译器**

[Graal](https://github.com/oracle/graal/blob/master/compiler/README.md) 是一个用 Java 编写的动态编译器，它与 HotSpot JVM 集成；它专注于高性能和可扩展性。它也是 JDK 9 中引入的实验性提前 (AOT) 编译器的基础。

JDK 10 使 Graal 编译器成为 Linux/x64 平台上的实验性 JIT 编译器。

要启用 Graal 作为 JIT 编译器，请在 java 命令行中使用以下选项：

```bash
-XX:+UnlockExperimentalVMOptions -XX:+UseJVMCICompiler复制
```

请注意，这是一项实验性功能，我们不一定能获得比现有 JIT 编译器更好的性能。

## **5.结论**

在这篇快速文章中，我们重点关注并探索了 Java 10 中的性能改进功能。