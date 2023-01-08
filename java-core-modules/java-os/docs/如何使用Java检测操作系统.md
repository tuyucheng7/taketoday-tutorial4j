## 1. 概述

有几种方法可以确定我们的代码运行的操作系统。

在这篇简短的文章中，我们将了解如何专注于使用Java进行操作系统检测。

## 2.实施

一种方法是使用System。getProperty(os.name)获取操作系统的名称。

第二种方法是使用Apache Commons Lang API中的SystemUtils。

让我们看看他们两个在行动。

### 2.1. 使用系统属性

我们可以使用System类来检测操作系统。

让我们来看看：

```java
public String getOperatingSystem() {
    String os = System.getProperty("os.name");
    // System.out.println("Using System Property: " + os);
    return os;
}
```

### 2.2. SystemUtils – Apache Commons Lang

来自 Apache Commons Lang 的SystemUtils是另一个值得尝试的流行选项。这是一个很好的 API，可以优雅地处理这些细节。

让我们使用 SystemUtils 找出操作系统：

```java
public String getOperatingSystemSystemUtils() {
    String os = SystemUtils.OS_NAME;
    // System.out.println("Using SystemUtils: " + os);
    return os;
}
```

## 3. 结果

在我们的环境中执行代码会得到相同的结果：

```plaintext
Using SystemUtils: Windows 10
Using System Property: Windows 10
```

## 4。总结

在这篇快速文章中，我们了解了如何从Java中以编程方式查找/检测操作系统。