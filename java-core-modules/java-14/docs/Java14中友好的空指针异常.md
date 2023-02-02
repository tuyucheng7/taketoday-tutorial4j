## 1. 概述

在本教程中，我们介绍[Java 14系列](https://www.baeldung.com/tag/java-14)中友好的空指针异常，这是此版本的JDK引入的一项新功能。

## 2. 传统的NullPointerException

在实践中，我们经常看到或编写将Java中的方法链接起来的代码链。但是，当这段代码抛出NullPointerException时，可能很难知道异常的确切位置。

假设我们想找出员工的电子邮件地址：

```java
String emailAddress = employee.getPersonalDetails().getEmailAddress().toLowerCase();
```

如果employee对象getPersonalDetails()或getEmailAddress()为空，则JVM抛出NullPointerException：

```shell
Exception in thread "main" java.lang.NullPointerException
  at cn.tuyucheng.taketoday.java14.npe.HelpfulNullPointerException.main(HelpfulNullPointerException.java:10)
```

异常的根本原因是什么？如果不使用debug很难确定哪个变量为空。**而且，JVM只会打印出导致异常的方法、文件名和行号**。

在下一节中，我们将看到Java 14如何通过JEP 358解决这个问题。

## 3. 友好的NullPointerException

SAP在2006年为其商业JVM实现了友好的NullPointerException。2019年2月，它被提议作为OpenJDK社区的一个增强，此后很快就成为了JEP。**因此，该功能已于2019年10月完成并推送到JDK 14版本中**。

**本质上，**[JEP 358](https://openjdk.java.net/jeps/358)**旨在通过描述哪个变量为null来提高JVM生成的NullPointerException的可读性**。

JEP 358通过描述空变量、方法、文件名和行号来带来详细的NullPointerException消息，它通过分析程序的字节码指令来工作。因此，它能够准确地确定哪个变量或表达式为null。

最重要的是，**在JDK 14中默认关闭详细的异常消息**。要启用它，我们需要使用命令行参数：

```shell
-XX:+ShowCodeDetailsInExceptionMessages
```

### 3.1 详细的异常消息

当我们在激活ShowCodeDetailsInExceptionMessages标志的情况下再次运行代码：

```shell
Exception in thread "main" java.lang.NullPointerException: 
  Cannot invoke "String.toLowerCase()" because the return value of 
"cn.tuyucheng.taketoday.java14.npe.HelpfulNullPointerException$PersonalDetails.getEmailAddress()" is null
  at cn.tuyucheng.taketoday.java14.npe.HelpfulNullPointerException.main(HelpfulNullPointerException.java:10)
```

这一次，从附加信息中，我们可以知道缺少员工个人详细信息的电子邮件地址导致了我们的异常。从这个增强中收获的好处可以在调试过程中节省我们的时间。

JVM由两部分组成详细的异常消息。**第一部分表示失败的操作，这是引用为null的结果，而第二部分标识了null引用的原因**：

```shell
Cannot invoke "String.toLowerCase()" because the return value of "getEmailAddress()" is null
```

为了构建异常消息，JEP 358重新创建了将空引用推送到操作数堆栈的源代码部分。

### 3.2 技术方面

现在我们已经很好地理解了如何使用友好的NullPointerException识别空引用，让我们来看看它的一些技术方面。

首先，**只有当JVM本身抛出NullPointerException时才会进行详细的消息计算，如果我们在Java代码中显式抛出异常，则不会执行计算**。这背后的原因是，在这些情况下，很可能我们已经在异常构造函数中传递了有意义的消息。

其次，**JEP 358延迟地计算消息，这意味着仅在我们打印异常消息时而不是在异常发生时计算**。因此，对于通常的JVM流程，我们捕获并重新抛出异常不应该有任何性能影响，因为我们并不总是打印异常消息。

最后，**详细的异常消息可能包括我们源代码中的局部变量名**，因此，我们可以认为这是一个潜在的安全风险。但是，这仅在我们运行在激活-g标志的情况下编译的代码时发生，它会生成调试信息并将其添加到我们的class文件中。

考虑一个简单的例子，我们编译它以包含此附加调试信息：

```java
Employee employee = null;
employee.getName();
```

当我们运行这段代码时，异常消息会打印局部变量名称：

```shell
Cannot invoke 
  "cn.tuyucheng.taketoday.java14.npe.HelpfulNullPointerException$Employee.getName()" 
because "employee" is null
```

相反，在没有额外调试信息的情况下，JVM只会在详细消息中提供它所知道的有关变量的信息：

```shell
Cannot invoke 
  "cn.tuyucheng.taketoday.java14.npe.HelpfulNullPointerException$Employee.getName()" 
because "<local1>" is null
```

**JVM打印由编译器分配的变量索引，而不是局部变量名(employee)**。

## 4. 总结

在这个快速教程中，我们了解了Java 14中友好的NullPointerException新功能。如上所示，改进的消息有助于我们更快地调试代码，因为异常消息中存在源代码详细信息。