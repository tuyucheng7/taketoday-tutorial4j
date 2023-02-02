## 1. 概述

[Java的record关键字](https://www.baeldung.com/java-record-keyword)是Java 14中引入的一个新语义特性。**记录对于创建小型不可变对象非常有用**。另一方面，[Lombok](https://www.baeldung.com/intro-to-project-lombok)**是一个Java库，可以自动生成一些已知的模式作为Java字节码**。尽管它们都可以用来减少样板代码，但它们是不同的工具。因此，我们应该在给定的上下文中使用更适合我们需求的那个。

在本文中，我们将探讨各种用例，包括Java记录的一些限制。对于每个示例，我们将看到Lombok如何派上用场并比较这两种解决方案。

## 2. 小型不可变对象

对于我们的第一个示例，我们使用Color对象。Color由代表红色、绿色和蓝色的三个整数值组成。此外，颜色将暴露其十六进制表示。例如，RGB(255,0,0)的颜色将具有#FF0000的十六进制表示。此外，如果两种颜色具有相同的RGB值，我们希望它们相等。

出于这些原因，在这种情况下选择记录是非常有意义的：

```java
public record ColorRecord(int red, int green, int blue) {

    public String getHexString() {
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}
```

同样，Lombok允许我们使用@Value注解创建不可变对象：

```java
@Value
public class ColorValueObject {
    int red;
    int green;
    int blue;

    public String getHexString() {
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}
```

尽管如此，从Java 14开始，记录将成为这些用例的自然方式。

## 3. 透明数据载体

根据JDK增强提案([JEP 359](https://openjdk.org/jeps/359))，**记录是充当不可变数据的透明载体的类。因此，我们无法阻止记录公开其成员字段**。例如，我们不能强制上一个示例中的ColorRecord只公开hexString而完全隐藏三个int字段。

但是，Lombok允许我们自定义getter的名称、访问级别和返回类型。让我们相应地更新ColorValueObject：

```java
@Value
@Getter(AccessLevel.NONE)
public class ColorValueObject {
    int red;
    int green;
    int blue;

    public String getHexString() {
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}
```

**因此，如果我们需要不可变的数据对象，记录是一个很好的解决方案**。

**但是，如果我们想隐藏成员字段并只公开使用它们执行的一些操作，则Lombok将更适合**。

## 4. 有很多字段的类

记录旨在提供一种非常方便的方式来创建小的、不可变的对象。但如果数据模型需要更多字段，记录会是什么样子。对于此示例，考虑以下Student数据模型：

```java
public record StudentRecord(
      String firstName,
      String lastName,
      Long studentId,
      String email,
      String phoneNumber,
      String address,
      String country,
      int age) {
}
```

我们已经可以猜到StudentRecord的实例化将难以阅读和理解，特别是如果某些字段不是强制性的：

```java
StudentRecord john = new StudentRecord("John", "Doe", null, "john@doe.com", null, null, "England", 20);
```

为了促进这些用例，Lombok提供了[构建器设计模式](https://www.baeldung.com/creational-design-patterns#builder)的实现。

为了使用它，我们只需要用@Builder标注我们的类：

```java
@Getter
@Builder
public class StudentBuilder {
    private String firstName;
    private String lastName;
    private Long studentId;
    private String email;
    private String phoneNumber;
    private String address;
    private String country;
    private int age;
}
```

现在，让我们使用StudentBuilder创建一个具有相同属性的对象：

```java
StudentBuilder john = StudentBuilder.builder()
    .firstName("John")
    .lastName("Doe")
    .email("john@doe.com")
    .country("England")
    .age(20)
    .build();
```

如果我们将两者进行比较，可以注意到使用构建器模式是有利的，这样的代码通常更清晰。

**总之，记录更适合较小的对象。但是，对于具有许多字段的对象，缺少构建器模式将使Lombok的@Builder成为更好的选择**。

## 5. 可变数据

我们可以将Java记录专门用于不可变数据。**如果上下文需要一个可变的Java对象，我们可以使用Lombok的@Data对象来代替**：

```java
@Data
@AllArgsConstructor
public class ColorData {

    private int red;
    private int green;
    private int blue;

    public String getHexString() {
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}
```

某些框架可能需要带有Setter或默认构造函数的对象，例如Hibernate就属于这一类。在创建@Entity时，我们必须使用Lombok的注解或纯Java。

## 6. 继承

**Java记录不支持继承**，因此它们不能被扩展或继承其他类。另一方面，Lombok的@Value对象可以扩展其他类，但它们是final的：

```java
@Value
public class MonochromeColor extends ColorData {
    
    public MonochromeColor(int grayScale) {
        super(grayScale, grayScale, grayScale);
    }
}
```

此外，@Data对象既可以扩展其他类，也可以被扩展。**总之，如果我们需要继承，我们应该坚持使用Lombok的解决方案**。

## 7. 总结

在本文中，我们已经看到Lombok和Java记录是不同的工具，并且具有不同的用途。此外，我们发现Lombok更灵活，可用于记录有限的场景。