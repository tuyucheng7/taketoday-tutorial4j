## 1. 概述

Java String是最重要的类之一，我们已经在与[String相关的系列教程](https://www.baeldung.com/java-string)中介绍了它的很多方面。

在本教程中，我们将重点介绍Java 中的String初始化。

## 2.创作

首先，我们应该记住String在Java中是如何创建的。

我们可以使用new关键字或文字语法：

```java
String usingNew = new String("baeldung");
String usingLiteral = "baeldung";
```

而且，我们了解如何[在专门的池中管理](https://www.baeldung.com/java-string-pool)[String](https://www.baeldung.com/java-string-pool)也很重要。

## 3.仅字符串声明

首先，让我们只声明一个String，而不显式分配一个值。

我们可以在本地或作为成员变量执行此操作：

```java
public class StringInitialization {

    String fieldString;

    void printDeclaredOnlyString() {
        String localVarString;
        
        // System.out.println(localVarString); -> compilation error
        System.out.println(fieldString);
    }
}
```

如我们所见，如果我们在给它赋值之前尝试使用localVarString ，我们将得到一个编译错误。另一方面，控制台将为 fieldString 的值显示“ null ”。

看，成员变量在构造类时使用默认值初始化， 在String的情况下为null。但是，我们必须自己初始化局部变量。

如果我们给localVarString一个null的值，我们会看到两者现在确实相等：

```java
String localVarString = null;
assertEquals(fieldString, localVarString);
```

## 4.使用字面量初始化字符串

现在让我们使用相同的文字创建两个String ：

```java
String literalOne = "Baeldung";
String literalTwo = "Baeldung";
```

我们将通过比较引用来确认只创建了一个对象：

```java
assertTrue(literalOne == literalTwo);
```

这样做的原因可以追溯到[String存储在池中](https://www.baeldung.com/java-string-pool)这一事实。 literalOne 将 字符串 “baeldung”添加到池中， literalTwo 重用它。

## 5.字符串初始化使用new

但是，如果我们使用new关键字，我们会看到一些不同的行为。

```java
String newStringOne = new String("Baeldung");
String newStringTwo = new String("Baeldung");
```

尽管两个String的值与之前相同，但这次我们必须使用不同的对象：

```java
assertFalse(newStringOne == newStringTwo);
```

## 6.空字符串

现在让我们创建三个空的String：

```java
String emptyLiteral = "";
String emptyNewString = new String("");
String emptyNewStringTwo = new String();
```

我们现在知道，emptyLiteral将被添加到String池中，而其他两个直接进入堆。

尽管这些[不是相同的对象，但它们都具有相同的值](https://www.baeldung.com/java-compare-strings)：

```java
assertFalse(emptyLiteral == emptyNewString)
assertFalse(emptyLiteral == emptyNewStringTwo)
assertFalse(emptyNewString == emptyNewStringTwo)
assertEquals(emptyLiteral, emptyNewString);
assertEquals(emptyNewString, emptyNewStringTwo);
```

## 7.空值

最后，让我们看看 null String的行为方式。

让我们声明并初始化一个空 字符串：

```java
String nullValue = null;
```

如果我们打印nullValue，我们会看到“null”这个词，就像我们之前看到的那样。而且，如果我们尝试调用nullValue 上的任何方法，我们 将按预期获得NullPointerException 。

但是，为什么要打印“null”？null实际上是什么？

好吧，[JVM 规范](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.4)说 null是所有引用的默认值，所以它没有专门绑定到String。实际上，该规范并未强制要求对null进行任何具体的值编码。

那么，打印字符串的“null”从何而来呢？

如果我们看一下PrintStream# println实现，我们会看到它调用String#valueOf：

```java
public void println(Object x) {
    String s = String.valueOf(x);
    synchronized (this) {
        print(s);
        newLine();
    }
}
```

而且，如果我们查看String#valueOf，我们会得到答案：

```java
public static String valueOf(Object obj) {
    return (obj == null) ? "null" : obj.toString();
}
```

显然，这就是“null”的原因。

## 八、总结

在本文中，我们探讨了String初始化。我们解释了声明和初始化之间的区别。我们还谈到了使用new和使用文字语法。

最后，我们了解了将空值分配给String的含义、空值在内存中的表示方式以及打印时的外观。