## 1. 概述

在这个简短的教程中，我们将探讨在Java中比较字符的不同方法。

我们将从讨论如何比较原始字符开始。然后，我们将研究比较Character对象的不同方法。

## 2.比较原始字符

首先，让我们从强调如何比较原始字符开始。

### 2.1. 使用关系运算符

通常，比较字符的最简单方法是使用[关系运算符](https://www.baeldung.com/java-operators#relational-operators)。

简而言之，字符在Java中根据其[ASCII 码](https://www.baeldung.com/cs/ascii-code)的顺序进行比较：

```java
assertFalse('a' == 'A');
assertTrue('a' < 'v');
assertTrue('F' > 'D');

```

### 2.2. 使用Character.compare() 方法

同样，另一种解决方案是使用Character类的compare() 方法。

简单来说，Character类将原始类型char的值包装在一个对象中。compare()方法接受两个char参数并进行数值比较：

```java
assertTrue(Character.compare('C', 'C') == 0);
assertTrue(Character.compare('f', 'A') > 0);
assertTrue(Character.compare('Y', 'z') < 0);

```

如上所示，compare(char a, char b)方法返回一个int值。它表示a和b的 ASCII 码之间的差异。

如果两个 char 值相同，则返回值等于零，如果a < b则返回值小于零，否则返回值大于零。

## 3.比较字符对象

现在我们知道如何比较原始字符，让我们看看如何比较Character对象。

### 3.1. 使用Character.compareTo()方法

Character类提供了compareTo()方法来比较两个字符对象的数值：

```java
Character chK = Character.valueOf('K');
assertTrue(chK.compareTo(chK) == 0);

Character chG = Character.valueOf('G');
assertTrue(chK.compareTo(chG) > 0);

Character chH = Character.valueOf('H');
assertTrue(chG.compareTo(chH) < 0);

```

在这里，我们使用valueOf()方法来创建Character对象，因为自Java9 以来构造函数已被弃用。

### 3.2. 使用Object.equals()方法

此外，比较对象的一种常见解决方案是使用equals()方法。如果两个对象相等则返回true ，否则返回false。

那么，让我们看看如何使用它来比较字符：

```java
Character chL = 'L';
assertTrue(chL.equals(chL));

Character chV = 'V';
assertFalse(chL.equals(chV));

```

### 3.3. 使用Objects.equals()方法

Objects类由用于操作对象的实用方法组成。它提供了另一种通过equals()方法比较字符对象的方法：

```java
Character chA = 'A';
Character chB = 'B';

assertTrue(Objects.equals(chA, chA));
assertFalse(Objects.equals(chA, chB));

```

如果字符对象彼此相等，则equals ()方法返回true ，否则返回false。

## 4。总结

在本文中，我们学习了多种在Java中比较原始字符和对象字符的方法。