## 1. 概述

JPA 2.2 版本正式引入了对[Java 8 Date and Time API](https://www.baeldung.com/java-8-date-time-intro)的支持。在此之前，要么我们必须依赖专有解决方案，要么必须使用 JPA Converter API。

在本教程中，我们将展示如何映射各种Java8日期和时间类型。我们将特别关注那些考虑偏移量信息的。

## 2.Maven依赖

在开始之前，我们需要将 JPA 2.2 API 包含到项目类路径中。在基于 Maven 的项目中，我们可以简单地将其依赖项添加到我们的 pom.xml文件中：

```xml
<dependency>
    <groupId>javax.persistence</groupId>
    <artifactId>javax.persistence-api</artifactId>
    <version>2.2</version>
</dependency>
```

此外，要运行该项目，我们需要一个 JPA 实现和我们将使用的数据库的 JDBC 驱动程序。在本教程中，我们将使用 EclipseLink 和 PostgreSQL 数据库：

```xml
<dependency>
    <groupId>org.eclipse.persistence</groupId>
    <artifactId>eclipselink</artifactId>
    <version>2.7.4</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.2.5</version>
    <scope>runtime</scope>
    <type>bundle</type>
</dependency>
```

请随时在 Maven Central 上检查最新版本的[JPA API](https://search.maven.org/search?q=g:javax.persistence AND a:javax.persistence-api&core=gav)、[EclipseLink](https://search.maven.org/search?q=g:org.eclipse.persistence AND a:eclipselink&core=gav)和[PostgreSQL JDBC 驱动程序。](https://search.maven.org/search?q=g:org.postgresql AND a:postgresql&core=gav)

当然我们也可以使用其他的数据库或者Hibernate之类的JPA实现。

## 3.时区支持

我们可以使用任何数据库，但首先，我们应该检查对这些标准 SQL 类型的支持，因为 JDBC 4.2 基于：

-   带时区的时间戳(n)
-   没有时区的时间戳(n)
-   带时区的时间(n)
-   没有时区的时间(n)

这里，n是小数秒精度，介于 0 到 9 位之间。 WITHOUT TIME ZONE是可选的，可以省略。如果指定了WITH TIME ZONE，则需要时区名称或与 UTC 的偏移量。

我们可以用以下两种格式之一表示时区：

-   时区名称
-   与 UTC 的偏移量或 UTC 的字母 Z

对于我们的示例，我们选择了 PostgreSQL 数据库，因为它完全支持 SQL 类型TIME WITH TIME ZONE。

请注意，其他数据库可能不支持这些类型。

## 4.映射Java 8之前的日期类型

在Java8 之前，我们通常必须将通用 SQL 类型TIME、DATE和TIMESTAMP分别映射到java.sql. 类java.sql.Time、 java.sql.Date和java.sql.Timestamp，或java.util类型java.util.Date和java.util.Calendar。

首先，让我们看看如何使用java.sql类型。在这里，我们只是将java.sql类型的属性定义为@Entity类的一部分：

```java
@Entity
public class JPA22DateTimeEntity {

    private java.sql.Time sqlTime;
    private java.sql.Date sqlDate;
    private java.sql.Timestamp sqlTimestamp;
    
    // ...
}
```

虽然java.sql类型与任何其他类型一样工作而无需任何额外映射，但java.util类型需要指定相应的时间类型。

这是通过@Temporal注解完成的，其value属性允许我们使用 TemporalType 枚举指定相应的 JDBC 类型：

```java
@Temporal(TemporalType.TIME)
private java.util.Date utilTime;

@Temporal(TemporalType.DATE)
private java.util.Date utilDate;

@Temporal(TemporalType.TIMESTAMP)
private java.util.Date utilTimestamp;
```

请注意，如果我们使用 Hibernate 作为实现，则不支持将Calendar映射 到TIME。

同样，我们可以使用Calendar类：

```java
@Temporal(TemporalType.TIME)
private Calendar calendarTime;

@Temporal(TemporalType.DATE)
private Calendar calendarDate;

@Temporal(TemporalType.TIMESTAMP)
private Calendar calendarTimestamp;
```

这些类型都不支持时区或偏移量。为了处理这些信息，我们传统上必须存储 UTC 时间。

## 5. 映射Java8 日期类型

Java 8 引入了java.time 包，JDBC 4.2 API 添加了对附加 SQL 类型TIMESTAMP WITH TIME ZONE和TIME WITH TIME ZONE的支持。

我们现在可以将 JDBC 类型TIME、DATE和TIMESTAMP映射到java.time类型 – LocalTime 、 LocalDate和LocalDateTime：

```java
@Column(name = "local_time", columnDefinition = "TIME")
private LocalTime localTime;

@Column(name = "local_date", columnDefinition = "DATE")
private LocalDate localDate;

@Column(name = "local_date_time", columnDefinition = "TIMESTAMP")
private LocalDateTime localDateTime;
```

此外，我们通过 OffsetTime 和 OffsetDateTime 类支持将本地时区偏移到UTC：

```java
@Column(name = "offset_time", columnDefinition = "TIME WITH TIME ZONE")
private OffsetTime offsetTime;

@Column(name = "offset_date_time", columnDefinition = "TIMESTAMP WITH TIME ZONE")
private OffsetDateTime offsetDateTime;
```

对应的映射列类型应该是TIME WITH TIME ZONE和TIMESTAMP WITH TIME ZONE。不幸的是，并非所有数据库都支持这两种类型。

正如我们所见，JPA 支持这五个类作为基本类型，并且不需要额外的信息来区分日期和/或时间信息。

保存实体类的新实例后，我们可以检查数据是否已正确插入：

[![约会时间](https://www.baeldung.com/wp-content/uploads/2019/01/date_time.png)](https://www.baeldung.com/wp-content/uploads/2019/01/date_time.png)

## 六. 总结

在Java8 和 JPA 2.2 之前，开发人员通常必须在持久化之前将日期/时间类型转换为 UTC。JPA 2.2 现在通过支持到 UTC 的偏移量和利用 JDBC 4.2 对时区的支持来开箱即用地支持此功能。