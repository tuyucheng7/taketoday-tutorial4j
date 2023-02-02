## 1. 简介

在许多Java应用程序中，在对象之间传递不可变数据是最常见但又很平凡的任务之一。

在Java 14之前，这需要创建一个包含样板字段和方法的类，这很容易出现小错误和混乱的意图。

随着Java 14的发布，我们现在可以使用记录来解决这些问题。

在本教程中，**我们将了解记录的基础知识，包括它的用途、生成的方法和自定义技术**。

## 2. 目的

通常，我们编写类来简单地保存数据，例如数据库结果、查询结果或来自服务的信息。

在许多情况下，这些数据是[不可变](https://www.baeldung.com/java-immutable-object)的，**因为不可变性确保了**[数据在没有同步的情况下的有效性](https://www.baeldung.com/java-immutable-object#benefits-of-immutability)。

为此，我们使用以下内容创建数据类：

1.  每个数据对应一个private，final字段
2.  每个字段的getter方法
3.  公共构造函数，每个字段都有相应的参数
4.  equals方法，当所有字段都匹配时，该方法为同一类的对象返回true
5.  当所有字段匹配时返回相同值的hashCode方法
6.  toString方法，该方法包括类的名称和每个字段的名称及其对应的值

例如，我们可以创建一个带有姓名和地址的简单Person数据类：

```java
public class Person {
    private final String name;
    private final String address;

    public Person(String name, String address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Person)) {
            return false;
        } else {
            Person other = (Person) obj;
            return Objects.equals(name, other.name)
                    && Objects.equals(address, other.address);
        }
    }

    @Override
    public String toString() {
        return "Person [name=" + name + ", address=" + address + "]";
    }

    // standard getters
}
```

虽然这实现了我们的目标，但它存在两个问题：

1.  有很多样板代码
2.  我们模糊了这个类的目的：代表一个有名字和地址的人

在第一种情况下，我们不得不为每个数据类重复同样繁琐的过程，单调地为每个数据创建一个新的字段；创建equals、hashCode和toString方法；并创建一个接收每个字段的构造函数。

虽然现代化IDE(例如Intellij和Eclipse)可以自动生成许多这样的类，**但它们无法在我们添加新字段时自动更新我们的类**。例如，如果我们添加了一个新字段，我们必须更新我们的equals方法以合并该字段。

在第二种情况下，**额外的代码掩盖了我们的类只是一个具有两个String字段(name和address)的数据类**。

更好的方法是显式声明我们的类是数据类。

## 3. 基础知识

从JDK 14开始，我们可以用记录替换重复的数据类。**记录是不可变的数据类，只需要字段的类型和名称**。

equals、hashCode和toString方法，以及private、final字段和public构造函数，都是由Java编译器生成的。

要创建Person记录，我们需要使用record关键字：

```java
public record Person(String name, String address) {
}
```

### 3.1 构造器

**使用记录，编译器为我们生成一个公共构造函数，包含每个字段作为参数**。

对于我们的Person记录，等效的构造函数为：

```java
public Person(String name, String address) {
    this.name = name;
    this.address = address;
}
```

这个构造函数可以用与类相同的方式从记录中实例化对象：

```java
Person person = new Person("John Doe", "100 Linda Ln.");
```

### 3.2 Getters

**我们还开箱即用的获得公共getter方法，其getter方法名称与我们的字段名称相匹配**。

在我们的Person记录中，这意味着name()和address()是它们对应字段的getter：

```java
@Test
void givenValidNameAndAddress_whenGetNameAndAddress_thenExpectedValuesReturned() {
    String name = "John Doe";
    String address = "100 Linda Ln.";

    Person person = new Person(name, address);

    assertEquals(name, person.name());
    assertEquals(address, person.address());
}
```

### 3.3 equals

此外，还为我们生成了一个equals方法。

**如果提供的对象属于同一类型并且其所有字段的值都匹配，则此方法返回true**：

```java
@Test
void givenSameNameAndAddress_whenEquals_thenPersonsEqual() {
    String name = "John Doe";
    String address = "100 Linda Ln.";

    Person person1 = new Person(name, address);
    Person person2 = new Person(name, address);

    assertTrue(person1.equals(person2));
}
```

如果两个Person实例之间的任何字段不同，则equals方法将返回false。

### 3.4 hashcode

和我们的equals方法类似，也会为我们生成对应的hashCode方法。

**如果两个Person对象的所有字段值都匹配(除非**[生日悖论](https://en.wikipedia.org/wiki/Birthday_problem)**冲突)，该hashCode方法会为两个Person对象返回相同的值**：

```java
@Test
void givenSameNameAndAddress_whenHashCode_thenPersonsEqual() {
    String name = "John Doe";
    String address = "100 Linda Ln.";

    Person person1 = new Person(name, address);
    Person person2 = new Person(name, address);

    assertEquals(person1.hashCode(), person2.hashCode());
}
```

如果任何字段值不同，则hashCode值将不同。

### 3.5 toString

最后，**我们还可以使用toString方法，该方法生成一个包含记录名称的字符串，后跟每个字段的名称及其在方括号中的对应值**。 

因此，实例化一个name为“John Doe”且address为“100 Linda Ln.”的Person对象并调用toString方法时的结果如下：

```shell
Person[name=John Doe, address=100 Linda Ln.]
```

## 4. 构造函数

虽然编译器会为我们生成公共构造函数，但我们仍然可以自定义构造函数实现。

**此自定义旨在用于验证，应尽可能保持简单**。

例如，我们可以使用以下构造函数实现来确保提供给我们的Person记录的name和address不为空：

```java
public record Person(String name, String address) {
    public Person {
        Objects.requireNonNull(name);
        Objects.requireNonNull(address);
    }
}
```

我们还可以通过提供不同的参数列表来创建具有不同参数的新构造函数：

```java
public record Person(String name, String address) {
    public Person(String name) {
        this(name, "Unknown");
    }
}
```

与类构造函数一样，**可以使用this关键字(例如this.name和this.address)引用字段**，并且**参数与字段的名称(即name和address)相匹配**。

请注意，**使用与生成的公共构造函数相同的参数创建构造函数是有效的，但这需要手动初始化每个字段**：

```java
public record Person(String name, String address) {
    public Person(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
```

此外，**声明一个紧凑的构造函数和一个具有与生成的构造函数匹配的参数列表的构造函数会导致编译错误**。

因此，以下内容不会编译：

```java
public record Person(String name, String address) {
    public Person {
        Objects.requireNonNull(name);
        Objects.requireNonNull(address);
    }

    public Person(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
```

## 5. 静态变量和方法

与常规Java类一样，**我们也可以在记录中包含静态变量和方法**。

我们使用与类相同的语法声明静态变量：

```java
public record Person(String name, String address) {
    public static String UNKNOWN_ADDRESS = "Unknown";
}
```

同样，我们可以使用与类相同的语法声明静态方法：

```java
public record Person(String name, String address) {
    public static Person unnamed(String address) {
        return new Person("Unnamed", address);
    }
}
```

然后我们可以使用记录的名称来引用静态变量和静态方法：

```java
Person.UNKNOWN_ADDRESS
Person.unnamed("100 Linda Ln.");
```

## 6. 总结

在本文中，我们研究了Java 14中引入的record关键字，包括基本概念和更复杂的层面。

使用记录及其编译器生成的方法，我们可以减少样板代码并提高不可变类的可靠性。