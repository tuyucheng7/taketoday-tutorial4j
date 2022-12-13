## 1. 简介

在本快速教程中，我们介绍使用[Stream API]()计算整数总和的各种方法。

为了简单起见，我们在示例中使用整数；但是，我们也可以将相同的方法应用于long和double值。

## 2. 使用Stream.reduce()

**Stream.reduce()是对Stream的元素执行归约的终端操作**。

它对流中的每个元素应用一个二元运算符(累加器)，其中第一个操作数是前一个应用的返回值，第二个操作数是当前流元素。

在使用reduce()方法的第一种方法中，累加器函数是一个lambda表达式，它将两个Integer值相加并返回一个Integer值：

```java
List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
Integer sum = integers.stream()
	.reduce(0, (a, b) -> a + b);
```

同样，我们可以使用方法引用一个已经存在的Java方法：

```java
List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
Integer sum = integers.stream()
	.reduce(0, Integer::sum);
```

或者我们可以定义并使用我们的自定义方法：

```java
public class ArithmeticUtils {

	public static int add(int a, int b) {
		return a + b;
	}
}
```

然后我们可以将此函数作为参数传递给reduce()方法：

```java
List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
Integer sum = integers.stream()
	.reduce(0, ArithmeticUtils::add);
```

## 3. 使用Stream.collect()

计算整数集合总和的第二种方法是使用[collect()]()终端操作：

```java
List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
Integer sum = integers.stream()
	.collect(Collectors.summingInt(Integer::intValue));
```

类似地，Collectors类提供了summingLong()和summingDouble()方法来分别计算long和double的总和。

## 4. 使用IntStream.sum()

Stream API为我们提供了mapToInt()中间操作，它将我们的流转换为IntStream对象。

此方法将映射器作为参数，用于进行转换，然后我们可以调用sum()方法来计算流元素的总和。

让我们看一个如何使用它的简单示例：

```java
List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
Integer sum = integers.stream()
	.mapToInt(Integer::intValue)
    .sum();
```

以同样的方式，我们可以使用mapToLong()和mapToDouble()方法分别计算long和double的总和。

## 5. 将Stream#sum与Map结合使用

要计算Map<Object, Integer>数据结构的值之和，首先我们从该Map的值创建一个流。接下来，我们应用我们之前使用的方法之一。

例如，通过使用IntStream.sum()：

```java
Integer sum = map.values()
    .stream()
    .mapToInt(Integer::valueOf)
    .sum();
```

## 6. 将Stream#sum与对象一起使用

假设我们有一个对象集合，并且我们想要计算这些对象的给定字段的所有值的总和。

例如：

```java
public class Item {

	private int id;
	private Integer price;

	public Item(int id, Integer price) {
		this.id = id;
		this.price = price;
	}

	// Standard getters and setters
}
```

接下来假设我们要计算以下集合中所有Item的price总和：

```java
Item item1 = new Item(1, 10);
Item item2 = new Item(2, 15);
Item item3 = new Item(3, 25);
Item item4 = new Item(4, 40);
        
List<Item> items = Arrays.asList(item1, item2, item3, item4);
```

在这种情况下，为了使用前面示例中使用的方法计算总和，我们需要调用map()方法将我们的流转换为整数流。

因此，我们可以使用Stream.reduce()、Stream.collect()和IntStream.sum()来计算总和：

```java
Integer sum = items.stream()
    .map(x -> x.getPrice())
    .reduce(0, ArithmeticUtils::add);
```

```java
Integer sum = items.stream()
    .map(x -> x.getPrice())
    .reduce(0, Integer::sum);
```

```java
Integer sum = items.stream()
    .map(item -> item.getPrice())
    .reduce(0, (a, b) -> a + b);
```

```java
Integer sum = items.stream()
    .map(x -> x.getPrice())
    .collect(Collectors.summingInt(Integer::intValue));
```

```java
items.stream()
    .mapToInt(x -> x.getPrice())
    .sum();
```

## 7. 将Stream#sum与字符串一起使用

假设我们有一个包含一些整数的String对象。

要计算这些整数的总和，首先我们需要将该字符串转换为数组。接下来我们需要过滤掉非整数元素，最后，将该数组的剩余元素转换为数字。

让我们看看所有这些步骤的实际应用：

```java
String string = "Item1 10 Item2 25 Item3 30 Item4 45";

Integer sum = Arrays.stream(string.split(" "))
    .filter((s) -> s.matches("d+"))
    .mapToInt(Integer::valueOf)
    .sum();
```

## 8. 总结

在本文中，我们讨论了如何使用Stream API计算整数集合总和的几种方法。我们还使用这些方法来计算对象集合的给定字段的值总和、Map值的总和以及给定String对象中的数字。