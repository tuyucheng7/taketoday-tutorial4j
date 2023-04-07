## **一、简介**

Java 类型系统是 Java 开发人员技术面试中经常提到的一个话题。本文回顾了一些最常被问到但可能很难正确回答的重要问题。

## **2.问题**

### **Q1。描述对象类在类型层次结构中的位置。哪些类型继承自对象，哪些不继承？数组是否继承自对象？可以将 Lambda 表达式分配给对象变量吗？**

java.lang.Object位于 Java 类层次结构*的*顶部。所有类都从它继承，显式、隐式（当类定义中省略*extends*关键字时）或通过继承链传递。

但是，有八种原始类型不继承自*Object*，即*boolean*、*byte*、*short*、*char*、*int*、*float*、*long*和*double*。

根据 Java 语言规范，数组也是对象。它们可以分配给*对象*引用，并且可以在它们上调用所有*对象方法。*

Lambda 表达式不能直接分配给*Object*变量，因为*Object*不是功能接口。但是您可以将 lambda 分配给功能接口变量，然后将其分配给*Object*变量（或者通过同时将其转换为功能接口来简单地将其分配给 Object 变量）。

 

### **Q2。解释原始类型和引用类型之间的区别。**

引用类型继承自顶级*java.lang.Object*类并且它们本身是可继承的（*final*类除外）。基本类型不继承，也不能被子类化。

原始类型的参数值总是通过堆栈传递，这意味着它们是按值传递的，而不是按引用传递的。这具有以下含义：对方法内原始参数值所做的更改不会传播到实际参数值。

原始类型通常使用底层硬件值类型进行存储。

例如，要存储一个*int*值，可以使用 32 位存储单元。引用类型引入了对象头的开销，它存在于引用类型的每个实例中。

对象标头的大小相对于简单的数值大小可能非常重要。这就是为什么首先引入基本类型的原因——为了节省对象开销的空间。不利之处在于，并非 Java 中的所有东西在技术上都是对象——原始值不继承自*Object*类。

 

### **Q3. 描述不同的原始类型和它们占用的内存量。**

Java 有 8 种原始类型：

-   *boolean* — 逻辑*真*/*假*值。布尔值的大小不是由 JVM 规范定义的，并且在不同的实现中可能会有所不同。
-   *byte* — 带符号的 8 位值，
-   *short* — 带符号的 16 位值，
-   *char* — 无符号 16 位值，
-   *int* — 带符号的 32 位值，
-   *long* — 带符号的 64 位值，
-   *float* — 符合 IEEE 754 标准的 32 位单精度浮点值，
-   *double* — 符合 IEEE 754 标准的 64 位双精度浮点值。

 

### **Q4. 抽象类和接口有什么区别？一个和另一个的用例是什么？**

抽象类是在其定义中带有*抽象修饰符的**类*。它不能被实例化，但它可以被子类化。接口是一种用*interface*关键字描述的类型。它也不能实例化，但可以实现。

抽象类和接口之间的主要区别是一个类可以实现多个接口，但只能扩展一个抽象类。

*抽象*类通常用作某些类层次结构中的基类型，它表示从它继承的所有类的主要意图。

*抽象*类还可以实现所有子类所需的一些基本方法。例如，JDK 中的大多数地图集合都继承自*AbstractMap*类，该类实现了子类使用的许多方法（例如*equals*方法）。

接口指定类同意的一些契约。一个实现的接口可能不仅表示该类的主要意图，而且还表示一些额外的契约。

例如，如果一个类实现了*Comparable*接口，这意味着可以比较该类的实例，无论该类的主要目的是什么。

 

### **Q5. 接口类型的成员（字段和方法）有哪些限制？**

接口可以声明字段，但它们被隐式声明为*public*、*static*和*final*，即使您没有指定这些修饰符。因此，您不能将接口字段显式定义为*private*。本质上，接口可能只有常量字段，没有实例字段。

接口的所有方法也是隐式*公开的*。它们也可以是（隐含地）*abstract*或*default*。

 

### **Q6. 内部类和静态嵌套类有什么区别？**

简单地说——嵌套类基本上是在另一个类中定义的一个类。

嵌套类分为两类，具有非常不同的属性。内部类是在不先实例化封闭类的情况下无法实例化的类，即内部类的任何实例都隐式绑定到封闭类的某个实例。

下面是一个内部类的示例——您可以看到它可以以*OuterClass1.this*构造的形式访问对外部类实例的引用：

```java
public class OuterClass1 {

    public class InnerClass {

        public OuterClass1 getOuterInstance() {
            return OuterClass1.this;
        }

    }

}复制
```

要实例化这样的内部类，您需要有一个外部类的实例：

```java
OuterClass1 outerClass1 = new OuterClass1();
OuterClass1.InnerClass innerClass = outerClass1.new InnerClass();复制
```

静态嵌套类是完全不同的。从语法上讲，它只是一个嵌套类，在其定义中带有*static*修饰符。

实际上，这意味着此类可以像任何其他类一样被实例化，而无需将其绑定到封闭类的任何实例：

```java
public class OuterClass2 {

    public static class StaticNestedClass {
    }

}复制
```

要实例化此类，您不需要外部类的实例：

```java
OuterClass2.StaticNestedClass staticNestedClass = new OuterClass2.StaticNestedClass();复制
```

 

### **Q7. Java有多重继承吗？**

Java 不支持类的多重继承，这意味着一个类只能继承自一个超类。

但是你可以用一个类实现多个接口，并且这些接口的一些方法可能被定义为*默认的*并且有一个实现。这允许您以更安全的方式在单个类中混合不同的功能。

 

### **Q8. 什么是包装类？什么是自动装箱？**

对于 Java 中的八种原始类型中的每一种，都有一个包装类，可用于包装原始值并像对象一样使用它。相应地，这些类是*Boolean*、*Byte*、*Short*、*Character*、*Integer*、*Float*、*Long*和*Double*。这些包装器很有用，例如，当您需要将原始值放入仅接受引用对象的通用集合中时。

```java
List<Integer> list = new ArrayList<>();
list.add(new Integer(5));复制
```

为了省去手动来回转换原语的麻烦，Java 编译器提供了一种称为自动装箱/自动拆箱的自动转换。

```java
List<Integer> list = new ArrayList<>();
list.add(5);
int value = list.get(0);复制
```

 

### **Q9. 描述 equals() 和 == 之间的区别**

== 运算符允许您比较两个对象的“相同性”（即两个变量都引用内存中的同一个对象）。重要的是要记住*new*关键字总是创建一个新对象，它不会传递与任何其他对象的*==*相等性，即使它们看起来具有相同的值：

```java
String string1 = new String("Hello");
String string2 = new String("Hello");

assertFalse(string1 == string2);复制
```

此外，== 运算符允许比较原始值：

```java
int i1 = 5;
int i2 = 5;

assertTrue(i1 == i2);复制
```

equals *()方法在**java.lang.Object*类中定义，因此可用于任何引用类型。默认情况下，它只是通过 == 运算符检查对象是否相同。但它通常在子类中被覆盖，以提供类的特定比较语义。

例如，对于*String*类，此方法检查字符串是否包含相同的字符：

```java
String string1 = new String("Hello");
String string2 = new String("Hello");

assertTrue(string1.equals(string2));复制
```

 

### **Q10。假设您有一个引用类类型实例的变量。你如何检查一个对象是这个类的一个实例？**

在这种情况下，您不能使用*instanceof*关键字，因为它仅在您以文字形式提供实际类名时才有效。

值得庆幸的是，*Class*类有一个方法*isInstance*允许检查一个对象是否是这个类的实例：

```java
Class<?> integerClass = new Integer(5).getClass();
assertTrue(integerClass.isInstance(new Integer(4)));复制
```

 

### **Q11. 什么是匿名类？描述它的用例。**

匿名类是在需要其实例的同一位置定义的一次性类。此类在同一位置定义和实例化，因此不需要名称。

在 Java 8 之前，您通常会使用匿名类来定义单个方法接口的实现，例如*Runnable*。在 Java 8 中，使用 lambda 代替单个抽象方法接口。但是匿名类仍然有用例，例如，当您需要具有多个方法的接口实例或具有某些附加功能的类实例时。

以下是创建和填充地图的方法：

```java
Map<String, Integer> ages = new HashMap<String, Integer>(){{
    put("David", 30);
    put("John", 25);
    put("Mary", 29);
    put("Sophie", 22);
}};复制
```

下一步**»**

[Java并发面试问题（+答案）](https://www.baeldung.com/java-concurrency-interview-questions)

**«**上一页

[Java 集合面试题](https://www.baeldung.com/java-collections-interview-questions)