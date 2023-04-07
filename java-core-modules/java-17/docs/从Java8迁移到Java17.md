## 1. 概述

我们经常面临是迁移到更新版本的Java还是继续使用现有版本的两难选择。换句话说，我们需要平衡新功能和增强功能与迁移所需的总工作量。

在本教程中，**我们将介绍新版本Java中提供的一些非常有用的功能**。这些特性不仅易于学习，而且在计划从Java 8迁移到Java 17时也可以毫不费力地快速实现。

## 2. 使用字符串

让我们看一下String类的一些有趣的增强功能。

### 2.1 紧凑的字符串

Java 9引入了[Compact String](https://www.baeldung.com/java-9-compact-string)，这是一种性能增强功能，可以优化String对象的内存消耗。

**简单来说，String对象将在内部表示为byte[]而不是char[]**。解释一下，每个char由2个字节组成，因为Java内部使用UTF-16。在大多数情况下，String对象是可以用单个字节表示的英语单词，它只不过是LATIN-1表示法。

Java根据String对象的实际内容在内部处理此表示：一个byte[]用于LATIN-1字符集，如果内容包含任何特殊字符(UTF-16)，则为char[]。因此，它对使用String对象的开发人员来说是完全透明的。

在Java 8之前，字符串在内部表示为char[]：

```java
char[] value;
```

从Java 9开始，它将是一个byte[]：

```java
byte[] value;
```

### 2.2 文本块

在Java 15之前，嵌入多行代码片段需要明确的行终止符、字符串拼接和分隔符。为了解决这个问题，**Java 15引入了[文本块](https://www.baeldung.com/java-text-blocks)，允许我们或多或少地按原样嵌入代码片段和文本序列**。这在处理HTML、JSON和SQL等文字片段时特别有用。

文本块是字符串表示的另一种形式，可以在任何可以使用普通双引号字符串文本的地方使用。例如，可以在不显式使用行终止符和字符串拼接的情况下表示多行字符串文本：

```java
// Using a Text Block
String value = """
            Multi-line
            Text
            """;
```

在此功能之前，多行文本的可读性不强，表示起来也很复杂：

```java
// Using a Literal String
String value = "Multi-line"
                + "\n" \\ line separator
                "Text"
                + "\n";
String str = "Multi-line\nText\n";
```

### 2.3 新的字符串方法

在处理String对象时，我们往往倾向于使用Apache Commons等第三方库来进行常见的字符串操作。具体来说，这是实用程序函数检查空白/空值和其他字符串操作(如重复、缩进等)的情况。

随后，Java 11和Java 12引入了许多这样方便的函数，这样我们就可以依靠内置函数来进行常规的String操作：isBlank()、repeat()、indent()、lines()、strip()和transform()。

让我们看看它们的实际效果：

```java
assertThat("  ".isBlank());
assertThat("Twinkle ".repeat(2)).isEqualTo("Twinkle Twinkle ");
assertThat("Format Line".indent(4)).isEqualTo("    Format Line\n");
assertThat("Line 1 \n Line2".lines()).asList().size().isEqualTo(2);
assertThat(" Text with white spaces   ".strip()).isEqualTo("Text with white spaces");
assertThat("Car, Bus, Train".transform(s1 -> Arrays.asList(s1.split(","))).get(0)).isEqualTo("Car");
```

## 3. 记录

数据传输对象(DTO)在对象之间传递数据时很有用。但是，创建DTO会附带很多样板代码，例如字段、构造函数、getter/setter、[equals()](https://www.baeldung.com/java-equals-hashcode-contracts#equals)、[hashcode()](https://www.baeldung.com/java-hashcode)和[toString()](https://www.baeldung.com/java-tostring)方法：

```java
public class StudentDTO {

    private int rollNo;
    private String name;

    // constructors
    // getters & setters
    // equals(), hashcode() & toString() methods
}
```

输入[记录](https://www.baeldung.com/java-15-new#records-jep-384)类，**这是一种特殊的类，可以以更紧凑的方式定义不可变数据对象，并且与[Project Lombok](https://www.baeldung.com/intro-to-project-lombok)相同**。记录类最初作为Java 14的预览功能引入，是Java 16的标准功能：

```java
public record Student(int rollNo, String name) {
}
```

如我们所见，记录类只需要字段的类型和名称。随后，除了公共构造函数、私有字段和最终字段之外，编译器还生成equals()、hashCode()和toString()方法：

```java
Student student = new Student(10, "Priya");
Student student2 = new Student(10, "Priya");
        
assertThat(student.rollNo()).isEqualTo(10);
assertThat(student.name()).isEqualTo("Priya");
assertThat(student.equals(student2));
assertThat(student.hashCode()).isEqualTo(student2.hashCode());
```

## 4. 友好的NullPointerException

NullPointerException(NPE)是每个开发人员都会遇到的非常常见的异常。在大多数情况下，编译器抛出的错误消息对于识别为null的确切对象没有用。此外，近来函数式编程和方法链的趋势使得编写代码更加简洁明了，这使得调试NPE变得更加困难。

让我们看一个使用方法链的例子：

```java
student.getAddress().getCity().toLowerCase();
```

在这里，如果在这一行中抛出NPE，则很难精确定位null对象的确切位置，因为三个可能的对象都可能是null。

从Java 14开始，**我们现在可以使用额外的VM参数指示编译器获取[友好的NPE](https://www.baeldung.com/java-14-nullpointerexception)消息**：

```shell
-XX:+ShowCodeDetailsInExceptionMessages
```

启用此选项后，错误消息会更加准确：

```shell
Cannot invoke "String.toLowerCase()" because the return value of "com.baeldung.java8to17.Address.getCity()" is null
```

## 5. 模式匹配

**模式匹配解决了程序中的一个通用逻辑，即有条件地从对象中提取组件，以更简洁和安全地表达**。

让我们看一下Java中支持模式匹配的两个特性。

### 5.1 增强的instanceOf运算符

每个程序都有一个共同的逻辑是检查某个类型或结构并将其转换为所需的类型以执行进一步的处理。这涉及很多样板代码。

让我们看一个例子：

```java
if (obj instanceof Address) {
    Address address = (Address) obj; 
    city = address.getCity();
}
```

在这里，我们可以看到涉及三个步骤：测试(确认类型)、转换(转换为特定类型)和新局部变量(进一步处理)。

从Java 16开始，instanceof运算符的[模式匹配](https://openjdk.org/jeps/394)是解决此问题的标准功能。现在，我们可以通过更具可读性的方式直接访问目标类型：

```java
if (obj instanceof Address address) {
    city = address.getCity();
}
```

### 5.2 Switch表达式

**switch表达式(Java 14)类似于计算或返回单个值并可在语句中使用的正则表达式**。此外，Java 17使我们能够在[switch表达式](https://www.baeldung.com/java-switch-pattern-matching)中使用模式匹配(预览功能)：

```java
double circumference = switch(shape) {
    case Rectangle r -> 2 * r.length() + 2 * r.width();
    case Circle c -> 2 * c.radius() * Math.PI;
    default -> throw new IllegalArgumentException("Unknown shape");
};
```

正如我们所注意到的，有一种新的case标签语法。switch表达式使用“case L ->”标签而不是“case L:”标签。**此外，不需要明确的break语句来防止失败。此外，switch选择器表达式可以是任何类型**。

在switch表达式中使用传统的“case L:”标签时，我们必须使用yield关键字(而不是break语句)来返回值。

## 6. 密封类

继承的主要目的是代码的可重用性。但是，某些业务领域模型可能只需要一组预定义的类来扩展基类或接口。这在使用领域驱动设计时特别有价值。

为了增强这种行为，Java 17提供了[密封类](https://www.baeldung.com/java-sealed-classes-interfaces)作为标准功能。简而言之，**一个密封的类或接口只能由那些被允许这样做的类和接口来扩展或实现**。

让我们看看如何定义密封类：

```java
public sealed class Shape permits Circle, Square, Triangle {
}
```

在这里，Shape类只允许被一组受限制的类继承。此外，**允许的子类必须定义以下修饰符之一：final、sealed或non-sealed**：

```java
public final class Circle extends Shape {
    public float radius;
}

public non-sealed class Square extends Shape {
   public double side;
}   

public sealed class Triangle extends Shape permits ColoredTriangle {
    public double height, base;
}
```

## 7. 总结

多年来，Java从Java 8(LTS)到Java 17(LTS)分阶段引入了一系列新特性。这些特性中的每一个都旨在提高多个方面，例如生产力、性能、可读性和可扩展性。

在本文中，我们探讨了一组精选的可快速学习和实施的功能。具体来说，对String类、记录类型和模式匹配的改进将使从Java 8迁移到Java 17的开发人员的生活更加轻松。