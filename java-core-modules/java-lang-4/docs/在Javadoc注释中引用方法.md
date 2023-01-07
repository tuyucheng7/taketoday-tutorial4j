## 1. 概述

在本教程中，我们将讨论如何在[Javadoc 注解](https://www.baeldung.com/javadoc)中引用Java方法。此外，我们将解决如何引用不同类和包中的方法。

## 2. @link标签

Javadoc 提供了[@link](https://www.baeldung.com/javadoc)内联标记来引用Java类中的成员。我们可以把@link标签想象成类似于 HTML 中的 [锚标签](https://www.w3schools.com/html/html_links.asp)，它用于通过超链接将一个页面链接到另一个页面。

让我们看一下使用@link标记在 Javadoc 注解中引用方法的语法：

```java
{@link path_to_member label}
```

与锚标签类似，path_to_member是目的地，label是显示文本。

标签是可选的，但path_to_member是引用方法所必需的。但是，最好始终使用标签名称以避免复杂的引用链接。path_to_member的语法 根据我们引用的方法是否位于同一类中而有所不同。

应该注意的是，左花括号{和@link之间不能有空格。如果它们之间有空格，Javadoc 工具将无法正确生成引用。但是， path_to_member、label和右花括号之间没有空格限制。

## 3.引用同一个类中的方法

引用方法的最简单方法是在同一个类中：

```java
{@link #methodName() LabelName}
```

假设我们正在记录一个方法，我们想从同一个类中引用另一个方法：

```java
/
  Also, check the {@link #move() Move} method for more movement details.
 /
public void walk() {
}

public void move() {
}
```

在这种情况下，walk()方法引用同一类中的move()实例方法。

如果被引用的方法有参数，每当我们想引用一个重载或参数化的方法时，我们必须相应地指定其参数的类型。

考虑以下引用重载方法的示例：

```java
/
  Check this {@link #move(String) Move} method for direction-oriented movement.
 /
public void move() {

}

public void move(String direction) {

}
```

move()方法是指采用一个String参数的重载方法。

## 4. 引用另一个类中的方法

要引用另一个类中的方法，我们将使用类名，后跟标签，然后是方法名：

```java
{@link ClassName#methodName() LabelName}
```

语法类似于引用同一类中的方法，除了在#符号之前提及类名。

现在，让我们考虑在另一个类中引用方法的示例：

```java
/
  Additionally, check this {@link Animal#run(String) Run} method for direction based run.
 /
public void run() {

}
```

引用的方法在同一个包中的Animal类中：

```java
public void run(String direction) {

}
```

如果我们想引用驻留在另一个包中的方法，我们有 2 个选项。一种方法是直接指定包和类名：

```java
/
  Also consider checking {@link com.baeldung.sealed.classes.Vehicle#Vehicle() Vehicle} 
  constructor to initialize vehicle object.
 /
public void goToWork() {

}

```

在这种情况下，Vehicle类已用完整的包名称提及，以引用Vehicle()方法。

此外，我们可以导入包并单独提及类名：

```java
import com.baeldung.sealed.records.Car;

/
  Have a look at {@link Car#getNumberOfSeats() SeatsAvailability} 
  method for checking the available seats needed for driving.
 /
public void drive() {

}
```

在这里，驻留在另一个包中的Car类已被导入。所以，@link只需要包含类名和方法。

我们可以选择两种方式中的任何一种来引用不同包中的方法。如果有单次使用的包，那么我们可以走第一种方式，否则，如果有多个依赖，我们应该选择第二种方式。

## 5. @linkplain标签

我们已经在注解中看到了用于引用方法的@link Javadoc 标记。Javadoc 提供了另一个名为@linkplain 的标签，用于在注解中引用方法，类似于@link标签。主要区别在于，在生成文档时，@link以等宽格式文本生成标签值，而@linkplain以标准格式(如纯文本)生成标签值。

## 六，总结

在本文中，我们讨论了如何在 Javadoc 注解中引用方法，我们还探讨了在其他类和包中引用方法。最后，我们检查了@link和@linkplain标签之间的区别。