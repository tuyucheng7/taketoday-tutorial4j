## 1. 简介

java.util.Objects类自1.7版本以来一直是Java的一部分，该类为对象提供静态工具方法，可用于执行一些常见任务，例如检查相等性、空值检查等。

在本文中，我们介绍Java 9中java.util.Objects类中引入的新方法。

## 2. requireNonNullElse方法

此方法接收两个参数，，如果第一个参数不为空，则返回第一个参数，否则返回第二个参数。如果两个参数都是null，则抛出NullPointerException：

```java
private List<String> aMethodReturningNullList(){
    return null;
}
```

```java
@Test
void givenNullObject_whenRequireNonNullElse_thenElse() {
	List<String> aList = Objects.<List>requireNonNullElse(
			aMethodReturningNullList(), Collections.EMPTY_LIST);
	assertThat(aList, is(Collections.EMPTY_LIST));
}

private List<String> aMethodReturningNonNullList() {
	return List.of("item1", "item2");
}

@Test
void givenObject_whenRequireNonNullElse_thenObject() {
	List<String> aList = Objects.<List>requireNonNullElse(
			aMethodReturningNonNullList(), Collections.EMPTY_LIST);
	assertThat(aList, is(List.of("item1", "item2")));
}

@Test
void givenNull_whenRequireNonNullElse_thenException() {
	assertThrows(NullPointerException.class, () -> Objects.<List>requireNonNullElse(null, null));
}
```

## 3. 使用requireNonNullElseGet

这个方法类似于requireNonNullElse，只是它的第二个参数是java.util.function.Supplier接口，允许对提供的集合进行惰性实例化。Supplier实现负责返回一个非空对象，如下所示：

```java
@Test
void givenObject_whenRequireNonNullElseGet_thenObject() {
	List<String> aList = Objects.<List>requireNonNullElseGet(null, List::of);
	assertThat(aList, is(List.of()));
}
```

## 4. 使用检查索引

此方法用于检查索引是否在给定长度内。如果0 <= index < length则返回索引。否则，它会抛出IndexOutOfBoundsException，如下所示：

```java
@Test
void givenNumber_whenInvokeCheckIndex_thenNumber() {
	int length = 5;
	assertThat(Objects.checkIndex(4, length), is(4));
}

@Test
void givenOutOfRangeNumber_whenInvokeCheckIndex_thenException() {
	int length = 5;
	assertThrows(IndexOutOfBoundsException.class, () -> Objects.checkIndex(5, length));
}
```

## 5. 使用checkFromToIndex

此方法用于检查由[fromIndex, toIndex)形成的给定子范围是否在[0, length)形成的范围内，如果子范围有效，则返回下限，如下所示：

```java
@Test
void givenSubRange_whenCheckFromToIndex_thenNumber() {
	int length = 6;
	assertThat(Objects.checkFromToIndex(2, length, length), is(2));
}

@Test
void givenInvalidSubRange_whenCheckFromToIndex_thenException() {
	int length = 6;
	assertThrows(IndexOutOfBoundsException.class, () -> Objects.checkFromToIndex(2, 7, length));
}
```

注：在数学中，以[a, b)形式表示的范围表示该范围包含a，不包含b。“[”和“]”表示包含该数字，而“(”和“)”表示排除该数字。

## 6. 使用checkFromIndexSize

该方法与checkFromToIndex类似，只是我们提供子范围的大小和下限，而不是提供子范围的上限。

在这种情况下，子范围是[fromIndex, fromIndex + size)并且此方法检查子范围是否在[0, length)形成的范围内：

```java
@Test
void givenSubRange_whenCheckFromIndexSize_thenNumber() {
	int length = 6;
	assertThat(Objects.checkFromIndexSize(2, 3, length), is(2));
}

@Test
void givenInvalidSubRange_whenCheckFromIndexSize_thenException() {
	int length = 6;
	assertThrows(IndexOutOfBoundsException.class, () -> Objects.checkFromIndexSize(2, 6, length));
}
```

## 7. 总结

JDK 9中的java.util.Objects类涵盖了一些新的工具方法，这也是令人鼓舞的，因为自Java 7中引入此工具类以来，它一直在定期更新。