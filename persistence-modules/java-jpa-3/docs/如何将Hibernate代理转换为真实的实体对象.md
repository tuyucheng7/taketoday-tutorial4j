## 1. 概述

在本教程中，我们将学习如何将[Hibernate 代理](https://www.baeldung.com/hibernate-proxy-load-method)转换为真实的实体对象。在此之前，我们将了解 Hibernate 何时创建代理对象。然后，我们将讨论为什么 Hibernate 代理很有用。最后，我们将模拟一个需要取消代理对象的场景。

## 2. Hibernate什么时候创建代理对象？

Hibernate 使用代理对象来允许[延迟加载](https://www.baeldung.com/hibernate-lazy-eager-loading)。为了更好地形象化场景，让我们看看PaymentReceipt和Payment实体：

```java
@Entity
public class PaymentReceipt {
    ...
    @OneToOne(fetch = FetchType.LAZY)
    private Payment payment;
    ...
}
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Payment {
    ...
    @ManyToOne(fetch = FetchType.LAZY)
    protected WebUser webUser;
    ...
}
```

例如，加载这些实体中的任何一个都将导致 Hibernate 使用FetchType.LAZY为关联字段创建一个代理对象。

为了演示，让我们创建并运行一个集成测试：

```java
@Test
public void givenPaymentReceipt_whenAccessingPayment_thenVerifyType() {
    PaymentReceipt paymentReceipt = entityManager.find(PaymentReceipt.class, 3L);
    Assert.assertTrue(paymentReceipt.getPayment() instanceof HibernateProxy);
}
```

从测试中，我们加载了一个PaymentReceipt并验证了支付对象不是CreditCardPayment的实例——它是一个HibernateProxy对象。

相反，如果没有延迟加载，之前的测试将失败，因为返回的支付对象将是CreditCardPayment的一个实例。

此外，值得一提的是，Hibernate 正在使用字节码检测来创建代理对象。

为了验证这一点，我们可以在集成测试的断言语句行添加一个断点，并在调试模式下运行它。现在，让我们看看调试器显示了什么：

```elixir
paymentReceipt = {PaymentReceipt@5042} 
 payment = {Payment$HibernateProxy$CZIczfae@5047} "com.baeldung.jpa.hibernateunproxy.CreditCardPayment@2"
  $$_hibernate_interceptor = {ByteBuddyInterceptor@5053} 
```

从调试器中，我们可以看到 Hibernate 正在使用[Byte Buddy](https://www.baeldung.com/byte-buddy)，这是一个用于在运行时动态生成Java类的库。

## 3. Hibernate Proxy 为什么有用？

### 3.1. 用于延迟加载的 Hibernate 代理

我们之前已经对此有所了解。为了赋予它更多的意义，让我们尝试从 PaymentReceipt和Payment实体中删除延迟加载机制：

```java
public class PaymentReceipt {
    ...
    @OneToOne
    private Payment payment;
    ...
}
public abstract class Payment {
    ...
    @ManyToOne
    protected WebUser webUser;
    ...
}
```

现在，让我们快速检索一个PaymentReceipt并从日志中检查生成的 SQL：

```sql
select
    paymentrec0_.id as id1_2_0_,
    paymentrec0_.payment_id as payment_3_2_0_,
    paymentrec0_.transactionNumber as transact2_2_0_,
    payment1_.id as id1_1_1_,
    payment1_.amount as amount2_1_1_,
    payment1_.webUser_id as webuser_3_1_1_,
    payment1_.cardNumber as cardnumb1_0_1_,
    payment1_.clazz_ as clazz_1_,
    webuser2_.id as id1_3_2_,
    webuser2_.name as name2_3_2_ 
from
    PaymentReceipt paymentrec0_ 
left outer join
    (
        select
            id,
            amount,
            webUser_id,
            cardNumber,
            1 as clazz_ 
        from
            CreditCardPayment 
    ) payment1_ 
        on paymentrec0_.payment_id=payment1_.id 
left outer join
    WebUser webuser2_ 
        on payment1_.webUser_id=webuser2_.id 
where
    paymentrec0_.id=?
```

正如我们从日志中看到的， 对 PaymentReceipt的查询 包含多个连接语句。 

现在，让我们在延迟加载的情况下运行它：

```sql
select
    paymentrec0_.id as id1_2_0_,
    paymentrec0_.payment_id as payment_3_2_0_,
    paymentrec0_.transactionNumber as transact2_2_0_ 
from
    PaymentReceipt paymentrec0_ 
where
    paymentrec0_.id=?
```

显然，通过省略所有不必要的连接语句，生成的 SQL 得到了简化。

### 3.2. 用于写入数据的 Hibernate 代理

为了说明这一点，让我们用它来创建一个Payment并为其分配一个WebUser。如果不使用代理，这将导致两个 SQL 语句：一个 用于检索 WebUser的SELECT语句 和一个用于创建Payment 的INSERT语句 。

让我们使用代理创建一个测试：

```java
@Test
public void givenWebUserProxy_whenCreatingPayment_thenExecuteSingleStatement() {
    entityManager.getTransaction().begin();

    WebUser webUser = entityManager.getReference(WebUser.class, 1L);
    Payment payment = new CreditCardPayment(new BigDecimal(100), webUser, "CN-1234");
    entityManager.persist(payment);

    entityManager.getTransaction().commit();
    Assert.assertTrue(webUser instanceof HibernateProxy);
}
```

值得强调的是，我们正在使用entityManager.getReference(…) 来获取代理对象。

接下来，让我们运行测试并检查日志：

```sql
insert 
into
    CreditCardPayment
    (amount, webUser_id, cardNumber, id) 
values
    (?, ?, ?, ?)
```

在这里，我们可以看到，当使用代理时，Hibernate 只执行了一条语句：用于创建付款的INSERT 语句 。 

## 4.场景：需要取消代理

给定我们的域模型，假设我们正在检索PaymentReceipt。正如我们所知，它与一个Payment 实体相关联，该实体具有[Table-per-Class](https://www.baeldung.com/hibernate-inheritance#table-per-class)的继承策略 和延迟获取类型。 

在我们的例子中，根据填充的数据， PaymentReceipt的关联付款 是 CreditCardPayment 类型 。但是，由于我们使用的是延迟加载，因此它将是一个代理对象。

现在，让我们看看 CreditCardPayment 实体：

```java
@Entity
public class CreditCardPayment extends Payment {
    
    private String cardNumber;
    ...
}
```

事实上，如果不解除支付 对象的代理，就不可能从CreditCardPayment 类 中检索c ardNumber 字段 。不管怎样，让我们尝试将 支付 对象转换为 CreditCardPayment，看看会发生什么：

```java
@Test
public void givenPaymentReceipt_whenCastingPaymentToConcreteClass_thenThrowClassCastException() {
    PaymentReceipt paymentReceipt = entityManager.find(PaymentReceipt.class, 3L);
    assertThrows(ClassCastException.class, () -> {
        CreditCardPayment creditCardPayment = (CreditCardPayment) paymentReceipt.getPayment();
    });
}
```

从测试中，我们看到需要将 支付 对象转换为 CreditCardPayment。然而，因为支付对象仍然 是 一个 Hibernate 代理对象，我们遇到了 ClassCastException。

## 5.实体对象的Hibernate代理

从 Hibernate 5.2.10 开始，我们可以使用内置的静态方法来取消代理 Hibernate 实体：

```java
Hibernate.unproxy(paymentReceipt.getPayment());
```

让我们使用这种方法创建最终的集成测试：

```java
@Test
public void givenPaymentReceipt_whenPaymentIsUnproxied_thenReturnRealEntityObject() {
    PaymentReceipt paymentReceipt = entityManager.find(PaymentReceipt.class, 3L);
    Assert.assertTrue(Hibernate.unproxy(paymentReceipt.getPayment()) instanceof CreditCardPayment);
}
```

从测试中，我们可以看到我们已经成功地将一个Hibernate代理转换为一个真实的实体对象。

另一方面，这是 Hibernate 5.2.10 之前的解决方案：

```java
HibernateProxy hibernateProxy = (HibernateProxy) paymentReceipt.getPayment();
LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();
CreditCardPayment unproxiedEntity = (CreditCardPayment) initializer.getImplementation();
```

## 六. 总结

在本教程中，我们学习了如何将 Hibernate 代理转换为真实的实体对象。除此之外，我们还讨论了 Hibernate 代理的工作原理及其有用的原因。然后，我们模拟了一个需要取消代理对象的情况。

最后，我们运行了几个集成测试来演示我们的示例并验证我们的解决方案。