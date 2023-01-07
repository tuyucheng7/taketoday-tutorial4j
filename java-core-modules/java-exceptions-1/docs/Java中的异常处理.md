## 1. 概述

在本教程中，我们将了解Java中异常处理的基础知识以及它的一些问题。

## 2.首要原则

### 2.1. 它是什么？

为了更好地理解异常和异常处理，我们来做一个真实的比较。

想象一下，我们在线订购了一件产品，但在途中，交货失败。一个好的公司可以处理这个问题并优雅地重新安排我们的包裹，以便它仍然准时到达。

同样，在Java中，代码在执行我们的指令时可能会遇到错误。良好的异常处理可以处理错误并优雅地重新路由程序，从而为用户提供仍然积极的体验。

### 2.2. 为什么使用它？ 

我们通常在一个理想化的环境中编写代码：文件系统总是包含我们的文件，网络是健康的，JVM 总是有足够的内存。有时我们称之为“快乐之路”。

但是，在生产中，文件系统可能会损坏、网络会崩溃并且 JVM 会耗尽内存。我们代码的健康取决于它如何处理“不愉快的路径”。

我们必须处理这些情况，因为它们会对应用程序的流程产生负面影响并形成异常：

```java
public static List<Player> getPlayers() throws IOException {
    Path path = Paths.get("players.dat");
    List<String> players = Files.readAllLines(path);

    return players.stream()
      .map(Player::new)
      .collect(Collectors.toList());
}
```

此代码选择不处理 IOException，而是将其向上传递到调用堆栈。在理想化的环境中，代码运行良好。

但是如果players.dat 丢失，生产中会发生什么？

```java
Exception in thread "main" java.nio.file.NoSuchFileException: players.dat <-- players.dat file doesn't exist
    at sun.nio.fs.WindowsException.translateToIOException(Unknown Source)
    at sun.nio.fs.WindowsException.rethrowAsIOException(Unknown Source)
    // ... more stack trace
    at java.nio.file.Files.readAllLines(Unknown Source)
    at java.nio.file.Files.readAllLines(Unknown Source)
    at Exceptions.getPlayers(Exceptions.java:12) <-- Exception arises in getPlayers() method, on line 12
    at Exceptions.main(Exceptions.java:19) <-- getPlayers() is called by main(), on line 19
```

如果不处理这个异常，一个原本健康的程序可能会完全停止运行！我们需要确保我们的代码在出现问题时有一个计划。

还要注意异常的另一个好处，那就是堆栈跟踪本身。由于这个堆栈跟踪，我们通常可以在不需要附加调试器的情况下查明有问题的代码。

## 3.异常层次结构

最终，异常 只是Java对象，它们都从 Throwable扩展而来：

```java
              ---> Throwable <--- 
              |    (checked)     |
              |                  |
              |                  |
      ---> Exception           Error
      |    (checked)        (unchecked)
      |
RuntimeException
  (unchecked)
```

异常情况主要分为三类：

-   检查异常
-   未经检查的异常/运行时异常
-   错误

运行时异常和未经检查的异常指的是同一件事。我们经常可以互换使用它们。 

### 3.1. 检查异常

Checked Exception是Java编译器要求我们处理的异常。我们必须以声明方式将异常抛出调用堆栈，或者我们必须自己处理它。稍后将详细介绍这两者。

[Oracle 的文档](https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html)告诉我们在可以合理地预期方法的调用者能够恢复时使用已检查的异常。

检查异常的几个例子是IOException和 ServletException。

### 3.2. 未经检查的异常

未检查异常是Java编译器不需要我们处理的异常。

简单地说，如果我们创建一个扩展 RuntimeException 的异常，它将被取消检查；否则，它将被检查。

虽然这听起来很方便，但[Oracle 的文档](https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html)告诉我们这两个概念都有很好的理由，比如区分情境错误(选中)和使用错误(未选中)。

未经检查的异常的一些示例是 NullPointerException、 IllegalArgumentException 和 SecurityException。

### 3.3. 错误

错误表示严重且通常无法恢复的情况，例如库不兼容、无限递归或内存泄漏。

即使它们没有扩展 RuntimeException，它们也未被检查。

在大多数情况下，我们处理、实例化或扩展Errors会很奇怪 。通常，我们希望它们一直向上传播。

StackOverflowError和 OutOfMemoryError是几个错误示例 。

## 4.处理异常

在JavaAPI 中，有很多地方可能出错，其中一些地方在签名或 Javadoc 中标有异常：

```java
/
  @exception FileNotFoundException ...
 /
public Scanner(String fileName) throws FileNotFoundException {
   // ...
}
```

如前所述，当我们调用这些“有风险”的方法时，我们必须处理已检查的异常，并且我们可能会处理未检查的异常。Java 为我们提供了几种方法来做到这一点：

### 4.1. 投掷

“处理”异常的最简单方法是重新抛出它：

```java
public int getPlayerScore(String playerFile)
  throws FileNotFoundException {
 
    Scanner contents = new Scanner(new File(playerFile));
    return Integer.parseInt(contents.nextLine());
}
```

因为FileNotFoundException 是一个检查异常，这是满足编译器的最简单方法，但这确实意味着任何调用我们方法的人现在也需要处理它！

parseInt可以抛出NumberFormatException，但因为它未被选中，所以我们不需要处理它。

### 4.2. 试着抓

如果我们想尝试自己处理异常，我们可以使用 try-catch块。我们可以通过重新抛出异常来处理它：

```java
public int getPlayerScore(String playerFile) {
    try {
        Scanner contents = new Scanner(new File(playerFile));
        return Integer.parseInt(contents.nextLine());
    } catch (FileNotFoundException noFile) {
        throw new IllegalArgumentException("File not found");
    }
}
```

或者通过执行恢复步骤：

```java
public int getPlayerScore(String playerFile) {
    try {
        Scanner contents = new Scanner(new File(playerFile));
        return Integer.parseInt(contents.nextLine());
    } catch ( FileNotFoundException noFile ) {
        logger.warn("File not found, resetting score.");
        return 0;
    }
}
```

### 4.3. 最后

现在，有时我们有需要执行的代码，无论是否发生异常，这就是 finally关键字的用武之地。

到目前为止，在我们的示例中，隐藏着一个令人讨厌的错误，即默认情况下Java不会将文件句柄返回给操作系统。

当然，无论我们是否可以读取文件，我们都希望确保我们进行了适当的清理！

让我们先试试这个“懒惰”的方式：

```java
public int getPlayerScore(String playerFile)
  throws FileNotFoundException {
    Scanner contents = null;
    try {
        contents = new Scanner(new File(playerFile));
        return Integer.parseInt(contents.nextLine());
    } finally {
        if (contents != null) {
            contents.close();
        }
    }
}

```

在这里， finally块指示我们希望Java运行什么代码，而不管尝试读取文件时发生了什么。

即使 FileNotFoundException在调用堆栈中抛出，Java 也会 在此之前调用finally的内容。

我们还可以处理异常并确保我们的资源被关闭：

```java
public int getPlayerScore(String playerFile) {
    Scanner contents;
    try {
        contents = new Scanner(new File(playerFile));
        return Integer.parseInt(contents.nextLine());
    } catch (FileNotFoundException noFile ) {
        logger.warn("File not found, resetting score.");
        return 0; 
    } finally {
        try {
            if (contents != null) {
                contents.close();
            }
        } catch (IOException io) {
            logger.error("Couldn't close the reader!", io);
        }
    }
}
```

因为close也是一个“有风险”的方法，我们也需要捕获它的异常！

这可能看起来很复杂，但我们需要每一部分来正确处理可能出现的每个潜在问题。

### 4.4. 试试资源

幸运的是，从Java7 开始，我们可以在处理扩展AutoCloseable 的东西时简化上述语法 ：

```java
public int getPlayerScore(String playerFile) {
    try (Scanner contents = new Scanner(new File(playerFile))) {
      return Integer.parseInt(contents.nextLine());
    } catch (FileNotFoundException e ) {
      logger.warn("File not found, resetting score.");
      return 0;
    }
}
```

当我们在 try 声明中放置可自动关闭的 引用时 ，我们就不需要自己关闭资源。

不过，我们仍然可以使用 finally块来执行我们想要的任何其他类型的清理。

请查看我们专门针对[try -with-resources](https://www.baeldung.com/java-try-with-resources)的文章以了解更多信息。

### 4.5. 多个catch 块

有时，代码可以抛出多个异常，我们可以有多个 catch 块分别处理：

```java
public int getPlayerScore(String playerFile) {
    try (Scanner contents = new Scanner(new File(playerFile))) {
        return Integer.parseInt(contents.nextLine());
    } catch (IOException e) {
        logger.warn("Player file wouldn't load!", e);
        return 0;
    } catch (NumberFormatException e) {
        logger.warn("Player file was corrupted!", e);
        return 0;
    }
}
```

如果需要，多个捕获让我们有机会以不同方式处理每个异常。

还要注意这里我们没有捕获 FileNotFoundException，那是因为它 扩展了 IOException。因为我们正在捕获 IOException，所以Java将考虑它的任何子类也被处理。

不过，假设我们需要将 FileNotFoundException 与更一般的 IOException区别对待：

```java
public int getPlayerScore(String playerFile) {
    try (Scanner contents = new Scanner(new File(playerFile)) ) {
        return Integer.parseInt(contents.nextLine());
    } catch (FileNotFoundException e) {
        logger.warn("Player file not found!", e);
        return 0;
    } catch (IOException e) {
        logger.warn("Player file wouldn't load!", e);
        return 0;
    } catch (NumberFormatException e) {
        logger.warn("Player file was corrupted!", e);
        return 0;
    }
}
```

Java 让我们单独处理子类异常，记住将它们放在捕获列表的较高位置。

### 4.6. 联合 捕获 块

然而，当我们知道我们处理错误的方式将是相同的时，Java 7 引入了在同一块中捕获多个异常的能力：

```java
public int getPlayerScore(String playerFile) {
    try (Scanner contents = new Scanner(new File(playerFile))) {
        return Integer.parseInt(contents.nextLine());
    } catch (IOException | NumberFormatException e) {
        logger.warn("Failed to load score!", e);
        return 0;
    }
}
```

## 5.抛出异常

如果我们不想自己处理异常或者我们想生成我们的异常以供其他人处理，那么我们需要熟悉 throw 关键字。

假设我们有以下我们自己创建的已检查异常：

```java
public class TimeoutException extends Exception {
    public TimeoutException(String message) {
        super(message);
    }
}
```

我们有一个可能需要很长时间才能完成的方法：

```java
public List<Player> loadAllPlayers(String playersFile) {
    // ... potentially long operation
}
```

### 5.1. 抛出检查异常

就像从方法中返回一样，我们可以在任何时候抛出 。

当然，当我们试图表明出现问题时，我们应该抛出：

```java
public List<Player> loadAllPlayers(String playersFile) throws TimeoutException {
    while ( !tooLong ) {
        // ... potentially long operation
    }
    throw new TimeoutException("This operation took too long");
}
```

因为检查了 TimeoutException ，我们还必须在签名中使用throws关键字，以便我们方法的调用者知道要处理它。

### 5.2. 抛出未经检查的异常

如果我们想做一些事情，比如说，验证输入，我们可以使用一个未经检查的异常来代替：

```java
public List<Player> loadAllPlayers(String playersFile) throws TimeoutException {
    if(!isFilenameValid(playersFile)) {
        throw new IllegalArgumentException("Filename isn't valid!");
    }
   
    // ...
}

```

因为IllegalArgumentException未选中，所以我们不必标记该方法，尽管我们欢迎这样做。

无论如何，有些人将该方法标记为一种文档形式。

### 5.3. 包装和重新抛出

我们还可以选择重新抛出我们捕获的异常：

```java
public List<Player> loadAllPlayers(String playersFile) 
  throws IOException {
    try { 
        // ...
    } catch (IOException io) { 		
        throw io;
    }
}
```

或者做一个包装并重新抛出：

```java
public List<Player> loadAllPlayers(String playersFile) 
  throws PlayerLoadException {
    try { 
        // ...
    } catch (IOException io) { 		
        throw new PlayerLoadException(io);
    }
}
```

这对于将许多不同的异常合并为一个非常有用。

### 5.4. 重新抛出Throwable或Exception

现在来看一个特例。

如果给定代码块可能引发的唯一可能异常是未经检查的异常，那么我们可以捕获并重新抛出 Throwable 或Exception 而无需将它们添加到我们的方法签名中：

```java
public List<Player> loadAllPlayers(String playersFile) {
    try {
        throw new NullPointerException();
    } catch (Throwable t) {
        throw t;
    }
}
```

虽然简单，但上面的代码不能抛出检查异常，因此，即使我们重新抛出检查异常，我们也不必使用 throws 子句标记 签名 。

这对于代理类和方法很方便。[可以在此处](http://4comprehension.com/sneakily-throwing-exceptions-in-lambda-expressions-in-java/)找到有关此的更多信息。

### 5.5. 遗产

当我们用throws 关键字标记方法时，它会影响子类如何覆盖我们的方法。

在我们的方法抛出检查异常的情况下：

```java
public class Exceptions {
    public List<Player> loadAllPlayers(String playersFile) 
      throws TimeoutException {
        // ...
    }
}
```

一个子类可以有一个“风险较小”的签名：

```java
public class FewerExceptions extends Exceptions {	
    @Override
    public List<Player> loadAllPlayers(String playersFile) {
        // overridden
    }
}
```

但不是“风险更高 ”的签名：

```java
public class MoreExceptions extends Exceptions {		
    @Override
    public List<Player> loadAllPlayers(String playersFile) throws MyCheckedException {
        // overridden
    }
}
```

这是因为契约是在编译时由引用类型确定的。如果我创建MoreExceptions 的实例并将其保存到 Exceptions：

```java
Exceptions exceptions = new MoreExceptions();
exceptions.loadAllPlayers("file");
```

然后 JVM 只会告诉我捕获TimeoutException ，这是错误的，因为我已经说过MoreExceptions #loadAllPlayers会抛出一个不同的异常。

简而言之，子类可以抛出 比它们的超类更少的检查异常，但不会 更多。

## 6.反模式

### 6.1. 吞咽异常

现在，还有另一种方法可以让编译器满意：

```java
public int getPlayerScore(String playerFile) {
    try {
        // ...
    } catch (Exception e) {} // <== catch and swallow
    return 0;
}
```

以上称为 吞噬异常。大多数时候，这样做对我们来说有点意思，因为它没有解决问题，而且它使其他代码也无法解决问题。

有时会出现我们确信永远不会发生的已检查异常。在这些情况下，我们仍然至少应该添加一条注解，说明我们有意吃掉了异常：

```java
public int getPlayerScore(String playerFile) {
    try {
        // ...
    } catch (IOException e) {
        // this will never happen
    }
}
```

我们可以“吞下”异常的另一种方法是简单地将异常打印到错误流中：

```java
public int getPlayerScore(String playerFile) {
    try {
        // ...
    } catch (Exception e) {
        e.printStackTrace();
    }
    return 0;
}
```

我们至少将错误写在某处以供以后诊断，从而稍微改善了我们的情况。

不过，我们使用记录器会更好：

```java
public int getPlayerScore(String playerFile) {
    try {
        // ...
    } catch (IOException e) {
        logger.error("Couldn't load the score", e);
        return 0;
    }
}
```

虽然以这种方式处理异常对我们来说非常方便，但我们需要确保我们没有吞下我们代码的调用者可以用来解决问题的重要信息。

最后，当我们抛出一个新的异常时，我们可能会不经意地吞下一个异常，因为它没有将它作为一个原因：

```java
public int getPlayerScore(String playerFile) {
    try {
        // ...
    } catch (IOException e) {
        throw new PlayerScoreException();
    }
}

```

在这里，我们为提醒我们的调用者错误而拍拍自己，但我们没有将 IOException作为原因。因此，我们丢失了呼叫者或接线员可以用来诊断问题的重要信息。

我们最好这样做：

```java
public int getPlayerScore(String playerFile) {
    try {
        // ...
    } catch (IOException e) {
        throw new PlayerScoreException(e);
    }
}
```

请注意将IOException包含为 PlayerScoreException的 原因的细微差别 。

### 6.2. 在finally块中使用 return

吞下异常的另一种方法是从finally块返回。这很糟糕，因为通过突然返回，JVM 将丢弃异常，即使它是由我们的代码抛出的：

```java
public int getPlayerScore(String playerFile) {
    int score = 0;
    try {
        throw new IOException();
    } finally {
        return score; // <== the IOException is dropped
    }
}
```

根据[Java 语言规范](https://docs.oracle.com/javase/specs/jls/se7/html/jls-14.html#jls-14.20.2)：

>   如果 try 块的执行由于任何其他原因R突然完成，则执行 finally 块，然后有一个选择。
>
>   如果finally块正常完成，则 try 语句突然完成，原因为 R。
>
>   如果finally块由于 S 的原因突然完成，则 try 语句由于 S 的原因突然完成(并且丢弃原因 R)。

### 6.3. 在finally块中使用throw

与在finally块中使用return类似， finally块中抛出的异常将优先于 catch 块中出现的异常。

这将从try块中“删除”原始异常，我们将丢失所有有价值的信息：

```java
public int getPlayerScore(String playerFile) {
    try {
        // ...
    } catch ( IOException io ) {
        throw new IllegalStateException(io); // <== eaten by the finally
    } finally {
        throw new OtherException();
    }
}
```

### 6.4. 使用throw作为goto

有些人也屈服于使用throw作为goto语句的诱惑：

```java
public void doSomething() {
    try {
        // bunch of code
        throw new MyException();
        // second bunch of code
    } catch (MyException e) {
        // third bunch of code
    }		
}
```

这很奇怪，因为代码试图将异常用于流程控制而不是错误处理。

## 7. 常见的异常和错误

以下是我们不时遇到的一些常见异常和错误：

### 7.1. 检查异常

-   IOException – 此异常通常是表示网络、文件系统或数据库上的某些内容发生故障的一种方式。

### 7.2. 运行时异常

-   ArrayIndexOutOfBoundsException——这个异常意味着我们试图访问一个不存在的数组索引，比如当试图从一个长度为 3 的数组中获取索引 5 时。
-   ClassCastException——这个异常意味着我们试图执行非法转换，比如试图将String转换为List。我们通常可以通过在投射前执行防御性 instanceof 检查来避免它。
-   IllegalArgumentException——这个异常是我们说提供的方法或构造函数参数之一无效的通用方式。
-   IllegalStateException——这个异常是我们说我们的内部状态(如对象的状态)无效的通用方式。
-   NullPointerException——这个异常意味着我们试图引用一个空对象。我们通常可以通过执行防御性空检查或使用 Optional 来避免它。
-   NumberFormatException – 此异常表示我们尝试将字符串转换为数字，但该字符串包含非法字符，例如尝试将“5f3”转换为数字。

### 7.3. 错误

-   StackOverflowError – 此异常表示堆栈跟踪太大。这有时会发生在大量应用程序中；然而，这通常意味着我们的代码中发生了一些无限递归。
-   NoClassDefFoundError——这个异常意味着一个类加载失败，要么是因为不在类路径上，要么是因为静态初始化失败。
-   OutOfMemoryError——这个异常意味着 JVM 没有更多的内存可以分配给更多的对象。有时，这是由于内存泄漏造成的。

## 八、总结

在本文中，我们介绍了异常处理的基础知识以及一些好的和不好的实践示例。