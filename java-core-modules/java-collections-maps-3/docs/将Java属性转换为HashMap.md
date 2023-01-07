## 1. 概述

许多开发人员决定将应用程序参数存储在源代码之外。在Java中这样做的一种方法是使用外部配置文件并通过[java.util.Properties](https://www.baeldung.com/java-properties)类读取它们。

在本教程中，我们将重点介绍将java.util.Properties转换为[HashMap](https://www.baeldung.com/java-hashmap)的各种方法。我们将使用普通 Java、[lambdas](https://www.baeldung.com/java-8-lambda-expressions-tips)或外部库实现不同的方法来实现我们的目标。通过示例，我们将讨论每种解决方案的优缺点。

## 2.HashMap构造函数

在我们实现我们的第一个代码之前，让我们检查[java.util.Properties](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Properties.html)[的Javadoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Properties.html)。如我们所见，这个实用程序类继承自[Hashtable](https://www.baeldung.com/java-hash-table)，它也实现了[Map](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html)接口。此外，Java 包装了它的Reader和Writer类以直接处理String值。

根据这些信息，我们可以使用类型转换和构造函数调用将Properties转换为HashMap<String, String> 。

假设我们已经正确加载了我们的属性，我们可以实现：

```java
public static HashMap<String, String> typeCastConvert(Properties prop) {
    Map step1 = prop;
    Map<String, String> step2 = (Map<String, String>) step1;
    return new HashMap<>(step2);
}
```

在这里，我们通过三个简单的步骤实现我们的转换。

首先，根据继承图，我们需要将Properties转换为原始Map。此操作将强制发出第一个编译器警告，可以使用[@SuppressWarnings(“rawtypes”)注解](https://www.baeldung.com/java-suppresswarnings)将其禁用。

之后，我们将原始Map转换为Map<String, String>，导致另一个编译器警告，可以使用@SupressWarnings(“unchecked”)省略。

最后，我们使用[构造函数](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/HashMap.html#(java.util.Map))构建我们的HashMap。这是转换我们的Properties最快的方法，但是这个解决方案也有一个与类型安全相关的很大缺点：我们的Properties可能在转换之前被破坏和修改。

根据文档，Properties类具有强制使用String值的[setProperty()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Properties.html#setProperty(java.lang.String,java.lang.String))和[getProperty()方法。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Properties.html#getProperty(java.lang.String))但也有从Hashtable继承的[put()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Hashtable.html#put(K,V))和[putAll()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Hashtable.html#putAll(java.util.Map))方法，它们允许在我们的Properties中使用任何类型作为键或值：

```java
properties.put("property4", 456);
properties.put(5, 10.11);

HashMap<String, String> hMap = typeCastConvert(properties);
assertThrows(ClassCastException.class, () -> {
    String s = hMap.get("property4");
});
assertEquals(Integer.class, ((Object) hMap.get("property4")).getClass());

assertThrows(ClassCastException.class, () -> {
    String s = hMap.get(5);
});
assertEquals(Double.class, ((Object) hMap.get(5)).getClass());
```

正如我们所见，我们的转换执行没有任何错误，但并非HashMap中的所有元素都是字符串。所以，即使这种方法看起来最简单，我们也必须在以后牢记一些与安全相关的检查。

## 3.番石榴API

如果我们可以使用第三方库，[Google Guava API](https://www.baeldung.com/guava-guide)就派上用场了。这个库提供了一个静态的[Maps.fromProperties()](https://guava.dev/releases/snapshot-jre/api/docs/com/google/common/collect/Maps.html#fromProperties-java.util.Properties-)方法，它几乎可以为我们做所有事情。根据文档，这个调用返回一个[ImmutableMap](https://www.baeldung.com/java-immutable-maps#guava-immutable-map)，所以如果我们想要HashMap，我们可以使用：

```java
public HashMap<String, String> guavaConvert(Properties prop) {
    return Maps.newHashMap(Maps.fromProperties(prop));
}
```

和以前一样，当我们完全确定Properties仅包含String值时，此方法可以正常工作。 有一些不合格的值会导致意想不到的行为：

```java
properties.put("property4", 456);
assertThrows(NullPointerException.class, 
    () -> PropertiesToHashMapConverter.guavaConvert(properties));

properties.put(5, 10.11);
assertThrows(ClassCastException.class, 
    () -> PropertiesToHashMapConverter.guavaConvert(properties));
```

Guava API 不执行任何额外的映射。结果，它不允许我们转换那些Properties，抛出异常。

在第一种情况下，由于无法通过 Properties检索Integer值而抛出NullPointerException 。getProperty()方法，因此被解释为null。第二个示例抛出与输入属性映射上出现的非字符串键 相关的ClassCastException 。

该解决方案为我们提供了更好的类型控制，并告知我们 在转换过程中发生的违规行为。

## 4.自定义类型安全实现

现在是解决前面示例中的安全问题类型的时候了。正如我们所知，Properties类实现了从Map接口继承的方法。我们将使用[迭代Map](https://www.baeldung.com/java-iterate-map)的一种可能方法来实现适当的解决方案并通过类型检查来丰富它。

### 4.1. 使用for循环迭代

让我们实现一个简单的 for循环：

```java
public HashMap<String, String> loopConvert(Properties prop) {
    HashMap<String, String> retMap = new HashMap<>();
    for (Map.Entry<Object, Object> entry : prop.entrySet()) {
        retMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
    }
    return retMap;
}
```

在此方法中，我们以与典型Map相同的方式遍历Properties。因此，我们可以一对一地访问由[Map.Entry](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.Entry.html)类表示的每个键对值。

在将值放入返回的HashMap之前，我们可以执行额外的检查，因此我们决定使用[String.valueOf()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#valueOf(java.lang.Object))方法。

### 4.2. 流和收集器API

我们甚至可以使用现代Java8 方式重构我们的方法：

```java
public HashMap<String, String> streamConvert(Properties prop) {
    return prop.entrySet().stream().collect(
      Collectors.toMap(
        e -> String.valueOf(e.getKey()),
        e -> String.valueOf(e.getValue()),
        (prev, next) -> next, HashMap::new
    ));
}
```

在这种情况下，我们使用的是[Java 8 流收集器](https://www.baeldung.com/java-collectors-tomap) ，没有显式HashMap构造。此方法实现与上一个示例中介绍的逻辑完全相同的逻辑。

这两种解决方案都稍微复杂一些，因为它们需要一些类型转换和 Guava 示例不需要的自定义实现。

但是，我们可以在将值放入生成的HashMap之前访问这些值，因此我们可以实施额外的检查或映射：

```java
properties.put("property4", 456);
properties.put(5, 10.11);

HashMap<String, String> hMap1 = loopConvert(properties);
HashMap<String, String> hMap2 = streamConvert(properties);

assertDoesNotThrow(() -> {
    String s1 = hMap1.get("property4");
    String s2 = hMap2.get("property4");
});
assertEquals("456", hMap1.get("property4"));
assertEquals("456", hMap2.get("property4"));

assertDoesNotThrow(() -> {
    String s1 = hMap1.get("property4");
    String s2 = hMap2.get("property4");
});
assertEquals("10.11", hMap1.get("5"));
assertEquals("10.11", hMap2.get("5"));

assertEquals(hMap2, hMap1);
```

如我们所见，我们解决了与非字符串值相关的问题。使用这种方法，我们可以手动调整映射逻辑以实现正确的实现。

## 5.总结

在本教程中，我们检查了将java.util.Properties转换为HashMap<String, String>的不同方法。

我们从类型转换解决方案开始，它可能是最快的转换，但也会带来编译器警告和潜在的类型安全错误。

然后我们看了一个使用 Guava API 的解决方案，它解决了编译器警告并为处理错误带来了一些改进。

最后，我们实现了我们的自定义方法，这些方法处理类型安全错误并给予我们最大的控制权。