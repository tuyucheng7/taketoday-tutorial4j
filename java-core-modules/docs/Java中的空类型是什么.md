## 一、简介

在 Java 的世界里，*null*类型无处不在，不遇到它就很难使用这门语言。在大多数情况下，它代表虚无或缺少某些东西的直觉理解足以有效地编程。然而，有时我们想要更深入地挖掘并彻底理解该主题。

在本教程中，我们将了解*null*类型如何在幕后工作以及它与其他类型的关系。

## 2. 什么是类型？

*在我们回答关于null*类型的具体问题之前，我们需要定义一个类型到底是什么。这不是一件容易的事，因为有很多相互竞争的定义。对我们最有用的是值空间的定义。在该定义中，**类型由它可以容纳的一组可能值定义**。

假设我们要声明一个*布尔*变量：

```java
boolean valid;复制
```

我们所做的是声明名为“valid”的变量将包含两个可能值之一：*true*或*false*。可能值的集合只有两个元素。如果我们想声明一个*int*变量，可能值的集合会大得多但仍然明确定义：从 -2^31 到 2^31-1 的每个可能的数字。

## 3.什么类型是*null？*

*null*是一种特殊类型，它只有一个可能的值。换句话说，一组可能值只有一个元素。单是这个特性就让*null*类型非常奇特。通常，变量的全部目的是它们可以采用不同的值。只有一个*空引用，因此**空*类型的变量只能保存一个特定的引用。除了变量存在之外，它不会带来任何信息。

有一个特征使得*null*类型以我们使用它的方式可用。**空\*引用\*可以转换为任何其他引用类型。**这意味着我们可以把它当作一个特殊的文字，它可以是任何非原始类型。实际上，*空*引用扩展了这些类型的有效可能值集。

这就解释了为什么我们可以将完全相同的*空*引用分配给完全不同引用类型的变量：

```java
Integer age = null;
List<String> names = null;复制
```

这也解释了为什么我们不能将*null值分配给**boolean*等基本类型的变量：

```java
Boolean validReference = null // this works fine
boolean validPrimitive = null // this does not复制
```

这是因为 *空*引用可以转换为引用类型，但不能转换为原始类型。*布尔*变量的一组可能值总是有两个元素。

## 4. *null*作为方法参数

让我们看一下两个简单的方法，它们都采用一个参数但类型不同：

```typescript
void printMe(Integer number) {
  System.out.println(number);
}

void printMe(String string) {
  System.out.println(string);
}复制
```

由于 Java 中的多态性，我们可以这样调用这些方法：

```java
printMe(6);
printMe("Hello");复制
```

编译器会理解我们引用的是什么方法。但是下面的语句会导致编译错误：

```java
printMe(null); // does not compile复制
```

**为什么？因为\*null\*可以同时转换为\*String\*和\*Integer –\*编译器不知道选择哪种方法。**

## 5.*空指针异常*

正如我们已经看到的，我们可以将*null*引用分配给引用类型的变量，即使 *null*在技术上是不同的、单独的类型。**如果我们尝试使用该变量的某些属性，就好像它不为\*空一样，\*我们将得到一个运行时异常\*——NullPointerException\*。**发生这种情况是因为*空*引用不是我们引用它的类型，并且不具有我们期望它具有的属性：

```java
String name = null;
name.toLowerCase(); // will cause exception at runtime复制
```

在 Java 14 之前， *NullPointerExceptions*很简短，只是说明错误发生在代码的哪一行。如果该行很复杂并且有一个调用链，则该信息不提供信息。但是，从 Java 14 开始，我们可以依赖所谓的[Helpful NullPointerExceptions](https://www.baeldung.com/java-14-nullpointerexception)。

## 六，结论

在本文中，我们仔细研究了*null*类型的工作原理。首先，我们定义了一个类型，然后我们找到了*空*类型如何适合该定义。最后，我们了解了如何将*空*引用转换为任何其他引用类型，使其成为我们了解和使用的工具。