## 1. 简介

本教程是对Java 8中存在的不同函数式接口的指南，以及它们的一般用例和在标准JDK库中的用法。

## 2. Java 8中的Lambda

Java 8以lambda表达式的形式带来了强大的新语法改进。lambda是一个匿名函数，我们可以将其作为一等语言公民来处理。例如，我们可以将它传递给方法或从方法返回它。

在Java 8之前，我们通常会为每种需要封装单个功能的情况创建一个类，这意味着有很多不必要的样板代码来定义一些作为原始函数表示的东西。

[“Lambda表达式和函数式接口：技巧和最佳实践”]()一文更详细地描述了使用lambda的函数式接口和最佳实践。本指南重点介绍java.util.function包中存在的一些特定函数式接口。

## 3. 函数式接口

建议所有函数式接口都有一个信息丰富的@FunctionalInterface注解。这清楚地传达了接口的用途，并且还允许编译器在带注解的接口不满足条件时生成错误。

任何带有SAM(Single Abstract Method)的接口都是函数式接口，它的实现可以被视为lambda表达式。

请注意，Java 8的默认方法不是抽象的，也不算数；函数式接口可能仍然有多个默认方法。我们可以通过查看函数的[文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/Function.html)来观察这一点。

## 4. Function

lambda最简单和一般的情况是函数式接口，其方法接收一个值并返回另一个值。单个参数的函数由Function接口表示，该接口由其参数类型和返回值类型参数化：

```java
public interface Function<T, R> { ... }
```

标准库中Function类型的用法之一是Map.computeIfAbsent方法。此方法通过键从Map返回一个值，但如果键不存在于Map中，则计算一个值。为了计算一个值，它使用传递的 Function实现：

```java
Map<String, Integer> nameMap = new HashMap<>();
Integer value = nameMap.computeIfAbsent("John", s -> s.length());
```

在这种情况下，我们通过将Function应用于键来计算值，将其放入Map中，并从方法调用中返回。**我们可以将lambda替换为与传递值和返回值类型匹配的方法引用**。

请记住，我们调用方法的对象实际上是方法的隐式第一个参数。这允许我们将实例方法length引用强制转换为Function接口：

```java
Integer value = nameMap.computeIfAbsent("John", String::length);
```

Function接口还有一个默认的compose方法，它允许我们将几个函数组合成一个并顺序执行它们：

```java
Function<Integer, String> intToString = Object::toString;
Function<String, String> quote = s -> "'" + s + "'";

Function<Integer, String> quoteIntToString = quote.compose(intToString);

assertEquals("'5'", quoteIntToString.apply(5));
```

quoteIntToString函数是将quote函数应用于intToString函数的结果的组合。

## 5. 原始类型函数特化

由于原始类型不能是泛型类型参数，因此对于最常用的原始类型double、int、long以及它们在参数和返回类型中的组合，存在Function接口的版本：

-   IntFunction、LongFunction、DoubleFunction：参数是指定类型，返回类型是参数化的
-   ToIntFunction、ToLongFunction、ToDoubleFunction：返回类型为指定类型，参数被参数化
-   DoubleToIntFunction、DoubleToLongFunction、IntToDoubleFunction、IntToLongFunction、LongToIntFunction、LongToDoubleFunction：将参数和返回类型都定义为原始类型，由它们的名称指定

举个例子，对于接收short并返回byte的函数，没有开箱即用的函数式接口，但我们当然可以编写自己的函数接口：

```java
@FunctionalInterface
public interface ShortToByteFunction {

    byte applyAsByte(short s);
}
```

现在我们可以编写一个方法，使用ShortToByteFunction定义的规则将short数组转换为byte数组：

```java
public byte[] transformArray(short[] array, ShortToByteFunction function) {
    byte[] transformedArray = new byte[array.length];
    for (int i = 0; i < array.length; i++) {
        transformedArray[i] = function.applyAsByte(array[i]);
    }
    return transformedArray;
}
```

以下是我们如何使用它将short数组转换为乘以2的字节数组：

```java
short[] array = {(short) 1, (short) 2, (short) 3};
byte[] transformedArray = transformArray(array, s -> (byte) (s * 2));

byte[] expectedArray = {(byte) 2, (byte) 4, (byte) 6};
assertArrayEquals(expectedArray, transformedArray);
```

## 6. 二元函数特化

要定义带有两个参数的lambda，我们必须使用名称中包含“Bi”关键字的其他接口：BiFunction、ToDoubleBiFunction、ToIntBiFunction和ToLongBiFunction。BiFunction具有参数和返回类型泛化，而ToDoubleBiFunction和其他函数允许我们返回原始类型值。

在标准API中使用此接口的典型示例之一是在Map.replaceAll方法中，它允许用某个计算值替换Map中的所有值。

下面我们使用接收键和旧值的BiFunction实现来计算薪水的新值并将其返回。

```java
Map<String, Integer> salaries = new HashMap<>();
salaries.put("John", 40000);
salaries.put("Freddy", 30000);
salaries.put("Samuel", 50000);

salaries.replaceAll((name, oldValue) -> 
  name.equals("Freddy") ? oldValue : oldValue + 10000);
```

## 7. Supplier

Supplier函数式接口是另一个不带任何参数的函数特化，我们通常使用它来延迟生成值。例如，让我们定义一个对double值求平方的函数。它本身不会接收值，而是接收此值的Supplier：

```java
public double squareLazy(Supplier<Double> lazyValue) {
    return Math.pow(lazyValue.get(), 2);
}
```

这允许我们使用Supplier实现延迟生成调用此函数的参数，如果参数的生成需要相当长的时间，这会很有用。我们将使用Guava的sleepUninterruptibly方法进行模拟：

```java
Supplier<Double> lazyValue = () -> {
    Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
    return 9d;
};

Double valueSquared = squareLazy(lazyValue);
```

Supplier的另一个用例是定义序列生成的逻辑。为了演示它，让我们使用静态Stream.generate方法来创建斐波那契数流：

```java
int[] fibs = {0, 1};
Stream<Integer> fibonacci = Stream.generate(() -> {
    int result = fibs[1];
    int fib3 = fibs[0] + fibs[1];
    fibs[0] = fibs[1];
    fibs[1] = fib3;
    return result;
});
```

我们传递给Stream.generate方法的函数实现了Supplier函数式接口。请注意，要用作生成器，Supplier通常需要某种外部状态。在这种情况下，它的状态包括最后两个斐波那契数列。

**为了实现这种状态，我们使用一个数组而不是几个变量，因为lambda内部使用的所有外部变量都必须有效地为final**。

Supplier函数式接口的其他特化包括BooleanSupplier、DoubleSupplier、LongSupplier和IntSupplier，它们的返回类型是相应的原始类型。

## 8. Consumer

与Supplier相反，Consumer接收一个泛型的参数并且不返回任何内容，它是一个表示副作用的函数。

例如，我们通过在控制台中打印问候语来问候names集合中的每个人，传递给List.forEach方法的lambda实现了Consumer函数式接口：

```java
List<String> names = Arrays.asList("John", "Freddy", "Samuel");
names.forEach(name -> System.out.println("Hello, " + name));
```

还有专门版本的消费者：DoubleConsumer、IntConsumer和LongConsumer，接收原始类型值作为参数。更有趣的是BiConsumer接口，它的一个用例是遍历Map的条目：

```java
Map<String, Integer> ages = new HashMap<>();
ages.put("John", 25);
ages.put("Freddy", 24);
ages.put("Samuel", 30);

ages.forEach((name, age) -> System.out.println(name + " is " + age + " years old"));
```

另一组专门的BiConsumer版本由ObjDoubleConsumer、ObjIntConsumer和ObjLongConsumer组成，它们接收两个参数；其中一个参数是泛型的，另一个是原始类型。

## 9. Predicate

在数理逻辑中，谓词是一个接收值并返回布尔值的函数。

Predicate函数式接口是Function的特化，它接收泛型值并返回布尔值。Predicate lambda的一个典型用例是过滤一组值：

```java
List<String> names = Arrays.asList("Angela", "Aaron", "Bob", "Claire", "David");

List<String> namesWithA = names.stream()
  .filter(name -> name.startsWith("A"))
  .collect(Collectors.toList());
```

在上面的代码中，我们使用Stream API过滤names集合并仅保留以字母“A”开头的名称，Predicate实现封装了过滤逻辑。

与前面的所有示例一样，此函数有接收原始类型值的IntPredicate、DoublePredicate和LongPredicate版本。

## 10. Operators

Operators接口是接收和返回相同值类型的函数的特例。UnaryOperator接口接收单个参数。它在Collections API中的一个用例是用一些相同类型的计算值替换集合中的所有值：

```java
List<String> names = Arrays.asList("bob", "josh", "megan");

names.replaceAll(name -> name.toUpperCase());
```

List.replaceAll函数返回void，因为它替换了适当的值。为了满足这个目的，用于转换集合值的lambda必须返回与其接收到的结果类型相同的结果类型，这就是为什么UnaryOperator在这里很有用的原因。

当然，我们可以简单地使用方法引用来代替name -> name.toUpperCase()：

```java
names.replaceAll(String::toUpperCase);
```

BinaryOperator最有趣的用例之一是归约运算。假设我们想要聚合整数集合values中所有值的总和，使用Stream API，我们可以使用收集器来完成此操作，但更通用的方法是使用reduce方法：

```java
List<Integer> values = Arrays.asList(3, 5, 8, 9, 12);

int sum = values.stream()
  .reduce(0, (i1, i2) -> i1 + i2);
```

reduce方法接收初始累加器值和BinaryOperator函数，该函数的参数是一对相同类型的值；该函数本身还包含将它们连接到同一类型的单个值的逻辑。传递的函数必须是关联的，这意味着值聚合的顺序无关紧要，即以下条件应成立：

```java
op.apply(a, op.apply(b, c)) == op.apply(op.apply(a, b), c)
```

BinaryOperator运算符函数的关联属性使我们能够轻松并行化归约过程。

当然，还有可以与原始类型值一起使用的UnaryOperator和BinaryOperator的特化，即DoubleUnaryOperator、IntUnaryOperator、LongUnaryOperator、DoubleBinaryOperator、IntBinaryOperator和LongBinaryOperator。

## 11. 遗留函数式接口

并非所有函数式接口都出现在Java 8中，Java早期版本中的许多接口都符合@FunctionalInterface的约束，我们也可以将它们用作lambda。其中最突出的例子包括在并发API中使用的Runnable和Callable接口。在Java 8中，这些接口也标有@FunctionalInterface注解，这使我们能够大大简化并发代码：

```java
Thread thread = new Thread(() -> System.out.println("Hello From Another Thread"));
thread.start();
```

## 12. 总结

在本文中，我们介绍了Java 8 API中存在的不同函数式接口，我们可以将它们用作lambda表达式。