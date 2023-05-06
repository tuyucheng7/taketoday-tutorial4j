## 1. 概述

作用域值使开发人员能够**在线程内和线程间存储和共享不可变数据**。这个新的API在Java 20中作为[JEP 439](https://openjdk.org/jeps/429)中提出的孵化器预览功能引入。

在本教程中，我们将首先比较作用域值与[线程局部](https://www.baeldung.com/java-threadlocal)变量，线程局部变量是具有类似目的的旧API。然后，我们将研究应用作用域值在线程之间共享数据、重新绑定值以及在子线程中继承它们。接下来，我们将了解如何在经典Web框架中应用作用域值。

最后，我们将了解如何在Java 20中启用此孵化器功能以进行试验。

## 2. 动机

复杂的Java应用程序通常包含多个需要在它们之间共享数据的模块和组件。当这些组件在多个线程中运行时，开发人员需要一种在它们之间共享不可变数据的方法。

但是，**不同的线程可能需要不同的数据，并且不应该能够访问或覆盖其他线程拥有的数据**。

### 2.1 线程本地

从Java 1.2开始，我们可以利用线程局部变量在组件之间共享数据，而无需求助于方法参数。线程局部变量只是一个特殊类型ThreadLocal的变量。

尽管它们看起来像普通变量，但**线程局部变量有多个化身，每个线程一个**。将使用的特定化身取决于哪个线程调用getter或setter方法来读取或写入其值。

线程局部变量通常被声明为[公共](https://www.baeldung.com/java-public-keyword)[静态](https://www.baeldung.com/java-static)字段，因此它们可以很容易地被许多组件访问。

### 2.2 缺点

尽管线程局部变量自1998年以来就可用，但该**API包含三个主要的设计缺陷**。

首先，每个线程局部变量都是可变的，并且允许任何代码随时调用setter方法。因此，数据可以在组件之间以任何方向流动，这使得很难理解哪个组件更新共享状态以及以什么顺序更新。

其次，当我们使用set方法写入线程的化身时，数据会在线程的整个生命周期内保留，或者直到线程调用remove方法。如果开发人员忘记调用remove方法，数据将在内存中保留的时间超过必要的时间。

最后，父线程的线程局部变量可以被子线程继承。当我们创建继承线程局部变量的子线程时，新线程将需要为所有父线程局部变量分配额外的存储空间。

### 2.3 虚拟线程

随着Java 19中[虚拟线程](https://www.baeldung.com/java-virtual-thread-vs-thread)的可用性，线程局部变量的缺点变得更加紧迫。

虚拟线程是由JDK而不是操作系统管理的轻量级线程。因此，许多虚拟线程共享同一个操作系统线程，允许开发人员使用大量虚拟线程。

由于虚拟线程是Thread的实例，因此它们可以使用线程局部变量。但是，**如果数百万个虚拟线程具有可变的线程局部变量，则内存占用量可能会很大**。

因此，Java 20引入了作用域值API作为一种解决方案，以维护为支持数百万个虚拟线程而构建的不可变和可继承的每线程(pre-thread)数据。

## 3. 作用域值

**作用域值可以在组件之间安全高效地共享不可变数据，而无需求助于方法参数**。作为[Loom](https://www.baeldung.com/openjdk-project-loom)项目的一部分，它们与虚拟线程和[结构化并发](https://www.baeldung.com/java-structured-concurrency)一起开发。

### 3.1 在线程之间共享数据

与线程局部变量类似，作用域值使用多个化身，每个线程一个。此外，它们通常被声明为公共静态字段，可以很容易地被许多组件访问：

```java
public final static ScopedValue<User> LOGGED_IN_USER = ScopedValue.newInstance();
```

另一方面，**作用域值被写入一次，然后是不可变的**。作用域值仅在线程执行的有限时间内可用：

```java
ScopedValue.where(LOGGED_IN_USER, user.get()).run(
    () -> service.getData()
);
```

where方法需要一个作用域值和一个它应该绑定到的对象。当调用run方法时，作用域值被绑定，创建一个对当前线程唯一的化身，然后执行lambda表达式。

在run方法的生命周期内，任何方法(无论是直接还是间接从表达式调用)都能够读取作用域值。但是，当run方法完成时，绑定将被销毁。

作用域变量的有限生命周期和不变性有助于简化关于线程行为的推理。不变性有助于确保更好的性能，并且数据仅以单向方式传输：从调用者到被调用者。

### 3.2 继承作用域值

**使用StructuredTaskScope创建的所有子线程自动继承作用域值**。子线程可以使用为父线程中的作用域值建立的绑定：

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<Optional<Data>> internalData = scope.fork(
        () -> internalService.getData(request)
    );
    Future<String> externalData = scope.fork(externalService::getData);
    try {
        scope.join();
        scope.throwIfFailed();

        Optional<Data> data = internalData.resultNow();
        // Return data in the response and set proper HTTP status
    } catch (InterruptedException | ExecutionException | IOException e) {
        response.setStatus(500);
    }
}
```

在这种情况下，我们仍然可以从通过fork方法创建的子线程中运行的服务访问作用域值。但是，与线程局部变量不同，不会将作用域值从父线程复制到子线程。

### 3.3 重新绑定作用域值

由于作用域值是不可变的，因此它们不支持用于更改存储值的set方法。但是，我们**可以为有限代码段的调用重新绑定一个作用域值**。

例如，我们可以使用where方法通过将其设置为[null](https://www.baeldung.com/java-null)来隐藏run中调用的方法的作用域值：

```java
ScopedValue.where(Server.LOGGED_IN_USER, null).run(service::extractData);
```

但是，一旦该代码段终止，原始值将再次可用。我们应该注意到run方法的返回类型是void。如果我们的服务返回一个值，我们可以使用call方法来启用对返回值的处理。

## 4. Web示例

现在让我们看一个实际示例，说明我们如何在经典Web框架用例中应用作用域值来共享登录用户的数据。

### 4.1 经典Web框架

Web服务器根据传入请求对用户进行身份验证，**并使已登录用户的数据可用于处理请求的代码**：

```java
public void serve(HttpServletRequest request, HttpServletResponse response) throws InterruptedException, ExecutionException {
    Optional<User> user = authenticateUser(request);
    if (user.isPresent()) {
        Future<?> future = executor.submit(() ->
            controller.processRequest(request, response, user.get())
        );
        future.get();
    } else {
        response.setStatus(401);
    }
}
```

处理请求的控制器通过方法参数接收登录用户的数据：

```java
public void processRequest(HttpServletRequest request, HttpServletResponse response, User loggedInUser) {
    Optional<Data> data = service.getData(request, loggedInUser);
    // Return data in the response and set proper HTTP status
}
```

服务还从控制器接收登录用户的数据，但不使用它。相反，它只是将信息传递给Repository：

```java
public Optional<Data> getData(HttpServletRequest request, User loggedInUser) {
    String id = request.getParameter("data_id");
    return repository.getData(id, loggedInUser);
}
```

在Repository中，我们最终使用登录用户的数据来检查用户是否有足够的权限：

```java
public Optional<Data> getData(String id, User loggedInUser) {
    return loggedInUser.isAdmin()
        ? Optional.of(new Data(id, "Title 1", "Description 1"))
        : Optional.empty();
}
```

在更复杂的Web应用程序中，请求处理可以扩展到大量方法。尽管登录用户的数据可能只在少数几个方法中需要，但我们可能需要将其传递给所有这些方法。

使用方法参数传递信息会使我们的代码产生噪音，我们很快就会超过每个方法推荐的三个参数。

### 4.2 应用作用域值

另一种方法是**将登录用户的数据存储在可以从任何方法访问的作用域值中**：

```java
public void serve(HttpServletRequest request, HttpServletResponse response) {
    Optional<User> user = authenticateUser(request);
    if (user.isPresent()) {
        ScopedValue.where(LOGGED_IN_USER, user.get())
            .run(() -> controller.processRequest(request, response));
    } else {
        response.setStatus(401);
    }
}
```

我们现在可以从所有方法中删除loggedInUser参数：

```java
public void processRequest(HttpServletRequest request, HttpServletResponse response) {
    Optional<Data> data = internalService.getData(request);
    // Return data in the response and set proper HTTP status
}
```

我们的服务不必关心将登录用户的数据传递到Repository：

```java
public Optional<Data> getData(HttpServletRequest request) {
    String id = request.getParameter("data_id");
    return repository.getData(id);
}
```

相反，Repository可以通过调用作用域值的get方法来检索登录用户的数据：

```java
public Optional<Data> getData(String id) {
    User loggedInUser = Server.LOGGED_IN_USER.get();
    return loggedInUser.isAdmin()
        ? Optional.of(new Data(id, "Title 1", "Description 1"))
        : Optional.empty();
}
```

在此示例中，应用作用域值可确保我们的代码更具可读性和可维护性。

### 4.3 运行孵化器预览

要运行上面的示例并在Java 20中试验作用域值，我们**需要启用预览功能并添加并发孵化器模块**：

```shell
$ javac --enable-preview -source 20 --add-modules jdk.incubator.concurrent *.java
$ java --enable-preview --add-modules jdk.incubator.concurrent Server.class
```

通过向[compile](https://www.baeldung.com/maven-compiler-plugin)和[surefire](https://www.baeldung.com/maven-surefire-plugin)插件添加相同的两个参数，可以使用Maven实现相同的目的：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>20</source>
        <target>20</target>
        <compilerArgs>
            <arg>--enable-preview</arg>
            <arg>--add-modules=jdk.incubator.concurrent</arg>
        </compilerArgs>
    </configuration>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>--enable-preview --add-modules=jdk.incubator.concurrent</argLine>
    </configuration>
</plugin>
```

## 5. 总结

在本文中，我们探讨了作用域值，这是Java 20的孵化器预览功能。我们将作用域值与线程局部变量进行了比较，并解释了创建新API以在线程内和线程间共享不可变数据的动机。

我们探讨了如何使用作用域值在线程之间共享数据、重新绑定它们的值以及在子线程中继承它们。然后，我们看到了如何在经典的Web框架示例中应用作用域值来共享登录用户的数据。最后，我们看到启用孵化器预览以在Java 20中试验作用域值。