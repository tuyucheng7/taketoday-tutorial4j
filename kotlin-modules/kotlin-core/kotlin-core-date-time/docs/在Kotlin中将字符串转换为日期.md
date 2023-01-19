## 1. 概述

将字符串转换为日期类型是最常见的任务之一。现代编程语言和库提供了多种方法来实现这一点。

在本教程中，我们将探索一些使用 Kotlin 将字符串转换为各种日期类型的有效方法。

## 2. String转LocalDate和LocalDateTime

首先，我们将了解如何将字符串解析为LocalDate和LocalDateTime类型。

### 2.1. 将字符串解析为LocalDate

LocalDate类具有用于解析字符串的静态解析方法。我们将看到的第一种方法采用一个参数，该参数应遵循标准日期格式yyyy-MM-dd：

```kotlin
fun givenString_whenDefaultFormat_thenLocalDateCreated() {
    val localDate = LocalDate.parse("2022-01-06")
    Assertions.assertThat(localDate).isEqualTo("2022-01-06")
}
```

第二个静态方法是一个带有两个参数的重载方法。在这种情况下，第二个参数接收我们字符串的日期格式：

```kotlin
fun givenString_whenCustomFormat_thenLocalDateCreated() {
    val localDate = LocalDate.parse("01-06-2022", DateTimeFormatter.ofPattern("MM-dd-yyyy"))
    assertThat(localDate).isEqualTo("2022-01-06")
}
```

### 2.2. 将字符串解析为LocalDateTime

LocalDateTime类定义了相同的两个静态方法来解析字符串，但考虑了时间信息。单参数方法接收应为ISO_LOCAL_DATE_TIME格式的字符串：

```kotlin
fun givenString_whenDefaultFormat_thenLocalDateTimeCreated() {
    val localDateTime = LocalDateTime.parse("2022-01-06T21:30:10")
    assertThat(localDateTime.toString()).isEqualTo("2022-01-06T21:30:10")
}
```

类似地，正如为LocalDate定义的那样，第二种方法采用任何其他日期时间模式的格式参数：

```kotlin
fun givenString_whenCustomFormat_thenLocalDateTimeCreated() {
    val text = "2022-01-06 20:30:45"
    val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val localDateTime = LocalDateTime.parse(text, pattern)

    Assertions.assertThat(localDateTime).isEqualTo("2022-01-06T20:30:45")
}
```

## 3.字符串转java.util.Date

有时，我们需要使用旧的 API java.util.Date。幸运的是，由于 Kotlin 与 Java 有很好的兼容性，我们可以使用SimpleDateFormat类。

现在让我们看看如何将字符串转换为java.util.Date类型的Date对象。

### 3.1. 将字符串解析为java.util.Date

首先，我们需要定义我们将用于日期模式的格式化对象。然后，我们将使用它来解析字符串：

```kotlin
fun givenString_whenParseDate_thenUtilDateIsCreated() {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    val text = "2022-01-06"
    val date = formatter.parse(text)

    Assertions.assertThat(formatter.format(date)).isEqualTo("2022-01-06")
}
```

## 4. 解析带有时区的字符串

### 4.1. 使用ZonedDateTime解析带时区的字符串

LocalDate和LocalDateTime对象与时区无关。但是，如果我们需要处理特定时区日期，则可以使用类ZonedDateTime来解析字符串：

```kotlin
fun givenString_whenParseWithTimeZone_thenZonedDateTimeIsCreated() {
    val text = "2022-01-06 20:30:45 America/Los_Angeles"
    val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
    val zonedDateTime = ZonedDateTime.parse(text, pattern)

    Assertions.assertThat(zonedDateTime.zone).isEqualTo(ZoneId.of("America/Los_Angeles"))
}
```

### 4.2. 使用时区解析java.util.Date

同样，java.util.Date也可以使用格式化对象提供时区信息：

```kotlin
fun givenString_whenParseDateWithTimeZone_thenUtilDateIsCreated() {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    formatter.timeZone = TimeZone.getTimeZone("America/Los_Angeles");

    val text = "2022-01-06"
    val date = formatter.parse(text)

    assertThat(formatter.format(date)).isEqualTo("2022-01-06")
}
```

## 5.总结

在本教程中，我们了解了如何 使用 Kotlin 的 Date-Time API将String对象转换为Date类型。我们还了解了如何使用java.util.Date API解析字符串。