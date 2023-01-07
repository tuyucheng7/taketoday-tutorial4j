## 1. 概述

在这个简短的教程中，我们将重点介绍[ClassCastException](https://javadoc.scijava.org/Java8/java/lang/ClassCastException.html)，这是一种[常见的Java异常](https://www.baeldung.com/java-common-exceptions)。

ClassCastException是一个[未经检查的异常](https://www.baeldung.com/java-checked-unchecked-exceptions)，表示代码已尝试将引用强制转换为不是子类型的类型。

让我们看一下导致抛出此异常的一些场景以及我们如何避免它们。

## 2.显式转换

对于我们的下一个实验，让我们考虑以下类：

```java
public interface Animal {
    String getName();
}
public class Mammal implements Animal {
    @Override
    public String getName() {
        return "Mammal";
    }
}
public class Amphibian implements Animal {
    @Override
    public String getName() {
        return "Amphibian";
    }
}
public class Frog extends Amphibian {
    @Override
    public String getName() {
        return super.getName() + ": Frog";
    }
}
```

### 2.1. 铸造类

到目前为止，遇到ClassCastException的最常见情况是显式转换为不兼容的类型。

例如，让我们尝试将Frog转换为Mammal：

```java
Frog frog = new Frog();
Mammal mammal = (Mammal) frog;
```

我们可能期望这里出现ClassCastException，但实际上，我们得到了一个编译错误：“不兼容的类型：Frog 无法转换为 Mammal”。但是，当我们使用公共超类型时，情况发生了变化：

```java
Animal animal = new Frog();
Mammal mammal = (Mammal) animal;
```

现在，我们从第二行得到一个ClassCastException ：

```bash
Exception in thread "main" java.lang.ClassCastException: class Frog cannot be cast to class Mammal (Frog and Mammal are in unnamed module of loader 'app') 
at Main.main(Main.java:9)
```

向下转换为Mammal与Frog引用不兼容，因为Frog不是Mammal的子类型。在这种情况下，编译器无法帮助我们，因为Animal变量可能包含兼容类型的引用。

有趣的是，编译错误仅在我们尝试强制转换为明确不兼容的类时发生。接口也不一样，因为Java支持接口多继承，而类只支持单继承。因此，编译器无法确定引用类型是否实现了特定接口。让我们举例说明：

```java
Animal animal = new Frog();
Serializable serial = (Serializable) animal;
```

我们在第二行得到一个ClassCastException而不是编译错误：

```bash
Exception in thread "main" java.lang.ClassCastException: class Frog cannot be cast to class java.io.Serializable (Frog is in unnamed module of loader 'app'; java.io.Serializable is in module java.base of loader 'bootstrap') 
at Main.main(Main.java:11)
```

### 2.2. 铸造阵列

我们已经了解了类如何处理类型转换，现在让我们看看数组。数组转换与类转换的工作方式相同。然而，我们可能会对自动装箱和类型提升感到困惑，或者没有。

因此，让我们看看当我们尝试以下转换时原始数组会发生什么：

```java
Object primitives = new int[1];
Integer[] integers = (Integer[]) primitives;
```

第二行抛出ClassCastException，因为[自动装箱](https://www.baeldung.com/java-wrapper-classes)不适用于数组。

类型提升怎么样？让我们尝试以下操作：

```java
Object primitives = new int[1];
long[] longs = (long[]) primitives;
```

我们还会得到ClassCastException，因为[类型提升](https://www.baeldung.com/java-primitive-conversions)不适用于整个数组。

### 2.3. 安全铸造

在显式转换的情况下，强烈建议在尝试使用[instanceof](https://www.baeldung.com/java-instanceof)进行转换之前检查类型的兼容性。

让我们看一个安全的转换示例：

```java
Mammal mammal;
if (animal instanceof Mammal) {
    mammal = (Mammal) animal;
} else {
    // handle exceptional case
}
```

## 3. 堆污染

根据[Java 规范](https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.12.2)：“[堆污染](https://en.wikipedia.org/wiki/Heap_pollution)只有在程序执行某些涉及原始类型的操作时才会发生，这会导致编译时未经检查的警告”。

对于我们的实验，让我们考虑以下泛型类：

```typescript
public static class Box<T> {
    private T content;

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
```

我们现在将尝试如下污染堆：

```java
Box<Long> originalBox = new Box<>();
Box raw = originalBox;
raw.setContent(2.5);
Box<Long> bound = (Box<Long>) raw;
Long content = bound.getContent();
```

最后一行将抛出ClassCastException，因为它无法将 D ouble引用转换为Long。

## 4. 通用类型

在Java中使用泛型时，我们必须警惕[类型擦除](https://www.baeldung.com/java-type-erasure)，这在某些情况下也会导致ClassCastException 。

让我们考虑以下通用方法：

```java
public static <T> T convertInstanceOfObject(Object o) {
    try {
        return (T) o;
    } catch (ClassCastException e) {
        return null;
    }
}
```

现在我们称它为：

```java
String shouldBeNull = convertInstanceOfObject(123);
```

乍一看，我们可以合理地预期从 catch 块返回空引用。但是，在运行时，由于类型擦除，参数被强制转换为Object而不是String。因此，编译器面临着将Integer分配给String的任务，这会抛出ClassCastException。

## 5.总结

在本文中，我们研究了一系列不当转换的常见场景。

无论是隐式还是显式，将Java引用强制转换为另一种类型都可能导致ClassCastException，除非目标类型与实际类型相同或为实际类型的后代。