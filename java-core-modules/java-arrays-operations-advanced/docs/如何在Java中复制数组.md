## 1. 概述

在本快速教程中，我们将讨论Java中不同的数组方法。数组似乎是一项微不足道的任务，但如果不小心，它可能会导致意外的结果和程序行为。

## 2.系统类

让我们从核心Java库System.arrayCopy() 开始。这会将一个数组从源数组到目标数组，从源位置开始操作到目标位置，直到指定的长度。

到目标数组的元素数等于指定的长度。它提供了一种将数组的子序列到另一个数组的简单方法。

如果任何数组参数为null，它会抛出NullPointerException。如果任何整数参数为负数或超出范围，它会抛出IndexOutOfBoundException。

让我们看一个使用java.util.System类将完整数组到另一个数组的示例：

```java
int[] array = {23, 43, 55};
int[] copiedArray = new int[3];

System.arraycopy(array, 0, copiedArray, 0, 3);
```

此方法采用以下参数：源数组、从源数组的起始位置、目标数组、目标数组中的起始位置以及要的元素数。

让我们看一下将子序列从源数组到目标的另一个示例：

```java
int[] array = {23, 43, 55, 12, 65, 88, 92};
int[] copiedArray = new int[3];

System.arraycopy(array, 2, copiedArray, 0, 3);

assertTrue(3 == copiedArray.length);
assertTrue(copiedArray[0] == array[2]);
assertTrue(copiedArray[1] == array[3]);
assertTrue(copiedArray[2] == array[4]);

```

## 3.数组类

Arrays类还提供了多个重载方法来将一个数组到另一个数组。在内部，它使用我们之前检查过的System类提供的相同方法。它主要提供了两个方法，copyOf(…)和copyRangeOf(…)。

让我们先看看copyOf ：

```java
int[] array = {23, 43, 55, 12};
int newLength = array.length;

int[] copiedArray = Arrays.copyOf(array, newLength);

```

重要的是要注意Arrays类使用Math.min(…)选择源数组长度的最小值，并使用新长度参数的值来确定结果数组的大小。

Arrays.copyOfRange()除了源数组参数外，还有两个参数“ from”和“ to” 。结果数组包含“ from”索引，但排除了“to”索引：

```java
int[] array = {23, 43, 55, 12, 65, 88, 92};

int[] copiedArray = Arrays.copyOfRange(array, 1, 4);

assertTrue(3 == copiedArray.length);
assertTrue(copiedArray[0] == array[1]);
assertTrue(copiedArray[1] == array[2]);
assertTrue(copiedArray[2] == array[3]);
```

如果应用于非原始对象类型的数组，这两种方法都会对对象进行浅表：

```java
Employee[] copiedArray = Arrays.copyOf(employees, employees.length);

employees[0].setName(employees[0].getName() + "_Changed");
 
assertArrayEquals(copiedArray, array);
```

因为结果是浅拷贝，原数组元素员工姓名的变化导致了数组的变化。

如果我们想要对非原始类型进行深度，我们可以选择下一节中描述的其他选项之一。

## 4. 用Object.clone()数组

Object.clone()继承自数组中的Object类。

首先，我们将使用 clone 方法一个基本类型数组：

```java
int[] array = {23, 43, 55, 12};
 
int[] copiedArray = array.clone();

```

这是它有效的证明：

```java
assertArrayEquals(copiedArray, array);
array[0] = 9;

assertTrue(copiedArray[0] != array[0]);
```

上面的例子表明它们在克隆后具有相同的内容，但它们持有不同的引用，因此其中一个的任何更改都不会影响另一个。

另一方面，如果我们使用相同的方法克隆一个非基本类型的数组，那么结果将不同。

它创建非基本类型数组元素的浅表副本，即使封闭对象的类实现了Cloneable接口并覆盖了Object类的clone()方法。

让我们看一个例子：

```java
public class Address implements Cloneable {
    // ...

    @Override
    protected Object clone() throws CloneNotSupportedException {
         super.clone();
         Address address = new Address();
         address.setCity(this.city);
        
         return address;
    }
}

```

我们可以通过创建一个新的地址数组并调用我们的clone()方法来测试我们的实现：

```java
Address[] addresses = createAddressArray();
Address[] copiedArray = addresses.clone();
addresses[0].setCity(addresses[0].getCity() + "_Changed");

assertArrayEquals(copiedArray, addresses);
```

此示例表明，原始数组或数组中的任何更改都会导致另一个数组发生更改，即使包含的对象是Cloneable也是如此。

## 5. 使用流API

事实证明，我们也可以使用 Stream API 来数组：

```java
String[] strArray = {"orange", "red", "green'"};
String[] copiedArray = Arrays.stream(strArray).toArray(String[]::new);

```

对于非原始类型，它还会进行对象的浅表。要了解有关Java 8 Streams的更多信息，我们可以从[这里](https://www.baeldung.com/java-8-streams)开始。

## 6. 外部图书馆

Apache Commons 3提供了一个名为SerializationUtils 的实用程序类，它提供了一个clone(...)方法。如果我们需要对非原始类型数组进行深度，这将非常有用。可以[在这里](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")下载，它的Maven依赖是：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>

```

让我们看一个测试用例：

```java
public class Employee implements Serializable {
    // fields
    // standard getters and setters
}

Employee[] employees = createEmployeesArray();
Employee[] copiedArray = SerializationUtils.clone(employees);

employees[0].setName(employees[0].getName() + "_Changed");
assertFalse(
  copiedArray[0].getName().equals(employees[0].getName()));
```

此类要求每个对象都应实现Serializable接口。在性能方面，它比为对象图中的每个对象手动编写的克隆方法要慢。

## 七、总结

在本文中，我们讨论了在Java中数组的各种选项。

我们选择使用的方法主要取决于具体的场景。只要我们使用原始类型数组，我们就可以使用System和Arrays类提供的任何方法。性能上应该没有任何差异。

对于非基本类型，如果我们需要对数组进行深拷贝，我们可以使用 SerializationUtils或显式地向我们的类添加克隆方法。