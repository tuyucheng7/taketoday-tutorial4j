## 1. 概述

本教程简要介绍了如何使用Java 8中强大的Stream API从给定列表或集合中查找最小值和最大值。

## 2. 在整数列表中找到最大值

我们可以使用通过java.util.Stream接口提供的max()方法，它接受一个方法引用：

```java
@Test
public void whenListIsOfIntegerThenMaxCanBeDoneUsingIntegerComparator() {
    // given
    List<Integer> listOfIntegers = Arrays.asList(1, 2, 3, 4, 56, 7, 89, 10);
    Integer expectedResult = 89;

    // then
    Integer max = listOfIntegers
      .stream()
      .mapToInt(v -> v)
      .max().orElseThrow(NoSuchElementException::new);

    assertEquals("Should be 89", expectedResult, max);
}
```

让我们仔细看看代码：

1.  调用列表上的stream()方法以从列表中获取值流
2.  在流上调用mapToInt(value -> value)以获取 Integer Stream
3.  在流上调用max()方法以获取最大值
4.  如果没有从max()接收到值，则调用orElseThrow()抛出异常

## 3. 使用自定义对象查找最小值

为了找到自定义对象的最小值/最大值，我们还可以为我们首选的排序逻辑提供一个 lambda 表达式。

让我们首先定义自定义 POJO：

```java
class Person {
    String name;
    Integer age;
      
    // standard constructors, getters and setters
}
```

我们想找到年龄最小的Person对象：

```java
@Test
public void whenListIsOfPersonObjectThenMinCanBeDoneUsingCustomComparatorThroughLambda() {
    // given
    Person alex = new Person("Alex", 23);
    Person john = new Person("John", 40);
    Person peter = new Person("Peter", 32);
    List<Person> people = Arrays.asList(alex, john, peter);

    // then
    Person minByAge = people
      .stream()
      .min(Comparator.comparing(Person::getAge))
      .orElseThrow(NoSuchElementException::new);

    assertEquals("Should be Alex", alex, minByAge);
}
```

我们来看看这个逻辑：

1.  调用列表上的stream()方法以从列表中获取值流
2.  在流上调用min()方法以获得最小值。我们传递一个 lambda 函数作为比较器，这用于决定决定最小值的排序逻辑。
3.  如果没有从min()接收到值，则调用orElseThrow()抛出异常

## 4。总结

在这篇快速文章中，我们探讨了如何使用Java 8 Stream API 中的max()和min()方法从List或Collection中查找最大值和最小值。