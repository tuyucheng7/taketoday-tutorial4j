## 一、概述

在本教程中，我们将了解为什么要为 Java 中的类使用[私有构造函数](https://www.baeldung.com/java-private-constructors)以及如何使用它。

## 2. 为什么要使用私有构造函数？

*在 Java 中，我们可以使用private*访问说明符将构造函数声明为私有的。**如果一个构造函数被声明为私有的，我们就不能创建类的对象，除非在类内。**

**当我们想要限制类的对象被实例化的方式时，使用私有构造**函数。例如，如果我们只想使用可以创建这些对象的工厂类来创建对象，我们可能会这样做。或者另一种情况是当我们只想拥有该类的一个对象实例时。

私有构造函数最常用的一些案例是[Singleton](https://www.baeldung.com/java-singleton)、[Builder](https://www.baeldung.com/java-creational-design-patterns#builder)和[Factory](https://www.baeldung.com/java-factory-pattern)，即[创建型设计模式](https://www.baeldung.com/creational-design-patterns)。

现在，我们可以想象这些模式的任意组合，因为它们可以很好地融合并实现健壮的代码库。

## 3.访问私有构造函数

通常，为了调用私有构造函数，上面列出的用例还有其他公共方法可以在类中调用私有构造函数。

或者，**我们可以使用[Java Reflection API](https://www.baeldung.com/java-reflection)直接访问私有构造函数**。

Java Reflection API 是一项高级功能，允许程序检查和修改在 JVM 中运行的应用程序的运行时行为。因此，**不推荐使用此方法，因为它会导致难以发现和修复错误**。

使用反射，我们可以看到任何类的方法和属性，并绕过访问修饰符修改或访问它们。

**使用反射最常用的案例是对具有私有方法的类进行单元**测试。要使用反射对私有构造函数或方法进行单元测试，我们需要执行以下步骤：

-   获取我们要实例化的类的类对象
-   有了class对象，调用*getDeclaredContructor()*方法得到*Constructor*对象
-   在*Constructor 对象*上，调用*setAccessible()*方法并使构造函数可访问
-   在*Constructor*对象可访问之后，我们可以调用*newInstance()*方法来创建该类的新对象

让我们创建一个带有私有构造函数的类。然后我们将使用 Java Reflection API 来实例化它并确保私有构造函数被调用：

```java
private PrivateConstructorClass() {
    System.out.println("Used the private constructor!");
}复制
```

让我们添加一个单元测试，在其中我们使用私有构造函数实例化此类：

```java
@Test
public void whenConstructorIsPrivate_thenInstanceSuccess() throws Exception {
    Constructor<PrivateConstructorClass> pcc = PrivateConstructorClass.class.getDeclaredConstructor();
    pcc.setAccessible(true);
    PrivateConstructorClass privateConstructorInstance = pcc.newInstance();
    Assertions.assertTrue(privateConstructorInstance instanceof PrivateConstructorClass);
}复制
```

在控制台输出中，我们应该看到调用了私有构造函数并且构造函数内的打印显示了消息。**尽管有私有访问修饰符，我们现在可以调用私有构造函数并实例化新对象。**

## 4。结论

在本文中，我们了解了为什么要使用私有构造函数以及几种不同的使用方法。我们还了解到，我们可以创建公共方法来访问私有构造函数，或者使用高级 Java 反射 API 来获得更高级的方法。