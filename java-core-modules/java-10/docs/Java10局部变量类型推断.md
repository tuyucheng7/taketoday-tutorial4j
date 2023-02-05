## 1. 概述

**JDK 10中最明显的增强功能之一是使用初始化器对局部变量进行类型推断**。

本教程通过示例提供了此功能的详细信息。

## 2. 简介

在Java 9之前，我们必须明确提及局部变量的类型，并确保它与用于初始化它的初始化值兼容：

```java
String message = "Good bye,Java9";
```

在Java 10中，我们可以这样声明一个局部变量：

```java
@Test
void whenVarInitWithString_thenGetStringTypeVar() {
    var message = "Hello,Java10";
    assertTrue(message instanceof String);
}
```

**我们不提供message的数据类型。相反，我们将message标记为var，编译器根据右侧的初始值设定项类型推断message的类型**。

在上面的例子中，message的类型将是String。

**请注意，此功能仅适用于具有初始化值的局部变量**。它不能用于成员变量、方法参数、返回类型等-初始值设定项是必需的，因为没有它编译器将无法推断类型。

这种增强有助于减少样板代码；例如：

```java
Map<Integer, String> map = new HashMap<>();
```

现在可以将其重写为：

```java
var idToNameMap = new HashMap<Integer, String>();
```

这也有助于我们将重点放在变量名上，而不是变量类型。

另一点需要注意的是**var不是关键字**，这确保了使用var作为函数或变量名的程序的向后兼容性。var是保留的类型名称，就像int一样。

最后，请注意，**使用var没有运行时开销，也不会使Java成为动态类型语言**。变量的类型仍然在编译时推断，以后无法更改。

## 3. 非法使用var

如前所述，如果没有初始值设定项，var将无法工作：

```java
var n; // error: cannot use 'var' on variable without initializer
```

如果使用null初始化，它也不会起作用：

```java
var emptyList = null; // error: variable initializer is 'null'
```

它不适用于非局部变量：

```java
public var = "hello"; // error: 'var' is not allowed here
```

Lambda表达式需要明确的目标类型，因此不能使用var：

```java
var p = (String s) -> s.length() > 10; // error: lambda expression needs an explicit target-type
```

数组初始值设定项也是如此：

```java
var arr = { 1, 2, 3 }; // error: array initializer needs an explicit target-type
```

## 4. var使用指南

在某些情况下var可以合法使用，但这样做可能不是一个好主意。

例如，在代码可能变得不那么可读的情况下：

```java
var result = obj.prcoess();
```

在这里，虽然var的使用是合法的，但是很难理解process()返回的类型，这使得代码的可读性降低。

[java.net](https://openjdk.org/)有一篇关于[Java中局部变量类型推断的样式指南](https://openjdk.org/projects/amber/guides/lvti-style-guide)的专门文章，其中讨论了我们在使用此功能时应该如何使用判断。

最好避免使用var的另一种情况是在具有长管道的流中：

```java
var x = emp.getProjects.stream()
    .findFirst()
    .map(String::length)
    .orElse(0);
```

使用var也可能会产生意想不到的结果。

例如，如果我们将它与Java 7中引入的钻石运算符一起使用：

```java
var empList = new ArrayList<>();
```

empList的类型将是ArrayList<Object\>而不是List<Object\>。如果我们希望它是ArrayList<Employee\>，我们必须明确指定：

```java
var empList = new ArrayList<Employee>();
```

**将var与不可表示的类型一起使用可能会导致意外错误**。

例如，如果我们将var与匿名类实例一起使用：

```java
@Test
void whenVarInitWithAnonymous_thenGetAnonymousType() {
    var obj = new Object() {};
    assertFalse(obj.getClass().equals(Object.class));
}
```

现在，如果我们尝试将另一个Object分配给obj，我们会得到一个编译错误：

```java
obj = new Object(); // error: Object cannot be converted to <anonymous Object>
```

这是因为obj的推断类型不是Object。

## 5. 总结

在本文中，我们通过几个实例介绍了新的Java 10局部变量类型推断功能。