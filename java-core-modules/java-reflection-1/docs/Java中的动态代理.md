## 1. 概述

这篇文章是关于[Java的动态代理](https://docs.oracle.com/javase/8/docs/technotes/guides/reflection/proxy.html)-这是我们在该语言中可用的主要代理机制之一。

简而言之，代理是前端或包装器，它们通过自己的设施(通常是真正的方法)传递函数调用-可能会添加一些功能。

动态代理允许具有单个方法的单个类为具有任意数量方法的任意类的多个方法调用提供服务。动态代理可以被认为是一种Facade，但它可以伪装成任何接口的实现。在幕后，**它将所有方法调用路由到单个处理程序-invoke()方法**。

虽然它不是用于日常编程任务的工具，但动态代理对于框架编写者来说可能非常有用。它也可以用于具体类实现直到运行时才知道的情况。

此功能内置于标准JDK中，因此不需要额外的依赖项。

## 2. 调用处理程序

让我们构建一个简单的代理，除了打印请求调用的方法并返回一个硬编码数字之外，它实际上什么都不做。

首先，我们需要创建java.lang.reflect.InvocationHandler的子类型：

```java
public class DynamicInvocationHandler implements InvocationHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(DynamicInvocationHandler.class);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LOGGER.info("Invoked method: {}", method.getName());

        return 42;
    }
}
```

这里我们定义了一个简单的代理，用于记录调用了哪个方法并返回42。

## 3. 创建代理实例

由我们刚刚定义的调用处理程序提供服务的代理实例是通过对java.lang.reflect.Proxy类的工厂方法调用创建的：

```java
Map proxyInstance = (Map) Proxy.newProxyInstance(
    DynamicProxyTest.class.getClassLoader(), 
    new Class[] { Map.class }, 
    new DynamicInvocationHandler());
```

一旦我们有了一个代理实例，我们就可以像往常一样调用它的接口方法：

```java
proxyInstance.put("hello", "world");
```

正如预期的那样，有关正在调用的put()方法的消息打印在日志中。

## 4. 通过Lambda表达式调用处理程序

由于InvocationHandler是一个函数式接口，因此可以使用lambda表达式内联定义处理程序：

```java
Map proxyInstance = (Map) Proxy.newProxyInstance(
    DynamicProxyTest.class.getClassLoader(), 
    new Class[] { Map.class }, 
    (proxy, method, methodArgs) -> {
    if (method.getName().equals("get")) {
        return 42;
    } else {
        throw new UnsupportedOperationException("Unsupported method: " + method.getName());
    }
});
```

在这里，我们定义了一个处理程序，它为所有get操作返回42，并为所有其他操作抛出UnsupportedOperationException。

它以完全相同的方式调用：

```java
(int) proxyInstance.get("hello"); // 42
proxyInstance.put("hello", "world"); // exception
```

## 5. 定时动态代理示例

让我们来看看动态代理的一个潜在真实场景。

假设我们要记录我们的函数执行需要多长时间。为此，我们首先定义一个能够包装“真实”对象、跟踪计时信息和反射调用的处理程序：

```java
public class TimingDynamicInvocationHandler implements InvocationHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(TimingDynamicInvocationHandler.class);

    private final Map<String, Method> methods = new HashMap<>();

    private Object target;

    public TimingDynamicInvocationHandler(Object target) {
        this.target = target;

        for(Method method: target.getClass().getDeclaredMethods()) {
            this.methods.put(method.getName(), method);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.nanoTime();
        Object result = methods.get(method.getName()).invoke(target, args);
        long elapsed = System.nanoTime() - start;

        LOGGER.info("Executing {} finished in {} ns", method.getName(), elapsed);

        return result;
    }
}
```

随后，此代理可用于各种对象类型：

```java
Map mapProxyInstance = (Map) Proxy.newProxyInstance(
    DynamicProxyTest.class.getClassLoader(), new Class[] { Map.class }, 
    new TimingDynamicInvocationHandler(new HashMap<>()));

mapProxyInstance.put("hello", "world");

CharSequence csProxyInstance = (CharSequence) Proxy.newProxyInstance(
    DynamicProxyTest.class.getClassLoader(), 
    new Class[] { CharSequence.class }, 
    new TimingDynamicInvocationHandler("Hello World"));

csProxyInstance.length()
```

在这里，我们代理了一个Map和一个CharSequence(String)。

代理方法的调用将委托给包装对象并生成日志记录语句：

```java
Executing put finished in 19153 ns 
Executing get finished in 8891 ns 
Executing charAt finished in 11152 ns 
Executing length finished in 10087 ns
```

## 6. 总结

在这个快速教程中，我们研究了Java的动态代理及其一些可能的用法。