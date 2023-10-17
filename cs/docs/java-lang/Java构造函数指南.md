## 1. 概述

构造函数是[面向对象设计](https://www.baeldung.com/java-polymorphism)的守门人。

在本教程中，我们将看到它们如何充当单个位置，从中初始化正在创建的对象的[内部状态](https://www.baeldung.com/java-inheritance-composition)。

让我们继续前进，创建一个代表银行账户的简单对象。

## 2. 银行账户类

假设我们需要创建一个代表银行账户的类。它将包含名称、创建日期和余额。

另外，让我们覆盖toString方法以将详细信息打印到控制台：

```java
class BankAccount {
    String name;
    LocalDateTime opened;
    double balance;

    @Override
    public String toString() {
        return String.format("%s, %s, %f",
              this.name, this.opened.toString(), this.balance);
    }
}
```

现在，此类包含存储银行帐户信息所需的所有必要字段，但它尚不包含构造函数。

**这意味着如果我们创建一个新对象，字段值将不会被初始化**：

```java
BankAccount account = new BankAccount();
account.toString();
```

运行上面的toString方法将导致异常，因为对象name和opened仍然为null：

```shell
java.lang.NullPointerException
    at cn.tuyucheng.taketoday.constructors.BankAccount.toString(BankAccount.java:12)
    at cn.tuyucheng.taketoday.constructors.ConstructorUnitTest
        .givenNoExplicitContructor_whenUsed_thenFails(ConstructorUnitTest.java:23)
```

## 3. 无参数构造函数

让我们用一个构造函数来解决这个问题：

```java
class BankAccount {
    public BankAccount() {
        this.name = "";
        this.opened = LocalDateTime.now();
        this.balance = 0.0d;
    }
}
```

注意关于我们刚刚编写的构造函数的一些事情。首先，它是一个方法，但它没有返回类型。这是因为构造函数隐式返回它创建的对象的类型。现在调用new BankAccount()将调用上面的构造函数。

其次，它不需要参数。这种特殊类型的构造函数称为无参构造函数。

但是，为什么我们第一次不需要它呢？这是因为**当我们没有显式编写任何构造函数时，编译器会添加一个默认的无参数构造函数**。

这就是为什么我们第一次能够构造对象，即使我们没有显式编写构造函数。默认的无参数构造函数会简单地将所有成员设置为其[默认值](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html)。

对于对象，它为null，这导致了我们之前看到的异常。

## 4. 参数化构造函数

现在，构造函数的一个真正好处是它们可以帮助我们在将状态注入对象时保持封装。

因此，要对这个银行账户做一些真正有用的事情，我们需要能够将一些初始值实际注入到对象中。

为此，**让我们编写一个参数化构造函数，即一个接收一些参数的构造函数**：

```java
class BankAccount {
    public BankAccount() { ... }

    public BankAccount(String name, LocalDateTime opened, double balance) {
        this.name = name;
        this.opened = opened;
        this.balance = balance;
    }
}
```

现在我们可以用我们的BankAccount类做一些有用的事情：

```java
LocalDateTime opened = LocalDateTime.of(2018, Month.JUNE, 29, 06, 30, 00);
BankAccount account = new BankAccount("Tom", opened, 1000.0f); 
account.toString();
```

注意，我们的类现在有2个构造函数。一个显式的、无参数构造函数和一个参数化的构造函数。

我们可以根据需要创建任意数量的构造函数，但我们可能不希望创建太多。这会有点混乱。

如果我们在代码中发现太多构造函数，一些[创建型设计模式](https://www.baeldung.com/creational-design-patterns)可能会有所帮助。

## 5. 复制构造函数

构造函数不必仅限于初始化。它们还可以用于以其他方式创建对象。**想象一下，我们需要能够从现有帐户创建一个新帐户**。

新账户应与旧账户同名、创建日期为当前时间并且没有资金。**我们可以使用复制构造函数来做到这一点**：

```java
public BankAccount(BankAccount other) {
    this.name = other.name;
    this.opened = LocalDateTime.now();
    this.balance = 0.0f;
}
```

现在我们有以下行为：

```java
LocalDateTime opened = LocalDateTime.of(2018, Month.JUNE, 29, 06, 30, 00);
BankAccount account = new BankAccount("Tim", opened, 1000.0f);
BankAccount newAccount = new BankAccount(account);

assertThat(account.getName()).isEqualTo(newAccount.getName());
assertThat(account.getOpened()).isNotEqualTo(newAccount.getOpened());
assertThat(newAccount.getBalance()).isEqualTo(0.0f);
```

## 6. 链式构造函数

当然，我们或许可以推断出一些构造函数的参数，或者给其中参数一些默认值。

例如，我们可以创建一个仅包含名称的新银行帐户。

因此，让我们创建一个带有name参数的构造函数，并为其他参数提供默认值：

```java
public BankAccount(String name, LocalDateTime opened, double balance) {
    this.name = name;
    this.opened = opened;
    this.balance = balance;
}

public BankAccount(String name) {
    this(name, LocalDateTime.now(), 0.0f);
}
```

使用关键字this，我们可以调用另一个构造函数。

我们必须记住，**如果我们想链接超类构造函数，我们必须使用super而不是this**。

另外，请记住**this或super表达式应该始终是构造函数的第一条语句**。

## 7. 值类型

Java中构造函数的一个有趣用途是创建值对象。**值对象是在初始化后不会改变其内部状态的对象**。

**也就是说，对象是不可变的**。Java中的不可变性有点[微妙](https://www.baeldung.com/java-immutable-object)，在创建对象时应该小心。

让我们继续创建一个不可变的类：

```java
class Transaction {
    final BankAccount bankAccount;
    final LocalDateTime date;
    final double amount;

    public Transaction(BankAccount account, LocalDateTime date, double amount) {
        this.bankAccount = account;
        this.date = date;
        this.amount = amount;
    }
}
```

请注意，我们现在在定义类的成员时使用final关键字。这意味着这些成员中的每一个都只能在类的构造函数中进行初始化。以后不能在任何其他方法中重新分配它们。我们可以读取这些值，但不能更改它们。

**如果我们为Transaction类创建多个构造函数，则每个构造函数都需要初始化每个final变量**。否则将导致编译错误。

## 8. 总结

我们已经介绍了构造函数构建对象的不同方式。如果使用得当，构造函数构成了Java中面向对象设计的基本构建块。