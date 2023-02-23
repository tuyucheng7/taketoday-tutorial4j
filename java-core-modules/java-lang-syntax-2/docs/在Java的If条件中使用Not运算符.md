## 1. 概述

在Java的[if-else语句](https://www.baeldung.com/java-if-else)中，我们可以在表达式为ture时执行特定操作，在表达式为false时执行替代操作。在本教程中，我们将学习如何使用not运算符来反转逻辑。

## 2. if-else语句

让我们从一个简单的if-else语句开始：

```java
boolean isValid = true;

if (isValid) {
    System.out.println("Valid");
} else {
    System.out.println("Invalid");
}
```

现在如果我们的程序只需要处理否定的情况呢？我们将如何重写上面的例子？

一种选择是简单地删除if块中的代码：

```java
boolean isValid = true;

if (isValid) {

} else {
    System.out.println("Invalid");
}
```

然而，一个空的if块看起来可能是不完整的代码，并且似乎是一种只处理否定条件的冗长方法。相反，我们可以尝试测试我们的逻辑表达式是否为false：

```java
boolean isValid = true;

if (isValid == false) {
    System.out.println("Invalid");
}
```

上面的版本相对容易阅读，但如果逻辑表达式更复杂，可能会更难阅读。不过，Java以not运算符的形式为我们提供了替代方案：

```java
boolean isValid = true;

if (!isValid) {
    System.out.println("Invalid");
}
```

## 3. not运算符

not运算符是一个逻辑运算符，在Java中由!表示。它是一个以布尔值作为操作数的一元运算符。**not运算符通过反转(或否定)其操作数的值来工作**。

### 3.1 将not运算符应用于布尔值

当应用于布尔值时，not运算符将true变为false，将false变为true：

```java
System.out.println(!true);   // prints false 
System.out.println(!false);  // prints true 
System.out.println(!!false); // prints false
```

### 3.2 将not运算符应用于布尔表达式

由于not是一元运算符，因此**当我们想要否定表达式的结果时，我们需要将该表达式括在括号中以获得正确答案**。首先计算括号中的表达式，然后not运算符反转其结果：

```java
int count = 2;

System.out.println(!(count > 2));  // prints true
System.out.println(!(count <= 2)); // prints false
boolean x = true;
boolean y = false;

System.out.println(!(x && y));  // prints true
System.out.println(!(x || y));  // prints false
```

我们应该注意到，当否定一个表达式时，[德摩根定律](https://en.wikipedia.org/wiki/De_Morgan's_laws)开始发挥作用。换句话说，表达式中的每一项都被否定并且运算符被反转。

这可以帮助我们简化更难阅读的表达式：

```java
!(x && y) is same as !x || !y
!(x || y) is same as !x && !y
!(a < 3 && b == 10) is same as a >= 3 || b != 10
```

## 4. 一些常见的陷阱

使用not运算符有时会损害我们代码的可读性。消极因素可能比积极因素更难理解。让我们看一些例子。

### 4.1 双重否定

由于not运算符是一个否定运算符，因此将它与具有否定名称的变量或函数一起使用会导致代码难以阅读。这类似于自然语言，双重否定通常被认为难以理解。

例如：

```java
if (product.isActive()) {...}
```

读起来比

```java
if (!product.isNotActive()) {...}
```

虽然我们的API可能不提供isActive方法，但我们可以创建一个方法来提高可读性。

### 4.2 复杂条件

not运算符有时会使已经很复杂的表达式更难阅读和理解。当出现这种情况时，我们可以通过反转条件或者提取方法来简化代码。让我们看一些由not运算符使条件变得复杂的示例，以及我们如何通过反转条件来简化它们：

```java
if (!true) // Complex
if (false) // Simplified

if (!myDate.onOrAfter(anotherDate)) // Complex 
if (myDate.before(anotherDate))     // Simplified
 
if (!(a >= b)) // Complex
if (a < b)     // Simplified

if (!(count >= 10 || total >= 1000))  // Complex
if (count < 10 && total < 1000)       // Simplified
```

## 5. 总结

在本文中，我们探讨了not运算符以及如何将它与布尔值、表达式和if-else语句一起使用。

我们还讨论了一些因在逆向编写表达式而导致的常见陷阱以及如何修复它们。