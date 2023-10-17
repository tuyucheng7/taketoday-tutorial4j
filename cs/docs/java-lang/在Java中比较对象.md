## 1. 概述

比较对象是面向对象编程语言的基本特征。

在本教程中，我们将探讨允许我们比较对象的Java语言的一些特性。我们还将查看外部库中的此类功能。

## 2. ==和!=运算符

让我们从==和!=运算符开始，它们可以分别判断两个Java对象是否相同。

### 2.1. 基元

对于原始类型，相同意味着具有相同的值：

```java
assertThat(1 == 1).isTrue();
```

多亏了自动拆箱，这在将原始值与其包装类型对应物进行比较时也有效：

```java
Integer a = new Integer(1);
assertThat(1 == a).isTrue();
```

如果两个整数具有不同的值，则==运算符将返回false，而!=运算符将返回true。

### 2.2. 对象

假设我们要比较具有相同值的两个Integer包装器类型：

```java
Integer a = new Integer(1);
Integer b = new Integer(1);

assertThat(a == b).isFalse();
```

通过比较两个对象，这些对象的值不是 1。而是它们[在堆栈](https://www.baeldung.com/java-stack-heap)中的内存地址不同，因为这两个对象都是使用new运算符创建的。如果我们将 a 分配给b ，那么我们将得到不同的结果：

```java
Integer a = new Integer(1);
Integer b = a;

assertThat(a == b).isTrue();
```

现在让我们看看当我们使用Integer#valueOf工厂方法时会发生什么：

```java
Integer a = Integer.valueOf(1);
Integer b = Integer.valueOf(1);

assertThat(a == b).isTrue();
```

在这种情况下，它们被认为是相同的。这是因为valueOf()方法将Integer存储在缓存中以避免创建太多具有相同值的包装器对象。因此，该方法为两次调用返回相同的Integer实例。

Java 也为String这样做：

```java
assertThat("Hello!" == "Hello!").isTrue();
```

但是，如果它们是使用new运算符创建的，那么它们将不相同。

最后，两个null引用被认为是相同的，而任何非null对象都被认为与null不同：

```java
assertThat(null == null).isTrue();

assertThat("Hello!" == null).isFalse();
```

当然，相等运算符的行为可能是有限的。如果我们想比较映射到不同地址的两个对象，但根据它们的内部状态将它们视为相等怎么办？我们将在下一节中看到如何做到这一点。

## 3.对象#equals方法

现在让我们用equals()方法讨论一个更广泛的相等性概念。

此方法定义在Object类中，以便每个Java对象都继承它。默认情况下，它的实现比较对象内存地址，因此它的工作方式与==运算符相同。然而，我们可以覆盖这个方法来定义相等对我们的对象意味着什么。

首先，让我们看看它对Integer等现有对象的行为方式：

```java
Integer a = new Integer(1);
Integer b = new Integer(1);

assertThat(a.equals(b)).isTrue();
```

当两个对象相同时，该方法仍返回true 。

我们应该注意，我们可以传递一个空对象作为方法的参数，但不能作为我们调用方法的对象。

我们也可以对我们自己的对象使用equals()方法。假设我们有一个Person类：

```java
public class Person {
    private String firstName;
    private String lastName;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
```

我们可以覆盖此类的equals()方法，以便我们可以根据两个Person的内部细节来比较它们：

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Person that = (Person) o;
    return firstName.equals(that.firstName) &&
      lastName.equals(that.lastName);
}
```

有关更多信息，请查看我们[关于此主题的文章](https://www.baeldung.com/java-equals-hashcode-contracts)。

## 4. Objects#equals静态方法

现在让我们看看[Objects#equals静态方法](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/Objects.html#equals(java.lang.Object,java.lang.Object))。前面我们提到不能使用null作为第一个对象的值，否则会抛出NullPointerException 。

Objects辅助类的equals()方法解决了这个问题。它接受两个参数并比较它们，同时处理空值。

让我们再次比较Person对象：

```java
Person joe = new Person("Joe", "Portman");
Person joeAgain = new Person("Joe", "Portman");
Person natalie = new Person("Natalie", "Portman");

assertThat(Objects.equals(joe, joeAgain)).isTrue();
assertThat(Objects.equals(joe, natalie)).isFalse();
```

正如我们所解释的，此方法处理空值。因此，如果两个参数都为null，它将返回true，如果其中只有一个为null，它将返回false。

这真的很方便。假设我们要向Person类添加一个可选的出生日期：

```java
public Person(String firstName, String lastName, LocalDate birthDate) {
    this(firstName, lastName);
    this.birthDate = birthDate;
}
```

然后我们必须更新我们的equals()方法，但要进行空处理。我们可以通过将条件添加到我们的equals()方法来做到这一点：

```java
birthDate == null ? that.birthDate == null : birthDate.equals(that.birthDate);
```

但是，如果我们向类中添加太多可为空的字段，它就会变得非常混乱。在我们的equals()实现中使用Objects#equals方法更加简洁，并提高了可读性：

```java
Objects.equals(birthDate, that.birthDate);
```

## 5. 对比界面

比较逻辑也可用于按特定顺序放置对象。[Comparable](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/Comparable.html)[接口](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/Comparable.html)允许我们 通过确定一个对象是大于、等于还是小于另一个对象来定义对象之间的顺序。

Comparable接口是通用的，只有一个方法compareTo()，它接受一个通用类型的参数并返回一个int。如果这低于参数，则返回值为负，如果它们相等则为 0，否则为正。

比方说，在我们的Person类中，我们想通过姓氏来比较Person对象：

```java
public class Person implements Comparable<Person> {
    //...

    @Override
    public int compareTo(Person o) {
        return this.lastName.compareTo(o.lastName);
    }
}
```

如果使用姓氏大于this的Person调用compareTo()方法，则返回负整数，如果姓氏相同则返回零，否则返回正数。

有关更多信息，请查看我们[关于此主题的文章](https://www.baeldung.com/java-comparator-comparable)。

## 6. 比较器接口

[Comparator接口](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/Comparator.html)是通用的，并且有一个比较方法，该方法采用该通用类型的两个参数并返回一个整数。我们之前已经通过Comparable接口看到了这种模式。

比较器类似；但是，它与类的定义是分开的。因此，我们可以为一个类定义任意多个Comparator，而我们只能提供一个Comparable实现。

假设我们有一个在表格视图中显示人员的网页，我们希望为用户提供按名字而不是姓氏对他们进行排序的可能性。如果我们还想保留当前的实现，这对于Comparable是不可能的，但我们可以实现自己的Comparators。

让我们创建一个Person Comparator，它将只通过他们的名字来比较他们：

```java
Comparator<Person> compareByFirstNames = Comparator.comparing(Person::getFirstName);
```

现在让我们使用Comparator对List of people 进行排序：

```java
Person joe = new Person("Joe", "Portman");
Person allan = new Person("Allan", "Dale");

List<Person> people = new ArrayList<>();
people.add(joe);
people.add(allan);

people.sort(compareByFirstNames);

assertThat(people).containsExactly(allan, joe);
```

我们可以在compareTo()实现中使用Comparator接口上的其他方法：

```java
@Override
public int compareTo(Person o) {
    return Comparator.comparing(Person::getLastName)
      .thenComparing(Person::getFirstName)
      .thenComparing(Person::getBirthDate, Comparator.nullsLast(Comparator.naturalOrder()))
      .compare(this, o);
}
```

在这种情况下，我们首先比较姓氏，然后比较名字。接下来我们比较出生日期，但由于它们可以为空，我们必须说明[如何处理它。](https://marcin-chwedczuk.github.io/comparing-with-nullsFirst-and-nullsLast)为此，我们给出了第二个论点，说明它们应该根据它们的自然顺序进行比较，null值排在最后。

## 7.阿帕奇公地

让我们看一下[Apache Commons 库](https://www.baeldung.com/java-commons-lang-3)。首先，让我们导入[Maven 依赖](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-lang3)项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

### 7.1. ObjectUtils#notEqual方法

首先，让我们谈谈ObjectUtils#notEqual方法。根据它们自己的equals()方法实现，它需要两个Object参数来确定它们是否不相等。它还处理空值。

让我们重用我们的字符串示例：

```java
String a = new String("Hello!");
String b = new String("Hello World!");

assertThat(ObjectUtils.notEqual(a, b)).isTrue();

```

需要注意的是，ObjectUtils有一个equals()方法。然而，自Java7 以来，当Objects#equals出现时，这已被弃用

### 7.2. ObjectUtils#compare方法

现在让我们用ObjectUtils#compare方法比较对象顺序。这是一个泛型方法，它采用该泛型类型的两个Comparable参数并返回一个Integer。

让我们再次使用字符串看看它：

```java
String first = new String("Hello!");
String second = new String("How are you?");

assertThat(ObjectUtils.compare(first, second)).isNegative();
```

默认情况下，该方法通过将null值视为更大来处理它们。它还提供了一个重载版本，提供反转该行为并认为它们较小，采用布尔参数。

## 8.番石榴

让我们来看看[番石榴](https://guava.dev/)。首先，让我们导入[依赖](https://search.maven.org/search?q=g:com.google.guava AND a:guava)项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

### 8.1. 对象#equal方法

类似于 Apache Commons 库，Google 为我们提供了一种判断两个对象是否相等的方法，Objects#equal。尽管它们有不同的实现，但它们返回相同的结果：

```java
String a = new String("Hello!");
String b = new String("Hello!");

assertThat(Objects.equal(a, b)).isTrue();
```

虽然它没有标记为已弃用，但此方法的 JavaDoc 表示应将其视为已弃用，因为Java7 提供了Objects#equals方法。

### 8.2. 比较方法

Guava 库不提供比较两个对象的方法(我们将在下一节中看到我们可以做些什么来实现这一点)，但它确实为我们提供了比较原始值的方法。让我们以Ints辅助类为例，看看它的compare()方法是如何工作的：

```java
assertThat(Ints.compare(1, 2)).isNegative();
```

与往常一样，如果第一个参数分别小于、等于或大于第二个参数，它会返回一个整数，该整数可能为负数、零或正数。除了bytes之外，所有原始类型都存在类似的方法。

### 8.3. 比较链类

最后，Guava 库提供了ComparisonChain类，允许我们通过比较链来比较两个对象。我们可以很容易地通过名字和姓氏来比较两个Person对象：

```java
Person natalie = new Person("Natalie", "Portman");
Person joe = new Person("Joe", "Portman");

int comparisonResult = ComparisonChain.start()
  .compare(natalie.getLastName(), joe.getLastName())
  .compare(natalie.getFirstName(), joe.getFirstName())
  .result();

assertThat(comparisonResult).isPositive();
```

底层比较是使用compareTo()方法实现的，因此传递给compare()方法的参数必须是原语或Comparable s。

## 9.总结

在本文中，我们学习了在Java中比较对象的不同方法。我们检查了同一性、平等性和有序性之间的区别。我们还查看了 Apache Commons 和 Guava 库中的相应功能。