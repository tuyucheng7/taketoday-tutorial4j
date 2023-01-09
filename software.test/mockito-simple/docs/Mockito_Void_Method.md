## 1. 概述

在这个简短的教程中，我们重点介绍如何在Mockito中mock返回值为void的方法。

下面的MyList类将用作测试用例中的mock对象。

本文中我们添加一个新方法：

```java
public class MyList extends AbstractList<String> {

    @Override
    public void add(int index, String element) {
        // no-op
    }
}
```

## 2. 简单的mock和verify

void方法可以与Mockito的doNothing()、doThrow()和doAnswer()方法一起使用，使mock和verify变得直观：

```java
class MockitoVoidMethodsUnitTest {

    @Test
    void whenAddCalledVerified() {
        MyList listMock = mock(MyList.class);
        doNothing().when(listMock).add(isA(Integer.class), isA(String.class));
        listMock.add(0, "");

        verify(listMock, times(1)).add(0, "");
    }
}
```

**但是，doNothing()是Mockito对void方法的默认行为**。

下面的这个测试实际上与上一个实现的功能是一样的：

```java
@Test
void whenAddCalledVerified() {
    MyList myList = mock(MyList.class);
    myList.add(0, "");
 
    verify(myList, times(1)).add(0, "");
}
```

doThrow()生成异常：

```java
@org.junit.Test(expected = Exception.class)
public void givenNull_addThrows() {
    MyList myList = mock(MyList.class);
    doThrow().when(myList).add(isA(Integer.class), isNull());
    
    myList.add(0, null);
}
```

## 3. 参数捕获

**使用doNothing()重写默认行为的一个原因是捕获参数**。在上面的例子中，我们使用verify()方法来检查传递给add()方法的参数。然而，我们可能需要捕获这些参数，并对它们做更多的处理。在这些情况下，我们像上面一样使用doNothing()，同时使用到ArgumentCaptor：

```java
@Test
void whenAddCalledValueCaptured() {
    MyList myList = mock(MyList.class);
    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
    doNothing().when(myList).add(any(Integer.class), valueCapture.capture());
    myList.add(0, "captured");
    
    assertEquals("captured", valueCapture.getValue());
}
```

## 4. Answer Void调用

一个方法可能会执行比仅仅添加或设置值更复杂的行为。对于这些情况，我们可以使用Mockito中的Answer来添加我们需要的行为：

```java
@Test
void whenAddCalledAnswered() {
    MyList myList = mock(MyList.class);
    
    doAnswer(invocation -> {
        Object arg0 = invocation.getArgument(0);
        Object arg1 = invocation.getArgument(1);
        // do something with the arguments here
        assertEquals(3, arg0);
        assertEquals("answer me", arg1);
        return null;
    }).when(myList).add(any(Integer.class), any(String.class));
    
    myList.add(3, "answer me");
}
```

正如[Mockito’s Java 8 Features](../../mockito/docs/Mockito_Java8.md)中所提到的的，我们使用带有Answer的lambda来定义add()的自定义行为。

## 5. 部分mock

部分mock也是一种选择，Mockito的doCallRealMethod()可以用于void方法：

```java
@Test
void whenAddCalledRealMethodCalled() {
    MyList myList = mock(MyList.class);
    doCallRealMethod().when(myList).add(any(Integer.class), any(String.class));
    myList.add(1, "real");
    
    verify(myList, times(1)).add(1, "real");
}
```

**这样，我们就可以调用实际的方法，同时进行验证。**

## 6. 总结

在这篇文章中，我们介绍了在使用Mockito进行测试时处理void返回值方法的四种不同方法。