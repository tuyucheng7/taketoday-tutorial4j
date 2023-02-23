## 1. 概述

Java是一种静态类型、面向对象的编程语言。它也是独立于平台的-Java程序可以在一种类型的机器(例如Windows系统)上编写和编译，然后在另一种机器(例如MacOS)上执行，而无需对源代码进行任何修改。

在本教程中，我们将了解并理解Java语法的基础知识。

## 2. 数据类型

Java中有两大类数据类型：[原始类型和对象/引用类型](https://www.baeldung.com/java-primitives-vs-objects)。

**[原始类型](https://www.baeldung.com/java-primitives)是存储简单数据并构成数据操作基础的基本数据类型**。例如，Java具有整数值(int、long、byte、short)、浮点值(float和double)、字符值(char)和逻辑值(boolean)的基本类型。

另一方面，**引用类型是包含对值和/或其他对象的引用的对象**，或者包含对特殊值null的引用以表示值不存在的对象。

String类是引用类型的一个很好的例子。该类的实例称为对象，表示字符序列，例如“Hello World”。

## 3. 在Java中声明变量

要在Java中声明一个变量，我们必须**指定它的名称(也称为标识符)和类型**。让我们看一个简单的例子：

```java
int a;
int b;
double c;
```

在上面的示例中，变量将根据其声明的类型接收默认初始值。由于我们将变量声明为int和double，因此它们的默认值分别为0和0.0。

或者，**我们可以在声明期间使用赋值运算符(=)来初始化变量**：

```java
int a = 10;
```

在上面的示例中，**我们将一个标识符为a的变量声明为int类型，并使用赋值运算符(=)为其赋值10，并以分号(;)终止该语句**。在Java中，所有语句都必须以分号结尾，这是强制性的。

**标识符是任意长度的名称，由字母、数字、下划线和美元符号组成**，符合以下规则：

-   以字母、下划线(_)或美元符号($)开头
-   不能是保留关键字
-   不能为true、false或null

让我们扩展上面的代码片段以包含一个简单的算术运算：

```java
int a = 10;
int b = 5;
double c = a + b;
System.out.println( a + " + " + b + " = " + c);
```

我们可以将上面代码片段的前三行理解为“**将10的值赋给a，将5的值赋给b，将a和b的值相加并将结果赋给c**”。在最后一行中，我们将运行结果输出到控制台：

```shell
10 + 5 = 15.0
```

其他类型变量的声明和初始化遵循我们上面显示的相同语法。例如，让我们声明String、char和boolean变量：

```java
String name = "Baeldung Blog";
char toggler = 'Y';
boolean isVerified = true;
```

为了强调起见，**表示char和String的文本值的主要区别在于值周围的引号数量**。因此，'a'是一个字符，而“a”是一个字符串。

## 4. 数组

数组是一种引用类型，可以存储特定类型的值的集合。在Java中声明数组的一般语法是：

> type[] identifier = new type[length];

该类型可以是任何原始类型或引用类型。

例如，让我们看看如何声明一个最多可容纳100个整数的数组：

```java
int[] numbers = new int[100];
```

要引用数组的特定元素，或为元素赋值，我们使用变量名及其索引：

```java
numbers[0] = 1;
numbers[1] = 2;
numbers[2] = 3;
int thirdElement = numbers[2];
```

**在Java中，数组索引从0开始**。数组的第一个元素位于索引0，第二个元素位于索引1，依此类推。

此外，我们可以通过调用numbers.length来获取数组的长度：

```java
int lengthOfNumbersArray = numbers.length;
```

## 5. Java关键字

**关键字是在Java中具有特殊含义的保留字**。

比如public、static、class、main、new、instanceof，在[Java](https://www.baeldung.com/tag/java-keyword/)中都是关键字，**我们不能将它们作为标识符(变量名)**。

## 6. Java中的运算符

现在我们已经看到了上面的赋值运算符(=)，让我们探索[Java语言中的一些其他类型的运算符](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html)：

### 6.1 算术运算符

Java支持以下可用于编写数学计算逻辑的[算术运算符](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op1.html)：

-   +(加法；也用于字符串拼接)
-   –(减法)
-   *(乘法)
-   /(除法)
-   %(模数或余数)

我们在前面的代码示例中使用了加号(+)运算符来执行两个变量的加法。其他算术运算符的使用类似。

加号(+)的另一个用途是拼接字符串以形成一个全新的字符串：

```java
String output =  a + " + " + b + " = " + c;
```

### 6.2 逻辑运算符

除了算术运算符之外，Java还支持以下用于评估布尔表达式的[逻辑运算符](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op2.html)：

-   &&(和)
-   ||(或)
-   !(非)

让我们考虑以下演示逻辑AND和OR运算符的代码片段。第一个示例显示了当number变量可同时被2和3整除时执行的打印语句：

```java
int number = 6;
        
if (number % 2 == 0 && number % 3 == 0) {
    System.out.println(number + " is divisible by 2 AND 3");
}
```

当数字可被2或5整除时，执行第二个：

```java
if (number % 2 == 0 || number % 5 == 0) {
    System.out.println(number + " is divisible by 2 OR 5");
}
```

### 6.3 比较运算符

当我们需要比较一个变量的值和另一个变量的值时，我们可以使用Java的[比较运算符](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op2.html)：

-   <(小于)
-   <=(小于或等于)
-   \>(大于)
-   \>=(大于或等于)
-   ==(等于)
-   !=(不等于)

例如，我们可以使用比较运算符来确定选民的资格：

```java
public boolean canVote(int age) {
    if(age < 18) {
        return false;
    }
    return true;
}
```

## 7. Java程序结构

现在我们已经了解了数据类型、变量和一些基本运算符，让我们看看如何将这些元素放在一个简单的可执行程序中。

**Java程序的基本单元是类**。一个类可以有一个或多个字段(有时称为属性)、方法，甚至其他称为内部类的类成员。

**要使一个类可执行，它必须有一个main方法**。main方法表示程序的入口点。

让我们编写一个简单的可执行类来练习我们之前考虑的代码片段之一：

```java
public class SimpleAddition {

    public static void main(String[] args) {
        int a = 10;
        int b = 5;
        double c = a + b;
        System.out.println(a + " + " + b + " = " + c);
    }
}
```

该类的名称是SimpleAddition，在其中，我们有一个包含逻辑的main方法。**左大括号和右大括号之间的代码段称为代码块**。

Java程序的源代码存储在扩展名为.java的文件中。

## 8. 编译和执行程序

要执行我们的源代码，我们首先需要编译它。此过程将生成一个带有.class文件扩展名的二进制文件。我们可以在任何安装了Java运行时环境(JRE)的机器上执行二进制文件。

让我们将上述示例中的源代码保存到名为SimpleAddition.java的文件中，并从我们保存文件的目录运行此命令：

```bash
javac SimpleAddition.java
```

要执行该程序，我们只需运行：

```bash
java SimpleAddition
```

这将向控制台产生如上所示的相同输出：

```shell
10 + 5 = 15.0
```

## 9. 总结

在本教程中，我们了解了Java的一些基本语法。就像任何其他编程语言一样，通过不断练习它会变得更简单。