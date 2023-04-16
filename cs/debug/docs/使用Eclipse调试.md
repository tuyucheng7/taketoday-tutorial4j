## 一、概述

在本快速指南中，我们将了解如何使用 Eclipse IDE 调试 Java 程序。

## 2. 基本概念

Eclipse 对调试应用程序有很好的支持。**它可视化逐步执行**并帮助我们发现错误。

为了演示 Eclipse 中的调试功能，我们将使用示例程序*PerfectSquareCounter*。该程序计算给定数字下的完全正方形总数和完全正方形总数：

```java
public class PerfectSquareCounter {

    static int evenPerfectSquareNumbers = 0;

    public static void main(String[] args) {
        int i = 100;
        System.out.println("Total Perfect Squares: " + calculateCount(i));
        System.out.println("Even Perfect Squares : " + evenPerfectSquareNumbers);
    }

    public static int calculateCount(int i) {
        int perfectSquaresCount = 0;
        for (int number = 1; number <= i; number++) {
            if (isPerfectSquare(number)) {
                perfectSquaresCount++;
                if (number % 2 == 0) {
                    evenPerfectSquareNumbers++;
                }
            }
        }
        return perfectSquaresCount;
    }

    private static boolean isPerfectSquare(int number) {
        double sqrt = Math.sqrt(number);
        return sqrt - Math.floor(sqrt) == 0;
    }
}复制
```

### 2.1. 调试模式

首先，我们需要**在 Eclipse 中以调试模式启动 Java 程序。**这可以通过两种方式实现：

-   右键单击编辑器并选择*Debug As -> Java Application*（如下图所示）
-   *从工具栏调试*程序（在下面的屏幕截图中突出显示）

[![调试eclipse1](https://www.baeldung.com/wp-content/uploads/2019/08/debugeclipse1.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/debugeclipse1.jpg)

### 2.2. 断点

我们需要**定义程序执行应该暂停以进行调查的点**。**这些称为断点，适用于方法。**它们也可以在执行之前或期间的任何时间定义。

基本上，有 3 种方法可以为程序添加断点：

-   右键单击该行对应的标记栏（垂直标尺）并选择 Toggle Breakpoint（如下图所示）
-   在编辑器中的必要行上按*Ctrl+Shift+B*
-   双击与所需行对应的标记栏（垂直标尺）

[![调试eclipse2](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse2.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse2.jpg)

### 2.3. 代码流控制

现在调试器在给定的断点处停止，我们可以继续进一步执行。

假设调试器当前位于第 16 行的以下屏幕截图中：

[![调试eclipse3](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse3.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse3.jpg)

最常用的调试选项是：

-   ***Step Into (F5) –\*此操作进入**当前行中使用的方法（如果有）；否则，它进入下一行。在此示例中，它将在方法*isPerfectSquare()中使用调试器*
-   ***跳过 (F6) –\*此操作处理当前行并继续到下一行。**在此示例中，这将执行方法*isPerfectSquare()*并继续下一行
-   ***步骤返回 (F7) –\*此操作完成当前方法并将我们带回调用方法。**因为在这种情况下，我们在循环中有一个断点，它仍然在方法内，否则它会回到 main 方法
-   ***Resume (F8) –\*该操作将简单地继续执行直到程序结束**，除非我们遇到任何进一步的断点

### 2.4. 调试视角

当我们以调试模式启动程序时，Eclipse 将提示一个选项以切换到调试透视图。**Debug 透视图是一些有用视图的集合，可帮助我们可视化调试器并与之交互。**

我们也可以随时手动切换到 Debug 透视图。

以下是其中包含的一些最有用的视图：

-   **调试视图**——这显示了不同的线程和调用堆栈跟踪
-   **变量视图**——显示任何给定点的变量值。如果我们需要查看静态变量，我们需要明确指定
-   **断点**——这显示了不同的断点和观察点（我们将在下面看到）
-   **调试 Shell** – 这允许我们在调试时编写和评估自定义代码（稍后将介绍示例）

[![调试eclipse4](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse4.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse4.jpg)

## 三、技巧

在本节中，我们将介绍一些有助于我们掌握 Eclipse 调试的重要技术。

### 3.1. 变量

我们可以在 Variables 视图下看到执行过程中变量的值。为了查看静态变量，我们可以选择下拉选项*Java -> Show Static Variables*。

**使用变量视图，可以在执行期间将任何值更改为所需的值。**

*例如，如果我们需要跳过几个数字并直接从数字 80 开始，我们可以通过更改变量number*的值来实现：

[![调试eclipse5](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse5.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse5.jpg)

### 3.2. 检查值

如果我们需要检查 Java 表达式或语句的值，我们可以在编辑器中选择特定的表达式，单击鼠标右键，然后检查，如下所示。一个方便的快捷方式是在表达式上**按\*Ctrl+Shift+I\*以查看值：**

[![调试eclipse6](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse6.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse6.jpg)

[![调试eclipse7](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse7.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse7.jpg)

如果我们需要**永久检查这个表达式，我们可以右键单击并观察。**现在，它被添加到 Expressions 视图中，并且可以在不同的运行中看到该表达式的值。

### 3.3. 调试外壳

在调试会话的上下文中，**我们可以编写和运行自定义代码来评估可能性。**这是在 Debug Shell 中完成的。

例如，如果我们需要交叉检查*sqrt*功能的正确性，我们可以在 Debug Shell 中进行。在代码上，*右键单击 -> 检查*以查看值：

[![调试eclipse8](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse8.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse8.jpg)

### [![调试eclipse9](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse9.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse9.jpg)

### 3.4. 条件断点

在某些情况下，我们只想针对特定条件进行调试。我们可以通过以下两种方式之一**向断点添加条件来实现此目的：**

-   右键单击断点并选择断点属性
-   在断点视图中，选择断点并指定条件

*例如，我们可以指定断点，只有当number*等于 10 时才暂停执行：

[![调试eclipse10](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse10.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse10.jpg)

### 3.5. 观察点

**什么** **断点是给方法的，观察点是给类级变量的**。在当前示例中，*evenPerfectSquareNumbers*声明上的断点称为观察点。现在，每次在观察点上访问或修改该字段时，调试器都会暂停执行。

这是默认行为，可以在观察点的属性中更改。

在此示例中，每当完全平方数为偶数时，调试器将停止执行：

[![调试eclipse11](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse11.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse11.jpg)

### 3.6. 触发点

假设我们正在调试具有大量源代码的应用程序中的复杂问题。由于分散的断点，调试器将继续暂停流程。

**当一个断点被标记为触发点时，这意味着只有当这个断点被击中时，其余的断点才会被启用。**

*例如，在下面的屏幕截图中， isPerfectSquare()*上的断点应该在循环中的每次迭代中命中。*但是，我们已将calculateCount()*方法上的断点指定为触发点以及条件。

因此，当迭代计数达到 10 时，这将触发其余的断点。因此，从现在开始，如果命中*isPerfectSquare()*上的断点，执行将暂停：

[![调试eclipse12](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse12.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/debug_eclipse12.jpg)

### 3.7. 远程调试

最后，如果应用程序运行在 Eclipse 之外，我们仍然可以使用上述所有功能，前提是远程应用程序允许调试。在 Eclipse 中，我们将选择**[Debug 作为 Remote Java Application](https://www.baeldung.com/spring-debugging)**。

## 4。结论

在本快速指南中，我们了解了在 Eclipse IDE 中调试程序的基础知识和不同技术。

与往常一样，本练习中使用的源代码可[在 GitHub 上](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-lang-math-2)获得。