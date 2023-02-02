## 1. 概述

Java SE 17发行版引入了switch表达式和语句的模式匹配([JEP 406](https://openjdk.java.net/jeps/406))作为预览功能。**在为switch case定义条件时，模式匹配为我们提供了更大的灵活性**。

除了现在可以包含模式的case标签之外，选择器表达式不再局限于少数类型。在模式匹配出现之前，switch case仅支持对需要精确匹配常量值的选择器表达式进行简单测试。

在本教程中，我们将介绍可以在switch语句中应用的三种不同模式类型。我们还将探讨一些switch细节，例如涵盖所有值、排序子类和处理空值。

## 2. Switch语句

我们在Java中使用[switch](https://www.baeldung.com/java-switch)将控制转移到几个预定义的case语句之一，选择哪个语句取决于switch选择器表达式的值。

**在Java的早期版本中，选择器表达式必须是数字、字符串或常量**。此外，case标签只能包含常量：

```java
final String b = "B";
switch (args[0]) {
	case "A" -> System.out.println("Parameter is A");
	case b -> System.out.println("Parameter is b");
	default -> System.out.println("Parameter is unknown");
}
```

在我们的例子中，如果变量b不使用final修饰，编译器会抛出一个需要常量表达式的错误。

## 3. 模式匹配

通常，模式匹配首先作为Java SE 14中的预览功能引入。

它仅限于一种形式的模式，即类型模式。典型的模式由类型名称和要将结果绑定到的变量组成。

将类型模式应用于[instanceof](https://www.baeldung.com/java-instanceof#:~:text=instanceof%20is%20a%20binary%20operator,check%20should%20always%20be%20used.)运算符可以简化类型检查和强制转换；此外，它使我们能够将两者组合成一个表达式：

```java
if (o instanceof String s) {
	System.out.printf("Object is a string %s", s);
} else if (o instanceof Number n) {
	System.out.printf("Object is a number %n", n);
}
```

这种内置的语言增强功能可帮助我们编写更少的代码并提高可读性。

## 4. Switch模式

[instanceof的模式匹配](https://www.baeldung.com/java-pattern-matching-instanceof)成为Java 16中的一个永久特性。

**在Java 17种，模式匹配的应用现在也扩展到了switch表达式**。

但是，它仍然是一个[预览功能](https://www.baeldung.com/java-preview-features)，因此我们需要启用预览才能使用它：

```shell
java --enable-preview --source 17 PatternMatching.java
```

### 4.1 类型模式

让我们看看如何在switch语句中应用类型模式和instanceof运算符。

例如，我们将创建一个使用if-else语句将不同类型的值转换为double值的方法，如果不支持该类型，我们的方法简单地返回0：

```java
static double getDoubleUsingIf(Object o) {
	double result;
	if (o instanceof Integer) {
		result = ((Integer) o).doubleValue();
	} else if (o instanceof Float) {
		result = ((Float) o).doubleValue();
	} else if (o instanceof String) {
		result = Double.parseDouble(((String) o));
	} else {
		result = 0d;
	}
	return result;
}
```

我们可以在switch中使用类型模式用更少的代码来解决同样的问题：

```java
static Double getDoubleUsingSwitch(Object o) {
	return switch (o) {
		case Integer i -> i.doubleValue();
		case Float f -> f.doubleValue();
		case String s -> Double.parseDouble(s);
		default -> 0d;
	};
}
```

在早期的Java版本中，选择器表达式仅限于少数几种类型。但是，对于类型模式，switch选择器表达式可以是任何类型。

### 4.2 保护模式

类型模式帮助我们根据特定类型转移控制权。然而，有时我们还需要**对传递的值进行额外的检查**。

例如，我们可以使用if语句来检查字符串的长度：

```java
static double getDoubleValueUsingIf(Object o) {
	return switch (o) {
		case String s -> {
			if (s.length() > 0) {
				yield Double.parseDouble(s);
			} else {
				yield 0d;
			}
		}
		default -> 0d;
	};
}
```

我们可以使用保护模式来解决同样的问题，它们使用模式和布尔表达式的组合：

```java
static double getDoubleValueUsingGuardedPatterns(Object o) {
	return switch (o) {
		case String s && s.length() > 0 -> Double.parseDouble(s);
		default -> 0d;
	};
}
```

**保护模式使我们能够避免编写switch语句中的额外if条件；相反，我们可以将条件逻辑移到case标签**。

### 4.3 括号模式

除了在cases标签中具有条件逻辑外，**括号模式还使我们能够对它们进行分组**。

在执行其他检查时，我们可以简单地在布尔表达式中使用括号：

```java
static double getDoubleValueUsingParenthesizedPatterns(Object o) {
	return switch (o) {
		case String s && s.length() > 0 && !(s.contains("#") || s.contains("@")) -> Double.parseDouble(s);
		default -> 0d;
	};
}
```

通过使用括号，我们可以避免使用额外的if-else语句。

## 5. Switch细节

现在让我们看一下在switch中使用模式匹配时要考虑的几种特定情况。

### 5.1 涵盖所有值

当在switch中使用模式匹配时，**Java编译器会检查类型覆盖率**。

让我们考虑一个接收任何对象但仅涵盖String情况的示例switch语句：

```java
static double getDoubleUsingSwitch(Object o) {
    return switch (o) {
        case String s -> Double.parseDouble(s);
    };
}
```

上面的示例将导致以下编译错误：

```shell
[ERROR] Failed to execute goal ... on project java17: Compilation failure
[ERROR] /D:/Projects/.../HandlingNullValuesUnitTest.java:[10,16] the switch expression does not cover all possible input values
```

这是因为switch **case标签需要包含选择器表达式的类型**。

也可以应用default case标签而不是特定的选择器类型。

### 5.2 排序子类

在switch中使用具有模式匹配的子类时，**case的顺序很重要**。

让我们考虑一个例子，其中String case在CharSequence case之后。

```java
static double getDoubleUsingSwitch(Object o) {
	return switch (o) {
		case CharSequence c -> Double.parseDouble(c.toString());
		case String s -> Double.parseDouble(s);
		default -> 0d;
	};
}
```

由于String是CharSequence的子类，我们的示例将导致以下编译错误：

```shell
[ERROR] Failed to execute goal ... on project java17: Compilation failure
[ERROR] /D:/Projects/.../HandlingNullValuesUnitTest.java:[12,18] this case label is dominated by a preceding case label
```

这个错误背后的原因是，由于传递给方法的任何字符串对象都将在第一个case下处理，因此**执行不可能转到第二个case**。

### 5.3 处理空值

在早期版本的Java中，每次将null值传递给switch语句都会导致NullPointerException。

但是，对于类型模式，现在**可以将空检查作为单独的case标签应用**：

```java
static double getDoubleUsingSwitch(Object o) {
	return switch (o) {
		case String s -> Double.parseDouble(s);
		case null -> 0d;
		default -> 0d;
	};
}
```

如果没有特定于null的case标签，**则总类型的模式标签将匹配null值**：

```java
static double getDoubleUsingSwitchTotalType(Object o) {
	return switch (o) {
		case String s -> Double.parseDouble(s);
		case Object ob -> 0d;
	};
}
```

我们应该注意，switch表达式不能同时具有null case和总类型case。

这样的switch语句将导致以下编译错误：

```shell
[ERROR] Failed to execute goal ... on project java17: Compilation failure
[ERROR] /D:/Projects/.../HandlingNullValuesUnitTest.java:[14,13] switch has both a total pattern and a default label
```

最后，使用模式匹配的switch语句仍然可以抛出NullPointerException。

但是，只有当switch块没有null匹配的case标签时，它才能这样做。

## 6. 总结

在本文中，我们探讨了switch表达式和语句的模式匹配，这是Java 17中的一个预览功能。我们看到，通过在case标签中使用模式，选择是通过模式匹配决定的，而不是简单的相等性检查。

在案例中，我们介绍了可以在switch语句中应用的三种不同的模式类型。最后，我们探讨了几个具体案例，包括覆盖所有值、对子类排序和处理空值。