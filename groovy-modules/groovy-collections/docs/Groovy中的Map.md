## 一、概述

[Groovy](https://www.baeldung.com/groovy-language) 扩展了[Java中的](https://www.baeldung.com/category/java/)[Map](https://www.baeldung.com/java-collections) API以提供过滤、搜索和排序等操作的方法。它还提供了多种创建和操作地图的速记方式。 

在本教程中，我们将了解使用地图的 Groovy 方式。

## 2.创建 Groovy Map

我们可以使用地图文字语法[k:v]来创建地图。基本上，它允许我们实例化一个映射并在一行中定义条目。

可以使用以下方法创建空地图：

```groovy
def emptyMap = [:]
```

同样，可以使用以下方法实例化具有值的映射：

```groovy
def map = [name: "Jerry", age: 42, city: "New York"]
```

请注意，键没有用引号引起来，并且默认情况下，Groovy 创建[java.util.LinkedHashMap](https://www.baeldung.com/java-linked-hashmap)的一个实例。我们可以使用as运算符覆盖此默认行为。

## 3.添加项目

让我们从定义地图开始：

```groovy
def map = [name:"Jerry"]
```

我们可以向地图添加一个键：

```java
map["age"] = 42
```

然而，另一种更像 Javascript 的方法是使用属性符号(点运算符)：

```groovy
map.city = "New York"
```

换句话说，Groovy 支持以类似 bean 的方式访问键值对。

在向地图添加新项目时，我们还可以使用变量而不是文字作为键：

```groovy
def hobbyLiteral = "hobby"
def hobbyMap = [(hobbyLiteral): "Singing"]
map.putAll(hobbyMap)
assertTrue(hobbyMap.hobby == "Singing")
assertTrue(hobbyMap[hobbyLiteral] == "Singing")
```

首先，我们必须创建一个新变量来存储关键爱好。然后我们使用这个括在括号中的变量和地图文字语法来创建另一个地图。

## 4. 取回物品

文字语法或属性符号可用于从地图中获取项目。

对于定义为：

```groovy
def map = [name:"Jerry", age: 42, city: "New York", hobby:"Singing"]
```

我们可以得到键名对应的值：

```groovy
assertTrue(map["name"] == "Jerry")
```

或者

```groovy
assertTrue(map.name == "Jerry")
```

## 5. 移除物品

我们可以使用remove()方法根据键从映射中删除任何条目，但有时我们可能需要从映射中删除多个条目。我们可以使用minus()方法来做到这一点。

minus()方法接受一个Map并在从基础 map 中删除给定 map 的所有条目后 返回一个新Map ：

```groovy
def map = [1:20, a:30, 2:42, 4:34, ba:67, 6:39, 7:49]

def minusMap = map.minus([2:42, 4:34]);
assertTrue(minusMap == [1:20, a:30, ba:67, 6:39, 7:49])
```

接下来，我们还可以根据条件删除条目。我们可以使用removeAll()方法来实现：

```groovy
minusMap.removeAll{it -> it.key instanceof String}
assertTrue(minusMap == [1:20, 6:39, 7:49])
```

相反，要保留满足条件的所有条目，我们可以使用retainAll()方法：

```groovy
minusMap.retainAll{it -> it.value % 2 == 0}
assertTrue(minusMap == [1:20])
```

## 6.遍历条目

我们可以使用each() 和eachWithIndex()方法[遍历条目](https://www.baeldung.com/groovy-map-iterating)。

each()方法提供隐式参数，如entry、key和 value ，它们对应于当前Entry。

除了Entry之外，eachWithIndex()方法还提供了一个索引。这两种方法都接受[闭包](https://www.baeldung.com/groovy-closures)作为参数。

在下一个示例中，我们将遍历每个条目。传递给each()方法的闭包从隐式参数条目中获取键值对并打印它：

```groovy
map.each{entry -> println "$entry.key: $entry.value"}
```

接下来，我们将使用eachWithIndex()方法打印当前索引以及其他值：

```groovy
map.eachWithIndex{entry, i -> println "$i $entry.key: $entry.value"}
```

也可以要求单独提供key、value和 index ：

```groovy
map.eachWithIndex{key, value, i -> println "$i $key: $value"}
```

## 7.过滤

我们可以使用find()、findAll()和grep()方法根据键和值过滤和搜索映射条目。

让我们首先定义一个映射来执行这些方法：

```groovy
def map = [name:"Jerry", age: 42, city: "New York", hobby:"Singing"]
```

首先，我们来看看find()方法，它接受一个闭包并返回第一个匹配闭包条件的条目：

```groovy
assertTrue(map.find{it.value == "New York"}.key == "city")
```

类似地，findAll 也接受一个Closure， 但返回一个Map ，其中包含满足Closure中条件的所有键值对：

```groovy
assertTrue(map.findAll{it.value == "New York"} == [city : "New York"])
```

如果我们更喜欢使用List，我们可以使用 grep而不是 findAll：

```groovy
map.grep{it.value == "New York"}.each{it -> assertTrue(it.key == "city" && it.value == "New York")}
```

我们首先使用 grep 查找具有值 New York 的条目。然后，为了证明返回类型是List，我们将遍历grep() 的结果。对于隐式参数中可用的列表中的每个条目，我们将检查其是否为预期结果。

接下来，要确定地图中的所有项目是否都满足条件，我们可以使用every， 它返回一个布尔值。

让我们检查映射中的所有值是否都是String类型：

```groovy
assertTrue(map.every{it -> it.value instanceof String} == false)
```

类似地，我们可以使用any来确定地图中的任何项目是否符合条件：

```groovy
assertTrue(map.any{it -> it.value instanceof String} == true)
```

## 8. 转化与收集

有时，我们可能希望将映射中的条目转换为新值。使用collect()和collectEntries()方法，可以分别将条目转换并收集到Collection或Map 中。

让我们看一些例子。给定员工 ID 和员工的地图：

```groovy
def map = [
  1: [name:"Jerry", age: 42, city: "New York"],
  2: [name:"Long", age: 25, city: "New York"],
  3: [name:"Dustin", age: 29, city: "New York"],
  4: [name:"Dustin", age: 34, city: "New York"]]
```

我们可以使用collect()将所有员工的姓名收集到一个列表中：

```groovy
def names = map.collect{entry -> entry.value.name}
assertTrue(names == ["Jerry", "Long", "Dustin", "Dustin"])
```

然后，如果我们对一组唯一的名称感兴趣，我们可以通过传递一个Collection对象来指定集合：

```groovy
def uniqueNames = map.collect([] as HashSet){entry -> entry.value.name}
assertTrue(uniqueNames == ["Jerry", "Long", "Dustin"] as Set)
```

如果我们想将映射中的员工姓名从小写更改为大写，我们可以使用collectEntries。此方法返回转换值的映射：

```groovy
def idNames = map.collectEntries{key, value -> [key, value.name]}
assertTrue(idNames == [1:"Jerry", 2:"Long", 3:"Dustin", 4:"Dustin"])
```

最后，还可以将collect方法与find和findAll方法结合使用来转换过滤结果：

```groovy
def below30Names = map.findAll{it.value.age < 30}.collect{key, value -> value.name}
assertTrue(below30Names == ["Long", "Dustin"])
```

在这里，我们将找到 20-30 岁之间的所有员工，并将他们收集到地图中。

## 9.分组

有时，我们可能希望根据条件将地图的某些项目分组为子地图。

groupBy ()方法返回地图的地图，每个地图包含键值对，这些键值对在给定条件下计算出相同的结果：

```groovy
def map = [1:20, 2: 40, 3: 11, 4: 93]
     
def subMap = map.groupBy{it.value % 2}
assertTrue(subMap == [0:[1:20, 2:40], 1:[3:11, 4:93]])
```

另一种创建子图的方法是使用subMap()。它与groupBy()的不同之处在于它只允许基于键进行分组：

```groovy
def keySubMap = map.subMap([1,2])
assertTrue(keySubMap == [1:20, 2:40])
```

在这种情况下，键 1 和 2 的条目将在新映射中返回，所有其他条目将被丢弃。

## 10.排序

通常在排序时，我们可能希望根据键或值或两者对映射中的条目进行排序。Groovy 提供了一个可用于此目的的sort()方法。

给定一张地图：

```groovy
def map = [ab:20, a: 40, cb: 11, ba: 93]
```

如果需要对键进行排序，我们将使用基于自然排序的无参数sort()方法：

```groovy
def naturallyOrderedMap = map.sort()
assertTrue([a:40, ab:20, ba:93, cb:11] == naturallyOrderedMap)
```

或者我们可以使用sort(Comparator)方法来提供比较逻辑：

```groovy
def compSortedMap = map.sort({k1, k2 -> k1 <=> k2} as Comparator)
assertTrue([a:40, ab:20, ba:93, cb:11] == compSortedMap)
```

接下来，要对键、值或两者进行排序，我们可以为sort()提供闭包条件：

```groovy
def cloSortedMap = map.sort({it1, it2 -> it1.value <=> it1.value})
assertTrue([cb:11, ab:20, a:40, ba:93] == cloSortedMap)
```

## 11.总结

在本文中，我们学习了如何在 Groovy中创建Map 。然后我们研究了从地图中添加、检索和删除项目的不同方式。

最后，我们介绍了 Groovy 开箱即用的方法来执行常见操作，例如过滤、搜索、转换和排序。