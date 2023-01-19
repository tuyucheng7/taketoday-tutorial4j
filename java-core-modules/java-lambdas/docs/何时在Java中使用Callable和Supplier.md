## 一、概述

在本教程中，我们将讨论结构相似但使用不同的*Callable*和*Supplier* [功能接口。](https://www.baeldung.com/java-8-functional-interfaces)

两者都返回一个类型值并且不接受任何参数。执行上下文是确定差异的判别式。

在本教程中，我们将重点关注异步任务的上下文。

## 2.型号

在我们开始之前，让我们定义一个类：

```java
public class User {

    private String name;
    private String surname;
    private LocalDate birthDate;
    private Integer age;
    private Boolean canDriveACar = false;

    // standard constructors, getters and setters
}复制
```

## 3.*可调用*

*Callable*是 Java 版本 5 中引入的接口，在版本 8 中演变为函数式接口。

**它的SAM（Single Abstract Method）是方法\*call()\*，返回一个泛型值，可能会抛出异常：**

```java
V call() throws Exception;复制
```

它旨在封装一个应该由另一个线程执行的任务，例如[*Runnable*](https://www.baeldung.com/java-runnable-callable)接口。那是因为*Callable*实例可以通过*[ExecutorService 执行](https://www.baeldung.com/java-executor-service-tutorial)*。

那么让我们定义一个实现：

```java
public class AgeCalculatorCallable implements Callable<Integer> {

    private final LocalDate birthDate;

    @Override
    public Integer call() throws Exception {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // standard constructors, getters and setters
}复制
```

当*call()*方法返回一个值时，主线程检索它以执行其逻辑。为此，我们可以使用[*Future*](https://www.baeldung.com/java-future)，一个在另一个线程上执行的任务完成时跟踪并获取值的对象。

### 3.1. 单一任务

让我们定义一个只执行一个异步任务的方法：

```java
public User execute(User user) {
    ExecutorService executorService = Executors.newCachedThreadPool();
    try {
        Future<Integer> ageFuture = executorService.submit(new AgeCalculatorCallable(user.getBirthDate()));
        user.setAge(age.get());
    } catch (ExecutionException | InterruptedException e) {
        throw new RuntimeException(e.getCause());
    }
    return user;
}复制
```

我们可以通过 lambda 表达式重写*submit()*的内部块：

```java
Future<Integer> ageFuture = executorService.submit(
  () -> Period.between(user.getBirthDate(), LocalDate.now()).getYears());复制
```

当我们尝试通过调用*get()*方法访问返回值时，我们必须处理两个已检查的异常：

-   *InterruptedException*：当线程处于休眠、活动或占用状态时发生中断时抛出
-   *ExecutionException*：当通过抛出异常中止任务时抛出。换句话说，它是一个包装器异常，中止任务的真正异常是原因（可以使用*getCause* () 方法检查）。

### 3.2. 任务链

执行属于链的任务取决于先前任务的状态。如果其中之一失败，则无法执行当前任务。

所以让我们定义一个新的*Callable*：

```java
public class CarDriverValidatorCallable implements Callable<Boolean> {

    private final Integer age;

    @Override
    public Boolean call() throws Exception {
        return age > 18;
    }
    // standard constructors, getters and setters
}复制
```

接下来，让我们定义一个任务链，其中第二个任务将前一个任务的结果作为输入参数：

```java
public User execute(User user) {
    ExecutorService executorService = Executors.newCachedThreadPool();
    try {
        Future<Integer> ageFuture = executorService.submit(new AgeCalculatorCallable(user.getBirthDate()));
        Integer age = ageFuture.get();
        Future<Boolean> canDriveACarFuture = executorService.submit(new CarDriverValidatorCallable(age));
        Boolean canDriveACar = canDriveACarFuture.get();
        user.setAge(age);
        user.setCanDriveACar(canDriveACar);
    } catch (ExecutionException | InterruptedException e) {
        throw new RuntimeException(e.getCause());
    }
    return user;
}复制
```

在任务链中使用*Callable*和*Future*有一些问题：

-   链中的每个任务都遵循“提交-获取”模式。在长链中，这会产生冗长的代码。
-   当链可以容忍任务失败时，我们应该创建一个专用的*try* / *catch*块。
-   调用时，*get()*方法会一直等待，直到*Callable*返回一个值。所以链的总执行时间等于所有任务执行时间的总和。但是，如果下一个任务仅依赖于前一个任务的正确执行，则链式过程会大大减慢。

## 4.*供应商*

*Supplier*是一个功能接口，其 SAM（单一抽象方法）是*get()*。

**它不接受任何参数，返回一个值，并且只抛出未经检查的异常：**

```java
T get();复制
```

此接口最常见的用例之一是推迟执行某些代码。

[*Optional*](https://www.baeldung.com/java-optional)类有一些方法接受*Supplier*作为参数，例如*Optional.or()*、*Optional.orElseGet() 。*

所以*Supplier*只有在*Optional*为空的时候才会执行。

我们还可以在异步计算上下文中使用它，特别是在[CompletableFuture](https://www.baeldung.com/java-completablefuture) API 中。

一些方法接受*Supplier*作为参数，例如*supplyAsync()*方法。

### 4.1. 单一任务

让我们定义一个只执行一个异步任务的方法：

```java
public User execute(User user) {
    ExecutorService executorService = Executors.newCachedThreadPool();
    CompletableFuture<Integer> ageFut = CompletableFuture.supplyAsync(() -> Period.between(user.getBirthDate(), LocalDate.now())
      .getYears(), executorService)
      .exceptionally(throwable -> {throw new RuntimeException(throwable);});
    user.setAge(ageFut.join());
    return user;
}复制
```

在这种情况下，lambda 表达式定义了*Supplier*，但我们也可以定义一个实现类。感谢*CompletableFuture，*我们为异步操作定义了一个模板，使其更易于理解和修改。

*join()*方法提供供应商的返回值*。*

### 4.2. 任务链

*我们还可以在Supplier*接口和*CompletableFuture*的支持下开发一系列任务：

```java
public User execute(User user) {
    ExecutorService executorService = Executors.newCachedThreadPool();
    CompletableFuture<Integer> ageFut = CompletableFuture.supplyAsync(() -> Period.between(user.getBirthDate(), LocalDate.now())
      .getYears(), executorService);
    CompletableFuture<Boolean> canDriveACarFut = ageFut.thenComposeAsync(age -> CompletableFuture.supplyAsync(() -> age > 18, executorService))
      .exceptionally((ex) -> false);
    user.setAge(ageFut.join());
    user.setCanDriveACar(canDriveACarFut.join());
    return user;
}复制
```

*使用CompletableFuture* – *Supplier*方法定义异步任务链可以解决之前使用*Future* – *Callable*方法引入的一些问题：

-   链中的每个任务都是孤立的。因此，如果任务执行失败，我们可以通过*exceptionally()*块来处理它。
-   *join()*方法不需要在编译时处理已检查的异常。
-   我们可以设计一个异步任务模板，完善每个任务的状态处理。

## 5.结论

在本文中，我们讨论了*Callable*和*Supplier*接口之间的区别，重点关注异步任务的上下文。

**接口设计级别的主要区别是\*Callable\*抛出的已检查异常。** 

*Callable*不适用于功能上下文。它随着时间的推移而适应，函数式编程和检查异常不相容。

所以任何函数式 API（例如*CompletableFuture* API）总是接受*Supplier*而不是*Callable*。