## 1. 概述

在这个简短的教程中，我们将使用[Junit 5](https://www.baeldung.com/junit-5)中可用的@BeforeAll和@AfterAll注解来实现非静态方法。

## 2. @BeforeAll和@AfterAll标注非静态方法

在进行单元测试时，我们有时可能希望在非静态的setup和teardowm方法上使用@BeforeAll和@AfterAll注解 - 例如在@Nested测试类中或作为接口默认方法。

让我们创建一个测试类，其中@BeforeAll和@AfterAll方法为非静态：

```java
class BeforeAndAfterAnnotationsUnitTest {
    String input;
    Long result;

    @BeforeAll
    void setup() {
        input = "77";
    }

    @AfterAll
    void teardown() {
        input = null;
        result = null;
    }

    @Test
    void whenConvertStringToLong_thenResultShouldBeLong() {
        result = Long.valueOf(input);
        Assertions.assertEquals(77L, result);
    }
}
```

如果我们运行上面的代码，它会抛出一个异常：

```shell
org.junit.platform.commons.JUnitException: 
@BeforeAll method 'void cn.tuyucheng.taketoday.junit5.nonstatic.BeforeAndAfterAnnotationsUnitTest.setup()' 
must be static unless the test class is annotated with @TestInstance(Lifecycle.PER_CLASS).
```

现在让我们看看如何避免这种情况。

## 3. @TestInstance注解

我们将使用[@TestInstance](https://www.baeldung.com/junit-testinstance-annotation)注解来配置测试的生命周期。如果我们没有在测试类上声明它，则生命周期模式默认为PER_METHOD。因此，**为了防止我们的测试类抛出JUnitException，我们需要使用@TestInstance(TestInstance.Lifecycle.PER_CLASS)对其进行标注**。

让我们修改我们的测试类并添加@TestInstance(TestInstance.Lifecycle.PER_CLASS)：

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BeforeAndAfterAnnotationsUnitTest {

    String input;
    Long result;

    @BeforeAll
    public void setup() {
        input = "77";
    }

    @AfterAll
    public void teardown() {
        input = null;
        result = null;
    }

    @Test
    public void whenConvertStringToLong_thenResultShouldBeLong() {
        result = Long.valueOf(input);
        Assertions.assertEquals(77l, result);
    }
}
```

在这种情况下，我们的测试运行成功。

## 4. 总结

在这篇简短的文章中，我们学习了如何在非静态方法上使用@BeforeAll和@AfterAll注解。首先，我们从一个简单的非静态方法开始，以展示如果我们不包含@TestInstance注解会发生什么。然后，我们使用@TestInstance(TestInstance.Lifecycle.PER_CLASS)标注我们的测试类以解决出现的错误。