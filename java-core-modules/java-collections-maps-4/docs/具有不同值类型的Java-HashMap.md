## 1. 概述

[HashMap](https://www.baeldung.com/java-hashmap)存储键值映射。在本教程中，我们将讨论如何在HashMap中存储不同类型的值。

## 二、问题简介

自从引入[Java 泛型](https://www.baeldung.com/java-generics)以来，我们通常以泛型方式使用HashMap——例如：

```java
Map<String, Integer> numberByName = new HashMap<>();
```

在这种情况下，我们只能将String和Integer数据作为键值对放入映射numberByName中。这很好，因为它确保了类型安全。例如，如果我们试图将一个Float对象放入Map中，我们将得到“不兼容类型”的编译错误。

然而，有时，我们希望将不同类型的数据放入一个Map中。例如，我们希望 numberByName映射也将Float 和BigDecimal对象存储为值。

在讨论如何实现之前，让我们创建一个示例问题来简化演示和解释。假设我们有三个不同类型的对象：

```java
Integer intValue = 777;
int[] intArray = new int[]{2, 3, 5, 7, 11, 13};
Instant instant = Instant.now();

```

正如我们所看到的，这三种类型是完全不同的。所以首先，我们将尝试将这三个对象放入HashMap中。为简单起见，我们将使用字符串值作为键。

当然，在某些时候，我们需要从Map中读取数据并使用数据。因此，我们将遍历HashMap中的条目，并为每个条目打印带有一些描述的值。

那么，让我们看看如何实现这一目标。

## 3. 使用Map<String, Object>

我们知道在Java中，Object是所有类型的超类型。因此，如果我们将 Map 声明为Map <String, Object>，它应该接受任何类型的值。

接下来我们看看这种方式是否满足我们的要求。

### 3.1. 将数据放入地图

正如我们之前提到的，Map<String, Object>允许我们将任何类型的值放入其中：

```java
Map<String, Object> rawMap = new HashMap<>();
rawMap.put("E1 (Integer)", intValue);
rawMap.put("E2 (IntArray)", intArray);
rawMap.put("E3 (Instant)", instant);

```

这很简单。接下来，让我们访问Map中的条目并打印值和描述。

### 3.2. 使用数据

在我们将一个值放入Map<String, Object>之后，我们就失去了该值的具体类型。因此，我们需要在使用数据之前检查并将值转换为正确的类型。例如，我们可以使用[instanceof运算符](https://www.baeldung.com/java-instanceof)来验证值的类型：

```java
rawMap.forEach((k, v) -> {
    if (v instanceof Integer) {
        Integer theV = (Integer) v;
        System.out.println(k + " -> "
          + String.format("The value is a %s integer: %d", theV > 0 ? "positive" : "negative", theV));
    } else if (v instanceof int[]) {
        int[] theV = (int[]) v;
        System.out.println(k + " -> "
          + String.format("The value is an array of %d integers: %s", theV.length, Arrays.toString(theV)));
    } else if (v instanceof Instant) {
        Instant theV = (Instant) v;
        System.out.println(k + " -> "
          + String.format("The value is an instant: %s", FORMATTER.format(theV)));
    } else {
        throw new IllegalStateException("Unknown Type Found.");
    }
});
```

如果我们执行上面的代码，我们将看到输出：

```java
E1 (Integer) -> The value is a positive integer: 777
E2 (IntArray) -> The value is an array of 6 integers: [2, 3, 5, 7, 11, 13]
E3 (Instant) -> The value is an instant: 2021-11-23 21:48:02
```

这种方法按我们预期的那样工作。

但是，它有一些缺点。接下来，让我们仔细看看它们。

### 3.3. 缺点

首先，如果我们打算让map支持相对更多的不同类型，那么多个if-else语句会变成一个大代码块，导致代码难以阅读。

而且，如果我们要使用的类型包含继承关系，instanceof检查可能会失败。

例如，如果我们在 map 中放置一个java.lang.Integer intValue 和一个 [java.lang.Number](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Number.html) numberValue，我们无法使用instanceof运算符区分它们。这是因为(intValue instanceof Integer)和(intValue instanceof Number)都返回true。

因此，我们必须添加额外的检查来确定一个值的具体类型。但是，当然，这会使代码难以阅读。

最后，由于我们的地图接受任何类型的值，我们失去了类型安全。也就是说，当遇到非预期的类型时，我们要处理异常。

可能会出现一个问题：有没有办法接受不同类型的数据并保持类型安全？

所以接下来，我们将讨论另一种解决问题的方法。

## 4. 为所有需要的类型创建超类型

在本节中，我们将介绍一个超类型来保护类型安全。

### 4.1. 数据模型

首先，我们创建一个接口DynamicTypeValue：

```java
public interface DynamicTypeValue {
    String valueDescription();
}
```

该接口将是我们希望映射支持的所有类型的超类型。它还可以包含一些常用操作。例如，我们定义了一个方法valueDescription。

然后，我们为每个具体类型创建一个类来包装值并实现我们创建的接口。例如，我们可以为Integer类型创建一个IntegerTypeValue类：

```java
public class IntegerTypeValue implements DynamicTypeValue {
    private Integer value;

    public IntegerTypeValue(Integer value) {
        this.value = value;
    }

    @Override
    public String valueDescription() {
        if(value == null){
            return "The value is null.";
        }
        return String.format("The value is a %s integer: %d", value > 0 ? "positive" : "negative", value);
    }
}

```

同样，让我们为其他两种类型创建类：

```java
public class IntArrayTypeValue implements DynamicTypeValue {
    private int[] value;

    public IntArrayTypeValue(int[] value) { ... }

    @Override
    public String valueDescription() {
        // null handling omitted
        return String.format("The value is an array of %d integers: %s", value.length, Arrays.toString(value));
    }
}
public class InstantTypeValue implements DynamicTypeValue {
    private static DateTimeFormatter FORMATTER = ...

    private Instant value;

    public InstantTypeValue(Instant value) { ... }

    @Override
    public String valueDescription() {
        // null handling omitted
        return String.format("The value is an instant: %s", FORMATTER.format(value));
    }
}

```

如果我们需要支持更多的类型，我们只需要添加相应的类。

接下来，让我们看看如何使用上面的数据模型来存储和使用地图中不同类型的值。

### 4.2. 在地图中放置和使用数据

首先，让我们看看如何声明Map并将各种类型的数据放入其中：

```java
Map<String, DynamicTypeValue> theMap = new HashMap<>();
theMap.put("E1 (Integer)", new IntegerTypeValue(intValue));
theMap.put("E2 (IntArray)", new IntArrayTypeValue(intArray));
theMap.put("E3 (Instant)", new InstantTypeValue(instant));

```

如我们所见，我们已将地图声明为Map<String, DynamicTypeValue> 以保证类型安全：只允许将具有DynamicTypeValue类型的数据放入地图中。

当我们向地图添加数据时，我们实例化了我们创建的相应类。

当我们使用数据时，不需要类型检查和转换：

```java
theMap.forEach((k, v) -> System.out.println(k + " -> " + v.valueDescription()));

```

如果我们运行代码，它会打印：

```java
E1 (Integer) -> The value is a positive integer: 777
E2 (IntArray) -> The value is an array of 5 integers: [2, 3, 5, 7, 11]
E3 (Instant) -> The value is an instant: 2021-11-23 22:32:43
```

正如我们所看到的，这种方法的代码很干净并且更容易阅读。

此外，由于我们为每个需要支持的类型都创建了一个包装类，因此具有继承关系的类型不会导致任何问题。

多亏了类型安全，我们不需要去处理面对非预期类型数据的错误情况。

## 5.总结

在本文中，我们讨论了如何使JavaHashMap支持不同类型的值数据。

此外，我们还通过示例介绍了两种实现它的方法。