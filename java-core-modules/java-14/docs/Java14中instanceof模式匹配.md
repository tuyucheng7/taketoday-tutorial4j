## 1. 概述

在本快速教程中，我们将继续介绍[Java 14系列](https://www.baeldung.com/tag/java-14)，例如模式匹配，这是此版本JDK中包含的另一个新预览功能。

**总之，**[JEP 305](https://openjdk.org/jeps/305)**旨在使从对象中有条件地提取组件变得更加简单、简洁、可读和安全**。

## 2. 传统的instanceOf操作符

在某些时候，我们可能都编写过或看到过包含某种条件逻辑的代码，用于测试对象是否具有特定类型。**通常，我们可能会使用**[instanceof](https://www.baeldung.com/java-instanceof)**运算符后跟一个**[cast](https://www.baeldung.com/java-type-casting)**来做到这一点**。这允许我们在应用特定于该类型的进一步处理之前提取我们的变量。

假设我们想要检查Animal对象的简单层次结构中的类型：

```java
if (animal instanceof Cat) {
    Cat cat = (Cat) animal;
    cat.meow();
   // other cat operations
} else if (animal instanceof Dog) {
    Dog dog = (Dog) animal;
    dog.woof();
    // other dog operations
}

// More conditional statements for different animals
```

在本例中，对于每个条件块，我们测试animal参数以确定其类型，通过强制转换对其进行转换并声明一个局部变量。然后，我们可以执行特定于该动物类型的操作。

虽然这种方法有效，但它有几个缺点：

-   在我们需要测试类型并为每个条件块进行转换的地方编写这种类型的代码很乏味
-   我们需要为每个if块重复类型名称三次
-   可读性很差，因为强制转换和变量提取在代码中占主导地位
-   **重复声明类型名称意味着引入错误的可能性更大**，这可能会导致意外的运行时错误
-   每当我们添加新动物的实现类时，问题都会进一步放大

在下一节中，我们将介绍Java 14提供了哪些增强功能来解决这些缺点。

## 3. Java 14中增强的instanceOf

Java 14通过JEP 305带来了instanceof运算符的改进版本，该运算符既可以测试参数，又可以将其分配给正确类型的绑定变量。

**这意味着我们可以用更简洁的方式编写我们之前的动物例子**：

```java
if (animal instanceof Cat cat) {
    cat.meow();
} else if(animal instanceof Dog dog) {
    dog.woof();
}
```

让我们仔细剖析一下上面的代码：在第一个if块中，我们将animal与类型模式Cat cat进行匹配。首先，我们测试animal变量看它是否是Cat的一个实例。如果是这样，它将被强制转换为我们的Cat类型，最后，我们将结果分配给cat变量。

**需要注意的是，变量名cat不是现有变量，而是模式变量的声明**。

我们还应该提到，变量cat和dog仅在范围内并在各自的模式匹配表达式返回true时分配。**因此，如果我们尝试在另一个位置使用任一变量，代码将产生编译错误**。

正如我们所看到的，这个版本的代码更容易理解。我们简化了代码，大大减少了显式强制转换的总数，可读性大大提高。

**此外，这种类型的测试模式在编写**[equals]()**方法时特别有用**。

## 4. 总结

在这个简短的教程中，我们研究了Java 14中引出的instanceof的模式匹配。使用这种新的内置语言增强功能可以帮助我们编写更好、更可读的代码，这通常是一件好事。