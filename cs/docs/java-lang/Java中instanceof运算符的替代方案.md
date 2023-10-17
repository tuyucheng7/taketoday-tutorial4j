## 1. 概述

[instanceof](https://www.baeldung.com/java-instanceof)是一个运算符，用于将对象的实例与类型进行比较。它也称为类型比较运算符。在本教程中，我们将研究传统instanceof方法的不同替代方法。我们可能需要替代方案来改进代码设计和可读性。

## 2. 示例设置

让我们开发一个简单的程序DinosaurandSpecies。该程序将有一个父类和两个子类，即子类将扩展父类。首先，让我们创建父类：

```java
public class Dinosaur {
}
```

接下来，让我们创建第一个子类：

```java
public class Anatotitan extends Dinosaur {
    String run() {
        return "running";
    }
}
```

最后，让我们创建第二个子类：

```java
public class Euraptor extends Dinosaur {	
    String flies() {
        return "flying";
    }
}
```

Dinosaur类具有子类共有的其他方法，但为简单起见，我们将跳过它们。接下来，让我们编写一个方法来创建对象的新实例并调用它们的移动。在返回结果之前，我们将使用instanceof来检查我们的新实例类型：

```java
public static void moveDinosaur(Dinosaur dinosaur) {
    if (dinosaur instanceof Anatotitan) {
        Anatotitan anatotitan = (Anatotitan) dinosaur;
        anatotitan.run();
    } 
    else if (dinosaur instanceof Euraptor) {
        Euraptor euraptor = (Euraptor) dinosaur;
        euraptor.flies();
    }
}
```

在接下来的部分中，我们将应用不同的替代方案。

## 3. 使用getClass()

[getClass()](https://www.baeldung.com/java-finding-class)方法有助于获取对象的类。在检查对象是否属于特定类时，我们可以使用getClass()作为instanceof的替代方法。在我们的示例设置中，让我们维护父类和子类的结构。然后，让我们为这种方法编写一个测试方法。我们将使用getClass()而不是instanceof：

```java
public static String moveDinosaurUsingGetClass(Dinosaur dinosaur) {
    if (dinosaur.getClass().equals(Anatotitan.class)) {
        Anatotitan anatotitan = (Anatotitan) dinosaur;
        return anatotitan.run();
    } else if (dinosaur.getClass().equals(Euraptor.class)) {
        Euraptor euraptor = (Euraptor) dinosaur;
        return euraptor.flies();
    }
    return "";
}
```

让我们继续为这种方法编写单元测试：

```java
@Test
public void givenADinosaurSpecie_whenUsingGetClass_thenGetMovementOfEuraptor() {
    assertEquals("flying", moveDinosaurUsingGetClass(new Euraptor()));
}
```

这个替代方案维护了我们原来的域对象。什么变化是使用getClass()。

## 4. 使用多态

[多态性](https://www.baeldung.com/java-polymorphism)的概念使子类覆盖父类的方法。我们可以使用这个概念来更改我们的示例设置并改进我们的代码设计和可读性。由于我们知道所有恐龙的移动方式，我们可以通过在父类中引入move()方法来更改我们的设计：

```java
public class Dinosaur {	
    String move() {
        return "walking";
    } 
}
```

接下来，让我们通过覆盖move()方法来修改我们的子类：

```java
public class Anatotitan extends Dinosaur {
    @Override
    String move() {
        return "running";
    }
}

public class Euraptor extends Dinosaur {
    @Override
    String move() {
        return "flying";
    }
}
```

现在我们可以在不使用instanceof方法的情况下引用子类。让我们编写一个接受我们的父类作为参数的方法。我们将根据其种类返回我们的恐龙运动：

```java
public static String moveDinosaurUsingPolymorphism(Dinosaur dinosaur) { 
    return dinosaur.move(); 
}
```

让我们为这种方法编写一个单元测试：

```java
@Test 
public void givenADinosaurSpecie_whenUsingPolymorphism_thenGetMovementOfAnatotitan() { 
    assertEquals("running", moveDinosaurUsingPolymorphism(new Anatotitan()));
}
```

如果可能，建议使用此方法更改我们的设计本身。instanceof的使用通常表明我们的设计违反了[Liskov替换原则(LSP)](https://www.baeldung.com/java-liskov-substitution-principle)。

## 5. 使用枚举

在[枚举](https://www.baeldung.com/a-guide-to-java-enums)类型中，变量可以定义为预定义常量的集合。我们可以使用这种方法来改进我们的简单程序。首先，让我们创建一个带有常量和方法的枚举。常量的方法覆盖枚举中的抽象方法：

```java
public enum DinosaurEnum {
    Anatotitan {
        @Override
        public String move() {
            return "running";
        }
    },
    Euraptor {
        @Override
        public String move() {
            return "flying";
        }
    };
    abstract String move();
}
```

枚举常量的行为类似于其他替代方案中使用的子类。接下来，让我们修改moveDinosaur()方法以使用enum类型：

```java
public static String moveDinosaurUsingEnum(DinosaurEnum dinosaurEnum) {
    return dinosaurEnum.move();
}
```

最后，让我们为这种方法编写一个单元测试：

```java
@Test
public void givenADinosaurSpecie_whenUsingEnum_thenGetMovementOfEuraptor() {
    assertEquals("flying", moveDinosaurUsingEnum(DinosaurEnum.Euraptor));
}
```

这种设计让我们去掉了父类和子类。在父类的行为比我们的示例设置更多的复杂场景中，不建议使用此方法。

## 6. 使用访问者模式

[访问者模式](https://www.baeldung.com/java-visitor-pattern)有助于对相似/相关的对象进行操作。它将逻辑从对象类移动到另一个类。让我们将这种方法应用到我们的示例设置中。首先，让我们创建一个带有方法的接口，并将Visitor作为参数传递。这将有助于检索我们对象的类型：

```java
public interface Dinosaur {
    String move(Visitor visitor);
}
```

接下来，让我们使用两个方法创建一个Visitor界面。这些方法接受我们的子类作为参数：

```java
public interface Visitor {
    String visit(Anatotitan anatotitan);
    String visit(Euraptor euraptor);
}
```

接下来，让我们的子类实现Dinosaur接口并覆盖它的方法。该方法将Visitor作为参数来检索我们的对象类型。此方法取代了instanceof的使用：

```java
public class Anatotitan implements Dinosaur {
    public String run() {
        return "running";
    }
    @Override
    public String move(Visitor dinoMove) {
        return dinoMove.visit(this);
    }
}
```

接下来，让我们创建一个类来实现我们的Visitor接口并重写方法：

```java
public class DinoVisitorImpl implements Visitor {
    @Override
    public String visit(Anatotitan anatotitan) {
        return anatotitan.run();
    }
    @Override
    public String visit(Euraptor euraptor) {
        return euraptor.flies();
    }
}
```

最后，让我们为这种方法编写一个测试方法：

```java
public static String moveDinosaurUsingVisitorPattern(Dinosaur dinosaur) {
    Visitor visitor = new DinoVisitorImpl();
    return dinosaur.move(visitor);
}
```

让我们为这种方法编写一个单元测试：

```java
@Test
public void givenADinosaurSpecie_whenUsingVisitorPattern_thenGetMovementOfAnatotitan() {
    assertEquals("running", moveDinosaurUsingVisitorPattern(new Anatotitan()));
}
```

这种方法使用了一个接口。Visitor包含我们的程序逻辑。

## 7. 总结

在本文中，我们研究了不同的instanceof替代方案。instanceof方法可能违反Liskov替换原则。采用替代方案为我们提供了更好、更可靠的设计。建议使用多态性方法，因为它增加了更多价值。