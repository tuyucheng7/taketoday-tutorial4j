## 1. 概述

在本教程中，我们将讨论使用Java计算数学表达式的各种方法。在我们想要评估以字符串格式提供的数学表达式的项目中，此功能可能会派上用场。

首先，我们将讨论一些第三方库及其用法。接下来，我们将了解如何使用内置的JavaScripting API 来完成此任务。

## 2.exp4j

[exp4j](https://www.objecthunter.net/exp4j/)是一个开源库，可用于评估数学表达式和函数。该库实现了[Dijkstra 的调车场算法，这是一种用于解析中](https://en.wikipedia.org/wiki/Shunting-yard_algorithm)[缀表示法](https://en.wikipedia.org/wiki/Infix_notation)指定的数学表达式的方法 。

除了使用标准运算符和函数之外，exp4j 还允许我们创建自定义运算符和函数。

### 2.1. 添加 Maven 依赖

要使用 exp4j，我们需要在项目中添加[Maven 依赖](https://search.maven.org/search?q=g:net.objecthunter a:exp4j)项：

```xml
<dependency>
    <groupId>net.objecthunter</groupId>
    <artifactId>exp4j</artifactId>
    <version>0.4.8</version>
</dependency>
```

### 2.2. 评估简单表达式

我们可以评估以字符串格式提供的简单数学表达式：

```java
@Test
public void givenSimpleExpression_whenCallEvaluateMethod_thenSuccess() {
    Expression expression = new ExpressionBuilder("3+2").build();
    double result = expression.evaluate();
    Assertions.assertEquals(5, result);
}
```

在上面的代码片段中，我们首先创建了一个ExpressionBuilder的实例。然后我们将它分配给一个表达式引用，我们用它来评估我们的表达式。

### 2.3. 在表达式中使用变量

现在我们知道如何计算简单的表达式，让我们向表达式添加一些变量：

```java
@Test
public void givenTwoVariables_whenCallEvaluateMethod_thenSuccess() {
    Expression expression = new ExpressionBuilder("3x+2y")
      .variables("x", "y")
      .build()
      .setVariable("x", 2)
      .setVariable("y", 3);
 
    double result = expression.evaluate();
 
    Assertions.assertEquals(12, result);
}
```

在上面的示例中，我们使用variables方法引入了两个变量x和 y 。我们可以使用此方法在表达式中添加任意数量的变量。一旦我们声明了变量，我们就可以使用setVariable方法为它们赋值。

### 2.4. 评估包含数学函数的表达式

现在让我们看一个简短的例子，说明我们如何评估一些标准的数学函数：

```java
@Test
public void givenMathFunctions_whenCallEvaluateMethod_thenSuccess() {
    Expression expression = new ExpressionBuilder("sin(x)sin(x)+cos(x)cos(x)")
      .variables("x")
      .build()
      .setVariable("x", 0.5);
 
    double result = expression.evaluate();
 
    Assertions.assertEquals(1, result);
}
```

## 3.Javaluator

[Javaluator](http://javaluator.sourceforge.net/en/home/)是另一个免费提供的独立轻量级库。与 exp4j 一样，Javaluator 也用于计算中缀表达式。

### 3.1. 添加 Maven 依赖

我们可以使用以下[Maven 依赖](https://search.maven.org/search?q=g:com.fathzer a:javaluator)项在我们的项目中使用 Javaluator：

```xml
<dependency>
    <groupId>com.fathzer</groupId>
    <artifactId>javaluator</artifactId>
    <version>3.0.3</version>
</dependency>
```

### 3.2. 评估简单表达式

要使用 Javaluator 计算表达式，我们首先需要创建一个 DoubleEvaluator实例：

```java
@Test
public void givenExpression_whenCallEvaluateMethod_thenSuccess() {
    String expression = "3+2";
    DoubleEvaluator eval = new DoubleEvaluator();
 
    Double result = eval.evaluate(expression);
 
    Assertions.assertEquals(5, result);
}
```

### 3.3. 计算包含变量的表达式

为了评估包含变量的表达式，我们使用StaticVariableSet：

```java
@Test
public void givenVariables_whenCallEvaluateMethod_thenSuccess() {
    String expression = "3x+2y";
    DoubleEvaluator eval = new DoubleEvaluator();
    StaticVariableSet<Double> variables = new StaticVariableSet<Double>();
    variables.set("x", 2.0);
    variables.set("y", 3.0);
 
    Double result = eval.evaluate(expression, variables);
 
    Assertions.assertEquals(12, result);
}
```

然后我们使用StaticVariableSet#set方法为变量赋值。

### 3.4. 评估包含数学函数的表达式

我们还可以使用 Javaluator 库求解包含数学函数的表达式：

```java
@Test
public void givenMathFunction_whenCallEvaluateMethod_thenSuccess() {
    String expression = "sin(x)sin(x)+cos(x)cos(x)";
    DoubleEvaluator eval = new DoubleEvaluator();
    StaticVariableSet<Double> variables = new StaticVariableSet<Double>();
    variables.set("x", 0.5);
 
    Double result = eval.evaluate(expression, variables);
 
    Assertions.assertEquals(1, result);
}
```

## 4.Java脚本API

现在我们已经讨论了第三方库，现在让我们讨论如何使用内置 API 来实现这一点。Java 已经带有一个小而强大的脚本 API。该 API 的所有类和接口都在javax.script包中。

它包含允许我们评估 JavaScript的ScriptEngineManager和ScriptEngine接口。在Java8 之前，Java 自带了Rhino引擎。然而，从Java8 开始，Java 自带了更新更强大的[Nashorn](https://www.baeldung.com/java-nashorn)引擎。

### 4.1. 获取ScriptEngine实例

要创建ScriptEngine，我们首先必须创建 ScriptEngineManager的实例。获得实例后，我们需要调用ScriptEngineManager#getEngineByName方法来获取 ScriptEngine：

```java
ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
```

请注意， Nashorn是与 JDK 一起打包的默认 JavaScript 引擎。

### 4.2. 评估简单表达式

我们现在可以使用上面的 scriptEngine实例来调用ScriptEngine#eval方法：

```java
String expression = "3+2";
Integer result = (Integer) scriptEngine.eval(expression);
Assertions.assertEquals(5, result);
```

### 4.3. 计算包含变量的表达式

要评估包含变量的表达式，我们需要声明和初始化变量：

```java
String expression = "x=2; y=3; 3x+2y;";
Double result = (Double) scriptEngine.eval(expression);
Assertions.assertEquals(12, result);
```

由于我们使用的是 JavaScript 引擎，因此我们可以像在 JavaScript 中那样直接将变量添加到表达式中。

注意 – JavaScript 没有执行数学运算的直接方法，需要访问Math对象。因此，我们无法使用JavaScripting API 解决数学表达式。


## 5.总结

在本文中，我们看到了使用Java计算数学表达式的各种技术。