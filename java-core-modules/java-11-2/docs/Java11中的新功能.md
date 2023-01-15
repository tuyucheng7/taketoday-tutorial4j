## 1. 概述

甲骨文于 2018 年 9 月发布了Java11，仅比其前身版本 10 晚了 6 个月。

Java 11 是继Java8 之后的第一个长期支持 (LTS) 版本 。Oracle 也在 2019 年 1 月停止支持Java 8。因此，我们中的很多人将升级到Java11。

在本教程中，我们将了解选择Java11 JDK 的选项。然后我们将探索Java11 中引入的新特性、删除的特性和性能增强。

## 进一步阅读：

## [Java 11 字符串 API 添加](https://www.baeldung.com/java-11-string-api)

了解Java11 中对 String API 的新增功能。

[阅读更多](https://www.baeldung.com/java-11-string-api)→

## [Lambda 参数的Java11 局部变量语法](https://www.baeldung.com/java-var-lambda-params)

了解如何在Java11 中将 var 语法与 lambda 表达式一起使用

[阅读更多](https://www.baeldung.com/java-var-lambda-params)→

## [使用Java11 否定谓词方法参考](https://www.baeldung.com/java-negate-predicate-method-reference)

了解如何使用Java11 否定谓词方法引用。

[阅读更多](https://www.baeldung.com/java-negate-predicate-method-reference)→

## 2. Oracle 与 Open JDK

Java 10 是我们可以在没有许可证的情况下进行商业使用的最后一个免费的 Oracle JDK 版本。从Java11 开始，Oracle 不再提供免费的长期支持 (LTS)。

值得庆幸的是，Oracle 继续提供[Open JDK](https://jdk.java.net/)版本，我们可以免费下载和使用。

除了 Oracle，我们还可以考虑[其他 Open JDK 提供商。](https://www.baeldung.com/oracle-jdk-vs-openjdk)

## 3. 开发者功能

让我们看一下对常用 API 的更改，以及对开发人员有用的其他一些功能。

### 3.1。新的字符串方法

Java 11为String类添加了一些[新方法](https://www.baeldung.com/java-11-string-api)：isBlank、lines、strip、stripLeading、stripTrailing和repeat。

让我们看看我们如何利用新方法从多行字符串中提取非空白、剥离的行：

```java
String multilineString = "Baeldung helps n n developers n explore Java.";
List<String> lines = multilineString.lines()
  .filter(line -> !line.isBlank())
  .map(String::strip)
  .collect(Collectors.toList());
assertThat(lines).containsExactly("Baeldung helps", "developers", "explore Java.");
```

这些方法可以减少操作字符串对象所涉及的样板数量，并使我们不必导入库。

对于strip方法，它们提供与更熟悉的trim方法相似的功能；但是，具有更好的控制和 Unicode 支持。

### 3.2. 新文件方法

此外，现在更容易从文件中读取和写入String 。

我们可以使用Files类中新的readString和writeString静态方法：

```java
Path filePath = Files.writeString(Files.createTempFile(tempDir, "demo", ".txt"), "Sample text");
String fileContent = Files.readString(filePath);
assertThat(fileContent).isEqualTo("Sample text");
```

### 3.3. 集合到数组

java.util.Collection接口包含一个新的默认toArray方法，该方法采用IntFunction参数。

这使得从集合中创建正确类型的数组变得更加容易：

```java
List sampleList = Arrays.asList("Java", "Kotlin");
String[] sampleArray = sampleList.toArray(String[]::new);
assertThat(sampleArray).containsExactly("Java", "Kotlin");
```

### 3.4. 非谓词方法

静态[not方法](https://www.baeldung.com/java-negate-predicate-method-reference)已添加到Predicate接口。我们可以使用它来否定一个现有的谓词，就像negate方法：

```java
List<String> sampleList = Arrays.asList("Java", "n n", "Kotlin", " ");
List withoutBlanks = sampleList.stream()
  .filter(Predicate.not(String::isBlank))
  .collect(Collectors.toList());
assertThat(withoutBlanks).containsExactly("Java", "Kotlin");
```

虽然not(isBlank)比isBlank .negate( )读起来更自然，但最大的优势是我们也可以将not与方法引用一起使用，例如not(String:isBlank)。

### 3.5. Lambda的局部变量语法

Java 11 中添加了对在 lambda 参数中使用[局部变量语法](https://www.baeldung.com/java-var-lambda-params)(var关键字)的支持。

我们可以利用此功能将修饰符应用于我们的局部变量，例如定义类型注解：

```java
List<String> sampleList = Arrays.asList("Java", "Kotlin");
String resultString = sampleList.stream()
  .map((@Nonnull var x) -> x.toUpperCase())
  .collect(Collectors.joining(", "));
assertThat(resultString).isEqualTo("JAVA, KOTLIN");
```

### 3.6. HTTP 客户端

Java 9 中引入了java.net.http包中的[新 HTTP 客户端](https://www.baeldung.com/java-9-http-client)。它现在已成为Java11 中的标准功能。

新的 HTTP API 提高了整体性能并提供对 HTTP/1.1 和 HTTP/2 的支持：

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

### 3.7. 基于巢的访问控制

Java 11 在 JVM 中引入了嵌套的概念[和](https://www.baeldung.com/java-nest-based-access-control)相关的访问规则。

Java 中的嵌套类意味着外部/主类及其所有嵌套类：

```java
assertThat(MainClass.class.isNestmateOf(MainClass.NestedClass.class)).isTrue();
```

嵌套类链接到NestMembers属性，而外部类链接到 NestHost属性：

```java
assertThat(MainClass.NestedClass.class.getNestHost()).isEqualTo(MainClass.class);
```

JVM 访问规则允许在巢友之间访问私有成员；但是，在以前的Java版本中，[反射 API](https://www.baeldung.com/java-reflection)拒绝了相同的访问。

Java 11 修复了这个问题，并提供了使用反射 API 查询新类文件属性的方法：

```java
Set<String> nestedMembers = Arrays.stream(MainClass.NestedClass.class.getNestMembers())
  .map(Class::getName)
  .collect(Collectors.toSet());
assertThat(nestedMembers).contains(MainClass.class.getName(), MainClass.NestedClass.class.getName());
```

### 3.8. 运行Java文件

这个版本的一个主要变化是我们不再需要使用javac显式编译Java源文件：

```bash
$ javac HelloWorld.java
$ java HelloWorld 
HelloJava8!
```

相反，我们可以使用java 命令[直接运行该文件：](https://www.baeldung.com/java-single-file-source-code)

```bash
$ java HelloWorld.java
HelloJava11!
```

## 4. 性能增强

现在让我们看一下主要目的是提高性能的几个新功能。

### 4.1。动态类文件常量

Java 类文件格式已扩展为支持名为CONSTANT_Dynamic的新常量池形式。

加载新的常量池会将创建委托给引导方法，就像链接调用动态 调用站点将链接委托给引导方法一样。

此功能增强了性能并针对语言设计者和编译器实现者。

### 4.2. 改进的 Aarch64 内在函数

Java 11 优化了 ARM64 或 AArch64 处理器上现有的字符串和数组内在函数。此外，为java.lang.Math的sin、cos和log方法实现了新的内在函数。

我们像其他任何东西一样使用内在函数；但是，内部函数由编译器以特殊方式处理。它利用 CPU 架构特定的汇编代码来提高性能。

### 4.3. 无操作垃圾收集器

一个名为 Epsilon 的新垃圾收集器可在Java11 中用作实验性功能。

它被称为 No-Op(无操作)，因为它分配内存但实际上并不收集任何垃圾。因此，Epsilon 适用于模拟内存不足错误。

显然 Epsilon 不适合典型的Java生产应用程序。但是，有一些特定的用例可能有用：

-   性能测试
-   内存压力测试
-   虚拟机接口测试和
-   寿命极短的工作

为了启用它，请使用-XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC 标志。

### 4.4. 飞行记录仪

[Java Flight Recorder](https://www.baeldung.com/java-flight-recorder-monitoring) (JFR) 现在在 Open JDK 中开源，而它曾经是 Oracle JDK 中的商业产品。JFR 是一个分析工具，我们可以使用它从正在运行的Java应用程序中收集诊断和分析数据。

要开始 120 秒的 JFR 录制，我们可以使用以下参数：

```bash
-XX:StartFlightRecording=duration=120s,settings=profile,filename=java-demo-app.jfr
```

我们可以在生产中使用 JFR，因为它的性能开销通常低于 1%。一旦时间过去，我们可以访问保存在 JFR 文件中的记录数据；但是，为了分析和可视化数据，我们需要使用另一个工具，称为[JDK Mission Control](https://www.baeldung.com/java-flight-recorder-monitoring) (JMC)。

## 5. 移除和弃用的模块

随着Java的发展，我们不能再使用它删除的任何特性，并且应该停止使用任何不推荐使用的特性。让我们快速浏览一下最值得注意的那些。

### 5.1。Java EE 和 CORBA

Java EE 技术的独立版本可在第三方站点上获得；因此，Java SE 不需要包含它们。

Java 9 已弃用选定的JavaEE 和 CORBA 模块。在第 11 版中，它现在已完全删除：

-   基于 XML 的 Web 服务的JavaAPI (java.xml.ws )
-   XML 绑定的Java体系结构(java.xml.bind )
-   JavaBeans 激活框架(java.activation )
-   通用注解(java.xml.ws.annotation )
-   公共对象请求代理架构(java.corba)
-   JavaTransaction API (java.transaction )

### 5.2. JMC 和 JavaFX

[JDK Mission Control](https://www.baeldung.com/java-flight-recorder-monitoring) (JMC) 不再包含在 JDK 中。JMC 的独立版本现在可以单独下载。

[JavaFX](https://www.baeldung.com/javafx)模块也是如此。JavaFX 将作为 JDK 之外的一组单独模块提供。

### 5.3. 已弃用的模块

此外，Java 11 弃用了以下模块：

-   Nashorn JavaScript 引擎，包括 JJS 工具
-   JAR 文件的 Pack200 压缩方案

## 6. 其他变化

Java 11 引入了一些重要的更改：

-   新的 ChaCha20 和 ChaCha20-Poly1305 密码实现取代了不安全的 RC4 流密码
-   支持 Curve25519 和 Curve448 的加密密钥协议替换现有的 ECDH 方案
-   将传输层安全性 (TLS) 升级到 1.3 版带来了安全性和性能改进
-   引入了低延迟垃圾收集器 ZGC，作为具有低暂停时间的实验性功能
-   支持 Unicode 10 带来更多字符、符号和表情符号

## 7. 总结

在本文中，我们探讨了Java11 的一些新特性。

我们介绍了 Oracle 和 Open JDK 之间的差异。我们还审查了 API 更改以及其他有用的开发功能、性能增强以及删除或弃用的模块。