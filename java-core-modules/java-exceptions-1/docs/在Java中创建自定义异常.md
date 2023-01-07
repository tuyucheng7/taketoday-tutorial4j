## 1. 概述

在本教程中，我们将介绍如何在Java中创建自定义异常。

我们将展示如何实现用户定义的异常并将其用于已检查和未检查的异常。

## 延伸阅读：

## [Java 中的异常处理](https://www.baeldung.com/java-exceptions)

学习Java中异常处理的基础知识以及一些最佳和最差实践。

[阅读更多](https://www.baeldung.com/java-exceptions)→

## [Java 中的已检查和未检查异常](https://www.baeldung.com/java-checked-unchecked-exceptions)

通过一些示例了解Java的已检查异常和未检查异常之间的区别

[阅读更多](https://www.baeldung.com/java-checked-unchecked-exceptions)→

## [常见的Java异常](https://www.baeldung.com/java-common-exceptions)

对常见Java异常的快速概述。

[阅读更多](https://www.baeldung.com/java-common-exceptions)→

## 2.自定义异常的需要

Java异常几乎涵盖了编程中必然会发生的所有一般异常。

然而，我们有时需要用我们自己的异常来补充这些标准异常。

这些是引入自定义异常的主要原因：

-   业务逻辑异常——特定于业务逻辑和工作流的异常。这些帮助应用程序用户或开发人员了解确切的问题是什么。
-   捕获现有Java异常的子集并提供特定处理

可以检查和取消检查Java异常。在接下来的部分中，我们将介绍这两种情况。

## 3.自定义检查异常

检查异常是需要显式处理的异常。

让我们考虑一段返回文件第一行的代码：

```java
try (Scanner file = new Scanner(new File(fileName))) {
    if (file.hasNextLine()) return file.nextLine();
} catch(FileNotFoundException e) {
    // Logging, etc 
}

```

上面的代码是处理Java检查异常的经典方法。虽然代码抛出FileNotFoundException，但尚不清楚确切原因是什么——文件不存在或文件名无效。

要创建自定义异常，我们必须扩展java.lang.Exception类。

让我们通过创建一个名为IncorrectFileNameException的自定义检查异常来查看此示例：

```java
public class IncorrectFileNameException extends Exception { 
    public IncorrectFileNameException(String errorMessage) {
        super(errorMessage);
    }
}

```

请注意，我们还必须提供一个构造函数，该构造函数将String作为错误消息并调用父类构造函数。

这就是我们定义自定义异常所需要做的全部工作。

接下来，让我们看看如何在示例中使用自定义异常：

```java
try (Scanner file = new Scanner(new File(fileName))) {
    if (file.hasNextLine())
        return file.nextLine();
} catch (FileNotFoundException e) {
    if (!isCorrectFileName(fileName)) {
        throw new IncorrectFileNameException("Incorrect filename : " + fileName );
    }
    //...
}

```

我们已经创建并使用了自定义异常，因此用户现在可以知道确切的异常是什么。

这够了吗？我们因此失去了异常的根本原因。

为了解决这个问题，我们还可以 向构造函数添加一个java.lang.Throwable参数。这样，我们可以将根异常传递给方法调用：

```java
public IncorrectFileNameException(String errorMessage, Throwable err) {
    super(errorMessage, err);
}

```

现在将IncorrectFileNameException与异常的根本原因一起使用：

```java
try (Scanner file = new Scanner(new File(fileName))) {
    if (file.hasNextLine()) {
        return file.nextLine();
    }
} catch (FileNotFoundException err) {
    if (!isCorrectFileName(fileName)) {
        throw new IncorrectFileNameException(
          "Incorrect filename : " + fileName , err);
    }
    // ...
}

```

这就是我们如何使用自定义异常 而不丢失它们发生的根本原因。

## 4.自定义未检查异常

在我们的同一个示例中，假设如果文件名不包含任何扩展名，我们需要一个自定义异常。

在这种情况下，我们需要一个类似于前一个的自定义未检查异常，因为这个错误只会在运行时检测到。

要创建自定义未检查异常，我们需要扩展java.lang.RuntimeException类：

```java
public class IncorrectFileExtensionException 
  extends RuntimeException {
    public IncorrectFileExtensionException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}

```

这样，我们就可以在示例中使用这个自定义的未经检查的异常：

```java
try (Scanner file = new Scanner(new File(fileName))) {
    if (file.hasNextLine()) {
        return file.nextLine();
    } else {
        throw new IllegalArgumentException("Non readable file");
    }
} catch (FileNotFoundException err) {
    if (!isCorrectFileName(fileName)) {
        throw new IncorrectFileNameException(
          "Incorrect filename : " + fileName , err);
    }
    
    //...
} catch(IllegalArgumentException err) {
    if(!containsExtension(fileName)) {
        throw new IncorrectFileExtensionException(
          "Filename does not contain extension : " + fileName, err);
    }
    
    //...
}

```

## 5.总结

当我们需要处理与业务逻辑相关的特定异常时，自定义异常非常有用。如果使用得当，它们可以作为更好的异常处理和日志记录的实用工具。