## 1. 概述

在本文中，我们将了解HashMap如何在内部管理键值对以及如何编写自定义键实现。

## 2.密钥管理

### 2.1. 内部结构

映射用于存储分配给键的值。该键用于识别Map中的值并检测重复项。

TreeMap使用Comparable#compareTo(Object)方法对键进行排序(以及识别相等性)，而HashMap使用基于散列的结构，使用快速草图可以更容易地解释它：

[![哈希表](https://www.baeldung.com/wp-content/uploads/2021/10/hashtable.svg)](https://www.baeldung.com/wp-content/uploads/2021/10/hashtable.svg)

Map不允许重复键，因此使用Object#equals(Object)方法将键相互比较。由于该方法性能较差，应尽量避免调用。这是通过Object#hashCode()方法实现的。此方法允许按对象的哈希值对对象进行排序，然后仅当对象共享相同的哈希值时才需要调用Object#equals方法。

这种密钥管理也适用于[HashSet](https://www.baeldung.com/java-hashset)类，其实现内部使用了一个HashMap。

### 2.2. 插入和查找键值对

让我们创建一个简单商店的HashMap示例，该商店通过商品 ID ( String )管理库存商品的数量 ( Integer )。在那里，我们输入了一个样本值：

```java
Map<String, Integer> items = new HashMap<>();
// insert
items.put("158-865-A", 56);
// find
Integer count = items.get("158-865-A");
```

插入键值对的算法：

1.  调用“158-865-A”.hashCode()获取哈希值

2.  查找共享相同散列值的现有键列表

3.  比较列表中的任何键与

    “158-865-A”.equals(key)

    1.  第一个相等被标识为已经存在，新的替换分配的值。
    2.  如果不相等，则键值对作为新条目插入。

要找到一个值，算法是相同的，只是没有替换或插入任何值。

## 3.自定义键类

我们可以得出总结，要为键使用自定义类，必须[正确实现](https://www.baeldung.com/java-equals-hashcode-contracts)[hashCode()和equals()](https://www.baeldung.com/java-equals-hashcode-contracts)。简单来说，我们必须确保hashCode()方法返回：

-   只要状态不变，对象的值就相同(内部一致性)
-   相同对象的相同值(等于一致性)
-   不相等的对象尽可能多的不同值。

我们通常可以说hashCode()和equals()在它们的计算中应该考虑相同的字段，我们必须覆盖它们两个或两个都不覆盖。我们可以使用[Lombok](https://www.baeldung.com/intro-to-project-lombok)或我们的 IDE 生成器轻松实现这一点。

另一个要点是：当对象用作键时，不要更改对象的哈希码。一个简单的解决方案是将键类设计成[不可变](https://www.baeldung.com/java-immutable-object)的，但只要我们能确保不能对键进行操作，这不是必需的。

不变性在这里有一个优势：哈希值可以在对象实例化时计算一次，这可以提高性能，尤其是对于复杂的对象。

### 3.1. 好的例子

例如，我们将设计一个由x和y值组成的Coordinate类，并将其用作HashMap中的键：

```java
Map<Coordinate, Color> pixels = new HashMap<>();
Coordinate coord = new Coordinate(1, 2);
pixels.put(coord, Color.CYAN);
// read the color
Color color = pixels.get(new Coordinate(1, 2));
```

让我们实现我们的Coordinate类：

```java
public class Coordinate {
    private final int x;
    private final int y;
    private int hashCode;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
        this.hashCode = Objects.hash(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Coordinate that = (Coordinate) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}

```

作为替代方案，我们可以使用 Lombok 使我们的课程更短：

```java
@RequiredArgsConstructor
@Getter
// no calculation in the constructor, but
// since Lombok 1.18.16, we can cache the hash code
@EqualsAndHashCode(cacheStrategy = CacheStrategy.LAZY)
public class Coordinate {
    private final int x;
    private final int y;
}

```

最佳的内部结构是：

[![哈希表最优](https://www.baeldung.com/wp-content/uploads/2021/10/hashtable_optimal.svg)](https://www.baeldung.com/wp-content/uploads/2021/10/hashtable_optimal.svg)

### 3.2. 坏例子：静态哈希值

如果我们通过对所有实例使用静态哈希值来实现Coordinate类， HashMap将正常工作，但性能会显着下降：

```java
public class Coordinate {

    ...

    @Override
    public int hashCode() {
        return 1; // return same hash value for all instances
    }
}
```

哈希结构如下所示：

[![哈希表最差](https://www.baeldung.com/wp-content/uploads/2021/10/hashtable_worst.svg)](https://www.baeldung.com/wp-content/uploads/2021/10/hashtable_worst.svg)

这完全否定了散列值的优势。

### 3.3. 坏例子：可修改的哈希值

如果我们使键类可变，我们应该确保实例的状态在用作键时永远不会改变：

```java
Map<Coordinate, Color> pixels = new HashMap<>();
Coordinate coord = new Coordinate(1, 2); // x=1, y=2
pixels.put(coord, Color.CYAN);
coord.setX(3); // x=3, y=2
```

因为Coordinate存放在旧的hash值下，在新的hash值下是找不到的。因此，下面的行将导致空值：

```java
Color color = pixels.get(coord);
```

以下行将导致对象在Map中存储两次：

```java
pixels.put(coord, Color.CYAN);
```

## 4。总结

在本文中，我们阐明了为HashMap实现自定义键类是正确实现equals()和hashCode()的问题。我们已经看到哈希值是如何在内部使用的，以及这将如何受到好的和坏的影响。