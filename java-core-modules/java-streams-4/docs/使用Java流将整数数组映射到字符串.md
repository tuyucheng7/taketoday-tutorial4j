## 一、简介

在本教程中，我们将了解如何使用[Java Streams将](https://www.baeldung.com/java-streams)*Integer*数组转换为*String*数组。

*我们将根据我们是否拥有Integer*数组或原始*int*值来比较我们需要采取的方法。对于*Integer* s，我们将利用*Stream<Integer>和**Integer*从*Object*继承的方法进行转换。对于*int*，我们将使用专门的*[IntStream](https://www.baeldung.com/java-intstream-convert)*。

## 2.从数组创建*流*

让我们从将数组转换为*Stream*开始。*我们可以在这里对Integer*和原始整数使用相同的方法，但返回类型会不同。如果我们有一个*Integer*数组，我们将得到一个*Stream<Integer>*：

```java
Integer[] integerArray = { 1, 2, 3, 4, 5 };
Stream<Integer> integerStream = Arrays.stream(integerArray);复制
```

如果我们改为从原始整数数组开始，我们将得到一个*IntStream*：

```java
int[] intArray = { 1, 2, 3, 4, 5 };
IntStream intStream = Arrays.stream(intArray);
复制
```

*IntStream*为我们提供了一组我们可以使用的方法，稍后我们将使用这些方法进行类型转换。

## 3.从*整数*转换

*准备好Stream<Integer>*后，我们可以继续将*Integer*转换为*String*。**由于\*Integer使我们可以访问\**Object\*的所有方法，因此我们可以将\*Object.toString()\*与\*map()\***一起使用：

```java
String[] convertToStringArray(Integer[] input) {
    return Arrays.stream(input)
      .map(Object::toString)
      .toArray(String[]::new);
}复制
```

然后让我们使用*convertToStringArray()来转换**Integer*数组并确认返回的*String*数组如我们所期望的那样出现：

```java
@Test
void whenConvertingIntegers_thenHandleStreamOfIntegers() {
    Integer[] integerNumbers = { 1, 2, 3, 4, 5 };
    String[] expectedOutput = { "1", "2", "3", "4", "5" };

    String[] strings = convertToStringArray(integerNumbers);

    Assert.assertArrayEquals(expectedOutput, strings);
}复制
```

## 4. 从原始整数转换

现在让我们看看如何处理以整数数组开头的*IntStream 。*

### 4.1. 返回数组

***准备好IntStream\*后，我们可以使用\*IntStream.mapToObj()\*进行转换**：

```java
String[] convertToStringArray(int[] input) {
    return Arrays.stream(input)
      .mapToObj(Integer::toString)
      .toArray(String[]::new);
}复制
```

mapToObj *()*方法使用我们提供的*Integer.toString()*方法返回一个对象值*Stream 。*因此，在我们方法的那个阶段之后，我们有一个*Stream<String>*可以使用，我们可以简单地使用*toArray()*收集内容。

然后我们可以再次检查使用*convertToStringArray()*是否为我们提供了一个匹配输入整数数组的*String数组：*

```java
@Test
void whenConvertingInts_thenHandleIntStream() {
    int[] intNumbers = { 1, 2, 3, 4, 5 };
    String[] expectedOutput = { "1", "2", "3", "4", "5" };

    String[] strings = convertToStringArray(intNumbers);

    Assert.assertArrayEquals(expectedOutput, strings);
}复制
```

此外，如果我们想在中途使用*Integer*类型带来的任何好处，我们可以使用*boxed()*：

```java
String[] convertToStringArrayWithBoxing(int[] input) {
    return Arrays.stream(input)
      .boxed()
      .map(Object::toString)
      .toArray(String[]::new);
}复制
```

### 4.2. 返回单个*字符串*

另一个潜在的用例是将我们的整数数组转换为单个*String*。我们可以重用上面的大部分代码以及[*Stream.collect()*](https://www.baeldung.com/java-8-collectors)*以将Stream*简化为*String*。collect *()*方法用途广泛，可以让我们将*Stream*终止为多种类型。在这里，我们将传递给它*Collectors.joining(“, “)*这样数组中的每个元素都将连接成一个*字符串*，它们之间有一个逗号：

```java
String convertToString(int[] input){
    return Arrays.stream(input)
      .mapToObj(Integer::toString)
      .collect(Collectors.joining(", "));
}复制
```

然后我们可以测试返回的*字符串*是否符合我们的预期：

```java
@Test
void givenAnIntArray_whenUsingCollectorsJoining_thenReturnCommaSeparatedString(){
    int[] intNumbers = { 1, 2, 3, 4, 5 };
    String expectedOutput = "1, 2, 3, 4, 5";

    String string = convertToString(intNumbers);

    Assert.assertEquals(expectedOutput, string);
}复制
```

## 5.结论

在本文中，我们学习了如何使用 Java Streams将*Integer*或原始整数的数组转换为*String 。*我们看到，在处理*Integer*时，我们需要期望一个*Stream<Integer>*。但是，当改为处理原始整数时，我们期望*IntStream*。

然后我们研究了如何处理这两种*Stream*类型以得到一个*String*数组。map *()*方法可用于*Stream<Integer>*，*mapToObj()*可用于*IntStream*。最后，我们看到了如何使用*Collectors.joining()*返回单个*字符串*。