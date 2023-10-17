## 1. 概述

在本快速教程中，我们将了解Java中的标记接口。

## 2. 标记接口

**标记接口是其中没有任何方法或常量的[接口](https://www.baeldung.com/java-interfaces)**。它提供**有关对象的运行时类型信息**，因此编译器和JVM具有有关该对象的附加信息。

标记接口也称为标记接口。

虽然标记接口仍在使用，但它们很可能指向代码味道，我们应该谨慎使用它们。这样做的主要原因是它们模糊了接口所代表的界限，因为标记不定义任何行为。较新的开发有利于注解来解决一些相同的问题。

## 3. JDK标记接口

Java有许多内置的标记接口，例如Serializable、Cloneable和Remote。

让我们以[Cloneable](https://www.baeldung.com/java-deep-copy)接口为例。如果我们尝试克隆一个没有实现此接口的对象，JVM会抛出CloneNotSupportedException。因此，Cloneable标记接口是JVM的指示器，我们可以调用Object.clone()方法。

同理，在调用ObjectOutputStream.writeObject()方法时，JVM会检查对象是否实现了Serializable标记接口。如果不是这种情况，则会抛出NotSerializableException。因此，该对象不会序列化到输出流。

## 4. 自定义标记接口

让我们创建自己的标记接口。

例如，我们可以创建一个标记接口，指示是否可以从数据库中删除一个对象：

```java
public interface Deletable {
}
```

为了从数据库中删除一个实体，表示该实体的对象必须实现我们的Deletable标记接口：

```java
public class Entity implements Deletable {
    // implementation details
}
```

假设我们有一个DAO对象，它具有从数据库中删除实体的方法。我们可以编写delete()方法，以便只能删除实现标记接口的对象：

```java
public class ShapeDao {

    // other dao methods

    public boolean delete(Object object) {
        if (!(object instanceof Deletable)) {
            return false;
        }

        // delete implementation details
        
        return true;
    }
}
```

如我们所见，我们向JVM提供了有关对象运行时行为的指示。 如果对象实现了我们的标记接口，就可以从数据库中删除它。

## 5. 标记接口与注解

通过引入注解，Java为我们提供了一种替代方法来实现与标记接口相同的结果。而且，像标记接口一样，我们可以将注解应用到任何类，我们可以将它们用作执行某些操作的指示符。

那么关键的区别是什么？

与注解不同，接口允许我们利用多态性。因此，我们可以 向标记接口添加额外的限制。

例如，让我们添加一个限制，即只能从数据库中删除一个Shape类型：

```java
public interface Shape {
    double getArea();
    double getCircumference();
}
```

在这种情况下，我们的标记接口DeletableShape将如下所示：

```java
public interface DeletableShape extends Shape {
}
```

然后我们的类将实现标记接口：

```java
public class Rectangle implements DeletableShape {
    // implementation details
}
```

因此，所有 DeletableShape实现也是Shape实现。显然，我们不能使用注解来做到这一点。

然而，每个设计决策都有权衡取舍，我们可以使用多态性来反驳标记接口。在我们的示例中，每个扩展Rectangle的类都将自动实现DeletableShape。

## 6. 标记接口与典型接口

在前面的示例中，我们可以通过修改DAO的delete()方法来测试我们的对象是否是Shape而不是测试它是否是Deletable来获得相同的结果：

```java
public class ShapeDao { 

    // other dao methods 
    
    public boolean delete(Object object) {
        if (!(object instanceof Shape)) {
            return false;
        }
    
        // delete implementation details
        
        return true;
    }
}
```

那么，当我们使用典型接口可以达到相同的结果时，为什么还要创建标记接口呢？

让我们想象一下，除了Shape类型之外，我们还想从数据库中删除Person类型。在这种情况下，有两种选择可以实现这一目标。

第一个选项是 在我们之前的delete()方法中添加一个额外的检查来验证要删除的对象是否是Person的实例：

```java
public boolean delete(Object object) {
    if (!(object instanceof Shape || object instanceof Person)) {
        return false;
    }
    
    // delete implementation details
        
    return true;
}
```

但是，如果我们还想从数据库中删除更多类型怎么办？显然，这不是一个好的选择，因为我们必须为每个新类型更改我们的方法。

第二个选项是 让Person类型实现Shape接口，它充当标记接口。但是Person对象真的是Shape吗？答案显然是否定的，这使得第二种选择比第一种更糟糕。

因此，虽然我们可以通过使用典型接口作为标记来获得相同的结果，但我们最终会得到一个糟糕的设计。

## 7. 总结

在本文中，我们了解了标记接口以及如何使用它们。然后我们探讨了这种接口的一些内置Java示例，以及JDK如何使用它们。

接下来，我们创建了自己的标记接口，并权衡了使用注解的效果。最后，我们演示了为什么在某些场景中使用标记接口而不是传统接口是一种很好的做法。