## 1. 概述

在本教程中，我们将介绍Mockito中的几个注解：@Mock、@Spy、@Captor和@InjectMocks。

## 2. 启用Mockito注解支持

首先，我们需要说明一下在Mockito测试中使用注解的不同方法。

### 2.1 MockitoJUnitRunner

第一种方式是**使用@RunWith(MockitoJUnitRunner.class)修饰Junit 4测试类**。

```java

@RunWith(MockitoJUnitRunner.class)
public class MockitoAnnotationTest {
    // ...
}
```

### 2.2 MockitoAnnotations.initMocks()

或者，我们可以通过调用MockitoAnnotations.initMocks()以编程方式启用Mockito注解支持：

```java
class MockitoAnnotationUnitTest {

    @Before
    void init() {
        MockitoAnnotations.initMocks(this);
    }
}
```

在比较高的Mockito版本中，initMocks()方法已被弃用，取而代之的是openMocks()方法：

```java
class MockitoAnnotationUnitTest {

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }
}
```

### 2.3 MockitoJUnit.rule()

我们还可以使用MockitoJUnit.rule()：

```java
public class MockitoInitWithMockitoJUnitRuleUnitTest {

    @Rule
    public MockitoRule initRule = MockitoJUnit.rule();

    @Mock
    private List<String> mockedList;

    @Test
    public void whenUsingMockitoJunitRule_thenMocksInitialized() {
        when(mockedList.size()).thenReturn(4);
        assertThat(mockedList.size()).isEqualTo(4);
    }
}
```

在这种情况下，我们必须确保initRule字段使用public修饰。

### 2.4 @ExtendWith(MockitoExtension.class)

最后，我们可以使用Junit 5支持的MockitoExtension：

```java

@ExtendWith(MockitoExtension.class)
class MockitoInitWithMockitoJUnitRuleUnitTest {

}
```

## 3. @Mock注解

Mockito中使用最广泛的注解是@Mock。我们可以使用@Mock来创建和注入mock实例，而无需手动调用Mockito.mock()方法。

在以下示例中，我们手动创建一个ArrayList的mock对象，不使用@Mock注解：

```java
class MockitoAnnotationUnitTest {

    @Test
    void whenNotUseMockAnnotation_thenCorrect() {
        final List<String> mockList = Mockito.mock(List.class);
        mockList.add("one");
        Mockito.verify(mockList).add("one");
        assertEquals(0, mockList.size());

        Mockito.when(mockList.size()).thenReturn(100);
        assertEquals(100, mockList.size());
    }
}
```

现在，我们使用@Mock注解注入mock：

```java
class MockitoAnnotationUnitTest {

    @Mock
    private List<String> mockedList;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenUseMockAnnotation_thenMockIsInjected() {
        mockedList.add("one");
        Mockito.verify(mockedList).add("one");
        assertEquals(0, mockedList.size());

        Mockito.when(mockedList.size()).thenReturn(100);
        assertEquals(100, mockedList.size());
    }
}
```

请注意，在这两个例子中，我们是如何与mock进行交互并验证其中的一些方法调用，以确保mock行为正确。

## 4. @Spy注解

现在让我们看看如何使用@Spy注解来spy现有实例。

在以下示例中，我们在不使用@Spy注解的情况下创建一个List的spy：

```java
class MockitoAnnotationUnitTest {

    @Test
    void whenNotUseSpyAnnotation_thenCorrect() {
        final List<String> spyList = Mockito.spy(new ArrayList<>());
        spyList.add("one");
        spyList.add("two");

        Mockito.verify(spyList).add("one");
        Mockito.verify(spyList).add("two");

        assertEquals(2, spyList.size());

        Mockito.doReturn(100).when(spyList).size();
        assertEquals(100, spyList.size());
    }
}
```

现在我们使用@Spy注解来创建一个List的spy：

```java
class MockitoAnnotationUnitTest {

    @Spy
    List<String> spiedList = new ArrayList<>();

    @Test
    void whenUseSpyAnnotation_thenSpyIsInjectedCorrectly() {
        spiedList.add("one");
        spiedList.add("two");

        Mockito.verify(spiedList).add("one");
        Mockito.verify(spiedList).add("two");

        assertEquals(2, spiedList.size());

        Mockito.doReturn(100).when(spiedList).size();
        assertEquals(100, spiedList.size());
    }
}
```

请注意，和之前一样，我们在这里与spy进行交互以确保其行为正确。在这个例子中，我们：

+ 使用了**真实**的方法spiedList.add()将元素添加到spiedList。
+ 使用Mockito.doReturn()将spiedList.size()方法**stubbed**以返回100而不是2。

## 5. @Captor注解

接下来让我们看看如何使用@Captor注解创建一个ArgumentCaptor实例。

在以下示例中，我们创建一个ArgumentCaptor，不使用@Captor注解：

```java
class MockitoAnnotationUnitTest {

    @Test
    void whenNotUseCaptorAnnotation_thenCorrect() {
        final List<String> mockList = Mockito.mock(List.class);
        final ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);
        mockList.add("one");
        Mockito.verify(mockList).add(arg.capture());

        assertEquals("one", arg.getValue());
    }
}
```

现在，让我们**使用@Captor注解**来创建ArgumentCaptor实例：

```java
class MockitoAnnotationUnitTest {

    @Mock
    List mockedList;

    @Captor
    ArgumentCaptor argCaptor;

    @Test
    void whenUseCaptorAnnotation_thenTheSame() {
        mockedList.add("one");
        Mockito.verify(mockedList).add(argCaptor.capture());

        assertEquals("one", argCaptor.getValue());
    }
}
```

当我们通过注解分离出mock的配置逻辑时，测试变得更简单，可读性更强。

## 6. @InjectMocks注解

现在我们介绍如何使用@InjectMocks注解将mock字段自动注入到测试类的对象中。

在下面的例子中，我们使用@InjectMocks将mock对象wordMap注入到dic对象的wordMap字段中：

```java
class MockitoAnnotationUnitTest {

    @Mock
    Map<String, String> wordMap;

    @InjectMocks
    MyDictionary dic = new MyDictionary();

    @Test
    void whenUseInjectMocksAnnotation_thenCorrect() {
        when(wordMap.get("aWord")).thenReturn("aMeaning");
        assertEquals("aMeaning", dic.getMeaning("aWord"));
    }
}
```

以下是MyDictionary类：

```java
class MyDictionary {
    private final Map<String, String> wordMap;

    MyDictionary() {
        wordMap = new HashMap<>();
    }

    public void add(final String word, final String meaning) {
        wordMap.put(word, meaning);
    }

    String getMeaning(final String word) {
        return wordMap.get(word);
    }
}
```

## 7. 将mock注入到Spy中

与上面的测试类似，我们可以将一个mock注入到spy中：

```java
class MockitoInjectIntoSpyUnitTest {

    @Mock
    Map<String, String> wordMap;

    @Spy
    MyDictionary spyDic = new MyDictionary();
}
```

**但是，Mockito不支持将mock注入到spy中**，以下测试会导致异常：

```java
class MockitoInjectIntoSpyUnitTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenUseInjectMocksAnnotation_thenCorrect() {
        Mockito.when(wordMap.get("aWord")).thenReturn("aMeaning");

        assertEquals("aMeaning", spyDic.getMeaning("aWord"));
    }
}
```

如果我们想将mock与spy一起使用，我们可以通过构造函数手动注入mock：

```java
class MyDictionary {

    MyDictionary(Map<String, String> wordMap) {
        this.wordMap = wordMap;
    }
}
```

现在我们可以手动创建spy，而不是使用注解：

```java
class MockitoInjectIntoSpyUnitTest {

    @Mock
    Map<String, String> wordMap;

    MyDictionary spyDic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        spyDic = Mockito.spy(new MyDictionary(wordMap));
    }
}
```

再次运行刚才的测试将通过。

## 8. 在使用注解时遇到NPE(空指针)

当我们尝试使用带有@Mock或@Spy注解的实例时，我们经常会遇到NullPointerException：

```java
class MockitoAnnotationsUninitializedUnitTest {

    @Mock
    List<String> mockedList;

    @Test
    void whenMockitoAnnotationsUninitialized_thenNPEThrown() {
        assertThrows(NullPointerException.class, () -> when(mockedList.size()).thenReturn(1));
    }
}
```

大多数情况下，这仅仅是因为我们没有正确启用Mockito的注解支持。

所以必须记住，只要我们使用到任何Mockito注解，我们必须添加额外的步骤并初始化它们，正如本文开头所介绍的那样。

## 9. 注意点

最后，以下是一些关于Mockito注解的注意事项：

+ Mockito的注解最大限度地减少了重复的mock创建代码。
+ 它们使测试更具可读性。
+ @InjectMocks是注入@Spy和@Mock实例所必需的。

## 10. 总结

在这篇简短的文章中，我们演示了Mockito库中一些注解的基本使用。