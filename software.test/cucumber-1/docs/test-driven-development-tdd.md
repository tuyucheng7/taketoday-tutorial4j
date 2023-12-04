## 1. 概述

TDD(Test Driven Development)是一个迭代开发过程，每次迭代都从为新功能编写的一组测试开始，这些测试应该在迭代开始时失败，因为此时还没有与测试对应的应用程序生产代码。在迭代的下一个阶段，编写应用程序代码的目的是通过迭代早期编写的所有测试。一旦应用程序代码准备就绪，就会运行测试。

测试运行中的任何失败都将被标记，并且会编写/重构更多的应用程序代码以使这些测试通过。添加/重构应用程序代码后，将再次运行测试，这个循环一直持续，直到所有测试都通过。一旦所有的测试都通过了，我们就可以确定为其编写测试的所有功能都已开发完成。

## 2. TDD的优点

1. 单元测试证明代码确实有效
2. 可以驱动程序的设计
3. 重构可以改进代码的设计
4. 低级回归测试套件
5. 优先测试可以降低Bug的成本

## 3. TDD的缺点

1. 开发人员可能认为这是浪费时间
2. 测试的目标可能变成类和方法的验证，而不是代码真正应该做什么
3. 测试成为项目维护开销的一部分
4. 当需求发生变化时，需要重新编写测试

## 4. TDD的实现过程

### 4.1 阶段1-需求定义

我们将以一个计算器应用程序为例，并基于计算器的基本功能定义需求。为了进一步简化，我们把计算器应用程序定义为一个简单的Java类：

```java
public class Calculator {
}
```

在阶段1中，收集并定义应用程序的需求。以一个简单的计算器为例，我们可以说在迭代1中，我们想要实现：

1. 两个数字相加的能力
2. 两个数字相减的能力
3. 两个数字相乘的能力

此时，我们打开Intellij IDEA或你选择的任何Java IDE，并创建一个新的Java项目。在项目中，我们将创建两个文件夹(src和test)。src将包含所有的应用程序代码，即计算器应用程序的代码；而test文件夹将包含所有的测试，我们将在这里使用JUnit 5框架。

如前所述，TDD从定义测试方面的需求开始，让我们从测试的角度来完善我们的第一个需求。

**需求1**：计算器应该具有将两个数字相加的能力。

> **测试1**：给定两个正数(10和20)，计算器应该能够将这两个数相加并给出正确的结果(30)。

> **测试2**：给定两个负数(-10和-20)，计算器应该能够将这两个数相加并得到正确的结果(-30)。

针对减法和乘法，同样给出类似的需求定义。在阶段1中，我们所要做的就是为所有的需求编写测试。此时，在我们的项目中，我们将只有一个名为Calculator的空类。

```java
package cn.tuyucheng.taketoday.cucumber.tdd;

public class Calculator {
}
```

我们将针对这个类编写所有的测试，下面是我们的需求1看起来的样子，我们把所有的加法测试放在一个名为AddingNumbersTests的类中：

```java
class AddingNumberTests {

    private final Calculator calculator = new Calculator();

    @Test
    void addTwoPositiveNumbers() {
        int expectedResult = 30;
        int actualResult = calculator.add(10, 20);
        assertEquals(expectedResult, actualResult, "the sum of two positive numbers is incorrect");
    }

    @Test
    void addTwoNegativeNumbers() {
        int expectedResult = -30;
        int actualResult = calculator.add(-10, -20);
        assertEquals(expectedResult, actualResult, "the sum of two negative numbers is incorrect");
    }
}
```

现在我们首先想到的第一件事是Calculator类没有任何方法，在我们的测试中，我们在Calculator类上调用了一个名为add()的方法，这会导致一个编译错误。嗯，这就是首先编写测试的全部意义，这将迫使我们只添加必要的代码。在这里我们忽略编译错误，继续下一步。

### 4.2 阶段2-执行测试

在此阶段，我们将简单地运行测试。

**尝试1**：当我们第一次运行测试时，会得到以下错误消息：

```text
java: 找不到符号
  符号:   方法 add(int,int)
  位置: 类型为cn.tuyucheng.taketoday.cucumber.tdd.Calculator的变量 myCalculator
```

此错误清楚地表明add方法不存在于Calculator类中：

![](/assets/images/2023/bdd/testdrivendevelopmenttdd01.png)

### 4.3 阶段3-添加/重构代码

在上一步测试失败后，我们需要采取必要的操作，我们简单地在Calculator类中添加一个名为add的方法，并使它暂时返回0。现在我们的Calculator类看起来像这样：

```java
public class Calculator {

    public int add(int left, int right) {
        return 0;
    }
}
```

通过此更改，我们将进入下一步，即重新运行测试，也就是之前提到的第2阶段，此时测试的结果为：

![](/assets/images/2023/bdd/testdrivendevelopmenttdd02.png)

现在，由于这次测试失败，我们得出结论，两个正数和两个负数的加法没有正确地进行计算。根据测试失败的情况，我们进一步添加足够多的代码以使这两个测试通过。当我们这样做时，我们会再次进入下一个阶段，也就是第3阶段。经过此阶段后，我们的Calculator类的代码如下：

```java
public class Calculator {

    public int add(int left, int right) {
        return left + right;
    }
}
```

现在我们再次回到阶段2，执行测试，此更改后的测试结果将使我们的所有测试通过，一旦所有的测试都通过了，我们就可以断定迭代已经完成了：

![](/assets/images/2023/bdd/testdrivendevelopmenttdd03.png)

一旦所有测试通过，就表示迭代结束。如果你的产品需要实现更多的功能，那么它将再次经历相同的阶段，但这一次会包含新的功能集和更多的测试。

从总的来说，以上的过程可以通过下图概述：

![](/assets/images/2023/bdd/testdrivendevelopmenttdd04.png)

## 5. 总结

有了对TDD的理解，我们接下来会介绍BDD，为理解Gherkin和Cucumber打好基础。