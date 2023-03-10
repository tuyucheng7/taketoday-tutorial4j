## 1. 概述

在本教程中，我们将分析使用非抽象方法对抽象类进行单元测试的各种用例和可能的替代解决方案。

请注意，**测试抽象类几乎总是应该通过具体实现的公共API**，因此除非你确定自己在做什么，否则不要应用以下技术。

## 2. Maven依赖

让我们从Maven依赖项开始：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>2.8.9</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.powermock</groupId>
    <artifactId>powermock-module-junit4</artifactId>
    <version>1.7.4</version>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.powermock</groupId>
    <artifactId>powermock-api-mockito2</artifactId>
    <version>1.7.4</version>
    <scope>test</scope>
</dependency>
```

您可以在[Maven Central](https://central.sonatype.com/artifact/org.junit.jupiter/junit-jupiter-engine/5.9.2)上找到这些库的最新版本 。

JUnit 5不完全支持Powermock。此外，powermock-module-junit4仅用于第5节中介绍的一个示例。

## 3. 独立非抽象方法

让我们考虑一个情况，当我们有一个带有公共非抽象方法的抽象类时：

```java
public abstract class AbstractIndependent {

    public abstract int abstractFunc();

    public String defaultImpl() {
        return "DEFAULT-1";
    }
}
```

如果我们想测试defaultImpl()方法，有两种可能的解决方案 - 使用具体的实现类，或者使用Mockito。

### 3.1 使用具体的类

创建一个扩展AbstractIndependent类的具体实现类，并使用它来测试该方法：

```java
public class ConcreteImpl extends AbstractIndependent {

    @Override
    public int abstractFunc() {
        return 4;
    }
}
```

```java
@Test
void givenNonAbstractMethod_whenConcreteImpl_testCorrectBehaviour() {
    ConcreteImpl conClass = new ConcreteImpl();
    String actual = conClass.defaultImpl();
    assertEquals("DEFAULT-1", actual);
}
```

这种解决方案的缺点是需要创建包含所有抽象方法的虚拟实现的具体类。

### 3.2 使用Mockito

或者，我们可以使用Mockito创建一个mock：

```java
@Test
void givenNonAbstractMethod_whenMockitoMock_testCorrectBehaviour() {
    AbstractIndependent absClass = Mockito.mock(AbstractIndependent.class, Mockito.CALLS_REAL_METHODS);
    assertEquals("DEFAULT-1", absClass.defaultImpl());
}
```

**这里最重要的部分是Mock的创建，我们使用Mockito.CALLS_REAL_METHODS指定调用方法时使用真实代码**。

## 4. 从非抽象方法调用抽象方法

在这种情况下，非抽象方法定义了全局执行流程，而抽象方法可以根据用例以不同的方式编写：

```java
public abstract class AbstractMethodCalling {

    public abstract String abstractFunc();

    public String defaultImpl() {
        String res = abstractFunc();
        return (res == null) ? "Default" : (res + " Default");
    }
}
```

为了测试这段代码，我们可以使用与之前相同的两种方法 - 创建一个具体的类或使用Mockito创建一个Mock：

```java
private AbstractMethodCalling cls;

@BeforeEach
void setup() {
    cls = Mockito.mock(AbstractMethodCalling.class);
}

@Test
void givenDefaultImpl_whenMockAbstractFunc_thenExpectedBehaviour() {
    Mockito.when(cls.abstractFunc()).thenReturn("Abstract");
    Mockito.doCallRealMethod().when(cls).defaultImpl();

    // validate result by mock abstractFun's behaviour
    assertEquals("Abstract Default", cls.defaultImpl());

    // check the value with null response from abstract method
    Mockito.doReturn(null).when(cls).abstractFunc();
    assertEquals("Default", cls.defaultImpl());
}
```

在这里，abstractFunc()使用我们指定的测试返回值进行stub，这意味着当我们调用非抽象方法defaultImpl()时，它将使用这个stub。

## 5. 具有测试障碍的非抽象方法

在某些情况下，我们要测试的方法调用包含测试障碍的私有方法。

在测试目标方法之前，我们需要绕过阻碍测试方法：

```java
public abstract class AbstractPrivateMethods {

    public abstract int abstractFunc();

    public String defaultImpl() {
        return getCurrentDateTime() + "DEFAULT-1";
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }
}
```

在此示例中，defaultImpl()方法调用私有方法getCurrentDateTime()。这个私有方法在运行时获取当前时间，这在我们的单元测试中应该避免。

现在，为了Mock这个私有方法的标准行为，我们甚至不能使用Mockito，因为它无法控制私有方法。

相反，我们需要使用[PowerMock](https://www.baeldung.com/intro-to-powermock)(**请注意，此示例仅适用于JUnit 4，因为JUnit 5不支持此依赖项**)：

```java
@RunWith(PowerMockRunner.class)
@PrepareForTest(AbstractPrivateMethods.class)
public class AbstractPrivateMethodsUnitTest {

    @Test
    public void givenNonAbstractMethodAndCallPrivateMethod_whenMockPrivateMethod_thenVerifyBehaviour() throws Exception {
        AbstractPrivateMethods mockClass = PowerMockito.mock(AbstractPrivateMethods.class);

        String dateTime = LocalDateTime.now().toString();

        PowerMockito.doCallRealMethod().when(mockClass).defaultImpl();
        PowerMockito.doReturn(dateTime).when(mockClass, "getCurrentDateTime");// .thenReturn(dateTime);
        String actual = mockClass.defaultImpl();

        assertEquals(dateTime + "DEFAULT-1", actual);
    }
}
```

此示例中的重要部分：

+ @RunWith将PowerMockRunner定义为测试的Runner
+ @PrepareForTest(AbstractPrivateMethods.class)告诉PowerMock为以后的处理准备类

有趣的是，我们告诉PowerMock stub私有方法getCurrentDateTime()。PowerMock将使用反射来找到它，因为该方法无法从外部访问。

因此，当我们调用defaultImpl()时，将调用为私有方法创建的stub而不是实际的方法。

## 6. 访问实例字段的非抽象方法

抽象类可以具有使用类字段实现的内部状态，字段的值可能会对要测试的方法产生重大影响。

如果一个字段是公共的或受保护的，我们可以很容易地从测试方法中访问它。

但如果它是私有的，我们必须使用PowerMockito：

```java
public abstract class AbstractInstanceFields {
    protected int count;
    private boolean active = false;

    public abstract int abstractFunc();

    public String testFunc() {
        String response;
        if (count > 5)
            response = "Overflow";
        else
            response = active ? "Added" : "Blocked";
        return response;
    }
}
```

在这里，testFunc()方法根据实例级字段count和active返回相应的值。

在测试testFunc()时，我们可以通过访问使用Mockito创建的实例来更改count字段的值。

另一方面，为了测试私有字段active的行为，我们将不得不再次使用PowerMockito及其Whitebox类：

```java
class AbstractInstanceFieldsUnitTest {

    @Test
    void givenNonAbstractMethodAndPrivateField_whenPowerMockitoAndActiveFieldTrue_thenCorrectBehaviour() {
        AbstractInstanceFields instClass = PowerMockito.mock(AbstractInstanceFields.class);
        PowerMockito.doCallRealMethod().when(instClass).testFunc();

        Whitebox.setInternalState(instClass, "active", true);

        // compare the expected result with actual
        assertEquals("Added", instClass.testFunc());
    }
}
```

我们正在使用PowerMockito.mock()创建一个存根类，并且我们使用Whitebox类来控制对象的内部状态。

active字段的值更改为true。

## 7. 总结

在本教程中，我们看到了涵盖大量用例的多个示例。根据所遵循的设计，我们可以在更多场景中使用抽象类。

此外，为抽象类方法编写单元测试与为普通类和方法编写单元测试一样重要。我们可以使用不同的技术或可用的不同测试支持库对它们进行测试。