## 1. 概述

在本教程中，我们将介绍两个密切相关的方法：equals()和hashCode()。我们将重点关注它们之间的关系、如何正确覆盖它们以及为什么我们应该覆盖两者或两者都不覆盖。

## 2. equals()

Object类同时定义了equals()和hashCode()方法，这意味着这两个方法在每个Java类中隐式定义，包括我们创建的类：

```java
class Money {
    int amount;
    String currencyCode;
}
```

```java
Money income = new Money(55, "USD");
Money expenses = new Money(55, "USD");
boolean balanced = income.equals(expenses)
```

我们期望income.equals(expenses)返回true，但是对于当前形式的Money类，它不会。

**Object类中equals()的默认实现表示相等与对象标识相同，income和expenses是两个不同的实例**。

### 2.1 覆盖equals()

让我们覆盖equals()方法，以便它不仅考虑对象标识，还考虑两个相关属性的值：

```java
@Override
public boolean equals(Object o) {
    if (o == this)
        return true;
    if (!(o instanceof Money))
        return false;
    Money other = (Money)o;
    boolean currencyCodeEquals = (this.currencyCode == null && other.currencyCode == null)
        || (this.currencyCode != null && this.currencyCode.equals(other.currencyCode));
    return this.amount == other.amount && currencyCodeEquals;
}
```

### 2.2 equals()合约

Java SE定义了equals()方法的实现必须满足的契约，**大多数标准都是常识**。equals()方法必须是：

-   自反：对象必须等于自身
-   对称：**x.equals(y)必须返回与y.equals(x)相同的结果**
-   传递：如果x.equals(y)和y.equals(z)，则也x.equals(z)
-   一致：只有当包含在equals()中的属性发生变化时，equals()的值才应该改变(不允许随机性)

我们可以在[Java SE文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html)中查找Object类的确切标准。

### 2.3 继承违反equals()的对称性

如果equals()的标准是这样的常识，那么我们怎么可能违反它呢？好吧，**如果我们扩展一个重写了equals()的类，违规行为就会经常发生**。让我们考虑一个扩展我们的Money类的Voucher类：

```java
class WrongVoucher extends Money {

    private String store;

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof WrongVoucher))
            return false;
        WrongVoucher other = (WrongVoucher)o;
        boolean currencyCodeEquals = (this.currencyCode == null && other.currencyCode == null)
              || (this.currencyCode != null && this.currencyCode.equals(other.currencyCode));
        boolean storeEquals = (this.store == null && other.store == null)
              || (this.store != null && this.store.equals(other.store));
        return this.amount == other.amount && currencyCodeEquals && storeEquals;
    }

    // other methods
}
```

乍一看，Voucher类及其对equals()的覆盖似乎是正确的。只要我们将Money与Money或Voucher与Voucher进行比较，这两种equals()方法的行为都是正确的。**但是，如果我们比较这两个对象，会发生什么**：

```java
Money cash = new Money(42, "USD");
WrongVoucher voucher = new WrongVoucher(42, "USD", "Amazon");

voucher.equals(cash) => false // As expected.
cash.equals(voucher) => true // That's wrong.
```

**这违反了equals()合约的对称标准**。

### 2.4 用组合解决equals()对称性

为了避免这个陷阱，我们应该**倾向组合而不是继承**。

让我们创建一个具有Money属性的Voucher类，而不是子类化Money：

```java
class Voucher {

    private Money value;
    private String store;

    Voucher(int amount, String currencyCode, String store) {
        this.value = new Money(amount, currencyCode);
        this.store = store;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Voucher))
            return false;
        Voucher other = (Voucher) o;
        boolean valueEquals = (this.value == null && other.value == null)
              || (this.value != null && this.value.equals(other.value));
        boolean storeEquals = (this.store == null && other.store == null)
              || (this.store != null && this.store.equals(other.store));
        return valueEquals && storeEquals;
    }

    // other methods
}
```

现在equals将按照合约要求对称地工作。

## 3. hashCode()

hashCode()返回一个表示类的当前实例的整数。我们应该根据类的相等定义来计算这个值。因此，**如果我们覆盖equals()方法，我们也必须覆盖hashCode()**。

有关详细信息，请查看我们的[hashCode()指南](https://www.baeldung.com/java-hashcode)。

### 3.1 hashCode()合约

Java SE还为hashCode()方法定义了一个约定。仔细观察它可以看出hashCode()和equals()的关系有多密切。

**hashCode()合约中的所有三个条件都以某种方式提到了equals()方法**：

-   内部一致性：hashCode()的值可能只有在equals()中的属性更改时更改
-   相等一致性：**相等的对象必须返回相同的hashCode**
-   碰撞：**不相等的对象可能具有相同的hashCode**

### 3.2 违反hashCode()和equals()的一致性

hashCode方法契约的第二个标准有一个重要的结果：如果我们覆盖equals()，我们也必须覆盖hashCode()。这是迄今为止最广泛的违反equals()和hashCode()方法契约的行为。

让我们看这样一个例子：

```java
class Team {

    String city;
    String department;

    @Override
    public final boolean equals(Object o) {
        // implementation
    }
}
```

Team类仅覆盖equals()，但它仍然隐含地使用Object类中定义的hashCode()的默认实现。这会为该类的每个实例返回一个不同的hashCode()，这违反了第二条规则。

现在，如果我们创建两个Team对象，城市为“New York”，部门为“development”，它们将是相等的，**但它们将返回不同的hashCode**。

### 3.3 具有不一致hashCode()的HashMap键

但是为什么我们的Team类中的契约违反是一个问题呢？好吧，当涉及到一些基于哈希的集合时，麻烦就开始了。让我们尝试使用我们的Team类作为HashMap的键：

```java
Map<Team,String> leaders = new HashMap<>();
leaders.put(new Team("New York", "development"), "Anne");
leaders.put(new Team("Boston", "development"), "Brian");
leaders.put(new Team("Boston", "marketing"), "Charlie");

Team myTeam = new Team("New York", "development");
String myTeamLeader = leaders.get(myTeam);
```

我们希望myTeamLeader返回“Anne”，**但对于当前的代码，它不会**。

如果我们想使用Team类的实例作为HashMap键，我们必须重写hashCode()方法以使其遵守约定；**相等的对象返回相同的hashCode**。

让我们看一个示例实现：

```java
@Override
public final int hashCode() {
    int result = 17;
    if (city != null) {
        result = 31 * result + city.hashCode();
    }
    if (department != null) {
        result = 31 * result + department.hashCode();
    }
    return result;
}
```

进行此更改后，leaders.get(myTeam)按预期返回“Anne”。

## 4. 我们什么时候覆盖equals()和hashCode()？

**通常，我们想要覆盖它们两者或两者都不覆盖**。我们刚刚在第3节中看到了如果忽略此规则会产生的不良后果。

领域驱动设计可以帮助我们决定什么时候应该离开它们。对于实体类，对于具有内在标识的对象，默认实现通常是有意义的。

但是，**对于值对象，我们通常更喜欢基于其属性的相等性**。因此，我们需要覆盖equals()和hashCode()。请记住我们第2节中的Money类：55美元等于55美元，即使它们是两个不同的实例。

## 5. 实现技巧

我们通常不会手动编写这些方法的实现。正如我们所看到的，有很多陷阱。

一种常见的选择是让我们的[IDE](https://www.baeldung.com/java-eclipse-equals-and-hashcode)生成equals()和hashCode()方法。

[Apache Commons Lang](https://www.baeldung.com/java-commons-lang-3)和[Google Guava](https://www.baeldung.com/whats-new-in-guava-19)都有工具类，以简化这两种方法的编写。

[Project Lombok](https://www.baeldung.com/intro-to-project-lombok)还提供了一个@EqualsAndHashCode注解。**再次注意equals()和hashCode()如何“走到一起”，甚至有一个共同的注解**。

## 6. 验证合约

如果我们想检查我们的实现是否遵守Java SE契约以及最佳实践，**我们可以使用EqualsVerifier库**。

让我们添加[EqualsVerifier](https://mvnrepository.com/artifact/nl.jqno.equalsverifier/equalsverifier) Maven测试依赖项：

```xml
<dependency>
    <groupId>nl.jqno.equalsverifier</groupId>
    <artifactId>equalsverifier</artifactId>
    <version>3.0.3</version>
    <scope>test</scope>
</dependency>
```

现在让我们验证我们的Team类是否遵循equals()和hashCode()契约：

```java
@Test
public void equalsHashCodeContracts() {
    EqualsVerifier.forClass(Team.class).verify();
}
```

值得注意的是，EqualsVerifier同时测试equals()和hashCode()方法。

**EqualsVerifier比Java SE契约严格得多**。例如，它确保我们的方法不会抛出NullPointerException。此外，它强制这两种方法或类本身都是最终的。

重要的是要认识到**EqualsVerifier的默认配置只允许不可变字段**。这是比Java SE契约所允许的更严格的检查。它遵循领域驱动设计的建议，使值对象不可变。

如果我们发现某些内置约束是不必要的，我们可以将suppress(Warning.SPECIFIC_WARNING)添加到我们的EqualsVerifier调用中。

## 7. 总结 

在本文中，我们讨论了equals()和hashCode()契约。我们应该记住：

-   如果我们重写equals()，则始终重写hashCode()
-   为值对象覆盖equals()和hashCode() 
-   注意扩展重写了equals()和hashCode()的类的陷阱
-   考虑使用IDE或第三方库来生成equals()和hashCode()方法
-   考虑使用EqualsVerifier来测试我们的实现