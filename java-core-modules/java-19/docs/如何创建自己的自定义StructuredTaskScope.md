## 1. 概述

在某些情况下，我们需要两个以上的StructuredTaskScope内置关机策略。例如，当x个线程已完成或虚拟线程已返回特定结果时。本文将创建一个StructuredTaskScope，当返回满足特定条件的值时，该作用域将关闭。

## 2. 什么是StructuredTaskScope

StructuredTaskScope管理从作用域派生的线程的生命周期。要创建我们的StructuredTaskScope，我们需要扩展StructuredTaskScope类并覆盖handleComplete(Future<T\> future)方法。每个完成的线程都会调用此方法，因此这是在满足特定条件时调用shutdown()方法的好地方。shutdown()方法将取消所有剩余的正在运行的线程。

## 3. 如何创建自己的StructuredTaskScope

在以下示例中，我们创建了一个扩展StructuredTaskScope<Product\>的CriteriaScope。例如，我们将Product类作为泛型传递给StructuredTaskScope。接下来，我们需要重写handleComplete方法，该方法将接收Future作为与泛型相同类型的参数。在这种情况下，这将是Product。

在handleComplete方法中，有一个if首先检查虚拟线程是否已完成。如果是，它将检查结果产品的价格是否小于100。当满足if语句的条件时，我们将结果存储在一个volatile变量中，因此我们可以稍后检索它。我们要做的最后一件事是通过调用shutdown()来关闭剩余的线程。

```java
class CriteriaScope extends StructuredTaskScope<Product> {

    private volatile Product product;

    @Override
    protected void handleComplete(Future<Product> future) {
        if (future.state() == Future.State.SUCCESS && future.resultNow().getPrice() < 100) {
            this.product = future.resultNow();
            shutdown();
        }
    }

    public Product getResult(){
        return product;
    }
}
```

### 3.1 使用结构化作用域

使用自定义的StructuredTaskScope作用域与任何其他内置StructuredTaskScope的工作方式相同。在下面的示例中，我们使用之前创建的CriteriaScope。

```java
public void start() throws InterruptedException {
    try (var scope = new CriteriaScope()) {

        Future<Product> fork = scope.fork(OverrideScope::getProduct50);
        Future<Product> fork1 = scope.fork(OverrideScope::getProduct300);
        Future<Product> fork2 = scope.fork(OverrideScope::getProduct200);

        scope.join();

        System.out.println("fork.state() = " + fork.state());
        System.out.println("fork.state() = " + fork1.state());
        System.out.println("fork.state() = " + fork2.state());

        System.out.println("result: " + scope.getResult().getPrice());
    }
}
```

## 4. 总结

在本文中，我们了解了如何扩展StructuredTaskScope来创建你的自定义作用域。我们覆盖了handleComplete方法以在返回特定结果时关闭作用域，最后一个示例展示了如何在代码中使用我们创建的作用域。