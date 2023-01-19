## 1. 概述

在本教程中，我们将研究java.time包中的Java Clock类。我们将解释Clock类是什么以及我们如何使用它。

## 2. 时钟类

Clock 是在Java8 中添加的，它使用最佳可用系统时钟提供对即时时间的访问，并用作时间提供者，可以有效地存根用于测试目的。

当前日期和时间取决于时区，对于全球化应用程序，需要时间提供程序来确保使用正确的时区创建日期和时间。

这个类帮助我们测试我们的代码更改是否适用于不同的时区，或者 - 当使用固定时钟时 - 该时间不会影响我们的代码。

Clock类是抽象的，所以我们不能创建它的实例。可以使用以下工厂方法：

-   offset(Clock, Duration) – 返回偏移指定Duration的时钟。主要用例是模拟未来或过去的跑步
-   systemUTC() – 返回代表 UTC 时区的时钟
-   fixed(Instant, ZoneId) – 总是返回相同的Instant。主要用例是在测试中，固定时钟确保测试不依赖于当前时钟

我们将研究 Clock类中可用的大多数方法。

### 2.1. 立即的()

此方法返回代表时钟定义的当前时刻的时刻：

```java
Clock clock = Clock.systemDefaultZone();
Instant instant = clock.instant();
System.out.println(instant);
```

将产生：

```plaintext
2018-04-07T03:59:35.555Z
```

### 2.2. 系统UTC()

此方法返回一个Clock对象，表示 UTC 时区中的当前时刻：

```java
Clock clock = Clock.systemUTC();
System.out.println("UTC time :: " + clock.instant());
```

将产生：

```plaintext
UTC time :: 2018-04-04T17:40:12.353Z
```

### 2.3. 系统()

此静态方法返回由给定时区 ID 标识的时区的Clock对象：

```java
Clock clock = Clock.system(ZoneId.of("Asia/Calcutta"));
System.out.println(clock.instant());
```

将产生：

```plaintext
2018-04-04T18:00:31.376Z
```

### 2.4. 系统默认区域()

此静态方法返回一个Clock对象，该对象表示当前时刻并使用其运行所在系统的默认时区：

```java
Clock clock = Clock.systemDefaultZone();
System.out.println(clock);
```

上面几行产生以下结果(假设我们的默认时区是“亚洲/加尔各答”)：

```plaintext
SystemClock[Asia/Calcutta]
```

我们可以通过传递ZoneId.systemDefault()来实现相同的行为 ：

```plaintext
Clock clock = Clock.system(ZoneId.systemDefault());
```

### 2.5. 毫秒()

此方法以毫秒为单位返回时钟的当前时刻。提供它是为了允许在无法创建对象的高性能用例中使用时钟。这个方法可以用在我们原本会使用System.currentTimeInMillis()的地方：

```java
Clock clock = Clock.systemDefaultZone();
System.out.println(clock.millis());
```

将产生：

```plaintext
1523104441258
```

### 2.6. 抵消()

此静态方法从指定的基本时钟返回一个瞬间，并添加了指定的持续时间。

如果持续时间为负，则生成的时钟时刻将早于给定的基本时钟。

使用 offset，我们可以获得给定基准时钟的过去和未来时刻。如果我们传递零持续时间，那么我们将获得与给定基本时钟相同的时钟：

```java
Clock baseClock = Clock.systemDefaultZone();

// result clock will be later than baseClock
Clock clock = Clock.offset(baseClock, Duration.ofHours(72));
System.out.println(clock5.instant());

// result clock will be same as baseClock                           
clock = Clock.offset(baseClock, Duration.ZERO);
System.out.println(clock.instant());

// result clock will be earlier than baseClock            
clock = Clock.offset(baseClock, Duration.ofHours(-72));
System.out.println(clock.instant());
```

将产生：

```plaintext
2018-04-10T13:24:07.347Z
2018-04-07T13:24:07.348Z
2018-04-04T13:24:07.348Z
```

### 2.7. 打钩()

此静态方法返回从指定时钟四舍五入到最接近指定持续时间的时刻。指定的时钟持续时间必须为正数：

```java
Clock clockDefaultZone = Clock.systemDefaultZone();
Clock clocktick = Clock.tick(clockDefaultZone, Duration.ofSeconds(30));

System.out.println("Clock Default Zone: " + clockDefaultZone.instant());
System.out.println("Clock tick: " + clocktick.instant());
```

将产生：

```plaintext
Clock Default Zone: 2018-04-07T16:42:05.473Z
Clock tick: 2018-04-07T16:42:00Z
```

### 2.8. tickSeconds()

此静态方法以整秒为单位返回给定时区的当前即时滴答声。该时钟将始终将纳秒级字段设置为零：

```java
ZoneId zoneId = ZoneId.of("Asia/Calcutta");
Clock clock = Clock.tickSeconds(zoneId);
System.out.println(clock.instant());
```

将产生：

```plaintext
2018-04-07T17:40:23Z
```

同样可以通过使用tick()来实现：

```java
Clock clock = Clock.tick(Clock.system(ZoneId.of("Asia/Calcutta")), Duration.ofSeconds(1));
```

### 2.9. tickMinutes()

此静态方法返回指定时区以整分钟为单位的时钟即时滴答声。这个时钟总是将纳秒和秒字段设置为零：

```java
ZoneId zoneId = ZoneId.of("Asia/Calcutta");
Clock clock = Clock.tickMinutes(zoneId);
System.out.println(clock.instant());
```

将产生：

```plaintext
2018-04-07T17:26:00Z
```

同样可以通过使用tick()来实现：

```java
Clock clock = Clock.tick(Clock.system(ZoneId.of("Asia/Calcutta")), Duration.ofMinutes(1));
```

### 2.10. 带区域()

此方法返回具有不同时区的此时钟的副本。

如果我们有一个特定时区的时钟实例，我们可以为不同的时区该时钟：

```java
ZoneId zoneSingapore = ZoneId.of("Asia/Singapore");  
Clock clockSingapore = Clock.system(zoneSingapore); 
System.out.println(clockSingapore.instant());

ZoneId zoneCalcutta = ZoneId.of("Asia/Calcutta");
Clock clockCalcutta = clockSingapore.withZone(zoneCalcutta);
System.out.println(clockCalcutta.instant());
```

将产生：

```plaintext
2018-04-07T17:55:43.035Z
2018-04-07T17:55:43.035Z
```

### 2.11. 获取区域()

此方法返回给定Clock的时区。

```java
Clock clock = Clock.systemDefaultZone();
ZoneId zone = clock.getZone();
System.out.println(zone.getId());
```

将产生：

```plaintext
Asia/Calcutta
```

### 2.12. 固定的()

此方法返回一个始终返回同一时刻的时钟。此方法的主要用例是在测试中，其中固定时钟确保测试不依赖于当前时钟。

```java
Clock fixedClock = Clock.fixed(Instant.parse("2018-04-29T10:15:30.00Z"),
ZoneId.of("Asia/Calcutta"));
System.out.println(fixedClock);
```

将产生：

```plaintext
FixedClock[2018-04-29T10:15:30Z,Asia/Calcutta]
```

## 3.总结

在本文中，我们深入探讨了JavaClock类以及我们可以通过可用方法使用它的不同方式。