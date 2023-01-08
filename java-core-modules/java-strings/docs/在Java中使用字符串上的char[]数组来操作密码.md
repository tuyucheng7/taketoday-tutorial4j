## 1. 概述

在本文中，我们将解释为什么我们应该使用char[]数组而不是Java中的String来表示密码。

请注意，本教程的重点是在内存中操作密码的方式，而不是存储密码的实际方式，后者通常在持久层处理。

我们还假设我们无法控制密码的格式(例如，密码来自String形式的第 3 方 API )。尽管使用java.lang.String类型的对象来操作密码似乎很明显，但Java团队自己建议改用char[]。 

例如，如果我们查看 javax.swing的[JPasswordField ](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/JPasswordField.html)，我们可以看到返回String的方法getText()自Java2 以来已被弃用，取而代之的是返回char[]的getPassword() 方法。

那么，让我们探讨一下为什么会出现这种情况的几个重要原因。

## 2. 字符串是不可变的

Java 中的String是不可变的，这意味着我们无法使用任何高级 API 更改它们。对String对象的任何更改 都会产生一个新的String，将旧的保留在内存中。

因此，存储在String中的密码将在内存中可用，直到垃圾收集器将其清除。我们无法控制它何时发生，但这段时间可能比常规对象长得多，因为字符串保存在字符串池中以实现可重用性目的。

因此，任何有权访问内存转储的人都可以从内存中检索密码。

使用char[]数组而不是String，我们可以在完成预期工作后显式擦除数据。这样，我们将确保甚至在垃圾收集发生之前就从内存中删除密码。

现在让我们看一下代码片段，它们演示了我们刚刚讨论的内容。

首先是String：

```java
System.out.print("Original String password value: ");
System.out.println(stringPassword);
System.out.println("Original String password hashCode: "
  + Integer.toHexString(stringPassword.hashCode()));

String newString = "";
stringPassword.replace(stringPassword, newString);

System.out.print("String password value after trying to replace it: ");
System.out.println(stringPassword);
System.out.println(
  "hashCode after trying to replace the original String: "
  + Integer.toHexString(stringPassword.hashCode()));
```

输出将是：

```plaintext
Original String password value: password
Original String password hashCode: 4889ba9b
String value after trying to replace it: password
hashCode after trying to replace the original String: 4889ba9b
```

现在为char[]：

```java
char[] charPassword = new char[]{'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};

System.out.print("Original char password value: ");
System.out.println(charPassword);
System.out.println(
  "Original char password hashCode: " 
  + Integer.toHexString(charPassword.hashCode()));

Arrays.fill(charPassword, '');

System.out.print("Changed char password value: ");
System.out.println(charPassword);
System.out.println(
  "Changed char password hashCode: " 
  + Integer.toHexString(charPassword.hashCode()));
```

输出是：

```plaintext
Original char password value: password
Original char password hashCode: 7cc355be
Changed char password value: 
Changed char password hashCode: 7cc355be
```

正如我们所看到的，在我们尝试替换原始String的内容后，值保持不变并且 hashCode()方法在应用程序的相同执行中没有返回不同的值，这意味着原始String保持不变。

对于char[]数组，我们能够更改同一对象中的数据。

## 3.我们可以不小心打印密码

在char[]数组中使用密码的另一个好处是可以防止在控制台、监视器或其他或多或少不安全的地方意外记录密码。

让我们看看下一个代码：

```java
String passwordString = "password";
char[] passwordArray = new char[]{'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
System.out.println("Printing String password -> " + passwordString);
System.out.println("Printing char[] password -> " + passwordArray);
```

随着输出：

```plaintext
Printing String password -> password
Printing char[] password -> [C@6e8cf4c6
```

我们看到在第一种情况下打印了内容本身，而在第二种情况下，数据没有那么有用，这使得char[]不易受到攻击。

## 4。总结

在这篇简短的文章中，我们强调了为什么我们不应该使用String来收集密码以及为什么我们应该使用char[]数组来代替的几个原因。