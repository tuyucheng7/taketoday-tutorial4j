## 1. 概述

在这个简短的教程中，我们将使用JUnit 5的@Timeout注解以声明式的方式为单元测试设置超时。我们将讨论使用它的不同方式，然后我们将看到它如何与[@ParameterizedTest](https://www.baeldung.com/parameterized-tests-junit-5)和[@Nested](https://www.baeldung.com/junit-5-nested-test-classes)测试交互。

## 2. @Timeout注解

我们可以使用JUnit 5的@Timeout标注单元测试以指定它可以运行的最大秒数；如果超过此值，测试将失败并出现java.util.concurrent.TimeoutException：

```java
@Test
@Timeout(1)
void shouldFailAfterOneSecond() throws InterruptedException {
    Thread.sleep(10_000);
}
```

### 2.1 Value和Unit属性

我们已经学习了如何通过指定测试失败的秒数来指定测试的超时。但是，我们可以利用注解的value和unit属性来指定不同的度量单位：

```java
@Test
@Timeout(value = 2, unit = TimeUnit.MINUTES)
void shouldFailAfterTwoMinutes() throws InterruptedException {
    Thread.sleep(10_000);
}
```

### 2.2 ThreadMode属性

假设我们有一个缓慢的测试，因此有一个很大的超时。为了有效地运行它，我们应该在不同的线程上运行这个测试，而不是阻塞其他测试。为了实现这一点，我们可以使用[JUnit 5的并行测试执行](https://www.baeldung.com/junit-5-parallel-tests)。

另一方面，@Timeout注解本身允许我们通过它的threadMode属性优雅地做到这一点：

```java
@Test
@Timeout(value = 5, unit = TimeUnit.MINUTES, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
void shouldUseADifferentThread() throws InterruptedException {
    System.out.println(Thread.currentThread().getName());
    Thread.sleep(10_000);
}
```

我们可以通过运行测试并打印当前线程的名称来检查这一点；它应该打印类似“junit-timeout-thread-1”的内容。

## 3. @Timeout的目标

正如前面强调的那样，@Timeout注解可以方便地应用于各个测试方法。但是，也可以通过将注解放在类级别来为整个类中的每个测试指定默认超时持续时间。因此，如果超过该值，没有覆盖类级超时的测试将失败：

```java
@Timeout(5)
class TimeoutUnitTest {

    @Test
    @Timeout(1)
    void shouldFailAfterOneSecond() throws InterruptedException {
        Thread.sleep(10_000);
    }

    @Test
    void shouldFailAfterDefaultTimeoutOfFiveSeconds() throws InterruptedException {
        Thread.sleep(10_000);
    }
}
```

### 3.1 @Timeout和@Nested测试

JUnit 5的[@Nested](https://www.baeldung.com/junit-5-nested-test-classes)注解可以为单元测试创建内部类。我们可以将它与@Timeout结合使用。如果父类定义了默认超时值，内部类的测试也会使用它：

```java
@Timeout(5)
class TimeoutUnitTest {

    @Nested
    class NestedClassWithoutTimeout {
        @Test
        void shouldFailAfterParentsDefaultTimeoutOfFiveSeconds() throws InterruptedException {
            Thread.sleep(10_000);
        }
    }
}
```

但是，可以在嵌套类级别或方法级别覆盖此值：

```java
@Nested
@Timeout(3)
class NestedClassWithTimeout {

    @Test
    void shouldFailAfterNestedClassTimeoutOfThreeSeconds() throws InterruptedException {
        Thread.sleep(10_000);
    }

    @Test
    @Timeout(1)
    void shouldFailAfterOneSecond() throws InterruptedException {
        Thread.sleep(10_000);
    }
}
```

### 3.2 @Timeout和@ParameterizedTest

我们可以利用[@ParameterizedTest](https://www.baeldung.com/parameterized-tests-junit-5)注解根据给定的一组输入值执行多个测试。我们可以使用@Timeout标注参数化测试，因此，每个生成的测试都将使用超时值。

例如，如果我们有一个将执行5个测试的@ParameterizedTest并且我们用@Timeout(1)标注它，则5个结果测试中的每一个如果超过1秒，都将失败：

```java
@Timeout(1)
@ParameterizedTest
@ValueSource(ints = {1, 2, 3, 4, 5})
void eachTestShouldFailAfterOneSecond(int input) throws InterruptedException {
    Thread.sleep(1100);
}
```

## 4. 总结

在本文中，我们讨论了JUnit 5的新@Timeout注解。我们学习了如何配置它并使用它以声明的方式为我们的单元测试设置超时值。