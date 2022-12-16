## 1. 概述

[SOLID设计原则](SOLID原则的可靠指南.md)由Robert C.Martin在其2000年的论文[设计原则和设计模式](https://fi.ort.edu.uy/innovaportal/file/2032/1/design_principles.pdf)中引入，SOLID设计原则帮助我们创建更易于维护、更易于理解和更灵活的软件。在本文中，我们介绍里氏替换原则，即首字母缩写词中的“L”。

## 2. 开放/封闭原则

要理解里氏替换原则，我们必须首先了解开闭原则(SOLID中的“O”)。

开放/封闭原则的目标鼓励我们设计我们的软件，以便我们仅**通过添加新代码来添加新功能**。如果这是可能的，我们就会松散耦合，从而更容易维护应用程序。

## 3. 示例用例

让我们看一个银行应用程序示例，以进一步了解开放/封闭原则。

### 3.1 没有开放/封闭原则

我们的银行应用程序支持两种账户类型：“活期”和“储蓄”，它们分别由类CurrentAccount和SavingsAccount表示。

BankingAppWithdrawalService为其用户提供取款功能：

<img src="../assets/img_2.png">

不幸的是，扩展此设计存在问题。BankingAppWithdrawalService知道账户的两个具体实现，因此，每次引入新帐户类型时都需要更改BankingAppWithdrawalService。

### 3.2 使用开放/封闭原则使代码可扩展

让我们重新设计解决方案以符合开放/封闭原则。当需要新的帐户类型时，我们将关闭BankingAppWithdrawalService的修改，方法是使用帐户基类：

<img src="../assets/img_3.png">

在这里，我们引入了一个新的抽象Account类，它由CurrentAccount和SavingsAccount扩展。

BankingAppWithdrawalService不再依赖于具体的账户类，因为它现在只依赖于抽象类，所以在引入新的帐户类型时不需要更改它。

因此，BankingAppWithdrawalService对新帐户类型的**扩展开放**，但对**修改关闭**，因为新类型不需要更改它即可集成。

### 3.3 Java代码

让我们看看Java中的这个例子。首先，我们定义Account类：

```java
public abstract class Account {
    protected abstract void deposit(BigDecimal amount);

    /**
     * Reduces the balance of the account by the specified amount
     * provided given amount > 0 and account meets minimum available
     * balance criteria.
     *
     * @param amount
     */
    protected abstract void withdraw(BigDecimal amount);
}
```

然后，我们定义BankingAppWithdrawalService：

```java
public class BankingAppWithdrawalService {
    private Account account;

    public BankingAppWithdrawalService(Account account) {
        this.account = account;
    }

    public void withdraw(BigDecimal amount) {
        account.withdraw(amount);
    }
}
```

现在，让我们看看在此设计中，新帐户类型如何可能违反违反里氏替换原则。

### 3.4 新账户类型

该银行现在希望为其客户提供高息定期存款账户。为了支持这一点，让我们引入一个新的FixedTermDepositAccount类。现实世界中的定期存款账户是一种(is a)账户类型，这意味着我们面向对象设计中的继承。

因此，我们让FixedTermDepositAccount成为Account的子类：

```java
public class FixedTermDepositAccount extends Account {
    // Overridden methods...
}
```

到目前为止，一切都很好。但是，银行不希望允许定期存款账户提款。

这意味着新的FixedTermDepositAccount类无法有意义地提供Account定义的withdraw方法，一个常见的解决方法是让FixedTermDepositAccount在它无法实现的方法中抛出UnsupportedOperationException：

```java
public class FixedTermDepositAccount extends Account {
    @Override
    protected void deposit(BigDecimal amount) {
        // Deposit into this account
    }

    @Override
    protected void withdraw(BigDecimal amount) {
        throw new UnsupportedOperationException("Withdrawals are not supported by FixedTermDepositAccount!!");
    }
}
```

### 3.5 使用新帐户类型进行测试

虽然新类工作正常，但让我们尝试将它与BankingAppWithdrawalService一起使用：

```java
Account myFixedTermDepositAccount = new FixedTermDepositAccount();
myFixedTermDepositAccount.deposit(new BigDecimal(1000.00));

BankingAppWithdrawalService withdrawalService = new BankingAppWithdrawalService(myFixedTermDepositAccount);
withdrawalService.withdraw(new BigDecimal(100.00));
```

不出所料，银行应用程序因错误而崩溃：

```shell
Withdrawals are not supported by FixedTermDepositAccount!!
```

如果对象的有效组合导致错误，则此设计显然存在问题。

### 3.6 什么地方出了错？

BankingAppWithdrawalService是Account类的客户端，它期望Account及其子类型都保证Account类为其withdraw方法指定的行为：

```java
/**
 * Reduces the account balance by the specified amount
 * provided given amount > 0 and account meets minimum available
 * balance criteria.
 *
 * @param amount
 */
protected abstract void withdraw(BigDecimal amount);
```

但是，由于不支持withdraw方法，FixedTermDepositAccount违反了此方法规范。因此，我们无法可靠地将FixedTermDepositAccount替换为Account。换句话说，FixedTermDepositAccount违反了里氏替换原则。

### 3.7 我们不能处理BankingAppWithdrawalService中的错误吗？

我们可以修改设计，使Account的withdraw方法的客户端必须知道调用它时可能出现的错误。然而，这意味着客户端必须对意外的子类型行为有特殊的了解，这开始打破开闭原则。

换句话说，为了使开放/封闭原则正常工作，所有**子类型都必须可以替代它们的超类型，而无需修改客户端代码**。遵守里氏替换原则可确保这种可替换性。

现在让我们详细了解里氏替换原则。

## 4. 里氏替换原则

### 4.1 定义

Robert C.Martin总结道：

>   子类型必须可以替代它们的基类型。

Barbara Liskov在1988年对其进行了定义，并提供了一个更数学化的定义：

>   如果对于S类型的每个对象o1都有一个T类型的对象o2，使得对于所有根据T定义的程序P，当o1替换为o2时P的行为保持不变，那么S是T的子类型。

### 4.2 什么时候子类型可以替代它的超类型？

子类型不会自动成为其超类型的替代品，**若要可替换，子类型的行为必须与其超类型类似**。

对象的行为是其客户可以依赖的契约，行为由公共方法、对其输入施加的任何约束、对象经历的任何状态更改以及方法执行的副作用指定。

Java中的子类型要求基类的属性和方法在子类中可用。

但是，[行为子类型化](https://en.wikipedia.org/wiki/Behavioral_subtyping)意味着子类型不仅提供超类型中的所有方法，而且还**必须遵守超类型的行为规范**，这确保了客户端对超类型行为所做的任何假设都被子类型满足。

这是里氏代换原则给面向对象设计带来的额外约束。现在，让我们重构我们的银行应用程序来解决我们之前遇到的问题。

## 5. 重构

为了解决我们在银行示例中发现的问题，让我们从了解根本原因开始。

### 5.1 根本原因

在示例中，我们的FixedTermDepositAccount不是Account的行为子类型。

Account的设计错误地假设所有Account类型都允许提款，因此，Account的所有子类型，包括不支持取款的FixedTermDepositAccount，都继承了withdraw方法。

虽然我们可以通过扩展Account的合同来解决这个问题，但还有其他解决方案。

### 5.2 修改后的类图

让我们以不同的方式设计我们的Account层次结构：

<img src="../assets/img_4.png">

因为并非所有账户都支持取款，我们将withdraw方法从Account类移到了一个新的抽象子类WithdrawableAccount中。CurrentAccount和SavingsAccount都允许提款，因此他们现在已经成为新的WithdrawableAccount的子类。

这意味着BankingAppWithdrawalService可以信任正确类型的帐户来提供取款功能。

### 5.3 重构BankingAppWithdrawalService

BankingAppWithdrawalService现在需要使用WithdrawableAccount：

```java
public class BankingAppWithdrawalService {
    private WithdrawableAccount withdrawableAccount;

    public BankingAppWithdrawalService(WithdrawableAccount withdrawableAccount) {
        this.withdrawableAccount = withdrawableAccount;
    }

    public void withdraw(BigDecimal amount) {
        withdrawableAccount.withdraw(amount);
    }
}
```

至于FixedTermDepositAccount，我们保留Account作为它的父类。因此，它只继承它可以可靠地实现的存款行为，而不再继承它不需要的提款方法。这种新设计避免了我们之前看到的问题。

## 6. 规则

现在让我们看一些关于方法签名、不变量、前提条件和后置条件的规则/技术，我们可以遵循并使用这些规则/技术来确保我们创建行为良好的子类型。

在他们的[《Java程序开发：抽象、规范和面向对象设计》](https://www.oreilly.com/library/view/program-development-in/9780768685299/)一书中，Barbara Liskov和John Guttag将这些规则分为三类：签名规则、属性规则和方法规则。

其中一些实践已经由Java的压倒一切的规则强制执行。

我们应该在这里注意一些术语，宽类型更通用-例如Object可以表示任何Java对象，并且比CharSequence更宽，其中String非常具体，因此更窄。

### 6.1 签名规则-方法参数类型

此规则指出，**重写的子类型方法参数类型可以与超类型方法参数类型相同或更宽**。

Java的方法重写规则通过强制重写的方法参数类型与超类型方法完全匹配来支持此规则。

### 6.2 签名规则-返回类型

**重写的子类型方法的返回类型可以比超类型方法的返回类型窄**，这称为返回类型的[协方差](https://en.wikipedia.org/wiki/Covariance_and_contravariance_(computer_science)#Covariant_method_return_type)(也称为协变)，协方差表示何时接受子类型代替超类型。Java支持返回类型的协方差，让我们看一个例子：

```java
public abstract class Foo {
    public abstract Number generateNumber();
    // Other Methods
}
```

Foo中的generateNumber方法的返回类型为Number，现在让我们通过返回更窄类型的Integer来重写此方法：

```java
public class Bar extends Foo {
    @Override
    public Integer generateNumber() {
        return new Integer(10);
    }
    // Other Methods
}
```

因为Integer是一个(is a)Number，所以期望Number的客户端代码可以毫无问题地将Foo替换为Bar。

另一方面，如果Bar中的重写方法返回比Number更广泛的类型，例如Object，则可能包括Object的任何子类型，例如Truck。任何依赖于Number返回类型的客户端代码都无法处理Truck！

幸运的是，Java的方法重写规则可防止重写方法返回更广泛的类型。

### 6.3 签名规则-异常

**与超类型方法相比，子类型方法可以引发更少或更窄(但不是任何附加或更宽)的异常**。

这是可以理解的，因为当客户端代码替换一个子类型时，它可以处理比超类型方法抛出更少异常的方法。但是，如果子类型的方法抛出新的或更广泛的受检异常，它会破坏客户端代码。

Java的方法重写规则已经对受检的异常强制执行此规则。但是，无论被重写的方法是否声明了异常，**Java中的重写方法都可以抛出任何RuntimeException**。

### 6.4 属性规则-类不变量

[类不变量](https://en.wikipedia.org/wiki/Class_invariant)是关于对象属性的断言，该断言对于对象的所有有效状态都必须为真。

让我们看一个例子：

```java
public abstract class Car {
    protected int limit;

    // invariant: speed < limit;
    protected int speed;

    // postcondition: speed < limit
    protected abstract void accelerate();

    // Other methods...
}
```

Car类指定了一个类不变量，即speed必须始终低于limit。不变量规则指出，**所有子类型方法(继承的和新的)必须维护或加强超类型的类不变量**。

让我们定义一个保留类不变量的Car的子类：

```java
public class HybridCar extends Car {
    // invariant: charge >= 0;
    private int charge;

    @Override
    // postcondition: speed < limit
    protected void accelerate() {
        // Accelerate HybridCar ensuring speed < limit
    }

    // Other methods...
}
```

在此示例中，Car中的不变量由HybridCar中重写的speed方法保留，HybridCar还定义了自己的类不变量charge >= 0，这非常好。

相反，如果子类型未保留类不变量，则会破坏依赖于超类型的任何客户端代码。

### 6.5 属性规则-历史约束

历史约束指出**子类方法(继承的或新的)不应该允许基类不允许的状态更改**。

让我们看一个例子：

```java
public abstract class Car {

    // Allowed to be set once at the time of creation.
    // Value can only increment thereafter.
    // Value cannot be reset.
    protected int mileage;

    public Car(int mileage) {
        this.mileage = mileage;
    }

    // Other properties and methods...
}
```

Car类指定了对mileage属性的约束，mileage属性只能在创建时设置一次，之后不能重置。

现在让我们定义一个扩展Car的ToyCar：

```java
public class ToyCar extends Car {
    public void reset() {
        mileage = 0;
    }

    // Other properties and methods
}
```

ToyCar有一个额外的方法reset可以重置mileage属性。在这样做时，ToyCar忽略了其父项对mileage属性强加的约束，这会破坏任何依赖于约束的客户端代码。因此，ToyCar不能替代Car。

同样，如果基类有一个不可变的属性，则子类不应该允许修改这个属性。这就是为什么[不可变类]()应该是final的原因。

### 6.6 方法规则-先决条件

在执行方法之前，应该满足[先决条件](https://en.wikipedia.org/wiki/Precondition)。让我们看一个关于参数值的前提条件的例子：

```java
public class Foo {

    // precondition: 0 < num <= 5
    public void doStuff(int num) {
        if (num <= 0 || num > 5) {
            throw new IllegalArgumentException("Input out of range 1-5");
        }
        // some logic here...
    }
}
```

此处，doStuff方法的前提条件规定num参数值必须介于1和5之间，我们已经通过方法内部的范围检查强制执行了此前提条件。**子类型可以削弱(但不能加强)它所覆盖的方法的前提条件**，当子类型削弱前提条件时，它会放松超类型方法施加的约束。

现在让我们用一个弱化的前提条件重写doStuff方法：

```java
public class Bar extends Foo {

    @Override
    // precondition: 0 < num <= 10
    public void doStuff(int num) {
        if (num <= 0 || num > 10) {
            throw new IllegalArgumentException("Input out of range 1-10");
        }
        // some logic here...
    }
}
```

在这里，在被重写的doStuff方法中，前提条件被削弱到0 < num <= 10，从而允许num的值范围更广。所有对Foo.doStuff有效的num值也对Bar.doStuff有效，因此，当Foo.doStuff的客户端在将Foo替换为Bar时，不会注意到有什么不同。

相反，当子类型加强前提条件时(例如，在我们的示例中为0 < num <= 3)，它会应用比超类型更严格的限制。例如，num的值4和5对Foo.doStuff有效，但对Bar.doStuff不再有效，这将破坏不期望这种新的更严格约束的客户端代码。

### 6.7 方法规则-后置条件

[后置条件](https://en.wikipedia.org/wiki/Postcondition)是执行方法后应满足的条件。

让我们看一个例子：

```java
public abstract class Car {

    protected int speed;

    // postcondition: speed must reduce
    protected abstract void brake();

    // Other methods...
}
```

在这里，Car的brake方法指定了一个后置条件，即在方法执行结束时必须降低Car的速度。**子类型可以加强(但不能削弱)它重写的方法的后置条件**，当子类型加强后置条件时，它提供的比超类型方法多。

现在，让我们定义一个Car的派生类来加强这个先决条件：

```java
public class HybridCar extends Car {

    // Some properties and other methods...

    @Override
    // postcondition: speed must reduce
    // postcondition: charge must increase
    protected void brake() {
        // Apply HybridCar brake
    }
}
```

HybridCar中的重写break方法通过额外确保charge也增加来加强后置条件。因此，任何依赖于Car类中brake方法的后置条件的客户端代码在用HybridCar替换Car时都不会注意到任何区别。

相反，如果HybridCar削弱了重写brake方法的后置条件，它将不再保证speed会降低。如果使用HybridCar代替Car，这可能会破坏客户端代码。

## 7. 代码异味

我们如何才能在现实世界中发现一个不能替代其超类型的子类型？让我们看一些常见的代码味道，它们是违反里氏替换原则的迹象。

### 7.1 子类型为其无法实现的行为抛出异常

我们在前面的银行应用程序示例中已经看到了这方面的示例。在重构之前，Account类有一个额外的方法withdraw，这是其子类FixedTermDepositAccount不想要的，FixedTermDepositAccount类通过为withdraw方法抛出UnsupportedOperationException来解决这个问题。然而，这只是为了掩盖继承层次建模中的弱点而进行的黑客攻击。

### 7.2 子类型没有为其无法实现的行为提供任何实现

这是上述代码味道的变体。子类型无法实现行为，因此它在重写的方法中不执行任何操作。

下面是一个示例，让我们定义一个FileSystem接口：

```java
public interface FileSystem {
    File[] listFiles(String path);

    void deleteFile(String path) throws IOException;
}
```

然后我们定义一个实现FileSystem的ReadOnlyFileSystem：

```java
public class ReadOnlyFileSystem implements FileSystem {
    public File[] listFiles(String path) {
        // code to list files
        return new File[0];
    }

    public void deleteFile(String path) throws IOException {
        // Do nothing.
        // deleteFile operation is not supported on a read-only file system
    }
}
```

在这里，ReadOnlyFileSystem不支持deleteFile操作，因此不提供实现。

### 7.3 客户了解子类型

如果客户端代码需要使用instanceof或向下转型，那么很可能同时违反了开闭原则和里氏替换原则。

让我们使用FilePurgingJob来说明这一点：

```java
public class FilePurgingJob {
    private FileSystem fileSystem;

    public FilePurgingJob(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public void purgeOldestFile(String path) {
        if (!(fileSystem instanceof ReadOnlyFileSystem)) {
            // code to detect oldest file
            fileSystem.deleteFile(path);
        }
    }
}
```

由于FileSystem模型从根本上与ReadOnlyFileSystem不兼容，因此ReadOnlyFileSystem继承了它不支持的deleteFile方法，此示例代码使用instanceof检查来根据子类型实现执行特殊工作。

### 7.4 子类型方法总是返回相同的值

这是一种比其他违规行为更微妙的违规行为，也更难被发现。在此示例中，ToyCar始终为remainingFuel属性返回一个固定值：

```java
public class ToyCar extends Car {

    @Override
    protected int getRemainingFuel() {
        return 0;
    }
}
```

它取决于接口和值的含义，但通常硬编码对象的可变状态值应该是一个标志，表明子类没有实现其整个超类型，并且不能真正替代它。

## 8. 总结

在本文中，我们了解了里氏替换设计原则。里氏替换原则帮助我们建模良好的继承层次结构，它可以帮助我们防止不符合开放/封闭原则的模型层次结构，任何遵循里氏替换原则的继承模型都将隐含地遵循开放/封闭原则。

首先，我们查看了一个用例，该用例试图遵循开放/封闭原则，但违反了里氏替换原则。接下来，我们研究了里氏替换原则的定义、行为子类型的概念以及子类型必须遵循的规则。

最后，我们介绍了一些常见的代码异味，它们可以帮助我们检测现有代码中的违规行为。