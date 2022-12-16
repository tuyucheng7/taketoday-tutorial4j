## 1. 概述

**装饰器模式可用于静态或动态地将附加职责附加到对象**，装饰器为原始对象提供增强的接口。

在这种模式的实现中，我们更倾向于组合而不是继承-这样我们就可以减少为每个装饰元素一次又一次地子类化的开销。这种设计中所涉及的递归可用于根据需要多次装饰我们的对象。

## 2. 装饰者模式示例

假设我们有一个圣诞树对象，我们想要装饰它。装饰不会改变对象本身；只是除了圣诞树之外，我们还添加了一些装饰品，如花环、金属丝、树顶、泡泡灯等：

<img src="../assets/img_1.png">

**对于这种情况，我们将遵循最初的四人帮设计和命名约定**。首先，我们将创建一个ChristmasTree接口及其实现：

```java
public interface ChristmasTree {
    String decorate();
}
```

该接口的实现如下所示：

```java
public class ChristmasTreeImpl implements ChristmasTree {

    @Override
    public String decorate() {
        return "Christmas tree";
    }
}
```

现在，我们将为这棵树创建一个抽象的TreeDecorator类，这个装饰器将实现ChristmasTree接口并持有相同的对象，来自同一接口的实现方法将简单地从我们的接口调用decorate()方法：

```java
public abstract class TreeDecorator implements ChristmasTree {
    private ChristmasTree tree;

    // standard constructors
    @Override
    public String decorate() {
        return tree.decorate();
    }
}
```

我们现在将创建一些装饰元素，这些装饰器将扩展我们的抽象TreeDecorator类，并根据我们的要求修改其decorate()方法：

```java
public class BubbleLights extends TreeDecorator {

    public BubbleLights(ChristmasTree tree) {
        super(tree);
    }

    public String decorate() {
        return super.decorate() + decorateWithBubbleLights();
    }

    private String decorateWithBubbleLights() {
        return " with Bubble Lights";
    }
}
```

对于这种情况，以下断言为真：

```java
@Test 
void whenDecoratorsInjectedAtRuntime_thenConfigSuccess() {
    ChristmasTree tree1 = new Garland(new ChristmasTreeImpl());
    assertEquals(tree1.decorate(), "Christmas tree with Garland");
     
    ChristmasTree tree2 = new BubbleLights(new Garland(new Garland(new ChristmasTreeImpl())));
    assertEquals(tree2.decorate(), "Christmas tree with Garland with Garland with Bubble Lights");
}
```

请注意，在第一个tree1对象中，我们只用一个Garland来装饰它，而我们用一个BubbleLights和两个Garlands装饰另一个tree2对象。这种模式使我们能够灵活地在运行时添加任意数量的装饰器。

## 3. 总结

在本文中，我们了解了装饰器设计模式，在以下情况下这是一个不错的选择：

-   当我们希望添加、增强甚至删除对象的行为或状态时
-   当我们只想修改类的单个对象的功能而其他对象保持不变时