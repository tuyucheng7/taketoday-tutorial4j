## 1. 概述

在本教程中，我们将重点介绍和描述[Spring Assert](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/Assert.html)类的用途，并演示如何使用它。

## 2. Assert类的目的

Spring Assert类帮助我们验证参数，**通过使用Assert类的方法，我们可以编写我们期望为true的假设**。如果不满足，则会抛出运行时异常。

每个Assert的方法都可以与Java [assert](https://docs.oracle.com/javase/8/docs/technotes/guides/language/assert.html)语句进行比较。Java assert语句在其条件失败时会在运行时抛出一个错误。有趣的是，这些断言可以被禁用。

以下是Spring Assert方法的一些特征：

-   Assert的方法是静态的
-   他们抛出IllegalArgumentException或IllegalStateException
-   第一个参数通常是用于验证的参数或要检查的逻辑条件
-   最后一个参数通常是验证失败时显示的异常消息
-   消息可以作为String参数或Supplier<String\>参数传递

另请注意，尽管名称相似，但Spring断言与[JUnit](https://junit.org/)和其他测试框架的断言没有任何共同之处。Spring断言不是用于测试，而是用于调试。

## 3. 使用示例

让我们用公共方法drive()定义一个Car类：

```java
public class Car {
	private String state = "stop";

	public void drive(int speed) {
		Assert.isTrue(speed > -1, "speed must be positive");
		this.state = "drive";
		// ...
	}
}
```

我们可以看到speed必须是正数，上面一行是检查条件并在条件失败时抛出异常的简短方法：

```java
if (!(speed > 0)) {
    throw new IllegalArgumentException("speed must be positive");
}
```

每个Assert的公共方法大致包含以下代码-一个带有运行时异常的条件块，应用程序预计不会从中恢复。

如果我们尝试使用负参数调用drive()方法，则会抛出IllegalArgumentException异常：

```shell
Exception in thread "main" java.lang.IllegalArgumentException: speed must be positive
```

## 4. 逻辑断言

### 4.1 isTrue()

上面讨论了这个断言，它接收布尔条件并在条件为false时抛出IllegalArgumentException。

### 4.2 state()

**state()方法与isTrue()具有相同的签名，但抛出IllegalStateException**。

顾名思义，当由于对象的非法状态而不能继续该方法时，应该使用它。

想象一下，如果汽车正在运行，我们不能调用fuel()方法。让我们在这种情况下使用state()断言：

```java
public void fuel() {
    Assert.state(this.state.equals("stop"), "car must be stopped");
    // ...
}
```

当然，我们可以使用逻辑断言来验证一切。但是为了更好的可读性，我们可以使用额外的断言来使我们的代码更具表现力。

## 5. 对象和类型断言

### 5.1 notNull()

我们可以使用notNull()方法假设一个对象不为空：

```java
public void сhangeOil(String oil) {
    Assert.notNull(oil, "oil mustn't be null");
    // ...
}
```

### 5.2 isNull()

另一方面，我们可以使用isNull()方法检查对象是否为空：

```java
public void replaceBattery(CarBattery carBattery) {
    Assert.isNull(carBattery.getCharge(), "to replace battery the charge must be null");
    // ...
}
```

### 5.3 isInstanceOf()

要检查一个对象是否是特定类型的另一个对象的实例，我们可以使用isInstanceOf()方法：

```java
public void сhangeEngine(Engine engine) {
    Assert.isInstanceOf(ToyotaEngine.class, engine);
    // ...
}
```

在我们的示例中，检查成功通过，因为ToyotaEngine是Engine的子类。

### 5.4 isAssignable()

要检查类型，我们可以使用Assert.isAssignable()：

```java
public void repairEngine(Engine engine) {
    Assert.isAssignable(Engine.class, ToyotaEngine.class);
    // ...
}
```

最近的两个断言表示一种is-a关系。

## 6. 文本断言

文本断言用于对字符串参数执行检查。

### 6.1 hasLength()

我们可以使用hasLength()方法检查一个String是否不为空，这意味着它至少包含一个空格：

```java
public void startWithHasLength(String key) {
    Assert.hasLength(key, "key must not be null and must not the empty");
    // ...
}
```

### 6.2 hasText()

我们可以使用hasText()方法加强条件并检查字符串是否包含至少一个非空白字符：

```java
public void startWithHasText(String key) {
    Assert.hasText(key, "key must not be null and must contain at least one non-whitespace  character");
    // ...
}
```

### 6.3 doesNotContain()

我们可以使用doesNotContain()方法来确定 字符串参数是否不包含特定的子字符串：

```java
public void startWithNotContain(String key) {
    Assert.doesNotContain(key, "123", "key mustn't contain 123");
    // ...
}
```

## 7. 集合和Map断言

### 7.1 notEmpty()用于集合

顾名思义，notEmpty()方法断言集合不为空，这意味着它不为空并且至少包含一个元素：

```java
public void repair(Collection<String> repairParts) {
    Assert.notEmpty(repairParts, "collection of repairParts mustn't be empty");
    // ...
}
```

### 7.2 notEmpty()用于Map

Map重载了相同的方法，我们可以检查Map是否不为空并且至少包含一个条目：

```java
public void repair(Map<String, String> repairParts) {
    Assert.notEmpty(repairParts, "map of repairParts mustn't be empty");
    // ...
}
```

## 8. 数组断言

### 8.1 数组的notEmpty()

最后，我们可以使用notEmpty()方法检查数组是否不为空并且至少包含一个元素：

```java
public void repair(String[] repairParts) {
    Assert.notEmpty(repairParts, "array of repairParts mustn't be empty");
    // ...
}
```

### 8.2 noNullElements()

我们可以使用noNullElements()方法验证数组不包含空元素：

```java
public void repairWithNoNull(String[] repairParts) {
    Assert.noNullElements(repairParts, "array of repairParts mustn't contain null elements");
    // ...
}
```

请注意，如果数组为空，只要其中没有空元素，此检查仍会通过。

## 9. 总结

在本文中，我们探讨了Assert类。这个类在Spring框架中被广泛使用，但我们可以轻松地利用它编写更健壮和更具表现力的代码。