## 1. 概述

在这篇简短的文章中，我们将深入介绍public修饰符，并讨论何时以及如何将它用于类和成员。

此外，我们将说明使用公共数据字段的缺点。

有关访问修饰符的一般概述，请务必查看我们关于[Java中的访问修饰符](https://www.baeldung.com/java-access-modifiers)的文章。

## 2. 何时使用公共访问修饰符

公共类和接口以及公共成员定义了一个API。这是我们代码的一部分，其他人可以看到并使用它来控制我们对象的行为。

但是，过度使用public修饰符违反了面向对象编程(OOP)的封装原则，并且有一些缺点：

-   它增加了API的大小，使客户更难使用
-   更改我们的代码变得越来越困难，因为客户端依赖它-任何未来的更改都可能破坏他们的代码

## 3. 公共接口和类

### 3.1 公共接口

**公共接口定义了可以具有一个或多个实现的规范**。这些实现可以由我们提供，也可以由其他人编写。

例如，Java API公开了Connection接口来定义数据库连接操作，将实际实现留给每个数据库供应商。在运行时，我们根据项目设置获得所需的连接：

```java
Connection connection = DriverManager.getConnection(url);
```

getConnection方法返回特定技术实现的实例。

### 3.2 公开类

我们定义公共类，以便客户端可以通过实例化和静态引用来使用它们的成员：

```java
assertEquals(0, new BigDecimal(0).intValue()); // instance member
assertEquals(2147483647, Integer.MAX_VALUE); // static member
```

此外，我们可以通过使用可选的[abstract](https://www.baeldung.com/java-abstract-class)修饰符来设计用于继承的公共类。**当我们使用abstract修饰符时，该类就像一个骨架，除了具有每个子类需要实现的抽象方法之外，还具有任何具体实现都可以使用的字段和预实现方法**。

例如，Java集合框架提供了AbstractList类作为创建自定义列表的基础：

```java
public class ListOfThree<E> extends AbstractList<E> {

    @Override
    public E get(int index) {
        // custom implementation
    }

    @Override
    public int size() {
        // custom implementation
    }
}
```

因此，我们只需要实现get()和size()方法。indexOf()和containsAll()等其他方法已经为我们实现了。

### 3.3 嵌套的公共类和接口

与公共顶级类和接口类似，嵌套的公共类和接口定义API数据类型。但是，它们在两个方面特别有用：

-   它们向API最终用户表明封闭的顶级类型及其封闭类型具有逻辑关系并一起使用
-   如果我们将它们声明为顶级类和接口，它们通过减少我们将使用的源代码文件的数量来使我们的代码库更加紧凑

一个例子是来自核心Java API的Map.Entry接口：

```java
for (Map.Entry<String, String> entry : mapObject.entrySet()) { }
```

使Map.Entry成为嵌套接口与java.util.Map接口紧密相关，使我们免于在java.util包中创建另一个文件。

请阅读[嵌套类](https://www.baeldung.com/java-nested-classes)文章以获取更多详细信息。

## 4. 公共方法

公共方法使用户能够执行现成的操作。一个示例是String API中的公共toLowerCase方法：

```java
assertEquals("alex", "ALEX".toLowerCase());
```

如果不使用任何实例字段，我们可以安全地将公共方法设为静态。Integer类中的parseInt方法是公共静态方法的一个示例：

```java
assertEquals(1, Integer.parseInt("1"));
```

构造函数通常是公共的，这样我们就可以实例化和初始化对象，尽管有时它们可能是私有的，就像在[单例](https://www.baeldung.com/java-singleton)中一样。

## 5. 公共字段

公共字段允许直接更改对象的状态。**经验法则是我们不应该使用公共字段**。这有几个原因，正如我们即将看到的。

### 5.1 线程安全

对非最终字段或最终可变字段使用公共可见性不是线程安全的。我们无法控制在不同线程中更改它们的引用或状态。

请查看我们关于[线程安全](https://www.baeldung.com/java-thread-safety)的文章，以了解有关编写线程安全代码的更多信息。

### 5.2 对修改采取行动

我们无法控制非最终公共字段，因为它的引用或状态可以直接设置。

相反，最好使用私有修饰符隐藏字段并使用公共设置器：

```java
public class Student {

    private int age;

    public void setAge(int age) {
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException();
        }

        this.age = age;
    }
}
```

### 5.3 更改数据类型

可变或不可变的公共字段是客户端合约的一部分。在未来的版本中更难更改这些字段的数据表示，因为客户端可能需要重构他们的实现。

通过赋予字段私有范围和使用访问器，我们可以灵活地更改内部表示，同时保持旧数据类型：

```java
public class Student {

    private StudentGrade grade; //new data representation
   
    public void setGrade(int grade) {        
        this.grade = new StudentGrade(grade);
    }

    public int getGrade() {
        return this.grade.getGrade().intValue();
    }
}
```

使用公共字段的唯一例外是使用static final不可变字段来表示常量：

```java
public static final String SLASH = "/";
```

## 6. 总结

在本教程中，我们看到public修饰符用于定义API。

此外，我们还描述了过度使用此修饰符可能会如何限制对我们的实现进行改进的能力。

最后，我们讨论了为什么对字段使用公共修饰符是一种不好的做法。