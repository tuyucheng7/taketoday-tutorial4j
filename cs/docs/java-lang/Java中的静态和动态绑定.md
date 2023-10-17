## 1. 概述

[多态性](https://www.baeldung.com/java-polymorphism)允许一个对象采用多种形式-当一个方法表现出多态性时，编译器必须将方法的名称映射到最终的实现。

**如果它是在编译时映射的，那么它就是静态或早期绑定**。

**如果它在运行时解析，则称为动态绑定或后期绑定**。

## 2. 通过代码理解

当子类扩展超类时，它可以重新实现超类中定义的方法。这称为方法覆盖。

例如，让我们创建一个超类Animal：

```java
public class Animal {

    static Logger logger = LoggerFactory.getLogger(Animal.class);

    public void makeNoise() {
        logger.info("generic animal noise");
    }

    public void makeNoise(Integer repetitions) {
        while (repetitions != 0) {
            logger.info("generic animal noise countdown " + repetitions);
            repetitions -= 1;
        }
    }
}
```

还有一个子类Dog：

```java
public class Dog extends Animal {

    static Logger logger = LoggerFactory.getLogger(Dog.class);

    @Override
    public void makeNoise() {
        logger.info("woof woof!");
    }

}
```

在重载方法时，例如Animal类的makeNoise()，编译器将在编译时解析该方法及其代码。**这是静态绑定的示例**。

但是，如果我们将Dog类型的对象分配给Animal类型的引用，编译器将在运行时解析函数代码映射。这就是动态绑定。

为了理解这是如何工作的，让我们编写一个小代码片段来调用类及其方法：

```java
Animal animal = new Animal();

// calling methods of animal object
animal.makeNoise();
animal.makeNoise(3);

// assigning a dog object to reference of type Animal
Animal dogAnimal = new Dog();

dogAnimal.makeNoise();
```

上述代码的输出将是:

```shell
cn.tuyucheng.taketoday.binding.Animal - generic animal noise 
cn.tuyucheng.taketoday.binding.Animal - generic animal noise countdown 3
cn.tuyucheng.taketoday.binding.Animal - generic animal noise countdown 2
cn.tuyucheng.taketoday.binding.Animal - generic animal noise countdown 1
cn.tuyucheng.taketoday.binding.Dog - woof woof!
```

现在，让我们创建一个类：

```java
class AnimalActivity {

    public static void eat(Animal animal) {
        System.out.println("Animal is eating");
    }

    public static void eat(Dog dog) {
        System.out.println("Dog is eating");
    }
}
```

让我们将这些行添加到主类中：

```java
AnimalActivity.eat(dogAnimal);
```

输出将是：

```shell
cn.tuyucheng.taketoday.binding.AnimalActivity - Animal is eating
```

**此示例显示静态函数经历静态绑定**。

原因是子类不能覆盖静态方法。如果子类实现了相同的方法，它就会隐藏超类的方法。**同样，如果方法是final或private，JVM将进行静态绑定**。

静态绑定方法不与特定对象相关联，而是在Type(Java中的类)上调用。这种方法的执行速度稍微快一些。

默认情况下，任何其他方法都会自动成为Java中的虚方法。JVM在运行时解析这些方法，这就是动态绑定。

确切的实现取决于JVM，但它会采用类似C++的方法，其中JVM查找虚拟表以决定调用哪个对象的方法。

## 3. 总结

绑定是实现多态性的语言不可或缺的一部分，了解静态和动态绑定的含义以确保我们的应用程序按照我们希望的方式运行非常重要。

但是，有了这种理解，我们就能够有效地使用类继承和方法重载。