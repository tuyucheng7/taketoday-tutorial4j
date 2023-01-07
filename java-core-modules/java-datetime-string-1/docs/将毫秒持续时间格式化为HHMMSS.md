## 1. 概述

持续时间是用小时、分钟、秒、毫秒等表示的时间量。我们可能希望将持续时间格式化为某种特定的时间模式。

我们可以通过在一些 JDK 库的帮助下编写自定义代码或使用第三方库来实现这一点。

在本快速教程中，我们将了解如何编写简单代码以将给定持续时间格式化为 HH:MM:SS 格式。

## 2. Java解决方案

可以通过多种方式来表示持续时间——例如，以分钟、秒和毫秒为单位，或者作为具有自己特定格式的JavaDuration 。

本节和后续部分将重点关注使用某些 JDK 库将间隔(经过的时间)格式化为 HH:MM:SS，以毫秒为单位指定。为了我们的示例，我们将 38114000ms 格式化为 10:35:14 (HH:MM:SS)。

### 2.1. 期间

从Java8 开始，引入了[Duration](https://www.baeldung.com/java-period-duration)类来处理各种单位的时间间隔。Duration类带有许多辅助方法，用于从持续时间中获取小时、分钟和秒。

要使用Duration类将间隔格式化为 HH:MM:SS ，我们需要使用Duration类中的Millis工厂方法从我们的间隔初始化Duration对象。这会将间隔转换为我们可以使用的Duration对象：

```java
Duration duration = Duration.ofMillis(38114000);
```

为了便于从秒到我们想要的单位的计算，我们需要获得持续时间或间隔中的总秒数：

```java
long seconds = duration.getSeconds();
```

然后，一旦我们有了秒数，我们就会为我们想要的格式生成相应的小时、分钟和秒：

```java
long HH = seconds / 3600;
long MM = (seconds % 3600) / 60;
long SS = seconds % 60;
```

最后，我们格式化生成的值：

```java
String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);
```

让我们试试这个解决方案：

```java
assertThat(timeInHHMMSS).isEqualTo("10:35:14");
```

如果我们使用Java9 或更高版本，我们可以使用一些辅助方法直接获取单位而无需执行任何计算：

```java
long HH = duration.toHours();
long MM = duration.toMinutesPart();
long SS = duration.toSecondsPart();
String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);

```

上面的代码片段会给我们与上面测试的相同的结果：

```java
assertThat(timeInHHMMSS).isEqualTo("10:35:14");
```

### 2.2. 时间单位

就像上一节中讨论的Duration 类一样， TimeUnit表示给定粒度的时间。它提供了一些辅助方法来跨单位转换——在我们的例子中是小时、分钟和秒——并在这些单位中执行计时和延迟操作。

要将以毫秒为单位的持续时间格式化为 HH:MM:SS 格式，我们需要做的就是使用TimeUnit中相应的辅助方法：

```java
long HH = TimeUnit.MILLISECONDS.toHours(38114000);
long MM = TimeUnit.MILLISECONDS.toMinutes(38114000) % 60;
long SS = TimeUnit.MILLISECONDS.toSeconds(38114000) % 60;
```

然后，根据上面生成的单位格式化持续时间：

```java
String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);
assertThat(timeInHHMMSS).isEqualTo("10:35:14");
```

## 3.使用第三方库

我们可能会选择使用第三方库方法而不是自己编写来尝试不同的路线。

### 3.1. 阿帕奇公地

要使用[Apache Commons](https://www.baeldung.com/java-commons-lang-3)，我们需要将[commons-lang3](https://search.maven.org/search?q=g:org.apache.commons a:commons-lang3)添加到我们的项目中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

正如预期的那样，该库在其DurationFormatUtils类中具有formatDuration 以及其他单位格式化方法：

```java
String timeInHHMMSS = DurationFormatUtils.formatDuration(38114000, "HH:MM:SS", true);
assertThat(timeInHHMMSS).isEqualTo("10:35:14");
```

### 3.2. 尤达时间

当我们使用Java8 之前的Java版本时， [Joda Time](https://www.baeldung.com/joda-time)库会派上用场，因为它有方便的辅助方法来表示和格式化时间单位。要使用 Joda Time，让我们将[joda-time 依赖](https://search.maven.org/search?q=g:joda-time a:joda-time)项添加到我们的项目中：

```xml
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.10.10</version>
</dependency>
```

Joda Time 有一个Duration类来表示时间。首先，我们将以毫秒为单位的时间间隔转换为 Joda Time Duration对象的实例：

```java
Duration duration = new Duration(38114000);
```

然后，我们使用Duration中的toPeriod 方法从上面的持续时间中获取周期，该方法将其转换或初始化为 Joda Time 中的Period类的实例：

```java
Period period = duration.toPeriod();
```

我们使用相应的辅助方法从Period中获取单位(小时、分钟和秒) ：

```java
long HH = period.getHours();
long MM = period.getMinutes();
long SS = period.getSeconds();
```

最后，我们可以格式化持续时间并测试结果：

```java
String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);
assertThat(timeInHHMMSS).isEqualTo("10:35:14");
```

## 4。总结

在本教程中，我们学习了如何将持续时间格式化为特定格式(在本例中为 HH:MM:SS)。

首先，我们使用Java 自带的Duration 和TimeUnit 类来获取所需的单位并借助Formatter对其进行格式化。

最后，我们研究了如何使用一些第三方库来实现结果。