## 1. 概述

在本教程中，我们将了解[MockK](https://mockk.io/)库的一些基本功能。

## 2.模拟K

在[Kotlin](https://www.baeldung.com/kotlin)中，所有类和方法都是最终的。虽然这有助于我们编写不可变代码，但它也会在测试期间引起一些问题。

大多数 JVM 模拟库都存在模拟或存根最终类的问题。当然，我们可以将“ open ”关键字添加到我们想要模拟的类和方法中。但是仅仅为了模拟一些代码而改变类并不是最好的方法。

MockK 库来了，它提供对 Kotlin 语言特性和结构的支持。MockK 为模拟类构建代理。这会导致一些性能下降，但 MockK 给我们带来的整体好处是值得的。

## 三、安装

安装尽可能简单。我们只需要将[mockk](https://search.maven.org/search?q=g:io.mockk a:mockk)依赖项添加到我们的 Maven 项目中：

```xml
<dependency>
    <groupId>io.mockk</groupId>
    <artifactId>mockk</artifactId>
    <version>1.9.3</version>
    <scope>test</scope>
</dependency>
```

对于[Gradle](https://www.baeldung.com/gradle)，我们需要将其添加为测试依赖项：

```groovy
testImplementation "io.mockk:mockk:1.9.3"
```

## 4. 基本示例

让我们创建一个我们想要模拟的服务：

```java
class TestableService {
    fun getDataFromDb(testParameter: String): String {
        // query database and return matching value
    }

    fun doSomethingElse(testParameter: String): String {
        return "I don't want to!"
    }
}
```

这是一个模拟TestableService的示例测试：

```java
@Test
fun givenServiceMock_whenCallingMockedMethod_thenCorrectlyVerified() {
    // given
    val service = mockk<TestableService>()
    every { service.getDataFromDb("Expected Param") } returns "Expected Output"
 
    // when
    val result = service.getDataFromDb("Expected Param")
 
    // then
    verify { service.getDataFromDb("Expected Param") }
    assertEquals("Expected Output", result)
}
```

为了定义模拟对象，我们使用了mockk<…>()方法。

在下一步中，我们定义了模拟的行为。为此，我们创建了一个every块来描述应该为哪个调用返回什么响应。

最后，我们使用验证块来验证是否按预期调用了模拟。

## 5.注解示例

可以使用 MockK 注解来创建各种模拟。让我们创建一个需要两个TestableService实例的服务：

```java
class InjectTestService {
    lateinit var service1: TestableService
    lateinit var service2: TestableService

    fun invokeService1(): String {
        return service1.getDataFromDb("Test Param")
    }
}
```

InjectTestService包含两个相同类型的字段。这对 MockK 来说不是问题。MockK 尝试按名称匹配属性，然后按类或超类。将对象注入私有字段也没有问题。

让我们使用注解在测试中模拟InjectTestService ：

```java
class AnnotationMockKUnitTest {

    @MockK
    lateinit var service1: TestableService

    @MockK
    lateinit var service2: TestableService

    @InjectMockKs
    var objectUnderTest = InjectTestService()

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    // Tests here
    ...
}
```

在上面的示例中，我们使用了@InjectMockKs注解。这指定了一个对象，其中定义的模拟应该被注入。默认情况下，它会注入尚未分配的变量。我们可以使用@OverrideMockKs来覆盖已经有值的字段。

MockK 需要MockKAnnotations.init(…)在声明带有注解的变量的对象上调用。对于[Junit5](https://www.baeldung.com/junit-5)，它可以替换为@ExtendWith(MockKExtension::class)。

## 6.间谍

Spy 只允许模拟某个类的特定部分。例如，它可用于模拟TestableService 中的特定方法：

```java
@Test
fun givenServiceSpy_whenMockingOnlyOneMethod_thenOtherMethodsShouldBehaveAsOriginalObject() {
    // given
    val service = spyk<TestableService>()
    every { service.getDataFromDb(any()) } returns "Mocked Output"
 
    // when checking mocked method
    val firstResult = service.getDataFromDb("Any Param")
 
    // then
    assertEquals("Mocked Output", firstResult)
 
    // when checking not mocked method
    val secondResult = service.doSomethingElse("Any Param")
 
    // then
    assertEquals("I don't want to!", secondResult)
}
```

在示例中，我们使用spyk方法创建了一个间谍对象。我们也可以使用@SpyK注解来实现相同的目的：

```java
class SpyKUnitTest {

    @SpyK
    lateinit var service: TestableService

    // Tests here
}
```

## 7.轻松的模拟

如果我们尝试调用未指定返回值的方法，典型的模拟对象将抛出MockKException 。

如果我们不想描述每个方法的行为，那么我们可以使用轻松的模拟。这种模拟为每个函数提供了默认值。例如，String返回类型将返回一个空String。这是一个简短的例子：

```java
@Test
fun givenRelaxedMock_whenCallingNotMockedMethod_thenReturnDefaultValue() {
    // given
    val service = mockk<TestableService>(relaxed = true)
 
    // when
    val result = service.getDataFromDb("Any Param")
 
    // then
    assertEquals("", result)
}
```

在示例中，我们使用带有relaxed属性的mockk方法来创建一个 relaxed 模拟对象。我们也可以使用@RelaxedMockK注解：

```java
class RelaxedMockKUnitTest {

    @RelaxedMockK
    lateinit var service: TestableService

    // Tests here
}
```

## 8.对象模拟

Kotlin 提供了一种使用object关键字声明单例的简单方法：

```java
object TestableService {
    fun getDataFromDb(testParameter: String): String {
        // query database and return matching value
    }
}
```

然而，大多数模拟库都存在模拟 Kotlin 单例实例的问题。正因为如此，MockK 提供了mockkObject方法。让我们来看看：

```java
@Test
fun givenObject_whenMockingIt_thenMockedMethodShouldReturnProperValue(){
    // given
    mockkObject(TestableService)
 
    // when calling not mocked method
    val firstResult = service.getDataFromDb("Any Param")
 
    // then return real response
    assertEquals(/ DB result /, firstResult)

    // when calling mocked method
    every { service.getDataFromDb(any()) } returns "Mocked Output"
    val secondResult = service.getDataFromDb("Any Param")
 
    // then return mocked response
    assertEquals("Mocked Output", secondResult)
}
```

## 9.分层模拟

MockK 的另一个有用的特性是模拟分层对象的能力。首先，让我们创建一个分层对象结构：

```java
class Foo {
    lateinit var name: String
    lateinit var bar: Bar
}

class Bar {
    lateinit var nickname: String
}
```

Foo类包含一个Bar 类型的字段。现在，我们只需一个简单的步骤就可以模拟这个结构。让我们模拟名称和昵称字段：

```java
@Test
fun givenHierarchicalClass_whenMockingIt_thenReturnProperValue() {
    // given
    val foo = mockk<Foo> {
        every { name } returns "Karol"
        every { bar } returns mockk {
            every { nickname } returns "Tomato"
        }
    }
 
    // when
    val name = foo.name 
    val nickname = foo.bar.nickname
 
    // then
    assertEquals("Karol", name)
    assertEquals("Tomato", nickname)
}
```

## 10. 捕获参数

如果我们需要捕获传递给方法的参数，那么我们可以使用CapturingSlot或MutableList。当我们想要在答案块中包含一些自定义逻辑或者我们只需要验证传递的参数的值时，它很有用。这是CapturingSlot 的示例：

```java
@Test
fun givenMock_whenCapturingParamValue_thenProperValueShouldBeCaptured() {
    // given
    val service = mockk<TestableService>()
    val slot = slot<String>()
    every { service.getDataFromDb(capture(slot)) } returns "Expected Output"
 
    // when
    service.getDataFromDb("Expected Param")
 
    // then
    assertEquals("Expected Param", slot.captured)
}
```

MutableList可用于捕获和存储所有方法调用的特定参数值：

```java
@Test
fun givenMock_whenCapturingParamsValues_thenProperValuesShouldBeCaptured() {
    // given
    val service = mockk<TestableService>()
    val list = mutableListOf<String>()
    every { service.getDataFromDb(capture(list)) } returns "Expected Output"
 
    // when
    service.getDataFromDb("Expected Param 1")
    service.getDataFromDb("Expected Param 2")
 
    // then
    assertEquals(2, list.size)
    assertEquals("Expected Param 1", list[0])
    assertEquals("Expected Param 2", list[1])
}
```

## 11.总结

在本文中，我们讨论了 MockK 最重要的特性。MockK 是一个强大的 Kotlin 语言库，提供了很多有用的功能。关于MockK的更多信息，我们可以查看[MockK官网的文档](https://mockk.io/)。