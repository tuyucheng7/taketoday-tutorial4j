## 1. 概述

在本教程中，我们将展示如何在 Hibernate 中映射时间列值，包括来自java.sql、java.util和java.time包的类。

## 2.项目设置

为了演示时间类型的映射，我们需要 H2 数据库和最新版本的hibernate-core库：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.4.12.Final</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.194</version>
</dependency>
```

对于当前版本的hibernate-core库，请转到[Maven 中央](https://search.maven.org/classic/#search|gav|1|g%3A"org.hibernate" AND a%3A"hibernate-core")存储库。

## 3. 时区设置

处理日期时，最好为 JDBC 驱动程序设置一个特定的时区。这样，我们的应用程序将独立于系统的当前时区。

对于我们的示例，我们将在每个会话的基础上进行设置：

```java
session = HibernateUtil.getSessionFactory().withOptions()
  .jdbcTimeZone(TimeZone.getTimeZone("UTC"))
  .openSession();
```

另一种方法是在用于构造会话工厂的 Hibernate 属性文件中设置hibernate.jdbc.time_zone属性。这样，我们可以为整个应用程序指定一次时区。

## 4.映射java.sql类型

java.sql包包含与 SQL 标准定义的类型一致的 JDBC 类型：

-   Date对应的是DATE SQL类型，只有日期没有时间。
-   时间对应于TIME SQL 类型，它是以小时、分钟和秒为单位指定的一天中的时间。
-   时间戳包括有关日期和时间的信息，精度可达纳秒，对应于TIMESTAMP SQL 类型。

这些类型与 SQL 一致，因此它们的映射相对简单。

我们可以使用@Basic或@Column注解：

```java
@Entity
public class TemporalValues {

    @Basic
    private java.sql.Date sqlDate;

    @Basic
    private java.sql.Time sqlTime;

    @Basic
    private java.sql.Timestamp sqlTimestamp;

}
```

然后我们可以设置相应的值：

```java
temporalValues.setSqlDate(java.sql.Date.valueOf("2017-11-15"));
temporalValues.setSqlTime(java.sql.Time.valueOf("15:30:14"));
temporalValues.setSqlTimestamp(
  java.sql.Timestamp.valueOf("2017-11-15 15:30:14.332"));
```

请注意，为实体字段选择java.sql类型可能并不总是一个好的选择。这些类是特定于 JDBC 的，并且包含许多已弃用的功能。

## 5.映射java.util.Date类型

java.util.Date类型包含日期和时间信息，精度可达毫秒。但它并不直接与任何 SQL 类型相关。

这就是为什么我们需要另一个注解来指定所需的 SQL 类型：

```java
@Basic
@Temporal(TemporalType.DATE)
private java.util.Date utilDate;

@Basic
@Temporal(TemporalType.TIME)
private java.util.Date utilTime;

@Basic
@Temporal(TemporalType.TIMESTAMP)
private java.util.Date utilTimestamp;
```

@Temporal注解具有TemporalType类型的单个参数值。它可以是DATE、TIME或TIMESTAMP，具体取决于我们要用于映射的基础 SQL 类型。

然后我们可以设置相应的字段：

```java
temporalValues.setUtilDate(
  new SimpleDateFormat("yyyy-MM-dd").parse("2017-11-15"));
temporalValues.setUtilTime(
  new SimpleDateFormat("HH:mm:ss").parse("15:30:14"));
temporalValues.setUtilTimestamp(
  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    .parse("2017-11-15 15:30:14.332"));
```

正如我们所见，java.util.Date类型(毫秒精度)不够精确，无法处理 Timestamp 值(纳秒精度)。

因此，当我们从数据库中检索实体时，我们会毫不奇怪地在该字段中找到一个java.sql.Timestamp实例，即使我们最初保留了一个java.util.Date：

```java
temporalValues = session.get(TemporalValues.class, 
  temporalValues.getId());
assertThat(temporalValues.getUtilTimestamp())
  .isEqualTo(java.sql.Timestamp.valueOf("2017-11-15 15:30:14.332"));
```

这对我们的代码应该没问题，因为Timestamp扩展了Date。

## 6. 映射java.util.Calendar类型

与java.util.Date一样，java.util.Calendar类型可能会映射到不同的 SQL 类型，因此我们必须使用@Temporal指定它们。

唯一的区别是 Hibernate 不支持将Calendar映射到TIME：

```java
@Basic
@Temporal(TemporalType.DATE)
private java.util.Calendar calendarDate;

@Basic
@Temporal(TemporalType.TIMESTAMP)
private java.util.Calendar calendarTimestamp;
```

以下是我们如何设置字段的值：

```java
Calendar calendarDate = Calendar.getInstance(
  TimeZone.getTimeZone("UTC"));
calendarDate.set(Calendar.YEAR, 2017);
calendarDate.set(Calendar.MONTH, 10);
calendarDate.set(Calendar.DAY_OF_MONTH, 15);
temporalValues.setCalendarDate(calendarDate);
```

## 7. 映射java.time类型

从Java8 开始，新的JavaDate and Time API 可用于处理时间值。此 API 修复了java.util.Date和java.util.Calendar类的许多问题。

java.time包中的类型直接映射到相应的 SQL 类型。

因此，无需显式指定@Temporal注解：

-   LocalDate映射到DATE。
-   LocalTime和OffsetTime映射到TIME。
-   Instant、LocalDateTime、OffsetDateTime和ZonedDateTime映射到TIMESTAMP。

这意味着我们只能使用@Basic(或@Column)注解来标记这些字段：

```java
@Basic
private java.time.LocalDate localDate;

@Basic
private java.time.LocalTime localTime;

@Basic
private java.time.OffsetTime offsetTime;

@Basic
private java.time.Instant instant;

@Basic
private java.time.LocalDateTime localDateTime;

@Basic
private java.time.OffsetDateTime offsetDateTime;

@Basic
private java.time.ZonedDateTime zonedDateTime;
```

java.time包中的每个时间类都有一个静态的parse()方法来使用适当的格式解析提供的String值。

所以，下面是我们如何设置实体字段的值：

```java
temporalValues.setLocalDate(LocalDate.parse("2017-11-15"));

temporalValues.setLocalTime(LocalTime.parse("15:30:18"));
temporalValues.setOffsetTime(OffsetTime.parse("08:22:12+01:00"));

temporalValues.setInstant(Instant.parse("2017-11-15T08:22:12Z"));
temporalValues.setLocalDateTime(
  LocalDateTime.parse("2017-11-15T08:22:12"));
temporalValues.setOffsetDateTime(
  OffsetDateTime.parse("2017-11-15T08:22:12+01:00"));
temporalValues.setZonedDateTime(
  ZonedDateTime.parse("2017-11-15T08:22:12+01:00[Europe/Paris]"));
```

## 八. 总结

在本文中，我们展示了如何在 Hibernate 中映射不同类型的时间值。