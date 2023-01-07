## 1. 概述

我们可能希望将[数组用作支持](https://www.baeldung.com/java-arrays-guide)[泛型的](https://www.baeldung.com/java-generics)类或函数的一部分，但由于Java处理泛型的方式，这可能很困难。

在本教程中，我们将讨论对数组使用泛型的挑战。然后我们将创建一个通用数组的示例。

最后，我们将了解JavaAPI 如何解决类似的问题。

## 2. 使用通用数组时的注意事项

数组和泛型之间的一个重要区别是它们如何执行类型检查。具体来说，数组在运行时存储和检查类型信息。然而，泛型在编译时检查类型错误，[在运行时没有类型信息。](https://www.baeldung.com/java-generics#type-erasure)

Java 的语法表明我们可以创建一个新的通用数组：

```java
T[] elements = new T[size];
```

但是如果我们尝试这样做，我们会得到一个编译错误。

要了解原因，让我们考虑以下几点：

```java
public <T> T[] getArray(int size) {
    T[] genericArray = new T[size]; // suppose this is allowed
    return genericArray;
}
```

由于未绑定泛型类型 T解析为 Object， 我们在运行时的方法将是：

```java
public Object[] getArray(int size) {
    Object[] genericArray = new Object[size];
    return genericArray;
}
```

如果我们调用我们的方法并将结果存储在一个字符串数组中：

```java
String[] myArray = getArray(5);
```

该代码可以正常编译，但在运行时会因ClassCastException而失败。这是因为我们刚刚将Object[]分配给了String[]引用。具体来说，编译器的隐式转换将无法将Object[]转换为我们所需的类型 String[]。

虽然我们不能直接初始化泛型数组，但如果调用代码提供了精确类型的信息，仍然可以实现等效操作。

## 3. 创建通用数组

对于我们的示例，让我们考虑一个有界堆栈数据结构MyStack，其中容量固定为特定大小。由于我们希望堆栈适用于任何类型，因此合理的实现选择是通用数组。

首先，我们将创建一个字段来存储堆栈中的元素，它是E类型的通用数组：

```java
private E[] elements;
```

然后我们将添加一个构造函数：

```java
public MyStack(Class<E> clazz, int capacity) {
    elements = (E[]) Array.newInstance(clazz, capacity);
}
```

请注意我们如何使用java.lang.reflect.Array#newInstance来初始化我们的通用数组，它需要两个参数。第一个参数指定新数组中对象的类型。第二个参数指定为数组创建多少空间。由于Array#newInstance的结果是Object 类型，我们需要将其转换为E[]以创建我们的通用数组。

我们还应该注意命名类型参数 clazz 的约定 ，而不是 class，这是Java中的保留字。

## 4.考虑ArrayList

### 4.1. 使用ArrayList代替数组

使用通用 ArrayList代替 通用数组通常更容易。让我们看看如何更改MyStack以使用ArrayList。

首先，我们将创建一个字段来存储我们的元素：

```java
private List<E> elements;
```

然后，在我们的堆栈构造函数中，我们可以用初始容量初始化ArrayList ：

```java
elements = new ArrayList<>(capacity);
```

它使我们的类更简单，因为我们不必使用反射。此外，我们不需要在创建堆栈时传入类文字。由于我们可以设置ArrayList的初始容量，因此我们可以获得与数组相同的好处。

因此，我们只需要在极少数情况下或在与某些需要数组的外部库交互时构造泛型数组。

### 4.2. ArrayList实现

有趣的是，ArrayList本身是使用通用数组实现的。让我们看看ArrayList 的内部结构。

首先，让我们看一下列表元素字段：

```java
transient Object[] elementData;
```

注意 ArrayList 使用Object作为元素类型。由于我们的泛型类型直到运行时才为人所知，因此Object被用作任何类型的超类。

值得注意的是，几乎所有ArrayList中的操作都可以使用这个泛型数组，因为它们不需要向外界提供强类型数组(除了一个方法，toArray)。

## 5. 从集合构建数组

### 5.1. 链表示例

让我们看看在JavaCollections API 中使用泛型数组，我们将从集合中构建一个新数组。

首先，我们将创建一个 带有类型参数 String 的新LinkedList ， 并向其中添加项目：

```java
List<String> items = new LinkedList();
items.add("first item");
items.add("second item");

```

然后我们将构建一个我们刚刚添加的项目的数组：

```java
String[] itemsAsArray = items.toArray(new String[0]);
```

要构建我们的数组，List。toArray方法需要一个输入数组。它纯粹使用此数组来获取类型信息以创建正确类型的返回数组。

在上面的示例中，我们使用new String[0]作为输入数组来构建结果字符串数组。

### 5.2. LinkedList.toArray实现

让我们深入了解 LinkedList.toArray ，看看它是如何在JavaJDK 中实现的。

首先，我们来看看方法签名：

```java
public <T> T[] toArray(T[] a)
```

然后我们将看到如何在需要时创建一个新数组：

```java
a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
```

注意它是如何使用 Array#newInstance来构建一个新数组的，就像我们之前的堆栈示例一样。我们还可以看到参数a用于为 Array#newInstance 提供类型。 最后，将Array#newInstance的结果转换为T[]以创建通用数组。

## 6. 从流创建数组

Java [Streams API](https://www.baeldung.com/java-streams)允许我们从流中的项目创建数组。有几个陷阱需要注意，以确保我们生成正确类型的数组。

### 6.1. 使用 toArray

我们可以轻松地将Java8 Stream中的项目转换为数组：

```java
Object[] strings = Stream.of("A", "AAA", "B", "AAB", "C")
  .filter(string -> string.startsWith("A"))
  .toArray();

assertThat(strings).containsExactly("A", "AAA", "AAB");

```

但是，我们应该注意，基本的toArray函数为我们提供了一个Object数组 ，而不是一个 String数组：

```java
assertThat(strings).isNotInstanceOf(String[].class);
```

正如我们之前看到的，每个数组的精确类型是不同的。由于Stream中的类型 是泛型，因此库无法[在运行时推断类型](https://www.baeldung.com/java-type-erasure)。

### 6.2. 使用toArray重载获取类型化数组

常见的集合类方法使用反射来构造特定类型的数组，而JavaStreams 库使用函数方法。我们可以传入一个 lambda 或方法引用，它会在Stream准备好填充它时创建一个具有正确大小和类型的数组：

```java
String[] strings = Stream.of("A", "AAA", "B", "AAB", "C")
  .filter(string -> string.startsWith("A"))
  .toArray(String[]::new);

assertThat(strings).containsExactly("A", "AAA", "AAB");
assertThat(strings).isInstanceOf(String[].class);
```

我们传递的方法是一个IntFunction，它接受一个整数作为输入并返回一个该大小的新数组。这正是String[]的构造函数所做的，因此我们可以使用[方法引用](https://www.baeldung.com/java-method-references) String[]::new。

### 6.3. 具有自己的类型参数的泛型

现在让我们假设我们想要将流中的值转换为一个本身具有类型参数的对象，例如 List或 Optional。也许我们有一个我们想要调用的 API，它将 Optional<String>[]作为其输入。

声明这种数组是有效的：

```java
Optional<String>[] strings = null;
```

我们还可以使用 map方法轻松获取 Stream<String>并将其转换为 Stream<Optional<String>>：

```java
Stream<Optional<String>> stream = Stream.of("A", "AAA", "B", "AAB", "C")
  .filter(string -> string.startsWith("A"))
  .map(Optional::of);
```

但是，如果我们尝试构造数组，我们会再次遇到编译器错误：

```javascript
// compiler error
Optional<String>[] strings = new Optional<String>[1];
```

幸运的是，这个例子和我们之前的例子是有区别的。其中String[]不是Object[]的子类，而 Optional[]实际上是与Optional<String>[]相同的运行时类型 。换句话说，这是一个我们可以通过类型转换来解决的问题：

```java
Stream<Optional<String>> stream = Stream.of("A", "AAA", "B", "AAB", "C")
  .filter(string -> string.startsWith("A"))
  .map(Optional::of);
Optional<String>[] strings = stream
  .toArray(Optional[]::new);
```

这段代码可以编译并运行，但是给了我们一个未经检查的赋值警告。我们需要在我们的方法中添加一个SuppressWarnings来解决这个问题：

```java
@SuppressWarnings("unchecked")
```

### 6.4. 使用辅助函数

如果我们想避免将 SuppressWarnings添加到代码中的多个位置，并希望记录我们的通用数组是如何从原始类型创建的，我们可以编写一个辅助函数：

```java
@SuppressWarnings("unchecked")
static <T, R extends T> IntFunction<R[]> genericArray(IntFunction<T[]> arrayCreator) {
    return size -> (R[]) arrayCreator.apply(size);
}
```

此函数将生成原始类型数组的函数转换为承诺生成我们需要的特定类型数组的函数：

```java
Optional<String>[] strings = Stream.of("A", "AAA", "B", "AAB", "C")
  .filter(string -> string.startsWith("A"))
  .map(Optional::of)
  .toArray(genericArray(Optional[]::new));
```

此处不需要取消未经检查的分配警告。

但是，我们应该注意，可以调用此函数来执行到更高类型的类型转换。例如，如果我们的流包含List<String>类型的对象，我们可能会错误地调用 genericArray来生成ArrayList<String>的数组 ：

```arduino
ArrayList<String>[] lists = Stream.of(singletonList("A"))
  .toArray(genericArray(List[]::new));
```

这会编译，但会抛出 ClassCastException ，因为ArrayList[]不是List[] 的子类。不过，编译器会为此生成一个未经检查的赋值警告，因此很容易发现。

## 七、总结

在本文中，我们研究了数组和泛型之间的区别。然后我们查看了创建通用数组的示例，演示了使用ArrayList比使用通用数组更容易。我们还讨论了 Collections API 中通用数组的使用。

最后，我们学习了如何从 Streams API 生成数组，以及如何处理创建使用类型参数的类型数组。