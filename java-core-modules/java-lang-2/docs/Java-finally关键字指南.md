## 1. 概述

在本教程中，我们将探讨Java中的finally关键字。我们将看到如何在错误处理中将它与try/catch块一起使用。虽然finally旨在保证代码的执行，但我们将讨论 JVM 不执行它的异常情况。

我们还将讨论一些常见的陷阱，其中finally块可能会产生意想不到的结果。

## 2.最后是什么？

finally定义了我们使用的代码块以及try关键字。它定义了始终在try和任何catch块之后、方法完成之前运行的代码。

不管异常是抛出还是捕获，finally 块都会执行。

### 2.1. 一个简单的例子

让我们看看 finally 中的 try-catch-finally块：

```java
try {
    System.out.println("The count is " + Integer.parseInt(count));
} catch (NumberFormatException e) {
    System.out.println("No count");
} finally {
    System.out.println("In finally");
}

```

在此示例中，无论参数count的值如何，JVM 都会执行finally块并打印“In finally”。

### 2.2. 在没有catch块的情况下使用finally

此外，无论是否存在catch块，我们都可以将finally块与try块一起使用：

```java
try {
    System.out.println("Inside try");
} finally {
    System.out.println("Inside finally");
}
```

我们将得到输出：

```java
Inside try
Inside finally
```

### 2.3. 为什么finally有用

我们通常使用finally块来执行清理代码，如关闭连接、关闭文件或释放线程，因为它会在不考虑异常的情况下执行。

注意： [try-with-resources](https://www.baeldung.com/java-try-with-resources)也可用于关闭资源而不是finally块。

## 3.finally什么时候执行

让我们看一下 JVM 何时执行finally块的所有排列，以便我们更好地理解它。

### 3.1. 没有异常被抛出

当 try块完成时， finally 块被执行，即使没有异常：

```java
try {
    System.out.println("Inside try");
} finally {
    System.out.println("Inside finally");
}
```

在这个例子中，我们没有从try块中抛出异常。因此，JVM 会执行try和finally 块中的所有代码。

这输出：

```java
Inside try
Inside finally
```

### 3.2. 抛出异常但未处理

如果有异常但没有被捕获，finally块仍然会被执行：

```java
try {
    System.out.println("Inside try");
    throw new Exception();
} finally {
    System.out.println("Inside finally");
}
```

即使在未处理异常的情况下，JVM 也会执行finally块。

输出将是：

```java
Inside try
Inside finally
Exception in thread "main" java.lang.Exception
```

### 3.3. 抛出并处理异常

如果出现异常并且被catch块捕获，则 finally块仍会执行：

```java
try {
    System.out.println("Inside try");
    throw new Exception();
} catch (Exception e) {
    System.out.println("Inside catch");
} finally {
    System.out.println("Inside finally");
}
```

在这种情况下，catch块处理抛出的异常，然后 JVM 执行finally块并产生输出：

```java
Inside try
Inside catch
Inside finally
```

### 3.4. 方法从try块返回

即使从方法返回也不会阻止 finally块运行：

```java
try {
    System.out.println("Inside try");
    return "from try";
} finally {
    System.out.println("Inside finally");
}
```

在这里，即使该方法有return语句，JVM 也会在将控制权移交给调用方法之前执行finally块。

我们将得到输出：

```java
Inside try
Inside finally
```

### 3.5. 方法从catch块返回

当catch块包含 return语句时，finally块仍然被调用：

```java
try {
    System.out.println("Inside try");
    throw new Exception();
} catch (Exception e) {
    System.out.println("Inside catch");
    return "from catch";
} finally {
    System.out.println("Inside finally");
}
```

当我们从try块中抛出异常时， catch块会处理该异常。尽管catch块中有 return 语句，但 JVM在将控制权移交给调用方法之前会执行finally块，并输出：

```java
Inside try
Inside catch
Inside finally
```

## 4. finally什么时候不执行

尽管我们总是希望 JVM 执行finally块中的语句，但在某些情况下 JVM 不会执行finally块。

我们可能已经预料到，如果操作系统停止了我们的程序，那么该程序将没有机会执行其所有代码。我们还可以采取一些措施来类似地阻止挂起的finally块的执行。

### 4.1. 调用System.exit

在这种情况下，我们通过调用[System.exit](https://www.baeldung.com/java-system-exit)终止 JVM ，因此 JVM 不会执行我们的finally块：

```java
try {
    System.out.println("Inside try");
    System.exit(1);
} finally {
    System.out.println("Inside finally");
}
```

这输出：

```java
Inside try
```

### 4.2. 调用停止

与System.exit类似，调用[Runtime.halt](https://www.baeldung.com/java-runtime-halt-vs-system-exit) 也会停止执行，JVM 不会执行任何finally块：

```java
try {
    System.out.println("Inside try");
    Runtime.getRuntime().halt(1);
} finally {
    System.out.println("Inside finally");
}
```

因此，输出将是：

```java
Inside try
```

### 4.3. 守护线程

如果一个[守护线程](https://www.baeldung.com/java-daemon-thread)进入try/finally块的执行，并且所有其他非守护线程在守护线程执行finally块之前退出，则 JVM 不会等待守护线程完成finally块的执行：

```java
Runnable runnable = () -> {
    try {
        System.out.println("Inside try");
    } finally {
        try {
            Thread.sleep(1000);
            System.out.println("Inside finally");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
};
Thread regular = new Thread(runnable);
Thread daemon = new Thread(runnable);
daemon.setDaemon(true);
regular.start();
Thread.sleep(300);
daemon.start();
```

在本例中，runnable一进入方法就打印“Inside try”，等待 1 秒后打印“Inside finally”。

在这里，我们以较小的延迟启动常规线程和守护线程。当普通线程执行finally块时，守护线程还在try块中等待。随着常规线程执行完毕退出，JVM也退出，并不等待守护线程执行完finally块。

这是输出：

```java
Inside try
Inside try
Inside finally
```

### 4.4. JVM 进入无限循环

这是一个 包含无限 while循环的try块：

```java
try {
    System.out.println("Inside try");
    while (true) {
    }
} finally {
    System.out.println("Inside finally");
}
```

虽然它不是特定于 finally，但值得一提的是，如果try或catch块包含无限循环，则 JVM 将永远不会到达该循环之外的任何块。

## 5. 常见陷阱

在使用finally块时，我们必须避免一些常见的陷阱。

虽然完全合法，但在finally块中使用return语句或抛出异常被认为是不好的做法，我们应该不惜一切代价避免它。

### 5.1. 忽略异常

finally块中的return语句忽略未捕获的异常：

```java
try {
    System.out.println("Inside try");
    throw new RuntimeException();
} finally {
    System.out.println("Inside finally");
    return "from finally";
}
```

在这种情况下，该方法忽略抛出的RuntimeException并返回值“from finally”。

### 5.2. 忽略其他返回语句

finally块中的return语句忽略try或catch块中的任何其他 return 语句。只有finally块中的return语句执行：

```java
try {
    System.out.println("Inside try");
    return "from try";
} finally {
    System.out.println("Inside finally");
    return "from finally";
}
```

在此示例中，该方法始终返回“from finally”并完全忽略try块中的return语句。这可能是一个很难发现的错误，这就是为什么我们应该避免 在 finally块中使用return的原因。

### 5.3. 更改抛出或返回的内容

此外，在从finally块中抛出异常的情况下，该方法会忽略try和catch块中抛出的异常或返回语句：

```java
try {
    System.out.println("Inside try");
    return "from try";
} finally {
    throw new RuntimeException();
}
```

此方法从不返回值并且总是抛出RuntimeException。

虽然我们可能不会像本例中那样故意从finally块中抛出异常，但我们仍然可能会遇到这个问题。当我们在finally块中使用的清理方法抛出异常时，就会发生这种情况。

## 六，总结

在本文中，我们讨论了finally块在Java中的作用以及如何使用它们。然后，我们研究了 JVM 执行它们的不同情况，以及一些可能不执行的情况。

最后，我们研究了一些与使用finally块相关的常见陷阱。