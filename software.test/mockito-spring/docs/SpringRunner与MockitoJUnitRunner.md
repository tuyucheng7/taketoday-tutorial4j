## 1. 概述

JUnit是Java中最流行的单元测试框架之一。此外，Spring Boot将其作为其应用程序的默认测试依赖项提供。

在本教程中，我们将比较两个JUnit运行器-SpringRunner和MockitoJUnitRunner。我们将了解它们的用途以及它们之间的主要区别。

## 2. @RunWith与@ExtendWith

在进一步讨论之前，让我们回顾一下如何扩展基本的JUnit功能或将其与其他库集成。

JUnit 4允许我们通过应用额外的功能来实现负责运行测试的[自定义Runner类](https://www.baeldung.com/junit-4-custom-runners)。要调用自定义Runner，我们使用@RunWith注解对测试类进行标注：

```java
@RunWith(CustomRunner.class)
class JUnit4Test {
    // ...
}
```

正如我们所知，JUnit 4现在处于遗留状态，由JUnit 5继承。较新的版本为我们带来了一个[全新的引擎和重写的API](https://www.baeldung.com/junit-5-migration)。它还改变了扩展模型的概念。我们现在可以使用带有@ExtendWith注解的[Extension](https://www.baeldung.com/junit-5-extensions) API，而不是实现自定义Runner或Rule类：

```java
@ExtendWith(CustomExtensionOne.class)
@ExtendWith(CustomExtensionTwo.class)
class JUnit5Test {
    // ...
}
```

与之前的Runner模型不同，我们可以为单个类提供多个扩展。大多数以前交付的Runner也为他们的扩展对应物重写了。

## 3. Spring示例应用

为了更好地理解，让我们介绍一个简单的Spring Boot应用程序-一个将给定字符串映射为大写的转换器。

让我们从数据提供程序的实现开始：

```java
@Component
public class DataProvider {

    private final List<String> memory = List.of("baeldung", "java", "dummy");

    public Stream<String> getValues() {
        return memory.stream();
    }
}
```

我们刚刚创建了一个带有硬编码字符串值的Spring组件。此外，它提供了一种流式传输这些字符串的方法。

其次，让我们实现一个转换值的服务类：

```java
@Service
public class StringConverter {

    private final DataProvider dataProvider;

    @Autowired
    public StringConverter(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public List<String> convert() {
        return dataProvider.getValues().map(String::toUpperCase).toList();
    }
}
```

这是一个简单的bean，它从先前创建的DataProvider中获取数据并应用大写映射。

现在，我们可以使用我们的应用程序来创建JUnit测试。我们将看到SpringRunner和MockitoJUnitRunner类之间的区别。

## 4. MockitoJUnitRunner

正如我们所知，[Mockito是一个mock框架](https://www.baeldung.com/mockito-annotations)，与其他测试框架结合使用以返回虚拟数据并避免外部依赖。该库为我们提供了[MockitoJUnitRunner](https://www.javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/junit/MockitoJUnitRunner.html)-**一个专用的JUnit 4 Runner**，用于集成Mockito并利用该库的功能。

现在让我们为StringConverter创建第一个测试：

```java
public class StringConverterTest {
    @Mock
    private DataProvider dataProvider;

    @InjectMocks
    private StringConverter stringConverter;

    @Test
    public void givenStrings_whenConvert_thenReturnUpperCase() {
        Mockito.when(dataProvider.getValues()).thenReturn(Stream.of("first", "second"));

        val result = stringConverter.convert();

        Assertions.assertThat(result).contains("FIRST", "SECOND");
    }
}
```

我们mock了DataProvider以返回两个字符串。但是如果我们运行它，测试就会失败：

```text
java.lang.NullPointerException: Cannot invoke "DataProvider.getValues()" because "this.dataProvider" is null
```

那是因为我们的mock没有正确初始化。@Mock和@InjectMocks注解当前不执行任何操作，我们可以通过实现init()方法来解决这个问题：

```java
@Before
public void init() {
    MockitoAnnotations.openMocks(this);
}
```

如果我们不想使用注解，我们也可以通过编程方式创建和注入mock：

```java
@Before
public void init() {
    dataProvider = Mockito.mock(DataProvider.class);
    stringConverter = new StringConverter(dataProvider);
}
```

我们使用Mockito API以编程方式初始化了mock。现在，测试按预期工作，我们的断言成功了。

接下来，让我们回到我们的第一个版本，删除init()方法，并使用MockitoJUnitRunner标注类：

```java
@RunWith(MockitoJUnitRunner.class)
public class StringConverterTest {
    // ...
}
```

再次，测试成功。我们调用了一个自定义Runner，它负责管理我们的mock。我们不必手动初始化它们。

总而言之，**MockitoJUnitRunner是Mockito框架的专用Runner。它负责初始化@Mock、@Spy和@InjectMock注解**，因此不需要显式使用[MockitoAnnotations.openMocks()](https://www.javadoc.io/static/org.mockito/mockito-core/4.8.1/org/mockito/MockitoAnnotations.html#openMocks(java.lang.Object))。此外，**它会检测测试中未使用的存根，并在每个测试方法之后验证mock使用情况**，就像[Mockito.validateMockitoUsage()](https://www.javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#validateMockitoUsage())所做的那样。

我们应该记住，所有Runner最初都是为JUnit 4设计的。如果我们想用JUnit 5支持Mockito注解，我们可以使用MockitoExtension：

```java
@ExtendWith(MockitoExtension.class)
public class StringConverterTest {
    // ...
}
```

此扩展将MockitoJUnitRunner的功能移植到新的扩展模型中。

## 5. SpringRunner

如果我们更深入地分析我们的测试，我们会发现，尽管使用了Spring，但我们根本没有启动Spring容器。现在让我们尝试修改我们的示例并初始化一个Spring上下文。

首先，我们将MockitoJUnitRunner替换为[SpringRunner](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/junit4/SpringRunner.html)类并检查结果：

```java
@RunWith(SpringRunner.class)
public class StringConverterTest {
    // ...
}
```

和以前一样，测试成功并且mock被正确初始化。此外，还启动了一个Spring上下文。我们可以得出结论，**SpringRunner不仅启用了Mockito注解，就像MockitoJUnitRunner所做的那样，而且还初始化了一个Spring上下文**。

当然，我们还没有在测试中看到Spring的全部潜力。我们可以将它们作为Spring bean注入，而不是构造新对象。正如我们所知，Spring测试模块默认集成了Mockito，它还为我们提供了[@MockBean和@SpyBean](https://www.baeldung.com/java-spring-mockito-mock-mockbean#spring-boots-mockbean-annotation)注解-将mocks和beans功能集成在一起。

让我们重写我们的测试：

```java
@ContextConfiguration(classes = StringConverter.class)
@RunWith(SpringRunner.class)
public class StringConverterTest {
    @MockBean
    private DataProvider dataProvider;

    @Autowired
    private StringConverter stringConverter;

    // ...
}
```

我们只是将DataProvider对象上的@Mock注解替换成@MockBean。它仍然是一个mock，但现在也可以用作bean。我们还通过@ContextConfiguration类注解配置了我们的Spring上下文并注入了StringConverter。结果还是测试成功了，不过现在是Spring beans和Mockito一起使用了。

综上所述，**SpringRunner是为JUnit 4创建的自定义Runner，它提供了Spring TestContext框架的功能**。由于Mockito是与Spring堆栈集成的默认mock框架，因此Runner带来了MockitoJUnitRunner提供的全面支持。有时，**我们可能还会遇到SpringJUnit4ClassRunner，这是一个别名，我们可以交替使用**。

如果我们正在寻找SpringRunner的扩展对应物，我们应该使用SpringExtension：

```java
@ExtendWith(SpringExtension.class)
public class StringConverterTest {
    // ...
}
```

由于JUnit 5是Spring Boot堆栈中的默认测试框架，因此该扩展已与许多测试切片注解集成，包括@SpringBootTest。

## 6. 总结

在本文中，我们了解了SpringRunner和MockitoJUnitRunner之间的区别。

我们首先回顾了JUnit 4和JUnit 5中使用的扩展模型。JUnit 4使用专用Runner，而JUnit 5支持扩展。同时，我们可以提供单个Runner或多个Extension。

然后，我们查看了MockitoJUnitRunner，它使Mockito框架在我们的JUnit 4测试中得到支持。通常，我们可以通过专用的@Mock、@Spy和@InjectMocks注解配置我们的mock，而无需任何初始化方法。

最后，我们讨论了SpringRunner，它释放了Mockito和Spring框架之间合作的所有优势。它不仅支持基本的Mockito注解，还支持Spring注解：@MockBean和@SpyBean。以这种方式构建的mock可以使用Spring上下文注入。