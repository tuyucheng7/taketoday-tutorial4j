## 1. 概述

在本教程中，我们介绍GoF行为型设计模式之一：状态模式。

首先，我们概述其目的并解释它试图解决的问题。然后，我们看看状态模式的UML图和实际示例的实现。

## 2. 状态设计模式

**状态模式的主要思想是允许对象在不改变其类的情况下改变其行为**。此外，通过实现它，代码应该在没有许多if/else语句的情况下保持清晰。

想象一下，我们有一个包裹被发送到邮局，包裹本身可以被订购，然后被送到邮局，最后被客户接收。现在，根据实际状态，我们要打印其交付状态。

最简单的方法是添加一些布尔标志，并在类中的每个方法中应用简单的if/else语句。在一个简单的场景中，这不会使它复杂化。然而，当我们要处理更多状态时，它可能会使我们的代码复杂化和污染，这将导致更多的if/else语句。

此外，每个状态的所有逻辑都将分布在所有方法中；现在，这是可以考虑使用状态模式的地方。得益于状态设计模式，我们可以将逻辑封装在专用类中，应用[单一职责原则](https://en.wikipedia.org/wiki/Single_responsibility_principle)和[开放/封闭原则](https://en.wikipedia.org/wiki/Open–closed_principle)，代码更简洁、更易于维护。

## 3. UML图

<img src="../assets/img_2.png">

在UML图中，我们看到Context类有一个关联的State，该State将在程序执行期间发生变化。

我们的上下文会**将行为委托给状态实现**。换句话说，所有传入的请求都将由状态的具体实现来处理。

我们看到逻辑是分离的，添加新状态很简单-如果需要的话，可以归结为添加另一个状态实现。

## 4. 实现

接下来设计我们的应用程序。如前所述，可以订购、交付和接收包裹，因此我们将拥有三种状态和上下文类。

首先，定义我们的上下文，这将是一个Package类：

```java
public class Package {

    private PackageState state = new OrderedState();

    // getter, setter

    public void previousState() {
        state.prev(this);
    }

    public void nextState() {
        state.next(this);
    }

    public void printStatus() {
        state.printStatus();
    }
}
```

正如我们所看到的，它包含一个用于管理状态的引用，请注意我们将任务委托给状态对象的previousState()、nextState()和printStatus()方法。这些状态将相互链接，并且**每个状态都将根据传递给这两种方法的this引用设置另一个状态**。

客户端将与Package类交互，但他不必处理状态设置，客户端所要做的就是转到下一个或上一个状态。

接下来，我们定义具有以下签名的三个方法的PackageState：

```java
public interface PackageState {

    void next(Package pkg);

    void prev(Package pkg);

    void printStatus();
}
```

该接口将由每个具体的状态类实现。

第一个具体状态是OrderedState：

```java
public class OrderedState implements PackageState {

    @Override
    public void next(Package pkg) {
        pkg.setState(new DeliveredState());
    }

    @Override
    public void prev(Package pkg) {
        System.out.println("The package is in its root state.");
    }

    @Override
    public void printStatus() {
        System.out.println("Package ordered, not delivered to the office yet.");
    }
}
```

在这里，我们指向订购包裹后将发生的下一个状态。订购状态是我们的根状态，我们明确标记它，我们可以在这两种方法中看到如何处理状态之间的转换。

让我们看一下DeliveredState类：

```java
public class DeliveredState implements PackageState {

    @Override
    public void next(Package pkg) {
        pkg.setState(new ReceivedState());
    }

    @Override
    public void prev(Package pkg) {
        pkg.setState(new OrderedState());
    }

    @Override
    public void printStatus() {
        System.out.println("Package delivered to post office, not received yet.");
    }
}
```

再一次，我们看到了各状态之间的联系，包裹正在将其状态从已订购更改为已交付，printStatus()中的消息也会发生变化。

最后一个状态是ReceivedState：

```java
public class ReceivedState implements PackageState {

    @Override
    public void next(Package pkg) {
        System.out.println("This package is already received by a client.");
    }

    @Override
    public void prev(Package pkg) {
        pkg.setState(new DeliveredState());
    }
}
```

这是我们到达最后一个状态的地方，我们只能回滚到以前的状态。

## 5. 测试

让我们看看实现的行为。首先，我们验证状态设置转换是否按预期工作：

```java
@Test
void givenNewPackage_whenPackageReceived_thenStateReceived() {
    Package pkg = new Package();

    assertTrue(pkg.getState() instanceOf OrderedState);
    pkg.nextState();

    assertTrue(pkg.getState() instanceOf DeliveredState);
    pkg.nextState();

    assertTrue(pkg.getState() instanceOf ReceivedState);
}
```

然后，检查我们的包裹是否可以返回其状态：

```java
@Test
void givenDeliveredPackage_whenPrevState_thenStateOrdered() {
    Package pkg = new Package();
    pkg.setState(new DeliveredState());
    pkg.previousState();

    assertTrue(pkg.getState() instanceOf OrderedState);
}
```

之后，让我们验证更改状态并查看printStatus()方法的实现如何在运行时更改其实现：

```java
public class StateDemo {

    public static void main(String[] args) {
        Package pkg = new Package();
        pkg.printStatus();

        pkg.nextState();
        pkg.printStatus();

        pkg.nextState();
        pkg.printStatus();

        pkg.nextState();
        pkg.printStatus();
    }
}
```

运行的输出如下：

```shell
Package ordered, not delivered to the office yet.
Package delivered to post office, not received yet.
Package was received by client.
This package is already received by a client.
Package was received by client.
```

由于我们一直在改变上下文的状态，因此行为在改变，但类保持不变，以及我们使用的API。

此外，状态之间的转换已经发生，我们的类改变了它的状态并因此改变了它的行为。

## 6. 缺点

状态模式的缺点是在状态之间实现转换时的回报，这使得状态被硬编码，这通常是一种不好的做法。

但是，根据我们的需求和要求，这可能是也可能不是问题。

## 7. 状态与策略模式

这两种设计模式非常相似，它们的UML图是相同的，但背后的思想略有不同。

首先，**[策略模式]()定义了一系列可互换的算法**。通常，它们实现相同的目标，但实现方式不同，例如排序或渲染算法。

**在状态模式中，行为可能会根据实际状态完全改变**。

接下来，**在策略模式中，客户必须知道可能的策略来明确地使用和改变它们**。而在状态模式中，每个状态都链接到另一个状态，并像在有限状态机中一样创建流。

## 8. 总结

当我们想要**避免原始的if/else语句**时，状态设计模式非常有用。相反，我们**提取逻辑以分离类**，并让我们的**上下文对象将行为委托给在状态类中实现的方法**。此外，我们可以利用状态之间的转换，其中一个状态可以改变上下文的状态。

一般来说，这种设计模式非常适合相对简单的应用程序，但对于更高级的方法，我们可以看看[Spring的状态机教程](https://www.baeldung.com/spring-state-machine)。