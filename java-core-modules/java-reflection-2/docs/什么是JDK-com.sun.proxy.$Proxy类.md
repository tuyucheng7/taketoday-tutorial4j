## 1. 概述

当我们使用[动态代理](https://www.baeldung.com/java-dynamic-proxies)时，JDK会动态生成一个$Proxy类。通常，这个$Proxy类的完全限定类名有点类似于com.sun.proxy.$Proxy0。正如[Java文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/reflect/Proxy.html)所说，“$Proxy”是代理类的保留名称前缀。

在本教程中，我们将探索这个$Proxy类。

## 2. $Proxy类

在开始之前，让我们先区分一下java.lang.reflect.Proxy类和$Proxy类。java.lang.reflect.Proxy是一个JDK内置类。而且，相比之下，**$Proxy类是在运行时动态生成的**。从类层次结构的角度来看，$Proxy类继承了java.lang.reflect.Proxy类。

### 2.1 动态代理示例

为了有一个讨论的基础，让我们定义两个接口：BasicOperation和AdvancedOperation。BasicOperation接口包含add和subtract方法：

```java
public interface BasicOperation {
    int add(int a, int b);

    int subtract(int a, int b);
}
```

另外，AdvancedOperation接口具有multiply和divide方法：

```java
public interface AdvancedOperation {
    int multiply(int a, int b);

    int divide(int a, int b);
}
```

要获取新生成的代理类$Proxy类，我们可以调用Proxy::getProxyClass方法：

```java
ClassLoader classLoader = ClassLoader.getSystemClassLoader();
Class<?>[] interfaces = {BasicOperation.class, AdvancedOperation.class};
Class<?> proxyClass = Proxy.getProxyClass(classLoader, interfaces);
```

但是，**上面的proxyClass只存在于一个正在运行的JVM中，我们无法直接查看它的类成员**。

### 2.2 转储$Proxy类

为了仔细检查这个$Proxy类，我们最好将它转储到磁盘。使用Java 8时，我们可以在命令行中指定“sun.misc.ProxyGenerator.saveGeneratedFiles”选项：

```shell
-Dsun.misc.ProxyGenerator.saveGeneratedFiles=true
```

或者我们可以通过调用System::setProperty方法来设置此选项：

```java
System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
```

在Java 9及更高版本中，我们应该使用“jdk.proxy.ProxyGenerator.saveGeneratedFiles”选项来代替。为什么会有这样的差异？由于Java模块系统，ProxyGenerator类的包发生了变化。在Java 8中，ProxyGenerator在“sun.misc”包中；然而，[从Java 9开始，ProxyGenerator已经移入“java.lang.reflect”包中](https://bugs.openjdk.java.net/browse/JDK-8145416)。

如果我们仍然不知道哪个选项合适，我们可以查找ProxyGenerator类的saveGeneratedFiles字段以确定正确的选项。

这里要小心：**ProxyGenerator类只读取这个属性一次**。并且，这意味着在JVM显式或隐式生成任何$Proxy类后，System::setProperty方法将无效。具体来说，调用Proxy::getProxyClass或Proxy::newProxyInstance方法将显式生成$Proxy类。另一方面，当我们读取注解时，尤其是在单元测试框架内，JVM会隐式或自动生成$Proxy类来表示注解实例。

转储类文件的确切位置与其完全限定的类名直接相关。例如，如果新生成的类名为“com.sun.proxy.$Proxy0”，则转储的类文件将在当前目录中为“com/sun/proxy/$Proxy0.class”：

<img src="../assets/img_1.png">

### 2.3 $Proxy类的成员

现在，是时候检查这个生成的$Proxy类的类成员了。

让我们首先检查类层次结构。**$Proxy0类有java.lang.reflect.Proxy作为它的超类，这隐含地解释了为什么动态代理只支持接口**。此外，$Proxy0类实现了我们之前定义的BasicOperation和AdvancedOperation接口：

```java
public final class $Proxy0 extends Proxy implements BasicOperation, AdvancedOperation
```

为了可读性，我们将$Proxy0类的字段名称更改为更有意义的名称。hashCodeMethod、equalsMethod和toStringMethod字段追溯到Object类；addMethod和subtractMethod字段与BasicOperation接口相关；multiplyMethod和divideMethod字段映射到AdvanceOperation接口：

```java
private static Method hashCodeMethod;
private static Method equalsMethod;
private static Method toStringMethod;
private static Method addMethod;
private static Method subtractMethod;
private static Method multiplyMethod;
private static Method divideMethod;
```

最后，$Proxy0类中定义的方法遵循相同的逻辑：**它们的所有实现都委托给InvocationHandler::invoke方法**。而且，$Proxy0类将从其构造函数中获取一个InvocationHandler实例：

```java
public $Proxy0(InvocationHandler handler) {
    super(handler);
}

public final int hashCode() {
    try {
        return (Integer) super.h.invoke(this, hashCodeMethod, (Object[]) null);
    }
    catch (RuntimeException | Error ex1) {
        throw ex1;
    }
    catch (Throwable ex2) {
        throw new UndeclaredThrowableException(ex2);
    }
}
```

## 3. 代理如何工作

在我们检查了$Proxy类本身之后，是时候更进一步了：如何生成$Proxy类以及如何加载$Proxy类？关键逻辑在于java.lang.reflect.Proxy和ProxyGenerator类。

随着新Java版本的发布，Proxy和ProxyGenerator类的实现细节不断发展。粗略地说，**ProxyGenerator负责生成$Proxy类的字节数组，而Proxy类负责将这个字节数组加载到JVM中**。

现在，让我们使用Java 8、Java 11和Java 17进行讨论，因为它们是LTS(长期支持)版本。

### 3.1 Java 8

在Java 8中，我们可以分五步来描述$Proxy类的生成过程：

<img src="../assets/img_2.png">

Proxy::getProxyClass或Proxy::newProxyInstance方法是我们的起点-任何一个都将调用Proxy::getProxyClass0方法。而且，Proxy::getProxyClass0方法是一个私有方法，将进一步调用ProxyClassFactory::apply方法。

ProxyClassFactory是Proxy类中定义的静态嵌套类。并且，它的apply方法计算出即将到来的类的包名、类名和访问标志。然后，apply方法将调用ProxyGenerator::generateProxyClass方法。

在Java 8中，ProxyGenerator类是定义在“sun.misc”包中的公共类。它从Java 9开始迁移到“java.lang.reflect”包。并且，generateProxyClass方法将创建一个ProxyGenerator实例，调用其负责生成字节码的generateClassFile方法，可选地转储类文件，并返回结果字节数组.

字节码生成成功后，Proxy::defineClass0方法负责将该字节数组加载到正在运行的JVM中。最后，我们得到一个动态生成的$Proxy类。

### 3.2 Java 11

与Java 8版本相比，Java 11引入了**三大变化**：

-   Proxy类添加了一个新的getProxyConstructor方法和一个静态嵌套的ProxyBuilder类
-   对于Java模块系统，ProxyGenerator已迁移到“java.lang.reflect”包并成为包私有类
-   要将生成的字节数组加载到JVM中，需要使用Unsafe::defineClass

<img src="../assets/img_3.png">

### 3.3 Java 17

与Java 11版本相比，Java 17有**两大变化**：

-   从实现的角度来看，ProxyGenerator类利用JDK内置的ASM来生成字节码
-   JavaLangAccess::defineClass方法负责将生成的字节码加载到JVM中

<img src="../assets/img_4.png">

## 4. 使用代理注解

在Java中，注解类型是一种特殊的接口类型。但是，我们可能想知道如何创建注解实例。事实上，我们不需要。**当我们使用Java反射API读取注解时，JVM会动态生成一个$Proxy类作为注解类型的实现**：

```java
FunctionalInterface instance = Consumer.class.getDeclaredAnnotation(FunctionalInterface.class);
Class<?> clazz = instance.getClass();

boolean isProxyClass = Proxy.isProxyClass(clazz);
assertTrue(isProxyClass);
```

在上面的代码片段中，我们使用Consumer类获取其FunctionalInterface实例，然后获取该实例的类，最后使用Proxy::isProxyClass方法检查该类是否为$Proxy类。

## 5. 总结

在本教程中，我们首先介绍了一个动态代理示例，然后转储生成的$Proxy类并检查其成员。为了更进一步，我们解释了Proxy和ProxyGenerator类如何协同工作以在不同的Java版本中生成和加载$Proxy类。最后，我们提到注解类型也是使用$Proxy类实现的。