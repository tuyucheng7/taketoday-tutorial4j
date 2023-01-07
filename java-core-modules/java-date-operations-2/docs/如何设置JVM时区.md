## 1. 概述

当涉及到时间戳时，我们应用程序的用户可能要求很高。他们希望我们的应用程序能够自动检测他们的时区，并在正确的时区显示时间戳。

在本教程中，我们将了解几种可以修改 JVM 时区的方法。我们还将了解与管理时区相关的一些陷阱。

## 2.时区介绍

默认情况下，JVM 从操作系统读取时区信息。此信息被传递到[TimeZone](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/TimeZone.html)类，该类存储时区并计算夏令时。

我们可以调用getDefault 方法，它会返回程序运行时所在的时区。此外，我们可以使用TimeZone.getAvailableIDs()从应用程序中获取支持的时区 ID 列表。

在命名时区时，Java 依赖于[tz 数据库](https://en.wikipedia.org/wiki/List_of_tz_database_time_zones#List)的命名约定。

## 3. 更改时区

在本节中，我们将了解在 JVM 中更改时区的几种方法。

### 3.1. 设置环境变量

让我们首先看看如何使用环境变量来更改时区。我们可以添加或修改环境变量TZ。

例如，在基于 Linux 的环境中，我们可以使用export命令：

```bash
export TZ="America/Sao_Paulo"
```

设置环境变量后，我们可以看到我们运行的应用程序的时区现在是America/Sao_Paulo：

```java
Calendar calendar = Calendar.getInstance();
assertEquals(calendar.getTimeZone(), TimeZone.getTimeZone("America/Sao_Paulo"));
```

### 3.2. 设置 JVM 参数

设置环境变量的替代方法是设置 JVM 参数user.timezone。此 JVM 参数优先于环境变量TZ。

例如，我们可以在运行应用程序时使用标志-D：

```bash
java -Duser.timezone="Asia/Kolkata" com.company.Main
```

同样，我们也可以从应用程序设置 JVM 参数：

```java
System.setProperty("user.timezone", "Asia/Kolkata");
```

我们现在可以看到时区是 Asia/Kolkata：

```java
Calendar calendar = Calendar.getInstance();
assertEquals(calendar.getTimeZone(), TimeZone.getTimeZone("Asia/Kolkata"));
```

### 3.3. 从应用程序设置时区

最后，我们还可以使用TimeZone类从应用程序修改 JVM 时区。此方法优先于环境变量和 JVM 参数。

设置默认时区很简单：

```java
TimeZone.setDefault(TimeZone.getTimeZone("Portugal"));
```

正如预期的那样，时区现在是Portugal：

```java
Calendar calendar = Calendar.getInstance();
assertEquals(calendar.getTimeZone(), TimeZone.getTimeZone("Portugal"));
```

## 4.陷阱

### 4.1. 使用三字母时区 ID

尽管可以使用三个字母的 ID 来表示时区，[但不推荐这样做](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/TimeZone.html)。

相反，我们应该使用更长的名称，因为三个字母的 ID 是不明确的。例如，IST 可以是印度标准时间、爱尔兰标准时间或以色列标准时间。

### 4.2. 全局设置

请注意，上述每种方法都是为整个应用程序全局设置时区。但是，在现代应用程序中，设置时区通常比这更细微。

例如，我们可能需要将时间转换为最终用户的时区，因此全球时区没有多大意义。如果不需要全球时区，请考虑直接在每个日期时间实例上指定时区。ZonedDateTime[或](https://www.baeldung.com/java-zoneddatetime-offsetdatetime)[OffsetDateTime是](https://www.baeldung.com/java-zoneddatetime-offsetdatetime)一个方便的类。

## 5.总结

在本教程中，我们解释了几种修改 JVM 时区的方法。我们看到我们可以设置系统范围的环境变量、更改 JVM 参数或从我们的应用程序以编程方式修改它。