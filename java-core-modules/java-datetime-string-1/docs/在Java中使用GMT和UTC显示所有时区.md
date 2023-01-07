## 1. 概述

每当我们处理时间和日期时，我们都需要一个参考框架。该标准是[UTC](https://en.wikipedia.org/wiki/Coordinated_Universal_Time) ，但我们在某些应用程序中也看到[GMT 。](https://en.wikipedia.org/wiki/Greenwich_Mean_Time)

简而言之，UTC 是标准，而 GMT 是时区。

这是维基百科告诉我们使用什么的内容：

>   对于大多数用途，UTC 被认为可以与格林威治标准时间 (GMT) 互换，但 GMT 不再由科学界精确定义。

换句话说，一旦我们编译了一个包含 UTC 时区偏移量的列表，我们也会为 GMT 提供它。

首先，我们将看看Java8 实现此目的的方法，然后我们将了解如何在Java7 中获得相同的结果。

## 2. 获取区域列表

首先，我们需要检索所有已定义时区的列表。

为此，ZoneId类有一个方便的静态方法：

```java
Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
```

然后，我们可以使用Set生成一个排序的时区列表及其相应的偏移量：

```java
public List<String> getTimeZoneList(OffsetBase base) {
 
    LocalDateTime now = LocalDateTime.now();
    return ZoneId.getAvailableZoneIds().stream()
      .map(ZoneId::of)
      .sorted(new ZoneComparator())
      .map(id -> String.format(
        "(%s%s) %s", 
        base, getOffset(now, id), id.getId()))
      .collect(Collectors.toList());
}
```

上面的方法使用一个枚举参数来表示我们想要看到的偏移量：

```java
public enum OffsetBase {
    GMT, UTC
}
```

现在让我们更详细地检查代码。

检索到所有可用区域 ID 后，我们需要一个实际时间参考，由LocalDateTime.now() 表示。

之后，我们使用Java的Stream API 迭代我们的时区字符串id 集合中的每个条目，并将其转换为具有相应偏移量的格式化时区列表。

对于这些条目中的每一个，我们使用map(ZoneId::of)生成一个ZoneId实例。

## 3.获得补偿

我们还需要找到实际的 UTC 偏移量。例如，在欧洲中部时间的情况下，偏移量将为+01:00。

要获取任何给定区域的 UTC 偏移量，我们可以使用LocalDateTime 的 getOffset()方法。

另请注意，Java 将+00:00偏移量表示为Z。

因此，为了使偏移量为零的时区字符串看起来一致，我们将用+00:00替换Z ：

```java
private String getOffset(LocalDateTime dateTime, ZoneId id) {
    return dateTime
      .atZone(id)
      .getOffset()
      .getId()
      .replace("Z", "+00:00");
}
```

## 4. 使区域具有可比性

可选地，我们还可以根据偏移量对时区进行排序。

为此，我们将使用ZoneComparator类：

```java
private class ZoneComparator implements Comparator<ZoneId> {

    @Override
    public int compare(ZoneId zoneId1, ZoneId zoneId2) {
        LocalDateTime now = LocalDateTime.now();
        ZoneOffset offset1 = now.atZone(zoneId1).getOffset();
        ZoneOffset offset2 = now.atZone(zoneId2).getOffset();

        return offset1.compareTo(offset2);
    }
}
```

## 5.显示时区

剩下要做的就是通过为每个OffsetBase 枚举值调用getTimeZoneList()方法并显示列表来将上述部分放在一起：

```java
public class TimezoneDisplayApp {

    public static void main(String... args) {
        TimezoneDisplay display = new TimezoneDisplay();

        System.out.println("Time zones in UTC:");
        List<String> utc = display.getTimeZoneList(
          TimezoneDisplay.OffsetBase.UTC);
        utc.forEach(System.out::println);

        System.out.println("Time zones in GMT:");
        List<String> gmt = display.getTimeZoneList(
          TimezoneDisplay.OffsetBase.GMT);
        gmt.forEach(System.out::println);
    }
}
```

当我们运行上面的代码时，它会打印 UTC 和 GMT 的时区。

下面是输出结果的一个片段：

```plaintext
Time zones in UTC:
(UTC+14:00) Pacific/Apia
(UTC+14:00) Pacific/Kiritimati
(UTC+14:00) Pacific/Tongatapu
(UTC+14:00) Etc/GMT-14
```

## 6.Java7 及之前版本

Java 8 通过使用Stream和Date and Time API 简化了这项任务。

但是，如果我们有一个Java7 和之前的项目，我们仍然可以通过依赖java.util.TimeZone类及其getAvailableIDs()方法来获得相同的结果：

```java
public List<String> getTimeZoneList(OffsetBase base) {
    String[] availableZoneIds = TimeZone.getAvailableIDs();
    List<String> result = new ArrayList<>(availableZoneIds.length);

    for (String zoneId : availableZoneIds) {
        TimeZone curTimeZone = TimeZone.getTimeZone(zoneId);
        String offset = calculateOffset(curTimeZone.getRawOffset());
        result.add(String.format("(%s%s) %s", base, offset, zoneId));
    }
    Collections.sort(result);
    return result;
}
```

与Java8 代码的主要区别在于偏移计算。

我们从TimeZone()的getRawOffset()方法获得的rawOffset表示时区的偏移量(以毫秒为单位) 。

因此，我们需要使用TimeUnit类将其转换为小时和分钟：

```java
private String calculateOffset(int rawOffset) {
    if (rawOffset == 0) {
        return "+00:00";
    }
    long hours = TimeUnit.MILLISECONDS.toHours(rawOffset);
    long minutes = TimeUnit.MILLISECONDS.toMinutes(rawOffset);
    minutes = Math.abs(minutes - TimeUnit.HOURS.toMinutes(hours));

    return String.format("%+03d:%02d", hours, Math.abs(minutes));
}
```

## 七、总结

在本快速教程中，我们了解了如何编译所有可用时区及其 UTC 和 GMT 时差的列表。