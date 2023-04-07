## 一、概述

使用 Java [*Stream*](https://www.baeldung.com/java-8-streams)生成*Map时*[，](https://www.baeldung.com/java-8-streams)可能会遇到重复的键。这可能会在向地图添加值时导致问题，因为与键关联的先前值可能会被覆盖。

在本教程中，我们将讨论在使用 Stream API生成*Map时如何处理重复键。*

## 二、问题介绍

像往常一样，让我们通过示例来理解问题。假设我们有一个*City*类：

```java
class City {
    private String name;
    private String locatedIn;

    public City(String name, String locatedIn) {
        this.name = name;
        this.locatedIn = locatedIn;
    }
    
    // Omitted getter methods
    // Omitted the equals() and hashCode() methods
    // ...
}复制
```

如上面的类代码所示，*City*是一个具有两个字符串属性的[POJO](https://www.baeldung.com/java-pojo-class)类。一是城市名称。另一个提供了有关城市所在位置的更多信息。

此外，该类重写了[*equals()*和*hashCode()*](https://www.baeldung.com/java-equals-hashcode-contracts)方法。**这两种方法检查\*name\*和\*locatedIn\*属性**。为了简单起见，我们没有将方法的实现放在代码片段中。

*接下来，让我们创建一个City*实例列表：

```java
final List<City> CITY_INPUT = Arrays.asList(
  new City("New York City", "USA"),
  new City("Shanghai", "China"),
  new City("Hamburg", "Germany"),
  new City("Paris", "France"),
  new City("Paris", "Texas, USA"));复制
```

如上面的代码所示，我们[从数组中](https://www.baeldung.com/java-init-list-one-line#create-from-an-array)[初始化一个*List*](https://www.baeldung.com/java-init-list-one-line#create-from-an-array)以与旧的 Java 版本兼容。CITY_INPUT列表包含五个城市*。*让我们关注一下我们打包到列表中的最后两个城市：

-   *新城市（“巴黎”，“法国”）* 
-   *新城市（“巴黎”，“德克萨斯州，美国”）* 

两座城市同名“*巴黎*”。然而，它们不同的*locatedIn*值告诉我们这两个*巴黎*实例是不同的城市。

现在，假设我们要使用城市名称作为*CITY_INPUT列表中的键来生成一个**地图*。显然，这两个*巴黎*城市将拥有相同的钥匙。

接下来，让我们看看如何在使用 Java Stream API 生成地图时处理重复的键。

为简单起见，我们将使用单元测试断言来验证每个解决方案是否生成预期结果。

## 3.使用*groupingBy()* 方法生成*Map<Key, List<Value>>*

处理映射中重复键的一种想法是**使键关联集合中的多个值**，例如*Map<String, List<City>>*。一些流行的库提供了*MultiMap*类型，例如[Guava 的*Multimap*](https://www.baeldung.com/guava-multimap)和[Apache Commons *MultiValuedMap*](https://www.baeldung.com/apache-commons-multi-valued-map)，以更轻松地处理多值映射。

在本教程中，我们将坚持使用标准 Java API。因此，我们将使用[*groupingBy()*](https://www.baeldung.com/java-groupingby-collector)收集器来生成*Map<String, List<City>>*结果，因为**groupingBy \*()\*方法可以按某些属性作为键对对象进行分组并将对象存储在\*Map\*实例**中：

```java
Map<String, List<City>> resultMap = CITY_INPUT.stream()
  .collect(groupingBy(City::getName));

assertEquals(4, resultMap.size());
assertEquals(Arrays.asList(new City("Paris", "France"), new City("Paris", "Texas, USA")),
  resultMap.get("Paris"));复制
```

正如我们在上面的测试中看到的，*groupingBy()*收集器生成的映射结果包含四个条目。此外，两个“ *Paris* ”城市实例被分组在键“ *Paris* ”下。

因此，使用多值映射的方法可以解决键重复问题。但是，此方法返回*Map<String, List<City>>。* **如果我们需要\*Map<String, City>\*作为返回类型，我们就不能再将具有重复键的对象组合在一个集合中。**

那么接下来，我们看看这种情况下如何处理重复的key。

## 4. 使用 *toMap()* 方法和处理重复键

Stream API 提供了**toMap [\*()\*](https://www.baeldung.com/java-collectors-tomap)收集器方法来将流收集到\*Map\***中。

此外，**toMap \*()\*方法允许我们指定一个合并函数，该函数将用于组合与重复键关联的值**。

例如，我们可以使用一个简单的[lambda 表达式](https://www.baeldung.com/java-8-lambda-expressions-tips)来忽略后面的*City*对象，如果它们的名称已经被收集的话：

```java
Map<String, City> resultMap1 = CITY_INPUT.stream()
  .collect(toMap(City::getName, Function.identity(), (first, second) -> first));

assertEquals(4, resultMap1.size());
assertEquals(new City("Paris", "France"), resultMap1.get("Paris"));复制
```

如上测试所示，由于法国的*巴黎*在美国得克萨斯州的*巴黎*之前，因此在输入列表中，生成的地图仅包含法国的城市巴黎。

或者，如果我们希望在出现重复键时始终覆盖映射中的现有条目，我们可以调整 lambda 表达式以返回第二个*City*对象：

```java
Map<String, City> resultMap2 = CITY_INPUT.stream()
  .collect(toMap(City::getName, Function.identity(), (first, second) -> second));

assertEquals(4, resultMap2.size());
assertEquals(new City("Paris", "Texas, USA"), resultMap2.get("Paris"));复制
```

如果我们运行测试，它就会通过。所以，这一次，关键的“*巴黎*”联想到了 美国得克萨斯州的*巴黎。*

当然，在实际项目中，除了简单的跳过或覆盖之外，我们可能还有更复杂的需求。我们总是可以在合并函数中实现所需的合并逻辑。

最后，让我们看另一个例子，将两个“*巴黎*”城市的 *locatedIn*属性合并为一个新的*城市*实例，并将这个新合并的*巴黎*实例放入地图结果中：

```java
Map<String, City> resultMap3 = CITY_INPUT.stream()
  .collect(toMap(City::getName, Function.identity(), (first, second) -> {
      String locations = first.getLocatedIn() + " and " + second.getLocatedIn();
      return new City(first.getName(), locations);
  }));

assertEquals(4, resultMap2.size());
assertEquals(new City("Paris", "France and Texas, USA"), resultMap3.get("Paris"));复制
```

## 5.结论

在本文中，我们探索了两种在使用 Stream API 生成*Map*结果时处理重复键的方法：

-   *groupingBy()* –以*Map<Key, List<Value>>*类型创建一个*Map结果*
-   *mapTo() – 允许我们在**合并*函数中实现合并逻辑 