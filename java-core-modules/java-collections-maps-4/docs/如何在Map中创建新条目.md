## 1. 概述

在本教程中，我们将讨论如何使用Java的内置类、第三方库和我们的自定义实现来创建表示[Map](https://www.baeldung.com/java-map-entry)中的键值关联的Entry对象。

## 2. 使用Java内置类

Java 提供了地图。Entry接口有两个简单的实现来创建一个Entry。让我们来看看它们。

### 2.1. 使用AbstractMap。简单入口

SimpleEntry类是 AbstractMap 类中的静态嵌套类。它提供了两个不同的构造函数来初始化一个实例：

```java
AbstractMap.SimpleEntry<String, String> firstEntry = new AbstractMap.SimpleEntry<>("key1", "value1");
AbstractMap.SimpleEntry<String, String> secondEntry = new AbstractMap.SimpleEntry<>("key2", "value2");
AbstractMap.SimpleEntry<String, String> thirdEntry = new AbstractMap.SimpleEntry<>(firstEntry);
thirdEntry.setValue("a different value");

assertThat(Stream.of(firstEntry, secondEntry, thirdEntry))
  .extracting("key", "value")
  .containsExactly(
    tuple("key1", "value1"),
    tuple("key2", "value2"),
    tuple("key1", "a different value"));
```

正如我们在这里看到的，其中一个构造函数接受键和值，而另一个构造函数接受一个Entry实例来初始化一个新的Entry实例。

### 2.2. 使用AbstractMap。SimpleImmutableEntry

就像SimpleEntry 一样，我们可以使用SimpleImmutableEntry来创建条目：

```java
AbstractMap.SimpleImmutableEntry<String, String> firstEntry = new AbstractMap.SimpleImmutableEntry<>("key1", "value1");
AbstractMap.SimpleImmutableEntry<String, String> secondEntry = new AbstractMap.SimpleImmutableEntry<>("key2", "value2");
AbstractMap.SimpleImmutableEntry<String, String> thirdEntry = new AbstractMap.SimpleImmutableEntry<>(firstEntry);

assertThat(Stream.of(firstEntry, secondEntry, thirdEntry))
  .extracting("key", "value")
  .containsExactly(
    tuple("key1", "value1"),
    tuple("key2", "value2"),
    tuple("key1", "value1"));
```

与SimpleEntry 相比，SimpleImmutableEntry不允许我们在初始化Entry实例后更改值。如果我们尝试更改该值，它会抛出java.lang.UnsupportedOperationException。

### 2.3. 使用地图。入口

从版本 9 开始，Java在Map接口中有一个静态方法entry()来创建一个Entry：

```java
Map.Entry<String, String> entry = Map.entry("key", "value");

assertThat(entry.getKey()).isEqualTo("key");
assertThat(entry.getValue()).isEqualTo("value");
```

我们需要记住，以这种方式创建的条目也是不可变的，如果我们在初始化后尝试更改值，则会导致java.lang.UnsupportedOperationException 。

## 3. 第三方库

除了Java本身，还有一些流行的库提供了创建条目的好方法。

### 3.1. 使用 Apache commons-collections4库

让我们首先包含我们的[Maven](https://mvnrepository.com/artifact/org.apache.commons/commons-collections4/4.4)依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
</dependency>
```

值得一提的是，除了Entry接口外，该库还提供了一个名为KeyValue 的接口：

```java
Map.Entry<String, String> firstEntry = new DefaultMapEntry<>("key1", "value1");
KeyValue<String, String> secondEntry = new DefaultMapEntry<>("key2", "value2");

KeyValue<String, String> thirdEntry = new DefaultMapEntry<>(firstEntry);
KeyValue<String, String> fourthEntry = new DefaultMapEntry<>(secondEntry);

firstEntry.setValue("a different value");

assertThat(firstEntry)
  .extracting("key", "value")
  .containsExactly("key1", "a different value");

assertThat(Stream.of(secondEntry, thirdEntry, fourthEntry))
  .extracting("key", "value")
  .containsExactly(
    tuple("key2", "value2"),
    tuple("key1", "value1"),
    tuple("key2", "value2"));
```

DefaultMapEntry类提供三种不同的构造函数。第一个接受键值对，第二个和第三个分别接受Entry 和KeyValue的参数类型。

UnmodifiableMapEntry类的行为方式也相同：

```java
Map.Entry<String, String> firstEntry = new UnmodifiableMapEntry<>("key1", "value1");
KeyValue<String, String> secondEntry = new UnmodifiableMapEntry<>("key2", "value2");

KeyValue<String, String> thirdEntry = new UnmodifiableMapEntry<>(firstEntry);
KeyValue<String, String> fourthEntry = new UnmodifiableMapEntry<>(secondEntry);

assertThat(firstEntry)
  .extracting("key", "value")
  .containsExactly("key1", "value1");

assertThat(Stream.of(secondEntry, thirdEntry, fourthEntry))
  .extracting("key", "value")
  .containsExactly(
    tuple("key2", "value2"),
    tuple("key1", "value1"),
    tuple("key2", "value2"));
```

然而，正如我们从它的名字可以理解的那样，UnmodifiableMapEntry也不允许我们在初始化后更改值。

### 3.2. 使用谷歌 Guava 库

让我们首先包含我们的[Maven](https://mvnrepository.com/artifact/com.google.guava/guava/31.0.1-jre)依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
 </dependency>
```

现在，让我们看看如何使用immutableEntry()方法：

```java
Map.Entry<String, String> firstEntry = Maps.immutableEntry("key1", "value1");
Map.Entry<String, String> secondEntry = Maps.immutableEntry("key2", "value2");

assertThat(Stream.of(firstEntry, secondEntry))
  .extracting("key", "value")
  .containsExactly(
    tuple("key1", "value1"),
    tuple("key2", "value2"));
```

由于它创建了一个不可变的条目，如果我们尝试更改该值，它会抛出java.lang.UnsupportedOperationException。

## 4.自定义实现

到目前为止，我们已经看到了一些创建Entry实例来表示键值关联的选项。这些类的设计方式必须符合Map接口实现(例如[HashMap](https://www.baeldung.com/java-hashmap) )的内部逻辑。 

这意味着只要我们遵守相同的规则，我们就可以创建我们自己的Entry接口实现。首先，让我们添加一个简单的实现：

```java
public class SimpleCustomKeyValue<K, V> implements Map.Entry<K, V> {

    private final K key;
    private V value;

    public SimpleCustomKeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }
    // standard getters and setters
    // standard equals and hashcode
    // standard toString
}
```

最后，让我们看几个使用示例：

```java
Map.Entry<String, String> firstEntry = new SimpleCustomKeyValue<>("key1", "value1");

Map.Entry<String, String> secondEntry = new SimpleCustomKeyValue<>("key2", "value2");
secondEntry.setValue("different value");

Map<String, String> map = Map.ofEntries(firstEntry, secondEntry);

assertThat(map)
  .isEqualTo(ImmutableMap.<String, String>builder()
    .put("key1", "value1")
    .put("key2", "different value")
    .build());
```

## 5.总结

在本文中，我们了解了如何使用Java提供的现有选项以及一些流行的第三方库提供的一些替代方法来创建Entry实例。此外，我们还创建了一个自定义实现并展示了一些使用示例。