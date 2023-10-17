## 1. 概述

在本教程中，我们将在Java中实现一个支持加减乘除运算的基本计算器。

我们还将运算符和操作数作为输入并基于它们处理计算。

## 2. 基本设置

首先，让我们展示一些关于计算器的信息：

```java
System.out.println("---------------------------------- n" +
  "Welcome to Basic Calculator n" +
  "----------------------------------");
System.out.println("Following operations are supported : n" +
  "1. Addition (+) n" +
  "2. Subtraction (-) n" +
  "3. Multiplication () n" +
  "4. Division (/) n");
```

现在，让我们使用[java.util.Scanner](https://www.baeldung.com/java-scanner)来获取用户输入：

```java
Scanner scanner = new Scanner(System.in);

System.out.println("Enter an operator: (+ OR - OR  OR /) ");
char operation = scanner.next().charAt(0);

System.out.println("Enter the first number: ");
double num1 = scanner.nextDouble();

System.out.println("Enter the second number: ");
double num2 = scanner.nextDouble();
```

当我们将输入输入系统时，我们需要验证它们。例如，如果运算符不是 +、-、 或 /，那么我们的计算器应该会指出错误的输入。同样，如果我们将第二个数输入0进行除法运算，结果也不会很好。

因此，让我们实施这些验证。

首先我们关注一下运算符无效时的情况：

```java
if (!(operation == '+' || operation == '-' || operation == '' || operation == '/')) {
    System.err.println("Invalid Operator. Please use only + or - or  or /");
}
```

然后我们可以显示无效操作的错误：

```java
if (operation == '/' && num2 == 0.0) {
    System.err.println("The second number cannot be zero for division operation.");
}
```

首先验证用户输入。之后，计算结果将显示为：

<数字 1> <操作> <数字 2> = <结果>

## 3. 处理计算

首先，我们可以使用[if-else](https://www.baeldung.com/java-if-else) 结构来处理计算

```java
if (operation == '+') {
    System.out.println(num1 + " + " + num2 + " = " + (num1 + num2));
} else if (operation == '-') {
    System.out.println(num1 + " - " + num2 + " = " + (num1 - num2));
} else if (operation == '') {
    System.out.println(num1 + " x " + num2 + " = " + (num1  num2));
} else if (operation == '/') {
    System.out.println(num1 + " / " + num2 + " = " + (num1 / num2));
} else {
    System.err.println("Invalid Operator Specified.");
}
```

同样，我们可以使用Java[switch](https://www.baeldung.com/java-switch)语句：

```java
switch (operation) {
    case '+':
        System.out.println(num1 + " + " + num2 + " = " + (num1 + num2));
        break;
    case '-':
        System.out.println(num1 + " - " + num2 + " = " + (num1 - num2));
        break;
    case '':
        System.out.println(num1 + " x " + num2 + " = " + (num1  num2));
        break;
    case '/':
        System.out.println(num1 + " / " + num2 + " = " + (num1 / num2));
        break;
    default:
        System.err.println("Invalid Operator Specified.");
        break;
}
```

我们可以使用一个变量来存储计算结果。结果，它可以在最后打印。在这种情况下，System.out.println将只使用一次。

此外，计算的最大范围是 2147483647。因此，如果超过它，我们就会从int数据类型溢出。因此，它应该存储在一个[更大数据类型](https://www.baeldung.com/java-primitives)的变量中，例如double数据类型。

## 4。总结

在本教程中，我们使用两种不同的结构在Java中实现了一个基本计算器。我们还确保输入在进一步处理之前得到验证。