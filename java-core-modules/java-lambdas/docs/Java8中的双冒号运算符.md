## 1. 概述

在这篇简短的文章中，我们将讨论Java 8中的双冒号运算符(::)并介绍可以使用该运算符的场景。

## 2. 从Lambdas到双冒号运算符

使用Lambdas表达式，我们知道代码可以变得非常简洁。例如，要创建一个比较器，以下语法就足够了：

```java
Comparator c = (Computer c1, Computer c2) -> c1.getAge().compareTo(c2.getAge());

```

然后，使用类型推断：

```java
Comparator c = (c1, c2) -> c1.getAge().compareTo(c2.getAge());
```

但是我们可以让上面的代码更具表现力和可读性吗？我们来看一下：

```java
Comparator c = Comparator.comparing(Computer::getAge);
```

我们使用::运算符作为lambda调用特定方法getAge的简写形式。最后，结果当然是更具可读性的语法。

## 3. 它是如何工作的？

非常简单地说，当我们使用方法引用时，目标引用放在分隔符::之前，方法名称在它之后提供。

例如：

```java
Computer::getAge;
```

然后我们可以使用该函数进行操作：

```java
Function<Computer, Integer> getAge = Computer::getAge;
Integer computerAge = getAge.apply(c1);
```

请注意，我们引用该函数，然后将其应用于正确类型的参数。

## 4. 方法引用

我们可以在相当多的场景中很好地利用这个运算符。

### 4.1 静态方法

首先，我们引用静态工具方法

```java
List inventory = Arrays.asList(new Computer( 2015, "white", 35), new Computer(2009, "black", 65));
inventory.forEach(ComputerUtils::repair);
```

### 4.2 现有对象的实例方法

接下来，让我们看一个有趣的场景-引用现有对象实例的方法。

我们将使用变量System.out-支持print方法的PrintStream类型的对象：

```java
Computer c1 = new Computer(2015, "white");
Computer c2 = new Computer(2009, "black");
Computer c3 = new Computer(2014, "black");
Arrays.asList(c1, c2, c3).forEach(System.out::print);
```

### 4.3 特定类型的任意对象的实例方法

```java
Computer c1 = new Computer(2015, "white", 100);
Computer c2 = new MacbookPro(2009, "black", 100);
List inventory = Arrays.asList(c1, c2);
inventory.forEach(Computer::turnOnPc);
```

如你所见，我们不是在特定实例上引用turnOnPc方法，而是在类型本身上引用。

在第4行，将为inventory的每个对象调用实例方法turnOnPc。

这自然意味着-对于c1，方法turnOnPc将在Computer实例上调用，而对于c2，将在MacbookPro实例上调用。

### 4.4 特定对象的父类方法

假设你在Computer父类中有以下方法：

```java
public Double calculateValue(Double initialValue) {
    return initialValue/1.50;
}
```

这是MacbookPro子类中的重写版本：

```java
@Override
public Double calculateValue(Double initialValue){
    Function<Double, Double> function = super::calculateValue;
    Double pcValue = function.apply(initialValue);
    return pcValue + (initialValue/10) ;
}
```

在MacbookPro实例上调用calculateValue方法：

```java
macbookPro.calculateValue(999.99);
```

还将产生对Computer超类上的calculateValue的调用。

## 5. 构造函数引用

### 5.1 创建一个新实例

引用构造函数来实例化对象可能非常简单：

```java
@FunctionalInterface
public interface InterfaceComputer {
    Computer create();
}

InterfaceComputer c = Computer::new;
Computer computer = c.create();
```

如果构造函数中有两个参数怎么办？

```java
BiFunction<Integer, String, Computer> c4Function = Computer::new; 
Computer c4 = c4Function.apply(2013, "white");
```

如果参数为三个或更多，则必须定义一个新的函数式接口：

```java
@FunctionalInterface 
interface TriFunction<A, B, C, R> { 
    R apply(A a, B b, C c); 
    default <V> TriFunction<A, B, C, V> andThen( Function<? super R, ? extends V> after) { 
        Objects.requireNonNull(after); 
        return (A a, B b, C c) -> after.apply(apply(a, b, c)); 
    } 
}
```

然后，初始化你的对象：

```java
TriFunction<Integer, String, Integer, Computer> c6Function = Computer::new;
Computer c3 = c6Function.apply(2008, "black", 90);
```

### 5.2 创建数组

最后，让我们看看如何创建一个包含五个元素的Computer对象数组：

```java
Function<Integer, Computer[]> computerCreator = Computer[]::new;
Computer[] computerArray = computerCreator.apply(5);
```

## 6. 总结

正如我们开始看到的那样，Java 8中引入的双冒号运算符在某些情况下非常有用，尤其是与Streams结合使用时。

查看函数式接口以更好地了解幕后发生的事情也很重要。