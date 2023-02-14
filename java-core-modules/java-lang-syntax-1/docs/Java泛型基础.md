## 1. 概述

JDK 5.0引入了Java泛型，目的是减少错误并在类型上添加额外的抽象层。

本教程是对Java中的泛型、它们背后的目标以及它们如何提高代码质量的快速介绍。

## 2. 对泛型的需求

让我们想象一个场景，我们想要在Java中创建一个列表来存储Integer。

我们可能会尝试编写以下内容：

```java
List list = new LinkedList();
list.add(new Integer(1)); 
Integer i = list.iterator().next();
```

令人惊讶的是，编译器会抱怨最后一行。它不知道返回什么数据类型。

编译器将需要显式强制转换：

```java
Integer i = (Integer) list.iterator.next();
```

没有任何协定可以保证列表的返回类型是Integer。定义的列表可以包含任何对象。我们只知道我们正在通过检查上下文来检索列表。在查看类型时，它只能保证它是一个Object，因此需要显式强制转换以确保该类型是安全的。

这种转换可能很烦人-我们知道这个列表中的数据类型是Integer。强制转换也使我们的代码变得混乱。如果程序员在显式转换时出错，它可能会导致与类型相关的运行时错误。

如果程序员可以表达他们使用特定类型的意图并且编译器确保这些类型的正确性，那将会容易得多。这是泛型背后的核心思想。

让我们修改前面代码片段的第一行：

```java
List<Integer> list = new LinkedList<>();
```

通过添加包含类型的菱形运算符<>，我们将此列表的特化范围缩小到仅Integer类型。换句话说，我们指定列表中保存的类型。编译器可以在编译时强制执行类型。

在小程序中，这似乎是一个微不足道的补充。但在较大的程序中，这可以显著增加健壮性并使程序更易于阅读。

## 3. 泛型方法

我们用单个方法声明编写泛型方法，我们可以用不同类型的参数调用它们。无论我们使用哪种类型，编译器都会确保其正确性。

这些是泛型方法的一些属性：

-   泛型方法在方法声明的返回类型之前有一个类型参数(包含类型的菱形运算符)。
-   类型参数可以是有界的(我们将在本文后面解释边界)。
-   泛型方法可以有不同的类型参数，在方法签名中用逗号分隔。
-   泛型方法的方法体就像普通方法一样。

下面是定义将数组转换为列表的泛型方法的示例：

```java
public <T> List<T> fromArrayToList(T[] a) {   
    return Arrays.stream(a).collect(Collectors.toList());
}
```

方法签名中的<T\>暗示该方法将处理泛型类型T。即使该方法返回void，这也是必需的。

如前所述，该方法可以处理不止一种泛型类型。在这种情况下，我们必须将所有泛型类型添加到方法签名中。

以下是我们如何修改上述方法来处理类型T和类型G：

```java
public static <T, G> List<G> fromArrayToList(T[] a, Function<T, G> mapperFunction) {
    return Arrays.stream(a)
        .map(mapperFunction)
        .collect(Collectors.toList());
}
```

我们正在传递一个函数，该函数将具有T类型元素的数组转换为具有G类型元素的列表。

一个示例是将Integer转换为其String表示形式：

```java
@Test
public void givenArrayOfIntegers_thanListOfStringReturnedOK() {
    Integer[] intArray = {1, 2, 3, 4, 5};
    List<String> stringList = Generics.fromArrayToList(intArray, Object::toString);
 
    assertThat(stringList, hasItems("1", "2", "3", "4", "5"));
}
```

请注意，Oracle建议使用大写字母来表示泛型类型，并选择更具描述性的字母来表示正式类型。在Java集合中，我们使用T表示类型，K表示键，V表示值。

### 3.1 有界泛型

请记住，类型参数可以有界。有界意味着“受限”，我们可以限制方法接受的类型。

例如，我们可以指定一个方法接受一个类型及其所有子类(上限)或一个类型及其所有超类(下限)。

要声明上限类型，我们在类型后使用关键字extends，然后是我们要使用的上限：

```java
public <T extends Number> List<T> fromArrayToList(T[] a) {
    ...
}
```

我们在这里使用关键字extends表示类型T在类的情况下扩展上限或在接口的情况下实现上限。

### 3.2 多重上限

一个类型也可以有多个上限：

```java
<T extends Number & Comparable>
```

如果T扩展的类型之一是类(例如Number)，我们必须将它放在边界列表的第一位。否则，会导致编译时错误。

## 4. 对泛型使用通配符

通配符在Java中由问号?表示，我们使用它们来引用未知类型。通配符对泛型特别有用，可以用作参数类型。

但首先，有一个重要的注意事项需要考虑。**我们知道Object是所有Java类的超类型。但是，Object的集合不是任何集合的超类型**。

例如，List<Object\>不是List<String\>的超类型，将List<Object\>类型的变量分配给List<String\>类型的变量将导致编译器错误。这是为了防止在将异构类型添加到同一集合时可能发生的冲突。

同样的规则适用于任何类型及其子类型的集合。

考虑这个例子：

```java
public static void paintAllBuildings(List<Building> buildings) {
    buildings.forEach(Building::paint);
}
```

如果我们想象一个Building的子类型，例如House，我们不能将此方法与House列表一起使用，即使House是Building的一个子类型。

如果我们需要将此方法用于类型Building及其所有子类型，则有界通配符可以发挥魔力：

```java
public static void paintAllBuildings(List<? extends Building> buildings) {
    ...
}
```

现在此方法将适用于类型Building及其所有子类型。这称为上限通配符，其中Building类型是上限。

我们还可以指定具有下限的通配符，其中未知类型必须是指定类型的超类型。可以使用super关键字后跟特定类型来指定下限。例如，<? super T\>表示未知类型，它是T的超类(=T及其所有父类)。

## 5. 类型擦除

泛型被添加到Java中以确保类型安全。为了确保泛型不会在运行时造成开销，编译器在编译时对泛型应用称为类型擦除的过程。

类型擦除删除所有类型参数，并用它们的边界替换它们，如果类型参数是无界的，则用Object替换它们。这样，编译后的字节码只包含普通的类、接口和方法，确保不会产生新的类型。在编译时，也会对Object类型应用正确的强制转换。

下面是类型擦除的一个示例：

```java
public <T> List<T> genericMethod(List<T> list) {
    return list.stream().collect(Collectors.toList());
}
```

通过类型擦除，无界类型T被替换为Object：

```java
// for illustration
public List<Object> withErasure(List<Object> list) {
    return list.stream().collect(Collectors.toList());
}

// which in practice results in
public List withErasure(List list) {
    return list.stream().collect(Collectors.toList());
}
```

如果类型是有界的，则类型将在编译时替换为绑定：

```java
public <T extends Building> void genericMethod(T t) {
    ...
}
```

编译后会改变：

```java
public void genericMethod(Building t) {
    ...
}
```

## 6. 泛型和原始数据类型

**Java中泛型的一个限制是类型参数不能是原始类型**。

例如，以下内容无法编译：

```java
List<int> list = new ArrayList<>();
list.add(17);
```

要理解为什么原始数据类型不起作用，让我们记住**泛型是一个编译时特性**，这意味着类型参数被删除并且所有泛型类型都作为类型Object实现。

我们来看一个列表的add方法：

```java
List<Integer> list = new ArrayList<>();
list.add(17);
```

add方法的签名是：

```java
boolean add(E e);
```

并将编译为：

```java
boolean add(Object e);
```

因此，类型参数必须可转换为Object。**由于基本类型不扩展Object，因此我们不能将它们用作类型参数**。

**然而，Java为原始类型提供了装箱类型，以及自动装箱和拆箱以解包它们**：

```java
Integer a = 17;
int b = a;
```

所以，如果我们想创建一个可以容纳整数的列表，我们可以使用这个包装器：

```java
List<Integer> list = new ArrayList<>();
list.add(17);
int first = list.get(0);
```

编译后的代码将等效于以下内容：

```java
List list = new ArrayList<>();
list.add(Integer.valueOf(17));
int first = ((Integer) list.get(0)).intValue();
```

**Java的未来版本可能允许泛型的原始数据类型**。[Valhalla](https://openjdk.java.net/projects/valhalla/)项目旨在改进处理泛型的方式。这个想法是按照[JEP 218](https://openjdk.java.net/jeps/218)中的描述实现泛型专业化。

## 7. 总结

Java泛型是对Java语言的强大补充，因为它使程序员的工作更轻松且不易出错。泛型在编译时强制执行类型正确性，最重要的是，它可以实现泛型算法而不会对我们的应用程序造成任何额外开销。