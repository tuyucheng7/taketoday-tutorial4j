## 1. 概述

在这个教程中，我们将介绍Mockito中的惰性验证。

**Mockito允许我们在测试结束时查看收集和报告的所有结果，而不是快速失败**。

## 2. Maven依赖

```xml

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.0.0</version>
</dependency>
```

## 3. 惰性验证

**Mockito的默认行为是在第一次失败时停止**，即急切地停止 - 这种方法也称为快速失败。

有时我们可能需要执行并报告所有验证 - 而不管以前的失败。

VerificationCollector是一个JUnit Rule，它收集测试方法中的所有验证。

如果出现failures，它们将在测试结束时执行并报告：

```java
public class LazyVerificationTest {

    @Rule
    public VerificationCollector verificationCollector = MockitoJUnit.collector();
    // ...
}
```

让我们添加一个简单的测试：

```java
public class LazyVerificationUnitTest {

    @Test
    public void testLazyVerification() {
        List mockList = mock(ArrayList.class);

        verify(mockList).add("one");
        verify(mockList).clear();
    }
}
```

**执行此测试时，将报告两次验证的失败**：

```text
org.mockito.exceptions.base.MockitoAssertionError: There were multiple verification failures:
1. Wanted but not invoked:
arrayList.add("one");
-> at cn.tuyucheng.taketoday.mockito.lazyverification.LazyVerificationTest.testLazyVerification(LazyVerificationTest.java:21)
Actually, there were zero interactions with this mock.

2. Wanted but not invoked:
arrayList.clear();
-> at cn.tuyucheng.taketoday.mockito.lazyverification.LazyVerificationTest.testLazyVerification(LazyVerificationTest.java:22)
Actually, there were zero interactions with this mock.
```

如果没有VerificationCollector Rule，则只会报告第一个验证：

```text
Wanted but not invoked:
list.add("one");
-> at cn.tuyucheng.taketoday.mockito.lazyverification.LazyVerificationUnitTest.whenLazilyVerified_thenReportsMultipleFailures(LazyVerificationUnitTest.java:16)
Actually, there were zero interactions with this mock.
```

## 4. 总结

我们快速介绍了如何在Mockito中使用惰性验证。