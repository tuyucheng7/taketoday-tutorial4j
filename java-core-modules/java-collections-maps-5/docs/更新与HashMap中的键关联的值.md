## 1. 概述

本教程将介绍更新与[HashMap](https://www.baeldung.com/java-hashmap)中给定键关联的值的不同方法。首先，我们将查看一些仅使用Java8 之前可用的功能的常见解决方案。然后，我们将查看Java8 及更高版本中可用的一些其他解决方案。

## 2. 初始化我们的示例HashMap

为了展示如何更新HashMap中的值，我们必须先创建并填充一个。因此，我们将创建一个地图，其中水果作为键，价格作为值：

```java
Map<String, Double> priceMap = new HashMap<>();
priceMap.put("apple", 2.45);
priceMap.put("grapes", 1.22);
```

我们将在整个示例中使用此HashMap 。现在，我们已经准备好熟悉更新与HashMap键关联的值的方法。

## 3.Java 8之前

让我们从Java8 之前可用的方法开始。

### 3.1. put方法_

put方法更新值或添加新条目。如果它与已存在的键一起使用，则put方法将更新关联的值。否则，它将添加一个新的(key, value)对。

让我们用两个简单的例子来测试这个方法的行为：

```java
@Test
public void givenFruitMap_whenPuttingAList_thenHashMapUpdatesAndInsertsValues() {
    Double newValue = 2.11;
    fruitMap.put("apple", newValue);
    fruitMap.put("orange", newValue);
    
    Assertions.assertEquals(newValue, fruitMap.get("apple"));
    Assertions.assertTrue(fruitMap.containsKey("orange"));
    Assertions.assertEquals(newValue, fruitMap.get("orange"));
}

```

关键苹果已经在地图上了。因此，第一个断言将通过。

由于地图中不存在橙色， put方法将添加它。因此，其他两个断言也将通过。

### 3.2. containsKey和 put方法的组合

containsKey和 put方法的组合是更新HashMap中键值的另一种方法。此选项检查映射是否已包含键。在这种情况下，我们可以使用put方法更新值。 否则，我们要么向地图添加一个条目，要么什么都不做。

在我们的例子中，我们将通过一个简单的测试来检查这种方法：

```java
@Test
public void givenFruitMap_whenKeyExists_thenValuesUpdated() {
    double newValue = 2.31;
    if (fruitMap.containsKey("apple")) {
        fruitMap.put("apple", newValue);
    }
    
    Assertions.assertEquals(Double.valueOf(newValue), fruitMap.get("apple"));
}
```

由于apple在地图上，containsKey 方法将返回true。因此，将执行对put 方法的调用，并更新值。

## 4.Java 8及以上

从Java8 开始，有许多新方法可以用来简化更新HashMap中键值的过程。那么，让我们来认识一下他们吧。

### 4.1. 替换 方法_

从版本 8 开始， Map接口中提供了两个重载的replace方法。让我们看一下方法签名：

```java
public V replace(K key, V value);
public boolean replace(K key, V oldValue, V newValue);
```

第一个替换方法只需要一个键和一个新值。它还返回旧值。

让我们看看该方法是如何工作的：

```java
@Test
public void givenFruitMap_whenReplacingOldValue_thenNewValueSet() {
    double newPrice = 3.22;
    Double applePrice = fruitMap.get("apple");
    
    Double oldValue = fruitMap.replace("apple", newPrice);
    
    Assertions.assertNotNull(oldValue);
    Assertions.assertEquals(oldValue, applePrice);
    Assertions.assertEquals(Double.valueOf(newPrice), fruitMap.get("apple"));
}
```

键苹果的值将使用替换方法更新为新价格。因此，第二个和第三个断言将通过。

然而，第一个断言很有趣。如果我们的HashMap中没有关键苹果怎么办？如果我们尝试更新不存在的键的值，将返回 null 。考虑到这一点，另一个问题出现了：如果有一个空值的键怎么办？我们不知道从replace方法返回的值是否确实是提供的键的值，或者我们是否尝试更新不存在的键的值。

所以，为了避免误会，我们可以使用第二种replace方法。它需要三个参数：

-   关键
-   与键关联的当前值
-   与键关联的新值

它会在一种情况下将键的值更新为新值： 如果第二个参数是当前值，则键值将更新为新值。该方法返回true表示更新成功。否则，返回false。

因此，让我们实施一些测试来检查第二个替换方法：

```java
@Test
public void givenFruitMap_whenReplacingWithRealOldValue_thenNewValueSet() {
    double newPrice = 3.22;
    Double applePrice = fruitMap.get("apple");
    
    boolean isUpdated = fruitMap.replace("apple", applePrice, newPrice);
    
    Assertions.assertTrue(isUpdated);
}

@Test
public void givenFruitMap_whenReplacingWithWrongOldValue_thenNewValueNotSet() {
    double newPrice = 3.22;
    boolean isUpdated = fruitMap.replace("apple", Double.valueOf(0), newPrice);
    
    Assertions.assertFalse(isUpdated);
}
```

由于第一个测试使用键的当前值调用replace方法，因此该值将被替换。

另一方面，不使用当前值调用第二个测试。因此，返回false。

### 4.2. getOrDefault 和 put方法的组合

如果我们没有提供的键的条目，则getOrDefault方法是一个完美的选择。在这种情况下，我们为不存在的键设置默认值。然后，条目被添加到映射中。使用这种方法，我们可以轻松地逃避NullPointerException。

让我们用一个原本不在地图上的键来试试这个组合：

```java
@Test
public void givenFruitMap_whenGetOrDefaultUsedWithPut_thenNewEntriesAdded() {
    fruitMap.put("plum", fruitMap.getOrDefault("plum", 2.41));
    
    Assertions.assertTrue(fruitMap.containsKey("plum"));
    Assertions.assertEquals(Double.valueOf(2.41), fruitMap.get("plum"));
}
```

由于没有这样的键，getOrDefault方法将返回默认值。然后，put方法将添加一个新的 (key, value) 对。因此，所有断言都会通过。

### 4.3. putIfAbsent方法_

putIfAbsent方法的作用与前面的getOrDefault和put方法的组合相同。

如果HashMap中不存在具有提供的键的对，则putIfAbsent方法将添加该对。但是，如果存在这样的一对，putIfAbsent方法将不会更改地图。

但是，有一个例外：如果现有对具有 空值，则该对将更新为新值。

让我们对putIfAbsent方法进行测试。我们将通过两个示例来测试该行为：

```java
@Test
public void givenFruitMap_whenPutIfAbsentUsed_thenNewEntriesAdded() {
    double newValue = 1.78;
    fruitMap.putIfAbsent("apple", newValue);
    fruitMap.putIfAbsent("pear", newValue);
    
    Assertions.assertTrue(fruitMap.containsKey("pear"));
    Assertions.assertNotEquals(Double.valueOf(newValue), fruitMap.get("apple"));
    Assertions.assertEquals(Double.valueOf(newValue), fruitMap.get("pear"));
}
```

地图上有一个关键的 苹果 。putIfAbsent方法不会更改其当前值。

与此同时，关键的梨从地图上消失了。因此，它将被添加。

### 4.4. 计算方法_

计算方法根据作为第二个参数提供的BiFunction 更新键的[值](https://www.baeldung.com/java-bifunction-interface)。如果键在地图上不存在，我们可以期待一个NullPointerException。

让我们用一个简单的测试来检查这个方法的行为：

```java
@Test
public void givenFruitMap_whenComputeUsed_thenValueUpdated() {
    double oldPrice = fruitMap.get("apple");
    BiFunction<Double, Integer, Double> powFunction = (x1, x2) -> Math.pow(x1, x2);
    
    fruitMap.compute("apple", (k, v) -> powFunction.apply(v, 2));
    
    Assertions.assertEquals(
      Double.valueOf(Math.pow(oldPrice, 2)), fruitMap.get("apple"));
    
    Assertions.assertThrows(
      NullPointerException.class, () -> fruitMap.compute("blueberry", (k, v) -> powFunction.apply(v, 2)));
}
```

正如预期的那样，由于键apple存在，它在地图中的值将被更新。另一方面，没有键blueberry，因此在最后一个断言中第二次调用compute方法将导致NullPointerException。

### 4.5. computeIfAbsent 方法_

如果HashMap中没有特定键的对，则前一个方法会抛出异常。如果地图不存在， computeIfAbsent方法将通过添加(key, value)对来更新地图。

让我们测试一下这个方法的行为：

```java
@Test
public void givenFruitMap_whenComputeIfAbsentUsed_thenNewEntriesAdded() {
    fruitMap.computeIfAbsent("lemon", k -> Double.valueOf(k.length()));
    
    Assertions.assertTrue(fruitMap.containsKey("lemon"));
    Assertions.assertEquals(Double.valueOf("lemon".length()), fruitMap.get("lemon"));
}
```

地图上不存在关键柠檬。因此，computeIfAbsent方法添加了一个条目。

### 4.6. computeIfPresent方法_

如果键存在于HashMap中，则computeIfPresent 方法会更新该键的值。

让我们看看如何使用这个方法：

```java
@Test
public void givenFruitMap_whenComputeIfPresentUsed_thenValuesUpdated() {
    Double oldAppleValue = fruitMap.get("apple");
    BiFunction<Double, Integer, Double> powFunction = (x1, x2) -> Math.pow(x1, x2);
    
    fruitMap.computeIfPresent("apple", (k, v) -> powFunction.apply(v, 2));
    
    Assertions.assertEquals(Double.valueOf(Math.pow(oldAppleValue, 2)), fruitMap.get("apple"));
}
```

断言将通过，因为键 apple在映射中，并且computeIfPresent方法将根据 BiFunction 更新值。

### 4.7. 合并方法_

如果存在这样的键，则合并方法使用BiFunction[更新](https://www.baeldung.com/java-bifunction-interface)HashMap 中键的值。否则，它将添加一个新的(key, value)对，并将值设置为作为该方法的第二个参数提供的值。

那么，让我们检查一下这个方法的行为：

```java
@Test
public void givenFruitMap_whenMergeUsed_thenNewEntriesAdded() {
    double defaultValue = 1.25;
    BiFunction<Double, Integer, Double> powFunction = (x1, x2) -> Math.pow(x1, x2);
    
    fruitMap.merge("apple", defaultValue, (k, v) -> powFunction.apply(v, 2));
    fruitMap.merge("strawberry", defaultValue, (k, v) -> powFunction.apply(v, 2));
    
    Assertions.assertTrue(fruitMap.containsKey("strawberry"));
    Assertions.assertEquals(Double.valueOf(defaultValue), fruitMap.get("strawberry"));
    Assertions.assertEquals(Double.valueOf(Math.pow(defaultValue, 2)), fruitMap.get("apple"));
}
```

测试首先在键apple上执行merge 方法。它已经在地图上，因此它的值将会改变。它将是我们传递给该方法的defaultValue参数的平方。

关键草莓不在地图上。因此，merge方法将以defaultValue作为值添加它。

## 5.总结

在本文中，我们描述了几种更新与HashMap中的键关联的值的方法。

首先，我们从最常见的方法开始。然后，我们展示了自Java8 以来可用的几种方法。