## 1. 概述

在本教程中，我们将了解switch语句是什么以及如何使用它。

**switch语句允许我们替换多个嵌套的if-else结构，从而提高代码的可读性**。

switch随着时间的推移而发展。添加了新的支持类型，特别是在Java 5和7中。此外，它还在不断发展-switch表达式可能会在Java 12中引入。

下面我们将给出一些代码示例来演示switch语句的使用、break语句的作用、switch参数/case值的要求以及switch语句中String的比较。

让我们继续看这个例子。

## 2. 使用示例

假设我们有以下嵌套的if-else语句：

```java
public String exampleOfIF(String animal) {
    String result;
    if (animal.equals("DOG") || animal.equals("CAT")) {
        result = "domestic animal";
    } else if (animal.equals("TIGER")) {
        result = "wild animal";
    } else {
        result = "unknown animal";
    }
    return result;
}
```

上面的代码看起来不太好，很难维护和推理。

为了提高可读性，我们可以使用switch语句：

```java
public String exampleOfSwitch(String animal) {
    String result;
    switch (animal) {
        case "DOG":
            result = "domestic animal"; 
            break;
        case "CAT":
            result = "domestic animal";
            break;
        case "TIGER":
            result = "wild animal";
            break;
        default:
            result = "unknown animal";
            break;
    }
    return result;
}
```

我们将switch参数animal与几个case值进行比较。如果所有case值都不等于参数，则执行default标签下的块。

**简单地说，break语句用于退出switch语句**。

## 3. break语句

尽管现实生活中的大多数switch语句都暗示只应执行一个case块，但break语句是块完成后退出switch所必需的。

**如果我们忘记写break，下面的块将被执行**。

为了演示这一点，让我们省略break语句并将输出添加到每个块的控制台：

```java
public String forgetBreakInSwitch(String animal) {
    switch (animal) {
    case "DOG":
        System.out.println("domestic animal");
    default:
        System.out.println("unknown animal");
    }
}
```

让我们执行这段代码forgetBreakInSwitch("DOG")并检查输出以证明所有块都被执行：

```shell
domestic animal
unknown animal
```

因此，我们应该小心并在每个块的末尾添加break语句，除非需要传递到下一个标签下的代码。

唯一不需要中断的块是最后一个块，但是在最后一个块中添加中断可以使代码更不容易出错。

**当我们希望为多个case语句执行相同的代码时，我们也可以利用此行为来省略break**。

让我们通过将前两种情况组合在一起来重写上一节中的示例：

```java
public String exampleOfSwitch(String animal) {
    String result;
    switch (animal) {
        case "DOG":
        case "CAT":
            result = "domestic animal";
            break;
        case "TIGER":
            result = "wild animal";
            break;
        default:
            result = "unknown animal";
            break;
    }
    return result;
}
```

## 4. switch参数和case值

现在让我们讨论允许的switch参数和case值类型、对它们的要求以及switch语句如何与字符串一起使用。

### 4.1 数据类型

我们无法在switch语句中比较所有类型的对象和原始类型。**switch仅适用于四个基本类型及其包装器以及枚举类型和String类**：

-   byte和Byte
-   short和Short
-   int和Integer
-   char和Character
-   枚举
-   String

字符串类型在从Java 7开始的switch语句中可用。

枚举类型是在Java 5中引入的，从那时起就可以在switch语句中使用。

包装类也从Java 5开始可用。

当然，switch参数和case值应该是同一类型。

### 4.2 无空值

**我们不能将null值作为参数传递给switch语句**。

如果我们这样做，程序将抛出NullPointerException，使用我们的第一个switch示例：

```java
@Test(expected=NullPointerException.class)
public void whenSwitchAgumentIsNull_thenNullPointerException() {
    String animal = null;
    Assert.assertEquals("domestic animal", s.exampleOfSwitch(animal));
}
```

当然，我们也不能将null作为值传递给switch语句的case标签。如果我们这样做，代码将无法编译。

### 4.3 作为编译时常量的case值

如果我们尝试用变量dog替换DOG case值，代码将不会编译，直到我们将dog变量标记为final：

```java
final String dog="DOG";
String cat="CAT";

switch (animal) {
case dog: //compiles
    result = "domestic animal";
case cat: //does not compile
    result = "feline"
}
```

### 4.4 字符串比较

如果switch语句使用相等运算符来比较字符串，我们将无法正确地将使用new运算符创建的String参数与String case值进行比较。

幸运的是，**switch运算符在底层使用的是equals()方法**。

让我们证明这一点：

```java
@Test
public void whenCompareStrings_thenByEqual() {
    String animal = new String("DOG");
    assertEquals("domestic animal", s.exampleOfSwitch(animal));
}
```

## 5. Switch表达式

[JDK 13](https://openjdk.java.net/jeps/354)现已推出，并带来了[JDK 12](https://openjdk.java.net/jeps/325)中首次引入的新功能的改进版本：switch表达式。

**为了启用它，我们需要将--enable-preview传递给编译器**。

### 5.1 新的Switch表达式

让我们看看新的switch表达式在几个月内切换时的样子：

```java
var result = switch(month) {
    case JANUARY, JUNE, JULY -> 3;
    case FEBRUARY, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER -> 1;
    case MARCH, MAY, APRIL, AUGUST -> 2;
    default -> 0; 
};
```

发送诸如Month.JUNE之类的值会将result设置为3。

请注意，新语法使用->运算符而不是我们在switch语句中习惯使用的冒号。此外，没有break关键字：switch表达式不会落入cases。

另一个补充是我们现在可以有逗号分隔的标准。

### 5.2 yield关键字

更进一步，有可能通过使用代码块获得对表达式右侧发生的事情的细粒度控制。

在这种情况下，我们需要使用关键字yield：

```java
var result = switch (month) {
    case JANUARY, JUNE, JULY -> 3;
    case FEBRUARY, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER -> 1;
    case MARCH, MAY, APRIL, AUGUST -> {
        int monthLength = month.toString().length();
        yield monthLength * 4;
    }
    default -> 0;
};
```

虽然我们的示例有点武断，但关键是我们可以在这里访问更多Java语言。

### 5.3 返回内部switch表达式

由于switch语句和switch表达式之间的区别，**可以从switch语句内部返回，但我们不允许从switch表达式内部这样做**。

下面的例子是完全有效的并且可以编译：

```java
switch (month) {
    case JANUARY, JUNE, JULY -> { return 3; }
    default -> { return 0; }
}
```

但是，以下代码将无法编译，因为我们试图在封闭的switch表达式之外返回：

```java
var result = switch (month) {
    case JANUARY, JUNE, JULY -> { return 3; }
    default -> { return 0; }
};
```

### 5.4 详尽性

**使用switch语句时，是否涵盖所有情况并不重要**。

例如，下面的代码是完全有效的并且可以编译：

```java
switch (month) { 
    case JANUARY, JUNE, JULY -> 3; 
    case FEBRUARY, SEPTEMBER -> 1;
}
```

但是对于switch表达式，编译器坚持涵盖所有可能的情况。

以下代码片段无法编译，因为没有默认情况，并且也没有涵盖所有可能的情况：

```java
var result = switch (month) {
    case JANUARY, JUNE, JULY -> 3;
    case FEBRUARY, SEPTEMBER -> 1;
}
```

但是，当涵盖所有可能的情况时，switch表达式将有效：

```java
var result = switch (month) {
    case JANUARY, JUNE, JULY -> 3;
    case FEBRUARY, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER -> 1;
    case MARCH, MAY, APRIL, AUGUST -> 2;
}
```

请注意，上面的代码片段没有默认case。只要覆盖了所有情况，switch表达式就有效。

## 6. 总结

在本文中，我们讨论了在Java中使用switch语句的微妙之处。我们可以根据可读性和比较值的类型来决定是否使用switch。

当我们在预定义的集合(例如，一周中的几天)中只有有限数量的选项时，switch语句是一个很好的候选者。

否则，每次添加或删除新值时我们都必须修改代码，这可能不可行。对于这些情况，我们应该考虑其他方法，例如[多态性](https://www.baeldung.com/java-polymorphism)或其他设计模式，例如[命令模式](https://www.baeldung.com/java-command-pattern)。