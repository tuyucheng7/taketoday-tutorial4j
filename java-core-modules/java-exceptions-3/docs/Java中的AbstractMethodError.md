## 1. 概述

有时，我们可能会 在应用程序运行时遇到[AbstractMethodError 。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/AbstractMethodError.html)如果我们不太清楚这个错误，可能需要一段时间才能确定问题的原因。

在本教程中，我们将仔细研究AbstractMethodError。我们将了解AbstractMethodError是什么以及它何时会发生。

## 2.AbstractMethodError简介

当应用程序试图调用未实现的抽象方法时，将抛出 AbstractMethodError 。

我们知道，如果有未实现的抽象方法，编译器首先会报错。因此，应用程序根本不会构建。

我们可能会问我们如何在运行时得到这个错误？

首先，让我们看一下AbstractMethodError在Java异常层次结构中的位置：

```bash
java.lang.Object
|_java.lang.Throwable
  |_java.lang.Error
    |_java.lang.LinkageError
      |_java.lang.IncompatibleClassChangeError
        |_java.lang.AbstractMethodError
```

如上面的层次结构所示，此错误是IncompatibleClassChangeError的子类。正如其父类的名称所暗示的那样，当编译的类或 JAR 文件之间存在不兼容性时，通常会抛出AbstractMethodError 。

接下来，让我们了解这个错误是如何发生的。

## 3. 这个错误是如何发生的

当我们构建应用程序时，通常我们会导入一些库来简化我们的工作。

比方说，在我们的应用程序中，我们包含一个baeldung-queue 库。baeldung -queue 库是一个高级规范库，它只包含一个接口：

```java
public interface BaeldungQueue {
    void enqueue(Object o);
    Object dequeue();
}

```

此外，为了使用BaeldungQueue接口，我们导入了一个BaeldungQueue实现库：good-queue。good -queue 库也只有一个类：

```java
public class GoodQueue implements BaeldungQueue {
    @Override
    public void enqueue(Object o) {
       //implementation 
    }

    @Override
    public Object dequeue() {
        //implementation 
    }
}

```

现在，如果good-queue和baeldung-queue 都在类路径中，我们可以在我们的应用程序中创建一个BaeldungQueue实例：

```java
public class Application {
    BaeldungQueue queue = new GoodQueue();

    public void someMethod(Object element) {
        queue.enqueue(element);
        // ...
        queue.dequeue();
        // ...
    }
}

```

到目前为止，一切都很好。

有一天，我们了解到baeldung-queue发布了2.0版并且它附带了一个新方法：

```java
public interface BaeldungQueue {
    void enqueue(Object o);
    Object dequeue();

    int size();
}

```

我们想在我们的应用程序中使用新的size()方法。因此，我们将baeldung-queue库从1.0升级到2.0。但是，我们忘记检查是否有新版本的good-queue库实现了BaeldungQueue接口更改。

因此，我们 在类路径中有good-queue 1.0和baeldung-queue 2.0 。

此外，我们开始在我们的应用程序中使用新方法：

```java
public class Application {
    BaeldungQueue queue = new GoodQueue();

    public void someMethod(Object element) {
        // ...
        int size = queue.size(); //<-- AbstractMethodError will be thrown
        // ...
    }
}

```

我们的代码将毫无问题地编译。

但是，当 在运行时执行queue.size()行时，将抛出AbstractMethodError 。这是因为good-queue 1.0库没有在BaeldungQueue接口中实现size()方法。

## 4. 一个真实的例子

通过简单的BaeldungQueue和GoodQueue场景，我们可以了解到应用程序何时会抛出AbstractMethodError。 

在本节中，我们将看到AbstractMethodError的实际示例。

[java.sql.Connection](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/Connection.html)是JDBC API 中的一个重要接口。从 1.7 版本开始， Connection接口中添加了几个新方法，例如[getSchema()](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/Connection.html#getSchema())。

[H2 数据库](https://www.h2database.com/html/main.html)是一个非常快速的开源 SQL 数据库。从1.4.192版本开始，增加了对java.sql.Connection.getSchema()方法的支持。但是在之前的版本中，H2数据库还没有实现这个方法。

接下来，我们将从旧 H2 数据库版本1.4.191上的Java8 应用程序调用java.sql.Connection.getSchema()方法。让我们看看会发生什么。

让我们创建一个单元测试类来验证调用Connection.getSchema()方法是否会抛出AbstractMethodError：

```java
class AbstractMethodErrorUnitTest {
    private static final String url = "jdbc:h2:mem:A-DATABASE;INIT=CREATE SCHEMA IF NOT EXISTS myschema";
    private static final String username = "sa";

    @Test
    void givenOldH2Database_whenCallgetSchemaMethod_thenThrowAbstractMethodError() throws SQLException {
        Connection conn = DriverManager.getConnection(url, username, "");
        assertNotNull(conn);
        Assertions.assertThrows(AbstractMethodError.class, () -> conn.getSchema());
    }
}

```

如果我们运行测试，它将通过，确认对getSchema() 的调用抛出AbstractMethodError。

## 5.总结

有时我们可能会在运行时看到AbstractMethodError。在本文中，我们通过示例讨论了错误发生的时间。

当我们升级应用程序的一个库时，检查其他依赖项是否正在使用该库并考虑更新相关依赖项始终是一个好习惯。

另一方面，一旦我们遇到AbstractMethodError，如果对这个错误有很好的理解，我们可能会很快解决问题。