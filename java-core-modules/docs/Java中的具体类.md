## **一、简介**

在本快速指南中，**我们将讨论 Java 中的术语“具体类”**。

首先，我们将定义术语。然后，我们将了解它与接口和抽象类有何不同。

## **2.什么是具体类？**

***具体类是我们可以使用new\*关键字创建其实例的类**。

换句话说，它是 **其蓝图的完整实现**。一个具体的类就完成了。

想象一下，例如，一个*Car* 类：

```java
public class Car {
    public String honk() {
        return "beep!";
    }

    public String drive() {
        return "vroom";
    }
}复制
```

因为它的所有方法都已实现，我们称它为具体类，我们可以实例化它：

```java
Car car = new Car();复制
```

JDK 中具体类的一些示例是***HashMap\*、\*HashSet\*、\*ArrayList\*和\*LinkedList\*。**

## **3. Java 抽象与具体类**

**不过，并非所有 Java 类型都实现了它们的所有方法。**这种灵活性，也称为*抽象*，使我们能够以更一般的方式思考我们试图建模的领域。

**在Java中，我们可以使用接口和抽象类来实现抽象。**

让我们通过将它们与其他类进行比较来更好地了解具体类。

### **3.1. 接口**

**接口是类的蓝图**。或者，换句话说，它是一组未实现的方法签名：

```java
interface Driveable {
    void honk();
    void drive();
}复制
```

**请注意，它使用 \*interface\*关键字而不是 \*class。\***

因为 *Driveable* 有未实现的方法，我们不能用 *new* 关键字实例化它。

但是，像***Car\*** **这样的具体类可以实现这些方法。**

JDK 提供了许多接口，如 ***Map\*、\*List\*和\*Set\*。**

### **3.2. 抽象类**

**抽象类是一个具有未实现方法的类，**尽管它实际上可以同时拥有：

```java
public abstract class Vehicle {
    public abstract String honk();

    public String drive() {
        return "zoom";
    }
}复制
```

**请注意，我们使用关键字 \*abstract\*标记抽象类。**

同样，由于*Vehicle*有一个未实现的方法 *honk*，我们将无法使用*new* 关键字。

JDK 抽象类的一些示例是***AbstractMap\*和\*AbstractList\*。**

### **3.3. 具体类**

相反，**具体类没有任何未实现的方法。** 不管实现是否继承，只要每个方法都有实现，类就是具体的。

具体类可以像前面的*Car*示例一样简单。它们还可以实现接口并扩展抽象类：

```java
public class FancyCar extends Vehicle implements Driveable {
    public String honk() { 
        return "beep";
    }
}复制
```

FancyCar类提供了*honk*的实现 ，它继承了Vehicle *的* *drive* 实现 *。*

**因此，它没有未实现的方法**。*因此，我们可以使用new*关键字创建一个*FancyCar*类实例 。

```java
FancyCar car = new FancyCar();复制
```

**或者，简单地说，所有不是抽象的类，我们都可以调用具体类。**

## **4.总结**

在这个简短的教程中，我们了解了具体类及其规范。

此外，我们还展示了接口与具体类和抽象类之间的区别。