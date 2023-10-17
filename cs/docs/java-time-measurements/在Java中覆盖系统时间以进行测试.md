## 1. 概述

在本快速教程中，我们将重点介绍覆盖系统时间以进行测试的不同方法。

有时我们的代码中有关于当前日期的逻辑。也许一些函数调用，例如new Date()或Calendar.getInstance()，最终将调用System.CurrentTimeMillis。

有关Java Clock的使用介绍，请参阅[此处的这篇文章](https://www.baeldung.com/java-clock)。[或者，到这里](https://www.baeldung.com/aspectj)使用 AspectJ 。

## 2.在java.time中使用时钟

Java 8中的java.time包包含一个抽象类java.time.Clock，目的是允许在需要时插入备用时钟。有了它，我们可以插入我们自己的实现或找到一个已经满足我们需求的实现。

为实现我们的目标，上述库包含用于产生特殊实现的静态方法。我们将使用其中两个返回不可变、线程安全和可序列化的实现。

第一个是固定的。从中，我们可以获得一个始终返回相同Instant 的Clock， 确保测试不依赖于当前时钟。

要使用它，我们需要一个Instant和一个ZoneOffset：

```java
Instant.now(Clock.fixed( 
  Instant.parse("2018-08-22T10:00:00Z"),
  ZoneOffset.UTC))
```

第二个静态方法是offset。在这一个中，一个时钟包装另一个时钟，使其成为返回的对象，能够获得比指定持续时间晚或早的瞬间。

换句话说，可以模拟未来、过去或任意时间点的跑步：

```java
Clock constantClock = Clock.fixed(ofEpochMilli(0), ZoneId.systemDefault());

// go to the future:
Clock clock = Clock.offset(constantClock, Duration.ofSeconds(10));
        
// rewind back with a negative value:
clock = Clock.offset(constantClock, Duration.ofSeconds(-5));
 
// the 0 duration returns to the same clock:
clock = Clock.offset(constClock, Duration.ZERO);
```

使用Duration类，可以从纳秒到几天进行操作。此外，我们可以取反一个持续时间，这意味着获得一个长度取反的持续时间的副本。

## 3.使用面向方面的编程

另一种覆盖系统时间的方法是通过 AOP。通过这种方法，我们能够编织System类以返回一个预定义的值，我们可以在我们的测试用例中设置该值。

此外，还可以编织应用程序类以将对System.currentTimeMillis()或new Date() 的调用重定向 到我们自己的另一个实用程序类。

实现这一点的一种方法是使用 AspectJ：

```java
public aspect ChangeCallsToCurrentTimeInMillisMethod {
    long around(): 
      call(public static native long java.lang.System.currentTimeMillis()) 
        && within(user.code.base.pckg.) {
          return 0;
      }
}

```

在上面的示例中，我们捕获了指定包内对System.currentTimeMillis () 的每次调用，在本例中为user.code.base.pckg.，并在每次发生此事件时返回零。

正是在这个地方，我们可以声明我们自己的实现以获得所需的时间(以毫秒为单位)。

使用 AspectJ 的优点之一是它直接在字节码级别上运行，因此不需要原始源代码即可工作。

因此，我们不需要重新编译它。

## 4. 模拟Instant.now()方法

我们可以使用 [Instant](https://www.baeldung.com/current-date-time-and-timestamp-in-java-8) 类来表示时间轴上的一个瞬间点。通常，我们可以用它来记录我们应用程序中的事件时间戳。此类的now()方法允许我们从 UTC 时区的系统时钟获取当前时刻。

让我们看看在测试时更改其行为的一些替代方法。

### 4.1. 用 时钟重载now()

我们可以用固定的 [Clock](https://www.baeldung.com/java-clock)实例重载now()方法 。java.time包中的许多类 都有一个带有Clock参数的now()方法，这使它成为我们的首选方法：

```java
@Test
public void givenFixedClock_whenNow_thenGetFixedInstant() {
    String instantExpected = "2014-12-22T10:15:30Z";
    Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));

    Instant instant = Instant.now(clock);

    assertThat(instant.toString()).isEqualTo(instantExpected);
}
```

### 4.2. 使用Mockito

此外，如果我们需要在不发送参数的情况下修改now()方法的行为，我们可以使用[Mockito](https://www.baeldung.com/mockito-mock-static-methods)：

```java
@Test
public void givenInstantMock_whenNow_thenGetFixedInstant() {
    String instantExpected = "2014-12-22T10:15:30Z";
    Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
    Instant instant = Instant.now(clock);

    try (MockedStatic<Instant> mockedStatic = mockStatic(Instant.class)) {
        mockedStatic.when(Instant::now).thenReturn(instant);
        Instant now = Instant.now();
        assertThat(now.toString()).isEqualTo(instantExpected);
    }
}
```

### 4.3. 使用JMockit

或者，我们可以使用 [JMockit](https://www.baeldung.com/jmockit-101) 库。

JMockit为我们提供了两种 [模拟静态方法](https://www.baeldung.com/jmockit-static-method)的方法。一种是使用MockUp类：

```java
@Test
public void givenInstantWithJMock_whenNow_thenGetFixedInstant() {
    String instantExpected = "2014-12-21T10:15:30Z";
    Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
    new MockUp<Instant>() {
        @Mock
        public Instant now() {
            return Instant.now(clock);
        }
    };

    Instant now = Instant.now();

    assertThat(now.toString()).isEqualTo(instantExpected);
}
```

另一个是使用[Expectations](https://www.baeldung.com/jmockit-expectations)类：

```java
@Test
public void givenInstantWithExpectations_whenNow_thenGetFixedInstant() {
    Clock clock = Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));
    Instant instantExpected = Instant.now(clock);
    new Expectations(Instant.class) {
        {
            Instant.now();
            result = instantExpected;
        }
    };

    Instant now = Instant.now();

    assertThat(now).isEqualTo(instantExpected);
}
```

## 5. 模拟LocalDateTime.now()方法

java.time包中的另一个有用的类是 [LocalDateTime](https://www.baeldung.com/java-8-date-time-intro) 类。此类表示 ISO-8601 日历系统中没有时区的日期时间。此类的now()方法允许我们从默认时区的系统时钟获取当前日期时间。

我们可以使用与之前看到的相同的替代方法来模拟它。例如，用固定的Clock重载now()：

```java
@Test
public void givenFixedClock_whenNow_thenGetFixedLocalDateTime() {
    Clock clock = Clock.fixed(Instant.parse("2014-12-22T10:15:30.00Z"), ZoneId.of("UTC"));
    String dateTimeExpected = "2014-12-22T10:15:30";

    LocalDateTime dateTime = LocalDateTime.now(clock);

    assertThat(dateTime).isEqualTo(dateTimeExpected);
}
```

## 六，总结

在本文中，我们探讨了覆盖系统时间以进行测试的不同方法。首先，我们查看了本地包java.time及其Clock类。接下来，我们看到了如何应用方面来编织System类。最后，我们看到了在 Instant和 LocalDateTime类上模拟now()方法的不同替代方法 。