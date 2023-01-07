## 1. 概述

在本教程中，我们将学习如何使用字节数组作为[HashMap](https://www.baeldung.com/java-hashmap)中的键。由于HashMap的工作方式，不幸的是，我们不能直接这样做。我们将调查原因并研究解决该问题的几种方法。

## 2.为HashMap设计一个好的Key

### 2.1. HashMap 是如何工作的

HashMap使用[散列机制](https://www.baeldung.com/java-hashmap#internals-hashmap)来存储和检索自身的值。当我们调用put(key, value)方法时，HashMap会根据键的hashCode()方法计算哈希码。此哈希用于标识最终存储值的存储桶：

```java
public V put(K key, V value) {
    if (key == null)
        return putForNullKey(value);
    int hash = hash(key.hashCode());
    int i = indexFor(hash, table.length);
    for (Entry e = table[i]; e != null; e = e.next) {
        Object k;
        if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
            V oldValue = e.value;
            e.value = value;
            e.recordAccess(this);
            return oldValue;
        }
    }
 
    modCount++;
    addEntry(hash, key, value, i);
    return null;
}
```

当我们使用get(key)方法检索值时，会涉及到类似的过程。密钥用于计算哈希码，然后用于查找存储桶。然后使用equals()方法检查桶中的每个条目是否相等。最后返回匹配条目的值：

```java
public V get(Object key) {
    if (key == null)
        return getForNullKey();
    int hash = hash(key.hashCode());
    for (Entry e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
        Object k;
        if (e.hash == hash && ((k = e.key) == key || key.equals(k)))
            return e.value;
    }
    return null;
}
```

### 2.2. equals () 和hashCode ()之间的契约

[equals和hashCode](https://www.baeldung.com/java-equals-hashcode-contracts)方法都有应遵守[的约定。](https://www.baeldung.com/java-equals-hashcode-contracts)在HashMaps的上下文中，有一个方面特别重要：彼此相等的对象必须返回相同的hashCode。但是，返回相同hashCode的对象不需要彼此相等。这就是为什么我们可以在一个桶中存储多个值。

### 2.3. 不变性

HashMap中key的hashCode不应该改变。虽然这不是强制性的，但强烈建议密钥不可变。如果对象是不可变的，则无论hashCode方法的实现如何，它的hashCode都没有机会更改。

默认情况下，哈希是根据对象的所有字段计算的。如果我们想要一个可变键，我们需要覆盖hashCode方法以确保在其计算中不使用可变字段。为了维护合同，我们还需要更改equals方法。

### 2.4. 有意义的平等

为了能够成功地从地图中检索值，相等性必须有意义。在大多数情况下，我们需要能够创建一个新的键对象，该对象将等于地图中的某个现有键。出于这个原因，对象身份在这种情况下不是很有用。

这也是为什么使用原始字节数组不是真正的选择的主要原因。Java 中的数组使用对象标识来确定相等性。如果我们创建以字节数组为键的HashMap，我们将能够仅使用完全相同的数组对象来检索值。

让我们创建一个以字节数组为键的简单实现：

```java
byte[] key1 = {1, 2, 3};
byte[] key2 = {1, 2, 3};
Map<byte[], String> map = new HashMap<>();
map.put(key1, "value1");
map.put(key2, "value2");
```

我们不仅有两个具有几乎相同键的条目，而且我们无法使用具有相同值的新创建数组检索任何内容：

```java
String retrievedValue1 = map.get(key1);
String retrievedValue2 = map.get(key2);
String retrievedValue3 = map.get(new byte[]{1, 2, 3});

assertThat(retrievedValue1).isEqualTo("value1");
assertThat(retrievedValue2).isEqualTo("value2");
assertThat(retrievedValue3).isNull();
```

## 3.使用现有容器

我们可以使用现有的类来代替字节数组，这些类的相等性实现基于内容，而不是对象标识。

### 3.1. 细绳

字符串相等性基于字符数组的内容：

```java
public boolean equals(Object anObject) {
    if (this == anObject) {
        return true;
    }
    if (anObject instanceof String) {
        String anotherString = (String)anObject;
        int n = count;
        if (n == anotherString.count) {
            char v1[] = value;
            char v2[] = anotherString.value;
            int i = offset;
            int j = anotherString.offset;
            while (n-- != 0) {
                if (v1[i++] != v2[j++])
                   return false;
            }
            return true;
        }
    }
    return false;
}
```

String也是不可变的，基于字节数组创建String非常简单。我们可以使用[Base64](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Base64.html)方案轻松地对String进行编码和解码：

```java
String key1 = Base64.getEncoder().encodeToString(new byte[]{1, 2, 3});
String key2 = Base64.getEncoder().encodeToString(new byte[]{1, 2, 3});
```

现在我们可以创建一个以String作为键而不是字节数组的HashMap 。我们将以类似于前面示例的方式将值放入Map中：

```java
Map<String, String> map = new HashMap<>();
map.put(key1, "value1");
map.put(key2, "value2");
```

然后我们可以从地图中检索一个值。对于这两个键，我们将获得相同的第二个值。我们还可以检查键是否真正相等：

```java
String retrievedValue1 = map.get(key1);
String retrievedValue2 = map.get(key2);

assertThat(key1).isEqualTo(key2);
assertThat(retrievedValue1).isEqualTo("value2");
assertThat(retrievedValue2).isEqualTo("value2");
```

### 3.2. 列表

与String类似，List#equals方法将检查其每个元素是否相等。如果这些元素有一个合理的equals()方法并且是不可变的，List将作为HashMap键正确工作。我们只需要确保我们使用的是不可变的List实现：

```java
List<Byte> key1 = ImmutableList.of((byte)1, (byte)2, (byte)3);
List<Byte> key2 = ImmutableList.of((byte)1, (byte)2, (byte)3);
Map<List<Byte>, String> map = new HashMap<>();
map.put(key1, "value1");
map.put(key2, "value2");

assertThat(map.get(key1)).isEqualTo(map.get(key2));
```

请注意，字节对象的列表将比字节基元数组占用更多的内存。因此，该解决方案虽然方便，但在大多数情况下并不可行。

## 4. 实现自定义容器

我们还可以实现自己的包装器以完全控制哈希码计算和相等性。这样我们就可以确保解决方案很快并且不会占用大量内存。

让我们用一个最终的私有字节数组字段创建一个类。它没有 setter，它的 getter 将制作一个防御性副本以确保完全不变：

```java
public final class BytesKey {
    private final byte[] array;

    public BytesKey(byte[] array) {
        this.array = array;
    }

    public byte[] getArray() {
        return array.clone();
    }
}
```

我们还需要实现自己的equals和hashCode方法。幸运的是，我们可以使用Arrays实用程序类来完成这两项任务：

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BytesKey bytesKey = (BytesKey) o;
    return Arrays.equals(array, bytesKey.array);
}

@Override
public int hashCode() {
    return Arrays.hashCode(array);
}
```

最后，我们可以将包装器用作HashMap中的键：

```arduino
BytesKey key1 = new BytesKey(new byte[]{1, 2, 3});
BytesKey key2 = new BytesKey(new byte[]{1, 2, 3});
Map<BytesKey, String> map = new HashMap<>();
map.put(key1, "value1");
map.put(key2, "value2");
```

然后，我们可以使用任一声明的键检索第二个值，或者我们可以使用动态创建的键：

```java
String retrievedValue1 = map.get(key1);
String retrievedValue2 = map.get(key2);
String retrievedValue3 = map.get(new BytesKey(new byte[]{1, 2, 3}));

assertThat(retrievedValue1).isEqualTo("value2");
assertThat(retrievedValue2).isEqualTo("value2");
assertThat(retrievedValue3).isEqualTo("value2");
```

## 5.总结

在本教程中，我们研究了在HashMap中使用字节数组作为键的不同问题和解决方案。首先，我们调查了为什么我们不能使用数组作为键。然后我们使用一些内置容器来缓解这个问题，最后实现了我们自己的包装器。