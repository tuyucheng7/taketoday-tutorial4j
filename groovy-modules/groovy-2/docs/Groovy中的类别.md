## 一、概述

有时我们可能想知道是否可以向编译后的 Java 或 Groovy 类添加一些额外的方便方法，而我们无法修改源代码。事实证明，Groovy 类别可以让我们做到这一点。

[Groovy](https://www.baeldung.com/groovy-language)是一种动态且功能强大的 JVM 语言，具有许多[元编程](https://www.baeldung.com/groovy-metaprogramming)功能。

在本教程中，我们将探讨 Groovy 中类别的概念。

## 2.什么是类别？

类别是一种元编程功能，受 Objective-C 的启发，它允许我们向新的或现有的 Java 或 Groovy 类添加额外的功能。

与[extensions](https://www.baeldung.com/groovy-metaprogramming#extensions)不同，默认情况下不启用类别提供的附加功能。因此，启用类别的关键是使用代码块。

类别实现的额外功能只能在使用代码块内访问。

## 3. Groovy 中的类别

让我们讨论一些在 Groovy 开发工具包中已经可用的重要类别。

### 3.1. 时间类别

TimeCategory类在 groovy.time 包中可用，[它](http://docs.groovy-lang.org/latest/html/api/groovy/time/TimeCategory.html)添加了一些方便的方法来处理日期和时间对象。

此类别添加了将 Integer 转换为时间符号的功能，如秒、分钟、天和月。

此外，TimeCategory类还提供了诸如plus和minus之类的方法，分别用于轻松地将Duration添加到Date对象和从Date对象中减去Duration。

让我们检查一下TimeCategory类提供的一些方便的功能。对于这些示例，我们将首先创建一个Date对象，然后使用TimeCategory执行一些操作：

```groovy
def jan_1_2019 = new Date("01/01/2019")
use (TimeCategory) {
    assert jan_1_2019 + 10.seconds == new Date("01/01/2019 00:00:10")
    assert jan_1_2019 + 20.minutes == new Date("01/01/2019 00:20:00")
    assert jan_1_2019 - 1.day == new Date("12/31/2018")
    assert jan_1_2019 - 2.months == new Date("11/01/2018")
}
```

让我们详细讨论代码。

此处，10.seconds创建值为 10 秒的[TimeDuration对象。](http://docs.groovy-lang.org/latest/html/api/groovy/time/TimeDuration.html)并且，加号 (+) 运算符将TimeDuration对象添加到Date对象。

同样，1.day创建值为 1 天的[Duration对象。](http://docs.groovy-lang.org/latest/html/api/groovy/time/Duration.html)并且，减号 (-) 运算符从Date对象中减去Duration对象。

此外， TimeCategory类还提供一些方法，例如now、ago和from，它允许创建相对日期。

例如，5.days.from.now将创建一个Date对象，其值比当前日期早 5 天。同样，2.hours.ago设置当前时间之前 2 小时的值。

让我们看看它们的实际效果。此外，我们将使用SimpleDateFormat在比较两个相似的Date对象时忽略时间的界限：

```groovy
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
use (TimeCategory) {
    assert sdf.format(5.days.from.now) == sdf.format(new Date() + 5.days)

    sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
    assert sdf.format(10.minutes.from.now) == sdf.format(new Date() + 10.minutes)
    assert sdf.format(2.hours.ago) == sdf.format(new Date() - 2.hours)
}
```

因此，使用TimeCategory类，我们可以使用我们已知的类编写简单且更易读的代码。

### 3.2. DOM类别

DOMCategory类在[groovy.xml.dom](http://docs.groovy-lang.org/latest/html/api/groovy/xml/dom/DOMCategory.html)包中可用。它提供了一些方便的方法来处理 Java 的 DOM 对象。

更具体地说，DOMCategory允许对 DOM 元素进行 GPath 操作，从而可以更轻松地遍历和处理 XML。

首先，让我们编写一个简单的 XML 文本并使用DOMBuilder类解析它：

```groovy
def baeldungArticlesText = """
<articles>
    <article core-java="true">
        <title>An Intro to the Java Debug Interface (JDI)</title>
        <desc>A quick and practical overview of Java Debug Interface.</desc>
    </article>
    <article core-java="false">
        <title>A Quick Guide to Working with Web Services in Groovy</title>
        <desc>Learn how to work with Web Services in Groovy.</desc>
    </article>
</articles>
"""

def baeldungArticlesDom = DOMBuilder.newInstance().parseText(baeldungArticlesText)
def root = baeldungArticlesDom.documentElement
```

在这里，根对象包含 DOM 的所有子节点。让我们使用DOMCategory类遍历这些节点：

```groovy
use (DOMCategory) {
    assert root.article.size() == 2

    def articles = root.article
    assert articles[0].title.text() == "An Intro to the Java Debug Interface (JDI)"
    assert articles[1].desc.text() == "Learn how to work with Web Services in Groovy."
}
```

在这里，DOMCategory类允许使用[GPath](https://groovy-lang.org/processing-xml.html#_gpath)提供的点操作轻松访问节点和元素。此外，它还提供了size和text等方法来访问任何节点或元素的信息。

现在，让我们使用DOMCategory将一个新节点附加到根DOM 对象：

```groovy
use (DOMCategory) {
    def articleNode3 = root.appendNode(new QName("article"), ["core-java": "false"])
    articleNode3.appendNode("title", "Metaprogramming in Groovy")
    articleNode3.appendNode("desc", "Explore the concept of metaprogramming in Groovy")

    assert root.article.size() == 3
    assert root.article[2].title.text() == "Metaprogramming in Groovy"
}
```

同样，DOMCategory类也包含一些方法，如appendNode和setValue来修改 DOM。

## 4.创建一个类别

现在我们已经看到了几个实际的 Groovy 类别，让我们探讨如何创建自定义类别。

### 4.1. 使用自身对象

类别类必须遵循某些惯例才能实现附加功能。

首先，添加附加功能的方法应该是静态的。其次，该方法的第一个参数应该是这个新特性适用的对象。

让我们将大写功能添加到String类。这将简单地将字符串的第一个字母更改为大写。

首先，我们将使用静态方法capitalize和String类型作为第一个参数来编写BaeldungCategory类：

```groovy
class BaeldungCategory {
    public static String capitalize(String self) {
        String capitalizedStr = self;
        if (self.size() > 0) {
            capitalizedStr = self.substring(0, 1).toUpperCase() + self.substring(1);
        }
        return capitalizedStr
    }
}
```

接下来，让我们编写一个快速测试以启用BaeldungCategory并验证String对象的大写功能：

```groovy
use (BaeldungCategory) {
    assert "norman".capitalize() == "Norman"
}
```

同样，让我们编写一个功能来提高一个数的另一个数的幂：

```groovy
public static double toThePower(Number self, Number exponent) {
    return Math.pow(self, exponent);
}
```

最后，让我们测试一下我们的自定义类别：

```groovy
use (BaeldungCategory) {
    assert 50.toThePower(2) == 2500
    assert 2.4.toThePower(4) == 33.1776
}
```

### 4.2. @Category注解

我们还可以使用@groovy.lang.Category注解将类别声明为实例样式类。使用注解时，我们必须提供我们的类别适用的类名。

可以在方法中使用this关键字访问对象的实例。因此，self对象不需要是第一个参数。

让我们编写一个NumberCategory类，并使用@Category注解将其声明为类别。此外，我们将向我们的新类别添加一些附加功能，例如cube和divideWithRoundUp ：

```groovy
@Category(Number)
class NumberCategory {
    public Number cube() {
        return thisthisthis
    }
    
    public int divideWithRoundUp(BigDecimal divisor, boolean isRoundUp) {
        def mathRound = isRoundUp ? BigDecimal.ROUND_UP : BigDecimal.ROUND_DOWN
        return (int)new BigDecimal(this).divide(divisor, 0, mathRound)
    }
}
```

在这里， divideWithRoundUp功能将一个数字除以除数，并根据isRoundUp参数将结果向上/向下舍入为下一个或上一个整数。

让我们测试一下我们的新类别：

```groovy
use (NumberCategory) {
    assert 3.cube() == 27
    assert 25.divideWithRoundUp(6, true) == 5
    assert 120.23.divideWithRoundUp(6.1, true) == 20
    assert 150.9.divideWithRoundUp(12.1, false) == 12
}
```

## 5.总结

在本文中，我们探讨了 Groovy 中类别的概念——一种元编程功能，可以在 Java 和 Groovy 类上启用其他功能。

我们已经检查了几个类别，例如TimeCategory和DOMCategory，它们在 Groovy 中已经可用。同时，我们探索了一些额外的便捷方式来使用这些类别来处理Date和 Java 的 DOM。

最后，我们探索了几种创建我们自己的自定义类别的方法。