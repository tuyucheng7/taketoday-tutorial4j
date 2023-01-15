## 1. 简介

在本文中，我们介绍在Java 7中引入并在Java 9版本中得到增强的重要API，即[java.lang.invoke.MethodHandles](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/invoke/MethodHandles.html)。

特别是，我们将学习什么是方法句柄，如何创建它们以及如何使用它们。

## 2. 什么是方法句柄？

如API文档中所述，对其定义进行了说明：

>   方法句柄是对底层方法、构造函数、字段或类似低级操作的类型化、可直接执行的引用，具有可选的参数或返回值转换。

更简单地说，方法句柄是一种用于查找、调整和调用方法的低级机制。

方法句柄是不可变的并且没有可见状态。

要创建和使用MethodHandle，需要4个步骤：

-   创建Lookup
-   创建MethodType
-   查找方法句柄
-   调用方法句柄

### 2.1 方法句柄与反射

引入方法句柄是为了与现有的[java.lang.reflect](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/reflect/package-summary.html) API一起使用，因为它们有不同的用途和不同的特性。

从性能的角度来看，MethodHandles API可以比反射API快得多，因为访问检查是在创建时而不是在执行时进行的。如果存在安全管理器，则这种差异会被进一步放大，因为成员和类查找需要进行额外检查。

然而，考虑到性能并不是任务的唯一适用性衡量标准，我们还必须考虑MethodHandles API更难以使用，因为缺少成员类枚举、可访问性标志检查等机制。

即便如此，MethodHandles API提供了对方法进行柯里化、更改参数类型和更改顺序的可能性。

有了MethodHandles API的明确定义和目标，我们现在可以从查找开始着手处理它们。

## 3. 创建Lookup(查找)

当我们想要创建方法句柄时，首先要做的是检索查找，工厂对象负责为查找类可见的方法、构造函数和字段创建方法句柄。

通过MethodHandles API，可以创建具有不同访问模式的查找对象。

让我们创建提供对公共方法的访问的查找：

```java
MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
```

但是，如果我们还想访问私有和受保护的方法，我们可以使用lookup()方法：

```java
MethodHandles.Lookup lookup = MethodHandles.lookup();
```

## 4. 创建一个MethodType

为了能够创建MethodHandle，查找对象需要定义其类型，这是通过MethodType类实现的。

特别是，MethodType表示方法句柄接收和返回的参数和返回类型，或者方法句柄调用者传递和预期的参数和返回类型。

MethodType的结构很简单，它由返回类型和适当数量的参数类型组成，这些参数类型必须在方法句柄及其所有调用者之间正确匹配。与MethodHandle一样，即使是MethodType的实例也是不可变的。

让我们看看如何定义一个MethodType，将java.util.List类指定为返回类型，将Object数组指定为输入类型：

```java
MethodType mt = MethodType.methodType(List.class, Object[].class);
```

如果方法返回原始类型或void作为其返回类型，我们将使用表示这些类型的类(void.class、int.class ...)。

以下定义一个MethodType，它返回一个int值并接收一个Object：

```java
MethodType mt = MethodType.methodType(int.class, Object.class);
```

## 5. 查找MethodHandle

一旦我们定义了我们的方法类型，为了创建一个MethodHandle，我们必须通过lookup或publicLookup对象找到它，同时提供原始类和方法名称。

特别是，查找工厂提供了[一组方法](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html#lookups)，允许我们在考虑方法范围的情况下以适当的方式查找方法句柄。从最简单的场景开始，让我们探索主要的场景。

### 5.1 方法的方法句柄

使用findVirtual()方法允许我们为对象方法创建MethodHandle。让我们根据String类的concat()方法创建一个：

```java
MethodType mt = MethodType.methodType(String.class, String.class);
MethodHandle concatMH = publicLookup.findVirtual(String.class, "concat", mt);
```

### 5.2 静态方法的方法句柄

当我们想要访问静态方法时，我们可以使用findStatic()方法：

```java
MethodType mt = MethodType.methodType(List.class, Object[].class);

MethodHandle asListMH = publicLookup.findStatic(Arrays.class, "asList", mt);
```

在这种情况下，我们创建了一个方法句柄，将对象数组转换为对象列表。

### 5.3 构造函数的方法句柄

可以使用findConstructor()方法访问构造函数。让我们创建一个方法句柄，其行为类似于Integer类的构造函数，接收String参数：

```java
MethodType mt = MethodType.methodType(void.class, String.class);

MethodHandle newIntegerMH = publicLookup.findConstructor(Integer.class, mt);
```

### 5.4 字段的方法句柄

使用方法句柄也可以访问字段。首先我们定义一个Book类：

```java
public class Book {
    
    String id;
    String title;

    // constructor
}
```

以方法句柄和声明的属性之间的可直接访问可见性为前提，我们可以创建一个充当getter的方法句柄：

```java
MethodHandle getTitleMH = lookup.findGetter(Book.class, "title", String.class);
```

### 5.5 私有方法的方法句柄

在[java.lang.reflect](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/reflect/package-summary.html) API的帮助下，我们还可以为私有方法创建方法句柄。

下面为Book类添加一个私有方法：

```java
private String formatBook() {
    return id + " > " + title;
}
```

现在我们可以创建一个与formatBook()方法完全相同的方法句柄：

```java
Method formatBookMethod = Book.class.getDeclaredMethod("formatBook");
formatBookMethod.setAccessible(true);

MethodHandle formatBookMH = lookup.unreflect(formatBookMethod);
```

## 6. 调用方法句柄

一旦我们创建了方法句柄，下一步就是使用它们。特别是，MethodHandle类提供了3种不同的方法来执行方法句柄：invoke()、invokeWithArugments()和invokeExact()。

### 6.1 invoke

当使用invoke()方法时，我们强制固定参数的数量(arity)，但我们允许对参数和返回类型进行强制转换和装箱/拆箱。

让我们看看如何将invoke()与装箱参数一起使用：

```java
MethodType mt = MethodType.methodType(String.class, char.class, char.class);
MethodHandle replaceMH = publicLookup.findVirtual(String.class, "replace", mt);

String output = (String) replaceMH.invoke("jovo", Character.valueOf('o'), 'a');

assertEquals("java", output);
```

在这种情况下，replaceMH需要char参数，但invoke()在执行之前对Character参数执行拆箱。

### 6.2 invokeWithArguments

使用invokeWithArguments方法调用方法句柄是三个方法中限制最少的。事实上，除了参数和返回类型的强制转换和装箱/拆箱外，它还允许变量arity调用。

这允许我们从一个int值数组开始创建一个整数列表：

```java
MethodType mt = MethodType.methodType(List.class, Object[].class);
MethodHandle asList = publicLookup.findStatic(Arrays.class, "asList", mt);

List<Integer> list = (List<Integer>) asList.invokeWithArguments(1,2);

assertThat(Arrays.asList(1,2), is(list));
```

### 6.3 invokeExact

如果我们希望在执行方法句柄(参数的数量及其类型)的方式上更加严格，我们必须使用invokeExact()方法。事实上，它不提供对提供的类的任何转换，并且需要固定数量的参数。

让我们看看如何使用方法句柄对两个int值求和：

```java
MethodType mt = MethodType.methodType(int.class, int.class, int.class);
MethodHandle sumMH = lookup.findStatic(Integer.class, "sum", mt);

int sum = (int) sumMH.invokeExact(1, 11);

assertEquals(12, sum);
```

如果在这种情况下，我们向invokeExact方法传递一个不是int的数字，则调用将导致WrongMethodTypeException。

## 7. 与数组一起使用

MethodHandles不仅适用于字段或对象，还适用于数组。事实上，使用asSpreader() API，可以处理数组扩展方法句柄。在这种情况下，方法句柄接收一个数组参数，将其元素作为位置参数展开，并且可以选择数组的长度。

让我们看看如何扩展方法句柄来检查数组中的元素是否相等：

```java
MethodType mt = MethodType.methodType(boolean.class, Object.class);
MethodHandle equals = publicLookup.findVirtual(String.class, "equals", mt);

MethodHandle methodHandle = equals.asSpreader(Object[].class, 2);

assertTrue((boolean) methodHandle.invoke(new Object[] { "java", "java" }));
```

## 8. 增强方法句柄

一旦我们定义了一个方法句柄，就可以通过将方法句柄绑定到一个参数而不实际调用它来增强它。

例如，在Java 9中，这种行为用于优化字符串拼接。

让我们看看如何执行拼接，将后缀绑定到我们的concatMH：

```java
MethodType mt = MethodType.methodType(String.class, String.class);
MethodHandle concatMH = publicLookup.findVirtual(String.class, "concat", mt);

MethodHandle bindedConcatMH = concatMH.bindTo("Hello ");

assertEquals("Hello World!", bindedConcatMH.invoke("World!"));
```

## 9.Java9增强功能

在Java 9中，对MethodHandles API进行了一些改进，目的是使其更易于使用。

增强功能影响了3个主要方面：

-   查找函数：允许从不同的上下文中进行类查找，并支持接口中的非抽象方法
-   参数处理：改进参数折叠、参数收集和参数传播功能
-   其他组合：添加循环(loop、whileLoop、doWhileLoop...)以及更好的tryFinally异常处理支持

这些变化带来了一些额外的好处：

-   增加了JVM编译器优化
-   实例化减少
-   在MethodHandles API的使用中启用精度

[MethodHandles API Javadoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/invoke/MethodHandles.html)中提供了增强的详细信息。

## 10. 总结

在本文中，我们介绍了MethodHandles API、它们是什么以及我们如何使用它们。

我们还介绍了它与反射API的关系，并且由于方法句柄允许低级操作，因此最好避免使用它们，除非它们完全适合使用范围。