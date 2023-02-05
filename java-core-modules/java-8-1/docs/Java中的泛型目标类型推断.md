## 1. 简介

类型推断是在Java 5中引入的，以补充泛型的引入，并在随后的Java版本中得到了大幅扩展，这也称为泛型目标类型推断。

在本教程中，我们将通过代码示例来探索这个概念。

## 2. 泛型

泛型为我们提供了许多好处，例如提高类型安全性、避免类型转换错误和泛型算法。你可以在[本文](https://www.baeldung.com/java-generics)中阅读有关泛型的更多信息。

然而，**由于需要传递类型参数，泛型的引入导致编写样板代码的必要性**。一些例子是：

```java
Map<String, Map<String, String>> mapOfMaps = new HashMap<String, Map<String, String>>();
List<String> strList = Collections.<String>emptyList();
List<Integer> intList = Collections.<Integer>emptyList();
```

## 3. Java 8之前的类型推断

**为了减少不必要的代码冗长，Java引入了类型推断，这是根据上下文信息自动推断表达式的未指定数据类型的过程**。

现在，我们可以在不指定参数类型的情况下调用相同的泛型类型和方法，编译器会在需要时自动推断参数类型。

我们可以看到使用新概念的相同代码：

```java
List<String> strListInferred = Collections.emptyList();
List<Integer> intListInferred = Collections.emptyList();
```

在上面的示例中，基于预期的返回类型List<String\>和List<Integer\>，编译器能够将类型参数推断为以下泛型方法：

```java
public static final <T> List<T> emptyList()
```

正如我们所见，生成的代码很简洁。**现在，如果可以推断类型参数，我们可以将泛型方法作为普通方法调用**。

在Java 5中，我们可以在如上所示的特定上下文中进行类型推断。

Java 7扩展了它可以执行的上下文，它引入了钻石运算符<>。你可以在[本文](https://www.baeldung.com/java-diamond-operator)中阅读有关钻石运算符的更多信息。

现在，**我们可以在赋值上下文中对泛型类构造函数执行此操作**，一个这样的例子是：

```java
Map<String, Map<String, String>> mapOfMapsInferred = new HashMap<>();
```

在这里，Java编译器使用预期的赋值类型来推断HashMap构造函数的类型参数。

## 4. 泛型目标类型推断-Java 8

Java 8进一步扩展了类型推断的范围，我们将这种扩展的推理能力称为泛型目标类型推理。你可以在[此处](https://docs.oracle.com/javase/specs/jls/se8/html/jls-18.html)阅读技术详细信息。

Java 8还引入了Lambda表达式，**Lambda表达式没有明确的类型。它们的类型是通过查看上下文或情况的目标类型来推断的**。表达式的目标类型是Java编译器期望的数据类型，具体取决于表达式出现的位置。

Java 8支持在方法上下文中使用目标类型进行推理。**当我们调用没有显式类型参数的泛型方法时，编译器可以查看方法调用和相应的方法声明以确定使调用适用的类型参数(或参数)**。

让我们看一个示例代码：

```java
public static <T> List<T> add(List<T> list, T a, T b) {
	list.add(a);
	list.add(b);
	return list;
}

List<String> strListGeneralized = add(new ArrayList<>(), "abc", "def");
List<Integer> intListGeneralized = add(new ArrayList<>(), 1, 2);
List<Number> numListGeneralized = add(new ArrayList<>(), 1, 2.0);
```

在代码中，ArrayList<>没有显式提供类型参数，因此编译器需要推断它。首先，编译器查看add方法的参数。然后，它查看在不同调用中传递的参数。

**它执行调用适用性推理分析，以确定该方法是否适用于这些调用**。如果多个方法由于重载而适用，编译器将选择最具体的方法。

然后，**编译器执行调用类型推断分析以确定类型参数，此分析中还使用了预期的目标类型**。它将三个实例中的参数推导出为ArrayList<String\>、ArrayList<Integer\>和ArrayList<Number\>。

目标类型推断允许我们不为Lambda表达式参数指定类型：

```java
List<Integer> intList = Arrays.asList(5, 2, 4, 2, 1);
Collections.sort(intList, (a, b) -> a.compareTo(b));

List<String> strList = Arrays.asList("Red", "Blue", "Green");
Collections.sort(strList, (a, b) -> a.compareTo(b));
```

这里，参数a和b没有明确定义的类型。它们的类型在第一个Lambda表达式中被推断为Integer，在第二个中被推断为String。

## 5. 总结

在这篇快速文章中，我们回顾了类型推断，它与泛型和Lambda表达式一起使我们能够编写简洁的Java代码。
