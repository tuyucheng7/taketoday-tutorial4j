## 一、概述

在这个简短的教程中，我们将展示在Groovy中将String转换为 Integer 的不同方法。

## 2. as铸造 

我们可以用于转换的第一个方法是as 关键字，它与调用类的asType()方法相同：

```groovy
@Test
void givenString_whenUsingAsInteger_thenConvertToInteger() {
    def stringNum = "123"
    Integer expectedInteger = 123
    Integer integerNum = stringNum as Integer

    assertEquals(integerNum, expectedInteger)
}
```

像上面一样，我们可以使用 as int：

```groovy
@Test
void givenString_whenUsingAsInt_thenConvertToInt() {
    def stringNum = "123"
    int expectedInt = 123
    int intNum = stringNum as int

    assertEquals(intNum, expectedInt)
}
```

## 3.整数

另一种方法来自[java.lang.CharSequence](https://docs.groovy-lang.org/latest/html/groovy-jdk/java/lang/CharSequence.html#toInteger())的GroovyJDK 扩展：

```groovy
@Test
void givenString_whenUsingToInteger_thenConvertToInteger() {
    def stringNum = "123"
    int expectedInt = 123
    int intNum = stringNum.toInteger()

    assertEquals(intNum, expectedInt)
}
```

## 4.整数#parseInt

第三种方法是使用Java 的静态方法Integer.parseInt()：

```groovy
@Test
void givenString_whenUsingParseInt_thenConvertToInteger() {
    def stringNum = "123"
    int expectedInt = 123
    int intNum = Integer.parseInt(stringNum)

    assertEquals(intNum, expectedInt)
}
```

## 5.整数#intValue

另一种方法是创建一个新的Integer对象并调用它的intValue方法：

```groovy
@Test
void givenString_whenUsingIntValue_thenConvertToInteger() {
    def stringNum = "123"
    int expectedInt = 123
    int intNum = new Integer(stringNum).intValue()

    assertEquals(intNum, expectedInt)
}
```

或者，在这种情况下，我们也可以只使用new Integer(stringNum)：

```groovy
@Test
void givenString_whenUsingNewInteger_thenConvertToInteger() {
    def stringNum = "123"
    int expectedInt = 123
    int intNum = new Integer(stringNum)

    assertEquals(intNum, expectedInt)
}
```

## 6.整数#valueOf

类似于Integer.parseInt()，我们也可以使用Java的静态方法Integer#valueOf：

```groovy
@Test
void givenString_whenUsingValueOf_thenConvertToInteger() {
    def stringNum = "123"
    int expectedInt = 123
    int intNum = Integer.valueOf(stringNum)

    assertEquals(intNum, expectedInt)
}
```

## 7.十进制格式

对于最后一个方法，我们可以应用Java的DecimalFormat类：

```groovy
@Test
void givenString_whenUsingDecimalFormat_thenConvertToInteger() {
    def stringNum = "123"
    int expectedInt = 123
    DecimalFormat decimalFormat = new DecimalFormat("#")
    int intNum = decimalFormat.parse(stringNum).intValue()

    assertEquals(intNum, expectedInt)
}
```

## 8. 异常处理

所以，如果转换失败，比如有非数字字符，将抛出NumberFormatException。此外，在String为null的情况下，将抛出NullPointerException ：

```groovy
@Test(expected = NumberFormatException.class)
void givenInvalidString_whenUsingAs_thenThrowNumberFormatException() {
    def invalidString = "123a"
    invalidString as Integer
}

@Test(expected = NullPointerException.class)
void givenNullString_whenUsingToInteger_thenThrowNullPointerException() {
    def invalidString = null
    invalidString.toInteger()
}
```

为了防止这种情况发生，我们可以使用isInteger 方法：

```groovy
@Test
void givenString_whenUsingIsInteger_thenCheckIfCorrectValue() {
    def invalidString = "123a"
    def validString = "123"
    def invalidNum = invalidString?.isInteger() ? invalidString as Integer : false
    def correctNum = validString?.isInteger() ? validString as Integer : false

    assertEquals(false, invalidNum)
    assertEquals(123, correctNum)
}
```

## 9.总结

在这篇简短的文章中，我们展示了一些在Groovy中从String 对象切换到 Integer对象的有效方法。

在选择转换对象类型的最佳方法时，以上所有方法都同样好。最重要的是通过首先检查我们应用程序中String的值是否可以是非数字、空或null来避免错误。