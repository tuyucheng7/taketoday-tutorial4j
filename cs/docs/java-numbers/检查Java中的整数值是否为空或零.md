## 一、概述

在本快速教程中，我们将学习几种不同的方法来检查给定*Integer*实例的值是否为空或零。

为简单起见，我们将使用单元测试断言来验证每种方法是否按预期工作。

那么，接下来，让我们看看他们的行动。

## 2.使用标准方式

**使用逻辑 OR 运算符**可能是执行检查的第一个想法。它只是检查给定的*整数*是否为空或零。

让我们创建一个方法来实现此检查以便于验证：

```java
public static boolean usingStandardWay(Integer num) {
    return num == null || num == 0;
}复制
```

这可能是执行检查的最直接方法。让我们创建一个测试，看看它是否适用于不同的*Integer*实例：

```java
int n0 = 0;
boolean result0 = usingStandardWay(n0);
assertTrue(result0);

boolean resultNull = usingStandardWay(null);
assertTrue(resultNull);

int n42 = 42;
boolean result42 = usingStandardWay(n42);
assertFalse(result42);复制
```

如我们所见，我们通过将三个*Integer对象传递给**usingStandardWay()*方法来测试它：零、*null*和 42。我们将使用这三个*Integer*对象作为本教程中进一步测试的输入。

## 3. 使用三元运算符

我们经常使用[三元运算符](https://www.baeldung.com/java-ternary-operator)来编写带条件的变量赋值，例如：

```java
variable = booleanExpression ? expression1 : expression2复制
```

接下来，让我们看看如何使用三元运算符来执行所需的*整数*检查：

```java
public static boolean usingTernaryOperator(Integer num) {
    return 0 == (num == null ? 0 : num);
}复制
```

如上面的代码所示，*(num == null ? 0 : num)*首先检查*num*变量是否为*null*。如果是这种情况，我们取零。否则，我们将采用原始的*num*值。

换句话说，**这个表达式在这里做了一个“空到零”的转换**。由于我们已经处理了*null*的情况，我们只需要检查三元运算符的表达式的结果是否为零。

接下来，让我们测试这是否适用于我们的三个*Integer*实例：

```java
int n0 = 0;
boolean result0 = usingTernaryOperator(n0);
assertTrue(result0);

boolean resultNull = usingTernaryOperator(null);
assertTrue(resultNull);

int n42 = 42;
boolean result42 = usingTernaryOperator(n42);
assertFalse(result42);复制
```

如果我们试一试，测试就会通过。因此，*usingTernaryOperator()*方法按预期工作。

## 4.使用*可选*类

Java 8 引入了*[Optional](https://www.baeldung.com/java-optional)**类型，因此如果我们的 Java 版本是 8 或更高版本，我们也可以使用Optional*类中的静态方法来实现此检查：

```java
public static boolean usingOptional(Integer num) {
    return Optional.ofNullable(num).orElse(0) == 0;
}复制
```

由于我们不知道给定的*Integer*变量是否为*空*，我们可以 使用*Optional.ofNullable(num)从它构建一个**Optional*实例。

接下来，我们调用*Optional*的[*orElse()*](https://www.baeldung.com/java-optional-or-else-optional)方法。**orElse \*(x)\* 方法检查\*Optional\*对象：如果存在值，则返回值，否则返回\*x\*。**因此，*orElse(0)*也执行“null 到 0”的转换。然后，剩下的就很简单了。我们只需要检查*orElse(0)*是否等于零。

接下来，让我们验证这种方法是否适用于我们的输入：

```java
int n0 = 0;
boolean result0 = usingOptional(n0);
assertTrue(result0);

boolean resultNull = usingOptional(null);
assertTrue(resultNull);

int n42 = 42;
boolean result42 = usingOptional(n42);
assertFalse(result42);复制
```

如果我们运行测试，它就会通过。因此，*usingOptional()*方法也可以完成这项工作。

## 5. 使用*ObjectUtils*类

[Apache Commons Lang 3](https://www.baeldung.com/java-commons-lang-3)是一个非常流行的库。如果这个库已经是我们正在处理的 Java 项目的依赖项，我们还可以使用它的*ObjectUtils*类执行*整数*检查：

```java
public static boolean usingObjectUtils(Integer num) {
    return ObjectUtils.defaultIfNull(num, 0) == 0;
}复制
```

如上面的实现所示，我们在检查中调用了*ObjectUtils.defaultIfNull(num, 0)*方法。defaultIfNull *()*方法是一种通用方法。如果我们看一下它是如何实现的，我们就会理解*usingObjectUtils()*的 实现：

```java
public static <T> T defaultIfNull(final T object, final T defaultValue) {
    return object != null ? object : defaultValue;
}复制
```

如我们所见，如果我们跳过泛型声明，***defaultIfNull()\*的实现就像使用三元运算符的表达式一样简单。**这对我们来说并不新鲜。因此，**它也进行“空到零”转换。** 

与往常一样，让我们测试一下我们的*usingObjectUtils()*方法：

```java
int n0 = 0;
boolean result0 = usingObjectUtils(n0);
assertTrue(result0);

boolean resultNull = usingObjectUtils(null);
assertTrue(resultNull);

int n42 = 42;
boolean result42 = usingObjectUtils(n42);
assertFalse(result42);复制
```

不出所料，测试也通过了。

## 六，结论

在本文中，我们探索了三种方法来检查给定的*Integer*对象是否为空或其值是否为零。