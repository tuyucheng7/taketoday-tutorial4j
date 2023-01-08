## 1. 概述

String对象是Java语言中使用最多的类。

在这篇简短的文章中，我们将探索Java字符串池 — JVM 存储字符串的特殊内存区域。

## 2. 字符串实习

由于Java中字符串的不变性，JVM 可以通过在池中只存储每个文字字符串的一份副本来优化为它们分配的内存量。这个过程称为实习。

当我们创建一个String变量并为其赋值时，JVM 会在池中搜索一个相等值的String。

如果找到，Java 编译器将简单地返回对其内存地址的引用，而不分配额外的内存。

如果没有找到，它将被添加到池中(interned)并返回它的引用。

让我们写一个小测试来验证这一点：

```java
String constantString1 = "Baeldung";
String constantString2 = "Baeldung";
        
assertThat(constantString1)
  .isSameAs(constantString2);
```

## 3.使用构造函数分配的字符串

当我们 通过 new运算符创建一个String时 ，Java 编译器会创建一个新对象并将其存储在为 JVM 保留的堆空间中。

像这样创建的每个 字符串 都将指向一个具有自己地址的不同内存区域。

让我们看看这与之前的案例有何不同：

```java
String constantString = "Baeldung";
String newString = new String("Baeldung");
 
assertThat(constantString).isNotSameAs(newString);
```

## 4.字符串 文字与字符串对象

当我们 使用new()运算符创建一个String对象 时，它总是在堆内存中创建一个新对象。另一方面，如果我们使用字符串文字语法创建一个对象，例如“Baeldung”，它可能会从字符串池中返回一个现有对象，如果它已经存在的话。否则，它将创建一个新的 String 对象并放入字符串池中以供将来重复使用。

在高层次上，两者都是String对象，但主要区别在于new()运算符总是创建一个新的String对象。此外，当我们使用字面量创建一个String时——它是 interned 的。

当我们比较使用String文字和 new运算符创建的两个String对象时，这将更加清楚：

```java
String first = "Baeldung"; 
String second = "Baeldung"; 
System.out.println(first == second); // True
```

在此示例中，String对象将具有相同的引用。

接下来，让我们使用new创建两个不同的对象并检查它们是否具有不同的引用：

```java
String third = new String("Baeldung");
String fourth = new String("Baeldung"); 
System.out.println(third == fourth); // False
```

同样，当我们使用 == 运算符将String文字与使用new()运算符创建的String对象进行比较时，它将返回false：

```java
String fifth = "Baeldung";
String sixth = new String("Baeldung");
System.out.println(fifth == sixth); // False
```

一般来说，我们应该尽可能使用String字面量表示法。它更容易阅读，并且让编译器有机会优化我们的代码。

## 5. 手工实习

我们可以通过调用要实习的对象的intern()方法，在Java字符串池中手动实习一个字符串。

手动驻留String会将其引用存储在池中，JVM 将在需要时返回此引用。

让我们为此创建一个测试用例：

```java
String constantString = "interned Baeldung";
String newString = new String("interned Baeldung");

assertThat(constantString).isNotSameAs(newString);

String internedString = newString.intern();

assertThat(constantString)
  .isSameAs(internedString);
```

## 6. 垃圾收集

在Java7 之前，JVM将JavaString Pool 放置在PermGen空间中，它具有固定大小——它不能在运行时扩展，也不符合垃圾回收的条件。

在PermGen(而不是堆)中驻留字符串的风险是，如果我们驻留太多字符串，我们可能会从 JVM 得到一个OutOfMemory错误。

从Java7 开始，Java String Pool存放在Heap空间，由 JVM进行垃圾回收。这种方法的优点是降低了OutOfMemory错误的风险，因为未引用的字符串将从池中删除，从而释放内存。

## 7.性能和优化

在Java6 中，我们唯一可以执行的优化是在程序调用期间使用MaxPermSize JVM 选项增加PermGen空间：

```java
-XX:MaxPermSize=1G
```

在Java7 中，我们有更详细的选项来检查和扩展/减小池大小。让我们看看查看池大小的两个选项：

```java
-XX:+PrintFlagsFinal
-XX:+PrintStringTableStatistics
```

如果我们想根据桶增加池大小，我们可以使用StringTableSize JVM 选项：

```java
-XX:StringTableSize=4901
```

在Java7u40 之前，默认池大小为 1009 个桶，但此值在较新的Java版本中有一些变化。准确地说，从Java7u40 到Java11 的默认池大小是 60013，现在增加到 65536。

请注意，增加池大小将消耗更多内存，但具有减少将字符串插入表所需时间的优势。

## 8. 关于Java9 的注意事项

在Java8 之前，字符串在内部表示为字符数组 – char[]，以UTF-16编码，因此每个字符使用两个字节的内存。

Java 9 提供了一种新的表示形式，称为紧凑字符串。这种新格式将根据存储的内容在char[]和byte[]之间选择合适的编码。

由于新的String表示仅在必要时才使用UTF-16编码，因此堆内存量将显着减少，从而导致JVM上的垃圾收集器开销减少。

## 9.总结

在本指南中，我们展示了 JVM 和Java编译器如何通过Java字符串池优化String对象的内存分配。