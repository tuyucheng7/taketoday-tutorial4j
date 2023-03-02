## 1. 概述

在本教程中，我们将了解四种从类API的方法中检索类名称的方法：[getSimpleName()](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Class.html#getSimpleName())、[getName()](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Class.html#getName())、[getTypeName()](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Class.html#getTypeName())和[getCanonicalName()](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Class.html#getCanonicalName())。

由于它们相似的名称和有些模糊的Javadoc，这些方法可能会造成混淆。当涉及原始类型、对象类型、内部或匿名类以及数组时，它们也有一些细微差别。

## 2. 检索简单名称

让我们从getSimpleName()方法开始。

在Java中，有两种名称：simple和qualified。简单名称由唯一标识符组成，而限定名称是由点分隔的简单名称序列。

顾名思义，getSimpleName()返回底层类的简单名称，即它在源代码中给出的名称。

让我们想象一下下面的类：

```java
package com.baeldung.className;
public class RetrieveClassName {}
```

它的简单名称是RetrieveClassName：

```java
assertEquals("RetrieveClassName", RetrieveClassName.class.getSimpleName());
```

我们还可以获得原始类型和数组的简单名称。对于将只是其名称的原始类型，如int、boolean或float。

对于数组，该方法将返回数组类型的简单名称，后跟数组每个维度的一对左括号和右括号([])：

```java
RetrieveClassName[] names = new RetrieveClassName[];
assertEquals("RetrieveClassName[]", names.getClass().getSimpleName());
```

因此，对于二维字符串数组，对其类调用getSimpleName()将返回String[][]。

最后，还有匿名类的具体情况。在匿名类上调用getSimpleName()将返回一个空字符串。

## 3. 检索其他名称

现在是时候看看我们如何获得类的名称、类型名称或规范名称了。与getSimpleName()不同，这些名称旨在提供有关该类的更多信息。

getCanonicalName()方法始终返回[Java语言规范](https://docs.oracle.com/javase/specs/jls/se11/html/jls-6.html#jls-6.7)中定义的规范名称。

至于其他方法，输出可能会根据用例略有不同。我们将看到这对不同的原始类型和对象类型意味着什么。

### 3.1 原始类型

让我们从原始类型开始，因为它们很简单。对于原始类型，所有三种方法getName()、getTypeName()和getCanonicalName()都将返回与getSimpleName()相同的结果：

```java
assertEquals("int", int.class.getName());
assertEquals("int", int.class.getTypeName());
assertEquals("int", int.class.getCanonicalName());
```

### 3.2 对象类型

我们现在将看到这些方法如何与对象类型一起工作。它们的行为通常是相同的：它们都返回类的规范名称。

在大多数情况下，这是一个限定名称，其中包含所有类包的简单名称以及类的简单名称：

```java
assertEquals("com.baeldung.className.RetrieveClassName", RetrieveClassName.class.getName());
assertEquals("com.baeldung.className.RetrieveClassName", RetrieveClassName.class.getTypeName());
assertEquals("com.baeldung.className.RetrieveClassName", RetrieveClassName.class.getCanonicalName());
```

### 3.3 内部类

我们在上一节中看到的是这些方法调用的一般行为，但也有一些例外。

内部类就是其中之一。getName()和getTypeName()方法的行为不同于内部类的getCanonicalName()方法。

getCanonicalName()仍然返回类的规范名称，即封闭类规范名称加上由点分隔的内部类简单名称。

另一方面，getName()和getTypeName()方法返回的结果几乎相同，但使用美元作为封闭类规范名称和内部类简单名称之间的分隔符。

让我们想象一下RetrieveClassName的内部类InnerClass：

```java
public class RetrieveClassName {
    public class InnerClass {}
}
```

然后每次调用都以稍微不同的方式表示内部类：

```java
assertEquals("com.baeldung.RetrieveClassName.InnerClass", 
    RetrieveClassName.InnerClass.class.getCanonicalName());
assertEquals("com.baeldung.RetrieveClassName$InnerClass", 
    RetrieveClassName.InnerClass.class.getName());
assertEquals("com.baeldung.RetrieveClassName$InnerClass", 
    RetrieveClassName.InnerClass.class.getTypeName());
```

### 3.4 匿名类

匿名类是另一个例外。

正如我们已经看到的，它们没有简单的名称，但它们也没有规范的名称。因此，getCanonicalName()不返回任何内容。与getSimpleName()相反，getCanonicalName()在匿名类上调用时将返回null而不是空字符串。

至于getName()和getTypeName()，它们将返回调用类的规范名称，后跟一个美元和一个数字，表示匿名类在调用类中创建的所有匿名类中的位置。

让我们用一个例子来说明这一点。我们将在这里创建两个匿名类，并在第一个类上调用getName()，在第二个类上调用getTypeName()，在com.baeldung.Main中声明它们：

```java
assertEquals("com.baeldung.Main$1", new RetrieveClassName() {}.getClass().getName());
assertEquals("com.baeldung.Main$2", new RetrieveClassName() {}.getClass().getTypeName());
```

我们应该注意到，第二次调用返回的名称末尾增加了数字，因为它应用于第二个匿名类。

### 3.5 数组

最后，我们看看上面三个方法是如何处理数组的。

为了表明我们正在处理数组，每个方法都会更新其标准结果。getTypeName()和getCanonicalName()方法会将括号对附加到它们的结果中。

让我们看看下面的示例，我们在二维InnerClass数组上调用getTypeName()和getCanonicalName()：

```java
assertEquals("com.baeldung.RetrieveClassName$InnerClass[][]", 
    RetrieveClassName.InnerClass[][].class.getTypeName());
assertEquals("com.baeldung.RetrieveClassName.InnerClass[][]", 
    RetrieveClassName.InnerClass[][].class.getCanonicalName());
```

请注意第一次调用是如何使用美元而不是点来将内部类部分与名称的其余部分分开的。

现在让我们看看getName()方法是如何工作的。当在原始类型数组上调用时，它将返回一个左括号和[一个代表原始类型的字母](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Class.html#getName())。让我们用下面的例子来检查一下，在二维原始整数数组上调用该方法：

```java
assertEquals("[[I", int[][].class.getName());
```

另一方面，当在对象数组上调用时，它将在其标准结果中添加左括号和字母L，并以分号结束。让我们在RetrieveClassName数组上试试：

```java
assertEquals("[Lcom.baeldung.className.RetrieveClassName;", RetrieveClassName[].class.getName());
```

## 4. 总结

在本文中，我们研究了四种在Java中访问类名的方法。这些方法是：getSimpleName()、getName()、getTypeName()和getCanonicalName()。

我们了解到，第一个只返回类的源代码名称，而其他提供更多信息，例如包名称和该类是内部类还是匿名类的指示。