## 1. 概述

双重分派是一个技术术语，用于描述根据接收者和参数类型选择要调用的方法的过程。

许多开发人员经常将双重分派与[Strategy Pattern](https://en.wikipedia.org/wiki/Strategy_pattern)混淆。

Java 不支持双重分派，但是我们可以使用一些技术来克服这个限制。

在本教程中，我们将重点展示领域驱动设计 (DDD) 和策略模式背景下的双重分派示例。

## 2.双重派遣

在我们讨论双分派之前，让我们回顾一些基础知识并解释一下单分派实际上是什么。

### 2.1. 单一派遣

单一分派是一种根据接收者运行时类型选择方法实现的方式。在Java中，这基本上与多态性相同。

例如，我们来看看这个简单的折扣策略界面：

```java
public interface DiscountPolicy {
    double discount(Order order);
}
```

DiscountPolicy接口有两个实现。统一的，总是返回相同的折扣：

```java
public class FlatDiscountPolicy implements DiscountPolicy {
    @Override
    public double discount(Order order) {
        return 0.01;
    }
}
```

和第二个实现，它返回基于订单总成本的折扣：

```java
public class AmountBasedDiscountPolicy implements DiscountPolicy {
    @Override
    public double discount(Order order) {
        if (order.totalCost()
            .isGreaterThan(Money.of(CurrencyUnit.USD, 500.00))) {
            return 0.10;
        } else {
            return 0;
        }
    }
}
```

为了这个例子的需要，我们假设Order类有一个totalCost()方法。

现在，Java 中的单一分派只是一个非常著名的多态行为，在以下测试中得到了证明：

```java
@DisplayName(
    "given two discount policies, " +
    "when use these policies, " +
    "then single dispatch chooses the implementation based on runtime type"
    )
@Test
void test() throws Exception {
    // given
    DiscountPolicy flatPolicy = new FlatDiscountPolicy();
    DiscountPolicy amountPolicy = new AmountBasedDiscountPolicy();
    Order orderWorth501Dollars = orderWorthNDollars(501);

    // when
    double flatDiscount = flatPolicy.discount(orderWorth501Dollars);
    double amountDiscount = amountPolicy.discount(orderWorth501Dollars);

    // then
    assertThat(flatDiscount).isEqualTo(0.01);
    assertThat(amountDiscount).isEqualTo(0.1);
}
```

如果这一切看起来很简单，请继续关注。稍后我们将使用相同的示例。

我们现在准备引入双重分派。

### 2.2. 双重分派与方法重载

双重分派根据接收者类型和参数类型确定在运行时调用的方法。

Java 不支持双重分派。

请注意，双重分派经常与方法重载相混淆，它们不是一回事。方法重载仅根据编译时信息(如变量的声明类型)选择要调用的方法。

以下示例详细解释了此行为。

让我们介绍一个名为SpecialDiscountPolicy的新折扣接口：

```java
public interface SpecialDiscountPolicy extends DiscountPolicy {
    double discount(SpecialOrder order);
}
```

SpecialOrder只是扩展了Order而没有添加新的行为。

现在，当我们创建SpecialOrder的实例 但将其声明为普通Order时，则不使用特殊折扣方法：

```java
@DisplayName(
    "given discount policy accepting special orders, " +
    "when apply the policy on special order declared as regular order, " +
    "then regular discount method is used"
    )
@Test
void test() throws Exception {
    // given
    SpecialDiscountPolicy specialPolicy = new SpecialDiscountPolicy() {
        @Override
        public double discount(Order order) {
            return 0.01;
        }

        @Override
        public double discount(SpecialOrder order) {
            return 0.10;
        }
    };
    Order specialOrder = new SpecialOrder(anyOrderLines());

    // when
    double discount = specialPolicy.discount(specialOrder);

    // then
    assertThat(discount).isEqualTo(0.01);
}
```

因此，方法重载不是双重分派。

即使Java不支持双重分派，我们也可以使用一种模式来实现类似的行为：[Visitor](https://en.wikipedia.org/wiki/Visitor_pattern)。

### 2.3. 访客模式

访问者模式允许我们在不修改现有类的情况下向它们添加新行为。这要归功于模拟双重分派的巧妙技术。

让我们暂时搁置折扣示例，以便介绍访客模式。

想象一下，我们想为每种订单使用不同的模板来生成 HTML 视图。我们可以将此行为直接添加到订单类中，但由于违反了 SRP，这不是最好的主意。

相反，我们将使用访问者模式。

首先，我们需要引入Visitable接口：

```java
public interface Visitable<V> {
    void accept(V visitor);
}
```

我们还将使用一个访问者界面，在我们名为OrderVisitor的案例中：

```java
public interface OrderVisitor {
    void visit(Order order);
    void visit(SpecialOrder order);
}
```

然而，访问者模式的缺点之一是它需要可访问的类来识别访问者。


如果类未设计为支持访问者，则可能很难(如果源代码不可用，甚至不可能)应用此模式。

每个订单类型都需要实现Visitable接口并提供自己的实现，这看起来是相同的，这是另一个缺点。

请注意，添加到Order和SpecialOrder的方法是相同的：

```java
public class Order implements Visitable<OrderVisitor> {
    @Override
    public void accept(OrderVisitor visitor) {
        visitor.visit(this);        
    }
}

public class SpecialOrder extends Order {
    @Override
    public void accept(OrderVisitor visitor) {
        visitor.visit(this);
    }
}
```

不在子类中重新实现accept可能很诱人。但是，如果我们不这样做，那么OrderVisitor.visit(Order)方法将始终被使用，当然，由于多态性。

最后，让我们看看负责创建 HTML 视图的OrderVisitor的实现：

```java
public class HtmlOrderViewCreator implements OrderVisitor {
    
    private String html;
    
    public String getHtml() {
        return html;
    }

    @Override
    public void visit(Order order) {
        html = String.format("<p>Regular order total cost: %s</p>", order.totalCost());
    }

    @Override
    public void visit(SpecialOrder order) {
        html = String.format("<h1>Special Order</h1><p>total cost: %s</p>", order.totalCost());
    }

}
```

以下示例演示了HtmlOrderViewCreator的使用：

```java
@DisplayName(
        "given collection of regular and special orders, " +
        "when create HTML view using visitor for each order, " +
        "then the dedicated view is created for each order"   
    )
@Test
void test() throws Exception {
    // given
    List<OrderLine> anyOrderLines = OrderFixtureUtils.anyOrderLines();
    List<Order> orders = Arrays.asList(new Order(anyOrderLines), new SpecialOrder(anyOrderLines));
    HtmlOrderViewCreator htmlOrderViewCreator = new HtmlOrderViewCreator();

    // when
    orders.get(0)
        .accept(htmlOrderViewCreator);
    String regularOrderHtml = htmlOrderViewCreator.getHtml();
    orders.get(1)
        .accept(htmlOrderViewCreator);
    String specialOrderHtml = htmlOrderViewCreator.getHtml();

    // then
    assertThat(regularOrderHtml).containsPattern("<p>Regular order total cost: .</p>");
    assertThat(specialOrderHtml).containsPattern("<h1>Special Order</h1><p>total cost: .</p>");
}
```

## 3. DDD中的双重调度

在前面的部分中，我们讨论了双重分派和访问者模式。

我们现在终于准备好展示如何在 DDD 中使用这些技术。

让我们回到订单和折扣政策的例子。

### 3.1. 作为策略模式的折扣政策

早些时候，我们介绍了Order类及其计算所有订单行项目总和的totalCost()方法：

```java
public class Order {
    public Money totalCost() {
        // ...
    }
}
```

还有用于计算订单折扣的DiscountPolicy接口。引入此接口是为了允许使用不同的折扣策略并在运行时更改它们。

这种设计比简单地在Order类中硬编码所有可能的折扣策略要灵活得多：

```java
public interface DiscountPolicy {
    double discount(Order order);
}
```

到目前为止我们还没有明确提到这一点，但是这个例子使用了[策略模式](https://en.wikipedia.org/wiki/Strategy_pattern)。DDD 经常使用这种模式来符合[Ubiquitous Language](https://martinfowler.com/bliki/UbiquitousLanguage.html)原则并实现低耦合。在 DDD 世界中，Strategy 模式通常被称为 Policy。

让我们看看如何结合双重派遣技术和折扣政策。

### 3.2. 双重派遣和折扣政策

要正确使用 Policy 模式，将其作为参数传递通常是个好主意。此方法遵循支持更好封装的[“告诉，不询问”](https://martinfowler.com/bliki/TellDontAsk.html)原则。

例如，Order类可能会像这样实现totalCost：

```java
public class Order / ... / {
    // ...
    public Money totalCost(SpecialDiscountPolicy discountPolicy) {
        return totalCost().multipliedBy(1 - discountPolicy.discount(this), RoundingMode.HALF_UP);
    }
    // ...
}
```

现在，假设我们想以不同方式处理每种类型的订单。

例如，在计算特殊订单的折扣时，还有一些其他规则需要SpecialOrder类特有的信息。我们希望避免铸造和反射，同时能够计算每个订单的总成本，并正确应用折扣。

我们已经知道方法重载发生在编译时。因此，自然的问题出现了：我们如何根据订单的运行时类型动态地将订单折扣逻辑分派给正确的方法？

答案？我们需要稍微修改订单类。

根Order类需要在运行时分派给折扣策略参数。实现此目的的最简单方法是添加受保护的applyDiscountPolicy方法：

```java
public class Order / ... / {
    // ...
    public Money totalCost(SpecialDiscountPolicy discountPolicy) {
        return totalCost().multipliedBy(1 - applyDiscountPolicy(discountPolicy), RoundingMode.HALF_UP);
    }

    protected double applyDiscountPolicy(SpecialDiscountPolicy discountPolicy) {
        return discountPolicy.discount(this);
    }
   // ...
}
```

由于这种设计，我们避免了在Order子类的totalCost方法中重复业务逻辑。

让我们展示一个使用演示：

```java
@DisplayName(
    "given regular order with items worth $100 total, " +
    "when apply 10% discount policy, " +
    "then cost after discount is $90"
    )
@Test
void test() throws Exception {
    // given
    Order order = new Order(OrderFixtureUtils.orderLineItemsWorthNDollars(100));
    SpecialDiscountPolicy discountPolicy = new SpecialDiscountPolicy() {

        @Override
        public double discount(Order order) {
            return 0.10;
        }

        @Override
        public double discount(SpecialOrder order) {
            return 0;
        }
    };

    // when
    Money totalCostAfterDiscount = order.totalCost(discountPolicy);

    // then
    assertThat(totalCostAfterDiscount).isEqualTo(Money.of(CurrencyUnit.USD, 90));
}
```

此示例仍然使用访客模式，但版本略有修改。订单类知道SpecialDiscountPolicy(访客)有一些意义并计算折扣。

如前所述，我们希望能够根据Order的运行时类型应用不同的折扣规则。因此，我们需要在每个子类中覆盖受保护的applyDiscountPolicy方法。

让我们在SpecialOrder类中覆盖此方法：

```java
public class SpecialOrder extends Order {
    // ...
    @Override
    protected double applyDiscountPolicy(SpecialDiscountPolicy discountPolicy) {
        return discountPolicy.discount(this);
    }
   // ...
}
```

我们现在可以在折扣政策中使用有关SpecialOrder的额外信息来计算正确的折扣：

```java
@DisplayName(
    "given special order eligible for extra discount with items worth $100 total, " +
    "when apply 20% discount policy for extra discount orders, " +
    "then cost after discount is $80"
    )
@Test
void test() throws Exception {
    // given
    boolean eligibleForExtraDiscount = true;
    Order order = new SpecialOrder(OrderFixtureUtils.orderLineItemsWorthNDollars(100), 
      eligibleForExtraDiscount);
    SpecialDiscountPolicy discountPolicy = new SpecialDiscountPolicy() {

        @Override
        public double discount(Order order) {
            return 0;
        }

        @Override
        public double discount(SpecialOrder order) {
            if (order.isEligibleForExtraDiscount())
                return 0.20;
            return 0.10;
        }
    };

    // when
    Money totalCostAfterDiscount = order.totalCost(discountPolicy);

    // then
    assertThat(totalCostAfterDiscount).isEqualTo(Money.of(CurrencyUnit.USD, 80.00));
}
```

此外，由于我们在订单类中使用多态行为，我们可以轻松修改总成本计算方法。

## 4. 总结

在本文中，我们学习了如何在领域驱动设计中使用双重分派技术和策略(也称为策略)模式。