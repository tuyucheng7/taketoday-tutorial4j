---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java
copyright: java
excerpt: Java
---

## 1.概述

在Java中，字符串是不可变的。一个在面试中非常普遍的明显问题是“为什么字符串在Java中被设计为不可变的？”

Java的创造者JamesGosling[曾在一次采访中被问及](https://www.artima.com/intv/gosling313.html)什么时候应该使用不可变对象，他回答说：

>   我会尽可能使用不可变的。

他进一步支持他的论点，即不可变性提供的特性，例如缓存、安全性、无需复制即可轻松重用等。

在本教程中，我们将进一步探讨为什么Java语言设计者决定保持String不可变。

## 2.什么是不可变对象？

不可变对象是在完全创建后内部状态保持不变的对象。这意味着一旦对象被分配给一个变量，我们既不能更新引用也不能以任何方式改变内部状态。

我们有一篇单独的文章详细讨论了不可变对象。有关详细信息，请阅读[Java中的不可变对象](https://www.baeldung.com/java-immutable-object)一文。

## 3.为什么String在Java中是不可变的？

将此类保持为不可变的主要好处是缓存、安全性、同步和性能。

让我们讨论一下这些东西是如何工作的。

### 3.1.介绍字符串池

String是使用最广泛的数据结构。缓存String文字并重用它们可以节省大量堆空间，因为不同的String变量引用String池中的同一个对象。字符串实习生池正是用于此目的。

Java字符串池是JVM存储字符串的特殊内存区域。由于字符串在Java中是不可变的，因此JVM通过在池中仅存储每个文字字符串的一个副本来优化为它们分配的内存量。这个过程称为实习：

```java
String s1 = "Hello World";
String s2 = "Hello World";
         
assertThat(s1 == s2).isTrue();
```

由于前面示例中字符串池的存在，两个不同的变量从池中指向同一个字符串对象，从而节省了关键的内存资源。

[![为什么字符串在Java中是不可变的](https://www.baeldung.com/wp-content/uploads/2018/08/Why_String_Is_Immutable_In_Java.jpg)](https://www.baeldung.com/wp-content/uploads/2018/08/Why_String_Is_Immutable_In_Java.jpg)

我们有一篇专门介绍JavaStringPool的文章。有关更多信息，[请转到那篇文章](https://www.baeldung.com/java-string-pool)。

### 3.2.安全

String在Java应用程序中广泛用于存储敏感信息，如用户名、密码、连接URL、网络连接等。在加载类时，它还被JVM类加载器广泛使用。

因此，保护String类对于整个应用程序的总体安全性至关重要。例如，考虑这个简单的代码片段：

```java
void criticalMethod(String userName) {
    // perform security checks
    if (!isAlphaNumeric(userName)) {
        throw new SecurityException(); 
    }
	
    // do some secondary tasks
    initializeDatabase();
	
    // critical task
    connection.executeUpdate("UPDATE Customers SET Status = 'Active' " +
      " WHERE UserName = '" + userName + "'");
}
```

在上面的代码片段中，假设我们从不可信的来源收到了一个String对象。我们首先会进行所有必要的安全检查，以检查字符串是否仅为字母数字，然后进行更多操作。

请记住，我们不可靠的源调用方方法仍然引用了这个userName对象。

如果字符串是可变的，那么当我们执行更新时，我们不能确定我们收到的字符串是否安全，即使在执行安全检查之后也是如此。不可信的调用方方法仍然具有引用并且可以在完整性检查之间更改String。因此，在这种情况下，我们的查询容易受到SQL注入。因此，随着时间的推移，可变字符串可能会导致安全性下降。

StringuserName对另一个线程可见的情况也可能发生，该线程可能会在完整性检查后更改其值。

通常，在这种情况下，不可变性可以为我们提供帮助，因为当值不变时，使用敏感代码进行操作会更容易，因为可能影响结果的操作交错较少。

### 3.3.同步化

不可变自动使String线程安全，因为当从多个线程访问时它们不会被更改。

因此，一般来说，不可变对象可以在同时运行的多个线程之间共享。它们也是线程安全的，因为如果一个线程更改了值，那么不会修改它，而是会在字符串池中创建一个新的字符串。因此，字符串对于多线程是安全的。

### 3.4.哈希码缓存

由于String对象被大量用作数据结构，因此它们也广泛用于哈希实现，如HashMap、HashTable、HashSet等。在对这些哈希实现进行操作时，hashCode()方法被频繁调用以进行分桶。

不变性保证字符串的值不会改变。因此在String类中重写了hashCode()方法以促进缓存，以便在第一次hashCode()调用期间计算并缓存哈希值，此后返回相同的值。

这反过来又提高了使用String对象操作时使用散列实现的集合的性能。

另一方面，如果String的内容在操作后被修改，可变字符串将在插入和检索时产生两个不同的哈希码，可能会丢失Map中的值对象。

### 3.5.表现

正如我们之前看到的，字符串池的存在是因为字符串是不可变的。反过来，它通过节省堆内存和在使用字符串操作时更快地访问哈希实现来提高性能。

由于String是使用最广泛的数据结构，提高String的性能对提高整个应用程序的性能有相当大的影响。

## 4。总结

通过这篇文章，我们可以得出总结，Strings是不可变的，因此它们的引用可以被视为一个普通变量，并且可以在方法之间和线程之间传递它们，而不用担心它指向的实际String对象是否会改变。

我们还了解了促使Java语言设计者将此类设为不可变的其他原因。
