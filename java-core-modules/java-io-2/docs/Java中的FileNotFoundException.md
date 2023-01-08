## 1. 概述

在本文中，我们将讨论Java中一个非常常见的异常—— FileNotFoundException。

我们将介绍可能发生的情况、可能的处理方法和一些示例。

## ~~~~2. 什么时候抛出异常？

如Java的 API 文档所示，在以下情况下会抛出此异常：

-   具有指定路径名的文件不 存在
-   具有指定路径名的文件确实存在但由于某种原因 无法访问(请求写入只读文件，或者权限不允许访问该文件)

## 3.如何处理？

首先，考虑到它扩展了java.lang.Exception 的java.io.IOException ，你将需要像处理任何其他已检查的Exception一样使用try-catch块来处理它。

然后， try-catch块内部做什么(业务/逻辑相关)实际上取决于你需要做什么。

你可能需要：

-   Rise a business-specific exception：这可能是一个停止执行的错误，但你会把决定权留在应用程序的上层(不要忘记包含原始异常)
-   通过对话或错误消息提醒用户：这不是停止执行错误，因此只需通知就足够了
-   创建一个文件：读取一个可选的配置文件，没有找到它并创建一个具有默认值的新文件
-   在另一个路径中创建一个文件：你需要写一些东西，如果第一个路径不可用，你尝试使用故障安全路径
-   只记录一个错误：这个错误不应该停止执行，但你记录它以供将来分析

## 4.例子

现在我们将看到一些示例，所有示例都将基于以下测试类：

```java
public class FileNotFoundExceptionTest {

    private static final Logger LOG
      = Logger.getLogger(FileNotFoundExceptionTest.class);
    private String fileName = Double.toString(Math.random());
    
    protected void readFailingFile() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(new File(fileName)));
        rd.readLine();
        // no need to close file
    }

    class BusinessException extends RuntimeException {
        public BusinessException(String string, FileNotFoundException ex) {
            super(string, ex);
        }
    }
}
```

### 4.1. 记录异常

如果运行以下代码，它将在控制台中“记录”错误：

```java
@Test
public void logError() throws IOException {
    try {
        readFailingFile();
    } catch (FileNotFoundException ex) {
        LOG.error("Optional file " + fileName + " was not found.", ex);
    }
}
```

### 4.2. 引发业务特定异常

接下来，举一个业务特定异常的例子，以便可以在上层处理错误：

```java
@Test(expected = BusinessException.class)
public void raiseBusinessSpecificException() throws IOException {
    try {
        readFailingFile();
    } catch (FileNotFoundException ex) {
        throw new BusinessException(
          "BusinessException: necessary file was not present.", ex);
    }
}
```

### 4.3. 创建文件

最后，我们将尝试创建文件以便读取它(可能是对于一个连续读取文件的线程)，但再次捕获异常并处理可能的第二个错误：

```java
@Test
public void createFile() throws IOException {
    try {
        readFailingFile();
    } catch (FileNotFoundException ex) {
        try {
            new File(fileName).createNewFile();
            readFailingFile();            
        } catch (IOException ioe) {
            throw new RuntimeException(
              "BusinessException: even creation is not possible.", ioe);
        }
    }
}
```

## 5.总结

在这篇简短的文章中，我们已经了解了何时会发生FileNotFoundException以及处理它的几个选项。