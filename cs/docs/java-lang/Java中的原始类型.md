## 1. 概述

在这个快速教程中，我们将了解原始类型，它们是什么，以及为什么我们应该避免使用它们。

## 2. 原始类型

**原始类型是没有类型参数的泛型接口或类的名称**：

```java
List list = new ArrayList(); // raw type
```

而不是：

```java
List<Integer> listIntgrs = new ArrayList<>(); // parameterized type
```

List<Integer\>是接口List<E\>的参数化类型，而List是接口List<E\>的原始类型。

与非泛型遗留代码交互时，原始类型很有用。

否则，**不鼓励使用它**。这是因为：

1.  他们没有表现力
2.  它们缺乏类型安全，并且
3.  在运行时而不是在编译时观察到问题

## 3. 缺乏表达能力

原始类型不会像参数化类型那样自文档化和解释自己。

我们可以很容易地推断出参数化类型List<String\>是一个包含String的列表。但是，原始类型缺乏这种清晰度，因此很难使用它及其API方法。

让我们看看List接口中方法get(int index)的签名，以便更好地理解这一点：

```java
/**
 * Returns the element at the specified position in this list.
 *
 * @param index index of the element to return
 * @return the element at the specified position in this list
 * @throws IndexOutOfBoundsException if the index is out of range
 *         (<tt>index < 0 || index >= size()</tt>)
 */
E get(int index);
```

方法get(int index)在参数化类型List<String\>的位置index处返回一个字符串。

但是，对于原始类型List，它返回一个Object。因此，我们需要付出额外的努力来检查和识别原始类型列表中的元素类型，**并添加适当的类型转换**。这可能会在运行时引入错误，因为原始类型**不是类型安全的**。

## 4. 非类型安全

我们得到原始类型的预泛型行为。因此，原始类型List接受Object并且**可以容纳任何数据类型的元素**。当我们混合使用参数化类型和原始类型时，这可能会导致类型安全问题。

让我们通过创建一些实例化List<String\>的代码来了解这一点，然后将其传递给接收原始类型List并向其添加Integer的方法：

```java
public void methodA() {
    List<String> parameterizedList = new ArrayList<>();
    parameterizedList.add("Hello Folks");
    methodB(parameterizedList);
}

public void methodB(List rawList) { // raw type!
    rawList.add(1);
}
```

代码被编译(带有警告)，并且Integer在执行时被添加到原始类型List中。作为参数传递的List<String\>**现在包含一个String和一个Integer**。

由于使用原始类型，编译器会打印出一条警告：

```shell
Note: RawTypeDemo.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
```

## 5. 运行时的问题

原始类型缺少类型安全性会产生因果效应，可能导致运行时出现异常。

让我们修改前面的示例，以便methodA在调用methodB后获取List<String\>的索引位置1处的元素：

```java
public void methodA() {
    List<String> parameterizedList = new ArrayList<>();
    parameterizedList.add("Hello Folks");
    methodB(parameterizedList);
    String s = parameterizedList.get(1);
}

public void methodB(List rawList) {
    rawList.add(1);
}
```

代码被编译(带有相同的警告)并在执行时抛出ClassCastException。发生这种情况是因为方法get(int index)返回一个Integer，它不能分配给String类型的变量：

```shell
Exception in thread "main" java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
```

## 6. 总结

原始类型很难使用，并且可能会在我们的代码中引入错误。

使用它们可能会导致灾难性的后果，不幸的是，大多数这些灾难都发生在运行时。