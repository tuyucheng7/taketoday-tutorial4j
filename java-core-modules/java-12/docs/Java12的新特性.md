## 1. 简介

在本教程中，我们将对Java 12附带的一些新功能进行快速、高级的概述。[官方文档](https://openjdk.org/projects/jdk/12/)中提供了所有新功能的完整列表。

## 2. 语言变化和特性

Java 12引入了许多新的语言特性，在本节中，我们将通过代码示例讨论一些最有趣的内容，以便更好地理解。

### 2.1 String类新方法

Java 12在[String](https://www.baeldung.com/java-string)类中提供了两个新方法。

第一个方法indent根据整数参数调整每行的缩进，如果参数大于零，将在每行的开头插入新的空格；另一方面，如果参数小于零，则会从每行的开头删除空格。如果给定行不包含足够的空格，则删除所有前导空格字符。

下面让我们看一个基本的例子，首先我们将文本缩进四个空格，然后删除整个缩进：

```java
String text = "Hello Tuyucheng!nThis isJava12 article.";

text = text.indent(4);
System.out.println(text);

text = text.indent(-10);
System.out.println(text);
```

输出如下所示：

```shell
    Hello Tuyucheng!
    This isJava12 article.

Hello Tuyucheng!
This isJava12 article.
```

请注意，即使我们传递的值-10超过了缩进数，也只有空格受到影响，其他字符保持不变。

第二个新方法是transform，它接收一个单参函数作为将应用于字符串的参数。例如，让我们使用transform方法反转字符串：

```java
@Test
void givenString_thenRevertValue() {
	String text = "Tuyucheng";
	String transformed = text.transform(value -> 
			new StringBuilder(value).reverse().toString());
    
	assertEquals("gnehcuyuT", transformed);
}
```

### 2.2 File::mismatch方法

Java 12在[nio.file.Files](https://www.baeldung.com/java-nio-2-file-api)工具类中引入了一个新的mismatch方法：

```java
public static long mismatch(Path path, Path path2) throws IOException
```

该方法用于比较两个文件并查找其内容中第一个不匹配字节的位置。

返回值将在0L到较小文件的字节大小的包含范围内，如果文件相同，则返回值为-1L。

让我们来看两个例子。在第一个中，我们将创建两个相同的文件并尝试找出不匹配的地方，返回值应该是-1L：

```java
@Test
void givenIdenticalFiles_thenShouldNotFindMismatch() throws IOException {
	Path filePath1 = Files.createTempFile("file1", ".txt");
	Path filePath2 = Files.createTempFile("file2", ".txt");
	Files.writeString(filePath1, "Java 12 Article");
	Files.writeString(filePath2, "Java 12 Article");
    
	long mismatch = Files.mismatch(filePath1, filePath2);
	assertEquals(-1, mismatch);
}
```

在第二个示例中，我们创建两个分别包含“Java 12 Article”和“Java 12 Tutorial”内容的文件，此时调用mismatch方法应该返回8L，因为8位置处是第一个不同的字节：

```java
@Test
void givenDifferentFiles_thenShouldFindMismatch() throws IOException {
	Path filePath3 = Files.createTempFile("file3", ".txt");
	Path filePath4 = Files.createTempFile("file4", ".txt");
	Files.writeString(filePath3, "Java 12 Article");
	Files.writeString(filePath4, "Java 12 Tutorial");
    
	long mismatch = Files.mismatch(filePath3, filePath4);
	assertEquals(8, mismatch);
}
```

### 2.3 Teeing收集器

Java 12中引入了一个新的Teeing收集器作为[Collectors](https://www.baeldung.com/java-8-collectors)类的补充：

```java
Collector<T, ?, R> teeing(Collector<? super T, ?, R1> downstream1,
                          Collector<? super T, ?, R2> downstream2, 
                          BiFunction<? super R1, ? super R2, R> merger)
```

它是两个下游收集器的组合，每个元素都由两个下游收集器处理，然后将它们的结果传递给merger函数并转化为最终结果。

Teeing收集器的示例用法是从一组数字中计算平均值。第一个收集器参数将对值求和，第二个参数将给出所有数字的计数。合并函数将获取这些结果并计算平均值：

```java
@Test
void givenSetOfNumbers_thenCalculateAverage() {
	double mean = Stream.of(1, 2, 3, 4, 5)
			.collect(Collectors.teeing(Collectors.summingDouble(i -> i),
					Collectors.counting(), (sum, count) -> sum / count));
	assertEquals(3.0, mean, 0);
}
```

### 2.4 紧凑的数字格式

Java 12附带了一个新的[数字格式化器](https://www.baeldung.com/java-number-formatting)CompactNumberFormat，它的设计是基于给定区域设置提供的模式，以较短的形式表示数字。

我们可以通过NumberFormat类中的getCompactNumberInstance方法获取它的实例：

```java
public static NumberFormat getCompactNumberInstance(
        Locale locale, 
        NumberFormat.Style formatStyle)
```

如前所述，locale参数负责提供正确的格式模式，格式样式可以是SHORT或LONG。为了更好地理解格式样式，让我们考虑美国语言环境中的数字10000。SHORT样式会将其格式化为“10K”，而LONG样式会将其格式化为“10000”。

现在让我们看一个示例，该示例将采用本文中的点赞数并使用两种不同的样式对其进行压缩：

```java
@Test
void givenNumber_thenCompactValues() {
	NumberFormat likesShort = NumberFormat.getCompactNumberInstance(new Locale("en", "US"), NumberFormat.Style.SHORT);
	likesShort.setMaximumFractionDigits(2);
	assertEquals("2.59K", likesShort.format(2592));
    
	NumberFormat likesLong = NumberFormat.getCompactNumberInstance(new Locale("en", "US"), NumberFormat.Style.LONG);
	likesLong.setMaximumFractionDigits(2);
	assertEquals("2.59 thousand", likesLong.format(2592));
}
```

## 3. 预览变化

一些新功能仅作为预览特性提供，要启用它们，我们需要在IDE中切换适当的设置或明确告诉编译器使用预览功能：

```bash
javac -Xlint:preview --enable-preview -source 12 src/main/java/File.java
```

### 3.1 Switch表达式(预览)

Java 12中引入的最受青睐的功能是[Switch表达式](https://www.baeldung.com/java-switch)。

作为演示，我们比较一下新旧switch语句。我们将使用它们根据来自LocalDate实例的DayOfWeek枚举来区分工作日和周末。

首先，让我们看一下旧语法：

```java
DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
String typeOfDay = "";
switch (dayOfWeek) {
	case MONDAY:
	case TUESDAY:
	case WEDNESDAY:
	case THURSDAY:
	case FRIDAY:
		typeOfDay = "Working Day";
		break;
	case SATURDAY:
	case SUNDAY:
		typeOfDay = "Day Off";
}
```

现在，让我们看看相同的逻辑开关表达式：

```java
typeOfDay = switch (dayOfWeek) {
	case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Working Day";
	case SATURDAY, SUNDAY -> "Day Off";
};
```

新的switch语句不仅更加紧凑和可读，它还消除了对break语句的需要，代码执行不会在第一次匹配后失败。

另一个显著的区别是我们可以将switch语句直接分配给变量，而以前是不可能的。

也可以在switch表达式中执行代码而不返回任何值：

```java
switch (dayOfWeek) {
	case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> System.out.println("Working Day");
	case SATURDAY, SUNDAY -> System.out.println("Day Off");
}
```

更复杂的逻辑应该用大括号括起来：

```java
case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> {
	// more logic
	System.out.println("Working Day")
}
```

请注意，我们可以在旧语法和新语法之间进行选择，Java 12的switch表达式只是一个扩展，而不是替代。

### 3.2 instanceof的模式匹配(预览版)

Java 12中引入的另一个预览功能是[instanceof的模式匹配](https://www.baeldung.com/java-pattern-matching-instanceof)。

在以前的Java版本中，例如，当将if语句与[instanceof](https://www.baeldung.com/java-instanceof)一起使用时，我们必须显式地对对象进行类型转换才能访问其功能：

```java
Object obj = "Hello World!";
if (obj instanceof String) {
	String s = (String) obj;
	int length = s.length();
}
```

使用Java 12，我们可以直接在语句中声明新的类型转换变量：

```java
if (obj instanceof String s) {
	int length = s.length();
}
```

编译器将自动为我们注入类型转换后的String变量。

## 4. JVM的变化

Java 12附带了几个JVM增强功能，在本节中，我们快速浏览一些最重要的内容。

### 4.1 Shenandoah：低停顿时间垃圾收集器

Shenandoah是一种实验性的[垃圾收集(GC)](https://www.baeldung.com/jvm-garbage-collectors)算法，目前未包含在默认的Java 12构建中。

它通过与正在运行的Java线程同时执行疏散工作来减少GC暂停时间，这意味着对于Shenandoah，暂停时间不依赖于堆的大小并且应该是一致的。收集200GB堆或2GB堆的垃圾应该具有类似的低暂停行为。

从版本15开始，Shenandoah将成为主线JDK构建的一部分。

### 4.2 微基准套件

Java 12为JDK源代码引入了一套大约100个微基准测试。

这些测试将允许在JVM上进行持续的性能测试，并且对于希望在JVM本身上工作或创建新的微基准测试的每个开发人员都非常有用。

### 4.3 默认CDS档案

类数据共享(CDS)功能有助于减少多个Java虚拟机之间的启动时间和内存占用，它使用构建时生成的默认类列表，其中包含选定的核心库类。

Java 12带来的变化是默认启用CDS存档，要在关闭CDS的情况下运行程序，我们需要将Xshare标志设置为关闭：

```shell
java -Xshare:off HelloWorld.java
```

请注意，这可能会延迟程序的启动时间。

## 5. 总结

在本文中，我们介绍了Java 12中实现的大部分新功能，并列出了其他一些值得注意的添加和删除。