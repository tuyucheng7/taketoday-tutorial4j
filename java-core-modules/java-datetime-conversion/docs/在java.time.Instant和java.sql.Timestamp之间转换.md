## 1. 概述

java.time.Instant 和 java.sql.Timestamp类都表示UTC 时间轴上的一个点。换句话说，它们表示自[Java 纪元以来的](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html)纳秒数。

在本快速教程中，我们将使用内置的Java方法将一个转换为另一个。

## 2. 将 Instant转换 为 时间戳并返回

我们可以使用Timestamp.from() 将 Instant转换为时间戳：

```java
Instant instant = Instant.now();
Timestamp timestamp = Timestamp.from(instant);
assertEquals(instant.toEpochMilli(), timestamp.getTime());
```

反之亦然，我们可以使用 Timestamp.toInstant() 将 Timestamp s 转换为 Instant s：


```java
instant = timestamp.toInstant();
assertEquals(instant.toEpochMilli(), timestamp.getTime());
```

无论哪种方式，Instant和Timestamp都 代表时间轴上的同一点。

接下来，让我们看看两个类和时区之间的交互。

## 3. toString()方法差异

 在 Instant和 Timestamp上调用 toString()在时区方面表现不同。 Instant.toString() 返回 UTC 时区的时间。另一方面，Timezone.toString() 返回本地机器时区的时间。

让我们看看分别在 instant和 timestamp上调用toString()时得到了什么：

```plaintext
Instant (in UTC): 2018-10-18T00:00:57.907Z
Timestamp (in GMT +05:30): 2018-10-18 05:30:57.907
```

此处，timestamp.toString()产生的时间比instant.toString()返回的时间晚 5 小时 30 分钟 。这是因为本地机器的时区是 GMT +5:30 时区。

toString()方法的输出不同，但 timestamp和 instant都代表时间线上的同一点。

我们还可以通过将时间戳转换为 UTC 时区来验证这一点：

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
formatter = formatter.withZone(TimeZone.getTimeZone("UTC").toZoneId());
DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

assertThat(formatter.format(instant)).isEqualTo(df.format(timestamp));
```

## 4。总结

在本快速教程中，我们了解了如何使用内置方法在Java中的java.time.Instant和java.sql.Timestamp类之间进行转换。

我们还了解了时区如何影响输出的变化。