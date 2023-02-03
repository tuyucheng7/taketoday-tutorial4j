## 1. 概述

**根据Java六个月的新发布节奏，2019年9月发布了JDK 13**。在本文中，我们将了解此版本中引入的新功能和改进。

## 2. 预览开发者功能

**Java 13带来了两个新的语言特性，尽管是在预览模式下**。这意味着这些功能已完全实现，供开发人员评估，但尚未做好生产准备。此外，它们可以根据反馈在未来的版本中删除或永久保留。

**我们需要将--enable-preview指定为命令行标志才能使用预览功能**。让我们深入了解一下。

### 2.1 Switch表达式(JEP 354)

我们最初在[JDK 12](https://www.baeldung.com/java-switch#switch-expressions)中看到了switch表达式。[Java 13的switch表达式](https://openjdk.java.net/jeps/354)通过添加新的yield语句构建在先前版本的基础上。

**使用yield，我们现在可以有效地从switch表达式返回值**：

```java
@Test
@SuppressWarnings("preview")
public void whenSwitchingOnOperationSquareMe_thenWillReturnSquare() {
    var me = 4;
    var operation = "squareMe";
    var result = switch (operation) {
        case "doubleMe" -> {
            yield me * 2;
        }
        case "squareMe" -> {
            yield me * me;
        }
        default -> me;
    };

    assertEquals(16, result);
}
```

正如我们所看到的，现在很容易使用新的switch来实现[策略模式](https://www.baeldung.com/java-strategy-pattern)。

### 2.2.文本块([JEP 355](https://openjdk.java.net/jeps/355))

第二个预览功能是嵌入JSON、XML、HTML等多行String的[文本块](https://www.baeldung.com/java-text-blocks)。

早些时候，为了在我们的代码中嵌入JSON，我们会将其声明为字符串文本：

```java
private static final String JSON_STRING = "{\r\n" + "\"name\" : \"Tuyucheng\",\r\n" + "\"website\" : \"https://www.%s.com/\"\r\n" + "}";
```

现在让我们使用字符串文本块编写相同的JSON：

```java
String TEXT_BLOCK_JSON = """
{
    "name" : "Tuyucheng",
    "website" : "https://www.%s.com/"
}
""";
```

很明显，无需转义双引号或添加回车符。通过使用文本块，嵌入式JSON编写起来更简单，也更易于阅读和维护。

此外，所有String函数都可用：

```java
@Test
public void whenTextBlocks_thenStringOperationsWorkSame() {        
    assertThat(TEXT_BLOCK_JSON.contains("Tuyucheng")).isTrue();
    assertThat(TEXT_BLOCK_JSON.indexOf("www")).isGreaterThan(0);
    assertThat(TEXT_BLOCK_JSON.length()).isGreaterThan(0);
}
```

此外，java.lang.String现在具有三种操作文本块的新方法：

-   stripIndent()：模仿编译器删除附带的空白
-   translateEscapes()：将转义序列如“\\\t”翻译成“\t”
-   formatted()：与String::format相同，但用于文本块

让我们快速看一下String::formatted示例：

```java
assertThat(TEXT_BLOCK_JSON.formatted("tuyucheng").contains("www.tuyucheng.com")).isTrue();
assertThat(String.format(JSON_STRING,"tuyucheng").contains("www.tuyucheng.com")).isTrue();
```

由于文本块是一项预览功能，可能在未来的版本中删除，因此这些新方法已标记为弃用。

## 3. 动态CDS档案(JEP 350)

一段时间以来，类数据共享(CDS)一直是Java HotSpot VM的一个突出特性。**它允许在不同的JVM之间共享类元数据，以减少启动时间和内存占用**。JDK 10通过添加应用程序CDS([AppCDS](https://openjdk.java.net/jeps/310))扩展了这种能力-让开发人员能够将应用程序类包含在共享存档中。JDK 12进一步增强了此功能，[默认包含CDS存档](https://openjdk.org/jeps/341)。

然而，归档应用程序类的过程是乏味的。为了生成存档文件，开发人员必须先试运行他们的应用程序以创建类列表，然后将其转储到存档中。之后，这个存档可用于在JVM之间共享元数据。

通过[动态归档](https://openjdk.org/jeps/350)，JDK 13简化了这个过程。**现在我们可以在应用程序退出时生成共享存档，这消除了试运行的需要**。

为了使应用程序能够在默认系统存档之上创建动态共享存档，我们需要添加一个选项-XX:ArchiveClassesAtExit并将存档名称指定为参数：

```shell
java -XX:ArchiveClassesAtExit=<archive filename> -cp <app jar> AppName
```

然后我们可以使用新创建的存档来运行带有-XX:SharedArchiveFile选项的相同应用程序：

```shell
java -XX:SharedArchiveFile=<archive filename> -cp <app jar> AppName
```

## 4. ZGC：取消提交未使用的内存(JEP 351)

[Z Garbage Collector(ZGC)](https://www.baeldung.com/jvm-zgc-garbage-collector)是在Java 11中引入的一种低延迟垃圾收集机制，因此GC暂停时间永远不会超过10毫秒。然而，与G1和Shenandoah等其他HotSpot VM GC不同，它无法将未使用的堆内存返回给操作系统。[Java 13将此功能添加](https://openjdk.org/jeps/351)到ZGC中。

我们现在减少了内存占用并提高了性能。

从Java 13开始，**ZGC现在默认将未提交的内存返回给操作系统**，直到达到指定的最小堆大小。如果我们不想使用这个特性，我们可以通过以下方式回到Java 11的方式：

-   使用选项-XX:-ZUncommit，或
-   设置相等的最小(-Xms)和最大(-Xmx)堆大小

此外，ZGC现在支持的最大堆大小为16TB。早些时候，4TB是极限。

## 5. 重新实现遗留套接字API(JEP 353)

自Java诞生以来，我们就已经看到Socket API(java.net.Socket和java.net.ServerSocket)作为Java不可或缺的一部分。然而，它们在过去二十年中从未进行过现代化改造。它们是用遗留的Java和C编写的，既麻烦又难以维护。

Java 13逆势而行，[替换了底层实现](https://openjdk.org/jeps/353)，使API与未来的用户模式线程保持一致。提供者接口现在指向NioSocketImpl而不是PlainSocketImpl。这个新编码的实现基于与java.nio相同的内部基础结构。

同样，我们确实有办法返回使用PlainSocketImpl。我们可以通过将系统属性-Djdk.net.usePlainSocketImpl设置为true来启动JVM，以使用旧的实现。默认是NioSocketImpl。

## 6. 其他变更

除了上面列出的JEP之外，Java 13还为我们提供了一些更显著的变化：

-   java.nio：方法FileSystems.newFileSystem(Path, Map<String, ?>)添加
-   java.time：添加了新的官方日本时代名称
-   javax.crypto：支持MS Cryptography Next Generation(CNG)
-   javax.security：添加属性jdk.sasl.disabledMechanisms以禁用SASL机制
-   javax.xml.crypto：引入新的字符串常量来表示Canonical XML 1.1 URI
-   javax.xml.parsers：添加了新方法来实例化具有命名空间支持的DOM和SAX工厂
-   Unicode支持升级到版本12.1
-   添加了对Kerberos主体名称规范化和跨领域引用的支持

此外，还[建议删除](http://cr.openjdk.java.net/~iris/se/13/latestSpec/#APIs-proposed-for-removal)一些API。其中包括上面列出的三个String方法和javax.security.cert API。

删除的内容包括rmic工具和[JavaDoc工具的旧功能](https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8215608)。JDK 1.4之前的SocketImpl实现也不再受支持。

## 7. 总结

在本文中，我们看到了Java 13实现的所有五个JDK增强提案。我们还列出了其他一些值得注意的添加和删除。