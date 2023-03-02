## 1. 概述

Java assert关键字允许开发人员快速验证程序的某些假设或状态。

在本文中，我们将了解如何使用Java assert关键字。

## 2. Java断言的历史

Java assert关键字是在Java 1.4中引入的，因此它已经存在了很长一段时间。但是，它仍然是一个鲜为人知的关键字，可以大大减少样板代码并使我们的代码更具可读性。

例如，在我们的代码中，我们经常需要验证某些可能会阻止我们的应用程序正常工作的条件。通常我们会这样写：

```java
Connection conn = getConnection();
if(conn == null) {
    throw new RuntimeException("Connection is null");
}
```

使用断言，我们可以使用单个断言语句删除if和throw语句。

## 3. 启用Java断言

因为Java断言使用assert关键字，所以不需要库或要导入的包。

请注意，在Java 1.4之前，使用“assert”一词来命名变量、方法等是完全合法的。这可能会在将旧代码与新JVM版本一起使用时造成命名冲突。

因此，为了向后兼容，JVM默认禁用断言验证。它们必须使用-enableassertions命令行参数或其简写-ea显式启用：

```bash
java -ea assertion.cn.tuyucheng.taketoday.Assertion
```

在此示例中，我们为所有类启用了断言。

我们还可以为特定的包和类启用断言：

```bash
java -ea:com.baeldung.assertion... assertion.cn.tuyucheng.taketoday.Assertion
```

在这里，我们为com.baeldung.assertion包中的所有类启用了断言。

同样，可以使用-disableassertions命令行参数或其简写 -da 为特定的包和类禁用它们。我们也可以同时使用所有这四个参数。

## 4. 使用Java断言

要添加断言，只需使用assert关键字并给它一个布尔条件：

```java
public void setup() {
    Connection conn = getConnection();
    assert conn != null;
}
```

Java还为接受字符串的断言提供了第二种语法，如果抛出一个字符串，它将用于构造AssertionError：

```java
public void setup() {
    Connection conn = getConnection();
    assert conn != null : "Connection is null";
}
```

在这两种情况下，代码都会检查与外部资源的连接是否返回非空值。如果该值为null，JVM将自动抛出AssertionError。

在第二种情况下，异常将具有额外的详细信息，这些信息将显示在堆栈跟踪中并有助于调试问题。

让我们看一下在启用断言的情况下运行我们的类的结果：

```bash
Exception in thread "main" java.lang.AssertionError: Connection is null
        at assertion.cn.tuyucheng.taketoday.Assertion.setup(Assertion.java:15)
        at assertion.cn.tuyucheng.taketoday.Assertion.main(Assertion.java:10)
```

## 5. 处理断言错误

类AssertionError扩展了Error，它本身扩展了Throwable。这意味着AssertionError是一个未经检查的异常。

因此，使用断言的方法不需要声明它们，进一步的调用代码不应该尝试和捕获它们。

AssertionErrors旨在指示应用程序中不可恢复的情况，因此永远不要尝试处理它们或尝试恢复。

## 6. 最佳实践

关于断言，要记住的最重要的事情是它们可以被禁用，所以永远不要假设它们会被执行。

因此，在使用断言时请记住以下几点：

- 始终在适当的地方检查null值和空Optionals
- 避免使用断言检查公共方法的输入，而是使用未经检查的异常，例如IllegalArgumentException或NullPointerException
- 不要在断言条件下调用方法，而是将方法的结果分配给局部变量并将该变量与断言一起使用
- 断言非常适合代码中永远不会执行的地方，例如switch语句的默认情况或永远不会结束的循环之后

## 7. 总结

Java assert关键字已经使用多年，但仍然是该语言的一个鲜为人知的特性。它可以帮助删除大量样板代码，使代码更具可读性，并有助于在程序开发的早期识别错误。

请记住，默认情况下不启用断言，因此永远不要假设它们会在代码中使用时执行。