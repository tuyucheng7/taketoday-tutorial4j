## 1. 概述

在本教程中，我们将讨论新的预览功能[JEP-405](https://openjdk.org/jeps/405)：[Java SE 19](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html)中的记录模式。我们将看到如何分解记录值以及如何将记录模式与类型模式结合起来。

## 2. 模型

我们将使用这两个[记录](https://www.baeldung.com/java-16-new-features#records-jep-395)一个具有纬度和经度的GPSPoint：

```java
public record GPSPoint (double latitude, double longitude) {}
```

和一个包含名称和GPSPoint的Location：

```java
public record Location (String name, GPSPoint gpsPoint) {}
```

## 3. 预览

记录模式是一种构造，它允许我们将值与记录类型进行匹配，并将变量绑定到记录的相应组件。我们还可以给记录模式一个可选的标识符，这使它成为一个命名的记录模式，并允许我们引用记录模式变量。

### 3.1 instanceof

Java 16中引入的[instanceof模式匹配](https://www.baeldung.com/java-16-new-features#pattern-matching-for-instanceof-jep-394)允许我们在instanceof检查期间直接声明变量。从Java 19开始，它现在也适用于记录：

```java
if (o instanceof Location location) {
    System.out.println(loocation.name());
}
```

我们还可以使用模式匹配将模式变量位置的值提取到变量中。我们可以省略模式变量位置，因为调用访问器方法name()变得不必要了：

```java
if (o instanceof Location (String name, GPSPoint gpsPoint)) {
    System.out.println(name);
}
```

如果我们有一个对象o我们想要匹配记录模式。该模式只有在它是相应记录类型的实例时才会匹配。如果模式匹配，它会初始化变量并将它们转换为相应的类型。请记住：空值不匹配任何记录模式。我们可以用var替换变量的类型，在那种特定情况下，编译器将为我们推断类型：

```java
if (o instanceof Location (var name, var gpsPoint)) { 
    System.out.println(name); 
}
```

我们甚至可以更进一步并销毁GPSPoint：

```java
if (o instanceof Location (var name, GPSPoint(var latitude, var longitude))) {
    System.out.println("lat: " + latitude + ", lng: " + longitude);
}
```

这称为嵌套销毁。它帮助我们进行数据导航，允许我们直接访问经纬度，而无需使用Location的getter获取GPSPoint，然后使用GPSPoint对象上的getter获取经纬度值。

我们也可以将其用于泛型记录。让我们介绍一个名为Wrapper的新泛型记录：

```java
public record Wrapper<T>(T t, String description) { }
```

该记录包装了任何类型的对象，并允许我们向其添加描述。我们仍然可以像以前一样使用instanceof甚至销毁记录：

```java
Wrapper<Location> wrapper = new Wrapper<>(new Location("Home", new GPSPoint(1.0, 2.0)), "Description");
if (wrapper instanceof Wrapper<Location>(var location, var description)) {
    System.out.println(description);
}
```

编译器还可以推断变量location的类型。

### 3.2 switch表达式

我们还可以使用[switch表达式](https://www.baeldung.com/java-switch)根据我们的对象类型执行特定操作：

```java
String result = switch (o) {
    case Location l -> l.name();
    default -> "default";
};
```

它将寻找第一个匹配的case。同样，我们也可以使用嵌套销毁：

```java
Double result = switch (object) {
    case Location(var name, GPSPoint(var latitude, var longitude)) -> latitude;
    default -> 0.0;
};
```

我们必须记住，我们总是在没有匹配的情况下放入默认情况。

如果我们想避免默认情况，我们还可以使用密封接口并允许必须实现该接口的对象：

```java
public sealed interface ILocation permits Location {
    default String getName() {
        return switch (this) {
            case Location(var name, var ignored) -> name;
        };
    }
}
```

这有助于我们消除默认情况并仅创建相应的情况。

也可以保护特定的情况。例如，我们将使用when关键字来检查是否相等并在出现某些不需要的行为时引入一个新的Location：

```java
String result = switch (object) {
    case Location(var name, var ignored) when name.equals("Home") -> new Location("Test", new GPSPoint(1.0, 2.0)).getName();
    case Location(var name, var ignored) -> name;
    default -> "default";
};
```

如果此switch表达式被以下对象调用：

```java
Object object = new Location("Home", new GPSPoint(1.0, 2.0));
```

它将变量结果分配给“Test”，我们的对象是Location记录类型，它的名字是“Home”。因此它直接跳转到switch的第一个case。如果名称不是“Home”，它会跳转到第二个case。如果对象根本不是Location类型，则将返回默认值。

## 4. 总结

在本文中，我们了解了记录模式如何允许我们使用模式匹配将记录的值提取到变量中。我们可以使用instanceof、switch语句，甚至带有附加条件的守卫来做到这一点。在处理嵌套记录或密封记录的层次结构时，记录模式特别有用。