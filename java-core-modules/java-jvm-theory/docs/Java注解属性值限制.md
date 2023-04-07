## 一、概述

如今，很难想象 Java 没有注解这个 Java 语言中的强大工具。

Java 提供了一组[内置的注解](https://www.baeldung.com/java-default-annotations)。此外，还有大量来自不同库的注释。我们甚至可以定义和处理我们自己的注解。我们可以使用属性值调整这些注释，但是，这些属性值有局限性。特别地，**注解属性值必须是常量表达式**。

在本教程中，我们将了解该限制的一些原因，并深入了解 JVM 以更好地解释它。我们还将查看一些涉及注释属性值的问题示例和解决方案。

## 2. 底层的 Java 注解属性

让我们考虑一下 Java 类文件如何存储注释属性。Java 有一个特殊的结构，称为*[element_value](https://docs.oracle.com/javase/specs/jvms/se15/html/jvms-4.html#jvms-4.7.16.1)*。此结构存储特定的注释属性。

结构*element_value*可以存储四种不同类型的值：

-   来自常量池的常量
-   类字面量
-   嵌套注解
-   一组值

因此，注释属性中的常量是[编译时常量](https://www.baeldung.com/java-compile-time-constants#1-compile-time-constants)。否则，编译器将不知道应该将什么值放入常量池并用作注释属性。

Java 规范定义了生成[常量表达式的](https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.28)操作。如果我们将这些操作应用于编译时常量，我们将得到编译时常量。

假设我们有一个具有属性*值的注释**@Marker*：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Marker {
    String value();
}复制
```

例如，这段代码编译没有错误：

```java
@Marker(Example.ATTRIBUTE_FOO + Example.ATTRIBUTE_BAR)
public class Example {
    static final String ATTRIBUTE_FOO = "foo";
    static final String ATTRIBUTE_BAR = "bar";

    // ...
}复制
```

在这里，我们将注释属性定义为两个字符串的串联。串联运算符产生常量表达式。

## 3.使用静态初始化器

*让我们考虑在静态*块中初始化的常量：

```java
@Marker(Example.ATTRIBUTE_FOO)
public class Example {
    static final String[] ATTRIBUTES = {"foo", "Bar"};
    static final String ATTRIBUTE_FOO;

    static {
        ATTRIBUTE_FOO = ATTRIBUTES[0];
    }
    
    // ...
}
复制
```

*它初始化静态*块中的字段并尝试将该字段用作注释属性。**这种方法会导致编译错误。**

首先，变量*ATTRIBUTE_FOO*具有*static*和*final*修饰符，但编译器无法计算该字段。应用程序在运行时计算它。

其次，**在 JVM 加载类之前，注释属性必须具有准确的值**。但是，当*静态*初始化程序运行时，该类已经加载。所以，这个限制是有道理的。

在字段初始化时会出现同样的错误。由于同样的原因，此代码不正确：

```java
@Marker(Example.ATTRIBUTE_FOO)
public class Example {
    static final String[] ATTRIBUTES = {"foo", "Bar"};
    static final String ATTRIBUTE_FOO = ATTRIBUTES[0];

    // ...
}
复制
```

JVM 如何初始化*ATTRIBUTE_FOO*？数组访问运算符*ATTRIBUTES[0]*在类初始值设定项中运行。所以，*ATTRIBUTE_FOO*是一个运行时常量。它不是在编译时定义的。

## 4. 数组常量作为注解属性

让我们考虑一个数组注释属性：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Marker {
    String[] value();
}
复制
```

此代码将无法编译：

```java
@Marker(value = Example.ATTRIBUTES)
public class Example {
    static final String[] ATTRIBUTES = {"foo", "bar"};

    // ...
}复制
```

首先，虽然*final*修饰符保护引用不被更改，但**我们仍然可以修改数组元素**。

其次，**数组文字不能是运行时常量。JVM 在静态初始值设定项中设置每个元素**——这是我们之前描述的限制。

最后，类文件存储该数组中每个元素的值。因此，编译器计算属性数组的每个元素，并且发生在编译时。

这样，我们每次只能指定一个数组属性：

```java
@Marker(value = {"foo", "bar"})
public class Example {
    // ...
}复制
```

我们仍然可以使用常量作为数组属性的原始元素。

## 5. 标记界面中的注释：为什么不起作用？

所以，如果一个注解属性是一个数组，我们必须每次都重复它。但我们想避免这种复制粘贴。我们为什么不做我们的注解*@Inherited*？我们可以将注释添加到[标记界面](https://www.baeldung.com/java-marker-interfaces#:~:text=A marker interface is an,also called a tagging interface.)：

```java
@Marker(value = {"foo", "bar"})
public interface MarkerInterface {
}
复制
```

然后，我们可以让需要这个注解的类实现它：

```java
public class Example implements MarkerInterface {
    // ...
}复制
```

**这种方法行不通**。代码将编译无误。但是，**Java 不支持从 interfaces 继承注释**，即使注释本身具有*@Inherited*注释。因此，实现标记接口的类不会继承注释。

**这样做的原因是多重继承的问题**。的确，如果多个接口有相同的注解，Java 就无法取其一。

所以，我们无法避免这种带有标记界面的复制粘贴。

## 6. 数组元素作为注解属性

假设我们有一个数组常量，我们使用这个常量作为注解属性：

```java
@Marker(Example.ATTRIBUTES[0])
public class Example {
    static final String[] ATTRIBUTES = {"Foo", "Bar"};
    // ...
}复制
```

此代码无法编译。注释参数必须是编译时常量。但是，正如我们之前所考虑的，**数组不是编译时常量**。

此外，**数组访问表达式不是常量表达式**。

如果我们有一个*列表*而不是数组怎么办？方法调用不属于常量表达式。因此，使用*List*类的*get*方法会导致相同的错误。

相反，我们应该显式地引用一个常量：

```java
@Marker(Example.ATTRIBUTE_FOO)
public class Example {
    static final String ATTRIBUTE_FOO = "Foo";
    static final String[] ATTRIBUTES = {ATTRIBUTE_FOO, "Bar"};
    // ...
}
复制
```

这样，我们在字符串常量中指定了注解属性值，Java 编译器就可以明确地找到该属性值。

## 七、结论

在本文中，我们了解了注解参数的局限性。我们考虑了注释属性问题的一些示例。我们还在这些限制的上下文中讨论了 JVM 内部结构。

在所有示例中，我们对常量和注释使用相同的类。但是，所有这些限制都适用于常量来自另一个类的情况。