## 1. 概述

在本综合教程中，我们将介绍Java 8 Streams从创建到并行执行的实际应用。

要理解本文章，读者需要具备Java 8(lambda表达式、Optional、方法引用)和Stream API的基本知识。为了更加熟悉这些主题，请看一下我们之前的文章：[Java 8的新特性](https://www.baeldung.com/java-8-new-features)和[Java 8 Streams简介](https://www.baeldung.com/java-8-streams-introduction)。

## 2. 创建流

有很多方法可以创建不同来源的流实例。一旦创建，**实例将不会修改其源**，因此允许从单个源创建多个实例。

### 2.1 空流

在创建空流的情况下，我们应该使用**empty()**方法：

```java
Stream<String> streamEmpty = Stream.empty();
```

我们经常在创建时使用empty()方法来避免为没有元素的流返回null：

```java
public Stream<String> streamOf(List<String> list) {
    return list == null || list.isEmpty() ? Stream.empty() : list.stream();
}
```

### 2.2 收集流

我们还可以创建任何类型的集合流(Collection、List、Set)：

```java
Collection<String> collection = Arrays.asList("a", "b", "c");
Stream<String> streamOfCollection = collection.stream();
```

### 2.3 数组流

数组也可以是流的来源：

```java
Stream<String> streamOfArray = Stream.of("a", "b", "c");
```

我们还可以从现有数组或数组的一部分创建流：

```java
String[] arr = new String[]{"a", "b", "c"};
Stream<String> streamOfArrayFull = Arrays.stream(arr);
Stream<String> streamOfArrayPart = Arrays.stream(arr, 1, 3);
```

### 2.4 Stream.builder()

**使用构建器时，需要在语句的右侧额外指定所需的类型**，否则build()方法将创建Stream<Object\>的实例：

```java
Stream<String> streamBuilder = Stream.<String>builder().add("a").add("b").add("c").build();
```

### 2.5 Stream.generate()

**generate()**方法接收Supplier<T\>来生成元素。由于生成的流是无限的，开发人员应指定所需的大小，否则generate()方法将一直工作直到达到内存限制：

```java
Stream<String> streamGenerated = Stream.generate(() -> "element").limit(10);
```

上面的代码创建了一个包含10个字符串的序列，其值为“element”。

### 2.6 Stream.iterate()

另一种创建无限流的方法是使用**iterate()**方法：

```java
Stream<Integer> streamIterated = Stream.iterate(40, n -> n + 2).limit(20);
```

结果流的第一个元素是iterate()方法的第一个参数。创建每个后续元素时，指定的函数将应用于前一个元素。在上面的示例中，第二个元素将为42。

### 2.7 原始类型流

Java 8提供了从三种原始类型创建流的可能性：int、long和double。由于Stream<T\>是一个泛型接口，并且无法使用基本类型作为泛型的类型参数，因此创建了三个新的特殊接口：**IntStream、LongStream、DoubleStream**。

使用新接口减少了不必要的自动装箱，从而提高了工作效率：

```java
IntStream intStream = IntStream.range(1, 3);
LongStream longStream = LongStream.rangeClosed(1, 3);
```

**range(int startInclusive, int endExclusive)**方法创建一个从第一个参数到第二个参数的有序流。它以等于1的步长递增后续元素的值。结果不包括最后一个参数，它只是序列的上限。

**rangeClosed(int startInclusive, int endInclusive)**方法做同样的事情，只有一个区别，即包括第二个元素。我们可以使用这两种方法来生成三种类型的原始类型流中的任何一种。

从Java 8开始，[Random](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Random.html)类提供了广泛的方法来生成原始类型流。例如，以下代码创建了一个DoubleStream，它具有三个元素：

```java
Random random = new Random();
DoubleStream doubleStream = random.doubles(3);
```

### 2.8 字符串流

在String类的chars()方法的帮助下，我们还可以使用String作为创建流的源。由于JDK中没有CharStream的接口，我们使用IntStream来表示一个字符流。

```java
IntStream streamOfChars = "abc".chars();
```

以下示例根据指定的RegEx将String分解成子字符串：

```java
Stream<String> streamOfString = Pattern.compile(", ").splitAsStream("a, b, c");
```

### 2.9 文件流

此外，Java NIO类Files允许我们通过lines()方法生成文本文件的Stream<String\>。文本的每一行都成为流的一个元素：

```java
Path path = Paths.get("C:\\file.txt");
Stream<String> streamOfStrings = Files.lines(path);
Stream<String> streamWithCharset = Files.lines(path, Charset.forName("UTF-8"));
```

Charset可以指定为lines()方法的参数。

## 3. 引用流

我们可以实例化一个流，并有一个可访问的引用，只要只调用中间操作。执行终端操作会使流不可访问。

为了证明这一点，我们将暂时忘记最佳实践是链接操作顺序。除了不必要的冗长之外，从技术上讲，以下代码是有效的：

```java
Stream<String> stream = Stream.of("a", "b", "c").filter(element -> element.contains("b"));
Optional<String> anyElement = stream.findAny();
```

但是，在调用终端操作后尝试重用相同的引用将触发IllegalStateException：

```java
Optional<String> firstElement = stream.findFirst();
```

由于IllegalStateException是一个RuntimeException，编译器不会发出有关问题的信号。所以记住**Java 8流不能被重用**是非常重要的。

这种行为是合乎逻辑的。我们将流设计为以函数式风格将有限的操作序列应用于元素源，而不是存储元素。

因此，为了使前面的代码正常工作，应该做一些改动：

```java
List<String> elements =
    Stream.of("a", "b", "c").filter(element -> element.contains("b"))
        .collect(Collectors.toList());
Optional<String> anyElement = elements.stream().findAny();
Optional<String> firstElement = elements.stream().findFirst();
```

## 4. 流管道

要对数据源的元素执行一系列操作并聚合其结果，我们需要三个部分：**源、中间操作和终端操作**。

中间操作返回一个新的修改流。例如，要创建一个现有流的部分元素的新流，应该使用skip()方法：

```java
Stream<String> onceModifiedStream = Stream.of("abcd", "bbcd", "cbcd").skip(1);
```

如果我们需要多个修改，我们可以链接中间操作。假设我们还需要用前几个字符的子字符串替换当前Stream<String\>的每个元素。我们可以通过链接skip()和map()方法来做到这一点：

```java
Stream<String> twiceModifiedStream = stream.skip(1).map(element -> element.substring(0, 3));
```

如我们所见，map()方法将lambda表达式作为参数。如果我们想了解有关lambda的更多信息，可以查看我们的教程[Lambda表达式和函数式接口：技巧和最佳实践](https://www.baeldung.com/java-8-lambda-expressions-tips)。

流本身是没有价值的；用户对终端操作的结果感兴趣，该结果可以是某种类型的值或应用于流的每个元素的操作。**每个流我们只能使用一个终端操作**。

**使用流的正确和最方便的方法是通过流管道，它是流源、中间操作和终端操作的链**：

```java
List<String> list = Arrays.asList("abc1", "abc2", "abc3");
long size = list.stream().skip(1)
    .map(element -> element.substring(0, 3)).sorted().count();
```

## 5. 惰性调用

**中间操作是惰性的**。这意味着**只有在执行终端操作所必需时才会调用它们**。

例如，让我们调用wasCalled()方法，它会在每次调用时递增一个内部计数器：

```java
private long counter;
 
private void wasCalled() {
    counter++;
}
```

现在让我们从操作filter()调用方法wasCalled()：

```java
List<String> list = Arrays.asList(“abc1”, “abc2”, “abc3”);
counter = 0;
Stream<String> stream = list.stream().filter(element -> {
    wasCalled();
    return element.contains("2");
});
```

由于我们有三个元素的来源，我们可以假设filter()方法将被调用三次，并且counter变量的值将为3。但是，运行这段代码根本不会改变counter，它仍然为0，所以filter()方法甚至没有被调用一次。原因是缺失终端操作。

让我们通过添加一个map()操作和一个终端操作findFirst()来稍微重写这段代码。我们还将添加借助日志记录来跟踪方法调用顺序的功能：

```java
Optional<String> stream = list.stream().filter(element -> {
    log.info("filter() was called");
    return element.contains("2");
}).map(element -> {
    log.info("map() was called");
    return element.toUpperCase();
}).findFirst();
```

结果日志显示我们调用了filter()方法两次和map()方法一次。这是因为管道是垂直执行的。在我们的示例中，流的第一个元素不满足过滤器的谓词。然后我们为第二个元素调用了filter()方法，它通过了过滤器。没有为第三个元素调用filter()，我们通过管道向下到达map()方法。

findFirst()操作只满足一个元素。所以在这个特定的例子中，惰性调用让我们避免了两个方法调用，一个用于filter()，一个用于map()。

## 6. 执行顺序

从性能的角度来看，**正确的顺序是流管道中链接操作最重要的方面之一**：

```java
long size = list.stream().map(element -> {
    wasCalled();
    return element.substring(0, 3);
}).skip(2).count();
```

执行此代码会将counter的值增加3。这意味着我们调用了三次Stream的map()方法，但是size的值是1。因此生成的流只有一个元素，我们无缘无故地执行了三次中的两次昂贵的map()操作。

如果我们改变skip()和map()方法的顺序，counter只会增加一次。所以我们将只调用一次map()方法：

```java
long size = list.stream().skip(2).map(element -> {
    wasCalled();
    return element.substring(0, 3);
}).count();
```

这给我们带来了以下规则：**减少流大小的中间操作应该放在应用于每个元素的操作之前**。因此，我们需要将skip()、filter()和distinct()等方法保留在流管道的顶部。

## 7. 流归约

API有许多终端操作，它们将流聚合为一种类型或原始类型：count()、max()、min()和sum()。但是，这些操作根据预定义的实现工作。**那么如果开发者需要自定义一个Stream的归约机制怎么办**？有两种方法可以让我们做到这一点，**reduce()**和**collect()**方法。

### 7.1 reduce()方法

此方法有三种变体，它们的签名和返回类型不同。它们可以有以下参数：

**identity**–累加器的初始值，如果流为空且没有可累加的内容，则为默认值

**accumulator**-指定元素聚合逻辑的函数。由于累加器为归约的每一步创建一个新值，新值的数量等于流的大小，只有最后一个值有用。这对性能不是很好。

**combiner**-聚合累加器结果的函数。我们仅以并行方式调用组合器，以归约来自不同线程的累加器的结果。

现在让我们看看这三种方法的实际应用：

```java
OptionalInt reduced = IntStream.range(1, 4).reduce((a, b) -> a + b);
```

reduce = 6(1 + 2 + 3)

```java
int reducedTwoParams = IntStream.range(1, 4).reduce(10, (a, b) -> a + b);
```

reducedTwoParams = 16(10 + 1 + 2 + 3)

```java
int reducedParams = Stream.of(1, 2, 3)
    .reduce(10, (a, b) -> a + b, (a, b) -> {
        log.info("combiner was called");
        return a + b;
    });
```

结果将与前面的示例(16)相同，并且没有日志记录，这意味着未调用组合器。要使组合器工作，流应该是并行的：

```java
int reducedParallel = Arrays.asList(1, 2, 3).parallelStream()
    .reduce(10, (a, b) -> a + b, (a, b) -> {
        log.info("combiner was called");
        return a + b;
    });
```

这里的结果不同(36)，组合器被调用了两次。这里的归约通过以下算法进行：累加器通过将流的每个元素添加到identity来运行三次。这些行动是并行进行的。结果，他们有(10 + 1 = 11; 10 + 2 = 12; 10 + 3 = 13;)。现在组合器可以合并这三个结果。它需要两次迭代(12 + 13 = 25; 25 + 11 = 36)。

### 7.2 collect()方法

流的归约也可以通过另一个终端操作collect()方法来执行。它接收Collector类型的参数，该参数指定归约机制。已经为大多数常见操作创建了预定义的收集器。可以在Collectors类型的帮助下访问它们。

在本节中，我们将使用以下列表作为所有流的来源：

```java
List<Product> productList = Arrays.asList(new Product(23, "potatoes"),
    new Product(14, "orange"), new Product(13, "lemon"),
    new Product(23, "bread"), new Product(13, "sugar"));
```

**将流转换为集合(Collection、List或Set)**：

```java
List<String> collectorCollection = productList.stream().map(Product::getName).collect(Collectors.toList());
```

**归约到String**：

```java
String listToString = productList.stream().map(Product::getName)
    .collect(Collectors.joining(", ", "[", "]"));
```

joining()方法可以有一到三个参数(分隔符、前缀、后缀)。使用joining()最方便的一点是开发人员不需要检查流是否到达其末尾来应用后缀而不是应用分隔符。Collector会处理这个问题。

处理流的所有数字元素的平均值：

```java
double averagePrice = productList.stream()
    .collect(Collectors.averagingInt(Product::getPrice));
```

**处理流的所有数字元素的总和**：

```java
int summingPrice = productList.stream()
    .collect(Collectors.summingInt(Product::getPrice));
```

averagingXX()、summingXX()和summarizingXX()方法可以与原始类型(int、long、double)及其包装类(Integer、Long、Double)一起使用。这些方法的一个更强大的功能是提供映射。因此，开发人员不需要在collect()方法之前使用额外的map()操作。

**收集有关流元素的统计信息**：

```java
IntSummaryStatistics statistics = productList.stream()
    .collect(Collectors.summarizingInt(Product::getPrice));
```

通过使用IntSummaryStatistics类型的结果实例，开发人员可以通过应用toString()方法创建统计报告。结果将是此字符串“IntSummaryStatistics {count=5, sum=86, min=13, average=17,200000, max=23}”。

通过应用getCount()、getSum()、getMin()、getAverage()和getMax()方法，也可以很容易地从该对象中提取计数、总和、最小值和平均值的单独值。所有这些值都可以从单个管道中提取。

**根据指定的函数对流的元素进行分组**：

```java
Map<Integer, List<Product>> collectorMapOfLists = productList.stream()
    .collect(Collectors.groupingBy(Product::getPrice));
```

在上面的示例中，流被归约为Map，它按价格对所有产品进行分组。

**根据一些谓词将流的元素分成组**：

```java
Map<Boolean, List<Product>> mapPartioned = productList.stream()
    .collect(Collectors.partitioningBy(element -> element.getPrice() > 15));
```

**推动收集器执行额外的转换**：

```java
Set<Product> unmodifiableSet = productList.stream()
    .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
```

在这种特殊情况下，收集器已将流转换为Set，然后从中创建不可更改的Set。

**自定义收集器**：

如果由于某种原因需要创建自定义收集器，最简单且最不冗长的方法是使用Collector类型的方法of()。

```java
Collector<Product, ?, LinkedList<Product>> toLinkedList =
    Collector.of(LinkedList::new, LinkedList::add, 
        (first, second) -> { 
            first.addAll(second); 
            return first; 
        });

LinkedList<Product> linkedListOfPersons = productList.stream().collect(toLinkedList);
```

在此示例中，Collector的一个实例被归约为LinkedList<Persone\>。

## 8. 并行流

在Java 8之前，并行化很复杂。[ExecutorService](https://www.baeldung.com/java-executor-service-tutorial)和[ForkJoin](https://www.baeldung.com/java-fork-join)的出现稍微简化了开发人员的生活，但仍然值得记住如何创建特定的ExecutorService、如何运行它等等。Java 8引入了一种以函数式风格实现并行性的方法。

API允许我们创建并行流，这些流以并行模式执行操作。当流的来源是Collection或Array时，可以借助**parallelStream()**方法来实现：

```java
Stream<Product> streamOfCollection = productList.parallelStream();
boolean isParallel = streamOfCollection.isParallel();
boolean bigPrice = streamOfCollection
    .map(product -> product.getPrice() * 12)
    .anyMatch(price -> price > 200);
```

如果流的源不是Collection或Array，则应使用**parallel()**方法：

```java
IntStream intStreamParallel = IntStream.range(1, 150).parallel();
boolean isParallel = intStreamParallel.isParallel();
```

在底层，Stream API自动使用ForkJoin框架并行执行操作。默认情况下，将使用公共线程池，并且无法(至少目前)无法为其分配一些自定义线程池。这可以通过使用一组[自定义的并行收集器](https://github.com/pivovarit/parallel-collectors)来克服。

在并行模式下使用流时，请避免阻塞操作。当任务需要类似的时间来执行时，最好使用并行模式。如果一个任务比另一个任务持续的时间长得多，它会减慢整个应用程序的工作流程。

可以使用sequential()方法将并行模式的流转换回顺序模式：

```java
IntStream intStreamSequential = intStreamParallel.sequential();
boolean isParallel = intStreamSequential.isParallel();
```

## 9. 总结

Stream API是一组功能强大且易于理解的用于处理元素序列的工具。如果使用得当，它可以让我们减少大量的样板代码，创建更具可读性的程序，并提高应用程序的生产力。

在本文中显示的大多数代码示例中，我们没有使用流(我们没有应用close()方法或终端操作)。在真实的应用程序中，**不要让实例化的流未被使用，因为这会导致内存泄漏**。