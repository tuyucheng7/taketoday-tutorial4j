## 1. 概述

在本教程中，我们解释导致Java抛出UndeclaredThrowableException异常实例的原因。

首先，我们从一些理论开始。然后，我们通过两个真实示例更好地理解此异常的性质。

## 2. UndeclaredThrowableException

从理论上讲，当我们尝试抛出未声明的检查异常时，Java会抛出UndeclaredThrowableException的实例。
**也就是说，我们没有在throws子句中声明检查的异常，但我们在方法体中抛出了该异常**。

有人可能会认为这是不可能的，因为Java编译器会通过编译错误来防止这种情况。例如，如果我们编写以下代码：

```text
public void undeclared() {
    throw new IOException();
}
```

这显然是不能编译通过的：

<img src="../assets/img.png">

**即使在编译时可能不会抛出未声明的检查异常，但在运行时仍然有可能**。
例如，让我们考虑一个运行时代理拦截一个不抛出任何异常的方法：

```text
public void save(Object data) {
    // omitted
}
```

**如果代理本身抛出一个检查异常，从调用者的角度来看，会认为是save方法抛出的该检查异常**。
调用者可能对该代理的存在一无所知，并且会将此异常归咎于save方法。

**在这种情况下，Java会将实际受检异常封装在UndeclaredThrowableException中，并改为抛出UndeclaredThrowableException**。
值得一提的是，UndeclaredThrowableException本身是一个非受检的异常。

## 3. Java动态代理

作为我们的第一个例子，让我们为java.util.List接口创建一个运行时代理并拦截其方法调用。
首先，我们应该实现InvocationHandler接口并在invoke方法中实现额外的逻辑：

```java
private static class ExceptionalInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("size".equals(method.getName())) {
            throw new SomeCheckedException("Always fails");
        }
        throw new RuntimeException();
    }
}

public class SomeCheckedException extends Exception {

    public SomeCheckedException(String message) {
        super(message);
    }
}
```

如果被代理方法是size，则此代理抛出受检异常；否则，它抛出非受检异常。

让我们看看Java如何处理这两种情况。首先，我们调用List.size()方法：

```java
class UndeclaredThrowableExceptionUnitTest {

    @Test
    void givenAProxy_whenProxyUndeclaredThrowsCheckedException_thenShouldBeWrapped() {
        ClassLoader classLoader = getClass().getClassLoader();
        InvocationHandler invocationHandler = new ExceptionalInvocationHandler();
        List<String> proxy = (List<String>) Proxy.newProxyInstance(classLoader, new Class[]{List.class}, invocationHandler);

        assertThatThrownBy(proxy::size)
                .isInstanceOf(UndeclaredThrowableException.class)
                .hasCauseInstanceOf(SomeCheckedException.class);
    }
}
```

在上面的代码中，我们为List接口创建一个代理，并在代理上调用size方法。
**反过来，代理拦截size的调用并抛出一个受检异常。然后，Java将这个受检异常封装在UndeclaredThrowableException的实例中**。
发生这种情况的原因是因为我们以某种方式抛出了一个受检异常，而没有在方法声明中声明它。

如果我们在List接口上调用任何其他方法：

```java
class UndeclaredThrowableExceptionUnitTest {

    @Test
    public void givenAProxy_whenProxyThrowsUncheckedException_thenShouldBeThrownAsIs() {
        ClassLoader classLoader = getClass().getClassLoader();
        InvocationHandler invocationHandler = new ExceptionalInvocationHandler();
        List<String> proxy = (List<String>) Proxy.newProxyInstance(classLoader, new Class[]{List.class}, invocationHandler);

        assertThatThrownBy(proxy::isEmpty).isInstanceOf(RuntimeException.class);
    }
}
```

**由于代理抛出的是非受检的异常，Java允许该异常按原样传播**。

## 4. Spring切面

**当我们在Spring切面中抛出一个受检异常，而被通知的方法(目标方法)没有声明它们时，也会发生同样的情况**。

让我们看看下面的例子：

```java

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ThrowUndeclared {

}
```

我们将通知应用于使用此注解标注的所有方法：

```java

@Aspect
@Component
public class UndeclaredAspect {

    @Around("@annotation(undeclared)")
    public Object advise(ProceedingJoinPoint pjp, ThrowUndeclared undeclared) throws Throwable {
        throw new SomeCheckedException("AOP Checked Exception");
    }
}
```

**基本上，这个通知会使所有带@ThrowUndeclared注解的方法都抛出一个受检异常，即使这些方法没有声明这样的异常**。

现在，我们创建一个Service类：

```java

@Service
public class UndeclaredService {

    @ThrowUndeclared
    public void doSomething() {
    }
}
```

如果我们调用doSomething()方法，Java将抛出UndeclaredThrowableException异常的实例：

```java

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UndeclaredApplication.class)
class UndeclaredThrowableExceptionIntegrationTest {

    @Autowired
    private UndeclaredService service;

    @Test
    void givenAnAspect_whenCallingAdvisedMethod_thenShouldWrapTheException() {

        assertThatThrownBy(service::doSomething)
                .isInstanceOf(UndeclaredThrowableException.class)
                .hasCauseInstanceOf(SomeCheckedException.class);
    }
}
```

如上所示，Java将实际异常封装为cause，并改为抛出UndeclaredThrowableException异常。

## 5. 总结

在本教程中，我们解释了导致Java抛出UndeclaredThrowableException异常实例的原因。