## 1. 概述

在Java中，枚举是一种数据类型，可帮助我们将一组预定义的常量分配给变量。

在本快速教程中，我们将学习在Java中迭代Enum的不同方法。

## 2. 遍历枚举值

让我们首先定义一个Enum，这样我们就可以创建一些简单的代码示例：

```java
public enum DaysOfWeekEnum {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY
}
```

枚举没有迭代方法，例如forEach()或iterator()。相反，我们可以使用values()方法返回的枚举值数组。

### 2.1 使用for循环迭代

首先，我们可以简单地使用老派的for循环：

```java
for (DaysOfWeekEnum day : DaysOfWeekEnum.values()) { 
    System.out.println(day); 
}
```

### 2.2 使用Stream进行迭代

我们还可以使用java.util.Stream对Enum值执行操作。

要创建Stream，我们有两种选择。第一个是使用Stream.of：

```java
Stream.of(DaysOfWeekEnum.values());
```

第二个是使用Arrays.stream：

```java
Arrays.stream(DaysOfWeekEnum.values());
```

让我们扩展DaysOfWeekEnum类以使用Stream创建一个示例：

```java
public enum DaysOfWeekEnum {
    
    SUNDAY("off"), 
    MONDAY("working"), 
    TUESDAY("working"), 
    WEDNESDAY("working"), 
    THURSDAY("working"), 
    FRIDAY("working"), 
    SATURDAY("off");

    private String typeOfDay;

    DaysOfWeekEnum(String typeOfDay) {
        this.typeOfDay = typeOfDay;
    }
	
    // standard getters and setters 

    public static Stream<DaysOfWeekEnum> stream() {
        return Stream.of(DaysOfWeekEnum.values()); 
    }
}
```

现在我们将编写一个示例来打印非工作日：

```java
public class EnumStreamExample {

    public static void main() {
        DaysOfWeekEnum.stream()
              .filter(d -> d.getTypeOfDay().equals("off"))
              .forEach(System.out::println);
    }
}
```

我们运行时得到的输出是：

```shell
SUNDAY
SATURDAY
```

### 2.3 使用forEach()迭代

forEach()方法被添加到Java8中的Iterable接口。因此所有的java集合类都有forEach()方法的实现。为了将这些与Enum一起使用，我们首先需要将Enum转换为合适的集合。我们可以使用Arrays.asList()生成一个ArrayList，然后我们可以使用forEach()方法对其进行循环：

```java
Arrays.asList(DaysOfWeekEnum.values())
    .forEach(day -> System.out.println(day));
```

### 2.4 使用EnumSet迭代

EnumSet是一个专门的集合实现，我们可以将其与枚举类型一起使用：

```java
EnumSet.allOf(DaysOfWeekEnum.class)
    .forEach(day -> System.out.println(day));
```

### 2.5 使用枚举值的ArrayList

我们还可以将Enum的值添加到List中。这允许我们像其他操作一样操作List：

```java
List<DaysOfWeekEnum> days = new ArrayList<>();
days.add(DaysOfWeekEnum.FRIDAY);
days.add(DaysOfWeekEnum.SATURDAY);
days.add(DaysOfWeekEnum.SUNDAY);
for (DaysOfWeekEnum day : days) {
    System.out.println(day);
}
days.remove(DaysOfWeekEnum.SATURDAY);
if (!days.contains(DaysOfWeekEnum.SATURDAY)) {
    System.out.println("Saturday is no longer in the list");
}
for (DaysOfWeekEnum day : days) {
    System.out.println(day);
}
```

我们还可以使用Arrays.asList()创建一个ArrayList。

但是，由于ArrayList由枚举值数组支持，因此它将是不可变的，因此我们无法在列表中添加或删除项目。以下代码中的删除将失败并出现UnsupportedOperationException：

```java
List<DaysOfWeekEnum> days = Arrays.asList(DaysOfWeekEnum.values());
days.remove(0);
```

## 3. 总结

在本文中，我们讨论了使用forEach、Stream和Java中的for循环迭代Enum的各种方法。

如果我们需要执行任何并行操作，Stream是一个不错的选择。否则，对使用哪种方法没有限制。