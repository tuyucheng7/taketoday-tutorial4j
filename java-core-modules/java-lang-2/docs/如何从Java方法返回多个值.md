## 1. 概述

在本教程中，我们将学习从Java方法返回多个值的不同方法。

首先，我们将返回数组和集合。然后我们将演示如何将容器类用于复杂数据，并学习如何创建通用元组类。

最后，我们将说明如何使用第三方库返回多个值。

## 2. 使用数组

数组可用于返回原始数据类型和引用数据类型。

例如，以下getCoordinates方法返回一个包含两个双精度值的数组：

```java
double[] getCoordinatesDoubleArray() {
  
    double[] coordinates = new double[2];

    coordinates[0] = 10;
    coordinates[1] = 12.5;
  
    return coordinates;
}
```

如果我们想返回一个不同引用类型的数组，我们可以使用一个共同的父类型作为数组的类型：

```java
Number[] getCoordinatesNumberArray() {
  
    Number[] coordinates = new Number[2];

    coordinates[0] = 10;   // Integer
    coordinates[1] = 12.5; // Double
  
    return coordinates;
}
```

这里我们定义了Number类型的坐标数组，因为它是Integer和Double元素之间的公共类。

## 3.使用集合

使用通用[Java 集合](https://www.baeldung.com/java-collections)，我们可以返回一个通用类型的多个值。

集合框架具有广泛的类和接口。但是，在本节中，我们将只讨论List和Map接口。

### 3.1. 返回列表中相似类型的值

首先，让我们使用List<Number>重写前面的数组示例：

```java
List<Number> getCoordinatesList() {
  
    List<Number> coordinates = new ArrayList<>();
  
    coordinates.add(10);  // Integer
    coordinates.add(12.5);  // Double
  
    return coordinates;
}
```

与Number[]一样，List<Number>集合包含一系列具有相同通用类型的混合类型元素。

### 3.2. 返回映射中的命名值

如果我们想命名我们集合中的每个条目，可以使用Map代替：

```java
Map<String, Number> getCoordinatesMap() {
  
    Map<String, Number> coordinates = new HashMap<>();
  
    coordinates.put("longitude", 10);
    coordinates.put("latitude", 12.5);
  
    return coordinates;
}
```

getCoordinatesMap方法的用户可以通过Map#get方法使用“经度”或“纬度” 键来检索相应的值。

## 4. 使用容器类

与数组和集合不同，容器类 (POJO) 可以包装具有不同数据类型的多个字段。

例如，以下Coordinates类有两种不同的数据类型，double和String：

```java
public class Coordinates {
  
    private double longitude;
    private double latitude;
    private String placeName;
  
    public Coordinates(double longitude, double latitude, String placeName) {
  
        this.longitude = longitude;
        this.latitude = latitude;
        this.placeName = placeName;
    }
  
    // getters and setters
}
```

使用像Coordinates这样的容器类使我们能够为具有有意义名称的复杂数据类型建模。

下一步是实例化并返回Coordinates的实例：

```java
Coordinates getCoordinates() {
  
    double longitude = 10;
    double latitude = 12.5;
    String placeName = "home";
  
    return new Coordinates(longitude, latitude, placeName);
}
```

建议我们使像Coordinates这样的数据类 [不可变](https://www.baeldung.com/java-immutable-object)。通过这样做，我们创建了简单的、线程安全的、可共享的对象。

## 5. 使用元组

与容器一样，元组存储不同类型的字段。但是，它们的不同之处在于它们不是特定于应用程序的。

当我们使用它们来描述我们希望它们处理的类型时，它们是专门的，但也可以充当一定数量的值的通用容器。这意味着我们不需要编写自定义代码来拥有它们，我们可以使用一个库，或者创建一个通用的单一实现。

元组可以有任意数量的字段，通常称为元组n，其中 n 是字段数。例如，Tuple2 是一个二域元组，Tuple3 是一个三域元组，等等。

为了说明元组的重要性，让我们考虑以下示例。假设我们想要找到Coordinates点与List<Coordinates>中所有其他点之间的距离。然后我们需要返回最远的 Coordinate 对象，以及距离。

让我们首先创建一个通用的双字段元组：

```java
public class Tuple2<K, V> {

    private K first;
    private V second;
  
    public Tuple2(K first, V second){
        this.first = first;
        this.second = second;
    }

    // getters and setters
}
```

接下来，让我们实现我们的逻辑并使用Tuple2<Coordinates, Double>实例来包装结果：

```java
Tuple2<Coordinates, Double> getMostDistantPoint(List<Coordinates> coordinatesList, 
                                                       Coordinates target) {

    return coordinatesList.stream()
      .map(coor -> new Tuple2<>(coor, coor.calculateDistance(target)))
      .max((d1, d2) -> Double.compare(d1.getSecond(), d2.getSecond())) // compare distances
      .get();
}
```

在前面的示例中使用Tuple2<Coordinates, Double>使我们免于创建单独的容器类来一次性使用此特定方法。

像容器一样，元组应该是不可变的。此外，由于它们的通用性质，我们应该在内部使用元组，而不是作为我们公共 API 的一部分。

## 6. 第三方库

一些第三方库已经实现了不可变的Pair或Triple类型。[Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)和[javatuples](http://www.javatuples.org/index.html)是最好的例子。一旦我们将这些库作为应用程序中的依赖项，我们就可以直接使用库提供的Pair或Triple类型，而不用自己创建它们。

让我们看一个使用 Apache Commons Lang 返回Pair或Triple对象的示例。

首先，让我们在pom.xml中添加[commons-lang3](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

### 6.1. 来自 Apache Commons Lang的 ImmutablePair

Apache Commons Lang 中的[ImmutablePair](https://commons.apache.org/proper/commons-lang/javadocs/api-3.9/org/apache/commons/lang3/tuple/ImmutablePair.html)类型正是我们想要的：一种使用简单的不可变类型。

它包含两个字段，left和right。让我们看看如何让我们的getMostDistantPoint方法返回一个ImmutablePair类型的对象：

```java
ImmutablePair<Coordinates, Double> getMostDistantPoint(
  List<Coordinates> coordinatesList, Coordinates target) {
    return coordinatesList.stream()
      .map(coordinates -> ImmutablePair.of(coordinates, coordinates.calculateDistance(target)))
      .max(Comparator.comparingDouble(Pair::getRight))
      .get();
}
```

### 6.2. 来自 Apache Commons Lang的 ImmutableTriple

[ImmutableTriple](https://commons.apache.org/proper/commons-lang/javadocs/api-3.9/org/apache/commons/lang3/tuple/ImmutableTriple.html)与ImmutablePair非常相似。唯一的区别是，顾名思义，ImmutableTriple包含三个字段：left、middle和right。

现在让我们在坐标计算中添加一个新方法来展示如何使用ImmutableTriple类型。

我们将遍历List<Coordinates>中的所有点，以找出到给定目标点的最小、平均和最大距离。

让我们看看如何使用ImmutableTriple类通过一个方法返回三个值：

```java
ImmutableTriple<Double, Double, Double> getMinAvgMaxTriple(
  List<Coordinates> coordinatesList, Coordinates target) {
    List<Double> distanceList = coordinatesList.stream()
      .map(coordinates -> coordinates.calculateDistance(target))
      .collect(Collectors.toList());
    Double minDistance = distanceList.stream().mapToDouble(Double::doubleValue).min().getAsDouble();
    Double avgDistance = distanceList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0D);
    Double maxDistance = distanceList.stream().mapToDouble(Double::doubleValue).max().getAsDouble();

    return ImmutableTriple.of(minDistance, avgDistance, maxDistance);
}
```

## 七、总结

在本文中，我们学习了如何使用数组、集合、容器和元组从一个方法返回多个值。我们可以在简单的情况下使用数组和集合，因为它们包装了一种数据类型。

相反，容器和元组在创建复杂类型时很有用，容器提供更好的可读性。

我们还了解到一些第三方库已经实现了对和三元组类型，并举例说明了 Apache Commons Lang 库中的一些示例。