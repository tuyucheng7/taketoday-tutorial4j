## 1. 简介

在本教程中，我们介绍[SOLID原则](SOLID原则的可靠指南.md)中的接口隔离原则，在“SOLID”中表示“I”。接口隔离简单地说就是我们应该将较大的接口分成较小的接口，从而确保实现类不需要实现不需要的方法。

## 2. 接口隔离原则

该原则首先由Robert C.Martin定义为：“**不应强迫客户依赖他们不使用的接口**”。

此原则的目标是通过**将应用程序接口分解为较小的接口来减少使用较大接口的副作用**，它类似于[单一职责原则](Java中的单一职责原则.md)，其中每个类或接口都服务于单一目的。

精确的应用程序设计和正确的抽象是接口隔离原则背后的关键，**虽然在应用程序的设计阶段会花费更多的时间和精力，并且可能会增加代码的复杂性，但最终我们得到的是灵活的代码**。

我们将在后面的部分中查看一些违反该原则的示例，然后我们通过正确应用该原则来解决问题。

## 3. 示例接口和实现

让我们看一个Payment接口和它的一个实现类的情况：

```java
public interface Payment {
    void initiatePayments();

    Object status();

    List<Object> getPayments();
}
```

和实现：

```java
public class BankPayment implements Payment {

    @Override
    public void initiatePayments() {
        // ...
    }

    @Override
    public Object status() {
        // ...
    }

    @Override
    public List<Object> getPayments() {
        // ...
    }
}
```

为了简单起见，我们忽略了这些方法的实际业务实现。这一点非常清楚-到目前为止，实现类BankPayment需要Payment接口中的所有方法，因此它不违反接口隔离原则。

## 4. 污染接口

现在，随着时间的推移和更多功能的出现，需要添加LoanPayment服务。这个服务也是Payment的一种，只是多了一些操作。

为了开发这个新功能，我们将向Payment接口添加新方法：

```java
public interface Payment {

    // original methods
    // ...
    void intiateLoanSettlement();

    void initiateRePayment();
}
```

接下来，我们将实现LoanPayment：

```java
public class LoanPayment implements Payment {

    @Override
    public void initiatePayments() {
        throw new UnsupportedOperationException("This is not a bank payment");
    }

    @Override
    public Object status() {
        // ...
    }

    @Override
    public List<Object> getPayments() {
        // ...
    }

    @Override
    public void intiateLoanSettlement() {
        // ...
    }

    @Override
    public void initiateRePayment() {
        // ...
    }
}
```

现在，由于Payment接口已更改并添加了更多方法，因此所有实现类现在都必须实现新方法。**问题是，实现它们是不需要的，并且可能导致许多副作用**。在这里，LoanPayment实现类必须实现initiatePayments()但又没有任何实际需要。因此，违反了该原则。

那么，我们的BankPayment类会发生什么：

```java
public class BankPayment implements Payment {

    @Override
    public void initiatePayments() {
        // ...
    }

    @Override
    public Object status() {
        // ...
    }

    @Override
    public List<Object> getPayments() {
        // ...
    }

    @Override
    public void intiateLoanSettlement() {
        throw new UnsupportedOperationException("This is not a loan payment");
    }

    @Override
    public void initiateRePayment() {
        throw new UnsupportedOperationException("This is not a loan payment");
    }
}
```

请注意，BankPayment实现现在已经实现了新方法，由于它不需要这些新方法并且没有它们的逻辑，因此**它只是抛出一个UnsupportedOperationException，这就是我们开始违反原则的地方**。

## 5. 应用原则

在上一节中，我们故意污染了接口并违反了原则，**在本节中，我们介绍如何在不违反原则的情况下添加LoanPayment的新功能**。

让我们分解每种支付类型的接口，目前的情况：

<img src="../assets/img.png">

请注意，在类图中，并参考前面部分中的接口，status()和getPayments()方法在这两个实现中都是必需的。另一方面，initiatePayments()仅在BankPayment中是必需的，而initiateLoanSettlement()和initiateRePayment()方法仅适用于LoanPayment。

排序后，让我们分解接口并应用接口隔离原则。因此，我们现在有一个通用接口：

```java
public interface Payment {
    Object status();

    List<Object> getPayments();
}
```

以及两种支付方式的另外两个接口：

```java
public interface Bank extends Payment {
    void initiatePayments();
}
```

```java
public interface Loan extends Payment {
    void intiateLoanSettlement();

    void initiateRePayment();
}
```

以及相应的实现，从BankPayment开始：

```java
public class BankPayment implements Bank {

    @Override
    public void initiatePayments() {
        // ...
    }

    @Override
    public Object status() {
        // ...
    }

    @Override
    public List<Object> getPayments() {
        // ...
    }
}
```

最后是我们修改后的LoanPayment实现：

```java
public class LoanPayment implements Loan {

    @Override
    public void intiateLoanSettlement() {
        // ...
    }

    @Override
    public void initiateRePayment() {
        // ...
    }

    @Override
    public Object status() {
        // ...
    }

    @Override
    public List<Object> getPayments() {
        // ...
    }
}
```

现在，让我们回顾一下新的类图：

<img src="../assets/img_1.png">

**正如我们所看到的，接口没有违反原则**，实现不必提供空方法，这可以保持代码干净并减少出现错误的机会。

## 6. 总结

在本教程中，我们演示了一个简单的场景，首先我们偏离了接口隔离原则，并看到了这种偏离导致的问题；然后我们演示了如何正确应用该原则以避免这些问题。

如果我们正在处理无法修改的受污染的遗留接口，[适配器模式]()可以派上用场。

接口隔离原则是设计和开发应用程序时的一个重要概念，遵循这一原则有助于避免具有多重职责的臃肿接口，这最终也有助于我们遵循单一职责原则。