## 1. 概述

这本食谱被组织成小而集中的食谱和代码片段，用于使用 Guava 函数式元素——谓词和函数。

食谱格式重点突出且实用——无需多余的细节和解释。

## 2.食谱

按条件过滤集合(自定义谓词)

```java
List<Integer> numbers = Lists.newArrayList(1, 2, 3, 6, 10, 34, 57, 89);
Predicate<Integer> acceptEven = new Predicate<Integer>() {
    @Override
    public boolean apply(Integer number) {
        return (number % 2) == 0;
    }
};
List<Integer> evenNumbers = Lists.newArrayList(Collections2.filter(numbers, acceptEven));
Integer found = Collections.binarySearch(evenNumbers, 57);
assertThat(found, lessThan(0));
```

从集合中过滤掉空值

```java
List<String> withNulls = Lists.newArrayList("a", "bc", null, "def");
Iterable<String> withoutNuls = Iterables.filter(withNulls, Predicates.notNull());
assertTrue(Iterables.all(withoutNuls, Predicates.notNull()));
```

检查集合中所有元素的条件

```java
List<Integer> evenNumbers = Lists.newArrayList(2, 6, 8, 10, 34, 90);
Predicate<Integer> acceptEven = new Predicate<Integer>() {
    @Override
    public boolean apply(Integer number) {
        return (number % 2) == 0;
    }
};
assertTrue(Iterables.all(evenNumbers, acceptEven));
```

否定谓词

```java
List<Integer> evenNumbers = Lists.newArrayList(2, 6, 8, 10, 34, 90);
Predicate<Integer> acceptOdd = new Predicate<Integer>() {
    @Override
    public boolean apply(Integer number) {
        return (number % 2) != 0;
    }
};
assertTrue(Iterables.all(evenNumbers, Predicates.not(acceptOdd)));
```

应用一个简单的函数

```java
List<Integer> numbers = Lists.newArrayList(1, 2, 3);
List<String> asStrings = Lists.transform(numbers, Functions.toStringFunction());
assertThat(asStrings, contains("1", "2", "3"));
```

通过首先应用中间函数对集合进行排序

```java
List<Integer> numbers = Arrays.asList(2, 1, 11, 100, 8, 14);
Ordering<Object> ordering = Ordering.natural().onResultOf(Functions.toStringFunction());
List<Integer> inAlphabeticalOrder = ordering.sortedCopy(numbers);
List<Integer> correctAlphabeticalOrder = Lists.newArrayList(1, 100, 11, 14, 2, 8);
assertThat(correctAlphabeticalOrder, equalTo(inAlphabeticalOrder));
```

复杂示例——链接谓词和函数

```java
List<Integer> numbers = Arrays.asList(2, 1, 11, 100, 8, 14);
Predicate<Integer> acceptEvenNumber = new Predicate<Integer>() {
    @Override
    public boolean apply(Integer number) {
        return (number % 2) == 0;
    }
};
Function<Integer, Integer> powerOfTwo = new Function<Integer, Integer>() {
    @Override
    public Integer apply(Integer input) {
        return (int) Math.pow(input, 2);
    }
};

FluentIterable<Integer> powerOfTwoOnlyForEvenNumbers = 
FluentIterable.from(numbers).filter(acceptEvenNumber).transform(powerOfTwo);
assertThat(powerOfTwoOnlyForEvenNumbers, contains(4, 10000, 64, 196));
```

组合两个函数

```java
List<Integer> numbers = Arrays.asList(2, 3);
Function<Integer, Integer> powerOfTwo = new Function<Integer, Integer>() {
    @Override
    public Integer apply(Integer input) {
        return (int) Math.pow(input, 2);
    }
};
List<Integer> result = Lists.transform(numbers, 
    Functions.compose(powerOfTwo, powerOfTwo));
assertThat(result, contains(16, 81));
```

创建一个由 Set 和 Function 支持的 Map

```java
Function<Integer, Integer> powerOfTwo = new Function<Integer, Integer>() {
    @Override
    public Integer apply(Integer input) {
        return (int) Math.pow(input, 2);
    }
};
Set<Integer> lowNumbers = Sets.newHashSet(2, 3, 4);

Map<Integer, Integer> numberToPowerOfTwoMuttable = Maps.asMap(lowNumbers, powerOfTwo);
Map<Integer, Integer> numberToPowerOfTwoImuttable = Maps.toMap(lowNumbers, powerOfTwo);
assertThat(numberToPowerOfTwoMuttable.get(2), equalTo(4));
assertThat(numberToPowerOfTwoImuttable.get(2), equalTo(4));
```

从谓词创建函数

```java
List<Integer> numbers = Lists.newArrayList(1, 2, 3, 6);
Predicate<Integer> acceptEvenNumber = new Predicate<Integer>() {
    @Override
    public boolean apply(Integer number) {
        return (number % 2) == 0;
    }
};
Function<Integer, Boolean> isEventNumberFunction = Functions.forPredicate(acceptEvenNumber);
List<Boolean> areNumbersEven = Lists.transform(numbers, isEventNumberFunction);

assertThat(areNumbersEven, contains(false, true, false, true));
```

## 3. 更多番石榴食谱

Guava 是一个全面且非常有用的库——这里有一些以食谱形式介绍的 API：

-   #### [番石榴订购食谱](https://www.baeldung.com/guava-order)

-   #### [番石榴系列食谱](https://www.baeldung.com/guava-collections)

享受。

## 4. 总结

这种格式与我通常的教程略有不同——主要是因为这是一本我已经保存和使用了很长时间的内部开发手册。目标是让这些信息在网上随时可用——并在我遇到新的有用示例时添加到其中。