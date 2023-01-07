## 1. 概述

选择一个随机的List元素是一个非常基本的操作，但实现起来并不那么明显。在本文中，我们将展示在不同上下文中执行此操作的最有效方法。

## 2. 随机挑选物品

为了从List实例中获取随机项目，你需要生成一个随机索引号，然后使用List.get()方法通过这个生成的索引号获取一个项目。

这里的关键点是记住你不能使用超过列表大小的索引。

### 2.1. 单件随机物品

为了选择一个随机索引，你可以使用Random.nextInt(int bound)方法：

```java
public void givenList_shouldReturnARandomElement() {
    List<Integer> givenList = Arrays.asList(1, 2, 3);
    Random rand = new Random();
    int randomElement = givenList.get(rand.nextInt(givenList.size()));
}
```

除了Random类，你始终可以使用静态方法Math.random()并将其与列表大小相乘(Math.random()生成介于 0(含)和 1(不含)之间的Double随机值，因此请记住将其转换为int乘法后)。

### 2.2. 在多线程环境中选择随机索引

使用单个Random类实例编写多线程应用程序时，可能会导致为访问该实例的每个进程选择相同的值。我们始终可以使用专用的ThreadLocalRandom类为每个线程创建一个新实例：

```java
int randomElementIndex
  = ThreadLocalRandom.current().nextInt(listSize) % givenList.size();
```

### 2.3. 选择重复的随机项目

有时你可能想从列表中挑选几个元素。这很简单：

```java
public void givenList_whenNumberElementsChosen_shouldReturnRandomElementsRepeat() {
    Random rand = new Random();
    List<String> givenList = Arrays.asList("one", "two", "three", "four");

    int numberOfElements = 2;

    for (int i = 0; i < numberOfElements; i++) {
        int randomIndex = rand.nextInt(givenList.size());
        String randomElement = givenList.get(randomIndex);
    }
}
```

### 2.4. 选择没有重复的随机项目

在这里，你需要确保该元素在选择后从列表中删除：

```java
public void givenList_whenNumberElementsChosen_shouldReturnRandomElementsNoRepeat() {
    Random rand = new Random();
    List<String> givenList = Lists.newArrayList("one", "two", "three", "four");

    int numberOfElements = 2;

    for (int i = 0; i < numberOfElements; i++) {
        int randomIndex = rand.nextInt(givenList.size());
        String randomElement = givenList.get(randomIndex);
        givenList.remove(randomIndex);
    }
}
```

### 2.5. 选择随机系列

如果你想获得随机系列的元素，Collections utils 类可能会很方便：

```java
public void givenList_whenSeriesLengthChosen_shouldReturnRandomSeries() {
    List<Integer> givenList = Lists.newArrayList(1, 2, 3, 4, 5, 6);
    Collections.shuffle(givenList);

    int randomSeriesLength = 3;

    List<Integer> randomSeries = givenList.subList(0, randomSeriesLength);
}
```

## 3.总结

在本文中，我们探索了针对不同场景从 List 实例中获取随机元素的最有效方法。