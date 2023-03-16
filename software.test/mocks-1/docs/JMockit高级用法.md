## 1. 简介

在本文中，我们将介绍JMockit的一些高级场景，例如：

-   伪造(或MockUp API)
-   Deencapsulation工具类
-   如何仅使用一个mock来mock多个接口
-   如何重复使用期望和验证

如果你想了解JMockit的基础知识，请查看本系列的其他文章。可以在底部找到相关链接。

## 2. Maven依赖

首先，我们需要将[jmockit](https://central.sonatype.com/artifact/org.jmockit/jmockit/1.49)依赖项添加到我们的项目中：

```xml
<dependency> 
    <groupId>org.jmockit</groupId> 
    <artifactId>jmockit</artifactId> 
    <version>1.41</version>
</dependency>
```

## 3. 私有方法/内部类Mock

mock和测试私有方法或内部类通常被认为是不好的做法。

其背后的原因是，如果它们是私有的，则不应直接对其进行测试，因为它们是类的最内在因素，但有时仍然需要对它们进行测试，尤其是在处理遗留代码时。

使用JMockit，你有两种选择来处理这些问题：

-   用于更改实际实现的MockUp API(对于第二种情况)
-   Deencapsulation工具类，用于直接调用任何方法(对于第一种情况)

接下来的所有示例都将针对以下类完成，我们假设它们在与第一个配置相同的测试类上运行(以避免重复代码)：

```java
public class AdvancedCollaborator {
    int i;
    private int privateField = 5;

    // default constructor omitted 

    public AdvancedCollaborator(String string) throws Exception {
        i = string.length();
    }

    public String methodThatCallsPrivateMethod(int i) {
        return privateMethod() + i;
    }

    public int methodThatReturnsThePrivateField() {
        return privateField;
    }

    private String privateMethod() {
        return "default:";
    }

    class InnerAdvancedCollaborator {
        // ...
    }
}
```

### 3.1 使用MockUp伪造

JMockit的Mockup API为创建虚假实现或mock-up提供了支持。通常，mock-up针对要伪造的类中的一些方法和/或构造函数，而保留大多数其他方法和构造函数不变。这允许对类进行完全重写，因此可以针对任何方法或构造函数(具有任何访问修饰符)。

让我们看看如何使用Mockup的API重新定义privateMethod()：

```java
@RunWith(JMockit.class)
public class AdvancedCollaboratorTest {

    @Tested
    private AdvancedCollaborator mock;

    @Test
    public void testToMockUpPrivateMethod() {
        new MockUp<AdvancedCollaborator>() {
            @Mock
            private String privateMethod() {
                return "mocked: ";
            }
        };
        String res = mock.methodThatCallsPrivateMethod(1);
        assertEquals("mocked: 1", res);
    }
}
```

在此示例中，我们在具有匹配签名的方法上使用@Mock注解为AdvancedCollaborator类定义了一个新的MockUp。在此之后，对该方法的调用将委托给我们mock的方法。

我们还可以使用它来mock需要特定参数或配置的类的构造函数，以简化测试：

```java
@Test
public void testToMockUpDifficultConstructor() throws Exception {
	new MockUp<AdvancedCollaborator>() {
		@Mock
		public void $init(Invocation invocation, String string) {
			((AdvancedCollaborator) invocation.getInvokedInstance()).i = 1;
		}
	};
	AdvancedCollaborator coll = new AdvancedCollaborator(null);
	assertEquals(1, coll.i);
}
```

在此示例中，我们可以看到，对于构造函数mock，你需要mock $init方法。你可以传递一个Invocation类型的额外参数，使用该参数可以访问有关mock方法调用的信息，包括正在对其执行调用的实例。

### 3.2 使用Deencapsulation类

JMockit包含一个测试工具类Deencapsulation。顾名思义，它用于解封装对象的状态，使用它，你可以通过访问其他方式无法访问的字段和方法来简化测试。

你可以调用一个方法：

```java
@Test
public void testToCallPrivateMethodsDirectly(){
    Object value = Deencapsulation.invoke(mock, "privateMethod");
    assertEquals("default:", value);
}
```

你还可以设置字段：

```java
@Test
public void testToSetPrivateFieldDirectly(){
    Deencapsulation.setField(mock, "privateField", 10);
    assertEquals(10, mock.methodThatReturnsThePrivateField());
}
```

并获取字段：

```java
@Test
public void testToGetPrivateFieldDirectly(){
    int value = Deencapsulation.getField(mock, "privateField");
    assertEquals(5, value);
}
```

并创建新的类实例：

```java
@Test
public void testToCreateNewInstanceDirectly(){
    AdvancedCollaborator coll = Deencapsulation
        .newInstance(AdvancedCollaborator.class, "foo");
    assertEquals(3, coll.i);
}
```

甚至是内部类的新实例：

```java
@Test
public void testToCreateNewInnerClassInstanceDirectly(){
    InnerCollaborator inner = Deencapsulation
        .newInnerInstance(InnerCollaborator.class, mock);
    assertNotNull(inner);
}
```

如你所见，Deencapsulation类在测试密封类时非常有用。例如，设置一个在私有字段上使用@Autowired注解并且没有setter的类的依赖关系，或者对内部类进行单元测试而不必依赖其容器类的公共接口。

## 4. 在同一个Mock中mock多个接口

假设你要测试一个尚未实现的类，但你确定它会实现多个接口。

通常，你无法在实现类之前对其进行测试，但是使用JMockit，你可以通过使用一个mock对象mock多个接口来预先准备测试。

这可以通过使用泛型和定义扩展多个接口的类型来实现。这种泛型类型既可以为整个测试类定义，也可以只为一个测试方法定义。

例如，我们将通过两种方式为接口List和Comparable创建一个mock：

```java
@RunWith(JMockit.class)
public class AdvancedCollaboratorTest<MultiMock extends List<String> & Comparable<List<String>>> {

    @Mocked
    private MultiMock multiMock;

    @Test
    public void testOnClass() {
        new Expectations() {{
            multiMock.get(5); result = "foo";
            multiMock.compareTo((List<String>) any); result = 0;
        }};
        assertEquals("foo", multiMock.get(5));
        assertEquals(0, multiMock.compareTo(new ArrayList<>()));
    }

    @Test
    public <M extends List<String> & Comparable<List<String>>>
    void testOnMethod(@Mocked M mock) {
        new Expectations() {{
            mock.get(5); result = "foo";
            mock.compareTo((List<String>) any); result = 0;
        }};
        assertEquals("foo", mock.get(5));
        assertEquals(0, mock.compareTo(new ArrayList<>()));
    }
}
```

正如你在第2行中看到的，我们可以通过在类名上使用泛型来为整个测试定义一个新的测试类型。这样，MultiMock将作为一种类型提供，你将能够使用JMockit的任何注解为其创建mock。

在第7到18行中，我们可以看到一个使用为整个测试类定义的多类mock的示例。

如果你只需要一个测试的多接口mock，则可以通过在方法签名上定义泛型类型并将该新泛型的新mock作为测试方法参数传递来实现这一点。在第20到32行中，我们可以看到一个示例，该示例针对与上一个测试中相同的测试行为执行此操作。

## 5. 重用期望和验证

最后，在测试类时，你可能会遇到一遍又一遍地重复相同的期望和/或验证的情况。为了缓解这种情况，你可以轻松地重用两者。

我们通过一个示例来解释它(我们使用[JMockit 101](https://www.baeldung.com/jmockit-101)文章中的Model、Collaborator和Performer类)：

```java
@RunWith(JMockit.class)
public class ReusingTest {

    @Injectable
    private Collaborator collaborator;

    @Mocked
    private Model model;

    @Tested
    private Performer performer;

    @Before
    public void setup(){
        new Expectations(){{
            model.getInfo(); result = "foo"; minTimes = 0;
            collaborator.collaborate("foo"); result = true; minTimes = 0;
        }};
    }

    @Test
    public void testWithSetup() {
        performer.perform(model);
        verifyTrueCalls(1);
    }

    protected void verifyTrueCalls(int calls){
        new Verifications(){{
            collaborator.receive(true); times = calls;
        }};
    }

    final class TrueCallsVerification extends Verifications{
        public TrueCallsVerification(int calls){
            collaborator.receive(true); times = calls;
        }
    }

    @Test
    public void testWithFinalClass() {
        performer.perform(model);
        new TrueCallsVerification(1);
    }
}
```

在这个例子中，你可以在第15到18行中看到，我们正在为每个测试准备一个期望，以便model.getInfo()始终返回“foo”，而collaborator.collaborate()始终期望“foo”作为参数并返回true。我们放置了minTimes = 0语句，因此在测试中不实际使用它们时不会出现失败。

此外，我们还创建了方法verifyTrueCalls(int)以在传递的参数为true时简化对collaborator.receive(boolean)方法的验证。

最后，你还可以创建新类型的特定期望和验证，只需扩展任何Expectations或Verifications类。然后，如果你需要配置行为并在测试中创建所述类型的新实例，则定义一个构造函数，就像我们在第33到43行中所做的那样。

## 6. 总结

在JMockit系列的这一部分中，我们涉及了几个高级主题，这些主题肯定会帮助你进行日常mock和测试。

我们可能会在JMockit上发表更多文章，敬请关注以了解更多信息。

### 6.1 系列文章

该系列的所有文章：

-   [JMockit 101](https://www.baeldung.com/jmockit-101)
-   [JMockit期望指南](https://www.baeldung.com/jmockit-expectations)
-   [JMockit高级用法](https://www.baeldung.com/jmockit-advanced-usage)