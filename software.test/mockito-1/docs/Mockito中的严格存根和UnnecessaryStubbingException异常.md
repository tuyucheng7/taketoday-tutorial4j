## 1. 概述

**在这个教程中，我们将了解Mockito中的UnnecessaryStubbingException**。此异常是我们在错误使用stub时可能会遇到的常见异常。

我们将首先解释strict stubbing背后的原理，以及为什么Mockito默认情况下鼓励使用它。
然后，我们将介绍这个异常的确切含义，以及在什么情况下会发生。
最后，我们将通过一个示例，说明如何在测试中解决此异常。

## 2. Strict Stubbing

使用Mockito 1.x版本时，可以无限制地配置mock并与之交互。这意味着，随着时间的推移，测试往往会变得过于复杂，有时更难调试。

**自2.x版本以来，Mockito一直在引入新功能，推动框架朝着“strictness”的方向发展**。这背后的主要目标是：

+ 检测测试代码中未使用的stub。
+ 减少重复的测试代码和不必要的测试代码。
+ 通过删除“dead code”来促进更清爽的测试。
+ 帮助提高可调试性和生产力。

**遵循这些原则有助于我们通过消除不必要的测试代码来编写更清爽的测试**。还可以帮助我们避免复制粘贴错误以及其他开发人员的疏忽。

总而言之，strict stubbing指出不必要的stub，检测stub参数不匹配，并使我们的测试更加DRY(Don't Repeat Yourself)。
这有利于形成一个干净和可维护的代码库。

### 2.1 配置Strict Stubs

从Mockito 2.x开始，在使用以下任一方式初始化我们的mock时，默认使用strict stubbing：

+ MockitoJUnitRunner
+ MockitoJUnit.rule()
+ MockitoExtension

**Mockito强烈建议使用以上任何一种**。然而，当我们不使用以上方式时，还有另一种方法可以在我们的测试中启用strict stubbing：

```text
Mockito.mockitoSession()
  .initMocks(this)
  .strictness(Strictness.STRICT_STUBS)
  .startMocking();
```

**最后一个重要的点是，在Mockito 3.0中，所有的stub都将是“strict”，并在默认情况下进行验证**。

## 3. UnnecessaryStubbingException

**简单地说，不必要的stub是在测试执行期间从未实现的stub方法调用**。

我们来看一个简单的例子：

```java

@RunWith(MockitoJUnitRunner.class)
public class MockitoUnnecessaryStubUnitTest {

    @Rule
    public ExpectedTestFailureRule rule = new ExpectedTestFailureRule(MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS));

    @Mock
    private ArrayList<String> mockList;

    @Test
    public void givenUnusedStub_whenInvokingGetThenThrowUnnecessaryStubbingException() {
        when(mockList.add("one")).thenReturn(true); // this won't get called
        when(mockList.get(anyInt())).thenReturn("hello");
        assertEquals("List should contain hello", "hello", mockList.get(1));
    }
}
```

当我们运行这个单元测试时，Mockito会检测到未使用的stub并抛出UnnecessaryStubbingException：

```text
org.mockito.exceptions.misusing.UnnecessaryStubbingException: 
Unnecessary stubbings detected.
Clean & maintainable test code requires zero unnecessary code.
Following stubbings are unnecessary (click to navigate to relevant line of code):
  1. -> at cn.tuyucheng.taketoday.misusing.MockitoUnnecessaryStubUnitTest.givenUnusedStub_whenInvokingGetThenThrowUnnecessaryStubbingException(MockitoUnnecessaryStubUnitTest.java:37)
Please remove unnecessary stubbings or use 'lenient' strictness. More info: javadoc for UnnecessaryStubbingException class.
```

值得庆幸的是，从错误消息中可以清楚地看出问题所在，并且还能够定位到精确的错误行。

为什么会发生这种情况？好吧，当我们使用参数“one”调用add方法时，第一个when调用将我们的mock配置为返回true。
但是，我们没有在单元测试执行的其余部分调用此方法。

**Mockito告诉我们，我们的第一个when语句是多余的，也许我们在配置stub时出错了**。

虽然这个例子很简单，但是当我们mock复杂的对象层次结构时，这对我们快速定位错误很有帮助。

## 4. 绕过Strict Stubbing

最后，让我们看看如何绕过strict stubbing。

有时我们需要将特定的stub配置为lenient，同时保存所有其他stub和mock为strict：

```java
public class MockitoUnnecessaryStubUnitTest {

    @Test
    public void givenLenientStub_whenInvokingGetThenDontThrowUnnecessaryStubbingException() {
        lenient().when(mockList.add("one")).thenReturn(true);
        when(mockList.get(anyInt())).thenReturn("hello");

        assertEquals("List should contain hello", "hello", mockList.get(1));
    }
}
```

**在上面的示例中，我们使用静态方法Mockito.lenient()来启用对我们的mock List的add方法的lenient stub**。

lenient stub绕过“strict stubbing”验证规则。例如，当stub被声明为lenient时，将不会检查潜在的stub问题，例如前面描述的不必要的stub。

## 5. 总结

在这篇简短的文章中，我们介绍了Mockito中Strict Stubbing的概念，详细说明了为什么引入它以及为什么它很重要。

然后我们演示了一个UnnecessaryStubbingException的例子，最后介绍了如何将stub配置为lenient。