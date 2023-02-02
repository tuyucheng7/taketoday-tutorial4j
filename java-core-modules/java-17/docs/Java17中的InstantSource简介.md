## 1. 概述

在本教程中，我们将深入探讨Java 17中引入的InstantSource接口，该接口**提供了当前Instant的可插拔表示形式**，并避免了对时区的引用。

## 2. InstantSource接口

正如我们在原始[提案](https://mail.openjdk.java.net/pipermail/core-libs-dev/2021-May/077213.html)和[相关问题](https://github.com/ThreeTen/threeten-extra/issues/150)中看到的那样，该接口的第一个目标是创建对java.time.Clock提供的时区的抽象。它还简化了在测试检索Instant代码部分期间创建存根的过程。

它是在Java 17中添加的，**以提供一种安全的方式来访问当前时刻**，如我们在以下示例中所见：

```java
class AQuickTest {
    InstantSource source;

    // ...
    Instant getInstant() {
        return source.instant();
    }
}
```

然后，我们可以简单地得到一个Instant：

```java
var quickTest = new AQuickTest(InstantSource.system());
quickTest.getInstant();
```

它的实现创建了可以在任何地方用于检索Instant实例的对象，并且它提供了一种有效的方法来创建用于测试目的的stub实现。

让我们更深入地了解使用此接口的好处。

## 3. 问题与解决方案

为了更好地理解InstantSource接口，让我们深入了解它旨在解决的问题以及它提供的实际解决方案。

### 3.1 测试问题

涉及Instant检索的测试代码通常是一场噩梦，当获取Instant的方式基于当前的数据解决方案(例如LocalDateTime.now())时更是如此。

**为了让测试提供特定的日期，我们通常会创建变通方法，例如创建外部日期工厂并在测试中提供stubbed实例**。

让我们看看下面的代码，作为解决此问题的解决方法的示例。

InstantExample类使用InstantWrapper(或解决方法)来恢复Instant：

```java
class InstantExample {
    InstantWrapper instantWrapper;

    Instant getCurrentInstantFromInstantWrapper() {
        return instantWrapper.instant();
    }
}
```

我们的InstantWrapper解决方法类本身如下所示：

```java
class InstantWrapper {
    Clock clock;

    InstantWrapper() {
        this.clock = Clock.systemDefaultZone();
    }

    InstantWrapper(ZonedDateTime zonedDateTime) {
        this.clock = Clock.fixed(zonedDateTime.toInstant(), zonedDateTime.getZone());
    }

    Instant instant() {
        return clock.instant();
    }
}
```

然后，我们可以使用它来提供一个固定的Instant进行测试：

```java
// given
LocalDateTime now = LocalDateTime.now();
InstantExample tested = new InstantExample(InstantWrapper.of(now), null);
Instant currentInstant = now.toInstant(ZoneOffset.UTC);
// when
Instant returnedInstant = tested.getCurrentInstantFromWrapper();
// then
assertEquals(currentInstant, returnedInstant);
```

### 3.2 测试问题的解决方案

从本质上讲，我们上面应用的解决方法就是InstantSource所做的。**它提供了一个Instants的外部工厂，我们可以在任何需要的地方使用**。Java 17提供了一个默认的系统范围实现(在Clock类中)，我们也可以提供我们自己的：

```java
class InstantExample {
    InstantSource instantSource;

    Instant getCurrentInstantFromInstantSource() {
        return instantSource.instant();
    }
}
```

InstantSource是可插拔的，也就是说，它可以使用依赖注入框架注入，或者只是作为构造函数参数传递到我们正在测试的对象中。因此，我们可以很容易地创建一个subbted的InstantSource，将其提供给被测试的对象，并使其返回我们测试所需的Instant：

```java
// given
LocalDateTime now = LocalDateTime.now();
InstantSource instantSource = InstantSource.fixed(now.toInstant(ZoneOffset.UTC));
InstantExample tested = new InstantExample(null, instantSource);
Instant currentInstant = instantSource.instant();
// when
Instant returnedInstant = tested.getCurrentInstantFromInstantSource();
// then
assertEquals(currentInstant, returnedInstant);
```

### 3.3 时区问题

**当我们需要一个Instant时，我们可以从许多不同的地方获取它**，比如Instant.now()、Clock.systemDefaultZone().instant()甚至LocalDateTime.now.toInstant(zoneOffset)。问题是，根据我们选择的方法，**它可能会引入时区问题**。

例如，让我们看看当我们在Clock类上请求Instant时会发生什么：

```java
Clock.systemDefaultZone().instant();
```

此代码生成以下结果：

```shell
2022-11-18T16:47:15.001890204Z
```

让我们从不同的源获取相同的Instant：

```java
LocalDateTime.now().toInstant(ZoneOffset.UTC);
```

这会产生以下输出：

```shell
2022-11-18T17:47:15.001890204Z
```

我们应该得到相同的Instant，但实际上，两者之间有60分钟的差距。

最糟糕的是，可能有两个或更多开发人员在代码的不同部分使用这两个Instant源来处理同一代码。如果是这样的话，我们就遇到了问题。

**此时，我们通常不想处理时区问题**。但是，要创建Instant，我们需要一个源，并且该源总是附带一个时区。

### 3.4 解决时区问题

**InstantSource将我们从选择Instant的源中抽象出来**，这个选择已经为我们做出了。可能是另一个程序员设置了系统范围的自定义实现，或者我们正在使用Java 17提供的实现，我们将在下一节中看到。

正如InstantExample所示，我们插入了一个InstantSource，并且不需要知道任何其他信息，因此可以删除我们的InstantWrapper解决方法，而只使用插入的InstantSource。

现在我们已经了解了使用此接口的好处，让我们通过其静态方法和实例方法来看看它还能提供什么功能。

## 4. 工厂方法

以下工厂方法可用于创建InstantSource对象：

-   **system()**：默认的系统范围实现
-   **tick(InstantSource, Duration)**：返回一个InstantSource**截断为给定持续时间的最接近表示**
-   **fixed(Instant)**：返回一个**始终生成相同Instant的InstantSource**
-   **offset(InstantSource, Duration)**：返回一个InstantSource，它**为Instant提供给定的偏移量**

让我们看看这些方法的一些基本用法。

### 4.1 system()

Java 17中当前的默认实现是Clock.SystemInstantSource类。

```java
Instant i = InstantSource.system().instant();
```

### 4.2 tick()

基于前面的例子：

```java
Instant i = InstantSource.system().instant();
System.out.println(i);
```

运行此代码后，我们将得到以下输出：

```shell
2022-11-18T17:44:44.861040341Z
```

但是，如果我们应用2小时的刻度持续时间：

```java
Instant i = InstantSource.tick(InstantSource.system(), Duration.ofHours(2)).instant();
```

然后，我们将得到以下结果：

```shell
2022-11-18T06:00:00Z
```

### 4.3 fixed()

当我们需要为测试目的创建stubbed的InstantSource时，此方法很方便：

```java
LocalDateTime fixed = LocalDateTime.of(2022, 1, 1, 0, 0);
Instant i = InstantSource.fixed(fixed.toInstant(ZoneOffset.UTC)).instant();
System.out.println(i);
```

上面的代码总是返回相同的Instant：

```shell
2022-01-01T00:00:00Z
```

### 4.4 offset()

基于前面的示例，我们对固定的InstantSource应用一个偏移量以查看它返回的内容：

```java
LocalDateTime fixed = LocalDateTime.of(2022, 1, 1, 0, 0);
InstantSource fixedSource = InstantSource.fixed(fixed.toInstant(ZoneOffset.UTC));
Instant i = InstantSource.offset(fixedSource, Duration.ofDays(5)).instant();
System.out.println(i);
```

执行此代码后，我们将得到以下输出：

```shell
2022-01-06T00:00:00Z
```

## 5. 实例方法

可用于与InstantSource实例交互的方法有：

-   **instant()**：返回InstantSource提供的当前Instant
-   **millis()**：返回InstantSource提供的当前Instant的毫秒表示
-   **withZone(ZoneId)**：接收一个ZoneId并返回一个**基于给定InstantSource和指定ZoneId的Clock**

### 5.1 instant()

此方法最基本的用法是：

```java
Instant i = InstantSource.system().instant();
System.out.println(i);
```

运行此代码得到以下输出：

```shell
2022-11-18T18:29:17.641839778Z
```

### 5.2 millis()

要从InstantSource获取历元：

```java
long m = InstantSource.system().millis();
System.out.println(m);
```

并且，在运行它之后我们将得到以下信息：

```shell
1641371476655
```

### 5.3 withZone()

为特定的ZoneId获取一个Clock实例：

```java
Clock c = InstantSource.system().withZone(ZoneId.of("-4"));
System.out.println(c);
```

这将简单地打印以下内容：

```shell
SystemClock[-04:00]
```

## 6. 总结

在本文中，我们介绍了InstantSource接口，列举了创建它来解决的重要问题，并演示了我们如何在日常工作中利用它的真实示例。