## 1. 概述

哈希码是对象内容的数字表示。

在Java中，我们可以使用几种不同的方法来获取对象的哈希码：

-   Object.hashCode() 
-   Objects.hashCode() –在Java7 中引入
-   Objects.hash() – 在Java7 中引入

在本教程中，我们将研究其中的每一种方法。首先，我们将从定义和基本示例开始。在了解了基本用法之后，我们将深入研究它们之间的差异以及这些差异可能产生的后果。

## 2. 基本用法

### 2.1. Object.hashCode()

我们可以使用[Object.hashCode()](https://www.baeldung.com/java-hashcode)方法来检索对象的哈希码。它与Objects.hashCode()非常相似，只是如果我们的对象为null则不能使用它。

话虽如此，让我们在两个相同的Double对象上调用Object.hashCode() ：

```java
Double valueOne = Double.valueOf(1.0012);
Double valueTwo = Double.valueOf(1.0012);
        
int hashCode1 = valueOne.hashCode();
int hashCode2 = valueTwo.hashCode();
        
assertEquals(hashCode1, hashCode2);
```

正如预期的那样，我们收到了相同的哈希码。

相反，现在让我们在null对象上调用Object.hashCode()并期望抛出NullPointerException：

```java
Double value = null;
value.hashCode();
```

### 2.2. 对象.hashCode()

Objects.hashCode()是一种 null 安全方法，我们可以使用它来获取对象的[哈希](https://www.baeldung.com/java-hashcode)码。哈希码对于哈希表和equals()的正确实现是必需的。

[JavaDoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#hashCode())中指定的哈希码的一般约定是：

-   在应用程序的同一执行过程中，每次为未更改的对象调用时，返回的整数都是相同的
-   对于根据equals()方法相等的两个对象，返回相同的哈希码

虽然这不是必需的，但不同的对象尽可能返回不同的哈希码。

首先，让我们在两个相同的字符串上调用Objects.hashCode() ：

```java
String stringOne = "test";
String stringTwo = "test";
int hashCode1 = Objects.hashCode(stringOne);
int hashCode2 = Objects.hashCode(stringTwo);
        
assertEquals(hashCode1, hashCode2);
```

现在，我们希望返回的哈希码是相同的。

另一方面，如果我们为 Objects.hashCode() 提供一个空值，我们将返回零：

```java
String nullString = null;
int hashCode = Objects.hashCode(nullString);
assertEquals(0, hashCode);
```

### 2.3. 对象.hash()

与仅采用单个对象的 Objects.hashCode() 不同，Objects.hash ()可以采用一个或多个对象并为它们提供哈希码。在底层，hash()方法的工作原理是将提供的对象放入一个数组中，然后对它们调用Arrays.hashCode()。如果我们只向Objects.hash()方法提供一个对象，我们就不能期待与对该对象调用Objects.hashCode()相同的结果。

首先，让我们用两对相同的字符串调用Objects.hash() ：

```java
String strOne = "one";
String strTwo = "two";
String strOne2 = "one";
String strTwo2 = "two";
        
int hashCode1 = Objects.hash(strOne, strTwo);
int hashCode2 = Objects.hash(strOne2, strTwo2);
        
assertEquals(hashCode1, hashCode2);
```

接下来，让我们用一个字符串调用Objects.hash()和Objects.hashCode() ：

```java
String testString = "test string";
int hashCode1 = Objects.hash(testString);
int hashCode2 = Objects.hashCode(testString);
        
assertNotEquals(hashCode1, hashCode2);
```

正如预期的那样，返回的两个哈希码不匹配。

## 3. 主要差异

在上一节中，我们谈到了Objects.hash()和Objects.hashCode()之间的主要区别。现在，让我们更深入地研究一下，以便我们了解其中的一些后果。

如果我们需要重写我们类的[equals()](https://www.baeldung.com/java-equals-hashcode-contracts)方法之一，那么正确重写hashCode()也很重要。

让我们从为我们的示例创建一个简单的Player类开始：

```java
public class Player {
    private String firstName;
    private String lastName;
    private String position;

    // Standard getters/setters
}
```

### 3.1. 多字段哈希码实现

假设我们的Player类在所有三个字段中被认为是唯一的：firstName、lastName和position。

话虽如此，让我们看看在Java 7 之前[我们是如何实现](https://www.baeldung.com/java-eclipse-equals-and-hashcode) Player.hashCode()的：

```java
@Override
public int hashCode() {
    int result = 17;
    result = 31  result + firstName != null ? firstName.hashCode() : 0;
    result = 31  result + lastName != null ? lastName.hashCode() : 0;
    result = 31  result + position != null ? position.hashCode() : 0;
    return result;
}
```

因为Objects.hashCode()和Objects.hash()都是在Java7 中引入的，所以我们必须在对每个字段调用Object.hashCode()之前明确检查是否为null 。

让我们确认我们可以对同一个对象调用hashCode()两次并获得相同的结果，并且我们可以对相同的对象调用它并获得相同的结果：

```java
Player player = new Player("Eduardo", "Rodriguez", "Pitcher");
Player indenticalPlayer = new Player("Eduardo", "Rodriguez", "Pitcher");
        
int hashCode1 = player.hashCode();
int hashCode2 = player.hashCode();
int hashCode3 = indenticalPlayer.hashCode();
        
assertEquals(hashCode1, hashCode2);
assertEquals(hashCode1, hashCode3);
```

接下来，让我们看看如何利用我们通过Objects.hashCode()获得的空安全性来缩短它：

```java
int result = 17;
result = 31  result + Objects.hashCode(firstName);
result = 31  result + Objects.hashCode(lastName);
result = 31  result + Objects.hashCode(position);
return result;
```

如果我们运行相同的单元测试，我们应该期望相同的结果。

因为我们的类依赖于多个字段来确定相等性，所以让我们更进一步，使用Objects.hash()使我们的hashCode()方法非常简洁：

```java
return Objects.hash(firstName, lastName, position);
```

此次更新后，我们应该能够再次成功运行我们的单元测试。

### 3.2. Objects.hash()详细信息

在底层，当我们调用Objects.hash() 时，值被放置在一个数组中，然后在数组上调用Arrays.hashCode()。

话虽如此，让我们创建一个Player并将其哈希码与Arrays.hashCode()与我们使用的值进行比较：

```java
@Test
public void whenCallingHashCodeAndArraysHashCode_thenSameHashCodeReturned() {
    Player player = new Player("Bobby", "Dalbec", "First Base");
    int hashcode1 = player.hashCode();
    String[] playerInfo = {"Bobby", "Dalbec", "First Base"};
    int hashcode2 = Arrays.hashCode(playerInfo);
        
    assertEquals(hashcode1, hashcode2);
}
```

我们创建了一个Player，然后创建了一个String[]。然后我们在Player上调用hashCode()并在数组上使用Arrays.hashCode()并收到相同的哈希码。

## 4。总结

在本文中，我们了解了如何以及何时使用Object.hashCode()、Objects.hashCode()和Objects.hash()。此外，我们还研究了它们之间的差异。

作为回顾，让我们快速总结一下它们的用法：

-   Object.hashCode()：用于获取单个非空对象的哈希码
-   Objects.hashCode()：用于获取可能为空的单个对象的哈希码
-   Objects.hash()：用于获取多个对象的哈希码