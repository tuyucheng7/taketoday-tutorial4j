## 1. 概述

[JDeferred](http://jdeferred.org/)是一个小型Java库(也支持Groovy)，用于在不编写样板代码的情况下实现异步拓扑。该框架的灵感来自[Jquery 的 Promise/Ajax](https://api.jquery.com/jquery.ajax/)功能和[Android 的延迟对象](https://github.com/CodeAndMagic/android-deferred-object)模式。

在本教程中，我们将展示如何使用JDeferred及其不同的实用程序。

## 2.Maven依赖

通过将以下依赖项添加到我们的pom.xml中，我们可以开始在任何应用程序中使用JDeferred ：

```xml
<dependency>
    <groupId>org.jdeferred</groupId>
    <artifactId>jdeferred-core</artifactId>
    <version>1.2.6</version>
</dependency>
```

我们可以在[Central Maven Repository中查看](https://search.maven.org/classic/#search|gav|1|g%3A"org.jdeferred" AND a%3A"jdeferred-core")JDeferred项目的最新版本。

## 3.承诺

让我们看一下调用容易出错的同步REST API调用并根据 API 返回的数据执行一些任务的简单用例。

在简单的 JQuery 中，上述场景可以通过以下方式解决：

```javascript
$.ajax("/GetEmployees")
    .done(
        function() {
            alert( "success" );
        }
     )
    .fail(
        function() {
            alert( "error" );
        }
     )
    .always(
        function() {
            alert( "complete" );
        }
    );
```

类似地，JDeferred带有[Promise](https://github.com/jdeferred/jdeferred/blob/master/subprojects/jdeferred-core/src/main/java/org/jdeferred2/Promise.java)和[Deferred](https://github.com/jdeferred/jdeferred/blob/master/subprojects/jdeferred-core/src/main/java/org/jdeferred2/Deferred.java)接口，它们在相应的对象上注册一个线程无关的钩子，该钩子根据该对象的状态触发不同的可定制操作。

在这里，Deferred充当触发器，而Promise充当观察者。

我们可以很容易地创建这种类型的异步工作流：

```java
Deferred<String, String, String> deferred
  = new DeferredObject<>();
Promise<String, String, String> promise = deferred.promise();

promise.done(result -> System.out.println("Job done"))
  .fail(rejection -> System.out.println("Job fail"))
  .progress(progress -> System.out.println("Job is in progress"))
  .always((state, result, rejection) -> 
    System.out.println("Job execution started"));

deferred.resolve("msg");
deferred.notify("notice");
deferred.reject("oops");
```

在这里，每个方法都有不同的语义：

-   done() – 仅在延迟对象上的未决操作成功完成时触发
-   fail() – 在延迟对象上执行未决操作时引发某些异常时触发
-   progress() – 在延迟对象上的未决操作开始执行时触发
-   always() – 无论延迟对象的状态如何都会触发

默认情况下，延迟对象的状态可以是PENDING/REJECTED/RESOLVED。我们可以使用deferred.state()方法检查状态。

这里需要注意的是，一旦一个deferred对象的状态变为RESOLVED，我们就不能对该对象进行reject操作。

同样，一旦对象的状态变为REJECTED，我们就不能对该对象执行resolve或notify操作。任何违规行为都将导致IllegalStateExeption。

## 4.过滤器

[在检索最终结果之前，我们可以使用DoneFilter](https://github.com/jdeferred/jdeferred/blob/master/subprojects/jdeferred-core/src/main/java/org/jdeferred2/DoneFilter.java)对延迟对象执行过滤。

过滤完成后，我们将获得线程安全的延迟对象：

```java
private static String modifiedMsg;

static String filter(String msg) {
    Deferred<String, ?, ?> d = new DeferredObject<>();
    Promise<String, ?, ?> p = d.promise();
    Promise<String, ?, ?> filtered = p.then((result) > {
        modifiedMsg = "Hello "  result;
    });

    filtered.done(r > System.out.println("filtering done"));

    d.resolve(msg);
    return modifiedMsg;
}
```

## 5.管道

与过滤器类似，JDeferred提供了[DonePipe](https://github.com/jdeferred/jdeferred/blob/master/subprojects/jdeferred-core/src/main/java/org/jdeferred2/DonePipe.java)接口，用于在延迟的对象挂起操作得到解决后执行复杂的后过滤操作。

```java
public enum Result { 
    SUCCESS, FAILURE 
}; 

private static Result status; 

public static Result validate(int num) { 
    Deferred<Integer, ?, ?> d = new DeferredObject<>(); 
    Promise<Integer, ?, ?> p = d.promise(); 
    
    p.then((DonePipe<Integer, Integer, Exception, Void>) result > {
        public Deferred<Integer, Exception, Void> pipeDone(Integer result) {
            if (result < 90) {
                return new DeferredObject<Integer, Exception, Void>()
                  .resolve(result);
            } else {
                return new DeferredObject<Integer, Exception, Void>()
                  .reject(new Exception("Unacceptable value"));
            }
    }).done(r > status = Result.SUCCESS )
      .fail(r > status = Result.FAILURE );

    d.resolve(num);
    return status;
}
```

在这里，根据实际结果的值，我们提出了一个拒绝结果的异常。

## 6.递延经理

在实时场景中，我们需要处理多个 promise 观察到的多个延迟对象。在这种情况下，很难分别管理多个承诺。

这就是JDeferred带有[DeferredManager](https://github.com/jdeferred/jdeferred/blob/master/subprojects/jdeferred-core/src/main/java/org/jdeferred2/DeferredManager.java)接口的原因，该接口为所有承诺创建了一个公共观察者。因此，使用这个共同的观察者，我们可以为所有的承诺创建共同的行动：

```java
Deferred<String, String, String> deferred = new DeferredObject<>();
DeferredManager dm = new DefaultDeferredManager();
Promise<String, String, String> p1 = deferred.promise(), 
  p2 = deferred.promise(), 
  p3 = deferred.promise();
dm.when(p1, p2, p3)
  .done(result -> ... )
  .fail(result -> ... );
deferred.resolve("Hello Baeldung");
```

我们还可以将带有自定义线程池的[ExecutorService](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ExecutorService.html)分配给DeferredManager：

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
DeferredManager dm = new DefaultDeferredManager(executor);
```

其实我们完全可以忽略Promise的使用，直接定义[Callable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Callable.html)接口即可完成任务：

```java
DeferredManager dm = new DefaultDeferredManager();
dm.when(() -> {
    // return something and raise an exception to interrupt the task
}).done(result -> ... )
  .fail(e -> ... );
```

## 7. 线程安全动作

虽然，大多数时候我们需要处理异步工作流，但有时我们需要等待所有并行任务的结果。

在这种情况下，我们可能只使用Object的wait()方法来等待所有延迟任务完成：

```java
DeferredManager dm = new DefaultDeferredManager();
Deferred<String, String, String> deferred = new DeferredObject<>();
Promise<String, String, String> p1 = deferred.promise();
Promise<String, String, String> p = dm
  .when(p1)
  .done(result -> ... )
  .fail(result -> ... );

synchronized (p) {
    while (p.isPending()) {
        try {
            p.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

deferred.resolve("Hello Baeldung");
```

或者，我们可以使用Promise接口的waitSafely()方法来实现相同的目的。

```java
try {
    p.waitSafely();
} catch (InterruptedException e) {
    e.printStackTrace();
}
```

尽管上述两种方法执行的事情几乎相同，但始终建议使用第二种方法，因为第二种方法不需要同步。

## 8. 安卓集成

JDeferred可以使用 Android Maven 插件轻松地与 Android 应用程序集成。

对于 APKLIB 构建，我们需要在pom.xml中添加以下依赖项：

```xml
<dependency>
    <groupId>org.jdeferred</groupId>
    <artifactId>jdeferred-android</artifactId>
    <version>1.2.6</version>
    <type>apklib</type>
</dependency>
```

对于AAR构建，我们需要在pom.xml中添加以下依赖项：

```java
<dependency>
    <groupId>org.jdeferred</groupId>
    <artifactId>jdeferred-android-aar</artifactId>
    <version>1.2.6</version>
    <type>aar</type>
</dependency>
```

## 9.总结

在本教程中，我们探讨了JDeferred及其不同的实用程序。