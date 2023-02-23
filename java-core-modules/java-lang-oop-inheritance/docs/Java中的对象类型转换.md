## 1. 概述

Java类型系统由两种类型组成：原始类型和引用。

我们在[本文](https://www.baeldung.com/java-primitive-conversions)中介绍了原始类型转换，我们将在这里重点介绍引用转换，以便更好地理解Java如何处理类型。

### 延伸阅读

### [Java泛型基础](https://www.baeldung.com/java-generics)

快速介绍Java泛型的基础知识。

[阅读更多](https://www.baeldung.com/java-generics)→

### [Java instanceof运算符](https://www.baeldung.com/java-instanceof)

了解Java中的instanceof运算符

[阅读更多](https://www.baeldung.com/java-instanceof)→

## 2. 原始与引用类型

尽管原始类型转换和引用变量转换看起来很相似，但它们是完全[不同的概念](https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.1)。

在这两种情况下，我们都是将一种类型“转变”为另一种类型。但是，以一种简化的方式，原始变量包含它的值，原始变量的转换意味着它的值发生不可逆的变化：

```java
double myDouble = 1.1;
int myInt = (int) myDouble;
        
assertNotEquals(myDouble, myInt);
```

上例转换后，myInt变量为1，我们无法从中恢复原来的值1.1。

**引用变量不同**；引用变量只引用一个对象，但不包含对象本身。

转换引用变量不会触及它所引用的对象，而只会以另一种方式标记该对象，从而扩大或缩小使用它的机会。**向上转型缩小了该对象可用的方法和属性的列表，而向下转型可以扩展它**。

引用就像一个对象的遥控器。遥控器的按钮或多或少取决于其类型，对象本身存储在堆中。当我们进行转换时，我们改变了遥控器的类型但不改变对象本身。

## 3. 向上转型

**从子类到超类的转换称为向上转换**。通常，向上转换由编译器隐式执行。

向上转型与继承密切相关-继承是Java中的另一个核心概念。通常使用引用变量来引用更具体的类型。每次我们这样做时，都会发生隐式向上转换。

为了演示向上转型，让我们定义一个Animal类：

```java
public class Animal {

    public void eat() {
        // ... 
    }
}
```

现在让我们扩展Animal：

```java
public class Cat extends Animal {

    public void eat() {
        // ... 
    }

    public void meow() {
        // ... 
    }
}
```

现在我们可以创建一个Cat类的对象并将其分配给Cat类型的引用变量：

```java
Cat cat = new Cat();
```

我们也可以将它赋值给Animal类型的引用变量：

```java
Animal animal = cat;
```

在上面的赋值中，发生了隐式向上转型。

我们可以明确地做到这一点：

```java
animal = (Animal) cat;
```

但是不需要显式地转换继承树。编译器知道cat是Animal并且不会显示任何错误。

请注意，引用可以引用声明类型的任何子类型。

使用向上转换，我们限制了Cat实例可用的方法数量，但没有更改实例本身。现在我们不能做任何特定于Cat的事情-我们不能在animal变量上调用meow()。

虽然Cat对象仍然是Cat对象，但调用meow()会导致编译器错误：

```java
// animal.meow(); The method meow() is undefined for the type Animal
```

要调用meow()，我们需要向下转型animal，我们稍后会这样做。

但现在我们将描述是什么让我们进行了向上转换。由于向上转型，我们可以利用多态性。

### 3.1 多态性

让我们定义Animal的另一个子类，一个Dog类：

```java
public class Dog extends Animal {

    public void eat() {
        // ... 
    }
}
```

现在我们可以定义feed()方法，它像对待动物一样对待所有的猫狗：

```java
public class AnimalFeeder {

    public void feed(List<Animal> animals) {
        animals.forEach(animal -> {
            animal.eat();
        });
    }
}
```

我们不希望AnimalFeeder关心列表中的动物是Cat还是Dog。在feed()方法中，它们都是animals。

当我们将特定类型的对象添加到animals列表时，会发生隐式向上转型：

```java
List<Animal> animals = new ArrayList<>();
animals.add(new Cat());
animals.add(new Dog());
new AnimalFeeder().feed(animals);
```

我们添加了猫和狗，并将它们隐式转换为Animal类型。每个Cat都是Animal，每个Dog都是Animal。它们是多态的。

顺便说一句，所有Java对象都是多态的，因为每个对象至少是一个Object。我们可以将Animal的实例分配给Object类型的引用变量，编译器不会报错：

```java
Object object = new Animal();
```

这就是为什么我们创建的所有Java对象都已经具有特定于Object的方法，例如toString()。

向上转换为接口也很常见。

我们可以创建Mew接口并让Cat实现它：

```java
public interface Mew {
    void meow();
}

public class Cat extends Animal implements Mew {

    public void eat() {
        // ... 
    }

    public void meow() {
        // ... 
    }
}
```

现在任何Cat对象也可以向上转换为Mew：

```java
Mew mew = new Cat();
```

猫是Mew；向上转换是合法的并且是隐式完成的。

因此，Cat是Mew、Animal、Object和Cat。在我们的示例中，它可以分配给所有四种类型的引用变量。

### 3.2 覆盖

在上面的示例中，eat()方法被覆盖了。这意味着虽然eat()是在Animal类型的变量上调用的，但工作是由在真实对象(猫和狗)上调用的方法完成的：

```java
public void feed(List<Animal> animals) {
    animals.forEach(animal -> {
        animal.eat();
    });
}
```

如果我们向我们的类添加一些日志记录，我们将看到调用了Cat和Dog方法：

```bash
web - 2018-02-15 22:48:49,354 [main] INFO cn.tuyucheng.taketoday.casting.Cat - cat is eating
web - 2018-02-15 22:48:49,363 [main] INFO cn.tuyucheng.taketoday.casting.Dog - dog is eating
```

总结一下：

-   如果对象与变量属于同一类型或者是子类型，则引用变量可以引用对象
-   向上转型是隐式发生的
-   所有Java对象都是多态的，并且由于向上转换可以被视为超类型的对象

## 4. 向下转型

如果我们想使用Animal类型的变量来调用仅适用于Cat类的方法怎么办？向下转型来了。**这是从超类到子类的转换**。

让我们看一个例子：

```java
Animal animal = new Cat();
```

我们知道animal变量是指Cat的实例。我们想在animal上调用Cat的meow()方法，但是编译器抱怨类型Animal不存在meow()方法。

要调用meow()，我们应该将animal向下转换为Cat：

```java
((Cat) animal).meow();
```

内括号及其包含的类型有时称为强制转换运算符。请注意，编译代码还需要外部括号。

让我们用meow()方法重写前面的AnimalFeeder示例：

```java
public class AnimalFeeder {

    public void feed(List<Animal> animals) {
        animals.forEach(animal -> {
            animal.eat();
            if (animal instanceof Cat) {
                ((Cat) animal).meow();
            }
        });
    }
}
```

现在我们可以访问Cat类可用的所有方法。查看日志以确保meow()确实被调用：

```shell
web - 2018-02-16 18:13:45,445 [main] INFO cn.tuyucheng.taketoday.casting.Cat - cat is eating
web - 2018-02-16 18:13:45,454 [main] INFO cn.tuyucheng.taketoday.casting.Cat - meow
web - 2018-02-16 18:13:45,455 [main] INFO cn.tuyucheng.taketoday.casting.Dog - dog is eating
```

请注意，在上面的示例中，我们试图仅向下转型那些实际上是Cat实例的对象。为此，我们使用运算符instanceof。

### 4.1 instanceof运算符

我们经常在向下转型之前使用instanceof运算符来检查对象是否属于特定类型：

```java
if (animal instanceof Cat) {
    ((Cat) animal).meow();
}
```

### 4.2 类转换异常

如果我们没有使用instanceof运算符检查类型，编译器不会抱怨。但是在运行时，会出现异常。

为了证明这一点，让我们从上面的代码中删除instanceof运算符：

```java
public void uncheckedFeed(List<Animal> animals) {
    animals.forEach(animal -> {
        animal.eat();
        ((Cat) animal).meow();
    });
}
```

此代码编译没有问题。但是如果我们尝试运行它，我们会看到一个异常：

```shell
java.lang.ClassCastException: cn.tuyucheng.taketoday.casting.Dog cannot be cast to cn.tuyucheng.taketoday.casting.Cat
```

这意味着我们正在尝试将作为Dog实例的对象转换为Cat实例。

如果我们向下转换为的类型与真实对象的类型不匹配，则总是会在运行时抛出ClassCastException。

请注意，如果我们尝试向下转换为不相关的类型，编译器将不允许这样做：

```java
Animal animal;
String s = (String) animal;
```

编译器说“Cannot cast from Animal to String”。

对于要编译的代码，这两种类型应该在同一继承树中。

让我们总结一下：

-   必须进行向下转换才能访问特定于子类的成员
-   向下转换是使用转换运算符完成的
-   为了安全地向下转换对象，我们需要instanceof运算符
-   如果真实对象与我们向下转型的类型不匹配，那么ClassCastException将在运行时抛出

## 5. cast()方法

还有另一种使用Class方法强制转换对象的方法：

```java
public void whenDowncastToCatWithCastMethod_thenMeowIsCalled() {
    Animal animal = new Cat();
    if (Cat.class.isInstance(animal)) {
        Cat cat = Cat.class.cast(animal);
        cat.meow();
    }
}
```

在上面的示例中，使用了cast()和isInstance()方法，而不是相应地使用cast和instanceof运算符。

对泛型类型使用cast()和isInstance()方法是很常见的。

让我们用feed()方法创建AnimalFeederGeneric<T\>类，根据类型参数的值，它只“喂养”一种动物，猫或狗：

```java
public class AnimalFeederGeneric<T> {
    private Class<T> type;

    public AnimalFeederGeneric(Class<T> type) {
        this.type = type;
    }

    public List<T> feed(List<Animal> animals) {
        List<T> list = new ArrayList<T>();
        animals.forEach(animal -> {
            if (type.isInstance(animal)) {
                T objAsType = type.cast(animal);
                list.add(objAsType);
            }
        });
        return list;
    }
}
```

feed()方法检查每个动物并仅返回那些是T实例的动物。

请注意，Class实例也应传递给泛型类，因为我们无法从类型参数T中获取它。在我们的例子中，我们在构造函数中传递它。

让我们使T等于Cat并确保该方法只返回猫：

```java
@Test
public void whenParameterCat_thenOnlyCatsFed() {
    List<Animal> animals = new ArrayList<>();
    animals.add(new Cat());
    animals.add(new Dog());
    AnimalFeederGeneric<Cat> catFeeder
      = new AnimalFeederGeneric<Cat>(Cat.class);
    List<Cat> fedAnimals = catFeeder.feed(animals);

    assertTrue(fedAnimals.size() == 1);
    assertTrue(fedAnimals.get(0) instanceof Cat);
}
```

## 6. 总结

在本基础教程中，我们探讨了向上转型、向下转型、如何使用它们以及这些概念如何帮助你利用多态性。