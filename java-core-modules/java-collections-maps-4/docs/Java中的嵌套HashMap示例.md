## 1. 概述

在本教程中，我们将了解如何在 Java中处理嵌套的[HashMap 。](https://www.baeldung.com/java-hashmap)我们还将了解如何创建和比较它们。最后，我们还将了解如何在内部地图中删除和添加记录。

## 2.用例

嵌套HashMap非常有助于存储 JSON 或类似 JSON 的结构，其中对象相互嵌入。例如，结构或 JSON 类似于：

```json
{
    "type": "donut",
    "batters":
    {
        “batter”:
        [
            { "id": "1001", "type": "Regular" },
            { "id": "1002", "type": "Chocolate" },
            { "id": "1003", "type": "Blueberry" },
            { "id": "1004", "type": "Devil's Food" }
        ]
    }
} 
```

是嵌套HashMap的完美候选者。一般来说，只要我们需要将一个对象嵌入到另一个对象中，我们就可以使用它们。

## 3.创建一个 HashMap

有多种[创建HashMap](https://www.baeldung.com/java-initialize-hashmap)的方法，例如手动构建映射或使用[Streams](https://www.baeldung.com/java-streams)和分组函数。[Map](https://www.baeldung.com/java-hashmap)结构既可以是原始类型，也可以是[Objects](https://www.baeldung.com/java-classes-objects)。

### 3.1. 使用put ()方法

我们可以通过手动创建内部映射然后使用 put 方法将它们插入外部映射来构建嵌套的HashMap ：

```java
public Map<Integer, String> buildInnerMap(List<String> batterList) {
     Map<Integer, String> innerBatterMap = new HashMap<Integer, String>();
     int index = 1;
     for (String item : batterList) {
         innerBatterMap.put(index, item);
         index++;
     }
     return innerBatterMap;
}

```

我们可以测试它：

```java
assertThat(mUtil.buildInnerMap(batterList), is(notNullValue()));
Assert.assertEquals(actualBakedGoodsMap.keySet().size(), 2);
Assert.assertThat(actualBakedGoodsMap, IsMapContaining.hasValue(equalTo(mUtil.buildInnerMap(batterList))));
```

### 3.2. 使用流

如果我们有一个要转换为Map的List，我们可以创建一个流，然后使用[Collectors.toMap](https://www.baeldung.com/java-collectors-tomap)方法将其转换为Map 。在这里，我们有两个例子：一个有一个内部Map of Strings，另一个是一个有Integer和Object值的Map 。

在第一个示例中，Employee中嵌套 了Address对象。然后我们构建一个嵌套的HashMap：

```java
Map<Integer, Map<String, String>> employeeAddressMap = listEmployee.stream()
  .collect(Collectors.groupingBy(e -> e.getAddress().getAddressId(),
    Collectors.toMap(f -> f.getAddress().getAddressLocation(), Employee::getEmployeeName)));
return employeeAddressMap;
```

在第二个示例中，我们正在构建一个类型为<Employee id <Address id, Address object>>的对象：

```java
Map<Integer, Map<Integer, Address>> employeeMap = new HashMap<>();
employeeMap = listEmployee.stream().collect(Collectors.groupingBy((Employee emp) -> emp.getEmployeeId(),
  Collectors.toMap((Employee emp) -> emp.getAddress().getAddressId(), fEmpObj -> fEmpObj.getAddress())));
return employeeMap;
```

## 4. 遍历嵌套的HashMap

遍历嵌套的Hashmap与遍历常规或未嵌套的HashMap没有什么不同。嵌套和常规Map之间的唯一区别是嵌套HashMap的值是 Map 类型：

```java
for (Map.Entry<String, Map<Integer, String>> outerBakedGoodsMapEntrySet : outerBakedGoodsMap.entrySet()) {
    Map<Integer, String> valueMap = outerBakedGoodsMapEntrySet.getValue();
    System.out.println(valueMap.entrySet());
}

for (Map.Entry<Integer, Map<String, String>> employeeEntrySet : employeeAddressMap.entrySet()) {
    Map<String, String> valueMap = employeeEntrySet.getValue();
    System.out.println(valueMap.entrySet());
}
```

## 5. 比较嵌套的HashMap

在Java中有很多方法可以比较HashMap 。我们可以使用equals()方法比较它们。默认实现比较每个值。

如果我们更改内部 Map 的内容，相等性检查将失败。对于自定义对象，如果每次内部对象都是新实例，相等性检查也会失败。同样，如果我们更改外部Map的内容，相等性检查也会失败：

```java
assertNotEquals(outerBakedGoodsMap2, actualBakedGoodsMap);

outerBakedGoodsMap3.put("Donut", mUtil.buildInnerMap(batterList));
assertNotEquals(outerBakedGoodsMap2, actualBakedGoodsMap);

Map<Integer, Map<String, String>> employeeAddressMap1 = mUtil.createNestedMapfromStream(listEmployee);
assertNotEquals(employeeAddressMap1, actualEmployeeAddressMap);
```

对于以自定义对象为值的Map ，我们需要使用[比较HashMap一文](https://www.baeldung.com/java-compare-hashmaps)中提到的方法之一自定义相等方法。否则，检查将失败：

```java
//Comparing a Map<Integer, Map<String, String>> and Map<Integer, Map<Integer, Address>> map
assertNotSame(employeeMap1, actualEmployeeMap);
assertNotEquals(employeeMap1, actualEmployeeMap);
Map<Integer, Map<Integer, Address>> expectedMap = setupAddressObjectMap();
assertNotSame(expectedMap, actualEmployeeMap);
assertNotEquals(expectedMap, actualEmployeeMap);
```

如果两个映射相同，则相等性检查成功。对于用户定义的映射，如果所有相同的对象都移动到另一个映射中，则相等性检查成功：

```java
Map<String, Map<Integer, String>> outerBakedGoodsMap4 = new HashMap<>();
outerBakedGoodsMap4.putAll(actualBakedGoodsMap);
assertEquals(actualBakedGoodsMap, outerBakedGoodsMap4);
Map<Integer, Map<Integer, Address>> employeeMap1 = new HashMap<>();
employeeMap1.putAll(actualEmployeeMap);
assertEquals(actualEmployeeMap, employeeMap1);
```

## 6. 向嵌套的HashMap添加元素

要将元素添加到嵌套HashMap的内部Map中，我们首先必须检索它。我们可以使用get()方法检索内部对象。然后我们可以在内部Map对象上使用put()方法并插入新值：

```java
assertEquals(actualBakedGoodsMap.get("Cake").size(), 5);
actualBakedGoodsMap.get("Cake").put(6, "Cranberry");
assertEquals(actualBakedGoodsMap.get("Cake").size(), 6);
```

如果我们必须向外部Map添加一个条目，我们还需要为内部Map提供正确的条目：

```java
outerBakedGoodsMap.put("Eclair", new HashMap<Integer, String>() {
    {
        put(1, "Dark Chocolate");
    }
});
```

## 7. 从嵌套的HashMap中删除记录

要从内部Map中删除记录，首先，我们需要检索它，然后使用remove()方法将其删除。如果内部Map中只有一个值，则保留一个空对象作为值：

```java
assertNotEquals(actualBakedGoodsMap.get("Cake").get(5), null);
actualBakedGoodsMap.get("Cake").remove(5);
assertEquals(actualBakedGoodsMap.get("Cake").get(5), null);
assertNotEquals(actualBakedGoodsMap.get("Eclair").get(1), null);
actualBakedGoodsMap.get("Eclair").remove(1);
assertEquals(actualBakedGoodsMap.get("Eclair").get(1), null);
actualBakedGoodsMap.put("Eclair", new HashMap<Integer, String>() {
    {
        put(1, "Dark Chocolate");
    }
});
```

如果我们从外部Map中删除一条记录，Java 会同时删除内部和外部Map记录，这是显而易见的，因为内部Map是外部Map的“值” ：

```java
assertNotEquals(actualBakedGoodsMap.get("Eclair"), null);
actualBakedGoodsMap.remove("Eclair");
assertEquals(actualBakedGoodsMap.get("Eclair"), null);
```

## 8. 展平嵌套的HashMap

嵌套HashMap的一种替代方法是使用组合键。组合键通常将嵌套结构中的两个键与中间的点连接起来。例如，组合键将是Donut.1、Donut.2等等。我们可以“扁平化”，即从嵌套的Map结构转换为单个Map结构：

```java
var flattenedBakedGoodsMap = mUtil.flattenMap(actualBakedGoodsMap);
assertThat(flattenedBakedGoodsMap, IsMapContaining.hasKey("Donut.2"));
var flattenedEmployeeAddressMap = mUtil.flattenMap(actualEmployeeAddressMap);
assertThat(flattenedEmployeeAddressMap, IsMapContaining.hasKey("200.Bag End"));
```

组合键方法克服了嵌套HashMap带来的额外内存存储缺点。然而，组合键方法在缩放方面不是很好。

## 9.总结

在本文中，我们了解了如何创建、比较、更新和展平嵌套的HashMap。