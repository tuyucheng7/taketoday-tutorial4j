## 1. 概述

Java 21 SE 的发布引入了一项令人兴奋的预览功能：未命名模式和变量 ( [JEP 443](https://openjdk.org/jeps/443) )。**当副作用是我们唯一关心的问题时，这个新增功能使我们能够减少样板代码**。

[未命名模式是对Java 19 中的记录模式](https://www.baeldung.com/java-pattern-matching-instanceof)和 [Switch 模式匹配的](https://www.baeldung.com/java-switch-pattern-matching)改进。我们还应该熟悉Java 14 中作为预览引入的[记录功能。](https://www.baeldung.com/java-record-keyword)

在本教程中，我们将深入研究如何使用这些新功能来提高代码质量和可读性。

## 2. 目的

通常，在处理复杂对象时，我们不需要它们始终保存的所有数据。理想情况下，我们只从对象接收我们需要的东西，但这种情况很少发生。大多数时候，我们最终都会使用我们所得到的一小部分。

这样的例子在 OOP 中随处可见，[单一职责原则](https://www.baeldung.com/java-single-responsibility-principle)证明了这一点。未命名的模式和变量功能是 Java 在较小规模上解决这个问题的最新尝试。

**由于这是预览功能，我们必须确保启用它**。在 Maven 中，这是通过修改编译器插件配置以包含以下编译器参数来完成的：

```xml
<compilerArgs>
    <arg>--enable-preview</arg>
</compilerArgs>复制
```

## 3. 未命名变量

虽然对于 Java 来说是个新功能，但此功能在 Python 和 Go 等其他语言中很受欢迎。由于 Go 并不完全是面向对象的，因此 Java 在 OOP 领域率先引入了这一特性。

**当我们只关心操作的副作用时，使用未命名变量。可以根据需要多次定义它们，但以后不能引用它们。**

### 3.1. 增强型 For 循环

首先，假设我们有一个简单的*汽车*记录：

```java
public record Car(String name) {}复制
```

*然后，我们需要迭代汽车*集合来计算所有汽车的数量并执行一些其他业务逻辑：

```java
for (var car : cars) {
    total++;
    if (total > limit) {
        // side effect
    }
}复制
```

虽然我们需要检查汽车集合中的每个元素，但我们不需要使用它。命名变量会使代码更难阅读，所以让我们尝试一下新功能：

```java
for (var _ : cars) {
    total++;
    if (total > limit) {
        // side effect
    }
}复制
```

这让维护人员清楚地知道这辆车没有被使用过。当然，这也可以与基本的 for 循环一起使用：

```java
for (int i = 0, _ = sendOneTimeNotification(); i < cars.size(); i++) {
    // Notify car
}复制
```

**但请注意，sendOneTimeNotification \*()\*仅被调用一次。该方法还必须返回与第一次初始化相同的类型（在我们的例子中为\*i\*）。**

### 3.2. 赋值语句

我们还可以将未命名变量与赋值语句一起使用。**当我们既需要函数的副作用又需要一些返回值（但不是全部）时，这是最有用的**。

假设我们需要一个方法来删除队列中的前三个元素并返回第一个：

```java
static Car removeThreeCarsAndReturnFirstRemoved(Queue<Car> cars) {
    var car = cars.poll();
    var _ = cars.poll();
    var _ = cars.poll();
    return car;
}复制
```

正如我们在上面的示例中看到的，我们可以在同一个块中使用多个赋值。*我们也可以忽略poll()*调用的结果，但这样，它的可读性更强。

### 3.3. Try-Catch 块

未命名变量最有用的功能可能以未命名 catch 块的形式出现。**很多时候，我们想要处理异常，但实际上不需要知道异常包含什么**。

有了未命名的变量，我们就不用再担心了：

```java
try {
    someOperationThatFails(car);
} catch (IllegalStateException _) {
    System.out.println("Got an illegal state exception for: " + car.name());
} catch (RuntimeException _) {
    System.out.println("Got a runtime exception!");
}复制
```

它们还适用于同一捕获中的多种异常类型：

```java
catch (IllegalStateException | NumberFormatException _) { }复制
```

### 3.4. 尝试资源

虽然遇到的情况比*try-catch*少，但*[try-with](https://www.baeldung.com/java-try-with-resources)* 语法也从中受益。例如，在使用数据库时，我们通常不需要事务对象。

为了更好地了解这一点，我们首先创建一个模拟交易：

```java
class Transaction implements AutoCloseable {

    @Override
    public void close() {
        System.out.println("Closed!");
    }
}复制
```

让我们看看这是如何工作的：

```java
static void obtainTransactionAndUpdateCar(Car car) {
    try (var _ = new Transaction()) {
        updateCar(car);
    }
}复制
```

当然，还有多项作业：

```java
try (var _ = new Transaction(); var _ = new FileInputStream("/some/file"))复制
```

### 3.5. 拉姆达参数

**从本质上讲，Lambda 函数提供了一种重用代码的好方法**。很自然，通过提供这种灵活性，我们最终不得不解决我们不感兴趣的案例。

一个很好的例子是*Map接口中的*[*computeIfAbsent()*](https://www.baeldung.com/java-map-computeifabsent)方法。它检查映射中是否存在值或根据函数计算新值。

虽然很有用，但我们通常不需要 lambda 参数。它与传递给初始方法的密钥相同：

```java
static Map<String, List<Car>> getCarsByFirstLetter(List<Car> cars) {
    Map<String, List<Car>> carMap = new HashMap<>();
    cars.forEach(car ->
        carMap.computeIfAbsent(car.name().substring(0, 1), _ -> new ArrayList<>()).add(car)
    );
    return carMap;
}复制
```

这适用于多个 lambda 和多个 lambda 参数：

```java
map.forEach((_, _) -> System.out.println("Works!"));复制
```

## 4. 未命名模式

引入未命名模式作为[记录模式匹配](https://openjdk.org/jeps/440)的增强。**他们解决了一个非常明显的问题：我们通常不需要解构\*记录\***中的每个字段。

为了探讨这个主题，我们首先添加一个名为*Engine*的类：

```java
abstract class Engine { }复制
```

发动机可以是燃气发动机、电动发动机或混合动力发动机：

```java
class GasEngine extends Engine { }
class ElectricEngine extends Engine { }
class HybridEngine extends Engine { }复制
```

最后，让我们扩展*Car*以支持[参数化类型](https://www.baeldung.com/java-generics)，以便我们可以根据引擎类型重用它。我们还将添加一个名为*color*的新字段：

```java
public record Car<T extends Engine>(String name, String color, T engine) { }复制
```

### 4.1. *实例化*

**当使用模式解构记录时，未命名模式使我们能够忽略不需要的字段。**

假设我们得到一个物体，如果它是一辆汽车，我们想要获取它的颜色：

```java
static String getObjectsColor(Object object) {
    if (object instanceof Car(String name, String color, Engine engine)) {
        return color;
    }
    return "No color!";
}复制
```

虽然这有效，但很难阅读，而且我们正在定义不需要的变量。让我们看看未命名模式的外观如何：

```java
static String getObjectsColorWithUnnamedPattern(Object object) {
    if (object instanceof Car(_, String color, _)) {
        return color;
    }
    return "No color!";
}复制
```

现在很明显我们只需要汽车的*颜色*。

这也适用于简单的*instanceof*定义，但不太有用：

```java
if (car instanceof Car<?> _) { }复制
```

### 4.2. 切换模式

**使用 switch 模式解构还允许我们忽略字段：**

```java
static String getObjectsColorWithSwitchAndUnnamedPattern(Object object) {
    return switch (object) {
        case Car(_, String color, _) -> color;
        default -> "No color!";
    };
}复制
```

**除此之外，我们还可以处理参数化的情况**。例如，我们可以在不同的*switch情况下处理不同的**引擎*类型：

```java
return switch (car) {
    case Car(_, _, GasEngine _) -> "gas";
    case Car(_, _, ElectricEngine _) -> "electric";
    case Car(_, _, HybridEngine _) -> "hybrid";
    default -> "none";
};复制
```

我们还可以更轻松地将箱子与防护装置配对：

```java
return switch (car) {
    case Car(_, _, GasEngine _), Car(_, _, ElectricEngine _) when someVariable == someValue -> "not hybrid";
    case Car(_, _, HybridEngine _) -> "hybrid";
    default -> "none";
};复制
```

## 5。结论

未命名的模式和变量是解决单一职责原则的一个很好的补充。对于 Java 8 之前的版本来说，这是一个重大更改，但更高版本不受影响，因为不允许命名变量*_ 。*

**该功能通过减少样板代码和提高可读性，同时使一切看起来更简单，将它踢出了公园**。