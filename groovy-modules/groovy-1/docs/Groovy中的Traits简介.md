## 一、概述

在本教程中，我们将探索[Groovy](https://www.baeldung.com/groovy-language)中特征的概念。它们是在Groovy2.3 版本中引入的。

## 2. 什么是特质？

特征是可重用的组件，代表一组我们可以用来扩展多个类的功能的方法或行为。

出于这个原因，它们被视为接口，携带默认实现和状态。所有特征都使用trait关键字定义。

## 三、方法

在特征中声明方法类似于在类中声明任何常规方法。但是，我们不能在trait中声明 protected 或 package-private 方法。

让我们看看公有和私有方法是如何实现的。

### 3.1. 公共方法

首先，我们将探索如何在特征中实现公共方法。

让我们创建一个名为UserTrait的特征和一个公共的sayHello方法：

```groovy
trait UserTrait {
    String sayHello() {
        return "Hello!"
    }
}
```

之后，我们将创建一个实现 UserTrait的Employee类：

```groovy
class Employee implements UserTrait {}
```

现在，让我们创建一个测试来验证Employee实例是否可以访问UserTrait的 sayHello方法：

```groovy
def 'Should return msg string when using Employee.sayHello method provided by User trait' () {
    when:
        def msg = employee.sayHello()
    then:
        msg
        msg instanceof String
        assert msg == "Hello!"
}
```

### 3.2. 私有方法

我们也可以在trait中创建一个私有方法，然后在另一个公共方法中引用它。

我们看UserTrait中的代码实现：

```groovy
private String greetingMessage() {
    return 'Hello, from a private method!'
}
    
String greet() {
    def msg = greetingMessage()
    println msg
    return msg
}

```

请注意，如果我们访问实现类中的私有方法，它将抛出 MissingMethodException：

```groovy
def 'Should return MissingMethodException when using Employee.greetingMessage method' () {
    when:
        def exception
        try {
            employee.greetingMessage()
        } catch(Exception e) {
            exception = e
        }
        
    then:
        exception
        exception instanceof groovy.lang.MissingMethodException
        assert exception.message == "No signature of method: com.baeldung.traits.Employee.greetingMessage()"
          + " is applicable for argument types: () values: []"
}
```

在一个特征中，私有方法对于任何不应被任何类覆盖的实现来说可能是必不可少的，尽管其他公共方法需要它。

### 3.3. 抽象方法

特征还可以包含 抽象方法，然后可以在另一个类中实现这些方法：

```groovy
trait UserTrait {
    abstract String name()
    
    String showName() {
       return "Hello, ${name()}!"
    }
}
class Employee implements UserTrait {
    String name() {
        return 'Bob'
    }
}

```

### 3.4. 覆盖默认方法

通常，特征包含其公共方法的默认实现，但我们可以在实现类中覆盖它们：

```groovy
trait SpeakingTrait {
    String speak() {
        return "Speaking!!"
    }
}

class Dog implements SpeakingTrait {
    String speak() {
        return "Bow Bow!!"
    }
}

```

特征不支持受保护和私有范围。

## 4. 这个关键字

 this关键字的行为类似于Java中的行为。我们可以将特征视为超类。

例如，我们将创建一个在特征中返回this的方法：

```groovy
trait UserTrait {
    def self() {
        return this 
    }
}
```

## 5.接口

特征也可以 实现接口，就像普通类一样。

让我们创建一个接口并在特征中实现它：

```groovy
interface Human {
    String lastName()
}
trait UserTrait implements Human {
    String showLastName() {
        return "Hello, ${lastName()}!"
    }
}
```

现在，让我们在实现类中实现接口的抽象方法：

```groovy
class Employee implements UserTrait {
    String lastName() {
        return "Marley"
    }
}
```

## 6.属性

我们可以像在任何常规类中一样向特征添加属性：

```groovy
trait UserTrait implements Human { 
    String email
    String address
}
```

## 7. 扩展特征

类似于常规的 Groovy类，一个特征可以使用extends关键字扩展另一个特征：

```groovy
trait WheelTrait {
    int noOfWheels
}

trait VehicleTrait extends WheelTrait {
    String showWheels() {
        return "Num of Wheels $noOfWheels" 
    } 
}

class Car implements VehicleTrait {}
```

我们还可以使用implements子句扩展多个特征：

```groovy
trait AddressTrait {                                      
    String residentialAddress
}

trait EmailTrait {                                    
    String email
}

trait Person implements AddressTrait, EmailTrait {}
```

## 8.多重继承冲突

当一个类实现两个或多个具有相同签名方法的特征时，我们需要知道如何解决冲突。让我们看看Groovy默认情况下如何解决此类冲突，以及我们可以覆盖默认解决方案的方法。

### 8.1. 默认冲突解决

默认情况下，将选取implements子句中最后声明的特征 中的方法。

因此，traits帮助我们实现多重继承，而不会遇到 [钻石问题](http://www.lambdafaq.org/what-about-the-diamond-problem/)。

首先，让我们使用具有相同签名的方法创建两个特征：

```groovy
trait WalkingTrait {
    String basicAbility() {
        return "Walking!!"
    }
}

trait SpeakingTrait {
    String basicAbility() {
        return "Speaking!!"
    }
}

```

接下来，让我们编写一个实现这两个特征的类：

```groovy
class Dog implements WalkingTrait, SpeakingTrait {}

```

因为 SpeakingTrait是最后声明的，所以它的basicAbility方法实现将默认在Dog类中选取。

### 8.2. 明确的冲突解决

现在，如果我们不想简单地采用语言提供的默认冲突解决方案，我们可以通过使用 trait.super显式选择要调用的方法来覆盖它。方法参考。

例如，让我们为我们的两个特征添加另一个具有相同签名的方法：

```groovy
String speakAndWalk() {
    return "Walk and speak!!"
}
String speakAndWalk() {
    return "Speak and walk!!"
}
```

现在，让我们使用super关键字覆盖Dog类中多重继承冲突的默认解决方案：

```groovy
class Dog implements WalkingTrait, SpeakingTrait {
    String speakAndWalk() {
        WalkingTrait.super.speakAndWalk()
    }
}
```

## 9. 在运行时实现特征

要动态实现特征，我们可以 使用 as关键字 在运行时将对象强制为特征。

例如，让我们使用basicBehavior方法创建一个AnimalTrait：

```groovy
trait AnimalTrait {
    String basicBehavior() {
        return "Animalistic!!"
    }
}
```

要一次实现多个特征，我们可以使用withTraits方法而不是 as关键字：

```groovy
def dog = new Dog()
def dogWithTrait = dog.withTraits SpeakingTrait, WalkingTrait, AnimalTrait
```

## 10.总结

在本文中，我们了解了如何在Groovy中创建特征并探索了它们的一些有用特性。

特征是在我们的类中添加通用实现和功能的一种非常有效的方法。此外，它使我们能够最大限度地减少冗余代码并使代码维护更加容易。