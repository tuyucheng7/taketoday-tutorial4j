## 1.概述

EnumMap是一个[Map](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html)实现，专门以Enum作为其键。

在本教程中，我们将讨论它的属性、常见用例以及何时应该使用它。

## 2.项目设置

想象一个简单的需求，我们需要将一周中的几天与我们当天进行的运动对应起来：

```plaintext
Monday     Soccer                         
Tuesday    Basketball                     
Wednesday  Hiking                         
Thursday   Karate

```

为此，我们可以使用枚举：

```java
public enum DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
```

我们很快就会看到这将是我们地图的关键。

## 3.创作

要开始探索EnumMap，首先我们需要实例化一个：

```java
EnumMap<DayOfWeek, String> activityMap = new EnumMap<>(DayOfWeek.class);
activityMap.put(DayOfWeek.MONDAY, "Soccer");

```

这是我们与更常见的东西的第一个区别，比如HashMap。请注意，对于HashMap，类型参数化就足够了，这意味着我们可以使用newHashMap<>()。但是，EnumMap需要在构造函数中使用键类型。

### 3.1.EnumMap构造函数

EnumMap还附带了两个构造函数。第一个需要另一个EnumMap：

```java
EnumMap<DayOfWeek, String> activityMap = new EnumMap<>(DayOfWeek.class);
activityMap.put(DayOfWeek.MONDAY, "Soccer");
activityMap.put(DayOfWeek.TUESDAY, "Basketball");

EnumMap<DayOfWeek, String> activityMapCopy = new EnumMap<>(dayMap);
assertThat(activityMapCopy.size()).isEqualTo(2);
assertThat(activityMapCopy.get(DayOfWeek.MONDAY)).isEqualTo("Soccer");
assertThat(activityMapCopy.get(DayOfWeek.TUESDAY)).isEqualTo("Basketball");
```

### 3.2.地图构造函数

或者，如果我们有一个键为枚举的非空Map，那么我们也可以这样做：

```java
Map<DayOfWeek, String> ordinaryMap = new HashMap();
ordinaryMap.put(DayOfWeek.MONDAY, "Soccer");

EnumMap enumMap = new EnumMap(ordinaryMap);
assertThat(enumMap.size()).isEqualTo(1);
assertThat(enumMap.get(DayOfWeek.MONDAY)).isEqualTo("Soccer");
```

请注意，映射必须是非空的，以便EnumMap可以根据现有条目确定键类型。

如果指定的映射包含多个枚举类型，构造函数将抛出ClassCastException。

## 4.添加和检索元素

在实例化EnumMap之后，我们可以使用[put()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/EnumMap.html#put(K,V))方法添加我们的运动：

```java
activityMap.put(DayOfWeek.MONDAY, "Soccer");
```

要检索它，我们可以使用[get()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html#get(java.lang.Object))：

```java
assertThat(activityMap.get(DayOfWeek.MONDAY)).isEqualTo("Soccer");
```

## 5.检查元素

要检查我们是否为特定日期定义了映射，我们使用[containsKey()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/EnumMap.html#containsKey(java.lang.Object))：

```java
activityMap.put(DayOfWeek.WEDNESDAY, "Hiking");
assertThat(activityMap.containsKey(DayOfWeek.WEDNESDAY)).isTrue();
```

并且，要检查特定运动是否映射到我们使用的任何键[containsValue()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/EnumMap.html#containsValue(java.lang.Object))：

```java
assertThat(activityMap.containsValue("Hiking")).isTrue();

```

### 5.1.空值

现在，null是EnumMap的语义有效值。

让我们将null与“什么都不做”相关联，并将其映射到星期六：

```java
assertThat(activityMap.containsKey(DayOfWeek.SATURDAY)).isFalse();
assertThat(activityMap.containsValue(null)).isFalse();
activityMap.put(DayOfWeek.SATURDAY, null);
assertThat(activityMap.containsKey(DayOfWeek.SATURDAY)).isTrue();
assertThat(activityMap.containsValue(null)).isTrue();
```

## 6.删除元素

为了取消映射特定的一天，我们只需remove()它：

```java
activityMap.put(DayOfWeek.MONDAY, "Soccer");
assertThat(activityMap.remove(DayOfWeek.MONDAY)).isEqualTo("Soccer");
assertThat(activityMap.containsKey(DayOfWeek.MONDAY)).isFalse();

```

正如我们所观察到的，[remove(key)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/EnumMap.html#remove(java.lang.Object))返回与该键相关联的先前值，如果该键没有映射，则返回null。

我们还可以选择仅当某天映射到特定活动时才取消映射该特定日期：

```java
activityMap.put(DayOfWeek.Monday, "Soccer");
assertThat(activityMap.remove(DayOfWeek.Monday, "Hiking")).isEqualTo(false);
assertThat(activityMap.remove(DayOfWeek.Monday, "Soccer")).isEqualTo(true);

```

[remove(key,value)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html#remove(java.lang.Object,java.lang.Object))仅当键当前映射到指定值时才删除指定键的条目。

## 7.集合视图

就像普通地图一样，对于任何EnumMap，我们都可以有3个不同的视图或子集合。

首先，让我们创建一个新的活动地图：

```java
EnumMap<DayOfWeek, String> activityMap = new EnumMap(DayOfWeek.class);
activityMap.put(DayOfWeek.THURSDAY, "Karate");
activityMap.put(DayOfWeek.WEDNESDAY, "Hiking");
activityMap.put(DayOfWeek.MONDAY, "Soccer");
```

### 7.1.价值观

我们的活动地图的第一个视图是[values()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/EnumMap.html#values())，顾名思义，它返回地图中的所有值：

```java
Collection values = dayMap.values();
assertThat(values)
  .containsExactly("Soccer", "Hiking", "Karate");

```

注意这里EnumMap是一个有序映射。它使用DayOfWeek枚举的顺序来确定条目的顺序。

### 7.2.密钥集

类似地，[keySet()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/EnumMap.html#keySet())返回键的集合，同样是按枚举顺序：

```java
Set keys = dayMap.keySet();
assertThat(keys)
        .containsExactly(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY);

```

### 7.3.条目集

最后，[entrySet()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/EnumMap.html#entrySet())以键值对的形式返回映射：

```java
assertThat(dayMap.entrySet())
    .containsExactly(
        new SimpleEntry(DayOfWeek.MONDAY, "Soccer"),
        new SimpleEntry(DayOfWeek.WEDNESDAY, "Hiking"),
        new SimpleEntry(DayOfWeek.THURSDAY, "Karate")
    );

```

[在地图中排序当然可以派上用场，我们在比较TreeMap和HashMap](https://www.baeldung.com/java-treemap-vs-hashmap)的教程中进行了更深入的探讨。

### 7.4.可变性

现在，请记住，我们在原始活动地图中所做的任何更改都将反映在其任何视图中：

```java
activityMap.put(DayOfWeek.TUESDAY, "Basketball");
assertThat(values)
    .containsExactly("Soccer", "Basketball", "Hiking", "Karate");

```

反之亦然；我们对子视图所做的任何更改都将反映在原始活动图中：

```java
values.remove("Hiking");
assertThat(activityMap.containsKey(DayOfWeek.WEDNESDAY)).isFalse();
assertThat(activityMap.size()).isEqualTo(3);

```

根据EnumMap与Map接口的约定，子视图由原始地图支持。

## 8.何时使用EnumMap

### 8.1.表现

使用Enum作为键可以进行一些额外的性能优化，例如更快的哈希计算，因为所有可能的键都是预先知道的。

将枚举作为键的简单性意味着EnumMap只需要由具有非常简单的存储和检索逻辑的普通旧JavaArray进行备份。另一方面，通用Map实现需要解决与将通用对象作为其键相关的问题。例如，[HashMap](https://www.baeldung.com/java-hashmap)[需要复杂的数据结构和复杂得多的存储和检索逻辑](https://www.baeldung.com/java-hashmap)来应对哈希冲突的可能性。

### 8.2.功能性

此外，正如我们所见，EnumMap是一个有序映射，因为它的视图将按枚举顺序迭代。为了在更复杂的场景中获得类似的行为，我们可以查看TreeMap或LinkedHashMap。

## 9.总结

在本文中，我们探讨了Map接口的EnumMap实现。当使用Enum作为键时，EnumMap可以派上用场。