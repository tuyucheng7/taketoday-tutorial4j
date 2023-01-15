## 1. 概述

在本文中，我们将详细探讨JUnit中断言的用法。

我们主要介绍如何分别在Junit 4和Junit 5中使用断言，并将重点介绍使用JUnit 5中对断言所做的增强。

## 2. 断言

断言是支持测试中断言条件的工具方法；这些方法可以通过JUnit 4中的Assert类或JUnit 5中的Assertions类访问。

为了增加测试和断言本身的可读性，建议静态导入相应的断言类。这样我们可以直接引用断言方法本身，而不需要使用类名调用。

让我们开始介绍JUnit 4中可用的断言。

## 3. JUnit 4中的断言

在Junit 4中，断言可用于所有基本数据类型、对象和数组。

断言中的参数顺序分别是预期值和实际值；第一个参数还可以指定断言失败时提示的消息。

assertThat断言的定义方式稍微有一点不同，我们稍后会介绍。

### 3.1 assertEquals()

assertEquals断言验证预期值和实际值是否相等：

```java
public class Junit4AssertionsUnitTest {

    @Test
    public void whenAssertingEquality_thenEqual() {
        String expected = "Tuyucheng";
        String actual = "Tuyucheng";
        assertEquals(expected, actual);
    }
}
```

也可以指定断言失败时显示的消息：

```java
public class Junit4AssertionsUnitTest {
    @Test
    public void whenAssertingEqualityWithMessage_thenEqual() {
        String expected = "Tuyucheng";
        String actual = "Tuyucheng";
        assertEquals("failure - strings are not equal", expected, actual);
    }
}
```

### 3.2 assertArrayEquals()

如果我们想断言两个数组相等，我们可以使用assertArrayEquals()：

```java
public class Junit4AssertionsUnitTest {
    @Test
    public void whenAssertingArraysEquality_thenEqual() {
        char[] expected = {'J', 'u', 'n', 'i', 't'};
        char[] actual = "Junit".toCharArray();
        assertArrayEquals(expected, actual);
    }
}
```

如果两个数组都为null，断言也认为它们相等：

```java
public class Junit4AssertionsUnitTest {
    @Test
    public void givenNullArrays_whenAssertingArraysEquality_thenEqual() {
        int[] expected = null;
        int[] actual = null;
        assertArrayEquals(expected, actual);
    }
}
```

### 3.3 assertNotNull()和assertNull()

当我们想测试一个对象是否为null时，我们可以使用assertNull断言：

```java
public class Junit4AssertionsUnitTest {
    @Test
    public void whenAssertingNull_thenTrue() {
        Object car = null;
        assertNull("The car should be null", car);
    }
}
```

相反，如果我们想要断言一个对象不为null，我们可以使用assertNotNull断言。

### 3.4 assertNotSame()和assertSame()

使用assertNotSame，可以验证两个变量不是引用同一个对象：

```java
public class Junit4AssertionsUnitTest {
    @Test
    public void whenAssertingNotSameObject_thenDifferent() {
        Object cat = new Object();
        Object dog = new Object();
        assertNotSame(cat, dog);
    }
}
```

相反，当我们想要验证两个变量是引用同一个对象时，我们可以使用assertSame断言。

### 3.5 assertTrue()和assertFalse()

如果我们想验证某个条件是真还是假，我们可以分别使用assertTrue断言或assertFalse断言：

```java
public class Junit4AssertionsUnitTest {
    @Test
    public void whenAssertingConditions_thenVerified() {
        assertTrue("5 is greater then 4", 5 > 4);
        assertFalse("5 is not greater then 6", 5 > 6);
    }
}
```

### 3.6 fail()

fail断言是测试失败，并抛出AssertionFailedError。它可用于验证是否引发了实际异常，或者当我们希望在其开发过程中使测试失败时。

让我们看看如何在第一个场景中使用它：

```java
public class Junit4AssertionsUnitTest {
    @Test
    public void when_thenNotFailed() {
        try {
            methodThatShouldThrowException();
            fail("Exception not thrown");
        } catch (UnsupportedOperationException e) {
            assertEquals("Operation Not Supported", e.getMessage());
        }
    }

    private void methodThatShouldThrowException() {
        throw new UnsupportedOperationException("Operation Not Supported");
    }
}
```

### 3.7 assertThat()

assertThat断言是JUnit 4中唯一一个与其他断言相比参数顺序相反的断言。

在这种情况下，断言有一个可选的失败消息、实际值和一个Matcher对象。

让我们看看如何使用此断言来检查数组是否包含特定值：

```java
public class Junit4AssertionsUnitTest {
    @Test
    public void testAssertThatHasItems() {
        assertThat(Arrays.asList("Java", "Kotlin", "Scala"), hasItems("Java", "Kotlin"));
    }
}
```

## 4. JUnit 5中的断言

JUnit 5中保留了JUnit 4的许多断言方法，同时添加了一些支持Java 8的新方法。

Junit 5中的断言同样可用于所有基本数据类型、对象和数组(原始类型或对象)。

唯一发生改变的是断言的参数顺序发生了变化，将消息参数移至最后一个参数。由于Java 8的支持，输出消息可以是Supplier，允许对其进行惰性评估。

### 4.1 assertArrayEquals()

assertArrayEquals断言验证预期数组和实际数组是否相等：

```java

@DisplayName("Test case for assertions")
class Junit5AssertionsUnitTest {

    @Test
    @DisplayName("Arrays should be equals")
    void whenAssertingArraysEquality_thenEqual() {
        char[] expected = {'J', 'u', 'p', 'i', 't', 'e', 'r'};
        char[] actual = "Jupiter".toCharArray();
        assertArrayEquals(expected, actual, "Arrays should be equal");
    }
}
```

如果数组不相等，将输出“Arrays should be equal”作为提示消息。

### 4.2 assertEquals()

如果我们想要断言两个浮点数相等，我们可以使用简单的assertEquals断言：

```java
class Junit5AssertionsUnitTest {
    @Test
    @DisplayName("The area of two polygons should be equal")
    void whenAssertingEquality_thenEqual() {
        float square = 2 * 2;
        float rectangle = 2 * 2;
        assertEquals(square, rectangle);
    }
}
```

但是，如果我们想要断言实际值与预期值相差一个预定义的delta，我们仍然可以使用assertEquals，但我们必须将delta值作为第三个参数传递：

```java
class Junit5AssertionsUnitTest {
    @Test
    void whenAssertingEqualityWithDelta_thenEqual() {
        float square = 2 * 2;
        float rectangle = 3 * 2;
        float delta = 2;
        assertEquals(square, rectangle, delta);
    }
}
```

### 4.3 assertTrue()和assertFalse()

使用assertTrue断言，可以验证提供的条件是否为真：

```java
class Junit5AssertionsUnitTest {
    @Test
    void whenAssertingConditions_thenVerified() {
        assertTrue(5 > 4, "5 is greater the 4");
        assertTrue(null == null, "null is equal to null");
    }
}
```

有了对lambda表达式的支持，可以为断言提供BooleanSupplier而不是布尔条件。

让我们看看如何使用assertFalse断言BooleanSupplier的正确性：

```java
class Junit5AssertionsUnitTest {
    @Test
    void givenBooleanSupplier_whenAssertingCondition_thenVerified() {
        BooleanSupplier condition = () -> 5 > 6;
        assertFalse(condition, "5 is not greater then 6");
    }
}
```

### 4.4 assertNull()和assertNotNull()

当我们想要断言一个对象不为null时，我们可以使用assertNotNull断言：

```java
class Junit5AssertionsUnitTest {
    @Test
    void whenAssertingNotNull_thenTrue() {
        Object dog = new Object();
        assertNotNull(dog, "The dog should not be null");
    }
}
```

相反，我们可以使用assertNull断言对象应该为null：

```java
class Junit5AssertionsUnitTest {
    @Test
    void whenAssertingNull_thenTrue() {
        Object cat = null;
        assertNull(cat, "The cat should be null");
    }
}
```

在这两种情况下，都会以惰性方式检索失败消息，因为它是Supplier。

### 4.5 assertSame()和assertNotSame()

当我们想要断言期望和实际引用同一个对象时，我们必须使用assertSame断言：

```java
class Junit5AssertionsUnitTest {
    @Test
    void whenAssertingSameObject_thenSuccessfull() {
        String language = "Java";
        Optional<String> optional = Optional.of(language);
        assertSame(language, optional.get());
    }
}
```

相反，我们可以使用assertNotSame。

### 4.6 fail()

fail断言使用提供的失败消息以及根本原因使测试失败。这对于在开发尚未完成时标记测试很有用：

```java
class Junit5AssertionsUnitTest {
    @Test
    @Disabled
    void whenFailingATest_thenFailed() {
        fail("FAIL - test not completed");
    }
}
```

### 4.7 assertAll()

JUnit 5中引入的新断言之一是assertAll。

该断言允许创建分组断言，其中所有断言都被执行并且它们的失败被一起报告。
详细地说，此断言接收一个headling(该headling将包含在MultipleFailureError的message字符串中)，以及一个Executable流。

让我们定义一个分组断言：

```java
class Junit5AssertionsUnitTest {
    @Test
    void givenMultipleAssertion_whenAssertingAll_thenOK() {
        Object obj = null;
        assertAll("heading",
                () -> assertEquals(4, 2 * 2, "4 is 2 times 2"),
                () -> assertEquals("java", "JAVA".toLowerCase()),
                () -> assertEquals(obj, null, "null is equal to null")
        );
    }
}
```

仅当其中一个Executable引发列入黑名单的异常(例如OutOfMemoryError)时，才会中断分组断言的执行。

### 4.8 assertIterableEquals()

assertIterableEquals断言预期和实际的Iterable对象是完全相等的。

要完全相等，两个Iterable对象必须以相同的顺序返回相等的元素，并且不需要两个Iterable对象的类型相同才能相等。

考虑到这一点，让我们看看如何断言两个不同类型的集合(例如LinkedList和ArrayList)相等：

```java
class Junit5AssertionsUnitTest {
    @Test
    void givenTwoLists_whenAssertingIterables_thenEquals() {
        Iterable<String> al = new ArrayList<>(asList("Java", "Junit", "Test"));
        Iterable<String> ll = new LinkedList<>(asList("Java", "Junit", "Test"));
        assertIterableEquals(al, ll);
    }
}
```

与assertArrayEquals一样，如果两个Iterable对象都为null，它们也会被看作相等。

### 4.9 assertLinesMatch()

assertLinesMatch断言预期的String集合与实际集合匹配。

此方法与assertEquals和assertIterableEquals不同，因为对于每对预期和实际行，它执行以下算法：

1. 检查预期线是否等于实际线。如果是，则继续下一对。
2. 将预期的行视为正则表达式并使用 String.matches() 方法执行检查。如果是，则继续下一对。
3. 检查预期的行是否是快进标记。如果是，则应用快进并从步骤 1 重复算法。

让我们看看我们如何使用这个断言来断言两个String集合具有匹配的行：

```java
class Junit5AssertionsUnitTest {
    @Test
    void whenAssertingEqualityListOfStrings_thenEqual() {
        List<String> expected = asList("Java", "\\d+", "JUnit");
        List<String> actual = asList("Java", "11", "JUnit");
        assertLinesMatch(expected, actual);
    }
}
```

### 4.10 assertNotEquals()

作为assertEquals的补充，assertNotEquals断言预期值和实际值不相等：

```java
class Junit5AssertionsUnitTest {
    @Test
    void whenAssertingEquality_thenNotEqual() {
        Integer value = 5;
        assertNotEquals(0, value, "The result cannot be 0");
    }
}
```

如果两者都为null，则断言失败。

### 4.11 assertThrows()

为了增加简单性和可读性，新的assertThrows断言允许我们以一种清晰而简单的方式断言Executable是否抛出指定的异常类型。

让我们看看如何断言抛出的异常：

```java
class Junit5AssertionsUnitTest {
    @Test
    void whenAssertingException_thenThrown() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
                    throw new IllegalArgumentException("Exception message");
                }
        );
        assertEquals("Exception message", exception.getMessage());
    }
}
```

如果没有抛出异常，或者抛出了不同类型的异常，则断言将失败。

### 4.12 assertTimeout()和assertTimeoutPreemptively()

如果我们想要断言提供的Executable的执行在给定的超时之前结束，我们可以使用assertTimeout断言：

```java
class Junit5AssertionsUnitTest {
    @Test
    void whenAssertingTimeout_thenNotExceeded() {
        assertTimeout(ofSeconds(2),
                () -> {
                    Thread.sleep(1000);
                }
        );
    }
}
```

但是，使用assertTimeout断言，提供的Executable将在调用代码的同一线程中执行。因此，如果超过超时，供应商的执行不会被抢先中止。

如果我们想确保一旦超过超时时间就会中止可执行文件的执行，我们可以使用 assertTimeoutPreemptively 断言。

两个断言都可以接受ThrowingSupplier而不是Executable，它表示返回对象并可能抛出 Throwable 的任何通用代码块。

## 5. 总结

在本教程中，我们介绍了JUnit 4和JUnit 5中可用的所有断言。

我们简要说明了JUnit 5中所做的改进，引入了新的断言和对lambdas的支持。