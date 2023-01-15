## 1. 简介

Java 9为开发人员带来了许多有用的新特性。

其中之一是[java.lang.invoke.VarHandle](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/invoke/VarHandle.html) API(代表变量句柄)，我们将在本文中介绍。

## 2. 什么是变量句柄？

通常，变量句柄只是对变量的类型化引用。变量可以是数组元素、类的实例或静态字段。

VarHandle类在特定条件下提供对变量的读写访问。VarHandles是不可变的并且没有可见状态。更重要的是，它们不能被细分。

每个VarHandle都有：

-   泛型类型T，它是此VarHandle表示的每个变量的类型。
-   坐标类型CT的列表，它是坐标表达式的类型，允许定位由此VarHandle引用的变量。

坐标类型列表可能为空。

VarHandle的目标是为在字段和数组元素上调用java.util.concurrent.atomic和sun.misc.Unsafe操作的等效项定义一个标准。

这些操作大多是原子或有序操作，例如原子字段递增。

## 3. 创建变量句柄

要使用VarHandle，我们首先需要有变量。让我们声明一个简单的类，其中包含我们将在示例中使用的不同类型的int变量：

```java
public class VariableHandlesUnitTest {
    public int publicTestVariable = 1;
    private int privateTestVariable = 1;
    public int variableToSet = 1;
    public int variableToCompareAndSet = 1;
    public int variableToGetAndAdd = 0;
    public byte variableToBitwiseOr = 0;
}
```

### 3.1 准则和惯例

按照惯例，我们应该将VarHandle声明为[静态最终](http://gee.cs.oswego.edu/dl/html/j9mm.html)字段，并在静态块中显式初始化它们。另外，我们通常使用相应字段名称的大写版本作为它们的名称。 

例如，下面是Java本身如何在内部使用VarHandle来实现[AtomicReference](https://github.com/openjdk/jdk14u/blob/d6d8d4d931b06d919e7688c6106f489a173d8608/src/java.base/share/classes/java/util/concurrent/atomic/AtomicReference.java#L51)：

```java
private volatile V value;
private static final VarHandle VALUE;
static {
    try {
        MethodHandles.Lookup l = MethodHandles.lookup();
        VALUE = l.findVarHandle(AtomicReference.class, "value", Object.class);
    } catch (ReflectiveOperationException e) {
        throw new ExceptionInInitializerError(e);
    }
}
```

大多数时候，我们可以在使用VarHandle时使用相同的模式。现在我们知道了这一点，让我们看看如何在实践中使用它们。

### 3.2 公共变量的变量句柄

现在我们可以使用findVarHandle()方法为我们的publicTestVariable获取VarHandle：

```java
@Test
void whenVariableHandleForPublicVariableIsCreated_ThenItIsInitializedProperly() throws NoSuchFieldException, IllegalAccessException {
	VarHandle PUBLIC_TEST_VARIABLE = MethodHandles
			.lookup()
			.in(VariableHandlesUnitTest.class)
			.findVarHandle(VariableHandlesUnitTest.class, "publicTestVariable", int.class);
    
	assertEquals(1, PUBLIC_TEST_VARIABLE.coordinateTypes().size());
	assertEquals(VariableHandlesUnitTest.class, PUBLIC_TEST_VARIABLE.coordinateTypes().get(0));
}
```

我们可以看到，这个VarHandle的coordinateTypes属性不是空的，它有一个元素，就是我们的VariableHandlesUnitTest类。

### 3.3 私有变量的变量句柄

如果我们有一个私有成员并且我们需要这个变量的变量句柄，我们可以使用privateLookupIn()方法获得它：

```java
@Test
void whenVariableHandleForPrivateVariableIsCreated_ThenItIsInitializedProperly() throws NoSuchFieldException, IllegalAccessException {
	VarHandle PRIVATE_TEST_VARIABLE = MethodHandles
			.privateLookupIn(VariableHandlesUnitTest.class, MethodHandles.lookup())
			.findVarHandle(VariableHandlesUnitTest.class, "privateTestVariable", int.class);
    
	assertEquals(1, PRIVATE_TEST_VARIABLE.coordinateTypes().size());
	assertEquals(VariableHandlesUnitTest.class, PRIVATE_TEST_VARIABLE.coordinateTypes().get(0));
}
```

在这里，我们使用了privateLookupIn()方法，它比普通的lookup()方法具有更广泛的访问权限。这使我们能够访问私有、公共或受保护的变量。在Java 9之前，此操作的等效API是Unsafe类和Reflection API中的setAccessible()方法。

但是，这种方法有其缺点。例如，它只适用于变量的特定实例。VarHandle在这种情况下是更好更快的解决方案。

### 3.4 数组的变量句柄

我们可以使用前面的语法来获取数组字段。但是，我们也可以获取特定类型数组的VarHandle ：

```java
@Test
void whenVariableHandleForArrayVariableIsCreated_ThenItIsInitializedProperly() throws NoSuchFieldException, IllegalAccessException {
	VarHandle arrayVarHandle = MethodHandles
			.arrayElementVarHandle(int[].class);
    
	assertEquals(2, arrayVarHandle.coordinateTypes().size());
	assertEquals(int[].class, arrayVarHandle.coordinateTypes().get(0));
}
```

可以看到，这样的VarHandle有两个坐标类型int和[]，它们表示一个int基元数组。

## 4. 调用VarHandle方法

大多数VarHandle方法都需要可变数量的Object类型的参数，使用Object...作为参数会禁用静态参数检查。

所有参数检查都是在运行时完成的。此外，不同的方法期望具有不同数量的不同类型的参数。如果我们未能提供正确数量的正确类型的参数，则方法调用将抛出WrongMethodTypeException。

例如，get()需要至少一个参数，这有助于定位变量，但set()需要一个更多的参数，即要分配给变量的值。

## 5. 变量句柄访问模式

一般来说，VarHandle类的所有方法都属于五种不同的访问方式，让我们在接下来的小节中逐一介绍。

### 5.1 读取权限

具有读取访问级别的方法允许在指定的内存排序效果下获取变量的值。这种访问模式有几种方法，例如：get()、getAcquire()、getVolatile()和getOpaque()。

我们可以轻松地在VarHandle上使用get()方法：

```java
assertEquals(1, (int) PUBLIC_TEST_VARIABLE.get(this));
```

get()方法只接收CoordinateTypes作为参数，因此我们可以在本例中简单地使用this。

### 5.2 写访问

具有写入访问级别的方法允许我们在特定的内存排序效果下设置变量的值。与具有读访问权限的方法类似，我们有几个具有写访问权限的方法：set()、setOpaque()、setVolatile()和setRelease()。

我们可以在VarHandle上使用set()方法：

```java
VARIABLE_TO_SET.set(this, 15);
assertEquals(15, (int) VARIABLE_TO_SET.get(this));
```

set()方法至少需要两个参数：第一个将帮助定位变量，而第二个是要设置给变量的值。

### 5.3 原子更新访问

具有此访问级别的方法可用于原子更新变量的值，让我们使用compareAndSet()方法来看看效果：

```java
VARIABLE_TO_COMPARE_AND_SET.compareAndSet(this, 1, 100);
assertEquals(100, (int) VARIABLE_TO_COMPARE_AND_SET.get(this));
```

除了CoordinateTypes之外，compareAndSet()方法还需要两个额外参数：oldValue和newValue。如果变量等于oldVariable，则该方法设置变量的值，否则保持不变。

### 5.4 数值原子更新访问

这些方法允许在特定的内存排序效果下执行数值运算，例如getAndAdd()，让我们看看如何使用VarHandle执行原子操作：

```java
int before = (int) VARIABLE_TO_GET_AND_ADD.getAndAdd(this, 200);

assertEquals(0, before);
assertEquals(200, (int) VARIABLE_TO_GET_AND_ADD.get(this));
```

这里，getAndAdd()方法首先返回变量的值，然后加上提供的值。

### 5.5 按位原子更新访问

具有这种访问权限的方法允许我们在特定的内存排序效果下原子地执行位操作，让我们看一个使用getAndBitwiseOr()方法的例子：

```java
byte before = (byte) VARIABLE_TO_BITWISE_OR.getAndBitwiseOr(this, (byte) 127);

assertEquals(0, before);
assertEquals(127, (byte) VARIABLE_TO_BITWISE_OR.get(this));
```

此方法将获取变量的值，并对其执行按位或运算。如果方法所需的访问模式与变量所允许的访问模式不匹配，则方法调用将抛出IllegalAccessException。例如，如果我们尝试对final变量使用set()方法，就会发生这种情况。

## 6. 内存排序效应

我们之前提到VarHandle方法允许在特定的内存排序效果下访问变量。

对于大多数方法，有4种内存排序效果：

-   普通的读写保证了32位以下的引用和原始类型的位原子性。此外，它们对其他特征没有施加排序约束。
-   不透明操作是按位原子的，并且相对于对同一变量的访问顺序一致。
-   Acquire和Release操作遵循Opaque属性。此外，仅在匹配Release模式写入之后才会对Acquire读取进行排序。
-   易失性操作相对于彼此是完全有序的。

记住访问模式将覆盖以前的内存排序效果非常重要。这意味着，例如，如果我们使用get()，它将是一个普通的读取操作，即使我们将变量声明为volatile。因此，开发人员在使用VarHandle操作时必须格外小心。

## 7. 总结

在本教程中，我们介绍了变量句柄以及如何使用它们。这个主题非常复杂，因为变量句柄旨在允许低级操作，除非必要，否则不应使用它们。
