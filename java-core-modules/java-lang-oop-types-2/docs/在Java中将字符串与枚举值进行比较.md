## 一、概述

Java在版本 5 中引入了[枚举。枚举提供了一种安全、干净的方法来管理常量。](https://www.baeldung.com/a-guide-to-java-enums)

在本快速教程中，我们将探索如何将*String*与*枚举*对象进行比较。

## 二、问题介绍

首先，让我们看一个*枚举*示例：

```java
enum Weekday {
    Mon("Monday"),
    Tue("Tuesday"),
    Wed("Wednesday"),
    Thu("Thursday"),
    Fri("Friday"),
    Sat("Saturday");
                                 
    private String fullName;
                                 
    Weekday(String fullName) {
        this.fullName = fullName;
    }
                                 
    public String getFullName() {
        return fullName;
    }
}复制
```

如上面的代码所示，*Weekday*枚举包含六个以每个工作日缩写命名的常量。此外，它还有一个属性*fullName，* 用于保存每个工作日的完整名称。

现在假设我们得到一个字符串*s*。然后将***s\*****与*****枚举\*****实例进行比较 可以有两种可能性**：

-   *将s*与*枚举*实例名称进行比较
-   *将s与**枚举*实例的一个*String*属性进行比较

本教程涵盖这两种情况。此外，**我们将执行不区分大小写的比较。**

为简单起见，我们将使用单元测试断言来验证比较结果。

那么接下来，让我们创建两个*String*作为输入：

```java
final String SAT = "sAt";
final String SATURDAY = "sAtuRdAy";复制
```

我们将使用*SAT*字符串进行*枚举*名称比较，并使用*SATURDAY*变量进行*枚举*的属性比较。为了使其完整，让我们为负面测试创建另外两个*String ：*

```java
final String TYPO_FRI = "ffri";
final String TYPO_FRIDAY = "ffriday";复制
```

在了解如何比较*String*和*枚举*实例之后，我们还将讨论这些比较的常见用例。那么接下来，让我们看看他们的行动。

## 3. 将给定*字符串*与*枚举*实例的名称或属性进行比较

首先，让我们看一下将给定的*String*与*枚举*实例的名称进行比较。

**所有\*枚举\*类都继承了抽象的\*java.lang.Enum\*类**。这个抽象类定义了*name()*方法来返回一个*枚举*实例的名字：

```java
public abstract class Enum<E extends Enum<E>> implements Constable, Comparable<E>, Serializable {
    private final String name;
    ...

    public final String name() {
        return this.name;
    }
...复制
```

因此，我们可以使用*name()*方法获取*枚举*常量的名称并将其与给定的*String*进行比较：

```java
assertTrue(SAT.equalsIgnoreCase(Sat.name()));
assertFalse(TYPO_FRI.equalsIgnoreCase(Fri.name()));复制
```

如上面的测试所示，我们使用*equalsIgnoreCase()*方法进行不区分大小写的比较。

我们已经提到，根据需要，我们可能希望将*String*与*枚举常量*的属性进行比较，例如*Weekday中的**fullName*属性。这并不困难，因为*Weekday*枚举有一个获取属性值的 getter 方法：

```java
assertTrue(SATURDAY.equalsIgnoreCase(Sat.getFullName()));
assertFalse(TYPO_FRI.equalsIgnoreCase(Fri.getFullName()));复制
```

因此，正如我们所见，无论哪种情况，将*String*与*枚举*进行比较都非常简单。

但是我们什么时候需要在实践中进行这种比较呢？让我们通过例子来谈谈。

## 4.通过名称和属性查找*枚举实例*

一个需要比较的常见用例是**通过给定的*****String\*****确定\*枚举\***实例。例如，我们想通过字符串“ *SAT”找到**Weekday.Sat*常量。

接下来，让我们在 Weekday 枚举中添加两个*“查找”*方法*：*

```java
enum Weekday {
    Mon("Monday"),
    ...

    static Optional<Weekday> byNameIgnoreCase(String givenName) {
        return Arrays.stream(values()).filter(it -> it.name().equalsIgnoreCase(givenName)).findAny();
    }

    static Optional<Weekday> byFullNameIgnoreCase(String givenFullName) {
        return Arrays.stream(values()).filter(it -> it.fullName.equalsIgnoreCase(givenFullName)).findAny();
    }
   ...
}复制
```

这两种方法的实现非常相似。一种是按名称查找*，另一种是按**fullName*属性查找 。

我们在实现中使用了[Stream API 。](https://www.baeldung.com/java-8-streams)首先，*values()*是一个*静态*方法。此外，**它在任何\*枚举\*类型中都可用，并返回相应\*枚举类型的所有\**枚举\*常量的数组。**因此，*Weekday.values()*为我们提供了所有*工作日*常量。

然后，我们将常量数组转换为*Stream*对象。接下来，我们将不区分大小写的比较逻辑作为[lambda 表达式传递给](https://www.baeldung.com/java-8-lambda-expressions-tips)[*filter()*](https://www.baeldung.com/java-stream-filter-lambda)方法。

由于我们不知道*filter()*方法是否可以找到匹配的*枚举*实例，**因此我们返回[\*findAny()\*](https://www.baeldung.com/java-stream-findfirst-vs-findany)方法的结果，这是一个[\*Optional\*](https://www.baeldung.com/java-optional)对象**。

方法调用者可以通过检查此*可选*结果来决定下一步行动。接下来，让我们看看它在测试方法中是如何工作的：

```java
Optional<Weekday> optResult = Weekday.byNameIgnoreCase(SAT);
assertTrue(optResult.isPresent());
assertEquals(Sat, optResult.get());
                                                                  
Optional<Weekday> optResult2 = Weekday.byNameIgnoreCase(TYPO_FRI);
assertFalse(optResult2.isPresent());复制
```

如上面的测试所示，**仅当\*byNameIgnoreCase()\*找到常量时，\*Optional\*结果的\*isPresent()\*才返回\*true\***。

这与“通过属性查找*枚举*常量”场景非常相似。让我们为*byFullNameIgnoreCase()*方法的完整性创建一个测试：

```java
Optional<Weekday> optResult = Weekday.byFullNameIgnoreCase(SATURDAY);
assertTrue(optResult.isPresent());
assertEquals(Sat, optResult.get());
                                                                         
Optional<Weekday> optResult2 = Weekday.byFullNameIgnoreCase(TYPO_FRIDAY);
assertFalse(optResult2.isPresent());复制
```

## 5.结论

在本文中，我们学习了如何将*String*与*枚举*常量进行比较。此外，我们还通过示例讨论了比较的常见用例。