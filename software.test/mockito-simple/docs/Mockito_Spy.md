## 1. 概述

在本教程中，我们将说明如何使用Mockito中的Spy。

我们将介绍@Spy注解以及如何stubbing spy。最后，我们将探讨Mock和Spy之间的区别。

## 2. 简单的Spy案例

首先让我们看看如何使用Spy。

简单地说，Mockito.spy()来spy一个真实的对象。

这允许我们调用对象的所有正常方法，同时仍然跟踪每个交互，就像我们使用mock一样。

在下面的例子中，我们Spy现有的ArrayList对象：

```java

@ExtendWith(MockitoExtension.class)
class MockitoSpyUnitTest {

    @Test
    void whenSpyingOnList_thenCorrect() {
        final List<String> list = new ArrayList<>();
        final List<String> spyList = Mockito.spy(list);

        spyList.add("one");
        spyList.add("two");

        Mockito.verify(spyList).add("one");
        Mockito.verify(spyList).add("two");

        assertThat(spyList).hasSize(2);
    }
}
```

在上面的测试中，我们对add的方法调用会调用真实方法，因此我们对hasSize的调用也会返回2。

## 3. @Spy注解

接下来，让我们看看如何使用@Spy注解。我们可以使用@Spy注解代替spy()方法调用：

```java
class MockitoSpyUnitTest {

    @Spy
    private List<String> aSpyList = new ArrayList<>();

    @Test
    void whenUsingTheSpyAnnotation_thenObjectIsSpied() {
        aSpyList.add("one");
        aSpyList.add("two");

        Mockito.verify(aSpyList).add("one");
        Mockito.verify(aSpyList).add("two");

        assertThat(aSpyList).hasSize(2);
    }
}
```

为了启用Mockito注解支持(例如@Spy、@Mock...)，我们需要执行以下操作之一：

+ 调用MockitoAnnotations.open(this)方法初始化被标注字段。
+ 使用@ExtendWith(MockitoExtension.class)标注测试类。

## 4. Stubbing Spy

现在让我们看看如何stub Spy，我们可以使用与mock相同的语法配置/覆盖方法的行为。

在这里，我们使用doReturn()覆盖size()方法：

```java
class MockitoSpyUnitTest {

    @Test
    void whenStubASpy_thenStubbed() {
        final List<String> list = new ArrayList<>();
        final List<String> spyList = Mockito.spy(list);

        assertEquals(0, spyList.size());

        Mockito.doReturn(100).when(spyList).size();
        assertThat(spyList).hasSize(100);
    }
}
```

## 5. Mock与Spy

让我们说明一下Mockito中Mock和Spy之间的区别。我们不会深入这两个概念之间的理论差异，只是它们在Mockito本身内部的不同之处。

当Mockito创建mock时，它是从Type的Class中创建的，而不是从实际实例中创建的。mock只是创建了Class的一个基本的shell实例，完全用于跟踪与它的交互。

另一方面，**spy会包装现有实例**。它仍然会以与普通实例相同的方式运行；唯一的区别是它还将被用来跟踪与它的所有交互。

在下面，我们创建一个ArrayList类的mock：

```java
class MockitoSpyUnitTest {

    @Test
    void whenCreateMock_thenCreated() {
        final List<String> mockedList = Mockito.mock(ArrayList.class);

        mockedList.add("one");
        Mockito.verify(mockedList).add("one");

        assertThat(mockedList).hasSize(0);
    }
}
```

正如我们所见，向mock的ArrayList中添加一个元素实际上并没有添加任何内容。它只是调用方法，没有其他作用。

另一方面，spy的行为会有所不同。它会实际调用add方法的真正实现并将元素添加到底层ArrayList中：

```java
class MockitoSpyUnitTest {

    @Test
    void whenCreateSpy_thenCreate() {
        final List<String> spyList = Mockito.spy(new ArrayList<>());

        spyList.add("one");
        Mockito.verify(spyList).add("one");

        assertThat(spyList).hasSize(1);
    }
}
```

## 6. NotAMockException

在最后一节中，我们介绍Mockito中的NotAMockException。此异常是我们在滥用mock或spy时可能会遇到的常见异常之一。

让我们首先了解可能发生此异常的情况：

```text
List<String> list = new ArrayList<String>();
Mockito.doReturn(100).when(list).size();
assertEquals("Size should be 100: ", 100, list.size());
```

当我们运行这段代码时，我们会得到以下错误：

```text
org.mockito.exceptions.misusing.NotAMockException: 
Argument passed to when() is not a mock!
Example of correct stubbing:doThrow(new RuntimeException()).when(mock).someMethod();
```

从Mockito错误消息中可以清楚地看出问题所在。在我们的例子中，list对象不是mock对象。
**Mockito的when()方法需要一个mock或spy对象作为参数**。

我们还可以看到，异常消息甚至给出了正确的调用方式。因此可以通过以下方式解决：

```text
final List<String> spyList = Mockito.spy(new ArrayList<>());
assertThatNoException().isThrownBy(() -> Mockito.doReturn(100).when(spyList).size());
```

我们的例子现在按预期运行，不再抛出NotAMockException。

## 7. 总结

在这篇文章中，我们给出了使用Spy最有用的案例。

我们介绍了如何创建spy、使用@Spy注解、stub spy，最后了解了Mock和Spy之间的区别。