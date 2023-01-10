## 1. 概述

[Eclipse Collections](https://www.eclipse.org/collections/)是另一个改进的Java集合框架。

简而言之，它提供了优化的实现以及一些在核心Java中没有的附加数据结构和特性。

该库提供所有数据结构的可变和不可变实现。

## 2.Maven依赖

让我们首先将以下 Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency
    <groupId>org.eclipse.collections</groupId>
    <artifactId>eclipse-collections</artifactId>
    <version>8.2.0</version>
</dependency>
```

[我们可以在Maven Central Repository](https://mvnrepository.com/artifact/org.eclipse.collections/eclipse-collections)中找到最新版本的库。

## 3. 大局

### 3.1. 基本集合类型

Eclipse Collections 中的基本集合类型是：

-   ListIterable——一个有序的集合，保持插入顺序并允许重复元素。子接口包括： MutableList、 FixedSizeList和ImmutableList。最常见的 ListIterable 实现是 FastList，它是 MutableList 的子类
-   SetIterable——一个不允许重复元素的集合。它可以排序或未排序。子接口包括： SortedSetIterable和UnsortedSetIterable。最常见的未排序SetIterable实现是UnifiedSet
-   MapIterable——键/值对的集合。子接口包括MutableMap、 FixedSizeMap和ImmutableMap。两种常见的实现是UnifiedMap和MutableSortedMap。UnifiedMap不维护任何顺序，而MutableSortedMap维护元素的自然顺序
-   BiMap——可以在任一方向上迭代的键/值对的集合。BiMap扩展了MapIterable接口
-   Bag——一个允许重复的无序集合。子接口包括MutableBag 和 FixedSizeBag。最常见的实现是HashBag
-   StackIterable——一个保持“后进先出”顺序的集合，以相反的插入顺序迭代元素。子接口包括MutableStack和ImmutableStack
-   MultiMap——键/值对的集合，允许每个键有多个值

### 3.2. 原始集合

该框架还提供了大量原始集合；他们的实现以他们持有的类型命名。每种类型都有可变、不可变、同步和不可修改的形式：

-   原始列表
-   原始集
-   原始堆栈
-   原始包
-   原始地图
-   整数区间

有大量的原始映射形式，涵盖了原始或对象键以及原始或对象值的所有可能组合。

快速说明 - IntInterval是一个整数范围，可以使用步长值对其进行迭代。

## 4.实例化一个集合

要将元素添加到ArrayList或HashSet，我们通过调用无参数构造函数实例化一个集合，然后一个一个地添加每个元素。

虽然我们仍然可以在 Eclipse Collections 中这样做，但我们也可以实例化一个集合并在一行中同时提供所有初始元素。

让我们看看如何实例化FastList：

```java
MutableList<String> list = FastList.newListWith(
  "Porsche", "Volkswagen", "Toyota", "Mercedes", "Toyota");
```

同样，我们可以实例化一个UnifiedSet并通过将元素传递给newSetWith()静态方法来向其中添加元素：

```java
Set<String> comparison = UnifiedSet.newSetWith(
  "Porsche", "Volkswagen", "Toyota", "Mercedes");
```

下面是我们如何实例化一个HashBag：

```java
MutableBag<String> bag = HashBag.newBagWith(
  "Porsche", "Volkswagen", "Toyota", "Porsche", "Mercedes");
```

实例化映射并向它们添加键值对是类似的。唯一的区别是我们将键和值对作为Pair接口的实现传递给newMapWith()方法。

我们以UnifiedMap为例：

```java
Pair<Integer, String> pair1 = Tuples.pair(1, "One");
Pair<Integer, String> pair2 = Tuples.pair(2, "Two");
Pair<Integer, String> pair3 = Tuples.pair(3, "Three");

UnifiedMap<Integer, String> map = new UnifiedMap<>(pair1, pair2, pair3);
```

我们仍然可以使用JavaCollections API 方法：

```java
UnifiedMap<Integer, String> map = new UnifiedMap<>();

map.put(1, "one");
map.put(2, "two");
map.put(3, "three");
```

由于无法修改不可变集合，因此它们没有实现修改集合的方法，例如add()和remove()。

然而，不可修改的集合允许我们调用这些方法，但如果我们这样做，将会抛出UnsupportedOperationException 。

## 5. 从集合中检索元素

就像使用标准Lists一样，Eclipse Collections Lists的元素可以通过它们的索引来检索：

```java
list.get(0);
```

Eclipse Collections 映射的值可以使用它们的键来检索：

```java
map.get(0);
```

getFirst()和getLast()方法可用于分别检索列表的第一个和最后一个元素。对于其他集合，它们返回迭代器返回的第一个和最后一个元素。

```java
map.getFirst();
map.getLast();
```

max()和min()方法可用于根据自然顺序获取集合的最大值和最小值。

```java
map.max();
map.min();
```

## 6. 遍历一个集合

Eclipse Collections 提供了许多迭代集合的方法。让我们看看它们是什么以及它们在实践中是如何工作的。

### 6.1. 集合过滤

选择模式返回一个新集合，其中包含满足逻辑条件的集合元素。它本质上是一个过滤操作。

这是一个例子：

```java
@Test
public void givenListwhenSelect_thenCorrect() {
    MutableList<Integer> greaterThanThirty = list
      .select(Predicates.greaterThan(30))
      .sortThis();
    
    Assertions.assertThat(greaterThanThirty)
      .containsExactly(31, 38, 41);
}
```

使用一个简单的 lambda 表达式可以完成同样的事情：

```java
return list.select(i -> i > 30)
  .sortThis();
```

拒绝模式则相反。它返回所有不满足逻辑条件的元素的集合。

让我们看一个例子：

```java
@Test
public void whenReject_thenCorrect() {
    MutableList<Integer> notGreaterThanThirty = list
      .reject(Predicates.greaterThan(30))
      .sortThis();
    
    Assertions.assertThat(notGreaterThanThirty)
      .containsExactlyElementsOf(this.expectedList);
}
```

在这里，我们拒绝所有大于 30 的元素。

### 6.2. 收集()方法

collect方法返回一个新集合，其元素是所提供的 lambda 表达式返回的结果——本质上它是来自 Stream API 的map ( )和collect()的组合。

让我们看看它的实际效果：

```java
@Test
public void whenCollect_thenCorrect() {
    Student student1 = new Student("John", "Hopkins");
    Student student2 = new Student("George", "Adams");
    
    MutableList<Student> students = FastList
      .newListWith(student1, student2);
    
    MutableList<String> lastNames = students
      .collect(Student::getLastName);
    
    Assertions.assertThat(lastNames)
      .containsExactly("Hopkins", "Adams");
}
```

创建的集合lastNames包含从学生列表中收集的姓氏。

但是，如果返回的集合是集合的集合并且我们不想维护嵌套结构怎么办？

例如，如果每个学生都有多个地址，而我们需要一个以字符串形式包含地址的集合而不是集合的集合，我们可以使用flatCollect()方法。

这是一个例子：

```java
@Test
public void whenFlatCollect_thenCorrect() {
    MutableList<String> addresses = students
      .flatCollect(Student::getAddresses);
    
    Assertions.assertThat(addresses)
      .containsExactlyElementsOf(this.expectedAddresses);
}
```

### 6.3. 元素检测

detect方法查找并返回满足逻辑条件的第一个元素。

让我们看一个简单的例子：

```java
@Test
public void whenDetect_thenCorrect() {
    Integer result = list.detect(Predicates.greaterThan(30));
    
    Assertions.assertThat(result)
      .isEqualTo(41);
}
```

anySatisfy方法确定集合中的任何元素是否满足逻辑条件。

这是一个例子：

```java
@Test
public void whenAnySatisfiesCondition_thenCorrect() {
    boolean result = list.anySatisfy(Predicates.greaterThan(30));
    
    assertTrue(result);
}
```

同样，allSatisfy方法确定集合中的所有元素是否都满足逻辑条件。

让我们看一个简单的例子：

```java
@Test
public void whenAnySatisfiesCondition_thenCorrect() {
    boolean result = list.allSatisfy(Predicates.greaterThan(0));
    
    assertTrue(result);
}
```

### 6.4. partition( )方法

分区方法根据元素是否满足逻辑条件将集合的每个元素分配到两个集合之一。

让我们看一个例子：

```java
@Test
public void whenAnySatisfiesCondition_thenCorrect() {
    MutableList<Integer> numbers = list;
    PartitionMutableList<Integer> partitionedFolks = numbers
      .partition(i -> i > 30);
	
    MutableList<Integer> greaterThanThirty = partitionedFolks
      .getSelected()
      .sortThis();
    MutableList<Integer> smallerThanThirty = partitionedFolks
      .getRejected()
      .sortThis();
    
    Assertions.assertThat(smallerThanThirty)
      .containsExactly(1, 5, 8, 17, 23);
    Assertions.assertThat(greaterThanThirty)
      .containsExactly(31, 38, 41);
}
```

### 6.5. 惰性迭代

惰性迭代是一种优化模式，其中调用迭代方法，但它的实际执行被推迟，直到另一个后续方法需要它的操作或返回值。

```java
@Test
public void whenLazyIteration_thenCorrect() {
    Student student1 = new Student("John", "Hopkins");
    Student student2 = new Student("George", "Adams");
    Student student3 = new Student("Jennifer", "Rodriguez");

    MutableList<Student> students = Lists.mutable
      .with(student1, student2, student3);
    LazyIterable<Student> lazyStudents = students.asLazy();
    LazyIterable<String> lastNames = lazyStudents
      .collect(Student::getLastName);
    
    Assertions.assertThat(lastNames)
      .containsAll(Lists.mutable.with("Hopkins", "Adams", "Rodriguez"));
}
```

在这里，lazyStudents对象在调用collect()方法之前不会检索学生列表的元素。

## 7.配对集合元素

zip()方法通过将两个集合的元素组合成对来返回一个新集合。如果两个集合中的任何一个更长，则剩余的元素将被截断。

让我们看看如何使用它：

```java
@Test
public void whenZip_thenCorrect() {
    MutableList<String> numbers = Lists.mutable
      .with("1", "2", "3", "Ignored");
    MutableList<String> cars = Lists.mutable
      .with("Porsche", "Volvo", "Toyota");
    MutableList<Pair<String, String>> pairs = numbers.zip(cars);
    
    Assertions.assertThat(pairs)
      .containsExactlyElementsOf(this.expectedPairs);
}
```

我们还可以使用zipWithIndex()方法将集合的元素与其索引配对：

```java
@Test
public void whenZip_thenCorrect() {
    MutableList<String> cars = FastList
      .newListWith("Porsche", "Volvo", "Toyota");
    MutableList<Pair<String, Integer>> pairs = cars.zipWithIndex();
    
    Assertions.assertThat(pairs)
      .containsExactlyElementsOf(this.expectedPairs);
}
```

## 8. 转换集合

Eclipse Collections 提供了将一种容器类型转换为另一种容器类型的简单方法。这些方法是toList()、toSet()、toBag()和toMap() 。

让我们看看如何使用它们：

```java
public static List convertToList() {
    UnifiedSet<String> cars = new UnifiedSet<>();
    
    cars.add("Toyota");
    cars.add("Mercedes");
    cars.add("Volkswagen");
    
    return cars.toList();
}
```

让我们运行我们的测试：

```java
@Test
public void whenConvertContainerToAnother_thenCorrect() {
    MutableList<String> cars = (MutableList) ConvertContainerToAnother 
      .convertToList();
    
    Assertions.assertThat(cars)
      .containsExactlyElementsOf(
      FastList.newListWith("Volkswagen", "Toyota", "Mercedes"));
}
```

## 9.总结

在本教程中，我们快速了解了 Eclipse Collections 及其提供的功能。