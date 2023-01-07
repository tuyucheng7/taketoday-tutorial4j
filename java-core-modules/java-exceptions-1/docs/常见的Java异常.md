## 1. 概述

本教程重点介绍一些常见的Java异常。

我们将从讨论什么是异常开始。稍后，我们将详细讨论不同类型的已检查和未检查异常。

## 2. 例外情况

异常是程序执行过程中代码序列中出现的异常情况。当程序在运行时违反某些约束时，就会出现这种异常情况。

所有异常类型都是Exception类的子类 。然后将此类细分为已检查异常和未检查异常。我们将在后续部分详细讨论它们。

## 3.检查异常

检查异常是强制处理的。它们是Exception类的直接子类 。

[关于它们的重要性的争论](https://www.ibm.com/developerworks/library/j-jtp05254/index.html)值得一看。

让我们详细定义一些已检查的异常。

### 3.1. 异常

当任何输入/输出操作失败时，方法将抛出 IOException或其直接子类。 

这些 I/O 操作的典型用途包括：

-   使用java.io包处理文件系统或数据流 
-   使用java.net包创建网络应用程序 

FileNotFoundException异常

[FileNotFoundException](https://www.baeldung.com/java-filenotfound-exception)是处理文件系统时常见的IOException类型 

```java
try {
    new FileReader(new File("/invalid/file/location"));
} catch (FileNotFoundException e) {
    LOGGER.info("FileNotFoundException caught!");
}
```

格式错误的 URLException

使用 URL 时， 如果我们的 URL 无效，我们可能会遇到 MalformedURLException 。

```java
try {
    new URL("malformedurl");
} catch (MalformedURLException e) {
    LOGGER.error("MalformedURLException caught!");
}
```

### 3.2. 解析异常

Java 使用文本解析来创建基于给定字符串的对象。 如果解析导致错误，它会抛出 ParseException。

例如，我们可以用不同的方式表示日期，例如dd/mm/yyyy或dd,mm,yyyy，但尝试解析 具有不同格式的字符串：

```java
try {
    new SimpleDateFormat("MM, dd, yyyy").parse("invalid-date");
} catch (ParseException e) {
    LOGGER.error("ParseException caught!");
}
```

此处，String格式错误并导致ParseException。

### 3.3. 中断异常

每当Java线程调用 join()、sleep() 或 wait()时 ，它就会进入WAITING状态或TIMED_WAITING状态。

此外，一个线程可以通过调用另一个线程的 interrupt()方法来中断另一个线程。

因此，如果另一个线程在它处于WAITING或TIMED_WAITING状态时中断它，则该 线程将抛出InterruptedException。

考虑以下具有两个线程的示例：

-   主线程启动子线程并中断它
-   子线程启动并调用 sleep()

这种情况会导致 InterruptedException：

```java
class ChildThread extends Thread {

    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException caught!");
        }
    }
}

public class MainThread {

    public static void main(String[] args) 
      throws InterruptedException {
        ChildThread childThread = new ChildThread();
        childThread.start();
        childThread.interrupt();
    }
}
```

## 4. 未经检查的异常

对于 Unchecked Exceptions，编译器在编译过程中不检查。 因此，处理这些异常的方法不是强制性的。

所有未经检查的异常都扩展了类 RuntimeException。

让我们详细讨论一些未经检查的异常。

### 4.1. 空指针异常

如果应用程序试图在实际需要对象实例的地方 使用null ，则该方法将抛出NullPointerException。 

在不同的情况下，非法使用 null会导致 NullPointerException。让我们考虑其中的一些。

调用没有对象实例的类的方法：

```java
String strObj = null;
strObj.equals("Hello World"); // throws NullPointerException.
```

此外，如果应用程序试图访问或修改具有 空引用的实例变量，我们会得到 NullPointerException ：

```java
Person personObj = null;
String name = personObj.personName; // Accessing the field of a null object
personObj.personName = "Jon Doe"; // Modifying the field of a null object
```

### 4.2. ArrayIndexOutOfBoundsException异常

数组以连续的方式存储其元素。因此，我们可以通过索引访问它的元素。

但是，如果一段代码试图访问数组的非法索引，相应的方法将抛出 ArrayIndexOutOfBoundException 。

让我们看几个抛出ArrayIndexOutOfBoundException的例子：

```java
int[] nums = new int[] {1, 2, 3};
int numFromNegativeIndex = nums[-1]; // Trying to access at negative index
int numFromGreaterIndex = nums[4];   // Trying to access at greater index
int numFromLengthIndex = nums[3];    // Trying to access at index equal to size of the array
```

### 4.3. StringIndexOutOfBoundsException异常

Java 中的String类 提供了访问字符串的特定字符或从 String 中切出字符数组的方法。当我们使用这些方法时，它在内部将String转换为字符数组。

同样，该数组上的索引可能被非法使用。在这种情况下， String类的这些方法会抛出 StringIndexOutOfBoundsException。

此异常 表明索引大于或等于字符串的大小。 StringIndexOutOfBoundsException扩展了 IndexOutOfBoundsException。

当我们尝试访问索引等于字符串长度或其他一些非法索引处的字符时 ，类 String的方法 charAt(index)会抛出此异常：

```java
String str = "Hello World";
char charAtNegativeIndex = str.charAt(-1); // Trying to access at negative index
char charAtLengthIndex = str.charAt(11);   // Trying to access at index equal to size of the string		

```

### 4.4. 数字格式异常

应用程序通常以String中的数字数据结束。为了将此数据解释为数字，Java 允许将 String转换为数字类型。Integer、Float 等包装类 包含用于此目的的实用方法。

但是，如果String在转换过程中没有合适的格式，该方法将抛出 NumberFormatException 。

让我们考虑以下片段。

在这里，我们声明了一个包含字母数字数据的字符串。此外，我们尝试使用Integer包装器类的方法 将此数据解释为数字。

因此，这会导致NumberFormatException：

```java
String str = "100ABCD";
int x = Integer.parseInt(str); // Throws NumberFormatException
int y = Integer.valueOf(str); //Throws NumberFormatException
```

### 4.5. 算术异常

当程序评估算术运算并导致某些异常情况时，它会抛出 ArithmeticException。此外， ArithmeticException仅适用于 int 和 long数据类型。

例如，如果我们尝试将一个整数除以零，我们会得到一个ArithmeticException：

```java
int illegalOperation = 30/0; // Throws ArithmeticException
```

### 4.6. 类转换异常

Java 允许对象之间的[类型转换](https://www.baeldung.com/java-type-casting)以支持继承和多态性。我们可以向上转换一个对象，也可以向下转换它。

在向上转型中，我们将一个对象转换为它的超类型。在向下转换中，我们将一个对象转换为其子类型之一。

但是，在运行时，如果代码尝试将对象向下转换为它不是其实例的子类型，则该方法会抛出 ClassCastException。

运行时实例在类型转换中真正重要。考虑Animal、 Dog 和 Lion之间的以下继承 ：

```java
class Animal {}

class Dog extends Animal {}

class Lion extends Animal {}

```

此外，在驱动程序类中，我们将包含Lion实例的Animal引用转换为Dog。

但是，在运行时，JVM 注意到实例Lion与类Dog的子类型不兼容。

这导致ClassCastException：

```java
Animal animal = new Lion(); // At runtime the instance is Lion
Dog tommy = (Dog) animal; // Throws ClassCastException
```

### 4.7. 非法参数异常

如果我们使用一些非法或不适当的参数调用方法，则会抛出 IllegalArgumentException 。

例如，Thread类的sleep()方法需要正时间，而我们传递一个负时间间隔作为参数。这导致 IllegalArgumentException：

```java
Thread.currentThread().sleep(-10000); // Throws IllegalArgumentException
```

### 4.8. 非法状态异常

IllegalStateException 表示在非法或不适当的时间调用了方法。

每个Java对象都有一个状态(实例变量)和一些行为(方法)。因此，IllegalStateException意味着用当前状态变量调用此对象的行为是非法的。

但是，对于一些不同的状态变量，它可能是合法的。

例如，我们使用迭代器来迭代列表。每当我们初始化一个时，它都会在内部将其状态变量lastRet设置为 -1。

在此上下文中，程序尝试调用列表中的remove方法：

```java
//Initialized with index at -1
Iterator<Integer> intListIterator = new ArrayList<>().iterator(); 

intListIterator.remove(); // IllegalStateException

```

在内部，remove方法检查状态变量lastRet，如果它小于 0，则抛出IllegalStateException。 在这里，变量仍然指向值 -1。

结果，我们得到一个 IllegalStateException。

## 5.总结

在这篇文章中，我们首先讨论了什么是异常。异常 是在程序执行期间发生的事件，它会破坏程序指令的正常流程。

然后，我们将异常分为 Checked Exceptions 和 Unchecked Exceptions。

接下来，我们讨论了在编译时或运行时可能出现的不同类型的异常。