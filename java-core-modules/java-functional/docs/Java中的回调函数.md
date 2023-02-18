## 一、概述

回调函数是作为参数传递给另一个函数并在该函数完成或某些事件发生时执行的函数。在大多数编程语言中，回调函数在我们处理异步代码时特别有用。

在本文中，我们将学习 Java 中回调函数的实际用例以及我们如何实现它们。

## 2.实现回调函数

通常，我们可以通过暴露接口并接受其实现作为参数来在 Java 中创建回调函数。这样的回调可以同步或异步调用。

### 2.1. 同步回调

**同步操作是指一项任务需要在另一项任务开始之前完成的操作**。

例如，想象这样一个接口：

```java
public interface EventListener {

    String onTrigger();
}复制
```

上面的代码片段声明了一个*EventListener*接口，其中包含一个返回类型为*String 的**onTrigger()*方法。这将是我们的回调。

接下来，让我们声明一个实现这个接口的具体类：

```java
public class SynchronousEventListenerImpl implements EventListener {

    @Override
    public String onTrigger(){
        return "Synchronously running callback function";
    }
}复制
```

SynchronousEventListenerImpl类*实现*了*EventListener*接口，如上所示。

接下来，让我们创建一个*SynchronousEventConsumer类，它包含**EventListener*接口的一个实例并调用它的*onTrigger()*方法：

```java
public class SynchronousEventConsumer {

    private final EventListener eventListener;

    // constructor

    public String doSynchronousOperation(){
        System.out.println("Performing callback before synchronous Task");
        // any other custom operations
           return eventListener.onTrigger();
    }
}复制
```

SyncronousEventConsumer类有一个*EventListener*属性，它通过其构造函数初始化*。*当调用*doSynchronousOperation()*方法时，它返回从属于*EventListener的**onTrigger()*方法获得的值。

下面我们写一个测试来演示doSynchronousOperation *()*方法调用监听变量的*onTrigger()方法并获取它的返回值：*

```java
EventListener listener = new SynchronousEventListenerImpl();
SynchronousEventConsumer synchronousEventConsumer = new SynchronousEventConsumer(listener);
String result = synchronousEventConsumer.doSynchronousOperation();

assertNotNull(result);
assertEquals("Synchronously running callback function", result);复制
```

### 2.2. 异步回调函数

[异步操作](https://www.baeldung.com/java-asynchronous-programming)是彼此并行运行的操作。与上一节中说明的同步操作不同，**异步任务是非阻塞的**。他们在执行操作之前不会互相等待。让我们更新*EventListener*接口来说明 Java 中的异步回调函数：

```java
public interface EventListener {

    String onTrigger();

    void respondToTrigger();
}复制
```

接下来，让我们为修改后的*EventListener*创建一个实现：

```java
public class AsynchronousEventListenerImpl implements EventListener {

    @Override
    public String onTrigger(){
        respondToTrigger();
        return "Asynchronously running callback function";
    }
    @Override
    public void respondToTrigger(){
        System.out.println("This is a side effect of the asynchronous trigger.");
    }
}复制
```

上面的类实现了我们在上一节中声明的*EventListener*接口，并在其重写的*onTrigger()*方法中返回一个*字符串文字*。

*接下来，我们将异步运行onTrigger()*方法的类声明为回调函数：

```java
public class AsynchronousEventConsumer{

    private EventListener listener;

    public AsynchronousEventConsumer(EventListener listener) {
        this.listener = listener;
    }

    public void doAsynchronousOperation(){
        System.out.println("Performing operation in Asynchronous Task");

        new Thread(() -> listener.onTrigger()).start();
    }
}复制
```

上面的AsynchronousEventConsumer类声明了一个*doAsynchronousOperation()方法，该**方法*在新线程中隐式调用*EventListener*的*onTrigger()方法。*

**请注意，这种为每个方法调用创建新\*线程\*的方法是一种反模式，此处仅用于演示目的。生产就绪代码应该依赖于适当大小和调整的线程池。**查看我们的其他一些文章以了解有关[Java 中的并发性的](https://www.baeldung.com/java-concurrency)更多信息。

让我们验证程序确实从*doAsynchronousOperation()方法中调用了**onTrigger()*方法：

```java
EventListener listener = Mockito.mock(AsynchronousEventListenerImpl.class);
AsynchronousEventConsumer synchronousEventListenerConsumer = new AsynchronousEventConsumer(listener);
synchronousEventListenerConsumer.doAsynchronousOperation();

verify(listener, timeout(1000).times(1)).onTrigger();复制
```

### 2.3. 使用消费者

[*消费者*](https://docs.oracle.com/javase/8/docs/api/java/util/function/Consumer.html)是Java[函数式编程](https://www.baeldung.com/java-functional-programming)中常用的函数式接口 接口的实现接受参数并使用提供的参数执行操作但不返回结果。

**使用\*Consumers\*，我们可以将一个方法作为参数传递给另一个方法。这允许我们从父方法中调用和运行内部方法的操作**。

让我们考虑一种增加表示为年龄的给定数字的值的方法。我们可以将初始年龄作为第一个参数传递，并将*消费者*作为第二个方法来增加年龄。

*这是我们如何使用Consumers*将其实现为回调函数的说明：

```java
public class ConsumerCallback {
    public void getAge(int initialAge, Consumer<Integer> callback) {
        callback.accept(initialAge);
    }

    public void increaseAge(int initialAge, int ageDifference, Consumer<Integer> callback) {
        System.out.println("===== Increase age ====");

        int newAge = initialAge + ageDifference;
        callback.accept(newAge);
    }
}复制
```

在上面的*getAge()*方法中，我们将*initialAge*变量作为参数传递给*callback.accept()*方法。accept *()*方法接受一个参数（在本例中为整数），然后在运行时通过作为参数传递给 getAge *()*方法的方法或函数对输入执行任何操作。

increaseAge *()方法将对**initialAge*变量执行递增。*它将initialAge*的值添加到*ageDifference*，然后将结果传递给第三个参数 Consumer 的*accept* *()*方法。

下面是上述实现的演示：

```java
ConsumerCallback consumerCallback = new ConsumerCallback();
int ageDifference = 10;
AtomicInteger newAge1 = new AtomicInteger();
int initialAge = 20;
consumerCallback.getAge(initialAge, (initialAge1) -> {
    consumerCallback.increaseAge(initialAge, ageDifference, (newAge) -> {
        System.out.printf("New age ==> %s", newAge);
        newAge1.set(newAge);
     });
});
assertEquals(initialAge + ageDifference, newAge1.get());复制
```

在上面的代码片段中，我们将一个函数传递给*getAge()*方法。此函数调用*increaseAge()方法并断言**newAge*变量的值等于*initialAge*和*ageDifference*的总和。

*此上下文中的回调函数是传递给getAge()*和*increaseAge()*方法的函数。在每个*getAge()*和*increaseAge()*方法完成其任务后，将触发这些函数以执行任何自定义操作。

## 3.结论

在本文中，我们了解了 Java 中回调函数的概念。我们演示了如何通过接口同步和异步实现回调函数。我们还学习了如何使用 Java *Consumer*功能接口在 Java 中执行回调操作。