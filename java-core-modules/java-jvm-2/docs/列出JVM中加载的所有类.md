## 1. 概述

在本教程中，我们将学习不同的技术来列出JVM 中[加载的所有类。](https://www.baeldung.com/java-classloaders)例如，我们可以加载 JVM 的堆转储或将正在运行的应用程序连接到各种工具并列出该工具中加载的所有类。此外，还有各种库可以以编程方式完成此操作。

我们将探索非程序化和程序化方法。

## 2. 非程序化方法

### 2.1. 使用 VM 参数

列出所有加载的类的最直接方法是将其记录在控制台输出或文件中。

我们将使用以下 JVM 参数运行Java应用程序：

```bash
java <app_name> --verbose:class

[Opened /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/rt.jar]
[Loaded java.lang.Object from /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/rt.jar] 
[Loaded java.io.Serializable from /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/rt.jar] 
[Loaded java.lang.Comparable from /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/rt.jar] 
[Loaded java.lang.CharSequence from /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/rt.jar] 
[Loaded java.lang.String from /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/rt.jar] 
[Loaded java.lang.reflect.AnnotatedElement from /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/rt.jar] 
[Loaded java.lang.reflect.GenericDeclaration from /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/rt.jar] 
[Loaded java.lang.reflect.Type from /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/rt.jar] 
[Loaded java.lang.Class from /Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/jre/lib/rt.jar] 
...............................
```

对于Java9，我们将使用-Xlog JVM 参数来记录加载到文件中的类：

```bash
java <app_name> -Xlog:class+load=info:classloaded.txt
```

### 2.2. 使用堆转储

我们将看到不同的工具如何使用 JVM[堆转储](https://www.baeldung.com/java-heap-dump-capture)来提取类加载信息。但是，首先，我们将使用以下命令生成堆转储：

```perl
jmap -dump:format=b,file=/opt/tmp/heapdump.bin <app_pid>

```

可以在各种工具中打开上面的堆转储以获得不同的指标。

在 Eclipse 中，我们将在[Eclipse 内存分析器中加载堆转储文件](https://www.eclipse.org/mat/)heapdump.bin并使用直方图接口：

[![日食直方图](https://www.baeldung.com/wp-content/uploads/2021/11/eclipse-histogram.png)](https://www.baeldung.com/wp-content/uploads/2021/11/eclipse-histogram.png)

我们现在将在Java[VisualVM界面中打开堆转储文件](https://visualvm.github.io/)heapdump.bin 并按实例或大小选项使用类：

[![堆转储视觉虚拟机](https://www.baeldung.com/wp-content/uploads/2021/11/heapdump-visualvm.png)](https://www.baeldung.com/wp-content/uploads/2021/11/heapdump-visualvm.png)

### 2.3. J型材

[JProfiler](https://www.baeldung.com/java-profilers)是顶级Java应用程序分析器之一，具有一组丰富的功能来查看不同的指标。

在[JProfiler](https://www.ej-technologies.com/products/jprofiler/overview.html)中，我们可以附加到正在运行的 JVM 或加载堆转储文件并获取所有与 JVM 相关的指标，包括所有加载类的名称。

我们将使用附加进程功能让 JProfiler 连接到正在运行的应用程序ListLoadedClass：

[![jprofiler 附加进程](https://www.baeldung.com/wp-content/uploads/2021/11/jprofiler-attach-process.png)](https://www.baeldung.com/wp-content/uploads/2021/11/jprofiler-attach-process.png)

然后，我们将获取应用程序的快照并使用它来加载所有类：

[![jprofiler 快照](https://www.baeldung.com/wp-content/uploads/2021/11/jprofiler-snapshot.png)](https://www.baeldung.com/wp-content/uploads/2021/11/jprofiler-snapshot.png)

下面，我们可以使用 Heap Walker 功能查看加载类实例计数的名称：

[![jprofiler heapwalker](https://www.baeldung.com/wp-content/uploads/2021/11/jprofiler-heapwalker.png)](https://www.baeldung.com/wp-content/uploads/2021/11/jprofiler-heapwalker.png)

## 3.程序化方法

### 3.1. 仪器API

Java 提供了[Instrumentation API](https://www.baeldung.com/java-list-classes-class-loader)，它有助于在应用程序上获取有价值的指标。首先，我们必须创建并加载一个Java代理，以将Instrumentation接口的实例获取到应用程序中。Java 代理是一种检测在 JVM 上运行的程序的工具。

然后，我们需要调用Instrumentation方法[getInitiatedClasses(Classloader loader)](https://docs.oracle.com/en/java/javase/14/docs/api/java.instrument/java/lang/instrument/Instrumentation.html#getInitiatedClasses(java.lang.ClassLoader))来获取由特定类加载器类型加载的所有类。

### 3.2. 谷歌番石榴

我们将看到 Guava 库如何使用当前类加载器获取加载到 JVM 中的所有类的列表。

让我们首先将[Guava 依赖项](https://search.maven.org/search?q=g:com.google.guava AND a:guava)添加到我们的 Maven 项目中：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>

```

我们将使用当前类加载器实例初始化ClassPath对象：

```java
ClassPath classPath = ClassPath.from(ListLoadedClass.class.getClassLoader());
Set<ClassInfo> classes = classPath.getAllClasses();
Assertions.assertTrue(4 < classes.size());
```

### 3.3. 反射API

我们将使用扫描当前类路径并允许我们在运行时查询它的[Reflections库。](https://www.baeldung.com/reflections-library)

让我们首先将 [反射 依赖项](https://search.maven.org/search?q=g:org.reflections AND a:reflections)添加 到我们的 Maven 项目中：

```xml
<dependency>
    <groupId>org.reflections</groupId>
    <artifactId>reflections</artifactId>
    <version>0.10.2</version>
</dependency>
```

现在，我们将研究示例代码，它返回包下的一组类：

```java
Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
Set<Class> classes = reflections.getSubTypesOf(Object.class)
  .stream()
  .collect(Collectors.toSet());
Assertions.assertEquals(4, classes.size());
```

## 4。总结

在本文中，我们了解了列出 JVM 中加载的所有类的各种方法。首先，我们已经了解了如何使用 VM Argument 记录已加载类的列表。

然后，我们探讨了各种工具如何加载堆转储或连接到 JVM 以显示各种指标，包括加载的类。最后，我们介绍了一些Java库。