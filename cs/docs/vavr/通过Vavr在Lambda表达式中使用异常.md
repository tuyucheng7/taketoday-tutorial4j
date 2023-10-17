## 1. 简介

JDK 提供的功能接口没有为处理已检查的异常做好准备。如果你想阅读有关该问题的更多信息，请查看[这篇文章](https://www.baeldung.com/java-lambda-exceptions)。

在本文中，我们将研究使用函数式Java库[Vavr](http://www.vavr.io/)来克服此类问题的各种方法。

要获取有关Vavr及其设置方法的更多信息，请查看[这篇文章](https://www.baeldung.com/vavr)。

## 2. 使用CheckedFunction

Vavr提供函数式接口，这些接口具有抛出已检查异常的函数。这些函数是CheckedFunction0、CheckedFunction1等等，直到CheckedFunction8。函数名末尾的0，1，...8表示函数的输入参数个数。

让我们看一个例子：

```java
static Integer readFromFile(Integer integer) throws IOException {
    // logic to read from file which throws IOException
}
```

我们可以在不处理IOException的情况下在lambda表达式中使用上述方法：

```java
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);

CheckedFunction1<Integer, Integer> readFunction = i -> readFromFile(i);
integers.stream()
    .map(readFunction.unchecked());
```

如你所见，没有标准的try-catch或包装方法，我们仍然可以在lambda表达式中调用异常抛出方法。

在将此功能与Stream API一起使用时，我们必须谨慎行事，因为异常会立即终止操作-放弃流的其余部分。

## 3. 使用辅助方法

API类为上一节的例子提供了快捷方式：

```java
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);

integers.stream()
  	.map(API.unchecked(i -> readFromFile(i)));
```

## 4. 使用lift

为了优雅地处理IOException，我们可以在lambda表达式中引入标准的try-catch块。但是，lambda表达式的简洁性将丢失。Vavr的解除拯救了我们。

提升是函数式编程的一个概念。你可以将部分函数提升为返回Option作为结果的总函数。

部分函数是仅针对域的子集定义的函数，而不是针对其整个域定义的总函数。如果使用超出其支持范围的输入调用部分函数，它通常会抛出异常。

让我们重写上一节中的示例：

```java
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
 
integers.stream()
    .map(CheckedFunction1.lift(i -> readFromFile(i)))
    .map(k -> k.getOrElse(-1));
```

请注意，提升函数的结果是Option，如果出现异常，结果将是Option.None。在Option.None的情况下，getOrElse()方法采用替代值返回。

## 5. 使用Try

上一节的lift()方法虽然解决了程序突然终止的问题，但它实际上吞噬了异常。因此，我们方法的使用者不知道是什么导致了默认值。替代方法是使用Try容器。

Try是一个特殊的容器，我们可以用它封装一个可能会抛出异常的操作。在这种情况下，生成的Try对象代表一个Failure并且它包装了异常。

让我们看一下使用Try的代码：

```java
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);

integers.stream()
    .map(CheckedFunction1.liftTry(i -> readFromFile(i)))
    .flatMap(Value::toJavaStream)
    .forEach(i -> processValidValue(i));
```

要了解有关Try容器及其使用方法的更多信息，请查看[这篇文章](https://www.baeldung.com/vavr-try)。

## 6. 总结

在这篇快速文章中，我们展示了如何使用Vavr库中的功能来规避处理lambda表达式中的异常时出现的问题。

尽管这些特性使我们能够优雅地处理异常，但应格外小心地使用它们。使用其中一些方法，你的方法的使用者可能会对意外的已检查异常感到惊讶，尽管它们没有明确声明。