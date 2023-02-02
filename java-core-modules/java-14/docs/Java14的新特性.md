## 1. 概述

根据Java的新发布节奏，Java 14于2020年3月17日发布，比[上一版本](https://www.baeldung.com/java-13-new-features)晚了整整六个月。

在本教程中，**我们将查看该语言版本14的新功能和已弃用功能的摘要**。

我们还有关于[Java 14](https://www.baeldung.com/tag/java-14)的更详细的文章，提供了对新功能的深入介绍。

## 2. 从早期版本继承的特性

Java 14中的一些功能从以前的版本继承而来，让我们一一看看。

### 2.1 Switch表达式([JEP 361](https://openjdk.java.net/jeps/361))

这些功能在JDK 12中首次作为预览功能引入，即使在Java 13中，它们也仅作为预览功能继续存在。但是现在，[switch表达式](https://www.baeldung.com/java-13-new-features#switch-expression)**已经标准化，因此它们已成为开发工具包的重要组成部分**。

这实际上意味着这个功能现在可以在生产代码中使用，而不仅仅是在开发人员试验的预览模式中使用。

作为一个简单的例子，让我们考虑一个场景，我们将一周中的几天指定为工作日或周末。

在此增强功能之前，我们将其编写为：

```java
boolean isTodayHoliday;
switch (day) {
    case "MONDAY":
    case "TUESDAY":
    case "WEDNESDAY":
    case "THURSDAY":
    case "FRIDAY":
        isTodayHoliday = false;
        break;
    case "SATURDAY":
    case "SUNDAY":
        isTodayHoliday = true;
        break;
    default:
        throw new IllegalArgumentException("What's a " + day);
}
```

使用switch表达式，我们可以更简洁地编写相同的内容：

```java
boolean isTodayHoliday = switch (day) {
    case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> false;
    case "SATURDAY", "SUNDAY" -> true;
    default -> throw new IllegalArgumentException("What's a " + day);
};
```

### 2.2 文本块([JEP 368](https://openjdk.org/jeps/368))

[文本块](https://www.baeldung.com/java-text-blocks)继续他们的主流升级之旅，并且仍然可以作为预览功能使用。

除了JDK 13中提供的使多行字符串更易于使用的功能之外，在他们的第二个预览中，**文本块现在有两个新的转义序列**：

-   \\: 表示行的结尾，这样就不会引入新的换行符
-   \\s：表示单个空格

例如：

```java
String multiline = "A quick brown fox jumps over a lazy dog; the lazy dog howls loudly.";
```

现在可以写成：

```java
String multiline = """
    A quick brown fox jumps over a lazy dog; \
    the lazy dog howls loudly.""";
```

这提高了句子对人眼的可读性，但不会在dog；之后添加新行。

## 3. 新的预览功能

### 3.1 instanceof的模式匹配([JEP 305](https://openjdk.java.net/jeps/305)) 

JDK 14为[instanceof](https://www.baeldung.com/java-pattern-matching-instanceof)引入了模式匹配，目的是消除样板代码并让开发人员的工作变得更轻松。

为了理解这一点，让我们考虑一个简单的例子。

在此功能之前，我们应该都写过类似于下面的代码：

```java
if (obj instanceof String) {
    String str = (String) obj;
    int len = str.length();
    // ...
}
```

现在，我们不需要那么多代码：

```java
if (obj instanceof String str) {
    int len = str.length();
    // ...
}
```

**在未来的版本中，Java将为其他构造(例如switch)提供模式匹配**。

### 3.2 Record([JEP 359](https://openjdk.org/jeps/359))

引入[记录](https://www.baeldung.com/java-record-keyword)是为了减少数据模型POJO中的重复样板代码，**它们可以简化日常开发，提高了效率并大大降低了人为错误的风险**。

例如，具有id和password的用户的数据模型可以简单地定义为：

```java
public record User(int id, String password) {
}
```

正如我们所看到的，**我们在这里使用了一个新的关键字record**。这个简单的声明会自动为我们添加构造函数、getters、equals、hashCode和toString方法。

下面是使用JUnit的一些简单测试：

```java
private User user1 = new User(0, "UserOne");

@Test
void givenRecord_whenObjInitialized_thenValuesCanBeFetchedWithGetters() {
    assertEquals(0, user1.id());
    assertEquals("UserOne", user1.password());
}

@Test
void whenRecord_thenEqualsImplemented() {
    User user2 = user1;
    assertTrue(user1, user2);
}

@Test
void whenRecord_thenToStringImplemented() {
    assertTrue(user1.toString().contains("UserOne"));
}
```

## 4. 新的生产功能

除了两个新的预览功能外，Java 14还发布了一个具体的生产就绪功能。

### 4.1 友好的NullPointerExceptions([JEP 358](https://openjdk.org/jeps/358))

以前，NullPointerException的堆栈跟踪没有很多细节值得跟踪，除了给我们指出了哪个文件中给定行的某些值为空。

虽然这有用，但此信息仅建议一行进行调试，而不是为开发人员提供一个具体的错误位置，仅通过查看日志即可理解。

**现在，Java通过添加指出给定代码行中究竟什么为null的功能**[使这变得更容易](https://www.baeldung.com/java-14-nullpointerexception)。

例如，考虑以下简单代码段：

```java
int[] arr = null;
arr[0] = 1;
```

早些时候，在运行这段代码时，生成的错误信息应该为：

```shell
Exception in thread "main" java.lang.NullPointerException
at cn.tuyucheng.taketoday.MyClass.main(MyClass.java:27)
```

但是现在，给定相同的场景，异常的堆栈跟踪应该像下面这样：

```shell
java.lang.NullPointerException: Cannot store to int array because "a" is null
```

如我们所见，现在我们可以准确地知道是哪个变量导致了异常。

## 5. 孵化功能

这些是Java团队提出的非最终API和工具，并提供给我们进行实验。它们与预览功能不同，在包jdk.incubator中作为单独的模块提供。

### 5.1 外部内存访问API([JEP 370](https://openjdk.org/jeps/370))

这是一个[新的API](https://www.baeldung.com/java-foreign-memory-access)，允许Java程序以安全高效的方式访问堆外的外部内存，例如本地内存。

许多Java库(例如[mapDB](https://www.baeldung.com/mapdb)和[memcached](https://memcached.org/))确实会访问外部内存，现在是Java API本身提供更干净的解决方案的时候了。带着这个意图，团队提出了这个JEP作为其现有的访问非堆内存的方法的替代方案：ByteBuffer API和sun.misc.Unsafe API。

该API建立在MemorySegment、MemoryAddress和MemoryLayout的三个主要抽象之上，是访问堆内存和非堆内存的安全方式。

### 5.2 打包工具([JEP 343](https://openjdk.org/jeps/343))

传统上，为了交付Java代码，应用程序开发人员只需发送一个JAR文件，用户应该在他们自己的JVM中运行该文件。

但是，**用户更希望有一个安装程序，他们会双击以在其本机平台(例如Windows或macOS)上安装程序包**。

这个JEP正是为了做到这一点。开发人员可以使用[jlink](https://www.baeldung.com/jlink)将JDK压缩为所需的最少模块，然后使用此打包工具创建一个轻量级镜像，该镜像可以在Windows上作为exe安装，在macOS上作为dmg安装。

## 6. JVM/HotSpot特性

### 6.1 Windows([JEP 365](https://openjdk.org/jeps/365))和macOS([JEP 364](https://openjdk.org/jeps/364))上的ZGC–实验性

[Z Garbage Collector(ZGC)](https://www.baeldung.com/jvm-zgc-garbage-collector)是一种可扩展、低延迟的垃圾收集器，作为一项实验性功能首次在Java 11中引入。但最初，唯一受支持的平台是Linux/x64。

在收到关于ZGC for Linux的积极反馈后，**Java 14也将其支持移植到了Windows和macOS**。虽然它仍是一项实验性功能，但它已准备好[在下一个JDK版本](https://openjdk.org/jeps/377)中投入生产。

### 6.2 G1的NUMA感知内存分配([JEP 345](https://openjdk.org/jeps/345))

与并行收集器不同，到目前为止，G1垃圾收集器尚未实现非统一内存访问(NUMA)。

考虑到它为跨多个套接字运行单个JVM提供的性能改进，**引入这个JEP是为了让G1收集器也支持NUMA**。

目前，还没有计划将其复制到其他HotSpot收集器。

### 6.3 JFR事件流([JEP 349](https://openjdk.org/jeps/349))

通过这个增强功能，JDK的飞行记录器数据现已公开，以便可以对其进行持续监控。这涉及到对包jdk.jfr.consumer的修改，以便用户现在可以直接读取或流式传输记录数据。

## 7. 已弃用或删除的功能

Java 14弃用了一些特性：

-   Solaris和SPARC Ports([JEP 362](https://openjdk.org/jeps/362))：因为该Unix操作系统和RISC处理器在过去几年里没有积极开发
-   ParallelScavenge + SerialOld GC组合([JEP 366](https://openjdk.org/jeps/366))：因为这是一种很少使用的GC算法组合，并且需要大量的维护工作

还有一些删除的特性：

-   Concurrent Mark Sweep(CMS) Garbage Collector([JEP 363](https://openjdk.org/jeps/363))：已被Java 9弃用，此GC已由G1取代为默认GC。此外，现在还有其他性能更高的替代品可供使用，例如ZGC和Shenandoah，因此删除了
-   Pack200工具和API([JEP 367](https://openjdk.org/jeps/367))：这些工具和API在Java 11中被弃用，现在被移除

## 8. 总结

在本教程中，我们了解了Java 14的各种JEP。

总的来说，**这个版本的Java语言有16个主要功能**，包括预览功能、孵化器、弃用和删除。我们逐个介绍了所有这些，并通过示例查看了语言功能。