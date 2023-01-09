## 1. 简介

在本教程中，我们将说明如何将两个集合压缩为一个逻辑集合。

“ zip”操作与标准的“concat”或“merge”略有不同。“concat”或“merge”操作只是将新集合添加到现有集合的末尾，而“ zip”操作将从每个集合中取出一个元素并将它们组合起来。

核心库不隐式支持“ zip”，但肯定有第三方库具有这个有用的操作。

考虑两个列表，一个包含人名，另一个包含他们的年龄。

```java
List<String> names = new ArrayList<>(Arrays.asList("John", "Jane", "Jack", "Dennis"));

List<Integer> ages = new ArrayList<>(Arrays.asList(24, 25, 27));
```

压缩后，我们最终得到了由这两个集合中的相应元素构成的姓名-年龄对。

## 2. 使用Java8 IntStream

使用核心 Java，我们可以使用IntStream生成索引，然后使用它们从两个集合中提取相应的元素：

```java
IntStream
  .range(0, Math.min(names.size(), ages.size()))
  .mapToObj(i -> names.get(i) + ":" + ages.get(i))
  // ...
```

## 3.使用番石榴流

从版本 21 开始，Google Guava 在Streams类中提供了一个 zip 辅助方法。这消除了创建和映射索引的所有麻烦，并减少了输入和操作的语法：

```java
Streams
  .zip(names.stream(), ages.stream(), (name, age) -> name + ":" + age)
  // ...
```

## 4. 使用jOOλ (jOOL)

jOOL还提供了一些比Java8 Lambda 更吸引人的新增功能，并且在Tuple1到Tuple16 的支持下， zip 操作变得更加有趣：

```java
Seq
  .of("John","Jane", "Dennis")
  .zip(Seq.of(24,25,27));
```

这将产生一个包含压缩元素元组的Seq结果：

```java
(tuple(1, "a"), tuple(2, "b"), tuple(3, "c"))
```

jOOL 的 zip方法可以灵活地提供自定义转换功能：

```java
Seq
  .of(1, 2, 3)
  .zip(Seq.of("a", "b", "c"), (x, y) -> x + ":" + y);
```

或者如果一个人只想用索引压缩，他可以使用jOOL提供的zipWithIndex方法：

```java
Seq.of("a", "b", "c").zipWithIndex();
```

## 5.总结

在本快速教程中，我们了解了如何执行压缩操作。