## 1. 概述

我们几乎在每个应用程序中都会看到枚举。这些包括订单状态代码，如DRAFT和PROCESSING， 以及Web错误代码，如400、404、500、501等。每当我们在域中看到枚举数据时，我们都会在我们的应用程序中看到它的枚举。我们可以使用传入请求中的数据并找到该枚举。例如，我们可以将Web错误400映射到BAD_REQUEST。

因此，我们需要逻辑来按条件搜索枚举。这可能是它的名称或值。或者，它甚至可以是任意整数代码。

在本教程中，我们将学习如何按条件搜索枚举。此外，我们还将探索返回找到的枚举的不同方法。

## 2. 按名称搜索枚举

首先，我们知道枚举类型是一种特殊的数据类型。它使变量成为一组预定义的常量。让我们为方向定义一个枚举：

```java
public enum Direction {
    EAST, WEST, SOUTH, NORTH;
}
```

枚举值的名称是常量。例如，Direction.EAST的名称是EAST。现在我们可以通过名称搜索方向。实施不区分大小写的搜索是个好主意。因此，East、east和EAST都将映射到Direction.EAST。让我们将以下方法添加到Direction枚举中：

```java
public static Direction findByName(String name) {
    Direction result = null;
    for (Direction direction : values()) {
        if (direction.name().equalsIgnoreCase(name)) {
            result = direction;
            break;
        }
    }
    return result;
}
```

在此实现中，如果找不到给定名称的枚举，我们将返回null。这取决于我们如何处理未找到的情况。一种选择是我们可以返回一个默认的枚举值。相反，我们可以抛出异常。我们很快就会看到更多搜索枚举的例子。现在让我们测试我们的搜索逻辑。首先，积极的情景：

```java
@Test
public void givenWeekdays_whenValidDirectionNameProvided_directionIsFound() {
    Direction result = Direction.findByName("EAST");
    assertThat(result).isEqualTo(Direction.EAST);
}
```

在本文末尾，我们将提供完整代码实现的链接，但现在，我们将重点关注代码片段。在这里，我们搜索名称“EAST”的方向，我们希望得到Direction.EAST。如前所述，我们知道搜索不区分大小写，因此对于名称“east”或“East”我们应该得到相同的结果。让我们验证我们的期望：

```java
@Test
public void givenWeekdays_whenValidDirectionNameLowerCaseProvided_directionIsFound() {
    Direction result = Direction.findByName("east");
    assertThat(result).isEqualTo(Direction.EAST);
}
```

我们还可以再添加一个测试来验证搜索方法是否为名称“East”返回相同的结果。以下测试将说明我们对名称“East”得到相同的结果。

```java
@Test public void givenWeekdays_whenValidDirectionNameLowerCaseProvided_directionIsFound() { 
    Direction result = Direction.findByName("East"); 
    assertThat(result).isEqualTo(Direction.EAST); 
}
```

## 3. 按值搜索枚举

现在让我们为一周中的几天定义一个枚举。这一次，让我们提供一个值和名称。事实上，我们可以在枚举中定义任何数据成员，然后将其用于我们的应用程序逻辑。这是工作日枚举的代码：

```java
public Weekday {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    // ...
    SUNDAY("Sunday"),
    ;
    private final String value;

    Weekday(String value) {
        this.value = value;
    }
}
```

接下来，让我们实现按值搜索。所以对于“星期一”，我们应该得到Weekday.MONDAY。让我们将以下方法添加到枚举中：

```java
public static Weekday findByValue(String value) {
    Weekday result = null;
    for (Weekday day : values()) {
        if (day.getValue().equalsIgnoreCase(value)) {
            result = day;
            break;
        }
    }
    return result;
}
```

这里我们迭代枚举的常量，然后将值输入与枚举的值成员进行比较。如前所述，我们忽略了值的大小写。现在我们可以测试它：

```java
@Test
public void givenWeekdays_whenValidWeekdayValueProvided_weekdayIsFound() {
    Weekday result = Weekday.findByValue("Monday");
    assertThat(result).isEqualTo(Weekday.MONDAY);
}
```

如果我们不提供有效值，我们将返回null。让我们验证一下：

```java
@Test
public void givenWeekdays_whenInvalidWeekdayValueProvided_nullIsReturned() {
    Weekday result = Weekday.findByValue("mon");
    assertThat(result).isNull();
}
```

搜索并不总是需要按字符串值进行。那会很不方便，因为我们必须先将输入转换为字符串，然后再将其传递给搜索方法。现在让我们看看如何按非字符串值(例如整数值)进行搜索。

## 4. 按整数值搜索枚举

让我们定义一个名为Month的新枚举。这是Month枚举的代码：

```java
public enum Month {
    JANUARY("January", 1),
    FEBRUARY("February", 2),
    // ...
    DECEMBER("December", 12),
    ;

    private final String value;
    private final int code;

    Month(String value, int code) {
        this.value = value;
        this.code = code;
    }
}
```

我们可以看到month枚举有两个成员，value和code，其中code是一个整数值。让我们实现通过代码搜索月份的逻辑：

```java
public static Optional<Month> findByCode(int code) {
    return Arrays.stream(values()).filter(month -> month.getCode() == code).findFirst();
}
```

此搜索看起来与之前的搜索略有不同，因为我们使用Java 8功能来演示实现搜索的另一种方法。在这里，我们将返回枚举的Optional值，而不是返回枚举本身。同样，我们将返回一个空的Optional，而不是null。因此，如果我们在一个月中搜索代码1，我们应该得到Month.JANUARY。让我们通过测试来验证这一点：

```java
@Test
public void givenMonths_whenValidMonthCodeProvided_optionalMonthIsReturned() {
    Optional<Month> result = Month.findByCode(1);
    assertThat(result).isEqualTo(Optional.of(Month.JANUARY));
}
```

对于无效的代码值，我们应该得到一个空的Optional。让我们也通过测试来验证这一点：

```java
@Test
public void givenMonths_whenInvalidMonthCodeProvided_optionalEmptyIsReturned() {
    Optional<Month> result = Month.findByCode(0);
    assertThat(result).isEmpty();
}
```

在某些情况下，我们可能希望实施更严格的搜索。因此，我们不会容忍无效输入，我们会抛出异常来证明这一点。

## 5. 搜索方法抛出的异常

我们可能不想返回null或空的Optional值，而是抛出一个异常。抛出哪种异常完全取决于系统的需要。如果找不到枚举，我们将选择抛出IllegalArgumentException。这是搜索方法的代码：

```java
public static Month findByValue(String value) {
    return Arrays.stream(values()).filter(month -> month.getValue().equalsIgnoreCase(value)).findFirst().orElseThrow(IllegalArgumentException::new);
}
```

我们可以再次看到我们在抛出异常时使用了Java 8风格。让我们通过测试来验证它：

```java
@Test
public void givenMonths_whenInvalidMonthValueProvided_illegalArgExIsThrown() {
    assertThatIllegalArgumentException().isThrownBy(() -> Month.findByValue("Jan"));
}
```

本文中演示的搜索方法并不是唯一的方法，但它们代表了最常见的选项。我们还可以调整这些实现以满足我们系统的需要。

## 6. 总结

在本文中，我们学习了搜索枚举的各种方法。我们还讨论了返回结果的不同方式。最后，我们用可靠的单元测试支持这些实现。