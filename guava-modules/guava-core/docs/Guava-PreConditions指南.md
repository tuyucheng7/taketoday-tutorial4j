## 1. 概述

在本教程中，我们将展示如何使用 Google Guava 的Preconditions类。

Preconditions类提供了一个静态方法列表，用于检查是否使用有效参数值调用方法或构造函数。如果先决条件失败，则会抛出定制的异常。

## 2. Google Guava 的先决条件

Preconditions类中的每个静态方法都有三个变体：

-   没有争论。抛出异常而不显示错误消息
-   用作错误消息的额外Object参数。抛出异常并显示错误消息
-   一个额外的 String 参数，带有任意数量的附加Object参数，充当带有占位符的错误消息。这有点像printf，但为了 GWT 兼容性和效率，它只允许%s指示符

让我们看看如何使用Preconditions类。

### 2.1. Maven 依赖

让我们首先在pom.xml中添加 Google 的 Guava 库依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")检查最新版本的依赖项。

## 3.检查参数 

Preconditions 类的方法checkArgument确保传递给调用方法的参数的真实性。此方法接受布尔条件并在条件为假时抛出IllegalArgumentException 。

让我们看看我们如何通过一些例子来使用这个方法。

### 3.1. 没有错误信息

我们可以在不向checkArgument方法传递任何额外参数的情况下使用checkArgument：

```java
@Test
public void whenCheckArgumentEvaluatesFalse_throwsException() {
    int age = -18;
 
    assertThatThrownBy(() -> Preconditions.checkArgument(age > 0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(null).hasNoCause();
}
```

### 3.2. 有错误信息

我们可以通过传递错误消息从checkArgument方法中获取有意义的错误消息：

```java
@Test
public void givenErrorMsg_whenCheckArgEvalsFalse_throwsException() {
    int age = -18;
    String message = "Age can't be zero or less than zero.";
 
    assertThatThrownBy(() -> Preconditions.checkArgument(age > 0, message))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(message).hasNoCause();
}
```

### 3.3. 带有模板错误消息

通过传递错误消息，我们可以从checkArgument方法中获取有意义的错误消息以及动态数据：

```java
@Test
public void givenTemplateMsg_whenCheckArgEvalsFalse_throwsException() {
    int age = -18;
    String message = "Age should be positive number, you supplied %s.";
 
    assertThatThrownBy(
      () -> Preconditions.checkArgument(age > 0, message, age))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(message, age).hasNoCause();
}

```

## 4.检查元素索引 

checkElementIndex方法检查索引是否是列表、字符串或指定大小的数组中的有效索引。元素索引的范围可以从 0(含)到不包含大小。你不直接传递列表、字符串或数组，你只传递它的大小。如果索引不是有效的元素索引，此方法将抛出IndexOutOfBoundsException ，否则它会返回传递给该方法的索引。

让我们看看如何通过在checkElementIndex方法抛出异常时传递错误消息来显示有意义的错误消息来使用此方法：

```java
@Test
public void givenArrayAndMsg_whenCheckElementEvalsFalse_throwsException() {
    int[] numbers = { 1, 2, 3, 4, 5 };
    String message = "Please check the bound of an array and retry";
 
    assertThatThrownBy(() -> 
      Preconditions.checkElementIndex(6, numbers.length - 1, message))
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessageStartingWith(message).hasNoCause();
}
```

## 5.检查不为空 

checkNotNull方法检查作为参数提供的值是否为空。它返回已检查的值。如果传递给此方法的值为 null，则抛出NullPointerException。

接下来，我们将展示如何通过传递错误消息从checkNotNull方法获取有意义的错误消息来展示如何使用此方法：

```java
@Test
public void givenNullString_whenCheckNotNullWithMessage_throwsException () {
    String nullObject = null;
    String message = "Please check the Object supplied, its null!";
 
    assertThatThrownBy(() -> Preconditions.checkNotNull(nullObject, message))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(message).hasNoCause();
}
```

我们还可以通过将参数传递给错误消息，从checkNotNull方法中获取基于动态数据的有意义的错误消息：

```java
@Test
public void whenCheckNotNullWithTemplateMessage_throwsException() {
    String nullObject = null;
    String message = "Please check the Object supplied, its %s!";
 
    assertThatThrownBy(
      () -> Preconditions.checkNotNull(nullObject, message, 
        new Object[] { null }))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(message, nullObject).hasNoCause();
}
```

## 6.检查位置索引 

checkPositionIndex方法检查作为参数传递给此方法的索引是否是指定大小的列表、字符串或数组中的有效索引。位置索引的范围可以从 0(含)到大小(含)。你不直接传递列表、字符串或数组，你只传递它的大小。

如果传递的索引不在 0 和给定的大小之间，则此方法抛出IndexOutOfBoundsException，否则返回索引值。

让我们看看如何从checkPositionIndex方法中获取有意义的错误消息：

```java
@Test
public void givenArrayAndMsg_whenCheckPositionEvalsFalse_throwsException() {
    int[] numbers = { 1, 2, 3, 4, 5 };
    String message = "Please check the bound of an array and retry";
 
    assertThatThrownBy(
      () -> Preconditions.checkPositionIndex(6, numbers.length - 1, message))
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessageStartingWith(message).hasNoCause();
}
```

## 7.检查状态 

方法checkState检查对象状态的有效性并且不依赖于方法参数。例如，迭代器可能会使用它来检查在任何调用 remove 之前是否调用了 next。如果对象的状态(作为参数传递给方法的布尔值)处于无效状态，则此方法抛出IllegalStateException 。

让我们看看如何通过在checkState方法抛出异常时传递一条错误消息来显示一条有意义的错误消息来使用此方法：

```java
@Test
public void givenStatesAndMsg_whenCheckStateEvalsFalse_throwsException() {
    int[] validStates = { -1, 0, 1 };
    int givenState = 10;
    String message = "You have entered an invalid state";
 
    assertThatThrownBy(
      () -> Preconditions.checkState(
        Arrays.binarySearch(validStates, givenState) > 0, message))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith(message).hasNoCause();
}
```

## 八. 总结

在本教程中，我们演示了Guava 库中PreConditions类的方法。Preconditions类提供一组静态方法，用于验证方法或构造函数是否使用有效参数值调用。