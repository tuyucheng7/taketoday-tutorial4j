## 1. 概述

[在本教程中，我们将讨论 Java泛型](https://www.baeldung.com/java-generics)中形式类型参数和通配符之间的区别以及如何正确使用它们。

认为它们相同是一个常见的错误。在写泛型方法的时候，我们经常会想是应该使用类型参数还是通配符。在本教程中，我们将尝试找到这个问题的答案。

## 2. 通用类

在许多编程语言中，一种类型可以被另一种类型参数化。这是一种在 Java 中称为泛型或在其他语言中称为参数多态性的特性。

泛型是在 Java 5 中引入的。使用它们，我们可以创建仅类型不同的类和方法。此外，我们可以创建可重用的代码。

在我们的类和接口中引入泛型时，我们应该使用类型参数。

作为示例，让我们看一下[java.lang.Comparable](https://www.baeldung.com/java-comparator-comparable#comparable)接口：

```java
public interface Comparable<T> {
    public int compareTo(T o);
}
```

类型参数定义正式类型，通常以一个大写字母命名(例如T、E)。在这里，参数T在整个Comparable接口中都可用。

实例化时，我们需要提供具体类型(类型参数)。**我们不能使用通配符来定义泛型类或接口。**

## 3.通用方法

使用泛型的最常见方式是用于公共静态方法，因为它们不是可以声明类型的实例的一部分。我们经常使用它们来创建库或为我们的客户提供 API。

### 3.1. 方法参数

要编写带有泛型类型参数的方法，我们应该使用类型参数。因此，让我们创建一个打印给定项目的方法：

```java
public static <T> void print(T item){
    System.out.println(item);
}
```

在此示例中，我们无法用通配符替换类型参数T。***我们不能直接使用通配符来指定方法中参数的类型。**我们唯一可以使用它们的地方是作为泛型代码的一部分，例如，作为在尖括号中定义的泛型类型参数。

通常情况下，我们可以使用通配符或类型参数来声明泛型方法。例如，这里有两个可能的swap()方法声明：

```java
public static <E> void swap(List<E> list, int src, int des);
public static void swap(List<?> list, int src, int des);
```

第一种方法使用无界类型参数，而第二种方法使用无界通配符。每当我们有一个无限泛型类型时，我们应该更喜欢第二个声明。

通配符使代码更简单、更灵活。我们可以传递任何列表，此外，我们不必担心类型参数。**如果一个类型参数在方法声明中只出现一次，我们应该考虑用通配符代替它。**同样的规则也适用于有界类型参数。

没有上限或下限，通配符表示“任何类型”，或未知类型。其目的是允许在不同的方法调用中使用各种实际参数类型。此外，通配符旨在支持灵活的子类型化。

### 3.2. 返回类型

现在，让我们看一下返回通配符类型的merge()方法：

```java
public static <E> List<? extends E> mergeWildcard(List<? extends E> listOne, List<? extends E> listTwo) {
    return Stream.concat(listOne.stream(), listTwo.stream())
            .collect(Collectors.toList());
}
```

假设我们有两个要合并的Number列表：

```java
List<Number> numbers1 = new ArrayList<>();
numbers1.add(5);
numbers1.add(10L);

List<Number> numbers2 = new ArrayList<>();
numbers2.add(15f);
numbers2.add(20.0);
```

由于我们要传递两个Number列表，因此我们希望收到相同类型的List。如果我们将通配符类型作为返回类型，情况就不会如此。下面的代码无法编译：

```java
List<Number> numbersMerged = CollectionUtils.mergeWildcard(numbers1, numbers2);
```

使用通配符不会提供额外的灵活性，而是会迫使客户自己处理它们。如果使用得当，客户端不应该知道通配符的使用。否则，它可能表明我们的代码存在设计问题。

**当泛型方法返回泛型类型时，我们应该使用类型参数而不是通配符：**

```java
public static <E> List<E> mergeTypeParameter(List<? extends E> listOne, List<? extends E> listTwo) {
    return Stream.concat(listOne.stream(), listTwo.stream())
            .collect(Collectors.toList());
}
```

使用新的实现，我们可以将结果存储在Number元素列表中。

## 4.界限

通用类型边界允许我们限制可以使用哪些类型而不是通用类型。这个 Java 特性使得以多态方式处理泛型成为可能。

例如，对数字进行操作的方法可能只想接受Number类或其子类的实例。这就是有界类型参数的用途。

我们可以通过三种方式使用带边界的通配符：

-   无限通配符：List<?> – 表示任意类型的列表
-   上限通配符：List<? extends Number> – 表示Number或其子类型的列表(例如，Double或Integer)。
-   下界通配符：List<? super Integer> – 表示Integer或其超类型、Number和Object的列表

另一方面，有界参数类型是为泛型指定边界的泛型类型。我们可以通过两种方式绑定类型参数：

-   无界类型参数：List<T>表示类型为T的列表
-   Bounded Type Parameter: List<T extends Number & Comparable>表示实现了Comparable接口的Number或其子类型如Integer和Double的列表

**我们不能使用具有下限的类型参数。**此外，**类型参数可以有多个边界，而通配符不能。**

### 4.1. 上限类型

在泛型中，参数化类型是不变的。换句话说，我们知道，例如，Long类是Number类的子类型。但是，了解List<Long>不是List<Number>的子类型很重要。为了更好地理解后者，让我们创建一个方法来汇总集合中元素的值。

假设我们想要总结Number类的任何子类型。如果没有泛型，我们的实现可能如下所示：

```java
public static long sum(List<Number> numbers) {
    return numbers.stream().mapToLong(Number::longValue).sum();
}
```

现在，让我们创建一个Number元素列表并调用该方法：

```java
List<Number> numbers = new ArrayList<>();
numbers.add(5);
numbers.add(10L);
numbers.add(15f);
numbers.add(20.0);
CollectionUtils.sum(numbers);
```

在这里，一切都按预期工作。但是，如果我们想传递一个只包含Integer元素的列表，则会出现编译器错误。尽管Integer是Number的子类型，但List<Integer>不是List<Number>的子类型。

澄清一下，我们无法传递整数类型列表，因为我们会映射两个不兼容的类型。List<Integer>和List<Number>不像Integer和Number那样相关，它们只共享共同的父级(List<?>)。

为了解决这个问题，我们可以使用带上限的通配符。这种类型的绑定使用关键字extends并且它指定泛型类型必须是该类本身的给定类的子类型。

首先，让我们使用通配符修改方法：

```java
public static long sumWildcard(List<? extends Number> numbers) {
    return numbers.stream().mapToLong(Number::longValue).sum();
}
```

接下来，我们可以使用包含Number类的子类型的列表来调用该方法：

```java
List<Integer> integers = new ArrayList<>();
integers.add(5);
integers.add(10);
CollectionUtils.sumWildcard(integers);
```

在此示例中，List<Integer>是List<? extends Number>使方法的调用有效且类型安全。

同样，我们可以使用类型参数完成相同的功能：

```java
public static <T extends Number> long sumTypeParameter(List<T> numbers) {
    return numbers.stream().mapToLong(Number::longValue).sum();
}

```

在这里，列表中元素的类型成为方法的类型参数。它的名称为T，但仍未指定并受上限限制(<T extends Number>)。

### 4.2. 下界类型

**这种类型的绑定只能与通配符一起使用，因为类型参数不支持它们。**它使用super关键字表示，它指定层次结构中可用作泛型类型的较低类。

假设我们要编写一个将数字添加到列表的通用方法。假设我们不想支持小数，而只支持Integer。为了最大限度地提高灵活性，我们希望允许用户使用Integer及其所有超类型(Number或Object)的列表来调用我们的方法。换句话说，任何可以保存整数值的东西。我们应该使用下界通配符：

```java
public static void addNumber(List<? super Integer> list, Integer number) {
    list.add(number);
}
```

在这里，任何子类型都可以插入到用超类型定义的集合中。

**如果我们不确定应该使用上限还是下限，我们可以考虑[PECS](https://www.baeldung.com/java-generics-interview-questions#q13-when-would-you-choose-to-use-a-lower-bounded-type-vs-an-upper-bounded-type) – Producer Extends, Consumer Super。**

每当我们的方法使用一个集合时，例如添加元素，我们应该使用下限。另一方面，如果我们的方法只读取元素，我们应该使用上限。此外，如果我们的方法同时执行这两种操作，即它生成并使用一个集合，则我们不能应用 PECS 规则，而应该使用无界类型。

为了清楚起见，让我们将 PECS 规则应用到我们的示例中。我们的通用方法通过向列表添加元素来修改列表。从列表的角度来看，它允许添加元素，但读取元素并不安全。如果我们试图用上限替换下限，我们会得到一个编译器错误。

### 4.3. 无限类型

有时，我们想创建一个修改集合的通用方法。

我们已经提到我们应该考虑使用通配符而不是类型参数来增加灵活性。但是，对支持集合修改的方法使用通配符可能会很棘手。

让我们考虑前面提到的swap()方法的实现。该方法的直接实现不会编译：

```java
public static void swap(List<?> list, int srcIndex, int destIndex) {
    list.set(srcIndex, list.set(destIndex, list.get(srcIndex)));
}
```

代码将无法编译，因为我们将列表声明为任何类型，因此 Java 希望通过禁止对可以包含任何元素的列表进行任何修改来拯救我们。调用set()方法时，编译器无法确定要插入到列表中的对象的类型。因此它会产生错误。这样，Java 在编译时强制执行类型安全。

此外，我们不能使用边界，因为我们的方法会生成(读取)和使用(更新)列表。换句话说，这里不能应用 PECS 规则，我们应该使用无界类型。

我们可以通过编写一个通用的辅助方法来捕获通配符类型来解决这个问题：

```java
private static <E> void swapHelper(List<E> list, int src, int des) {
    list.set(src, list.set(des, list.get(src)));
}
```

swapHelper ()方法知道 list 是一个List<E>。因此，它知道它从这个列表中得到的任何值都是E类型的，并且可以安全地将任何E类型的值放回列表中。

### 4.4. 多重边界

当我们想要限制具有多个边界的类型时，类型参数很有用。

首先，让我们创建一个简单的层次结构。让我们定义一个Animal类：

```java
abstract class Animal {

    protected final String type;
    protected final String name;

    protected Animal(String type, String name) {
        this.type = type;
        this.name = name;
    }

    abstract String makeSound();
}

```

其次，让我们创建两个具体类：

```java
class Dog extends Animal {

    public Dog(String type, String name) {
        super(type, name);
    }

    @Override
    public String makeSound() {
        return "Wuf";
    }

}
```

此外，第二个具体类也实现了一个Comparable接口：

```java
class Cat extends Animal implements Comparable<Cat> {
    public Cat(String type, String name) {
        super(type, name);
    }

    @Override
    public String makeSound() {
        return "Meow";
    }

    @Override
    public int compareTo(@NotNull Cat cat) {
        return this.getName().length() - cat.getName().length();
    }
}
```

最后，让我们定义一个方法来对给定列表中的元素进行排序，并要求值是可比较的：

```java
public static <T extends Animal & Comparable<T>> void order(List<T> list) {
    list.sort(Comparable::compareTo);
}
```

这样，我们的列表就不能包含Dog类型的元素，因为该类没有实现Comparable接口。

## 5.总结

在本文中，我们讨论了 Java 泛型中类型参数和通配符之间的区别。总而言之，在编写广泛使用的库时，我们应该更喜欢通配符而不是类型参数。在决定使用哪种绑定类型时，我们应该记住基本的 PECS 规则。