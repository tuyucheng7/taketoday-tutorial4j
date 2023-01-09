## 1. 概述

在本教程中，我们重点介绍如何使用Mockito配置方法调用以引发异常。

下面是我们将用作测试的MyDirectory类：

```java
class MyDictionary {
    private Map<String, String> wordMap = new HashMap<>();

    public void add(String word, String meaning) {
        wordMap.put(word, meaning);
    }

    public String getMeaning(String word) {
        return wordMap.get(word);
    }
}
```

## 2. 非void返回值

首先，如果我们的方法返回类型不是void，我们可以使用when().thenThrow()子句：

```java
class MockitoExceptionUnitTest {

    @Test
    void whenConfigNonVoidReturnMethodToThrowEx_thenExIsThrown() {
        MyDictionary dictMock = mock(MyDictionary.class);
        when(dictMock.getMeaning(anyString())).thenThrow(NullPointerException.class);

        assertThrows(NullPointerException.class, () -> dictMock.getMeaning("word"));
    }
}
```

请注意，我们将getMeaning()方法配置为在调用时抛出NullPointerException，该方法实际返回String类型的值。

## 3. void返回值

如果我们的方法返回值为void，我们可以使用doThrow().when()子句：

```java
@Test
void whenConfigVoidReturnMethodToThrowEx_ThenExIsThrown() {
    MyDictionary dictMock = mock(MyDictionary.class);
    doThrow(IllegalStateException.class).when(dictMock).add(anyString(), anyString());

    assertThrows(IllegalStateException.class, () -> dictMock.add("word", "meaning"));
}
```

在这里，我们配置了add()方法(它返回void)在调用时抛出IllegalStateException。我们不能将when().thenThrow()与void返回类型的方法一起使用，因为编译器不允许在括号内使用void方法。

## 4. 异常作为对象

要配置异常本身，我们可以像前面的示例中那样传递异常的Class或作为对象传递：

```java
@Test
void whenConfigNonVoidReturnMethodToThrowExWithNewExObj_thenExIsThrown() {
    MyDictionary dictMock = mock(MyDictionary.class);
    when(dictMock.getMeaning(anyString())).thenThrow(new NullPointerException("Error occurred"));
    
    assertThrows(NullPointerException.class, () -> dictMock.getMeaning("word"));
}
```

我们可以用doThrow()实现相同的效果：

```java
@Test
void whenConfigVoidReturnMethodToThrowExWithNewExObj_thenExIsThrown() {
    MyDictionary dictMock = mock(MyDictionary.class);
    doThrow(new IllegalStateException("Error occurred")).when(dictMock).add(anyString(), anyString());
    
    assertThrows(IllegalStateException.class, () -> dictMock.add("word", "meaning"));
}
```

## 5. Spy

我们还可以将Spy配置为抛出异常，与mock的方式相同：

```java
@Test
void givenSpy_whenConfigNonVoidReturnMethodToThrowEx_thenExIsThrown() {
    MyDictionary dict = new MyDictionary();
    MyDictionary spy = Mockito.spy(dict);
    when(spy.getMeaning(anyString())).thenThrow(NullPointerException.class);
    
    assertThrows(NullPointerException.class, () -> spy.getMeaning("word"));
}
```

## 6. 总结

在本文中，我们介绍了如何配置方法调用以在Mockito中引发异常。