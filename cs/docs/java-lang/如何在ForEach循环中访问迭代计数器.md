## 1. 概述

在Java中迭代数据时，我们可能希望访问当前项及其在数据源中的位置。

这在经典的 for循环中很容易实现， 位置通常是循环计算的重点，但是当我们使用 for each 循环或流等构造时，它需要做更多的工作。

在这个简短的教程中，我们将了解几种可以为每个操作包含一个计数器的方法。

## 2. 实现计数器

让我们从一个简单的例子开始。我们将采用有序的电影列表并输出它们及其排名。

```java
List<String> IMDB_TOP_MOVIES = Arrays.asList("The Shawshank Redemption",
  "The Godfather", "The Godfather II", "The Dark Knight");
```

### 2.1. for循环

[for循环](https://www.baeldung.com/java-for-loop)使用计数器来引用当前项，因此这是一种对列表中的数据及其索引进行操作的简单方法：

```java
List rankings = new ArrayList<>();
for (int i = 0; i < movies.size(); i++) {
    String ranking = (i + 1) + ": " + movies.get(i);
    rankings.add(ranking);
}
```

由于这个List可能是一个 ArrayList，所以 get操作是高效的，上面的代码是我们问题的简单解决方案。

```java
assertThat(getRankingsWithForLoop(IMDB_TOP_MOVIES))
  .containsExactly("1: The Shawshank Redemption",
      "2: The Godfather", "3: The Godfather II", "4: The Dark Knight");
```

但是，并非Java中的所有数据源都可以通过这种方式进行迭代。有时候 get是一个耗时的操作，或者我们只能使用Stream或 Iterable 来处理数据源的下一个元素。

### 2.2. 对于每个循环

我们将继续使用我们的电影列表，但假设我们只能使用Java对每个结构进行迭代：

```java
for (String movie : IMDB_TOP_MOVIES) {
   // use movie value
}
```

这里我们需要使用一个单独的变量来跟踪当前索引。我们可以在循环外构造它，并在循环内递增它：

```java
int i = 0;
for (String movie : movies) {
    String ranking = (i + 1) + ": " + movie;
    rankings.add(ranking);

    i++;
}
```

我们应该注意，我们必须在循环中使用计数器后递增计数器。

## 3.每个函数

每次我们需要时都编写计数器扩展可能会导致代码重复，并且可能会冒关于何时更新计数器变量的意外错误的风险。因此，我们可以使用Java的[函数式接口](https://www.baeldung.com/java-8-functional-interfaces)来概括上述内容。

首先，我们应该将循环内的行为视为集合中项目和索引的消费者。这可以使用BiConsumer建模，它定义了一个 接受两个参数的函数

```java
@FunctionalInterface
public interface BiConsumer<T, U> {
   void accept(T t, U u);
}
```

由于我们的循环内部是使用两个值的东西，我们可以编写一个通用的循环操作。它可以采用源数据的 Iterable，for each 循环将在其上运行，以及 用于对每个项目及其索引执行的操作的BiConsumer 。我们可以使用类型参数T使其泛化 ：

```java
static <T> void forEachWithCounter(Iterable<T> source, BiConsumer<Integer, T> consumer) {
    int i = 0;
    for (T item : source) {
        consumer.accept(i, item);
        i++;
    }
}
```

我们可以通过将 BiConsumer的实现作为 lambda 表达式来将其用于我们的电影排名示例：

```java
List rankings = new ArrayList<>();
forEachWithCounter(movies, (i, movie) -> {
    String ranking = (i + 1) + ": " + movies.get(i);
    rankings.add(ranking);
});
```

## 4.用Stream为forEach添加一个计数器

[Java Stream](https://www.baeldung.com/java-8-streams-introduction) API 允许我们表达我们的数据如何通过过滤器和转换。它还提供了一个 forEach函数。让我们尝试将其转换为包含计数器的操作。

Stream forEach函数需要一个 Consumer 来处理下 一个项目。但是，我们可以创建Consumer来跟踪计数器并将项目传递给BiConsumer：

```java
public static <T> Consumer<T> withCounter(BiConsumer<Integer, T> consumer) {
    AtomicInteger counter = new AtomicInteger(0);
    return item -> consumer.accept(counter.getAndIncrement(), item);
}
```

此函数返回一个新的 lambda。该 lambda 使用 AtomicInteger对象在迭代期间跟踪计数器。每次有新项目时都会调用getAndIncrement函数。

此函数创建的 lambda 委托给传入的BiConsumer，以便算法可以处理项目及其索引。

让我们看看我们的电影排名示例对名为movies的Stream的使用：

```java
List rankings = new ArrayList<>();
movies.forEach(withCounter((i, movie) -> {
    String ranking = (i + 1) + ": " + movie;
    rankings.add(ranking);
}));
```

forEach内部 是对withCounter函数的调用， 以创建一个对象，该对象既跟踪计数又充当 forEach操作也传递其值的 消费者。

## 5.总结

在这篇简短的文章中，我们研究了三种为每个操作将计数器附加到Java的方法。

我们看到了如何在循环的每个实现中跟踪当前项目的索引。然后我们研究了如何推广这种模式以及如何将其添加到流式操作中。