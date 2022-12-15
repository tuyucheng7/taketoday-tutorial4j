## 1. 概述

在本快速教程中，我们介绍如何使用[模板方法模式](https://en.wikipedia.org/wiki/Template_method_pattern)。**通过将逻辑封装在单个方法中，可以更轻松地实现复杂的算法**。

## 2. 实现

为了演示模板方法模式的工作原理，让我们创建一个表示构建计算机工作站的简单示例。

给定模式的定义，**算法的结构将在定义模板buildComputer()方法的基类中定义**：

```java
public abstract class ComputerBuilder {

    // ...

    public final Computer buildComputer() {
        addMotherboard();
        setupMotherboard();
        addProcessor();
        return new Computer(computerParts);
    }

    public abstract void addMotherboard();

    public abstract void setupMotherboard();

    public abstract void addProcessor();

    // ...
}
```

**ComputerBuilder类负责通过声明添加和设置不同组件(如主板和处理器)的方法来概述构建计算机所需的步骤**。

在这里，**buildComputer()方法是模板方法**，它定义了组装计算机部件的算法步骤，并返回完全初始化的Computer实例。

请注意，**它被声明为final以防止它被重写**。

## 3. 实践

在已经设置基类的情况下，让我们尝试通过创建两个子类来使用它，一个构建“标准”计算机，另一个构建“高端”计算机：

```java
public class StandardComputerBuilder extends ComputerBuilder {

    @Override
    public void addMotherboard() {
        computerParts.put("Motherboard", "Standard Motherboard");
    }

    @Override
    public void setupMotherboard() {
        motherboardSetupStatus.add("Screwing the standard motherboard to the case.");
        motherboardSetupStatus.add("Pluging in the power supply connectors.");
        motherboardSetupStatus.forEach(
                step -> System.out.println(step));
    }

    @Override
    public void addProcessor() {
        computerParts.put("Processor", "Standard Processor");
    }
}
```

这是HighEndComputerBuilder变体：

```java
public class HighEndComputerBuilder extends ComputerBuilder {

    @Override
    public void addMotherboard() {
        computerParts.put("Motherboard", "High-end Motherboard");
    }

    @Override
    public void setupMotherboard() {
        motherboardSetupStatus.add("Screwing the high-end motherboard to the case.");
        motherboardSetupStatus.add("Pluging in the power supply connectors.");
        motherboardSetupStatus.forEach(
                step -> System.out.println(step));
    }

    @Override
    public void addProcessor() {
        computerParts.put("Processor", "High-end Processor");
    }
}
```

如我们所见，我们不需要担心整个组装过程，而只需要为单独的方法提供实现。

现在，让我们看看它的实际效果：

```java
new StandardComputerBuilder()
    .buildComputer();
    .getComputerParts()
    .forEach((k, v) -> System.out.println("Part : " + k + " Value : " + v));
        
new HighEndComputerBuilder()
    .buildComputer();
    .getComputerParts()
    .forEach((k, v) -> System.out.println("Part : " + k + " Value : " + v));
```

## 4. Java核心库中的模板方法

这种模式在Java核心库中被广泛使用，例如[java.util.AbstractList](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/AbstractList.html)或[java.util.AbstractSet](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/AbstractSet.html)。

例如，AbstractList提供了[List](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html)接口的框架实现。

模板方法的一个示例可以是addAll()方法，尽管它没有明确定义为final：

```java
public boolean addAll(int index, Collection<? extends E> c) {
    rangeCheckForAdd(index);
    boolean modified = false;
    for (E e : c) {
        add(index++, e);
        modified = true;
    }
    return modified;
}
```

用户只需要实现add()方法：

```java
public void add(int index, E element) {
    throw new UnsupportedOperationException();
}
```

在这里，程序员负责提供一个实现，用于在给定索引(集合算法的变体部分)处将元素添加到集合中。

## 5. 总结

在本文中，我们演示了模板方法模式以及如何在Java中实现它。模板方法模式促进了代码重用和解耦，但以使用继承为代价。