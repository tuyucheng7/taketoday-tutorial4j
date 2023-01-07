## 1. 概述

Java 允许我们创建固定大小的数组或使用集合类来完成类似的工作。

在本教程中，我们将了解ArrayList的容量与 Array 的大小之间的差异。

我们还将查看何时应使用容量初始化ArrayList的示例，以及在内存使用方面的优缺点。

## 2.例子

要了解差异，让我们首先尝试这两种选择。

### 2.1. 数组的大小

在 java 中，在创建数组的新实例时必须指定数组的大小：

```java
Integer[] array = new Integer[100]; 
System.out.println("Size of an array:" + array.length);
```

在这里，我们创建了一个大小为 100 的整数数组，结果如下

```
数组大小：100
```

### 2.2. ArrayList的容量

从技术上讲，新创建的ArrayList[的默认容量](https://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/java/util/ArrayList.java#:~:text=DEFAULT_CAPACITY)( DEFAULT_CAPACITY )是 10。但是，Java 8 出于性能原因更改了此初始容量的使用方式。

它不会立即使用，并且一旦将新项目添加到列表中就会延迟使用。

因此，空ArrayList的默认容量是 0 而不是Java8 中的 10。一旦添加了第一项，就会使用DEFAULT_CAPACITY，即 10。

由于没有获取默认容量的内置方法，让我们使用[反射](https://www.baeldung.com/java-reflection)来返回它：

```java
public static int getDefaultCapacity(ArrayList<?> arrayList) throws Exception {

    if (arrayList == null) {
        return 0;
    }

    Field field = ArrayList.class.getDeclaredField("elementData");
    field.setAccessible(true);

    return ((Object[]) field.get(arrayList)).length;
}
```

现在，让我们确认空ArrayList的默认容量为 0：

```java
@Test
void givenEmptyArrayList_whenGetDefaultCapacity_thenReturnZero() throws Exception {

    ArrayList<Integer> myList = new ArrayList<>();
    int defaultCapacity = DefaultArrayListCapacity.getDefaultCapacity(myList);

    assertEquals(0, defaultCapacity);
}
```

接下来，让我们看看将新项目添加到空ArrayList时会发生什么：

```java
@Test
void givenEmptyArrayList_whenAddItemAndGetDefaultCapacity_thenReturn10() throws Exception {

    ArrayList<String> myList = new ArrayList<>();
    myList.add("ITEM 1");

    int defaultCapacity = DefaultArrayListCapacity.getDefaultCapacity(myList);

    assertEquals(10, defaultCapacity);
}
```

Java 8 中这一变化的目的是节省内存消耗，避免立即分配内存。

现在，让我们创建一个初始容量为 100的ArrayList ：

```java
List<Integer> list = new ArrayList<>(100);
assertEquals(0, list.size());
```

由于尚未添加任何元素，因此大小为零。

现在，让我们向列表中添加一个元素并检查其大小：

```java
list.add(10);
assertEquals(10, list.size());
```

## 3. 数组中的大小与ArrayList

下面是数组大小和ArrayList 容量之间的一些主要区别。

### 3.1. 大小修改

数组是固定大小的。一旦我们用一些int值作为其大小初始化数组，它就不能改变。大小和容量也彼此相等。

ArrayList的大小和容量不固定。列表的逻辑大小根据其中元素的插入和删除而变化。这是与其物理存储大小分开管理的。同样，当达到ArrayList容量的阈值时，它会增加容量以为更多元素腾出空间。

### 3.2. 内存分配

数组内存在创建时分配。当我们初始化数组时，它会根据数组的大小和类型分配内存。它使用引用类型的空值和基本类型的默认值初始化所有元素。

ArrayList随着它的增长改变内存分配。当我们在初始化ArrayList时指定容量时，它会分配足够的内存来存储不超过该容量的对象。逻辑大小保持为 0。当需要扩展容量时，将创建一个新的更大的数组，并将值到其中。

我们应该注意到，空ArrayList对象有一个特殊的 0 大小的单例数组，这使得创建它们的成本非常低。还值得注意的是，ArrayList在内部使用了一个对象引用数组。

## 4. 什么时候用容量初始化ArrayList

当我们在创建ArrayList之前知道它所需的大小时，我们可能希望初始化它的容量，但这通常不是必需的。但是，这可能是最佳选择的原因有几个。

### 4.1. 构建大型ArrayList

当我们知道它会变大时，最好用初始容量初始化一个列表。这可以防止在我们添加元素时进行一些代价高昂的增长操作。

同样，如果列表非常大，自动增长操作可能会分配比实际最大大小所需更多的内存。这是因为每次增长的数量是按目前大小的比例计算的。因此，对于大型列表，这可能会导致内存浪费。

### 4.2. 构建小型多ArrayList

如果我们有很多小集合，那么ArrayList的自动容量可能会造成很大比例的内存浪费。假设ArrayList更喜欢 10 的大小和较少的元素，但我们只存储 2 或 3。这意味着 70% 的内存浪费，如果我们有大量这样的列表，这可能很重要。

预先设置容量可以避免这种情况。

## 5. 避免浪费

我们应该注意到， 对于支持随机访问的灵活大小的对象容器， ArrayList是一个很好的解决方案。它消耗的内存比数组略多，但提供了一组更丰富的操作。

在某些用例中，尤其是在大量原始值集合周围，标准数组可能更快并且使用更少的内存。

同样，为了存储不需要通过索引访问的可变数量的元素，LinkedList可以提高性能。它没有任何内存管理开销。

## 6.总结

在这篇简短的文章中，我们了解了ArrayList 的容量与数组大小之间的差异。我们还研究了何时应该使用容量初始化ArrayList，以及它在内存使用和性能方面的优势。