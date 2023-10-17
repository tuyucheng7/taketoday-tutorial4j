## 1. 概述

在本快速教程中，我们将讨论Java中native关键字的概念，并将展示如何将本地方法集成到Java代码中。

## 2. Java中的native关键字

首先，让我们讨论一下什么是Java中的native关键字。

**简单地说，这是一个非访问修饰符，用于访问用C/C++等Java以外的语言实现的方法**。

它表示方法或代码的平台相关实现，也充当[JNI](https://www.baeldung.com/jni)和其他编程语言之间的接口。

## 3. 原生方法

**原生方法是一种Java方法(实例方法或类方法)，其实现也是用另一种编程语言(如C/C++)编写的**。

此外，标记为native的方法不能有主体并且应该以分号结尾：

```java
[ public | protected | private] native [return_type] method ();
```

我们可以使用它们来：

-   使用其他编程语言编写的系统调用或库实现接口
-   访问只能从其他语言访问的系统或硬件资源
-   将现有的用C/C++编写的遗留代码集成到Java应用程序中
-   使用来自Java的任意代码调用已编译的动态加载库

## 4. 示例

现在让我们演示如何将这些方法集成到我们的Java代码中。

### 4.1 在Java中访问原生代码

首先，让我们创建一个DateTimeUtils类，它需要访问一个名为getSystemTime的依赖于平台的原生方法：

```java
public class DateTimeUtils {
    public native String getSystemTime();
    // ...
}
```

要加载它，我们将使用System.loadLibrary。

让我们将加载这个库的调用放在一个静态块中，以便它在我们的类中可用：

```java
public class DateTimeUtils {
    public native String getSystemTime();

    static {
        System.loadLibrary("nativedatetimeutils");
    }
}
```

我们创建了一个动态链接库nativedatetimeutils，它使用我们的[JNI指南](https://www.baeldung.com/jni)文章中介绍的详细说明在C++中实现getSystemTime。

### 4.2 测试原生方法

最后，让我们看看如何测试DateTimeUtils类中定义的原生方法：

```java
class DateTimeUtilsManualTest {

    @BeforeAll
    static void setUpClass() {
        // .. load other dependent libraries  
        System.loadLibrary("nativedatetimeutils");
    }

    @Test
    void givenNativeLibsLoaded_thenNativeMethodIsAccessible() {
        DateTimeUtils dateTimeUtils = new DateTimeUtils();
        LOG.info("System time is : " + dateTimeUtils.getSystemTime());
        
        assertNotNull(dateTimeUtils.getSystemTime());
    }
}
```

以下是日志的输出：

```java
[main] INFO  c.t.t.n.DateTimeUtilsManualTest - System time is : Wed Dec 19 11:34:02 2018
```

正如我们所看到的，在native关键字的帮助下，我们能够成功地访问用另一种语言(在我们的例子中是C++)编写的依赖于平台的实现。

## 5. 总结

在本文中，我们了解了native关键字和方法的基础知识。通过一个简单的示例，我们还学习了如何将它们集成到Java中。