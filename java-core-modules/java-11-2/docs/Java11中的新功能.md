## 1. 概述

Oracle于2018年9月发布了Java 11，仅比其前身版本10晚了6个月。

**Java 11是继Java 8之后的第一个长期支持(LTS)版本**。Oracle也在2019年1月停止支持Java 8。因此，我们中的很多人将升级到Java 11。

在本教程中，我们将了解选择Java 11 JDK的选项。然后我们将探索Java 11中引入的新特性、删除的特性和性能增强。

## 2. Oracle与Open JDK

Java 10是我们可以在没有许可证的情况下进行商业使用的最后一个免费的Oracle JDK版本。从Java 11开始，Oracle不再提供免费的长期支持(LTS)。

**值得庆幸的是，Oracle继续提供**[Open JDK](https://jdk.java.net/)**版本，我们可以免费下载和使用。**

除了Oracle，我们还可以考虑[其他Open JDK提供商](https://www.baeldung.com/oracle-jdk-vs-openjdk)。

## 3. 开发者功能

让我们看一下对常用API的更改，以及对开发人员有用的其他一些功能。

### 3.1 新的字符串方法

**Java 11为String类添加了一些**[新方法](https://www.baeldung.com/java-11-string-api)：isBlank、lines、strip、stripLeading、stripTrailing和repeat。

让我们看看我们如何利用新方法从多行字符串中提取非空白、剥离的行：

```java
String multilineString = "Baeldung helps \n \n developers \n explore Java.";
List<String> lines = multilineString.lines()
    .filter(line -> !line.isBlank())
    .map(String::strip)
    .collect(Collectors.toList());
assertThat(lines).containsExactly("Baeldung helps", "developers", "explore Java.");
```

这些方法可以减少操作字符串对象所涉及的样板数量，并使我们不必导入库。

对于strip方法，它们提供与更熟悉的trim方法相似的功能；但是，具有更好的控制和Unicode支持。

### 3.2 新文件方法

此外，现在更容易从文件中读取和写入String。

**我们可以使用Files类中新的readString和writeString静态方法**：

```java
Path filePath = Files.writeString(Files.createTempFile(tempDir, "demo", ".txt"), "Sample text");
String fileContent = Files.readString(filePath);
assertThat(fileContent).isEqualTo("Sample text");
```

### 3.3 集合到数组的转换

java.util.Collection接口包含一个新的默认toArray方法，该方法采用IntFunction参数。

这使得从集合中创建正确类型的数组变得更加容易：

```java
List sampleList = Arrays.asList("Java", "Kotlin");
String[] sampleArray = sampleList.toArray(String[]::new);
assertThat(sampleArray).containsExactly("Java", "Kotlin");
```

### 3.4 not谓词方法

静态[not方法](https://www.baeldung.com/java-negate-predicate-method-reference)已添加到Predicate接口。我们可以使用它来否定一个现有的谓词，就像negate方法：

```java
List<String> sampleList = Arrays.asList("Java", "\n \n", "Kotlin", " ");
List withoutBlanks = sampleList.stream()
    .filter(Predicate.not(String::isBlank))
    .collect(Collectors.toList());
assertThat(withoutBlanks).containsExactly("Java", "Kotlin");
```

**虽然not(isBlank)比isBlank.negate()读起来更自然，但最大的优势是我们也可以将not与方法引用一起使用，例如not(String:isBlank)**。

### 3.5 Lambda的局部变量语法

Java 11中添加了对在lambda参数中使用[局部变量语法](https://www.baeldung.com/java-var-lambda-params)(var关键字)的支持。

我们可以利用此功能将修饰符应用于我们的局部变量，例如定义类型注解：

```java
List<String> sampleList = Arrays.asList("Java", "Kotlin");
String resultString = sampleList.stream()
    .map((@Nonnull var x) -> x.toUpperCase())
    .collect(Collectors.joining(", "));
assertThat(resultString).isEqualTo("JAVA, KOTLIN");
```

### 3.6 HTTP客户端

Java 9中引入了java.net.http包中的[新HTTP客户端](https://www.baeldung.com/java-9-http-client)。它现在已成为Java 11中的标准功能。

**新的HTTP API提高了整体性能并提供对HTTP/1.1和HTTP/2的支持**：

```java
HttpClient httpClient = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .connectTimeout(Duration.ofSeconds(20))
    .build();
HttpRequest httpRequest = HttpRequest.newBuilder()
    .GET()
    .uri(URI.create("http://localhost:" + port))
    .build();
HttpResponse httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
assertThat(httpResponse.body()).isEqualTo("Hello from the server!");
```

### 3.7 基于嵌套的访问控制

**Java 11在JVM中引入了**[嵌套]((https://www.baeldung.com/java-nest-based-access-control))**的概念和相关的访问规则**。

Java中的嵌套类意味着外部/主类及其所有嵌套类：

```java
assertThat(MainClass.class.isNestmateOf(MainClass.NestedClass.class)).isTrue();
```

嵌套类链接到NestMembers属性，而外部类链接到NestHost属性：

```java
assertThat(MainClass.NestedClass.class.getNestHost()).isEqualTo(MainClass.class);
```

JVM访问规则允许在巢友之间访问私有成员；但是，在以前的Java版本中，[反射API](https://www.baeldung.com/java-reflection)拒绝了相同的访问。

Java 11修复了这个问题，并提供了使用反射API查询新类文件属性的方法：

```java
Set<String> nestedMembers = Arrays.stream(MainClass.NestedClass.class.getNestMembers())
    .map(Class::getName)
    .collect(Collectors.toSet());
assertThat(nestedMembers).contains(MainClass.class.getName(), MainClass.NestedClass.class.getName());
```

### 3.8 运行Java文件

这个版本的一个主要变化是**我们不再需要使用javac显式编译Java源文件**：

```bash
$ javac HelloWorld.java
$ java HelloWorld
Hello Java 8!
```

相反，我们可以使用java命令[直接运行该文件](https://www.baeldung.com/java-single-file-source-code)：

```bash
$ java HelloWorld.java
Hello Java 11!
```

## 4. 性能增强

现在让我们看一下主要目的是提高性能的几个新功能。

### 4.1 动态class文件常量

Java class文件格式已扩展为支持名为CONSTANT_Dynamic的新常量池形式。

加载新的常量池会将创建委托给引导方法，就像链接调用动态调用站点将链接委托给引导方法一样。

此功能增强了性能并针对语言设计者和编译器实现者。

### 4.2. 改进的Aarch64内联函数

Java 11优化了ARM64或AArch64处理器上现有的字符串和数组内在函数。此外，为java.lang.Math的sin、cos和log方法实现了新的内在函数。

我们像其他任何东西一样使用内在函数；但是，内部函数由编译器以特殊方式处理。它利用CPU架构特定的汇编代码来提高性能。

### 4.3 无操作垃圾收集器

一个名为Epsilon的新垃圾收集器可在Java 11中用作实验性功能。

它被称为No-Op(无操作)，因为它分配内存但实际上并不收集任何垃圾。因此，Epsilon适用于模拟内存不足错误。

显然Epsilon不适合典型的Java生产应用程序。但是，有一些特定的用例可能有用：

-   性能测试
-   内存压力测试
-   虚拟机接口测试和
-   寿命极短的工作

为了启用它，请使用-XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC标志。

### 4.4 飞行记录器

[Java Flight Recorder](https://www.baeldung.com/java-flight-recorder-monitoring)**(JFR)现在在Open JDK中开源**，而它曾经是Oracle JDK中的商业产品。JFR是一个分析工具，我们可以使用它从正在运行的Java应用程序中收集诊断和分析数据。

要开始120秒的JFR录制，我们可以使用以下参数：

```java
-XX:StartFlightRecording=duration=120s,settings=profile,filename=java-demo-app.jfr
```

我们可以在生产中使用JFR，因为它的性能开销通常低于1%。一旦时间过去，我们可以访问保存在JFR文件中的记录数据；但是，为了分析和可视化数据，我们需要使用另一个工具，称为[JDK Mission Control](https://www.baeldung.com/java-flight-recorder-monitoring)(JMC)。

## 5. 移除和弃用的模块

随着Java的发展，我们不能再使用它删除的任何特性，并且应该停止使用任何不推荐使用的特性。让我们快速浏览一下最值得注意的那些。

### 5.1 Java EE和CORBA

Java EE技术的独立版本可在第三方站点上获得；因此，Java SE不需要包含它们。

Java 9已弃用选定的Java EE和CORBA模块。在第11版本中，它现在已完全删除：

-   基于XML的Web服务的Java API(java.xml.ws)
-   XML绑定的Java体系结构(java.xml.bind)
-   Java Beans Activation框架(java.activation)
-   通用注解(java.xml.ws.annotation)
-   公共对象请求代理架构(java.corba)
-   Java Transaction API(java.transaction)

### 5.2 JMC和JavaFX

[JDK Mission Control](https://www.baeldung.com/java-flight-recorder-monitoring)(JMC)不再包含在JDK中。JMC的独立版本现在可以单独下载。

[JavaFX](https://www.baeldung.com/javafx)模块也是如此。JavaFX将作为JDK之外的一组单独模块提供。

### 5.3 已弃用的模块

此外，Java 11弃用了以下模块：

-   Nashorn JavaScript引擎，包括JJS工具
-   JAR文件的Pack200压缩方案

## 6. 其他变化

Java 11引入了一些重要的更改：

-   新的ChaCha20和ChaCha20-Poly1305 密码实现取代了不安全的RC4流密码
-   支持Curve25519和Curve448的加密密钥协议替换现有的ECDH方案
-   将传输层安全性(TLS)升级到1.3版带来了安全性和性能改进
-   引入了低延迟垃圾收集器ZGC，作为具有低暂停时间的实验性功能
-   支持Unicode 10带来更多字符、符号和表情符号

## 7. 总结

在本文中，我们探讨了Java 11的一些新特性。

我们介绍了Oracle和Open JDK之间的差异。我们还审查了API更改以及其他有用的开发功能、性能增强以及删除或弃用的模块。