## 1. 概述

在本快速教程中，我们将学习如何使用核心Java类生成不重复的随机数。首先，我们将从头开始实施几个解决方案，然后利用Java8+ 的特性获得更具扩展性的方法。

## 2. 小范围内的随机数

如果我们需要的数字范围很小，我们可以继续向列表中添加连续数字，直到达到大小n。然后，我们调用[Collections.shuffle()](https://www.baeldung.com/java-shuffle-collection)，它具有线性[时间复杂度](https://www.baeldung.com/java-algorithm-complexity)。之后，我们将得到一个随机的唯一数字列表。让我们创建一个实用程序类来生成和使用这些数字：

```java
public class UniqueRng implements Iterator<Integer> {
    private List<Integer> numbers = new ArrayList<>();

    public UniqueRng(int n) {
        for (int i = 1; i <= n; i++) {
            numbers.add(i);
        }

        Collections.shuffle(numbers);
    }
}
```

在构建我们的对象之后，我们将以 随机顺序从 1 到size的数字。请注意，我们正在实现[Iterator](https://www.baeldung.com/java-iterator-vs-iterable)，因此每次调用next()时我们都会得到一个随机数。另外，我们可以用hasNext()检查是否还有数字。所以，让我们[覆盖](https://www.baeldung.com/java-override)它们：

```java
@Override
public Integer next() {
    if (!hasNext()) {
        throw new NoSuchElementException();
    }
    return numbers.remove(0);
}

@Override
public boolean hasNext() {
    return !numbers.isEmpty();
}
```

因此，remove()返回列表中第一个删除的项目。同样，如果我们没有洗牌我们的集合，我们可以传递一个随机索引。但是，在构造时改组的好处是让我们提前知道整个序列。

### 2.1. 投入使用

要使用它，我们只需选择我们想要的数字并使用它们：

```java
UniqueRng rng = new UniqueRng(5);
while (rng.hasNext()) {
    System.out.print(rng.next() + " ");
}
```

这可能导致如下输出：

```apache
4 1 2 5 3
```

## 3. 大范围的随机数

如果我们想要更广泛的数字范围，我们需要一个不同的策略，只使用其中的几个。首先，我们不能依赖于向ArrayList添加随机数， 因为那样会产生重复项。所以，我们将使用[Set](https://www.baeldung.com/java-set-operations)，因为它保证了唯一的项目。然后，我们将使用LinkedHashSet实现，因为它维护插入顺序。

这一次，我们将在一个循环中向我们的集合中添加元素，直到我们达到size。此外，我们将使用[Random](https://www.baeldung.com/java-generating-random-numbers-in-range)生成从零到最大值的随机整数：

```java
public class BigUniqueRng implements Iterator<Integer> {
    private Random random = new Random();
    private Set<Integer> generated = new LinkedHashSet<>();

    public BigUniqueRng(int size, int max) {
        while (generated.size() < size) {
            Integer next = random.nextInt(max);
            generated.add(next);
        }
    }
}
```

请注意，我们不需要检查我们的集合中是否已经存在一个数字，因为add()会这样做。现在，由于我们不能通过索引删除项目，我们需要Iterator的帮助来实现next()：

```java
public Integer next() {
    Iterator<Integer> iterator = generated.iterator();
    Integer next = iterator.next();
    iterator.remove();
    return next;
}
```

## 4. 利用Java8+ 的特性

[虽然自定义实现更具可重用性，但我们可以仅使用Stream](https://www.baeldung.com/java-streams)来创建解决方案[。](https://www.baeldung.com/java-streams)从Java8 开始，Random有一个返回[IntStream](https://www.baeldung.com/java-intstream-convert)的ints()方法。我们可以对其进行流式处理并施加与之前相同的必要条件，例如范围和限制。让我们结合这些特征并将结果[收集](https://www.baeldung.com/java-stream-immutable-collection)到一个Set中：

```java
Set<Integer> set = new Random().ints(-5, 15)
  .distinct()
  .limit(5)
  .boxed()
  .collect(Collectors.toSet());
```

遍历的集合可以产生如下输出：

```diff
-5 13 9 -4 14
```

使用ints()，从负整数开始的范围更简单。但是，我们必须小心不要以无限流结束，例如，如果我们不调用limit()就会发生这种情况。

## 5.总结

在本文中，我们编写了几个解决方案来在两种情况下生成不重复的随机数。首先，我们使这些类可迭代，以便我们可以轻松地使用它们。然后，我们使用流创建了一个更有机的解决方案。