## 1. 概述

在Java 8中，Lambda表达式通过提供一种简洁的方式来表达行为，开始促进函数式编程。然而，JDK提供的函数式接口并不能很好地处理异常，当涉及到处理它们时，代码变得冗长和繁琐。

在本文中，我们将探讨一些在编写lambda表达式时处理异常的方法。

## 2. 处理未经检查的异常

首先，让我们通过一个例子来理解这个问题。

我们有一个List<Integer\>并且我们想用这个集合的每个元素除一个常量，比如50并打印结果：

```java
List<Integer> integers = Arrays.asList(3, 9, 7, 6, 10, 20);
integers.forEach(i -> System.out.println(50 / i));
```

这个表达式有效，但有一个问题。如果集合中的某个元素是0，那么我们会得到一个ArithmeticException: / by zero。让我们通过使用传统的try-catch块来解决这个问题，这样我们就会记录任何此类异常并继续执行下一个元素：

```java
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
integers.forEach(i -> {
    try {
        System.out.println(50 / i);
    } catch (ArithmeticException e) {
        System.err.println("Arithmetic Exception occured : " + e.getMessage());
    }
});
```

使用try-catch可以解决该问题，但是失去了Lambda表达式的简洁性。

为了处理这个问题，我们可以为lambda函数编写一个lambda包装器。让我们看一下代码，看看它是如何工作的：

```java
static Consumer<Integer> lambdaWrapper(Consumer<Integer> consumer) {
    return i -> {
        try {
            consumer.accept(i);
        } catch (ArithmeticException e) {
            System.err.println("Arithmetic Exception occured : " + e.getMessage());
        }
    };
}
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
integers.forEach(lambdaWrapper(i -> System.out.println(50 / i)));
```

首先，我们编写了一个包装器方法来负责处理异常，然后将lambda表达式作为参数传递给该方法。

包装器方法按预期工作，但你可能会争辩说，它基本上是从lambda表达式中删除try -catch块并将其移动到另一个方法，并且它不会减少实际编写的代码行数。

在这种情况下确实如此，其中包装器特定于特定用例，但我们可以利用泛型来改进此方法并将其用于各种其他场景：

```java
static <T, E extends Exception> Consumer<T> onsumerWrapper(Consumer<T> consumer, Class<E> clazz) {
    return i -> {
        try {
            consumer.accept(i);
        } catch (Exception ex) {
            try {
                E exCast = clazz.cast(ex);
                System.err.println(
                  "Exception occured : " + exCast.getMessage());
            } catch (ClassCastException ccEx) {
                throw ex;
            }
        }
    };
}
```

```java
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
integers.forEach(consumerWrapper(
    i -> System.out.println(50 / i), 
    ArithmeticException.class));
```

正如我们所见，包装器方法的这次迭代有两个参数，即lambda表达式和要捕获的异常类型。这个lambda包装器能够处理所有数据类型，而不仅仅是Integer，并捕获任何特定类型的异常，而不是超类Exception。

另外，请注意我们已将方法的名称从lambdaWrapper更改为consumerWrapper，这是因为此方法仅处理Consumer类型的函数接口的lambda表达式。我们可以为其他函数接口(如Function、BiFunction、BiConsumer等)编写类似的包装器方法。

## 3. 处理检查异常

让我们修改上一节中的示例，将输出写入一个文件，而不是打印到控制台。

```java
static void writeToFile(Integer integer) throws IOException {
    // logic to write to file which throws IOException
}
```

请注意，上述方法可能会抛出IOException。

```java
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
integers.forEach(i -> writeToFile(i));
```

在编译时，我们得到错误：

```shell
java.lang.Error: Unresolved compilation problem: Unhandled exception type IOException
```

**因为IOException是受检异常，所以我们必须显式处理它**。我们有两个选择：首先，我们可以简单地将异常抛到我们的方法之外并在其他地方处理它。或者，我们可以在使用lambda表达式的方法中处理它。

### 3.1 从Lambda表达式中抛出受检异常

让我们看看当我们在main方法上声明IOException时会发生什么：

```java
public static void main(String[] args) throws IOException {
    List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
    integers.forEach(i -> writeToFile(i));
}
```

尽管如此，我们在编译过程中仍会遇到未处理的IOException错误。

```java
java.lang.Error: Unresolved compilation problem: Unhandled exception type IOException
```

这是因为lambda表达式类似于[匿名内部类]()。

在我们的例子中，writeToFile方法是[Consumer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/class-use/Consumer.html)函数接口的实现。

我们来看看Consumer的定义：

```java
@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}
```

正如我们所看到的，accept方法没有声明任何受检的异常，这就是不允许writeToFile抛出IOException的原因。

最直接的方法是使用try-catch块，将受检的异常包装到非受检的异常中并重新抛出它：

```java
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
integers.forEach(i -> {
    try {
        writeToFile(i);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
});
```

这将获取要编译和运行的代码。然而，这种方法引入了我们在上一节中已经讨论过的相同问题-冗长和繁琐。

我们可以做得更好。

让我们创建一个自定义函数接口，其中包含引发异常的单个accept方法。

```java
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
    void accept(T t) throws E;
}
```

现在，让我们实现一个能够重新抛出异常的包装器方法：

```java
static <T> Consumer<T> throwingConsumerWrapper(ThrowingConsumer<T, Exception> throwingConsumer) {
    return i -> {
        try {
            throwingConsumer.accept(i);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    };
}
```

最后，我们能够简化使用writeToFile方法的方式：

```java
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
integers.forEach(throwingConsumerWrapper(i -> writeToFile(i)));
```

这仍然是一种变通方法，但最终结果看起来很干净，而且绝对更容易维护。

ThrowingConsumer和throwingConsumerWrapper都是通用的，可以在我们应用程序的不同地方轻松重用。

### 3.2 处理Lambda表达式中的受检异常

在这最后一节中，我们将修改包装器以处理受检的异常。

由于我们的ThrowingConsumer接口使用泛型，我们可以轻松处理任何特定的异常。

```java
static <T, E extends Exception> Consumer<T> handlingConsumerWrapper(ThrowingConsumer<T, E> throwingConsumer, Class<E> exceptionClass) {
    return i -> {
        try {
            throwingConsumer.accept(i);
        } catch (Exception ex) {
            try {
                E exCast = exceptionClass.cast(ex);
                System.err.println("Exception occured : " + exCast.getMessage());
            } catch (ClassCastException ccEx) {
                throw new RuntimeException(ex);
            }
        }
    };
}
```

让我们看看如何在实践中使用它：

```java
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
integers.forEach(handlingConsumerWrapper(
  i -> writeToFile(i), IOException.class));
```

**请注意，上面的代码仅处理IOException，而任何其他类型的异常都将作为RuntimeException重新抛出**。

## 4. 总结

在本文中，我们演示了如何借助包装器方法在不失简洁的情况下处理lambda表达式中的特定异常。我们还学习了如何为JDK中存在的函数式接口编写Throws的替代方案，以抛出或处理受检的异常。

另一种方法是[sneakily-throwing]()。

如果你正在寻找开箱即用的工作解决方案，[ThrowingFunction](https://github.com/pivovarit/throwing-function)项目值得一试。