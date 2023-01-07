## 1. 概述

大多数面向对象的编程语言(包括 Java)都使用静态方法。静态方法与实例方法的区别在于它们没有拥有它们的对象。相反，静态方法是在类级别定义的，无需创建实例即可使用。

在本教程中，我们将了解Java中静态方法的定义及其局限性。然后我们将查看使用静态方法的常见用例，并推荐何时在我们的代码中应用它们是有意义的。

最后，我们将看到如何测试静态方法以及如何模拟它们。

## 2. 静态方法

实例方法根据对象的运行时类型进行多态解析。另一方面，静态方法是在编译时根据定义它们的类来解析的。

### 2.1. 班级

Java 中的静态方法是类定义的一部分。我们可以通过在方法中添加[static](https://www.baeldung.com/java-static) 关键字来定义静态方法：

```java
private static int counter = 0;

public static int incrementCounter() {
    return ++counter;
}

public static int getCounterValue() {
    return counter;
}
```

要访问静态方法，我们使用类名后跟一个点和方法名：

```java
int oldValue = StaticCounter.getCounterValue();
int newValue = StaticCounter.incrementCounter();
assertThat(newValue).isEqualTo(oldValue + 1);
```

我们应该注意到，这个静态方法可以访问 StaticCounter类的静态状态。静态方法通常是无状态的，但它们可以作为各种技术的一部分处理类级数据，包括[单例模式](https://www.baeldung.com/java-singleton)。

尽管也可以使用对象引用静态方法，但这种反模式通常会被诸如[Sonar](https://rules.sonarsource.com/java/RSPEC-2209)之类的工具标记为错误。

### 2.2. 限制

由于静态方法不对实例成员进行操作，因此我们应该注意一些限制：

-   静态方法不能直接引用实例成员变量
-   静态方法不能直接调用实例方法
-   子类不能覆盖静态方法
-   我们不能在静态方法中使用关键字this和super

以上每一个都会导致编译时错误。我们还应该注意，如果我们在子类中声明一个同名的静态方法，它不会覆盖而是[隐藏基类方法](https://www.baeldung.com/java-variable-method-hiding#:~:text=Method hiding may happen in,a good place to start.)。

## 3.用例

现在让我们看一下在我们的Java代码中应用静态方法有意义的常见用例。

### 3.1. 标准行为

当我们开发具有对输入参数进行操作的标准行为的方法时，使用静态方法是有意义的。

Apache StringUtils 中的字符串操作就是一个很好的例子：

```java
String str = StringUtils.capitalize("baeldung");
assertThat(str).isEqualTo("Baeldung");

```

另一个很好的例子是Collections类，因为它包含对不同集合进行操作的通用方法：

```java
List<String> list = Arrays.asList("1", "2", "3");
Collections.reverse(list);
assertThat(list).containsExactly("3", "2", "1");
```

### 3.2. 跨实例重用

使用静态方法的一个正当理由是当我们在不同类的实例之间重用标准行为时。

例如，我们通常在领域和业务类中使用JavaCollections和 Apache StringUtils ：

[![utils_demo_class_diagram](https://www.baeldung.com/wp-content/uploads/2022/07/utils_demo2.png)](https://www.baeldung.com/wp-content/uploads/2022/07/utils_demo2.png)

由于这些功能没有自己的状态，也没有绑定到我们业务逻辑的特定部分，因此将它们保存在可以共享的模块中是有意义的。

### 3.3. 不改变状态

由于静态方法不能引用实例成员变量，因此对于不需要任何对象状态操作的方法来说，它们是一个不错的选择。

当我们对状态不受管理的操作使用静态方法时，方法调用更为实用。调用者可以直接调用该方法而无需创建实例。

当我们通过类的所有实例共享状态时，就像在静态计数器的情况下，那么操作该状态的方法应该是静态的。管理全局状态可能是错误的来源，因此当实例方法直接写入静态字段时， Sonar 将报告一个[严重问题。](https://rules.sonarsource.com/java/RSPEC-2696)

### 3.4. 纯函数

如果函数的返回值仅取决于传递的输入参数，则该函数称为纯函数。纯函数从它们的参数中获取所有数据，并从该数据中计算出一些东西。

纯函数不对任何实例或静态变量进行操作。因此，执行纯函数也应该没有副作用。

由于静态方法不允许覆盖和引用实例变量，因此它们是在Java中实现纯函数的绝佳选择。

## 4.实用类

由于Java没有为容纳一组函数而预留的特定类型，因此我们通常会创建一个实用程序类。实用程序类为纯静态函数提供了一个家。我们可以将我们在整个项目中重用的纯函数组合在一起，而不是一遍又一遍地编写相同的逻辑。

Java 中的实用程序类是一个无状态类，我们永远不应实例化。因此，建议将其声明为[final](https://www.baeldung.com/java-final)，这样它就不能被子类化(这不会增加价值)。另外，为了防止任何人试图实例化它，我们可以添加一个[私有构造函数](https://www.baeldung.com/java-sonar-hide-implicit-constructor)：

```java
public final class CustomStringUtils {

    private CustomStringUtils() {
    }

    public static boolean isEmpty(CharSequence cs) { 
        return cs == null || cs.length() == 0; 
    }
}
```

我们应该注意，我们放在实用程序类中的所有方法都应该是静态的。

## 5. 测试

让我们检查一下我们如何在Java中进行单元测试和模拟静态方法。

### 5.1. 单元测试

[使用JUnit](https://www.baeldung.com/junit-5)对设计良好的纯静态方法进行单元测试非常简单。我们可以使用类名来调用我们的静态方法并将一些测试参数传递给它。

我们的被测单元将根据其输入参数计算结果。因此，我们可以对结果进行断言并测试不同的输入输出组合：

```java
@Test
void givenNonEmptyString_whenIsEmptyMethodIsCalled_thenFalseIsReturned() {
    boolean empty = CustomStringUtils.isEmpty("baeldung");
    assertThat(empty).isFalse();
}
```

### 5.2. 嘲笑

大多数时候，我们不需要模拟静态方法，我们可以简单地在我们的测试中使用真正的函数实现。模拟静态方法的需要通常暗示代码设计问题。

如果必须，那么我们可以使用[Mockito](https://www.baeldung.com/mockito-mock-static-methods)模拟静态函数。但是，我们需要向我们的 pom.xml添加一个额外的[mockito-inline依赖项：](https://search.maven.org/classic/#search|ga|1|g%3Aorg.mockito a%3Amockito-inline)

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-inline</artifactId>
    <version>3.8.0</version>
    <scope>test</scope>
</dependency>
```

现在，我们可以使用 Mockito.mockStatic 方法模拟对静态方法调用的调用：

```java
try (MockedStatic<StringUtils> utilities = Mockito.mockStatic(StringUtils.class)) {
    utilities.when(() -> StringUtils.capitalize("karoq")).thenReturn("Karoq");

    Car car1 = new Car(1, "karoq");
    assertThat(car1.getModelCapitalized()).isEqualTo("Karoq");
}
```

## 六，总结

在本文中，我们 探讨了在Java代码中使用静态方法的常见用例。我们了解了Java中静态方法的定义及其局限性。

此外，我们探索了何时在我们的代码中使用静态方法是有意义的。我们看到静态方法对于具有标准行为的纯函数来说是一个不错的选择，这些纯函数可以跨实例重用但不会改变它们的状态。最后，我们研究了如何测试和模拟静态方法。