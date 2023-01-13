## 1. 概述

在本快速教程中，我们将展示如何使用[SLF4J](https://www.slf4j.org/) API 在Java中记录异常。我们将使用 slf4j-simple API 作为日志记录实现。

你可以在我们[之前的一篇文章](https://www.baeldung.com/category/logging/)中探索不同的日志记录技术。

## 2.Maven依赖

首先，我们需要将以下依赖项添加到我们的 pom.xml中：

```xml
<dependency>                             
    <groupId>org.slf4j</groupId>         
    <artifactId>slf4j-api</artifactId>   
    <version>1.7.30</version>  
</dependency> 
                       
<dependency>                             
    <groupId>org.slf4j</groupId>         
    <artifactId>slf4j-simple</artifactId>
    <version>1.7.30</version>  
</dependency>
```

这些库的最新版本可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.slf4j" AND (a%3A"slf4j-api" OR a%3A"slf4j-simple"))上找到。

## 3.例子

通常，所有异常都使用[Logger](https://static.javadoc.io/org.slf4j/slf4j-api/1.7.25/org/slf4j/Logger.html)类中可用的error()方法进行记录。这种方法有很多变体。我们将探索：

```java
void error(String msg);
void error(String format, Object... arguments);
void error(String msg, Throwable t);
```

让我们首先初始化 我们将要使用的记录器：

```java
Logger logger = LoggerFactory.getLogger(NameOfTheClass.class);
```

如果我们只需要显示错误消息，那么我们可以简单地添加：

```java
logger.error("An exception occurred!");
```

上述代码的输出将是：

```java
ERROR packageName.NameOfTheClass - An exception occurred!
```

这很简单。但是要添加更多关于异常的相关信息(包括堆栈跟踪)，我们可以这样写：

```java
logger.error("An exception occurred!", new Exception("Custom exception"));
```

输出将是：

```java
ERROR packageName.NameOfTheClass - An exception occurred!
java.lang.Exception: Custom exception
  at packageName.NameOfTheClass.methodName(NameOfTheClass.java:lineNo)
```

在存在多个参数的情况下，如果日志语句中的最后一个参数是异常，则 SLF4J 将假定用户希望将最后一个参数视为异常而不是简单参数：

```java
logger.error("{}, {}! An exception occurred!", 
  "Hello", 
  "World", 
  new Exception("Custom exception"));
```

在上面的代码片段中，字符串消息将根据传递的对象详细信息进行格式化。我们使用花括号作为 传递给方法的字符串参数的占位符。

在这种情况下，输出将是：

```java
ERROR packageName.NameOfTheClass - Hello, World! An exception occurred!
java.lang.Exception: Custom exception 
  at packageName.NameOfTheClass.methodName(NameOfTheClass.java:lineNo)
```

## 4. 总结

在本快速教程中，我们了解了如何使用 SLF4J API 记录异常。