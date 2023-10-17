## 1. 概述

在本教程中，我们将描述Java中的两种基本相等性检查——引用相等性和值相等性。我们将对它们进行比较、展示示例并突出显示它们之间的主要区别。

此外，我们将重点关注null检查并理解为什么在处理对象时应该使用引用相等而不是值相等。

## 2.参考平等

我们将从理解引用比较开始，它由相等运算符 ( == ) 表示。当两个引用指向内存中的同一个对象时，就会发生引用相等。

### 2.1. 原始类型的相等运算符

我们知道[Java 中的原始类型](https://www.baeldung.com/java-primitives)是简单的、非类的原始值。当我们对原始类型使用相等运算符时，我们只是在比较它们的值：

```java
int a = 10;
int b = 15;
assertFalse(a == b);

int c = 10;
assertTrue(a == c);

int d = a;
assertTrue(a == d);
```

如上所示，相等性和引用检查对原语的工作方式相同。当我们用相同的值初始化一个新的原语时，检查返回true。此外，如果我们将原始值重新分配给新变量并进行比较，运算符将返回相同的结果。

现在让我们执行空检查：

```java
int e = null; // compilation error
assertFalse(a == null); // compilation error
assertFalse(10 == null); // compilation error
```

Java 禁止将null分配给原语。通常，我们不能对原始变量或值使用相等运算符执行任何空值检查。

### 2.2. 对象类型的相等运算符

对于[Java 中的对象类型](https://www.baeldung.com/java-classes-objects)，相等运算符仅执行引用相等比较，忽略对象值。在我们实施测试之前，让我们创建一个简单的自定义类：

```arduino
public class Person {
    private String name;
    private int age;

    // constructor, getters, setters...
}
```

现在，让我们初始化一些类对象并检查相等运算符的结果：

```java
Person a = new Person("Bob", 20);
Person b = new Person("Mike", 40);
assertFalse(a == b);

Person c = new Person("Bob", 20);
assertFalse(a == c);

Person d = a;
assertTrue(a == d);
```

结果与以前大不相同。第二次检查返回false，而我们的基元为true。正如我们前面提到的，相等运算符在比较时会忽略对象的内部值。它只检查两个变量是否引用相同的内存地址。

与原语不同，我们可以在处理对象时使用null ：

```
assertFalse(a == null);Person e = null;assertTrue(e == null);
```

通过使用相等运算符并比较null，我们检查分配给变量的对象是否已经初始化。

## 3. 价值观平等

现在让我们关注价值平等测试。当两个独立的对象恰好具有相同的值或状态时，就会发生值相等。

[这比较值并且与Object 的 equals()方法](https://www.baeldung.com/java-comparing-objects#equals-instance)密切相关。和以前一样，让我们比较它与基元和对象类型的用法，看看主要区别。

### 3.1. 原始类型的equals()方法

正如我们所知，原语是具有单个值的基本类型，不实现任何方法。因此，不可能直接使用原语调用equals()方法：

```
int a = 10;assertTrue(a.equals(10)); // compilation error
```

然而，由于每个原语都有自己的[包装类](https://www.baeldung.com/java-wrapper-classes)，我们可以使用装箱机制将其转换为对象表示。然后，我们可以像使用对象类型一样轻松地调用equals()方法：

```java
int a = 10;
Integer b = a;

assertTrue(b.equals(10));
```

### 3.2. 对象类型的 equals()方法

让我们回到我们的Person类。为了使 equals() 方法正常工作，[我们需要](https://www.baeldung.com/java-eclipse-equals-and-hashcode)通过考虑类中包含的字段来覆盖自定义类中的方法：

```kotlin
public class Person {
    // other fields and methods omitted

    @Override
    public boolean equals(Object o) {
        if (this == o) 
            return true;
        if (o == null || getClass() != o.getClass()) 
            return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }
}
```

首先，如果给定值具有相同的引用，则equals()方法返回true，这是由引用运算符检查的。如果不是，我们开始平等测试。

此外，我们测试两个值的Class对象是否相等。如果它们不同，我们返回false 。否则，我们继续检查是否相等。最后，我们返回分别比较每个属性的组合结果。

现在，让我们修改之前的测试并检查结果：

```java
Person a = new Person("Bob", 20);
Person b = new Person("Mike", 40);
assertFalse(a.equals(b));

Person c = new Person("Bob", 20);
assertTrue(a.equals(c));

Person d = a;
assertTrue(a.equals(d));
```

正如我们所见，第二个检查返回true而不是引用相等性。我们重写的equals()方法比较对象的内部值。

如果我们不覆盖equals()方法，则使用父类Object 中的方法。由于Object.equals()方法仅进行引用相等性检查，因此在比较Person 对象时，行为可能不是我们所期望的。

虽然我们没有在上面显示hashCode()方法，但我们应该注意，每当我们重写 equals()方法时重写它[以确保这些方法之间的一致性](https://www.baeldung.com/java-equals-hashcode-contracts)是很重要的。 

## 4. 零平等

最后，让我们检查一下equals()方法如何处理空值：

```java
Person a = new Person("Bob", 20);
Person e = null;
assertFalse(a.equals(e));
assertThrows(NullPointerException.class, () -> e.equals(a));
```

当我们使用equals()方法对另一个对象进行检查时，根据这些变量的顺序，我们会得到两个不同的结果。最后一条语句抛出异常，因为我们在空引用上调用了equals()方法。要修复最后一条语句，我们应该首先调用相等运算符检查：

```java
assertFalse(e != null && e.equals(a));
```

现在，条件的左侧返回false，使整个语句等于false，从而防止抛出NullPointerException。因此，我们必须记住首先检查我们调用equals()方法的值不为null，否则，它会导致恼人的错误。

此外，从Java7 开始，我们可以使用空安全的[Objects#equals()](https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/util/Objects.html#equals(java.lang.Object,java.lang.Object)) 静态方法来执行相等性检查：

```java
assertFalse(Objects.equals(e, a));
assertTrue(Objects.equals(null, e));
```

此辅助方法执行额外的检查以防止抛出 NullPointerException ，当两个参数都为null时返回true。

## 5.总结

在本文中，我们讨论了 针对原始值和对象值的引用相等性和值相等性检查。

为了测试引用相等性，我们使用==运算符。此运算符对原始值和对象的工作方式略有不同。当我们将相等运算符与原语一起使用时，它会比较值。另一方面，当我们将它用于对象时，它会检查内存引用。通过将它与空值进行比较，我们只需检查该对象是否已在内存中初始化。

要在Java中执行值相等性测试，我们使用从Object继承的equals()方法。基元是简单的非类值，因此不能在不包装的情况下调用此方法。

我们还需要记住只对实例化对象调用equals()方法。否则，将抛出异常。为防止这种情况，如果我们怀疑是空值，我们应该使用==运算符检查该值。