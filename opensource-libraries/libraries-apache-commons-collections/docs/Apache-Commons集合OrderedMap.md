## 1. 概述

[Apache Commons Collections 库](https://commons.apache.org/proper/commons-collections/)提供了有用的类来补充JavaCollections Framework。

在本文中，我们将回顾接口[OrderedMap](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/OrderedMap.html)，它扩展了java.util.Map。

## 2.Maven依赖

我们需要做的第一件事是在我们的pom.xml中添加 Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.1</version>
</dependency>

```

[可以在Maven 中央存储库](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.commons" AND a%3A"commons-collections4")中找到该库的最新版本。

## 3. OrderedMap属性

简单的说，一个实现了OrderedMap接口的map：

-   保持其键集的顺序，尽管该集未排序
-   可以使用以下方法在两个方向上迭代：firstKey()和nextKey()，或lastKey()和previousKey()
-   可以用MapIterator遍历(也由库提供)
-   提供查找、更改、删除或替换元素的方法

## 4.使用OrderedMap

让我们在测试类中设置跑步者及其年龄的OrderedMap 。我们将使用LinkedMap——库中提供的OrderedMap实现之一。

首先，让我们设置跑步者和年龄数组，我们将使用它们来加载地图并验证值的顺序：

```java
public class OrderMapUnitTest {
    private String[] names = {"Emily", "Mathew", "Rose", "John", "Anna"};
    private Integer[] ages = {37, 28, 40, 36, 21};
    private LinkedMap<String, Integer> runnersLinkedMap;
 
    //...
}
```

现在，让我们初始化我们的地图：

```java
@Before
public void createRunners() {
    this.runnersLinkedMap = new LinkedMap<>();
    
    for (int i = 0; i < RUNNERS_COUNT; i++) {
        runners.put(this.names[i], this.ages[i]);
    }
}

```

### 4.1. 正向迭代

让我们看看前向迭代器是如何使用的：

```java
@Test
public void givenALinkedMap_whenIteratedForwards_thenPreservesOrder() {
    String name = this.runnersLinkedMap.firstKey();
    int i = 0;
    while (name != null) {
        assertEquals(name, names[i]);
        name = this.runnersLinkedMap.nextKey(name);
        i++;
    }
}

```

请注意，当我们到达最后一个键时，方法nextKey()将返回一个空值。

### 4.2. 向后迭代

现在让我们从最后一个键开始迭代：

```java
@Test
public void givenALinkedMap_whenIteratedBackwards_thenPreservesOrder() {
    String name = this.runnersLinkedMap.lastKey();
    int i = RUNNERS_COUNT - 1;
    while (name != null) {
        assertEquals(name, this.names[i]);
        name = this.runnersLinkedMap.previousKey(name);
        i--;
    }
}

```

一旦我们到达第一个键，previousKey()方法将返回 null。

### 4.3. MapIterator示例

现在让我们使用mapIterator()方法获取MapIterator ，因为我们展示了它如何保留数组names和ages中定义的跑步者顺序：

```java
@Test
public void givenALinkedMap_whenIteratedWithMapIterator_thenPreservesOrder() {
    OrderedMapIterator<String, Integer> runnersIterator 
      = this.runnersLinkedMap.mapIterator();
    
    int i = 0;
    while (runnersIterator.hasNext()) {
        runnersIterator.next();
 
        assertEquals(runnersIterator.getKey(), this.names[i]);
        assertEquals(runnersIterator.getValue(), this.ages[i]);
        i++;
    }
}

```

### 4.4. 删除元素

最后，让我们检查如何通过索引或对象删除元素：

```java
@Test
public void givenALinkedMap_whenElementRemoved_thenSizeDecrease() {
    LinkedMap<String, Integer> lmap 
      = (LinkedMap<String, Integer>) this.runnersLinkedMap;
    
    Integer johnAge = lmap.remove("John");
 
    assertEquals(johnAge, new Integer(36));
    assertEquals(lmap.size(), RUNNERS_COUNT - 1);

    Integer emilyAge = lmap.remove(0);
 
    assertEquals(emilyAge, new Integer(37));
    assertEquals(lmap.size(), RUNNERS_COUNT - 2);
}

```

## 5.提供的实现

目前，在库的 4.1 版中，有两个OrderedMap接口的实现—— ListOrderedMap和LinkedMap。

[ListOrderedMap](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/map/ListOrderedMap.html)使用java.util.List跟踪键集的顺序。它是OrderedMap的装饰器，可以使用静态方法ListOrderedMap.decorate(Map map)从任何Map创建。

[LinkedMap](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/map/LinkedMap.html)基于HashMap并通过允许双向迭代和OrderedMap接口的其他方法对其进行了改进。

这两种实现还提供了三种在OrderedMap接口之外的方法：

-   asList() – 获取类型为List<K>的列表(其中K是键的类型)，保留映射的顺序
-   get(int index) – 获取位置索引处的元素，与接口中提供的方法get(Object o)相反
-   indexOf(Object o) – 获取对象o在有序映射中

我们可以将OrderedMap转换为LinkedMap以使用asList()方法：

```java
@Test
public void givenALinkedMap_whenConvertedToList_thenMatchesKeySet() {
    LinkedMap<String, Integer> lmap 
      = (LinkedMap<String, Integer>) this.runnersLinkedMap;
    
    List<String> listKeys = new ArrayList<>();
    listKeys.addAll(this.runnersLinkedMap.keySet());
    List<String> linkedMap = lmap.asList();
 
    assertEquals(listKeys, linkedMap);
}

```

然后我们可以检查LinkedMap实现中方法indexOf(Object o)和get(int index)的功能：

```java
@Test
public void givenALinkedMap_whenSearchByIndexIsUsed_thenMatchesConstantArray() {
    LinkedMap<String, Integer> lmap 
      = (LinkedMap<String, Integer>) this.runnersLinkedMap;
    
    for (int i = 0; i < RUNNERS_COUNT; i++) {
        String name = lmap.get(i);
 
        assertEquals(name, this.names[i]);
        assertEquals(lmap.indexOf(this.names[i]), i);
    }
}

```

## 六. 总结

在本快速教程中，我们回顾了OrderedMap接口及其主要方法和实现。

有关详细信息，请参阅[Apache Commons Collections 库的 JavaDoc](https://commons.apache.org/proper/commons-collections/apidocs/index.html)。