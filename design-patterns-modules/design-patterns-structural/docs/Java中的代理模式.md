## 1. 概述

**代理模式允许我们创建一个充当另一个资源接口的中介**，同时还隐藏了组件的底层复杂性。

## 2. 代理模式示例

考虑一个需要一些初始配置的重型Java对象(如JDBC连接或SessionFactory)。

我们只希望按需初始化此类对象，一旦初始化，我们希望在所有调用中重用它们：

<img src="../assets/img_3.png">

现在让我们为这个对象创建一个简单的接口和配置：

```java
public interface ExpensiveObject {
    void process();
}
```

并且这个接口的实现有一个很大的初始配置：

```java
public class ExpensiveObjectImpl implements ExpensiveObject {

    public ExpensiveObjectImpl() {
        heavyInitialConfiguration();
    }

    @Override
    public void process() {
        LOG.info("processing complete.");
    }

    private void heavyInitialConfiguration() {
        LOG.info("Loading initial configuration...");
    }
}
```

我们现在将利用代理模式并按需初始化我们的对象：

```java
public class ExpensiveObjectProxy implements ExpensiveObject {
    private static ExpensiveObject object;

    @Override
    public void process() {
        if (object == null) {
            object = new ExpensiveObjectImpl();
        }
        object.process();
    }
}
```

每当我们的客户端调用process()方法时，他们只会看到处理过程，并且初始配置将始终保持隐藏状态：

```java
public static void main(String[] args) {
    ExpensiveObject object = new ExpensiveObjectProxy();
    object.process();
    object.process();
}
```

请注意，我们调用了process()方法两次，在幕后，设置部分只会发生一次-当对象第一次被初始化时。

对于所有其他后续调用，此模式将跳过初始配置，并且仅进行处理：

```shell
Loading initial configuration...
processing complete.
processing complete.
```

## 3. 何时使用代理

-   **当我们想要一个复杂或沉重的物体的简化版本时**，在这种情况下，我们可以用一个按需加载原始对象的骨架对象来表示它，也称为延迟初始化，这称为虚拟代理
-   **当原始对象存在于不同的地址空间中时，我们希望在本地表示它**。我们可以创建一个代理来执行所有必要的样板操作，例如创建和维护连接、编码、解码等，同时客户端访问它，因为它存在于他们的本地地址空间中，这称为远程代理
-   **当我们想在原来的底层对象上增加一层安全性，以提供基于客户端访问权限的受控访问，这称为保护代理**

## 4. 总结

在本文中，我们了解了代理设计模式，在以下情况下这是一个不错的选择：

-   当我们想要拥有对象的简化版本或更安全地访问对象时
-   当我们想要一个远程对象的本地版本时