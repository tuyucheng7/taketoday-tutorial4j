## 1. 概述

在Java中处理异常时，我们经常记录或只是显示堆栈跟踪。然而，有时，我们不想只打印堆栈跟踪，我们可能需要将堆栈跟踪写入文件、数据库甚至通过网络传输。

出于这些目的，将堆栈跟踪作为字符串将非常有用。不幸的是，Java 没有提供非常方便的方法来直接执行此操作。

## 2.用Core Java转换

让我们从核心库开始。

Exception类的函数printStackTrace()可以采用一个参数，可以是PrintStream或PrintWriter。因此，可以使用StringWriter将堆栈跟踪打印到String中：

```java
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
e.printStackTrace(pw);

```

然后，调用sw.toString()会将堆栈跟踪作为String返回。

## 3. Commons-Lang转换

虽然前面的方法是使用核心Java将堆栈跟踪转换为字符串的最简单方法，但它仍然有点麻烦。幸运的是，[Apache Commons-Lang](https://search.maven.org/classic/#search|ga|1|a%3A"commons-lang3")提供了一个函数来完成这项工作。

Apache Commons-Lang 是一个非常有用的库，它提供了JavaAPI 核心类中缺少的许多功能，包括可用于处理异常的类。

首先，让我们从项目配置开始。使用 Maven 时，我们只需将以下依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>

```

然后，在我们的例子中，最有趣的类是ExceptionUtils，它提供了处理异常的函数。使用此类，从异常中获取堆栈跟踪作为字符串非常简单：

```java
String stacktrace = ExceptionUtils.getStackTrace(e);

```

## 4。总结

将异常的堆栈跟踪作为字符串获取并不困难，但远非直观。本文介绍了两种实现方式，使用核心Java或使用 Apache Commons-Lang。

请记住，Java 9 将带来一个新的[StackWalking API](https://www.baeldung.com/java-9-stackwalking-api)，这将使事情变得更容易。