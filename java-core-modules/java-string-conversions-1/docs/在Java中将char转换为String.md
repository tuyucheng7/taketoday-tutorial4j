## 1. 概述

将 c char转换为String实例是一个非常常见的操作。在本文中，我们将展示多种解决这种情况的方法。

## 2.字符串.valueOf()

String类有一个专为这个特定用例设计的静态方法valueOf() 。在这里你可以看到它的实际效果：

```java
@Test
public void givenChar_whenCallingStringValueOf_shouldConvertToString() {
    char givenChar = 'x';

    String result = String.valueOf(givenChar);

    assertThat(result).isEqualTo("x");
}
```

## 3.字符.toString()

Character类有一个专用的静态toString ()方法。在这里你可以看到它的实际效果：

```java
@Test
public void givenChar_whenCallingToStringOnCharacter_shouldConvertToString() {
    char givenChar = 'x';

    String result = Character.toString(givenChar);

    assertThat(result).isEqualTo("x");
}
```

## 4.角色构造器

你还可以实例化Character对象并使用标准的toString()方法：

```java
@Test
public void givenChar_whenCallingCharacterConstructor_shouldConvertToString() {
    char givenChar = 'x';

    String result = new Character(givenChar).toString();

    assertThat(result).isEqualTo("x");
}
```

## 5.隐式转换为字符串类型

另一种方法是通过类型转换来利用扩大转换：

```java
@Test
public void givenChar_whenConcatenated_shouldConvertToString() {
    char givenChar = 'x';

    String result = givenChar + "";

    assertThat(result).isEqualTo("x");
}
```

## 6.字符串格式()

最后，你可以使用String.format()方法：

```java
@Test
public void givenChar_whenFormated_shouldConvertToString() {
    char givenChar = 'x';

    String result = String.format("%c", givenChar);

    assertThat(result).isEqualTo("x");
}
```

## 七、总结

在本文中，我们探讨了将char实例转换为String实例的多种方法。