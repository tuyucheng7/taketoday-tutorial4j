## 1. 概述

在本快速教程中，我们将学习Java中的抑制异常。简而言之，被抑制的异常是抛出但不知何故被忽略的异常。在Java中，一个常见的场景是当finally块抛出异常时。然后抑制最初在try块中抛出的任何异常。

从Java7 开始，我们现在可以在[Throwable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Throwable.html)类上使用两种方法来处理我们抑制的异常：addSuppressed和getSuppressed。我们应该注意到[try-with-resources](https://www.baeldung.com/java-try-with-resources)结构也在Java7 中被引入。我们将在示例中看到它们之间的关系。

## 2. 行动中的抑制异常

### 2.1. 抑制异常场景

让我们首先快速浏览一个示例，其中原始异常被finally块中发生的异常抑制：

```java
public static void demoSuppressedException(String filePath) throws IOException {
    FileInputStream fileIn = null;
    try {
        fileIn = new FileInputStream(filePath);
    } catch (FileNotFoundException e) {
        throw new IOException(e);
    } finally {
        fileIn.close();
    }
}
```

只要我们提供现有文件的路径，就不会抛出任何异常，并且该方法将按预期工作。

但是，假设我们提供了一个不存在的文件：

```java
@Test(expected = NullPointerException.class)
public void givenNonExistentFileName_whenAttemptFileOpen_thenNullPointerException() throws IOException {
    demoSuppressedException("/non-existent-path/non-existent-file.txt");
}
```

在这种情况下，try块在尝试打开不存在的文件时将抛出FileNotFoundException 。因为fileIn对象从未被初始化，所以当我们试图在finally块中关闭它时，它会抛出NullPointerException。我们的调用方法只会得到 NullPointerException ，而且最初的问题是什么并不明显：文件不存在。

### 2.2. 添加被抑制的异常

现在让我们看看如何利用Throwable.addSuppressed方法来提供原始异常：

```java
public static void demoAddSuppressedException(String filePath) throws IOException {
    Throwable firstException = null;
    FileInputStream fileIn = null;
    try {
        fileIn = new FileInputStream(filePath);
    } catch (IOException e) {
        firstException = e;
    } finally {
        try {
            fileIn.close();
        } catch (NullPointerException npe) {
            if (firstException != null) {
                npe.addSuppressed(firstException);
            }
            throw npe;
        }
    }
}
```

让我们进行单元测试，看看getSuppressed在这种情况下是如何工作的：

```java
try {
    demoAddSuppressedException("/non-existent-path/non-existent-file.txt");
} catch (Exception e) {
    assertThat(e, instanceOf(NullPointerException.class));
    assertEquals(1, e.getSuppressed().length);
    assertThat(e.getSuppressed()[0], instanceOf(FileNotFoundException.class));
}
```

我们现在可以从提供的抑制异常数组中访问原始异常。

### 2.3. 使用try-with-resources

最后，让我们看一个使用try-with-resources的例子，其中close方法抛出异常。Java 7 引入了try-with-resources结构和用于资源管理的AutoCloseable接口。

首先，让我们创建一个实现AutoCloseable 的资源：

```java
public class ExceptionalResource implements AutoCloseable {
    
    public void processSomething() {
        throw new IllegalArgumentException("Thrown from processSomething()");
    }

    @Override
    public void close() throws Exception {
        throw new NullPointerException("Thrown from close()");
    }
}
```

接下来，让我们在try-with-resources块中使用我们的ExceptionalResource：

```java
public static void demoExceptionalResource() throws Exception {
    try (ExceptionalResource exceptionalResource = new ExceptionalResource()) {
        exceptionalResource.processSomething();
    }
}
```

最后，让我们回到我们的单元测试，看看异常是如何产生的：

```java
try {
    demoExceptionalResource();
} catch (Exception e) {
    assertThat(e, instanceOf(IllegalArgumentException.class));
    assertEquals("Thrown from processSomething()", e.getMessage());
    assertEquals(1, e.getSuppressed().length);
    assertThat(e.getSuppressed()[0], instanceOf(NullPointerException.class));
    assertEquals("Thrown from close()", e.getSuppressed()[0].getMessage());
}
```

我们应该注意，在使用AutoCloseable时，它是close方法中抛出的异常被抑制。抛出原始异常。

## 3.总结

在这个简短的教程中，我们了解了什么是抑制异常以及它们是如何发生的。然后，我们了解了如何使用addSuppressed和getSuppressed方法来访问那些被抑制的异常。最后，我们看到了在使用try-with-resources块时抑制异常是如何工作的。