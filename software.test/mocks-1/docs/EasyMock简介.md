## 1. 简介

之前，我们广泛讨论了[JMockit](https://www.baeldung.com/jmockit-101)和[Mockito](https://www.baeldung.com/mockito-final)。

在本教程中，我们将介绍另一个mock工具[EasyMock](http://easymock.org/)。

## 2. Maven依赖

在深入研究之前，让我们将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.easymock</groupId>
    <artifactId>easymock</artifactId>
    <version>4.0.2</version>
    <scope>test</scope>
</dependency>
```

最新版本的依赖项可以从[这里](https://central.sonatype.com/artifact/org.easymock/easymock/5.1.0)获取。

## 3. 核心概念

在生成mock时，**我们可以mock目标对象，指定它的行为，最后验证它是否按预期使用**。  

使用EasyMock的mock包括四个步骤：

1.  创建目标类的mock
2.  记录其预期的行为，包括操作、结果、异常等
3.  在测试中使用mock
4.  验证它是否按预期运行

录制完成后，我们将其切换到“重播”模式，以便在与任何将使用它的对象协作时，mock的行为与录制的行为相同。

最终，我们会验证一切是否按预期进行。

上面提到的四个步骤与[org.easymock.EasyMock](http://easymock.org/api/org/easymock/EasyMock.html)中的方法有关：

1. [mock(...)](http://easymock.org/api/org/easymock/EasyMock.html#mock-java.lang.Class-)：生成目标类的mock，无论是具体类还是接口。一旦创建，mock就处于“记录”模式，这意味着EasyMock将记录mock对象采取的任何动作，并以“重播”模式重播它们
2. [expect(...)](http://easymock.org/api/org/easymock/EasyMock.html#expect-T-)：使用此方法，我们可以为相关的录制操作设置期望值，包括调用、结果和异常
3. [replay(...)](http://easymock.org/api/org/easymock/EasyMock.html#replay-java.lang.Object...-)：将给定的mock切换到“重播”模式。然后，任何触发先前记录的方法调用的操作都将重放“记录的结果”
4. [verify(...)](http://easymock.org/api/org/easymock/EasyMock.html#verify-java.lang.Object...-)：验证是否满足所有期望，并且没有在mock上执行意外调用

在下一节中，我们通过真实的示例演示这些步骤是如何工作的。

## 4. Mock的一个实际例子

假设我们有一个Tuyucheng博客的读者，他/她喜欢浏览网站上的文章，然后他/她尝试写文章。

让我们从创建以下模型开始：

```java
public class TuyuchengReader {

    private ArticleReader articleReader;
    private ArticleWriter articleWriter;

    // constructors

    public TuyuchengReader readNext() {
        return articleReader.next();
    }

    public List<TuyuchengArticle> readTopic(String topic) {
        return articleReader.ofTopic(topic);
    }

    public String write(String title, String content) {
        return articleWriter.write(title, content);
    }
}
```

在这个模型中，我们有两个私有成员：articleReader(一个具体类)和articleWriter(一个接口)。

接下来，我们将mock它们以验证TuyuchengReader的行为。

## 5. 使用Java代码Mock

让我们从mock一个ArticleReader开始。

### 5.1 典型的Mock

我们希望在读者跳过一篇文章时调用articleReader.next()方法：

```java
class TuyuchengReaderUnitTest {
    private TuyuchengReader tuyuchengReader;
    private ArticleReader mockArticleReader;
    private ArticleWriter mockArticleWriter;

    @Test
    void givenTuyuchengReader_whenReadNext_thenNextArticleRead() {
        mockArticleReader = mock(ArticleReader.class);
        tuyuchengReader = new TuyuchengReader(mockArticleReader);

        expect(mockArticleReader.next()).andReturn(null);
        replay(mockArticleReader);

        TuyuchengArticle article = tuyuchengReader.readNext();
        verify(mockArticleReader);
        assertNull(article);
    }
}
```

在上面的示例代码中，我们严格遵循使用EasyMock的四个步骤并mock ArticleReader类。

**虽然我们并不关心mockArticleReader.next()返回什么，但我们仍然需要使用expect(...).andReturn(...)为mockArticleReader.next()指定一个返回值**。

使用expect(...)，EasyMock期望该方法返回一个值或抛出一个异常。

如果我们只是这样做：

```java
mockArticleReader.next();
replay(mockArticleReader); 
```

则EasyMock会抱怨这一点，因为如果方法返回任何内容，它需要调用expect(...).andReturn(...)。

如果它是一个void方法，我们可以像这样使用[expectLastCall()](http://easymock.org/api/org/easymock/EasyMock.html#expectLastCall--)来期待它的操作：

```java
mockArticleReader.someVoidMethod();
expectLastCall();
replay(mockArticleReader); 
```

### 5.2 重播顺序

如果我们需要按特定顺序重播动作，我们可以更严格：

```java
@Test
void givenTuyuchengReader_whenReadNextAndSkimTopics_thenAllAllowed() {
	mockArticleReader = strictMock(ArticleReader.class);
	tuyuchengReader = new TuyuchengReader(mockArticleReader);
    
	expect(mockArticleReader.next()).andReturn(null);
	expect(mockArticleReader.ofTopic("easymock")).andReturn(null);
	replay(mockArticleReader);
    
	tuyuchengReader.readNext();
	tuyuchengReader.readTopic("easymock");
    
	verify(mockArticleReader);
}
```

在这段代码中，**我们使用[strictMock(...)](http://easymock.org/api/org/easymock/EasyMock.html#strictMock-java.lang.Class-)来检查方法调用的顺序**。对于由mock(...)和strictMock(...)创建的mock，任何意外的方法调用都会导致AssertionError。

**要允许任何方法调用mock，我们可以使用niceMock(...)**：

```java
@Test
void givenTuyuchengReader_whenReadNextAndOthers_thenAllowed() {
	mockArticleReader = niceMock(ArticleReader.class);
	tuyuchengReader = new TuyuchengReader(mockArticleReader);
    
	expect(mockArticleReader.next()).andReturn(null);
	replay(mockArticleReader);
    
	tuyuchengReader.readNext();
	tuyuchengReader.readTopic("easymock");
    
	verify(mockArticleReader);
}
```

在这里，我们没想到tuyuchengReader.readTopic(...)会被调用，但EasyMock不会抱怨。使用niceMock(...)，EasyMock现在只关心目标对象是否执行了预期的操作。

### 5.3 Mock异常抛出

现在，让我们继续mock接口ArticleWriter，以及如何处理预期的Throwables：

```java
@Test
void givenTuyuchengReader_whenWriteMaliciousContent_thenArgumentIllegal() {
	mockArticleWriter = mock(ArticleWriter.class);
	tuyuchengReader = new TuyuchengReader(mockArticleWriter);
    
	expect(mockArticleWriter.write("easymock", "<body onload=alert('tuyucheng')>")).andThrow(new IllegalArgumentException());
	replay(mockArticleWriter);
    
	Exception expectedException = null;
	try {
		tuyuchengReader.write("easymock", "<body onload=alert('tuyucheng')>");
	} catch (Exception exception) {
		expectedException = exception;
	}
    
	verify(mockArticleWriter);
    
	assertEquals(IllegalArgumentException.class, expectedException.getClass());
}
```

在上面的代码片段中，我们希望articleWriter足够可靠，可以检测[XSS(跨站点脚本)](https://www.owasp.org/index.php/Cross-site_Scripting_(XSS))攻击。

因此，当读者试图在文章内容中注入恶意代码时，write应该抛出一个IllegalArgumentException。我们使用expect(...).andThrow(...)记录了这种预期的行为。

## 6. 使用注解的mock

EasyMock还支持使用注解注入mock。**要使用它们，我们需要使用[EasyMockRunner](http://easymock.org/api/org/easymock/EasyMockRunner.html)运行我们的单元测试，以便它处理[@Mock](http://easymock.org/api/org/easymock/Mock.html)和[@TestSubject](http://easymock.org/api/org/easymock/TestSubject.html)注解**。

让我们重写前面的代码片段：

```java
@RunWith(EasyMockRunner.class)
public class TuyuchengReaderAnnotatedTest {

    @Mock
    ArticleReader mockArticleReader;

    @TestSubject
    TuyuchengReader tuyuchengReader = new TuyuchengReader();

    @Test
    public void givenTuyuchengReader_whenReadNext_thenNextArticleRead() {
        expect(mockArticleReader.next()).andReturn(null);
        replay(mockArticleReader);
        tuyuchengReader.readNext();
        verify(mockArticleReader);
    }
}
```

等效于mock(...)，mock将被注入到使用@Mock注解的字段中。这些mock将被注入到使用@TestSubject标注的类的字段中。

在上面的代码段中，我们没有显式初始化TuyuchengReader中的articleReader字段。当调用tuyuchengReader.readNext()时，我们可以隐式调用mockArticleReader。

这是因为mockArticleReader被注入到articleReader字段。

**请注意，如果我们想使用另一个测试Runner而不是EasyMockRunner，我们可以使用JUnit测试Rule [EasyMockRule](http://easymock.org/api/org/easymock/EasyMockRule.html)**：

```java
public class TuyuchengReaderAnnotatedWithRuleUnitTest {

    @Rule
    public EasyMockRule mockRule = new EasyMockRule(this);

    // Mock Object with Annotation ...

    @Test
    public void givenTuyuchengReader_whenReadNext_thenNextArticleRead() {
        expect(mockArticleReader.next()).andReturn(null);
        replay(mockArticleReader);
        tuyuchengReader.readNext();
        verify(mockArticleReader);
    }
}
```

## 7. 使用EasyMockSupport进行Mock 

有时我们需要在单个测试中引入多个mock，我们必须手动重复：

```java
replay(A);
replay(B);
replay(C);
//...
verify(A);
verify(B);
verify(C); 
```

这很难看，我们需要一个优雅的解决方案。

幸运的是，**我们在EasyMock中有一个EasyMockSupport类来帮助处理这个问题。它有助于跟踪mock，以便我们可以像这样批量重播和验证它们**：

```java
@RunWith(EasyMockRunner.class)
public class TuyuchengReaderMockSupportUnitTest extends EasyMockSupport {

    // Mock Object With Annotation ...

    @Test
    public void givenTuyuchengReader_whenReadAndWriteSequencially_thenWorks() {
        expect(mockArticleReader.next())
              .andReturn(null)
              .times(2)
              .andThrow(new NoSuchElementException());
        expect(mockArticleWriter.write("title", "content")).andReturn("Tuyucheng-201801");
        replayAll();

        Exception expectedException = null;
        try {
            for (int i = 0; i < 3; i++) {
                tuyuchengReader.readNext();
            }
        } catch (Exception exception) {
            expectedException = exception;
        }
        String articleId = tuyuchengReader.write("title", "content");
        verifyAll();

        assertEquals(NoSuchElementException.class, expectedException.getClass());
        assertEquals("Tuyucheng-201801", articleId);
    }
}
```

在这里，我们mock了articleReader和articleWriter。当将这些mock设置为“重播”模式时，我们使用了EasyMockSupport提供的静态方法[replayAll()](http://easymock.org/api/org/easymock/EasyMockSupport.html#replayAll--)，并使用[verifyAll()](http://easymock.org/api/org/easymock/EasyMockSupport.html#verifyAll--)批量验证它们的行为。

我们还在expect阶段引入了[times(...)](http://easymock.org/api/org/easymock/IExpectationSetters.html#times-int-)方法。它有助于指定我们希望调用该方法的次数，这样我们就可以避免引入重复代码。

我们也可以通过委托使用EasyMockSupport：

```java
public class TuyuchengReaderMockDelegationUnitTest {

    EasyMockSupport easyMockSupport = new EasyMockSupport();

    @Test
    public void givenTuyuchengReader_whenReadAndWriteSequencially_thenWorks() {
        ArticleReader mockArticleReader = easyMockSupport.createMock(ArticleReader.class);
        ArticleWriter mockArticleWriter = easyMockSupport.createMock(ArticleWriter.class);
        TuyuchengReader tuyuchengReader = new TuyuchengReader(mockArticleReader, mockArticleWriter);

        expect(mockArticleReader.next()).andReturn(null);
        expect(mockArticleWriter.write("title", "content")).andReturn("");
        easyMockSupport.replayAll();

        tuyuchengReader.readNext();
        tuyuchengReader.write("title", "content");
        easyMockSupport.verifyAll();
    }
}
```

之前，我们使用静态方法或注解来创建和管理mock，在幕后，这些静态和带注解的mock由全局EasyMockSupport实例控制。

在这里，我们显式地实例化了它，并通过委托将所有这些mock置于我们自己的控制之下。如果我们的测试代码与EasyMock有任何名称冲突或有任何类似情况，这可能有助于避免混淆。

## 8. 总结

在本文中，我们简要介绍了EasyMock的基本用法，关于如何生成mock对象，记录和重播它们的行为，以及验证它们的行为是否正确。

如果你可能感兴趣，请查看[本文](Mockito-EasyMock-JMockit.md)以了解EasyMock、Mockito和JMockit的区别。