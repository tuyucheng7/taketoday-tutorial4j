## 1. 概述

在这个快速教程中，我们介绍在Java 7、Java 8和Java 9中使用开始和结束日期迭代一系列日期的几种方法。

## 2.Java7

从Java 7开始，我们使用类java.util.Date来保存日期值，并使用java.util.Calendar来从一个日期递增到下一个日期。

让我们看一个使用简单while循环以及java.util.Date和java.util.Calendar类的示例：

```java
void iterateBetweenDatesJava7(Date start, Date end) {
    Date current = start;

    while (current.before(end)) {
        processDate(current);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DATE, 1);
        current = calendar.getTime();
    }
}

```

## 3.Java8

从Java 8开始，我们可以使用新的Java 8 Date API。这为我们提供了自处理、不可变、流式和线程安全的对象。它还允许我们编写更简洁的代码，而不需要像java.util.Calendar这样的工具类来增加日期。

让我们使用一个简单的for循环、LocalDate类和plusDays(1)方法在日期范围内前进：

```java
void iterateBetweenDatesJava8(LocalDate start, LocalDate end) {
    for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
        processDate(date);
    }
}
```

这里值得注意的是，虽然Stream API从Java 8开始可用，但在Java 9之前，使用Date API和Stream API在两个日期之间进行迭代是不可能的。

## 4.Java9+

Java 9引入了datesUntil方法，它允许我们使用Stream API从开始日期迭代到结束日期。

让我们更新我们的示例代码以利用此功能：

```java
void iterateBetweenDatesJava9(LocalDate start, LocalDate end) {
    start.datesUntil(end).forEach(this::processDate);
}
```

## 5. 总结

在Java中，迭代一系列日期是一项简单的任务。在使用Java 8及更高版本时尤其如此，我们可以使用Date API更轻松地处理日期。

请注意，在Java 7和更早版本中，建议同时处理日期和时间，即使我们只使用日期。但是，在Java 8及更高版本中，我们可以根据需要从Date API中选择合适的类，如LocalDate、 LocalDateTime和其他选项。

当然，从Java 9开始，我们可以结合使用Stream API和Date API来迭代日期流。