## 1. 概述

当我们试图访问网络中的一些资源时，首先要获得该资源的统一资源标识符(URI)。

Java标准库提供了[URI](https://www.baeldung.com/java-url-vs-uri)类，可以让我们更方便的处理URI。当然，要使用URI类，第一步是获取URI实例。

假设我们有网络中某些资源的地址字符串。有两种方法可以获取URI实例：

-   直接使用地址字符串调用构造函数 – URI myUri = new URI(theAddress);
-   调用URI.create() 静态方法 – URI myUri = URI.create(theAddress);

在本快速教程中，我们将仔细研究这两种方法并讨论它们的区别。

## 2. 使用URI构造函数

首先，让我们看一下构造函数的签名：

```java
public URI(String str) throws URISyntaxException
```

如我们所见，使用无效地址调用构造函数可能会引发URISyntaxException。为简单起见，让我们创建一个单元测试来查看此案例：

```java
assertThrows(URISyntaxException.class, () -> new URI("I am an invalid URI string."));

```

我们应该注意到URISyntaxException是Exception的子类：

```java
public class URISyntaxException extends Exception { ... }

```

因此，它是一个[已检查的异常](https://www.baeldung.com/java-checked-unchecked-exceptions)。换句话说，当我们调用这个构造函数时，我们必须处理URISyntaxException：

```java
try {
    URI myUri = new URI("https://www.baeldung.com/articles");
    assertNotNull(myUri);
} catch (URISyntaxException e) {
    fail();
}
```

我们已经将构造函数的使用和异常处理视为单元测试。如果我们执行测试，它就会通过。但是，如果没有try-catch，代码将无法编译。

如我们所见，使用构造函数创建URI实例非常简单。接下来，让我们转到URI.create()方法。

## 3. 使用URI.create()方法

我们已经提到URI.create()也可以创建一个URI实例。要了解create()方法和构造函数之间的区别 ，让我们看一下create()方法的源代码：

```java
public static URI create(String str) {
    try {
        return new URI(str);
    } catch (URISyntaxException x) {
        throw new IllegalArgumentException(x.getMessage(), x);
    }
}
```

正如我们在上面的代码中看到的，create()方法的实现非常简单。首先，它直接调用构造函数。此外，如果抛出URISyntaxException ，它会将异常包装在新的IllegalArgumentException 中。 

那么接下来，让我们创建一个测试，看看如果我们向create()提供无效的 URI 字符串，我们是否可以获得预期的IllegalArgumentException ：

```java
assertThrows(IllegalArgumentException.class, () -> URI.create("I am an invalid URI string."));
```

如果我们试一试，测试就会通过。

如果我们仔细查看IllegalArgumentException类，我们可以看到它是RuntimeException的子类：

```java
public class IllegalArgumentException extends RuntimeException { ... }
```

我们知道RuntimeException是一个未经检查的异常。这意味着当我们使用 create()方法创建URI实例时，我们不必在显式try-catch中处理异常：

```java
URI myUri = URI.create("https://www.baeldung.com/articles");
assertNotNull(myUri);
```

因此， create()和URI()构造函数之间的主要区别在于create()方法将构造函数的已检查异常 ( URISyntaxException ) 更改为未检查异常 ( IllegalArgumentException )。

如果我们不想处理已检查的异常，我们可以使用 create() 静态方法来创建 URI 实例。

## 5.总结

在本文中，我们讨论了使用构造函数和URI.create()方法实例化URI对象之间的区别。