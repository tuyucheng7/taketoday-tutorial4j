## 1. 概述

在软件开发中处理日期涉及解决特定问题的各种任务。与此相关的一项常规任务是获取当前日期或日期/时间。

在本教程中，我们将探索一些在 Kotlin 中获取当前日期/时间的技术。

## 2.实施

要处理日期或日期/时间值，我们将利用LocalDate和LocalDateTime类本机提供的支持。

### 2.1. 当前LocalDate和LocalDateTime

首先，让我们使用静态方法now()获取当前日期：

```kotlin
val current = LocalDate.now()

```

同样，让我们应用相同的方法来获取当前日期/时间：

```kotlin
val current = LocalDateTime.now()
```

如果我们想以某种格式获取日期，我们可以在DateTimeFormatter类的帮助下应用格式：

```kotlin
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
val current = LocalDateTime.now().format(formatter)
```

### 2.2. 使用日历

Calendar类提供了另一种通过检索时间属性获取当前日期/时间的方法。此外，我们可以应用格式来获得更合适的表示：

```kotlin
val time = Calendar.getInstance().time 
val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm") 
val current = formatter.format(time)
```

此外，Calendar类允许我们从其组件构建当前日期/时间：

```kotlin
val calendar = Calendar.getInstance()

val current = LocalDateTime.of(
	 calendar.get(Calendar.YEAR),
	 calendar.get(Calendar.MONTH),
	 calendar.get(Calendar.DAY_OF_MONTH),
	 calendar.get(Calendar.HOUR_OF_DAY),
	 calendar.get(Calendar.MINUTE),
	 calendar.get(Calendar.SECOND)
)
```

### 2.3. 使用java.util.Date

java.util包中包含Date类，该类在java.time包引入之前被广泛使用。要使用java.util实现获取当前日期，我们只需要实例化Date类的一个新对象。此外，我们可以采用与之前使用DateTimeFormatter相同的方式应用格式：

```kotlin
val formatter = SimpleDateFormat("yyyy-MM-dd")
val date = Date()
val current = formatter.format(date)
```

## 3.总结

在本教程中，我们探索了一些在 Kotlin 中生成当前日期/时间的技术。我们看到了使用LocalDate、LocalDateTime和Calendar的实现，还了解了java.util.Date类提供的支持。