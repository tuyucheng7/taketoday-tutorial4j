## 1. 简介

在这个简短的教程中，我们将了解如何使用Java 8的Stream API查找数组中的最大值和最小值。

我们将从查找整数数组中的最小值开始，然后我们将查找对象数组中的最大值。

## 2. 概述

有很多方法可以在无序数组中找到最小值或最大值，它们看起来都类似于：

```java
SET MAX to array[0]
FOR i = 1 to array length - 1
    IF array[i] > MAX THEN
        SET MAX to array[i]
    ENDIF
ENDFOR
```

我们将看看**Java 8如何向我们隐藏这些细节**。但是，如果Java的API不适合我们，我们总是可以使用这个基本算法。

因为我们需要检查数组中的每个值，所以所有实现都是O(n)。

## 3. 寻找最小值

**java.util.stream.IntStream接口提供了min方法**，它可以很好地满足我们的目的。

因为我们只处理整数，所以min不需要Comparator：

```java
@Test
void whenArrayIsOfIntegerThenMinUsesIntegerComparator() {
	int[] integers = new int[]{20, 98, 12, 7, 35};
    
	int min = Arrays.stream(integers)
	    .min()
	    .getAsInt();
    
	assertEquals(7, min);
}
```

请注意我们如何使用Arrays中的静态方法stream创建整数流对象，每个原始数组类型都有等效的stream方法。

由于数组可能为空，min返回一个Optional，因此要将其转换为int，我们使用getAsInt。

## 4. 查找最大的自定义对象

让我们创建一个简单的POJO：

```java
public class Car {
    private String model;
    private int topSpeed;

    // standard constructors, getters and setters
}
```

然后我们可以再次使用Stream API在Car数组中找到最快的汽车：

```java
@Test
public void whenArrayIsOfCustomTypeThenMaxUsesCustomComparator() {
	Car porsche = new Car("Porsche 959", 319);
	Car ferrari = new Car("Ferrari 288 GTO", 303);
	Car bugatti = new Car("Bugatti Veyron 16.4 Super Sport", 415);
	Car mcLaren = new Car("McLaren F1", 355);
	Car[] fastCars = { porsche, ferrari, bugatti, mcLaren };
    
	Car maxBySpeed = Arrays.stream(fastCars)
	    .max(Comparator.comparing(Car::getTopSpeed))
	    .orElseThrow(NoSuchElementException::new);
    
	assertEquals(bugatti, maxBySpeed);
}
```

在这种情况下，Arrays的静态方法stream返回**java.util.stream.Stream<T\>接口的实例，其中max方法需要一个Comparator**。

我们可以构建自己的自定义Comparator，但使用[Comparator.comparing更容易](https://www.baeldung.com/java-8-comparator-comparing)。

再次注意max出于与以前相同的原因返回一个Optional实例。

我们可以获取这个值，或者我们可以用Optional做任何其他可能的事情，比如orElseThrow，如果max没有返回值就会抛出异常。

## 5. 总结

在这篇简短的文章中可以看到，使用Java 8的Stream API在数组上找到最大值和最小值是多么容易和简洁。

有关此库的更多信息，请参阅[Oracle文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/package-summary.html)。

