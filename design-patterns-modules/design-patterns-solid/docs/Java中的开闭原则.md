## 1. 概述

在本教程中，我们介绍面向对象编程的[SOLID原则](SOLID原则的可靠指南.md)之一-开闭原则(OCP)。总的来说，我们将详细介绍这个原则是什么以及在设计我们的软件时如何实现它。

## 2. 开闭原则

**顾名思义，该原则指出软件实体应该对扩展开放，但对修改关闭**。因此，当业务需求发生变化时，可以扩展实体，但不能修改实体。

### 2.1 不合规

**假设我们正在构建一个计算器应用程序，该应用程序可能具有多个操作，例如加法和减法**。

首先，我们定义一个顶级接口CalculatorOperation：

```java
public interface CalculatorOperation {
}
```

**然后我们定义一个Addition类，它将两个数字相加并实现CalculatorOperation**：

```java
public class Addition implements CalculatorOperation {
    private double left;
    private double right;
    private double result = 0.0;

    public Addition(double left, double right) {
        this.left = left;
        this.right = right;
    }

    // getters and setters
}
```

截至目前，我们只有一个类Addition，因此我们需要定义另一个名为Subtraction的类：

```java
public class Subtraction implements CalculatorOperation {
    private double left;
    private double right;
    private double result = 0.0;

    public Subtraction(double left, double right) {
        this.left = left;
        this.right = right;
    }

    // getters and setters
}
```

**现在定义我们的主类，它将执行我们的计算操作**： 

```java
public class Calculator {

    public void calculate(CalculatorOperation operation) {
        if (operation == null) {
            throw new InvalidParameterException("Can not perform operation");
        }

        if (operation instanceof Addition) {
            Addition addition = (Addition) operation;
            addition.setResult(addition.getLeft() + addition.getRight());
        } else if (operation instanceof Subtraction) {
            Subtraction subtraction = (Subtraction) operation;
            subtraction.setResult(subtraction.getLeft() - subtraction.getRight());
        }
    }
}
```

**虽然这看起来不错，但它并不是OCP的一个很好的例子**。当有新的乘法或除法需求出现时，我们除了更改Calculator类的calculate方法外别无他法。**因此，我们可以说此代码不符合OCP**。

### 2.2 符合OCP标准

正如我们所看到的，我们的计算器应用程序尚不符合OCP标准，calculate方法中的代码将随着每个传入的新操作支持请求而改变。因此，我们需要提取这段代码并将其放在抽象层中。

一种解决方案是将每个操作委托给它们各自的类：

```java
public interface CalculatorOperation {
    void perform();
}
```

**因此，Addition类可以实现将两个数字相加的逻辑**：

```java
public class Addition implements CalculatorOperation {
    private double left;
    private double right;
    private double result;

    // constructor, getters and setters

    @Override
    public void perform() {
        result = left + right;
    }
}
```

同样，更新的Subtraction类也有类似的逻辑。与Addition和Subtraction类似，作为一个新的变更请求，我们可以实现除法逻辑：

```java
public class Division implements CalculatorOperation {
    private double left;
    private double right;
    private double result;

    // constructor, getters and setters
    @Override
    public void perform() {
        if (right != 0) {
            result = left / right;
        }
    }
}
```

**最后，我们的Calculator类不需要在引入新运算符时实现新逻辑**：

```java
public class Calculator {

    public void calculate(CalculatorOperation operation) {
        if (operation == null) {
            throw new InvalidParameterException("Cannot perform operation");
        }
        operation.perform();
    }
}
```

这样，该类就可以关闭以进行修改，但可以打开以进行扩展。

## 3. 总结

在本教程中，我们了解了OCP的定义，然后详细阐述了该定义。接下来我们演示了一个简单的计算器应用程序示例，该应用程序的设计存在缺陷。最后，我们通过遵守OCP来使设计变得更好。