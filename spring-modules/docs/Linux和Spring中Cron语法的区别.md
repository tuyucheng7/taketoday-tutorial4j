## 一、概述

Cron 表达式使我们能够安排任务在特定日期和时间定期运行。在 Unix 引入后，其他基于 Unix 的操作系统和软件库（包括 Spring Framework）都采用了它的方法来进行任务调度。

在本快速教程中，我们将了解基于 Unix 的操作系统和 Spring 框架中的 Cron 表达式之间的区别。

## 2. Unix 定时任务

大多数基于 Unix 的系统中的[Cron有五个字段：](https://www.manpagez.com/man/5/crontab/)**分钟 (0-59)、小时 (0-23)、月份中的日期 (1-31)、月份（1-12 或名称）和星期几 ( 0-7 或名字）。**

我们可以在每个字段中放置一些特殊值，例如星号 (*)：

```markdown
5 0 * * *复制
```

该作业将在每天午夜后 5 分钟执行。也可以使用一系列值：

```apache
5 0-5 * * *复制
```

这里调度器会在午夜后5分钟执行任务，每天1点、2点、3点、4点、5点后5分钟也会执行任务。

或者，我们可以使用值列表：

```apache
5 0,3 * * *复制
```

现在调度程序每天午夜后五分钟和凌晨三点后五分钟执行作业。原始的 Cron 表达式提供的功能 比我们目前介绍的[要多得多。](https://www.baeldung.com/cron-expressions)

然而，**它有一个很大的局限性：我们不能以秒级精度安排作业，因为它没有专门的第二个字段。**

让我们看看 Spring 如何解决这个限制。

## 3. 春季计划

要在 Spring 中调度周期性后台任务，我们通常将 Cron 表达式传递给 *[@Scheduled](https://www.baeldung.com/spring-scheduled-tasks#schedule-a-task-using-cron-expressions)* 注解。

与基于 Unix 的系统中的 Cron 表达式相反，**Spring 中的 Cron 表达式有六个以空格分隔的字段：秒、分、时、日、月和工作日**。

例如，要每十秒运行一次任务，我们可以这样做：

```plaintext
*/10 * * * * *复制
```

另外，每天早上 8 点到 10 米每 20 秒运行一次任务：

```apache
*/20 * 8-10 * * *复制
```

如以上示例所示，**第一个字段表示表达式的第二部分。这就是两种实现之间的区别。** 尽管第二个字段有所不同，但 Spring 支持原始 Cron 的许多功能，例如范围数字或列表。

从实现的角度来看，[*CronSequenceGenerator*](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html)类负责解析 Spring 中的 Cron 表达式。

## 4。结论

在这个简短的教程中，我们看到了 Spring 和大多数基于 Unix 的系统之间的 Cron 实现差异。在此过程中，我们看到了这两种实现的一些示例。

为了查看更多 Cron 表达式示例，强烈建议查看我们的[Cron 表达式指南](https://www.baeldung.com/cron-expressions)。此外，查看[*CronSequenceGenerator*](https://github.com/spring-projects/spring-framework/blob/master/spring-context/src/main/java/org/springframework/scheduling/support/CronSequenceGenerator.java) 类的源代码可以让我们很好地了解 Spring 如何实现此功能。