## 1. 概述

编译Java类时，会创建一个同名的class文件。但是，在嵌套类或嵌套接口的情况下，它会创建一个class文件，其名称结合了内部类和外部类名称，包括美元符号($)。

在本文中，我们将看到所有这些场景。

## 2. 细节

在Java中，我们可以在一个类中编写一个类。写在里面的类称为嵌套类，持有嵌套类的类称为外部类。嵌套类的作用域受其封闭类的作用域的限制。

同样，我们可以在另一个接口或类中声明一个接口。这样的接口称为嵌套接口。

我们可以使用嵌套类和接口对仅在一个地方使用的实体进行逻辑分组。这不仅使我们的代码更具可读性和可维护性，而且还增加了封装性。

在接下来的部分中，我们将详细讨论其中的每一个。我们还将看看枚举。

## 3. 嵌套类

[嵌套类](https://www.baeldung.com/java-nested-classes)是在另一个类或接口内部声明的类。任何时候我们需要一个单独的类但仍希望该类作为另一个类的一部分时，嵌套类是实现它的最佳方法。

当我们编译Java文件时，它会为封闭类创建一个.class文件，并为所有嵌套类创建单独的类文件。为封闭类生成的类文件将与Java类同名。

对于嵌套类，编译器使用不同的命名约定-OuterClassName$NestedClassName.class

首先，让我们创建一个简单的Java类：

```java
public class Outer {

    // variables and methods ...
}
```

当我们编译Outer类时，编译器会创建一个Outer.class文件。

在接下来的小节中，我们将在Outer类中添加嵌套类并查看类文件是如何命名的。

### 3.1 静态嵌套类

顾名思义，声明为静态的嵌套类称为静态嵌套类。在Java中，只允许嵌套类是静态的。

静态嵌套类可以同时具有静态和非静态字段和方法。它们与外部类相关联，而不是与特定实例相关联。因此，我们不需要外部类的实例来访问它们。

让我们在我们的外部类中声明一个静态嵌套类：

```java
public class Outer {
    static class StaticNested {
        public String message() {
            return "This is a static Nested Class";
        }
    }
}
```

当我们编译Outer类时，编译器会创建两个类文件，一个用于Outer，另一个用于StaticNested：

[![静态嵌套](https://www.baeldung.com/wp-content/uploads/2021/03/staticnested.jpg)](https://www.baeldung.com/wp-content/uploads/2021/03/staticnested.jpg)

### 3.2 非静态嵌套类

非静态嵌套类(也称为内部类)与封闭类的实例相关联，它们可以访问外部类的所有变量和方法。

外部类只能具有公共或默认访问权限，而内部类可以是私有、公共、受保护或具有默认访问权限。但是，它们不能包含任何静态成员。另外，我们需要创建一个外部类的实例来访问内部类。

让我们在我们的外部类中再添加一个嵌套类：

```java
public class Outer {
    class Nested {
        public String message() {
            return "This is a non-static Nested Class";
        }
    }
}
```

它又生成一个类文件：

[![非静态嵌套](https://www.baeldung.com/wp-content/uploads/2021/03/nonstaticnested.jpg)](https://www.baeldung.com/wp-content/uploads/2021/03/nonstaticnested.jpg)

### 3.3 局部类

局部类，也称为内部类，在块中定义——一组平衡括号之间的语句。例如，它们可以在方法体、for循环或if子句中。局部类的范围就像局部变量一样被限制在块内。本地类在编译时显示为带有自动生成数字的美元符号。

为本地类生成的类文件使用命名约定-OuterClassName$1LocalClassName.class

让我们在一个方法中声明一个本地类：

```java
public String message() {
    class Local {
        private String message() {
            return "This is a Local Class within a method";
        }
    }
    Local local = new Local();
    return local.message();
}
```

编译器为我们的本地类创建一个单独的类文件：

[![本地类](https://www.baeldung.com/wp-content/uploads/2021/03/localclass.jpg)](https://www.baeldung.com/wp-content/uploads/2021/03/localclass.jpg)

同样，我们可以在if子句中声明一个本地类：

```java
public String message(String name) {
    if (StringUtils.isEmpty(name)) {
        class Local {
            private String message() {
                return "This is a Local class within if clause";
            }
        }
        Local local = new Local();
        return local.message();
    } else
        return "Welcome to " + name;
}
```

虽然我们正在创建另一个具有相同名称的本地类，但编译器并没有抱怨。它再创建一个类文件并用增加的数字命名它：

[![本地类子句](https://www.baeldung.com/wp-content/uploads/2021/03/localclassinifclause.jpg)](https://www.baeldung.com/wp-content/uploads/2021/03/localclassinifclause.jpg)

### 3.4 匿名内部类

顾名思义，匿名类就是没有名字的内部类。编译器在美元符号后使用自动生成的数字来命名类文件。

我们需要在单个表达式中同时声明和实例化匿名类。它们通常扩展现有类或实现接口。

让我们看一个简单的例子：

```java
public String greet() {
    Outer anonymous = new Outer() {
        @Override
        public String greet() {
            return "Running Anonymous Class...";
        }
    };
    return anonymous.greet();
}
```

在这里，我们通过扩展Outer类创建了一个匿名类，编译器又添加了一个类文件：

[![匿名类](https://www.baeldung.com/wp-content/uploads/2021/03/anonymousclass.jpg)](https://www.baeldung.com/wp-content/uploads/2021/03/anonymousclass.jpg)

同样，我们可以用匿名类实现一个接口。

在这里，我们正在创建一个接口：

```java
interface HelloWorld {
    public String greet(String name);
}
```

现在，让我们创建一个匿名类：

```java
public String greet(String name) {
    HelloWorld helloWorld = new HelloWorld() {
        @Override
        public String greet(String name) {
            return "Welcome to "+name;
        }
    };
    return helloWorld.greet(name);
}
```

让我们观察修改后的类文件列表：

[![匿名2类](https://www.baeldung.com/wp-content/uploads/2021/03/anonymous2class.jpg)](https://www.baeldung.com/wp-content/uploads/2021/03/anonymous2class.jpg)

如我们所见，为接口HelloWorld生成了一个类文件，为名称为Outer$2的匿名类生成了另一个类文件。

### 3.5 接口内的内部类

我们已经看到了另一个类中的类，进一步，我们可以在接口中声明一个类。如果类的功能与接口功能密切相关，我们可以在接口内部声明它。当我们想为接口方法编写默认实现时，我们可以选择这个内部类。

让我们在HelloWorld接口中声明一个内部类：

```java
interface HelloWorld {
    public String greet(String name);
    class InnerClass implements HelloWorld {
        @Override
        public String message(String name) {
            return "Inner class within an interface";
        }
    }
}
```

编译器又生成了一个类文件：

[![内部类](https://www.baeldung.com/wp-content/uploads/2021/03/innerclass.jpg)](https://www.baeldung.com/wp-content/uploads/2021/03/innerclass.jpg)

## 4. 嵌套接口

嵌套接口，也称为内部接口，在类或另一个接口内部声明。使用嵌套接口的主要目的是通过对相关接口进行分组来解析命名空间。

我们不能直接访问嵌套接口。它们只能使用外部类或外部接口访问。例如，Map接口内部的Entry接口是嵌套的，可以作为Map.Entry访问。

让我们看看如何创建嵌套接口。

### 4.1 接口内的接口

在接口内部声明的接口是隐式公开的。

让我们在HelloWorld接口中声明我们的接口：

```java
interface HelloWorld {
    public String greet(String name);
    
    interface HelloSomeone{
        public String greet(String name);
    }
}
```

这将为嵌套接口创建一个名为HelloWorld$HelloSomeone的新类文件。

### 4.2 类中的接口

在类内部声明的接口可以采用任何访问修饰符。

让我们在我们的外部类中声明一个接口：

```java
public class Outer {
     interface HelloOuter {
        public String hello(String name);
    }
}
```

它将生成一个新的类文件，名称为：OuterClass$StaticNestedClass

## 5. 枚举

[枚举](https://www.baeldung.com/a-guide-to-java-enums)是在Java 5中引入的。它是一种包含一组固定常量的数据类型，这些常量是该枚举的实例。

enum声明定义了一个称为enum类型(也称为枚举数据类型)的类。我们可以向枚举中添加很多东西，比如构造函数、方法、变量，以及称为常量特定类主体的东西。

当我们创建一个enum时，我们正在创建一个新类，并且我们正在隐式地扩展Enum类。枚举不能继承任何其他类，也不能扩展。但是，它可以实现一个接口。

我们可以将一个枚举声明为一个独立的类，在它自己的源文件中，或者另一个类成员。让我们看看创建枚举的所有方法。

### 5.1 枚举类

首先，让我们创建一个简单的枚举：

```java
enum Level {
    LOW, MEDIUM, HIGH;
}
```

编译时，编译器将为我们的枚举创建一个名为Level的类文件。

### 5.2 类中的枚举

现在，让我们在Outer类中声明一个嵌套枚举：

```java
public class Outer {
    enum Color{ 
        RED, GREEN, BLUE; 
    }
}
```

编译器将为我们的嵌套枚举创建一个名为Outer$Color的单独类文件。

### 5.3 接口中的枚举

同样，我们可以在接口中声明一个枚举：

```java
interface HelloWorld {
    enum DIRECTIONS {
        NORTH, SOUTH, EAST, WEST;
    }
}
```

编译HelloWorld接口时，编译器会多加一个名为HelloWorld$Direction.class的类文件。

### 5.4 枚举中的枚举

我们可以在另一个枚举中声明一个枚举：

```java
enum Foods {
    DRINKS, EATS;
    enum DRINKS {
        APPLE_JUICE, COLA;
    }
    enum EATS {
        POTATO, RICE;
    }
}
```

最后我们看一下生成的类文件：

[![巢穴](https://www.baeldung.com/wp-content/uploads/2021/03/Nestedenum.jpg)](https://www.baeldung.com/wp-content/uploads/2021/03/Nestedenum.jpg)

编译器为每个枚举类型创建一个单独的类文件。

## 6. 总结

在本文中，我们看到了用于Java类文件的不同命名约定。我们在单个Java文件中添加了类、接口和枚举，并观察了编译器如何为它们中的每一个创建单独的类文件。