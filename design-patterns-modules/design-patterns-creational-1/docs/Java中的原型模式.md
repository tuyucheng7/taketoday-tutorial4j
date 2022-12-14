## 1. 简介

在本教程中，我们介绍一种[创建型设计模式]()-原型模式。首先我们解释该模式的概念，然后通过使用Java实现它，并讨论它的一些优点和缺点。

## 2. 原型模式

**当我们有一个类(原型)的实例并且我们想通过原型来创建新对象时，通常会使用原型模式**。

例如：在某些游戏中，我们希望背景中有树木或建筑物。我们可能会意识到，我们不必在每次角色移动时都创建新的树木或建筑物并将它们渲染在屏幕上。所以，我们首先创建树的一个实例，然后我们可以从这个实例(原型)中创建任意数量的树并更新它们的位置，我们也可以选择在游戏的新关卡中更改树木的颜色。

原型模式与上面这个例子非常相似，**我们无需创建新对象，只需克隆原型实例即可**。

## 3. UML图

<img src="../assets/img_2.png">

在图中，我们看到客户端告诉原型克隆自身并创建一个对象。Prototype是一个接口，并声明了一个克隆自身的方法，ConcretePrototype1和ConcretePrototype2实现了克隆自身的操作。

## 4. 实现

我们可以在Java中实现此模式的方法之一是使用clone()方法，为此，我们需要实现Cloneable接口。

**当我们尝试克隆时，我们应该决定是制作浅拷贝还是深拷贝**。最终，它归结为需求。

例如，如果类只包含[原始类型字段]()和[不可变字段]()，我们可以使用浅拷贝。

如果它包含对可变字段的引用，我们应该进行[深拷贝]()。我们可以使用复制[构造函数]()或[序列化和反序列化]()来做到这一点。 

以前面提到的示例为例，继续了解如何在不使用Cloneable接口的情况下应用Prototype模式。为了做到这一点，我们创建一个名为Tree的抽象类，它有一个抽象方法copy()。

```java
public abstract class Tree {

    // ...
    public abstract Tree copy();
}
```

现在假设我们有两种不同的Tree实现，分别称为PlasticTree和PineTree：

```java
public class PlasticTree extends Tree {

    // ...

    @Override
    public Tree copy() {
        PlasticTree plasticTreeClone = new PlasticTree(this.getMass(), this.getHeight());
        plasticTreeClone.setPosition(this.getPosition());
        return plasticTreeClone;
    }
}
```

```java
public class PineTree extends Tree {
    // ...

    @Override
    public Tree copy() {
        PineTree pineTreeClone = new PineTree(this.getMass(), this.getHeight());
        pineTreeClone.setPosition(this.getPosition());
        return pineTreeClone;
    }
}
```

因此，在这里我们看到扩展Tree并实现copy()方法的类可以充当创建自身副本的原型。

**原型模式还允许我们在不依赖于具体类的情况下创建对象的副本**。假设我们有一个树集合，我们想创建它们的副本，根据多态性，我们可以在不知道树的类型的情况下轻松创建多个副本。

## 5. 测试

现在，我们做一个简单的测试：

```java
class TreePrototypesUnitTest {

    @Test
    void givenAPlasticTreePrototypeWhenClonedThenCreateA_Clone() {
        // ...

        PlasticTree plasticTree = new PlasticTree(mass, height);
        plasticTree.setPosition(position);
        PlasticTree anotherPlasticTree = (PlasticTree) plasticTree.copy();
        anotherPlasticTree.setPosition(otherPosition);

        assertEquals(position, plasticTree.getPosition());
        assertEquals(otherPosition, anotherPlasticTree.getPosition());
    }
}
```

可以看到anotherPlasticTree是从原型中克隆出来的，我们有两个不同的PlasticTree实例，并更新了克隆树中anotherPlasticTree的position属性并保留了其他值。

现在让我们克隆一个树集合：

```java
@Test
void givenA_ListOfTreesWhenClonedThenCreateListOfClones() {

    // create instances of PlasticTree and PineTree

    List<Tree> trees = Arrays.asList(plasticTree, pineTree);
    List<Tree> treeClones = trees.stream().map(Tree::copy).collect(toList());

    // ...

    assertEquals(height, plasticTreeClone.getHeight());
    assertEquals(position, plasticTreeClone.getPosition());
}
```

请注意，我们可以在不依赖于Tree的具体实现的情况下对集合进行深度复制。

## 6. 优点和缺点

当我们的新对象与我们现有的对象仅略有不同时，这种模式很方便。在某些情况下，实例在一个类中可能只有几种状态组合。**因此，我们可以预先创建具有适当状态的实例，然后在需要时克隆它们，而不是创建新实例**。

有时，我们可能会遇到仅在状态不同的子类，我们可以通过创建具有初始状态的原型然后克隆它们来消除这些子类。

原型模式，就像所有其他设计模式一样，应该只在适当的时候使用。由于我们是在克隆对象，当有很多类时，这个过程可能会变得复杂，从而导致混乱。此外，很难克隆具有循环引用的类。

## 7. 总结

在本教程中，我们学习了原型模式的关键概念，并了解了如何在Java中实现它，最后讨论了它的一些优缺点。