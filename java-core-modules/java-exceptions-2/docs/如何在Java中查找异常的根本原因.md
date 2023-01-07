## 1. 概述

在Java中使用嵌套异常是很常见的，因为它们可以帮助我们跟踪错误的来源。

当我们处理这些类型的异常时，有时我们可能想知道导致异常的原始问题，以便我们的应用程序可以针对每种情况做出不同的响应。当我们使用将根异常包装到自己的框架中时，这尤其有用。

在这篇简短的文章中，我们将展示如何使用纯Java以及[Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)和[Google Guava](https://github.com/google/guava)等外部库来获取根本原因异常。

## 2. 年龄计算器应用程序

我们的应用程序将是一个年龄计算器，它告诉我们一个人从给定日期开始的年龄，该日期是ISO 格式的字符串。我们将在解析日期时处理 2 种可能的错误情况：格式错误的日期和未来的日期。

让我们首先为我们的错误案例创建异常：

```java
static class InvalidFormatException extends DateParseException {

    InvalidFormatException(String input, Throwable thr) {
        super("Invalid date format: " + input, thr);
    }
}

static class DateOutOfRangeException extends DateParseException {

    DateOutOfRangeException(String date) {
        super("Date out of range: " + date);
    }

}
```

这两个异常都继承自一个共同的父异常，这将使我们的代码更加清晰：

```java
static class DateParseException extends RuntimeException {

    DateParseException(String input) {
        super(input);
    }

    DateParseException(String input, Throwable thr) {
        super(input, thr);
    }
}
```

之后，我们可以使用解析日期的方法来实现AgeCalculator类：

```java
static class AgeCalculator {

    private static LocalDate parseDate(String birthDateAsString) {
        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(birthDateAsString);
        } catch (DateTimeParseException ex) {
            throw new InvalidFormatException(birthDateAsString, ex);
        }

        if (birthDate.isAfter(LocalDate.now())) {
            throw new DateOutOfRangeException(birthDateAsString);
        }

        return birthDate;
    }
}
```

如我们所见，当格式错误时，我们将 DateTimeParseException 包装到我们自定义的InvalidFormatException 中。

最后，让我们向我们的类添加一个公共方法来接收日期、解析它然后计算年龄：

```java
public static int calculateAge(String birthDate) {
    if (birthDate == null || birthDate.isEmpty()) {
        throw new IllegalArgumentException();
    }

    try {
        return Period
          .between(parseDate(birthDate), LocalDate.now())
          .getYears();
    } catch (DateParseException ex) {
        throw new CalculationException(ex);
    }
}
```

如图所示，我们再次包装异常。在这种情况下，我们将它们包装到我们必须创建的CalculationException中：

```java
static class CalculationException extends RuntimeException {

    CalculationException(DateParseException ex) {
        super(ex);
    }
}
```

现在，我们可以通过以 ISO 格式传递任何日期来使用我们的计算器：

```java
AgeCalculator.calculateAge("2019-10-01");
```

如果计算失败，了解问题出在哪里会很有用，不是吗？继续阅读以了解我们如何做到这一点。

## 3. 使用普通Java查找根本原因

我们将用来查找根本原因异常的第一种方法是创建一个自定义方法，该方法循环遍历所有原因，直到找到根源：

```java
public static Throwable findCauseUsingPlainJava(Throwable throwable) {
    Objects.requireNonNull(throwable);
    Throwable rootCause = throwable;
    while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
        rootCause = rootCause.getCause();
    }
    return rootCause;
}
```

请注意，我们在循环中添加了一个额外条件，以避免在处理递归原因时出现无限循环。

如果我们将无效格式传递给AgeCalculator，我们将得到DateTimeParseException作为根本原因：

```java
try {
    AgeCalculator.calculateAge("010102");
} catch (CalculationException ex) {
    assertTrue(findCauseUsingPlainJava(ex) instanceof DateTimeParseException);
}
```

但是，如果我们使用未来的日期，我们将得到一个DateOutOfRangeException：

```java
try {
    AgeCalculator.calculateAge("2020-04-04");
} catch (CalculationException ex) {
    assertTrue(findCauseUsingPlainJava(ex) instanceof DateOutOfRangeException);
}
```

此外，我们的方法也适用于非嵌套异常：

```java
try {
    AgeCalculator.calculateAge(null);
} catch (Exception ex) {
    assertTrue(findCauseUsingPlainJava(ex) instanceof IllegalArgumentException);
}
```

在这种情况下，我们得到一个IllegalArgumentException因为我们传入了null。

## 4. 使用 Apache Commons Lang 查找根本原因

我们现在将演示使用第三方库而不是编写我们的自定义实现来查找根本原因。

Apache Commons Lang 提供了一个[ExceptionUtils](https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/exception/ExceptionUtils.java)类，它提供了一些实用方法来处理异常。

我们将在前面的示例中使用getRootCause()方法：

```java
try {
    AgeCalculator.calculateAge("010102");
} catch (CalculationException ex) {
    assertTrue(ExceptionUtils.getRootCause(ex) instanceof DateTimeParseException);
}
```

我们得到了与以前相同的根本原因。相同的行为适用于我们上面列出的其他示例。

## 5.使用番石榴找到根本原因

我们要尝试的最后一种方法是使用 Guava。类似于 Apache Commons Lang，它提供了一个带有getRootCause()实用方法的[Throwables](https://github.com/google/guava/blob/master/guava/src/com/google/common/base/Throwables.java)类。

让我们用同样的例子试试看：

```java
try {
    AgeCalculator.calculateAge("010102");
} catch (CalculationException ex) {
    assertTrue(Throwables.getRootCause(ex) instanceof DateTimeParseException);
}
```

该行为与其他方法完全相同。

## 六，总结

在本文中，我们演示了如何在我们的应用程序中使用嵌套异常，并实现了一个实用方法来查找异常的根本原因。

我们还展示了如何使用第三方库(如 Apache Commons Lang 和 Google Guava)来实现同样的目的。