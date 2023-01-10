## 1. 概述

在本文中，我们将研究[PCollections](https://pcollections.org/)，这是一个提供持久、不可变集合的Java库。

在更新操作期间不能直接修改[持久数据结构(集合)，而是返回一个包含更新操作结果的新对象。](https://en.wikipedia.org/wiki/Persistent_data_structure)它们不仅是不可变的，而且是持久的——这意味着在执行修改后，集合的先前版本保持不变。

PCollections 类似于JavaCollections 框架并与之兼容。

## 2.依赖关系

让我们将以下依赖项添加到我们的pom.xml中，以便我们在我们的项目中使用 PCollections：

```xml
<dependency>
    <groupId>org.pcollections</groupId>
    <artifactId>pcollections</artifactId>
    <version>2.1.2</version>
</dependency>
```

如果我们的项目是基于 Gradle 的，我们可以将相同的工件添加到我们的build.gradle文件中：

```groovy
compile 'org.pcollections:pcollections:2.1.2'
```

最新版本可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"pcollections" g%3A"org.pcollections")上找到。

## 3. 映射结构(HashPMap)

HashPMap是一种持久化的地图数据结构。它类似于用于存储非空键值数据的java.util.HashMap 。

我们可以使用 HashTreePMap 中方便的静态方法来实例化HashPMap 。这些静态方法返回一个由 IntTreePMap支持的HashPMap实例。

HashTreePMap类的静态empty()方法创建一个没有元素的空HashPMap——就像使用java.util.HashMap的默认构造函数一样：

```java
HashPMap<String, String> pmap = HashTreePMap.empty();
```

我们可以使用另外两种静态方法来创建HashPMap。singleton()方法创建一个只有一个条目的HashPMap ：

```java
HashPMap<String, String> pmap1 = HashTreePMap.singleton("key1", "value1");
assertEquals(pmap1.size(), 1);
```

from()方法从现有的java.util.HashMap实例(和其他java.util.Map实现)创建一个HashPMap ：

```java
Map map = new HashMap();
map.put("mkey1", "mval1");
map.put("mkey2", "mval2");

HashPMap<String, String> pmap2 = HashTreePMap.from(map);
assertEquals(pmap2.size(), 2);
```

尽管HashPMap继承了java.util.AbstractMap和java.util.Map的一些方法，但它有自己独有的方法。

minus()方法从映射中删除单个条目，而minusAll()方法删除多个条目。还有分别添加单个和多个条目的plus()和plusAll()方法：

```java
HashPMap<String, String> pmap = HashTreePMap.empty();
HashPMap<String, String> pmap0 = pmap.plus("key1", "value1");

Map map = new HashMap();
map.put("key2", "val2");
map.put("key3", "val3");
HashPMap<String, String> pmap1 = pmap0.plusAll(map);

HashPMap<String, String> pmap2 = pmap1.minus("key1");

HashPMap<String, String> pmap3 = pmap2.minusAll(map.keySet());

assertEquals(pmap0.size(), 1);
assertEquals(pmap1.size(), 3);
assertFalse(pmap2.containsKey("key1"));
assertEquals(pmap3.size(), 0);
```

请务必注意，在pmap上调用put()将抛出UnsupportedOperationException。由于 PCollections 对象是持久且不可变的，因此每个修改操作都会返回一个对象的新实例 ( HashPMap )。

让我们继续看看其他数据结构。

## 4. 列表结构(TreePVector 和 ConsPStack)

TreePVector是 java.util.ArrayList 的持久类比，而ConsPStack是java.util.LinkedList 的类比。TreePVector和ConsPStack有方便的静态方法来创建新实例——就像HashPMap一样。

empty()方法创建一个空的TreePVector ，而singleton()方法创建一个只有一个元素的TreePVector 。还有from()方法可用于从任何java.util.Collection创建TreePVector的实例。

ConsPStack具有实现相同目标的同名静态方法。

TreePVector有操作它的方法。它具有用于删除元素的minus()和minusAll()方法；plus()和plusAll ()用于添加元素。

with()用于替换指定索引处的元素，而subList ()从集合中获取一定范围的元素。

这些方法在ConsPStack中也可用。

让我们考虑以下代码片段，它举例说明了上述方法：

```java
TreePVector pVector = TreePVector.empty();

TreePVector pV1 = pVector.plus("e1");
TreePVector pV2 = pV1.plusAll(Arrays.asList("e2", "e3", "e4"));
assertEquals(1, pV1.size());
assertEquals(4, pV2.size());

TreePVector pV3 = pV2.minus("e1");
TreePVector pV4 = pV3.minusAll(Arrays.asList("e2", "e3", "e4"));
assertEquals(pV3.size(), 3);
assertEquals(pV4.size(), 0);

TreePVector pSub = pV2.subList(0, 2);
assertTrue(pSub.contains("e1") && pSub.contains("e2"));

TreePVector pVW = (TreePVector) pV2.with(0, "e10");
assertEquals(pVW.get(0), "e10");
```

在上面的代码片段中，pSub是另一个TreePVector对象并且独立于pV2。可以看出，pV2没有被subList()操作改变；而是创建了一个新的TreePVector对象，并用索引 0 到 2的pV2元素填充。

这就是不变性的含义，也是 PCollections 的所有修改方法都会发生的情况。

## 5. 集合结构(MapPSet)

MapPSet是java.util.HashSet的持久的、地图支持的模拟。它可以通过HashTreePSet的静态方法方便地实例化——empty ()、from()和singleton()。它们的功能与前面示例中解释的相同。

MapPSet有plus()、plusAll()、minus()和minusAll()方法来操作集合数据。此外，它还继承了java.util.Set、java.util.AbstractCollection和java.util.AbstractSet的方法：

```java
MapPSet pSet = HashTreePSet.empty()     
  .plusAll(Arrays.asList("e1","e2","e3","e4"));
assertEquals(pSet.size(), 4);

MapPSet pSet1 = pSet.minus("e4");
assertFalse(pSet1.contains("e4"));
```

最后，还有OrderedPSet——它维护元素的插入顺序，就像java.util.LinkedHashSet一样。

## 六. 总结

总之，在这个快速教程中，我们探索了 PCollections——类似于我们在Java中可用的核心集合的持久数据结构。当然，PCollections [Javadoc](https://www.javadoc.io/doc/org.pcollections/pcollections/2.1.2)可以更深入地了解库的复杂性。