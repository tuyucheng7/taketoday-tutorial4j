## 一、概述

在本教程中，我们将讨论 OCP 认证的高级 Java 类设计目标。

## 2.OCP Java认证

OCP[认证是](https://education.oracle.com/oracle-certified-professional-java-se-8-programmer/trackp_357)[OCA 认证](https://education.oracle.com/oracle-certified-associate-java-se-8-programmer/trackp_333)的升级版，但采用相同的选择题格式。但是，它包括高级主题，例如并发、泛型和 NIO。

在本教程中，我们将重点关注考试的高级 Java 类设计目标。实际上，我们将讨论的一些主题与 OCA 考试的 Java 类设计目标重叠。但与此同时，**OCP 还包含有关内部类、枚举类型和 lambda 等高级主题的问题**。

以下每个部分都专门针对考试的一个目标。

## 3. 开发使用抽象类和方法的代码

*[第一个考试目标是抽象](https://www.baeldung.com/java-abstract-class)* 类和方法的使用。在 Java 中，我们使用*抽象* 类在具体的子类之间共享变量和方法。

### 考试技巧 3.1：*抽象* 类的不正确访问修饰符

*我们必须始终在有关抽象* 类和方法的问题中寻找访问修饰符。

例如，尝试解决以下问题：

```java
package animal;
public abstract class Animal {
    
    abstract boolean canFly();
}
    
package horse;
import animal.Animal;

public class Horse extends Animal {
    
    @Override
    boolean canFly() {
        return false;
    }
    
    public static void main(String[] args) {
    
        System.out.println(new Horse().canFly());
    }    
}复制
Which of the following is true?
A. The output is false
B. Compilation fails on Line 10
C. Compilation fails on Line 12
D. None of the above复制
```

值得注意的是， *抽象* 方法有一个默认的访问修饰符，因为两个类在**不同的包中，我们不能在\*Horse\* 类**中访问它。因此，正确答案为（B）。

### 考试技巧 3.2： *抽象* 类或方法中的语法错误

有些问题要求我们检查给定代码中的语法错误。使用 *抽象* 类，我们很容易错过这样的错误。

例如，尝试解决以下问题：

```java
public abstract class Animal {
  
    protected abstract boolean canFly() {
    }
  
    public abstract void eat() {
        System.out.println("Eat...");
    }
}
  
public class Amphibian extends Animal {
    @Override
    protected boolean canFly() {
        return false;
    }
  
    @Override
    public void eat() {
  
    }
  
    public abstract boolean swim();
}
  
public class Frog extends Amphibian {
}复制
Which are true? (Choose all that apply.)
A. Compilation error on line 3
B. Compilation error on line 6
C. Compilation error on line 11
D. Compilation error on line 13
E. Compilation error on line 22复制
```

重要的是要记住***抽象\* 方法不能有方法体**。此外， ***抽象\* 方法不能存在于非\*抽象\*类**中。因此，（A）、（B）和（C）是正确答案。

### 考试技巧 3.3：缺少 *抽象* 方法的实现

寻找没有*抽象* 方法具体实现的非*抽象*子类。

例如，尝试解决以下问题：

```java
public abstract class Animal {
  
    protected abstract boolean canFly();
  
    public abstract void eat();
}
 
public abstract class Amphibian extends Animal {
  
    @Override
    public void eat() {
        System.out.println("Eat...");
    }
  
    public abstract boolean swim();
}
  
public class Frog extends Amphibian {
  
    @Override
    protected boolean swim() {
        return false;
    }
  
}复制
Which are true? (Choose all that apply)
A. Compilation error on line 8
B. Compilation error on line 11
C. Compilation error on line 18
D. Compilation error on line 21
E. No compilation error复制
```

Frog类**没有实现*****canFly()\*****方法**，也降低了***swim()\*** **方法的***可见***性**。因此，（C）和（D）是正确的。

尽管*Amphibian* 没有实现 *canFly()，但* 它被声明为*抽象* 类，这就是 (A) 不正确的原因。

### 考试技巧 3.4：使用 *private*、 *final* 或 *static* With *abstract* 关键字

***abstract\* 关键字不能与\*static\*、\*private\*或\*final\*关键字结合使用**。因此，不允许使用以下任何语句：

```java
public final abstract class Animal {
}

public abstract class Animal {

    public final abstract void eat();
}

public abstract class Animal {

    private abstract void eat();
}复制
```

任何此类声明都将导致编译错误。

## 4. 开发使用*final* 关键字的代码

*[Java 中的final](https://www.baeldung.com/java-final)* 关键字 允许我们声明具有常量值的变量。此外，它还允许我们声明不能扩展或覆盖的类和方法。

### 考试提示 4.1：覆盖*最终*类或方法

查找声明为*final* 并在子类中被覆盖的方法。

例如，尝试解决以下问题：

```java
public abstract class Animal {
  
    public final void eat() {
        System.out.println("Eat...");
    }
}
  
public class Horse extends Animal {
  
    public void eat() {
        System.out.println("Eat Grass");
    }
  
    public static void main(String[] args) {
        Animal animal = new Horse();
        animal.eat();
    }
}复制
What is the output?
A. Eat...
B. Eat Grass
C. The code will not compile because of line 3
D. The code will not compile because of line 8
E. The code will not compile because of line 10复制
```

由于 *eat()***在*****Animal\*** 类中被声明为 ***final\*，我们不能在 \*Horse\* 类**中覆盖它。因此，（E）是正确答案。

此外，在方法的参数中查找*最终变量。*如果为此类变量分配新值，则会导致编译错误。

## 5.内部类

[关于内部类的](https://www.baeldung.com/java-nested-classes)问题通常不像其他主题那么简单。**泛型、集合、并发等**题型在考试中有很多题目都使用了内部类的语法，这让我们很难理解题目的意图。

### 考试技巧 5.1：非*静态*内部类的不正确实例化

*实例化非静态*内部类的唯一方法是通过外部类的实例。

例如，尝试解决以下问题：

```java
public class Animal {

    class EatingHabbits {
    }

    private EatingHabbits eatingHabbits() {
        return new EatingHabbits();
    }
}

public class Zookeeper {

    public static void main(String[] args) {
        Zookeeper zookeeper = new Zookeeper();
        zookeeper.feed();
    }

    private void feed() {
        EatingHabbits habbits = new EatingHabbits();
        Animal animal = new Animal();
        Animal.EatingHabbits habbits1 = animal.eatingHabbits();
    }
}复制
What is the result? (Choose all that apply.)
A. Compilation error on line 7
B. Compilation error on line 19
C. Compilation error on line 21
D. No compilation error复制
```

由于在第 19 行，我们尝试在没有外部类对象的情况下实例化内部类，(B) 是正确答案。

### 考试技巧 5.2：内部类中*此关键字的错误使用*

在内部类中查找*this 关键字的不正确使用：*

```java
public class Animal {
    private int age = 10;

    public class EatingHabbits {
        private int numOfTimes = 5;

        public void print() {
            System.out.println("The value of numOfTimes " + this.numOfTimes);
            System.out.println("The value of age " + this.age);
            System.out.println("The value of age " + Animal.this.age);
        }
    }

    public static void main(String[] args) {
        Animal.EatingHabbits habbits = new Animal().new EatingHabbits();
        habbits.print();
    }
}复制
```

由于 ***这\* 只能用于访问当前正在执行的对象**，因此第 9 行将导致编译错误。为此，我们必须密切观察内部类中*this 的使用。*

### 考试技巧 5.3：局部内部类中的非*最终 变量*

方法局部类不能访问局部变量，除非它被声明为*final* 或者它的值在内部类中保持不变。

例如，尝试解决以下问题：

```java
public class Animal {
    private int age = 10;

    public void printAge() {
        String message = "The age is ";
        class PrintUtility {
            void print() {
                System.out.println(message + age);
            }
        }

        PrintUtility utility = new PrintUtility();
        utility.print();
    }

    public static void main(String[] args) {
        new Animal().printAge();
    }
}复制
What is the result of the following code?
 
A. The age is 0
B. The age is 10
C. Line 8 generates a compiler error
D. Line 12 generates a compiler error
E. An exception is thrown复制
```

由于**我们从未更新\*消息\* 字段，因此它实际上是\*final\***。因此，（B）是正确答案。

### 考试提示 5.4：本地**内部类不能标记为\*私有、公共、受保护\*或*****静态***

与局部变量相同的规则适用于局部内部类。因此，我们必须注意任何违反此类约束的问题。

*此外，在静态*方法中声明的任何局部类只能访问 封闭类的*静态 成员。*

### 考试技巧 5.5：*静态* 内部类中的非*静态* 成员变量 

*静态*嵌套类无法**访问外部类的实例变量或非\*静态方法。\***

因此，重要的是要注意涉及*静态*嵌套类但表现为非*静态*嵌套类的问题：

```java
public class Animal {

    private int age = 10;

    static class EatingHabits {

        private int numOfTimes = 5;

        public void print() {
            System.out.println("The value of x " + age);
            System.out.println("The value of x " + Animal.this.age);
            System.out.println("The value of numOfTimes " + numOfTimes);
        }
    }
}复制
```

*尽管第 10 行和第 11 行对于非静态*嵌套类有效，但它会在此处导致编译错误。

### 考试技巧 5.6：匿名内部类的错误声明

[匿名类](https://www.baeldung.com/java-anonymous-classes)以与嵌套类相同的方式分散在 OCP 考试中。有很多关于使用匿名内部类的集合、线程和并发的问题，其中大部分的语法令人困惑。

例如，尝试解决以下问题：

```java
public class Animal {

    public void feed() {
        System.out.println("Eating Grass");
    }
}

public class Zookeeper {

    public static void main(String[] args) {
        Animal animal = new Animal(){
            public void feed(){
                System.out.println("Eating Fish");
            }
        }
        animal.feed();
    }
}复制
What is the result?
 
A. An exception occurs at runtime
B. Eating Fish
C. Eating Grass
D. Compilation fails because of an error on line 11
E. Compilation fails because of an error on line 12
F. Compilation fails because of an error on line 15复制
```

由于***Animal\*****的匿名类 没有用分号结束**，所以第 15 行出现编译错误，这就是为什么 (F) 是正确答案的原因。

### 考试技巧 5.7：实例化接口

寻找试图**实例化接口而不是实现接口的问题：**

```java
Runnable r = new Runnable(); // compilation error

Runnable r = new Runnable() { // legal statement
    @Override
    public void run() {
    
    }
};复制
```

## 6.枚举

[枚举](https://www.baeldung.com/a-guide-to-java-enums)是一种在 Java 中表示常量枚举列表的方法。它们的行为类似于普通的 Java 类，因此可以包含[变量、方法和构造函数](https://www.baeldung.com/java-enum-values)。

尽管相似，枚举的语法确实比常规类复杂。OCP 考试侧重于此类语法不确定性问题，其中包含枚举。

### 考试技巧 6.1：*枚举* 声明中的语法错误

留意具有不正确语法错误的*枚举* 声明。

例如，尝试解决以下问题：

```java
public enum AnimalSpecies {
    MAMMAL(false), FISH(true), BIRD(false),
    REPTILE(false), AMPHIBIAN(true)

    boolean hasFins;

    public AnimalSpecies(boolean hasFins) {
        this.hasFins = hasFins;
    }

    public boolean hasFins() {
        return hasFins;
    }
}复制
What is the result of the following code? (Choose all that apply.)
 
A. Compiler error on line 2
B. Compiler error on line 3
C. Compiler error on line 7
D. Compiler error on line 11
E. The code compiles successfully复制
```

这个问题有两个问题：

-   第 3 行缺少分号 (;)。请记住，如果*枚举* 包含**变量或方法，则必须使用分号**
-   *此枚举*中有一个公共构造函数

因此，（B）和（C）是正确答案。

### 考试技巧 6.2： *带有* *抽象*方法的枚举

寻找实现接口或包含*抽象 方法的**枚举*问题。

例如，尝试解决以下问题：

```java
public enum AnimalSpecies {
    MAMMAL(false), FISH(true){
        @Override
        boolean canFly() {
            return false;
        }
    }, BIRD(false),
    REPTILE(false), AMPHIBIAN(true);

    boolean hasFins;

    AnimalSpecies(boolean hasFins) {
        this.hasFins = hasFins;
    }

    public boolean hasFins() {
        return hasFins;
    }

    abstract boolean canFly();
}

public class Zookeeper {

    public static void main(String[] args) {
        AnimalSpecies.MAMMAL.canFly();
    }
}复制
What is the result of the following code? (Choose all that apply.)
  
A. Compilation error on line 2
B. Compilation error on line 4
C. Compilation error on line 20
D. Compilation error on line 26
E. No compilation error复制
```

由于存在 *抽象 方法，我们必须为每个**枚举*常量提供其实现。而且因为上面的代码只为 *FISH*实现它，我们会得到一个编译错误。因此，（A）是正确答案。

同样，如果*枚举* 实现了一个接口*，***则每个常量都必须**为该接口的所有方法提供实现*。*

### 考试技巧 6.3：遍历*枚举* 值

[Java 提供了迭代 *枚举* 值](https://www.baeldung.com/java-enum-iteration)的静态方法。我们必须预料到要求我们计算一次此类迭代的输出的问题。

例如，尝试解决以下问题：

```java
public enum AnimalSpecies {
    MAMMAL, FISH, BIRD, REPTILE, AMPHIBIAN
}

public class Zookeeper {

    public static void main(String[] args) {
        AnimalSpecies[] animals = AnimalSpecies.values();
        System.out.println(animals[2]);
    }
}复制
What is the result? (Choose all that apply.)
 
A. FISH
B. BIRD
C. Compilation fails due to an error on line 2
D. Compilation fails due to an error on line 8
E. Compilation fails due to an error on line 10复制
```

输出为*BIRD*，因此 (B) 是正确的。

## 7. Java 中的接口和 *@Override* 

在 Java 中，[接口](https://www.baeldung.com/java-interfaces)是为类定义契约的抽象类型。OCP 考试有各种问题，测试考生在继承、方法覆盖和多重继承问题上的问题。

### 考试技巧 7.1： 非*抽象* 类中的*抽象* 方法实现

寻找没有实现接口的所有*抽象 方法的具体实现。*

例如，尝试解决以下问题：

```java
class Bird implements Flyable {
    public void fly() {
    }
}
  
abstract class Catbirds extends Bird {
  
}
  
abstract class Flamingos extends Bird {
    public abstract String color();
}
  
class GreaterFlamingo extends Flamingos {
    public String color() {
        System.out.println("The color is pink");
    }    
}
  
interface Flyable {
    void fly();
}复制
What is the result? (Choose all that apply.)
 
A. Compilation succeeds
B. Compilation fails with an error on line 6
C. Compilation fails with an error on line 10
D. Compilation fails with an error on line 11
E. Compilation fails with an error on line 14复制
```

由于所有这些都是有效的陈述，（A）是正确的答案。

随着继承的水平，这样的问题有时会很棘手。因此，**在尝试通过跟踪重写方法来计算输出之前，我们必须注意任何编译错误。**

另一个这样的编译错误源于使用 *implements* 和 *extends：*

```java
interface Bird extends Flyable, Wings {}
 
public class GreaterFlamingo extends Flamingos implements Bird, Vegetarian {}
 
public class GreaterFlamingo extends Flamingos, Bird {}复制
```

这里，第 1 行和第 3 行是有效语句，而第 5 行在 Java 中是不允许的。第 3 行的GreaterFlamingo类现在必须提供所有*抽象方法**的*具体实现。

### 考试技巧 7.2：具有相同方法签名的*默认 方法*

从 JDK 8 开始，接口现在可以有[*静态*方法和*默认* 方法](https://www.baeldung.com/java-static-default-methods)。这可能会导致多个接口包含具有相同签名的*默认* 方法的情况 。我们会在考试中找到带有此类界面的问题。

例如，尝试解决以下问题：

```java
public interface Vegetarian {

    default void eat() {
        System.out.println("Eat Veg");
    }
}

public interface NonVegetarian {

    default void eat() {
        System.out.println("Eat NonVeg");
    }
}

public class Racoon implements Vegetarian, NonVegetarian {

    @Override
    void eat() {
        System.out.println("Eat Something")
    }

    public static void main(String[] args) {
        Racoon racoon = new Racoon();
        racoon.eat();
    }
}复制
What is the result?
 
A. Eat Veg
B. Eat NonVeg
C. Eat Something
D. The output is unpredictable
E. Compilation fails
F. An exception is thrown at runtime复制
```

[这个问题与多重继承](https://www.baeldung.com/java-inheritance)有关。值得注意的是，规则说**如果它被多个接口覆盖，我们必须提供*****默认\*** **方法的实现**。

*现在，由于此代码确实提供了eat()* 方法的实现 ，因此乍一看它可能看起来是有效的代码。然而，如果我们仔细观察，我们会发现重写的*eat()* 方法不是 *公开的。*因此，正确答案为（E）。

### *考试技巧 7.3： @Override* 的使用

*[@Override](https://www.baeldung.com/java-override)* 用于表示 Java 中的重写方法。虽然是可选的，但它提高了可读性并帮助编译器报告不正确的语法。寻找在考试中滥用此注释的情况。

例如，尝试解决以下问题：

```java
public abstract class Flamingo {

    public abstract String color();

    public abstract void fly();
}

public class GreaterFlamingo extends Flamingo {
    @Override
    public String color() {
        return "Pink";
    }

    @Override
    public void fly() {
        System.out.println("Flying");
    }

    @Override
    public void eat() {
        System.out.println("Eating");
    }
    
    public static void main(String[] args) {
        GreaterFlamingo flamingo = new GreaterFlamingo();
        System.out.println(flamingo.color());
    }
}复制
What is the result? (Choose all that apply.)
 
A. Pink
B. Compilation error on line 8
C. Compilation error on line 19
D. Compilation error on line 20复制
```

请注意，我们在*eat()* 方法上 使用了*@Override* 。*但是，由于Flamingo* 类中没有这样的*抽象*方法 ，因此这不是重写方法。因此，（C）是正确答案。

## 8. 创建和使用 Lambda 表达式

高级 Java 类设计的最后一个考试目标是关于 lambda 的。必须记住，lambda 表达式可以用作实现[功能接口的](https://www.baeldung.com/java-8-functional-interfaces)匿名内部类的替代品。因此，我们会在考试中看到很多问题交替使用它们。

lambda 表达式的语法有点棘手。要发现考试中的语法错误，了解[有关 lambda 的一些规则](https://www.baeldung.com/java-8-lambda-expressions-tips)很重要。

### 考试技巧 8.1：Lambda 声明中的非*最终 变量*

与方法局部类类似，我们只能在 lambda 函数中使用*final* 或有效的 *final变量。*考题可能不遵守此类限制。

例如，尝试解决以下问题：

```java
List<String> birds = Arrays.asList("eagle", "seagull", "albatross", "buzzard", "goose");
int longest = 0;
birds.forEach(b -> {
    if (b.length() > longest){
        longest = b.length();
    }
});
 
System.out.println("Longest bird name is length: " + longest);复制
What is the result?

A. "Longest bird name is length: 9"
B. Compilation fails because of an error on line 3
C. Compilation fails because of an error on line 5
D. A runtime exception occurs on line 5复制
```

这将导致编译错误，因为我们试图**在 lambda 表达式中为变量赋值**。因此，（C）是正确答案。

## 9.结论

一般来说，在考试中阅读和理解问题的句法很重要。**大多数编码问题都试图用编译错误来混淆考生**。因此，在计算输出之前排除任何此类错误非常重要。

在本文中，我们讨论了考试中经常出现的一些技巧以及一些样题。这些只是示例问题，以展示我们在考试中的期望。

当然，破解考试的最佳方法是事先练习这些模拟题！