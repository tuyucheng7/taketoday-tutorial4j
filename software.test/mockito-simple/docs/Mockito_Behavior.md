## 1. 概述

本文演示了如何使用Mockito在各种示例和用例中配置行为。

**旨在以案例为中心且实用**，不需要多余的细节和解释。

我们将mock一个简单的List实现，它与我们在上一篇文章中使用的实现相同：

```java
public class MyList extends AbstractList<String> {

    @Override
    public String get(final int index) {
        return null;
    }

    @Override
    public int size() {
        return 1;
    }
}
```

## 2. 案例

为mock配置简单的返回行为：

```java
class MockitoWhenThenExamplesUnitTest {

    @Test
    final void whenMockReturnBehaviorIsConfigured_thenBehaviorIsVerified() {
        final MyList listMock = Mockito.mock(MyList.class);
        when(listMock.add(anyString())).thenReturn(false);

        final boolean added = listMock.add(randomAlphabetic(6));
        assertThat(added).isFalse();
    }
}
```

以另一种方式配置mock的返回行为：

```java
class MockitoWhenThenExamplesUnitTest {

    @Test
    final void whenMockReturnBehaviorIsConfigured2_thenBehaviorIsVerified() {
        final MyList listMock = Mockito.mock(MyList.class);
        doReturn(false).when(listMock).add(anyString());

        final boolean added = listMock.add(randomAlphabetic(6));
        assertThat(added).isFalse();
    }
}
```

配置mock以在方法调用上抛出异常：

```java
class MockitoWhenThenExamplesUnitTest {

    @Test
    final void givenMethodIsConfiguredToThrowException_whenCallingMethod_thenExceptionIsThrown() {
        final MyList listMock = Mockito.mock(MyList.class);
        when(listMock.add(anyString())).thenThrow(IllegalStateException.class);

        assertThrows(IllegalStateException.class, () -> listMock.add(randomAlphabetic(6)));
    }
}
```

配置返回类型为void的方法的行为，以引发异常：

```java
class MockitoWhenThenExamplesUnitTest {

    @Test
    final void whenMethodHasNoReturnType_whenConfiguringBehaviorOfMethod_thenPossible() {
        final MyList listMock = Mockito.mock(MyList.class);
        doThrow(NullPointerException.class).when(listMock).clear();

        assertThrows(NullPointerException.class, listMock::clear);
    }
}
```

配置多个调用的行为：

```java
class MockitoWhenThenExamplesUnitTest {

    @Test
    final void givenBehaviorIsConfiguredToThrowExceptionOnSecondCall_whenCallingTwice_thenExceptionIsThrown() {
        final MyList listMock = Mockito.mock(MyList.class);
        when(listMock.add(anyString())).thenReturn(false).thenThrow(IllegalStateException.class);

        listMock.add(randomAlphabetic(6));
        assertThrows(IllegalStateException.class, () -> listMock.add(randomAlphabetic(6)));
    }
}
```

配置spy的行为：

```java
class MockitoWhenThenExamplesUnitTest {

    @Test
    final void givenSpy_whenConfiguringBehaviorOfSpy_thenCorrectlyConfigured() {
        final MyList instance = new MyList();
        final MyList spy = Mockito.spy(instance);

        doThrow(NullPointerException.class).when(spy).size();
        assertThrows(NullPointerException.class, spy::size);
    }
}
```

配置方法以在mock上调用真实的底层方法：

```java
class MockitoWhenThenExamplesUnitTest {

    @Test
    final void whenMockMethodCallIsConfiguredToCallTheRealMethod_thenRealMethodIsCalled() {
        final MyList listMock = Mockito.mock(MyList.class);
        when(listMock.size()).thenCallRealMethod();

        assertThat(listMock).hasSize(1);
    }
}
```

使用自定义Answer配置mock方法调用：

```java
class MockitoWhenThenExamplesUnitTest {

    @Test
    final void whenMockMethodCallIsConfiguredWithCustomAnswer_thenRealMethodIsCalled() {
        final MyList listMock = Mockito.mock(MyList.class);
        doAnswer(invocation -> "Always the same").when(listMock).get(anyInt());

        final String element = listMock.get(1);
        assertThat(element).isEqualTo("Always the same");
    }
}
```

## 3. 总结

所有这些案例和代码片段的实现都可以在我的[GitHub]()上找到。
这是一个基于Maven的项目，因此可以直接导入并运行。