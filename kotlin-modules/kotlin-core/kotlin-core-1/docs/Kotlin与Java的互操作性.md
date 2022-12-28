## 1. 概述

在本教程中，我们将讨论**Java和Kotlin之间的互操作性**，我们将涵盖一些基本示例以及其他一些更复杂的场景。

## 2. 设置Kotlin

使用IntelliJ、Eclipse甚至命令行创建Kotlin项目都非常简单，但是对于本教程，我们将遵循我们上一个教程[Kotlin简介](Kotlin语言简介.md)中的安装步骤，因为它已经包含了我们演示所需的内容。

## 3. 基础知识

**从Kotlin调用Java是直接和流畅的，因为它是带着互操作性的理念构建的**。

让我们使用核心Java创建这个Customer类：

```java
public class Customer {

    private String firstName;
    private String lastName;
    private int age;

    // standard setters and getters
}
```

## 4. Getters和Setters

现在让我们从Kotlin来使用这个简单的Java POJO。

遵循这些类型方法的Java约定的getter和setter在Kotlin中表示为属性：

```kotlin
val customer = Customer()

customer.firstName = "Frodo"
customer.lastName = "Baggins"

assertEquals(customer.firstName, "Frodo")
assertEquals(customer.lastName, "Baggins")
```

值得注意的是，**实例化对象不需要new关键字**。

该语言试图尽可能避免样板代码，因此我们不会显式调用getter/setter，我们可以简单地使用字段表示法来使用它们。

我们需要记住，如果Java类只有setter方法，则该属性将无法访问，因为该语言不支持仅设置属性。

如果一个方法返回void，那么当从Kotlin调用它时，它将返回Unit。

## 5. 空安全

Kotlin以其空安全特性而闻名，但正如我们所知，Java并非如此，这使得来自它的对象不切实际。如果我们有一个String数组，可以看到一个非常简单的例子：

```kotlin
val characterList = ArrayList<String>()
characterList.add("Bilbo")
val character = list[0]
```

当在平台类型的变量上调用方法时，Kotlin不会在编译时显示任何可空性错误，并且这种类型不能用语言显式编写。所以当一个值被赋值的时候，我们可以依赖这个推断，或者我们可以只选择我们期望的类型：

```kotlin
val a: String? = character
val a: String = character
```

它们都是允许的，但在非null类型的情况下，编译器将在赋值时立即断言，这将防止变量持有null值。

最后，编译器会尽力避免空值，但由于泛型，仍然无法消除它。

## 6. 数组

在Kotlin中，数组是不变的，这意味着它不允许我们将Array<Int\>分配给Array<Any\>以防止运行时失败。

假设我们有一个示例类：

```java
public class ArrayExample {

    public int sumValues(int[] nums) {
        int res = 0;

        for (int x : nums) {
            res += x;
        }

        return res;
    }
}
```

**如果我们想将基本类型数组传递给此方法，我们必须使用Kotlin中的一个专用类**：

```kotlin
val ex = ArrayExample()
val numArray = intArrayOf(1, 2, 3)

assertEquals(ex.sumValues(numArray), 6)
```

## 7. 可变参数

Java为我们提供了将任意数量的参数传递给方法的能力：

```java
public int sumArgValues(int... sums) {
    // same as above
}
```

过程是一样的，只是稍微不同的是我们需要使用扩展运算符*来传递数组：

```kotlin
assertEquals(ex.sumValues(*numArray), 6)
```

目前，不可能将null传递给可变参数方法。

## 8. 异常

**在Kotlin中，所有异常都是非受检的**，这意味着编译器不会强制我们捕获任何异常：

```java
// In our Java code

public void writeList() throws IOException {
    File file = new File("E://file.txt");
    FileReader fr = new FileReader(file);
    fr.close();
}

// In Kotlin

fun makeReadFile() {
    val ax = ArrayExample()
    ax.writeList()
}
```

## 9. 反射

简单地说，反射适用于Kotlin和Java类：

```kotlin
val instance = Customer::class.java
val constructors = instance.constructors

assertEquals(constructors.size, 1)
assertEquals(constructors[0].name, "cn.tuyucheng.taketoday.java.Customer")
```

我们还可以获得getter和setter方法、Java字段的KProperty和构造函数的KFunction。

## 10. 对象方法

将对象导入Kotlin时，所有类型为java.lang.Object的引用都会更改为kotlin.Any：

```kotlin
val instance = Customer::class
val supertypes = instance.supertypes

assertEquals(supertypes[0].toString(), "kotlin.Any")
```

## 11. 总结

这个快速教程让我们对Kotlin和Java的互操作性有了更深入的了解，我们查看了一些简单的示例，以展示Kotlin通常如何减少整体代码的冗长程度。