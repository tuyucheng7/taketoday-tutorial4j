## 1. 简介

在这篇快速文章中，我们介绍Java 9中的[StackWalking API](https://openjdk.java.net/jeps/259)。

新功能提供了对StackFrame的流的访问，使我们能够轻松地直接浏览堆栈，并充分利用Java 8中强大的Stream API。

## 2. StackWalker的优点

在Java 8中，Throwable::getStackTrace和Thread::getStackTrace返回一个StackTraceElement数组。如果不编写大量手动的代码，就无法丢弃不需要的帧而只保留我们感兴趣的帧。

除此之外，Thread::getStackTrace可能会返回部分堆栈跟踪。这是因为规范允许VM实现为了性能而省略一些堆栈帧。

在Java 9中，使用StackWalker的walk()方法，我们可以遍历一些我们感兴趣的帧或完整的堆栈跟踪。

当然，新功能是线程安全的；这允许多个线程共享一个StackWalker实例来访问它们各自的堆栈。

如[JEP-259](https://openjdk.java.net/jeps/259)中所述，JVM将得到增强，以便在需要时允许对额外的堆栈帧进行有效的延迟访问。

## 3. StackWalker实践

首先我们创建一个包含方法调用链的类：

```java
public class StackWalkerDemo {

    public void methodOne() {
        this.methodTwo();
    }

    public void methodTwo() {
        this.methodThree();
    }

    public void methodThree() {
        // stack walking code
    }
}
```

### 3.1 捕获整个堆栈跟踪

然后继续添加一些堆栈遍历代码：

```java
public void methodThree() {
    List<StackFrame> stackTrace = StackWalker.getInstance()
        .walk(this::walkExample);
}
```

StackWalker::walk方法接收一个函数引用，为当前线程创建一个StackFrame的流，将该函数应用于流，然后关闭流。

现在让我们定义StackWalkerDemo::walkExample方法：

```java
public List<StackFrame> walkExample(Stream<StackFrame> stackFrameStream) {
    return stackFrameStream.collect(Collectors.toList());
}
```

此方法只是收集StackFrame并将其作为List<StackFrame>返回。要测试此示例，我们运行JUnit测试：

```java
@Test
void giveStalkWalker_whenWalkingTheStack_thenShowStackFrames() {
    new StackWalkerDemo().methodOne();
}
```

将其作为JUnit测试运行的唯一原因是我们能够在堆栈中看到更多帧：

```plaintext
cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo#methodThree, Line 20
cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo#methodTwo, Line 15
cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo#methodOne, Line 11
cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemoUnitTest#giveStalkWalker_whenWalkingTheStack_thenShowStackFrames, Line 9
org.junit.platform.commons.util.ReflectionUtils#invokeMethod, Line 725
org.junit.jupiter.engine.execution.MethodInvocation#proceed, Line 60
org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation#proceed, Line 131
org.junit.jupiter.engine.extension.TimeoutExtension#intercept, Line 149
org.junit.jupiter.engine.extension.TimeoutExtension#interceptTestableMethod, Line 140
org.junit.jupiter.engine.extension.TimeoutExtension#interceptTestMethod, Line 84
```

在整个堆栈跟踪中，我们只对前四帧感兴趣，其余的帧只不过都是第三方框架的调用帧。

### 3.2 过滤StackFrame

我们修改堆栈遍历代码并消除不感兴趣的帧：

```java
public List<StackFrame> walkExample2(Stream<StackFrame> stackFrameStream) {
	return stackFrameStream
        .filter(frame -> frame.getClassName().contains("cn.tuyucheng.taketoday"))
        .collect(Collectors.toList());
}
```

使用Stream API的强大功能，我们只保留我们感兴趣的帧。这将清除噪音，在堆栈日志中只保留前四行：

```plaintext
class cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo#methodThree, Line 27
class cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo#methodTwo, Line 15
class cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo#methodOne, Line 11
class cn.tuyucheng.taketoday.java9.stackwalker
  .cStackWalkerDemoUnitTest#giveStalkWalker_whenWalkingTheStack_thenShowStackFrames, Line 9
```

现在让我们确定发起调用的JUnit测试：

```java
public String walkExample3(Stream<StackFrame> stackFrameStream) {
	return stackFrameStream
			.filter(frame -> frame.getClassName().contains("cn.tuyucheng.taketoday")
					&& frame.getClassName().endsWith("Test"))
			.findFirst()
			.map(frame -> frame.getClassName() + "#" + frame.getMethodName() + ", Line " + frame.getLineNumber())
			.orElse("Unknown caller");
}
```

请注意，在这里，我们只对单个StackFrame感兴趣。输出只会是包含cStackWalkerDemoUnitTest类的行。

### 3.3 捕获反射帧

为了捕获默认情况下隐藏的反射帧，需要为StackWalker配置一个附加参数SHOW_REFLECT_FRAMES：

```java
List<StackFrame> stackTrace = StackWalker
    .getInstance(StackWalker.Option.SHOW_REFLECT_FRAMES)
    .walk(this::walkExample);
```

使用此参数，将捕获包括Method.invoke()和Constructor.newInstance()在内的所有反射帧：

```plaintext
cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo#methodThree, Line 40
cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo#methodTwo, Line 16
cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo#methodOne, Line 12
cn.tuyucheng.taketoday.java9.stackwalker
  .cStackWalkerDemoUnitTest#giveStalkWalker_whenWalkingTheStack_thenShowStackFrames, Line 9
jdk.internal.reflect.NativeMethodAccessorImpl#invoke0, Line -2
jdk.internal.reflect.NativeMethodAccessorImpl#invoke, Line 62
jdk.internal.reflect.DelegatingMethodAccessorImpl#invoke, Line 43
java.lang.reflect.Method#invoke, Line 547
org.junit.runners.model.FrameworkMethod$1#runReflectiveCall, Line 50
  ...eclipse and junit frames...
org.eclipse.jdt.internal.junit.runner.RemoteTestRunner#main, Line 192
```

正如我们所见，jdk.internal帧是由SHOW_REFLECT_FRAMES参数捕获的新帧。

### 3.4 捕捉隐藏帧

除了反射帧之外，JVM实现还可以选择隐藏特定于实现的帧。但是，这些帧并没有从StackWalker隐藏：

```java
Runnable r = () -> {
	List<StackFrame> stackTrace2 = StackWalker
			.getInstance(StackWalker.Option.SHOW_HIDDEN_FRAMES)
			.walk(this::walkExample);
	printStackTrace(stackTrace2);
};
r.run();
```

请注意，在此示例中，我们将lambda引用分配给Runnable。唯一的原因是JVM会为lambda表达式创建一些隐藏帧。

这在堆栈跟踪中清晰可见：

```plaintext
cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo#lambda$0, Line 47
cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo$$Lambda$39/924477420#run, Line -1
cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo#methodThree, Line 50
cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo#methodTwo, Line 16
cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemo#methodOne, Line 12
cn.tuyucheng.taketoday.java9.stackwalker
  .cStackWalkerDemoUnitTest#giveStalkWalker_whenWalkingTheStack_thenShowStackFrames, Line 9
jdk.internal.reflect.NativeMethodAccessorImpl#invoke0, Line -2
jdk.internal.reflect.NativeMethodAccessorImpl#invoke, Line 62
jdk.internal.reflect.DelegatingMethodAccessorImpl#invoke, Line 43
java.lang.reflect.Method#invoke, Line 547
org.junit.runners.model.FrameworkMethod$1#runReflectiveCall, Line 50
  ...junit and eclipse frames...
org.eclipse.jdt.internal.junit.runner.RemoteTestRunner#main, Line 192
```

前两个帧是JVM内部创建的lambda代理帧。值得注意的是，我们在上一个示例中捕获的反射帧仍然通过SHOW_HIDDEN_FRAMES参数保留，这是因为SHOW_HIDDEN_FRAMES是SHOW_REFLECT_FRAMES的超集。

### 3.5 识别调用类

参数RETAIN_CLASS_REFERENCE在StackWalker遍历的所有StackFrame中零售Class的对象。这允许我们调用StackWalker::getCallerClass和StackFrame::getDeclaringClass方法。

让我们使用StackWalker::getCallerClass方法来识别调用类：

```java
public void findCaller() {
	Class<?> caller = StackWalker
			.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
			.getCallerClass();
	System.out.println(caller.getCanonicalName());
}
```

这一次，我们直接从单独的JUnit测试中调用此方法：

```java
@Test
void giveStalkWalker_whenInvokingFindCaller_thenFindCallingClass() {
    new StackWalkerDemo().findCaller();
}
```

caller.getCanonicalName()的输出将是：

```plaintext
cn.tuyucheng.taketoday.java9.stackwalker.StackWalkerDemoUnitTest
```

请注意，不应从堆栈底部的方法调用StackWalker::getCallerClass，因为这将导致引发IllegalCallerException。

## 4. 总结

通过本文，我们看到了使用StackWalker和Stream API的强大功能来处理StackFrame是多么容易。

当然，我们还可以探索各种其他功能；例如跳过、丢弃和限制StackFrame。[官方文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StackWalker.html)包含一些其他用例的可靠示例。