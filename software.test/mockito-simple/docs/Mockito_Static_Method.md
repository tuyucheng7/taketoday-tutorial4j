## 1. 概述

在编写测试时，我们经常会遇到需要mock静态方法的情况。**在Mockito的3.4.0版本之前，不可能直接mock静态方法，除非使用PowerMockito**。

在本教程中，我们介绍如何在Mockito 4.0.0版本中mock静态方法。

## 2. 工具类

我们测试的对象是一个简单的工具类：

```java
public class StaticUtils {

    private StaticUtils() {
    }

    public static List<Integer> range(int start, int end) {
        return IntStream.range(start, end)
                .boxed()
                .collect(Collectors.toList());
    }

    public static String name() {
        return "tuyucheng";
    }
}
```

出于演示目的，我们添加了两个方法：一个接收两个参数，另一个只是返回String。

## 3. 为静态方法配置Mockito

在可以使用Mockito mock静态方法之前，我们需要对其进行配置以激活内联MockMaker。

我们需要在项目的src/test/resources/mockito-extensions目录中添加一个名为org.mockito.plugins.MockMaker的文本文件，并添加以下内容：

```text
mock-maker-inline
```

## 4. 关于测试静态方法的简短说明

通常来说，有些人可能会觉得，在编写干净的面向对象代码时，我们不应该需要mock静态类。**因为这通常可能暗示我们的应用程序中存在设计问题或代码坏味道**。

为什么？首先，依赖于静态方法的类具有紧密耦合，其次，它几乎总是导致难以测试的代码。理想情况下，一个类不应该负责获取它的依赖关系，如果可能的话，它们应该被外部注入。

**因此，如果我们可以重构代码以使其更具可测试性，那么这是值得一试的**。当然，这并不总是可行，有时我们确实需要mock静态方法。

## 5. mock无参数静态方法

让我们来看看如何mock StaticUtils类中的name()方法:

```java
class MockedStaticUnitTest {

    @Test
    void givenStaticMethodWithNoArgs_whenMocked_thenReturnsMockSuccessfully() {
        assertThat(StaticUtils.name()).isEqualTo("tuyucheng");

        try (MockedStatic<StaticUtils> utilities = Mockito.mockStatic(StaticUtils.class)) {
            utilities.when(StaticUtils::name).thenReturn("Tuyucheng");
            assertThat(StaticUtils.name()).isEqualTo("Tuyucheng");
        }

        assertThat(StaticUtils.name()).isEqualTo("tuyucheng");
    }
}
```

如前所述，从Mockito 3.4.0开始，我们可以使用Mockito.mockStatic(Class<T> classToMock)方法来mock对静态方法的调用。**此方法为我们的类型返回一个MockedStatic对象，它是一个作用域mock对象**。

因此，在上面的单元测试中，utilities变量表示具有线程本地显式作用域的mock。**需要注意的是，作用域mock必须由激活mock的实体关闭**。这就是为什么我们在try-with-resources结构中定义我们的mock，以便在该作用域块执行完毕时自动关闭mock。

这是一个特别有用的特性，因为它确保我们的静态mock仍然是临时的。正如我们所知，如果我们在测试运行期间使用静态方法调用，由于运行测试的并发性和顺序性，这可能会对我们的测试结果产生不利影响。

除此之外，另一个很好的点是我们的测试仍然会运行得很快，因为Mockito不需要为每个测试替换类加载器。

## 6. mock带参数静态方法

现在让我们看看另一个常见的用例，当我们需要mock一个有参数的方法时：

```java
@Test
void givenStaticMethodWithArgs_whenMocked_thenReturnsMockSuccessfully() {
    assertThat(StaticUtils.range(2, 6)).containsExactly(2, 3, 4, 5);
    
    try (MockedStatic<StaticUtils> utilities = Mockito.mockStatic(StaticUtils.class)) {
        utilities.when(() -> StaticUtils.range(2, 6))
                .thenReturn(Arrays.asList(10, 11, 12));
                
        assertThat(StaticUtils.range(2, 6)).containsExactly(10, 11, 12);
    }
    assertThat(StaticUtils.range(2, 6)).containsExactly(2, 3, 4, 5);
}
```

**这里我们使用相同的方法，但这一次我们在when子句中使用了lambda表达式，在该子句中指定方法以及想要mock的任何参数**。

## 7. 总结

在这篇文章中，我们通过几个例子演示如何使用Mockito mock静态方法。总而言之，Mockito通过lambda为mock静态对象提供了一个优雅的解决方案。