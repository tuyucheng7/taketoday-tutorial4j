## 1. 概述

集合是表示唯一项目集合的便捷方式。

在本教程中，我们将更多地了解这意味着什么以及我们如何在Java中使用它。

## 2. 一点集合论

### 2.1. 什么是集合？

集合只是一组独特的事物。因此，任何集合的一个显着特征是它不包含重复项。

我们可以将任何我们喜欢的东西放入一个集合中。但是，我们通常使用集合将具有共同特征的事物组合在一起。例如，我们可以有一组车辆或一组动物。

让我们使用两组整数作为一个简单的例子：

```plaintext
setA : {1, 2, 3, 4}

setB : {2, 4, 6, 8}
```

我们可以通过简单地将值放入圆圈来将集合显示为图表：
[![两组的维恩图](https://www.baeldung.com/wp-content/uploads/2019/04/SetASetB-2.png)](https://www.baeldung.com/wp-content/uploads/2019/04/SetASetB-2.png)

像这样的图被称为维恩图，它为我们提供了一种有用的方式来显示集合之间的交互，我们将在后面看到。

### 2.2. 集合的交集

术语交集表示不同集合的公共值。

我们可以看到两个集合中都存在整数 2 和 4。所以 setA 和 setB 的交集是 2 和 4，因为这些是我们两个集合共有的值。

```plaintext
setA intersection setB = {2, 4}
```

为了在图表中显示交叉点，我们合并两个集合并突出显示两个集合共有的区域：
[![拦截的维恩图](https://www.baeldung.com/wp-content/uploads/2019/04/interesection-1.png)](https://www.baeldung.com/wp-content/uploads/2019/04/interesection-1.png)

### 2.3. 集合并集

术语联合意味着组合不同集合的值。

因此，让我们创建一个新集，它是我们示例集的并集。我们已经知道集合中不能有重复值。但是，我们的集合有一些重复值(2 和 4)。因此，当我们合并两组的内容时，我们需要确保删除重复项。所以我们最终得到 1、2、3、4、6 和 8。

```plaintext
setA union setB = {1, 2, 3, 4, 6, 8}
```

我们可以再次在图表中显示并集。因此，让我们合并两个集合并突出显示代表并集的区域：
[![联盟的维恩图](https://www.baeldung.com/wp-content/uploads/2019/04/union-2.png)](https://www.baeldung.com/wp-content/uploads/2019/04/union-2.png)

### 2.4. 集合的相对补集

术语相对补充表示一组中的值不在另一组中。它也被称为集合差异。

现在让我们创建新的集合，它们是setA和setB的相对补充。

```plaintext
relative complement of setA in setB = {6, 8}

relative complement of setB in setA = {1, 3}
```

现在，让我们突出显示setA中不属于setB的区域。这给了我们setA中setB的相对补充：
[![相对互补的维恩图](https://www.baeldung.com/wp-content/uploads/2019/04/relativecomplement-1.png)](https://www.baeldung.com/wp-content/uploads/2019/04/relativecomplement-1.png)

### 2.5. 子集和超集

子集只是较大集合的一部分，较大集合称为超集。当我们有子集和超集时，两者的并集等于超集，交集等于子集。

## 3. 使用java.util.Set实现集合操作 

为了了解我们如何在Java中执行集合操作，我们将使用示例集合并实现交集、联合和相对补集。因此，让我们从创建我们的整数样本集开始：

```java
private Set<Integer> setA = setOf(1,2,3,4);
private Set<Integer> setB = setOf(2,4,6,8);
    
private static Set<Integer> setOf(Integer... values) {
    return new HashSet<Integer>(Arrays.asList(values));
}
```

### 3.1. 路口

首先，我们将使用retainAll方法来创建样本集的交集。因为retainAll直接修改集合，所以我们将制作一个名为intersectSet的setA的副本。然后我们将使用retainAll方法来保留也在setB中的值：

```java
Set<Integer> intersectSet = new HashSet<>(setA);
intersectSet.retainAll(setB);
assertEquals(setOf(2,4), intersectSet);
```

### 3.2. 联盟

现在让我们使用addAll方法来创建样本集的并集。addAll方法将提供的集合的所有成员添加到另一个。同样，当addAll直接更新集合时，我们将创建一个名为unionSet的setA副本，然后将setB添加到其中：

```java
Set<Integer> unionSet = new HashSet<>(setA);
unionSet.addAll(setB);
assertEquals(setOf(1,2,3,4,6,8), unionSet);
```

### 3.3. 相对补语

最后，我们将使用removeAll方法在 setA中创建setB的相对补集。我们知道我们想要setA中不存在于setB中的值。所以我们只需要从setA 中删除所有也在setB中的元素：

```java
Set<Integer> differenceSet = new HashSet<>(setA);
differenceSet.removeAll(setB);
assertEquals(setOf(1,3), differenceSet);
```

## 4. 用Stream实现集合操作

### 4.1. 路口

让我们使用Streams创建集合的交集。

首先，我们将从setA中获取值到流中。然后我们将过滤流以保留所有也在setB中的值。最后，我们会将结果收集到一个新的Set中：

```java
Set<Integer> intersectSet = setA.stream()
    .filter(setB::contains)
    .collect(Collectors.toSet());
assertEquals(setOf(2,4), intersectSet);
```

### 4.2. 联盟

现在让我们使用静态方法Streams.concat将集合的值添加到单个流中。

为了从集合的串联中获得并集，我们需要删除所有重复项。我们将通过简单地将结果收集到一个Set中来做到这一点：

```java
Set<Integer> unionSet = Stream.concat(setA.stream(), setB.stream())
    .collect(Collectors.toSet());
assertEquals(setOf(1,2,3,4,6,8), unionSet);
```

### 4.3. 相对补语

最后，我们将在 setA 中创建setB的相对补充。

正如我们在交集示例中所做的那样，我们将首先从setA中获取值到流中。这次我们将过滤流以删除也在setB中的任何值。然后，我们会将结果收集到一个新的Set中：

```java
Set<Integer> differenceSet = setA.stream()
    .filter(val -> !setB.contains(val))
    .collect(Collectors.toSet());
assertEquals(setOf(1,3), differenceSet);
```

## 5. 集合运算的实用程序库

现在我们已经了解了如何使用纯Java执行基本的集合操作，让我们使用几个实用程序库来执行相同的操作。使用这些库的一个好处是方法名称清楚地告诉我们正在执行什么操作。

### 5.1. 依赖关系

为了使用[Guava Sets](https://search.maven.org/search?q=g:com.google.guava AND a:guava)和[Apache Commons Collections SetUtils](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-collections4)，我们需要添加它们的依赖项：

```java
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.3</version>
</dependency>
```

### 5.2. 番石榴套装

让我们使用 Guava Sets类对我们的示例集执行交集和联合。为此，我们可以简单地使用Sets类的静态方法union和intersection ：

```java
Set<Integer> intersectSet = Sets.intersection(setA, setB);
assertEquals(setOf(2,4), intersectSet);

Set<Integer> unionSet = Sets.union(setA, setB);
assertEquals(setOf(1,2,3,4,6,8), unionSet);
```

查看我们的[Guava Sets 文章](https://www.baeldung.com/guava-sets)以了解更多信息。

### 5.3. Apache 公共集合

现在让我们使用Apache Commons Collections中的SetUtils类的交集和联合静态方法：

```java
Set<Integer> intersectSet = SetUtils.intersection(setA, setB);
assertEquals(setOf(2,4), intersectSet);

Set<Integer> unionSet = SetUtils.union(setA, setB);
assertEquals(setOf(1,2,3,4,6,8), unionSet);
```

查看我们的[Apache Commons Collections SetUtils教程](https://www.baeldung.com/apache-commons-setutils)以了解更多信息。

## 六，总结

我们已经了解了如何对集合执行一些基本操作的概述，以及如何以多种不同方式实现这些操作的详细信息。