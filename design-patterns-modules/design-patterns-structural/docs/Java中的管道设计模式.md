## 一、概述

[在本教程中，我们将回顾一个有趣的模式，它不是经典GoF](https://en.wikipedia.org/wiki/Design_Patterns)模式的一部分——管道模式。

它功能强大，可以帮助解决棘手的问题并改进应用程序的设计。此外，Java 有一些内置的解决方案来帮助实现这种模式；我们将在最后讨论它们。

## 2.相关模式

通常，管道模式被比作[责任链](https://www.baeldung.com/chain-of-responsibility-pattern)。[Pipeline 与Decorator](https://www.baeldung.com/java-decorator-pattern)也有很多共同点。在某些方面，它更接近装饰者而不是责任链。让我们回顾一下这些模式之间的异同。

### 2.1. 责任链

**管道和责任链经常被比较，因为这两种模式都明确声明了一个循序渐进的过程。**Pipeline 和 Chain of Responsibility 之间的第一个区别是后者通常没有*handleRequest()*方法的返回值：

[![img](https://www.baeldung.com/wp-content/uploads/2023/03/ConcreteHandlerA-e1677430144625-1024x409.png)](https://www.baeldung.com/wp-content/uploads/2023/03/ConcreteHandlerA.png)

然而，没有什么能阻止我们从*handleRequest()*方法返回值。在这种情况下，它将被定义为*Handler*接口的一部分。

### 2.2. 装潢师

Decorator 并没有立即提出与管道模式的相似之处，因为它没有明确说明其链状结构。**但是，通过委托和递归嵌套，其行为与责任链或管道非常相似：**

[![img](https://www.baeldung.com/wp-content/uploads/2023/03/decorator-e1677430779799-1024x450.png)](https://www.baeldung.com/wp-content/uploads/2023/03/decorator.png)

在经典 (GoF) 实现中，此模式添加了行为并且没有操作的返回值。但是，这是更改对象状态或使用不同组件处理数据的[合理选择。](https://www.baeldung.com/java-decorator-pattern#decorator-pattern-example)**通常，状态改变解决方案可能过于复杂，因为我们可以使用更直接的结构来实现结果。**同时，Decorator 提供了时间依赖性的管理并维护了执行的顺序。

## 3.流水线

管道模式背后的主要思想是创建一组操作（管道）并通过它传递数据。虽然责任链和装饰者可以部分处理这个任务。**Pipeline 的主要功能在于它对其结果的类型具有灵活性。**

责任链和装饰器仅返回分别在处理程序和组件接口中定义的类型。另一方面，管道可以处理任何类型的输入和输出。这种模式的灵活性是它的主要特点。

### 3.1. 不可变管道

让我们为不可变管道创建一个简单示例。*我们将从Pipe*接口开始：

```java
public interface Pipe<IN, OUT> {
    OUT process(IN input);
}复制
```

这是一个非常简单的接口，只有一种方法，它接受输入并产生输出。**接口是参数化的，我们可以在其中提供任何实现。**另外，请注意本文中的示例将与[类型参数的官方命名约定不同。](https://docs.oracle.com/javase/tutorial/java/generics/types.html)这是为了更好地区分方法级别和类级别的参数。现在让我们创建一个将管道保存在管道中的类：

```java
public class Pipeline<IN, OUT> {

    private Collection<Pipe<?, ?>> pipes;

    private Pipeline(Pipe<IN, OUT> pipe) {
        pipes = Collections.singletonList(pipe);
    }

    private Pipeline(Collection<Pipe<?, ?>> pipes) {
        this.pipes = new ArrayList<>(pipes);
    }

    public static <IN, OUT> Pipeline<IN, OUT> of(Pipe<IN, OUT> pipe) {
        return new Pipeline<>(pipe);
    }

    public <NEW_OUT> Pipeline<IN, NEW_OUT> withNextPipe(Pipe<OUT, NEW_OUT> pipe) {
        final ArrayList<Pipe<?, ?>> newPipes = new ArrayList<>(pipes);
        newPipes.add(pipe);
        return new Pipeline<>(newPipes);
    }

    public OUT process(IN input) {
        Object output = input;
        for (final Pipe pipe : pipes) {
            output = pipe.process(input);
        }
        return (OUT) output;
    }
}复制
```

构造函数和静态工厂非常简单，所以让我们专注于*withNextPipe*方法：

```java
public <NEW_OUT> Pipeline<IN, NEW_OUT> withNextPipe(Pipe<OUT, NEW_OUT> pipe) {
    final ArrayList<Pipe<?, ?>> newPipes = new ArrayList<>(pipes);
    newPipes.add(pipe);
    return new Pipeline<>(newPipes);
}复制
```

因为我们需要一定程度的类型安全并且不允许管道失败，所以我们需要存储有关当前输入和输出类型的信息。**此信息存储在\*管道\* 对象中。但是，在添加新\*Pipe 时，\*我们需要更新此信息，而我们不能对同一个对象执行此操作。**这就是为什么决定让*Pipeline*不可变并且添加一个新的*Pipe*将产生一个新的单独的 *Pipeline。*

*Pipeline*的流程部分非常简单：

```java
public OUT process(IN input) {
    Object output = input;
    for (final Pipe pipe : pipes) {
        output = pipe.process(output);
    }
    return (OUT) output;
}复制
```

但是，在这种情况下我们需要使用原始类型。我们确保 Pipes 正确通过，所以应该没有问题。最终，我们必须将结果转换为预期的类型。

### 3.2. 简单管道

我们可以稍微简化上面的示例并完全摆脱 *Pipeline*类：

```java
public interface Pipe<IN, OUT> {
    OUT process(IN input);

    default <NEW_OUT> Pipe<IN, NEW_OUT> add(Pipe <OUT, NEW_OUT> pipe) {
        return input -> pipe.execute(execute(input));
    }
}
复制
```

此实现更接近于之前讨论的模式（装饰器和责任链），因为它具有从一个管道委托到另一个管道的递归结构。**但是，在此实现中，所有管道都隐藏在方法调用中，因此很难获取整个管道。**同时，与之前使用*Pipeline 的实现相比，该方案非常简单和灵活。*

### 3.3. 功能解决方案

我们可以迭代以前的解决方案并使用 vanilla Java 改进它。让我们再次看一下*Pipe 接口：*

```java
public interface Pipe<IN, OUT> {
    OUT process(IN input);

    default <NEW_OUT> Pipe<IN, NEW_OUT> add(Pipe <OUT, NEW_OUT> pipe) {
        return input -> pipe.execute(execute(input));
    }
}
复制
```

这是一个具有一种默认方法的功能接口。我们可以用一个已经存在的*Function*接口来代替它：

```java
public interface Function<T, R> {
    //...
    R apply(T t);
    //...
}复制
```

此外， *Function*接口包含几个有用的方法，其中之一是*andThen：*

```java
default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
    Objects.requireNonNull(after);
    return (T t) -> after.apply(apply(t));
}复制
```

我们可以使用它来代替我们之前的*add*方法。此外，*Function*接口提供了一种在管道开头添加函数的方法：

```java
default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
    Objects.requireNonNull(before);
    return (V v) -> apply(before.apply(v));
}复制
```

通过使用*Function*，我们可以创建非常灵活且易于使用的管道：

```java
@Test
void whenCombiningThreeFunctions_andInitializingPipeline_thenResultIsCorrect() {
    Function<Integer, Integer> square = s -> s * s;
    Function<Integer, Integer> half = s -> s / 2;
    Function<Integer, String> toString = Object::toString;
    Function<Integer, String> pipeline = square.andThen(half)
        .andThen(toString);
    String result = pipeline.apply(5);
    String expected = "12";
    assertEquals(expected, result);
}复制
```

管道直接*获取*参数，使这种方法非常干净。作为奖励，我们可以使用*BiFunctions*扩展管道：

```java
@Test
void whenCombiningFunctionAndBiFunctions_andInitializingPipeline_thenResultIsCorrect() {
    BiFunction<Integer, Integer, Integer> add = Integer::sum;
    BiFunction<Integer, Integer, Integer> mul = (a, b) -> a * b;
    Function<Integer, String> toString = Object::toString;
    BiFunction<Integer, Integer, String> pipeline = add.andThen(a -> mul.apply(a, 2))
        .andThen(toString);
    String result = pipeline.apply(1, 2);
    String expected = "6";
    assertEquals(expected, result);
}复制
```

**因为\*andThen\*方法采用 \*Function，所以\*我们必须使用[柯里化将](https://en.wikipedia.org/wiki/Currying)\*mul\* \*BiFunction\*转换 为函数。**尽管我们在函数内部而不是在调用管道时提供参数，但该解决方案仍然简单明了。Stream API 中使用了相同的方法，流中的操作序列称为管道。

## 4。结论

在本文中，我们将流水线模式作为一种强大的工具进行了讨论，但它并不流行，也未包含在已知模式的经典 (GoF) 列表中。

**我们可以通过多种方式实现这种模式，但 Java 也提供了一个很好的选择，可以通过 Stream API 来利用它。**在大多数情况下，Java 提供的解决方案就足够了。在特定管道的情况下，可以从头开始实施它们。

这种模式的主要好处是它允许简化逻辑并使代码更易于维护，同时简洁明了。