## 1. 简介

Java 15于2020年9月全面上市，是JDK平台14的下一个短期版本。它建立在早期版本的多项功能之上，并提供了一些新的增强功能。

**在这篇文章中，我们将介绍Java 15的一些新特性**，以及Java开发人员感兴趣的其他变化。

## 2. Record(JEP 384)

**记录是Java中的一种新型类，可以让创建不可变数据对象变得更容易**。

它最初作为[早期预览](https://www.baeldung.com/java-record-keyword)在Java 14中引入，Java 15旨在它成为正式产品功能之前[改进一些方面](https://openjdk.java.net/jeps/384)。

让我们看一个使用当前Java的示例，以及它如何随记录而变化。

### 2.1 不使用Record

在记录出现之前，我们创建一个不可变数据传输对象(DTO)需要像下面这样：

```java
public class Person {
    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
```

请注意，这里用了很多代码来创建一个真正只保存状态的不可变对象。我们所有的字段都是使用final显式定义的，我们有一个单一的全参数构造函数，并且每个字段都有一个访问器方法。在某些情况下，我们甚至可以将类本身声明为final以防止任何子类化。

在许多情况下，我们还需要进一步重写toString方法以提供有意义的日志记录输出。我们还可能重写equals和hashCode方法，以避免在比较这些对象的两个实例时出现意外后果。

### 2.2 使用Record

使用新的记录类，我们可以以更紧凑的方式定义相同的不可变数据对象：

```java
public record Person(String name, int age) {
}
```

这里发生了一些事情。**首先也是最重要的一点，类定义具有特定于记录的新语法**。我们在Person后的括号内提供有关记录中字段的详细信息。

使用此标头，编译器可以推断出内部字段。这意味着我们不需要定义特定的成员变量和访问器，因为它们是默认提供的，并且我们也不必提供构造函数。

此外，编译器还为toString、equals和hashCode方法提供了合理的实现。

**虽然记录提供了许多样板代码，但它也允许我们覆盖一些默认行为**。例如，我们可以定义一个规范的构造函数来做一些验证：

```java
public record Person(String name, int age) {
    public Person {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }
}
```

值得一提的是，记录也有一些限制。除其他事项外，它们始终是final的，不能声明为抽象的，并且它们不能使用[本地方法](https://www.baeldung.com/java-native)。

## 3. 密封类(JEP 360)

目前，**Java不提供对继承的细粒度控制**。public、protected、private等访问修饰符以及默认的package-private提供非常粗粒度的控制。

为此，[密封类](https://openjdk.org/jeps/360)的目标是允许各个类声明哪些类型可以用作子类型。这也适用于接口并确定哪些类型可以实现它们。

密封类涉及两个新的关键字-sealed和permits：

```java
public abstract sealed class Person
        permits Employee, Manager {
    //...
}
```

在本例中，我们声明了一个名为Person的抽象类，并且指定了唯一可以扩展它的类是Employee和Manager。扩展密封类与我们一直使用的方式相同，即使用extends关键字：

```java
public final class Employee extends Person {
}

public non-sealed class Manager extends Person {
}
```

需要注意的是，**任何扩展密封类的类本身都必须声明为sealed、non-sealed或final**。这可确保类层次结构保持有限并为编译器所知。

**这种有限而详尽的层次结构是使用密封类的一大好处**，让我们看一个实际的例子：

```java
if (person instanceof Employee) {
    return ((Employee) person).getEmployeeId();
} 
else if (person instanceof Manager) {
    return ((Manager) person).getSupervisorId();
}
```

**如果没有密封类，编译器就无法合理地确定所有可能的子类都包含在我们的if-else语句中**。如果在末尾没有else子句，编译器可能会发出警告，表明我们的逻辑并未涵盖所有情况。

## 4. 隐藏类(JEP 371)

Java 15中引入的一个新特性称为[隐藏类](https://openjdk.org/jeps/371)。虽然大多数开发人员不会直接从它身上获得好处，但使用动态字节码或JVM语言的任何人都可能会发现它们很有用。

**隐藏类的目标是允许在运行时创建不可发现的类**，这意味着它们不能被其他类链接，也不能通过[反射](https://www.baeldung.com/java-reflection)被发现。诸如此类的类通常具有较短的生命周期，因此，隐藏类被设计为高效加载和卸载。

请注意，当前的Java版本允许创建类似于隐藏类的匿名类。但是，它们依赖于[Unsafe API](https://www.baeldung.com/java-unsafe)，而隐藏类没有这种依赖性。

## 5. 模式匹配类型检查(JEP 375)

[模式匹配](https://openjdk.org/jeps/375)功能在Java 14中进行了预览，而Java 15旨在继续其预览状态而没有新的增强功能。

作为回顾，此功能的目标是删除大量通常与instanceof运算符一起提供的样板代码：

```java
if (person instanceof Employee) {
    Employee employee = (Employee) person;
    Date hireDate = employee.getHireDate();
    // ...
}
```

这是Java中非常常见的模式，每当我们检查一个变量是否是某种类型时，我们几乎总是在它之后进行对该类型的强制转换。

模式匹配功能通过引入新的绑定变量来简化此操作：

```java
if (person instanceof Employee employee) {
    Date hireDate = employee.getHireDate();
    // ...
}
```

请注意我们是如何提供一个新的变量名称employee作为类型检查的一部分的。**如果类型检查为true，那么JVM会自动为我们强制转换变量，并将结果分配给新的绑定变量**。

我们还可以将新的绑定变量与条件语句结合起来：

```java
if (person instanceof Employee employee && employee.getYearsOfService() > 5) {
    // ...
}
```

在未来的Java版本中，目标是将模式匹配扩展到其他语言功能，例如switch语句或者是record。

## 6. 外部内存API(JEP 383)

[外部内存访问](https://openjdk.org/jeps/383)已经是Java 14的一个孵化特性。在Java 15中，目标是继续其孵化状态，同时添加几个新特性：

-   一个新的VarHandle API，用于自定义内存访问变量句柄
-   支持使用Spliterator接口并行处理内存段
-   增强了对映射内存段的支持
-   能够操纵和取消引用来自本地调用之类的地址

外部内存通常是指位于托管JVM堆之外的内存。因此，它不受垃圾收集的影响，通常可以处理非常大的内存段。

虽然这些新的API可能不会直接影响大多数开发人员，但它们将为处理外部内存的第三方库提供很多价值。这包括分布式缓存、非规范化文档存储、大型任意字节缓冲区、内存映射文件等。

## 7. 垃圾收集器

**在Java 15中，**[ZGC](https://www.baeldung.com/jvm-zgc-garbage-collector)**(JEP 377)和Shenandoah(JEP 379)都不再是实验性功能了**，它们都将是团队可以选择使用的受支持配置，而G1收集器将保持默认设置。

两者以前都可以使用实验性功能标志获得，这种方法允许开发人员测试新的垃圾收集器并提交反馈，而无需下载单独的JDK或附加组件。

关于[Shenandoah](https://openjdk.org/jeps/379)的一个注意事项：并非所有供应商的JDK都提供它，最值得注意的是，Oracle JDK不包含它。

## 8. 其他变化

Java 15中还有其他几个值得注意的变化。

经过Java 13和14的多轮预览后，[文本块](https://www.baeldung.com/java-text-blocks)将成为Java 15中全面支持的产品特性。

[友好的空指针异常](https://www.baeldung.com/java-14-nullpointerexception)，最初在JEP 358下的Java 14中提供，现在默认启用。

旧版的DatagramSocket API已被重写，这是Socket API在Java 14中重写的后续。虽然它不会影响大多数开发人员，但它很有趣，因为它是[Project Loom](https://www.baeldung.com/openjdk-project-loom)的先决条件。

另外值得注意的是，Java 15包括对Edwards-Curve数字签名算法的加密支持。EdDSA是一种现代椭圆曲线签名方案，与JDK中现有的签名方案相比具有多项优势。

最后，Java 15中弃用了几项内容：偏向锁定、Solaris/SPARC端口和RMI Activation都已删除或计划在未来版本中删除。

值得注意的是，最初在Java 8中引入的Nashorn JavaScript引擎现已被删除。随着最近GraalVM和其他VM技术的引入，很明显Nashorn在JDK生态系统中不再占有一席之地。

## 9. 总结

Java 15建立在过去版本的多项功能之上，包括记录、文本块、新的垃圾收集算法等。**它还添加了新的预览功能，包括密封类和隐藏类**。

由于Java 15不是一个长期支持版本，我们预计对它的支持将于2021年3月结束。届时，我们可以期待Java 16，紧随其后的是新的长期支持版本Java 17。