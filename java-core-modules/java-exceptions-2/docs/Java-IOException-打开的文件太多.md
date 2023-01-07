## 1. 概述

在Java中处理文件时的一个常见陷阱是可能会用完可用的文件描述符。

在本教程中，我们将研究这种情况并提供两种方法来避免此问题。

## 2. JVM如何处理文件

尽管 JVM 在将我们与操作系统隔离开来方面做得非常出色，但它会将文件管理等低级操作委托给操作系统。

这意味着对于我们在Java应用程序中打开的每个文件，操作系统将分配一个文件描述符以将该文件与我们的Java进程相关联。一旦 JVM 处理完文件，它就会释放描述符。

现在，让我们深入探讨如何触发异常。

## 3. 泄漏文件描述符

回想一下，对于我们Java应用程序中的每个文件引用，我们在操作系统中都有一个相应的文件描述符。只有当文件引用实例被处理时，这个描述符才会被关闭。这将发生在垃圾收集阶段。

但是，如果引用保持活动状态并且打开的文件越来越多，那么最终操作系统将用完要分配的文件描述符。届时，它将把这种情况转发给 JVM，这将导致抛出IOException 。

我们可以通过一个简短的单元测试来重现这种情况：

```java
@Test
public void whenNotClosingResoures_thenIOExceptionShouldBeThrown() {
    try {
        for (int x = 0; x < 1000000; x++) {
            FileInputStream leakyHandle = new FileInputStream(tempFile);
        }
        fail("Method Should Have Failed");
    } catch (IOException e) {
        assertTrue(e.getMessage().containsIgnoreCase("too many open files"));
    } catch (Exception e) {
        fail("Unexpected exception");
    }
}

```

在大多数操作系统上，JVM 进程将在完成循环之前用完文件描述符，从而触发IOException。

让我们看看如何通过适当的资源处理来避免这种情况。

## 4.处理资源

正如我们之前所说，文件描述符在[垃圾收集](https://www.baeldung.com/jvm-garbage-collectors)期间由 JVM 进程释放。

但是如果我们没有正确关闭我们的文件引用，收集器可能会选择不销毁当时的引用，让描述符保持打开状态并限制我们可以打开的文件数量。

但是，我们可以通过确保在打开文件时确保在不再需要时关闭它来轻松解决此问题。

### 4.1. 手动释放引用

在 JDK 8 之前，手动释放引用是确保正确管理资源的常用方法。

我们不仅必须明确关闭我们打开的任何文件，而且还要确保即使我们的代码失败并抛出异常，我们也会这样做。这意味着使用[finally](https://www.baeldung.com/java-finally-keyword)关键字：

```java
@Test
public void whenClosingResoures_thenIOExceptionShouldNotBeThrown() {
    try {
        for (int x = 0; x < 1000000; x++) {
            FileInputStream nonLeakyHandle = null;
            try {
                nonLeakyHandle = new FileInputStream(tempFile);
            } finally {
                if (nonLeakyHandle != null) {
                    nonLeakyHandle.close();
                }
            }
        }
    } catch (IOException e) {
        assertFalse(e.getMessage().toLowerCase().contains("too many open files"));
        fail("Method Should Not Have Failed");
    } catch (Exception e) {
        fail("Unexpected exception");
    }
}

```

由于finally块总是被执行，它使我们有机会正确关闭我们的引用，从而限制打开的描述符的数量。

### 4.2. 使用 try-with-resources

JDK 7 为我们带来了一种更清洁的资源处理方式。它通常被称为[try-with-resources](https://www.baeldung.com/java-try-with-resources)并允许我们通过在try定义中包含资源来委托处理资源：

```java
@Test
public void whenUsingTryWithResoures_thenIOExceptionShouldNotBeThrown() {
    try {
        for (int x = 0; x < 1000000; x++) {
            try (FileInputStream nonLeakyHandle = new FileInputStream(tempFile)) {
                // do something with the file
            }
        }
    } catch (IOException e) {
        assertFalse(e.getMessage().toLowerCase().contains("too many open files"));
        fail("Method Should Not Have Failed");
    } catch (Exception e) {
        fail("Unexpected exception");
    }
}
```

在这里，我们在try语句中声明了nonLeakyHandle。因此，Java 会为我们关闭资源，而不是我们需要使用finally。

## 5.总结

如我们所见，未能正确关闭打开的文件可能会导致我们出现复杂的异常，并在整个程序中产生影响。通过适当的资源处理，我们可以确保这个问题永远不会出现。